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

/**
 * The key for advanced searching.
 *
 * @author Tomas Sekera
 */
public enum SearchKey {

    /**
     * The station tags.
     *
     * @see Station#getTagList()
     */
    TAG,
    /**
     * The station country code.
     *
     * @see Station#getCountryCode()
     */
    COUNTRYCODE,

}
