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
package org.openspotlight.graph.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLMetadata;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.info.SLSelectByNodeTypeInfo;
import org.openspotlight.graph.query.info.SLSelectInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;
import org.openspotlight.graph.util.ProxyUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class SLQueryImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLQueryImpl implements SLQuery {
	
	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLQueryImpl.class); 
	
	/** The session. */
	private SLGraphSession session;
	
	/** The tree session. */
	private SLPersistentTreeSession treeSession;
	
	/** The metadata. */
	private SLMetadata metadata;
	
	/** The selects. */
	private List<SLSelect> selects = new ArrayList<SLSelect>();
	
	/**
	 * Instantiates a new sL query impl.
	 * 
	 * @param session the session
	 * @param treeSession the tree session
	 */
	public SLQueryImpl(SLGraphSession session, SLPersistentTreeSession treeSession) {
		this.session = session;
		this.treeSession = treeSession;
		this.metadata = session.getMetadata();
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute()
	 */
	public SLQueryResult execute() throws SLInvalidQuerySyntaxException, SLGraphSessionException {
		return execute(SortMode.NOT_SORTED, false);
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute(org.openspotlight.graph.query.SLQuery.SortMode, boolean)
	 */
	public SLQueryResult execute(SortMode sortMode, boolean showSLQL) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
		
		try {
			
			SLSelectCommandDO commandDO = new SLSelectCommandDO();
			commandDO.setMetadata(metadata);
			commandDO.setTreeSession(treeSession);
			
			Collection<PNodeWrapper> selectNodeWrappers = null;
			Collection<PNodeWrapper> resultNodeWrappers = null;
			
			if (sortMode.equals(SortMode.SORTED)) {
				Comparator<PNodeWrapper> comparator = getPNodeWrapperComparator();
				resultNodeWrappers = new TreeSet<PNodeWrapper>(comparator);
			}
			else {
				resultNodeWrappers = new HashSet<PNodeWrapper>();
			}
			
			for (SLSelect select : selects) {
				
				SLSelectInfo selectInfo = SLSelectCommandSupport.getSelectInfo(select);
				Integer xTimes = selectInfo.getXTimes() == null ? 1 : selectInfo.getXTimes();
				SLSelectAbstractCommand command = SLSelectAbstractCommand.getExecuteCommand(select, selectInfo, commandDO);
				
				if (xTimes == SLSelectInfo.INDIFINITE) {
					print(showSLQL, selectInfo);
					do {
						command.execute();
						selectNodeWrappers = commandDO.getNodeWrappers();
						if (selectInfo.isKeepResult()) {
							resultNodeWrappers.addAll(selectNodeWrappers);
						}
						commandDO.setPreviousNodeWrappers(selectNodeWrappers);
					}
					while (!selectNodeWrappers.isEmpty());
				}
				else {
					print(showSLQL, selectInfo);
					for (int i = 0; i < xTimes; i++) {
						command.execute();
						selectNodeWrappers = commandDO.getNodeWrappers();
						if (selectNodeWrappers.isEmpty()) {
							if (commandDO.getPreviousNodeWrappers() != null) {
								commandDO.getPreviousNodeWrappers().clear();	
							}
							break;
						}
						if (selectInfo.isKeepResult()) {
							resultNodeWrappers.addAll(selectNodeWrappers);
						}
						commandDO.setPreviousNodeWrappers(selectNodeWrappers);
					}
				}
			}
			
			if (selectNodeWrappers != null) {
				resultNodeWrappers.addAll(selectNodeWrappers);	
			}

			Collection<SLNode> nodes = new ArrayList<SLNode>();
			for (PNodeWrapper pNodeWrapper : resultNodeWrappers) {
				SLNode node = session.getNodeByID(pNodeWrapper.getId());
				SLNode nodeProxy = ProxyUtil.createNodeProxy(SLNode.class, node);
				nodes.add(nodeProxy);
			}
			return new SLQueryResultImpl(nodes);
		}
		catch (SLException e) {
			throw new SLQueryException("Error on attempt to execute query.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#selectByNodeType()
	 */
	public SLSelectByNodeType selectByNodeType() throws SLGraphSessionException {
		SLSelectByNodeType select = new SLSelectByNodeTypeImpl();
		selects.add(select);
		return select;
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#selectByLinkType()
	 */
	public SLSelectByLinkType selectByLinkType() throws SLGraphSessionException {
		SLSelectByLinkType select = new SLSelectByLinkTypeImpl();
		selects.add(select);
		return select;
	}
	
	/**
	 * Prints the.
	 * 
	 * @param showSLQL the show slql
	 * @param object the object
	 */
	private void print(boolean showSLQL, Object object) {
		if (showSLQL) {
			LOGGER.info(object);
		}
	}
	
	/**
	 * Validate.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void validate(SLSelectByNodeTypeInfo selectInfo) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLGraphSessionException {

		/**
		List<SLWhereTypeInfo> typeInfoList = selectInfo.getTypeInfoList();
		for (SLWhereTypeInfo typeInfo : typeInfoList) {
			if (metadata.findMetaNodeType(typeInfo.getName()) == null) {
				throw new SLInvalidQueryElementException("Invalid select type: " + typeInfo.getName());
			}
		}
		//validateWhereStatement(selectInfo.getWhereStatementInfo());
		**/
	}
	
	/**
	 * Validate where statement.
	 * 
	 * @param whereStatementInfo the where statement info
	 * 
	 * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
	 */
	private void validateWhereStatement(SLTypeStatementInfo whereStatementInfo) throws SLInvalidQuerySyntaxException {
		if (whereStatementInfo == null) return;
		if (!whereStatementInfo.isClosed()) {
			SLInvalidQuerySyntaxException e = new SLInvalidQuerySyntaxException("bracket must be closed.");
			e.setStackTrace(whereStatementInfo.getOpenBraceStackTrace());
			throw e;
		}
		List<SLConditionInfo> conditionInfoList = whereStatementInfo.getConditionInfoList();
		for (SLConditionInfo conditionInfo : conditionInfoList) {
			if (conditionInfo.getInnerStatementInfo() != null) {
				validateWhereStatement(conditionInfo.getInnerStatementInfo());
			}
		}
	}
	
	/**
	 * Gets the p node wrapper comparator.
	 * 
	 * @return the p node wrapper comparator
	 */
	private Comparator<PNodeWrapper> getPNodeWrapperComparator() {
		return new Comparator<PNodeWrapper>() {
			public int compare(PNodeWrapper nodeWrapper1, PNodeWrapper nodeWrapper2) {
				try {
					if (nodeWrapper1.getTypeName().equals(nodeWrapper2.getTypeName())) {
						if (nodeWrapper1.getName().equals(nodeWrapper2.getName())) {
							return nodeWrapper1.getParentName().compareTo(nodeWrapper2.getParentName());
						}
						else {
							return nodeWrapper1.getName().compareTo(nodeWrapper2.getName());	
						}
					}
					else {
						return nodeWrapper1.getTypeName().compareTo(nodeWrapper2.getTypeName());
					}
				}
				catch (SLPersistentTreeSessionException e) {
					throw new SLRuntimeException("Error on attempt to execute persistent node wrapper comparator.", e);
				}
			}
		};
	}
}

class PNodeWrapper {
	
	private SLPersistentNode pNode;
	private String typeName;
	private String id;
	private String name;
	private String path;
	private String parentName;
	
	PNodeWrapper(SLPersistentNode pNode) {
		this.pNode = pNode;
	}
	
	PNodeWrapper(SLPersistentNode pNode, String typeName) {
		this.pNode = pNode;
		this.typeName = typeName;
	}
	
	public SLPersistentNode getPNode() {
		return pNode;
	}
	public void setPNode(SLPersistentNode node) {
		pNode = node;
	}
	public String getTypeName() throws SLPersistentTreeSessionException {
		if (typeName == null) {
			typeName = SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_TYPE);
		}
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getId() throws SLPersistentTreeSessionException {
		if (id == null) {
			id = pNode.getID();
		}
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() throws SLPersistentTreeSessionException {
		if (name == null) {
			name = SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_DECODED_NAME);
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() throws SLPersistentTreeSessionException {
		if (path == null) {
			path = pNode.getPath();
		}
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentName() throws SLPersistentTreeSessionException {
 		if (parentName == null) {
 			SLPersistentNode pParentNode = pNode.getParent();
			parentName = SLCommonSupport.getInternalPropertyAsString(pParentNode, SLConsts.PROPERTY_NAME_DECODED_NAME); 
		}
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	@Override
	public int hashCode() {
		try {
			return getId().hashCode();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate persistent node wrapper hash code.", e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			PNodeWrapper nodeWrapper = (PNodeWrapper) obj;
			return getId().equals(nodeWrapper.getId());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to verify persistent node wrapper equality.", e);
		}
	}
	
	@Override
	public String toString() {
		try {
			return SLCommonSupport.getInternalPropertyAsString(pNode, SLConsts.PROPERTY_NAME_DECODED_NAME);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to string " + this.getClass().getName());
		}
	}
}


class PLinkNodeWrapper {
	
	private SLPersistentNode pLinkNode;
	private String id;
	private Integer linkTypeHash;
	private String sourceID;
	private String targetID;
	
	PLinkNodeWrapper(SLPersistentNode pLinkNode) {
		this.pLinkNode = pLinkNode;
	}

	public SLPersistentNode getPLinkNode() {
		return pLinkNode;
	}

	public void setPLinkNode(SLPersistentNode linkNode) {
		pLinkNode = linkNode;
	}

	public String getId() throws SLPersistentTreeSessionException {
		if (id == null) {
			id = pLinkNode.getID();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getLinkTypeHash() throws SLPersistentTreeSessionException {
		if (linkTypeHash == null) {
			linkTypeHash = SLCommonSupport.getInternalPropertyAsInteger(pLinkNode, SLConsts.PROPERTY_NAME_LINK_TYPE_HASH);
		}
		return linkTypeHash;
	}

	public void setLinkTypeHash(Integer linkTypeHash) {
		this.linkTypeHash = linkTypeHash;
	}

	public String getSourceID() throws SLPersistentTreeSessionException {
		if (sourceID == null) {
			sourceID = SLCommonSupport.getInternalPropertyAsString(pLinkNode, SLConsts.PROPERTY_NAME_SOURCE_ID);
		}
		return sourceID;
	}

	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	public String getTargetID() throws SLPersistentTreeSessionException {
		if (targetID == null) {
			targetID = SLCommonSupport.getInternalPropertyAsString(pLinkNode, SLConsts.PROPERTY_NAME_TARGET_ID);
		}
		return targetID;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}
	
	@Override
	public int hashCode() {
		try {
			return getId().hashCode();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate persistent link node wrapper hash code.", e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			PLinkNodeWrapper linkNodeWrapper = (PLinkNodeWrapper) obj;
			return getId().equals(linkNodeWrapper.getId());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to verify persistent link node wrapper equality.", e);
		}
	}
}

