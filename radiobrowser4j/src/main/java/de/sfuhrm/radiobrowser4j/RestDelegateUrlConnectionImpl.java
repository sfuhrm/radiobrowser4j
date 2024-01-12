package de.sfuhrm.radiobrowser4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.Proxy;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/** URLConnection implementation of the RestDelegate.
 * @see HttpURLConnection
 * @author Stephan Fuhrmann
 * */
@Slf4j
class RestDelegateUrlConnectionImpl implements RestDelegate {

    /** The URI of the API endpoint. All paths are relative to this one. */
    private final URI endpoint;

    /** The connection parameters. */
    private final ConnectionParams connectionParams;

    /** The GSON adapter. */
    private final Gson gson;

    /** Create a new instance.
     * @param inConnectionParams the connection parameters to use.
     * */
    RestDelegateUrlConnectionImpl(final ConnectionParams inConnectionParams) {
        this.endpoint = URI.create(inConnectionParams.getApiUrl());
        this.connectionParams = inConnectionParams;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Stats.class, new StatsDeserializer())
                .registerTypeAdapter(Station.class, new StationDeserializer())
                .create();
    }

    private static Proxy getProxy(final ConnectionParams connectionParams) {
        Proxy proxy = null;
        if (connectionParams.getProxyUri() != null) {
            URI proxyUri = URI.create(connectionParams.getProxyUri());

            proxy = new Proxy(Proxy.Type.HTTP,
                    InetSocketAddress.createUnresolved(
                            proxyUri.getHost(),
                            proxyUri.getPort()));
        }
        return proxy;
    }

    private static Authenticator getProxyAuthenticator(
            final ConnectionParams connectionParams) {
        Authenticator auth = null;
        if (connectionParams.getProxyUri() != null
                && connectionParams.getProxyUser() != null
                && connectionParams.getProxyPassword() != null) {
            auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            connectionParams.getProxyUser(),
                            connectionParams.getProxyPassword().toCharArray());
                }
            };
        }
        return auth;
    }

    /** Create a new JAX-RS client.
     * @param path the path relative to the endpoint.
     * @return the client instance that has been created.
     *  */
    private HttpURLConnection newClient(
            final String path) throws IOException {
        URI fullUri = endpoint.resolve(path);

        log.debug("Connecting to {}", fullUri);
        HttpURLConnection connection;

        if (null != connectionParams.getProxyUri()) {
            Proxy proxy = getProxy(connectionParams);
            Authenticator auth = getProxyAuthenticator(connectionParams);
            if (null != auth) {
                Authenticator.setDefault(auth);
            }
            connection = (HttpURLConnection)
                    fullUri.toURL().openConnection(proxy);
        } else {
            connection = (HttpURLConnection)
                    fullUri.toURL().openConnection();
        }

        connection.setConnectTimeout(connectionParams.getTimeout());
        connection.setReadTimeout(connectionParams.getTimeout());

        return connection;
    }

    private static Charset guessCharsetFor(
            final String contentType) {
        if (null == contentType) {
            return StandardCharsets.UTF_8;
        }
        String[] parts = contentType.split(";");
        if (parts.length < 2) {
            return StandardCharsets.UTF_8;
        }
        for (String part : parts) {
            if (part.trim().startsWith("charset=")) {
                String charsetName = part.trim().substring(
                        "charset=".length());
                return Charset.forName(charsetName);
            }
        }
        return StandardCharsets.UTF_8;
    }

    private Reader readerFor(
            final HttpURLConnection connection) throws IOException {
        String encoding = connection.getContentEncoding();
        String contentType = connection.getContentType();
        Charset charset = guessCharsetFor(contentType);
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            return new InputStreamReader(
                    new GZIPInputStream(connection.getInputStream()),
                    charset);
        } else {
            return new InputStreamReader(connection.getInputStream(),
                    charset);
        }
    }

    @Override
    public <T> T get(final String path, final Class<T> resultClass) {
        try {
            HttpURLConnection connection = newClient(path);
            configure(connection);
            try (Reader reader = readerFor(connection)) {
                checkResponseStatus(connection);
                return gson.fromJson(reader, resultClass);
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RadioBrowserException(e);
        }
    }

    private void configure(final HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent",
                connectionParams.getUserAgent());
        connection.setRequestProperty("Accept",
                "application/json");
        connection.setRequestProperty("Accept-Encoding",
                "gzip");
    }

    @Override
    public List<Station> postWithListOfStation(final String path,
               final Map<String,
               String> requestParams) {
        return post(path,
                requestParams,
                new TypeToken<List<Station>>() { });    }

    @Override
    public List<Map<String, String>> postWithListOfMapOfString(
            final String path,
            final Map<String, String> requestParams) {
        return post(path,
                requestParams,
                new TypeToken<List<Map<String, String>>>() { });
    }

    private static String asWwwFormUrlEncoded(
            final Map<String, String> requestParams
    ) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return sb.toString();
    }

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param path the path on the web server.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @param resultClass the expected resulting class wrapped in a
     *                    generic type.
     * @param <T> the expected return type.
     * @return the resulting type.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    private <T> T post(final String path,
                       final Map<String, String> requestParams,
                       final TypeToken<T> resultClass) {
        try {
            String requestBody = asWwwFormUrlEncoded(requestParams);
            log.debug("POST body: {}", requestBody);

            HttpURLConnection connection = newClient(path);
            configure(connection);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8");
            connection.setDoOutput(true);
            connection.getOutputStream()
                    .write(requestBody.getBytes(StandardCharsets.UTF_8));
            try (Reader reader = readerFor(connection)) {
                checkResponseStatus(connection);
                return gson.fromJson(reader, resultClass);
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RadioBrowserException(e);
        }
    }

    @Override
    public <T> T post(final String path,
                      final Map<String, String> requestParams,
                      final Class<T> resultClass) {
        return post(path,
                requestParams,
                TypeToken.get(resultClass));
    }

    /** Check the response for a non 200 status code
     * and throw an exception if needed.
     * @param response the response to check the status
     *                 code of.
     * @throws RadioBrowserException if the HTTP code was not
     * 200.
     * */
    private static void checkResponseStatus(
            final HttpURLConnection response) throws IOException {
        logResponseStatus(response);
        if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RadioBrowserException("HTTP response "
                    + response.getResponseCode() + " "
                    + response.getResponseMessage());
        }
    }

    /** Log the response.
     * @param response the response to log the status
     *                 code of.
     * */
    private static void logResponseStatus(
            final HttpURLConnection response) throws IOException {
        if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
            log.warn("Non HTTP OK/200 status: status={}, reason={}",
                    response.getResponseCode(),
                    response.getResponseMessage()
            );
        } else {
            log.debug("HTTP response status={}, reason={}, length={}",
                    response.getResponseCode(),
                    response.getResponseMessage(),
                    response.getContentLength());
        }
    }
}
