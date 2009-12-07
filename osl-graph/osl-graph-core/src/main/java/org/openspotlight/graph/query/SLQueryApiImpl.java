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
import java.util.LinkedList;
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

class PLinkNodeWrapper {

	private SLPersistentNode pLinkNode;
	private String id;
	private Integer linkTypeHash;
	private String sourceID;
	private String targetID;

	PLinkNodeWrapper(final SLPersistentNode pLinkNode) {
		this.pLinkNode = pLinkNode;
	}

	@Override
	public boolean equals(final Object obj) {
		try {
			final PLinkNodeWrapper linkNodeWrapper = (PLinkNodeWrapper) obj;
			return this.getId().equals(linkNodeWrapper.getId());
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLRuntimeException(
					"Error on attempt to verify persistent link node wrapper equality.",
					e);
		}
	}

	public String getId() throws SLPersistentTreeSessionException {
		if (this.id == null) {
			this.id = this.pLinkNode.getID();
		}
		return this.id;
	}

	public Integer getLinkTypeHash() throws SLPersistentTreeSessionException {
		if (this.linkTypeHash == null) {
			this.linkTypeHash = SLCommonSupport.getInternalPropertyAsInteger(
					this.pLinkNode, SLConsts.PROPERTY_NAME_LINK_TYPE_HASH);
		}
		return this.linkTypeHash;
	}

	public SLPersistentNode getPLinkNode() {
		return this.pLinkNode;
	}

	public String getSourceID() throws SLPersistentTreeSessionException {
		if (this.sourceID == null) {
			this.sourceID = SLCommonSupport.getInternalPropertyAsString(
					this.pLinkNode, SLConsts.PROPERTY_NAME_SOURCE_ID);
		}
		return this.sourceID;
	}

	public String getTargetID() throws SLPersistentTreeSessionException {
		if (this.targetID == null) {
			this.targetID = SLCommonSupport.getInternalPropertyAsString(
					this.pLinkNode, SLConsts.PROPERTY_NAME_TARGET_ID);
		}
		return this.targetID;
	}

	@Override
	public int hashCode() {
		try {
			return this.getId().hashCode();
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLRuntimeException(
					"Error on attempt to calculate persistent link node wrapper hash code.",
					e);
		}
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setLinkTypeHash(final Integer linkTypeHash) {
		this.linkTypeHash = linkTypeHash;
	}

	public void setPLinkNode(final SLPersistentNode linkNode) {
		this.pLinkNode = linkNode;
	}

	public void setSourceID(final String sourceID) {
		this.sourceID = sourceID;
	}

	public void setTargetID(final String targetID) {
		this.targetID = targetID;
	}
}

class PNodeWrapper {

	private SLPersistentNode pNode;
	private String typeName;
	private String id;
	private String name;
	private String path;
	private String parentName;
	private final Map<String, Comparable<Serializable>> propertyValueMap = new HashMap<String, Comparable<Serializable>>();

	PNodeWrapper(final SLPersistentNode pNode) {
		this.pNode = pNode;
	}

	PNodeWrapper(final SLPersistentNode pNode, final String typeName) {
		this.pNode = pNode;
		this.typeName = typeName;
	}

	@Override
	public boolean equals(final Object obj) {
		try {
			final PNodeWrapper nodeWrapper = (PNodeWrapper) obj;
			return this.getID().equals(nodeWrapper.getID());
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLRuntimeException(
					"Error on attempt to verify persistent node wrapper equality.",
					e);
		}
	}

	public String getID() throws SLPersistentTreeSessionException {
		if (this.id == null) {
			this.id = this.pNode.getID();
		}
		return this.id;
	}

	public String getName() throws SLPersistentTreeSessionException {
		if (this.name == null) {
			this.name = SLCommonSupport.getInternalPropertyAsString(this.pNode,
					SLConsts.PROPERTY_NAME_DECODED_NAME);
		}
		return this.name;
	}

	public String getParentName() throws SLPersistentTreeSessionException {
		if (this.parentName == null) {
			final SLPersistentNode pParentNode = this.pNode.getParent();
			this.parentName = SLCommonSupport.getInternalPropertyAsString(
					pParentNode, SLConsts.PROPERTY_NAME_DECODED_NAME);
		}
		return this.parentName;
	}

