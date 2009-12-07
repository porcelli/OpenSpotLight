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
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

/**
 * The Class UserToken is necessary for doing remote invocations. This class should not be used directly, since it is stored
 * Internally by the proxy objects.
 */
public class UserToken implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7576539340354513998L;

    /** The user. */
    private final String      user;

    /** The token. */
    private final String      token;

    /** The hashcode. */
    private final int         hashcode;

    /**
     * Instantiates a new user token.
     * 
     * @param user the user
     * @param token the token
     */
    public UserToken(
                      final String user, final String token ) {
        checkNotEmpty("user", user);
        checkNotEmpty("token", token);
        this.user = user;
        this.token = token;
        this.hashcode = hashOf(user, token);
    }

    @Override
    public boolean equals( final Object obj ) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UserToken)) {
            return false;
        }
        final UserToken that = (UserToken)obj;
        return eachEquality(of(this.user, this.token), andOf(that.user, that.token));
    }

    /**
     * Gets the token.
     * 
     * @return the token
     */
    public String getToken() {
        return this.token;
    }

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public String getUser() {
        return this.user;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

    @Override
    public String toString() {
        return "UserToken: " + this.user;
    }

}
