package org.openspotlight.remote.server.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ServerAndClientTest {

    @Test
    public void shouldConnectToRemoteInstance() throws Exception {
        ExampleCajoServer.startServer();
        final int result = ExampleCajoClient.getResult();
        assertThat(result, is(6));
    }

}
