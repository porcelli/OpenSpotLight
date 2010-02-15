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
import java.text.Collator;
import java.util.Collection;

import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.concurrent.NeedsSyncronizationCollection;
import org.openspotlight.common.concurrent.NeedsSyncronizationSet;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.log.LogableObject;
import org.openspotlight.remote.annotation.DisposeMethod;

/**
 * The Interface SLNode.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLNode extends Comparable<SLNode>, LogableObject,
		LockContainer {

	/**
	 * Adds the line reference.
	 * 
	 * @param startLine
	 *            the start line
	 * @param endLine
	 *            the end line
	 * @param startColumn
	 *            the start column
	 * @param endColumn
	 *            the end column
	 * @param statement
	 *            the statement
	 * @param artifactId
	 *            the artifact id
	 * @param artifactVersion
	 *            the artifact version
	 * @return the sL line reference
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public SLLineReference addLineReference(int startLine, int endLine,
			int startColumn, int endColumn, String statement,
			String artifactId, String artifactVersion)
			throws SLGraphSessionException, SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param linkTypesForLinkDeletion
	 *            the link types for link deletion
	 * @param linkTypesForLinkedNodeDeletion
	 *            the link types for linked node deletion
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			SLEncoder encoder) throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @param linkTypesForLinkDeletion
	 *            the link types for link deletion
	 * @param linkTypesForLinkedNodeDeletion
	 *            the link types for linked node deletion
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			SLEncoder encoder,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @param persistenceMode
	 *            the persistence mode
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			SLEncoder encoder, SLPersistenceMode persistenceMode)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @param persistenceMode
	 *            the persistence mode
	 * @param linkTypesForLinkDeletion
	 *            the link types for link deletion
	 * @param linkTypesForLinkedNodeDeletion
	 *            the link types for linked node deletion
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			SLEncoder encoder, SLPersistenceMode persistenceMode,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param persistenceMode
	 *            the persistence mode
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			SLPersistenceMode persistenceMode)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param persistenceMode
	 *            the persistence mode
	 * @param linkTypesForLinkDeletion
	 *            the link types for link deletion
	 * @param linkTypesForLinkedNodeDeletion
	 *            the link types for linked node deletion
	 * @return the t
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <T extends SLNode> T addNode(Class<T> clazz, String name,
			SLPersistenceMode persistenceMode,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param name
	 *            the name
	 * @return the sL node
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public SLNode addNode(String name) throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException;

	/**
	 * Adds the node.
	 * 
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @return the sL node
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public SLNode addNode(String name, SLEncoder encoder)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Do cast.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the t
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public <T extends SLNode> T doCast(Class<T> clazz)
			throws SLGraphSessionException;

	/**
	 * Gets the caption.
	 * 
	 * @return the caption
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public String getCaption();

	/**
	 * Gets the child node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @return the child node
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public <T extends SLNode> NeedsSyncronizationSet<T> getChildNodes(
			Class<T> clazz) throws SLGraphSessionException;

	/**
	 * Gets the context.
	 * 
	 * @return the context
	 */
	public SLContext getContext();

	/**
	 * Gets the iD.
	 * 
	 * @return the iD
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public String getID();

	/**
	 * Gets the line references.
	 * 
	 * @return the line references
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public NeedsSyncronizationCollection<SLLineReference> getLineReferences()
			throws SLGraphSessionException;

    /**
     * Gets the line references for a specific artifactId.
     * 
     * @param the artifact id
     * @return the line references
     * @throws SLGraphSessionException
     *             the SL graph session exception
     */
    public NeedsSyncronizationCollection<SLLineReference> getLineReferences(String artifactId)
            throws SLGraphSessionException;

    /**
	 * Gets the meta type.
	 * 
	 * @return the meta type or null if its a simple node
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public SLMetaNodeType getMetaType() throws SLGraphSessionException;

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public String getName();

	/**
	 * Gets the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @return the node
	 * @throws SLInvalidNodeTypeException
	 *             the SL invalid node type exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public <T extends SLNode> T getNode(Class<T> clazz, String name)
			throws SLInvalidNodeTypeException, SLGraphSessionException;

	/**
	 * Gets the node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @return the node
	 * @throws SLInvalidNodeTypeException
	 *             the SL invalid node type exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public <T extends SLNode> T getNode(Class<T> clazz, String name,
			SLEncoder encoder) throws SLInvalidNodeTypeException,
			SLGraphSessionException;

	/**
	 * Gets the node.
	 * 
	 * @param name
	 *            the name
	 * @return the node
	 * @throws SLInvalidNodeTypeException
	 *             the SL invalid node type exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public SLNode getNode(String name) throws SLInvalidNodeTypeException,
			SLGraphSessionException;

	/**
	 * Gets the nodes.
	 * 
	 * @return the nodes
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public NeedsSyncronizationSet<SLNode> getNodes()
			throws SLGraphSessionException;

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public SLNode getParent() throws SLGraphSessionException;

	/**
	 * Gets the properties.
	 * 
	 * @return the properties
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public NeedsSyncronizationSet<SLNodeProperty<Serializable>> getProperties()
			throws SLGraphSessionException;

	/**
	 * Gets the property.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @return the property
	 * @throws SLNodePropertyNotFoundException
	 *             the SL node property not found exception
	 * @throws SLInvalidNodePropertyTypeException
	 *             the SL invalid node property type exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public <V extends Serializable> SLNodeProperty<V> getProperty(
			Class<V> clazz, String name)
			throws SLNodePropertyNotFoundException,
			SLInvalidNodePropertyTypeException, SLGraphSessionException;

	/**
	 * Gets the property.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param collator
	 *            the collator
	 * @return the property
	 * @throws SLNodePropertyNotFoundException
	 *             the SL node property not found exception
	 * @throws SLInvalidNodePropertyTypeException
	 *             the SL invalid node property type exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public <V extends Serializable> SLNodeProperty<V> getProperty(
			Class<V> clazz, String name, Collator collator)
			throws SLNodePropertyNotFoundException,
			SLInvalidNodePropertyTypeException, SLGraphSessionException;

	/**
	 * Gets the property value as string.
	 * 
	 * @param name
	 *            the name
	 * @return the property value as string
	 * @throws SLNodePropertyNotFoundException
	 *             the SL node property not found exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public String getPropertyValueAsString(String name);

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	public SLGraphSession getSession();

	/**
	 * Gets line references in tree format.
	 * 
	 * @return the tree line references
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public SLTreeLineReference getTreeLineReferences()
			throws SLGraphSessionException;

    /**
     * Gets line references in tree format for a specific artifact.
     * 
     * @param the artifact id
     * @return the tree line references
     * @throws SLGraphSessionException
     *             the SL graph session exception
     */
    public SLTreeLineReference getTreeLineReferences(String artifactId)
            throws SLGraphSessionException;

    /**
	 * Gets the type name.
	 * 
	 * @return the type name
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	public String getTypeName() throws SLGraphSessionException;

	/**
	 * Removes the.
	 * 
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	@DisposeMethod
	public void remove() throws SLGraphSessionException,
			SLInvalidCredentialException;

	/**
	 * Sets the property.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @return the sL node property< v>
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialsException
	 *             the SL invalid credentials exception
	 */
	public <V extends Serializable> SLNodeProperty<V> setProperty(
			Class<V> clazz, String name, V value)
			throws SLGraphSessionException, SLInvalidCredentialException;

	/**
	 * Sets the property.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param visibility
	 *            the visibility
	 * @param name
	 *            the name
	 * @param value
	 *            the value
	 * @return the sL node property< v>
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidCredentialException
	 *             the SL invalid credential exception
	 */
	public <V extends Serializable> SLNodeProperty<V> setProperty(
			Class<V> clazz, VisibilityLevel visibility, String name, V value)
			throws SLGraphSessionException, SLInvalidCredentialException;
}
