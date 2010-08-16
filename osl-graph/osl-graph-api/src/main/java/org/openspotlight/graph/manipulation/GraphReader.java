/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA **********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */

package org.openspotlight.graph.manipulation;

import java.io.Serializable;

import org.openspotlight.graph.Context;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkType;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.metadata.MetaLinkType;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.query.InvalidQuerySyntaxException;
import org.openspotlight.graph.query.QueryApi;
import org.openspotlight.graph.query.QueryText;

/**
 * This interfaces has a list of method that can read/query the graph. The location ({@link org.openspotlight.graph.GraphLocation}
 * where these method should look for data are defined by graph session.
 * 
 * @see org.openspotlight.graph.SimpleGraphSession
 * @see org.openspotlight.graph.FullGraphSession
 * @author porcelli
 * @author feuteston
 */
public interface GraphReader {
    /**
     * Factory method that constructs a query api interface. <br>
     * The query api is our internal dsl (fluent api), that enables query the graph.
     * 
     * @return the query api
     */
    QueryApi createQueryApi();

    /**
     * Factory method that constructs a text based query interface. <br>
     * The query text is our external dsl that enables query the graph.
     * 
     * @param query the query content
     * @return the query text
     * @throws IllegalArgumentException if the input param is null
     * @throws InvalidQuerySyntaxException if the input query content has invalid syntax
     */
    QueryText createQueryText(String query)
            throws IllegalArgumentException, InvalidQuerySyntaxException;

    /**
     * Returns the metadata query interface, that contains most common used methods to query the metadata information. If its
     * necessary more advanced serach capabilities you can query directly the metadata context.
     * 
     * @return the metadata query interface
     */
    Metadata getMetadata();

    /**
     * Returns the meta link type ({@link MetaLinkType} of a specific link.
     * 
     * @param link the link instance
     * @return meta link type or null if not found
     * @throws IllegalArgumentException if the input param is null
     */
    MetaLinkType getMetaType(Link link)
        throws IllegalArgumentException;

    /**
     * Returns the meta node type ({@link MetaNodeType} of a specific node.
     * 
     * @param node the node instance
     * @return meta link type or null if not found
     * @throws IllegalArgumentException if the input param is null
     */
    MetaNodeType getMetaType(Node node)
        throws IllegalArgumentException;

    /**
     * Returns the context based on its unique id.
     * 
     * @param id the unique node id
     * @return the context
     * @throws IllegalArgumentException if the input param is null
     */
    Context getContext(String id)
        throws IllegalArgumentException;

    /**
     * Returns the context of the node.
     * 
     * @param node the node
     * @return the context
     * @throws IllegalArgumentException if the input param is null
     */
    Context getContext(Node node)
        throws IllegalArgumentException;

    /**
     * Returns the parent node of the input node.
     * 
     * @param node the node
     * @return the parent node or null if there is no parent node
     * @throws IllegalArgumentException if the input param is null
     */
    Node getParentNode(Node node)
        throws IllegalArgumentException;

    /**
     * Returns a node based on its unique id inside the specific context.
     * 
     * @param context the context to be searched
     * @param id the node id
     * @return the node or null if not found
     * @throws IllegalArgumentException if any input param is null
     */
    Node getNode(Context context,
                  String id)
        throws IllegalArgumentException;

