package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.GZipEncoder;

/** Delegate for creating a new JAX-RS client.
 * */
final class RestClientFactory {

    /** No instance allowed. */
    private RestClientFactory() {
    }

    /** Create a new JAX-RS client.
     * @param timeout connect / read timeout in milliseconds.
     * @param proxyUri optional proxy URI.
     * @param proxyUser optional proxy user.
     * @param proxyPassword optional proxy password.
     * @return the client instance that has been created.
     *  */
    static Client newClient(final int timeout,
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
}
