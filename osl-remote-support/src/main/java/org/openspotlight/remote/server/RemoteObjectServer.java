package org.openspotlight.remote.server;

import java.lang.reflect.InvocationTargetException;

import org.openspotlight.remote.internal.RemoteObjectInvocation;
import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;

/**
 * The Interface RemoteObjectServer.
 */
public interface RemoteObjectServer {

    /**
     * A factory for creating any kind of object on server. If there's a factory, it will call its
     * {@link #createNewInstance(Object...)} method. If don't, it will call the constructor with its parameters.
     * 
     * @param <T>
     */
    public static interface InternalObjectFactory<T> {

        /**
         * Creates a new InternalObject object.
         * 
         * @param parameters the parameters
         * @return the T
         */
        public T createNewInstance( Object... parameters );

        /**
         * Gets the target object type.
         * 
         * @return the target object type
         */
        public Class<T> getTargetObjectType();

    }

    /**
     * Creates the remote reference.
     * 
     * @param <T>
     * @param userToken
     * @param remoteReferenceType the remote reference type
     * @param parameters the parameters
     * @return the remote reference< t>
     * @throws InvalidReferenceTypeException
     */
    public <T> RemoteReference<T> createRemoteReference( UserToken userToken,
                                                         Class<T> remoteReferenceType,
                                                         Object... parameters ) throws InvalidReferenceTypeException;

    /**
     * Creates the user token.
     * 
     * @param user the user
     * @param password the password
     * @param clientHost the client host
     * @return the user token
     * @throws AccessDeniedException the access denied exception
     */
    public UserToken createUserToken( final String user,
                                      final String password,
                                      final String clientHost ) throws AccessDeniedException;

    /**
     * Invoke remote method.
     * 
     * @param <T>
     * @param <R>
     * @param invocation the invocation
     * @return the returned object
     * @throws InternalErrorOnMethodInvocationException
     * @throws InvocationTargetException
     */
    public <T, R> R invokeRemoteMethod( RemoteObjectInvocation<T, R> invocation )
        throws InternalErrorOnMethodInvocationException, InvocationTargetException;

    /**
     * Register internal object factory.
     * 
     * @param <T>
     * @param objectType the object type
     * @param factory the factory
     */
    public <T> void registerInternalObjectFactory( Class<T> objectType,
                                                   InternalObjectFactory<T> factory );

    /**
     * Shutdown.
     */
    public void shutdown();
}
