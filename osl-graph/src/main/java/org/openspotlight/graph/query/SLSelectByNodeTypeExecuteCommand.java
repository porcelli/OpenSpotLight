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

import static org.openspotlight.common.util.StringBuilderUtil.append;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.info.SLSelectByNodeTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo;
import org.openspotlight.graph.query.info.SLSelectByNodeTypeInfo.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByNodeTypeInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;

// TODO: Auto-generated Javadoc
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
			StringBuilder statement = new StringBuilder("//osl/contexts//*");
			SLWhereByNodeTypeInfo whereStatementInfo = selectInfo.getWhereStatementInfo();

			List<String> typesNotFiltered = new ArrayList<String>();
	 		for (SLSelectTypeInfo typeInfo : typeInfoList) {
	 			List<String> hierarchyTypeNames = SLSelectCommandSupport.getHierarchyTypeNames(commandDO.getMetadata(), typeInfo.getName(), typeInfo.isSubTypes());
	 			typesNotFiltered.addAll(hierarchyTypeNames);
			}

	 		statement.append('['); 
	 		
	 		StringBuilder filterStatement = new StringBuilder();
	 		if (whereStatementInfo != null) {
	 	 		List<SLWhereTypeInfo> whereTypeInfoList = whereStatementInfo.getWhereTypeInfoList();
	 			if (whereTypeInfoList != null && !whereTypeInfoList.isEmpty()) {
	 				filterStatement.append('(');
	 				Map<String, SLWhereTypeInfo> whereTypeInfoMap = getWhereTypeInfoMap();
	 				for (SLWhereTypeInfo whereTypeInfo : whereTypeInfoMap.values()) {
	 					if (filterStatement.length() > 1) filterStatement.append(" or ");
	 					filterByWhereStatement(filterStatement, whereTypeInfo.getName(), whereTypeInfo.getTypeStatementInfo());
	 					typesNotFiltered.remove(whereTypeInfo.getName());
					}
	 				filterStatement.append(')');
	 				statement.append(filterStatement);
	 			}
	 		}
	 		
	 		if (commandDO.getPreviousNodeWrappers() == null) {
	 			if (!typesNotFiltered.isEmpty()) {
	 				if (filterStatement.length() > 0) {
	 					statement.append(" or ");
	 				}
	 				for (int i = 0; i < typesNotFiltered.size(); i++) {
	 					if (i > 0) statement.append(" or ");
	 					String typeName = typesNotFiltered.get(i);
	 					append(statement, typePropName, " = ", Strings.quote(typeName));
	 				}
	 			}
	 		}
	 			
			statement.append(']');
			statement.append(" order by @").append(typePropName);

			SLPersistentTreeSession treeSession = commandDO.getTreeSession();
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			
			Set<PNodeWrapper> pNodeWrappers = SLSelectCommandSupport.wrapNodes(result.getNodes());
			commandDO.setNodeWrappers(pNodeWrappers);
			
			if (commandDO.getPreviousNodeWrappers() != null) {
				for (int i = 0; i < typesNotFiltered.size(); i++) {
					if (i > 0) statement.append(" or ");
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
	
	
	/**
	 * Filter by where statement.
	 * 
	 * @param statement the statement
	 * @param typeName the type name
	 * @param typeStatementInfo the type statement info
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private void filterByWhereStatement(StringBuilder statement, String typeName, SLTypeStatementInfo typeStatementInfo) throws SLGraphSessionException, SLPersistentTreeSessionException {
		
		String typePropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
		List<SLConditionInfo> conditionInfoList = typeStatementInfo.getConditionInfoList();

		for (SLConditionInfo conditionInfo : conditionInfoList) {

			SLConditionalOperatorType conditionalOperator = conditionInfo.getConditionalOperator();
			if (conditionalOperator != null) {
				append(statement, ' ', conditionalOperator.symbol().toLowerCase(), ' ');
			}
			
			if (conditionInfo.getInnerStatementInfo() == null) {
				statement.append('(');
				String propertyName = SLCommonSupport.toUserPropertyName(conditionInfo.getPropertyName());
				SLRelationalOperatorType relationalOperator = conditionInfo.getRelationalOperator();
				String expression = relationalOperator.xPathExpression(propertyName, conditionInfo.getValue(), conditionInfo.isRelationalNotOperator());
				StringBuilder filterStatement = new StringBuilder(); 
				if (commandDO.getPreviousNodeWrappers() != null) {
					List<PNodeWrapper> pNodeWrappers = nodeWrapperListMap.get(typeName);
					if (pNodeWrappers != null && !pNodeWrappers.isEmpty()) {
						filterStatement.append('(');
						for (int j = 0; j < pNodeWrappers.size(); j++) {
							PNodeWrapper pNodeWrapper = pNodeWrappers.get(j);
							if (j > 0) filterStatement.append(" or ");
							append(filterStatement, "jcr:uuid = ", Strings.quote(pNodeWrapper.getId()));
						}
						filterStatement.append(')');
					}
				}
				else {
					append(filterStatement, typePropName, " = ", Strings.quote(typeName));
				}
				
				if (filterStatement.length() > 0) {
					statement.append('(');
					append(statement, filterStatement, " and ", expression);
					statement.append(')');
				}
				statement.append(')');
			}
			else {
				filterByWhereStatement(statement, typeName, conditionInfo.getInnerStatementInfo());
			}
		}
	}
}
