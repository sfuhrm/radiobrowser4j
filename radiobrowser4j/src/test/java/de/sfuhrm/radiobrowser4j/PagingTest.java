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

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for the Paging class.
 * @author Stephan Fuhrmann
 */
public class PagingTest {

    @Test
    public void at() {
        Paging paging = Paging.at(0, 1);
        assertThat(paging.getOffset(), is(0));
        assertThat(paging.getLimit(), is(1));
    }

    @Test
    public void atWithZeroLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            Paging.at(0, 0);
        });
    }

    @Test
    public void atWithNegativeOffset() {
        assertThrows(IllegalArgumentException.class, () -> {
            Paging.at(-1, 1);
        });
    }

    @Test
    public void next() {
        Paging pagingFirst = Paging.at(0, 64);
        Paging next = pagingFirst.next();

        assertThat(next.getOffset(), is(64));
        assertThat(next.getLimit(), is(64));
    }

    @Test
    public void previousWithBeginning() {
        Paging pagingFirst = Paging.at(0, 64);
        Paging next = pagingFirst.previous();

        assertThat(next.getOffset(), is(0 ));
        assertThat(next.getLimit(), is(64));
    }

    @Test
    public void previous() {
        Paging pagingFirst = Paging.at(64, 32);
        Paging next = pagingFirst.previous();

        assertThat(next.getOffset(), is(64 - 32 ));
        assertThat(next.getLimit(), is(32));
    }
}
