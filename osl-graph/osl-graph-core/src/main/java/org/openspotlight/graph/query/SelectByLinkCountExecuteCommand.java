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

import static org.openspotlight.graph.query.ConditionalOperatorType.AND;
import static org.openspotlight.graph.query.ConditionalOperatorType.OR;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.graph.exception.MetaNodeTypeNotFoundException;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.query.info.SelectByLinkCountInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo.SLConditionInfo;
import org.openspotlight.storage.domain.StorageNode;

/**
 * The Class SLSelectByLinkCountExecuteCommand.
 * 
 * @author Vitor Hugo Chagas
 */
public class SelectByLinkCountExecuteCommand extends SelectAbstractCommand {

    /** The select info. */
    private final SelectByLinkCountInfo    selectInfo;

    /** The command do. */
    private final SelectCommandDO          commandDO;

    /** The node wrapper list map. */
    private Map<String, List<StorageNode>> nodeWrapperListMap;

    /** The metadata. */
    private final Metadata                 metadata;

    /**
     * Instantiates a new sL select by link count execute command.
     * 
     * @param selectByLinkCountInfo the select by link count info
     * @param commandDO the command do
     */
    public SelectByLinkCountExecuteCommand(
                                           final SelectByLinkCountInfo selectByLinkCountInfo,
                                           final SelectCommandDO commandDO) {
        selectInfo = selectByLinkCountInfo;
        this.commandDO = commandDO;
        metadata = commandDO.getMetadata();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        try {

            final Set<StorageNode> nodeWrappers = new HashSet<StorageNode>();
            commandDO.setNodeWrappers(nodeWrappers);

            final Set<SLWhereTypeInfo> whereTypeInfoSet = getWhereTypeInfoSet();
            if (commandDO.getPreviousNodeWrappers() != null) {
                nodeWrapperListMap = QuerySupport.mapNodesByType(commandDO
                        .getPreviousNodeWrappers());
            } else {
                nodeWrapperListMap = new HashMap<String, List<StorageNode>>();
                for (final SLWhereTypeInfo whereTypeInfo: whereTypeInfoSet) {
                    final List<StorageNode> wrappers = getStorageNodesOfType(whereTypeInfo
                            .getName());
                    nodeWrapperListMap.put(whereTypeInfo.getName(), wrappers);
                }
            }

            for (final SLWhereTypeInfo whereTypeInfo: whereTypeInfoSet) {

                final List<StorageNode> inputNodeWrappers = nodeWrapperListMap
                        .get(whereTypeInfo.getName());
                final LinkCountMapper mapper = new LinkCountMapper(whereTypeInfo,
                        inputNodeWrappers);
                mapper.map();

                final LinkCountEvaluator evaluator = new LinkCountEvaluator(
                        whereTypeInfo, mapper);
                for (final StorageNode nodeWrapper: inputNodeWrappers) {
                    final String id = nodeWrapper.getKeyAsString();
                    final boolean status = evaluator.evaluate(id);
                    if (status) {
                        nodeWrappers.add(nodeWrapper);
                    }
                }
            }
        } catch (final Exception e) {
            throw new QueryException("Error on attempt to execute "
                    + this.getClass().getName() + " command.");
        }
    }

    /**
     * Gets the where type info set.
     * 
     * @return the where type info set
     */
    private Set<SLWhereTypeInfo> getWhereTypeInfoSet()
            throws MetaNodeTypeNotFoundException {
        final Set<SLWhereTypeInfo> set = new HashSet<SLWhereTypeInfo>();
        for (final SLWhereTypeInfo whereTypeInfo: selectInfo.getWhereStatementInfo()
                .getWhereTypeInfoList()) {
            final List<String> typeNames = QuerySupport.getHierarchyTypeNames(
                    metadata, whereTypeInfo.getName(),
                    whereTypeInfo.isSubTypes());
            for (final String typeName: typeNames) {
                final SLWhereTypeInfo typeInfo = new SLWhereTypeInfo(typeName);
                typeInfo.setTypeStatementInfo(whereTypeInfo
                        .getTypeStatementInfo());
                set.add(typeInfo);
            }
        }
        return set;
    }

    /**
     * Gets the p node wrappers of type.
     * 
     * @param name the name
     * @return the p node wrappers of type @ the SL persistent tree session exception
     */
    private List<StorageNode> getStorageNodesOfType(final String name) {
        // List<StorageNode> pNodeWrappers = new ArrayList<StorageNode>();
        // XPathStatementBuilder statementBuilder = new
        // XPathStatementBuilder(commandDO.getTreeSession().getXPathRootPath()
        // + "/contexts//*");
        // Statement rootStatement = statementBuilder.getRootStatement();
        // String typePropName =
        // SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
        // rootStatement.condition().leftOperand(typePropName).operator(EQUAL).rightOperand(name);
        // String xpath = statementBuilder.getXPath();
        // SLPersistentQuery query =
        // commandDO.getTreeSession().createQuery(xpath,
        // SLPersistentQuery.TYPE_XPATH);
        // SLPersistentQueryResult result = query.execute();
        // Collection<StorageNode> pNodes = result.getNodes();
        // for (StorageNode pNode : pNodes) {
        // pNodeWrappers.add(new StorageNode(pNode));
        // }
        // return pNodeWrappers;
        throw new UnsupportedOperationException();
    }

