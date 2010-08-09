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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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

package org.openspotlight.storage.redis;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;
import static org.jredis.ri.alphazero.support.DefaultCodec.toStr;
import static org.openspotlight.storage.StringIDSupport.getNodeEntryName;
import static org.openspotlight.storage.StringIDSupport.getPartition;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jredis.JRedis;
import org.openspotlight.common.CustomizedFormat;
import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.collection.IteratorBuilder.Converter;
import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STLinkEntry;
import org.openspotlight.storage.domain.node.STLinkEntryImpl;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STProperty;
import org.openspotlight.storage.domain.node.STPropertyContainer;
import org.openspotlight.storage.domain.node.STPropertyImpl;
import org.openspotlight.storage.redis.guice.JRedisFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */

enum Nothing {
	NOTHING
}

public class JRedisSTStorageSessionImpl extends
		AbstractSTStorageSession<Nothing> {

	private final STPartitionFactory partitionFactory;

	private static enum PropertyType {
		SIMPLE(SET_WITH_NODE_PROPERTY_SIMPLE_NAMES, false, false), INDEXED(
				SET_WITH_NODE_PROPERTY_INDEXED_NAMES, true, false), KEY(
				SET_WITH_NODE_PROPERTY_KEY_NAMES, true, true);

		private final boolean key;
		private final boolean indexed;

		public boolean isKey() {
			return key;
		}

		public boolean isIndexed() {
			return indexed;
		}

		private PropertyType(CustomizedFormat f, boolean indexed, boolean key) {
			this.f = f;
			this.key = key;
			this.indexed = indexed;
		}

		private final CustomizedFormat f;

		public String getSetName(String uniqueId) {
			return f.format(uniqueId);
		}

		public STPropertyImpl createProperty(String name,
				STPropertyContainer parent) {
			if (isKey())
				return STPropertyImpl.createKey(name, parent);
			if (isIndexed())
				return STPropertyImpl.createIndexed(name, parent);
			return STPropertyImpl.createSimple(name, parent);

		}

	}

	private class JRedisLoggedExecution {
		private final String usedKeys;
		private final String uuid;

		private final JRedis jredis;

		private JRedisLoggedExecution(String uuid, JRedis jredis) {
			this.usedKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uuid);
			this.jredis = jredis;
			this.uuid = uuid;
		}

		public void sadd(String s, byte[] o) throws Exception {
			jredis.sadd(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void sadd(String s, Number o) throws Exception {
			jredis.sadd(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void sadd(String s, String o) throws Exception {
			jredis.sadd(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void sadd(String s, Serializable o) throws Exception {
			jredis.sadd(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void set(String s, Number o) throws Exception {
			jredis.set(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void set(String s, String o) throws Exception {
			jredis.set(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void set(String s, Serializable o) throws Exception {
			jredis.set(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}

		public void set(String s, byte[] o) throws Exception {
			jredis.set(s, o);
			if (!s.contains(uuid))
				jredis.sadd(usedKeys, s);
		}
	}

	private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_NAME = new CustomizedFormat(
			"lname: :uids");
	private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_ORIGIN = new CustomizedFormat(
			"lorigin: :uids");
	private static final CustomizedFormat SET_WITH_ALL_LINKS_FOR_TARGET = new CustomizedFormat(
			"ltarget: :uids");

	private static final CustomizedFormat SET_WITH_INDEX_ENTRY = new CustomizedFormat(
			"index: :pname: :uid");
	private static final CustomizedFormat SET_WITH_ALL_DEPENDENT_KEYS = new CustomizedFormat(
			"nuid: :dependent-keys");
	private static final String SET_WITH_ALL_KEYS = "uids";
	private static final String SET_WITH_ALL_KEY_NAMES = "names";
	private static final CustomizedFormat SET_WITH_ALL_NODE_KEYS_FOR_NAME = new CustomizedFormat(
			"name: :uids");
	private static final CustomizedFormat SET_WITH_ALL_LOCAL_KEYS = new CustomizedFormat(
			"lkeys: :uids");
	private static final CustomizedFormat SET_WITH_NODE_PROPERTY_KEY_NAMES = new CustomizedFormat(
			"nuid: :knames");
	private static final CustomizedFormat SET_WITH_NODE_PROPERTY_SIMPLE_NAMES = new CustomizedFormat(
			"nuid: :pnames");
	private static final CustomizedFormat SET_WITH_NODE_PROPERTY_INDEXED_NAMES = new CustomizedFormat(
			"nuid: :inames");
	private static final CustomizedFormat SET_WITH_NODE_CHILDREN_KEYS = new CustomizedFormat(
			"nuid: :cld-uids");
	private static final CustomizedFormat SET_WITH_NODE_CHILDREN_NAMED_KEYS = new CustomizedFormat(
			"nuid: :nname: :cld-uids");
	private static final CustomizedFormat KEY_WITH_PROPERTY_VALUE = new CustomizedFormat(
			"nuid: :pname: :value");
	private static final CustomizedFormat KEY_WITH_PARENT_UNIQUE_ID = new CustomizedFormat(
			"nuid: :prt-uid");

	private static enum SearchType {
		EQUAL, STRING_STARTS_WITH, STRING_ENDS_WITH, STRING_CONTAINS
	}

	private final JRedisFactory factory;

	@Inject
	public JRedisSTStorageSessionImpl(STFlushMode flushMode,
			JRedisFactory factory, STRepositoryPath repositoryPath,
			STPartitionFactory partitionFactory) {
		super(flushMode, repositoryPath, partitionFactory);
		this.factory = factory;
		this.partitionFactory = partitionFactory;
	}

	@Override
	protected Nothing createNodeReferenceIfNecessary(STPartition partition,
			STNodeEntry entry) {
		return Nothing.NOTHING;
	}

	@Override
	protected byte[] internalPropertyGetValue(STPartition partition,
			STProperty stProperty) throws Exception {
		JRedis jredis = this.factory.getFrom(partition);
		String uniqueKey = stProperty.getParent().getKeyAsString();
		byte[] propertyValue = jredis.get(KEY_WITH_PROPERTY_VALUE.format(
				uniqueKey, stProperty.getPropertyName()));
		return propertyValue;

	}

	@Override
	protected void internalSavePartitions(STPartition... partitions)
			throws Exception {
		for (STPartition p : partitions) {
			this.factory.getFrom(p).save();
		}
	}

	@Override
	protected Set<STNodeEntry> internalFindByCriteria(STPartition partition,
			STCriteria criteria) throws Exception {
		List<String> propertiesIntersection = newLinkedList();
		List<String> uniqueIdsFromLocalOnes = newLinkedList();
		boolean first = true;
		List<String> uniqueIds = newLinkedList();
		JRedis jredis = factory.getFrom(partition);
		for (STCriteriaItem c : criteria.getCriteriaItems()) {
			if (c instanceof STPropertyCriteriaItem) {
				STPropertyCriteriaItem p = (STPropertyCriteriaItem) c;
				if (first) {
					propertiesIntersection.addAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.EQUAL, p.getValue()));
					first = false;
				} else {
					propertiesIntersection.retainAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.EQUAL, p.getValue()));
				}
			}
			if (c instanceof STPropertyContainsString) {
				STPropertyContainsString p = (STPropertyContainsString) c;
				if (first) {
					propertiesIntersection.addAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.STRING_CONTAINS, p.getValue()));
					first = false;
				} else {
					propertiesIntersection.retainAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.STRING_CONTAINS, p.getValue()));
				}
			}
			if (c instanceof STPropertyStartsWithString) {
				STPropertyStartsWithString p = (STPropertyStartsWithString) c;
				if (first) {
					propertiesIntersection.addAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.STRING_STARTS_WITH, p.getValue()));
					first = false;
				} else {
					propertiesIntersection.retainAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.STRING_STARTS_WITH, p.getValue()));
				}
			}
			if (c instanceof STPropertyEndsWithString) {
				STPropertyEndsWithString p = (STPropertyEndsWithString) c;
				if (first) {
					propertiesIntersection.addAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.STRING_ENDS_WITH, p.getValue()));
					first = false;
				} else {
					propertiesIntersection.retainAll(keysFromProperty(jredis, p
							.getNodeEntryName(), p.getPropertyName(),
							SearchType.STRING_ENDS_WITH, p.getValue()));
				}
			}
			if (c instanceof STUniqueKeyCriteriaItem) {
				STUniqueKeyCriteriaItem uniqueCriteria = (STUniqueKeyCriteriaItem) c;
				uniqueIds.add(uniqueCriteria.getValue().getKeyAsString());

			}
			if (c instanceof STUniqueKeyAsStringCriteriaItem) {
				STUniqueKeyAsStringCriteriaItem uniqueCriteria = (STUniqueKeyAsStringCriteriaItem) c;
				uniqueIds.add(uniqueCriteria.getKeyAsString());

			}
			if (c instanceof STLocalKeyCriteriaItem) {
				STLocalKeyCriteriaItem uniqueCriteria = (STLocalKeyCriteriaItem) c;
				String localHash = uniqueCriteria.getValue().getKeyAsString();
				uniqueIdsFromLocalOnes.addAll(listBytesToListString(jredis
						.smembers(SET_WITH_ALL_LOCAL_KEYS.format(localHash))));

			}
		}
		if (criteria.getCriteriaItems().size() == 0) {
			List<String> keys = listBytesToListString(jredis
					.smembers(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(criteria
							.getNodeName())));
			uniqueIds.addAll(keys);
		}

		if (!uniqueIds.isEmpty() && !propertiesIntersection.isEmpty())
			throw new IllegalArgumentException(
					"criteria with unique ids can't be used with other criteria types");

		List<String> ids;
		if (uniqueIds.isEmpty()) {
			ids = propertiesIntersection;
			if (!uniqueIdsFromLocalOnes.isEmpty()) {
				if (ids.isEmpty()) {
					ids = uniqueIdsFromLocalOnes;
				} else {
					ids.retainAll(uniqueIdsFromLocalOnes);
				}
			}
		} else {
			ids = uniqueIds;
		}

		Set<STNodeEntry> nodeEntries = newHashSet();
		for (String id : ids) {
			STNodeEntry nodeEntry = loadNodeOrReturnNull(id, criteria
					.getPartition());
			if (nodeEntry != null) {
				nodeEntries.add(nodeEntry);
			}
		}

		return ImmutableSet.copyOf(nodeEntries);
	}

	private Collection<String> keysFromProperty(final JRedis jredis,
			final String nodeEntryName, final String propertyName,
			final SearchType equal, final String value) throws Exception {
		if (!SearchType.EQUAL.equals(equal))
			throw new UnsupportedOperationException("Finding by " + equal
					+ " isn't supported");

		ImmutableList.Builder<String> builder = ImmutableList.builder();
		String transientValueAsString = stripString(value);
		List<String> ids = listBytesToListString(jredis
				.smembers(SET_WITH_INDEX_ENTRY.format(transientValueAsString,
						propertyName)));
		for (String id : ids) {
			String propertyValue = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE
					.format(id, propertyName)));
			if (propertyValue == null)
				propertyValue = "null";
			boolean needsToAdd = false;
			switch (equal) {
			case EQUAL:
				needsToAdd = stripString(propertyValue).equals(
						transientValueAsString);
				break;

			}
			if (nodeEntryName != null && needsToAdd) {

				String name = getNodeEntryName(id);
				if (!nodeEntryName.equals(name)) {
					needsToAdd = false;
				}
			}
			if (needsToAdd) {
				builder.add(id);
			}
		}

		return builder.build();
	}

	private STNodeEntry loadNodeOrReturnNull(final String firstParentKey,
			STPartition firstPartition) throws Exception {

		JRedis jredis = factory.getFrom(firstPartition);
		if (!jredis.sismember(SET_WITH_ALL_KEYS, firstParentKey))
			return null;
		STUniqueKeyBuilder keyBuilder = withPartition(firstPartition)
				.createKey(getNodeEntryName(firstParentKey));
		List<String> keyPropertyNames = listBytesToListString(jredis
				.smembers(SET_WITH_NODE_PROPERTY_KEY_NAMES
						.format(firstParentKey)));

		for (String keyName : keyPropertyNames) {
			String value = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(
					firstParentKey, keyName)));
			keyBuilder.withEntry(keyName, value);
		}
		String parentKey = toStr(jredis.get(KEY_WITH_PARENT_UNIQUE_ID
				.format(firstParentKey)));
		if (parentKey != null) {
			keyBuilder = keyBuilder.withParent(parentKey);
		}
		STUniqueKey uniqueKey = keyBuilder.andCreate();
		STNodeEntry nodeEntry = createFoundEntryWithKey(uniqueKey);

		return nodeEntry;
	}

	private static List<String> listBytesToListString(List<byte[]> ids) {
		List<String> idsAsString = newLinkedList();
		if (ids != null) {
			for (byte[] b : ids) {
				String s = toStr(b);
				idsAsString.add(s);
			}
		}
		return idsAsString;
	}

	@Override
	protected void flushNewItem(Nothing reference, STPartition partition,
			STNodeEntry entry) throws Exception {

		flushRemoved(partition, entry, entry.getNodeEntryName());

	}

	private void flushRemoved(STPartition partition, STPropertyContainer entry,
			String entryName) throws Exception {
		JRedis jredis = factory.getFrom(partition);

		String uniqueKey = entry.getKeyAsString();
		jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
		if (entryName != null)
			jredis.srem(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(entryName),
					uniqueKey);
		String simpleProperties = SET_WITH_NODE_PROPERTY_SIMPLE_NAMES
				.format(uniqueKey);
		String keyProperties = SET_WITH_NODE_PROPERTY_KEY_NAMES
				.format(uniqueKey);
		String indexedProperties = SET_WITH_NODE_PROPERTY_INDEXED_NAMES
				.format(uniqueKey);
		List<String> allPropertyNames = newLinkedList();
		allPropertyNames.addAll(listBytesToListString(jredis
				.smembers(simpleProperties)));
		allPropertyNames.addAll(listBytesToListString(jredis
				.smembers(keyProperties)));
		allPropertyNames.addAll(listBytesToListString(jredis
				.smembers(indexedProperties)));
		for (String s : allPropertyNames) {
			jredis.del(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(uniqueKey, s),
					KEY_WITH_PROPERTY_VALUE.format(uniqueKey, s));
		}
		jredis.del(simpleProperties, keyProperties, indexedProperties,
				SET_WITH_NODE_CHILDREN_KEYS.format(uniqueKey),
				KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey));

		String dependentKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uniqueKey);
		List<String> keys = listBytesToListString(jredis
				.smembers(dependentKeys));

		for (String key : keys) {
			if (key.contains(uniqueKey)) {
				jredis.del(key);
			} else {
				List<String> possibleValues = listBytesToListString(jredis
						.smembers(key));
				for (String possibleValue : possibleValues) {
					if (possibleValue.contains(uniqueKey)) {
						jredis.srem(key, possibleValue);
					}
				}

			}
		}
		jredis.del(dependentKeys);

	}

	@Override
	protected void flushRemovedItem(STPartition partition, STNodeEntry entry)
			throws Exception {
		JRedis jredis = factory.getFrom(partition);

		String uniqueKey = entry.getUniqueKey().getKeyAsString();
		jredis.srem(SET_WITH_ALL_KEYS, uniqueKey);
		jredis.srem(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(entry
				.getNodeEntryName()), uniqueKey);
		String simpleProperties = SET_WITH_NODE_PROPERTY_SIMPLE_NAMES
				.format(uniqueKey);
		String keyProperties = SET_WITH_NODE_PROPERTY_KEY_NAMES
				.format(uniqueKey);
		String indexedProperties = SET_WITH_NODE_PROPERTY_INDEXED_NAMES
				.format(uniqueKey);
		List<String> allPropertyNames = newLinkedList();
		allPropertyNames.addAll(listBytesToListString(jredis
				.smembers(simpleProperties)));
		allPropertyNames.addAll(listBytesToListString(jredis
				.smembers(keyProperties)));
		allPropertyNames.addAll(listBytesToListString(jredis
				.smembers(indexedProperties)));
		for (String s : allPropertyNames) {
			jredis.del(SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(uniqueKey, s),
					KEY_WITH_PROPERTY_VALUE.format(uniqueKey, s));
		}
		jredis.del(simpleProperties, keyProperties, indexedProperties,
				SET_WITH_NODE_CHILDREN_KEYS.format(uniqueKey),
				KEY_WITH_PARENT_UNIQUE_ID.format(uniqueKey));

		String dependentKeys = SET_WITH_ALL_DEPENDENT_KEYS.format(uniqueKey);
		List<String> keys = listBytesToListString(jredis
				.smembers(dependentKeys));

		for (String key : keys) {
			if (key.contains(uniqueKey)) {
				jredis.del(key);
			} else {
				List<String> possibleValues = listBytesToListString(jredis
						.smembers(key));
				for (String possibleValue : possibleValues) {
					if (possibleValue.contains(uniqueKey)) {
						jredis.srem(key, possibleValue);
					}
				}

			}
		}
		jredis.del(dependentKeys);
	}

	@Override
	protected Set<STNodeEntry> internalNodeEntryGetNamedChildren(
			STPartition partition, STNodeEntry stNodeEntry, String name)
			throws Exception {
		JRedis jredis = factory.getFrom(partition);

		String parentKey = stNodeEntry.getUniqueKey().getKeyAsString();
		String keyName = name == null ? SET_WITH_NODE_CHILDREN_KEYS
				.format(parentKey) : SET_WITH_NODE_CHILDREN_NAMED_KEYS.format(
				parentKey, name);
		List<String> childrenKeys = listBytesToListString(jredis
				.smembers(keyName));
		ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet.builder();
		for (String id : childrenKeys) {
			STPartition childPartition = getPartition(id, partitionFactory);
			if (partition.equals(childPartition)) {
				STNodeEntry loadedNode = loadNodeOrReturnNull(id,
						childPartition);
				if (loadedNode != null) {
					builder.add(loadedNode);
				}
			}
		}
		return builder.build();
	}

	@Override
	protected Set<STNodeEntry> internalNodeEntryGetChildren(
			STPartition partition, STNodeEntry stNodeEntry) throws Exception {
		return internalNodeEntryGetNamedChildren(partition, stNodeEntry, null);
	}

	@Override
	protected STNodeEntry internalNodeEntryGetParent(STPartition partition,
			STNodeEntry stNodeEntry) throws Exception {

		String parentKeyAsString = stNodeEntry.getUniqueKey()
				.getParentKeyAsString();
		if (parentKeyAsString == null)
			return null;
		return loadNodeOrReturnNull(parentKeyAsString, partition);
	}

	@Override
	protected Set<STNodeEntry> internalFindNamed(STPartition partition,
			String nodeEntryName) throws Exception {

		JRedis jRedis = factory.getFrom(partition);
		List<String> ids = listBytesToListString(jRedis
				.smembers(SET_WITH_ALL_NODE_KEYS_FOR_NAME.format(nodeEntryName)));
		ImmutableSet.Builder<STNodeEntry> builder = ImmutableSet
				.<STNodeEntry> builder();
		for (String id : ids) {
			STNodeEntry loadedNode = loadNodeOrReturnNull(id, partition);
			if (loadedNode != null) {
				builder.add(loadedNode);
			}
		}
		return builder.build();
	}

	private STProperty loadProperty(STPartition partition,
			STPropertyContainer stNodeEntry, String parentKey,
			String propertyName, PropertyType type) throws Exception {
		JRedis jredis = factory.getFrom(partition);

		STProperty property = type.createProperty(propertyName, stNodeEntry);
		if (type.isKey()) {
			String value = toStr(jredis.get(KEY_WITH_PROPERTY_VALUE.format(
					parentKey, propertyName)));
			property.getInternalMethods().setStringValueOnLoad(this, value);
		}
		return property;
	}

	@Override
	protected void internalFlushSimpleProperty(Nothing reference,
			STPartition partition, STProperty dirtyProperty) throws Exception {
		JRedis jredis = factory.getFrom(partition);
		String uniqueKey = dirtyProperty.getParent().getKeyAsString();
		JRedisLoggedExecution jredisExecution = new JRedisLoggedExecution(
				uniqueKey, jredis);
		PropertyType type = dirtyProperty.isKey() ? PropertyType.KEY
				: (dirtyProperty.isIndexed() ? PropertyType.INDEXED
						: PropertyType.SIMPLE);

		internalFlushSimplePropertyAndCreateIndex(jredisExecution, partition,
				dirtyProperty.getPropertyName(), dirtyProperty
						.getInternalMethods().getTransientValueAsBytes(this),
				uniqueKey, type);

	}

	private void internalFlushSimplePropertyAndCreateIndex(
			JRedisLoggedExecution exec, STPartition partition,
			String propertyName, byte[] propertyValue, String uniqueKey,
			PropertyType propertyType) throws Exception {

		JRedis jredis = factory.getFrom(partition);
		String setName = propertyType.getSetName(uniqueKey);
		exec.sadd(setName, propertyName);
		String valueKey = KEY_WITH_PROPERTY_VALUE.format(uniqueKey,
				propertyName);
		if (propertyType.isIndexed()) {
			String stripped = stripString(propertyValue != null ? new String(
					propertyValue) : null);
			if (jredis.exists(valueKey)) {
				String existent = stripString(toStr(jredis.get(valueKey)));
				if (!existent.equals(stripped)) {
					jredis.srem(SET_WITH_INDEX_ENTRY.format(existent,
							propertyName), uniqueKey);
					exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped,
							propertyName), uniqueKey);
				}
			} else {
				exec.sadd(SET_WITH_INDEX_ENTRY.format(stripped, propertyName),
						uniqueKey);
			}
		}
		if (propertyValue != null) {
			exec.set(valueKey, propertyValue);
		} else {
			jredis.del(valueKey);
		}
	}

	private String stripString(String transientValueAsString) {
		return transientValueAsString == null ? "null" : transientValueAsString
				.replaceAll("[ ]|[\n]|[\t]|[\r]", "-");
	}

	@Override
	protected Iterable<String> internalGetAllNodeNames(STPartition partition)
			throws Exception {
		JRedis jredis = factory.getFrom(partition);
		return listBytesToListString(jredis.smembers(SET_WITH_ALL_KEY_NAMES));
	}

	@Override
	protected Nothing createLinkReferenceIfNecessary(STPartition partition,
			STLinkEntry entry) {
		return Nothing.NOTHING;
	}

	@Override
	protected void flushRemovedLink(STPartition partition, STLinkEntry link)
			throws Exception {
		JRedis jredis = factory.getFrom(partition);
		jredis.srem(SET_WITH_ALL_LINKS_FOR_NAME.format(link.getLinkName()),
				link.getKeyAsString());
		jredis.srem(SET_WITH_ALL_LINKS_FOR_ORIGIN.format(link.getOrigin()
				.getKeyAsString()), link.getKeyAsString());
		jredis.srem(SET_WITH_ALL_LINKS_FOR_TARGET.format(link.getTarget()
				.getKeyAsString()), link.getKeyAsString());
		flushRemoved(partition, link, null);
	}

	@Override
	protected void handleNewLink(STPartition partition, STNodeEntry origin,
			STLinkEntry link) throws Exception {
		JRedis jredis = factory.getFrom(partition);
		jredis.sadd(SET_WITH_ALL_LINKS_FOR_NAME.format(link.getLinkName()),
				link.getKeyAsString());
		jredis.sadd(SET_WITH_ALL_LINKS_FOR_ORIGIN.format(link.getOrigin()
				.getKeyAsString()), link.getKeyAsString());
		jredis.sadd(SET_WITH_ALL_LINKS_FOR_TARGET.format(link.getTarget()
				.getKeyAsString()), link.getKeyAsString());

	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterable<STLinkEntry> internalFindLinks(
			final STPartition partition, STNodeEntry origin,
			STNodeEntry destiny, String name) throws Exception {
		JRedis jredis = factory.getFrom(partition);
		List<String> linkIds = listBytesToListString(jredis
				.smembers(SET_WITH_ALL_LINKS_FOR_ORIGIN.format(origin
						.getKeyAsString())));
		if (destiny != null) {
			List<String> newIds = listBytesToListString(jredis
					.smembers(SET_WITH_ALL_LINKS_FOR_TARGET.format(destiny
							.getKeyAsString())));
			linkIds.retainAll(newIds);
		}
		if (name != null) {
			List<String> newIds = listBytesToListString(jredis
					.smembers(SET_WITH_ALL_LINKS_FOR_NAME.format(name)));
			linkIds.retainAll(newIds);
		}
		if (linkIds.size() == 0)
			return Collections.emptyList();
		return IteratorBuilder.<STLinkEntry, String> createIteratorBuilder()
				.withItems(linkIds).withConverter(
						new Converter<STLinkEntry, String>() {

							@Override
							public STLinkEntry convert(String o)
									throws Exception {
								STNodeEntry originNode = JRedisSTStorageSessionImpl.this
										.loadNodeOrReturnNull(
												StringIDSupport
														.getOriginKeyAsStringFromLinkKey(o),
												partition);
								String targetId = StringIDSupport
										.getTargeyKeyAsStringFromLinkKey(o);
								STPartition targetPartition = StringIDSupport
										.getPartition(
												targetId,
												JRedisSTStorageSessionImpl.this.partitionFactory);
								STNodeEntry targetNode = JRedisSTStorageSessionImpl.this
										.loadNodeOrReturnNull(targetId,
												targetPartition);

								return new STLinkEntryImpl(StringIDSupport
										.getLinkNameFromLinkKey(o), originNode,
										targetNode, true);
							}
						}).andBuild();
	}

	@Override
	protected Set<STProperty> internalPropertyContainerLoadProperties(
			Nothing reference, STPartition partition,
			STPropertyContainer stNodeEntry) throws Exception {
		JRedis jredis = factory.getFrom(partition);
		Set<STProperty> result = newHashSet();

		String parentKey = stNodeEntry.getKeyAsString();
		for (PropertyType type : PropertyType.values()) {
			String properties = type.getSetName(parentKey);
			if (jredis.exists(properties)) {
				List<String> propertyNames = listBytesToListString(jredis
						.smembers(properties));
				for (String propertyName : propertyNames) {
					STProperty property = loadProperty(partition, stNodeEntry,
							parentKey, propertyName, type);
					if (property != null)
						result.add(property);
				}
			}
		}
		return result;
	}

}
