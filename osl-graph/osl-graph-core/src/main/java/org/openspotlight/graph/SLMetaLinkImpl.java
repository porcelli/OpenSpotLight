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

import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.meta.SLMetaLink;
import org.openspotlight.graph.meta.SLMetaLinkProperty;
import org.openspotlight.graph.meta.SLMetaLinkType;
import org.openspotlight.graph.meta.SLMetadata;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * The Class SLMetaLinkImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaLinkImpl implements SLMetaLink {

    private final Lock                          lock;

    /** The meta link node. */
    private final SLPersistentNode              metaLinkNode;

    /** The meta link type. */
    private final SLMetaLinkType                metaLinkType;

    /** The source type. */
    private final Class<? extends SLNode>       sourceType;

    /** The target type. */
    private final Class<? extends SLNode>       targetType;

    /** The side types. */
    private final List<Class<? extends SLNode>> sideTypes;

    /** The bidirectional. */
    private final boolean                       bidirectional;

    /**
     * Instantiates a new sL meta link impl.
     * 
     * @param metaLinkNode the meta link node
     * @param metaLinkType the meta link type
     * @param sourceType the source type
     * @param targetType the target type
     * @param sideTypes the side types
     * @param bidirectional the bidirectional
     */
    SLMetaLinkImpl(
                    final SLPersistentNode metaLinkNode, final SLMetaLinkType metaLinkType,
                    final Class<? extends SLNode> sourceType, final Class<? extends SLNode> targetType,
                    final List<Class<? extends SLNode>> sideTypes, final boolean bidirectional, final LockContainer parent ) {
        this.metaLinkNode = metaLinkNode;
        this.metaLinkType = metaLinkType;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.sideTypes = sideTypes;
        this.bidirectional = bidirectional;
        lock = parent.getLockObject();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        synchronized (lock) {
            try {
                final String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
                final SLPersistentProperty<String> prop = SLCommonSupport.getProperty(metaLinkNode, String.class, propName);
                return prop == null ? null : prop.getValue();
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta link description.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public VisibilityLevel getVisibility() {
        synchronized (lock) {
            try {
                final String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_VISIBILITY);
                final SLPersistentProperty<String> prop = SLCommonSupport.getProperty(metaLinkNode, String.class, propName);
                return prop == null ? null : VisibilityLevel.valueOf(prop.getValue());
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta link visibility.", e);
            }
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
        return metaLinkType.getMetadata();
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaLinkType getMetaLinkType() {
        return metaLinkType;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLMetaLinkProperty> getMetaProperties() {
        synchronized (lock) {
            try {
                final Collection<SLMetaLinkProperty> metaProperties = new HashSet<SLMetaLinkProperty>();
                final Collection<SLPersistentProperty<Serializable>> pProperties = metaLinkNode.getProperties(SLConsts.PROPERTY_PREFIX_USER.concat(".*"));
                for (final SLPersistentProperty<Serializable> pProperty : pProperties) {
                    final SLMetaLinkProperty metaProperty = new SLMetaLinkPropertyImpl(this, pProperty);
                    metaProperties.add(metaProperty);
                }
                return metaProperties;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta link properties.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaLinkProperty getMetaProperty( final String name ) {
        synchronized (lock) {
            try {
                final String propName = SLCommonSupport.toUserPropertyName(name);
                SLPersistentProperty<Serializable> pProperty = null;
                try {
                    pProperty = metaLinkNode.getProperty(Serializable.class, propName);
                } catch (final SLPersistentPropertyNotFoundException e) {
                }
                SLMetaLinkProperty metaProperty = null;
                if (pProperty != null) {
                    metaProperty = new SLMetaLinkPropertyImpl(this, pProperty);
                }
                return metaProperty;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta link property.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Class<? extends SLNode>> getSideTypes() {
        return sideTypes;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends SLNode> getSourceType() {
        if (bidirectional) {
            // this method cannot be used on bidirecional meta links, because
            // source and targets types are relatives.
            // on unidirecional links, source and target types are well defined.
            throw new UnsupportedOperationException("SLMetaLink.getSource() cannot be used on bidirecional meta links.");
        }
        return sourceType;
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends SLNode> getTargetType() {
        if (bidirectional) {
            // this method cannot be used on bidirecional meta links, because
            // source and targets types are relatives.
            // on unidirecional links, source and target types are well defined.
            throw new UnsupportedOperationException("SLMetaLink.getTarget() cannot be used on bidirecional meta links.");
        }
        return targetType;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBidirectional() {
        return bidirectional;
    }

    /**
     * {@inheritDoc}
     */
    public SLPersistentNode getNode() {
        return metaLinkNode;
    }
}
