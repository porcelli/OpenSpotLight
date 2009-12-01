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

import static org.openspotlight.graph.SLRecursiveMode.NOT_RECURSIVE;
import static org.openspotlight.graph.SLRecursiveMode.RECURSIVE;

import java.util.ArrayList;
import java.util.Collection;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;

/**
 * The Class SLMetadataImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetadataImpl implements SLMetadata {

	private final Object lock;

	/** The tree session. */
	private final SLPersistentTreeSession treeSession;

	/**
	 * Instantiates a new sL metadata impl.
	 * 
	 * @param treeSession
	 *            the tree session
	 */
	public SLMetadataImpl(final SLPersistentTreeSession treeSession) {
		this.treeSession = treeSession;
		this.lock = treeSession.getLockObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetadata#findMetaNodeType(java.lang.Class)
	 */
	public SLMetaNodeType findMetaNodeType(
			final Class<? extends SLNode> nodeClass)
			throws SLGraphSessionException {
		synchronized (this.lock) {
			return this.findMetaNodeType(nodeClass.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLMetadata#findMetaNodeType(java.lang.String)
	 */
	public SLMetaNodeType findMetaNodeType(final String typeName)
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final StringBuilder statement = new StringBuilder(
						this.treeSession.getXPathRootPath()
								+ "/metadata/types//*");
				StringBuilderUtil.append(statement, '[',
						SLConsts.PROPERTY_NAME_NODE_TYPE, "='", typeName, "']");
				final SLPersistentQuery query = this.treeSession.createQuery(
						statement.toString(), SLPersistentQuery.TYPE_XPATH);
				final SLPersistentQueryResult result = query.execute();
				SLMetaNodeType metaNode = null;
				if (result.getRowCount() == 1) {
					final SLPersistentNode pMetaNode = result.getNodes()
							.iterator().next();
					metaNode = new SLMetaNodeTypeImpl(this, pMetaNode);
				}
				return metaNode;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve meta node.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLMetadata#findMetaNodeTypeByDescription(java
	 * .lang.String)
	 */
	public SLMetaNodeType findMetaNodeTypeByDescription(final String description)
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final StringBuilder statement = new StringBuilder(
						this.treeSession.getXPathRootPath()
								+ "/metadata/types//*");
				StringBuilderUtil.append(statement, '[',
						SLConsts.PROPERTY_NAME_DESCRIPTION, "='", description,
						"']");
				final SLPersistentQuery query = this.treeSession.createQuery(
						statement.toString(), SLPersistentQuery.TYPE_XPATH);
				final SLPersistentQueryResult result = query.execute();
				SLMetaNodeType metaNode = null;
				if (result.getRowCount() == 1) {
					final SLPersistentNode pMetaNode = result.getNodes()
							.iterator().next();
					metaNode = new SLMetaNodeTypeImpl(this, pMetaNode);
				}
				return metaNode;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve meta node.", e);
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
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkType(java.lang.Class)
	 */
	public SLMetaLinkType getMetaLinkType(final Class<? extends SLLink> linkType)
			throws SLGraphSessionException {
		synchronized (this.lock) {
			return this.getMetaLinkType(linkType.getName());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkType(java.lang.String)
	 */
	public SLMetaLinkType getMetaLinkType(final String name)
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final StringBuilder statement = new StringBuilder();
				statement.append(
						this.treeSession.getXPathRootPath()
								+ "/metadata/links/").append(name);
				final SLPersistentQuery query = this.treeSession.createQuery(
						statement.toString(), SLPersistentQuery.TYPE_XPATH);
				final SLPersistentQueryResult result = query.execute();
				SLMetaLinkType metaLinkType = null;
				if (result.getRowCount() == 1) {
					metaLinkType = new SLMetaLinkTypeImpl(this, result
							.getNodes().iterator().next());
				}
				return metaLinkType;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve meta link type.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLMetadata#getMetaLinkTypeByDescription(java.
	 * lang.String)
	 */
	public SLMetaLinkType getMetaLinkTypeByDescription(final String description)
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final StringBuilder statement = new StringBuilder();
				statement.append(this.treeSession.getXPathRootPath()
						+ "/metadata/links/*");
				StringBuilderUtil.append(statement, '[',
						SLConsts.PROPERTY_NAME_DESCRIPTION, "='", description,
						"']");
				final SLPersistentQuery query = this.treeSession.createQuery(
						statement.toString(), SLPersistentQuery.TYPE_XPATH);
				final SLPersistentQueryResult result = query.execute();
				SLMetaLinkType metaLinkType = null;
				if (result.getRowCount() == 1) {
					metaLinkType = new SLMetaLinkTypeImpl(this, result
							.getNodes().iterator().next());
				}
				return metaLinkType;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve meta link type.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkTypes()
	 */
	public Collection<SLMetaLinkType> getMetaLinkTypes()
			throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final Collection<SLMetaLinkType> metaLinkTypes = new ArrayList<SLMetaLinkType>();
				final StringBuilder statement = new StringBuilder(
						this.treeSession.getXPathRootPath()
								+ "/metadata/links/*");
				final SLPersistentQuery query = this.treeSession.createQuery(
						statement.toString(), SLPersistentQuery.TYPE_XPATH);
				final SLPersistentQueryResult result = query.execute();
				final Collection<SLPersistentNode> linkTypeNodes = result
						.getNodes();
				for (final SLPersistentNode linkTypeNode : linkTypeNodes) {
					final SLMetaLinkType metaLinkType = new SLMetaLinkTypeImpl(
							this, linkTypeNode);
					metaLinkTypes.add(metaLinkType);
				}
				return metaLinkTypes;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve meta link type.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetadata#getMetaNodesTypes()
	 */
	public Collection<SLMetaNodeType> getMetaNodesTypes()
			throws SLGraphSessionException {
		synchronized (this.lock) {
			return this.getMetaNodesTypes(NOT_RECURSIVE);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetadata#getMetaNodes()
	 */
	public Collection<SLMetaNodeType> getMetaNodesTypes(
			final SLRecursiveMode recursiveMode) throws SLGraphSessionException {
		synchronized (this.lock) {
			try {
				final Collection<SLMetaNodeType> metaNodes = new ArrayList<SLMetaNodeType>();
				final StringBuilder statement = new StringBuilder(
						this.treeSession.getXPathRootPath() + "/metadata/types");
				if (recursiveMode.equals(RECURSIVE)) {
					statement.append("//*");
				} else {
					statement.append("/*");
				}
				final SLPersistentQuery query = this.treeSession.createQuery(
						statement.toString(), SLPersistentQuery.TYPE_XPATH);
				final SLPersistentQueryResult result = query.execute();
				final Collection<SLPersistentNode> pNodes = result.getNodes();
				for (final SLPersistentNode pNode : pNodes) {
					final SLMetaNodeType metaNode = new SLMetaNodeTypeImpl(
							this, pNode);
					metaNodes.add(metaNode);
				}
				return metaNodes;
			} catch (final SLException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve node metadata.", e);
			}
		}
	}
}
