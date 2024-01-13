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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for the AdvancedSearch class.
 * @author Stephan Fuhrmann
 */
public class AdvancedSearchTest {

    /** A fresh builder for testing. */
    private AdvancedSearch.AdvancedSearchBuilder builder;

    /** The map to write to. */
    private Map<String, String> map;

    @BeforeEach
    public void setUp() {
        builder = new AdvancedSearch.AdvancedSearchBuilder();
        map = new HashMap<>();
    }

    @Test
    public void applyWithEmpty() {
        builder.build().apply(map);
        assertThat(map, is(Collections.emptyMap()));
    }

    @Test
    public void applyWithName() {
        builder.name("foo").build().apply(map);
        assertThat(map, is(Collections.singletonMap("name", "foo")));
    }

    @Test
    public void applyWithNameExact() {
        builder.nameExact(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("nameExact", "true")));
    }

    @Test
    public void applyWithCountryCode() {
        builder.countryCode("DE").build().apply(map);
        assertThat(map, is(Collections.singletonMap("countrycode", "DE")));
    }

    @Test
    public void applyWithState() {
        builder.state("Schleswig-Holstein").build().apply(map);
        assertThat(map, is(Collections.singletonMap("state", "Schleswig-Holstein")));
    }

    @Test
    public void applyWithStateExact() {
        builder.stateExact(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("stateExact", "true")));
    }

    @Test
    public void applyWithLanguage() {
        builder.language("German").build().apply(map);
        assertThat(map, is(Collections.singletonMap("language", "German")));
    }

    @Test
    public void applyWithLanguageExact() {
        builder.languageExact(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("languageExact", "true")));
    }

    @Test
    public void applyWithTag() {
        builder.tag("Rock").build().apply(map);
        assertThat(map, is(Collections.singletonMap("tag", "Rock")));
    }

    @Test
    public void applyWithTagExact() {
        builder.tagExact(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("tagExact", "true")));
    }

    @Test
    public void applyWithTagList() {
        builder.tagList(Arrays.asList("Hello", "World")).build().apply(map);
        assertThat(map, is(Collections.singletonMap("tagList", "Hello,World")));
    }

    @Test
    public void applyWithCodec() {
        builder.codec("FOO").build().apply(map);
        assertThat(map, is(Collections.singletonMap("codec", "FOO")));
    }

    @Test
    public void applyWithBitrateMin() {
        builder.bitrateMin(128).build().apply(map);
        assertThat(map, is(Collections.singletonMap("bitrateMin", "128")));
    }

    @Test
    public void applyWithBitrateMax() {
        builder.bitrateMax(128).build().apply(map);
        assertThat(map, is(Collections.singletonMap("bitrateMax", "128")));
    }

    @Test
    public void applyWithHasGeoInfo() {
        builder.hasGeoInfo(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("has_geo_info", "true")));
    }

    @Test
    public void applyWithHasExtendedInfo() {
        builder.hasExtendedInfo(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("has_extended_info", "true")));
    }

    @Test
    public void applyWithIsHttps() {
        builder.isHttps(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("is_https", "true")));
    }

    @Test
    public void applyWithOrder() {
        builder.order(FieldName.NAME).build().apply(map);
        assertThat(map, is(Collections.singletonMap("order", "name")));
    }

    @Test
    public void applyWithReverse() {
        builder.reverse(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("reverse", "true")));
    }

    @Test
    public void applyWithHideBrokenTrue() {
        builder.hideBroken(true).build().apply(map);
        assertThat(map, is(Collections.singletonMap("hidebroken", "true")));
    }

    @Test
    public void applyWithHideBrokenFalse() {
        builder.hideBroken(false).build().apply(map);
        assertThat(map, is(Collections.singletonMap("hidebroken", "false")));
    }
}
