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
package org.openspotlight.security.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.identity.idm.api.Credential;
import org.jboss.identity.idm.api.CredentialType;
import org.jboss.identity.idm.impl.api.BinaryCredential;
import org.jboss.identity.idm.impl.api.PasswordCredential;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectCredential;
import org.jboss.identity.idm.spi.model.IdentityObjectCredentialType;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

public class SLPasswordEntry implements SimpleNodeType, Serializable {

    private static final long serialVersionUID = -989160074137053633L;

    @SuppressWarnings( "unchecked" )
    public static SLPasswordEntry create( final IdentityObject identityObject,
                                          final IdentityObjectCredential credential ) {
        final SLPasswordEntry entry = new SLPasswordEntry();
        entry.setCredentialClass((Class<? extends Credential>)credential.getClass());
        final IdentityObjectCredentialType type = credential.getType();
        if (type != null) {
            entry.setCredentialTypeClass((Class<? extends CredentialType>)type.getClass());
            entry.setCredentialTypeName(type.getName());
        }
        entry.setUserId(identityObject.getId());
        entry.setUserName(identityObject.getName());
        if (credential instanceof PasswordCredential) {
            final PasswordCredential passwordCredential = (PasswordCredential)credential;
            entry.setPasswordValue(passwordCredential.getValue());
        } else if (credential instanceof BinaryCredential) {
            final BinaryCredential binaryCredential = (BinaryCredential)credential;
            final byte[] rawValue = binaryCredential.getValue() != null ? binaryCredential.getValue() : new byte[0];
            final List<Byte> autoboxed = new ArrayList<Byte>();
            for (final byte b : rawValue) {
                autoboxed.add(b);
            }
            entry.setAutoboxedBinaryValue(autoboxed);
        } else {
            throw Exceptions.logAndReturn(new IllegalArgumentException("invalid credential type"));
        }
        return entry;
    }

    private String                          credentialTypeName;

    private Class<? extends CredentialType> credentialTypeClass;

    private Class<? extends Credential>     credentialClass;

    private String                          userId;

    private String                          userName;

    private List<Byte>                      autoboxedBinaryValue = new ArrayList<Byte>();

    private String                          passwordValue;

    public Credential asCredential() {
        if (PasswordCredential.class.equals(this.credentialClass)) {
            final PasswordCredential credential = new PasswordCredential(this.passwordValue);
            return credential;
        } else if (BinaryCredential.class.equals(this.credentialClass)) {

            final List<Byte> autoboxed = this.getAutoboxedBinaryValue();
            final byte[] raw = new byte[autoboxed == null ? 0 : autoboxed.size()];
            for (int i = 0, size = raw.length; i < size; i++) {
                raw[i] = autoboxed.get(i);
            }

            final BinaryCredential credential = new BinaryCredential(raw);
            return credential;
        } else {
            throw Exceptions.logAndReturn(new IllegalArgumentException("invalid credential type"));
        }
    }

    public List<Byte> getAutoboxedBinaryValue() {
        return this.autoboxedBinaryValue;
    }

    public Class<? extends Credential> getCredentialClass() {
        return this.credentialClass;
    }

    public Class<? extends CredentialType> getCredentialTypeClass() {
        return this.credentialTypeClass;
    }

    public String getCredentialTypeName() {
        return this.credentialTypeName;
    }

    public String getPasswordValue() {
        return this.passwordValue;
    }

    @KeyProperty
    public String getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    @TransientProperty
    public boolean isValid( final IdentityObject identityObject,
                            final IdentityObjectCredential credential ) {
        if (this.credentialClass.isInstance(credential)) {
            if (identityObject.getId().equals(this.userId)) {

                if (PasswordCredential.class.equals(this.credentialClass)) {
                    final PasswordCredential thisCredential = (PasswordCredential)this.asCredential();
                    final PasswordCredential thatCredential = (PasswordCredential)credential;
                    return thisCredential.getEncodedValue().equals(thatCredential.getEncodedValue());
                }
                if (BinaryCredential.class.equals(this.credentialClass)) {
                    final BinaryCredential thisCredential = (BinaryCredential)this.asCredential();
                    final BinaryCredential thatCredential = (BinaryCredential)credential;
                    return Arrays.equals(thisCredential.getValue(), thatCredential.getValue());
                }
            }
        }
        return false;

    }

    public void setAutoboxedBinaryValue( final List<Byte> autoboxedBinaryValue ) {
        this.autoboxedBinaryValue = autoboxedBinaryValue;
    }

    public void setCredentialClass( final Class<? extends Credential> credentialClass ) {
        this.credentialClass = credentialClass;
    }

    public void setCredentialTypeClass( final Class<? extends CredentialType> credentialTypeClass ) {
        this.credentialTypeClass = credentialTypeClass;
    }

    public void setCredentialTypeName( final String credentialTypeName ) {
        this.credentialTypeName = credentialTypeName;
    }

    public void setPasswordValue( final String passwordValue ) {
        this.passwordValue = passwordValue;
    }

    public void setUserId( final String userId ) {
        this.userId = userId;
    }

    public void setUserName( final String userName ) {
        this.userName = userName;
    }

}
