package org.openspotlight.graph.server.test;

import gnu.cajo.invoke.Remote;

public class ExampleCajoClient {

    public static void main( final String[] args ) throws Exception {

        final Object fromServer = Remote.getItem("//127.0.0.1:1198/server");
        final Object result = Remote.invoke(fromServer, "add3", new Object[] {3});
        final Object strangeResult = result.getClass().getMethod("getI").invoke(result);
        System.out.println(strangeResult);

    }

}
