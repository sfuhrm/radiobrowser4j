package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

import java.net.URI;

public class RestImpl {
    private Client client;

    RestImpl(int timeout, String proxyUri, String proxyUser, String proxyPassword) {
        client = RestClientFactory.newClient(timeout,
                proxyUri,
                proxyUser,
                proxyPassword);
    }

    <T> T get(URI endpoint, String path, Class<T> resultClass, String userAgent) {
        WebTarget webTarget = client.target(endpoint);
        return webTarget.path("json/stats")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .get(resultClass);
    }
}
