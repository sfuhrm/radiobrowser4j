package de.sfuhrm.radiobrowser4j;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Discovers the radio browser API endpoint that suits the application.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class EndpointDiscovery {

    /** DNS address to resolve API endpoints.
     * */
    static final String DNS_API_ADDRESS =
            "all.api.radio-browser.info";

    /** The user agent to use for discovery. */
    private final String userAgent;

    /** The optional proxy URI. */
    private final String proxyUri;

    /** The optional proxy user. */
    private final String proxyUser;

    /** The optional proxy password. */
    private final String proxyPassword;

    /** Constructs a new instance.
     * @param myUserAgent the user agent String to use while discovery.
     * */
    public EndpointDiscovery(@NonNull final String myUserAgent) {
        this(myUserAgent, null, null, null);
    }

    /** Constructs a new instance.
     * @param myUserAgent the user agent String to use while discovery.
     * @param myProxyUri the optional URI of a HTTP proxy to use.
     * @param myProxyUser  the optional username to
     *                     authenticate with to access the proxy.
     * @param myProxyPassword the optional password
     *                        to authenticate with to access the proxy.
     * */
    public EndpointDiscovery(@NonNull final String myUserAgent,
                             final String myProxyUri,
                             final String myProxyUser,
                             final String myProxyPassword) {
        this.userAgent = myUserAgent;
        this.proxyUri = myProxyUri;
        this.proxyUser = myProxyUser;
        this.proxyPassword = myProxyPassword;
    }

    /** Get the URLs of all API endpoints that are returned by the DNS service.
     * @return the list of possible API endpoints as per DNS request.
     * Not all returned API endpoints may be working.
     * @throws UnknownHostException if there is a problem resolving the
     * API DNS name.
     * */
    List<String> apiUrls() throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName(DNS_API_ADDRESS);
        List<String> fqdns = new ArrayList<>();
        for (InetAddress inetAddress : addresses) {
            fqdns.add(inetAddress.getCanonicalHostName());
        }
        return fqdns.stream()
                .map(s -> String.format("https://%s/", s))
                .collect(Collectors.toList());
    }

    /** The default number of threads for discovery. */
    static final int DEFAULT_THREADS = 10;
    /** The default timeout for network connecting and reading for discovery. */
    static final int DEFAULT_TIMEOUT_MILLIS = 5000;

    /** A discovery for one API endpoint. */
    @Value
    static class DiscoveryResult {
        /** The endpoint address for this result. */
        private String endpoint;
        /** The connection and retrieval duration in milliseconds. */
        private long duration;
        /** The stats read from the endpoint. */
        private Stats stats;
    }

    /**
     * Do a discovery of the API endpoints.
     * @param apiUrls the possible API urls, see {@link #apiUrls()}.
     * @return the data about the discovered endpoints.
     * Unreachable endpoints are not returned.
     * */
    List<DiscoveryResult> discoverApiUrls(final List<String> apiUrls) {
        ExecutorService executorService =
                Executors.newFixedThreadPool(DEFAULT_THREADS);

        try {
            List<Future<DiscoveryResult>> futureList = new ArrayList<>();
            for (final String apiUrl : apiUrls) {
                Callable<DiscoveryResult> discoveryResultCallable = () -> {
                    long start = System.currentTimeMillis();
                    log.debug("Starting check for {}", apiUrl);
                    Stats stats = getStats(apiUrl, DEFAULT_TIMEOUT_MILLIS);
                    long duration = System.currentTimeMillis() - start;
                    log.debug("Finished check for {}, took {} ms",
                            apiUrl, duration);
                    return new DiscoveryResult(apiUrl, duration, stats);
                };
                futureList.add(executorService.submit(discoveryResultCallable));
            }

            List<DiscoveryResult> discoveryResults = new ArrayList<>();
            for (Future<DiscoveryResult> future : futureList) {
                try {
                    DiscoveryResult discoveryResult =
                            future.get(
                                    DEFAULT_TIMEOUT_MILLIS,
                                    TimeUnit.MILLISECONDS);
                    discoveryResults.add(discoveryResult);
                } catch (ExecutionException
                        | TimeoutException | InterruptedException e) {
                    log.warn("Endpoint "
                            + (apiUrls.get(futureList.indexOf(future)))
                            + " had an exception", e);
                }
            }
            return discoveryResults;
        } finally {
            executorService.shutdown();
        }
    }

    /** Get the stats for a specific API endpoint.
     * @param endpoint the API endpoint URI.
     * @param timeout the timeout in millis.
     * @return the stats object from the server.
     */
    Stats getStats(final String endpoint, final int timeout) {
        if (timeout <= 0) {
            throw new IllegalArgumentException(
                    "timeout must be > 0, but is "
                            + timeout);
        }

        Client client = RestClientFactory.newClient(timeout,
                proxyUri,
                proxyUser,
                proxyPassword);
        WebTarget webTarget = client.target(endpoint);
        return webTarget.path("json/stats")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .get(Stats.class);
    }

    /** Discovers the best performing endpoint.
     * @return an optional endpoint address that can be passed to
     * the {@link RadioBrowser} constructors.
     * @throws IOException when there is an IO problem while discovery.
     * */
    public Optional<String> discover() throws IOException {
        List<DiscoveryResult> discoveryResults = discoverApiUrls(apiUrls());

        return discoveryResults
                .stream()
                .sorted(Comparator.comparingLong(o -> o.duration))
                .map(DiscoveryResult::getEndpoint)
                .findFirst();
    }
}
