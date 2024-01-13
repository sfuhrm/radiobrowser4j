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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.CoreMatchers.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Some integration tests.
 * Uses either the real service or mocked stubs that are delivered from WireMock.
 * Please trigger {@linkplain #RECORDING} for recording.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class RadioBrowserTest {

    private final static int WIREMOCK_PORT = 9123;
    private final static String API_URL = "http://localhost:"+WIREMOCK_PORT+"/";

    private final static String USER_AGENT = "https://github.com/sfuhrm/radiobrowser4j";

    private static RadioBrowser browser;
    private static WireMockServer wireMockServer;
    private static WireMock wireMockClient;

    /** Paging with 5 elements. */
    private final static Paging FIRST_FIVE = Paging.at(0, 5);

    private final static Paging SECOND_FIVE = Paging.at(5, 5);

    private final static Limit FIVE = Limit.of(5);

    /** Name of test station to generate. */
    private final static String TEST_NAME = "Integration test for radiobrowser4j - ignore";

    /** Trigger this to record instead of playback of recorded responses
     * in {@code src/test/resources/mappings}.
     *
     * WARNING! This calls radiobrowser.info API directly and
     * creates entities which are deleted afterwards!
     * */
    public final static boolean RECORDING = false;

    @AfterEach
    public void gracefulSleepInterval() throws InterruptedException {
        if (RECORDING) {
            Thread.sleep(1000);
        }
    }

    @BeforeAll
    public static void createBrowser() {
        WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();
        wireMockConfiguration.port(WIREMOCK_PORT);
        wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.start();
        wireMockClient = new WireMock("localhost", WIREMOCK_PORT);

        if (RECORDING) {
            // Wiremock does not support recording of the last slash
            String originalUrl = RadioBrowser.DEFAULT_API_URL;
            int lastSlash = originalUrl.lastIndexOf('/');
            String urlWithoutSlash = originalUrl.substring(0, lastSlash);
            wireMockClient.startStubRecording(urlWithoutSlash);
        }

        browser = new RadioBrowser(ConnectionParams.builder().apiUrl(API_URL).timeout(20000).userAgent(USER_AGENT).build());
    }

    @AfterAll
    public static void shutdownBrowser() {
        if (wireMockServer != null) {
            if (RECORDING) {
                SnapshotRecordResult snapshotRecordResult = wireMockClient.stopStubRecording();
                wireMockClient.saveMappings();
            }
            wireMockServer.shutdown();
        }
    }

    @Test
    public void listCountries() {
        Map<String, Integer> countries = browser.listCountries();
        assertThat(countries, notNullValue());
        assertThat(countries.size(), is(not(0)));
        assertThat(countries.get("Germany"), is(not(0)));
    }

    @Test
    public void listCodecs() {
        Map<String, Integer> codecs = browser.listCodecs();
        assertThat(codecs, notNullValue());
        assertThat(codecs.size(), is(not(0)));
        assertThat(codecs.containsKey("AAC"), is(true));
    }

    @Test
    public void listLanguages() {
        Map<String, Integer> languages = browser.listLanguages();
        assertThat(languages, notNullValue());
        assertThat(languages.size(), is(not(0)));
        assertThat(languages.containsKey("german"), is(true));
    }

    @Test
    public void listTags() {
        Map<String, Integer> tags = browser.listTags();
        assertThat(tags, notNullValue());
        assertThat(tags.size(), is(not(0)));
        assertThat(tags.containsKey("80s"), is(true));
    }

    @Test
    public void listStations() {
        List<Station> firstStations = browser.listStations(FIRST_FIVE);
        assertThat(firstStations, notNullValue());
        assertThat(firstStations.size(), is(FIRST_FIVE.getLimit()));

        List<Station> secondStations = browser.listStations(SECOND_FIVE);
        assertThat(secondStations, notNullValue());
        assertThat(secondStations.size(), is(SECOND_FIVE.getLimit()));

        assertThat(firstStations, is(not(secondStations)));
    }

    @Test
    public void listStationsWithStream() {
        List<Station> stations = browser
                .listStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(stations.size(), is(256));
    }

    @Test
    public void listStationsWithStreamAndOrder() {
        // bitrate is hopefully stable when paging
        List<Station> stations = browser
                .listStations(ListParameter.create().order(FieldName.BITRATE))
                .limit(256)
                .collect(Collectors.toList());

        assertThat(stations.size(), is(256));

        Station last = null;
        for (Station station : stations) {
            if (station.getBitrate() != null && last != null && last.getBitrate() != null) {
                assertThat("station list must contain bitrate in ascending order",
                        station.getBitrate(), is(Matchers.greaterThanOrEqualTo(last.getBitrate())));
            }
            last = station;
        }
    }

    @Test
    public void listStationsWithFullStream() {
        List<Station> stations = browser
                .listStations()
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
    }

    @Test
    public void listBrokenStations() {
        List<Station> stations = browser.listBrokenStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getSize()));
    }

    @Test
    public void listBrokenStationsWithStream() {
        List<Station> stations = browser
                .listBrokenStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(stations.size(), is(256));
    }

    @Test
    public void listTopClickStations() {
        List<Station> stations = browser.listTopClickStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getSize()));
    }

    @Test
    public void listTopClickStationsWithStream() {
        List<Station> stations = browser
                .listTopClickStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(stations.size(), is(256));
    }

    @Test
    public void listTopVoteStations() {
        List<Station> stations = browser.listTopVoteStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getSize()));
    }

    @Test
    public void listTopVoteStationsWithStream() {
        List<Station> stations = browser
                .listTopVoteStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(stations.size(), is(256));
    }

    @Test
    public void listLastClickStations() {
        List<Station> stations = browser.listLastClickStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getSize()));
    }

    @Test
    public void listLastClickStationsWithStream() {
        List<Station> stations = browser
                .listLastClickStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(stations.size(), is(256));
    }

    @Test
    public void listLastChangedStations() {
        List<Station> stations = browser.listLastChangedStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIRST_FIVE.getLimit()));
    }

    @Test
    public void listLastChangedStationsWithStream() {
        List<Station> stations = browser
                .listLastChangedStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(stations.size(), is(256));
    }

    @Test
    public void getStationByUUID() {
        List<Station> stations = browser.listStations(FIRST_FIVE);

        Station first = stations.get(0);
        Optional<Station> station = browser.getStationByUUID(first.getStationUUID());
        assertThat(station.isPresent(), is(true));
        assertThat(station.get(), is(first));
        assertThat(first.getStationUUID(), is(not(nullValue())));
        assertThat(first.getState(), is(not(nullValue())));
        assertThat(first.getName(), is(not(nullValue())));
        assertThat(first.getUrl(), is(not(nullValue())));
        assertThat(first.getLastchecktime(), is(not(nullValue())));
        assertThat(first.getLastcheckok(), is(not(nullValue())));
        assertThat(first.getLastchangetime(), is(not(nullValue())));
        assertThat(first.getCountryCode(), is(not(nullValue())));
        assertThat(first.getFavicon(), is(not(nullValue())));
        assertThat(first.getHomepage(), is(not(nullValue())));
        assertThat(first.getHasExtendedInfo(), is(not(nullValue())));
        assertThat(first.getLanguage(), is(not(nullValue())));
        assertThat(first.getTags(), is(not(nullValue())));
        assertThat(first.getTagList(), is(not(nullValue())));
        assertThat(first.getVotes(), is(not(nullValue())));
        assertThat(first.getBitrate(), is(not(nullValue())));
        assertThat(first.getClickcount(), is(not(nullValue())));
        assertThat(first.getUrlResolved(), is(not(nullValue())));
    }

    @Test
    public void listStationsByWithName() {
        List<Station> stations = browser.listStationsBy(FIRST_FIVE, SearchMode.BYNAME, "synthradio");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(1));
        assertThat(stations.get(0).getUrl(), is("http://synth-radio.ru/synthradio192.m3u"));
    }

    @Test
    public void resolveStreamUrl() throws MalformedURLException {
        List<Station> stations = browser.listStationsBy(FIRST_FIVE, SearchMode.BYNAME, "synthradio");
        URL response = browser.resolveStreamUrl(stations.get(0).getStationUUID());
        assertThat(response, notNullValue());
        assertThat(response, is(new URL("http://77.51.212.205:8005/live192")));
    }

    @Test
    public void listStationsBy() {
        List<Station> stations = browser.listStationsBy(FIRST_FIVE, SearchMode.BYNAME, "ding");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIRST_FIVE.getLimit()));
        assertThat(stations.get(0).getName().toLowerCase(), containsString("ding"));
    }

    @Test
    public void listStationsByWithStream() {
        List<Station> stations = browser
                .listStationsBy(SearchMode.BYNAME, "pop")
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
    }

    // this is being accepted by server 0.6.11, so it is not tested anymore
    @Disabled
    @Test
    public void postNewWithFail() {
        assertThrows(RadioBrowserException.class, () -> {
            Station station = new Station();
            // URL is missing
            station.setHomepage("https://github.com/sfuhrm/radiobrowser4j");
            station.setName(TEST_NAME);
            station.setFavicon("https://github.com/favicon.ico");
            browser.postNewStation(station);
        });
    }

    @Test
    public void postNewWithSuccess() {
        Station station = new Station();
        station.setUrl("https://github.com/sfuhrm/radiobrowser4j");
        station.setHomepage("https://github.com/sfuhrm/radiobrowser4j");
        station.setName(TEST_NAME);
        station.setFavicon("https://github.com/favicon.ico");
        station.setCountryCode("DE");
        UUID id = browser.postNewStation(station);
        assertThat(id, is(not(nullValue())));
    }

    @Test
    public void voteStation() {
        Station station = new Station();
        station.setUrl("https://github.com/sfuhrm/radiobrowser4j");
        station.setHomepage("https://github.com/sfuhrm/radiobrowser4j");
        station.setName(TEST_NAME);
        station.setFavicon("https://github.com/favicon.ico");
        station.setCountryCode("DE");
        UUID id = browser.postNewStation(station);
        assertThat(id, is(not(nullValue())));

        Optional<Station> before = browser.getStationByUUID(id);
        browser.voteForStation(id);
        Optional<Station> readBack1 = browser.getStationByUUID(id);

        // assertThat(readBack1.get().getVotes(), is(1));
    }

    @Test
    public void getServerStats() {
        Stats stats = browser.getServerStats();
        assertThat(stats, is(not(nullValue())));
        assertThat(stats.getSupportedVersion(), is(not(nullValue())));
        assertThat(stats.getSoftwareVersion(), is(not(nullValue())));
        assertThat(stats.getStatus(), is(not(nullValue())));
        assertThat(stats.getStations(), is(not(nullValue())));
        assertThat(stats.getStationsBroken(), is(not(nullValue())));
        assertThat(stats.getTags(), is(not(nullValue())));
        assertThat(stats.getClicksLastHour(), is(not(nullValue())));
        assertThat(stats.getClicksLastDay(), is(not(nullValue())));
        assertThat(stats.getLanguages(), is(not(nullValue())));
        assertThat(stats.getCountries(), is(not(nullValue())));
    }


    @Test
    public void listStationsWithAdvancedSearch() {
        List<Station> stationsList = browser
                .listStationsWithAdvancedSearch(
                        AdvancedSearch.builder()
                                .countryCode("DE")
                                .state("Hamburg")
                                .build())
                .collect(Collectors.toList());
        assertThat(stationsList.get(0).getCountryCode(), is("DE"));
        assertThat(stationsList.get(0).getState(), is("Hamburg"));
    }
}
