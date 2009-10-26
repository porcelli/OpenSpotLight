package org.openspotlight.remote.server.test;

import org.junit.Test;
import org.openspotlight.remote.client.RemoteObjectFactory;
import org.openspotlight.remote.server.RemoteObjectServer;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.test.RemoteObjectFactoryTest.AllowUserValidAutenticator;

public class ShutdownAndRestartTheServerSeveralTimes {

    @Test
    public void ShutdownAndRestartTheServerSeveralTimes() throws Exception {

        RemoteObjectServer server = RemoteObjectServerImpl.getDefault(new AllowUserValidAutenticator(), 7070, 250L);
        server.registerInternalObjectFactory(ExampleInterface.class, new ExampleInterfaceFactory());
        new RemoteObjectFactory("localhost", 7070, "valid", "sa").createRemoteObject(ExampleInterface.class);
        server = RemoteObjectServerImpl.getDefault(new AllowUserValidAutenticator(), 7070, 250L);
        new RemoteObjectFactory("localhost", 7070, "valid", "sa").createRemoteObject(ExampleInterface.class);
        server = RemoteObjectServerImpl.getDefault(new AllowUserValidAutenticator(), 7070, 250L);
        new RemoteObjectFactory("localhost", 7070, "valid", "sa").createRemoteObject(ExampleInterface.class);
        server.shutdown();

    }

}
