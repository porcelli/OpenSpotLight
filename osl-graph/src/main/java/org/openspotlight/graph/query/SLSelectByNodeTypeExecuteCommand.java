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

import static org.openspotlight.graph.query.SLConditionalOperatorType.AND;
import static org.openspotlight.graph.query.SLConditionalOperatorType.OR;
import static org.openspotlight.graph.query.SLRelationalOperatorType.EQUAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement.Condition;
import org.openspotlight.graph.query.info.SLSelectByNodeTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo;
import org.openspotlight.graph.query.info.SLSelectByNodeTypeInfo.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;

/**
 * The Class SLSelectByNodeTypeExecuteCommand.
 * 
 * @author Vitor Hugo Chagas
 */
/**
 * @author vitorchagas
 *
 */
public class SLSelectByNodeTypeExecuteCommand extends SLSelectAbstractCommand {
	
	/** The select info. */
	private SLSelectByNodeTypeInfo selectInfo;
	
	/** The command do. */
	private SLSelectCommandDO commandDO;
	
	/** The node wrapper list map. */
	private Map<String, List<PNodeWrapper>> nodeWrapperListMap;
	
	/**
	 * Instantiates a new sL select by node type execute command.
	 * 
	 * @param selectInfo the select info
	 * @param commandDO the command do
	 */
	SLSelectByNodeTypeExecuteCommand(SLSelectByNodeTypeInfo selectInfo, SLSelectCommandDO commandDO) {
		this.selectInfo = selectInfo;
		this.commandDO = commandDO;
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.query.SLSelectAbstractCommand#execute()
	 */
	@Override
	public void execute() throws SLGraphSessionException {
		
		try {
			
			if (commandDO.getPreviousNodeWrappers() != null) {
				nodeWrapperListMap = SLSelectCommandSupport.mapNodesByType(commandDO.getPreviousNodeWrappers());
			}
			
			List<SLSelectTypeInfo> typeInfoList = selectInfo.getTypeInfoList();
			String typePropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
			SLWhereByNodeTypeInfo whereStatementInfo = selectInfo.getWhereStatementInfo();

			List<String> typesNotFiltered = new ArrayList<String>();
	 		for (SLSelectTypeInfo typeInfo : typeInfoList) {
	 			List<String> hierarchyTypeNames = SLSelectCommandSupport.getHierarchyTypeNames(commandDO.getMetadata(), typeInfo.getName(), typeInfo.isSubTypes());
	 			typesNotFiltered.addAll(hierarchyTypeNames);
			}

	 		SLXPathStatementBuilder statementBuilder = new SLXPathStatementBuilder("//osl/contexts//*");
	 		Statement rootStatement = statementBuilder.getRootStatement();
	 		
	 		if (whereStatementInfo != null) {
	 	 		List<SLWhereTypeInfo> whereTypeInfoList = whereStatementInfo.getWhereTypeInfoList();
	 			if (whereTypeInfoList != null && !whereTypeInfoList.isEmpty()) {
	 				Map<String, SLWhereTypeInfo> whereTypeInfoMap = getWhereTypeInfoMap();
	 				List<SLWhereTypeInfo> list = new ArrayList<SLWhereTypeInfo>(whereTypeInfoMap.values());
	 				for (int i = 0; i < list.size(); i++) {
	 					SLWhereTypeInfo typeInfo = list.get(i);
	 					Statement typeStatement;
	 					if (i > 0) typeStatement = rootStatement.operator(OR).openBracket();
	 					else typeStatement = rootStatement.openBracket(); 
	 					Statement typeFilterStatement = typeStatement.condition()
	 						.leftOperand(typePropName).operator(EQUAL).rightOperand(typeInfo.getName())
	 						.operator(AND).openBracket();
	 					filterByWhereStatement(typeFilterStatement, typeInfo.getName(), typeInfo.getTypeStatementInfo());
	 					typeFilterStatement.closeBracket();
	 					typeStatement.closeBracket();
	 					typesNotFiltered.remove(typeInfo.getName());
					}
	 			}
	 		}
	 		
	 		if (commandDO.getPreviousNodeWrappers() == null) {
 				for (int i = 0; i < typesNotFiltered.size(); i++) {
	 				Condition condition;
	 				if (rootStatement.getConditionCount() == 0) {
	 					condition = rootStatement.condition();
	 				}
	 				else {
	 					condition = rootStatement.operator(OR).condition();
	 				}
	 				condition.leftOperand(typePropName).operator(EQUAL).rightOperand(typesNotFiltered.get(i));
 				}
	 		}
	 		
	 		statementBuilder.setOrderBy(typePropName);

			SLPersistentTreeSession treeSession = commandDO.getTreeSession();
			String xpath = statementBuilder.getXPath();
			SLPersistentQuery query = treeSession.createQuery(xpath, SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			
			Set<PNodeWrapper> pNodeWrappers = SLSelectCommandSupport.wrapNodes(result.getNodes());
			commandDO.setNodeWrappers(pNodeWrappers);
			
			if (commandDO.getPreviousNodeWrappers() != null) {
				for (int i = 0; i < typesNotFiltered.size(); i++) {
					String typeName = typesNotFiltered.get(i);
					List<PNodeWrapper> typeNodeWrappers = nodeWrapperListMap.get(typeName);
					if (typeNodeWrappers != null) {
						pNodeWrappers.addAll(typeNodeWrappers);
					}
				}
			}
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to execute " + this.getClass().getName() + " command.");
		}
	}
	
	/**
	 * Gets the where type info map.
	 * 
	 * @return the where type info map
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private Map<String, SLWhereTypeInfo> getWhereTypeInfoMap() throws SLGraphSessionException {
		Map<String, SLWhereTypeInfo> map = new HashMap<String, SLWhereTypeInfo>();
		List<SLWhereTypeInfo> list = selectInfo.getWhereStatementInfo().getWhereTypeInfoList();
		for (SLWhereTypeInfo whereTypeInfo : list) {
			List<String> typeNames = SLSelectCommandSupport.getHierarchyTypeNames(commandDO.getMetadata(), whereTypeInfo.getName(), whereTypeInfo.isSubTypes());
			for (String typeName : typeNames) {
				if (!map.containsKey(typeName)) {
					SLWhereTypeInfo typeInfo = new SLWhereTypeInfo(typeName);
					typeInfo.setTypeStatementInfo(whereTypeInfo.getTypeStatementInfo());
					map.put(typeName, typeInfo);
				}
			}
		}
		return map;
	}
	
	private void filterByWhereStatement(Statement statement, String typeName, SLTypeStatementInfo typeStatementInfo) throws SLGraphSessionException, SLPersistentTreeSessionException {
		
		List<SLConditionInfo> conditionInfoList = typeStatementInfo.getConditionInfoList();
		
		for (SLConditionInfo conditionInfo : conditionInfoList) {

			Statement conditionStatement;
			if (conditionInfo.getConditionalOperator() == null) {
				conditionStatement = statement.openBracket();
			}
			else {
				conditionStatement = statement.operator(conditionInfo.getConditionalOperator(), conditionInfo.isConditionalNotOperator()).openBracket();
			}
			
			if (conditionInfo.getInnerStatementInfo() == null) {
				Statement idStatement = null;
				if (commandDO.getPreviousNodeWrappers() != null) {
					List<PNodeWrapper> pNodeWrappers = nodeWrapperListMap.get(typeName);
					if (pNodeWrappers != null && !pNodeWrappers.isEmpty()) {
						idStatement = conditionStatement.openBracket();
						for (int j = 0; j < pNodeWrappers.size(); j++) {
							PNodeWrapper pNodeWrapper = pNodeWrappers.get(j);
							Condition idCondition;
							if (j == 0) idCondition = idStatement.condition();
							else idCondition = idStatement.operator(OR).condition();
							idCondition.leftOperand("jcr:uuid").operator(EQUAL, conditionInfo.isRelationalNotOperator()).rightOperand(pNodeWrapper.getId());
						}
						idStatement.closeBracket();
					}
				}
				Condition condition;
				if (idStatement == null) {
					condition = conditionStatement.condition();
				}
				else {
					condition = conditionStatement.operator(AND).condition();
				}
				String propertyName = SLCommonSupport.toUserPropertyName(conditionInfo.getPropertyName());
				condition.leftOperand(propertyName).operator(conditionInfo.getRelationalOperator()).rightOperand(conditionInfo.getValue());
			}
			else {
				filterByWhereStatement(conditionStatement, typeName, conditionInfo.getInnerStatementInfo());
			}
			
			conditionStatement.closeBracket();
		}
	}
}


