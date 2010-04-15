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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLMetadata.BooleanOperator;
import org.openspotlight.graph.SLMetadata.LogicOperator;
import org.openspotlight.graph.SLMetadata.MetaNodeTypeProperty;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.exception.SLRenderHintNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaNodeTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaNodeTypeImpl implements SLMetaNodeType {

    /** The lock. */
    private final Lock             lock;

    /** The metadata. */
    private final SLMetadata       metadata;

    /** The p meta node. */
    private final SLPersistentNode pMetaNode;

    /**
     * Instantiates a new sL meta node type impl.
     * 
     * @param metadata the metadata
     * @param pNode the node
     */
    SLMetaNodeTypeImpl(
                        final SLMetadata metadata, final SLPersistentNode pNode ) {
        this.metadata = metadata;
        pMetaNode = pNode;
        lock = metadata.getLockObject();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        synchronized (lock) {
            try {
                final String propName = SLCommonSupport
                                                       .toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
                final SLPersistentProperty<String> prop = SLCommonSupport
                                                                         .getProperty(pMetaNode, String.class, propName);
                return prop == null ? null : prop.getValue();
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta node description.",
                                                  e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public VisibilityLevel getVisibility() {
        synchronized (lock) {
            try {
                final String propName = SLCommonSupport
                                                       .toInternalPropertyName(SLConsts.PROPERTY_NAME_VISIBILITY);
                final SLPersistentProperty<String> prop = SLCommonSupport
                                                                         .getProperty(pMetaNode, String.class, propName);
                return prop == null ? null : VisibilityLevel.valueOf(prop.getValue());
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta node visibility.",
                                                  e);
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
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public SLPersistentNode getNode() {
        return pMetaNode;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLMetaNodeProperty> getMetaProperties() {
        synchronized (lock) {
            try {
                final Collection<SLMetaNodeProperty> metaProperties = new HashSet<SLMetaNodeProperty>();
                final Collection<SLPersistentProperty<Serializable>> pProperties = pMetaNode
                                                                                            .getProperties(SLConsts.PROPERTY_PREFIX_USER
                                                                                                                                        .concat(".*"));
                for (final SLPersistentProperty<Serializable> pProperty : pProperties) {
                    final SLMetaNodeProperty metaProperty = new SLMetaNodePropertyImpl(
                                                                                       metadata, this, pProperty);
                    metaProperties.add(metaProperty);
                }
                return metaProperties;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta node properties.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaNodeProperty getMetaProperty( final String name ) {
        synchronized (lock) {

            try {
                final String propName = SLCommonSupport
                                                       .toUserPropertyName(name);
                SLPersistentProperty<Serializable> pProperty = null;
                try {
                    pProperty = pMetaNode.getProperty(Serializable.class,
                                                      propName);
                } catch (final SLPersistentPropertyNotFoundException e) {
                }
                SLMetaNodeProperty metaProperty = null;
                if (pProperty != null) {
                    metaProperty = new SLMetaNodePropertyImpl(metadata, this,
                                                              pProperty);
                }
                return metaProperty;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta node property.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaRenderHint getMetaRenderHint( final String name ) throws SLRenderHintNotFoundException {
        synchronized (lock) {
            try {
                SLMetaRenderHint renderHint = null;
                final String pattern = SLCommonSupport
                                                      .toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT
                                                                              + "." + name);
                final SLPersistentProperty<Serializable> pProperty = SLCommonSupport
                                                                                    .getProperty(pMetaNode, Serializable.class, pattern);
                if (pProperty != null) {
                    renderHint = new SLMetaRenderHintImpl(this, pProperty);
                } else {
                    throw new SLRenderHintNotFoundException(name);
                }
                return renderHint;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta render hint.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLMetaRenderHint> getMetaRenderHints() {
        synchronized (lock) {

            try {
                final Collection<SLMetaRenderHint> renderHints = new ArrayList<SLMetaRenderHint>();
                final String pattern = SLCommonSupport
                                                      .toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT)
                                       + ".*";
                final Set<SLPersistentProperty<Serializable>> pProperties = pMetaNode
                                                                                     .getProperties(pattern);
                for (final SLPersistentProperty<Serializable> pProperty : pProperties) {
                    final SLMetaRenderHint renderHint = new SLMetaRenderHintImpl(
                                                                                 this, pProperty);
                    renderHints.add(renderHint);
                }
                return renderHints;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta render hints.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaNodeType getParent() {
        synchronized (lock) {

            try {
                SLMetaNodeType parentMetaNodeType = null;
                final SLPersistentTreeSession treeSession = pMetaNode
                                                                     .getSession();
                final SLPersistentNode pMetaTypesNode = SLCommonSupport
                                                                       .getMetaTypesNode(treeSession);
                final SLPersistentNode pParentNode = pMetaNode.getParent();
                if (!pParentNode.equals(pMetaTypesNode)) {
                    parentMetaNodeType = new SLMetaNodeTypeImpl(metadata,
                                                                pParentNode);
                }
                return parentMetaNodeType;
            } catch (final SLException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta node type parent.",
                                                  e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaNodeType getSubMetaNodeType(
                                              final Class<? extends SLNode> nodeClass ) {
        synchronized (lock) {
            return getSubMetaNodeType(nodeClass.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaNodeType getSubMetaNodeType( final String name ) {
        synchronized (lock) {
            try {
                SLMetaNodeType metaNode = null;
                final SLPersistentNode pChildMetaNode = pMetaNode.getNode(name);
                if (pChildMetaNode != null) {
                    metaNode = new SLMetaNodeTypeImpl(metadata, pChildMetaNode);
                }
                return metaNode;
            } catch (final SLException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta node.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLMetaNodeType> getSubMetaNodeTypes() {
        synchronized (lock) {
            try {
                final Collection<SLMetaNodeType> subMetaNodeTypes = new ArrayList<SLMetaNodeType>();
                final Collection<SLPersistentNode> pMetaNodes = pMetaNode
                                                                         .getNodes();
                for (final SLPersistentNode pMetaNode : pMetaNodes) {
                    final SLMetaNodeType metaNode = new SLMetaNodeTypeImpl(
                                                                           metadata, pMetaNode);
                    subMetaNodeTypes.add(metaNode);
                }
                return subMetaNodeTypes;
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve meta nodes.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Collection<SLMetaNodeType> searchSubMetaNodeTypes( SLRecursiveMode recursiveMode,
                                                              VisibilityLevel visibility,
                                                              MetaNodeTypeProperty property2Find,
                                                              LogicOperator logicOp,
                                                              BooleanOperator booleanOp,
                                                              List<String> values ) {
        synchronized (lock) {
            try {
                final String statement = SLMetadataXPathSupporter.buildXpathForMetaNodeType("/" + pMetaNode.getPath(), recursiveMode, visibility, property2Find, logicOp, booleanOp, values);

                final SLPersistentQuery query = pMetaNode.getSession().createQuery(
                                                                                   statement, SLPersistentQuery.TYPE_XPATH);
                final SLPersistentQueryResult result = query.execute();
                final Collection<SLMetaNodeType> metaNodes = new ArrayList<SLMetaNodeType>();
                final Collection<SLPersistentNode> pNodes = result.getNodes();
                for (final SLPersistentNode pNode : pNodes) {
                    final SLMetaNodeType metaNode = new SLMetaNodeTypeImpl(
                                                                           metadata, pNode);
                    metaNodes.add(metaNode);
                }
                return metaNodes;

            } catch (final SLException e) {
                throw new SLGraphSessionException(
                                                  "Error on attempt to retrieve node metadata.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Class<? extends SLNode> getType() {
        try {
            return (Class<? extends SLNode>)Class.forName(pMetaNode.getName());
        } catch (final Exception e) {
            throw new SLGraphSessionException(
                                              "Error on attempt to retrieve node type.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getTypeName() {
        try {
            return pMetaNode.getName();
        } catch (final Exception e) {
            throw new SLGraphSessionException(
                                              "Error on attempt to retrieve node type name.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( final Object obj ) {
        synchronized (lock) {
            if (!(obj instanceof SLMetaNodeTypeImpl)) {
                return false;
            }
            final SLMetaNodeTypeImpl metaNode = (SLMetaNodeTypeImpl)obj;
            return pMetaNode.equals(metaNode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return pMetaNode.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        synchronized (lock) {
            return getType().toString();
        }
    }
}
