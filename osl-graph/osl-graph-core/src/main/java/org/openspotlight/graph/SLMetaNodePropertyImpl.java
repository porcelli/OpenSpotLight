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

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaNodePropertyImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaNodePropertyImpl implements SLMetaNodeProperty {

    private final Lock                               lock;

    /** The metadata. */
    private final SLMetadata                         metadata;

    /** The meta node. */
    private final SLMetaNodeType                     metaNode;

    /** The p property. */
    private final SLPersistentProperty<Serializable> pProperty;

    private VisibilityLevel                          visibility = null;

    /**
     * Instantiates a new sL meta node property impl.
     * 
     * @param metadata the metadata
     * @param metaNode the meta node
     * @param pProperty the property
     */
    SLMetaNodePropertyImpl(
                            final SLMetadata metadata, final SLMetaNodeType metaNode,
                            final SLPersistentProperty<Serializable> pProperty ) {
        this.metadata = metadata;
        this.metaNode = metaNode;
        this.pProperty = pProperty;
        lock = pProperty.getLockObject();
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals( final Object obj ) {
        synchronized (lock) {
            if (!(obj instanceof SLMetaNodePropertyImpl)) {
                return false;
            }
            final SLMetaNodePropertyImpl metaProperty = (SLMetaNodePropertyImpl)obj;
            return pProperty.equals(metaProperty.pProperty);
        }

    }

    /**
     * {@inheritDoc}
     */
    public Lock getLockObject() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    public SLMetadata getMetadata() {
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaNodeType getMetaNode() {
        return metaNode;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        synchronized (lock) {
            try {
                return SLCommonSupport.toSimplePropertyName(pProperty.getName());
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta node property name.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Class<? extends Serializable> getType() {
        synchronized (lock) {
            try {
                return (Class<? extends Serializable>)Class.forName((String)pProperty.getValue());
            } catch (final Exception e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta node property type.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public VisibilityLevel getVisibility() {
        try {
            if (visibility == null) {
                final String propName = SLCommonSupport.toInternalPropertyName(pProperty.getName() + "."
                                                                               + SLConsts.PROPERTY_NAME_VISIBILITY);

                SLPersistentProperty<String> visibilityProperty = metaNode.getNode().getProperty(String.class, propName);
                visibility = visibilityProperty == null ? VisibilityLevel.PUBLIC : VisibilityLevel.valueOf(visibilityProperty.getValue());
            }
            return visibility;
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve meta property visibility.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return pProperty.hashCode();
    }
}
