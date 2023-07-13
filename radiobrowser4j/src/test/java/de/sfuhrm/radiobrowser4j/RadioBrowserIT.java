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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
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

    @Before
    public void before() throws IOException {
        Optional<String> myEndpoint = new EndpointDiscovery(USER_CLIENT).discover();
        this.endpoint = myEndpoint.get();
        radioBrowser = new RadioBrowser(endpoint, TIMEOUT, USER_CLIENT);
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
        long count = stream.limit(1000).count();
        assertThat((int) count, Matchers.greaterThan(0));
    }

    @Test
    public void testGetServerStats() throws IOException {
        Stats stats = radioBrowser.getServerStats();
        assertThat(stats, Matchers.notNullValue());
        assertThat(stats.getStations(), Matchers.greaterThan(1));
        assertThat(stats.getTags(), Matchers.greaterThan(1));
    }

    @Test
    public void testAdvancedSearch() throws IOException {
        AdvancedSearch advancedSearch = AdvancedSearch.builder()
                .country("Mexico").build();
        Stream<Station> stream = radioBrowser.listStationsWithAdvancedSearch(advancedSearch);
        assertThat((int) stream.count(), Matchers.greaterThan(1));
    }
}
