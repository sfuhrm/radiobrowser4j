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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Representation of a api endpoint statistic.
 * @author Stephan Fuhrmann
 */
@EqualsAndHashCode(of = {"id", "name"})
@JsonIgnoreProperties(ignoreUnknown = true)
final class Stats {

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
