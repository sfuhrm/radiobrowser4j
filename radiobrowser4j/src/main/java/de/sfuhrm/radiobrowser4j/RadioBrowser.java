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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

@Slf4j
public class RadioBrowser {

    /** The base URL of the REST service.
     * */
    final static String API_URL = "http://www.radio-browser.info/webservice/";

    /** The JAX-RS web target for service access. */
    private final WebTarget webTarget;

    /** The user agent name. */
    private final String userAgent;

    /** Custom constructor for mocked unit testing.
     * */
    RadioBrowser(final String apiUrl,
                         final int timeout,
                         final String userAgent) {
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout must be > 0, but is "+timeout);
        }
        this.userAgent = Objects.requireNonNull(userAgent, "User agent is null");
        Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
        client.property(ClientProperties.CONNECT_TIMEOUT, timeout);
        client.property(ClientProperties.READ_TIMEOUT,    timeout);
        webTarget = client.target(apiUrl);
    }


    public RadioBrowser(final int timeout,
                        final String userAgent) {
        this(API_URL, timeout, userAgent);
    }

    private static void applyPaging(Paging paging, MultivaluedMap<String, String> requestParams) {
        Objects.requireNonNull(paging, "Paging must be non-null");
        log.info("paging={}", paging);
        requestParams.put("limit", Collections.singletonList(Integer.toString(paging.getLimit())));
        requestParams.put("offset", Collections.singletonList(Integer.toString(paging.getOffset())));
    }

    /** Retrieve a generic list containing a value/stationcount mapping.
     * @return map of value and stationcount pairs.
     * */
    private Map<String, Integer> retrieveValueStationCountList(String subpath) {
        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<>();

        Entity entity = Entity.form(requestParams);

        Response response = webTarget.path(subpath)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .post(entity);

        List<Map<String, String>> map = response.readEntity(new GenericType<List<Map<String, String>>>() {});

        Map<String, Integer> result = map.stream().collect(Collectors.toMap(
                m -> m.get("value"),
                m-> Integer.parseInt(m.get("stationcount"))));
        return result;
    }

    /** List the known countries.
     * @return a list of countries (keys) and country usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_countries">API</a>
     * */
    public Map<String, Integer> listCountries() {
        return retrieveValueStationCountList("json/countries");
    }

    /** List the known codecs.
     * @return a list of codecs (keys) and codec usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_codecs">API</a>
     * */
    public Map<String, Integer> listCodecs() {
        return retrieveValueStationCountList("json/codecs");
    }

    /** List the known languages.
     * @return a list of languages (keys) and language usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_languages">API</a>
     * */
    public Map<String, Integer> listLanguages() {
        return retrieveValueStationCountList("json/languages");
    }

    /** List the known tags.
     * @return a list of tags (keys) and tag usages (values).
     * @see <a href="http://www.radio-browser.info/webservice#list_tags">API</a>
     * */
    public Map<String, Integer> listTags() {
        return retrieveValueStationCountList("json/tags");
    }

    /** Get a list of all stations. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @return the partial list of the stations. Can be empty for exceeding the
     * possible stations.
     */
    public List<Station> listStations(Paging paging) {
        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<>();
        applyPaging(paging, requestParams);
        Entity entity = Entity.form(requestParams);
        Response response = webTarget.path("json/stations")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .post(entity);
        log.debug("response status={}, length={}", response.getStatus(), response.getLength());

        return response.readEntity(new GenericType<List<Station>>() {});
    }

    /** Get a list of stations matching a certain search criteria. Will return a single batch.
     * @param paging the offset and limit of the page to retrieve.
     * @param searchMode the field to match.
     * @param searchTerm the term to search for.
     * @return the partial list of the stations. Can be empty for exceeding the
     * number of matching stations.
     */
    public List<Station> listStationsBy(Paging paging, SearchMode searchMode, String searchTerm) {
        // http://www.radio-browser.info/webservice/format/stations/byid/searchterm

        Objects.requireNonNull(searchMode, "searchMode must be non-null");
        Objects.requireNonNull(searchTerm, "searchTerm must be non-null");

        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<>();
        applyPaging(paging, requestParams);
        Entity entity = Entity.form(requestParams);
        Response response = webTarget
                .path("json/stations").path(searchMode.name()).path(searchTerm)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .post(entity);
        log.debug("response status={}, length={}", response.getStatus(), response.getLength());

        return response.readEntity(new GenericType<List<Station>>() {});
    }

    /** Resolves the streaming URL for the given station.
     * @param station the station to retrieve the stream URL for.
     * @return the URL response that either describes the error or the result.
     */
    public UrlResponse resolveStreamUrl(Station station) {
        Objects.requireNonNull(station, "station must be non-null");
        // http://www.radio-browser.info/webservice/v2/json/url/stationid
        Response response = webTarget.path("v2/json/url").path(station.getId())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("User-Agent", userAgent)
                .get();

        log.debug("URI is {}", webTarget.getUri());
        if (response.getStatus() != 200) {
            log.warn("Non 200 status: {} {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new RadioBrowserException(response.getStatusInfo().getReasonPhrase());
        }
        return response.readEntity(UrlResponse.class);
    }

    /** Posts a new station to the server.
     * Note: This call only transmits certain fields.
     * @param station the station to add to the REST service.
     * @see <a href="http://www.radio-browser.info/webservice#add_station">The API endpoint</a>
     */
    public void postNewStation(Station station) {
        // http://www.radio-browser.info/webservice/json/add
        Objects.requireNonNull(station, "station must be non-null");
        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<>();
        requestParams.put("name", Collections.singletonList(station.getName()));
        requestParams.put("url", Collections.singletonList(station.getUrl()));

        if (station.getHomepage() != null) {
            requestParams.put("homepage", Collections.singletonList(station.getHomepage()));
        }
        if (station.getFavicon() != null) {
            requestParams.put("favicon", Collections.singletonList(station.getFavicon()));
        }
        if (station.getCountry() != null) {
            requestParams.put("country", Collections.singletonList(station.getCountry()));
        }
        if (station.getState() != null) {
            requestParams.put("state", Collections.singletonList(station.getState()));
        }
        if (station.getLanguage() != null) {
            requestParams.put("language", Collections.singletonList(station.getLanguage()));
        }
        if (station.getTags() != null) {
            requestParams.put("tags", Collections.singletonList(station.getTags()));
        }
        Entity entity = Entity.form(requestParams);

        Response response = webTarget.path("json/add")
        .request(MediaType.APPLICATION_JSON_TYPE)
        .accept(MediaType.APPLICATION_JSON_TYPE)
        .header("User-Agent", userAgent)
        .post(entity);

        Map<String, Object> map = response.readEntity(new GenericType<Map<String, Object>>() {});


        if (log.isDebugEnabled()) {
            log.debug("Result: {}", map);
        }
    }
}
