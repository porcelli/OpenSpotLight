/**
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

package org.openspotlight.remote.internal;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.remote.server.RemoteObjectServer;

/**
 * The Class RemoteReference will be used to locate the remote reference on actions like sending messages to the
 * {@link RemoteObjectServer} asking for some method invocation.
 * 
 * @param <T>
 */
public class RemoteReference<T> implements Serializable {

    /**
     * The Interface ObjectMethods contains commons methods used on Object class. This is necessary for the proxy implementation.
     */
    public interface ObjectMethods {

        /**
         * Equals.
         * 
         * @param o the o
         * @return true, if successful
         */
        @Override
        public boolean equals(Object o);

        /**
         * Hash code.
         * 
         * @return the int
         */
        @Override
        public int hashCode();

        /**
         * To string.
         * 
         * @return the string
         */
        @Override
        public String toString();
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1738168629624338103L;

    /** The remote type. */
    private final Class<T>    remoteType;

    /** The interfaces. */
    private final Class<?>[]  interfaces;

    /** The remote reference id. */
    private final String      remoteReferenceId;

    /** The user token. */
    private final UserToken   userToken;

    /** The hashcode. */
    private final int         hashcode;

    /**
     * Instantiates a new remote reference.
     * 
     * @param remoteType the remote type
     * @param remoteReferenceId the remote reference id
     * @param userToken the user token
     * @param interfaces the interfaces
     */
    @SuppressWarnings("unchecked")
    public RemoteReference(
                            final Class<T> remoteType, final Class<?>[] interfaces, final String remoteReferenceId,
                            final UserToken userToken) {
        checkNotEmpty("remoteReferenceId", remoteReferenceId);
        checkNotNull("userToken", userToken);
        this.remoteType = remoteType;
        this.remoteReferenceId = remoteReferenceId;
        this.userToken = userToken;
        this.interfaces = Arrays.unionOf(interfaces, ObjectMethods.class, remoteType);
        this.hashcode = hashOf(this.remoteType, this.remoteReferenceId, this.userToken);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) { return true; }
        if (!(obj instanceof RemoteReference<?>)) { return false; }
        final RemoteReference<?> that = (RemoteReference<?>) obj;
        return eachEquality(of(this.remoteType, this.remoteReferenceId, this.userToken), andOf(that.remoteType,
                                                                                               that.remoteReferenceId,
                                                                                               that.userToken));
    }

    /**
     * Gets the interfaces.
     * 
     * @return the interfaces
     */
    public Class<?>[] getInterfaces() {
        return this.interfaces;
    }

    /**
     * Gets the remote reference id.
     * 
     * @return the remote reference id
     */
    public String getRemoteReferenceId() {
        return this.remoteReferenceId;
    }

    /**
     * Gets the remote type.
     * 
     * @return the remote type
     */
    public Class<T> getRemoteType() {
        return this.remoteType;
    }

    /**
     * Gets the user token.
     * 
     * @return the user token
     */
    public UserToken getUserToken() {
        return this.userToken;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.hashcode;
    }
}
