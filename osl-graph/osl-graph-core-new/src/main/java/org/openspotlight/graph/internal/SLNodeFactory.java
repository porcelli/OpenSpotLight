package org.openspotlight.graph.internal;

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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.Pair.PairEqualsMode;
import org.openspotlight.graph.SLElement;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLTreeLineReference;
import org.openspotlight.graph.annotation.SLDefineHierarchy;
import org.openspotlight.graph.exception.PropertyNotFoundException;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.AbstractSTStorageSession.STUniqueKeyBuilderImpl;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.common.collect.ImmutableSet;
import static org.openspotlight.common.util.Sha1.getNumericSha1Signature;
import static org.openspotlight.common.util.Conversion.convert;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Exceptions.logAndReturn;

public class SLNodeFactory {

	@SuppressWarnings("unchecked")
	private static <T extends SLNode> T createNode(STStorageSession session,
			SLNode parent, Class<T> clazz, String name,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion,
			STRepositoryPath repositoryPath) {
		Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
		Map<String, Serializable> propertyValues = newHashMap();
		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(clazz);
		STNodeEntry node = null;
		STUniqueKey internalNodeKey;
		if (session != null) {
			internalNodeKey = session.withPartition(SLPartition.GRAPH)
					.createKey(clazz.getName()).withEntry("name", name)
					.andCreate();
			node = session.withPartition(SLPartition.GRAPH).createCriteria()
					.withUniqueKey(internalNodeKey).buildCriteria()
					.andFindUnique(session);
		} else {
			internalNodeKey = new STUniqueKeyBuilderImpl(clazz.getName(),
					SLPartition.GRAPH, repositoryPath).withEntry("name", name)
					.andCreate();
		}

		for (PropertyDescriptor d : descriptors) {
			propertyTypes.put(d.getName(), (Class<? extends Serializable>) d
					.getPropertyType());
			Serializable value = node != null ? (Serializable) convert(node
					.getPropertyAsString(session, d.getName()), d
					.getPropertyType()) : null;
			propertyValues.put(d.getName(), value);
		}

		SLNodeImpl internalNode = new SLNodeImpl(name, clazz, internalNodeKey
				.getKeyAsString(), propertyTypes, propertyValues);
		if (node != null) {
			internalNode.cachedEntry = new WeakReference<STNodeEntry>(node);
		}
		final Enhancer e = new Enhancer();
		e.setSuperclass(clazz);
		e.setCallback(new SLNodeInterceptor(internalNode));
		return (T) e.create(new Class[0], new Object[0]);
	}

	public static <T extends SLNode> T createNode(
			STRepositoryPath repositoryPath, SLNode parent, Class<T> clazz,
			String name,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		return createNode(null, parent, clazz, name, linkTypesForLinkDeletion,
				linkTypesForLinkedNodeDeletion, repositoryPath);

	}

	public static <T extends SLNode> T createNode(STStorageSession session,
			SLNode parent, Class<T> clazz, String name,
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		return createNode(session, parent, clazz, name,
				linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion, null);

	}

	private static class SLNodeImpl extends SLNode {

		private WeakReference<STNodeEntry> cachedEntry;

		private String caption;

		private final String name;

		private final Class<? extends SLNode> type;

		private final String id;

		private final String typeName;

		private final BigInteger weight;

		private final Map<String, Class<? extends Serializable>> propertyTypes;
		private final Map<String, Serializable> propertyValues;
		private final AtomicBoolean dirty;

		private Set<String> removedProperties;

