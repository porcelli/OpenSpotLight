package org.openspotlight.remote.server;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.remote.internal.RemoteObjectInvocation;
import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;

/**
 * The Class RemoteObjectServer will handle and take care of all object instances.
 */
public class RemoteObjectServerImpl implements RemoteObjectServer {
    //FIXME create GC thread
    /**
     * The Class RemoteReferenceInternalData.
     */
    private static class RemoteReferenceInternalData<T> {

        /** The object. */
        private final T                  object;

        /** The user token. */
        private final RemoteReference<T> remoteReference;

        /** The last date access. */
        private final AtomicLong         lastDateAccess;

        /** The hashcode. */
        private final int                hashcode;

        /**
         * Instantiates a new remote reference internal data.
         * 
         * @param remoteReference the remote reference
         * @param object the object
         */
        public RemoteReferenceInternalData(
                                            final RemoteReference<T> remoteReference, final T object ) {
            super();
            this.remoteReference = remoteReference;
            this.lastDateAccess = new AtomicLong(System.currentTimeMillis());
            this.object = object;
            this.hashcode = hashOf(this.remoteReference, this.lastDateAccess);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( final Object obj ) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof RemoteReferenceInternalData<?>)) {
                return false;
            }
            final RemoteReferenceInternalData<?> that = (RemoteReferenceInternalData<?>)obj;
            return eachEquality(of(this.remoteReference, this.lastDateAccess), andOf(that.remoteReference, that.lastDateAccess));
        }

        /**
         * Gets the last date access.
         * 
         * @return the last date access
         */
        public AtomicLong getLastDateAccess() {
            return this.lastDateAccess;
        }

        /**
         * Gets the object.
         * 
         * @return the object
         */
        public T getObject() {
            return this.object;
        }

        /**
         * Gets the remote reference.
         * 
         * @return the remote reference
         */
        public RemoteReference<T> getRemoteReference() {
            return this.remoteReference;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.hashcode;
        }

    }

    /**
     * The Class UserTokenInternalData.
     */
    private static class UserTokenInternalData {

        /** The user token. */
        private final UserToken  userToken;

        /** The last date access. */
        private final AtomicLong lastDateAccess;

        /** The hashcode. */
        private final int        hashcode;

        /**
         * Instantiates a new user token internal data.
         * 
         * @param userToken the user token
         */
        public UserTokenInternalData(
                                      final UserToken userToken ) {
            super();
            this.userToken = userToken;
            this.lastDateAccess = new AtomicLong(System.currentTimeMillis());
            this.hashcode = hashOf(this.userToken, this.lastDateAccess);
        }

        @Override
        public boolean equals( final Object obj ) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof UserTokenInternalData)) {
                return false;
            }
            final UserTokenInternalData that = (UserTokenInternalData)obj;
            return eachEquality(of(this.userToken, this.lastDateAccess), andOf(that.userToken, that.lastDateAccess));
        }

        /**
         * Gets the last date access.
         * 
         * @return the last date access
         */
        public AtomicLong getLastDateAccess() {
            return this.lastDateAccess;
        }

        /**
         * Gets the user token.
         * 
         * @return the user token
         */
        public UserToken getUserToken() {
            return this.userToken;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.hashcode;
        }

    }

    /** The user authenticator. */
    private final UserAuthenticator                                       userAuthenticator;

    /** The last user access. */
    private final Map<UserToken, UserTokenInternalData>                   userTokenDataMap         = new ConcurrentHashMap<UserToken, UserTokenInternalData>();

    /** The internal object factory map. */
    private final Map<Class<?>, InternalObjectFactory<?>>                 internalObjectFactoryMap = new HashMap<Class<?>, InternalObjectFactory<?>>();

    /** The remote references. */
    private final Map<RemoteReference<?>, RemoteReferenceInternalData<?>> remoteReferences         = new ConcurrentHashMap<RemoteReference<?>, RemoteReferenceInternalData<?>>();

    /** The timeout in milliseconds. */
    private final long                                                    timeoutInMilliseconds;

    /**
     * Instantiates a new remote object server.
     * 
     * @param userAutenticator the user authenticator
     * @param portToUse the port to use
     * @param timeoutInMinutes the timeout in minutes
     */
    public RemoteObjectServerImpl(
                                   final UserAuthenticator userAutenticator, final Integer portToUse,
                                   final Integer timeoutInMinutes ) {
        try {
            checkNotNull("userAutenticator", userAutenticator);
            checkNotNull("portToUse", portToUse);
            checkCondition("portToUseBiggerThanZero", portToUse.intValue() > 0);
            checkNotNull("timeoutInMinutes", timeoutInMinutes);
            checkCondition("timeoutInMinutesBiggerThanZero", timeoutInMinutes.intValue() > 0);
            this.userAuthenticator = userAutenticator;
            this.timeoutInMilliseconds = timeoutInMinutes.longValue() * 60 * 1000;
            Remote.config(null, portToUse.intValue(), null, 0);
            ItemServer.bind(this, "RemoteObjectServer");
        } catch (final RemoteException e) {
            throw logAndReturnNew(format("Problem starting remote object server inside port {0}", portToUse), e,
                                  ConfigurationException.class);
        }
    }

    /* (non-Javadoc)
     * @see org.openspotlight.remote.server.RemoteObjectServer#createRemoteReference(org.openspotlight.remote.internal.UserToken, java.lang.Class, java.lang.Object[])
     */
    @SuppressWarnings( "unchecked" )
    public <T> RemoteReference<T> createRemoteReference( final UserToken userToken,
                                                         final Class<T> remoteReferenceType,
                                                         final Object... parameters ) throws InvalidReferenceTypeException {
        try {
            checkNotNull("userToken", userToken);

            checkNotNull("remoteReferenceType", remoteReferenceType);
            checkCondition("remoteReferenceTypeIsInterface", remoteReferenceType.isInterface());
            checkCondition("remoteReferenceTypeContainsFactory", this.internalObjectFactoryMap.containsKey(remoteReferenceType));
            checkCondition("isUserTokenValid", this.isUserTokenValid(userToken));

            final InternalObjectFactory<T> internalFactory = (InternalObjectFactory<T>)this.internalObjectFactoryMap.get(remoteReferenceType);

            final T newObject = internalFactory.createNewInstance(parameters);

            final String remoteReferenceId = UUID.randomUUID().toString();

            final RemoteReference<T> reference = new RemoteReference<T>(remoteReferenceType, remoteReferenceId, userToken);
            this.remoteReferences.put(reference, new RemoteReferenceInternalData<T>(reference, newObject));
            this.updateRemoteReference(reference);

            return reference;
        } catch (final Exception e) {
            throw logAndReturnNew(e, InvalidReferenceTypeException.class);
        } finally {
            this.updateUserToken(userToken);

        }
    }

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
                                      final String clientHost ) throws AccessDeniedException {
        checkNotEmpty("user", user);
        checkNotEmpty("password", password);
        checkNotEmpty("clientHost", clientHost);
        final boolean canConnect = this.userAuthenticator.canConnect(user, password, clientHost);
        if (!canConnect) {
            throw logAndReturn(new AccessDeniedException(format("User {0} from host {1} can't connect to this server instance",
                                                                user, clientHost)));
        }

        final UserToken token = new UserToken(user, UUID.randomUUID().toString());
        this.userTokenDataMap.put(token, new UserTokenInternalData(token));

        // FIXME see if we need some limit, for example: if an user connect again, invalidate the last token

        return token;
    }

    @SuppressWarnings( "unchecked" )
    public <T, R> R invokeRemoteMethod( final RemoteObjectInvocation<T, R> invocation )
        throws InternalErrorOnMethodInvocationException, InvocationTargetException {
        checkNotNull("invocation", invocation);
        checkCondition("remoteReferenceValid", this.isRemoteReferenceValid(invocation.getRemoteReference()));
        try {
            final RemoteReferenceInternalData<T> remoteReferenceData = (RemoteReferenceInternalData<T>)this.remoteReferences.get(invocation.getRemoteReference());
            final T object = remoteReferenceData.getObject();
            final Method method = invocation.getRemoteReference().getRemoteType().getMethod(invocation.getMethodName(),
                                                                                            invocation.getParameterTypes());
            final R result = (R)method.invoke(object, invocation.getParameters());
            return result;
        } catch (final InvocationTargetException e) {
            throw logAndReturn(e);
        } catch (final Exception e) {
            throw logAndReturnNew(e, InternalErrorOnMethodInvocationException.class);
        } finally {
            this.updateRemoteReference(invocation.getRemoteReference());
        }
    }

    /**
     * Checks if is remote reference valid.
     * 
     * @param remoteReference the remote reference
     * @return true, if is remote reference valid
     */
    private boolean isRemoteReferenceValid( final RemoteReference<?> remoteReference ) {
        checkNotNull("remoteReference", remoteReference);
        checkCondition("userTokenValid", this.isUserTokenValid(remoteReference.getUserToken()));
        final boolean contains = this.remoteReferences.containsKey(remoteReference);
        if (!contains) {
            return false;
        }
        final long lastDate = this.remoteReferences.get(remoteReference).getLastDateAccess().get();
        final long now = System.currentTimeMillis();
        final long diff = now - lastDate;
        final boolean isValid = diff < this.timeoutInMilliseconds;
        return isValid;
    }

    /**
     * Checks if is user token valid.
     * 
     * @param userToken the user token
     * @return true, if is user token valid
     */
    private boolean isUserTokenValid( final UserToken userToken ) {
        checkNotNull("userToken", userToken);
        final boolean contains = this.userTokenDataMap.containsKey(userToken);
        if (!contains) {
            return false;
        }
        final long lastDate = this.userTokenDataMap.get(userToken).getLastDateAccess().get();
        final long now = System.currentTimeMillis();
        final long diff = now - lastDate;
        final boolean isValid = diff < this.timeoutInMilliseconds;
        return isValid;
    }

    public <T> void registerInternalObjectFactory( final Class<T> objectType,
                                                   final InternalObjectFactory<T> factory ) {
        checkNotNull("objectType", objectType);
        checkNotNull("factory", factory);

        this.internalObjectFactoryMap.put(objectType, factory);

    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        Remote.shutdown();
    }

    /**
     * Update remote reference.
     * 
     * @param remoteReference the remote reference
     */
    private void updateRemoteReference( final RemoteReference<?> remoteReference ) {
        checkNotNull("remoteReference", remoteReference);
        checkCondition("remoteReferenceValid", this.remoteReferences.containsKey(remoteReference));
        this.remoteReferences.get(remoteReference).getLastDateAccess().set(System.currentTimeMillis());
        this.updateUserToken(remoteReference.getUserToken());
    }

    /**
     * Update user token.
     * 
     * @param userToken the user token
     */
    private void updateUserToken( final UserToken userToken ) {
        checkNotNull("userToken", userToken);
        checkCondition("userTokenValid", this.userTokenDataMap.containsKey(userToken));
        this.userTokenDataMap.get(userToken).getLastDateAccess().set(System.currentTimeMillis());
    }

}
