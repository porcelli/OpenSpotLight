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
package org.openspotlight.graph.internal;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Sha1.getNumericSha1Signature;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.text.html.FormSubmitEvent.MethodType;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.Pair.PairEqualsMode;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.Element;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkType;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.PropertyContainer;
import org.openspotlight.graph.TreeLineReference;
import org.openspotlight.graph.annotation.DefineHierarchy;
import org.openspotlight.graph.annotation.InitialWeight;
import org.openspotlight.graph.annotation.IsMetaType;
import org.openspotlight.graph.annotation.TransientProperty;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.AbstractSTStorageSession.STUniqueKeyBuilderImpl;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STLinkEntry;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.common.collect.ImmutableSet;

public class NodeAndLinkSupport {

	public static interface NodeMetadata {
		public STNodeEntry getCached();

		public void setCached(STNodeEntry entry);

	}

	public static final String NUMERIC_TYPE = "__node_numeric_type";
	public static final String CAPTION = "__node_caption";
	public static final String CORRECT_CLASS = "__node_concrete_class";
	public static final String NAME = "__node_name";
	public static final String WEIGTH_VALUE = "__node_weigth_value";
	public static final String NODE_ID = "__node_weigth_value";

	public static int findInitialWeight(final Class<?> clazz) {
		return clazz.getAnnotation(InitialWeight.class).value();
	}

	public static boolean isMetanode(final Class<? extends Node> clazz) {
		return clazz.isAnnotationPresent(IsMetaType.class);
	}

	public static BigInteger findNumericType(final Class<? extends Node> type) {
		Class<?> currentType = type;
		int depth = 0;
		while (currentType != null) {
			if (!Node.class.isAssignableFrom(currentType)) {
				throw logAndReturn(new IllegalStateException(
						"No SLNode inherited type found with annotation "
								+ DefineHierarchy.class.getSimpleName()));
			}
			if (currentType.isAnnotationPresent(DefineHierarchy.class)) {
				return numericTypeFromClass((Class<? extends Node>) currentType)
						.add(BigInteger.valueOf(depth));
			}
			currentType = currentType.getSuperclass();
			depth++;
		}
		throw logAndReturn(new IllegalStateException(
				"No SLNode inherited type found with annotation "
						+ DefineHierarchy.class.getSimpleName() + " for type"
						+ type));
	}

	private static BigInteger numericTypeFromClass(
			Class<? extends Node> currentType) {
		return getNumericSha1Signature(currentType.getName());
	}

