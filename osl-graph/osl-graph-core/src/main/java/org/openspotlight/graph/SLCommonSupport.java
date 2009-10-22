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
import java.util.Arrays;

import org.openspotlight.graph.annotation.SLLinkAttribute;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.ProxyUtil;

/**
 * The Class SLCommonSupport.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLCommonSupport {

    /**
     * Gets the p node.
     * 
     * @param node the node
     * @return the p node
     */
    public static SLPersistentNode getPNode( SLNode node ) {
        SLPNodeGetter getter = (SLPNodeGetter)((node instanceof SLPNodeGetter) ? node : ProxyUtil.getNodeFromProxy(node));
        return getter.getPNode();
    }

    /**
     * Gets the link type.
     * 
     * @param link the link
     * @return the link type
     */
    @SuppressWarnings( "unchecked" )
    public static Class<? extends SLLink> getLinkType( SLLink link ) {
        return (Class<? extends SLLink>)link.getClass().getInterfaces()[0];
    }

    /**
     * Gets the node type.
     * 
     * @param node the node
     * @return the node type
     */
    @SuppressWarnings( "unchecked" )
    public static Class<? extends SLNode> getNodeType( SLNode node ) {
        return (Class<? extends SLNode>)node.getClass().getInterfaces()[0];
    }

    /**
     * Gets the user node name.
     * 
     * @param pNode the node
     * @return the user node name
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static String getUserNodeName( SLPersistentNode pNode ) throws SLPersistentTreeSessionException {
        String decodedName = SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_DECODED_NAME);
        return decodedName == null ? pNode.getName() : decodedName;
    }

    /**
     * Sets the internal string property.
     * 
     * @param pNode the node
     * @param propName the prop name
     * @param value the value
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static void setInternalStringProperty( SLPersistentNode pNode,
                                                  String propName,
                                                  String value ) throws SLPersistentTreeSessionException {
        String internalPropName = toInternalPropertyName(propName);
        pNode.setProperty(String.class, internalPropName, value);
    }

    /**
     * Sets the internal integer property.
     * 
     * @param pNode the node
     * @param propName the prop name
     * @param value the value
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static void setInternalIntegerProperty( SLPersistentNode pNode,
                                                   String propName,
                                                   Integer value ) throws SLPersistentTreeSessionException {
        String internalPropName = toInternalPropertyName(propName);
        pNode.setProperty(Integer.class, internalPropName, value);
    }

    /**
     * Gets the user property.
     * 
     * @param pNode the node
     * @param name the name
     * @return the user property
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static Serializable getUserPropertyAsSerializable( SLPersistentNode pNode,
                                                              String name ) throws SLPersistentTreeSessionException {
        Serializable value = null;
        SLPersistentProperty<Serializable> prop = getProperty(pNode, Serializable.class, toUserPropertyName(name));
        if (prop != null) value = prop.getValue();
        return value;
    }

    /**
     * Gets the internal property as string.
     * 
     * @param pNode the node
     * @param propName the prop name
     * @return the internal property as string
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static String getInternalPropertyAsString( SLPersistentNode pNode,
                                                      String propName ) throws SLPersistentTreeSessionException {
        String value = null;
        String internalPropName = toInternalPropertyName(propName);
        SLPersistentProperty<String> prop = getProperty(pNode, String.class, internalPropName);
        if (prop != null) value = prop.getValue();
        return value;
    }

    /**
     * Gets the internal property as integer.
     * 
     * @param pNode the node
     * @param propName the prop name
     * @return the internal property as integer
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static Integer getInternalPropertyAsInteger( SLPersistentNode pNode,
                                                        String propName ) throws SLPersistentTreeSessionException {
        Integer value = null;
        String internalPropName = toInternalPropertyName(propName);
        SLPersistentProperty<Integer> prop = getProperty(pNode, Integer.class, internalPropName);
        if (prop != null) value = prop.getValue();
        return value;
    }

    /**
     * Gets the node type name.
     * 
     * @param pNode the node
     * @return the node type name
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static String getNodeTypeName( SLPersistentNode pNode ) throws SLPersistentTreeSessionException {
        return SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_TYPE);
    }

    /**
     * Gets the name in id form.
     * 
     * @param node the node
     * @return the name in id form
     * @throws SLGraphSessionException the SL graph session exception
     */
    public static String getNameInIDForm( SLNode node ) throws SLGraphSessionException {
        return "node.".concat(node.getID().replace("-", "."));
    }

    /**
     * Gets the node id.
     * 
     * @param linkNodeName the link node name
     * @return the node id
     */
    public static String getNodeID( String linkNodeName ) {
        return linkNodeName.substring("node.".length()).replace(".", "-");
    }

    /**
     * To internal property name.
     * 
     * @param name the name
     * @return the string
     */
    public static String toInternalPropertyName( String name ) {
        return new StringBuilder().append(SLConsts.PROPERTY_PREFIX_INTERNAL).append('.').append(name).toString();
    }

    /**
     * To user property name.
     * 
     * @param name the name
     * @return the string
     */
    public static String toUserPropertyName( String name ) {
        return new StringBuilder().append(SLConsts.PROPERTY_PREFIX_USER).append('.').append(name).toString();
    }

    /**
     * To user property name.
     * 
     * @param name the name
     * @param attributeName the attribute name
     * @return the string
     */
    public static String toUserPropertyName( String name,
                                             String attributeName ) {
        return new StringBuilder().append(SLConsts.PROPERTY_PREFIX_USER).append('.').append(name).append('.').append(attributeName).toString();
    }

    /**
     * To simple property name.
     * 
     * @param name the name
     * @return the string
     */
    public static String toSimplePropertyName( String name ) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Gets the link index node name.
     * 
     * @param index the index
     * @return the link index node name
     */
    public static String getLinkIndexNodeName( long index ) {
        return "index." + index;
    }

    /**
     * Allows change to bidirectional.
     * 
     * @param linkClass the link class
     * @return true, if successful
     */
    public static boolean allowsChangeToBidirectional( Class<? extends SLLink> linkClass ) {
        SLLinkAttribute attribute = linkClass.getAnnotation(SLLinkAttribute.class);
        return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_CHANGE_TO_BIDIRECTIONAL) > -1;
    }

    /**
     * Gets the property.
     * 
     * @param pNode the node
     * @param clazz the clazz
     * @param name the name
     * @return the property
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static <T extends Serializable> SLPersistentProperty<T> getProperty( SLPersistentNode pNode,
                                                                                Class<T> clazz,
                                                                                String name )
        throws SLPersistentTreeSessionException {
        SLPersistentProperty<T> pProperty = null;
        try {
            pProperty = pNode.getProperty(clazz, name);
        } catch (SLPersistentPropertyNotFoundException e) {
        }
        return pProperty;
    }

    //osl/contexts
    /**
     * Gets the contexts persistent node.
     * 
     * @param treeSession the tree session
     * @return the contexts persistent node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getContextsPersistentNode( SLPersistentTreeSession treeSession )
        throws SLPersistentTreeSessionException {
        SLPersistentNode oslRootNode = treeSession.getRootNode();
        SLPersistentNode contextsPersistentNode = oslRootNode.getNode(SLConsts.NODE_NAME_CONTEXTS);
        if (contextsPersistentNode == null) {
            contextsPersistentNode = oslRootNode.addNode(SLConsts.NODE_NAME_CONTEXTS);
        }
        return contextsPersistentNode;
    }

    //osl/links
    /**
     * Gets the links persistent node.
     * 
     * @param treeSession the tree session
     * @return the links persistent node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getLinksPersistentNode( SLPersistentTreeSession treeSession )
        throws SLPersistentTreeSessionException {
        SLPersistentNode oslPersistentNode = treeSession.getRootNode();
        SLPersistentNode linksPersistentNode = oslPersistentNode.getNode(SLConsts.NODE_NAME_LINKS);
        if (linksPersistentNode == null) {
            linksPersistentNode = oslPersistentNode.addNode(SLConsts.NODE_NAME_LINKS);
        }
        return linksPersistentNode;
    }

    //osl/links/linkClassFullQualifiedName
    /**
     * Gets the link class node.
     * 
     * @param treeSession the tree session
     * @param linkClass the link class
     * @return the link class node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getLinkClassNode( SLPersistentTreeSession treeSession,
                                                     Class<? extends SLLink> linkClass ) throws SLPersistentTreeSessionException {
        SLPersistentNode linksNode = SLCommonSupport.getLinksPersistentNode(treeSession);
        SLPersistentNode linkClassNode = linksNode.getNode(linkClass.getName());
        if (linkClassNode == null) {
            linkClassNode = linksNode.addNode(linkClass.getName());
        }
        return linkClassNode;
    }

    //osl/metadata
    /**
     * Gets the metadata node.
     * 
     * @param treeSession the tree session
     * @return the metadata node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getMetadataNode( SLPersistentTreeSession treeSession ) throws SLPersistentTreeSessionException {
        SLPersistentNode oslRootNode = treeSession.getRootNode();
        SLPersistentNode metadataNode = oslRootNode.getNode(SLConsts.NODE_NAME_METADATA);
        if (metadataNode == null) {
            metadataNode = oslRootNode.addNode(SLConsts.NODE_NAME_METADATA);
        }
        return metadataNode;
    }

    //osl/queryCache
    /**
     * Gets the query cache node.
     * 
     * @param treeSession the tree session
     * @return the query cache node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getQueryCacheNode( SLPersistentTreeSession treeSession )
        throws SLPersistentTreeSessionException {
        SLPersistentNode oslRootNode = treeSession.getRootNode();
        SLPersistentNode queryCacheNode = oslRootNode.getNode(SLConsts.NODE_NAME_QUERY_CACHE);
        if (queryCacheNode == null) {
            queryCacheNode = oslRootNode.addNode(SLConsts.NODE_NAME_QUERY_CACHE);
        }
        return queryCacheNode;
    }

    public static boolean containsQueryCache( SLPersistentTreeSession treeSession )
        throws SLPersistentTreeSessionException {
        SLPersistentNode oslRootNode = treeSession.getRootNode();
        SLPersistentNode queryCacheNode = oslRootNode.getNode(SLConsts.NODE_NAME_QUERY_CACHE);
        if (queryCacheNode == null) {
            return false;
        }
        return true;
    }

    //osl/metadata/types
    /**
     * Gets the meta types node.
     * 
     * @param treeSession the tree session
     * @return the meta types node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getMetaTypesNode( SLPersistentTreeSession treeSession )
        throws SLPersistentTreeSessionException {
        SLPersistentNode oslRootNode = getMetadataNode(treeSession);
        SLPersistentNode typesNode = oslRootNode.getNode(SLConsts.NODE_NAME_TYPES);
        if (typesNode == null) {
            typesNode = oslRootNode.addNode(SLConsts.NODE_NAME_TYPES);
        }
        return typesNode;
    }

    //osl/metadata/links
    /**
     * Gets the meta links node.
     * 
     * @param treeSession the tree session
     * @return the meta links node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getMetaLinksNode( SLPersistentTreeSession treeSession )
        throws SLPersistentTreeSessionException {
        SLPersistentNode oslRootNode = getMetadataNode(treeSession);
        SLPersistentNode linksNode = oslRootNode.getNode(SLConsts.NODE_NAME_LINKS);
        if (linksNode == null) {
            linksNode = oslRootNode.addNode(SLConsts.NODE_NAME_LINKS);
        }
        return linksNode;
    }

    //osl/metadata/links/linkClassFullQualifiedName
    /**
     * Gets the meta link class node.
     * 
     * @param treeSession the tree session
     * @param linkClass the link class
     * @return the meta link class node
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    public static SLPersistentNode getMetaLinkClassNode( SLPersistentTreeSession treeSession,
                                                         Class<? extends SLLink> linkClass )
        throws SLPersistentTreeSessionException {
        SLPersistentNode linksNode = getMetaLinksNode(treeSession);
        SLPersistentNode linkClassNode = linksNode.getNode(linkClass.getName());
        if (linkClassNode == null) {
            linkClassNode = linksNode.addNode(linkClass.getName());
        }
        return linkClassNode;
    }
}
