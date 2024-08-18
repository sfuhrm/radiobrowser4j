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
 * @param <T> the element type to return in the spliteration.
 * @author Stephan Fuhrmann
 * */
@Slf4j
class PagingSpliterator<T> extends Spliterators.AbstractSpliterator<T> {
    /** Default page size. */
    private static final int FETCH_SIZE_DEFAULT = 128;

    /** The current paging. */
    private Paging logicalPage;

    /** The current page data. */
    private List<T> currentData;

    /** Current index to {@linkplain #currentData}. */
    private int currentDataIndex = 0;

    /** The function for getting the specified page. */
    private final Function<Paging, List<T>> fetchPage;

    /** The optional view boundary of the paging. Paging will happen in this
     * range if it is set.
     * */
    private Paging view;

    /** Whether this spliterator is at the end of the list. */
    private boolean endOfList;

    /** Creates a new instance.
     * @param fetchPageFunction the function for fetching the specified
     *                          page.
     * @param view optional view boundary of the spliterator. This is the offset and limit range in
     *                 what the spliterator is returning elements.
     * */
    PagingSpliterator(final Function<Paging, List<T>> fetchPageFunction, final Paging view) {
        super(Long.MAX_VALUE, 0);
        logicalPage = Paging.at(0, FETCH_SIZE_DEFAULT);
        this.fetchPage = fetchPageFunction;
        this.currentData = null;
        this.view = view;
        this.endOfList = false;
    }

    /** Loads the page given in {@link #logicalPage}. */
    private void loadPage() {

        Paging physicalPage = null;
        // do we have a view?
        if (view != null) {
            boolean lastPage = logicalPage.getOffset() + FETCH_SIZE_DEFAULT >= view.getLimit();

            int fetchLimit = FETCH_SIZE_DEFAULT;
            // last page has a hard restriction
            if (lastPage) {
                fetchLimit = Math.min(FETCH_SIZE_DEFAULT, view.getLimit() - logicalPage.getOffset());
                if (fetchLimit <= 0) {
                    fetchLimit = 0;
                    endOfList = true;
                }
            }

            if (fetchLimit > 0) {
                physicalPage = Paging.at(
                        view.getOffset() + logicalPage.getOffset(), fetchLimit);
            }
        } else {
            physicalPage = logicalPage;
        }

        log.debug("Loading logical page {}, physical page {}, view {}", logicalPage, physicalPage, view);
        if (physicalPage != null) {
            currentData = fetchPage.apply(physicalPage);
            log.debug("Elements in loaded page: {}", currentData.size());
            currentDataIndex = 0;
            if (currentData.isEmpty()) {
                endOfList = true;
            }
        }
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        boolean hasMore = ! endOfList;

        // has no data -> load
        if (currentData == null && !endOfList) {
            loadPage();
        }

        // end of page -> increment, load
        if (currentData != null &&
                ! endOfList &&
                currentDataIndex >= currentData.size()) {
            logicalPage = logicalPage.next();
            loadPage();
        }

        // in page -> return element
        if (currentData != null &&  currentDataIndex < currentData.size()) {
            T element = currentData.get(currentDataIndex);
            action.accept(element);
            currentDataIndex++;
            hasMore = true;
        }

        return hasMore;
    }
}
