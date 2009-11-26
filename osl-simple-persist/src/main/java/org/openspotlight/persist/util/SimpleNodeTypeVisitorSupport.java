package org.openspotlight.persist.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Reflection.UnwrappedCollectionTypeFromMethodReturn;
import org.openspotlight.common.util.Reflection.UnwrappedMapTypeFromMethodReturn;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistSupport;

/**
 * Class with static method to accept visitor of beans of type
 * {@link SimpleNodeType}. This
 * {@link #acceptVisitorOn(Class, SimpleNodeType, SimpleNodeTypeVisitor)} method
 * will ignore any property annotated with {@link ParentProperty} annotation.
 * This method isn't recursive. So it is stack overflow safe.
 * 
 * It will visit {@link SimpleNodeType types } inside collections, map values
 * and as a simple property. It won't visit any array, since arrays are not
 * supported by {@link SimplePersistSupport}.
 * 
 * @author feu
 * 
 */
public class SimpleNodeTypeVisitorSupport {

	/**
	 * Accepts this visitor on rootNode.
	 * 
	 * @param <S>
	 * @param targetType
	 * @param rootNode
	 * @param visitor
	 */
	public static <S extends SimpleNodeType> void acceptVisitorOn(
			final Class<S> targetType, final SimpleNodeType rootNode,
			final SimpleNodeTypeVisitor<S> visitor) {
		try {
			final List<S> allNodes = SimpleNodeTypeVisitorSupport.fillItems(
					targetType, rootNode);
			for (final S s : allNodes) {
				visitor.visitBean(s);
			}
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S extends SimpleNodeType> List<S> fillItems(
			final Class<S> targetType, final SimpleNodeType rootNode)
			throws Exception {
		final List<SimpleNodeType> allItemsToVisit = new LinkedList<SimpleNodeType>();
		final List<SimpleNodeType> currentItemsToVisit = SimpleNodeTypeVisitorSupport
				.fillItems(rootNode);
		while (currentItemsToVisit.size() != 0) {
			allItemsToVisit.addAll(currentItemsToVisit);
			final List<SimpleNodeType> copy = new ArrayList<SimpleNodeType>(
					currentItemsToVisit);
			currentItemsToVisit.clear();
			for (final SimpleNodeType node : copy) {
				final List<SimpleNodeType> newNodes = SimpleNodeTypeVisitorSupport
						.fillItems(node);
				currentItemsToVisit.addAll(newNodes);
			}
		}
		final List<S> allNodesOfGivenType = new LinkedList<S>();
		for (final SimpleNodeType t : allItemsToVisit) {
			if (targetType.isInstance(t)) {
				allNodesOfGivenType.add((S) t);
			}
		}
		return allNodesOfGivenType;

	}

	@SuppressWarnings("unchecked")
	public static List<SimpleNodeType> fillItems(final SimpleNodeType rootNode)
			throws Exception {

		if (rootNode == null) {
			return Collections.emptyList();
		}
		final List<SimpleNodeType> itemsToVisit = new LinkedList<SimpleNodeType>();

		final PropertyDescriptor[] allDescriptors = PropertyUtils
				.getPropertyDescriptors(rootNode);
		for (final PropertyDescriptor desc : allDescriptors) {
			final Method readMethod = desc.getReadMethod();
			if (readMethod.isAnnotationPresent(ParentProperty.class)) {
				continue;
			}
			final Class<?> currentType = desc.getPropertyType();
			if (SimpleNodeType.class.isAssignableFrom(currentType)) {
				final SimpleNodeType bean = (SimpleNodeType) readMethod
						.invoke(rootNode);
				itemsToVisit.add(bean);
			} else if (Iterable.class.isAssignableFrom(currentType)) {
				final UnwrappedCollectionTypeFromMethodReturn<Object> metadata = Reflection
						.unwrapCollectionFromMethodReturn(readMethod);
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType())) {
					final Iterable<SimpleNodeType> collection = (Iterable<SimpleNodeType>) readMethod
							.invoke(rootNode);
					for (final SimpleNodeType t : collection) {
						itemsToVisit.add(t);
					}
				}
			} else if (Map.class.isAssignableFrom(currentType)) {
				final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection
						.unwrapMapFromMethodReturn(readMethod);
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType().getK2())) {
					final Map<Object, SimpleNodeType> map = (Map<Object, SimpleNodeType>) readMethod
							.invoke(rootNode);
					for (final Entry<Object, SimpleNodeType> entry : map
							.entrySet()) {
						itemsToVisit.add(entry.getValue());
					}
				}
			}
		}
		return itemsToVisit;
	}

}
