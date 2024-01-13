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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for the RestDelegateImpl.
 * @author Stephan Fuhrmann
 */
public class RestDelegateImplTest {

    @Test
    public void guessCharsetForWithUnknown() {
        Charset actual = RestDelegateImpl.guessCharsetFor("text/plain; charset=-");
        assertThat(actual, is(StandardCharsets.UTF_8));
    }

    @Test
    public void guessCharsetForWithUndefined() {
        Charset actual = RestDelegateImpl.guessCharsetFor("text/plain");
        assertThat(actual, is(StandardCharsets.UTF_8));
    }

    @Test
    public void guessCharsetForWithISO() {
        Charset actual = RestDelegateImpl.guessCharsetFor("text/plain; charset=ISO-8859-1");
        assertThat(actual, is(StandardCharsets.ISO_8859_1));
    }

    @Test
    public void guessCharsetForWithUTF16() {
        Charset actual = RestDelegateImpl.guessCharsetFor("text/plain; charset=UTF-16");
        assertThat(actual, is(StandardCharsets.UTF_16));
    }

    @Test
    public void guessCharsetForWithNoSpace() {
        Charset actual = RestDelegateImpl.guessCharsetFor("text/plain;charset=UTF-16");
        assertThat(actual, is(StandardCharsets.UTF_16));
    }
}
