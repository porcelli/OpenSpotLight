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
	
	/** The tree session. */
	private SLPersistentTreeSession treeSession;
	
	/**
	 * Instantiates a new sL metadata impl.
	 * 
	 * @param treeSession the tree session
	 */
	public SLMetadataImpl(SLPersistentTreeSession treeSession) {
		this.treeSession = treeSession;
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#findMetaNodeType(java.lang.Class)
	 */
	public SLMetaNodeType findMetaNodeType(Class<? extends SLNode> nodeClass) throws SLGraphSessionException {
		return findMetaNodeType(nodeClass.getName());
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#findMetaNodeType(java.lang.String)
	 */
	public SLMetaNodeType findMetaNodeType(String typeName) throws SLGraphSessionException {
		try {
			StringBuilder statement = new StringBuilder("//osl/metadata/types//*");
			StringBuilderUtil.append(statement, '[', SLConsts.PROPERTY_NAME_NODE_TYPE, "='", typeName, "']");
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			SLMetaNodeType metaNode = null;
			if (result.getRowCount() == 1) {
				SLPersistentNode pMetaNode = result.getNodes().iterator().next();
				metaNode = new SLMetaNodeTypeImpl(this, pMetaNode);
			}
			return metaNode;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#findMetaNodeTypeByDescription(java.lang.String)
	 */
	public SLMetaNodeType findMetaNodeTypeByDescription(String description) throws SLGraphSessionException {
		try {
			StringBuilder statement = new StringBuilder("//osl/metadata/types//*");
			StringBuilderUtil.append(statement, '[', SLConsts.PROPERTY_NAME_DESCRIPTION, "='", description, "']");
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			SLMetaNodeType metaNode = null;
			if (result.getRowCount() == 1) {
				SLPersistentNode pMetaNode = result.getNodes().iterator().next();
				metaNode = new SLMetaNodeTypeImpl(this, pMetaNode);
			}
			return metaNode;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#getMetaNodesTypes()
	 */
	public Collection<SLMetaNodeType> getMetaNodesTypes() throws SLGraphSessionException {
		return getMetaNodesTypes(NOT_RECURSIVE);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#getMetaNodes()
	 */
	public Collection<SLMetaNodeType> getMetaNodesTypes(SLRecursiveMode recursiveMode) throws SLGraphSessionException {
		try {
			Collection<SLMetaNodeType> metaNodes = new ArrayList<SLMetaNodeType>();
			StringBuilder statement = new StringBuilder("//osl/metadata/types");
			if (recursiveMode.equals(RECURSIVE)) statement.append("//*");
			else statement.append("/*");
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> pNodes = result.getNodes();
			for (SLPersistentNode pNode : pNodes) {
				SLMetaNodeType metaNode = new SLMetaNodeTypeImpl(this, pNode);
				metaNodes.add(metaNode);
			}
			return metaNodes;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node metadata.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkType(java.lang.Class)
	 */
	public SLMetaLinkType getMetaLinkType(Class<? extends SLLink> linkType) throws SLGraphSessionException {
		return getMetaLinkType(linkType.getName());
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkType(java.lang.String)
	 */
	public SLMetaLinkType getMetaLinkType(String name) throws SLGraphSessionException {
		try {
			StringBuilder statement = new StringBuilder();
			statement.append("//osl/metadata/links/").append(name);
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			SLMetaLinkType metaLinkType = null;
			if (result.getRowCount() == 1) {
				metaLinkType = new SLMetaLinkTypeImpl(this, result.getNodes().iterator().next());
			}
			return metaLinkType;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link type.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkTypeByDescription(java.lang.String)
	 */
	public SLMetaLinkType getMetaLinkTypeByDescription(String description) throws SLGraphSessionException {
		try {
			StringBuilder statement = new StringBuilder();
			statement.append("//osl/metadata/links/*");
			StringBuilderUtil.append(statement, '[', SLConsts.PROPERTY_NAME_DESCRIPTION, "='", description, "']");
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			SLMetaLinkType metaLinkType = null;
			if (result.getRowCount() == 1) {
				metaLinkType = new SLMetaLinkTypeImpl(this, result.getNodes().iterator().next());
			}
			return metaLinkType;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link type.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetadata#getMetaLinkTypes()
	 */
	public Collection<SLMetaLinkType> getMetaLinkTypes() throws SLGraphSessionException {
		try {
			Collection<SLMetaLinkType> metaLinkTypes = new ArrayList<SLMetaLinkType>();
			StringBuilder statement = new StringBuilder("//osl/metadata/links/*");
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> linkTypeNodes = result.getNodes();
			for (SLPersistentNode linkTypeNode : linkTypeNodes) {
				SLMetaLinkType metaLinkType = new SLMetaLinkTypeImpl(this, linkTypeNode);
				metaLinkTypes.add(metaLinkType);
			}
			return metaLinkTypes;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link type.", e);
		}
	}
}

