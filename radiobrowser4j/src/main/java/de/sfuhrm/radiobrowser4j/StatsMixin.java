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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mix-ins for the Stats for Jackson mapping.
 * @author Stephan Fuhrmann
 */
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class StatsMixin {

    /** The supported version.
     * @return does not matter.
     * */
    @JsonProperty("supported_version")
    abstract Integer getSupportedVersion();

    /** The version of the API.
     * @return does not matter.
     * */
    @JsonProperty("software_version")
    abstract String getSoftwareVersion();

    /** The status, should be "OK".
     * @return does not matter.
     * */
    @JsonProperty("status")
    abstract String getStatus();

    /** The number of broken stations.
     * @return does not matter.
     * */
    @JsonProperty("stations_broken")
    abstract Integer getStationsBroken();

    /** The number of clicks in the last hour.
     * @return does not matter.
     * */
    @JsonProperty("clicks_last_hour")
    abstract Integer getClicksLastHour();

    /** The number of clicks in the last 24 hours.
     * @return does not matter.
     * */
    @JsonProperty("clicks_last_day")
    abstract Integer getClicksLastDay();
}
