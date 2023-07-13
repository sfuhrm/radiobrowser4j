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

/** Immutable limit configuration.
 * The limit is used to limit the size of a sub list that can be retrieved.
 * @see Paging
 * @author Stephan Fuhrmann
 * */
@ToString
@EqualsAndHashCode
public final class Limit {

    /** The total number of elements to process. */
    @Getter
    private final int size;

    /** Creates a new paging. This method is private.
     * @param myLimit the limit to use.
     * @throws IllegalArgumentException if limit or offset are wrong.
     * @see #of(int)
     * */
    private Limit(final int myLimit) {
        if (myLimit <= 0) {
            throw new IllegalArgumentException(
                    "Limit is " + myLimit + ", but must be > 0");
        }
        this.size = myLimit;
    }

    /** Creates a new limit with the given number of entries.
     * @param limit the maximum number of entries.
     * @return the limit instance created.
     * */
    public static Limit of(final int limit) {
        return new Limit(limit);
    }
}
