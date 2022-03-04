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
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
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
    protected static final String DEFAULT_API_URL =
            "https://de1.api.radio-browser.info/";

    /** The JAX-RS web target for service access. */
    private final WebTarget webTarget;

    /** The user agent name. */
    private final String userAgent;

    /**
     * Creates a new API client.
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
     * Creates a new API client.
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
    }

    /**
     * Creates a new API client.
     * @param timeout the timeout for connect and read requests in milliseconds.
     *                Must be greater than zero.
     * @param myUserAgent the user agent String for your user agent.
     *                    Please use a user agent string that somehow
     *                    points to your github project,
     *                    home page, or what ever.
     */
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
                .header("User-Agent", userAgent);
    }

    /**
     * Transfer the paging parameters to the passed multi-valued-map.
     * @param paging the source of the paging params.
     * @param requestParams the target of the paging params.
     * */
    private static void applyPaging(@NonNull final Paging paging,
                    final MultivaluedMap<String, String> requestParams) {
        log.info("paging={}", paging);
        requestParams.put("limit", Collections.singletonList(
                Integer.toString(paging.getLimit())));
        requestParams.put("offset", Collections.singletonList(
                Integer.toString(paging.getOffset())));
    }

    /** Retrieve a generic list containing a value/stationcount mapping.
     * @param subPath the API sub path to use for the call.
     * @return map of value and stationcount pairs.
     * */
    private Map<String, Integer> retrieveValueStationCountList(
            final String subPath) {
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        Entity<Form> entity = Entity.form(requestParams);

        Response response = null;
        try {
            response = builder(webTarget.path(subPath))
                    .post(entity);

            List<Map<String, String>> map = response.readEntity(
                    new GenericType<List<Map<String, String>>>() {
            });
            checkResponseStatus(response);
            return map.stream()
                    .collect(Collectors.toMap(
                    m -> m.get("name"),
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
    private List<Station> listStationsPathWithPaging(
            final Optional<Paging> paging,
            final String path,
            final ListParameter...listParam) {
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        paging.ifPresent(p -> applyPaging(p, requestParams));
        Arrays.stream(listParam).forEach(lp -> lp.applyTo(requestParams));
        Entity<Form> entity = Entity.form(requestParams);
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
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();

        Arrays.stream(listParam).forEach(lp -> lp.applyTo(requestParams));
        Entity<Form> entity = Entity.form(requestParams);
        Response response = null;
        try {
            WebTarget target = webTarget.path(path);
            if (limit.isPresent()) {
                target = target.path(Integer.toString(limit.get().getSize()));
            }
            response = builder(target)
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
     * @return the partial list of the improvable stations.
     * Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listImprovableStations(@NonNull final Limit limit) {
        return listStationsPathWithLimit(Optional.of(limit),
                "json/stations/improvable");
    }

    /** Get a list of all broken stations as one continuous stream.
     * @return the continuous stream of all improvable stations.
     */
    public Stream<Station> listImprovableStations() {
        return StreamSupport.stream(
                new PagingSpliterator<>(
                        p -> listStationsPathWithPaging(Optional.of(p),
                                "json/stations/improvable")),
                false);
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
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();
        applyPaging(paging, requestParams);
        Arrays.stream(listParam).forEach(l -> l.applyTo(requestParams));
        Entity<Form> entity = Entity.form(requestParams);
        Response response = null;

        try {
            response = builder(webTarget
                       .path("json/stations")
                       .path(searchMode.name().toLowerCase())
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
    public Stream<Station> listStationsBy(
            @NonNull final SearchMode searchMode,
            @NonNull final String searchTerm,
            final ListParameter...listParam) {

        Function<Paging, List<Station>> fetcher = p -> {
            MultivaluedMap<String, String> requestParams =
                    new MultivaluedHashMap<>();
            applyPaging(p, requestParams);
            Arrays.stream(listParam).forEach(l -> l.applyTo(requestParams));
            Entity<Form> entity = Entity.form(requestParams);
            Response response = null;

            try {
                response = builder(webTarget
                        .path("json/stations")
                        .path(searchMode.name().toLowerCase())
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
     * @param stationUUID the station UUID to retrieve the stream URL for.
     * @return the URL of the stream.
     * @throws RadioBrowserException if the URL could not be retrieved
     */
    public URL resolveStreamUrl(@NonNull final UUID stationUUID) {
        Response response = null;
        try {
            response = builder(webTarget.path("v2/json/url")
                    .path(stationUUID.toString()))
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

    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * The fields are:
     * name, url, homepage, favicon, country, state, language and tags.
     * @param station the station to add to the REST service.
     * @return the {@linkplain Station#getStationUUID() id} of the new station.
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
        Response response = null;
        try {
            response = builder(webTarget
                    .path("json/vote/").path(stationUUID.toString()))
                    .get();

            logResponseStatus(response);
            UrlResponse urlResponse = response.readEntity(UrlResponse.class);

            if (!urlResponse.isOk()) {
                throw new RadioBrowserException(urlResponse.getMessage());
            }
        } finally {
            close(response);
        }
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
        MultivaluedMap<String, String> requestParams =
                new MultivaluedHashMap<>();
        transferToMultivaluedMap(station, requestParams);
        Entity<Form> entity = Entity.form(requestParams);

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

            return urlResponse.getUuid();
        } finally {
            close(response);
        }
    }

    /** Transfers all parameters for a new station to the given target params.
     * @param sourceStation the station to get fields from.
     * @param targetParams the target multi-valued-map to write the
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
        sourceStation.getLanguage();
        targetParams.put("language",
                Collections.singletonList(sourceStation.getLanguage()));
        sourceStation.getTags();
        targetParams.put("tagList",
                Collections.singletonList(
                        sourceStation.getTags()));
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
