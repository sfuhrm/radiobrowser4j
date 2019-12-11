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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of a api endpoint statistic.
 * @author Stephan Fuhrmann
 */
@EqualsAndHashCode(of = {"id", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
final class Stats {
    // {"supported_version":1,"software_version":"0.5.0","status":"OK","stations":25539,"stations_broken":828,"tags":6533,"clicks_last_hour":1,"clicks_last_day":74,"languages":366,"countries":232}

    /** The supported version. */
    @Getter @Setter
    @JsonProperty("supported_version")
    private Integer supportedVersion;

    /** The software version. */
    @Getter @Setter
    @JsonProperty("software_version")
    private String softwareVersion;

    /** The status, should be "OK". */
    @Getter @Setter
    @JsonProperty("status")
    private String status;
}
