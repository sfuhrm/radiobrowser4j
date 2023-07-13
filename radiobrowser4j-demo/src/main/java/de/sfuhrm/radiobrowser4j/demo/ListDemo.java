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
package de.sfuhrm.radiobrowser4j.demo;

import de.sfuhrm.radiobrowser4j.EndpointDiscovery;
import de.sfuhrm.radiobrowser4j.FieldName;
import de.sfuhrm.radiobrowser4j.ListParameter;
import de.sfuhrm.radiobrowser4j.RadioBrowser;

import java.io.IOException;
import java.util.Optional;

/** List all radio stations.
 * @author Stephan Fuhrmann
 * */
public final class ListDemo {

    /** Timeout in millis. */
    public static final int TIMEOUT_DEFAULT = 5000;

    /** Stations to show. */
    public static final int LIMIT_DEFAULT = 64;

    /** No instance allowed. */
    private ListDemo() {

    }

    /** Main method.
     * @param args command line arguments.
     * @throws IOException if endpoint discovery fails.
     * */
    public static void main(final String...args) throws IOException {
        String myAgent = "https://github.com/sfuhrm/radiobrowser4j";
        Optional<String> endpoint = new EndpointDiscovery(myAgent).discover();
        RadioBrowser radioBrowser = new RadioBrowser(
                endpoint.get(),
                TIMEOUT_DEFAULT,
                myAgent);
        radioBrowser
                .listStations(ListParameter.create().order(FieldName.NAME))
                .limit(LIMIT_DEFAULT)
                .forEach(s -> System.out.printf("%s: %s%n",
                        s.getName(), s.getUrl()
                        ));
    }
}
