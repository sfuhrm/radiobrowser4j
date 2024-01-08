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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for the EndpointDiscovery class.
 * @author Stephan Fuhrmann
 */
@ExtendWith(MockitoExtension.class)
public class EndpointDiscoveryTest {

    @Mock
    private InetAddressHelper inetAddressHelper;

    @Mock
    private InetAddress inetAddress;

    @Mock
    private RadioBrowser radioBrowser;

    @Mock
    private ExecutorService executorService;

    @Mock
    private Future future;

    private EndpointDiscovery endpointDiscovery;

    @BeforeEach
    public void setUp() {
        endpointDiscovery = new EndpointDiscovery("my useragent",
                null, null, null,
                inetAddressHelper);
        endpointDiscovery.setExecutorServiceProducer(() -> executorService);
    }

    @Test
    public void createWithNull() {
        assertThrows(NullPointerException.class, () -> {
            new EndpointDiscovery(null);
        });
    }

    @Test
    public void apiUrls() throws UnknownHostException {
        InetAddress[] inetAddresses = new InetAddress[1];
        inetAddresses[0] = inetAddress;
        when(inetAddressHelper.getAllByName(EndpointDiscovery.DNS_API_ADDRESS)).thenReturn(inetAddresses);
        when(inetAddress.getCanonicalHostName()).thenReturn("127.0.0.1");

        List<String> urls = endpointDiscovery.apiUrls();

        assertThat(urls, is(Collections.singletonList("https://127.0.0.1/")));

        verify(inetAddressHelper, times(1)).getAllByName(EndpointDiscovery.DNS_API_ADDRESS);
        verify(inetAddress, times(1)).getCanonicalHostName();
    }

    @Test
    public void discoverApiUrls() throws ExecutionException, InterruptedException, TimeoutException {
        Stats myStats = new Stats();
        EndpointDiscovery.DiscoveryResult discoveryResult = new EndpointDiscovery.DiscoveryResult("https://127.0.0.1/", 123, myStats);

        when(executorService.submit(ArgumentMatchers.any(Callable.class))).thenReturn(future);
        when(future.get(anyLong(), any())).thenReturn(discoveryResult);

        List<EndpointDiscovery.DiscoveryResult> results = endpointDiscovery.discoverApiUrls(Collections.singletonList("https://127.0.0.1/"));
        assertThat(results.size(), is(1));
        assertThat(results.get(0).getDuration(), is(Matchers.greaterThan(0L)));
        assertThat(results.get(0).getEndpoint(), is("https://127.0.0.1/"));

        verify(executorService, times(1)).shutdown();
    }

    @Test
    public void discover() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Stats myStats = new Stats();
        EndpointDiscovery.DiscoveryResult discoveryResult = new EndpointDiscovery.DiscoveryResult("https://127.0.0.1/", 123, myStats);

        InetAddress[] inetAddresses = new InetAddress[1];
        inetAddresses[0] = inetAddress;
        when(inetAddressHelper.getAllByName(EndpointDiscovery.DNS_API_ADDRESS)).thenReturn(inetAddresses);
        when(executorService.submit(ArgumentMatchers.any(Callable.class))).thenReturn(future);
        when(future.get(anyLong(), any())).thenReturn(discoveryResult);

        Optional<String> name = endpointDiscovery.discover();
        assertThat(name.isPresent(), is(true));
        assertThat(name.get(), is("https://127.0.0.1/"));

        verify(executorService, times(1)).shutdown();
    }

}
