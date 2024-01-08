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

import org.hamcrest.collection.IsArrayWithSize;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for the ListParameter class.
 * @author Stephan Fuhrmann
 */
public class ListParameterTest {

    @Test
    public void create() {
        ListParameter listParameter = ListParameter.create();

        assertThat(listParameter, is(not(nullValue())));
    }

    @Test
    public void applyToWithNothingSet() {
        ListParameter listParameter = ListParameter.create();
        Map<String,String> multivaluedMap = new HashMap<>();
        listParameter.apply(multivaluedMap);

        assertThat(multivaluedMap.keySet().toArray(), is(IsArrayWithSize.emptyArray()));
    }

    @Test
    public void applyToWithOrderSet() {
        ListParameter listParameter = ListParameter.create();
        listParameter.order(FieldName.BITRATE);
        Map<String,String> multivaluedMap = new HashMap<>();
        listParameter.apply(multivaluedMap);

        assertThat(multivaluedMap.keySet(),     is(new HashSet<>(Collections.singletonList("order"))));
        assertThat(multivaluedMap.get("order"),  is("bitrate"));
    }

    @Test
    public void applyToWithReverseSet() {
        ListParameter listParameter = ListParameter.create();
        listParameter.reverseOrder(true);
        Map<String,String> multivaluedMap = new HashMap<>();
        listParameter.apply(multivaluedMap);

        assertThat(multivaluedMap.keySet(),     is(new HashSet<>(Collections.singletonList("reverse"))));
        assertThat(multivaluedMap.get("reverse"),  is("true"));
    }

    @Test
    public void applyToWithOrderAndReverseSet() {
        ListParameter listParameter = ListParameter.create();
        listParameter.order(FieldName.CODEC);
        listParameter.reverseOrder(true);
        Map<String,String> map = new HashMap<>();
        listParameter.apply(map);

        assertThat(map.keySet(),     is(new HashSet<>(Arrays.asList("reverse", "order"))));
        assertThat(map.get("reverse"),  is("true"));
        assertThat(map.get("order"),  is("codec"));
    }
}
