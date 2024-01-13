package de.sfuhrm.radiobrowser4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
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

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

/** URLConnection implementation of the RestDelegate.
 * @see HttpURLConnection
 * @author Stephan Fuhrmann
 * */
@Slf4j
class RestDelegateImpl implements RestDelegate {

    /** The URI of the API endpoint. All paths are relative to this one. */
    private final URI endpoint;

    /** The connection parameters. */
    private final ConnectionParams connectionParams;

    /** The GSON adapter. */
    private final Gson gson;

    /** Create a new instance.
     * @param inConnectionParams the connection parameters to use.
     * */
    RestDelegateImpl(final ConnectionParams inConnectionParams) {
        this.endpoint = URI.create(inConnectionParams.getApiUrl());
        this.connectionParams = inConnectionParams;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Stats.class, new StatsDeserializer())
                .registerTypeAdapter(Station.class, new StationDeserializer())
                .create();
    }

    static class HttpException extends RadioBrowserException {
        @Getter
        private int code;
        @Getter
        private String message;

        HttpException(int inCode, String inMessage) {
            super("HTTP response "
                    + inCode + " "
                    + inMessage);
            this.code = inCode;
            this.message = inMessage;
        }
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
        URL url = fullUri.toURL();

        log.debug("Connecting to {}", url.toExternalForm());
        HttpURLConnection connection;

        if (null != connectionParams.getProxyUri()) {
            Proxy proxy = getProxy(connectionParams);
            Authenticator auth = getProxyAuthenticator(connectionParams);
            if (null != auth) {
                Authenticator.setDefault(auth);
            }
            connection = (HttpURLConnection)
                    url.openConnection(proxy);
        } else {
            connection = (HttpURLConnection)
                    url.openConnection();
        }

        connection.setConnectTimeout(connectionParams.getTimeout());
        connection.setReadTimeout(connectionParams.getTimeout());

        return connection;
    }

    static Charset guessCharsetFor(
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
                try {
                    return Charset.forName(charsetName);
                }
                catch(IllegalCharsetNameException e) {
                    log.warn("Illegal charset name: {}", charsetName);
                    return StandardCharsets.UTF_8;
                }
                catch(UnsupportedCharsetException e) {
                    log.warn("Unsupported charset name: {}", charsetName);
                    return StandardCharsets.UTF_8;
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static Reader readerFor(
            final HttpURLConnection connection) throws IOException {
        String encoding = connection.getContentEncoding();
        String contentType = connection.getContentType();
        Charset charset = guessCharsetFor(contentType);

        checkResponseStatus(connection);

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
            return retryLoop(() -> {
                try {
                    HttpURLConnection connection = newClient(path);
                    configure(connection);
                    try (Reader reader = readerFor(connection)) {
                        return gson.fromJson(reader, resultClass);
                    } finally {
                        connection.disconnect();
                    }
                } catch (IOException e) {
                    throw new RadioBrowserException(e);
                }
            });
    }

    /** Retries on HTTP errors. */
    private <T> T retryLoop(final Supplier<T> supplier) {
        int retries = connectionParams.getRetries();
        long retryInterval = connectionParams.getRetryInterval();
        while (true) {
            try {
                return supplier.get();
            } catch (HttpException e) {
                if (retries-- <= 0) {
                    throw e;
                }
                try {
                    log.debug("Got HTTP {}, retrying in {} ms", e.code, retryInterval);
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e1) {
                    throw new RadioBrowserException(e1);
                }
            }
        }
    }

    /** Configure the user agent and accept / accept-encoding headers. */
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

    /** Get the request body as "application/json". */
    private String asApplicationJson(
            final Map<String, String> requestParams
    ) throws UnsupportedEncodingException {
        String json = gson.toJson(requestParams);
        return json;
    }

    /** Get the request body as "application/x-www-form-urlencoded". */
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
        return retryLoop(() -> {
            try {
                HttpURLConnection connection = newClient(path);
                configure(connection);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                if (false) {
                    sendJsonRequest(connection, requestParams);
                } else {
                    sendXWWWFormUrlencodedRequest(connection, requestParams);
                }
                try (Reader reader = readerFor(connection)) {
                    return gson.fromJson(reader, resultClass);
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                throw new RadioBrowserException(e);
            }
        });
    }

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param connection the connection to send the request to.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    private void sendXWWWFormUrlencodedRequest(final HttpURLConnection connection,
                                 final Map<String, String> requestParams) throws IOException {
        connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");
        String encoded = asWwwFormUrlEncoded(requestParams);
        log.debug("POST WWW-Form-UrlEncoded body: {}", encoded);
        connection.getOutputStream()
                .write(encoded.getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();
    }


    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/json" encoded data.
     * @param connection the connection to send the request to.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/json" encoding.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    private void sendJsonRequest(final HttpURLConnection connection,
                                 final Map<String, String> requestParams) throws IOException {
        String json = asApplicationJson(requestParams);
        log.debug("POST JSON body: {}", json);
        connection.setRequestProperty(
                "Content-Type",
                "application/json; charset=UTF-8");
        connection.getOutputStream()
                .write(json.getBytes(StandardCharsets.UTF_8));
        connection.getOutputStream().flush();
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
            throw new HttpException(
                    response.getResponseCode(),
                    response.getResponseMessage());
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
