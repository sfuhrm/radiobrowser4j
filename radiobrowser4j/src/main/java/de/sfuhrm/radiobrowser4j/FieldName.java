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

/** The field name for ordering.
 * @author Stephan Fuhrmann
 * */
public enum FieldName {
    /**
     * The station name.
     * @see Station#name
     */
    name,
    /**
     * The station url.
     * @see Station#url
     */
    url,
    /**
     * The station homepage.
     * @see Station#homepage
     */
    homepage,
    /**
     * The station favicon.
     * @see Station#favicon
     */
    favicon,
    /**
     * The station tags.
     * @see Station#tagList
     */
    tags,
    /**
     * The station country.
     * @see Station#country
     */
    country,
    /**
     * The station state.
     * @see Station#state
     */
    state,
    /**
     * The station language.
     * @see Station#language
     */
    language,
    /**
     * The station votes.
     * @see Station#votes
     */
    votes,
    /**
     * The station negative votes.
     * @see Station#negativevotes
     */
    negativevotes,
    /**
     * The station codec.
     * @see Station#codec
     */
    codec,
    /**
     * The station bitrate.
     * @see Station#bitrate
     */
    bitrate,
    /**
     * Whether last check was ok.
     * @see Station#lastcheckok
     */
    lastcheckok,
    /**
     * Last check time.
     * @see Station#lastchecktime
     */
    lastchecktime,
    /**
     * The click time stamp.
     * @see Station#clicktimestamp
     */
    clicktimestamp,
    /**
     * The station click count.
     * @see Station#clickcount
     */
    clickcount,
    /**
     * The station click trend.
     * @see Station#clicktrend
     */
    clicktrend
}
