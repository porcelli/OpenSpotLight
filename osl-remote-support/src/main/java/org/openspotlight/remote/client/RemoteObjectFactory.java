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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.util.reflection.MethodIdentificationSupport;
import org.openspotlight.common.util.reflection.MethodIdentificationSupport.MethodWithParametersKey;
import org.openspotlight.remote.annotation.CachedInvocation;
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

// TODO: Auto-generated Javadoc
/**
 * A factory for creating RemoteObject objects.
 */
public class RemoteObjectFactory {

    /**
     * The Class RemoteReferenceHandler.
     */
    private static class RemoteReferenceHandler<T> implements InvocationHandler {

        private static class ExceptionWrapper {

            private final Throwable throwable;

            public ExceptionWrapper(
                                     final Throwable throwable ) {
                super();
                this.throwable = throwable;
            }

            public Throwable getThrowable() {
                return this.throwable;
            }

        }

        private static final Object                        NULL_VALUE        = new Object();

        private final Map<MethodWithParametersKey, Object> methodResultCache = new ConcurrentHashMap<MethodWithParametersKey, Object>();

        /** The remote reference. */
        private final RemoteReference<T>                   remoteReference;

        /** The from server. */
        private final RemoteObjectServer                   fromServer;

        /** The Constant EMPTY_ARR. */
        private static final Object[]                      EMPTY_ARR         = new Object[0];

        /**
         * Instantiates a new remote reference handler.
         * 
         * @param fromServer the from server
         * @param remoteReference the remote reference
         */
        public RemoteReferenceHandler(
                                       final RemoteObjectServer fromServer, final RemoteReference<T> remoteReference ) {
            this.remoteReference = remoteReference;
            this.fromServer = fromServer;
        }

        /* (non-Javadoc)
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke( final Object proxy,
                              final Method method,
                              final Object[] args ) throws Throwable {
            if (method.isAnnotationPresent(UnsupportedRemoteMethod.class)) {
                throw new UnsupportedOperationException();
            }

            final Class<?>[] parameterTypes = method.getParameterTypes();
            MethodWithParametersKey key = null;
            if (method.isAnnotationPresent(CachedInvocation.class)) {
                final String uniqueName = MethodIdentificationSupport.getMethodUniqueName(method);
                key = new MethodWithParametersKey(uniqueName, args == null ? EMPTY_ARR : args);
                if (this.methodResultCache.containsKey(key)) {
                    return this.returnResultFromCache(key);
                }
            }

            final RemoteObjectInvocation<T> invocation = new RemoteObjectInvocation<T>(method.getReturnType(), parameterTypes,
                                                                                       args == null ? EMPTY_ARR : args,
                                                                                       method.getName(), this.remoteReference);
            try {
                Object resultFromMethod = null;

                final AbstractInvocationResponse<Object> result = this.fromServer.invokeRemoteMethod(invocation);
                if (result instanceof LocalCopyInvocationResponse<?>) {
                    final LocalCopyInvocationResponse<Object> localCopy = (LocalCopyInvocationResponse<Object>)result;
                    resultFromMethod = localCopy.getLocalCopy();
                } else if (result instanceof RemoteReferenceInvocationResponse<?>) {
                    //FIXME here, cache is mandatory if on server the return is the same reference
                    final RemoteReferenceInvocationResponse<Object> remoteReferenceResponse = (RemoteReferenceInvocationResponse<Object>)result;
                    final RemoteReference<Object> methodResponseRemoteReference = remoteReferenceResponse.getRemoteReference();
                    resultFromMethod = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                                              new Class[] {invocation.getReturnType()},
                                                              new RemoteReferenceHandler<Object>(this.fromServer,
                                                                                                 methodResponseRemoteReference));

                } else {
                    throw logAndReturn(new IllegalStateException());
                }
                if (method.isAnnotationPresent(CachedInvocation.class)) {

                    this.storeResultOnCache(key, resultFromMethod);
                }
                return resultFromMethod;
            } catch (final InvocationTargetException e) {
                if (method.isAnnotationPresent(CachedInvocation.class)) {
                    this.storeResultOnCache(key, new ExceptionWrapper(e.getCause()));
                }
                throw e.getCause();
            }

        }

        private Object returnResultFromCache( final MethodWithParametersKey key ) throws Throwable {
            final Object value = this.methodResultCache.get(key);
            if (value == NULL_VALUE) {
                return null;
            }

            if (value instanceof ExceptionWrapper) {
                final ExceptionWrapper ex = (ExceptionWrapper)value;
                throw ex.getThrowable();
            }

            return value;
        }

        private void storeResultOnCache( final MethodWithParametersKey key,
                                         final Object resultFromMethod ) {

            if (resultFromMethod == null) {
                this.methodResultCache.put(key, NULL_VALUE);
            } else {
                this.methodResultCache.put(key, resultFromMethod);
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
     * @param remoteObjectType the remote object type
     * @param parameters the parameters
     * @return the T
     * @throws InvalidReferenceTypeException the invalid reference type exception
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
