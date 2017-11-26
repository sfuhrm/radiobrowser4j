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
 * Some integration tests against the real service.
 * This kind of testing sucks.
 * @author Stephan Fuhrmann
 */
public class RadioBrowserTest {

    private final static int WIREMOCK_PORT = 9123;
    private final static String API_URL = "http://localhost:"+WIREMOCK_PORT+"/";
    private final static String USER_AGENT = "https://github.com/sfuhrm/radiorecorder";

    private static RadioBrowser browser;
    private static WireMock wireMockClient;
    private static WireMockServer wireMockServer;

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
    public void listStations() {
        List<Station> stations = browser.listStations(new Paging(0,5));
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
    }

    @Test
    public void listStationsByWithName() {
        List<Station> stations = browser.listStationsBy(new Paging(0,5), SearchMode.byname, "synthradio");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(1));
        assertThat(stations.get(0).getUrl(), is("http://synth-radio.ru/synthradio192.m3u"));
    }

    @Test
    public void resolveStreamUrl() {
        List<Station> stations = browser.listStationsBy(new Paging(0,5), SearchMode.byname, "synthradio");
        UrlResponse response = browser.resolveStreamUrl(stations.get(0));
        assertThat(response, notNullValue());
        assertThat(response.url, is("http://86.62.102.131:8005/live192"));
    }

    @Test
    public void listStationsBy() {
        List<Station> stations = browser.listStationsBy(new Paging(0,5), SearchMode.byname, "ding");
        assertThat(stations, notNullValue());
        assertThat(stations.size(), is(5));
        assertThat(stations.get(0).getName().toLowerCase(), containsString("ding"));
    }
}
