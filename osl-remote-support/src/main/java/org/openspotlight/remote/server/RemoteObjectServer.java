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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.openspotlight.remote.internal.RemoteObjectInvocation;
import org.openspotlight.remote.internal.RemoteReference;
import org.openspotlight.remote.internal.UserToken;

/**
 * The Interface RemoteObjectServer.
 */
public interface RemoteObjectServer {

    /**
     * The Class AbstractInvocationResponse.
     * 
     * @param <R>
     */
    public static abstract class AbstractInvocationResponse<R> implements Serializable {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -8373059894096363951L;
        //
    }

    /**
     * The Class SetOfRemoteInvocationResponse.
     */
    public static final class CollectionOfRemoteInvocationResponse<W, R extends Collection<W>>
        extends AbstractInvocationResponse<R> {

        /**
         * 
         */
        private static final long                    serialVersionUID = -5319417381072030066L;
        /** The result. */
        private final Collection<RemoteReference<W>> result;

        private final Class<R>                       resultType;

        /**
         * Instantiates a new sets the of remote invocation response.
         * 
         * @param result the result
         */
        public CollectionOfRemoteInvocationResponse(
                                                     final Class<R> resultType, final Collection<RemoteReference<W>> result ) {
            this.result = result;
            this.resultType = resultType;
        }

        /**
         * Gets the result.
         * 
         * @return the result
         */
        public Collection<RemoteReference<W>> getResult() {
            return this.result;
        }

        public Class<R> getResultType() {
            return resultType;
        }
    }

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
        public T createNewInstance( Object... parameters ) throws Exception;

        /**
         * Gets the target object type.
         * 
         * @return the target object type
         */
        public Class<T> getTargetObjectType();

        public void shutdown();

    }

    /**
     * The Class LocalCopyInvocationResponse.
     */
    public static final class LocalCopyInvocationResponse<R> extends AbstractInvocationResponse<R> {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 45524732045499074L;

        /** The local copy. */
        private final R           localCopy;

        /**
         * Instantiates a new local copy invocation response.
         * 
         * @param localCopy the local copy
         */
        public LocalCopyInvocationResponse(
                                            final R localCopy ) {
            super();
            this.localCopy = localCopy;
        }

        /**
         * Gets the local copy.
         * 
         * @return the local copy
         */
        public R getLocalCopy() {
            return this.localCopy;
        }

    }

    /**
     * The Class MapOfRemoteInvocationResponse.
     */
    public static final class MapOfRemoteInvocationResponse<K, W, R extends Map<K, W>> extends AbstractInvocationResponse<R> {

        private static final long                serialVersionUID = -5728186157743872011L;

        /** The result. */
        private final Map<K, RemoteReference<W>> result;

        /**
         * Instantiates a new map of remote invocation response.
         * 
         * @param result the result
         */
        public MapOfRemoteInvocationResponse(
                                              final Map<K, RemoteReference<W>> result ) {
            this.result = result;
        }

        /**
         * Gets the result.
         * 
         * @return the result
         */
        public Map<K, RemoteReference<W>> getResult() {
            return this.result;
        }
    }

    /**
     * The Class RemoteReferenceInvocationResponse.
     */
    public static final class RemoteReferenceInvocationResponse<R> extends AbstractInvocationResponse<R> {

        /** The Constant serialVersionUID. */
        private static final long        serialVersionUID = 1539869691497856873L;

        /** The remote reference. */
        private final RemoteReference<R> remoteReference;

        /**
         * Instantiates a new remote reference invocation response.
         * 
         * @param remoteReference the remote reference
         */
        public RemoteReferenceInvocationResponse(
                                                  final RemoteReference<R> remoteReference ) {
            this.remoteReference = remoteReference;
        }

        /**
         * Gets the remote reference.
         * 
         * @return the remote reference
         */
        public RemoteReference<R> getRemoteReference() {
            return this.remoteReference;
        }
    }

    public void closeAllObjects();

    /**
     * Creates the remote reference.
     * 
     * @param userToken the user token
     * @param remoteReferenceType the remote reference type
     * @param parameters the parameters
     * @return the remote reference< t>
     * @throws InvalidReferenceTypeException the invalid reference type exception
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
     * @param invocation the invocation
     * @return the returned object
     * @throws InternalErrorOnMethodInvocationException the internal error on method invocation exception
     * @throws InvocationTargetException the invocation target exception
     * @throws RemoteReferenceInvalid the remote reference invalid
     * @throws UserTokenInvalid the user token invalid
     */
    public <T, R> AbstractInvocationResponse<R> invokeRemoteMethod( RemoteObjectInvocation<T> invocation )
        throws InternalErrorOnMethodInvocationException, InvocationTargetException, RemoteReferenceInvalid, UserTokenInvalid;

    /**
     * Register internal object factory.
     * 
     * @param objectType the object type
     * @param factory the factory
     */
    public <T> void registerInternalObjectFactory( Class<T> objectType,
                                                   InternalObjectFactory<T> factory );

    /**
     * Shutdown. This method should be called <b>only one time during the VM life cycle</b>. This is necessary due some static
     * garbage on RMI.
     */
    public void shutdown();
}