	public String getPath() throws SLPersistentTreeSessionException {
		if (this.path == null) {
			this.path = this.pNode.getPath();
		}
		return this.path;
	}

	public SLPersistentNode getPNode() {
		return this.pNode;
	}

	@SuppressWarnings("unchecked")
	public Comparable<Serializable> getPropertyValue(final String name)
			throws SLPersistentTreeSessionException {
		Comparable<Serializable> comparableValue = this.propertyValueMap
				.get(name);
		if (comparableValue == null) {
			final Serializable value = SLCommonSupport
					.getUserPropertyAsSerializable(this.pNode, name);
			if (value instanceof Comparable) {
				comparableValue = (Comparable<Serializable>) value;
			}
			if (comparableValue != null) {
				this.propertyValueMap.put(name, comparableValue);
			}
		}
		return comparableValue;
	}

	public String getTypeName() throws SLPersistentTreeSessionException {
		if (this.typeName == null) {
			this.typeName = SLCommonSupport.getInternalPropertyAsString(
					this.pNode, SLConsts.PROPERTY_NAME_TYPE);
		}
		return this.typeName;
	}

	@Override
	public int hashCode() {
		try {
			return this.getID().hashCode();
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLRuntimeException(
					"Error on attempt to calculate persistent node wrapper hash code.",
					e);
		}
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setParentName(final String parentName) {
		this.parentName = parentName;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setPNode(final SLPersistentNode node) {
		this.pNode = node;
	}

	public void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		try {
			return SLCommonSupport.getInternalPropertyAsString(this.pNode,
					SLConsts.PROPERTY_NAME_DECODED_NAME);
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to string "
					+ this.getClass().getName());
		}
	}
}

/**
 * The Class SLQueryImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLQueryApiImpl extends AbstractSLQuery implements SLQueryApi {

	private final Object lock;

	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLQueryApiImpl.class);

	/** The metadata. */
	private final SLMetadata metadata;

	/** The selects. */
	private final List<SLSelect> selects = new ArrayList<SLSelect>();

	/** The cache. */
	private SLQueryCache cache = null;

	/** The collator strength. */
	private int collatorStrength = Collator.IDENTICAL;

	/**
	 * Instantiates a new sL query impl.
	 * 
	 * @param session
	 *            the session
	 * @param treeSession
	 *            the tree session
	 */
	public SLQueryApiImpl(final SLGraphSession session,
			final SLPersistentTreeSession treeSession, final SLQueryCache cache) {
		super(session, treeSession);
		this.metadata = session.getMetadata();
		this.cache = cache;
		this.lock = session.getLockObject();
	}

