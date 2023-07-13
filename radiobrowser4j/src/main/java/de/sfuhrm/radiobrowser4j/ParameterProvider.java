package de.sfuhrm.radiobrowser4j;

import jakarta.ws.rs.core.MultivaluedMap;

/** A provider for HTTP request parameters. */
abstract class ParameterProvider {
    /** Applies the parameters stored in this instance to the given
     * request parameters.
     * @param requestParams the parameters to apply the instance
     *                      content to.
     * */
    protected abstract void apply(MultivaluedMap<String, String> requestParams);
}
