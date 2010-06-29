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

import org.jboss.identity.idm.common.exception.PolicyValidationException;
import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectType;
import org.openspotlight.persist.annotation.IndexedProperty;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SLIdentityObject implements IdentityObject, SimpleNodeType, Serializable {

    private static final long     serialVersionUID = -4651245099086963026L;

    private String                typeAsString;

    private Set<SLAttributeEntry> attributes       = new HashSet<SLAttributeEntry>();

    private String                id;

    private SLIdentityObjectType  typedIdentityType;

    private String                name;

    public Set<SLAttributeEntry> getAttributes() {
        return this.attributes;
    }

    @TransientProperty
    public Set<SLTransientIdentityObjectAttribute> getAttributesAsIdentityAttributes() {
        final HashSet<SLTransientIdentityObjectAttribute> result = new HashSet<SLTransientIdentityObjectAttribute>();
        for (final SLAttributeEntry entry : this.attributes) {
            result.add(entry.asIdentityAttribute());
        }
        return result;
    }

    @KeyProperty
    public String getId() {
        return this.id;
    }

    @TransientProperty
    public IdentityObjectType getIdentityType() {
        return this.typedIdentityType;
    }

    @KeyProperty
    public String getName() {
        return this.name;
    }

    @IndexedProperty
    public String getTypeAsString() {
        return this.typeAsString;
    }

    public SLIdentityObjectType getTypedIdentityType() {
        return this.typedIdentityType;
    }

    public void setAttributes( final Set<SLAttributeEntry> attributes ) {
        this.attributes = attributes;
    }

    public void setId( final String id ) {
        this.id = id;
    }

    public void setIdentityType( final IdentityObjectType identityType ) {
        this.typedIdentityType = (SLIdentityObjectType)identityType;
        this.typeAsString = identityType == null ? null : identityType.getName();
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setTypeAsString( final String typeAsString ) {
        this.typeAsString = typeAsString;
    }

    public void setTypedIdentityType( final SLIdentityObjectType typedIdentityType ) {
        this.typedIdentityType = typedIdentityType;
    }

    public void validatePolicy() throws PolicyValidationException {
        // TODO Auto-generated method stub

    }
}
