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
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.annotation.SLDescription;
import org.openspotlight.graph.annotation.SLRenderHint;
import org.openspotlight.graph.annotation.SLRenderHints;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The listener interface for receiving SLMetadata events. The class that is
 * interested in processing a SLMetadata event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addSLMetadataListener<code> method. When
 * the SLMetadata event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLMetadataEvent
 * @author Vitor Hugo Chagas
 */
public class SLMetadataListener extends SLAbstractGraphSessionEventListener {
    
    private final Set<Class<? extends SLLink>> sessionLinkTypeCache = new CopyOnWriteArraySet<Class<? extends SLLink>>();
    
    private final Set<Class<? extends SLNode>> sessionNodeTypeCache = new CopyOnWriteArraySet<Class<? extends SLNode>>();
    
    /**
     * Adds the description.
     * 
     * @param type
     *            the type
     * @param pNode
     *            the node
     * 
     * @throws SLPersistentTreeSessionException
     *             the SL persistent tree session exception
     */
    private void addDescription(final Class<?> type,
            final SLPersistentNode pNode)
            throws SLPersistentTreeSessionException {
        final SLDescription description = type
                .getAnnotation(SLDescription.class);
        if (description != null) {
            final String propName = SLCommonSupport
                    .toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
            final SLPersistentProperty<String> prop = SLCommonSupport
                    .getProperty(pNode, String.class, propName);
            if (prop == null) {
                pNode.setProperty(String.class, propName, description.value());
            }
        }
    }
    
