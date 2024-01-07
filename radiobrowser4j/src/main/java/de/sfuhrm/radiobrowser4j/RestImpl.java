package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.GZipEncoder;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
final class RestImpl {

    /** The internal jax-rs client to use.  */
    private final Client client;

    /** The URI of the API endpoint. All paths are relative to this one. */
    private final URI endpoint;

    /** The String of the user agent to identify with. */
    private final String userAgent;

    /** Create a new instance.
     * @param inEndpoint the API endpoint URI address.
     * @param inTimeout the timeout in millis.
     * @param inProxyUri the optional proxy URI.
     * @param inProxyUser the optional proxy user.
     * @param inProxyPassword the optional proxy password.
     * @param inUserAgent the mandatory user agent string to send.
     * */
    RestImpl(final URI inEndpoint,
             final int inTimeout,
             final String inProxyUri,
             final String inProxyUser,
             final String inProxyPassword,
             final String inUserAgent) {
        this.endpoint = inEndpoint;
        client = newClient(inTimeout,
                inProxyUri,
                inProxyUser,
                inProxyPassword);
        this.userAgent = inUserAgent;
    }

    /** Create a new JAX-RS client.
     * @param timeout connect / read timeout in milliseconds.
     * @param proxyUri optional proxy URI.
     * @param proxyUser optional proxy user.
     * @param proxyPassword optional proxy password.
     * @return the client instance that has been created.
     *  */
    private static Client newClient(final int timeout,
                            final String proxyUri,
                            final String proxyUser,
                            final String proxyPassword) {
        Client client = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .register(GZipEncoder.class)
                .build();
        client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
        client.property(ClientProperties.READ_TIMEOUT, timeout);
        if (proxyUri != null) {
            client.property(ClientProperties.PROXY_URI, proxyUri);
            if (proxyUser != null) {
                client.property(ClientProperties.PROXY_USERNAME,
                        proxyUser);
            }
            if (proxyPassword != null) {
                client.property(ClientProperties.PROXY_PASSWORD,
                        proxyPassword);
            }
        }
        return client;
    }

    /** Composes URI path components with '/' separators.
     * @param components the components to compose.
     * @return the joint path.
     * */
    static String paths(final String...components) {
        return Arrays.stream(components).collect(Collectors.joining("/"));
    }

    /** Sends a GET request to the remote server.
     * @param path the path on the web server.
     * @param resultClass the result class to retrieve.
     * @param <T> the expected return type.
     * @return an instance of the result class.
     * */
    <T> T get(final String path, final Class<T> resultClass) {
        WebTarget webTarget = client.target(endpoint);
        return webTarget.path(path)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .get(resultClass);
    }

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param path the path on the web server.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @return the resulting type.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    List<Station> postWithListOfStation(final String path,
                                        final Map<String,
                                                String> requestParams) {
        return post(path,
                requestParams,
                new GenericType<List<Station>>() { });
    }

    /** Sends a POST request to the remote server. The
     * body gets transferred as
     *  "application/x-www-form-urlencoded" encoded data.
     * @param path the path on the web server.
     * @param requestParams the request parameters to send as the POST body in
     *                       "application/x-www-form-urlencoded" encoding.
     * @return the resulting type.
     * @throws RadioBrowserException if the sever sent a non-OK response.
     * */
    List<Map<String, String>> postWithListOfMapOfString(
            final String path,
            final Map<String, String> requestParams) {
        return post(path,
                requestParams,
                new GenericType<List<Map<String, String>>>() { });
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
    <T> T post(final String path,
                       final Map<String, String> requestParams,
                       final Class<T> resultClass) {
        Entity<Form> entity = Entity.form(
                new MultivaluedHashMap<>(requestParams));
        WebTarget webTarget = client.target(endpoint);
        try (Response response = builder(webTarget.path(path))
                .post(entity)) {
            checkResponseStatus(response);

            return response.readEntity(resultClass);
        }
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
               final GenericType<T> resultClass) {
        Entity<Form> entity = Entity.form(
                new MultivaluedHashMap<>(requestParams));
        WebTarget webTarget = client.target(endpoint);
        try (Response response = builder(webTarget.path(path))
                .post(entity)) {
            checkResponseStatus(response);

            return response.readEntity(resultClass);
        }
    }

    /** Creates a builder from the given web target
     * applying the standard request and accept
     * types.
     * @param in the web target to create a builder from.
     * @return an invocation builder that is built from the web target.
     * */
    private Invocation.Builder builder(final WebTarget in) {
        return in.request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .header(HttpHeaders.ACCEPT_ENCODING, "gzip");
    }

    /** Check the response for a non 200 status code
     * and throw an exception if needed.
     * @param response the response to check the status
     *                 code of.
     * @throws RadioBrowserException if the HTTP code was not
     * 200.
     * */
    private static void checkResponseStatus(final Response response) {
        logResponseStatus(response);
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            throw new RadioBrowserException(
                    response.getStatusInfo().getReasonPhrase());
        }
    }

    /** Log the response.
     * @param response the response to log the status
     *                 code of.
     * */
    private static void logResponseStatus(final Response response) {
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            log.warn("Non HTTP OK/200 status: status={}, reason={}",
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase()
            );
        } else {
            log.debug("HTTP response status={}, reason={}, length={}",
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase(),
                    response.getLength());
        }
    }
}
