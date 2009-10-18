package org.openspotlight.remote.client;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import gnu.cajo.utils.extra.TransparentItemProxy;

import java.net.InetAddress;

import org.openspotlight.remote.internal.UserToken;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.RemoteObjectServer;

public class RemoteObjectFactory {
    final RemoteObjectServer fromServer;
    final UserToken          userToken;

    public RemoteObjectFactory(
                                final String host, final int port, final String userName, final String password )
        throws CantConnectException, AccessDeniedException {
        String clientHost;
        try {
            clientHost = InetAddress.getLocalHost().getHostAddress().toString();
            final String connectionString = format("//{0}:{1}/RemoteObjectServer", host, new Integer(port).toString());
            this.fromServer = (RemoteObjectServer)TransparentItemProxy.getItem(connectionString,
                                                                               new Class<?>[] {RemoteObjectServer.class});

        } catch (final Exception e) {
            throw logAndReturnNew(e, CantConnectException.class);
        }

        this.userToken = this.fromServer.createUserToken(userName, password, clientHost);
    }

    public <T> T createRemoteObject( final Class<T> remoteObjectType,
                                     final Object... parameters ) {
        throw new UnsupportedOperationException();
    }

}