	private Collection<PNodeWrapper> applyLimitOffset(
			final Collection<PNodeWrapper> input, final Integer limit,
			final Integer offset) {
		if (limit == null) {
			return input;
		}
		final LinkedList<PNodeWrapper> resultList = new LinkedList<PNodeWrapper>();

		final int m_limit = limit;
		final int m_offset = offset == null ? 0 : offset - 1;
		int i = 0;
		int addedElements = 0;
		for (final PNodeWrapper pNodeWrapper : input) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.query.SLQuery#execute(org.openspotlight.graph
	 * .query.SLQuery.SortMode, boolean)
	 */
	@Override
	public SLQueryResult execute(final String[] inputNodesIDs,
			final SortMode sortMode, final boolean showSLQL,
			final Integer limit, final Integer offset)
			throws SLInvalidQuerySyntaxException,
			SLInvalidQueryElementException, SLQueryException {
		synchronized (this.lock) {

			this.validateSelects();

			try {

				final String queryId = this.cache.buildQueryId(this.selects,
						this.collatorStrength, inputNodesIDs, sortMode, limit,
						offset);

				final SLQueryResult queryResult = this.cache.getCache(queryId);
				if (queryResult != null) {
					return queryResult;
				}

				Collection<PNodeWrapper> resultSelectNodeWrappers = null;
				// here is the result
				Collection<PNodeWrapper> resultNodeWrappers = null;

				final SLSelectCommandDO commandDO = new SLSelectCommandDO();
				commandDO.setMetadata(this.metadata);
				commandDO.setTreeSession(this.treeSession);

				final Set<PNodeWrapper> wrappers = SLQuerySupport
						.getNodeWrappers(this.treeSession, inputNodesIDs);
				commandDO.setPreviousNodeWrappers(wrappers);

				final SLSelectStatementInfo lastSelectInfo = this
						.getLastSelect();
				resultNodeWrappers = this.getResultCollection(lastSelectInfo,
						sortMode);

				for (final SLSelect select : this.selects) {
					Collection<PNodeWrapper> selectNodeWrappers = null;
					final SLSelectStatementInfo selectStatementInfo = SLQuerySupport
							.getSelectStatementInfo(select);
					final Integer xTimes = selectStatementInfo.getXTimes() == null ? 1
							: selectStatementInfo.getXTimes();
					final SLSelectAbstractCommand command = SLSelectAbstractCommand
							.getCommand(select, selectStatementInfo, commandDO);
					commandDO.setCollatorStrength(this
							.getCollatorStrength(selectStatementInfo));
					resultSelectNodeWrappers = this.getResultCollection(
							selectStatementInfo, sortMode);

					if (xTimes == SLSelectInfo.INDEFINITE) {
						this.print(showSLQL, selectStatementInfo);
						do {
							command.execute();
							selectNodeWrappers = commandDO.getNodeWrappers();
							resultSelectNodeWrappers.addAll(selectNodeWrappers);
							commandDO
									.setPreviousNodeWrappers(selectNodeWrappers);
						} while (!selectNodeWrappers.isEmpty());
					} else {
						this.print(showSLQL, selectStatementInfo);
						for (int i = 0; i < xTimes; i++) {
							command.execute();
							selectNodeWrappers = commandDO.getNodeWrappers();
							if (selectNodeWrappers.isEmpty()) {
								if (commandDO.getPreviousNodeWrappers() != null) {
									commandDO.getPreviousNodeWrappers().clear();
								}
								break;
							}
							resultSelectNodeWrappers.addAll(selectNodeWrappers);
							commandDO
									.setPreviousNodeWrappers(selectNodeWrappers);
						}
					}

					resultSelectNodeWrappers = this.applyLimitOffset(
							resultSelectNodeWrappers, selectStatementInfo
									.getLimit(), selectStatementInfo
									.getOffset());

					if (selectStatementInfo.isKeepResult()) {
						resultNodeWrappers.addAll(resultSelectNodeWrappers);
					}
				}

				if (resultSelectNodeWrappers != null) {
					resultNodeWrappers.addAll(resultSelectNodeWrappers);
				}

				resultNodeWrappers = this.applyLimitOffset(resultNodeWrappers,
						limit, offset);

				this.cache.add2Cache(queryId, resultNodeWrappers);

				return this.cache.getCache(queryId);
			} catch (final SLException e) {
				throw new SLQueryException(
						"Error on attempt to execute query.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLQuery#getCollatorStrength()
	 */
	public int getCollatorStrength() {
		synchronized (this.lock) {
			return this.collatorStrength;
		}
	}

	/**
	 * Gets the collator strength.
	 * 
	 * @param selectStatementInfo
	 *            the select statement info
	 * @return the collator strength
	 */
	private int getCollatorStrength(
			final SLSelectStatementInfo selectStatementInfo) {
		return selectStatementInfo.getCollatorStrength() == null ? this
				.getCollatorStrength() : selectStatementInfo
				.getCollatorStrength();
	}

	/**
	 * Gets the last select order by statement info.
	 * 
	 * @return the last select order by statement info
	 */
	private SLSelectStatementInfo getLastSelect() {
		if (!this.selects.isEmpty()) {
			return SLQuerySupport.getSelectStatementInfo(this.selects
					.get(this.selects.size() - 1));
		}
		return null;
	}

	public Object getLockObject() {
		return this.lock;
	}

	/**
	 * Gets the meta node types.
	 * 
	 * @param name
	 *            the name
	 * @param subTypes
	 *            the sub types
	 * @return the meta node types
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	private Collection<SLMetaNodeType> getMetaNodeTypes(final String name,
			final boolean subTypes) throws SLGraphSessionException {
		final Collection<SLMetaNodeType> metaNodeTypes = new ArrayList<SLMetaNodeType>();
		SLMetaNodeType metaNodeType = this.metadata
				.findMetaNodeTypeByDescription(name);
		if (metaNodeType == null) {
			metaNodeType = this.metadata.findMetaNodeType(name);
		}
		metaNodeTypes.add(metaNodeType);
		if (subTypes) {
			final Collection<SLMetaNodeType> subMetaNodeTypes = metaNodeType
					.getSubMetaNodeTypes();
			for (final SLMetaNodeType subMetaNodeType : subMetaNodeTypes) {
				metaNodeTypes.add(subMetaNodeType);
			}
		}
		return metaNodeTypes;
	}

	/**
	 * Gets the order by p node wrapper comparator.
	 * 
	 * @param orderByStatementInfo
	 *            the order by statement info
	 * @return the order by p node wrapper comparator
	 */
	private Comparator<PNodeWrapper> getOrderByPNodeWrapperComparator(
			final SLOrderByStatementInfo orderByStatementInfo) {
		return new Comparator<PNodeWrapper>() {
			public int compare(final PNodeWrapper nodeWrapper1,
					final PNodeWrapper nodeWrapper2) {
				try {
					final String typeName1 = nodeWrapper1.getTypeName();
					final String typeName2 = nodeWrapper2.getTypeName();
					final Integer index1 = this.getTypeIndex(typeName1);
					final Integer index2 = this.getTypeIndex(typeName2);
					if (index1 == index2) {
						if (nodeWrapper1.getID().equals(nodeWrapper2.getID())) {
							return 0;
						} else {
							final List<SLOrderByTypeInfo> typeInfoList = orderByStatementInfo
									.getOrderByTypeInfoList();
							final SLOrderByTypeInfo typeInfo = typeInfoList
									.get(index1);
							final String propertyName = index1 < typeInfoList
									.size() ? typeInfo.getPropertyName() : null;
							Comparable<Serializable> value1 = nodeWrapper1
									.getPropertyValue(propertyName);
							Comparable<Serializable> value2 = nodeWrapper2
									.getPropertyValue(propertyName);
							if (propertyName != null) {
								value1 = nodeWrapper1
										.getPropertyValue(propertyName);
								value2 = nodeWrapper2
										.getPropertyValue(propertyName);
							}
							int compareValue;
							if (value1 == null && value2 == null) {
								compareValue = nodeWrapper1.getPath()
										.compareTo(nodeWrapper2.getPath());
							} else if (value1 == null && value2 != null) {
								compareValue = 1;
							} else if (value1 != null && value2 == null) {
								compareValue = -1;
							} else {
								compareValue = value1
										.compareTo((Serializable) value2);
							}
							return this.normalizeCompareValue(compareValue,
									typeInfo.getOrderType());
						}
					} else {
						return index1.compareTo(index2);
					}
				} catch (final SLException e) {
					throw new SLRuntimeException(
							"Error on attempt on order by comparator.", e);
				}
			}

			private int getTypeIndex(final String typeName)
					throws SLGraphSessionException {
				final List<SLOrderByTypeInfo> typeInfoList = orderByStatementInfo
						.getOrderByTypeInfoList();
				for (int i = 0; i < typeInfoList.size(); i++) {
					final SLOrderByTypeInfo typeInfo = typeInfoList.get(i);
					if (this.isInstanceOf(typeName, typeInfo.getTypeName())) {
						return i;
					}
				}
				return typeInfoList.size();
			}

			private boolean isInstanceOf(final String subTypeName,
					final SLMetaNodeType metaNodeType)
					throws SLGraphSessionException {
				boolean status = false;
				final SLMetaNodeType subMetaNodeType = metaNodeType
						.getSubMetaNodeType(subTypeName);
				if (subMetaNodeType == null) {
					final Collection<SLMetaNodeType> subMetaNodeTypes = metaNodeType
							.getSubMetaNodeTypes();
					for (final SLMetaNodeType current : subMetaNodeTypes) {
						status = this.isInstanceOf(subTypeName, current);
						if (status) {
							break;
						}
					}
				} else {
					status = true;
				}
				return status;
			}

			private boolean isInstanceOf(final String subTypeName,
					final String typeName) throws SLGraphSessionException {
				boolean status = subTypeName.equals(typeName);
				if (!status) {
					final SLMetaNodeType metaNodeType = SLQueryApiImpl.this.metadata
							.findMetaNodeType(typeName);
					status = this.isInstanceOf(subTypeName, metaNodeType);
				}
				return status;
			}

			private int normalizeCompareValue(final int value,
					final OrderType orderType) {
				return orderType.equals(OrderType.ASCENDING) ? value : -value;
			}
		};
	}

	/**
	 * Gets the p node wrapper comparator.
	 * 
	 * @return the p node wrapper comparator
	 */
	private Comparator<PNodeWrapper> getPNodeWrapperComparator() {
		return new Comparator<PNodeWrapper>() {
			public int compare(final PNodeWrapper nodeWrapper1,
					final PNodeWrapper nodeWrapper2) {
				try {
					if (nodeWrapper1.getTypeName().equals(
							nodeWrapper2.getTypeName())) {
						if (nodeWrapper1.getName().equals(
								nodeWrapper2.getName())) {
							return nodeWrapper1.getParentName().compareTo(
									nodeWrapper2.getParentName());
						} else {
							return nodeWrapper1.getName().compareTo(
									nodeWrapper2.getName());
						}
					} else {
						return nodeWrapper1.getTypeName().compareTo(
								nodeWrapper2.getTypeName());
					}
				} catch (final SLPersistentTreeSessionException e) {
					throw new SLRuntimeException(
							"Error on attempt to execute persistent node wrapper comparator.",
							e);
				}
			}
		};
	}

	private Collection<PNodeWrapper> getResultCollection(
			final SLSelectStatementInfo selectStatementInfo,
			final SortMode sortMode) {
		final SLOrderByStatementInfo orderByStatementInfo = selectStatementInfo
				.getOrderByStatementInfo();
		if (orderByStatementInfo == null) {
			if (sortMode.equals(SortMode.SORTED)) {
				final Comparator<PNodeWrapper> comparator = this
						.getPNodeWrapperComparator();
				return new TreeSet<PNodeWrapper>(comparator);
			} else {
				return new HashSet<PNodeWrapper>();
			}
		} else {
			final Comparator<PNodeWrapper> comparator = this
					.getOrderByPNodeWrapperComparator(orderByStatementInfo);
			return new TreeSet<PNodeWrapper>(comparator);
		}
	}

	/**
	 * Link type exists.
	 * 
	 * @param name
	 *            the name
	 * @return true, if successful
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	boolean linkTypeExists(final String name) throws SLGraphSessionException {
		return this.metadata.getMetaLinkType(name) != null
				|| this.metadata.getMetaLinkTypeByDescription(name) != null;
	}

	/**
	 * Node type exists.
	 * 
	 * @param name
	 *            the name
	 * @return true, if successful
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	private boolean nodeTypeExists(final String name)
			throws SLGraphSessionException {
		return this.metadata.findMetaNodeType(name) != null
				|| this.metadata.findMetaNodeTypeByDescription(name) != null;
	}

	/**
	 * Normalize select statement info.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	private void normalizeSelectStatementInfo(
			final SLSelectStatementInfo selectInfo)
			throws SLGraphSessionException {

		final Set<SLSelectTypeInfo> selectTypeInfoSet = new HashSet<SLSelectTypeInfo>();
		if (selectInfo.getAllTypes() != null) {
			if (selectInfo.getAllTypes().isOnWhere()) {
				final List<SLWhereTypeInfo> whereTypeInfoList = selectInfo
						.getWhereStatementInfo().getWhereTypeInfoList();
				for (final SLWhereTypeInfo whereTypeInfo : whereTypeInfoList) {
					final Collection<SLMetaNodeType> metaNodeTypes = this
							.getMetaNodeTypes(whereTypeInfo.getName(),
									whereTypeInfo.isSubTypes());
					for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
						final SLSelectTypeInfo selectTypeInfo = new SLSelectTypeInfo(
								selectInfo, metaNodeType.getTypeName());
						selectTypeInfoSet.add(selectTypeInfo);
					}
				}
			} else {
				final Collection<SLMetaNodeType> metaNodeTypes = this.metadata
						.getMetaNodesTypes(SLRecursiveMode.RECURSIVE);
				for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
					final SLSelectTypeInfo selectTypeInfo = new SLSelectTypeInfo(
							selectInfo, metaNodeType.getTypeName());
					selectTypeInfoSet.add(selectTypeInfo);
				}
			}
		} else {
			for (final SLSelectTypeInfo selectTypeInfo : selectInfo
					.getTypeInfoList()) {
				final Collection<SLMetaNodeType> metaNodeTypes = this
						.getMetaNodeTypes(selectTypeInfo.getName(),
								selectTypeInfo.isSubTypes());
				for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
					final SLSelectTypeInfo current = new SLSelectTypeInfo(
							selectInfo, metaNodeType.getTypeName());
					selectTypeInfoSet.add(current);
				}
			}
		}
		selectInfo.getTypeInfoList().clear();
		selectInfo.getTypeInfoList().addAll(selectTypeInfoSet);

		final SLWhereStatementInfo whereInfo = selectInfo
				.getWhereStatementInfo();

		if (whereInfo != null) {
			final Set<SLWhereTypeInfo> whereTypeInfoSet = new HashSet<SLWhereTypeInfo>();
			for (final SLWhereTypeInfo whereTypeInfo : whereInfo
					.getWhereTypeInfoList()) {
				final Collection<SLMetaNodeType> metaNodeTypes = this
						.getMetaNodeTypes(whereTypeInfo.getName(),
								whereTypeInfo.isSubTypes());
				for (final SLMetaNodeType metaNodeType : metaNodeTypes) {
					final SLWhereTypeInfo current = SerializationUtil
							.clone(whereTypeInfo);
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
	 * Prints the.
	 * 
	 * @param showSLQL
	 *            the show slql
	 * @param object
	 *            the object
	 */
	private void print(final boolean showSLQL, final Object object) {
		if (showSLQL) {
			SLQueryApiImpl.LOGGER.info(object);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLSelectFacade#select()
	 */
	public SLSelectStatement select() throws SLGraphSessionException {
		synchronized (this.lock) {
			final SLSelectStatement select = new SLSelectStatementImpl(this);
			this.selects.add(select);
			return select;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLSelectFacade#selectByLinkCount()
	 */
	public SLSelectByLinkCount selectByLinkCount()
			throws SLGraphSessionException {
		synchronized (this.lock) {
			final SLSelectByLinkCount select = new SLSelectByLinkCountImpl(this);
			this.selects.add(select);
			return select;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLQuery#selectByLinkType()
	 */
	public SLSelectByLinkType selectByLinkType() throws SLGraphSessionException {
		synchronized (this.lock) {
			final SLSelectByLinkType select = new SLSelectByLinkTypeImpl(this);
			this.selects.add(select);
			return select;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLQuery#selectByNodeType()
	 */
	public SLSelectByNodeType selectByNodeType() throws SLGraphSessionException {
		synchronized (this.lock) {
			final SLSelectByNodeType select = new SLSelectByNodeTypeImpl(this);
			this.selects.add(select);
			return select;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLQuery#setCollatorStrength(int)
	 */
	public void setCollatorStrength(final int collatorStrength) {
		synchronized (this.lock) {
			this.collatorStrength = collatorStrength;
		}
	}

	/**
	 * Validate all types.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 */
	private void validateAllTypes(final SLSelectStatementInfo selectInfo)
			throws SLInvalidQueryElementException {
		if (selectInfo.getAllTypes() != null) {
			if (!selectInfo.getTypeInfoList().isEmpty()) {
				throw new SLInvalidQueryElementException(
						"When all types (*) or all types on where (**) are used, no type can be specifically used on select clause.");
			}
			final SLWhereStatementInfo whereInfo = selectInfo
					.getWhereStatementInfo();
			if (whereInfo != null) {
				if (selectInfo.getAllTypes().isOnWhere()
						&& whereInfo.getWhereTypeInfoList().isEmpty()) {
					throw new SLInvalidQueryElementException(
							"When all types on where (**)  is used, at least on type filter on where clause must be used.");
				}
			}
		}
	}

	/**
	 * Validate link types on where.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 */
	private void validateLinkTypesOnWhere(final SLSelectStatementInfo selectInfo)
			throws SLInvalidQueryElementException {
		final SLWhereStatementInfo whereInfo = selectInfo
				.getWhereStatementInfo();
		if (whereInfo != null) {
			final Set<SLSelectByLinkInfo> byLinkInfoSet = new HashSet<SLSelectByLinkInfo>(
					selectInfo.getByLinkInfoList());
			for (final SLWhereLinkTypeInfo whereLinkTypeInfo : whereInfo
					.getWhereLinkTypeInfoList()) {
				if (!byLinkInfoSet.contains(new SLSelectByLinkInfo(
						whereLinkTypeInfo.getName()))) {
					throw new SLInvalidQueryElementException(
							"Link type not present in select by link clause: "
									+ whereLinkTypeInfo.getName());
				}
			}
		}
	}

	/**
	 * Validate link type statement info.
	 * 
	 * @param linkTypeStatementInfo
	 *            the link type statement info
	 * @throws SLInvalidQuerySyntaxException
	 *             the SL invalid query syntax exception
	 */
	private void validateLinkTypeStatementInfo(
			final SLLinkTypeStatementInfo linkTypeStatementInfo)
			throws SLInvalidQuerySyntaxException {
		if (linkTypeStatementInfo == null) {
			return;
		}
		if (!linkTypeStatementInfo.isClosed()) {
			final SLInvalidQuerySyntaxException e = new SLInvalidQuerySyntaxException(
					"bracket must be closed.");
			e.setStackTrace(linkTypeStatementInfo.getOpenBraceStackTrace());
			throw e;
		}
		final List<SLLinkTypeConditionInfo> conditionInfoList = linkTypeStatementInfo
				.getConditionInfoList();
		for (final SLLinkTypeConditionInfo conditionInfo : conditionInfoList) {
			if (conditionInfo.getInnerStatementInfo() != null) {
				this.validateLinkTypeStatementInfo(conditionInfo
						.getInnerStatementInfo());
			}
		}
	}

	/**
	 * Validate node types on where.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 */
	private void validateNodeTypesOnWhere(final SLSelectStatementInfo selectInfo)
			throws SLInvalidQueryElementException {
		final SLWhereStatementInfo whereInfo = selectInfo
				.getWhereStatementInfo();
		if (whereInfo != null) {
			final Set<SLSelectTypeInfo> selectTypeInfoSet = new HashSet<SLSelectTypeInfo>(
					selectInfo.getTypeInfoList());
			for (final SLWhereTypeInfo whereTypeInfo : whereInfo
					.getWhereTypeInfoList()) {
				if (!selectTypeInfoSet.contains(new SLSelectTypeInfo(
						whereTypeInfo.getName()))) {
					throw new SLInvalidQueryElementException(
							"Node type not present in select clause: "
									+ whereTypeInfo.getName());
				}
			}
		}
	}

	/**
	 * Validate selects.
	 * 
	 * @throws SLInvalidQuerySyntaxException
	 *             the SL invalid query syntax exception
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 * @throws SLQueryException
	 *             the SL query exception
	 */
	private void validateSelects() throws SLInvalidQuerySyntaxException,
			SLInvalidQueryElementException, SLQueryException {
		try {
			for (final SLSelect select : this.selects) {
				final SLSelectStatementInfo selectStatementInfo = SLQuerySupport
						.getSelectStatementInfo(select);
				this
						.validateSelectStatementInfoBeforeNormalization(selectStatementInfo);
				this.normalizeSelectStatementInfo(selectStatementInfo);
				this
						.validateSelectStatementInfoAfterNormalization(selectStatementInfo);
			}
		} catch (final SLInvalidQueryElementException e) {
			throw e;
		} catch (final SLGraphSessionException e) {
			throw new SLQueryException(
					"Error on attempt to validate select information.", e);
		}
	}

	/**
	 * Validate select statement info after normalization.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 */
	private void validateSelectStatementInfoAfterNormalization(
			final SLSelectStatementInfo selectInfo)
			throws SLInvalidQueryElementException {
		this.validateNodeTypesOnWhere(selectInfo);
		this.validateLinkTypesOnWhere(selectInfo);
	}

	/**
	 * Validate select statement info before normalization.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLInvalidQuerySyntaxException
	 */
	private void validateSelectStatementInfoBeforeNormalization(
			final SLSelectStatementInfo selectInfo)
			throws SLGraphSessionException, SLInvalidQuerySyntaxException {
		this.validateSelectType(selectInfo);
		this.validateAllTypes(selectInfo);
		this.validateTypesExsistence(selectInfo);
		this.validateWhereStatements(selectInfo);
	}

	/**
	 * Validate select type.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 */
	private void validateSelectType(final SLSelectStatementInfo selectInfo)
			throws SLInvalidQueryElementException {
		final SLWhereStatementInfo whereInfo = selectInfo
				.getWhereStatementInfo();
		if (whereInfo != null) {
			if (selectInfo.getByLinkInfoList().isEmpty()) {
				if (!whereInfo.getWhereLinkTypeInfoList().isEmpty()) {
					throw new SLInvalidQueryElementException(
							"Link types on where clause can only be used if 'by link' is used on select clause.");
				}
			} else {
				if (!whereInfo.getWhereTypeInfoList().isEmpty()) {
					throw new SLInvalidQueryElementException(
							"Node types cannot be used on where clause if 'by link' is used on select, however, link type filters are allowed instead.");
				}
			}
		}
	}

	/**
	 * Validate types exsistence.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQueryElementException
	 *             the SL invalid query element exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	private void validateTypesExsistence(final SLSelectStatementInfo selectInfo)
			throws SLInvalidQueryElementException, SLGraphSessionException {
		for (final SLSelectTypeInfo selectTypeInfo : selectInfo
				.getTypeInfoList()) {
			if (!this.nodeTypeExists(selectTypeInfo.getName())) {
				throw new SLInvalidQueryElementException(
						"Node type on select clause not found: "
								+ selectTypeInfo.getName());
			}
		}
		/**
		 * for (SLSelectByLinkInfo byLinkInfo : selectInfo.getByLinkInfoList())
		 * { if (!linkTypeExists(byLinkInfo.getName())) { throw new
		 * SLInvalidQueryElementException
		 * ("Link type on select by link clause not found: " +
		 * byLinkInfo.getName()); } }
		 **/
		final SLWhereStatementInfo whereInfo = selectInfo
				.getWhereStatementInfo();
		if (whereInfo != null) {
			for (final SLWhereTypeInfo whereTypeInfo : whereInfo
					.getWhereTypeInfoList()) {
				if (!this.nodeTypeExists(whereTypeInfo.getName())) {
					throw new SLInvalidQueryElementException(
							"Node type on where clause not found: "
									+ whereTypeInfo.getName());
				}
			}
			/**
			 * for (SLWhereLinkTypeInfo linkTypeInfo :
			 * whereInfo.getWhereLinkTypeInfoList()) { if
			 * (!linkTypeExists(linkTypeInfo.getName())) { throw new
			 * SLInvalidQueryElementException
			 * ("Link type on where clause not found: " +
			 * linkTypeInfo.getName()); } }
			 **/
		}
	}

	/**
	 * Validate type statement info.
	 * 
	 * @param typeStatementInfo
	 *            the type statement info
	 * @throws SLInvalidQuerySyntaxException
	 *             the SL invalid query syntax exception
	 */
	private void validateTypeStatementInfo(
			final SLTypeStatementInfo typeStatementInfo)
			throws SLInvalidQuerySyntaxException {
		if (typeStatementInfo == null) {
			return;
		}
		if (!typeStatementInfo.isClosed()) {
			final SLInvalidQuerySyntaxException e = new SLInvalidQuerySyntaxException(
					"bracket must be closed.");
			final StackTraceElement[] st = typeStatementInfo
					.getOpenBraceStackTrace();
			if (st != null) {
				e.setStackTrace(st);
			}
			throw e;
		}
		final List<SLTypeConditionInfo> conditionInfoList = typeStatementInfo
				.getConditionInfoList();
		for (final SLTypeConditionInfo conditionInfo : conditionInfoList) {
			if (conditionInfo.getInnerStatementInfo() != null) {
				this.validateTypeStatementInfo(conditionInfo
						.getInnerStatementInfo());
			}
		}
	}

	/**
	 * Validate where statements.
	 * 
	 * @param selectInfo
	 *            the select info
	 * @throws SLInvalidQuerySyntaxException
	 *             the SL invalid query syntax exception
	 */
	private void validateWhereStatements(final SLSelectStatementInfo selectInfo)
			throws SLInvalidQuerySyntaxException {
		final SLWhereStatementInfo whereStatementInfo = selectInfo
				.getWhereStatementInfo();
		if (whereStatementInfo != null) {
			for (final SLWhereTypeInfo whereTypeInfo : whereStatementInfo
					.getWhereTypeInfoList()) {
				this.validateTypeStatementInfo(whereTypeInfo
						.getTypeStatementInfo());
			}
			for (final SLWhereLinkTypeInfo whereTypeInfo : whereStatementInfo
					.getWhereLinkTypeInfoList()) {
				this.validateLinkTypeStatementInfo(whereTypeInfo
						.getLinkTypeStatementInfo());
			}
		}
	}
}
