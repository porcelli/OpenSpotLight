/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 *  Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 *  or third-party contributors as indicated by the @author tags or express
 *  copyright attribution statements applied by the authors.  All third-party
 *  contributions are distributed under license by CARAVELATECH CONSULTORIA E
 *  TECNOLOGIA EM INFORMATICA LTDA.
 *
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU
 *  Lesser General Public License, as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this distribution; if not, write to:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 *
 * **********************************************************************
 *  OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 *  Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 *  EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 *  @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 *  Todas as contribuições de terceiros estão distribuídas sob licença da
 *  CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 *  Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 *  termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 *  Foundation.
 *
 *  Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 *  GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 *  FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 *  programa; se não, escreva para:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 */

package org.openspotlight.graph.manipulation;

import org.openspotlight.graph.Context;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkType;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.exception.NodeNotFoundException;
import org.openspotlight.graph.metadata.MetaLinkType;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryText;

/**
 * Created by IntelliJ IDEA. User: porcelli Date: 06/07/2010 Time: 11:33:31 To change this template use File | Settings | File
 * Templates.
 */
public interface GraphReader {
    /**
     * Creates the api query.
     * 
     * @return the sL query
     */
    SLQueryApi createQueryApi();

    /**
     * Creates the text query.
     * 
     * @param slqlInput the slql input
     * @return the sL query
     * @throws org.openspotlight.graph.query.SLInvalidQuerySyntaxException invalid syntax
     */
    SLQueryText createQueryText( String slqlInput )
            throws SLInvalidQuerySyntaxException;

    /**
     * Gets the context.
     * 
     * @param id the id
     * @return the context
     */
    Context getContext( String id );

    /**
     * Gets the node by id.
     * 
     * @param id the id
     * @return the node by id
     * @throws org.openspotlight.graph.exception.SLNodeNotFoundException node not found
     */
    Node getNode( Context context,
                  String id ) throws NodeNotFoundException;

    Iterable<Node> getNode( String id ) throws NodeNotFoundException;

    /**
     * Gets the links.
     * 
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @return the links
     */
    <L extends Link> L getLink( Class<L> linkClass,
                                  Node source,
                                  Node target,
                                  LinkType linkDirection );

    /**
     * Gets the links.
     * 
     * @param source the source
     * @param target the target
     * @return the links
     */
    Iterable<Link> getLinks( Node source,
                               Node target,
                               LinkType linkDirection );

    /**
     * Gets the bidirectional links by side.
     * 
     * @param linkClass the link class
     * @param side the side
     * @return the bidirectional links by side
     */
    <L extends Link> Iterable<L> getBidirectionalLinks( Class<L> linkClass,
                                                          Node side );

    /**
     * Gets the unidirectional links by source.
     * 
     * @param linkClass the link class
     * @param source the source
     * @return the unidirectional links by source
     */
    <L extends Link> Iterable<L> getUnidirectionalLinksBySource(
                                                                   Class<L> linkClass,
                                                                   Node source );

    /**
     * Gets the unidirectional links by target.
     * 
     * @param linkClass the link class
     * @param target the target
     * @return the unidirectional links by target
     */
    <L extends Link> Iterable<L> getUnidirectionalLinksByTarget(
                                                                   Class<L> linkClass,
                                                                   Node target );

    /**
     * Gets the bidirectional links by side.
     * 
     * @param side the side
     * @return the bidirectional links by side
     */
    Iterable<Link> getBidirectionalLinks( Node side );

    /**
     * Gets the unidirectional links by source.
     * 
     * @param source the source
     * @return the unidirectional links by source
     */
    Iterable<Link> getUnidirectionalLinksBySource( Node source );

    /**
     * Gets the unidirectional links by target.
     * 
     * @param target the target
     * @return the unidirectional links by target
     */
    Iterable<Link> getUnidirectionalLinksByTarget( Node target );

    /**
     * Gets the metadata.
     * 
     * @return the metadata
     */
    Metadata getMetadata();

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @return the nodes by link
     */
    Iterable<Node> getLinkedNodes( Class<? extends Link> linkClass );

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @param node the node
     * @return the nodes by link
     */
    Iterable<Node> getLinkedNodes( Class<? extends Link> linkClass,
                                     Node node,
                                     LinkType linkDirection );

    /**
     * Gets the nodes by link.
     * 
     * @param linkClass the link class
     * @param node the node
     * @param nodeClass the node class
     * @param returnSubTypes the return sub types
     * @param linkDirection the link direction
     * @return the nodes by link
     */
    <N extends Node> Iterable<N> getLinkedNodes(
                                                   Class<? extends Link> linkClass,
                                                   Node node,
                                                   Class<N> nodeClass,
                                                   boolean returnSubTypes,
                                                   LinkType linkDirection );

    /**
     * Gets the nodes by link.
     * 
     * @param node the node
     * @param nodeClass the node class
     * @param returnSubTypes the return sub types
     * @return the nodes by link
     */
    <N extends Node> Iterable<N> getLinkedNodes( Node node,
                                                   Class<N> nodeClass,
                                                   boolean returnSubTypes,
                                                   LinkType linkDirection );

    /**
     * Gets the nodes by link.
     * 
     * @param node the node
     * @return the nodes by link
     */
    Iterable<Node> getLinkedNodes( Node node,
                                     LinkType linkDirection );

    /**
     * Gets the node.
     * 
     * @param clazz the clazz
     * @param name the name
     * @return the node
     */
    <T extends Node> T getChildNode( Node node,
                                       Class<T> clazz,
                                       String name );

    /**
     * Gets the child node.
     * 
     * @param clazz the clazz
     * @return the child node
     */
    <T extends Node> Iterable<T> getChildrenNodes( Node node,
                                                     Class<T> clazz );

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    Node getParentNode( Node node );

    /**
     * Gets the meta link.
     * 
     * @return the meta link
     */
    MetaLinkType getMetaType( Link link );

    /**
     * Gets the meta node type.
     * 
     * @return the meta type or null if its a simple node
     */
    MetaNodeType getMetaType( Node node );

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    Context getContext( Node node );

    <T extends Node> Iterable<T> findNodes( Class<T> clazz,
                                              String name,
                                              Context context,
                                              Context... aditionalContexts );

    Iterable<Node> findNodes( String name,
                                Context context,
                                Context... aditionalContexts );

    <T extends Node> Iterable<T> findNodes( Class<T> clazz,
                                              Context context,
                                              Context... aditionalContexts );

    <T extends Node> T findUniqueNode( Class<T> clazz,
                                         String name,
                                         Context context,
                                         Context... aditionalContexts );

    Node findUniqueNode( String name,
                           Context context,
                           Context... aditionalContexts );

    <T extends Node> T findUniqueNode( Class<T> clazz,
                                         Context context,
                                         Context... aditionalContexts );
}
