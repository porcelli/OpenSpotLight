/**
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

import java.util.List;

import org.openspotlight.graph.query.XPathStatementBuilder.Statement;
import org.openspotlight.graph.query.info.SelectByLinkInfo;
import org.openspotlight.graph.query.info.SelectStatementInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo;
import org.openspotlight.graph.query.info.WhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereStatementInfo;

public class SelectByLinkTypeCommand extends SelectAbstractCommand {

    /** The select info. */
    private SelectStatementInfo selectInfo;

    /** The command do. */
    private SelectCommandDO     commandDO;

    /**
     * Instantiates a new sL select by link type execute command.
     * 
     * @param selectInfo the select info
     * @param commandDO the command do
     */
    SelectByLinkTypeCommand(
                               SelectStatementInfo selectInfo, SelectCommandDO commandDO ) {
        this.selectInfo = selectInfo;
        this.commandDO = commandDO;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.query.SLSelectAbstractCommand#execute()
     */
    @Override
    public void execute() {
    	throw new UnsupportedOperationException();
//        try {
//
//            Set<StorageNode> nodeWrappers = new HashSet<StorageNode>();
//            commandDO.setNodeWrappers(nodeWrappers);
//
//            List<StorageNode> inputNodeWrappers = new ArrayList<StorageNode>(commandDO.getPreviousNodeWrappers());
//            if (!inputNodeWrappers.isEmpty()) {
//
//                List<SelectTypeInfo> selectTypeInfoList = selectInfo.getTypeInfoList();
//                XPathStatementBuilder statementBuilder = new XPathStatementBuilder(
//                                                                                       commandDO.getTreeSession().getXPathRootPath()
//                                                                                       + "/links/*//*");
//                Statement rootStatement = statementBuilder.getRootStatement();
//
//                List<SelectByLinkInfo> byLinkInfoList = selectInfo.getByLinkInfoList();
//                for (int i = 0; i < byLinkInfoList.size(); i++) {
//
//                    SelectByLinkInfo byLinkInfo = byLinkInfoList.get(i);
//
//                    Statement statement;
//                    if (i == 0) statement = rootStatement.openBracket();
//                    else statement = rootStatement.operator(OR).openBracket();
//
//                    String linkTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH);
//                    statement.condition().leftOperand(linkTypeHashPropName).operator(EQUAL).rightOperand(
//                                                                                                         byLinkInfo.getName().hashCode());
//
//                    SLLinkTypeStatementInfo linkTypeStatementInfo = getLinkTypeStatementInfo(byLinkInfo.getName());
//                    if (linkTypeStatementInfo != null) {
//                        Statement byLinkTypeStatement = statement.operator(AND).openBracket();
//                        filterByWhereStatement(byLinkTypeStatement, linkTypeStatementInfo);
//                        byLinkTypeStatement.closeBracket();
//                    }
//
//                    Statement typeStatement = statement.operator(AND).openBracket();
//                    ;
//                    SLSideType side = byLinkInfo.getSide();
//
//                    if (side.equals(SLSideType.A_SIDE) || side.equals(SLSideType.B_SIDE)) {
//                        String typeHashPropName = toInternalPropertyName(side.equals(SLSideType.A_SIDE) ? SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH : SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH);
//                        String idPropName = toInternalPropertyName(side.equals(SLSideType.A_SIDE) ? SLConsts.PROPERTY_NAME_TARGET_ID : SLConsts.PROPERTY_NAME_SOURCE_ID);
//                        for (int j = 0; j < selectTypeInfoList.size(); j++) {
//                            Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
//                            String typeName = selectTypeInfoList.get(j).getName();
//                            condition.leftOperand(typeHashPropName).operator(EQUAL).rightOperand(typeName.hashCode());
//                        }
//                        typeStatement.closeBracket();
//                        typeStatement = statement.operator(AND).openBracket();
//                        for (int j = 0; j < inputNodeWrappers.size(); j++) {
//                            Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
//                            StorageNode pNodeWrapper = inputNodeWrappers.get(j);
//                            condition.leftOperand(idPropName).operator(EQUAL).rightOperand(pNodeWrapper.getID());
//                        }
//                        typeStatement.closeBracket();
//                    } else {
//                        ConditionalOperatorType operator = side.equals(SLSideType.ANY_SIDE) ? OR : AND;
//                        for (int j = 0; j < selectTypeInfoList.size(); j++) {
//                            Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
//                            String typeName = selectTypeInfoList.get(j).getName();
//                            String sourceTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_TYPE_HASH);
//                            String targetTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_TYPE_HASH);
//                            condition.leftOperand(sourceTypeHashPropName).operator(EQUAL).rightOperand(typeName.hashCode()).operator(
//                                                                                                                                     operator).condition().leftOperand(
//                                                                                                                                                                       targetTypeHashPropName).operator(
//                                                                                                                                                                                                        EQUAL).rightOperand(
//                                                                                                                                                                                                                            typeName.hashCode());
//                        }
//                        typeStatement.closeBracket();
//                        typeStatement = statement.operator(AND).openBracket();
//                        for (int j = 0; j < inputNodeWrappers.size(); j++) {
//                            Condition condition = j == 0 ? typeStatement.condition() : typeStatement.operator(OR).condition();
//                            StorageNode pNodeWrapper = inputNodeWrappers.get(j);
//                            String sourceIdPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_ID);
//                            String targetIdPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_ID);
//                            condition.leftOperand(sourceIdPropName).operator(EQUAL).rightOperand(pNodeWrapper.getID()).operator(
//                                                                                                                                operator).condition().leftOperand(
//                                                                                                                                                                  targetIdPropName).operator(
//                                                                                                                                                                                             EQUAL).rightOperand(
//                                                                                                                                                                                                                 pNodeWrapper.getID());
//                        }
//                        typeStatement.closeBracket();
//                    }
//                    statement.closeBracket();
//                }
//
//                SLPersistentTreeSession treeSession = commandDO.getTreeSession();
//                String xpath = statementBuilder.getXPath();
//                SLPersistentQuery query = treeSession.createQuery(xpath, SLPersistentQuery.TYPE_XPATH);
//                Collection<PLinkNodeWrapper> pLinkNodeWrappers = SLQuerySupport.wrapLinkNodes(query.execute().getNodes());
//
//                if (!pLinkNodeWrappers.isEmpty()) {
//
//                    for (PLinkNodeWrapper pLinkNodeWrapper : pLinkNodeWrappers) {
//
//                        int linkTypeHash = pLinkNodeWrapper.getLinkTypeHash();
//                        SelectByLinkInfo byLinkInfo = getByLinkInfo(byLinkInfoList, linkTypeHash);
//                        SLSideType side = byLinkInfo.getSide();
//
//                        if (side.equals(SLSideType.A_SIDE)) {
//                            String sourceID = pLinkNodeWrapper.getSourceID();
//                            SLPersistentNode pNode = treeSession.getNodeByID(sourceID);
//                            nodeWrappers.add(new StorageNode(pNode));
//                        } else if (side.equals(SLSideType.B_SIDE)) {
//                            String targetID = pLinkNodeWrapper.getTargetID();
//                            SLPersistentNode pNode = treeSession.getNodeByID(targetID);
//                            nodeWrappers.add(new StorageNode(pNode));
//                        } else if (side.equals(SLSideType.ANY_SIDE)) {
//                            String sourceID = pLinkNodeWrapper.getSourceID();
//                            String targetID = pLinkNodeWrapper.getTargetID();
//                            StorageNode sourceNodeWrapper = SLQuerySupport.findStorageNode(inputNodeWrappers, sourceID);
//                            StorageNode targetNodeWrapper = SLQuerySupport.findStorageNode(inputNodeWrappers, targetID);
//
//                            if (sourceNodeWrapper == null && targetNodeWrapper != null) {
//                                SLPersistentNode pNode = treeSession.getNodeByID(sourceID);
//                                nodeWrappers.add(new StorageNode(pNode));
//                            } else if (targetNodeWrapper == null && sourceNodeWrapper != null) {
//                                SLPersistentNode pNode = treeSession.getNodeByID(targetID);
//                                nodeWrappers.add(new StorageNode(pNode));
//                            } else {
//                                nodeWrappers.add(sourceNodeWrapper);
//                                nodeWrappers.add(targetNodeWrapper);
//                            }
//                        } else if (side.equals(SLSideType.BOTH_SIDES)) {
//                            String sourceID = pLinkNodeWrapper.getSourceID();
//                            SLPersistentNode pNode = treeSession.getNodeByID(sourceID);
//                            nodeWrappers.add(new StorageNode(pNode));
//                        }
//                    }
//                }
//            }
//        } catch (SLException e) {
//            throw new QueryException("Error on attempt to execute " + this.getClass().getName() + " command.");
//        }
    }

    /**
     * Gets the by link info.
     * 
     * @param byLinkInfoList the by link info list
     * @param linkTypeHash the link type hash
     * @return the by link info
     */
    private SelectByLinkInfo getByLinkInfo( List<SelectByLinkInfo> byLinkInfoList,
                                              int linkTypeHash ) {
        SelectByLinkInfo byLinkInfo = null;
        for (SelectByLinkInfo current : byLinkInfoList) {
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
     * @return the link type statement info
     */
    private SLLinkTypeStatementInfo getLinkTypeStatementInfo( String linkTypeName ) {
        SLLinkTypeStatementInfo linkTypeStatementInfo = null;
        WhereStatementInfo whereInfo = selectInfo.getWhereStatementInfo();
        if (whereInfo != null) {
            for (WhereLinkTypeInfo linkTypeInfo : whereInfo.getWhereLinkTypeInfoList()) {
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
     * @throws SLPersistentTreeSessionException the SL persistent tree session exception
     */
    private void filterByWhereStatement( Statement statement,
                                         SLLinkTypeStatementInfo linkTypeStatementInfo ){

//        List<SLLinkTypeConditionInfo> conditionInfoList = linkTypeStatementInfo.getConditionInfoList();
//        for (SLLinkTypeConditionInfo conditionInfo : conditionInfoList) {
//
//            Statement conditionStatement;
//            if (conditionInfo.getConditionalOperator() == null) {
//                conditionStatement = statement.openBracket();
//            } else {
//                conditionStatement = statement.operator(conditionInfo.getConditionalOperator(),
//                                                        conditionInfo.isConditionalNotOperator()).openBracket();
//            }
//
//            if (conditionInfo.getInnerStatementInfo() == null) {
//
//                WhereLinkTypeInfo linkTypeInfo = conditionInfo.getLinkTypeInfo();
//                String linkTypeName = linkTypeInfo.getName();
//                String propertyName = SLCommonSupport.toUserPropertyName(conditionInfo.getPropertyName());
//
//                String linkTypeHashPropName = toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH);
//                conditionStatement.condition().leftOperand(linkTypeHashPropName).operator(EQUAL).rightOperand(
//                                                                                                              linkTypeName.hashCode());
//
//                Statement propStatement = conditionStatement.operator(AND).openBracket();
//                propStatement.condition().leftOperand(propertyName).operator(conditionInfo.getRelationalOperator(),
//                                                                             conditionInfo.isRelationalNotOperator()).rightOperand(
//                                                                                                                                   conditionInfo.getValue());
//                int collatorStrength = commandDO.getCollatorStrength();
//                if (conditionInfo.getValue() instanceof String && collatorStrength != Collator.IDENTICAL) {
//                    propertyName = SLCollatorSupport.getCollatorKeyPropName(conditionInfo.getPropertyName(),
//                                                                            commandDO.getCollatorStrength());
//                    String value = SLCollatorSupport.getCollatorKey(commandDO.getCollatorStrength(),
//                                                                    conditionInfo.getValue().toString());
//                    propStatement.operator(OR).condition().leftOperand(propertyName).operator(
//                                                                                              conditionInfo.getRelationalOperator(),
//                                                                                              conditionInfo.isRelationalNotOperator()).rightOperand(
//                                                                                                                                                    value);
//                }
//                propStatement.closeBracket();
//            } else {
//                filterByWhereStatement(conditionStatement, conditionInfo.getInnerStatementInfo());
//            }
//            conditionStatement.closeBracket();
//        }
    	throw new UnsupportedOperationException();
    }
}
