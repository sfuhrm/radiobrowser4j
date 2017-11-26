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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/** Immutable paging configuration.
 * */
@ToString
@EqualsAndHashCode
public final class Paging {
    /** A default start page with offset 0 and limit 64.
     * */
    public final static Paging DEFAULT_START = new Paging(0, 64);

    /** The start element to start processing. */
    @Getter
    private final int offset;

    /** The total number of elements to process. */
    @Getter
    private final int limit;

    public Paging(int myOffset, int myLimit) {
        if (myOffset < 0) {
            throw new IllegalArgumentException("Offset is "+myOffset+", but must be >= 0");
        }
        if (myLimit <= 0) {
            throw new IllegalArgumentException("Limit is "+myLimit+", but must be > 0");
        }
        this.offset = myOffset;
        this.limit = myLimit;
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
}
