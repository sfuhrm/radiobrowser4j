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

/** The mode to search for.
 * @see RadioBrowser#listStationsBy(Paging,
 * SearchMode, String, ListParameter...)
 * @author Stephan Fuhrmann
 * */
public enum SearchMode {
    /** Search by UUID. */
    BYUUID,
    /** Search by name. */
    BYNAME,
    /** Search by exact name. */
    BYNAMEEXACT,
    /** Search by codec. */
    BYCODEC,
    /** Search by exact codec. */
    BYCODECEXACT,
    /** Search by country. */
    BYCOUNTRY,
    /** Search by exact country. */
    BYCOUNTRYEXACT,
    /** Search by ISO 3166-1 alpha-2 country code. */
    BYCOUNTRYCODEEXACT,
    /** Search by state. */
    BYSTATE,
    /** Search by exact state. */
    BYSTATEEXACT,
    /** Search by language. */
    BYLANGUAGE,
    /** Search by exact language. */
    BYLANGUAGEEXACT,
    /** Search by tag. */
    BYTAG,
    /** Search by exact tag. */
    BYTAGEXACT,
    /** Advanced search by chosen {@link SearchParameter}. */
    SEARCH
}
