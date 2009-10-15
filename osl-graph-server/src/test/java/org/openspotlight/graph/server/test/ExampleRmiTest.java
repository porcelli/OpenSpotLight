package org.openspotlight.graph.server.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ExampleRmiTest {

    public static void main( final String... args ) throws Exception {
        if (args.length == 0) {
            throw new Exception("use arguments: server or client");
        }
        setupSecurity();
        if ("server".equals(args[0])) {
            startServer();
        } else if ("client".equals(args[0])) {
            shouldCallTheServerObject();
        } else {
            throw new Exception("use arguments: server or client");
        }
    }

    public static void setupSecurity() throws Exception {
        System.setProperty("java.security.policy", "src/main/resources/policy-files/all-permissions.policy");
        System.setSecurityManager(new RMISecurityManager());
    }

    public static void shouldCallTheServerObject() throws Exception {
        final Example fromServer = (Example)Naming.lookup("rmi://localhost:7070/example");
        final int result = fromServer.add3(3);
        System.out.println(result);
        assertThat(result, is(6));
    }

    public static void startServer() throws Exception {

        final Example server = new ExampleServer();
        final Example stub = (Example)UnicastRemoteObject.exportObject(server, 0);
        final Registry registry = LocateRegistry.createRegistry(7070);
        registry.rebind("example", stub);
        while (true) {
            Thread.currentThread().sleep(5000);
        }

    }

}
