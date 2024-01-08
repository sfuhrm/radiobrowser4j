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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for the spliterator.
 * @author Stephan Fuhrmann
 */
public class PagingSpliteratorTest {

    @Test
    public void testPagingSpliterator() {
        PagingSpliterator<Integer> integerPagingSpliterator = new PagingSpliterator<>(
                paging -> {
                    List<Integer> data;

                    if (paging.getOffset() < 100) {
                        data = IntStream.range(paging.getOffset(),
                                paging.getOffset() + paging.getLimit())
                                .boxed()
                                .collect(Collectors.toList());
                        if (data.size() > paging.getLimit()) {
                            throw new IllegalStateException();
                        }
                    } else {
                        data = Collections.emptyList();
                    }
                    return data;
                }
        );

        List<Integer> actual = StreamSupport.stream(integerPagingSpliterator, false)
                .collect(Collectors.toList());

        List<Integer> expected = IntStream.range(0, 128)
                .boxed()
                .collect(Collectors.toList());

        assertThat(actual, is(expected));
    }
}
