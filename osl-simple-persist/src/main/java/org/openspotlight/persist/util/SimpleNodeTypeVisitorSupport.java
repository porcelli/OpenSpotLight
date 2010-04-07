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
package org.openspotlight.persist.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Reflection.UnwrappedCollectionTypeFromMethodReturn;
import org.openspotlight.common.util.Reflection.UnwrappedMapTypeFromMethodReturn;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * Class with static method to accept visitor of beans of type
 * {@link SimpleNodeType}. This
 * will ignore any property annotated with {@link ParentProperty} annotation.
 * This method isn't recursive. So it is stack overflow safe.
 * 
 * It will visit {@link SimpleNodeType types } inside collections, map values
 * and as a simple property. It won't visit any array, since arrays are not
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
	@SuppressWarnings("unchecked")
	public static <S extends SimpleNodeType> void acceptVisitorOn(
			final Class<S> targetType, final SimpleNodeType rootNode,
			final SimpleNodeTypeVisitor<S> visitor,
			final Class<? extends Annotation>... annotationsToIgnore) {
		try {
			if (targetType.isAssignableFrom(rootNode.getClass())) {
				visitor.visitBean((S) rootNode);
			}
			final List<S> allNodes = SimpleNodeTypeVisitorSupport.fillItems(
					targetType, rootNode, annotationsToIgnore);
			for (final S s : allNodes) {
				visitor.visitBean(s);
			}
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S extends SimpleNodeType> List<S> fillItems(
			final Class<S> targetType, final SimpleNodeType rootNode,final Class<? extends Annotation>... annotationsToIgnore)
			throws Exception {
		final Set<SimpleNodeType> allItemsToVisit = new LinkedHashSet<SimpleNodeType>();
		final Set<SimpleNodeType> currentItemsToVisit = SimpleNodeTypeVisitorSupport
		.fillItems(rootNode,annotationsToIgnore);
		int lastSize = 0;
		while (currentItemsToVisit.size() != 0) {
			allItemsToVisit.addAll(currentItemsToVisit);
			if (allItemsToVisit.size() == lastSize) {
				break;
				// just added the same items
			}

			final List<SimpleNodeType> copy = new ArrayList<SimpleNodeType>(
					currentItemsToVisit);
			currentItemsToVisit.clear();
			for (final SimpleNodeType node : copy) {
				final Set<SimpleNodeType> newNodes = SimpleNodeTypeVisitorSupport
				.fillItems(node,annotationsToIgnore);
				currentItemsToVisit.addAll(newNodes);
			}
			lastSize = allItemsToVisit.size();
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
	public static Set<SimpleNodeType> fillItems(final SimpleNodeType rootNode,
			final Class<? extends Annotation>... annotationsToIgnore)
			throws Exception {

		if (rootNode == null) {
			return Collections.emptySet();
		}
		final Set<SimpleNodeType> itemsToVisit = new LinkedHashSet<SimpleNodeType>();

		final PropertyDescriptor[] allDescriptors = PropertyUtils
		.getPropertyDescriptors(rootNode);
		looping: for (final PropertyDescriptor desc : allDescriptors) {
			final Method readMethod = desc.getReadMethod();
			if (readMethod.isAnnotationPresent(ParentProperty.class)) {
				continue looping;
			}
			for (final Class<? extends Annotation> annotation : annotationsToIgnore) {
				if (readMethod.isAnnotationPresent(annotation)) {
					continue looping;
				}
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
					if (collection != null) {
						for (final SimpleNodeType t : collection) {
							itemsToVisit.add(t);
						}
					}
				}
			} else if (Map.class.isAssignableFrom(currentType)) {
				final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection
				.unwrapMapFromMethodReturn(readMethod);
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType().getK2())) {
					final Map<Object, SimpleNodeType> map = (Map<Object, SimpleNodeType>) readMethod
					.invoke(rootNode);
					if (map != null) {
						for (final Entry<Object, SimpleNodeType> entry : map
								.entrySet()) {
							itemsToVisit.add(entry.getValue());
						}
					}
				}
			}
		}
		return itemsToVisit;
	}

}