		private SLNodeImpl(String name, Class<? extends SLNode> type,
				String id,
				Map<String, Class<? extends Serializable>> propertyTypes,
				Map<String, Serializable> propertyValues) {
			dirty = new AtomicBoolean();
			this.type = type;
			this.typeName = type.getName();
			this.name = name;
			this.weight = findWeigth(type);
			this.id = id;
			this.propertyTypes = propertyTypes;
			this.propertyValues = propertyValues;
			this.removedProperties = newHashSet();
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

		private static BigInteger findWeigth(final Class<? extends SLNode> type) {
			Class<?> currentType = type;
			int depth = 0;
			while (currentType != null) {
				if (!SLNode.class.isAssignableFrom(currentType)) {
					throw logAndReturn(new IllegalStateException(
							"No SLNode inherited type found with annotation "
									+ SLDefineHierarchy.class.getSimpleName()));
				}
				if (currentType.isAnnotationPresent(SLDefineHierarchy.class)) {
					return weightFromClass(
							(Class<? extends SLNode>) currentType).add(
							BigInteger.valueOf(depth));
				}
				currentType = currentType.getSuperclass();
				depth++;
			}
			throw logAndReturn(new IllegalStateException(
					"No SLNode inherited type found with annotation "
							+ SLDefineHierarchy.class.getSimpleName()));
		}

		private static BigInteger weightFromClass(
				Class<? extends SLNode> currentType) {
			return getNumericSha1Signature(currentType.getName());
		}

		@Override
		public int compareTo(SLNode o) {
			return weight.compareTo(o.getWeight());
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
		public <V extends Serializable> V getPropertyValue(String key)
				throws PropertyNotFoundException {
			return (V) propertyValues.get(key);
		}

		@Override
		public <V extends Serializable> V getPropertyValue(String key,
				V defaultValue) {
			V value = (V) propertyValues.get(key);
			return value == null ? defaultValue : value;
		}

		@Override
		public String getPropertyValueAsString(String key)
				throws PropertyNotFoundException {
			return convert(propertyValues.get(key), String.class);
		}

		@Override
		public SLTreeLineReference getTreeLineReferences() {
			throw new UnsupportedOperationException(
					"Should be lazy property? :D");
		}

		@Override
		public SLTreeLineReference getTreeLineReferences(String artifactId) {
			throw new UnsupportedOperationException(
					"Should be lazy property? :D");
		}

		@Override
		public String getTypeName() {
			return typeName;
		}

		@Override
		public BigInteger getWeight() {
			return weight;
		}

		@Override
		public boolean hasProperty(String key) throws IllegalArgumentException {
			return propertyTypes.containsKey(key);
		}

		@Override
		public void removeProperty(String key) {
			propertyTypes.remove(key);
			propertyValues.remove(key);
			removedProperties.add(key);
			dirty.set(true);
		}

		@Override
		public <V extends Serializable> void setProperty(String key, V value)
				throws IllegalArgumentException {
			if (!hasProperty(key))
				throw logAndReturn(new IllegalArgumentException(
						"invalid property key " + key + " for type "
								+ getTypeName()));
			if (value != null && !propertyTypes.get(key).isInstance(value)) {
				throw logAndReturn(new IllegalArgumentException(
						"invalid property type " + value.getClass().getName()
								+ " for type " + getTypeName() + " (should be "
								+ propertyTypes.get(key).getName() + ")"));
			}
			propertyValues.put(key, value);
			dirty.set(true);
		}

		@Override
		public boolean isDirty() {
			return dirty.get();
		}

	}

	private static class SLNodeInterceptor implements MethodInterceptor {

		private final SLNodeImpl internalNodeImpl;

		public SLNodeInterceptor(SLNodeImpl nodeImpl) {
			this.internalNodeImpl = nodeImpl;
		}

		@Override
		public Object intercept(Object obj, Method method, Object[] args,
				MethodProxy proxy) throws Throwable {

			Class<?> declarringClass = method.getDeclaringClass();
			boolean methodFromSLNode = declarringClass.equals(SLNode.class)
					|| declarringClass.equals(SLElement.class);
			String methodName = method.getName();
			if (methodFromSLNode && !Modifier.isAbstract(method.getModifiers())) {
				if (isSetterMethod(methodName, method)) {
					updateValueOnThisImpl(obj, proxy, args);
				}
				return method.invoke(internalNodeImpl, args);
			} else {
				if (isSetterMethod(methodName, method)) {
					updateValueOnInternalImpl(methodName, method, args);
				}
				return proxy.invokeSuper(obj, args);
			}
		}

		private void updateValueOnThisImpl(Object obj, MethodProxy method,
				Object[] args) throws Throwable {
			method.invokeSuper(obj, args);
		}

		private void updateValueOnInternalImpl(String methodName,
				Method method, Object[] args) {
			internalNodeImpl.setProperty(methodName.substring(3),
					(Serializable) args[0]);

		}

		private boolean isSetterMethod(String methodName, Method method) {
			if (methodName.startsWith("set")
					&& method.getParameterTypes().length == 1
					&& internalNodeImpl.hasProperty(methodName.substring(3))) {
				return true;
			}
			return false;
		}

	}

}
