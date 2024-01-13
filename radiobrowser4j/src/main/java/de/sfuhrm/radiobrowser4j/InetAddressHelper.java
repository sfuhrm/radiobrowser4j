package de.sfuhrm.radiobrowser4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** Helper class for simplifying testing.
 * @author Stephan Fuhrmann
 * */
class InetAddressHelper {

    /**
     * Gets all addresses by the host name.
     * @param host the FQDN to resolve.
     * @return the addresses that could be resolved.
     * @throws UnknownHostException if the host is not known.
     *  @see InetAddress#getAllByName(String)
     * */
    InetAddress[] getAllByName(final String host) throws UnknownHostException {
        return InetAddress.getAllByName(host);
    }
}
