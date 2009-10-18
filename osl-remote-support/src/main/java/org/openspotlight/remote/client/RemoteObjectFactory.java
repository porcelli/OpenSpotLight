package org.openspotlight.remote.client;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import gnu.cajo.utils.extra.TransparentItemProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;

import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.InvalidReferenceTypeException;
import org.openspotlight.remote.server.RemoteObjectServer;

/**
 * A factory for creating RemoteObject objects.
 */
public class RemoteObjectFactory {

    private static class RemoteReferenceHandler implements InvocationHandler {

        private final RemoteReference<?> remoteReference;

        public RemoteReferenceHandler(
                                       final RemoteReference<?> remoteReference ) {
            this.remoteReference = remoteReference;
        }

        public Object invoke( final Object proxy,
                              final Method method,
                              final Object[] args ) throws Throwable {

            throw new UnsupportedOperationException();
        }

    }

    /** The from server. */
    final RemoteObjectServer fromServer;

    /** The user token. */
    final UserToken          userToken;

    /**
     * Instantiates a new remote object factory.
     * 
     * @param host the host
     * @param port the port
     * @param userName the user name
     * @param password the password
     * @throws CantConnectException the cant connect exception
     * @throws AccessDeniedException the access denied exception
     */
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

    /**
     * Creates a new object on server.
     * 
     * @param <T>
     * @param remoteObjectType the remote object type
     * @param parameters the parameters
     * @return the T
     * @throws InvalidReferenceTypeException
     */
    public <T> T createRemoteObject( final Class<T> remoteObjectType,
                                     final Object... parameters ) throws InvalidReferenceTypeException {
        final RemoteReference<T> remoteReference = this.fromServer.createRemoteReference(this.userToken, remoteObjectType,
                                                                                         parameters);
        final T newObjectProxy = (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {remoteObjectType},
                                                           new RemoteReferenceHandler(remoteReference));
        return newObjectProxy;
    }

}
