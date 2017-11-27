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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test for the Station.
 * @author Stephan Fuhrmann
 */
public class StationTest {

    @Test
    public void setTagsCommaSeparated() {
        Station testMe = new Station();
        testMe.setTagsCommaSeparated("foo,bar,baz");
        assertThat(Arrays.asList("foo", "bar", "baz"), is(testMe.getTags()));
    }

    @Test
    public void getTagsCommaSeparated() {
        Station testMe = new Station();
        testMe.setTags(Arrays.asList("foo", "bar", "baz"));
        assertThat("foo,bar,baz", is(testMe.getTagsCommaSeparated()));
    }
}
