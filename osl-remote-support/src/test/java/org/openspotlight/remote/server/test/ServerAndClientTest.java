package org.openspotlight.remote.server.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class ServerAndClientTest {

    @Test
    @Ignore
    // for some strange reason it doesn't work on maven, but it works on eclipse and junit. 
    // since there's some best tests running I'll leave this test ignored
    public void shouldConnectToRemoteInstance() throws Exception {
        ExampleCajoServer.startServer();
        final int result = ExampleCajoClient.getResult();
        assertThat(result, is(6));
    }

}
