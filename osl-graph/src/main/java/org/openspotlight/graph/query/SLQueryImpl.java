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

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.SerializationUtil;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLMetaNodeType;
import org.openspotlight.graph.SLMetadata;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLRecursiveMode;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.info.SLOrderByStatementInfo;
import org.openspotlight.graph.query.info.SLOrderByTypeInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkInfo;
import org.openspotlight.graph.query.info.SLSelectInfo;
import org.openspotlight.graph.query.info.SLSelectStatementInfo;
import org.openspotlight.graph.query.info.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SLWhereLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereStatementInfo;
import org.openspotlight.graph.query.info.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.SLOrderByTypeInfo.OrderType;
import org.openspotlight.graph.query.info.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo;
import org.openspotlight.graph.query.info.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereTypeInfo.SLTypeStatementInfo.SLTypeConditionInfo;
import org.openspotlight.graph.util.ProxyUtil;

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
	
	/** The collator strength. */
	private int collatorStrength = Collator.IDENTICAL;
	
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
	 * @see org.openspotlight.graph.query.SLQuery#selectByNodeType()
	 */
	public SLSelectByNodeType selectByNodeType() throws SLGraphSessionException {
		SLSelectByNodeType select = new SLSelectByNodeTypeImpl(this);
		selects.add(select);
		return select;
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#selectByLinkType()
	 */
	public SLSelectByLinkType selectByLinkType() throws SLGraphSessionException {
		SLSelectByLinkType select = new SLSelectByLinkTypeImpl(this);
		selects.add(select);
		return select;
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkCount()
	 */
	public SLSelectByLinkCount selectByLinkCount() throws SLGraphSessionException {
		SLSelectByLinkCount select = new SLSelectByLinkCountImpl(this);
		selects.add(select);
		return select;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectFacade#select()
	 */
	public SLSelectStatement select() throws SLGraphSessionException {
		SLSelectStatement select = new SLSelectStatementImpl(this);
		selects.add(select);
		return select;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute()
	 */
	public SLQueryResult execute() throws SLInvalidQuerySyntaxException, SLGraphSessionException {
		return execute((String[]) null, SortMode.NOT_SORTED, false);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute(java.util.Collection)
	 */
	public SLQueryResult execute(Collection<SLNode> inputNodes) throws SLInvalidQuerySyntaxException, SLGraphSessionException {
		return execute(SLQuerySupport.getNodeIDs(inputNodes), SortMode.NOT_SORTED, false);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute(java.lang.String[])
	 */
	public SLQueryResult execute(String[] inputNodesIDs) throws SLInvalidQuerySyntaxException, SLGraphSessionException {
		return execute(inputNodesIDs, SortMode.NOT_SORTED, false);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute(org.openspotlight.graph.query.SLQuery.SortMode, boolean)
	 */
	public SLQueryResult execute(SortMode sortMode, boolean showSLQL) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
		return execute((String[]) null, sortMode, showSLQL);
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute(java.util.Collection, org.openspotlight.graph.query.SLQuery.SortMode, boolean)
	 */
	public SLQueryResult execute(Collection<SLNode> inputNodes, SortMode sortMode, boolean showSLQL) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
		return execute(SLQuerySupport.getNodeIDs(inputNodes), SortMode.NOT_SORTED, false);
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#execute(org.openspotlight.graph.query.SLQuery.SortMode, boolean)
	 */
	public SLQueryResult execute(String[] inputNodesIDs, SortMode sortMode, boolean showSLQL) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
		
		validateSelects();
		
		try {
			
			Collection<PNodeWrapper> selectNodeWrappers = null;
			//here is the result
			Collection<PNodeWrapper> resultNodeWrappers = null;

			SLSelectCommandDO commandDO = new SLSelectCommandDO();
			commandDO.setMetadata(metadata);
			commandDO.setTreeSession(treeSession);

			Set<PNodeWrapper> wrappers = SLQuerySupport.getNodeWrappers(treeSession, inputNodesIDs);
			commandDO.setPreviousNodeWrappers(wrappers);
			
			SLOrderByStatementInfo orderByStatementInfo = getLastSelectOrderByStatementInfo();
			if (orderByStatementInfo == null) {
				if (sortMode.equals(SortMode.SORTED)) {
					Comparator<PNodeWrapper> comparator = getPNodeWrapperComparator();
					resultNodeWrappers = new TreeSet<PNodeWrapper>(comparator);
				}
				else {
					resultNodeWrappers = new HashSet<PNodeWrapper>();	
				}
			}
			else {
			    //TODO check it later
				Comparator<PNodeWrapper> comparator = getOrderByPNodeWrapperComparator(orderByStatementInfo);
				resultNodeWrappers = new TreeSet<PNodeWrapper>(comparator);
			}
			
			for (SLSelect select : selects) {
				
				SLSelectStatementInfo selectStatementInfo = SLQuerySupport.getSelectStatementInfo(select);
				Integer xTimes = selectStatementInfo.getXTimes() == null ? 1 : selectStatementInfo.getXTimes();
				SLSelectAbstractCommand command = SLSelectAbstractCommand.getCommand(select, selectStatementInfo, commandDO);
				commandDO.setCollatorStrength(getCollatorStrength(selectStatementInfo));
				
				if (xTimes == SLSelectInfo.INDIFINITE) {
					print(showSLQL, selectStatementInfo);
					do {
						command.execute();
						selectNodeWrappers = commandDO.getNodeWrappers();
						if (selectStatementInfo.isKeepResult()) {
							resultNodeWrappers.addAll(selectNodeWrappers);
						}
						commandDO.setPreviousNodeWrappers(selectNodeWrappers);
					}
					while (!selectNodeWrappers.isEmpty());
				}
				else {
					print(showSLQL, selectStatementInfo);
					for (int i = 0; i < xTimes; i++) {
						command.execute();
						selectNodeWrappers = commandDO.getNodeWrappers();
						if (selectNodeWrappers.isEmpty()) {
							if (commandDO.getPreviousNodeWrappers() != null) {
								commandDO.getPreviousNodeWrappers().clear();	
							}
							break;
						}
						if (selectStatementInfo.isKeepResult()) {
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
				SLNode node = session.getNodeByID(pNodeWrapper.getID());
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
	 * @see org.openspotlight.graph.query.SLQuery#getCollatorStrength()
	 */
	public int getCollatorStrength() {
		return collatorStrength;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLQuery#setCollatorStrength(int)
	 */
	public void setCollatorStrength(int collatorStrength) {
		this.collatorStrength = collatorStrength;
	}
	
	/**
	 * Gets the last select order by statement info.
	 * 
	 * @return the last select order by statement info
	 */
	private SLOrderByStatementInfo getLastSelectOrderByStatementInfo() {
		SLOrderByStatementInfo orderByStatementInfo = null;
		if (!selects.isEmpty()) {
			SLSelectStatementInfo selectStatementInfo = SLQuerySupport.getSelectStatementInfo(selects.get(selects.size() - 1));
			orderByStatementInfo = selectStatementInfo.getOrderByStatementInfo();
		}
		return orderByStatementInfo;
	}
	
	/**
	 * Gets the collator strength.
	 * 
	 * @param selectStatementInfo the select statement info
	 * 
	 * @return the collator strength
	 */
	private int getCollatorStrength(SLSelectStatementInfo selectStatementInfo) {
		return selectStatementInfo.getCollatorStrength() == null ? getCollatorStrength() : selectStatementInfo.getCollatorStrength();  
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
	 * Validate selects.
	 * 
	 * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 * @throws SLQueryException the SL query exception
	 */
	private void validateSelects() throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
		try {
			for (SLSelect select : selects) {
				SLSelectStatementInfo selectStatementInfo = SLQuerySupport.getSelectStatementInfo(select);
				validateSelectStatementInfoBeforeNormalization(selectStatementInfo);
				normalizeSelectStatementInfo(selectStatementInfo);
				validateSelectStatementInfoAfterNormalization(selectStatementInfo);
			}
		}
		catch (SLInvalidQuerySyntaxException e) {
			throw e;
		}
		catch (SLInvalidQueryElementException e) {
			throw e;
		}
		catch (SLGraphSessionException e) {
			throw new SLQueryException("Error on attempt to validate select information.", e);
		}
	}
	
	/**
	 * Validate select statement info before normalization.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void validateSelectStatementInfoBeforeNormalization(SLSelectStatementInfo selectInfo) throws SLGraphSessionException {
		validateSelectType(selectInfo);
		validateAllTypes(selectInfo);
		validateTypesExsistence(selectInfo);
		validateWhereStatements(selectInfo);
	}
	
	/**
	 * Validate select statement info after normalization.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 */
	private void validateSelectStatementInfoAfterNormalization(SLSelectStatementInfo selectInfo) throws SLInvalidQueryElementException {
		validateNodeTypesOnWhere(selectInfo);
		validateLinkTypesOnWhere(selectInfo);
	}

	/**
	 * Validate link types on where.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 */
	private void validateLinkTypesOnWhere(SLSelectStatementInfo selectInfo) throws SLInvalidQueryElementException {
		SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
		if (whereInfo != null) {
			Set<SLSelectByLinkInfo> byLinkInfoSet = new HashSet<SLSelectByLinkInfo>(selectInfo.getByLinkInfoList());
			for (SLWhereLinkTypeInfo whereLinkTypeInfo : whereInfo.getWhereLinkTypeInfoList()) {
				if (!byLinkInfoSet.contains(new SLSelectByLinkInfo(whereLinkTypeInfo.getName()))) {
					throw new SLInvalidQueryElementException("Link type not present in select by link clause: " + whereLinkTypeInfo.getName());
				}
			}
		}
	}

	/**
	 * Validate node types on where.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 */
	private void validateNodeTypesOnWhere(SLSelectStatementInfo selectInfo) throws SLInvalidQueryElementException {
		SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
		if (whereInfo != null) {
			Set<SLSelectTypeInfo> selectTypeInfoSet = new HashSet<SLSelectTypeInfo>(selectInfo.getTypeInfoList());
			for (SLWhereTypeInfo whereTypeInfo : whereInfo.getWhereTypeInfoList()) {
				if (!selectTypeInfoSet.contains(new SLSelectTypeInfo(whereTypeInfo.getName()))) {
					throw new SLInvalidQueryElementException("Node type not present in select clause: " + whereTypeInfo.getName());
				}
			}
		}
	}
	
	/**
	 * Validate types exsistence.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void validateTypesExsistence(SLSelectStatementInfo selectInfo) throws SLInvalidQueryElementException, SLGraphSessionException {
		for (SLSelectTypeInfo selectTypeInfo : selectInfo.getTypeInfoList()) {
			if (!nodeTypeExists(selectTypeInfo.getName())) {
				throw new SLInvalidQueryElementException("Node type on select clause not found: " + selectTypeInfo.getName());
			}
		}
		/**
		for (SLSelectByLinkInfo byLinkInfo : selectInfo.getByLinkInfoList()) {
			if (!linkTypeExists(byLinkInfo.getName())) {
				throw new SLInvalidQueryElementException("Link type on select by link clause not found: " + byLinkInfo.getName());
			}
		}
		**/
		SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
		if (whereInfo != null) {
			for (SLWhereTypeInfo whereTypeInfo : whereInfo.getWhereTypeInfoList()) {
				if (!nodeTypeExists(whereTypeInfo.getName())) {
					throw new SLInvalidQueryElementException("Node type on where clause not found: " + whereTypeInfo.getName());	
				}
			}
			/**
			for (SLWhereLinkTypeInfo linkTypeInfo : whereInfo.getWhereLinkTypeInfoList()) {
				if (!linkTypeExists(linkTypeInfo.getName())) {
					throw new SLInvalidQueryElementException("Link type on where clause not found: " + linkTypeInfo.getName());
				}
			}
			**/
		}
	}
	
	/**
	 * Link type exists.
	 * 
	 * @param name the name
	 * 
	 * @return true, if successful
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	boolean linkTypeExists(String name) throws SLGraphSessionException {
		return metadata.getMetaLinkType(name) != null || metadata.getMetaLinkTypeByDescription(name) != null;
	}
	
	/**
	 * Node type exists.
	 * 
	 * @param name the name
	 * 
	 * @return true, if successful
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private boolean nodeTypeExists(String name) throws SLGraphSessionException {
		return metadata.findMetaNodeType(name) != null || metadata.findMetaNodeTypeByDescription(name) != null;
	}

	/**
	 * Validate all types.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 */
	private void validateAllTypes(SLSelectStatementInfo selectInfo) throws SLInvalidQueryElementException {
		if (selectInfo.getAllTypes() != null) {
			if (!selectInfo.getTypeInfoList().isEmpty()) {
				throw new SLInvalidQueryElementException("When all types (*) or all types on where (**) are used, no type can be specifically used on select clause.");
			}
			SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
			if (whereInfo != null) {
				if (selectInfo.getAllTypes().isOnWhere() && whereInfo.getWhereTypeInfoList().isEmpty()) {
					throw new SLInvalidQueryElementException("When all types on where (**)  is used, at least on type filter on where clause must be used.");
				}
			}
		}
	}
	
	/**
	 * Validate select type.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQueryElementException the SL invalid query element exception
	 */
	private void validateSelectType(SLSelectStatementInfo selectInfo) throws SLInvalidQueryElementException {
		SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
		if (whereInfo != null) {
			if (selectInfo.getByLinkInfoList().isEmpty()) {
				if (!whereInfo.getWhereLinkTypeInfoList().isEmpty()) {
					throw new SLInvalidQueryElementException("Link types on where clause can only be used if 'by link' is used on select clause.");
				}
			}
			else {
				if (!whereInfo.getWhereTypeInfoList().isEmpty()) {
					throw new SLInvalidQueryElementException("Node types cannot be used on where clause if 'by link' is used on select, however, link type filters are allowed instead.");	
				}
			}
		}
	}
	
	/**
	 * Validate where statements.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
	 */
	private void validateWhereStatements(SLSelectStatementInfo selectInfo) throws SLInvalidQuerySyntaxException {
		SLWhereStatementInfo whereStatementInfo = selectInfo.getWhereStatementInfo();
		if (whereStatementInfo != null) {
			for (SLWhereTypeInfo whereTypeInfo : whereStatementInfo.getWhereTypeInfoList()) {
				validateTypeStatementInfo(whereTypeInfo.getTypeStatementInfo());
			}
			for (SLWhereLinkTypeInfo whereTypeInfo : whereStatementInfo.getWhereLinkTypeInfoList()) {
				validateLinkTypeStatementInfo(whereTypeInfo.getLinkTypeStatementInfo());
			}
		}
	}
	
	/**
	 * Validate type statement info.
	 * 
	 * @param typeStatementInfo the type statement info
	 * 
	 * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
	 */
	private void validateTypeStatementInfo(SLTypeStatementInfo typeStatementInfo) throws SLInvalidQuerySyntaxException {
		if (typeStatementInfo == null) return;
		if (!typeStatementInfo.isClosed()) {
			SLInvalidQuerySyntaxException e = new SLInvalidQuerySyntaxException("bracket must be closed.");
			e.setStackTrace(typeStatementInfo.getOpenBraceStackTrace());
			throw e;
		}
		List<SLTypeConditionInfo> conditionInfoList = typeStatementInfo.getConditionInfoList();
		for (SLTypeConditionInfo conditionInfo : conditionInfoList) {
			if (conditionInfo.getInnerStatementInfo() != null) {
				validateTypeStatementInfo(conditionInfo.getInnerStatementInfo());
			}
		}
	}

	/**
	 * Validate link type statement info.
	 * 
	 * @param linkTypeStatementInfo the link type statement info
	 * 
	 * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
	 */
	private void validateLinkTypeStatementInfo(SLLinkTypeStatementInfo linkTypeStatementInfo) throws SLInvalidQuerySyntaxException {
		if (linkTypeStatementInfo == null) return;
		if (!linkTypeStatementInfo.isClosed()) {
			SLInvalidQuerySyntaxException e = new SLInvalidQuerySyntaxException("bracket must be closed.");
			e.setStackTrace(linkTypeStatementInfo.getOpenBraceStackTrace());
			throw e;
		}
		List<SLLinkTypeConditionInfo> conditionInfoList = linkTypeStatementInfo.getConditionInfoList();
		for (SLLinkTypeConditionInfo conditionInfo : conditionInfoList) {
			if (conditionInfo.getInnerStatementInfo() != null) {
				validateLinkTypeStatementInfo(conditionInfo.getInnerStatementInfo());
			}
		}
	}
	
	/**
	 * Normalize select statement info.
	 * 
	 * @param selectInfo the select info
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void normalizeSelectStatementInfo(SLSelectStatementInfo selectInfo) throws SLGraphSessionException {
		
		Set<SLSelectTypeInfo> selectTypeInfoSet = new HashSet<SLSelectTypeInfo>();
		if (selectInfo.getAllTypes() != null) {
			if (selectInfo.getAllTypes().isOnWhere()) {
				List<SLWhereTypeInfo> whereTypeInfoList = selectInfo.getWhereStatementInfo().getWhereTypeInfoList();
				for (SLWhereTypeInfo whereTypeInfo : whereTypeInfoList) {
					Collection<SLMetaNodeType> metaNodeTypes = getMetaNodeTypes(whereTypeInfo.getName(), whereTypeInfo.isSubTypes());
					for (SLMetaNodeType metaNodeType : metaNodeTypes) {
						SLSelectTypeInfo selectTypeInfo = new SLSelectTypeInfo(selectInfo, metaNodeType.getTypeName());
						selectTypeInfoSet.add(selectTypeInfo);
					}
				}
			}
			else {
				Collection<SLMetaNodeType> metaNodeTypes = metadata.getMetaNodesTypes(SLRecursiveMode.RECURSIVE);
				for (SLMetaNodeType metaNodeType : metaNodeTypes) {
					SLSelectTypeInfo selectTypeInfo = new SLSelectTypeInfo(selectInfo, metaNodeType.getTypeName());
					selectTypeInfoSet.add(selectTypeInfo);
				}
			}
		}
		else {
			for (SLSelectTypeInfo selectTypeInfo : selectInfo.getTypeInfoList()) {
				Collection<SLMetaNodeType> metaNodeTypes = getMetaNodeTypes(selectTypeInfo.getName(), selectTypeInfo.isSubTypes());
				for (SLMetaNodeType metaNodeType : metaNodeTypes) {
					SLSelectTypeInfo current = new SLSelectTypeInfo(selectInfo, metaNodeType.getTypeName());
					selectTypeInfoSet.add(current);
				}
			}
		}
		selectInfo.getTypeInfoList().clear();
		selectInfo.getTypeInfoList().addAll(selectTypeInfoSet);
		
		SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
		
		if (whereInfo != null) {
			Set<SLWhereTypeInfo> whereTypeInfoSet = new HashSet<SLWhereTypeInfo>();
			for (SLWhereTypeInfo whereTypeInfo : whereInfo.getWhereTypeInfoList()) {
				Collection<SLMetaNodeType> metaNodeTypes = getMetaNodeTypes(whereTypeInfo.getName(), whereTypeInfo.isSubTypes());
				for (SLMetaNodeType metaNodeType : metaNodeTypes) {
					SLWhereTypeInfo current = SerializationUtil.clone(whereTypeInfo);
					current.setName(metaNodeType.getTypeName());
					current.setSubTypes(false);
					whereTypeInfoSet.add(current);
				}
			}
			whereInfo.getWhereTypeInfoList().clear();
			whereInfo.getWhereTypeInfoList().addAll(whereTypeInfoSet);
		}
	}
	
	/**
	 * Gets the meta node types.
	 * 
	 * @param name the name
	 * @param subTypes the sub types
	 * 
	 * @return the meta node types
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private Collection<SLMetaNodeType> getMetaNodeTypes(String name, boolean subTypes) throws SLGraphSessionException {
		Collection<SLMetaNodeType> metaNodeTypes = new ArrayList<SLMetaNodeType>();
		SLMetaNodeType metaNodeType = metadata.findMetaNodeTypeByDescription(name);
		if (metaNodeType == null) metaNodeType = metadata.findMetaNodeType(name);
		metaNodeTypes.add(metaNodeType);
		if (subTypes) {
			Collection<SLMetaNodeType> subMetaNodeTypes = metaNodeType.getSubMetaNodeTypes();
			for (SLMetaNodeType subMetaNodeType : subMetaNodeTypes) {
				metaNodeTypes.add(subMetaNodeType);
			}
		}
		return metaNodeTypes;
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
	
	/**
	 * Gets the order by p node wrapper comparator.
	 * 
	 * @param orderByStatementInfo the order by statement info
	 * 
	 * @return the order by p node wrapper comparator
	 */
	private Comparator<PNodeWrapper> getOrderByPNodeWrapperComparator(final SLOrderByStatementInfo orderByStatementInfo) {
		return new Comparator<PNodeWrapper>() {
			public int compare(PNodeWrapper nodeWrapper1, PNodeWrapper nodeWrapper2) {
				try {
					String typeName1 = nodeWrapper1.getTypeName();
					String typeName2 = nodeWrapper2.getTypeName();
					Integer index1 = getTypeIndex(typeName1);
					Integer index2 = getTypeIndex(typeName2);
					if (index1 == index2) {
						if (nodeWrapper1.getID().equals(nodeWrapper2.getID())) {
							return 0;
						}
						else {
							List<SLOrderByTypeInfo> typeInfoList = orderByStatementInfo.getOrderByTypeInfoList();
							SLOrderByTypeInfo typeInfo = typeInfoList.get(index1);
							String propertyName = index1 < typeInfoList.size() ? typeInfo.getPropertyName() : null;
							Comparable<Serializable> value1 = nodeWrapper1.getPropertyValue(propertyName);
							Comparable<Serializable> value2 = nodeWrapper2.getPropertyValue(propertyName);
							if (propertyName != null) {
								value1 = nodeWrapper1.getPropertyValue(propertyName);
								value2 = nodeWrapper2.getPropertyValue(propertyName);
							}
							int compareValue;
							if (value1 == null && value2 == null) {
								compareValue = nodeWrapper1.getPath().compareTo(nodeWrapper2.getPath());
							}
							else if (value1 == null && value2 != null) {
								compareValue =  1;
							}
							else if (value1 != null && value2 == null) {
								compareValue = -1;
							}
							else {
								compareValue =  value1.compareTo((Serializable) value2);
							}
							return normalizeCompareValue(compareValue, typeInfo.getOrderType());
						}
					}
					else {
						return index1.compareTo(index2);
					}
				}
				catch (SLException e) {
					throw new SLRuntimeException("Error on attempt on order by comparator.", e);
				}
			}
			
			private int normalizeCompareValue(int value, OrderType orderType) {
				return orderType.equals(OrderType.ASCENDING) ? value : -value;
			}
			
			private int getTypeIndex(String typeName) throws SLGraphSessionException {
				List<SLOrderByTypeInfo> typeInfoList = orderByStatementInfo.getOrderByTypeInfoList();
				for (int i = 0; i < typeInfoList.size(); i++) {
					SLOrderByTypeInfo typeInfo = typeInfoList.get(i);
					if (isInstanceOf(typeName, typeInfo.getTypeName())) {
						return i;
					}
				}
				return typeInfoList.size();
			}
			
			private boolean isInstanceOf(String subTypeName, String typeName) throws SLGraphSessionException {
				boolean status = subTypeName.equals(typeName);
				if (!status) {
					SLMetaNodeType metaNodeType = metadata.findMetaNodeType(typeName);
					status = isInstanceOf(subTypeName, metaNodeType);
				}
				return status; 
			}
			
			private boolean isInstanceOf(String subTypeName, SLMetaNodeType metaNodeType) throws SLGraphSessionException {
				boolean status = false;
				SLMetaNodeType subMetaNodeType = metaNodeType.getSubMetaNodeType(subTypeName);
				if (subMetaNodeType == null) {
					Collection<SLMetaNodeType> subMetaNodeTypes = metaNodeType.getSubMetaNodeTypes();
					for (SLMetaNodeType current : subMetaNodeTypes) {
						status = isInstanceOf(subTypeName, current);
						if (status) break;
					}
				}
				else {
					status = true;
				}
				return status;
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
	private Map<String, Comparable<Serializable>> propertyValueMap = new HashMap<String, Comparable<Serializable>>();
	
	PNodeWrapper(SLPersistentNode pNode) {
		this.pNode = pNode;
	}
	
	PNodeWrapper(SLPersistentNode pNode, String typeName) {
		this.pNode = pNode;
		this.typeName = typeName;
	}
	
	@SuppressWarnings("unchecked")
	public Comparable<Serializable> getPropertyValue(String name) throws SLPersistentTreeSessionException {
		Comparable<Serializable> comparableValue = propertyValueMap.get(name);
		if (comparableValue == null) {
			Serializable value = SLCommonSupport.getUserPropertyAsSerializable(pNode, name);
			if (value instanceof Comparable) comparableValue = (Comparable<Serializable>) value;
			if (comparableValue != null) propertyValueMap.put(name, comparableValue);
		}
		return comparableValue;
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
	public String getID() throws SLPersistentTreeSessionException {
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
			return getID().hashCode();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate persistent node wrapper hash code.", e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			PNodeWrapper nodeWrapper = (PNodeWrapper) obj;
			return getID().equals(nodeWrapper.getID());
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

