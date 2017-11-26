/*
* Copyright 2017 Stephan Fuhrmann
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package de.sfuhrm.radiobrowser4j;

import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.jackson.JacksonFeature;

/** API facade for the RadioBrowser.
 * You usually create a new {@linkplain #RadioBrowser(int, String) instance}
 * and then use the methods to invoke API calls.
 * @author Stephan Fuhrmann
 * */
@Slf4j
public final class RadioBrowser {

    /** The base URL of the REST service.
     * */
    static final String API_URL = "http://www.radio-browser.info/webservice/";

    /** The JAX-RS web target for service access. */
    private final WebTarget webTarget;

    /** The user agent name. */
    private final String userAgent;

    /** Custom constructor for mocked unit testing.
     * @param apiUrl the base URL of the API.
     * @param timeout the timeout in milliseconds for connecting
     *                and reading.
     * @param myUserAgent the user agent string to use.
     * */
    RadioBrowser(final String apiUrl,
                         final int timeout,
                         final String myUserAgent) {
        if (timeout <= 0) {
            throw new IllegalArgumentException(
                    "timeout must be > 0, but is "
                            + timeout);
        }
        this.userAgent = Objects.requireNonNull(myUserAgent,
                "User agent is null");
        Client client = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .build();
        client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
        client.property(ClientProperties.READ_TIMEOUT,    timeout);
        webTarget = client.target(apiUrl);
    }


    /**
     * Creates a new API client.
     * @param timeout the timeout for connect and read requests in milliseconds.
     *                Must be greater than zero.
     * @param myUserAgent the user agent String for your user agent.
     *                  Must be something to point to you.
     */
    public RadioBrowser(final int timeout,
                        final String myUserAgent) {
        this(API_URL, timeout, myUserAgent);
    }

    /**
     * Transfer the paging parameters the the passed multi valued map.
     * @param paging the source of the paging params.
     * @param requestParams the target of the paging params.
     * */
    private static void applyPaging(final Paging paging,
                    final MultivaluedMap<String, String> requestParams) {
        Objects.requireNonNull(paging, "Paging must be non-null");
        log.info("paging={}", paging);
        requestParams.put("limit", Collections.singletonList(
                Integer.toString(paging.getLimit())));
        requestParams.put("offset", Collections.singletonList(
                Integer.toString(paging.getOffset())));
    }

    /** Retrieve a generic list containing a value/stationcount mapping.
     * @param subpath the API sub path to use for the call.
     * @return map of value and stationcount pairs.
     * */
    private Map<String, Integer> retrieveValueStationCountList(
            final String subpath) {
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        Entity entity = Entity.form(requestParams);

        Response response = null;
        try {
            response = webTarget.path(subpath)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("User-Agent", userAgent)
                    .post(entity);

            List<Map<String, String>> map = response.readEntity(
                    new GenericType<List<Map<String, String>>>() {
            });

            Map<String, Integer> result = map.stream()
                    .collect(Collectors.toMap(
                    m -> m.get("value"),
                    m -> Integer.parseInt(m.get("stationcount"))));
            return result;
        } finally {
            close(response);
        }
    }

    /** List the known countries.
     * @return a list of countries (keys) and country usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_countries">
     *     API</a>
     * */
    public Map<String, Integer> listCountries() {
        return retrieveValueStationCountList("json/countries");
    }

    /** List the known codecs.
     * @return a list of codecs (keys) and codec usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_codecs">
     *     API</a>
     * */
    public Map<String, Integer> listCodecs() {
        return retrieveValueStationCountList("json/codecs");
    }

    /** List the known languages.
     * @return a list of languages (keys) and language usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_languages">
     *     API</a>
     * */
    public Map<String, Integer> listLanguages() {
        return retrieveValueStationCountList("json/languages");
    }

    /** List the known tags.
     * @return a list of tags (keys) and tag usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_tags">
     *     API</a>
     * */
    public Map<String, Integer> listTags() {
        return retrieveValueStationCountList("json/tags");
    }

