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
package org.openspotlight.remote.internal;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

/**
 * The Class RemoteObjectInvocation is used to transfer data to invoke objects on the server.
 * 
 * @param <T>
 */
public class RemoteObjectInvocation<T> implements Serializable {

    /**
     * 
     */
    private static final long        serialVersionUID = -4821171830502625526L;

    /** The user token. */
    private final UserToken          userToken;

    /** The return type. */
    private final Class<?>           returnType;

    /** The parameter types. */
    private final Class<?>[]         parameterTypes;

    /** The parameters. */
    private final Object[]           parameters;

    /** The method name. */
    private final String             methodName;

    /** The remote reference. */
    private final RemoteReference<T> remoteReference;

    /** The hashcode. */
    private final int                hashcode;

    /**
     * Instantiates a new remote object invocation.
     * 
     * @param userToken the user token
     * @param returnType the return type
     * @param parameterTypes the parameter types
     * @param parameters the parameters
     * @param methodName the method name
     * @param remoteReference the remote reference
     */
    public RemoteObjectInvocation(
                                   final Class<?> returnType, final Class<?>[] parameterTypes, final Object[] parameters,
                                   final String methodName, final RemoteReference<T> remoteReference ) {
        checkNotNull("returnType", returnType);
        checkNotNull("parameterTypes", parameterTypes);
        checkNotNull("parameters", parameters);
        checkNotEmpty("methodName", methodName);
        checkNotNull("remoteReference", remoteReference);
        checkCondition("correctNumberOfArguments", parameters.length == parameterTypes.length);

        this.userToken = remoteReference.getUserToken();
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.methodName = methodName;
        this.remoteReference = remoteReference;
        this.hashcode = hashOf(this.userToken, this.returnType, this.parameterTypes, this.methodName, this.remoteReference,
                               this.parameters);
    }

    @Override
    public boolean equals( final Object obj ) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RemoteObjectInvocation<?>)) {
            return false;
        }
        final RemoteObjectInvocation<?> that = (RemoteObjectInvocation<?>)obj;
        return eachEquality(of(this.userToken, this.returnType, this.parameterTypes, this.methodName, this.remoteReference,
                               this.parameters), andOf(that.userToken, that.returnType, that.parameterTypes, that.methodName,
                                                       that.remoteReference, that.parameters));
    }

    public String getMethodName() {
        return this.methodName;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public RemoteReference<T> getRemoteReference() {
        return this.remoteReference;
    }

    public final Class<?> getReturnType() {
        return this.returnType;
    }

    public UserToken getUserToken() {
        return this.userToken;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

}
