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

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement.Condition;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo.SLSelectByLinkInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo.SLConditionInfo;

import java.util.*;

import static org.openspotlight.graph.SLCommonSupport.toInternalPropertyName;
import static org.openspotlight.graph.query.SLConditionalOperatorType.AND;
import static org.openspotlight.graph.query.SLConditionalOperatorType.OR;
import static org.openspotlight.graph.query.SLRelationalOperatorType.EQUAL;

/**
 * The Class SLSelectByLinkTypeExecuteCommand.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLSelectByLinkTypeExecuteCommand extends SLSelectAbstractCommand {
	
	/** The select info. */
	private SLSelectByLinkTypeInfo selectInfo;
	
	/** The command do. */
	private SLSelectCommandDO commandDO;
	
	/**
	 * Instantiates a new sL select by link type execute command.
	 * 
	 * @param selectInfo the select info
	 * @param commandDO the command do
	 */
	SLSelectByLinkTypeExecuteCommand(SLSelectByLinkTypeInfo selectInfo, SLSelectCommandDO commandDO) {
		this.selectInfo = selectInfo;
		this.commandDO = commandDO;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectAbstractCommand#execute()
	 */
	@Override
	public void execute() throws SLGraphSessionException {
		
		try {
			
			Set<PNodeWrapper> nodeWrappers = new HashSet<PNodeWrapper>();
			commandDO.setNodeWrappers(nodeWrappers);
			
			List<PNodeWrapper> inputNodeWrappers = new ArrayList<PNodeWrapper>(commandDO.getPreviousNodeWrappers());
			if (!inputNodeWrappers.isEmpty()) {

				List<String> typeNames = new ArrayList<String>();
				List<SLSelectTypeInfo> typeInfoList = selectInfo.getTypeInfoList();
				for (SLSelectTypeInfo typeInfo : typeInfoList) {
					Collection<String> hierarchyTypeNames = SLQuerySupport.getHierarchyTypeNames(commandDO.getMetadata(), typeInfo.getName(), typeInfo.isSubTypes());
					typeNames.addAll(hierarchyTypeNames);
				}
				
		 		SLXPathStatementBuilder statementBuilder = new SLXPathStatementBuilder(commandDO.getTreeSession().getXPathRootPath() + "/links/*//*");
		 		Statement rootStatement = statementBuilder.getRootStatement();
		 		
				List<SLSelectByLinkInfo> byLinkInfoList = selectInfo.getByLinkInfoList();
				for (int i = 0; i < byLinkInfoList.size(); i++) {
					
					SLSelectByLinkInfo byLinkInfo = byLinkInfoList.get(i);
					
					Statement statement;
					if (i == 0) statement = rootStatement.openBracket();
					else statement = rootStatement.operator(OR).openBracket();
					
					String linkTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH);
					statement.condition().leftOperand(linkTypeHashPropName).operator(EQUAL).rightOperand(byLinkInfo.getName().hashCode());
					
					SLLinkTypeStatementInfo linkTypeStatementInfo = getLinkTypeStatementInfo(byLinkInfo.getName());
					if (linkTypeStatementInfo != null) {
						Statement byLinkTypeStatement = statement.operator(AND).openBracket();
						filterByWhereStatement(byLinkTypeStatement, linkTypeStatementInfo);
						byLinkTypeStatement.closeBracket();
					}
					
					Statement typeStatement = statement.operator(AND).openBracket();;
					SLSideType side = byLinkInfo.getSide();
					
					if (side.equals(SLSideType.A_SIDE) || side.equals(SLSideType.B_SIDE)) {
						String typeHashPropName = toInternalPropertyName(side.equals(SLSideType.A_SIDE) ? SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH : SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH);
						String idPropName = toInternalPropertyName(side.equals(SLSideType.A_SIDE) ? SLConsts.PROPERTY_NAME_TARGET_ID : SLConsts.PROPERTY_NAME_SOURCE_ID);
						for (int j = 0; j < typeNames.size(); j++) {
							Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
							String typeName = typeNames.get(j);
							condition.leftOperand(typeHashPropName).operator(EQUAL).rightOperand(typeName.hashCode());
						}
						typeStatement.closeBracket();
						typeStatement = statement.operator(AND).openBracket();
						for (int j = 0; j < inputNodeWrappers.size(); j++) {
							Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
							PNodeWrapper pNodeWrapper = inputNodeWrappers.get(j);
							condition.leftOperand(idPropName).operator(EQUAL).rightOperand(pNodeWrapper.getID());
						}
						typeStatement.closeBracket();
					}
					else {
						SLConditionalOperatorType operator = side.equals(SLSideType.ANY_SIDE) ? OR : AND;
						for (int j = 0; j < typeNames.size(); j++) {
							Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
							String typeName = typeNames.get(j);
							String sourceTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH);
							String targetTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH);
							condition.leftOperand(sourceTypeHashPropName).operator(EQUAL).rightOperand(typeName.hashCode())
								.operator(operator).condition().leftOperand(targetTypeHashPropName).operator(EQUAL).rightOperand(typeName.hashCode());
						}
						typeStatement.closeBracket();
						typeStatement = statement.operator(AND).openBracket();
						for (int j = 0; j < inputNodeWrappers.size(); j++) {
							Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
							PNodeWrapper pNodeWrapper = inputNodeWrappers.get(j);
							String sourceIdPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_ID);
							String targetIdPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_ID);
							condition.leftOperand(sourceIdPropName).operator(EQUAL).rightOperand(pNodeWrapper.getID())
								.operator(operator).condition().leftOperand(targetIdPropName).operator(EQUAL).rightOperand(pNodeWrapper.getID());
						}
						typeStatement.closeBracket();
					}
					statement.closeBracket();
				}
		 		
				SLPersistentTreeSession treeSession = commandDO.getTreeSession();
				String xpath = statementBuilder.getXPath();
				SLPersistentQuery query = treeSession.createQuery(xpath, SLPersistentQuery.TYPE_XPATH);
				Collection<PLinkNodeWrapper> pLinkNodeWrappers = SLQuerySupport.wrapLinkNodes(query.execute().getNodes());
				
				if (!pLinkNodeWrappers.isEmpty()) {
					
					for (PLinkNodeWrapper pLinkNodeWrapper : pLinkNodeWrappers) {

						int linkTypeHash = pLinkNodeWrapper.getLinkTypeHash();
						SLSelectByLinkInfo byLinkInfo = getByLinkInfo(byLinkInfoList, linkTypeHash);
						SLSideType side = byLinkInfo.getSide();
						
						if (side.equals(SLSideType.A_SIDE)) {
							String sourceID = pLinkNodeWrapper.getSourceID(); 
							SLPersistentNode pNode = treeSession.getNodeByID(sourceID);
							nodeWrappers.add(new PNodeWrapper(pNode));
						}
						else if (side.equals(SLSideType.B_SIDE)) {
							String targetID = pLinkNodeWrapper.getTargetID(); 
							SLPersistentNode pNode = treeSession.getNodeByID(targetID);
							nodeWrappers.add(new PNodeWrapper(pNode));
						}
						else if (side.equals(SLSideType.ANY_SIDE)) {
							String sourceID = pLinkNodeWrapper.getSourceID();
							String targetID = pLinkNodeWrapper.getTargetID();
							PNodeWrapper sourceNodeWrapper = SLQuerySupport.findPNodeWrapper(inputNodeWrappers, sourceID);
							PNodeWrapper targetNodeWrapper = SLQuerySupport.findPNodeWrapper(inputNodeWrappers, targetID);
							
							if (sourceNodeWrapper == null && targetNodeWrapper != null) {
								SLPersistentNode pNode = treeSession.getNodeByID(sourceID);
								nodeWrappers.add(new PNodeWrapper(pNode));
							}
							else if (targetNodeWrapper == null && sourceNodeWrapper != null) {
								SLPersistentNode pNode = treeSession.getNodeByID(targetID);
								nodeWrappers.add(new PNodeWrapper(pNode));
							}
							else {
								nodeWrappers.add(sourceNodeWrapper);
								nodeWrappers.add(targetNodeWrapper);
							}
						}
						else if (side.equals(SLSideType.BOTH_SIDES)) {
							String sourceID = pLinkNodeWrapper.getSourceID();
							SLPersistentNode pNode = treeSession.getNodeByID(sourceID);
							nodeWrappers.add(new PNodeWrapper(pNode));
						}
					}
				}
			}
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to execute " + this.getClass().getName() + " command.");
		}
	}

	/**
	 * Gets the by link info.
	 * 
	 * @param byLinkInfoList the by link info list
	 * @param linkTypeHash the link type hash
	 * 
	 * @return the by link info
	 */
	private SLSelectByLinkInfo getByLinkInfo(List<SLSelectByLinkInfo> byLinkInfoList, int linkTypeHash) {
		SLSelectByLinkInfo byLinkInfo = null;
		for (SLSelectByLinkInfo current : byLinkInfoList) {
			if (current.getName().hashCode() == linkTypeHash) {
				byLinkInfo = current;
				break;
			}
		}
		return byLinkInfo;
	}
	
	/**
	 * Gets the link type statement info.
	 * 
	 * @param linkTypeName the link type name
	 * 
	 * @return the link type statement info
	 */
	private SLLinkTypeStatementInfo getLinkTypeStatementInfo(String linkTypeName) {
		SLLinkTypeStatementInfo linkTypeStatementInfo = null;
		SLWhereByLinkTypeInfo whereInfo = selectInfo.getWhereByLinkTypeInfo();
		if (whereInfo != null) {
			for (SLWhereLinkTypeInfo linkTypeInfo : whereInfo.getWhereLinkTypeInfoList()) {
				if (linkTypeInfo.getName().equals(linkTypeName)) {
					linkTypeStatementInfo = linkTypeInfo.getLinkTypeStatementInfo();
				}
			}
		}
		return linkTypeStatementInfo;
	}
	
	/**
	 * Filter by where statement.
	 * 
	 * @param statement the statement
	 * @param linkTypeStatementInfo the link type statement info
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private void filterByWhereStatement(Statement statement, SLLinkTypeStatementInfo linkTypeStatementInfo) throws SLGraphSessionException, SLPersistentTreeSessionException {
		
		List<SLConditionInfo> conditionInfoList = linkTypeStatementInfo.getConditionInfoList();
		for (SLConditionInfo conditionInfo : conditionInfoList) {
			
			Statement conditionStatement;
			if (conditionInfo.getConditionalOperator() == null) {
				conditionStatement = statement.openBracket();
			}
			else {
				conditionStatement = statement.operator(conditionInfo.getConditionalOperator(), conditionInfo.isConditionalNotOperator()).openBracket();
			}
			
			if (conditionInfo.getInnerStatementInfo() == null) {
				
				SLWhereLinkTypeInfo linkTypeInfo = conditionInfo.getTypeInfo();
				String linkTypeName = linkTypeInfo.getName();
				String propertyName = SLCommonSupport.toUserPropertyName(conditionInfo.getPropertyName());
				
				String linkTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH); 
				conditionStatement.condition().leftOperand(linkTypeHashPropName).operator(EQUAL).rightOperand(linkTypeName.hashCode())
					.operator(AND).condition().leftOperand(propertyName).operator(conditionInfo.getRelationalOperator(), conditionInfo.isRelationalNotOperator()).rightOperand(conditionInfo.getValue());

			}
			else {
				filterByWhereStatement(conditionStatement, conditionInfo.getInnerStatementInfo());
			}
			conditionStatement.closeBracket();
		}
	}
}