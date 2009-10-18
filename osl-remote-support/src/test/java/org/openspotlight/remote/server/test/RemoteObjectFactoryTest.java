package org.openspotlight.remote.server.test;

import org.junit.Test;
import org.openspotlight.remote.client.CantConnectException;
import org.openspotlight.remote.client.RemoteObjectFactory;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAutenticator;

public class RemoteObjectFactoryTest {

    private static class AllowAlwaysUserAutenticator implements UserAutenticator {

        public boolean canConnect( final String userName,
                                   final String password,
                                   final String clientHost ) {
            return true;
        }

    }

    private static class AllowNeverUserAutenticator implements UserAutenticator {

        public boolean canConnect( final String userName,
                                   final String password,
                                   final String clientHost ) {
            return false;
        }

    }

    @Test
    public void shouldCreateRemoteObjectFactory() throws Exception {
        final RemoteObjectServerImpl server = new RemoteObjectServerImpl(new AllowAlwaysUserAutenticator(), 7070);
        final RemoteObjectFactory client = new RemoteObjectFactory("localhost", 7070, "userName", "password");
        server.shutdown();
        Thread.sleep(500);
    }

    @Test( expected = CantConnectException.class )
    public void shouldNotCreateRemoteObjectFactoryWhenServerIsInvalid() throws Exception {
        final RemoteObjectFactory client = new RemoteObjectFactory("localhost", 666, "userName", "password");
    }

    @Test( expected = AccessDeniedException.class )
    public void shouldNotCreateRemoteObjectFactoryWhenUserIsInvalid() throws Exception {
        final RemoteObjectServerImpl server = new RemoteObjectServerImpl(new AllowNeverUserAutenticator(), 7171);
        final RemoteObjectFactory client = new RemoteObjectFactory("localhost", 7171, "userName", "password");
        server.shutdown();
        Thread.sleep(500);
    }

}
