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

import jakarta.ws.rs.core.HttpHeaders;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** API facade for the RadioBrowser.
 * You usually create a new {@linkplain #RadioBrowser(int, String) instance}
 * and then use the methods to invoke API calls.
 * @author Stephan Fuhrmann
 * */
@Slf4j
public final class RadioBrowser {

    /** The base URL of the REST service. */
    @Deprecated
    protected static final String DEFAULT_API_URL =
            "https://at1.api.radio-browser.info/";

    /** The JAX-RS web target for service access. */
    private final WebTarget webTarget;

    /** REST implementation. */
    private final RestImpl rest;

    /** The user agent name. */
    private final String userAgent;

    /**
     * Creates a new API client using a specific api endpoint.
     * @param apiUrl the base URL of the API.
     *               Can be determined by
     *               {@linkplain  EndpointDiscovery#discover()}
     *               or set to {@linkplain #DEFAULT_API_URL}.
     * @param timeout the timeout in milliseconds for connecting
     *                and reading.
     * @param myUserAgent the user agent string to use.
     *                    Please use a user agent string that somehow
     *                    points to your github project, home page,
     *                    or what ever.
     * */
    public RadioBrowser(@NonNull final String apiUrl,
                        final int timeout,
                        @NonNull final String myUserAgent) {
        this(apiUrl, timeout, myUserAgent, null, null, null);
    }

    /**
     * Creates a new API client using a proxy.
     * @param apiUrl the base URL of the API. Can be determined
     *               by {@linkplain  EndpointDiscovery#discover()}
     *               or set to {@linkplain #DEFAULT_API_URL}.
     * @param timeout the timeout in milliseconds for connecting
     *                and reading.
     * @param myUserAgent the user agent string to use.
     *                    Please use a user agent string that somehow
     *                    points to your github project,
     *                    home page, or what ever.
     * @param proxyUri optional URI of the proxy server, or {@code null}
     *                 if no proxy is required.
     * @param proxyUser optional username to authenticate
     *                  with if using a proxy.
     * @param proxyPassword optional password to authenticate
     *                      with if using a proxy
     * */
    public RadioBrowser(@NonNull final String apiUrl,
                         final int timeout,
                         @NonNull final String myUserAgent,
                        final String proxyUri,
                        final String proxyUser,
                        final String proxyPassword) {
        if (timeout <= 0) {
            throw new IllegalArgumentException(
                    "timeout must be > 0, but is "
                            + timeout);
        }
        this.userAgent = myUserAgent;
        Client client = RestClientFactory.newClient(
                timeout,
                proxyUri,
                proxyUser,
                proxyPassword);
        webTarget = client.target(apiUrl);
        rest = new RestImpl(URI.create(apiUrl), timeout, proxyUri, proxyUser, proxyPassword, myUserAgent);
    }

    /**
     * Creates a new API client using the default endpoint.
     * @param timeout the timeout for connect and read requests in milliseconds.
     *                Must be greater than zero.
     * @param myUserAgent the user agent String for your user agent.
     *                    Please use a user agent string that somehow
     *                    points to your github project,
     *                    home page, or what ever.
     * @deprecated This method is deprecated since it can use an
     * obsolete endpoint. Using the endpoint discovery
     * and passing the result to one of the other
     * constructors is recommended.
     */
    @Deprecated
    public RadioBrowser(final int timeout,
                        final String myUserAgent) {
        this(DEFAULT_API_URL, timeout, myUserAgent);
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

    /** Retrieve a generic list containing a value/stationcount mapping.
     * @param subPath the API sub path to use for the call.
     * @return map of value and stationcount pairs.
     * */
    private Map<String, Integer> retrieveValueStationCountList(
            final String subPath) {

        List<Map<String, String>> map =
                rest.post(subPath, Collections.emptyMap(),
                new GenericType<List<Map<String, String>>>() {});
        return map.stream()
                .collect(Collectors.toMap(
                    m -> m.get("name"),
                    m -> Integer.parseInt(m.get("stationcount")),
                        (a, b) -> a));
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
    private List<Station> listStationsPathWithPaging(
            final Optional<Paging> paging,
            final String path,
            final ListParameter...listParam) {
        Map<String, String> requestParams =
                new HashMap<>();

        paging.ifPresent(p -> p.apply(requestParams));
        Arrays.stream(listParam).forEach(lp -> lp.apply(requestParams));

        return rest.post(path, requestParams, new GenericType<List<Station>>() {
        });
    }

    /** Get a list of all stations on a certain API path.
     * @param limit the limit of the page to retrieve.
     * @param path the path to retrieve, for example "json/stations".
     * @param listParam the optional listing parameters.
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    private List<Station> listStationsPathWithLimit(
                                final Optional<Limit> limit,
                                final String path,
                                final ListParameter...listParam) {
        Map<String, String> requestParams =
                new HashMap<>();

        Arrays.stream(listParam).forEach(lp -> lp.apply(requestParams));
        String myPath = path;
        if (limit.isPresent()) {
            myPath = myPath + '/' + limit.get().getSize();
        }

        return rest.post(myPath,
                requestParams,
                new GenericType<List<Station>>() {});
    }

    /** Get a list of all stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @param listParam the optional listing parameters.
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listStations(@NonNull final Paging paging,
                                      final ListParameter...listParam) {
        return listStationsPathWithPaging(Optional.of(paging),
                "json/stations",
                listParam);
    }

    /** Get a list of all stations. Will return all
     * stations in a stream.
     * @param listParam the optional listing parameters.
     * @return the full stream of stations.
     */
    public Stream<Station> listStations(final ListParameter...listParam) {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStations(p, listParam)),
                false);
    }

    /** Get a list of all broken stations. Will return a single batch.
     * @param limit the limit of the page to retrieve.
     * @return the partial list of the broken stations. Can be empty
     * for exceeding the possible stations.
     */
    public List<Station> listBrokenStations(@NonNull final Limit limit) {
        return listStationsPathWithLimit(Optional.of(limit),
                "json/stations/broken"
                );
    }

    /** Get a list of all broken stations as one continuous stream.
     * @return the continuous stream of all broken stations.
     */
    public Stream<Station> listBrokenStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPathWithPaging(Optional.of(p),
                                "json/stations/broken")),
                false);
    }

    /** Get a list of all improvable stations. Will return a single batch.
     * @param limit the limit of the page to retrieve.
     * @return an empty list of improvable stations as a placeholder
     * for the removed API endpoint.
     * @deprecated This API endpoint has been removed.
     */
    public List<Station> listImprovableStations(@NonNull final Limit limit) {
        return Collections.emptyList();
    }

    /** Get a list of all broken stations as one continuous stream.
     * @return an empty stream of improvable stations as a placeholder
     * for the removed API endpoint.
     * @deprecated This API endpoint has been removed.
     */
    @Deprecated
    public Stream<Station> listImprovableStations() {
        return new ArrayList<Station>().stream();
    }

    /** Get a list of the top click stations. Will return a single batch.
     * @param limit the limit of the page to retrieve.
     * @return the partial list of the top click stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listTopClickStations(@NonNull final Limit limit) {
        return listStationsPathWithLimit(Optional.of(limit),
                "json/stations/topclick");
    }

    /** Get a stream of all top click stations.
     * @return the complete stream of all top click stations.
     */
    public Stream<Station> listTopClickStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPathWithPaging(Optional.of(p),
                                "json/stations/topclick")),
                false);
    }

    /** Get a list of the top vote stations. Will return a single batch.
     * @param limit the limit of the page to retrieve.
     * @return the partial list of the top vote stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listTopVoteStations(@NonNull final Limit limit) {
        return listStationsPathWithLimit(Optional.of(limit),
                "json/stations/topvote");
    }

    /** Get a stream of the top vote stations.
     * @return the complete stream of the top vote stations.
     */
    public Stream<Station> listTopVoteStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPathWithPaging(Optional.of(p),
                                "json/stations/topvote")),
                false);
    }

    /** Get a list of the last clicked stations. Will return a single batch.
     * @param limit the limit of the page to retrieve.
     * @return the partial list of the last clicked stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listLastClickStations(@NonNull final Limit limit) {
        return listStationsPathWithLimit(Optional.of(limit),
                "json/stations/lastclick");
    }

    /** Get a stream of last clicked stations.
     * @return the complete stream of the last clicked stations.
     */
    public Stream<Station> listLastClickStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPathWithPaging(Optional.of(p),
                                "json/stations/lastclick")),
                false);
    }

    /** Get a list of the last changed stations. Will return a single batch.
     * @param limit the limit of the page to retrieve.
     * @return the partial list of the last clicked stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listLastChangedStations(@NonNull final Limit limit) {
        return listStationsPathWithLimit(Optional.of(limit),
                "json/stations/lastchange");
    }

    /** Get a stream of last changed stations.
     * @return the complete stream of the last changed stations.
     */
    public Stream<Station> listLastChangedStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPathWithPaging(Optional.of(p),
                                "json/stations/lastchange")),
                false);
    }

    /** Get a station referenced by its UUID.
     * @param uuid the UUID of the station to retrieve.
     * @return an optional containing either the station or nothing.
     * Nothing is returned if the API didn't find the station by the
     * given ID.
     */
    public Optional<Station> getStationByUUID(@NonNull final UUID uuid) {
        List<Station> stationList = listStationsBy(
                Paging.at(0, 1),
                SearchMode.BYUUID,
                uuid.toString());
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
    public List<Station> listStationsBy(@NonNull final Paging paging,
                                        @NonNull final SearchMode searchMode,
                                        @NonNull final String searchTerm,
                                        final ListParameter...listParam) {
        Map<String, String> requestParams =
                new HashMap<>();
        paging.apply(requestParams);
        Arrays.stream(listParam).forEach(l -> l.apply(requestParams));

        String path = RestImpl.paths("json/stations", searchMode.name().toLowerCase(), searchTerm);
        return rest.post(path,
                requestParams,
                new GenericType<List<Station>>() {});
    }

    /** Get a stream of stations matching a certain search criteria.
     * @param searchMode the field to match.
     * @param searchTerm the term to search for.
     * @param listParam the optional listing parameters.
     * @return the full stream of matching stations.
     */
    public Stream<Station> listStationsBy(
            @NonNull final SearchMode searchMode,
            @NonNull final String searchTerm,
            final ListParameter...listParam) {

        Function<Paging, List<Station>> fetcher = p -> {
            Map<String, String> requestParams =
                    new HashMap<>();
            p.apply(requestParams);
            Arrays.stream(listParam).forEach(l -> l.apply(requestParams));

            String path = RestImpl.paths("json/stations",
                    searchMode.name().toLowerCase(),
                    searchTerm);

            return rest.post(path,
                    requestParams,
                    new GenericType<List<Station>>() {});
        };

        return StreamSupport.stream(
                new PagingSpliterator<>(
                        fetcher), false);
    }

    /** Resolves the streaming URL for the given station.
     * @param stationUUID the station UUID to retrieve the stream URL for.
     * @return the URL of the stream.
     * @throws RadioBrowserException if the URL could not be retrieved
     */
    public URL resolveStreamUrl(@NonNull final UUID stationUUID) {
        try (Response response = builder(webTarget.path("json/url")
                .path(stationUUID.toString()))
                .get()) {
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
        }
    }

    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * The fields are:
     * name, url, homepage, favicon, country, state, language and tags.
     * @param station the station to add to the REST service.
     * @return the uuid of the new station.
     * @throws RadioBrowserException if there was a problem
     * creating the station.
     * @see <a href="https://de1.api.radio-browser.info/#Add_radio_station">
     *     The API endpoint</a>
     */
    public UUID postNewStation(@NonNull final Station station) {
        return postNewOrEditStation(station, "json/add");
    }

    /**
     * Votes for a station.
     * @param stationUUID The uuid of the station to vote for.
     * @throws RadioBrowserException if there was a problem
     * voting for the station.
     */
    public void voteForStation(@NonNull final UUID stationUUID) {
        try (Response response = builder(webTarget
                .path("json/vote/").path(stationUUID.toString()))
                .get()) {

            logResponseStatus(response);
            UrlResponse urlResponse = response.readEntity(UrlResponse.class);

            if (!urlResponse.isOk()) {
                throw new RadioBrowserException(urlResponse.getMessage());
            }
        }
    }

    /** Get the server statistics.
     * @return the statistics for the configured server
     * endpoint.
     * */
    public Stats getServerStats() {
        Response response = builder(webTarget
                .path("json/stats")
        ).get();
        checkResponseStatus(response);
        return response.readEntity(Stats.class);
    }

    /** Get a stream of stations matching a certain search criteria.
     * @param advancedSearch the advanced search query object.
     *          A builder can be created by calling
     *          {@code AdvancedSearch.builder()},
     *          and then when you are finished
     *          {@code AdvancedSearch.AdvancedSearchBuilder.build()}.
     * @return the full stream of matching stations.
     */
    public Stream<Station> listStationsWithAdvancedSearch(
            @NonNull final AdvancedSearch advancedSearch) {

        Function<Paging, List<Station>> fetcher = p -> {
            Map<String, String> requestParams =
                    new HashMap<>();
            p.apply(requestParams);
            advancedSearch.apply(requestParams);
            Entity<Form> entity = Entity.form(
                    new MultivaluedHashMap<>(requestParams));

            try (Response response = builder(webTarget
                    .path("/json/stations/search"))
                    .post(entity)) {

                checkResponseStatus(response);
                return response.readEntity(new GenericType<List<Station>>() {
                });
            }
        };

        return StreamSupport.stream(
                new PagingSpliterator<>(
                        fetcher), false);
    }


    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * The fields are:
     * name, url, homepage, favicon, country, state, language and tags.
     * @param station the station to add to the REST service.
     * @param path the path of the new / edit call.
     * @return the {@linkplain Station#getStationUUID() id} of the new station.
     * @throws RadioBrowserException if there was a problem
     * creating the station.
     */
    private UUID postNewOrEditStation(@NonNull final Station station,
                                        final String path) {
        Map<String, String> requestParams =
                new HashMap<>();
        station.apply(requestParams);
        Entity<Form> entity = Entity.form(
                new MultivaluedHashMap<>(requestParams));

        try (Response response = builder(webTarget
                .path(path))
                .post(entity)) {

            logResponseStatus(response);
            UrlResponse urlResponse = response.readEntity(
                    UrlResponse.class);

            if (log.isDebugEnabled()) {
                log.debug("Result: {}", urlResponse);
            }

            if (!urlResponse.isOk()) {
                throw new RadioBrowserException(urlResponse.getMessage());
            }

            return urlResponse.getUuid();
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
}
