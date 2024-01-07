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

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import mockit.*;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

/**
 * Test for the EndpointDiscovery class.
 * @author Stephan Fuhrmann
 */
public class EndpointDiscoveryTest {

    private EndpointDiscovery endpointDiscovery;

    @Before
    public void init() {
        endpointDiscovery = new EndpointDiscovery("RadioBrowser4J");
    }

    @Test(expected = NullPointerException.class)
    public void createWithNull() {
        new EndpointDiscovery(null);
    }

    @Test
    public void apiUrls(@Mocked InetAddress inetAddressMock) throws UnknownHostException {
        InetAddress[] inetAddresses = new InetAddress[1];
        inetAddresses[0] = inetAddressMock;

        new Expectations() {{
            InetAddress.getAllByName(EndpointDiscovery.DNS_API_ADDRESS); result = inetAddresses;
            inetAddressMock.getCanonicalHostName(); result = "127.0.0.1";
        }};

        List<String> urls = endpointDiscovery.apiUrls();

        assertThat(urls, is(Collections.singletonList("https://127.0.0.1/")));

        new Verifications() {{
            InetAddress.getAllByName(EndpointDiscovery.DNS_API_ADDRESS); times = 1;
        }};
    }

    @Test
    public void discoverApiUrls(@Mocked Client clientMock,
                                @Mocked ClientBuilder clientBuilderMock,
                                @Mocked WebTarget webTargetMock) {
        new Expectations() {{
            ClientBuilder.newBuilder(); result = clientBuilderMock;
            clientBuilderMock.build(); result = clientMock;
            clientMock.target(URI.create("https://127.0.0.1/")); result = webTargetMock;
        }};

        List<EndpointDiscovery.DiscoveryResult> results = endpointDiscovery.discoverApiUrls(Collections.singletonList("https://127.0.0.1/"));
        assertThat(results.size(), is(1));
        assertThat(results.get(0).getDuration(), is(Matchers.greaterThan(0L)));

        new Verifications() {{
            clientMock.target(URI.create("https://127.0.0.1/")); times = 1;
        }};
    }

    @Test
    public void discover(@Mocked Client clientMock,
                         @Mocked ClientBuilder clientBuilderMock,
                         @Mocked WebTarget webTargetMock,
                         @Mocked InetAddress inetAddressMock) throws IOException {
        new Expectations() {{
            ClientBuilder.newBuilder(); result = clientBuilderMock;
            clientBuilderMock.build(); result = clientMock;
            clientMock.target(URI.create("https://127.0.0.1/")); result = webTargetMock;
            InetAddress.getAllByName(EndpointDiscovery.DNS_API_ADDRESS); result = new InetAddress[] {inetAddressMock};
            inetAddressMock.getCanonicalHostName(); result = "127.0.0.1";
        }};

        Optional<String> name = endpointDiscovery.discover();
        assertThat(name.isPresent(), is(true));
    }

    @Test
    public void getStats(@Mocked Client clientMock,
                         @Mocked ClientBuilder clientBuilderMock,
                         @Mocked WebTarget webTargetMock,
                         @Mocked Invocation.Builder invocationBuilderMock) {
        Stats myStats = new Stats();

        new Expectations() {{
            ClientBuilder.newBuilder(); result = clientBuilderMock;
            clientBuilderMock.build(); result = clientMock;
            clientMock.target(URI.create(RadioBrowser.DEFAULT_API_URL)); result = webTargetMock;
            invocationBuilderMock.get(Stats.class); result = myStats;
        }};

        Stats stats = endpointDiscovery.getStats(RadioBrowser.DEFAULT_API_URL, 5000);
        assertThat(stats, is(myStats));
    }
}
