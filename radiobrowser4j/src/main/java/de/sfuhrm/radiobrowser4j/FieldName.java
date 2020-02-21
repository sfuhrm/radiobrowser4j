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
     * @see Station#getName()
     */
    NAME,
    /**
     * The station url.
     * @see Station#getUrl()
     */
    URL,
    /**
     * The station homepage.
     * @see Station#getHomepage()
     */
    HOMEPAGE,
    /**
     * The station favicon.
     * @see Station#getFavicon()
     */
    FAVICON,
    /**
     * The station tags.
     * @see Station#getTagList()
     */
    TAGS,
    /**
     * The station country.
     * @see Station#getCountry()
     */
    COUNTRY,
    /**
     * The station state.
     * @see Station#getState()
     */
    STATE,
    /**
     * The station language.
     * @see Station#getLanguage()
     */
    LANGUAGE,
    /**
     * The station votes.
     * @see Station#getVotes()
     */
    VOTES,
    /**
     * The station codec.
     * @see Station#getCodec()
     */
    CODEC,
    /**
     * The station bitrate.
     * @see Station#getBitrate()
     */
    BITRATE,
    /**
     * Whether last check was ok.
     * @see Station#getLastcheckok()
     */
    LASTCHECKOK,
    /**
     * Last check time.
     * @see Station#getLastchecktime()
     */
    LASTCHECKTIME,
    /**
     * The click time stamp.
     * @see Station#getClicktimestamp()
     */
    CLICKTIMESTAMP,
    /**
     * The station click count.
     * @see Station#getClickcount()
     */
    CLICKCOUNT,
    /**
     * The station click trend.
     * @see Station#getClicktrend()
     */
    CLICKTREND
}
