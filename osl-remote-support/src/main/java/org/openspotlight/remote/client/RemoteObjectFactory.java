package org.openspotlight.remote.client;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import gnu.cajo.utils.extra.TransparentItemProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.util.Collections;
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
import org.openspotlight.remote.server.RemoteObjectServer.CollectionOfRemoteInvocationResponse;
import org.openspotlight.remote.server.RemoteObjectServer.LocalCopyInvocationResponse;
import org.openspotlight.remote.server.RemoteObjectServer.MapOfRemoteInvocationResponse;
import org.openspotlight.remote.server.RemoteObjectServer.RemoteReferenceInvocationResponse;

/**
 * A factory for creating RemoteObject objects.
 */
public class RemoteObjectFactory {

    /**
     * The Class RemoteReferenceHandler.
     */
    private static class RemoteReferenceHandler<T> implements InvocationHandler {

        /**
         * The Class ExceptionWrapper.
         */
        private static class ExceptionWrapper {

            /** The throwable. */
            private final Throwable throwable;

            /**
             * Instantiates a new exception wrapper.
             * 
             * @param throwable the throwable
             */
            public ExceptionWrapper(
                                     final Throwable throwable ) {
                super();
                this.throwable = throwable;
            }

            /**
             * Gets the throwable.
             * 
             * @return the throwable
             */
            public Throwable getThrowable() {
                return this.throwable;
            }

        }

        /** The Constant NULL_VALUE. */
        private static final Object                        NULL_VALUE        = new Object();

        /** The method result cache. */
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
            checkNotNull("fromServer", fromServer);
            checkNotNull("remoteReference", remoteReference);

            this.remoteReference = remoteReference;
            this.fromServer = fromServer;
        }

        public RemoteReference<T> getRemoteReference() {
            return this.remoteReference;
        }

        @SuppressWarnings( "unchecked" )
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

