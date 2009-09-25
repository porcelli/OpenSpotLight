package org.openspotlight.graph.query;

import static org.openspotlight.graph.SLCommonSupport.toInternalPropertyName;
import static org.openspotlight.graph.query.SLConditionalOperatorType.AND;
import static org.openspotlight.graph.query.SLConditionalOperatorType.OR;
import static org.openspotlight.graph.query.SLRelationalOperatorType.EQUAL;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLCollatorSupport;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement;
import org.openspotlight.graph.query.SLXPathStatementBuilder.Statement.Condition;
import org.openspotlight.graph.query.info.SLSelectByLinkInfo;
import org.openspotlight.graph.query.info.SLSelectStatementInfo;
import org.openspotlight.graph.query.info.SLSelectTypeInfo;
import org.openspotlight.graph.query.info.SLWhereLinkTypeInfo;
import org.openspotlight.graph.query.info.SLWhereStatementInfo;
import org.openspotlight.graph.query.info.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo.SLLinkTypeConditionInfo;

public class SLSelectByLinkTypeCommand extends SLSelectAbstractCommand {

	/** The select info. */
	private SLSelectStatementInfo selectInfo;
	
	/** The command do. */
	private SLSelectCommandDO commandDO;
	
	/**
	 * Instantiates a new sL select by link type execute command.
	 * 
	 * @param selectInfo the select info
	 * @param commandDO the command do
	 */
	SLSelectByLinkTypeCommand(SLSelectStatementInfo selectInfo, SLSelectCommandDO commandDO) {
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

				List<SLSelectTypeInfo> selectTypeInfoList = selectInfo.getTypeInfoList();
		 		SLXPathStatementBuilder statementBuilder = new SLXPathStatementBuilder("//osl/links/*//*");
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
						for (int j = 0; j < selectTypeInfoList.size(); j++) {
							Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
							String typeName = selectTypeInfoList.get(j).getName();
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
						for (int j = 0; j < selectTypeInfoList.size(); j++) {
							Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
							String typeName = selectTypeInfoList.get(j).getName();
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
		SLWhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
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
		
		List<SLLinkTypeConditionInfo> conditionInfoList = linkTypeStatementInfo.getConditionInfoList();
		for (SLLinkTypeConditionInfo conditionInfo : conditionInfoList) {
			
			Statement conditionStatement;
			if (conditionInfo.getConditionalOperator() == null) {
				conditionStatement = statement.openBracket();
			}
			else {
				conditionStatement = statement.operator(conditionInfo.getConditionalOperator(), conditionInfo.isConditionalNotOperator()).openBracket();
			}
			
			if (conditionInfo.getInnerStatementInfo() == null) {
				
				SLWhereLinkTypeInfo linkTypeInfo = conditionInfo.getLinkTypeInfo();
				String linkTypeName = linkTypeInfo.getName();
				String propertyName = SLCommonSupport.toUserPropertyName(conditionInfo.getPropertyName());
				
				String linkTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH); 
				conditionStatement.condition().leftOperand(linkTypeHashPropName).operator(EQUAL).rightOperand(linkTypeName.hashCode());
				
				Statement propStatement = conditionStatement.operator(AND).openBracket();
				propStatement.condition().leftOperand(propertyName).operator(conditionInfo.getRelationalOperator(), conditionInfo.isRelationalNotOperator()).rightOperand(conditionInfo.getValue());
				int collatorStrength = commandDO.getCollatorStrength();
				if (conditionInfo.getValue() instanceof String && collatorStrength != Collator.IDENTICAL) {
					propertyName = SLCollatorSupport.getCollatorPropName(conditionInfo.getPropertyName(), commandDO.getCollatorStrength());
					String value = SLCollatorSupport.getCollatorKey(commandDO.getCollatorStrength(), conditionInfo.getValue().toString());
					propStatement.operator(OR).condition().leftOperand(propertyName).operator(conditionInfo.getRelationalOperator(), conditionInfo.isRelationalNotOperator()).rightOperand(value);
				}
				propStatement.closeBracket();
			}
			else {
				filterByWhereStatement(conditionStatement, conditionInfo.getInnerStatementInfo());
			}
			conditionStatement.closeBracket();
		}
	}
}
