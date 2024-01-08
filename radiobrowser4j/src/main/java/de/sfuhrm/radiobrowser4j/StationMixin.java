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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import java.util.UUID;

/**
 * Mixin for station.
 * @author Stephan Fuhrmann
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class StationMixin {
    /** @see Station#getChangeUUID().
     * @return does not matter.
     * */
    @JsonProperty("changeuuid")
    abstract UUID getChangeUUID();

    /** @see Station#getStationUUID().
     * @return does not matter.
     * */
    @JsonProperty("stationuuid")
    abstract UUID getStationUUID();

    /** @see Station#getStationUUID().
     * @return does not matter.
     * */
    @JsonProperty("url_resolved")
    abstract String getUrlResolved();

    /** The tags for this station as a list.
     * @return does not matter.
     * @see #getTagList() ()
     * */
    @JsonIgnore
    abstract List<String> getTagList();

    /** Official country codes according to ISO 3166-1 alpha-2.
     * @return does not matter.
     * */
    @JsonProperty("countrycode")
    abstract String getCountryCode();

    /** @see Station#getLanguageList()
     * @return does not matter.
     * */
    @JsonIgnore
    abstract List<String> getLanguageList();

    /**
     * @see Station#getGeoLatitude()
     * @return does not matter.
     * */
    @JsonProperty("geo_lat")
    abstract Double getGeoLatitude();

    /** @see Station#getGeoLongitude()
     *  @return does not matter.
     * */
    @JsonProperty("geo_long")
    abstract Double getGeoLongitude();

    /**
     * @return does not matter.
     * @see Station#getHasExtendedInfo()
     * */
    @JsonProperty("has_extended_info")
    abstract Boolean getHasExtendedInfo();

    /**
     * @return does not matter.
     * @see Station#getTags()
     * */
    @JsonGetter("tags")
    abstract String getTags();

    /**
     * @param commaTags does not matter.
     * @see Station#setTags(String)
     * */
    @JsonSetter("tags")
    abstract void setTags(String commaTags);

    /**
     * @return does not matter.
     * @see Station#getLanguage()
     * */
    @JsonGetter("language")
    abstract String getLanguage();

    /**
     * @param commaLanguages does not matter.
     * @see Station#getHasExtendedInfo()
     * */
    @JsonSetter("language")
    abstract void setLanguage(String commaLanguages);
}
