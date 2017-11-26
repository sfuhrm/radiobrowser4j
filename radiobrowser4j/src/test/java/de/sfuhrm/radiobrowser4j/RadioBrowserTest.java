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

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.recording.SnapshotRecordResult;
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
    private final static String USER_AGENT = "https://github.com/sfuhrm/radiorecorder";

    private static RadioBrowser browser;
    private static WireMock wireMockClient;
    private static WireMockServer wireMockServer;

    private final static Paging FIVE = Paging.at(0, 5);

    /** Trigger this to record instead of playback of recorded responses
     * in {@code src/test/resources/mappings}.
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
            wireMockClient.startStubRecording(RadioBrowser.API_URL);
        }

        browser = new RadioBrowser(API_URL,5000, USER_AGENT);
    }

    @AfterClass
    public static void shutdownBrowser() {
        if (RECORDING) {
            SnapshotRecordResult recordedMappings = wireMockClient.stopStubRecording();
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
        assertThat(countries.size(), is(178));
        assertThat(countries.get("Germany"), is(1658));
    }

    @Test
    public void listCodecs() {
        Map<String, Integer> codecs = browser.listCodecs();
        assertThat(codecs, notNullValue());
        assertThat(codecs.size(), is(5));
        assertThat(codecs.containsKey("AAC"), is(true));
    }

    @Test
    public void listLanguages() {
        Map<String, Integer> languages = browser.listLanguages();
        assertThat(languages, notNullValue());
        assertThat(languages.size(), is(878));
        assertThat(languages.containsKey("German"), is(true));
    }

    @Test
    public void listTags() {
        Map<String, Integer> tags = browser.listTags();
        assertThat(tags, notNullValue());
        assertThat(tags.size(), is(4845));
        assertThat(tags.containsKey("80s"), is(true));
    }

    @Test
    public void listStations() {
        List<Station> stations = browser.listStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listBrokenStations() {
        List<Station> stations = browser.listBrokenStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listTopClickStations() {
        List<Station> stations = browser.listTopClickStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listTopVoteStations() {
        List<Station> stations = browser.listTopVoteStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listLastClickStations() {
        List<Station> stations = browser.listLastClickStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listLastChangedStations() {
        List<Station> stations = browser.listLastChangedStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listDeletedStations() {
        List<Station> stations = browser.listDeletedStations();
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(337));
    }

    @Test
    public void listImprovableStations() {
        List<Station> stations = browser.listImprovableStations(FIVE);
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listStationsByWithName() {
        List<Station> stations = browser.listStationsBy(FIVE, SearchMode.byname, "synthradio");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(1));
        assertThat(stations.get(0).getUrl(), is("http://synth-radio.ru/synthradio192.m3u"));
    }

    @Test
    public void resolveStreamUrl() {
        List<Station> stations = browser.listStationsBy(FIVE, SearchMode.byname, "synthradio");
        UrlResponse response = browser.resolveStreamUrl(stations.get(0));
        assertThat(response, notNullValue());
        assertThat(response.getUrl(), is("http://86.62.102.131:8005/live192"));
    }

    @Test
    public void listStationsBy() {
        List<Station> stations = browser.listStationsBy(FIVE, SearchMode.byname, "ding");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
        assertThat(stations.get(0).getName().toLowerCase(), containsString("ding"));
    }
}
