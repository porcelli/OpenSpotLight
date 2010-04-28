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

import org.jboss.identity.idm.spi.model.IdentityObject;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationship;
import org.jboss.identity.idm.spi.model.IdentityObjectRelationshipType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

public class SLIdentityObjectRelationship implements SimpleNodeType, Serializable, IdentityObjectRelationship {

    private static final long                serialVersionUID = 4264443925216883621L;

    private String                           typeAsString;

    private String                           fromIdentityObjectId;

    private String                           toIdentityObjectId;

    private IdentityObject                   fromIdentityObject;

    private IdentityObject                   toIdentityObject;

    private String                           name;

    private SLIdentityObjectRelationshipType typedRelationshipType;

    @TransientProperty
    public IdentityObject getFromIdentityObject() {
        return this.fromIdentityObject;
    }

    @KeyProperty
    public String getFromIdentityObjectId() {
        return this.fromIdentityObjectId;
    }

    @KeyProperty
    public String getName() {
        return this.name;
    }

    @TransientProperty
    public IdentityObject getToIdentityObject() {
        return this.toIdentityObject;
    }

    @KeyProperty
    public String getToIdentityObjectId() {
        return this.toIdentityObjectId;
    }

    @TransientProperty
    public IdentityObjectRelationshipType getType() {
        return this.typedRelationshipType;
    }

    @KeyProperty
    public String getTypeAsString() {
        return this.typeAsString;
    }

    public SLIdentityObjectRelationshipType getTypedRelationshipType() {
        return this.typedRelationshipType;
    }

    public void setFromIdentityObject( final IdentityObject fromIdentityObject ) {
        this.fromIdentityObject = fromIdentityObject;
        if (fromIdentityObject != null) {
            this.fromIdentityObjectId = fromIdentityObject.getId();
        }
    }

    public void setFromIdentityObjectId( final String fromIdentityObjectId ) {
        this.fromIdentityObjectId = fromIdentityObjectId;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setToIdentityObject( final IdentityObject toIdentityObject ) {
        this.toIdentityObject = toIdentityObject;
        if (toIdentityObject != null) {
            this.toIdentityObjectId = toIdentityObject.getId();
        }
    }

    public void setToIdentityObjectId( final String toIdentityObjectId ) {
        this.toIdentityObjectId = toIdentityObjectId;
    }

    public void setType( final IdentityObjectRelationshipType type ) {
        this.typedRelationshipType = (SLIdentityObjectRelationshipType)type;
        this.typeAsString = type == null ? null : type.getName();
    }

    public void setTypeAsString( final String typeAsString ) {
        this.typeAsString = typeAsString;
    }

    public void setTypedRelationshipType( final SLIdentityObjectRelationshipType typedRelationshipType ) {
        this.typedRelationshipType = typedRelationshipType;
        this.typeAsString = typedRelationshipType == null ? null : typedRelationshipType.getName();
    }

}
