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

import jakarta.ws.rs.core.MultivaluedMap;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/** Immutable paging configuration.
 * The paging is used to address a sub list that can be retrieved in
 * one logical page. This enables the client to itereate through a
 * long list without needing to store the whole result in memory.
 * @see Limit
 * @author Stephan Fuhrmann
 * */
@ToString
@EqualsAndHashCode
@Slf4j
public final class Paging extends ParameterProvider {
    /** A default start page with offset 0 and limit 64.
     * */
    public static final Paging DEFAULT_START = new Paging(0, 64);

    /** The start element to start processing. */
    @Getter
    private final int offset;

    /** The total number of elements to process. */
    @Getter
    private final int limit;

    /** Creates a new paging. This method is private.
     * @param myOffset the offset to use.
     * @param myLimit the limit to use.
     * @throws IllegalArgumentException if limit or offset are wrong.
     * @see #at(int, int)
     * */
    private Paging(final int myOffset, final int myLimit) {
        if (myOffset < 0) {
            throw new IllegalArgumentException(
                    "Offset is " + myOffset + ", but must be >= 0");
        }
        if (myLimit <= 0) {
            throw new IllegalArgumentException(
                    "Limit is " + myLimit + ", but must be > 0");
        }
        this.offset = myOffset;
        this.limit = myLimit;
    }

    /** Creates a new paging at the given offset and limit.
     * @param offset the positive offset of the page.
     * @param limit the maximum number of entries.
     * @return the paging instance created.
     * */
    public static Paging at(final int offset, final int limit) {
        return new Paging(offset, limit);
    }

    /** Address the previous paging.
     * Will never go beyond offset 0.
     * @return returns the paging at {@code offset - limit} and keeps the
     * {@code limit}. Will return {@code 0} if going beyond offset 0.
     * */
    public Paging previous() {
        int newOffset = offset - limit;
        if (newOffset < 0) {
            newOffset = 0;
        }
        return new Paging(newOffset, limit);
    }

    /** Address the next paging.
     * @return returns the paging at {@code offset + limit} and keeps the
     * {@code limit}.
     * */
    public Paging next() {
        return new Paging(offset + limit, limit);
    }

    @Override
    protected void apply(final MultivaluedMap<String, String> requestParams) {
        log.info("paging={}", this);
        requestParams.put("limit", Collections.singletonList(
                Integer.toString(getLimit())));
        requestParams.put("offset", Collections.singletonList(
                Integer.toString(getOffset())));
    }
}
