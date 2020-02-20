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
    name,
    /**
     * The station url.
     * @see Station#getUrl()
     */
    url,
    /**
     * The station homepage.
     * @see Station#getHomepage()
     */
    homepage,
    /**
     * The station favicon.
     * @see Station#getFavicon()
     */
    favicon,
    /**
     * The station tags.
     * @see Station#getTagList()
     */
    tags,
    /**
     * The station country.
     * @see Station#getCountry()
     */
    country,
    /**
     * The station state.
     * @see Station#getState()
     */
    state,
    /**
     * The station language.
     * @see Station#getLanguage()
     */
    language,
    /**
     * The station votes.
     * @see Station#getVotes()
     */
    votes,
    /**
     * The station negative votes.
     * @see Station#getNegativevotes()
     */
    @Deprecated
    negativevotes,
    /**
     * The station codec.
     * @see Station#getCodec()
     */
    codec,
    /**
     * The station bitrate.
     * @see Station#getBitrate()
     */
    bitrate,
    /**
     * Whether last check was ok.
     * @see Station#getLastcheckok()
     */
    lastcheckok,
    /**
     * Last check time.
     * @see Station#getLastchecktime()
     */
    lastchecktime,
    /**
     * The click time stamp.
     * @see Station#getClicktimestamp()
     */
    clicktimestamp,
    /**
     * The station click count.
     * @see Station#getClickcount()
     */
    clickcount,
    /**
     * The station click trend.
     * @see Station#getClicktrend()
     */
    clicktrend
}
