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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openspotlight.graph.exception.MetaNodeTypeNotFoundException;
import org.openspotlight.graph.exception.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.query.info.OrderByStatementInfo;
import org.openspotlight.graph.query.info.SelectStatementInfo;
import org.openspotlight.graph.query.info.WhereByLinkCountInfo.SLWhereTypeInfo.SLTypeStatementInfo;
import org.openspotlight.graph.query.info.WhereByLinkTypeInfo.SLWhereLinkTypeInfo.SLLinkTypeStatementInfo;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;

/**
 * The Class SLQueryImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class QueryApiImpl extends AbstractSLQuery implements QueryApi {

    /** The Constant LOGGER. */
    static final Logger          LOGGER           = Logger.getLogger(QueryApiImpl.class);

    /** The cache. */
    private QueryCache           cache            = null;

    /** The collator strength. */
    private int                  collatorStrength = Collator.IDENTICAL;

    /** The metadata. */
    private final Metadata       metadata;

    /** The selects. */
    private final List<Select>   selects          = new ArrayList<Select>();

    private final StorageSession treeSession;

    /**
     * Instantiates a new sL query impl.
     * 
     * @param session the session
     * @param treeSession the tree session
     */
    public QueryApiImpl(final GraphReader session, final QueryCache cache,
                        final StorageSession treeSession) {
        super(session);
        metadata = session.getMetadata();
        this.cache = cache;
        this.treeSession = treeSession;
    }

    private static <T> Collection<T> applyLimitOffset(
                                                      final Collection<T> input, final Integer limit, final Integer offset) {
        if (limit == null) { return input; }
        final LinkedList<T> resultList = new LinkedList<T>();

        final int m_limit = limit;

        final int m_offset = offset == null ? 0 : offset - 1;
        int i = 0;
        int addedElements = 0;
        for (final T pNodeWrapper: input) {
            if (i >= m_offset) {
                resultList.add(pNodeWrapper);
                addedElements++;
                if (addedElements == m_limit) {
                    break;
                }
            }
            i++;
        }

        return resultList;
    }

    /**
     * Gets the collator strength.
     * 
     * @param selectStatementInfo the select statement info
     * @return the collator strength
     */
    private int getCollatorStrength(
                                    final SelectStatementInfo selectStatementInfo) {
        return selectStatementInfo.getCollatorStrength() == null ? this
                .getCollatorStrength() : selectStatementInfo
                .getCollatorStrength();
    }

    /**
     * Gets the last select order by statement info.
     * 
     * @return the last select order by statement info
     */
    private SelectStatementInfo getLastSelect() {
        if (!selects.isEmpty()) { return QuerySupport.getSelectStatementInfo(selects.get(selects
                    .size() - 1)); }
        return null;
    }

    /**
     * Gets the meta node types.
     * 
     * @param name the name
     * @param subTypes the sub types
     * @return the meta node types
     */
    private Collection<MetaNodeType> getMetaNodeTypes(final String name,
                                                      final boolean subTypes)
        throws MetaNodeTypeNotFoundException {

        // final Collection<MetaNodeType> metaNodeTypes = new
        // ArrayList<MetaNodeType>();
        // if (name.equals(NoNoNodeetName())) {
        // for (final SLMetaNodeType metaNodeType :
        // metadata.getMetaNodesTypes()) {
        // metaNodeTypes.add(metaNodeType);
        // metaNodeTypes.addAll(metaNodeType.getSubMetaNodeTypes());
        // }
        // } else {
        // SLMetaNodeType metaNodeType =
        // metadata.getMetaNodeTypeByDescription(name);
        // if (metaNodeType == null) {
        // metaNodeType = metadata.getMetaNodeType(name);
        // }
        // metaNodeTypes.add(metaNodeType);
        // if (subTypes) {
        // final Collection<SLMetaNodeType> subMetaNodeTypes =
        // metaNodeType.searchSubMetaNodeTypes(
        // SLRecursiveMode.RECURSIVE,
        // null, null, null, null,
        // null);
        // for (final SLMetaNodeType subMetaNodeType : subMetaNodeTypes) {
        // metaNodeTypes.add(subMetaNodeType);
        // }
        // }
        // }
        //
        // return metaNodeTypes;
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the order by p node wrapper comparator.
     * 
     * @param orderByStatementInfo the order by statement info
     * @return the order by p node wrapper comparator
     */
    private Comparator<StorageNode> getOrderByStorageNodeComparator(
                                                                    final OrderByStatementInfo orderByStatementInfo) {
        throw new UnsupportedOperationException();
        // return new Comparator<StorageNode>() {
        // public int compare( final StorageNode nodeWrapper1,
        // final StorageNode nodeWrapper2 ) {
        // try {
        // final String typeName1 = nodeWrapper1.getTypeName();
        // final String typeName2 = nodeWrapper2.getTypeName();
        // final Integer index1 = getTypeIndex(typeName1);
        // final Integer index2 = getTypeIndex(typeName2);
        // if (index1.equals(index2)) {
        // if (nodeWrapper1.getID().equals(nodeWrapper2.getID())) {
        // return 0;
        // } else {
        // final List<SLOrderByTypeInfo> typeInfoList =
        // orderByStatementInfo.getOrderByTypeInfoList();
        // final SLOrderByTypeInfo typeInfo = typeInfoList.get(index1);
        // final String propertyName = index1 < typeInfoList.size() ?
        // typeInfo.getPropertyName() : null;
        // Comparable<Serializable> value1 =
        // nodeWrapper1.getPropertyValue(propertyName);
        // Comparable<Serializable> value2 =
        // nodeWrapper2.getPropertyValue(propertyName);
        // if (propertyName != null) {
        // value1 = nodeWrapper1.getPropertyValue(propertyName);
        // value2 = nodeWrapper2.getPropertyValue(propertyName);
        // }
        // int compareValue;
        // if (value1 == null && value2 == null) {
        // compareValue =
        // nodeWrapper1.getPath().compareTo(nodeWrapper2.getPath());
        // } else if (value1 == null && value2 != null) {
        // compareValue = 1;
        // } else if (value1 != null && value2 == null) {
        // compareValue = -1;
        // } else {
        // compareValue = value1.compareTo((Serializable)value2);
        // }
        // return normalizeCompareValue(compareValue, typeInfo.getOrderType());
        // }
        // } else {
        // return index1.compareTo(index2);
        // }
        // } catch (final SLException e) {
        // throw new
        // SLRuntimeException("Error on attempt on order by comparator.", e);
        // }
        // }
        //
        // private int getTypeIndex( final String typeName ) {
        // final List<SLOrderByTypeInfo> typeInfoList =
        // orderByStatementInfo.getOrderByTypeInfoList();
        // for (int i = 0; i < typeInfoList.size(); i++) {
        // final SLOrderByTypeInfo typeInfo = typeInfoList.get(i);
        // if (this.isInstanceOf(typeName, typeInfo.getTypeName())) {
        // return i;
        // }
        // }
        // return typeInfoList.size();
        // }
        //
        // private boolean isInstanceOf( final String subTypeName,
        // final SLMetaNodeType metaNodeType ) {
        // boolean status = false;
        // final SLMetaNodeType subMetaNodeType =
        // metaNodeType.getSubMetaNodeType(subTypeName);
        // if (subMetaNodeType == null) {
        // final Collection<SLMetaNodeType> subMetaNodeTypes =
        // metaNodeType.getSubMetaNodeTypes();
        // for (final SLMetaNodeType current : subMetaNodeTypes) {
        // status = this.isInstanceOf(subTypeName, current);
        // if (status) {
        // break;
        // }
        // }
        // } else {
        // status = true;
        // }
        // return status;
        // }
        //
        // private boolean isInstanceOf( final String subTypeName,
        // final String typeName ) {
        // try {
        // boolean status = subTypeName.equals(typeName);
        // if (!status) {
        // final SLMetaNodeType metaNodeType =
        // metadata.getMetaNodeType(typeName);
        // status = this.isInstanceOf(subTypeName, metaNodeType);
        // }
        // return status;
        // } catch (SLMetaNodeTypeNotFoundException ex) {
        // throw new
        // SLGraphRuntimeException("Error on attempt to check instanceof node type.",
        // ex);
        // }
        // }
        //
        // private int normalizeCompareValue( final int value,
        // final OrderType orderType ) {
        // return orderType.equals(OrderType.ASCENDING) ? value : -value;
        // }
        // };
    }

    private Collection<StorageNode> getResultCollection(
                                                        final SelectStatementInfo selectStatementInfo,
                                                        final SortMode sortMode) {
        throw new UnsupportedOperationException();
        // final SLOrderByStatementInfo orderByStatementInfo =
        // selectStatementInfo.getOrderByStatementInfo();
        // if (orderByStatementInfo == null) {
        // if (sortMode.equals(SortMode.SORTED)) {
        // final Comparator<StorageNode> comparator =
        // getStorageNodeComparator();
        // return new TreeSet<StorageNode>(comparator);
        // } else {
        // return new HashSet<StorageNode>();
        // }
        // } else {
        // final Comparator<StorageNode> comparator =
        // getOrderByStorageNodeComparator(orderByStatementInfo);
        // return new TreeSet<StorageNode>(comparator);
        // }
    }

    /**
     * Gets the p node wrapper comparator.
     * 
     * @return the p node wrapper comparator
     */
    private Comparator<StorageNode> getStorageNodeComparator() {
        throw new UnsupportedOperationException();
        // return new Comparator<StorageNode>() {
        // public int compare( final StorageNode nodeWrapper1,
        // final StorageNode nodeWrapper2 ) {
        // try {
        // if (nodeWrapper1.getTypeName().equals(nodeWrapper2.getTypeName())) {
        // if (nodeWrapper1.getName().equals(nodeWrapper2.getName())) {
        // return
        // nodeWrapper1.getParentName().compareTo(nodeWrapper2.getParentName());
        // } else {
        // return nodeWrapper1.getName().compareTo(nodeWrapper2.getName());
        // }
        // } else {
        // return
        // nodeWrapper1.getTypeName().compareTo(nodeWrapper2.getTypeName());
        // }
        // } catch (final StorageSessionException e) {
        // throw new
        // SLRuntimeException("Error on attempt to execute persistent node wrapper comparator.",
        // e);
        // }
        // }
        // };
    }

    /**
     * Node type exists.
     * 
     * @param name the name
     * @return true, if successful
     */
    private boolean nodeTypeExists(final String name) {
        throw new UnsupportedOperationException();
        // if (name.equals(NodeNodeNodeme())) {
        // return true;
        // }
        // try {
        // return metadata.getMetaNodeType(name) != null;
        // } catch (SLMetaNodeTypeNotFoundException e) {
        // return false;
        // }
    }

    /**
     * Normalize select statement info.
     * 
     * @param selectInfo the select info
     */
    private void normalizeSelectStatementInfo(
                                              final SelectStatementInfo selectInfo)
            throws MetaNodeTypeNotFoundException {
        throw new UnsupportedOperationException();
        //
        // final Set<SLSelectTypeInfo> selectTypeInfoSet = new
        // HashSet<SLSelectTypeInfo>();
        // if (selectInfo.getAllTypes() != null) {
        // if (selectInfo.getAllTypes().isOnWhere()) {
        // final List<SLWhereTypeInfo> whereTypeInfoList =
        // selectInfo.getWhereStatementInfo().getWhereTypeInfoList();
        // for (final SLWhereTypeInfo whereTypeInfo : whereTypeInfoList) {
        // final Collection<SLMetaNodeType> metaNodeTypes =
        // getMetaNodeTypes(whereTypeInfo.getName(),
        // whereTypeInfo.isSubTypes());
        // for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
        // final SLSelectTypeInfo selectTypeInfo = new
        // SLSelectTypeInfo(selectInfo, metaNodeType.getTypeName());
        // selectTypeInfoSet.add(selectTypeInfo);
        // }
        // }
        // } else {
        // final Collection<SLMetaNodeType> metaNodeTypes =
        // metadata.getMetaNodesTypes(SLRecursiveMode.RECURSIVE);
        // for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
        // final SLSelectTypeInfo selectTypeInfo = new
        // SLSelectTypeInfo(selectInfo, metaNodeType.getTypeName());
        // selectTypeInfoSet.add(selectTypeInfo);
        // }
        // }
        // } else {
        // for (final SLSelectTypeInfo selectTypeInfo :
        // selectInfo.getTypeInfoList()) {
        // final Collection<SLMetaNodeType> metaNodeTypes =
        // getMetaNodeTypes(selectTypeInfo.getName(),
        // selectTypeInfo.isSubTypes());
        // for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
        // final SLSelectTypeInfo current = new SLSelectTypeInfo(selectInfo,
        // metaNodeType.getTypeName());
        // selectTypeInfoSet.add(current);
        // }
        // }
        // }
        // selectInfo.getTypeInfoList().clear();
        // selectInfo.getTypeInfoList().addAll(selectTypeInfoSet);
        //
        // final SLWhereStatementInfo whereInfo =
        // selectInfo.getWhereStatementInfo();
        //
        // if (whereInfo != null) {
        // final Set<SLWhereTypeInfo> whereTypeInfoSet = new
        // HashSet<SLWhereTypeInfo>();
        // for (final SLWhereTypeInfo whereTypeInfo :
        // whereInfo.getWhereTypeInfoList()) {
        // final Collection<SLMetaNodeType> metaNodeTypes =
        // getMetaNodeTypes(whereTypeInfo.getName(),
        // whereTypeInfo.isSubTypes());
        // for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
        // final SLWhereTypeInfo current =
        // SerializationUtil.clone(whereTypeInfo);
        // current.setName(metaNodeType.getTypeName());
        // current.setSubTypes(false);
        // whereTypeInfoSet.add(current);
        // }
        // }
        // whereInfo.getWhereTypeInfoList().clear();
        // whereInfo.getWhereTypeInfoList().addAll(whereTypeInfoSet);
        // }
    }

    /**
     * Prints the.
     * 
     * @param showSLQL the show slql
     * @param object the object
     */
    private void print(final boolean showSLQL, final Object object) {
        if (showSLQL) {
            QueryApiImpl.LOGGER.info(object);
        }
    }

    /**
     * Validate all types.
     * 
     * @param selectInfo the select info
     * @throws InvalidQueryElementException the SL invalid query element exception
     */
    private void validateAllTypes(final SelectStatementInfo selectInfo)
            throws InvalidQueryElementException {
        throw new UnsupportedOperationException();
        // if (selectInfo.getAllTypes() != null) {
        // if (!selectInfo.getTypeInfoList().isEmpty()) {
        // throw new InvalidQueryElementException(
        // "When all types (*) or all types on where (**) are used, no type can be specifically used on select clause.");
        // }
        // final SLWhereStatementInfo whereInfo =
        // selectInfo.getWhereStatementInfo();
        // if (whereInfo != null) {
        // if (selectInfo.getAllTypes().isOnWhere() &&
        // whereInfo.getWhereTypeInfoList().isEmpty()) {
        // throw new InvalidQueryElementException(
        // "When all types on where (**)  is used, at least on type filter on where clause must be used.");
        // }
        // }
        // }
    }

    /**
     * Validate link types on where.
     * 
     * @param selectInfo the select info
     * @throws InvalidQueryElementException the SL invalid query element exception
     */
    private void validateLinkTypesOnWhere(final SelectStatementInfo selectInfo)
            throws InvalidQueryElementException {
        throw new UnsupportedOperationException();
        // final SLWhereStatementInfo whereInfo =
        // selectInfo.getWhereStatementInfo();
        // if (whereInfo != null) {
        // final Set<SLSelectByLinkInfo> byLinkInfoSet = new
        // HashSet<SLSelectByLinkInfo>(selectInfo.getByLinkInfoList());
        // for (final SLWhereLinkTypeInfo whereLinkTypeInfo :
        // whereInfo.getWhereLinkTypeInfoList()) {
        // if (!byLinkInfoSet.contains(new
        // SLSelectByLinkInfo(whereLinkTypeInfo.getName()))) {
        // throw new
        // InvalidQueryElementException("Link type not present in select by link clause: "
        // + whereLinkTypeInfo.getName());
        // }
        // }
        // }
    }

    /**
     * Validate link type statement info.
     * 
     * @param linkTypeStatementInfo the link type statement info
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private void validateLinkTypeStatementInfo(
                                               final SLLinkTypeStatementInfo linkTypeStatementInfo)
            throws SLInvalidQuerySyntaxException {
        throw new UnsupportedOperationException();
        // if (linkTypeStatementInfo == null) {
        // return;
        // }
        // if (!linkTypeStatementInfo.isClosed()) {
        // final SLInvalidQuerySyntaxException e = new
        // SLInvalidQuerySyntaxException("bracket must be closed.");
        // e.setStackTrace(linkTypeStatementInfo.getOpenBraceStackTrace());
        // throw e;
        // }
        // final List<SLLinkTypeConditionInfo> conditionInfoList =
        // linkTypeStatementInfo.getConditionInfoList();
        // for (final SLLinkTypeConditionInfo conditionInfo : conditionInfoList)
        // {
        // if (conditionInfo.getInnerStatementInfo() != null) {
        // validateLinkTypeStatementInfo(conditionInfo.getInnerStatementInfo());
        // }
        // }
    }

    /**
     * Validate node types on where.
     * 
     * @param selectInfo the select info
     * @throws InvalidQueryElementException the SL invalid query element exception
     */
    private void validateNodeTypesOnWhere(final SelectStatementInfo selectInfo)
            throws InvalidQueryElementException {
        throw new UnsupportedOperationException();
        //
        // final SLWhereStatementInfo whereInfo =
        // selectInfo.getWhereStatementInfo();
        // if (whereInfo != null) {
        // final Set<SLSelectTypeInfo> selectTypeInfoSet = new
        // HashSet<SLSelectTypeInfo>(selectInfo.getTypeInfoList());
        // for (final SLWhereTypeInfo whereTypeInfo :
        // whereInfo.getWhereTypeInfoList()) {
        // if (!selectTypeInfoSet.contains(new
        // SLSelectTypeInfo(whereTypeInfo.getName()))) {
        // throw new
        // InvalidQueryElementException("Node type not present in select clause: "
        // + whereTypeInfo.getName());
        // }
        // }
        // }
    }

    /**
     * Validate selects.
     * 
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     * @throws InvalidQueryElementException the SL invalid query element exception
     * @throws QueryException the SL query exception
     */
    private void validateSelects()
        throws SLInvalidQuerySyntaxException,
            InvalidQueryElementException, QueryException {
        throw new UnsupportedOperationException();
        //
        // try {
        // for (final SLSelect select : selects) {
        // final SLSelectStatementInfo selectStatementInfo =
        // QuerySupport.getSelectStatementInfo(select);
        // validateSelectStatementInfoBeforeNormalization(selectStatementInfo);
        // normalizeSelectStatementInfo(selectStatementInfo);
        // validateSelectStatementInfoAfterNormalization(selectStatementInfo);
        // }
        // } catch (final InvalidQueryElementException e) {
        // throw e;
        // } catch (final GraphReaderException e) {
        // throw new QueryException("Error on attempt to validate query.", e);
        // } catch (SLMetaNodeTypeNotFoundException e) {
        // throw new QueryException("Error on attempt to validate query.", e);
        // }
    }

    /**
     * Validate select statement info after normalization.
     * 
     * @param selectInfo the select info
     * @throws InvalidQueryElementException the SL invalid query element exception
     */
    private void validateSelectStatementInfoAfterNormalization(
                                                               final SelectStatementInfo selectInfo)
            throws InvalidQueryElementException {
        validateLinkTypesOnWhere(selectInfo);
    }

    /**
     * Validate select statement info before normalization.
     * 
     * @param selectInfo the select info
     * @throws SLInvalidQuerySyntaxException
     */
    private void validateSelectStatementInfoBeforeNormalization(
                                                                final SelectStatementInfo selectInfo)
            throws SLInvalidQuerySyntaxException, InvalidQueryElementException {
        validateSelectType(selectInfo);
        validateAllTypes(selectInfo);
        validateTypesExsistence(selectInfo);
        validateWhereStatements(selectInfo);
    }

    /**
     * Validate select type.
     * 
     * @param selectInfo the select info
     * @throws InvalidQueryElementException the SL invalid query element exception
     */
    private void validateSelectType(final SelectStatementInfo selectInfo)
            throws InvalidQueryElementException {
        throw new UnsupportedOperationException();
        //
        // final SLWhereStatementInfo whereInfo =
        // selectInfo.getWhereStatementInfo();
        // if (whereInfo != null) {
        // if (selectInfo.getByLinkInfoList().isEmpty()) {
        // if (!whereInfo.getWhereLinkTypeInfoList().isEmpty()) {
        // throw new InvalidQueryElementException(
        // "Link types on where clause can only be used if 'by link' is used on select clause.");
        // }
        // } else {
        // if (!whereInfo.getWhereTypeInfoList().isEmpty()) {
        // throw new InvalidQueryElementException(
        // "Node types cannot be used on where clause if 'by link' is used on select, however, link type filters are allowed instead.");
        // }
        // }
        // }
    }

    /**
     * Validate types exsistence.
     * 
     * @param selectInfo the select info
     * @throws InvalidQueryElementException the SL invalid query element exception
     */
    private void validateTypesExsistence(final SelectStatementInfo selectInfo)
            throws InvalidQueryElementException {
        throw new UnsupportedOperationException();
        //
        // for (final SLSelectTypeInfo selectTypeInfo :
        // selectInfo.getTypeInfoList()) {
        // if (!nodeTypeExists(selectTypeInfo.getName())) {
        // throw new
        // InvalidQueryElementException("Node type on select clause not found: "
        // + selectTypeInfo.getName());
        // }
        // }
        // final SLWhereStatementInfo whereInfo =
        // selectInfo.getWhereStatementInfo();
        // if (whereInfo != null) {
        // for (final SLWhereTypeInfo whereTypeInfo :
        // whereInfo.getWhereTypeInfoList()) {
        // if (!nodeTypeExists(whereTypeInfo.getName())) {
        // throw new
        // InvalidQueryElementException("Node type on where clause not found: "
        // + whereTypeInfo.getName());
        // }
        // }
        // }
    }

    /**
     * Validate type statement info.
     * 
     * @param typeStatementInfo the type statement info
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private void validateTypeStatementInfo(
                                           final SLTypeStatementInfo typeStatementInfo)
            throws SLInvalidQuerySyntaxException {
        throw new UnsupportedOperationException();
        // if (typeStatementInfo == null) {
        // return;
        // }
        // if (!typeStatementInfo.isClosed()) {
        // final SLInvalidQuerySyntaxException e = new
        // SLInvalidQuerySyntaxException("bracket must be closed.");
        // final StackTraceElement[] st =
        // typeStatementInfo.getOpenBraceStackTrace();
        // if (st != null) {
        // e.setStackTrace(st);
        // }
        // throw e;
        // }
        // final List<SLTypeConditionInfo> conditionInfoList =
        // typeStatementInfo.getConditionInfoList();
        // for (final SLTypeConditionInfo conditionInfo : conditionInfoList) {
        // if (conditionInfo.getInnerStatementInfo() != null) {
        // validateTypeStatementInfo(conditionInfo.getInnerStatementInfo());
        // }
        // }
    }

    /**
     * Validate where statements.
     * 
     * @param selectInfo the select info
     * @throws SLInvalidQuerySyntaxException the SL invalid query syntax exception
     */
    private void validateWhereStatements(final SelectStatementInfo selectInfo)
            throws SLInvalidQuerySyntaxException {
        throw new UnsupportedOperationException();
        // final SLWhereStatementInfo whereStatementInfo =
        // selectInfo.getWhereStatementInfo();
        // if (whereStatementInfo != null) {
        // for (final SLWhereTypeInfo whereTypeInfo :
        // whereStatementInfo.getWhereTypeInfoList()) {
        // validateTypeStatementInfo(whereTypeInfo.getTypeStatementInfo());
        // }
        // for (final SLWhereLinkTypeInfo whereTypeInfo :
        // whereStatementInfo.getWhereLinkTypeInfoList()) {
        // validateLinkTypeStatementInfo(whereTypeInfo.getLinkTypeStatementInfo());
        // }
        // }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult execute(final String[] inputNodesIDs,
                               final SortMode sortMode, final boolean showSLQL,
                               final Integer limit, final Integer offset)
            throws InvalidQuerySyntaxException, InvalidQueryElementException,
            QueryException {

        validateSelects();

        // try {
        //
        // final String queryId = cache.buildQueryId(selects, collatorStrength,
        // inputNodesIDs, sortMode, limit, offset);
        //
        // final SLQueryResult queryResult = cache.getCache(queryId);
        // if (queryResult != null) {
        // return queryResult;
        // }
        //
        // Collection<StorageNode> resultSelectNodeWrappers = null;
        // // here is the result
        // Collection<StorageNode> resultNodeWrappers = null;
        //
        // final SLSelectCommandDO commandDO = new SLSelectCommandDO();
        // commandDO.setMetadata(metadata);
        // commandDO.setTreeSession(treeSession);
        //
        // final Set<StorageNode> wrappers =
        // QuerySupport.getNodeWrappers(treeSession, inputNodesIDs);
        // commandDO.setPreviousNodeWrappers(wrappers);
        //
        // final SLSelectStatementInfo lastSelectInfo = getLastSelect();
        // resultNodeWrappers = getResultCollection(lastSelectInfo, sortMode);
        //
        // for (final SLSelect select : selects) {
        // Collection<StorageNode> selectNodeWrappers = null;
        // final SLSelectStatementInfo selectStatementInfo =
        // QuerySupport.getSelectStatementInfo(select);
        // final Integer xTimes = selectStatementInfo.getXTimes() == null ? 1 :
        // selectStatementInfo.getXTimes();
        // final SLSelectAbstractCommand command =
        // SLSelectAbstractCommand.getCommand(select, selectStatementInfo,
        // commandDO);
        // commandDO.setCollatorStrength(this.getCollatorStrength(selectStatementInfo));
        // resultSelectNodeWrappers = getResultCollection(selectStatementInfo,
        // sortMode);
        //
        // if (xTimes == SLSelectInfo.INDEFINITE) {
        // print(showSLQL, selectStatementInfo);
        // do {
        // command.execute();
        // selectNodeWrappers = commandDO.getNodeWrappers();
        // resultSelectNodeWrappers.addAll(selectNodeWrappers);
        // commandDO.setPreviousNodeWrappers(selectNodeWrappers);
        // } while (!selectNodeWrappers.isEmpty());
        // } else {
        // print(showSLQL, selectStatementInfo);
        // for (int i = 0; i < xTimes; i++) {
        // command.execute();
        // selectNodeWrappers = commandDO.getNodeWrappers();
        // if (selectNodeWrappers.isEmpty()) {
        // if (commandDO.getPreviousNodeWrappers() != null) {
        // commandDO.getPreviousNodeWrappers().clear();
        // }
        // break;
        // }
        // resultSelectNodeWrappers.addAll(selectNodeWrappers);
        // commandDO.setPreviousNodeWrappers(selectNodeWrappers);
        // }
        // }
        //
        // resultSelectNodeWrappers = applyLimitOffset(resultSelectNodeWrappers,
        // selectStatementInfo.getLimit(),
        // selectStatementInfo.getOffset());
        //
        // if (selectStatementInfo.isKeepResult()) {
        // resultNodeWrappers.addAll(resultSelectNodeWrappers);
        // }
        // }
        //
        // if (resultSelectNodeWrappers != null) {
        // resultNodeWrappers.addAll(resultSelectNodeWrappers);
        // }
        //
        // resultNodeWrappers = applyLimitOffset(resultNodeWrappers, limit,
        // offset);
        //
        // cache.add2Cache(queryId, resultNodeWrappers);
        //
        // return cache.getCache(queryId);
        // } catch (final SLException e) {
        // throw new QueryException("Error on attempt to execute query.", e);
        // }
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCollatorStrength() {
        return collatorStrength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectStatement select() {
        final SelectStatement select = new SelectStatementImpl(this);
        selects.add(select);
        return select;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectByLinkCount selectByLinkCount() {
        final SelectByLinkCount select = new SelectByLinkCountImpl(this);
        selects.add(select);
        return select;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectByLinkType selectByLinkType() {
        final SelectByLinkType select = new SelectByLinkTypeImpl(this);
        selects.add(select);
        return select;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SelectByNodeType selectByNodeType() {
        final SelectByNodeType select = new SelectByNodeTypeImpl(this);
        selects.add(select);
        return select;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCollatorStrength(final int collatorStrength) {
        this.collatorStrength = collatorStrength;
    }

}
