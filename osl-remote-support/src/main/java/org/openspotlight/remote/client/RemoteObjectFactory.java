package org.openspotlight.remote.client;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import gnu.cajo.utils.extra.TransparentItemProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;

import org.openspotlight.remote.annotation.UnsupportedRemoteMethod;
import org.openspotlight.remote.internal.RemoteObjectInvocation;
import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.InvalidReferenceTypeException;
import org.openspotlight.remote.server.RemoteObjectServer;
import org.openspotlight.remote.server.RemoteObjectServer.AbstractInvocationResponse;
import org.openspotlight.remote.server.RemoteObjectServer.LocalCopyInvocationResponse;
import org.openspotlight.remote.server.RemoteObjectServer.RemoteReferenceInvocationResponse;

/**
 * A factory for creating RemoteObject objects.
 */
public class RemoteObjectFactory {

    private static class RemoteReferenceHandler<T> implements InvocationHandler {

        private final RemoteReference<T> remoteReference;
        private final RemoteObjectServer fromServer;

        private static final Object[]    EMPTY_ARR = new Object[0];

        public RemoteReferenceHandler(
                                       final RemoteObjectServer fromServer, final RemoteReference<T> remoteReference ) {
            this.remoteReference = remoteReference;
            this.fromServer = fromServer;
        }

        public Object invoke( final Object proxy,
                              final Method method,
                              final Object[] args ) throws Throwable {
            if (method.getAnnotation(UnsupportedRemoteMethod.class) != null) {
                throw new UnsupportedOperationException();
            }
            final Class<?>[] parameterTypes = method.getParameterTypes();
            final RemoteObjectInvocation<T> invocation = new RemoteObjectInvocation<T>(method.getReturnType(), parameterTypes,
                                                                                       args == null ? EMPTY_ARR : args,
                                                                                       method.getName(), this.remoteReference);
            try {
                final AbstractInvocationResponse<Object> result = this.fromServer.invokeRemoteMethod(invocation);
                if (result instanceof LocalCopyInvocationResponse<?>) {
                    final LocalCopyInvocationResponse<Object> localCopy = (LocalCopyInvocationResponse<Object>)result;
                    return localCopy.getLocalCopy();
                } else if (result instanceof RemoteReferenceInvocationResponse<?>) {
                    //FIXME here, cache is mandatory if on server the return is the same reference
                    final RemoteReferenceInvocationResponse<Object> remoteReferenceResponse = (RemoteReferenceInvocationResponse<Object>)result;
                    final RemoteReference<Object> methodResponseRemoteReference = remoteReferenceResponse.getRemoteReference();
                    final Object newObjectProxy = Proxy.newProxyInstance(
                                                                         this.getClass().getClassLoader(),
                                                                         new Class[] {invocation.getReturnType()},
                                                                         new RemoteReferenceHandler<Object>(this.fromServer,
                                                                                                            methodResponseRemoteReference));

                    return newObjectProxy;
                } else {
                    throw logAndReturn(new IllegalStateException());
                }

            } catch (final InvocationTargetException e) {
                throw e.getCause();
            }

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
    @SuppressWarnings( "unchecked" )
    public <T> T createRemoteObject( final Class<T> remoteObjectType,
                                     final Object... parameters ) throws InvalidReferenceTypeException {
        final RemoteReference<T> remoteReference = this.fromServer.createRemoteReference(this.userToken, remoteObjectType,
                                                                                         parameters);
        final T newObjectProxy = (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {remoteObjectType},
                                                           new RemoteReferenceHandler(this.fromServer, remoteReference));
        return newObjectProxy;
    }

}
