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
package org.openspotlight.graph;

import org.openspotlight.log.LogableObject;

/**
 * The Node is the base data structure that enables represent any information into OpenSpotLight Graph. Any information can be
 * modeled as a Node using its unique identifiers or properties. <br>
 * A Node is uniquely identified by three properties: Type, Name and ParentNode, and based on these data an algorithm is used to
 * generate an unique id.
 * <p>
 * To secure the data consistency its not possible change the unique identifiers of a Node. If you need so, you'll have to delete
 * it and create a new one.
 * </p>
 * <p>
 * A Node defines a information, to relate this data with other you'll have to create {@link Link} to connect those nodes.
 * </p>
 * <p>
 * The Node type can be promoted to a more specific type and to define the behavior of type promotion and its hierarchy its
 * necessary to use annotate the top node type on the hierarchy with {@link org.openspotlight.graph.annotation.DefineHierarchy}
 * annotation. <br>
 * The promotion of the node types are automatically handled by OpenSpotLight Graph engine.
 * </p>
 * <p>
 * Nodes can be created as transients by {@link org.openspotlight.graph.manipulation.GraphTransientWriter#createTransientNode}
 * method or as permanent by {@link org.openspotlight.graph.manipulation.GraphWriter#createNode(Node, Class, String)} or
 * {@link org.openspotlight.graph.manipulation.GraphWriter#createNode(Node, Class, String, java.util.Collection, java.util.Collection)}
 * methods wich are the most common use.
 * </p>
 * <p>
 * Along with {@link org.openspotlight.graph.Link}, nodes are are the core of OpenSpotLight Graph data model.
 * </p>
 * <p>
 * <b>Important Notes:</b><br>
 * &nbsp;1. Its a abstract class to avoid more than one type of element for a node.<br>
 * &nbsp;2. The same node (uniquely identified by Type, Name and Parent) can be stored in more than one
 * {@link org.openspotlight.graph.Context}.
 * </p>
 * 
 * @author porcelli
 * @author feuteston
 */
public abstract class Node implements Element, Comparable<Node>, LogableObject {

    /**
     * Returns the name. <br>
     * The name is one of the three properties ({@link Node#getName}, {@link Node#getParentId} and {@link Node#getTypeName}) that
     * defines uniquely the node.
     * 
     * @return the name
     */
    public abstract String getName();

    /**
     * Returns the caption.
     * 
     * @return the caption
     */
    public abstract String getCaption();

    /**
     * Sets the caption. Caption is just a simple form to identify (not uniquely) the node.
     * 
     * @param caption the caption
     */
    public abstract void setCaption( String caption );

    /**
     * Returns the contextId where the node is stored.
     * <p>
     * <b>Note</b> the context is not directly exposed due performance issues.
     * 
     * @return the contextId
     */
    public abstract String getContextId();

    /**
     * Returns the parent's node id. <br>
     * The parentId is one of the three properties ({@link Node#getName}, {@link Node#getParentId} and {@link Node#getTypeName})
     * that defines uniquely the node.
     * <p>
     * <b>Note</b> the parent node is not directly exposed due performance issues.
     * 
     * @return the parent id
     */
    public abstract String getParentId();
}
