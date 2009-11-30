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
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.util.ProxyUtil;
import org.openspotlight.security.authz.Action;
import org.openspotlight.security.authz.EnforcementContext;
import org.openspotlight.security.authz.EnforcementException;
import org.openspotlight.security.authz.EnforcementResponse;
import org.openspotlight.security.authz.graph.GraphElement;

/**
 * The Class SLNodeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLNodeImpl implements SLNode, SLPNodeGetter {
	private final Object lock;
	/** The context. */
	private final SLContext context;

	/** The parent. */
	private final SLNode parent;

	/** The p node. */
	private final SLPersistentNode pNode;

	/** The event poster. */
	private final SLGraphSessionEventPoster eventPoster;

	/**
	 * Instantiates a new sL node impl.
	 * 
	 * @param context
	 *            the context
	 * @param parent
	 *            the parent
	 * @param persistentNode
	 *            the persistent node
	 * @param eventPoster
	 *            the event poster
	 */
	public SLNodeImpl(final SLContext context, final SLNode parent,
			final SLPersistentNode persistentNode,
			final SLGraphSessionEventPoster eventPoster) {
		this.context = context;
		this.parent = parent;
		this.pNode = persistentNode;
		this.eventPoster = eventPoster;
		this.lock = persistentNode.getLockObject();
	}

	/**
	 * Adds the child node.
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
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	private <T extends SLNode> T addChildNode(
			final Class<T> clazz,
			final String name,
			final SLEncoder encoder,
			final SLPersistenceMode persistenceMode,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLGraphSessionException, SLInvalidCredentialException {
		try {

			if (!this.hasPrivileges(GraphElement.NODE, Action.WRITE)) {
				throw new SLInvalidCredentialException(
						"User does not have privilegies to add nodes.");
			}

			Class<T> type = null;
			final String encodedName = encoder.encode(name);
			SLPersistentNode pChildNode = this.getHierarchyChildNode(clazz,
					name, encodedName);
			if (pChildNode == null) {
				type = clazz;
				pChildNode = this.pNode.addNode(encodedName);
				SLCommonSupport.setInternalStringProperty(pChildNode,
						SLConsts.PROPERTY_NAME_DECODED_NAME, name);
			} else {
				final Class<? extends SLNode> nodeType = this
						.getNodeType(pChildNode);
				type = this.getLessGenericNodeType(clazz, nodeType);
			}
			final String typeName = SLCommonSupport
					.getInternalPropertyAsString(pChildNode,
							SLConsts.PROPERTY_NAME_TYPE);
			if (typeName == null || !typeName.equals(type.getName())) {
				SLCommonSupport.setInternalStringProperty(pChildNode,
						SLConsts.PROPERTY_NAME_TYPE, type.getName());
			}

			final T nodeProxy = this.createNodeProxy(type, pChildNode);
			this.eventPoster.post(new SLNodeEvent(SLNodeEvent.TYPE_NODE_ADDED,
					nodeProxy, pChildNode, persistenceMode,
					linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion));
			return nodeProxy;
		} catch (final SLException e) {
			throw new SLGraphSessionException("Error on attempt to add node.",
					e);
		}
	}

	/**
	 * Adds the child nodes.
	 * 
	 * @param childNodes
	 *            the child nodes
	 * @param node
	 *            the node
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	private void addChildNodes(final Collection<SLNode> childNodes,
			final SLNode node) throws SLGraphSessionException {
		final Collection<SLNode> nodes = node.getNodes();
		for (final SLNode current : nodes) {
			this.addChildNodes(childNodes, current);
		}
		childNodes.add(node);
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addLineReference(int, int, int, int,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public SLLineReference addLineReference(final int startLine,
			final int endLine, final int startColumn, final int endColumn,
			final String statement, final String artifactId,
			final String artifactVersion) throws SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {

			try {

				if (!this.hasPrivileges(GraphElement.LINE_REFERENCE,
						Action.WRITE)) {
					throw new SLInvalidCredentialException(
							"User does not have privilegies to line references.");
				}

				final StringBuilder lineReferenceKey = new StringBuilder()
						.append(startLine).append('.').append(endLine).append(
								'.').append(startColumn).append('.').append(
								endColumn).append('.').append(statement)
						.append(artifactId).append('.').append(artifactVersion);

				final SLEncoderFactory factory = this.getSession()
						.getEncoderFactory();
				final SLEncoder fakeEncoder = factory.getFakeEncoder();
				final SLEncoder uuidEncoder = factory.getUUIDEncoder();

				final String propName = "lineRef."
						+ uuidEncoder.encode(lineReferenceKey.toString());
				final SLLineReference lineRef = this.addChildNode(
						SLLineReference.class, propName, fakeEncoder,
						SLPersistenceMode.NORMAL, null, null);

				lineRef.setStartLine(startLine);
				lineRef.setEndLine(endLine);
				lineRef.setStartColumn(startColumn);
				lineRef.setEndColumn(endColumn);
				lineRef.setStatement(statement);
				lineRef.setArtifactId(artifactId);
				lineRef.setArtifactVersion(artifactVersion);

				return lineRef;
			} catch (final SLGraphSessionException e) {
				throw new SLGraphSessionException(
						"Error on attempt to add line reference.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String)
	 */
	public <T extends SLNode> T addNode(final Class<T> clazz, final String name)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, this.getSession()
					.getDefaultEncoder(), SLPersistenceMode.NORMAL, null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, java.util.Collection, java.util.Collection)
	 */
	public <T extends SLNode> T addNode(
			final Class<T> clazz,
			final String name,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, this.getSession()
					.getDefaultEncoder(), SLPersistenceMode.NORMAL,
					linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLEncoder)
	 */
	public <T extends SLNode> T addNode(final Class<T> clazz,
			final String name, final SLEncoder encoder)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, encoder,
					SLPersistenceMode.NORMAL, null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLEncoder,
	 * java.util.Collection, java.util.Collection)
	 */
	public <T extends SLNode> T addNode(
			final Class<T> clazz,
			final String name,
			final SLEncoder encoder,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, this.getSession()
					.getDefaultEncoder(), SLPersistenceMode.NORMAL,
					linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLEncoder,
	 * org.openspotlight.graph.SLPersistenceMode)
	 */
	public <T extends SLNode> T addNode(final Class<T> clazz,
			final String name, final SLEncoder encoder,
			final SLPersistenceMode persistenceMode)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, encoder, persistenceMode,
					null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLEncoder,
	 * org.openspotlight.graph.SLPersistenceMode, java.util.Collection,
	 * java.util.Collection)
	 */
	public <T extends SLNode> T addNode(
			final Class<T> clazz,
			final String name,
			final SLEncoder encoder,
			final SLPersistenceMode persistenceMode,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, encoder, persistenceMode,
					linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLPersistenceMode)
	 */
	public <T extends SLNode> T addNode(final Class<T> clazz,
			final String name, final SLPersistenceMode persistenceMode)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, this.getSession()
					.getDefaultEncoder(), persistenceMode, null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLPersistenceMode,
	 * java.util.Collection, java.util.Collection)
	 */
	public <T extends SLNode> T addNode(
			final Class<T> clazz,
			final String name,
			final SLPersistenceMode persistenceMode,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(clazz, name, this.getSession()
					.getDefaultEncoder(), persistenceMode,
					linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.String)
	 */
	public SLNode addNode(final String name)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(SLNode.class, name, this.getSession()
					.getDefaultEncoder(), SLPersistenceMode.NORMAL, null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#addNode(java.lang.String,
	 * org.openspotlight.graph.SLEncoder)
	 */
	public SLNode addNode(final String name, final SLEncoder encoder)
			throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			return this.addChildNode(SLNode.class, name, encoder,
					SLPersistenceMode.NORMAL, null, null);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final SLNode node) {
		synchronized (this.lock) {
			try {
				final SLNodeInvocationHandler handler = (SLNodeInvocationHandler) Proxy
						.getInvocationHandler(node);
				final SLNodeImpl n = (SLNodeImpl) handler.getNode();
				return this.pNode.getPath().compareTo(n.pNode.getPath());
			} catch (final SLException e) {
				throw new SLRuntimeException(
						"Error on attempt to compare nodes.", e);
			}
		}
	}

	/**
	 * Creates the node proxy.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param pNode
	 *            the node
	 * @return the t
	 */
	public <T extends SLNode> T createNodeProxy(final Class<T> clazz,
			final SLPersistentNode pNode) {
		synchronized (this.lock) {
			final SLNode node = new SLNodeImpl(this.context, this, pNode,
					this.eventPoster);
			final InvocationHandler handler = new SLNodeInvocationHandler(node);
			return ProxyUtil.createProxy(clazz, handler);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		synchronized (this.lock) {
			try {
				if (obj == null || !(obj instanceof SLNode)) {
					return false;
				}
				final SLPersistentNode pNode = SLCommonSupport
						.getPNode((SLNode) obj);
				return this.pNode.getID().equals(pNode.getID());
			} catch (final SLException e) {
				throw new RuntimeException("Error on " + this.getClass()
						+ " equals method.", e);
			}
		}
	}

	/**
	 * Gets the child node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @return the child node
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	<T extends SLNode> T getChildNode(final Class<T> clazz, final String name)
			throws SLGraphSessionException {
		return this.getChildNode(clazz, name, this.getSession()
				.getDefaultEncoder());
	}

	/**
	 * Gets the child node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param name
	 *            the name
	 * @param encoder
	 *            the encoder
	 * @return the child node
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	<T extends SLNode> T getChildNode(final Class<T> clazz, final String name,
			final SLEncoder encoder) throws SLGraphSessionException {
		try {
			T proxyNode = null;
			final String nodeName = encoder.encode(name);
			Collection<SLPersistentNode> pChildNodes = this.pNode
					.getNodes(nodeName);
			for (final SLPersistentNode pChildNode : pChildNodes) {
				final Class<? extends SLNode> nodeType = this
						.getNodeType(pChildNode);
				// if classes are of same hierarchy ...
				if (this.nodeTypesOfSameHierarchy(clazz, nodeType)) {
					// gets the less generic type ...
					final Class<? extends T> lessGenericNodeType = this
							.getLessGenericNodeType(clazz, nodeType);
					final SLNode node = new SLNodeImpl(this.getContext(), this,
							pChildNode, this.eventPoster);
					final InvocationHandler handler = new SLNodeInvocationHandler(
							node);
					proxyNode = ProxyUtil.createProxy(lessGenericNodeType,
							handler);
					break;
				}
			}

			// try collator if necessary ...
			if (proxyNode == null
					&& !SLCollatorSupport.isCollatorStrengthIdentical(clazz)) {
				pChildNodes = this.pNode.getNodes();
				final Collator collator = SLCollatorSupport
						.getNodeCollator(clazz);
				for (final SLPersistentNode pChildNode : pChildNodes) {
					// if collator filter succeeds ...
					final String currentDecodedName = SLCommonSupport
							.getUserNodeName(pChildNode);
					if (collator.compare(name, currentDecodedName) == 0) {
						final Class<? extends SLNode> nodeType = this
								.getNodeType(pChildNode);
						// if classes are of same hierarchy ...
						if (this.nodeTypesOfSameHierarchy(clazz, nodeType)) {
							// gets the less generic type ...
							final Class<? extends T> lessGenericNodeType = this
									.getLessGenericNodeType(clazz, nodeType);
							final SLNode node = new SLNodeImpl(this
									.getContext(), this, pChildNode,
									this.eventPoster);
							final InvocationHandler handler = new SLNodeInvocationHandler(
									node);
							proxyNode = ProxyUtil.createProxy(
									lessGenericNodeType, handler);
							break;
						}
					}
				}
			}
			return proxyNode;
		} catch (final Exception e) {
			throw new SLGraphSessionException("Error on attempt to get node.",
					e);
		}
	}

	/**
	 * Gets the child nodes.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return the child nodes
	 * @throws SLException
	 *             the SL exception
	 */
	private Set<SLNode> getChildNodes(final String pattern) throws SLException {
		final SLGraphFactory factory = AbstractFactory
				.getDefaultInstance(SLGraphFactory.class);
		final Set<SLNode> childNodes = new HashSet<SLNode>();
		final Collection<SLPersistentNode> persistentChildNodes = pattern == null ? this.pNode
				.getNodes()
				: this.pNode.getNodes(pattern);
		for (final SLPersistentNode persistentChildNode : persistentChildNodes) {
			final SLNode childNode = factory.createNode(this.getContext(),
					this, persistentChildNode, this.eventPoster);
			final SLNodeInvocationHandler handler = new SLNodeInvocationHandler(
					childNode);
			final Class<? extends SLNode> nodeType = this
					.getNodeType(persistentChildNode);
			final SLNode childNodeProxy = ProxyUtil.createProxy(nodeType,
					handler);
			childNodes.add(childNodeProxy);
		}
		return childNodes;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getContext()
	 */
	public SLContext getContext() {
		return this.context;
	}

	/**
	 * Gets the hierarchy child node.
	 * 
	 * @param clazz
	 *            the clazz
	 * @param decodedName
	 *            the decoded name
	 * @param encodedName
	 *            the encoded name
	 * @return the hierarchy child node
	 * @throws SLException
	 *             the SL exception
	 */
	private SLPersistentNode getHierarchyChildNode(
			final Class<? extends SLNode> clazz, final String decodedName,
			final String encodedName) throws SLException {

		SLPersistentNode pChildNode = null;
		final String nodeName = encodedName == null ? decodedName : encodedName;
		final Collection<SLPersistentNode> pChildNodes = this.pNode
				.getNodes(nodeName);
		for (final SLPersistentNode current : pChildNodes) {
			final Class<? extends SLNode> nodeType = this.getNodeType(current);
			if (this.nodeTypesOfSameHierarchy(clazz, nodeType)) {
				pChildNode = current;
				break;
			}
		}

		if (pChildNode == null
				&& !SLCollatorSupport.isCollatorStrengthIdentical(clazz)) {
			final Collator collator = SLCollatorSupport.getNodeCollator(clazz);
			for (final SLPersistentNode current : this.pNode.getNodes()) {
				final String currentDecodedName = SLCommonSupport
						.getUserNodeName(current);
				if (collator.compare(decodedName, currentDecodedName) == 0) {
					final Class<? extends SLNode> nodeType = this
							.getNodeType(current);
					if (this.nodeTypesOfSameHierarchy(clazz, nodeType)) {
						pChildNode = current;
						break;
					}
				}
			}
		}

		return pChildNode;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getID()
	 */
	public String getID() throws SLGraphSessionException {
		try {
			return this.pNode.getID();
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException(
					"Error on attempt to retrieve the node ID.", e);
		}
	}

	/**
	 * Gets the less generic node type.
	 * 
	 * @param type1
	 *            the type1
	 * @param type2
	 *            the type2
	 * @return the less generic node type
	 */
	@SuppressWarnings("unchecked")
	private <T extends SLNode> Class<T> getLessGenericNodeType(
			final Class<T> type1, final Class<? extends SLNode> type2) {
		return (Class<T>) (type1.isAssignableFrom(type2) ? type2 : type1);
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getLineReferences()
	 */
	public Collection<SLLineReference> getLineReferences()
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final Collection<SLLineReference> lineReferences = new ArrayList<SLLineReference>();
				final Collection<SLNode> nodes = this
						.getChildNodes("lineRef.*");
				for (final SLNode node : nodes) {
					final SLLineReference lineRef = SLLineReference.class
							.cast(node);
					lineReferences.add(lineRef);
				}
				return lineReferences;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve line references.", e);
			}
		}
	}

	public Object getLockObject() {
		return this.lock;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getName()
	 */
	public String getName() throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final String decodedName = SLCommonSupport
						.getInternalPropertyAsString(this.pNode,
								SLConsts.PROPERTY_NAME_DECODED_NAME);
				return decodedName == null ? this.pNode.getName() : decodedName;
			} catch (final SLPersistentTreeSessionException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve the node name.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getNode(java.lang.Class,
	 * java.lang.String)
	 */
	public <T extends SLNode> T getNode(final Class<T> clazz, final String name)
			throws SLInvalidNodeTypeException, SLGraphSessionException {
		synchronized (this.lock) {
			return this.getChildNode(clazz, name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getNode(java.lang.Class,
	 * java.lang.String, org.openspotlight.graph.SLEncoder)
	 */
	public <T extends SLNode> T getNode(final Class<T> clazz,
			final String name, final SLEncoder encoder)
			throws SLInvalidNodeTypeException, SLGraphSessionException {
		synchronized (this.lock) {
			return this.getChildNode(clazz, name, encoder);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getNode(java.lang.String)
	 */
	public SLNode getNode(final String name) throws SLInvalidNodeTypeException,
			SLGraphSessionException {
		synchronized (this.lock) {
			return this.getChildNode(SLNode.class, name);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getNodes()
	 */
	public Set<SLNode> getNodes() throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				return this.getChildNodes(null);
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve child nodes.", e);
			}
		}
	}

	/**
	 * Gets the node type.
	 * 
	 * @param pNode
	 *            the node
	 * @return the node type
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends SLNode> getNodeType(final SLPersistentNode pNode)
			throws SLGraphSessionException {
		try {
			Class<? extends SLNode> type = null;
			final String propName = SLCommonSupport
					.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
			final SLPersistentProperty<String> typeNameProp = SLCommonSupport
					.getProperty(pNode, String.class, propName);
			if (typeNameProp != null) {
				type = (Class<? extends SLNode>) Class.forName(typeNameProp
						.getValue());
			}
			return type == null ? SLNode.class : type;
		} catch (final Exception e) {
			throw new SLGraphSessionException(
					"Error on attempt to retrieve node type.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getParent()
	 */
	public SLNode getParent() throws SLGraphSessionException {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLPNodeGetter#getPNode()
	 */
	public SLPersistentNode getPNode() {
		return this.pNode;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getProperties()
	 */
	public Set<SLNodeProperty<Serializable>> getProperties()
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final Class<? extends SLNode> nodeType = this
						.getNodeType(this.pNode);
				final SLGraphFactory factory = AbstractFactory
						.getDefaultInstance(SLGraphFactory.class);
				final Set<SLNodeProperty<Serializable>> properties = new HashSet<SLNodeProperty<Serializable>>();
				final Set<SLPersistentProperty<Serializable>> persistentProperties = this.pNode
						.getProperties(SLConsts.PROPERTY_PREFIX_USER + ".*");
				for (final SLPersistentProperty<Serializable> persistentProperty : persistentProperties) {
					final SLNode nodeProxy = ProxyUtil.createNodeProxy(
							nodeType, this);
					final SLNodeProperty<Serializable> property = factory
							.createProperty(nodeProxy, persistentProperty,
									this.eventPoster);
					properties.add(property);
				}
				return properties;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve node properties.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getProperty(java.lang.Class,
	 * java.lang.String)
	 */
	public <V extends Serializable> SLNodeProperty<V> getProperty(
			final Class<V> clazz, final String name)
			throws SLNodePropertyNotFoundException,
			SLInvalidNodePropertyTypeException, SLGraphSessionException {
		synchronized (this.lock) {
			return this.getProperty(clazz, name, null);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getProperty(java.lang.Class,
	 * java.lang.String, java.text.Collator)
	 */
	public <V extends Serializable> SLNodeProperty<V> getProperty(
			final Class<V> clazz, final String name, final Collator collator)
			throws SLNodePropertyNotFoundException,
			SLInvalidNodePropertyTypeException, SLGraphSessionException {
		synchronized (this.lock) {

			SLNodeProperty<V> property = null;

			try {

				final String propName = SLCommonSupport
						.toUserPropertyName(name);
				SLPersistentProperty<V> pProperty = SLCommonSupport
						.getProperty(this.pNode, clazz, propName);
				final Class<? extends SLNode> nodeType = this
						.getNodeType(this.pNode);

				// if property not found find collator if its strength is not
				// identical ...
				if (pProperty == null) {
					if (nodeType != null) {
						final Set<SLPersistentProperty<Serializable>> pProperties = this.pNode
								.getProperties(SLConsts.PROPERTY_PREFIX_USER
										+ ".*");
						for (final SLPersistentProperty<Serializable> current : pProperties) {
							final String currentName = SLCommonSupport
									.toSimplePropertyName(current.getName());
							final Collator currentCollator = collator == null ? SLCollatorSupport
									.getPropertyCollator(nodeType, currentName)
									: collator;
							if (currentCollator.compare(name, currentName) == 0) {
								pProperty = this.pNode.getProperty(clazz,
										current.getName());
								break;
							}
						}
					}
				}

				if (pProperty != null) {
					final SLGraphFactory factory = AbstractFactory
							.getDefaultInstance(SLGraphFactory.class);
					final SLNode nodeProxy = ProxyUtil.createNodeProxy(
							nodeType, this);
					property = factory.createProperty(nodeProxy, pProperty,
							this.eventPoster);
				}
			} catch (final SLInvalidPersistentPropertyTypeException e) {
				throw new SLInvalidNodePropertyTypeException(e);
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve node property.", e);
			}

			if (property == null) {
				throw new SLNodePropertyNotFoundException(name);
			}
			return property;
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLNode#getPropertyValueAsString(java.lang.String)
	 */
	public String getPropertyValueAsString(final String name)
			throws SLNodePropertyNotFoundException, SLGraphSessionException {
		synchronized (this.lock) {
			return this.getProperty(Serializable.class, name).getValue()
					.toString();
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#getSession()
	 */
	public SLGraphSession getSession() {
		return this.context.getSession();
	}

	/**
	 * {@inheritDoc}
	 */
	public SLTreeLineReference getTreeLineReferences()
			throws SLGraphSessionException {
		synchronized (this.lock) {
			return new SLTreeLineReferenceImpl(this.getID(), this
					.getLineReferences());
		}
	}

	public String getTypeName() throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				return SLCommonSupport.getInternalPropertyAsString(this.pNode,
						SLConsts.PROPERTY_NAME_TYPE);
			} catch (final SLPersistentTreeSessionException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve node type name.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		try {
			return this.getID().hashCode();
		} catch (final SLGraphSessionException e) {
			throw new SLRuntimeException(
					"Error on attempt to calculate node hash code.", e);
		}
	}

	/**
	 * Checks for privileges.
	 * 
	 * @param element
	 *            the element
	 * @param action
	 *            the action
	 * 
	 * @return true, if successful
	 */
	private boolean hasPrivileges(final GraphElement element,
			final Action action) {
		final EnforcementContext enforcementContext = new EnforcementContext();
		enforcementContext.setAttribute("user", this.getSession().getUser());
		enforcementContext.setAttribute("graphElement", element);
		enforcementContext.setAttribute("action", action);
		enforcementContext.setAttribute("node", this);

		try {
			final EnforcementResponse response = this.getSession()
					.getPolicyEnforcement().checkAccess(enforcementContext);
			if (response.equals(EnforcementResponse.GRANTED)) {
				return true;
			}
			return false;
		} catch (final EnforcementException e) {
			Exceptions.catchAndLog(e);
			return false;
		}
	}

	/**
	 * Node types of same hierarchy.
	 * 
	 * @param type1
	 *            the type1
	 * @param type2
	 *            the type2
	 * @return true, if successful
	 */
	private boolean nodeTypesOfSameHierarchy(
			final Class<? extends SLNode> type1,
			final Class<? extends SLNode> type2) {
		return type1.isAssignableFrom(type2) || type2.isAssignableFrom(type2);
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#remove()
	 */
	public void remove() throws SLGraphSessionException,
			SLInvalidCredentialException {
		synchronized (this.lock) {
			try {
				if (!this.hasPrivileges(GraphElement.NODE, Action.DELETE)) {
					throw new SLInvalidCredentialException(
							"User does not have privilegies to delete nodes.");
				}

				final Collection<SLNode> childNodes = new ArrayList<SLNode>();
				final Iterator<SLNode> iter = this.getNodes().iterator();
				while (iter.hasNext()) {
					this.addChildNodes(childNodes, iter.next());
				}
				for (final SLNode current : childNodes) {
					final Collection<SLLink> links = this.getSession()
							.getLinks(current, null);
					for (final SLLink link : links) {
						link.remove();
					}
				}
				this.pNode.remove();
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to remove node.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLNode#setProperty(java.lang.Class,
	 * java.lang.String, java.io.Serializable)
	 */
	public <V extends Serializable> SLNodeProperty<V> setProperty(
			final Class<V> clazz, final String name, final V value)
			throws SLGraphSessionException, SLInvalidCredentialException {
		synchronized (this.lock) {
			try {
				final String propName = SLCommonSupport
						.toUserPropertyName(name);
				final SLPersistentProperty<V> pProperty = this.pNode
						.setProperty(clazz, propName, value);
				final SLGraphFactory factory = AbstractFactory
						.getDefaultInstance(SLGraphFactory.class);
				final Class<? extends SLNode> nodeType = this
						.getNodeType(this.pNode);
				final SLNode nodeProxy = ProxyUtil.createNodeProxy(nodeType,
						this);
				final SLNodeProperty<V> property = factory.createProperty(
						nodeProxy, pProperty, this.eventPoster);
				final SLNodePropertyEvent event = new SLNodePropertyEvent(
						SLNodePropertyEvent.TYPE_NODE_PROPERTY_SET, property,
						pProperty);
				this.eventPoster.post(event);
				return property;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to set property.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		synchronized (this.lock) {
			try {
				final StringBuilder sb = new StringBuilder();
				sb.append(this.getName());
				sb.append("\n\t");
				sb.append("ID:");
				sb.append(this.getID());
				sb.append("\n\t");
				for (final SLNodeProperty<Serializable> activeProperty : this
						.getProperties()) {
					sb.append(activeProperty.getName());
					sb.append(":");
					sb.append(activeProperty.getValueAsString());
					sb.append("\n\t");
				}
				return sb.toString();
			} catch (final SLGraphSessionException e) {
			}
			return this.pNode.toString();
		}
	}
}