    /** Get a list of all stations on a certain API path.
     * @param paging the offset and limit of the page to retrieve.
     * @param path the path to retrieve, i.e. "json/stations"
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    private List<Station> listStationsPath(final Optional<Paging> paging,
                                           final String path) {
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        paging.ifPresent(p -> applyPaging(p, requestParams));
        Entity entity = Entity.form(requestParams);
        Response response = null;
        try {
            response = webTarget.path(path)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("User-Agent", userAgent)
                    .post(entity);
            log.debug("response status={}, length={}",
                    response.getStatus(),
                    response.getLength());

            return response.readEntity(new GenericType<List<Station>>() {
            });
        } finally {
            close(response);
        }
    }

    /** Get a list of all stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations");
    }

    /** Get a list of all broken stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the broken stations. Can be empty
     * for exceeding the possible stations.
     */
    public List<Station> listBrokenStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/broken");
    }

    /** Get a list of all broken stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the broken stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listImprovableStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/improvable");
    }

    /** Get a list of the top click stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the top click stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listTopClickStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/topclick");
    }

    /** Get a list of the top vote stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the top vote stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listTopVoteStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/topvote");
    }

    /** Get a list of the last clicked stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the last clicked stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listLastClickStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/lastclick");
    }

    /** Get a list of the last clicked stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the last clicked stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listLastChangedStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/lastchange");
    }

    /** Get a list of the deleted stations. Will return a single batch.
     * @return the partial list of the deleted stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listDeletedStations() {
        return listStationsPath(Optional.empty(),
                "json/stations/deleted");
    }

    /** Get a list of stations matching a certain search criteria.
     * Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @param searchMode the field to match.
     * @param searchTerm the term to search for.
     * @return the partial list of the stations. Can be empty for exceeding the
     * number of matching stations.
     */
    public List<Station> listStationsBy(final Paging paging,
                                        final SearchMode searchMode,
                                        final String searchTerm) {
        Objects.requireNonNull(searchMode,
                "searchMode must be non-null");
        Objects.requireNonNull(searchTerm,
                "searchTerm must be non-null");

        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();
        applyPaging(paging, requestParams);
        Entity entity = Entity.form(requestParams);
        Response response = null;

        try {
            response = webTarget
                    .path("json/stations")
                    .path(searchMode.name())
                    .path(searchTerm)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("User-Agent", userAgent)
                    .post(entity);
            log.debug("response status={}, length={}",
                    response.getStatus(),
                    response.getLength());

            return response.readEntity(new GenericType<List<Station>>() {
            });
        } finally {
            close(response);
        }
    }

    /** Resolves the streaming URL for the given station.
     * @param station the station to retrieve the stream URL for.
     * @return the URL response that either describes the error or the result.
     */
    public UrlResponse resolveStreamUrl(final Station station) {
        Objects.requireNonNull(station, "station must be non-null");

        Response response = null;
        try {
            response = webTarget.path("v2/json/url").path(station.getId())
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("User-Agent", userAgent)
                    .get();

            log.debug("URI is {}", webTarget.getUri());
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                log.warn("Non 200 status: {} {}",
                        response.getStatus(),
                        response.getStatusInfo().getReasonPhrase());
                throw new RadioBrowserException(
                        response.getStatusInfo().getReasonPhrase());
            }
            return response.readEntity(UrlResponse.class);
        } finally {
            close(response);
        }
    }

    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * @param station the station to add to the REST service.
     * @see <a href="http://www.radio-browser.info/webservice#add_station">
     *     The API endpoint</a>
     */
    public void postNewStation(final Station station) {
        // http://www.radio-browser.info/webservice/json/add
        Objects.requireNonNull(station, "station must be non-null");
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();
        requestParams.put("name", Collections.singletonList(station.getName()));
        requestParams.put("url", Collections.singletonList(station.getUrl()));

        if (station.getHomepage() != null) {
            requestParams.put("homepage",
                    Collections.singletonList(station.getHomepage()));
        }
        if (station.getFavicon() != null) {
            requestParams.put("favicon",
                    Collections.singletonList(station.getFavicon()));
        }
        if (station.getCountry() != null) {
            requestParams.put("country",
                    Collections.singletonList(station.getCountry()));
        }
        if (station.getState() != null) {
            requestParams.put("state",
                    Collections.singletonList(station.getState()));
        }
        if (station.getLanguage() != null) {
            requestParams.put("language",
                    Collections.singletonList(station.getLanguage()));
        }
        if (station.getTags() != null) {
            requestParams.put("tags",
                    Collections.singletonList(station.getTags()));
        }
        Entity entity = Entity.form(requestParams);

        Response response = null;

        try {
            response = webTarget.path("json/add")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .header("User-Agent", userAgent)
                    .post(entity);

            Map<String, Object> map = response.readEntity(
                    new GenericType<Map<String, Object>>() { });


            if (log.isDebugEnabled()) {
                log.debug("Result: {}", map);
            }
        } finally {
            close(response);
        }
    }

    /** Close the response if non-null.
     * @param response the response to close.
     * */
    private static void close(final Response response) {
        if (response != null) {
            response.close();
        }
    }
}
