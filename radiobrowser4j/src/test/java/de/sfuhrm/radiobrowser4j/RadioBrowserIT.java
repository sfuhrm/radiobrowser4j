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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration test using the real service on the internet.
 * @author Stephan Fuhrmann
 */
public class RadioBrowserIT {
    private static final String USER_CLIENT = "radiobrowser4j-integration";
    private static final int TIMEOUT = 10 * 1000;

    private String endpoint;

    private RadioBrowser radioBrowser;

    @BeforeEach
    public void before() throws IOException {
        Optional<String> myEndpoint = new EndpointDiscovery(USER_CLIENT).discover();
        this.endpoint = myEndpoint.get();
        radioBrowser = new RadioBrowser(ConnectionParams.builder()
                .apiUrl(endpoint).timeout(TIMEOUT).userAgent(USER_CLIENT).build());
    }

    @AfterEach
    public void gracefulSleepInterval() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Test
    public void testListCountries() throws IOException {
        Map<String, Integer> countries = radioBrowser.listCountries();
        assertThat(countries.size(), Matchers.greaterThan(0));
        assertThat(countries.get("Germany"), Matchers.notNullValue());
    }

    @Test
    public void testListCodecs() throws IOException {
        Map<String, Integer> codecs = radioBrowser.listCodecs();
        assertThat(codecs.size(), Matchers.greaterThan(0));
        assertThat(codecs.get("MP3"), Matchers.notNullValue());
    }

    @Test
    public void testListLanguages() throws IOException {
        Map<String, Integer> languages = radioBrowser.listLanguages();
        assertThat(languages.size(), Matchers.greaterThan(0));
        assertThat(languages.get("german"), Matchers.notNullValue());
    }

    @Test
    public void testListTags() throws IOException {
        Map<String, Integer> tags = radioBrowser.listTags();
        assertThat(tags.size(), Matchers.greaterThan(0));
        assertThat(tags.get("information"), Matchers.notNullValue());
    }

    @Test
    public void testListStations() throws IOException {
        Stream<Station> stream = radioBrowser.listStations();
        List<Station> list = stream.limit(1000).collect(Collectors.toList());
        assertThat(list.size(), Matchers.greaterThan(0));
    }

    @Test
    public void testGetServerStats() throws IOException {
        Stats stats = radioBrowser.getServerStats();
        assertThat(stats, Matchers.notNullValue());
        assertThat(stats.getStations(), Matchers.greaterThan(1));
        assertThat(stats.getTags(), Matchers.greaterThan(1));
    }

    @Test
    public void testAdvancedSearchWithCountry() throws IOException {
        AdvancedSearch advancedSearch = AdvancedSearch.builder()
                .countryCode("MX").build();
        Stream<Station> stream = radioBrowser.listStationsWithAdvancedSearch(advancedSearch);
        assertThat((int) stream.count(), Matchers.greaterThan(1));
    }

    @Test
    public void testAdvancedSearchWithBitRateMin() throws IOException {
        AdvancedSearch advancedSearch = AdvancedSearch.builder()
                .bitrateMin(320).build();
        Stream<Station> stream = radioBrowser.listStationsWithAdvancedSearch(advancedSearch);
        List<Station> stations = stream.limit(1000).collect(Collectors.toList());
        assertThat(stations.stream().filter(s -> s.getBitrate() >= 320).count(), Matchers.greaterThan(1L));
        assertThat(stations.stream().filter(s -> s.getBitrate() < 320).count(), Matchers.equalTo(0L));    }
}