    /**
     * The Class LinkCountMapper.
     * 
     * @author Vitor Hugo Chagas
     */
    class LinkCountMapper {

        /** The map. */
        private final Map<SLConditionInfo, Map<String, Integer>> map = new HashMap<SLConditionInfo, Map<String, Integer>>();

        /** The where type info. */
        private final SLWhereTypeInfo                            whereTypeInfo;

        /** The input node wrappers. */
        private final List<StorageNode>                          inputNodeWrappers;

        /**
         * Instantiates a new link count mapper.
         * 
         * @param whereTypeInfo the where type info
         * @param inputNodeWrappers the input node wrappers
         */
        private LinkCountMapper(final SLWhereTypeInfo whereTypeInfo,
                                final List<StorageNode> inputNodeWrappers) {
            this.whereTypeInfo = whereTypeInfo;
            this.inputNodeWrappers = inputNodeWrappers;
        }

        /**
         * Gets the link count.
         * 
         * @param conditionInfo the condition info
         * @param id the id
         * @return the link count
         */
        private int getLinkCount(final SLConditionInfo conditionInfo, final String id) {
            return map.get(conditionInfo).get(id);
        }

        /**
         * Map. @ the SL persistent tree session exception
         */
        private void map() {
            map(whereTypeInfo.getTypeStatementInfo());
        }

        /**
         * Map.
         * 
         * @param statementInfo the statement info @ the SL persistent tree session exception
         */
        private void map(final SLTypeStatementInfo statementInfo) {

            // List<SLConditionInfo> conditionInfoList =
            // statementInfo.getConditionInfoList();
            // for (SLConditionInfo conditionInfo : conditionInfoList) {
            //
            // if (conditionInfo.getInnerStatementInfo() == null) {
            //
            // if (inputNodeWrappers.isEmpty()) continue;
            //
            // Map<String, Integer> numberOcurrencesMap =
            // createNodeOccurencesMap(inputNodeWrappers);
            // map.put(conditionInfo, numberOcurrencesMap);
            //
            // XPathStatementBuilder statementBuilder = new
            // XPathStatementBuilder(
            // commandDO.getTreeSession().getXPathRootPath()
            // + "/links/*//*");
            // Statement rootStatement = statementBuilder.getRootStatement();
            //
            // SLSideType side = conditionInfo.getSide();
            // String linkTypeHashPropName =
            // toInternalPropertyName(SLConsts.PROPERTY_NAME_LINK_TYPE_HASH);
            // String idPropName =
            // toInternalPropertyName(side.equals(SLSideType.A_SIDE) ?
            // SLConsts.PROPERTY_NAME_SOURCE_ID :
            // SLConsts.PROPERTY_NAME_TARGET_ID);
            // rootStatement.condition().leftOperand(linkTypeHashPropName).operator(EQUAL).rightOperand(
            // conditionInfo.getLinkTypeName().hashCode());
            //
            // Statement statement = rootStatement.operator(AND).openBracket();
            // for (int j = 0; j < inputNodeWrappers.size(); j++) {
            // Condition condition = j == 0 ? statement.condition() :
            // statement.operator(OR).condition();
            // StorageNode pNodeWrapper = inputNodeWrappers.get(j);
            // condition.leftOperand(idPropName).operator(EQUAL).rightOperand(pNodeWrapper.getID());
            // }
            // statement.closeBracket();
            //
            // SLPersistentTreeSession treeSession = commandDO.getTreeSession();
            // String xpath = statementBuilder.getXPath();
            // SLPersistentQuery query = treeSession.createQuery(xpath,
            // SLPersistentQuery.TYPE_XPATH);
            // SLPersistentQueryResult result = query.execute();
            // Collection<StorageNode> wrappers =
            // getNodeWrappers(result.getNodes(), conditionInfo.getSide());
            // updateNumberOcurrences(numberOcurrencesMap, wrappers,
            // conditionInfo.getSide());
            // } else {
            // map(conditionInfo.getInnerStatementInfo());
            // }
            // }
            throw new UnsupportedOperationException();
        }

