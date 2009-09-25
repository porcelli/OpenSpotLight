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

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionIterator;

import org.apache.log4j.Logger;
import org.openspotlight.common.util.JCRUtil;

/**
 * The Class SLPersistentTreeSessionImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLPersistentTreeSessionImpl implements SLPersistentTreeSession {
	
	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLPersistentTreeSessionImpl.class);
	
	/** The jcr session. */
	private Session jcrSession;
	
	/** The event poster. */
	private SLPersistentEventPoster eventPoster;
	
	/** The root node. */
	private Node rootNode;

	/**
	 * Instantiates a new sL persistent tree session impl.
	 * 
	 * @param session the session
	 */
	public SLPersistentTreeSessionImpl(Session session) {
		this.jcrSession = session;
		SLPersistentEventListener listener = new SLPersistentEventListenerImpl();
		this.eventPoster = new SLPersistentEventPosterImpl(listener);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#getRootNode()
	 */
	public SLPersistentNode getRootNode() throws SLPersistentTreeSessionException {
		if (rootNode == null) {
			try {
				rootNode = JCRUtil.getChildNode(jcrSession.getRootNode(), "osl");
				if (rootNode == null) {
					createRootNode();
					jcrSession.save();
				}
				else {
					SortedSet<Double> versionNumbers = new TreeSet<Double>();
					VersionIterator iter = rootNode.getVersionHistory().getAllVersions();
					while (iter.hasNext()) {
						Version version = iter.nextVersion();
						if (!version.getName().equals("jcr:rootVersion")) {
							versionNumbers.add(new Double(version.getName()));
						}
					}
					if (versionNumbers.isEmpty()) {
						rootNode.remove();
						createRootNode();
						jcrSession.save();
					}
					else {
						//rootNode.restore(versionNumbers.last().toString(), true);
					}
				}
				rootNode.checkout();
			}
			catch (RepositoryException e) {
				throw new SLPersistentTreeSessionException("Couldn't create persistent root node.", e);
			}
		}
		return new SLPersistentNodeImpl(this, null, rootNode, eventPoster);		
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#close()
	 */
	public void close() {
		jcrSession.logout();
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#save()
	 */
	public void save() throws SLPersistentTreeSessionException {
		try {
			rootNode.save();
			rootNode.checkin();
			jcrSession.save();
			jcrSession.logout();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to save persistent session.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#clear()
	 */
	public void clear() throws SLPersistentTreeSessionException {
		if (rootNode != null) {
			try {
				NodeIterator iter = rootNode.getNodes();
				while (iter.hasNext()) {
					Node node = iter.nextNode();
					node.remove();
				}
			}
			catch (RepositoryException e) {
				throw new SLPersistentTreeSessionException("Error on attempt to clear the persistent tree session.", e);
			}
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#createQuery(java.lang.String, int)
	 */
	public SLPersistentQuery createQuery(String statement, int type) throws SLPersistentTreeSessionException {
		return new SLPersistentQueryImpl(this, jcrSession, statement, type);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#getNodeByID(java.lang.String)
	 */
	public SLPersistentNode getNodeByID(String id) throws SLPersistentNodeNotFoundException, SLPersistentTreeSessionException {
		try {
			SLPersistentNode persistentNode = null;
			Node jcrNode = jcrSession.getNodeByUUID(id);
			String[] names = jcrNode.getPath().substring(1).split("/");
			for (int i = 0; i < names.length; i++) {
				if (persistentNode == null) {
					persistentNode = getRootNode();
				}
				else {
					persistentNode = persistentNode.getNode(names[i]);
				}
			}
			return persistentNode;
		}
		catch (ItemNotFoundException e) {
			throw new SLPersistentNodeNotFoundException(id, e);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent node by id.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentTreeSession#getNodeByPath(java.lang.String)
	 */
	public SLPersistentNode getNodeByPath(String path) throws SLPersistentTreeSessionException {
		SLPersistentQuery query = createQuery(path, SLPersistentQuery.TYPE_XPATH);
		SLPersistentQueryResult result = query.execute();
		return result.getRowCount() == 1 ? result.getNodes().iterator().next() : null;
	}
	
	/**
	 * Creates the root node.
	 * 
	 * @throws RepositoryException the repository exception
	 */
	private void createRootNode() throws RepositoryException {
		rootNode = jcrSession.getRootNode().addNode("osl");
		JCRUtil.makeVersionable(rootNode);
		JCRUtil.makeReferenceable(rootNode);
	}
}



