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

import java.util.Collection;

import org.openspotlight.graph.Context;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.Node;

/**
 * This interfaces has a list of method that can writes into graph server.
 * <p>
 * <b>Important Note:</b> All these operations are queued and executed when possible by graph engine (asynchronous), although this
 * mechanism, the consitency and durability are preserved by the engine.
 * 
 * @see org.openspotlight.graph.SimpleGraphSession
 * @see org.openspotlight.graph.FullGraphSession
 * @author porcelli
 * @author feuteston
 */
public interface GraphWriter {

    //TODO DO NOT FORGET TO USE THE ARTIFACT_ID DURRING CREATE METHODS

    /**
     * Sets the caption of a given context.
     * 
     * @param context the context
     * @param caption the caption
     * @throws IllegalArgumentException if any input param is null
     */
    void setContextCaption(Context context,
                            String caption)
        throws IllegalArgumentException;

    /**
     * Removes a context and all information (nodes and its links) inside. <br>
     * <b>Note</b> that this operation cannot be undone.
     * 
     * @param context the context
     * @throws IllegalArgumentException if the input param is null
     */
    void removeContext(Context context)
        throws IllegalArgumentException;

    /**
     * Adds a new node, based on the parameter node type, inside the parent context.<br>
     * <p>
     * If the added node already exists as transient, the transient is converted to a permanent node. <br>
     * <p>
     * In case that the new added node already exists but with a less specific type (high level abstraction of node type), the
     * already existed node type will be converted to the new type during graph server update. This mechanism is called type
     * promotion, and it is inspired by object-oriented common behavior.<br>
     * The oposite situation is also provided, if the new added node already exists but with a more specific type (lower type in
     * abstraction of the node type) the already existed node type will be preserved.
     * <p>
     * <b>Note</b> that if node already exists inside context its not duplicated and the new and already existent became the same.
     * 
     * @param <T> node type
     * @param context the target context
     * @param clazz the node type to be created
     * @param name the node name
     * @return the added node
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> T addNode(Context context,
                                Class<T> clazz,
                                String name)
        throws IllegalArgumentException;

    /**
     * Adds a new child node, based on the parameter node type, inside the parent context.<br>
     * <p>
     * If the added child node already exists as transient, the transient is converted to a permanent node. <br>
     * <p>
     * In case that the new added child node already exists but with a less specific type (high level abstraction of node type),
     * the already existed child node type will be converted to the new type during graph server update. This mechanism is called
     * type promotion, and it is inspired by object-oriented common behavior.<br>
     * The oposite situation is also provided, if the new added child node already exists but with a more specific type (lower
     * type in abstraction of the node type) the already existed child node type will be preserved.
     * <p>
     * <b>Note</b> that if child node already exists inside context its not duplicated and the new and already existent became the
     * same.
     * 
     * @param <T> node type
     * @param parent the parent node
     * @param clazz the node type to be created
     * @param name the node name
     * @return the added node
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> T addChildNode(Node parent,
                                     Class<T> clazz,
                                     String name)
        throws IllegalArgumentException;

    /**
     * Adds a new node, based on the parameter node type, inside the parent context.<br>
     * <p>
     * In case that the added node already exists its possible to reset its state by removing already connected nodes and links.<br>
     * The algorithm that executes this clean-up is fairly simple: based on a list of link types (first collection parameter) the
     * graph automatically tags to remove the links (of those types) related to the node (doesn't matter if its a source or target
     * node).<br>
     * A similar algorithm is executed to remove linked nodes: based on a list of link types (second collection parameter) all
     * nodes that are linked by those types to the node are tagged to be removed.<br>
     * The process that effectively removes the tagged data happens during {@link #flush()} method.<br>
     * <b>Important Note:</b> if any removed data (nodes or links) is created before the execution of {@link #flush()} method, the
     * graph untags the data wich preserves the original state of that data (ie. its properties)<br>
     * <p>
     * If the added node already exists as transient, the transient is converted to a permanent node. <br>
     * <p>
     * In case that the new added node already exists but with a less specific type (high level abstraction of node type), the
     * already existed node type will be converted to the new type during graph server update. This mechanism is called type
     * promotion, and it is inspired by object-oriented common behavior.<br>
     * The oposite situation is also provided, if the new added node already exists but with a more specific type (lower type in
     * abstraction of the node type) the already existed node type will be preserved.<br>
     * More on type promotions, check {@link Node}.
     * <p>
     * <b>Note</b> that if node already exists inside context its not duplicated and the new and already existent became the same.
     * 
     * @param <T> node type
     * @param context the target context
     * @param clazz the node type to be created
     * @param name the node name
     * @param linkTypesForLinkDeletion the list of link types used to tag links to be deleted
     * @param linkTypesForLinkedNodeDeletion the list of link types that defines wich linked nodes should be tagged to be deleted
     * @return the added node
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> T addNode(Context context,
                                Class<T> clazz,
                                String name,
                                Collection<Class<? extends Link>> linkTypesForLinkDeletion,
                                Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion)
        throws IllegalArgumentException;

    /**
     * Adds a new child node, based on the parameter node type, inside the parent context.<br>
     * <p>
     * In case that the added child node already exists its possible to reset its state by removing already connected nodes and
     * links.<br>
     * The algorithm that executes this clean-up is fairly simple: based on a list of link types (first collection parameter) the
     * graph automatically tags to remove the links (of those types) related to the node (doesn't matter if its a source or target
     * node).<br>
     * A similar algorithm is executed to remove linked nodes: based on a list of link types (second collection parameter) all
     * nodes that are linked by those types to the node are tagged to be removed.<br>
     * The process that effectively removes the tagged data happens during {@link #flush()} method.<br>
     * <b>Important Note:</b> if any removed data (nodes or links) is created before the execution of {@link #flush()} method, the
     * graph untags the data wich preserves the original state of that data (ie. its properties)<br>
     * <p>
     * If the added child node already exists as transient, the transient is converted to a permanent node. <br>
     * <p>
     * In case that the new added child node already exists but with a less specific type (high level abstraction of node type),
     * the already existed child node type will be converted to the new type during graph server update. This mechanism is called
     * type promotion, and it is inspired by object-oriented common behavior.<br>
     * The oposite situation is also provided, if the new added child node already exists but with a more specific type (lower
     * type in abstraction of the node type) the already existed child node type will be preserved.
     * <p>
     * <b>Note</b> that if child node already exists inside context its not duplicated and the new and already existent became the
     * same.
     * 
     * @param <T> node type
     * @param parent the parent node
     * @param clazz the node type to be created
     * @param name the node name
     * @param linkTypesForLinkDeletion the list of link types used to tag links to be deleted
     * @param linkTypesForLinkedNodeDeletion the list of link types that defines wich linked nodes should be tagged to be deleted
     * @return the added node
     * @throws IllegalArgumentException if any input param is null
     */
    <T extends Node> T addChildNode(Node parent,
                                     Class<T> clazz,
                                     String name,
                                     Collection<Class<? extends Link>> linkTypesForLinkDeletion,
                                     Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion)
        throws IllegalArgumentException;

