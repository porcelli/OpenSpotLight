package org.openspotlight.graph.server.test;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

public class ExampleCajoServer {

    public static void main( final String... args ) throws Exception {
        startServer();
        while (true) {
            Thread.currentThread().sleep(5000);
        }
    }

    public static void startServer() throws Exception {

        final Example server = new ExampleServer();
        Remote.config(null, 1198, null, 0);
        ItemServer.bind(server, "server");

    }

}
