package org.openspotlight.remote.client;

public class RemoteObjectFactory {

    public RemoteObjectFactory(
                                final String host, final int port, final String userName, final String password ) {
        throw new UnsupportedOperationException();
        //server side: createUserToken
    }

    public <T> T createRemoteObject( final Class<T> remoteObjectType,
                                     final Object... parameters ) {
        throw new UnsupportedOperationException();
    }

}
