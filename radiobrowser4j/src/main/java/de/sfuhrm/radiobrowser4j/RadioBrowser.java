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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
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

    /** The base URL of the REST service. */
    protected static final String DEFAULT_API_URL =
            "https://de1.api.radio-browser.info/";

    /**
     * @deprecated There are multiple API URLs at the moment.
     * In the past this was a single URL.
     * Reference for compatibility for legacy clients.
     * */
    @Deprecated
    protected static final String API_URL = DEFAULT_API_URL;

    /** The JAX-RS web target for service access. */
    private final WebTarget webTarget;

    /** The user agent name. */
    private final String userAgent;

    /**
     * Creates a new API client.
     * @param apiUrl the base URL of the API.
     * @param timeout the timeout in milliseconds for connecting
     *                and reading.
     * @param myUserAgent the user agent string to use.
     * */
    public RadioBrowser(final String apiUrl,
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
        this(DEFAULT_API_URL, timeout, myUserAgent);
    }

    /** Creates a builder from the given web target
     * applying the standard request and accept
     * types.
     * @param in the web target to create a builder from.
     * @return a invocation builder that is built from the web target.
     * */
    private Invocation.Builder builder(final WebTarget in) {
        return in.request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent);
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
            response = builder(webTarget.path(subpath))
                    .post(entity);

            List<Map<String, String>> map = response.readEntity(
                    new GenericType<List<Map<String, String>>>() {
            });
            checkResponseStatus(response);
            return map.stream()
                    .collect(Collectors.toMap(
                    m -> m.get("value"),
                    m -> Integer.parseInt(m.get("stationcount"))));
        } finally {
            close(response);
        }
    }

    /** List the known countries.
     * @return a list of countries (keys) and country usages (values).
     * @see <a href="https://de1.api.radio-browser.info/#List_of_countries">
     *     API</a>
     * */
    public Map<String, Integer> listCountries() {
        return retrieveValueStationCountList("json/countries");
    }

    /** List the known codecs.
     * @return a list of codecs (keys) and codec usages (values).
     * @see <a href="https://de1.api.radio-browser.info/#List_of_codecs">
     *     API</a>
     * */
    public Map<String, Integer> listCodecs() {
        return retrieveValueStationCountList("json/codecs");
    }

    /** List the known languages.
     * @return a list of languages (keys) and language usages (values).
     * @see <a href="https://de1.api.radio-browser.info/#List_of_languages">
     *     API</a>
     * */
    public Map<String, Integer> listLanguages() {
        return retrieveValueStationCountList("json/languages");
    }

    /** List the known tags.
     * @return a list of tags (keys) and tag usages (values).
     * @see <a href="https://de1.api.radio-browser.info/#List_of_tags">
     *     API</a>
     * */
    public Map<String, Integer> listTags() {
        return retrieveValueStationCountList("json/tags");
    }

    /** Get a list of all stations on a certain API path.
     * @param paging the offset and limit of the page to retrieve.
     * @param path the path to retrieve, for example "json/stations".
     * @param listParam the optional listing parameters.
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    private List<Station> listStationsPath(final Optional<Paging> paging,
                                           final String path,
                                           final ListParameter...listParam) {
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        paging.ifPresent(p -> applyPaging(p, requestParams));
        Arrays.stream(listParam).forEach(lp -> lp.applyTo(requestParams));
        Entity entity = Entity.form(requestParams);
        Response response = null;
        try {
            response = builder(webTarget.path(path))
                    .post(entity);
            checkResponseStatus(response);

            return response.readEntity(new GenericType<List<Station>>() {
            });
        } finally {
            close(response);
        }
    }

    /** Get a list of all stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @param listParam the optional listing parameters.
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listStations(final Paging paging,
                                      final ListParameter...listParam) {
        return listStationsPath(Optional.of(paging),
                "json/stations",
                listParam);
    }

    /** Get a list of all stations. Will return all
     * stations in a stream..
     * @param listParam the optional listing parameters.
     * @return the full stream of stations..
     */
    public Stream<Station> listStations(final ListParameter...listParam) {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStations(p, listParam)),
                false);
    }

    /** Get a list of all broken stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the broken stations. Can be empty
     * for exceeding the possible stations.
     */
    public List<Station> listBrokenStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/broken"
                );
    }

    /** Get a list of all broken stations as one continuous stream.
     * @return the continuous stream of all broken stations.
     */
    public Stream<Station> listBrokenStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPath(Optional.of(p),
                                "json/stations/broken")),
                false);
    }

    /** Get a list of all improvable stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the improvable stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listImprovableStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/improvable");
    }

    /** Get a list of all broken stations as one continuous stream.
     * @return the continuous stream of all improvable stations.
     */
    public Stream<Station> listImprovableStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPath(Optional.of(p),
                                "json/stations/improvable")),
                false);
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

    /** Get a stream of all top click stations.
     * @return the complete stream of all top click stations.
     */
    public Stream<Station> listTopClickStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPath(Optional.of(p),
                                "json/stations/topclick")),
                false);
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

    /** Get a stream of the top vote stations.
     * @return the complete stream of the top vote stations.
     */
    public Stream<Station> listTopVoteStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPath(Optional.of(p),
                                "json/stations/topvote")),
                false);
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

    /** Get a stream of last clicked stations.
     * @return the complete stream of the last clicked stations.
     */
    public Stream<Station> listLastClickStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPath(Optional.of(p),
                                "json/stations/lastclick")),
                false);
    }

    /** Get a list of the last changed stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the last clicked stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listLastChangedStations(final Paging paging) {
        return listStationsPath(Optional.of(paging),
                "json/stations/lastchange");
    }

    /** Get a stream of last changed stations.
     * @return the complete stream of the last changed stations.
     */
    public Stream<Station> listLastChangedStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPath(Optional.of(p),
                                "json/stations/lastchange")),
                false);
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

    /** Get a station referenced by the ID.
     * @param id the id of the station to retrieve.
     * @return an optional containing either the station or nothing.
     * Nothing is returned if the API didn't find the station by the
     * given ID:
     */
    public Optional<Station> getStationById(final String id) {
        Objects.requireNonNull(id, "id must be non-null");
        List<Station> stationList = listStationsBy(
                Paging.at(0, 1),
                SearchMode.byid,
                        id);
        if (stationList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(stationList.get(0));
        }
    }

    /** Get a list of stations matching a certain search criteria.
     * Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @param searchMode the field to match.
     * @param searchTerm the term to search for.
     * @param listParam the optional listing parameters.
     * @return the partial list of the stations. Can be empty for exceeding the
     * number of matching stations.
     */
    public List<Station> listStationsBy(final Paging paging,
                                        final SearchMode searchMode,
                                        final String searchTerm,
                                        final ListParameter...listParam) {
        Objects.requireNonNull(searchMode,
                "searchMode must be non-null");
        Objects.requireNonNull(searchTerm,
                "searchTerm must be non-null");

        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();
        applyPaging(paging, requestParams);
        Arrays.stream(listParam).forEach(l -> l.applyTo(requestParams));
        Entity entity = Entity.form(requestParams);
        Response response = null;

        try {
            response = builder(webTarget
                       .path("json/stations")
                       .path(searchMode.name())
                       .path(searchTerm))
                    .post(entity);
            checkResponseStatus(response);
            return response.readEntity(new GenericType<List<Station>>() {
            });
        } finally {
            close(response);
        }
    }

    /** Get a stream of stations matching a certain search criteria.
     * @param searchMode the field to match.
     * @param searchTerm the term to search for.
     * @param listParam the optional listing parameters.
     * @return the full stream of matching stations.
     */
    public Stream<Station> listStationsBy(final SearchMode searchMode,
                                        final String searchTerm,
                                        final ListParameter...listParam) {
        Objects.requireNonNull(searchMode,
                "searchMode must be non-null");
        Objects.requireNonNull(searchTerm,
                "searchTerm must be non-null");

        Function<Paging, List<Station>> fetcher = p -> {
            MultivaluedMap<String, String> requestParams =
                    new MultivaluedHashMap<>();
            applyPaging(p, requestParams);
            Arrays.stream(listParam).forEach(l -> l.applyTo(requestParams));
            Entity entity = Entity.form(requestParams);
            Response response = null;

            try {
                response = builder(webTarget
                        .path("json/stations")
                        .path(searchMode.name())
                        .path(searchTerm))
                        .post(entity);
                checkResponseStatus(response);
                return response.readEntity(new GenericType<List<Station>>() {
                });
            } finally {
                close(response);
            }
        };

        return StreamSupport.stream(
                new PagingSpliterator<>(
                        fetcher), false);
    }

    /** Resolves the streaming URL for the given station.
     * @param station the station to retrieve the stream URL for.
     * @return the URL of the stream.
     * @throws RadioBrowserException if the URL could not be retrieved
     */
    public URL resolveStreamUrl(final Station station) {
        Objects.requireNonNull(station, "station must be non-null");

        Response response = null;
        try {
            response = builder(webTarget.path("v2/json/url")
                    .path(station.getId()))
                    .get();

            checkResponseStatus(response);
            log.debug("URI is {}", webTarget.getUri());
            try {
                UrlResponse urlResponse = response.readEntity(
                        UrlResponse.class);
                if (!urlResponse.isOk()) {
                    throw new RadioBrowserException(urlResponse.getMessage());
                }
                return new URL(urlResponse.getUrl());
            } catch (MalformedURLException e) {
                throw new RadioBrowserException(e);
            }
        } finally {
            close(response);
        }
    }

    /**
     * Calls a state alteration method for one station.
     * @param station the station to undelete/delete from the REST service.
     * @param path the URL path of the state alteration endpoint.
     */
    private void triggerStationState(final Station station,
                                     final String path) {
        Objects.requireNonNull(station, "station must be non-null");
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        Entity entity = Entity.form(requestParams);

        Response response = null;

        try {
            response = builder(webTarget
                        .path(path)
                        .path(station.getId()))
                    .post(entity);
            logResponseStatus(response);
            UrlResponse urlResponse = response.readEntity(
                    UrlResponse.class);
            if (!urlResponse.isOk()) {
                throw new RadioBrowserException(urlResponse.getMessage());
            }
        } finally {
            close(response);
        }
    }

    /** Deletes a station.
     * The station is only marked as being deleted.
     * @param station the station to delete from the REST service.
     */
    public void deleteStation(final Station station) {
        triggerStationState(station, "json/delete");
    }

    /** Undeletes a station.
     * The station is only marked as being deleted.
     * @param station the station to delete from the REST service.
     */
    public void undeleteStation(final Station station) {
        triggerStationState(station, "json/undelete");
    }

    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * The fields are:
     * name, url, homepage, favicon, country, state, language and tags.
     * @param station the station to add to the REST service.
     * @return the {@linkplain Station#id id} of the new station.
     * @throws RadioBrowserException if there was a problem
     * creating the station.
     * @see <a href="https://de1.api.radio-browser.info/#Add_radio_station">
     *     The API endpoint</a>
     */
    public String postNewStation(final Station station) {
        return postNewOrEditStation(station, "json/add");
    }

    /** Edits an existing station on the server.
     * Note: This call only transmits certain fields.
     * The fields are:
     * name, url, homepage, favicon, country, state, language and tags.
     * @param station the station to edit with the REST service.
     * @return the {@linkplain Station#id id} of the station.
     * @throws RadioBrowserException if there was a problem
     * editing the station.
     */
    public String editStation(final Station station) {
        Objects.requireNonNull(station.getId(), "id must be non-null");
        return postNewOrEditStation(station, "json/edit/" + station.getId());
    }

    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * The fields are:
     * name, url, homepage, favicon, country, state, language and tags.
     * @param station the station to add to the REST service.
     * @param path the path of the new / edit call.
     * @return the {@linkplain Station#id id} of the new station.
     * @throws RadioBrowserException if there was a problem
     * creating the station.
     */
    private String postNewOrEditStation(final Station station,
                                        final String path) {
        Objects.requireNonNull(station, "station must be non-null");
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();
        transferToMultivaluedMap(station, requestParams);
        Entity entity = Entity.form(requestParams);

        Response response = null;
        try {
            response = builder(webTarget
                    .path(path))
                    .post(entity);

            logResponseStatus(response);
            UrlResponse urlResponse = response.readEntity(
                    UrlResponse.class);

            if (log.isDebugEnabled()) {
                log.debug("Result: {}", urlResponse);
            }

            if (!urlResponse.isOk()) {
                throw new RadioBrowserException(urlResponse.getMessage());
            }

            return urlResponse.getId();
        } finally {
            close(response);
        }
    }

    /** Transfers all parameters for a new station to the given target params.
     * @param sourceStation the station to get fields from.
     * @param targetParams the target multi valued map to write the
     *                     request params to.
     * */
    private static void transferToMultivaluedMap(final Station sourceStation,
                     final MultivaluedMap<String, String> targetParams) {
        targetParams.put("name",
                Collections.singletonList(sourceStation.getName()));
        targetParams.put("url",
                Collections.singletonList(sourceStation.getUrl()));

        if (sourceStation.getHomepage() != null) {
            targetParams.put("homepage",
                    Collections.singletonList(sourceStation.getHomepage()));
        }
        if (sourceStation.getFavicon() != null) {
            targetParams.put("favicon",
                    Collections.singletonList(sourceStation.getFavicon()));
        }
        if (sourceStation.getCountry() != null) {
            targetParams.put("country",
                    Collections.singletonList(sourceStation.getCountry()));
        }
        if (sourceStation.getState() != null) {
            targetParams.put("state",
                    Collections.singletonList(sourceStation.getState()));
        }
        if (sourceStation.getLanguage() != null) {
            targetParams.put("language",
                    Collections.singletonList(sourceStation.getLanguage()));
        }
        if (sourceStation.getTags() != null) {
            targetParams.put("tagList",
                    Collections.singletonList(
                            sourceStation.getTags()));
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

    /** Close the response if non-null.
     * @param response the response to close.
     * */
    private static void close(final Response response) {
        if (response != null) {
            response.close();
        }
    }
}