            if (args != null) {
                for (int i = 0, size = args.length; i < size; i++) {
                    if (args[i] != null) {
                        if (Proxy.isProxyClass(args[i].getClass())) {
                            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(args[i]);
                            if (invocationHandler instanceof RemoteReferenceHandler<?>) {
                                final RemoteReferenceHandler<?> handler = (RemoteReferenceHandler<?>)invocationHandler;
                                args[i] = handler.getRemoteReference();
                            }
                        } else if (args[i] instanceof Collection) {
                            final Collection<?> collection = (Collection<?>)args[i];
                            if (collection.size() > 0) {
                                final Iterator<?> it = collection.iterator();
                                Object o = null;
                                while (o == null) {
                                    o = it.next();
                                }
                                if (Proxy.isProxyClass(o.getClass())) {
                                    final InvocationHandler invocationHandlerForTest = Proxy.getInvocationHandler(o);
                                    if (invocationHandlerForTest instanceof RemoteReferenceHandler<?>) {
                                        //here, it *needs* to wrap only the references before sending it to the server
                                        final Collection<Object> newCollection = Collections.createNewCollection(
                                                                                                                 collection.getClass(),
                                                                                                                 collection.size());
                                        for (final Object item : collection) {
                                            if (item != null) {
                                                final InvocationHandler invocationHandler = Proxy.getInvocationHandler(item);
                                                if (invocationHandler instanceof RemoteReferenceHandler<?>) {
                                                    final RemoteReferenceHandler<?> handler = (RemoteReferenceHandler<?>)invocationHandler;
                                                    final Object newO = handler.getRemoteReference();
                                                    newCollection.add(newO);
                                                } else {
                                                    newCollection.add(item);
                                                }
                                            } else {
                                                newCollection.add(null);
                                            }
                                        }
                                        args[i] = newCollection;
                                    }
                                }

                            }
                        } else if (args[i] instanceof Map) {
                            final Map<Object, Object> map = (Map<Object, Object>)args[i];
                            if (map.size() > 0) {
                                final Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
                                Object o = null;
                                while (o == null) {
                                    o = it.next().getValue();
                                }
                                if (Proxy.isProxyClass(o.getClass())) {
                                    final InvocationHandler invocationHandlerForTest = Proxy.getInvocationHandler(o);
                                    if (invocationHandlerForTest instanceof RemoteReferenceHandler<?>) {
                                        //here, it *needs* to wrap only the references before sending it to the server
                                        final Map<Object, Object> newMap = new HashMap<Object, Object>();
                                        for (final Entry<Object, Object> item : map.entrySet()) {
                                            if (item.getValue() != null) {
                                                final InvocationHandler invocationHandler = Proxy.getInvocationHandler(item.getValue());
                                                if (invocationHandler instanceof RemoteReferenceHandler<?>) {
                                                    final RemoteReferenceHandler<?> handler = (RemoteReferenceHandler<?>)invocationHandler;
                                                    final Object newO = handler.getRemoteReference();
                                                    newMap.put(item.getKey(), newO);
                                                } else {
                                                    newMap.put(item.getKey(), item.getValue());
                                                }
                                            } else {
                                                newMap.put(item.getKey(), null);
                                            }
                                        }
                                        args[i] = newMap;
                                    }
                                }

                            }

                        }
                    }
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
                    final RemoteReferenceInvocationResponse<Object> remoteReferenceResponse = (RemoteReferenceInvocationResponse<Object>)result;
                    final RemoteReference<Object> methodResponseRemoteReference = remoteReferenceResponse.getRemoteReference();
                    if (methodResponseRemoteReference == null) {
                        resultFromMethod = null;
                    } else {
                        resultFromMethod = Proxy.newProxyInstance(
                                                                  this.getClass().getClassLoader(),
                                                                  methodResponseRemoteReference.getInterfaces(),
                                                                  new RemoteReferenceHandler<Object>(this.fromServer,
                                                                                                     methodResponseRemoteReference));
                    }

                } else if (result instanceof CollectionOfRemoteInvocationResponse) {

                    final CollectionOfRemoteInvocationResponse resultCollection = (CollectionOfRemoteInvocationResponse)result;
                    final Collection<Object> remoteResultCollection = (Collection<Object>)Collections.createNewCollection(
                                                                                                                          resultCollection.getResultType(),

                                                                                                                          resultCollection.getResult().size());

                    final Collection<RemoteReference<Object>> colection = resultCollection.getResult();
                    for (final RemoteReference<Object> remoteRef : colection) {
                        final Object proxyInstance = Proxy.newProxyInstance(
                                                                            this.getClass().getClassLoader(),
                                                                            remoteRef.getInterfaces(),
                                                                            new RemoteReferenceHandler<Object>(
                                                                                                               this.fromServer,
                                                                                                               (RemoteReference<Object>)remoteRef));

                        remoteResultCollection.add(proxyInstance);
                    }
                    resultFromMethod = remoteResultCollection;
                } else if (result instanceof MapOfRemoteInvocationResponse) {
                    final MapOfRemoteInvocationResponse resultMap = (MapOfRemoteInvocationResponse)result;
                    final Map<Object, Object> remoteResultMap = new HashMap<Object, Object>();
                    final Set<Entry<Object, Object>> entrySet = resultMap.getResult().entrySet();
                    for (final Entry<Object, Object> remoteRef : entrySet) {
                        final RemoteReference<?> remoteRefValue = (RemoteReference<?>)remoteRef.getValue();
                        final Object proxyInstance = Proxy.newProxyInstance(
                                                                            this.getClass().getClassLoader(),
                                                                            remoteRefValue.getInterfaces(),
                                                                            new RemoteReferenceHandler<Object>(
                                                                                                               this.fromServer,
                                                                                                               (RemoteReference<Object>)remoteRef.getValue()));

                        remoteResultMap.put(remoteRef.getKey(), proxyInstance);
                    }
                    resultFromMethod = remoteResultMap;
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

        /**
         * Return result from cache.
         * 
         * @param key the key
         * @return the object
         * @throws Throwable the throwable
         */
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

        /**
         * Store result on cache.
         * 
         * @param key the key
         * @param resultFromMethod the result from method
         */
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
        final T newObjectProxy = (T)Proxy.newProxyInstance(this.getClass().getClassLoader(), remoteReference.getInterfaces(),
                                                           new RemoteReferenceHandler(this.fromServer, remoteReference));
        return newObjectProxy;
    }

}
