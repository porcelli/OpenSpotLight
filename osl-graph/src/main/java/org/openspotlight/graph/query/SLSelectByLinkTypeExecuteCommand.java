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
import static org.openspotlight.graph.SLCommonSupport.toInternalPropertyName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo.SLSelectByLinkInfo;
import org.openspotlight.graph.query.info.SLSelectByLinkTypeInfo.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo.SLConditionInfo;


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
					Collection<String> hierarchyTypeNames = SLSelectCommandSupport.getHierarchyTypeNames(commandDO.getMetadata(), typeInfo.getName(), typeInfo.isSubTypes());
					typeNames.addAll(hierarchyTypeNames);
				}
				
				StringBuilder statement = new StringBuilder("//osl/links/*//*");
				statement.append('[');

				List<SLSelectByLinkInfo> byLinkInfoList = selectInfo.getByLinkInfoList();
				for (int i = 0; i < byLinkInfoList.size(); i++) {

					if (i > 0) statement.append(" or ");
					statement.append('(');
					SLSelectByLinkInfo byLinkInfo = byLinkInfoList.get(i);
					append(statement, toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH), " = ", byLinkInfo.getName().hashCode());
					
					SLLinkTypeStatementInfo linkTypeStatementInfo = getLinkTypeStatementInfo(byLinkInfo.getName());
					if (linkTypeStatementInfo != null) {
						append(statement, " and (");
						filterByWhereStatement(statement, linkTypeStatementInfo);
						append(statement, ')');
					}
					
					SLSideType side = byLinkInfo.getSide();
					append(statement, " and ");
					
					if (side.equals(SLSideType.A_SIDE) || side.equals(SLSideType.B_SIDE)) {
						String typeHashPropName = toInternalPropertyName(side.equals(SLSideType.A_SIDE) ? SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH : SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH);
						String idPropName = toInternalPropertyName(side.equals(SLSideType.A_SIDE) ? SLConsts.PROPERTY_NAME_TARGET_ID : SLConsts.PROPERTY_NAME_SOURCE_ID); 
						statement.append("(");
						for (int j = 0; j < typeNames.size(); j++) {
							if (j > 0) statement.append(" or ");
							String typeName = typeNames.get(j);
							append(statement, typeHashPropName, " = ", typeName.hashCode());
						}
						statement.append(") and (");
						for (int j = 0; j < inputNodeWrappers.size(); j++) {
							if (j > 0) statement.append(" or ");
							PNodeWrapper pNodeWrapper = inputNodeWrappers.get(j);
							append(statement, idPropName, " = ", Strings.quote(pNodeWrapper.getId()));
						}
						statement.append(')');
					}
					else {
						String operator = side.equals(SLSideType.ANY_SIDE) ? " or " : " and ";
						statement.append("(");
						for (int j = 0; j < typeNames.size(); j++) {
							if (j > 0) statement.append(" or ");
							String typeName = typeNames.get(j);
							append(statement, '(', toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH), " = ", typeName.hashCode(), 
								operator, toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH), " = ", typeName.hashCode(), ')');
						}
						statement.append(") and (");
						for (int j = 0; j < inputNodeWrappers.size(); j++) {
							if (j > 0) statement.append(" or ");
							PNodeWrapper pNodeWrapper = inputNodeWrappers.get(j);
							append(statement, '(', toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_ID), " = ", Strings.quote(pNodeWrapper.getId()), 
								operator, toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_ID), " = ", Strings.quote(pNodeWrapper.getId()), ')');
						}
						statement.append(')');
					}
					statement.append(')');
				}
				statement.append(']');
				
				SLPersistentTreeSession treeSession = commandDO.getTreeSession();
				SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
				Collection<PLinkNodeWrapper> pLinkNodeWrappers = SLSelectCommandSupport.wrapLinkNodes(query.execute().getNodes());
				
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
							PNodeWrapper sourceNodeWrapper = SLSelectCommandSupport.findPNodeWrapper(inputNodeWrappers, sourceID);
							PNodeWrapper targetNodeWrapper = SLSelectCommandSupport.findPNodeWrapper(inputNodeWrappers, targetID);
							
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
	private void filterByWhereStatement(StringBuilder statement, SLLinkTypeStatementInfo linkTypeStatementInfo) throws SLGraphSessionException, SLPersistentTreeSessionException {
		
		List<SLConditionInfo> conditionInfoList = linkTypeStatementInfo.getConditionInfoList();
		for (SLConditionInfo conditionInfo : conditionInfoList) {

			SLConditionalOperatorType conditionalOperator = conditionInfo.getConditionalOperator();
			if (conditionalOperator != null) {
				append(statement, ' ', conditionalOperator.symbol().toLowerCase(), ' ');
			}
			
			if (conditionInfo.getInnerStatementInfo() == null) {
				statement.append('(');

				SLWhereLinkTypeInfo linkTypeInfo = conditionInfo.getTypeInfo();
				String linkTypeName = linkTypeInfo.getName();
				String propertyName = SLCommonSupport.toUserPropertyName(conditionInfo.getPropertyName());
				
				SLOperatorType operator = conditionInfo.getOperator();
				String expression = operator.xPathExpression(propertyName, conditionInfo.getValue());

				append(statement, toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH), " = ", linkTypeName.hashCode());
				append(statement, " and ", expression);
					
				statement.append(')');
			}
			else {
				filterByWhereStatement(statement, conditionInfo.getInnerStatementInfo());
			}
		}
	}
	
}