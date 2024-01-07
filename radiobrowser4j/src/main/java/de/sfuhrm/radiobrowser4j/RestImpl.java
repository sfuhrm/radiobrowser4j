package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.client.Client;
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

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RestImpl {
    private Client client;

    private URI endpoint;

    private String userAgent;

    RestImpl(URI endpoint, int timeout, String proxyUri, String proxyUser, String proxyPassword, String userAgent) {
        this.endpoint = endpoint;
        client = RestClientFactory.newClient(timeout,
                proxyUri,
                proxyUser,
                proxyPassword);
        this.userAgent = userAgent;
    }

    /** Composes URI path components with '/' separators.
     * @param components the components to compose.
     * @return the joint path.
     * */
    static String paths(String...components) {
        return Arrays.stream(components).collect(Collectors.joining("/"));
    }

    <T> T get(String path, Class<T> resultClass) {
        WebTarget webTarget = client.target(endpoint);
        return webTarget.path(path)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .get(resultClass);
    }

    <T> T post(String path, Map<String, String> requestParams, GenericType<T> resultClass) {
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
