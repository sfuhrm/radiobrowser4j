package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import java.util.concurrent.TimeUnit;

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
        ClientBuilder clientBuilder = ClientBuilder.newBuilder()
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS);

        if (proxyUri != null) {
            if (clientBuilder instanceof ResteasyClientBuilder) {
                clientBuilder =
                        ((ResteasyClientBuilder) clientBuilder)
                                .defaultProxy(proxyUri);
            }
        }

        return clientBuilder.build();
    }
}
