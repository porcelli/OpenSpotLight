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
package org.openspotlight.graph.persistence;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import org.apache.log4j.Logger;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.JCRUtil;
import org.openspotlight.jcr.provider.SessionWithLock;

/**
 * The Class SLPersistentTreeSessionImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLPersistentTreeSessionImpl implements SLPersistentTreeSession {

	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger
			.getLogger(SLPersistentTreeSessionImpl.class);

	/** The jcr session. */
	private final SessionWithLock jcrSession;

	private final Object lock;

	/** The event poster. */
	private final SLPersistentEventPoster eventPoster;

	/** The root node. */
	private Node rootNode;

	/** The repository name. */
	private final String repositoryName;

	private final String xpathRootPath;

	/**
	 * Instantiates a new sL persistent tree session impl.
	 * 
	 * @param session
	 *            the session
	 */
	public SLPersistentTreeSessionImpl(final SessionWithLock session,
			final String repositoryName) {
		this.jcrSession = session;
		this.lock = session.getLockObject();
		final SLPersistentEventListener listener = new SLPersistentEventListenerImpl();
		this.eventPoster = new SLPersistentEventPosterImpl(listener);
		this.repositoryName = repositoryName;
		this.xpathRootPath = "//" + SharedConstants.DEFAULT_JCR_ROOT_NAME + "/"
				+ repositoryName;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#clear()
	 */
	public void clear() throws SLPersistentTreeSessionException {
		synchronized (this.lock) {
			if (this.rootNode != null) {
				try {
					final NodeIterator iter = this.rootNode.getNodes();
					while (iter.hasNext()) {
						final Node node = iter.nextNode();
						node.remove();
					}

				} catch (final RepositoryException e) {
					throw new SLPersistentTreeSessionException(
							"Error on attempt to clear the persistent tree session.",
							e);
				}
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#close()
	 */
	public void close() {
		this.jcrSession.logout();
	}

	/**
	 * Creates the product root node.
	 * 
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private Node createProductRootNode() throws RepositoryException {

		return this.jcrSession.getRootNode().addNode(
				SharedConstants.DEFAULT_JCR_ROOT_NAME);
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.persistence.SLPersistentTreeSession#createQuery
	 * (java.lang.String, int)
	 */
	public SLPersistentQuery createQuery(final String statement, final int type)
			throws SLPersistentTreeSessionException {
		synchronized (this.lock) {
			return new SLPersistentQueryImpl(this, this.jcrSession, statement,
					type);
		}
	}

	/**
	 * Creates the repository root node.
	 * 
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private void createRepositoryRootNode(final Node repositoryRootNode)
			throws RepositoryException {

		this.rootNode = repositoryRootNode.addNode(this.repositoryName);
		JCRUtil.makeVersionable(this.rootNode);
		JCRUtil.makeReferenceable(this.rootNode);

	}

	public Object getLockObject() {
		return this.lock;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.persistence.SLPersistentTreeSession#getNodeByID
	 * (java.lang.String)
	 */
	public SLPersistentNode getNodeByID(final String id)
			throws SLPersistentNodeNotFoundException,
			SLPersistentTreeSessionException {
		synchronized (this.lock) {
			try {
				SLPersistentNode persistentNode = null;

				final Node jcrNode = this.jcrSession.getNodeByUUID(id);
				final String[] names = jcrNode.getPath().substring(5)
						.split("/");
				for (final String name : names) {
					if (persistentNode == null) {
						persistentNode = this.getRootNode();
					} else {
						persistentNode = persistentNode.getNode(name);
					}
				}

				return persistentNode;
			} catch (final ItemNotFoundException e) {
				throw new SLPersistentNodeNotFoundException(id, e);
			} catch (final RepositoryException e) {
				throw new SLPersistentTreeSessionException(
						"Error on attempt to retrieve persistent node by id.",
						e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.persistence.SLPersistentTreeSession#getNodeByPath
	 * (java.lang.String)
	 */
	public SLPersistentNode getNodeByPath(final String path)
			throws SLPersistentTreeSessionException {

		synchronized (this.lock) {
			final SLPersistentQuery query = this.createQuery(path,
					SLPersistentQuery.TYPE_XPATH);
			final SLPersistentQueryResult result = query.execute();
			return result.getRowCount() == 1 ? result.getNodes().iterator()
					.next() : null;
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.persistence.SLPersistentTreeSession#getRootNode()
	 */
	public SLPersistentNode getRootNode()
			throws SLPersistentTreeSessionException {
		synchronized (this.lock) {

			if (this.rootNode == null) {
				try {
					Node oslRootNode = JCRUtil.getChildNode(this.jcrSession
							.getRootNode(),
							SharedConstants.DEFAULT_JCR_ROOT_NAME);
					if (oslRootNode == null) {
						oslRootNode = this.createProductRootNode();
					}
					this.rootNode = JCRUtil.getChildNode(oslRootNode,
							this.repositoryName);
					if (this.rootNode == null) {
						this.createRepositoryRootNode(oslRootNode);
						this.jcrSession.save();
					} else {
						final SortedSet<Double> versionNumbers = new TreeSet<Double>();
						final VersionIterator iter = this.rootNode
								.getVersionHistory().getAllVersions();
						while (iter.hasNext()) {
							final Version version = iter.nextVersion();
							if (!version.getName().equals("jcr:rootVersion")) {
								versionNumbers
										.add(new Double(version.getName()));
							}
						}
						if (versionNumbers.isEmpty()) {
							this.rootNode.remove();
							this.createRepositoryRootNode(oslRootNode);
							this.jcrSession.save();
						} else {
							// rootNode.restore(versionNumbers.last().toString(),
							// true);
						}
					}
					this.rootNode.checkout();
				} catch (final RepositoryException e) {
					throw new SLPersistentTreeSessionException(
							"Couldn't create persistent root node.", e);
				}
			}
			return new SLPersistentNodeImpl(this, null, this.rootNode,
					this.eventPoster);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getXPathRootPath() {
		return this.xpathRootPath;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#save()
	 */
	public void save() throws SLPersistentTreeSessionException {
		synchronized (this.lock) {
			try {
				// this.rootNode.save();
				this.rootNode.checkin();
				this.jcrSession.save();
				// jcrSession.logout();
			} catch (final RepositoryException e) {
				Exceptions.catchAndLog(e);
				throw new SLPersistentTreeSessionException(
						"Error on attempt to save persistent session.", e);
			}
		}
	}
}
