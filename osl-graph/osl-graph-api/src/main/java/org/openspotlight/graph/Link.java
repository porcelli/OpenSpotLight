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
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH
 * CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de
 * terceiros estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é
 * software livre; você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme
 * publicada pela Free Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença
 * Pública Geral Menor do GNU para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU
 * junto com este programa; se não, escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA
 * 02110-1301 USA
 */
package org.openspotlight.graph;

import org.openspotlight.graph.annotation.LinkAutoBidirectional;

/**
 * The Link is the way you correlate informations ({@link Node} instances) in graph. Any relationship between nodes are
 * materialized by a creation of a link. <br>
 * A Link is uniquely identified by three data: Type, Source Node and Target Node, and based on these data an algorithm is used to
 * generate an unique id.
 * <p>
 * We have two types of links (see {@link LinkDirection}): <br>
 * &nbsp;- <i>Unidirectional</i> links creates a link between a source and a target node that can be represented as: source ->
 * target<br>
 * &nbsp;- <i>Bidirectional</i> links creates a link between two nodes that can be represented as: node1 <-> node2
 * </p>
 * <p>
 * Links can be promoted from unidirectional to bidirectional, but to allow this promotion the link type should use the
 * {@link org.openspotlight.graph.annotation.LinkAutoBidirectional} annotation. <br>
 * The mechanism that creates this promotion is really simple and can be described as: if you create a link with a link type that
 * allows auto-bidirectional bettwen a source and a target nodes (node1 -> node2) and later creates the same link with the same
 * type bettwen target and source (node2 -> node1) it will convert the original link to a bidirectional link (node1 <-> node2).
 * </p>
 * <p>
 * The link has a special property called count that enables users keep tracking of how many instances of the active link (type,
 * source and target) exists.
 * </p>
 * <p>
 * To secure the data consistency its not possible change the unique identifiers of a Link. If you need so, you'll have to delete
 * it and create a new one.
 * </p>
 * <p>
 * Links can be created as transients by {@link org.openspotlight.graph.manipulation.GraphTransientWriter#createTransientLink} or
 * {@link org.openspotlight.graph.manipulation.GraphTransientWriter#createTransientBidirectionalLink} methods or as permanent by
 * {@link org.openspotlight.graph.manipulation.GraphWriter#addLink} or
 * {@link org.openspotlight.graph.manipulation.GraphWriter#addBidirectionalLink} methods wich are the most common use.
 * </p>
 * <p>
 * Along with {@link org.openspotlight.graph.Node}, links are are the core of graph data model.
 * </p>
 * <b>Important Note</b> Its a abstract class to avoid more than one type of element for a node.
 * 
 * @author porcelli
 * @author feuteston
 */
public abstract class Link implements Element, Comparable<Link> {

    /**
     * Returns the count value.
     * 
     * @return the count value
     */
    public abstract int getCount();

    /**
     * This method returns the {@link LinkDirection}. But the {@link LinkDirection} itself could be promoted to
     * {@link LinkDirection#BIDIRECTIONAL} if the {@link Link} implementation is annotated with {@link LinkAutoBidirectional}
     * 
     * @return the LinkType
     */
    public abstract LinkDirection getLinkDirection();

    /**
     * Gets the raw Link Class. This is useful since the instances will be extended at runtime.
     * 
     * @return
     */
    public abstract Class<? extends Link> getLinkType();

    /**
     * A convenience operation that, given a node that is attached to this link, returns the other node. For example if node is a
     * source node, the target node will be returned, and vice versa.
     * 
     * @param node the source or target node of this links
     * @return the other node
     * @throws IllegalArgumentException if the given node is null or neither the source nor target node of this link
     */
    public abstract Node getOtherSide(
                                      Node node)
        throws IllegalArgumentException;

    /**
     * Returns the two nodes that are attached to this link.
     * 
     * @return nodes attached to this link
     */
    public abstract Node[] getSides();

    /**
     * Returns the source node of this link.
     * 
     * @return the source node
     */
    public abstract Node getSource();

    /**
     * Returns the target node of this link.
     * 
     * @return the target node
     */
    public abstract Node getTarget();

    /**
     * Checks if this link instance is bidirectional.
     * 
     * @return true if is bidirectional, false otherwise
     */
    public abstract boolean isBidirectional();

    /**
     * Sets the count value
     * 
     * @param value the count
     */
    public abstract void setCount(
                                  int value);
}
