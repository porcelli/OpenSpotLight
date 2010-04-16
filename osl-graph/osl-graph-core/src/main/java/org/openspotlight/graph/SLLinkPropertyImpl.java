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
package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLLinkPropertyImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLLinkPropertyImpl<V extends Serializable> implements SLLinkProperty<V> {

    /** The link. */
    private SLLink                  link;

    /** The persistent property. */
    private SLPersistentProperty<V> persistentProperty;

    /**
     * Instantiates a new sL link property impl.
     * 
     * @param link the link
     * @param persistentProperty the persistent property
     */
    public SLLinkPropertyImpl(
                               SLLink link, SLPersistentProperty<V> persistentProperty ) {
        this.link = link;
        this.persistentProperty = persistentProperty;
    }

    /**
     * {@inheritDoc}
     */
    public SLLink getLink() {
        return link;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        try {
            return SLCommonSupport.toSimplePropertyName(persistentProperty.getName());
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve link property name.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public V getValue() {
        try {
            return persistentProperty.getValue();
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve link property value.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getValueAsString() {
        return getValue().toString();
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        try {
            persistentProperty.remove();
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to remove link property.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setValue( V value ) {
        try {
            persistentProperty.setValue(value);
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to set link property value.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public boolean equals( Object obj ) {
        if (obj == null || !(obj instanceof SLLinkProperty)) return false;
        SLLinkProperty<? extends Serializable> property = (SLLinkProperty<? extends Serializable>)obj;
        String name1 = property.getLink().getID() + ":" + getName();
        String name2 = getLink().getID() + ":" + getName();
        return name1.equals(name2);
    }
}
