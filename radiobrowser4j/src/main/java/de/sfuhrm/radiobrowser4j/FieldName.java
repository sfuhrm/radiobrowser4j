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
     */
    NAME,
    /**
     * The station url.
     */
    URL,
    /**
     * The station homepage.
     */
    HOMEPAGE,
    /**
     * The station favicon.
     */
    FAVICON,
    /**
     * The station tags.
     */
    TAGS,
    /**
     * The station country.
     */
    COUNTRY,
    /**
     * The station state.
     */
    STATE,
    /**
     * The station language.
     */
    LANGUAGE,
    /**
     * The station votes.
     */
    VOTES,
    /**
     * The station codec.
     */
    CODEC,
    /**
     * The station bitrate.
     */
    BITRATE,
    /**
     * Whether last check was ok.
     */
    LASTCHECKOK,
    /**
     * Last check time.
     */
    LASTCHECKTIME,
    /**
     * The click time stamp.
     */
    CLICKTIMESTAMP,
    /**
     * The station click count.
     */
    CLICKCOUNT,
    /**
     * The station click trend.
     */
    CLICKTREND
}
