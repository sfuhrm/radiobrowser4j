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
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * Parameters for search calls.
 *
 * @author Tomas Sekera
 */
@Slf4j
@ToString
public final class SearchParameter {
    /**
     * The field name to sort by.
     */
    @Getter
    private final SearchKey searchKey;

    @Getter
    private final String searchValue;

    /**
     * Private constructor.
     *
     * @see #create(SearchKey, String) ()
     */
    private SearchParameter(@NonNull final SearchKey searchKey, @NonNull final String searchValue) {
        this.searchKey = searchKey;
        this.searchValue = searchValue;
    }

    /**
     * Creates a new instance.
     *
     * @param searchKey  the search name.
     * @param searchValue the search value.
     * @return a new instance.
     */
    public static SearchParameter create(@NonNull final SearchKey searchKey, @NonNull final String searchValue) {
        return new SearchParameter(searchKey, searchValue);
    }

    /**
     * Transfer this list parameter the the passed multi valued map.
     *
     * @param requestParams the target of the list params.
     */
    void applyTo(final MultivaluedMap<String, String> requestParams) {
        log.info("list={}", this);
        if (getSearchKey() != null) {
            requestParams.put(getSearchKey().name().toLowerCase(),
                    Collections.singletonList(searchValue));
        }
    }
}