	@SuppressWarnings("unchecked")
	private static <T extends Node> T createNode(STPartitionFactory factory,
			STStorageSession session, String contextId, String parentId,
			Class<T> clazz, String name, boolean needsToVerifyType,
			Collection<Class<? extends Link>> linkTypesForLinkDeletion,
			Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion,
			STRepositoryPath repositoryPath) {
		Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
		Map<String, Serializable> propertyValues = newHashMap();
		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(clazz);
		STNodeEntry node = null;
		if (contextId == null) {
			throw new IllegalStateException();
		}
		STPartition partition = factory.getPartitionByName(contextId);
		STUniqueKey internalNodeKey;
		Class<? extends Node> targetNodeType = findTargetClass(clazz);

		if (session != null) {
			internalNodeKey = session.withPartition(partition).createKey(
					targetNodeType.getName()).withEntry(NAME, name).andCreate();
			node = session.withPartition(partition).createCriteria()
					.withUniqueKey(internalNodeKey).buildCriteria()
					.andFindUnique(session);
		} else {
			internalNodeKey = new STUniqueKeyBuilderImpl(targetNodeType
					.getName(), partition, repositoryPath)
					.withEntry(NAME, name).andCreate();
		}

		for (PropertyDescriptor d : descriptors) {
			if (d.getName().equals("class"))
				continue;
			propertyTypes.put(d.getName(),
					(Class<? extends Serializable>) Reflection
							.findClassWithoutPrimitives(d.getPropertyType()));
			Object rawValue = node != null ? node.getPropertyAsString(session,
					d.getName()) : null;
			Serializable value = (Serializable) (rawValue != null ? Conversion
					.convert(rawValue, d.getPropertyType()) : null);
			propertyValues.put(d.getName(), value);
		}
		int weigthValue;
		Set<String> stNodeProperties = node != null ? node
				.getPropertyNames(session) : Collections.<String> emptySet();
		if (stNodeProperties.contains(WEIGTH_VALUE)) {
			weigthValue = Conversion.convert(node.getPropertyAsString(session,
					WEIGTH_VALUE), Integer.class);
		} else {
			weigthValue = findInitialWeight(clazz);
		}
		Class<? extends Node> savedClass = null;
		if (stNodeProperties.contains(CORRECT_CLASS)) {
			savedClass = Conversion.convert(node.getPropertyAsString(session,
					CORRECT_CLASS), Class.class);
		}
		BigInteger savedClassNumericType = savedClass != null ? findNumericType(savedClass)
				: null;
		BigInteger proposedClassNumericType = findNumericType(clazz);
		Class<? extends Node> classToUse = savedClassNumericType != null
				&& savedClassNumericType.compareTo(proposedClassNumericType) > 0 ? savedClass
				: clazz;

		NodeImpl internalNode = new NodeImpl(name, classToUse, targetNodeType,
				internalNodeKey.getKeyAsString(), propertyTypes,
				propertyValues, parentId, contextId, weigthValue);
		if (node != null) {
			internalNode.cachedEntry = new WeakReference<STNodeEntry>(node);
			if (needsToVerifyType) {
				fixTypeData(session, classToUse, node);
			}
			String captionAsString = node.getPropertyAsString(session, CAPTION);
			if (captionAsString != null) {
				internalNode.setCaption(captionAsString);
			}

		}
		final Enhancer e = new Enhancer();
		e.setSuperclass(classToUse);
		e.setInterfaces(new Class<?>[] { NodeMetadata.class });
		e.setCallback(new PropertyContainerInterceptor(internalNode));
		return (T) e.create(new Class[0], new Object[0]);
	}

	private static void fixTypeData(STStorageSession session,
			Class<? extends Node> clazz, STNodeEntry node) {
		String numericTypeAsString = node.getPropertyAsString(session,
				NUMERIC_TYPE);
		BigInteger numericTypeFromTargetNodeType = findNumericType(clazz);
		if (numericTypeAsString != null) {
			BigInteger numericTypeAsBigInteger = new BigInteger(
					numericTypeAsString);
			if (numericTypeFromTargetNodeType
					.compareTo(numericTypeAsBigInteger) > 0) {
				setWeigthAndTypeOnNode(session, node, clazz,
						numericTypeFromTargetNodeType);
			}
		} else {
			setWeigthAndTypeOnNode(session, node, clazz,
					numericTypeFromTargetNodeType);
		}
	}

	private static void setWeigthAndTypeOnNode(STStorageSession session,
			STNodeEntry node, Class<? extends Node> type,
			BigInteger weightFromTargetNodeType) {
		node.setIndexedProperty(session, NUMERIC_TYPE, weightFromTargetNodeType
				.toString());
		node.setIndexedProperty(session, CORRECT_CLASS, type.getName());

	}

	public static Class<? extends Node> findTargetClass(final Class<?> type) {
		Class<?> currentType = type;
		while (currentType != null) {
			if (!Node.class.isAssignableFrom(currentType)) {
				throw logAndReturn(new IllegalStateException(
						"No SLNode inherited type found with annotation "
								+ DefineHierarchy.class.getSimpleName()));
			}
			if (currentType.isAnnotationPresent(DefineHierarchy.class)) {
				return (Class<? extends Node>) currentType;
			}
			currentType = currentType.getSuperclass();
		}
		throw logAndReturn(new IllegalStateException(
				"No SLNode inherited type found with annotation "
						+ DefineHierarchy.class.getSimpleName()));
	}

