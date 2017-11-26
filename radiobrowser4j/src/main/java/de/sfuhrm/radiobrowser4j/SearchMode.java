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
 * @see RadioBrowser#listStationsBy(Paging, SearchMode, String)
 * */
public enum SearchMode {
    /** Search by ID. */
    byid,
    /** Search by name. */
    byname,
    /** Search by exact name. */
    bynameexact,
    /** Search by codec. */
    bycodec,
    /** Search by exact codec. */
    bycodecexact,
    /** Search by country. */
    bycountry,
    /** Search by exact country. */
    bycountryexact,
    /** Search by state. */
    bystate,
    /** Search by exact state. */
    bystateexact,
    /** Search by language. */
    bylanguage,
    /** Search by exact language. */
    bylanguageexact,
    /** Search by tag. */
    bytag,
    /** Search by exact tag. */
    bytagexact;
}
