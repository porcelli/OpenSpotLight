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
 * A node in the graph with properties, name, caption and its metadata information.
 *
 * As SLNode extends the {@link org.openspotlight.graph.SLElement} it has a unique identifier that is calculated based
 * on its composite key information: type, name and parent node.
 * The OpenSpotLight defined by {@link org.openspotlight.graph.annotation.SLDefineHierarchy) annotation.
 *
 * Together with {@link org.openspotlight.graph.SLLink } nodes are are the core of OpenSpotLight data model.
 *
 * Note: Its a abstract class to avoid more than one type of element in a node
 */
/**
 * A node in the graph with properties and relationships to other entities.
 * Along with {@link Relationship relationships}, nodes are the core building
 * blocks of the Neo4j data representation model. Nodes are created by invoking
 * the {@link GraphDatabaseService#createNode} method.
 * <p>
 * Node has three major groups of operations: operations that deal with
 * relationships, operations that deal with properties (see
 * {@link PropertyContainer}) and operations that create {@link Traverser
 * traversers}.
 * <p>
 * The relationship operations provide a number of overloaded accessors (such as
 * <code>getRelationships(...)</code> with "filters" for type, direction, etc),
 * as well as the factory method {@link #createRelationshipTo
 * createRelationshipTo(...)} that connects two nodes with a relationship. It
 * also includes the convenience method {@link #getSingleRelationship
 * getSingleRelationship(...)} for accessing the commonly occurring
 * one-to-zero-or-one association.
 * <p>
 * The property operations give access to the key-value property pairs. Property
 * keys are always strings. Valid property value types are all the Java
 * primitives (<code>int</code>, <code>byte</code>, <code>float</code>, etc),
 * <code>java.lang.String</code>s and arrays of primitives and Strings.
 * <p>
 * <b>Please note</b> that Neo4j does NOT accept arbitrary objects as property
 * values. {@link #setProperty(String, Object) setProperty()} takes a
 * <code>java.lang.Object</code> only to avoid an explosion of overloaded
 * <code>setProperty()</code> methods. For further documentation see
 * {@link PropertyContainer}.
 * <p>
 * The traversal factory methods instantiate a {@link Traverser traverser} that
 * starts traversing from this node.
 * <p>
 * A node's id is unique, but may not be unique over time since neo4j reuses
 * deleted ids. See <a href="http://wiki.neo4j.org/content/Id_Reuse">
 * wiki.neo4j.org/content/Id_Reuse</a>.
 */

public abstract class SLNode implements Comparable<SLNode>, LogableObject,
		SLElement {

	public abstract boolean isDirty();
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Gets the caption.
	 * 
	 * @return the caption
	 */
	public abstract String getCaption();

	/**
	 * Sets the caption.
	 * 
	 * @param caption
	 *            the caption
	 */
	public abstract void setCaption(String caption);

	/**
	 * Do cast.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T extends SLNode> T doCast(Class<T> clazz) {
		return (T) this;
	}
}