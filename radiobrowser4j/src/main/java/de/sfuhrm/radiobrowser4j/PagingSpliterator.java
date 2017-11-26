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

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;

/** A spliterator that iterates over a paged set of entities.
 * @author Stephan Fuhrmann
 * */
@Slf4j
class PagingSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    /** The current paging. */
    private Paging currentPage;

    /** The current page data. */
    private List<T> currentData;

    /** Current index to {@linkplain #currentData}. */
    private int pageIndex = 0;

    /** The function for getting the specified page. */
    private final Function<Paging, List<T>> fetchPage;

    /** Creates a new instance.
     * @param fetchPageFunction the function for fetching the specified
     *                          page.
     * */
    PagingSpliterator(final Function<Paging, List<T>> fetchPageFunction) {
        super(Long.MAX_VALUE, 0);
        currentPage = Paging.at(0, 32);
        this.fetchPage = fetchPageFunction;
        loadPage();
    }

    /** Loads the page given in {@link #currentPage}. */
    private void loadPage() {
        log.debug("Loading page {}", currentPage);
        currentData = fetchPage.apply(currentPage);
        log.debug("Elements in loaded page: {}", currentData.size());
        pageIndex = 0;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (pageIndex >= currentData.size()) {
            currentPage = currentPage.next();
            loadPage();
        }

        if (pageIndex < currentData.size()) {
            T element = currentData.get(pageIndex);
            action.accept(element);
            pageIndex++;
            return true;
        }

        return false;
    }
}