        /**
         * Gets the node wrappers.
         * 
         * @param pLinkNodes the link nodes
         * @param side the side
         * @return the node wrappers @ the SL persistent tree session exception
         */
        private Collection<StorageNode> getNodeWrappers(
                                                        final Collection<StorageNode> pLinkNodes, final SideType side) {
            // Collection<StorageNode> nodeWrappers = new
            // ArrayList<StorageNode>();
            // Collection<PLinkNodeWrapper> pLinkNodeWrappers =
            // QuerySupport.wrapLinkNodes(pLinkNodes);
            // for (PLinkNodeWrapper linkNodeWrapper : pLinkNodeWrappers) {
            // String id = side.equals(SLSideType.A_SIDE) ?
            // linkNodeWrapper.getSourceID() : linkNodeWrapper.getTargetID();
            // StorageNode pNode = commandDO.getTreeSession().getNodeByID(id);
            // StorageNode nodeWrapper = new StorageNode(pNode);
            // nodeWrappers.add(nodeWrapper);
            // }
            // return nodeWrappers;
            throw new UnsupportedOperationException();
        }

        /**
         * Update number ocurrences.
         * 
         * @param map the map
         * @param nodeWrappers the node wrappers
         * @param side the side @ the SL persistent tree session exception
         */
        private void updateNumberOcurrences(final Map<String, Integer> map,
                                            final Collection<StorageNode> nodeWrappers, final SideType side) {
            for (final StorageNode nodeWrapper: nodeWrappers) {
                final String id = nodeWrapper.getKeyAsString();
                final Integer occurences = map.get(id);
                map.put(id, occurences + 1);

            }
        }

        /**
         * Creates the node occurences map.
         * 
         * @param nodeWrappers the node wrappers
         * @return the map< string, integer> @ the SL persistent tree session exception
         */
        private Map<String, Integer> createNodeOccurencesMap(
                                                             final List<StorageNode> nodeWrappers) {
            final Map<String, Integer> map = new HashMap<String, Integer>();
            for (final StorageNode nodeWrapper: nodeWrappers) {
                final String id = nodeWrapper.getKeyAsString();
                map.put(id, 0);
            }
            return map;
        }
    }

    /**
     * The Class LinkCountEvaluator.
     * 
     * @author Vitor Hugo Chagas
     */
    class LinkCountEvaluator {

        /** The where type info. */
        private final SLWhereTypeInfo whereTypeInfo;

        /** The mapper. */
        private final LinkCountMapper mapper;

        /**
         * Instantiates a new link count evaluator.
         * 
         * @param whereTypeInfo the where type info
         * @param mapper the mapper
         */
        private LinkCountEvaluator(final SLWhereTypeInfo whereTypeInfo,
                                   final LinkCountMapper mapper) {
            this.whereTypeInfo = whereTypeInfo;
            this.mapper = mapper;
        }

        /**
         * Evaluate.
         * 
         * @param id the id
         * @return true, if successful
         */
        private boolean evaluate(final String id) {
            return evaluate(whereTypeInfo.getTypeStatementInfo(), id);
        }

        /**
         * Evaluate.
         * 
         * @param statementInfo the statement info
         * @param id the id
         * @return true, if successful
         */
        private boolean evaluate(final SLTypeStatementInfo statementInfo, final String id) {

            boolean status = false;

            final List<SLConditionInfo> conditionInfoList = statementInfo
                    .getConditionInfoList();
            for (final SLConditionInfo conditionInfo: conditionInfoList) {

                final ConditionalOperatorType conditionalOperator = conditionInfo
                        .getConditionalOperator();
                if (conditionalOperator != null) {
                    if (status && conditionalOperator.equals(OR)) { return true; }
                    if (!status && conditionalOperator.equals(AND)) { return false; }
                }

                if (conditionInfo.getInnerStatementInfo() == null) {
                    final int linkCount = mapper.getLinkCount(conditionInfo, id);
                    if (conditionInfo.getRelationalOperator().equals(
                            RelationalOperatorType.EQUAL)) {
                        status = linkCount == conditionInfo.getValue();
                    } else if (conditionInfo.getRelationalOperator().equals(
                            RelationalOperatorType.GREATER_THAN)) {
                        status = linkCount > conditionInfo.getValue();
                    } else if (conditionInfo.getRelationalOperator().equals(
                            RelationalOperatorType.GREATER_OR_EQUAL_THAN)) {
                        status = linkCount >= conditionInfo.getValue();
                    } else if (conditionInfo.getRelationalOperator().equals(
                            RelationalOperatorType.LESSER_THAN)) {
                        status = linkCount < conditionInfo.getValue();
                    } else if (conditionInfo.getRelationalOperator().equals(
                            RelationalOperatorType.LESSER_OR_EQUAL_THAN)) {
                        status = linkCount <= conditionInfo.getValue();
                    }
                } else {
                    status = evaluate(conditionInfo.getInnerStatementInfo(), id);
                }
            }
            return status;
        }
    }
}