    /**
     * Copies all the node hierarchy (parents and children) to the target context.<br>
     * <b>Note</b> that this operation cannot be undone.
     * 
     * @param node the node
     * @param target the target context
     * @throws IllegalArgumentException if any input param is null
     */
    void copyNodeHierarchy(Node node,
                            Context target)
        throws IllegalArgumentException;

    /**
     * Moves (copy and remove) all the node hierarchy (parents and children) to the target context. <br>
     * <b>Note</b> that this operation cannot be undone.
     * 
     * @param node the node
     * @param target the target context
     * @throws IllegalArgumentException if any input param is null
     */
    void moveNodeHierarchy(Node node,
                            Context target)
        throws IllegalArgumentException;

    /**
     * Removes the node and all its children and any link that its associated. <br>
     * <b>Note</b> that this operation cannot be undone.
     * 
     * @param node the node to be removed
     * @throws IllegalArgumentException if the input param is null
     */
    void removeNode(Node node)
        throws IllegalArgumentException;

    /**
     * Adds an unidirectional link between the source and target nodes with the specified link type.
     * <p>
     * If the link type is marked with {@link org.openspotlight.graph.annotation.LinkAutoBidirectional} annotation and the link
     * already exists between target and source (target -> source), its automatically converted to a bidirectional link.
     * <p>
     * <b>Note</b> that if link already exists its not duplicated.
     * 
     * @param <L> link type
     * @param linkClass the link type to be created
     * @param source the source node
     * @param target the target node
     * @return the added link
     * @throws IllegalArgumentException if any input param is null
     */
    <L extends Link> L addLink(Class<L> linkClass,
                                   Node source,
                                   Node target)
        throws IllegalArgumentException;

    /**
     * Adds a bidirectional link between nodes with the specified link type. <br>
     * <b>Note</b> that if link already exists its not duplicated.
     * 
     * @param <L> link type
     * @param linkClass the link type to be created
     * @param nodea the node
     * @param nodeb the node
     * @return the added link
     * @throws IllegalArgumentException if any input param is null
     */
    <L extends Link> L addBidirectionalLink(Class<L> linkClass,
                                                Node nodea,
                                                Node nodeb)
        throws IllegalArgumentException;

    /**
     * Removes the link<br>
     * <b>Note</b> that this operation cannot be undone.
     * 
     * @param link the link
     * @throws IllegalArgumentException if the input param is null
     */
    void removeLink(Link link)
        throws IllegalArgumentException;

    /**
     * Executes a general data flush on pending updates as well process all tagged data to be removed (more information about this
     * tag: {@link #addNode(Context, Class, String, Collection, Collection)} or
     * {@link #addChildNode(Node, Class, String, Collection, Collection)} )
     * <p>
     * <b>Note</b> that this method can be considered a lifeclycle method, that "marks" an update cycle.<br>
     * <b>Important Note:</b> all updates into grah server are executed by queueing the commands, wich means that any updates into
     * graph are executed asynchronously due performance reasons.
     */
    void flush();
}
