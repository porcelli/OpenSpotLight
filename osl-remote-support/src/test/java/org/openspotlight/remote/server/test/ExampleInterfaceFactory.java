package org.openspotlight.remote.server.test;

import org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory;

public class ExampleInterfaceFactory implements InternalObjectFactory<ExampleInterface> {

    public ExampleInterface createNewInstance( final Object... parameters ) {
        return new ExampleInterfaceImplementation();
    }

    public Class<ExampleInterface> getTargetObjectType() {
        return ExampleInterface.class;
    }

    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
