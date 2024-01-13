package de.sfuhrm.radiobrowser4j;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/** A tuple of parameters for creating API connections to the
 * radio browser API.
 * */
@Builder
@Getter(AccessLevel.PACKAGE)
class ConnectionParams {
    /** The URL of the radio browser API.
     * Must not be {@code null}.
     * */
    private final String apiUrl;

    /** The timeout for connect and read requests in milliseconds.
     *  Must be greater than zero. */
    private final int timeout;

    /** The user agent to use for identifying with the API.
     * Must not be {@code null}.
     * */
    private final String userAgent;

    /** The proxy URI to use. May be {@code null}.
     * */
    private final String proxyUri;

    /** The proxy user to use. May be {@code null}.
     * */
    private final String proxyUser;

    /** The proxy password to use. May be {@code null}.
     * */
    private final String proxyPassword;

    /** The wait interval between two retries. */
    private final long retryInterval = 1000;

    /** The number of retries on error. */
    private final int retries = 3;

    /** Checks the parameters.
     * @throws IllegalArgumentException if the parameters are invalid.
     * */
    void check() {
        if (apiUrl == null) {
            throw new IllegalArgumentException(
                    "apiUrl must not be null");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException(
                    "timeout must be > 0, but is "
                            + getTimeout());
        }
        if (userAgent == null) {
            throw new IllegalArgumentException(
                    "userAgent must not be null");
        }
        if (proxyUri != null) {
            if (proxyUser != null && proxyPassword == null) {
                throw new IllegalArgumentException(
                        "proxyUser was given, but not a proxyPassword");
            }
            if (proxyUser == null && proxyPassword != null) {
                throw new IllegalArgumentException(
                        "proxyUser was not given, but a proxyPassword");
            }
        }
    }
}