    /**
     * Returns a list node based on its unique id inside the specific context.
     * 
     * @param id the unique node id
     * @return the node list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    Iterable<Node> getNode(String id)
        throws IllegalArgumentException;

    /**
     * Returns the child node that matches uniquely by node type and name.
     * 
     * @param <T> the node type
     * @param node the parent node
     * @param clazz the child class node type
     * @param name the node name
     * @return the child node or null if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> T getChildNode(Node node,
                                     Class<T> clazz,
                                     String name)
        throws IllegalArgumentException;

    /**
     * Returns the children node list of the parameter node type.
     * 
     * @param <T> the node type
     * @param node the parent node
     * @param clazz the child class node type
     * @return the children node list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> Iterable<T> getChildrenNodes(Node node,
                                                   Class<T> clazz)
        throws IllegalArgumentException;

    /**
     * Returns the children node list.
     * 
     * @param <T> the node type
     * @param node the parent node
     * @return the children node list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    <T extends Node> Iterable<T> getChildrenNodes(Node node)
        throws IllegalArgumentException;

    /**
     * Returns a unique link instance defined by input parameters.
     * 
     * @param <L> link type
     * @param linkTypeClass the link type class
     * @param source the source node
     * @param target the target node
     * @param linkDirection the desired direction
     * @return the link or null if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <L extends Link> L getLink(Class<L> linkTypeClass,
                                Node source,
                                Node target,
                                LinkType linkDirection)
        throws IllegalArgumentException;

    /**
     * Returns a list of link instances of any link type that match the source, target and direction.
     * 
     * @param source the source node
     * @param target the target node
     * @param linkDirection the desired direction
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    Iterable<Link> getLinks(Node source,
                             Node target,
                             LinkType linkDirection)
        throws IllegalArgumentException;

    /**
     * Returns a list of bidirectional link instances that matches the link type of a given node.
     * 
     * @param <L> link type
     * @param linkClass the desired link type
     * @param side the node
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <L extends Link> Iterable<L> getBidirectionalLinks(Class<L> linkClass,
                                                        Node side)
        throws IllegalArgumentException;

    /**
     * Returns a list of unidirectional link instances that matches the link type of a given source node.
     * 
     * @param <L> link type
     * @param linkClass the desired link type
     * @param source the source node
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <L extends Link> Iterable<L> getUnidirectionalLinksBySource(Class<L> linkClass,
                                                                 Node source)
        throws IllegalArgumentException;

    /**
     * Returns a list of unidirectional link instances that matches the link type of a given target node.
     * 
     * @param <L> link type
     * @param linkClass the desired link type
     * @param target the target node
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <L extends Link> Iterable<L> getUnidirectionalLinksByTarget(Class<L> linkClass,
                                                                 Node target)
        throws IllegalArgumentException;

    /**
     * Returns a list of any bidirectional link instances of a given node.
     * 
     * @param side the input node
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    Iterable<Link> getBidirectionalLinks(Node side)
        throws IllegalArgumentException;

    /**
     * Returns a list of any unidirectional link instances of a given source node.
     * 
     * @param source the source node
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    Iterable<Link> getUnidirectionalLinksBySource(Node source)
        throws IllegalArgumentException;

    /**
     * Returns a list of any unidirectional link instances of a given target node.
     * 
     * @param target the source node
     * @return the link list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    Iterable<Link> getUnidirectionalLinksByTarget(Node target)
        throws IllegalArgumentException;

    /**
     * Returns a list of linked nodes of an input node based on a specific link type and direction.
     * 
     * @param linkClass the link type
     * @param node the input node
     * @param linkDirection the direction
     * @return the linked nodes, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    Iterable<Node> getLinkedNodes(Class<? extends Link> linkClass,
                                   Node node,
                                   LinkType linkDirection)
        throws IllegalArgumentException;

    /**
     * Returns a list of linked nodes of a specific type (and optionally its subtypes) of an input node based on a specific link
     * type and direction.
     * 
     * @param <N> node type
     * @param linkClass the link type
     * @param node the input node
     * @param nodeClass the node type filter
     * @param returnSubTypes if returns all subtypes of the node type
     * @param linkDirection the direction
     * @return the linked nodes, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <N extends Node> Iterable<N> getLinkedNodes(Class<? extends Link> linkClass,
                                                 Node node,
                                                 Class<N> nodeClass,
                                                 boolean returnSubTypes,
                                                 LinkType linkDirection)
        throws IllegalArgumentException;

    /**
     * Returns a list of linked nodes of a specific type (and optionally its subtypes) of an input node just based on direction.
     * 
     * @param <N> node type
     * @param node the input node
     * @param nodeClass the node type filter
     * @param returnSubTypes if returns all subtypes of the node type
     * @param linkDirection the direction
     * @return the linked nodes, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <N extends Node> Iterable<N> getLinkedNodes(Node node,
                                                 Class<N> nodeClass,
                                                 boolean returnSubTypes,
                                                 LinkType linkDirection)
        throws IllegalArgumentException;

    /**
     * Returns a list of linked nodes of an input node just based on direction.
     * 
     * @param node the input node
     * @param linkDirection the direction
     * @return the linked nodes, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    Iterable<Node> getLinkedNodes(Node node,
                                   LinkType linkDirection)
        throws IllegalArgumentException;

    /**
     * Returns a list of nodes of a given type (and optionally its subtypes) and context list.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    <T extends Node> Iterable<T> listNodes(Class<T> clazz,
                                            boolean returnSubTypes,
                                            Context context,
                                            Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Returns a list of nodes of a given type (and optionally its subtypes) on every context.<br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @return the node list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> Iterable<T> listNodes(Class<T> clazz,
                                            boolean returnSubTypes)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru input contexts based on type (optionally its subtypes) and name.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param name the node name
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    <T extends Node> Iterable<T> findNodesByName(Class<T> clazz,
                                                  String name,
                                                  boolean returnSubTypes,
                                                  Context context,
                                                  Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru entire graph based on type (optionally its subtypes) and name. <br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param name the node name
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> Iterable<T> findNodesByName(Class<T> clazz,
                                                  String name,
                                                  boolean returnSubTypes)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru input contexts based on name.
     * 
     * @param name the node name
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    Iterable<Node> findNodesByName(String name,
                                    Context context,
                                    Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru entire graph based on name. <br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param name the node name
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    Iterable<Node> findNodesByName(String name)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru input contexts based on type (optionally its subtypes) and caption.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param caption the node caption
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    <T extends Node> Iterable<T> findNodesByCaption(Class<T> clazz,
                                                     String caption,
                                                     boolean returnSubTypes,
                                                     Context context,
                                                     Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru entire graph based on type (optionally its subtypes) and caption. <br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param caption the node caption
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> Iterable<T> findNodesByCaption(Class<T> clazz,
                                                     String caption,
                                                     boolean returnSubTypes)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru input contexts based on caption.
     * 
     * @param caption the node caption
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    Iterable<Node> findNodesByCaption(String caption,
                                       Context context,
                                       Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru entire graph based on caption. <br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param caption the node caption
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if the input param is null
     */
    Iterable<Node> findNodesByCaption(String caption)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru input contexts based on type (optionally its subtypes) and a custom property value.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param propertyName the property name to be searched
     * @param value the property value that should match
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    <T extends Node> Iterable<T> findNodesByCustomProperty(Class<T> clazz,
                                                            String propertyName,
                                                            Serializable value,
                                                            boolean returnSubTypes,
                                                            Context context,
                                                            Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru entire graph based on type (optionally its subtypes) and a custom property value. <br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param <T> node type
     * @param clazz the node type to be listed
     * @param propertyName the property name to be searched
     * @param value the property value that should match
     * @param returnSubTypes the flag that indicates if should return subtypes
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> Iterable<T> findNodesByCustomProperty(Class<T> clazz,
                                                            String propertyName,
                                                            Serializable value,
                                                            boolean returnSubTypes)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru input contexts based on a custom property value.
     * 
     * @param propertyName the property name to be searched
     * @param value the property value that should match
     * @param context the main context
     * @param aditionalContexts the optional context list
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param, except the last varargs, is null
     */
    Iterable<Node> findNodesByCustomProperty(String propertyName,
                                              Serializable value,
                                              Context context,
                                              Context... aditionalContexts)
        throws IllegalArgumentException;

    /**
     * Search for nodes thru entire graph based on a custom property value. <br>
     * <b>Note</b> that this operation has a performance penalty because it needs scan every available context.
     * 
     * @param propertyName the property name to be searched
     * @param value the property value that should match
     * @return the found node list, empty if not found
     * @throws IllegalArgumentException if any input param is null
     */
    Iterable<Node> findNodesByCustomProperty(String propertyName,
                                              Serializable value)
        throws IllegalArgumentException;

}
