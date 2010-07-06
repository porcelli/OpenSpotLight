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

import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLLinkDirection;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.exception.SLNodeNotFoundException;
import org.openspotlight.graph.meta.SLMetaLink;
import org.openspotlight.graph.meta.SLMetaNodeType;
import org.openspotlight.graph.meta.SLMetadata;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryText;

/**
 * Created by IntelliJ IDEA.
 * User: porcelli
 * Date: 06/07/2010
 * Time: 11:33:31
 * To change this template use File | Settings | File Templates.
 */
public interface SLGraphReader {
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
    SLQueryText createQueryText(String slqlInput) throws SLInvalidQuerySyntaxException;

    /**
     * Gets the context.
     *
     * @param id the id
     * @return the context
     */
    SLContext getContext(String id);

    /**
     * Gets the node by id.
     *
     * @param id the id
     * @return the node by id
     * @throws org.openspotlight.graph.exception.SLNodeNotFoundException node not found
     */
    SLNode getNode(String id) throws SLNodeNotFoundException;

    /**
     * Gets the links.
     *
     * @param linkClass the link class
     * @param source    the source
     * @param target    the target
     * @return the links
     */
    <L extends SLLink> L getLink(Class<L> linkClass,
                                        SLNode source,
                                        SLNode target,
                                        SLLinkDirection linkDirection);

    /**
     * Gets the links.
     *
     * @param source the source
     * @param target the target
     * @return the links
     */
    Iterable<SLLink> getLinks(SLNode source,
                                     SLNode target,
                                     SLLinkDirection linkDirection);

    /**
     * Gets the bidirectional links by side.
     *
     * @param linkClass the link class
     * @param side      the side
     * @return the bidirectional links by side
     */
    <L extends SLLink> Iterable<L> getBidirectionalLinks(Class<L> linkClass,
                                                                SLNode side);

    /**
     * Gets the unidirectional links by source.
     *
     * @param linkClass the link class
     * @param source    the source
     * @return the unidirectional links by source
     */
    <L extends SLLink> Iterable<L> getUnidirectionalLinksBySource(Class<L> linkClass,
                                                                         SLNode source);

    /**
     * Gets the unidirectional links by target.
     *
     * @param linkClass the link class
     * @param target    the target
     * @return the unidirectional links by target
     */
    <L extends SLLink> Iterable<L> getUnidirectionalLinksByTarget(Class<L> linkClass,
                                                                         SLNode target);

    /**
     * Gets the bidirectional links by side.
     *
     * @param side the side
     * @return the bidirectional links by side
     */
    Iterable<SLLink> getBidirectionalLinks(SLNode side);

    /**
     * Gets the unidirectional links by source.
     *
     * @param source the source
     * @return the unidirectional links by source
     */
    Iterable<SLLink> getUnidirectionalLinksBySource(SLNode source);

    /**
     * Gets the unidirectional links by target.
     *
     * @param target the target
     * @return the unidirectional links by target
     */
    Iterable<SLLink> getUnidirectionalLinksByTarget(SLNode target);

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    SLMetadata getMetadata();

    /**
     * Gets the nodes by link.
     *
     * @param linkClass the link class
     * @return the nodes by link
     */
    Iterable<SLNode> getLinkedNodes(Class<? extends SLLink> linkClass);

    /**
     * Gets the nodes by link.
     *
     * @param linkClass the link class
     * @param node      the node
     * @return the nodes by link
     */
    Iterable<SLNode> getLinkedNodes(Class<? extends SLLink> linkClass,
                                           SLNode node, SLLinkDirection linkDirection);

    /**
     * Gets the nodes by link.
     *
     * @param linkClass      the link class
     * @param node           the node
     * @param nodeClass      the node class
     * @param returnSubTypes the return sub types
     * @param linkDirection  the link direction
     * @return the nodes by link
     */
    <N extends SLNode> Iterable<N> getLinkedNodes(Class<? extends SLLink> linkClass,
                                                         SLNode node,
                                                         Class<N> nodeClass,
                                                         boolean returnSubTypes,
                                                         SLLinkDirection linkDirection);

    /**
     * Gets the nodes by link.
     *
     * @param node           the node
     * @param nodeClass      the node class
     * @param returnSubTypes the return sub types
     * @return the nodes by link
     */
    <N extends SLNode> Iterable<N> getLinkedNodes(SLNode node,
                                                         Class<N> nodeClass,
                                                         boolean returnSubTypes,
                                                         SLLinkDirection linkDirection);

    /**
     * Gets the nodes by link.
     *
     * @param node the node
     * @return the nodes by link
     */
    Iterable<SLNode> getLinkedNodes(SLNode node,
                                           SLLinkDirection linkDirection);

    /**
     * Gets the node.
     *
     * @param clazz the clazz
     * @param name  the name
     * @return the node
     */
    <T extends SLNode> T getChildNode(SLNode node, Class<T> clazz,
                                                      String name);

    /**
     * Gets the child node.
     *
     * @param clazz the clazz
     * @return the child node
     */
    <T extends SLNode> Iterable<T> getChildrenNodes(SLNode node, Class<T> clazz);

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    SLNode getParentNode(SLNode node);

    /**
     * Gets the meta link.
     *
     * @return the meta link
     */
    SLMetaLink getMetaLink(SLLink link);

    /**
     * Gets the meta node type.
     *
     * @return the meta type or null if its a simple node
     */
    SLMetaNodeType getMetaType(SLNode node);

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    SLContext getContext(SLNode node);

    Iterable<SLNode> findNodes(String name);

    <T extends SLNode> Iterable<T> findNodes(Class<T> clazz, String name);

    Iterable<SLNode> findNodes(SLContext context, String name);

    <T extends SLNode> Iterable<T> findNodes(Class<T> clazz, SLContext context, String name);
}
