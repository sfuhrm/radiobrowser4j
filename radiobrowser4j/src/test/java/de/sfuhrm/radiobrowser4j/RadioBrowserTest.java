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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Some integration tests.
 * Uses either the real service or mocked stubs that are delivered from WireMock.
 * Please trigger {@linkplain #RECORDING} for recording.
 * @author Stephan Fuhrmann
 */
public class RadioBrowserTest {

    private final static int WIREMOCK_PORT = 9123;
    private final static String API_URL = "http://localhost:"+WIREMOCK_PORT+"/";

    private final static String USER_AGENT = "https://github.com/sfuhrm/radiobrowser4j";

    private static RadioBrowser browser;
    private static WireMock wireMockClient;
    private static WireMockServer wireMockServer;

    /** Paging with 5 elements. */
    private final static Paging FIVE = Paging.at(0, 5);

    /** Name of test station to generate. */
    private final static String TEST_NAME = "Integration test for radiobrowser4j - ignore";

    /** Trigger this to record instead of playback of recorded responses
     * in {@code src/test/resources/mappings}.
     *
     * WARNING! This calls radiobrowser.info API directly and
     * creates entities which are deleted afterwards!
     * */
    public final static boolean RECORDING = false;

    @BeforeClass
    public static void createBrowser() {
        WireMockConfiguration wireMockConfiguration = new WireMockConfiguration();
        wireMockConfiguration.port(WIREMOCK_PORT);
        wireMockServer = new WireMockServer(wireMockConfiguration);
        wireMockServer.start();

        wireMockClient = new WireMock(WIREMOCK_PORT);
        if (RECORDING) {
            wireMockClient.startStubRecording(RadioBrowser.DEFAULT_API_URL);
        }

        browser = new RadioBrowser(API_URL,5000, USER_AGENT);
    }

    @AfterClass
    public static void shutdownBrowser() {
        if (RECORDING) {
            wireMockClient.stopStubRecording();
        }

        if (wireMockClient != null) {
            wireMockClient.saveMappings();
        }
        if (wireMockServer != null) {
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
        assertThat(languages.containsKey("German"), is(true));
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
        List<Station> stations = browser.listStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listStationsWithStream() {
        List<Station> stations = browser
                .listStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test
    public void listStationsWithStreamAndOrder() {
        List<Station> stations = browser
                .listStations(ListParameter.create().order(FieldName.name))
                .limit(256)
                .collect(Collectors.toList());

        List<String> streamOrder = stations
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        List<String> selfOrder = new ArrayList<>(streamOrder);
        selfOrder.sort(String.CASE_INSENSITIVE_ORDER);

        assertThat(streamOrder, is(selfOrder));
    }

    @Test
    public void listStationsWithFullStream() {
        List<Station> stations = browser
                .listStations()
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test
    public void listBrokenStations() {
        List<Station> stations = browser.listBrokenStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listBrokenStationsWithStream() {
        List<Station> stations = browser
                .listBrokenStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
    }

    @Test
    public void listTopClickStations() {
        List<Station> stations = browser.listTopClickStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listTopClickStationsWithStream() {
        List<Station> stations = browser
                .listTopClickStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test
    public void listTopVoteStations() {
        List<Station> stations = browser.listTopVoteStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listTopVoteStationsWithStream() {
        List<Station> stations = browser
                .listTopVoteStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test
    public void listLastClickStations() {
        List<Station> stations = browser.listLastClickStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listLastClickStationsWithStream() {
        List<Station> stations = browser
                .listLastClickStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test
    public void listLastChangedStations() {
        List<Station> stations = browser.listLastChangedStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listLastChangedStationsWithStream() {
        List<Station> stations = browser
                .listLastChangedStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test
    public void listDeletedStations() {
        List<Station> stations = browser.listDeletedStations();
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(not(Collections.emptyList())));
    }

    @Test
    public void getStationById() {
        List<Station> stations = browser.listStations(FIVE);

        Station first = stations.get(0);
        Optional<Station> station = browser.getStationById(first.getId());
        assertThat(station.isPresent(), is(true));
        assertThat(station.get(), is(first));
    }

    @Test
    public void listImprovableStations() {
        List<Station> stations = browser.listImprovableStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
    }

    @Test
    public void listImprovableStationsWithStream() {
        List<Station> stations = browser
                .listImprovableStations()
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
    }

    @Test
    public void listStationsByWithName() {
        List<Station> stations = browser.listStationsBy(FIVE, SearchMode.byname, "synthradio");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(1));
        assertThat(stations.get(0).getUrl(), is("http://synth-radio.ru/synthradio192.m3u"));
    }

    @Test
    public void resolveStreamUrl() throws MalformedURLException {
        List<Station> stations = browser.listStationsBy(FIVE, SearchMode.byname, "synthradio");
        URL response = browser.resolveStreamUrl(stations.get(0));
        assertThat(response, notNullValue());
        assertThat(response, is(new URL("http://86.62.102.131:8005/live192")));
    }

    @Test
    public void listStationsBy() {
        List<Station> stations = browser.listStationsBy(FIVE, SearchMode.byname, "ding");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(FIVE.getLimit()));
        assertThat(stations.get(0).getName().toLowerCase(), containsString("ding"));
    }

    @Test
    public void listStationsByWithStream() {
        List<Station> stations = browser
                .listStationsBy(SearchMode.byname, "pop")
                .limit(256)
                .collect(Collectors.toList());
        assertThat(stations, notNullValue());
        assertThat(stations, is(not(Collections.emptyList())));
        assertThat(new HashSet<>(stations).size(), is(stations.size()));
    }

    @Test(expected = RadioBrowserException.class)
    public void postNewWithFail() {
        Station station = new Station();
        // URL is missing
        station.setHomepage("https://github.com/sfuhrm/radiobrowser4j");
        station.setName(TEST_NAME);
        station.setFavicon("https://github.com/favicon.ico");
        browser.postNewStation(station);
    }

    @Test
    public void postNewWithSuccess() {
        Station station = new Station();
        station.setUrl("https://github.com/sfuhrm/radiobrowser4j");
        station.setHomepage("https://github.com/sfuhrm/radiobrowser4j");
        station.setName(TEST_NAME);
        station.setFavicon("https://github.com/favicon.ico");
        String id = browser.postNewStation(station);
        assertNotNull(id);

        Optional<Station> stationOpt = browser.getStationById(id);
        stationOpt.ifPresent(s -> browser.deleteStation(s));
    }

    @Test
    public void editStation() {
        Station station = new Station();
        station.setUrl("https://github.com/sfuhrm/radiobrowser4j");
        station.setHomepage("https://github.com/sfuhrm/radiobrowser4j");
        station.setName(TEST_NAME);
        station.setFavicon("https://github.com/favicon.ico");
        String id = browser.postNewStation(station);
        assertNotNull(id);

        Optional<Station> readBack1 = browser.getStationById(id);

        readBack1.ifPresent(s -> s.setName("Foo bar baz"));

        // changes the name
        browser.editStation(readBack1.get());

        Optional<Station> readBack2 = browser.getStationById(id);

        assertThat(readBack2.get().getName(), is("Foo bar baz"));

        browser.deleteStation(readBack2.get());
    }
}
