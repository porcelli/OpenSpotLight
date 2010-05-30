/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.remote.server;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Reflection.UnwrappedCollectionTypeFromMethodReturn;
import org.openspotlight.common.util.Reflection.UnwrappedMapTypeFromMethodReturn;
import org.openspotlight.remote.annotation.DisposeMethod;
import org.openspotlight.remote.annotation.UnsupportedRemoteMethod;
import org.openspotlight.remote.internal.RemoteObjectInvocation;
import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;
import org.openspotlight.remote.internal.RemoteReference.ObjectMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RemoteObjectServer will handle and take care of all object instances.
 */
public class RemoteObjectServerImpl implements RemoteObjectServer {

    /**
     * The Class ActivityMonitor.
     */
    private class ActivityMonitor implements Runnable {

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            logger.trace("Starting GC...");
            garbageCollection();

        }

    }

    /**
     * The Class RemoteReferenceInternalData.
     */
    private static class RemoteReferenceInternalData<T> implements TimeoutAble, Comparable<RemoteReferenceInternalData<T>> {

        final CopyOnWriteArraySet<RemoteReferenceInternalData<?>> children = new CopyOnWriteArraySet<RemoteReferenceInternalData<?>>();

        final RemoteReferenceInternalData<?> parentInternalData;

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
         * @param parentInternalData
         */
        public RemoteReferenceInternalData(
                final RemoteReference<T> remoteReference, final T object, RemoteReferenceInternalData<?> parentInternalData) {
            super();
            this.remoteReference = remoteReference;
            this.parentInternalData = parentInternalData;
            this.lastDateAccess = new AtomicLong(System.currentTimeMillis());
            this.object = object;
            this.hashcode = hashOf(this.remoteReference, this.lastDateAccess);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equalsTo(java.lang.Object)
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

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.hashcode;
        }

        public int compareTo(RemoteReferenceInternalData<T> o) {
            int thisSize = this.children.size();
            int anotherSize = o.children.size();
            return thisSize<anotherSize ? -1 : (thisSize==anotherSize ? 0 : 1);
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
            lastDateAccess = new AtomicLong(System.currentTimeMillis());
            hashcode = hashOf(this.userToken, lastDateAccess);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equalsTo(java.lang.Object)
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
            return eachEquality(of(userToken, lastDateAccess), andOf(that.userToken, that.lastDateAccess));
        }

        /**
         * Gets the last date access.
         * 
         * @return the last date access
         */
        public AtomicLong getLastDateAccess() {
            return lastDateAccess;
        }

        /**
         * Gets the user token.
         * 
         * @return the user token
         */
        public UserToken getUserToken() {
            return userToken;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return hashcode;
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
    private final Map<RemoteReference<?>, RemoteReferenceInternalData<?>> activeRemoteReferences = new ConcurrentHashMap<RemoteReference<?>, RemoteReferenceInternalData<?>>();

    /** The timeout in milliseconds. */
    private final long                                                    timeoutInMilliseconds;

    private static RemoteObjectServerImpl                                 defaultReference         = null;

    public synchronized static RemoteObjectServer getDefault( final UserAuthenticator userAuthenticator,
                                                              final Integer portToUse,
                                                              final Long timeoutInMilliseconds ) {
        if (defaultReference == null) {
            defaultReference = new RemoteObjectServerImpl(userAuthenticator, portToUse, timeoutInMilliseconds);
        } else {
            checkCondition("sameUserAutenticator", userAuthenticator.equals(defaultReference.userAuthenticator));
            checkCondition("samePort", portToUse.equals(defaultReference.portToUse));
            checkCondition("sameTimeoutInMilliseconds", timeoutInMilliseconds.equals(defaultReference.timeoutInMilliseconds));
        }

        return defaultReference;

    }

    private final Integer portToUse;

    /**
     * Instantiates a new remote object server impl.
     * 
     * @param userAuthenticator the user autenticator
     * @param portToUse the port to use
     * @param timeoutInMilliseconds the timeout in milliseconds
     */
    private RemoteObjectServerImpl(
                                    final UserAuthenticator userAuthenticator, final Integer portToUse,
                                    final Long timeoutInMilliseconds ) {
        try {
            checkNotNull("userAutenticator", userAuthenticator);
            checkNotNull("portToUse", portToUse);
            checkCondition("portToUseBiggerThanZero", portToUse.intValue() > 0);
            checkNotNull("timeoutInMinutes", timeoutInMilliseconds);
            checkCondition("timeoutInMinutesBiggerThanOrEqualToZero", timeoutInMilliseconds.longValue() >= 0L);
            this.userAuthenticator = userAuthenticator;
            this.portToUse = portToUse;
            this.timeoutInMilliseconds = timeoutInMilliseconds;
            Remote.config(null, portToUse.intValue(), null, 0);
            ItemServer.bind(this, "RemoteObjectServer");
            new Thread(new ActivityMonitor(), "OSL-Remote-Server-GC").start();
        } catch (final RemoteException e) {
            throw logAndReturnNew(format("Problem starting remote object server inside port {0}", portToUse), e,
                                  ConfigurationException.class);
        }
    }

    public synchronized void closeAllObjects() {
        for (final Entry<RemoteReference<?>, RemoteReferenceInternalData<?>> e : activeRemoteReferences.entrySet()) {
            try{
                removeDeathEntry(e.getValue());
            }catch(Exception ex){
                Exceptions.catchAndLog("error on closing death object entry",ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.remote.server.RemoteObjectServer#createRemoteReference
     * (org.openspotlight.remote.internal.UserToken, java.lang.Class,
     * java.lang.Object[])
     */
    @SuppressWarnings( "unchecked" )
    public <T> RemoteReference<T> createRemoteReference( final UserToken userToken,
                                                         final Class<T> remoteReferenceType,
                                                         final Object... parameters ) throws InvalidReferenceTypeException {
        try {
            checkNotNull("userToken", userToken);

            checkNotNull("remoteReferenceType", remoteReferenceType);
            checkCondition("remoteReferenceTypeIsInterface", remoteReferenceType.isInterface());
            checkCondition("remoteReferenceTypeContainsFactory", internalObjectFactoryMap.containsKey(remoteReferenceType));
            checkCondition("isUserTokenValid", isUserTokenValid(userToken));

            final InternalObjectFactory<T> internalFactory = (InternalObjectFactory<T>)internalObjectFactoryMap.get(remoteReferenceType);

            final T newObject = internalFactory.createNewInstance(parameters);

            final RemoteReference<T> reference = this.internalCreateRemoteReference(null,userToken, remoteReferenceType, newObject);

            return reference;
        } catch (final Exception e) {
            throw logAndReturnNew(e, InvalidReferenceTypeException.class);
        } finally {
            updateUserToken(userToken);

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
        final boolean canConnect = userAuthenticator.canConnect(user, password, clientHost);
        if (!canConnect) {
            throw logAndReturn(new AccessDeniedException(format("User {0} from host {1} can't connect to this server instance",
                                                                user, clientHost)));
        }

        final UserToken token = new UserToken(user, UUID.randomUUID().toString());
        userTokenDataMap.put(token, new UserTokenInternalData(token));

        // FIXME see if we need some limit, for example: if an user connect
        // again, invalidate the last token

        return token;
    }

    /**
     * Garbage collection.
     * 
     */
    private void garbageCollection() {
        while (true) {
            try {
                Thread.sleep(timeoutInMilliseconds / 2);
            } catch (final InterruptedException e) {

            }
            final long curTimeMillis = System.currentTimeMillis();

            final long acceptableDiff = curTimeMillis - timeoutInMilliseconds;
            if (logger.isTraceEnabled()) {
                logger.trace(format("diff in milliseconds {0} for timeout {1} and currentTimeMillis {2} ", acceptableDiff,
                                    timeoutInMilliseconds, curTimeMillis));
            }

            final Set<UserTokenInternalData> deathUserEntries = new HashSet<UserTokenInternalData>();
            final Set<Entry<UserToken, UserTokenInternalData>> userEntries = userTokenDataMap.entrySet();
            for (final Entry<UserToken, UserTokenInternalData> entry : userEntries) {
                if (entry.getValue().getLastDateAccess().get() < acceptableDiff) {
                    deathUserEntries.add(entry.getValue());
                }
            }

            for (final UserTokenInternalData deathUserEntry : deathUserEntries) {
                logger.info(format("removing user {0} token {1}", deathUserEntry.getUserToken().getUser(),
                                   deathUserEntry.getUserToken().getToken()));

                userTokenDataMap.remove(deathUserEntry.getUserToken());
                final Set<Entry<RemoteReference<?>, RemoteReferenceInternalData<?>>> remoteObjectsEntries = activeRemoteReferences.entrySet();
                for (final Entry<RemoteReference<?>, RemoteReferenceInternalData<?>> entry : remoteObjectsEntries) {
                    final Set<RemoteReferenceInternalData<?>> deathEntries = new HashSet<RemoteReferenceInternalData<?>>();
                    if (entry.getKey().getUserToken().equals(deathUserEntry)) {
                        deathEntries.add(entry.getValue());
                    }
                    for (final RemoteReferenceInternalData<?> deathEntry : deathEntries) {
                        removeDeathEntry(deathEntry);
                    }
                }
            }

            final Set<RemoteReferenceInternalData<?>> deathEntries = new HashSet<RemoteReferenceInternalData<?>>();
            final Set<Entry<RemoteReference<?>, RemoteReferenceInternalData<?>>> remoteObjectsEntries = activeRemoteReferences.entrySet();
            for (final Entry<RemoteReference<?>, RemoteReferenceInternalData<?>> entry : remoteObjectsEntries) {
                if (entry.getValue().getLastDateAccess().get() < acceptableDiff) {
                    deathEntries.add(entry.getValue());
                }
            }

            for (final RemoteReferenceInternalData<?> deathEntry : deathEntries) {
                removeDeathEntry(deathEntry);
            }
            if (closed.get()) {

                logger.trace("Stopping GC...");
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
    @SuppressWarnings( "unchecked" )
    private <T> RemoteReference<T> internalCreateRemoteReference( RemoteReferenceInternalData<?> parentInternalData,
                                                                  final UserToken userToken,
                                                                  final Class<T> remoteReferenceType,
                                                                  final T newObject ) {
        if (newObject == null) {
            return null;
        }
        RemoteReference<T> reference;
        if (newObject != null) {
            for (final Entry<RemoteReference<?>, RemoteReferenceInternalData<?>> entry : activeRemoteReferences.entrySet()) {
                if (newObject == entry.getValue().getObject()) {
                    if (userToken.equals(entry.getKey().getUserToken())) {
                        reference = (RemoteReference<T>)entry.getKey();
                        return reference;
                    }
                }
            }
        }

        final String remoteReferenceId = UUID.randomUUID().toString();

        reference = new RemoteReference<T>(remoteReferenceType, newObject.getClass().getInterfaces(), remoteReferenceId,
                                           userToken);
        RemoteReferenceInternalData<T> internalData = new RemoteReferenceInternalData<T>(reference, newObject, parentInternalData);
        if(parentInternalData!=null)
            parentInternalData.children.add(internalData);
        activeRemoteReferences.put(reference, internalData);
        updateRemoteReferenceIfNecessary(reference);
        return reference;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.remote.server.RemoteObjectServer#invokeRemoteMethod
     * (org.openspotlight.remote.internal.RemoteObjectInvocation)
     */
    @SuppressWarnings( "unchecked" )
    public <T, R> AbstractInvocationResponse<R> invokeRemoteMethod( final RemoteObjectInvocation<T> invocation )
        throws InternalErrorOnMethodInvocationException, InvocationTargetException, RemoteReferenceInvalid, UserTokenInvalid {
        checkNotNull("invocation", invocation);
        checkCondition("remoteReferenceValid:" + invocation.getMethodName(),
                       isRemoteReferenceValid(invocation.getRemoteReference()));

        try {
            final RemoteReferenceInternalData<T> remoteReferenceData = (RemoteReferenceInternalData<T>) activeRemoteReferences.get(invocation.getRemoteReference());
            final T object = remoteReferenceData.getObject();
            Method method = null;

            for (final Class<?> iface : invocation.getRemoteReference().getInterfaces()) {
                if (iface.equals(ObjectMethods.class)) {
                    continue;
                }
                try {
                    method = iface.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    break;
                } catch (final NoSuchMethodException e) {

                }
            }
            if (method == null) {
                try {
                    method = Object.class.getMethod(invocation.getMethodName(), invocation.getParameterTypes());

                } catch (final NoSuchMethodException e) {

                }
            }
            checkCondition("methodNotNull:" + invocation.getMethodName(), method != null);
            if (method.isAnnotationPresent(UnsupportedRemoteMethod.class)) {
                throw new UnsupportedOperationException();
            }
            final Object[] args = invocation.getParameters();
            for (int i = 0, size = args.length; i < size; i++) {
                if (args[i] instanceof RemoteReference<?>) {
                    final RemoteReference<?> ref = (RemoteReference<?>)args[i];
                    args[i] = activeRemoteReferences.get(ref).getObject();
                } else if (args[i] instanceof Collection<?>) {
                    final Collection<Object> collection = (Collection<Object>)args[i];
                    final Iterator<?> it = collection.iterator();
                    Object o = null;
                    while (o == null) {
                        o = it.next();
                    }
                    if (o instanceof RemoteReference<?>) {
                        // here it needs to use the correct reference instead of
                        // using the remote one
                        final Collection<Object> newCollection = SLCollections.createNewCollection(collection.getClass(),
                                                                                                   collection.size());
                        for (final Object item : collection) {
                            if (item instanceof RemoteReference<?>) {
                                final RemoteReference<Object> remoteRef = (RemoteReference<Object>)item;
                                final Object correctInstance = activeRemoteReferences.get(remoteRef).getObject();
                                newCollection.add(correctInstance);
                            } else {
                                newCollection.add(item);
                            }
                        }
                        args[i] = newCollection;
                    }
                } else if (args[i] instanceof Map<?, ?>) {
                    final Map<Object, Object> map = (Map<Object, Object>)args[i];
                    final Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
                    Object o = null;
                    while (o == null) {
                        o = it.next().getValue();
                    }
                    if (o instanceof RemoteReference<?>) {
                        // here it needs to use the correct reference instead of
                        // using the remote one
                        final Map<Object, Object> newMap = new HashMap<Object, Object>();
                        for (final Entry<Object, Object> entry : map.entrySet()) {
                            if (entry.getValue() instanceof RemoteReference<?>) {
                                final RemoteReference<Object> remoteRef = (RemoteReference<Object>)entry.getValue();
                                final Object correctInstance = activeRemoteReferences.get(remoteRef).getObject();
                                newMap.put(entry.getKey(), correctInstance);
                            } else {
                                newMap.put(entry.getKey(), entry.getValue());
                            }
                        }
                        args[i] = newMap;
                    }
                }
            }

            final R result = (R)method.invoke(object, invocation.getParameters());
            if (method.isAnnotationPresent(DisposeMethod.class)) {
                removeDeathEntry(remoteReferenceData);
            }

            if (isRemote(method)) {
                if (result instanceof Collection) {

                    final AbstractInvocationResponse<R> response = (AbstractInvocationResponse<R>)this.wrapResultIntoCollection(remoteReferenceData,
                                                                                                                                method,
                                                                                                                                invocation.getUserToken(),
                                                                                                                                (Collection<?>)result);
                    return response;
                }
                if (result instanceof Map) {
                    final AbstractInvocationResponse<R> response = (AbstractInvocationResponse<R>)this.wrapResultIntoMap(remoteReferenceData,
                                                                                                                         method,
                                                                                                                         invocation.getUserToken(),
                                                                                                                         (Map<?, ?>)result);

                    return response;
                }
                final RemoteReference<R> remoteReference = this.internalCreateRemoteReference(remoteReferenceData,
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

            updateRemoteReferenceIfNecessary(invocation.getRemoteReference());
        }
    }

    /**
     * Checks if is remote.
     * 
     * @param method the method
     * @return true, if is remote
     * @throws Exception the exception
     */
    private boolean isRemote( final Method method ) throws Exception {
        final Class<?> returnType = method.getReturnType();
        if (Collection.class.isAssignableFrom(returnType)) {
            final UnwrappedCollectionTypeFromMethodReturn<Object> metadata = Reflection.unwrapCollectionFromMethodReturn(method);
            return isTypeRemote(metadata.getItemType());
        } else if (Map.class.isAssignableFrom(returnType)) {
            final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection.unwrapMapFromMethodReturn(method);
            return isTypeRemote(metadata.getItemType().getK2());
        }
        return isTypeRemote(returnType);
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
        checkCondition("userTokenValid", isUserTokenValid(remoteReference.getUserToken()));
        final boolean contains = activeRemoteReferences.containsKey(remoteReference);
        if (!contains) {
            throw logAndReturn(new RemoteReferenceInvalid(remoteReference.getRemoteType()
                                                          + remoteReference.getRemoteReferenceId()
                                                          + " is invalid. Try to get this object again."));
        }
        return true;
    }

    private boolean isTypeRemote( final Class<?> returnType ) {
        if (returnType == null) {
            return true;
        }

        if (returnType.isPrimitive()) {
            return false;
        }
        if (Serializable.class.isAssignableFrom(returnType)) {
            return false;
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
        final boolean contains = userTokenDataMap.containsKey(userToken);
        if (!contains) {
            throw logAndReturn(new UserTokenInvalid(userToken.getUser() + " is invalid. Try to connect again."));
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.openspotlight.remote.server.RemoteObjectServer#
     * registerInternalObjectFactory(java.lang.Class,
     * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory)
     */
    public <T> void registerInternalObjectFactory( final Class<T> objectType,
                                                   final InternalObjectFactory<T> factory ) {
        checkNotNull("objectType", objectType);
        checkNotNull("factory", factory);

        internalObjectFactoryMap.put(objectType, factory);

    }

    /**
     * Removes the death entry.
     * 
     * @param deathEntry the death entry
     */
    private void removeDeathEntry( final RemoteReferenceInternalData<?> deathEntry ) {
        if(deathEntry==null) return;
        for(RemoteReferenceInternalData<?>  toRemoveBefore: deathEntry.children){
            removeDeathEntry(toRemoveBefore);
        }

        activeRemoteReferences.remove(deathEntry.getRemoteReference());
        final Method[] methods = deathEntry.getObject().getClass().getMethods();
        for (final Method m : methods) {
            if (m.isAnnotationPresent(DisposeMethod.class) && m.getParameterTypes().length == 0) {
                final DisposeMethod disposeAnnotation = m.getAnnotation(DisposeMethod.class);
                if (disposeAnnotation.callOnTimeout()) {
                    try {
                        m.invoke(deathEntry.getObject());
                        return;
                    } catch (final Exception e) {
                        catchAndLog(e);
                    }
                }
            }
        }
    }

    /**
     * Shutdown.
     */
    public synchronized void shutdown() {
        Remote.shutdown();

        closed.set(true);
        for (final Entry<Class<?>, InternalObjectFactory<?>> entry : internalObjectFactoryMap.entrySet()) {
            entry.getValue().shutdown();
        }

        closeAllObjects();
    }

    /**
     * Update remote reference.
     * 
     * @param remoteReference the remote reference
     */
    private void updateRemoteReferenceIfNecessary( final RemoteReference<?> remoteReference ) {
        checkNotNull("remoteReference", remoteReference);
        if (activeRemoteReferences.containsKey(remoteReference)) {
            activeRemoteReferences.get(remoteReference).getLastDateAccess().set(System.currentTimeMillis());
            updateUserToken(remoteReference.getUserToken());
        }
    }

    /**
     * Update user token.
     * 
     * @param userToken the user token
     */
    private void updateUserToken( final UserToken userToken ) {
        checkNotNull("userToken", userToken);
        checkCondition("userTokenValid", userTokenDataMap.containsKey(userToken));
        userTokenDataMap.get(userToken).getLastDateAccess().set(System.currentTimeMillis());
    }

    @SuppressWarnings( "unchecked" )
    public <W, R extends Collection<W>> AbstractInvocationResponse<R> wrapResultIntoCollection( RemoteReferenceInternalData<?> parentInternalData,
                                                                                                final Method method,
                                                                                                final UserToken userToken,
                                                                                                final R collection )
        throws Exception {
        final UnwrappedCollectionTypeFromMethodReturn<Object> metadata = Reflection.unwrapCollectionFromMethodReturn(method);
        final Class<? extends Iterable<?>> collectionType = metadata.getCollectionType();
        final Class<W> remoteReferenceType = (Class<W>)metadata.getItemType();
        final Collection<RemoteReference<W>> remoteReferencesCollection = SLCollections.createNewCollection(collectionType,0);

        for (final W o : collection) {
            final RemoteReference<W> remoteRef = this.internalCreateRemoteReference(parentInternalData,userToken, remoteReferenceType, o);
            remoteReferencesCollection.add(remoteRef);
        }

        final CollectionOfRemoteInvocationResponse<W, R> wrapped = new CollectionOfRemoteInvocationResponse<W, R>(
                                                                                                                  (Class<R>)collectionType,
                                                                                                                  remoteReferencesCollection);
        return wrapped;
    }

    @SuppressWarnings( "unchecked" )
    public <K, W, R extends Map<K, W>> AbstractInvocationResponse<R> wrapResultIntoMap( RemoteReferenceInternalData<?> parentInternalData,
                                                                                        final Method method,
                                                                                        final UserToken userToken,
                                                                                        final R map ) throws Exception {
        final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection.unwrapMapFromMethodReturn(method);
        final Class<W> remoteReferenceType = (Class<W>)metadata.getItemType().getK2();
        final Map<K, RemoteReference<W>> remoteReferencesMap = new HashMap<K, RemoteReference<W>>(map.size());
        for (final Entry<K, W> o : map.entrySet()) {
            final RemoteReference<W> remoteRef = this.internalCreateRemoteReference(parentInternalData,userToken, remoteReferenceType, o.getValue());
            remoteReferencesMap.put(o.getKey(), remoteRef);
        }

        final MapOfRemoteInvocationResponse<K, W, R> wrapped = new MapOfRemoteInvocationResponse<K, W, R>(remoteReferencesMap);
        return wrapped;
    }
}
