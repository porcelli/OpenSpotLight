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
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.Element;
import org.openspotlight.graph.Link;
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
import org.openspotlight.storage.AbstractSTStorageSession.STUniqueKeyBuilderImpl;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.common.collect.ImmutableSet;

public class NodeSupport {

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
			Class<T> clazz, String name,
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

			propertyTypes.put(d.getName(),
					(Class<? extends Serializable>) Reflection
							.findClassWithoutPrimitives(d.getPropertyType()));
			Serializable value = node != null ? (Serializable) convert(node
					.getPropertyAsString(session, d.getName()), d
					.getPropertyType()) : null;
			propertyValues.put(d.getName(), value);
		}
		int weigthValue;
		if (propertyValues.containsKey(WEIGTH_VALUE)) {
			weigthValue = Conversion.convert(propertyValues.get(WEIGTH_VALUE),
					Integer.class);
		} else {
			weigthValue = findInitialWeight(clazz);
		}
		Class<? extends Node> savedClass = null;
		if (propertyValues.containsKey(CORRECT_CLASS)) {
			savedClass = Conversion.convert(propertyValues.get(CORRECT_CLASS),
					Class.class);
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
			fixTypeData(session, clazz, node);
			String captionAsString = node.getPropertyAsString(session, CAPTION);
			if (captionAsString != null) {
				internalNode.setCaption(captionAsString);
			}

		}
		final Enhancer e = new Enhancer();
		e.setSuperclass(clazz);
		e.setInterfaces(new Class<?>[] { NodeMetadata.class });
		e.setCallback(new NodeInterceptor(internalNode));
		return (T) e.create(new Class[0], new Object[0]);
	}

	private static void fixTypeData(STStorageSession session,
			Class<? extends Node> clazz, STNodeEntry node) {
		String weigthAsString = node.getPropertyAsString(session, NUMERIC_TYPE);
		BigInteger weightFromTargetNodeType = findNumericType(clazz);
		if (weigthAsString != null) {
			BigInteger weightAsBigInteger = new BigInteger(weigthAsString);
			if (weightAsBigInteger.compareTo(weightFromTargetNodeType) > 0) {
				setWeigthAndTypeOnNode(session, node, clazz,
						weightFromTargetNodeType);
			}
		} else {
			setWeigthAndTypeOnNode(session, node, clazz,
					weightFromTargetNodeType);
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
			STStorageSession session, Context context, Node node) {
		NodeMetadata metadata = (NodeMetadata) node;
		STNodeEntry internalNode = metadata.getCached();
		if (internalNode == null) {
			STPartition partition = factory.getPartitionByName(context.getId());
			internalNode = session.withPartition(partition).createWithName(
					findTargetClass(node.getClass()).getName()).withKeyEntry(
					NAME, node.getName())
					.withParentAsString(node.getParentId()).andCreate();
			fixTypeData(session, (Class<? extends Node>) node.getClass()
					.getSuperclass(), internalNode);
			metadata.setCached(internalNode);
			internalNode
					.setIndexedProperty(session, CAPTION, node.getCaption());
			for (String propName : node.getPropertyKeys()) {
				Serializable value = node.getPropertyValue(propName);
				internalNode.setIndexedProperty(session, propName, Conversion
						.convert(value, String.class));
			}

		}
		return internalNode;
	}

	public static <T extends Node> T createNode(STPartitionFactory factory,
			STStorageSession session, String contextId, String parentId,
			Class<T> clazz, String name,
			Collection<Class<? extends Link>> linkTypesForLinkDeletion,
			Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion) {
		return createNode(factory, session, contextId, parentId, clazz, name,
				linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion, null);

	}

	private static class NodeImpl extends Node implements NodeMetadata {

		public int getInitialWeightValue() {
			return initialWeightValue;
		}

		public int getWeightValue() {
			return weightValue;
		}

		public BigInteger getNumericType() {
			return numericType;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Node))
				return false;
			Node slnode = (Node) obj;
			return getId().equals(slnode.getId());
		}

		@Override
		public int hashCode() {
			return getId().hashCode();
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

		private final String id;
		private final String parentId;

		public String getParentId() {
			return parentId;
		}

		private final String typeName;

		private Context context;

		private final Map<String, Class<? extends Serializable>> propertyTypes;
		private final Map<String, Serializable> propertyValues;
		private final AtomicBoolean dirty;

		private Set<String> removedProperties;

		private NodeImpl(String name, Class<? extends Node> type,
				Class<? extends Node> targetNode, String id,
				Map<String, Class<? extends Serializable>> propertyTypes,
				Map<String, Serializable> propertyValues, String parentId,
				String contextId, int weightValue) {
			dirty = new AtomicBoolean();
			this.type = type;
			this.typeName = type.getName();
			this.name = name;
			this.numericType = findNumericType(type);
			this.initialWeightValue = findInitialWeight(type);
			this.weightValue = weightValue;
			this.id = id;
			this.propertyTypes = propertyTypes;
			this.propertyValues = propertyValues;
			this.removedProperties = newHashSet();
			this.targetNode = targetNode;
			this.parentId = parentId;
			this.contextId = contextId;
		}

		public void resetDirtyFlag() {
			dirty.set(false);
			removedProperties.clear();
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
		public void createLineReference(int beginLine, int endLine,
				int beginColumn, int endColumn, String statement,
				String artifactId) {
			throw new UnsupportedOperationException(
					"Should be lazy property? :D");

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

		@Override
		public <V extends Serializable> V getPropertyValue(String key) {
			return (V) propertyValues.get(key);
		}

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

		@Override
		public STNodeEntry getCached() {
			return cachedEntry != null ? cachedEntry.get() : null;
		}

		@Override
		public void setCached(STNodeEntry entry) {
			this.cachedEntry = new WeakReference<STNodeEntry>(entry);

		}

		private final int initialWeightValue;

		private final int weightValue;

		private final BigInteger numericType;

	}

	private static class NodeInterceptor implements MethodInterceptor {

		private final NodeImpl internalNodeImpl;

		public NodeInterceptor(NodeImpl nodeImpl) {
			this.internalNodeImpl = nodeImpl;
		}

		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy proxy) throws Throwable {

			Class<?> declarringClass = method.getDeclaringClass();
			boolean methodFromSuperClasses = declarringClass.equals(Node.class)
					|| declarringClass.isInterface()
					|| declarringClass.equals(Object.class);
			String methodName = method.getName();
			if (methodFromSuperClasses) {
				return method.invoke(internalNodeImpl, args);
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
			internalNodeImpl.setProperty(methodName.substring(3),
					(Serializable) args[0]);
			return null;
		}

		private Serializable invokeGetter(String methodName) {

			String propertyName = methodName.startsWith("get") ? Strings
					.firstLetterToLowerCase(methodName.substring(3)) : Strings
					.firstLetterToLowerCase(methodName.substring(2));// is
			return internalNodeImpl.getPropertyValue(propertyName);

		}

		private MethodType getMethodType(String methodName, Method method) {
			if (method.isAnnotationPresent(TransientProperty.class))
				return MethodType.OTHER;
			if (methodName.startsWith("set")
					&& method.getParameterTypes().length == 1
					&& internalNodeImpl.hasProperty(methodName.substring(3))) {
				return MethodType.SETTER;
			} else if (methodName.startsWith("get")
					&& method.getParameterTypes().length == 0
					&& internalNodeImpl.hasProperty(methodName.substring(3))) {
				return MethodType.GETTER;
			} else if (methodName.startsWith("is")
					&& method.getParameterTypes().length == 0
					&& internalNodeImpl.hasProperty(methodName.substring(2))) {
				return MethodType.GETTER;
			}
			return MethodType.OTHER;
		}

		private static enum MethodType {
			SETTER, GETTER, OTHER
		}
	}

}