	public static STNodeEntry retrievePreviousNode(STPartitionFactory factory,
			STStorageSession session, Context context, Node node,
			boolean needsToVerifyType) {
		try {
			NodeMetadata metadata = (NodeMetadata) node;
			STNodeEntry internalNode = metadata.getCached();
			if (internalNode == null) {
				STPartition partition = factory.getPartitionByName(context
						.getId());
				internalNode = session.withPartition(partition).createWithName(
						findTargetClass(node.getClass()).getName())
						.withKeyEntry(NAME, node.getName()).withParentAsString(
								node.getParentId()).andCreate();
				if (needsToVerifyType) {
					fixTypeData(session, (Class<? extends Node>) node
							.getClass().getSuperclass(), internalNode);
				}
				metadata.setCached(internalNode);

			}
			internalNode
					.setIndexedProperty(session, CAPTION, node.getCaption());
			for (String propName : node.getPropertyKeys()) {
				Serializable value = node.getPropertyValue(propName);
				if (!PropertyUtils.getPropertyDescriptor(node, propName)
						.getReadMethod().isAnnotationPresent(
								TransientProperty.class)) {
					internalNode.setIndexedProperty(session, propName,
							Conversion.convert(value, String.class));

				}

			}
			return internalNode;
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public static <T extends Link> T createLink(STPartitionFactory factory,
			STStorageSession session, Class<T> clazz, Node rawOrigin,
			Node rawTarget, LinkType type, boolean createIfDontExists) {
		Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
		Map<String, Serializable> propertyValues = newHashMap();
		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(clazz);

		STLinkEntry linkEntry = null;
		Node origin, target;

		if (LinkType.BIDIRECTIONAL.equals(type)
				&& rawOrigin.compareTo(rawTarget) < 0) {
			origin = rawTarget;
			target = rawOrigin;
		} else {
			origin = rawOrigin;
			target = rawTarget;
		}
		String linkId = null;
		if (session != null) {
			STNodeEntry originAsSTNode = session.findNodeByStringId(origin
					.getId());
			STNodeEntry targetAsSTNode = session.findNodeByStringId(target
					.getId());

			linkEntry = session.getLink(originAsSTNode, targetAsSTNode, clazz
					.getName());
			if (linkEntry == null && createIfDontExists) {

				linkEntry = session.addLink(originAsSTNode, targetAsSTNode,
						clazz.getName());
			}
			linkId = linkEntry != null ? linkEntry.getKeyAsString()
					: StringIDSupport.getLinkKeyAsString(originAsSTNode
							.getPartition(), clazz.getName(), originAsSTNode,
							targetAsSTNode);
		}

		for (PropertyDescriptor d : descriptors) {
			if (d.getName().equals("class"))
				continue;
			propertyTypes.put(d.getName(),
					(Class<? extends Serializable>) Reflection
							.findClassWithoutPrimitives(d.getPropertyType()));
			Object rawValue = linkEntry != null ? linkEntry
					.getPropertyAsString(session, d.getName()) : null;
			Serializable value = (Serializable) (rawValue != null ? Conversion
					.convert(rawValue, d.getPropertyType()) : null);
			propertyValues.put(d.getName(), value);
		}
		int weigthValue;
		Set<String> stNodeProperties = linkEntry != null ? linkEntry
				.getPropertyNames(session) : Collections.<String> emptySet();
		if (stNodeProperties.contains(WEIGTH_VALUE)) {
			weigthValue = Conversion.convert(linkEntry.getPropertyAsString(
					session, WEIGTH_VALUE), Integer.class);
		} else {
			weigthValue = findInitialWeight(clazz);
		}
		LinkImpl internalLink = new LinkImpl(linkId, clazz.getName(), clazz,
				propertyTypes, propertyValues, findInitialWeight(clazz),
				weigthValue, origin, target, LinkType.BIDIRECTIONAL
						.equals(type));
		if (linkEntry != null) {
			internalLink.cachedEntry = new WeakReference<STLinkEntry>(linkEntry);

		}
		final Enhancer e = new Enhancer();
		e.setSuperclass(clazz);
		e.setInterfaces(new Class<?>[] { NodeMetadata.class });
		e.setCallback(new PropertyContainerInterceptor(internalLink));
		return (T) e.create(new Class[0], new Object[0]);
	}

	public static <T extends Node> T createNode(STPartitionFactory factory,
			STStorageSession session, String contextId, String parentId,
			Class<T> clazz, String name, boolean needsToVerifyType,
			Collection<Class<? extends Link>> linkTypesForLinkDeletion,
			Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion) {
		return createNode(factory, session, contextId, parentId, clazz, name,
				needsToVerifyType, linkTypesForLinkDeletion,
				linkTypesForLinkedNodeDeletion, null);

	}

	private static class LinkImpl extends Link {

		private WeakReference<STLinkEntry> cachedEntry;

		public LinkImpl(String id, String linkName,
				Class<? extends Link> linkType,
				Map<String, Class<? extends Serializable>> propertyTypes,
				Map<String, Serializable> propertyValues,
				int initialWeigthValue, int weightValue, Node source,
				Node target, boolean bidirectional) {
			this.propertyContainerImpl = new PropertyContainerImpl(id, linkType
					.getName(), propertyTypes, propertyValues,
					initialWeigthValue, weightValue);
			this.sides[SOURCE] = source;
			this.sides[TARGET] = target;

		}

		private final PropertyContainerImpl propertyContainerImpl;

		public void createLineReference(int beginLine, int endLine,
				int beginColumn, int endColumn, String statement,
				String artifactId) {
			propertyContainerImpl.createLineReference(beginLine, endLine,
					beginColumn, endColumn, statement, artifactId);
		}

		public boolean equals(Object obj) {
			return propertyContainerImpl.equals(obj);
		}

		public String getId() {
			return propertyContainerImpl.getId();
		}

		public final int getInitialWeightValue() {
			return propertyContainerImpl.getInitialWeightValue();
		}

		public Set<Pair<String, Serializable>> getProperties() {
			return propertyContainerImpl.getProperties();
		}

		public Iterable<String> getPropertyKeys() {
			return propertyContainerImpl.getPropertyKeys();
		}

		public <V extends Serializable> V getPropertyValue(String key,
				V defaultValue) {
			return propertyContainerImpl.getPropertyValue(key, defaultValue);
		}

		public <V extends Serializable> V getPropertyValue(String key) {
			return propertyContainerImpl.getPropertyValue(key);
		}

		public String getPropertyValueAsString(String key) {
			return propertyContainerImpl.getPropertyValueAsString(key);
		}

		public TreeLineReference getTreeLineReferences() {
			return propertyContainerImpl.getTreeLineReferences();
		}

		public TreeLineReference getTreeLineReferences(String artifactId) {
			return propertyContainerImpl.getTreeLineReferences(artifactId);
		}

		public String getTypeName() {
			return propertyContainerImpl.getTypeName();
		}

		public final int getWeightValue() {
			return propertyContainerImpl.getWeightValue();
		}

		public int hashCode() {
			return propertyContainerImpl.hashCode();
		}

		public boolean hasProperty(String key) throws IllegalArgumentException {
			return propertyContainerImpl.hasProperty(key);
		}

		public boolean isDirty() {
			return propertyContainerImpl.isDirty();
		}

		public void removeProperty(String key) {
			propertyContainerImpl.removeProperty(key);
		}

		public void resetDirtyFlag() {
			propertyContainerImpl.resetDirtyFlag();
		}

		public <V extends Serializable> void setProperty(String key, V value)
				throws IllegalArgumentException {
			propertyContainerImpl.setProperty(key, value);
		}

		public String toString() {
			return propertyContainerImpl.toString();
		}

		private static final int SOURCE = 0;
		private static final int TARGET = 1;

		private int count;
		private boolean bidirectional;

		private final Node[] sides = new Node[2];

		@Override
		public Node getOtherSide(Node node) throws IllegalArgumentException {
			if (node.equals(sides[SOURCE]))
				return sides[TARGET];
			if (node.equals(sides[TARGET]))
				return sides[SOURCE];
			throw new IllegalArgumentException();
		}

		@Override
		public Node[] getSides() {
			return new Node[] { sides[SOURCE], sides[TARGET] };
		}

		@Override
		public Node getSource() {
			return sides[SOURCE];
		}

		@Override
		public Node getTarget() {
			return sides[TARGET];
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public boolean isBidirectional() {
			return bidirectional;
		}

		@Override
		public void setCount(int value) {
			this.count = value;
			this.propertyContainerImpl.markAsDirty();

		}

		@Override
		public int compareTo(Link o) {
			return getId().compareTo(o.getId());
		}

	}

	private static class PropertyContainerImpl implements Element {

		public final int getInitialWeightValue() {
			return initialWeightValue;
		}

		public final int getWeightValue() {
			return weightValue;
		}

		private final String id;

		private final String typeName;

		private final Map<String, Class<? extends Serializable>> propertyTypes;
		private final Map<String, Serializable> propertyValues;
		private final AtomicBoolean dirty;

		public void markAsDirty() {
			this.dirty.set(true);
		}

		private Set<String> removedProperties;

		public PropertyContainerImpl(String id, String typeName,
				Map<String, Class<? extends Serializable>> propertyTypes,
				Map<String, Serializable> propertyValues,
				int initialWeigthValue, int weightValue) {
			dirty = new AtomicBoolean();
			this.typeName = typeName;
			this.initialWeightValue = initialWeigthValue;
			this.weightValue = weightValue;
			this.id = id;
			this.propertyTypes = propertyTypes;
			this.propertyValues = propertyValues;
			this.removedProperties = newHashSet();
		}

		public void resetDirtyFlag() {
			dirty.set(false);
			removedProperties.clear();
		}

		@Override
		public void createLineReference(int beginLine, int endLine,
				int beginColumn, int endColumn, String statement,
				String artifactId) {
			throw new UnsupportedOperationException(
					"Should be lazy property? :D");
			// TODO implement
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public Set<Pair<String, Serializable>> getProperties() {
			ImmutableSet.Builder<Pair<String, Serializable>> builder = ImmutableSet
					.builder();
			for (Map.Entry<String, ? extends Serializable> entry : propertyValues
					.entrySet()) {
				builder.add(newPair(entry.getKey(), (Serializable) entry
						.getValue(), PairEqualsMode.K1));
			}
			return builder.build();
		}

		@Override
		public Iterable<String> getPropertyKeys() {
			return ImmutableSet.copyOf(propertyTypes.keySet());
		}

		@SuppressWarnings("unchecked")
		@Override
		public <V extends Serializable> V getPropertyValue(String key) {
			return (V) propertyValues.get(key);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <V extends Serializable> V getPropertyValue(String key,
				V defaultValue) {
			V value = (V) propertyValues.get(key);
			return value == null ? defaultValue : value;
		}

		@Override
		public String getPropertyValueAsString(String key) {
			return convert(propertyValues.get(key), String.class);
		}

		@Override
		public TreeLineReference getTreeLineReferences() {
			throw new UnsupportedOperationException(
					"Should be lazy property? :D");
		}

		@Override
		public TreeLineReference getTreeLineReferences(String artifactId) {
			throw new UnsupportedOperationException(
					"Should be lazy property? :D");
		}

		@Override
		public String getTypeName() {
			return typeName;
		}

		@Override
		public boolean hasProperty(String key) throws IllegalArgumentException {
			return propertyTypes.containsKey(Strings
					.firstLetterToLowerCase(key));
		}

		@Override
		public void removeProperty(String key) {
			key = Strings.firstLetterToLowerCase(key);
			propertyTypes.remove(key);
			propertyValues.remove(key);
			removedProperties.add(key);
			dirty.set(true);
		}

		@Override
		public <V extends Serializable> void setProperty(String key, V value)
				throws IllegalArgumentException {
			key = Strings.firstLetterToLowerCase(key);
			if (!hasProperty(key))
				throw logAndReturn(new IllegalArgumentException(
						"invalid property key " + key + " for type "
								+ getTypeName()));
			Class<? extends Serializable> propType = propertyTypes.get(key);
			if (value != null) {
				Class<?> valueType = Reflection
						.findClassWithoutPrimitives(value.getClass());
				if (!valueType.isAssignableFrom(propType)) {
					throw logAndReturn(new IllegalArgumentException(
							"invalid property type "
									+ value.getClass().getName() + " for type "
									+ getTypeName() + " (should be "
									+ propertyTypes.get(key).getName() + ")"));

				}

			}
			propertyValues.put(key, value);
			dirty.set(true);
		}

		@Override
		public boolean isDirty() {
			return dirty.get();
		}

		private final int initialWeightValue;

		private final int weightValue;

	}

	private static class NodeImpl extends Node implements NodeMetadata {

		private final PropertyContainerImpl propertyContainerImpl;

		public void createLineReference(int beginLine, int endLine,
				int beginColumn, int endColumn, String statement,
				String artifactId) {
			propertyContainerImpl.createLineReference(beginLine, endLine,
					beginColumn, endColumn, statement, artifactId);
		}

		public String getId() {
			return propertyContainerImpl.getId();
		}

		public final int getInitialWeightValue() {
			return propertyContainerImpl.getInitialWeightValue();
		}

		public Set<Pair<String, Serializable>> getProperties() {
			return propertyContainerImpl.getProperties();
		}

		public Iterable<String> getPropertyKeys() {
			return propertyContainerImpl.getPropertyKeys();
		}

		public <V extends Serializable> V getPropertyValue(String key,
				V defaultValue) {
			return propertyContainerImpl.getPropertyValue(key, defaultValue);
		}

		public <V extends Serializable> V getPropertyValue(String key) {
			return propertyContainerImpl.getPropertyValue(key);
		}

		public String getPropertyValueAsString(String key) {
			return propertyContainerImpl.getPropertyValueAsString(key);
		}

		public TreeLineReference getTreeLineReferences() {
			return propertyContainerImpl.getTreeLineReferences();
		}

		public TreeLineReference getTreeLineReferences(String artifactId) {
			return propertyContainerImpl.getTreeLineReferences(artifactId);
		}

		public String getTypeName() {
			return propertyContainerImpl.getTypeName();
		}

		public final int getWeightValue() {
			return propertyContainerImpl.getWeightValue();
		}

		public boolean hasProperty(String key) throws IllegalArgumentException {
			return propertyContainerImpl.hasProperty(key);
		}

		public boolean isDirty() {
			return propertyContainerImpl.isDirty();
		}

		public void removeProperty(String key) {
			propertyContainerImpl.removeProperty(key);
		}

		public <V extends Serializable> void setProperty(String key, V value)
				throws IllegalArgumentException {
			propertyContainerImpl.setProperty(key, value);
		}

		public BigInteger getNumericType() {
			return numericType;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Node))
				return false;
			Node slnode = (Node) obj;

			boolean result = getId().equals(slnode.getId())
					&& Equals.eachEquality(getParentId(), slnode.getParentId())
					&& Equals.eachEquality(getContextId(), slnode
							.getContextId());
			return result;
		}

		private volatile int hashCode = 0;

		@Override
		public int hashCode() {
			int result = hashCode;
			if (result == 0) {
				result = HashCodes.hashOf(getId(), getParentId(),
						getContextId());
				hashCode = result;
			}
			return result;
		}

		@Override
		public String toString() {
			return getName() + ":" + getId();
		}

		private final String contextId;

		public String getContextId() {
			return contextId;
		}

		private WeakReference<STNodeEntry> cachedEntry;

		private final Class<? extends Node> targetNode;

		private String caption;

		private final String name;

		private final Class<? extends Node> type;

		private final String parentId;

		public String getParentId() {
			return parentId;
		}

		private NodeImpl(String name, Class<? extends Node> type,
				Class<? extends Node> targetNode, String id,
				Map<String, Class<? extends Serializable>> propertyTypes,
				Map<String, Serializable> propertyValues, String parentId,
				String contextId, int weightValue) {
			this.propertyContainerImpl = new PropertyContainerImpl(id, type
					.getName(), propertyTypes, propertyValues,
					findInitialWeight(type), weightValue);
			this.type = type;
			this.name = name;
			this.numericType = findNumericType(type);
			this.targetNode = targetNode;
			this.parentId = parentId;
			this.contextId = contextId;
		}

		public void resetDirtyFlag() {
			propertyContainerImpl.resetDirtyFlag();
		}

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(Node o) {
			return numericType.compareTo(o.getNumericType());
		}

		@Override
		public STNodeEntry getCached() {
			return cachedEntry != null ? cachedEntry.get() : null;
		}

		@Override
		public void setCached(STNodeEntry entry) {
			this.cachedEntry = new WeakReference<STNodeEntry>(entry);

		}

		private final BigInteger numericType;

	}

	private static class PropertyContainerInterceptor implements
			MethodInterceptor {

		private final PropertyContainer internalPropertyContainerImpl;

		public PropertyContainerInterceptor(
				PropertyContainer propertyContainerImpl) {
			this.internalPropertyContainerImpl = propertyContainerImpl;
		}

		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy proxy) throws Throwable {

			Class<?> declarringClass = method.getDeclaringClass();
			boolean methodFromSuperClasses = declarringClass.equals(Node.class)
					|| declarringClass.equals(Link.class)
					|| declarringClass.equals(PropertyContainerImpl.class)
					|| declarringClass.isInterface()
					|| declarringClass.equals(Object.class);
			String methodName = method.getName();
			if (methodFromSuperClasses) {
				return method.invoke(internalPropertyContainerImpl, args);
			} else {
				switch (getMethodType(methodName, method)) {
				case GETTER:
					return invokeGetter(methodName);
				case SETTER:
					return invokeSetter(obj, methodName, method, args, proxy);
				}
				return proxy.invokeSuper(obj, args);
			}

		}

		private Object invokeSetter(Object obj, String methodName,
				Method method, Object[] args, MethodProxy methodProxy)
				throws Throwable {
			internalPropertyContainerImpl.setProperty(methodName.substring(3),
					(Serializable) args[0]);
			return null;
		}

		private Serializable invokeGetter(String methodName) {

			String propertyName = methodName.startsWith("get") ? Strings
					.firstLetterToLowerCase(methodName.substring(3)) : Strings
					.firstLetterToLowerCase(methodName.substring(2));// is
			return internalPropertyContainerImpl.getPropertyValue(propertyName);

		}

		private MethodType getMethodType(String methodName, Method method) {
			if (method.isAnnotationPresent(TransientProperty.class))
				return MethodType.OTHER;
			if (methodName.startsWith("set")
					&& method.getParameterTypes().length == 1
					&& internalPropertyContainerImpl.hasProperty(methodName
							.substring(3))) {
				return MethodType.SETTER;
			} else if (methodName.startsWith("get")
					&& method.getParameterTypes().length == 0
					&& internalPropertyContainerImpl.hasProperty(methodName
							.substring(3))) {
				return MethodType.GETTER;
			} else if (methodName.startsWith("is")
					&& method.getParameterTypes().length == 0
					&& internalPropertyContainerImpl.hasProperty(methodName
							.substring(2))) {
				return MethodType.GETTER;
			}
			return MethodType.OTHER;
		}

		private static enum MethodType {
			SETTER, GETTER, OTHER
		}
	}

}
