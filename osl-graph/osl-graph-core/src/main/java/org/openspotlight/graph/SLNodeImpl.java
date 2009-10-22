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

import static org.openspotlight.graph.SLCommonSupport.getInternalPropertyAsString;
import static org.openspotlight.graph.SLCommonSupport.setInternalStringProperty;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.ProxyUtil;

/**
 * The Class SLNodeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLNodeImpl implements SLNode, SLPNodeGetter {

    /** The context. */
    private SLContext                 context;

    /** The parent. */
    private SLNode                    parent;

    /** The p node. */
    private SLPersistentNode          pNode;

    /** The event poster. */
    private SLGraphSessionEventPoster eventPoster;

    /**
     * Instantiates a new sL node impl.
     * 
     * @param context the context
     * @param parent the parent
     * @param persistentNode the persistent node
     * @param eventPoster the event poster
     */
    public SLNodeImpl(
                       SLContext context, SLNode parent, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster ) {
        this.context = context;
        this.parent = parent;
        this.pNode = persistentNode;
        this.eventPoster = eventPoster;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLPNodeGetter#getPNode()
     */
    public SLPersistentNode getPNode() {
        return pNode;
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getSession()
     */
    public SLGraphSession getSession() {
        return context.getSession();
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getContext()
     */
    public SLContext getContext() {
        return context;
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getID()
     */
    public String getID() throws SLGraphSessionException {
        try {
            return pNode.getID();
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve the node ID.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getName()
     */
    public String getName() throws SLGraphSessionException {
        try {
            String decodedName = getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_DECODED_NAME);
            return decodedName == null ? pNode.getName() : decodedName;
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve the node name.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.String)
     */
    public SLNode addNode( String name ) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(SLNode.class, name, getSession().getDefaultEncoder(), SLPersistenceMode.NORMAL, null, null);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.String, org.openspotlight.graph.SLEncoder)
     */
    public SLNode addNode( String name,
                           SLEncoder encoder ) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(SLNode.class, name, encoder, SLPersistenceMode.NORMAL, null, null);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name ) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, getSession().getDefaultEncoder(), SLPersistenceMode.NORMAL, null, null);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLEncoder)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         SLEncoder encoder ) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, encoder, SLPersistenceMode.NORMAL, null, null);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLPersistenceMode)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         SLPersistenceMode persistenceMode )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, getSession().getDefaultEncoder(), persistenceMode, null, null);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLEncoder, org.openspotlight.graph.SLPersistenceMode)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         SLEncoder encoder,
                                         SLPersistenceMode persistenceMode )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, encoder, persistenceMode, null, null);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, java.util.Collection, java.util.Collection)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, getSession().getDefaultEncoder(), SLPersistenceMode.NORMAL, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLPersistenceMode, java.util.Collection, java.util.Collection)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         SLPersistenceMode persistenceMode,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, getSession().getDefaultEncoder(), persistenceMode, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLEncoder, java.util.Collection, java.util.Collection)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         SLEncoder encoder,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, getSession().getDefaultEncoder(), SLPersistenceMode.NORMAL, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLEncoder, org.openspotlight.graph.SLPersistenceMode, java.util.Collection, java.util.Collection)
     */
    public <T extends SLNode> T addNode( Class<T> clazz,
                                         String name,
                                         SLEncoder encoder,
                                         SLPersistenceMode persistenceMode,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                         Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        return addChildNode(clazz, name, encoder, persistenceMode, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getNode(java.lang.String)
     */
    public SLNode getNode( String name ) throws SLInvalidNodeTypeException, SLGraphSessionException {
        return getChildNode(SLNode.class, name);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getNode(java.lang.Class, java.lang.String)
     */
    public <T extends SLNode> T getNode( Class<T> clazz,
                                         String name ) throws SLInvalidNodeTypeException, SLGraphSessionException {
        return getChildNode(clazz, name);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getNode(java.lang.Class, java.lang.String, org.openspotlight.graph.SLEncoder)
     */
    public <T extends SLNode> T getNode( Class<T> clazz,
                                         String name,
                                         SLEncoder encoder ) throws SLInvalidNodeTypeException, SLGraphSessionException {
        return getChildNode(clazz, name, encoder);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getNodes()
     */
    public Set<SLNode> getNodes() throws SLGraphSessionException {
        try {
            return getChildNodes(null);
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve child nodes.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#remove()
     */
    public void remove() throws SLGraphSessionException {
        try {
            Collection<SLNode> childNodes = new ArrayList<SLNode>();
            Iterator<SLNode> iter = getNodes().iterator();
            while (iter.hasNext()) {
                addChildNodes(childNodes, iter.next());
            }
            for (SLNode current : childNodes) {
                Collection<SLLink> links = getSession().getLinks(current, null);
                for (SLLink link : links) {
                    link.remove();
                }
            }
            pNode.remove();
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to remove node.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getParent()
     */
    public SLNode getParent() throws SLGraphSessionException {
        return parent;
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#setProperty(java.lang.Class, java.lang.String, java.io.Serializable)
     */
    public <V extends Serializable> SLNodeProperty<V> setProperty( Class<V> clazz,
                                                                   String name,
                                                                   V value ) throws SLGraphSessionException {
        try {
            String propName = SLCommonSupport.toUserPropertyName(name);
            SLPersistentProperty<V> pProperty = pNode.setProperty(clazz, propName, value);
            SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            Class<? extends SLNode> nodeType = getNodeType(pNode);
            SLNode nodeProxy = ProxyUtil.createNodeProxy(nodeType, this);
            SLNodeProperty<V> property = factory.createProperty(nodeProxy, pProperty, eventPoster);
            SLNodePropertyEvent event = new SLNodePropertyEvent(SLNodePropertyEvent.TYPE_NODE_PROPERTY_SET, property, pProperty);
            eventPoster.post(event);
            return property;
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to set property.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getProperty(java.lang.Class, java.lang.String)
     */
    public <V extends Serializable> SLNodeProperty<V> getProperty( Class<V> clazz,
                                                                   String name )
        throws SLNodePropertyNotFoundException, SLInvalidNodePropertyTypeException, SLGraphSessionException {
        return getProperty(clazz, name, null);
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getProperty(java.lang.Class, java.lang.String, java.text.Collator)
     */
    public <V extends Serializable> SLNodeProperty<V> getProperty( Class<V> clazz,
                                                                   String name,
                                                                   Collator collator )
        throws SLNodePropertyNotFoundException, SLInvalidNodePropertyTypeException, SLGraphSessionException {

        SLNodeProperty<V> property = null;

        try {

            String propName = SLCommonSupport.toUserPropertyName(name);
            SLPersistentProperty<V> pProperty = SLCommonSupport.getProperty(pNode, clazz, propName);
            Class<? extends SLNode> nodeType = getNodeType(pNode);

            // if property not found find collator if its strength is not identical ...
            if (pProperty == null) {
                if (nodeType != null) {
                    Set<SLPersistentProperty<Serializable>> pProperties = pNode.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
                    for (SLPersistentProperty<Serializable> current : pProperties) {
                        String currentName = SLCommonSupport.toSimplePropertyName(current.getName());
                        Collator currentCollator = collator == null ? SLCollatorSupport.getPropertyCollator(nodeType, currentName) : collator;
                        if (currentCollator.compare(name, currentName) == 0) {
                            pProperty = pNode.getProperty(clazz, current.getName());
                            break;
                        }
                    }
                }
            }

            if (pProperty != null) {
                SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
                SLNode nodeProxy = ProxyUtil.createNodeProxy(nodeType, this);
                property = factory.createProperty(nodeProxy, pProperty, eventPoster);
            }
        } catch (SLInvalidPersistentPropertyTypeException e) {
            throw new SLInvalidNodePropertyTypeException(e);
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve node property.", e);
        }

        if (property == null) throw new SLNodePropertyNotFoundException(name);
        return property;
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getPropertyValueAsString(java.lang.String)
     */
    public String getPropertyValueAsString( String name ) throws SLNodePropertyNotFoundException, SLGraphSessionException {
        return getProperty(Serializable.class, name).getValue().toString();
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getProperties()
     */
    public Set<SLNodeProperty<Serializable>> getProperties() throws SLGraphSessionException {
        try {
            Class<? extends SLNode> nodeType = getNodeType(pNode);
            SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            Set<SLNodeProperty<Serializable>> properties = new HashSet<SLNodeProperty<Serializable>>();
            Set<SLPersistentProperty<Serializable>> persistentProperties = pNode.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
            for (SLPersistentProperty<Serializable> persistentProperty : persistentProperties) {
                SLNode nodeProxy = ProxyUtil.createNodeProxy(nodeType, this);
                SLNodeProperty<Serializable> property = factory.createProperty(nodeProxy, persistentProperty, eventPoster);
                properties.add(property);
            }
            return properties;
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve node properties.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#addLineReference(int, int, int, int, java.lang.String, java.lang.String, java.lang.String)
     */
    public SLLineReference addLineReference( int startLine,
                                             int endLine,
                                             int startColumn,
                                             int endColumn,
                                             String statement,
                                             String artifactId,
                                             String artifactVersion ) throws SLGraphSessionException {

        try {

            StringBuilder lineReferenceKey = new StringBuilder()
                                                                .append(startLine).append('.').append(endLine).append('.')
                                                                .append(startColumn).append('.').append(endColumn).append('.')
                                                                .append(statement).append(artifactId).append('.').append(artifactVersion);

            SLEncoderFactory factory = getSession().getEncoderFactory();
            SLEncoder fakeEncoder = factory.getFakeEncoder();
            SLEncoder uuidEncoder = factory.getUUIDEncoder();

            String propName = "lineRef." + uuidEncoder.encode(lineReferenceKey.toString());
            SLLineReference lineRef = addChildNode(SLLineReference.class, propName, fakeEncoder, SLPersistenceMode.NORMAL, null, null);

            lineRef.setStartLine(startLine);
            lineRef.setEndLine(endLine);
            lineRef.setStartColumn(startColumn);
            lineRef.setEndColumn(endColumn);
            lineRef.setStatement(statement);
            lineRef.setArtifactId(artifactId);
            lineRef.setArtifactVersion(artifactVersion);

            return lineRef;
        } catch (SLGraphSessionException e) {
            throw new SLGraphSessionException("Error on attempt to add line reference.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.SLNode#getLineReferences()
     */
    public Collection<SLLineReference> getLineReferences() throws SLGraphSessionException {
        try {
            Collection<SLLineReference> lineReferences = new ArrayList<SLLineReference>();
            Collection<SLNode> nodes = getChildNodes("lineRef.*");
            for (SLNode node : nodes) {
                SLLineReference lineRef = SLLineReference.class.cast(node);
                lineReferences.add(lineRef);
            }
            return lineReferences;
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve line references.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLTreeLineReference getTreeLineReferences() throws SLGraphSessionException {
        //TODO Implement here
        return null;
    }

    public String getTypeName() throws SLGraphSessionException {
        try {
            return SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_TYPE);
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve node type name.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj ) {
        //TODO isn't better to compare by getId()? -> not by getPath?
        try {
            if (obj == null || !(obj instanceof SLNode)) return false;
            SLPersistentNode pNode = SLCommonSupport.getPNode((SLNode)obj);
            return this.pNode.getID().equals(pNode.getID());
        } catch (SLException e) {
            throw new RuntimeException("Error on " + this.getClass() + " equals method.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        try {
            return getID().hashCode();
        } catch (SLGraphSessionException e) {
            throw new SLRuntimeException("Error on attempt to calculate node hash code.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( SLNode node ) {
        try {
            SLNodeInvocationHandler handler = (SLNodeInvocationHandler)Proxy.getInvocationHandler(node);
            SLNodeImpl n = (SLNodeImpl)handler.getNode();
            return pNode.getPath().compareTo(n.pNode.getPath());
        } catch (SLException e) {
            throw new SLRuntimeException("Error on attempt to compare nodes.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(this.getName());
            sb.append("\n\t");
            sb.append("ID:");
            sb.append(this.getID());
            sb.append("\n\t");
            for (SLNodeProperty<Serializable> activeProperty : this.getProperties()) {
                sb.append(activeProperty.getName());
                sb.append(":");
                sb.append(activeProperty.getValueAsString());
                sb.append("\n\t");
            }
            return sb.toString();
        } catch (SLGraphSessionException e) {
        }
        return pNode.toString();
    }

    /**
     * Creates the node proxy.
     * 
     * @param clazz the clazz
     * @param pNode the node
     * @return the t
     */
    public <T extends SLNode> T createNodeProxy( Class<T> clazz,
                                                 SLPersistentNode pNode ) {
        SLNode node = new SLNodeImpl(context, this, pNode, eventPoster);
        InvocationHandler handler = new SLNodeInvocationHandler(node);
        return ProxyUtil.createProxy(clazz, handler);
    }

    /**
     * Adds the child node.
     * 
     * @param clazz the clazz
     * @param name the name
     * @param encoder the encoder
     * @param persistenceMode the persistence mode
     * @param linkTypesForLinkDeletion the link types for link deletion
     * @param linkTypesForLinkedNodeDeletion the link types for linked node deletion
     * @return the t
     * @throws SLGraphSessionException the SL graph session exception
     */
    private <T extends SLNode> T addChildNode( Class<T> clazz,
                                               String name,
                                               SLEncoder encoder,
                                               SLPersistenceMode persistenceMode,
                                               Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                               Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion )
        throws SLGraphSessionException {
        try {
            Class<T> type = null;
            String encodedName = encoder.encode(name);
            SLPersistentNode pChildNode = getHierarchyChildNode(clazz, name, encodedName);
            if (pChildNode == null) {
                type = clazz;
                pChildNode = pNode.addNode(encodedName);
                setInternalStringProperty(pChildNode, SLConsts.PROPERTY_NAME_DECODED_NAME, name);
            } else {
                Class<? extends SLNode> nodeType = getNodeType(pChildNode);
                type = getLessGenericNodeType(clazz, nodeType);
            }
            setInternalStringProperty(pChildNode, SLConsts.PROPERTY_NAME_TYPE, type.getName());
            T nodeProxy = createNodeProxy(type, pChildNode);
            eventPoster.post(new SLNodeEvent(SLNodeEvent.TYPE_NODE_ADDED, nodeProxy, pChildNode, persistenceMode, linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion));
            return nodeProxy;
        } catch (SLException e) {
            throw new SLGraphSessionException("Error on attempt to add node.", e);
        }
    }

    /**
     * Gets the child node.
     * 
     * @param clazz the clazz
     * @param name the name
     * @return the child node
     * @throws SLGraphSessionException the SL graph session exception
     */
    <T extends SLNode> T getChildNode( Class<T> clazz,
                                       String name ) throws SLGraphSessionException {
        return getChildNode(clazz, name, getSession().getDefaultEncoder());
    }

    /**
     * Gets the child node.
     * 
     * @param clazz the clazz
     * @param name the name
     * @param encoder the encoder
     * @return the child node
     * @throws SLGraphSessionException the SL graph session exception
     */
    <T extends SLNode> T getChildNode( Class<T> clazz,
                                       String name,
                                       SLEncoder encoder ) throws SLGraphSessionException {
        try {
            T proxyNode = null;
            String nodeName = encoder.encode(name);
            Collection<SLPersistentNode> pChildNodes = pNode.getNodes(nodeName);
            for (SLPersistentNode pChildNode : pChildNodes) {
                Class<? extends SLNode> nodeType = getNodeType(pChildNode);
                // if classes are of same hierarchy ...
                if (nodeTypesOfSameHierarchy(clazz, nodeType)) {
                    // gets the less generic type ...
                    Class<? extends T> lessGenericNodeType = getLessGenericNodeType(clazz, nodeType);
                    SLNode node = new SLNodeImpl(getContext(), this, pChildNode, eventPoster);
                    InvocationHandler handler = new SLNodeInvocationHandler(node);
                    proxyNode = ProxyUtil.createProxy(lessGenericNodeType, handler);
                    break;
                }
            }

            // try collator if necessary ...
            if (proxyNode == null && !SLCollatorSupport.isCollatorStrengthIdentical(clazz)) {
                pChildNodes = pNode.getNodes();
                Collator collator = SLCollatorSupport.getNodeCollator(clazz);
                for (SLPersistentNode pChildNode : pChildNodes) {
                    // if collator filter succeeds ...
                    String currentDecodedName = SLCommonSupport.getUserNodeName(pChildNode);
                    if (collator.compare(name, currentDecodedName) == 0) {
                        Class<? extends SLNode> nodeType = getNodeType(pChildNode);
                        // if classes are of same hierarchy ...
                        if (nodeTypesOfSameHierarchy(clazz, nodeType)) {
                            // gets the less generic type ...
                            Class<? extends T> lessGenericNodeType = getLessGenericNodeType(clazz, nodeType);
                            SLNode node = new SLNodeImpl(getContext(), this, pChildNode, eventPoster);
                            InvocationHandler handler = new SLNodeInvocationHandler(node);
                            proxyNode = ProxyUtil.createProxy(lessGenericNodeType, handler);
                            break;
                        }
                    }
                }
            }
            return proxyNode;
        } catch (Exception e) {
            throw new SLGraphSessionException("Error on attempt to get node.", e);
        }
    }

    /**
     * Gets the less generic node type.
     * 
     * @param type1 the type1
     * @param type2 the type2
     * @return the less generic node type
     */
    @SuppressWarnings( "unchecked" )
    private <T extends SLNode> Class<T> getLessGenericNodeType( Class<T> type1,
                                                                Class<? extends SLNode> type2 ) {
        return (Class<T>)(type1.isAssignableFrom(type2) ? type2 : type1);
    }

    /**
     * Gets the hierarchy child node.
     * 
     * @param clazz the clazz
     * @param decodedName the decoded name
     * @param encodedName the encoded name
     * @return the hierarchy child node
     * @throws SLException the SL exception
     */
    private SLPersistentNode getHierarchyChildNode( Class<? extends SLNode> clazz,
                                                    String decodedName,
                                                    String encodedName ) throws SLException {

        SLPersistentNode pChildNode = null;
        String nodeName = encodedName == null ? decodedName : encodedName;
        Collection<SLPersistentNode> pChildNodes = pNode.getNodes(nodeName);
        for (SLPersistentNode current : pChildNodes) {
            Class<? extends SLNode> nodeType = getNodeType(current);
            if (nodeTypesOfSameHierarchy(clazz, nodeType)) {
                pChildNode = current;
                break;
            }
        }

        if (pChildNode == null && !SLCollatorSupport.isCollatorStrengthIdentical(clazz)) {
            Collator collator = SLCollatorSupport.getNodeCollator(clazz);
            for (SLPersistentNode current : pNode.getNodes()) {
                String currentDecodedName = SLCommonSupport.getUserNodeName(current);
                if (collator.compare(decodedName, currentDecodedName) == 0) {
                    Class<? extends SLNode> nodeType = getNodeType(current);
                    if (nodeTypesOfSameHierarchy(clazz, nodeType)) {
                        pChildNode = current;
                        break;
                    }
                }
            }
        }

        return pChildNode;
    }

    /**
     * Node types of same hierarchy.
     * 
     * @param type1 the type1
     * @param type2 the type2
     * @return true, if successful
     */
    private boolean nodeTypesOfSameHierarchy( Class<? extends SLNode> type1,
                                              Class<? extends SLNode> type2 ) {
        return type1.isAssignableFrom(type2) || type2.isAssignableFrom(type2);
    }

    /**
     * Gets the node type.
     * 
     * @param pNode the node
     * @return the node type
     * @throws SLGraphSessionException the SL graph session exception
     */
    @SuppressWarnings( "unchecked" )
    private Class<? extends SLNode> getNodeType( SLPersistentNode pNode ) throws SLGraphSessionException {
        try {
            Class<? extends SLNode> type = null;
            String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
            SLPersistentProperty<String> typeNameProp = SLCommonSupport.getProperty(pNode, String.class, propName);
            if (typeNameProp != null) {
                type = (Class<? extends SLNode>)Class.forName(typeNameProp.getValue());
            }
            return type == null ? SLNode.class : type;
        } catch (Exception e) {
            throw new SLGraphSessionException("Error on attempt to retrieve node type.", e);
        }
    }

    /**
     * Adds the child nodes.
     * 
     * @param childNodes the child nodes
     * @param node the node
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addChildNodes( Collection<SLNode> childNodes,
                                SLNode node ) throws SLGraphSessionException {
        Collection<SLNode> nodes = node.getNodes();
        for (SLNode current : nodes) {
            addChildNodes(childNodes, current);
        }
        childNodes.add(node);
    }

    /**
     * Gets the child nodes.
     * 
     * @param pattern the pattern
     * @return the child nodes
     * @throws SLException the SL exception
     */
    private Set<SLNode> getChildNodes( String pattern ) throws SLException {
        SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        Set<SLNode> childNodes = new HashSet<SLNode>();
        Collection<SLPersistentNode> persistentChildNodes = pattern == null ? pNode.getNodes() : pNode.getNodes(pattern);
        for (SLPersistentNode persistentChildNode : persistentChildNodes) {
            SLNode childNode = factory.createNode(getContext(), this, persistentChildNode, eventPoster);
            SLNodeInvocationHandler handler = new SLNodeInvocationHandler(childNode);
            Class<? extends SLNode> nodeType = getNodeType(persistentChildNode);
            SLNode childNodeProxy = ProxyUtil.createProxy(nodeType, handler);
            childNodes.add(childNodeProxy);
        }
        return childNodes;
    }
}
