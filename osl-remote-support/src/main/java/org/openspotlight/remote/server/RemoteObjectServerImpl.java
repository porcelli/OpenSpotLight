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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.remote.annotation.DisposeMethod;
import org.openspotlight.remote.annotation.ReturnsRemoteReference;
import org.openspotlight.remote.annotation.UnsupportedRemoteMethod;
import org.openspotlight.remote.internal.RemoteObjectInvocation;
import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class RemoteObjectServer will handle and take care of all object instances.
 */
public class RemoteObjectServerImpl implements RemoteObjectServer {

    /**
     * The Class ActivityMonitor.
     */
    private class ActivityMonitor implements Runnable {

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            RemoteObjectServerImpl.this.logger.trace("Starting GC...");
            RemoteObjectServerImpl.this.garbageCollection();

        }

    }

    /**
     * The Class RemoteReferenceInternalData.
     */
    private static class RemoteReferenceInternalData<T> implements TimeoutAble {

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
     * The Interface TimeoutAble.
     */
    private interface TimeoutAble {

        /**
         * Gets the last date access.
         * 
         * @return the last date access
         */
        public AtomicLong getLastDateAccess();
    }

    /**
     * The Class UserTokenInternalData.
     */
    private static class UserTokenInternalData implements TimeoutAble {

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

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
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

    private final Logger                                                  logger                   = LoggerFactory.getLogger(this.getClass());

    /** The closed. */
    private final AtomicBoolean                                           closed                   = new AtomicBoolean(false);

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
     * Instantiates a new remote object server impl.
     * 
     * @param userAutenticator the user autenticator
     * @param portToUse the port to use
     * @param timeoutInMilliseconds the timeout in milliseconds
     */
    public RemoteObjectServerImpl(
                                   final UserAuthenticator userAutenticator, final Integer portToUse,
                                   final Integer timeoutInMilliseconds ) {
        try {
            checkNotNull("userAutenticator", userAutenticator);
            checkNotNull("portToUse", portToUse);
            checkCondition("portToUseBiggerThanZero", portToUse.intValue() > 0);
            checkNotNull("timeoutInMinutes", timeoutInMilliseconds);
            checkCondition("timeoutInMinutesBiggerThanOrEqualToZero", timeoutInMilliseconds.intValue() >= 0);
            this.userAuthenticator = userAutenticator;
            this.timeoutInMilliseconds = timeoutInMilliseconds;
            Remote.config(null, portToUse.intValue(), null, 0);
            ItemServer.bind(this, "RemoteObjectServer");
            new Thread(new ActivityMonitor(), "OSL-Remote-Server-GC").start();
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

            final RemoteReference<T> reference = this.internalCreateRemoteReference(userToken, remoteReferenceType, newObject);

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

    /**
     * Garbage collection.
     * 
     * @param acceptableDiff the acceptable diff
     */
    private void garbageCollection() {
        while (true) {
            try {
                Thread.sleep(this.timeoutInMilliseconds / 2);
            } catch (final InterruptedException e) {

            }
            final long curTimeMillis = System.currentTimeMillis();

            final long acceptableDiff = curTimeMillis - this.timeoutInMilliseconds;
            if (RemoteObjectServerImpl.this.logger.isTraceEnabled()) {
                RemoteObjectServerImpl.this.logger.trace(format(
                                                                "diff in milliseconds {0} for timeout {1} and currentTimeMillis {2} ",
                                                                acceptableDiff, this.timeoutInMilliseconds, curTimeMillis));
            }

            final Set<UserTokenInternalData> deathUserEntries = new HashSet<UserTokenInternalData>();
            final Set<Entry<UserToken, UserTokenInternalData>> userEntries = this.userTokenDataMap.entrySet();
            for (final Entry<UserToken, UserTokenInternalData> entry : userEntries) {
                if (entry.getValue().getLastDateAccess().get() < acceptableDiff) {
                    deathUserEntries.add(entry.getValue());
                }
            }

            for (final UserTokenInternalData deathUserEntry : deathUserEntries) {
                this.logger.info(format("removing user {0} token {1}", deathUserEntry.getUserToken().getUser(),
                                        deathUserEntry.getUserToken().getToken()));

                this.userTokenDataMap.remove(deathUserEntry.getUserToken());
                final Set<Entry<RemoteReference<?>, RemoteReferenceInternalData<?>>> remoteObjectsEntries = this.remoteReferences.entrySet();
                for (final Entry<RemoteReference<?>, RemoteReferenceInternalData<?>> entry : remoteObjectsEntries) {
                    final Set<RemoteReferenceInternalData<?>> deathEntries = new HashSet<RemoteReferenceInternalData<?>>();
                    if (entry.getKey().getUserToken().equals(deathUserEntry)) {
                        deathEntries.add(entry.getValue());
                    }
                    for (final RemoteReferenceInternalData<?> deathEntry : deathEntries) {
                        this.removeDeathEntry(deathEntry);
                    }
                }
            }

            final Set<RemoteReferenceInternalData<?>> deathEntries = new HashSet<RemoteReferenceInternalData<?>>();
            final Set<Entry<RemoteReference<?>, RemoteReferenceInternalData<?>>> remoteObjectsEntries = this.remoteReferences.entrySet();
            for (final Entry<RemoteReference<?>, RemoteReferenceInternalData<?>> entry : remoteObjectsEntries) {
                if (entry.getValue().getLastDateAccess().get() < acceptableDiff) {
                    deathEntries.add(entry.getValue());
                }
            }

            for (final RemoteReferenceInternalData<?> deathEntry : deathEntries) {
                this.removeDeathEntry(deathEntry);
            }
            if (this.closed.get()) {

                RemoteObjectServerImpl.this.logger.trace("Stopping GC...");
                return;
            }

        }
    }

    /**
     * Internal create remote reference.
     * 
     * @param userToken the user token
     * @param remoteReferenceType the remote reference type
     * @param newObject the new object
     * @return the remote reference< t>
     */
    private <T> RemoteReference<T> internalCreateRemoteReference( final UserToken userToken,
                                                                  final Class<T> remoteReferenceType,
                                                                  final T newObject ) {
        final String remoteReferenceId = UUID.randomUUID().toString();

        final RemoteReference<T> reference = new RemoteReference<T>(remoteReferenceType, remoteReferenceId, userToken);
        this.remoteReferences.put(reference, new RemoteReferenceInternalData<T>(reference, newObject));
        this.updateRemoteReferenceIfNecessary(reference);
        return reference;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.remote.server.RemoteObjectServer#invokeRemoteMethod(org.openspotlight.remote.internal.RemoteObjectInvocation)
     */
    @SuppressWarnings( "unchecked" )
    public <T, R> AbstractInvocationResponse<R> invokeRemoteMethod( final RemoteObjectInvocation<T> invocation )
        throws InternalErrorOnMethodInvocationException, InvocationTargetException, RemoteReferenceInvalid, UserTokenInvalid {
        checkNotNull("invocation", invocation);
        checkCondition("remoteReferenceValid", this.isRemoteReferenceValid(invocation.getRemoteReference()));

        try {
            final RemoteReferenceInternalData<T> remoteReferenceData = (RemoteReferenceInternalData<T>)this.remoteReferences.get(invocation.getRemoteReference());
            final T object = remoteReferenceData.getObject();
            final Method method = invocation.getRemoteReference().getRemoteType().getMethod(invocation.getMethodName(),
                                                                                            invocation.getParameterTypes());
            if (method.isAnnotationPresent(UnsupportedRemoteMethod.class)) {
                throw new UnsupportedOperationException();
            }
            final R result = (R)method.invoke(object, invocation.getParameters());
            if (method.isAnnotationPresent(DisposeMethod.class)) {
                this.removeDeathEntry(remoteReferenceData);
            }

            if (method.getAnnotation(ReturnsRemoteReference.class) != null) {
                //FIXME here, cache is mandatory if on server the return is the same reference
                final RemoteReference<R> remoteReference = this.internalCreateRemoteReference(
                                                                                              invocation.getUserToken(),
                                                                                              (Class<R>)invocation.getReturnType(),
                                                                                              result);
                return new RemoteReferenceInvocationResponse(remoteReference);
            }
            return new LocalCopyInvocationResponse(result);
        } catch (final UnsupportedOperationException e) {
            throw logAndReturn(e);
        } catch (final InvocationTargetException e) {
            throw logAndReturn(e);
        } catch (final Exception e) {
            throw logAndReturnNew(e, InternalErrorOnMethodInvocationException.class);
        } finally {

            this.updateRemoteReferenceIfNecessary(invocation.getRemoteReference());
        }
    }

    /**
     * Checks if is remote reference valid.
     * 
     * @param remoteReference the remote reference
     * @return true, if is remote reference valid
     */
    private boolean isRemoteReferenceValid( final RemoteReference<?> remoteReference )
        throws RemoteReferenceInvalid, UserTokenInvalid {
        checkNotNull("remoteReference", remoteReference);
        checkCondition("userTokenValid", this.isUserTokenValid(remoteReference.getUserToken()));
        final boolean contains = this.remoteReferences.containsKey(remoteReference);
        if (!contains) {
            throw logAndReturn(new RemoteReferenceInvalid(remoteReference.getRemoteType()
                                                          + remoteReference.getRemoteReferenceId()
                                                          + " is invalid. Try to get this object again."));
        }
        return true;
    }

    /**
     * Checks if is user token valid.
     * 
     * @param userToken the user token
     * @return true, if is user token valid
     */
    private boolean isUserTokenValid( final UserToken userToken ) throws UserTokenInvalid {
        checkNotNull("userToken", userToken);
        final boolean contains = this.userTokenDataMap.containsKey(userToken);
        if (!contains) {
            throw logAndReturn(new UserTokenInvalid(userToken.getUser() + " is invalid. Try to connect again."));
        }
        return true;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.remote.server.RemoteObjectServer#registerInternalObjectFactory(java.lang.Class, org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory)
     */
    public <T> void registerInternalObjectFactory( final Class<T> objectType,
                                                   final InternalObjectFactory<T> factory ) {
        checkNotNull("objectType", objectType);
        checkNotNull("factory", factory);

        this.internalObjectFactoryMap.put(objectType, factory);

    }

    /**
     * Removes the death entry.
     * 
     * @param deathEntry the death entry
     */
    private void removeDeathEntry( final RemoteReferenceInternalData<?> deathEntry ) {
        this.logger.info(format("removing reference {0} id {1}", deathEntry.getObject(),
                                deathEntry.getRemoteReference().getRemoteReferenceId()));

        RemoteObjectServerImpl.this.remoteReferences.remove(deathEntry.getRemoteReference());
        final Method[] methods = deathEntry.getObject().getClass().getMethods();
        for (final Method m : methods) {
            if (m.isAnnotationPresent(DisposeMethod.class) && m.getParameterTypes().length == 0) {
                try {
                    m.invoke(deathEntry.getObject());
                    return;
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        this.closed.set(true);
        Remote.shutdown();
    }

    /**
     * Update remote reference.
     * 
     * @param remoteReference the remote reference
     */
    private void updateRemoteReferenceIfNecessary( final RemoteReference<?> remoteReference ) {
        checkNotNull("remoteReference", remoteReference);
        if (this.remoteReferences.containsKey(remoteReference)) {
            this.remoteReferences.get(remoteReference).getLastDateAccess().set(System.currentTimeMillis());
            this.updateUserToken(remoteReference.getUserToken());
        }
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
