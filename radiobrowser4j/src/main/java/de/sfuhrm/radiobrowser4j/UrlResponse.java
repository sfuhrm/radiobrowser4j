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

import lombok.Getter;

import java.util.UUID;


/**
 * API response from the radio browser API.
 * @see <a href="https://at1.api.radio-browser.info/">API documentation</a>
 * @author Stephan Fuhrmann
 */
@Getter
class UrlResponse {
    /** Whether this operation went ok. */
    private boolean ok;

    /** The error message of this operation. */
    private String message;

    /** The uuid of the referenced entity. */
    private UUID uuid;

    /** The name of the referenced entity. */
    private String name;

    /** The url of the referenced entity. */
    private String url;

}