    /**
     * Adds the link node.
     * 
     * @param pairKeyNode
     *            the pair key node
     * @param direction
     *            the direction
     * 
     * @return the sL persistent node
     * 
     * @throws SLPersistentTreeSessionException
     *             the SL persistent tree session exception
     */
    private SLPersistentNode addLinkNode(final SLPersistentNode pairKeyNode,
            final int direction) throws SLPersistentTreeSessionException {
        final long linkCount = this.incLinkCount(pairKeyNode);
        final String name = SLCommonSupport.getLinkIndexNodeName(linkCount);
        final SLPersistentNode linkNode = pairKeyNode.addNode(name);
        linkNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT,
                linkCount);
        linkNode.setProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION,
                direction);
        return linkNode;
    }
    
    /**
     * Adds the render hints.
     * 
     * @param nodeType
     *            the node type
     * @param pMetaNode
     *            the meta node
     * 
     * @throws SLPersistentTreeSessionException
     *             the SL persistent tree session exception
     */
    private void addRenderHints(final Class<? extends SLNode> nodeType,
            final SLPersistentNode pMetaNode)
            throws SLPersistentTreeSessionException {
        final SLRenderHints renderHints = nodeType
                .getAnnotation(SLRenderHints.class);
        if (renderHints != null) {
            final SLRenderHint[] renderHintArr = renderHints.value();
            for (final SLRenderHint renderHint : renderHintArr) {
                final String propName = SLCommonSupport
                        .toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT
                                + "." + renderHint.name());
                final SLPersistentProperty<String> prop = SLCommonSupport
                        .getProperty(pMetaNode, String.class, propName);
                if (prop == null) {
                    pMetaNode.setProperty(String.class, propName, renderHint
                            .value());
                }
            }
        }
    }
    
    /**
     * Builds the metadata type node path.
     * 
     * @param pNode
     *            the node
     * 
     * @return the string
     * 
     * @throws SLPersistentTreeSessionException
     *             the SL persistent tree session exception
     */
    private String buildMetadataTypeNodePath(SLPersistentNode pNode)
            throws SLPersistentTreeSessionException {
        final String typePropName = SLCommonSupport
                .toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
        final StringBuilder statement = new StringBuilder();
        final List<String> typeNames = new ArrayList<String>();
        do {
            try {
                final SLPersistentProperty<String> typeProp = pNode
                        .getProperty(String.class, typePropName);
                statement.insert(0, typeProp.getValue()).insert(0, '/');
                typeNames.add(0, typeProp.getValue());
            } catch (final SLPersistentPropertyNotFoundException e) {
                break;
            }
        } while ((pNode = pNode.getParent()) != null);
        statement.insert(0, "//osl/metadata/types");
        return statement.toString();
    }
    
    /**
     * Gets the a class.
     * 
     * @param sourceClass
     *            the source class
     * @param targetClass
     *            the target class
     * 
     * @return the a class
     * 
     * @throws SLException
     *             the SL exception
     */
    private Class<?> getAClass(final Class<?> sourceClass,
            final Class<?> targetClass) throws SLException {
        return sourceClass.getName().compareTo(targetClass.getName()) < 0 ? sourceClass
                : targetClass;
    }
    
    /**
     * Gets the b class.
     * 
     * @param sourceClass
     *            the source class
     * @param targetClass
     *            the target class
     * 
     * @return the b class
     * 
     * @throws SLException
     *             the SL exception
     */
    private Class<?> getBClass(final Class<?> sourceClass,
            final Class<?> targetClass) throws SLException {
        return sourceClass.getName().compareTo(targetClass.getName()) < 0 ? targetClass
                : sourceClass;
    }
    
    /**
     * Gets the class pair key node.
     * 
     * @param treeSession
     *            the tree session
     * @param linkClass
     *            the link class
     * @param sourceClass
     *            the source class
     * @param targetClass
     *            the target class
     * 
     * @return the class pair key node
     * 
     * @throws SLException
     *             the SL exception
     */
    private SLPersistentNode getClassPairKeyNode(
            final SLPersistentTreeSession treeSession,
            final Class<? extends SLLink> linkClass,
            final Class<?> sourceClass, final Class<?> targetClass)
            throws SLException {
        
        final Class<?> aClass = this.getAClass(sourceClass, targetClass);
        final Class<?> bClass = this.getBClass(sourceClass, targetClass);
        
        final StringBuilder pairKey = new StringBuilder();
        pairKey.append(aClass.getName()).append('.').append(bClass.getName());
        final SLPersistentNode linkClassNode = SLCommonSupport
                .getMetaLinkClassNode(treeSession, linkClass);
        SLPersistentNode pairKeyNode = linkClassNode
                .getNode(pairKey.toString());
        
        if (pairKeyNode == null) {
            pairKeyNode = linkClassNode.addNode(pairKey.toString());
            pairKeyNode.setProperty(String.class,
                    SLConsts.PROPERTY_NAME_A_CLASS_NAME, aClass.getName());
            pairKeyNode.setProperty(String.class,
                    SLConsts.PROPERTY_NAME_B_CLASS_NAME, bClass.getName());
            pairKeyNode.setProperty(Long.class,
                    SLConsts.PROPERTY_NAME_LINK_COUNT, 0L);
        }
        
        return pairKeyNode;
    }
    
    /**
     * Gets the meta link direction.
     * 
     * @param sourceClass
     *            the source class
     * @param targetClass
     *            the target class
     * @param bidirecional
     *            the bidirecional
     * 
     * @return the meta link direction
     * 
     * @throws SLException
     *             the SL exception
     */
    private int getMetaLinkDirection(final Class<?> sourceClass,
            final Class<?> targetClass, final boolean bidirecional)
            throws SLException {
        if (bidirecional) {
            return SLConsts.DIRECTION_BOTH;
        } else {
            return this.getAClass(sourceClass, targetClass).equals(sourceClass) ? SLConsts.DIRECTION_AB
                    : SLConsts.DIRECTION_BA;
        }
    }
    
    /**
     * Inc link count.
     * 
     * @param linkKeyPairNode
     *            the link key pair node
     * 
     * @return the long
     * 
     * @throws SLPersistentTreeSessionException
     *             the SL persistent tree session exception
     */
    private long incLinkCount(final SLPersistentNode linkKeyPairNode)
            throws SLPersistentTreeSessionException {
        final SLPersistentProperty<Long> linkCountProp = linkKeyPairNode
                .getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT);
        final long linkCount = linkCountProp.getValue() + 1;
        linkCountProp.setValue(linkCount);
        return linkCount;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkAdded
     * (org.openspotlight.graph.SLLinkEvent)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void linkAdded(final SLLinkEvent event)
            throws SLGraphSessionException {
        
        try {
            final SLLink link = event.getLink();
            final Class<? extends SLLink> linkType = link.getClass()
                    .getInterfaces()[0];
            if (this.sessionLinkTypeCache.contains(linkType)) {
                return;
            }
            final SLNode[] sides = link.getSides();
            final SLNode source = sides[0];
            final SLNode target = sides[1];
            
            final SLPersistentNode linkNode = event.getLinkNode();
            final SLPersistentTreeSession treeSession = linkNode.getSession();
            
            final Class<?> sourceClass = source.getClass().getInterfaces()[0];
            final Class<?> targetClass = target.getClass().getInterfaces()[0];
            
            final SLPersistentNode classPairKeyNode = this.getClassPairKeyNode(
                    treeSession, linkType, sourceClass, targetClass);
            final int direction = this.getMetaLinkDirection(sourceClass,
                    targetClass, link.isBidirectional());
            
            final StringBuilder statement = new StringBuilder();
            statement.append(classPairKeyNode.getPath()).append("/*[").append(
                    SLConsts.PROPERTY_NAME_DIRECTION).append("=").append(
                    direction).append(']');
            
            SLPersistentNode metaLinkNode = null;
            final SLPersistentQuery query = treeSession.createQuery(statement
                    .toString(), SLPersistentQuery.TYPE_XPATH);
            final SLPersistentQueryResult result = query.execute();
            if (result.getRowCount() == 0) {
                metaLinkNode = this.addLinkNode(classPairKeyNode, direction);
            }
            
            final String propName = SLCommonSupport
                    .toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
            linkNode.setProperty(String.class, propName, metaLinkNode.getID());
            
            this.addDescription(linkType, metaLinkNode);
            this.sessionLinkTypeCache.add(linkType);
        } catch (final SLException e) {
            throw new SLGraphSessionException(
                    "Error on attempt to add meta link node.", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkPropertySet
     * (org.openspotlight.graph.SLLinkPropertyEvent)
     */
    @Override
    public void linkPropertySet(final SLLinkPropertyEvent event)
            throws SLGraphSessionException {
        
        try {
            
            final SLPersistentProperty<? extends Serializable> pProperty = event
                    .getPersistentProperty();
            
            final SLPersistentNode linkNode = pProperty.getNode();
            final String propName = SLCommonSupport
                    .toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
            SLPersistentProperty<String> metaNodeIDProp = null;
            try {
                metaNodeIDProp = linkNode.getProperty(String.class, propName);
            } catch (final SLPersistentPropertyNotFoundException e) {
            }
            final SLPersistentTreeSession treeSession = linkNode.getSession();
            final SLPersistentNode metaLinkNode = treeSession
                    .getNodeByID(metaNodeIDProp.getValue());
            metaLinkNode.setProperty(String.class, pProperty.getName(),
                    pProperty.getValue().getClass().getName());
            
        } catch (final SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException(
                    "Error on attempt to set meta link property.", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodeAdded
     * (org.openspotlight.graph.SLNodeEvent)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void nodeAdded(final SLNodeEvent event)
            throws SLGraphSessionException {
        try {
            
            final SLPersistentNode pNode = event.getPersistentNode();
            final Class<? extends SLNode> nodeType = event.getNode().getClass()
                    .getInterfaces()[0];
            if (this.sessionNodeTypeCache.contains(nodeType)) {
                return;
            }
            if (nodeType.equals(SLNode.class)) {
                return;
            }
            final String path = this.buildMetadataTypeNodePath(pNode);
            final SLPersistentTreeSession treeSession = pNode.getSession();
            SLPersistentNode pMetaNode = treeSession.getNodeByPath(path);
            if (pMetaNode == null) {
                final String parentPath = this.buildMetadataTypeNodePath(pNode
                        .getParent());
                SLPersistentNode parentTypeNode;
                if (parentPath.equals("//osl/metadata/types")) {
                    parentTypeNode = SLCommonSupport
                            .getMetaTypesNode(treeSession);
                } else {
                    parentTypeNode = treeSession.getNodeByPath(parentPath);
                }
                if (parentTypeNode != null) {
                    pMetaNode = parentTypeNode.addNode(nodeType.getName());
                    // FIXME sometimes the parentTypeNode is null
                    final String idPropName = SLCommonSupport
                            .toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
                    pNode.setProperty(String.class, idPropName, pMetaNode
                            .getID());
                }
                
            }
            if (pMetaNode != null) {
                // FIXME sometimes the parentTypeNode is null
                this.addRenderHints(nodeType, pMetaNode);
                this.addDescription(nodeType, pMetaNode);
            }
            this.sessionNodeTypeCache.add(nodeType);
        } catch (final SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException(
                    "Error on attempt to add node metadata.", e);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodePropertySet
     * (org.openspotlight.graph.SLNodePropertyEvent)
     */
    @Override
    public void nodePropertySet(final SLNodePropertyEvent event)
            throws SLGraphSessionException {
        try {
            final SLPersistentProperty<? extends Serializable> pProperty = event
                    .getPersistentProperty();
            
            final SLPersistentNode pNode = pProperty.getNode();
            SLPersistentProperty<String> metaNodeIDProp = null;
            try {
                final String propName = SLCommonSupport
                        .toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
                metaNodeIDProp = pNode.getProperty(String.class, propName);
            } catch (final SLPersistentPropertyNotFoundException e) {
            }
            if (metaNodeIDProp != null) {
                final SLPersistentTreeSession session = pNode.getSession();
                final SLPersistentNode metaNode = session
                        .getNodeByID(metaNodeIDProp.getValue());
                final String propertyName = pProperty.getName();
                metaNode.setProperty(String.class, propertyName, pProperty
                        .getValue().getClass().getName());
            }
        } catch (final SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException(
                    "Error on attempt to set meta node property.", e);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void sessionCleaned() {
        this.sessionLinkTypeCache.clear();
        this.sessionNodeTypeCache.clear();
    }
}
