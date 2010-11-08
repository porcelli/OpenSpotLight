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

package org.openspotlight.persist.support;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.text.MessageFormat.format;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Reflection.unwrapCollectionFromMethodReturn;
import static org.openspotlight.common.util.Reflection.unwrapMapFromMethodReturn;
import static org.openspotlight.common.util.SLCollections.iterableToList;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.SerializationUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Wrapper;
import org.openspotlight.persist.annotation.IndexedProperty;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.PersistPropertyAsStream;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;
import org.openspotlight.persist.internal.LazyProperty;
import org.openspotlight.persist.internal.StreamPropertyWithParent;
import org.openspotlight.storage.Criteria.CriteriaBuilder;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.NodeFactory;
import org.openspotlight.storage.domain.StorageNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;

/**
 * Created by IntelliJ IDEA. User: feuteston Date: 05/04/2010 Time: 13:19:32 To change this template use File | Settings | File
 * Templates.
 */
@Singleton
public class SimplePersistImpl implements
        SimplePersistCapable<StorageNode, StorageSession> {

    private static final String  NODE_ENTRY_TYPE    = "internal-node-entry-type";
    private static final String  NODE_PROPERTY_NAME = "internal-node-proeprty-name";
    private static final String  SHA1_PROPERTY_NAME = "internal-{0}-sha1";

    private final StorageSession currentSession;

    private final Partition      currentPartition;

    public SimplePersistImpl(final StorageSession currentSession,
                             final Partition currentPartition) {
        this.currentSession = currentSession;
        this.currentPartition = currentPartition;
    }

    @Override
    public StorageSession getCurrentSession() {
        return currentSession;
    }

    @Override
    public Partition getCurrentPartition() {
        return currentPartition;
    }

    @Override
    public StorageSession.PartitionMethods getPartitionMethods() {
        return currentSession.withPartition(currentPartition);
    }

    @Override
    public <T> Iterable<StorageNode> convertBeansToNodes(
                                                         final StorageNode parent, final Iterable<T> beans) {
        try {
            final List<StorageNode> itemsConverted = newArrayList();
            for (final T bean: beans) {
                itemsConverted.add(convertBeanToNode(parent, bean));
            }
            return itemsConverted;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);

        }

    }

    @Override
    public <T> StorageNode convertBeanToNode(final StorageNode parent,
                                             final T bean) {
        try {
            return internalConvertBeanToNode(parent, bean);
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);

        }

    }

    private <T> StorageNode internalConvertBeanToNode(final StorageNode parent,
                                                      final T bean)
        throws Exception {
        final ConversionToNodeContext context = new ConversionToNodeContext(
                (SimpleNodeType) bean);
        internalConvertBeanToNode(context, null, null, parent);
        final StorageNode result = context.nodeReference.getWrapped();
        checkNotNull("result", result);
        return result;
    }

    private <T> StorageNode internalConvertBeanToNode(
                                                      final ConversionToNodeContext context, final String propertyName,
                                                      final SimpleNodeType bean, final StorageNode parentNode)
            throws Exception {
        final boolean firstInvocation = bean == null;
        if (firstInvocation) {
            StorageNode currentParentNode = parentNode;
            final Descriptors currentBeanDescriptors = Descriptors
                    .fillDescriptors(context.bean);
            final List<Pair<String, SimpleNodeType>> rootObjects = currentBeanDescriptors
                    .getRootObjects();
            for (final Pair<String, SimpleNodeType> pair: rootObjects) {
                currentParentNode = internalConvertBeanToNode(context,
                        pair.getK1(), pair.getK2(), currentParentNode);
            }
            context.allNodes.removeAll(context.nodesConverted.values());
            for (final StorageNode unusedNode: context.allNodes) {
                currentSession.removeNode(unusedNode);
            }
            return context.nodeReference.getWrapped();
        } else {
            StorageNode cached = context.nodesConverted.get(bean);
            if (cached == null) {
                final Descriptors parentDescriptors = Descriptors
                        .fillDescriptors(bean);
                cached = createNewNode(context, parentNode, parentDescriptors,
                        propertyName);
                context.nodesConverted.put(bean, cached);
                fillNodeSimpleProperties(bean,
                        parentDescriptors.simplePropertiesDescriptor, cached);
                fillNodeStreamProperties(bean,
                        parentDescriptors.streamPropertiesDescriptor, cached);
                fillNodeChildrenProperties(context, bean,
                        parentDescriptors.childrenPropertiesDescriptor, cached);
                fillNodeLazyProperties(context, bean,
                        parentDescriptors.lazyPropertiesDescriptor, cached);
                if (bean == context.bean) {
                    context.nodeReference.setWrapped(cached);
                }
            }
            return cached;
        }

    }

    private static <T extends Serializable> InputStream asStream(final T o)
            throws Exception {
        if (o == null) { return null; }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream ois = new ObjectOutputStream(baos);
        ois.writeObject(o);
        ois.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    private static <T extends Serializable> T asObject(final InputStream is)
            throws Exception {
        if (is == null) { return null; }
        final ObjectInputStream ois = new ObjectInputStream(is);
        final T result = (T) ois.readObject();
        return result;
    }

    private void fillNodeLazyProperties(final ConversionToNodeContext context,
                                        final SimpleNodeType bean,
                                        final List<PropertyDescriptor> lazyPropertiesDescriptor,
                                        final StorageNode nodeEntry)
        throws Exception {
        for (final PropertyDescriptor descriptor: lazyPropertiesDescriptor) {
            final LazyProperty<?> property = (LazyProperty<?>) descriptor
                    .getReadMethod().invoke(bean);
            if (property != null && property.getMetadata().needsSave()) {
                final String propertyName = descriptor.getName();
                final Object value = property.getMetadata().getTransient();
                if (value instanceof InputStream) {
                    nodeEntry.setSimpleProperty(currentSession, propertyName,
                            (InputStream) value);
                } else if (value instanceof Set) {
                    nodeEntry
                            .setSimpleProperty(
                                    currentSession,
                                    propertyName,
                                    asStream(beforeSerializeSet(
                                            (Set<? extends Serializable>) value,
                                            null)));
                } else if (value instanceof List) {
                    nodeEntry
                            .setSimpleProperty(
                                    currentSession,
                                    propertyName,
                                    asStream(beforeSerializeList(
                                            (List<? extends Serializable>) value,
                                            null)));
                } else if (value instanceof Map) {
                    nodeEntry
                            .setSimpleProperty(
                                    currentSession,
                                    propertyName,
                                    asStream(beforeSerializeMap(
                                            (Map<? extends Serializable, ? extends Serializable>) value,
                                            null)));

                } else {// Serializable
                    nodeEntry
                            .setSimpleProperty(
                                    currentSession,
                                    propertyName,
                                    asStream(beforeSerializeSerializable((Serializable) value)));

                }
                nodeEntry.setSimpleProperty(currentSession,
                        format(SHA1_PROPERTY_NAME, propertyName), property
                                .getMetadata().getSha1());
                property.getMetadata().markAsSaved();
            }
            ;
        }

    }

    private <T> void fillNodeChildrenProperties(
                                                final ConversionToNodeContext context, final T bean,
                                                final List<PropertyDescriptor> childrenPropertiesDescriptor,
                                                final StorageNode newNodeEntry)
        throws Exception {

        final Map<String, BeanToNodeChildData> nodesToConvert = newHashMap();
        for (final PropertyDescriptor property: childrenPropertiesDescriptor) {
            final String propertyName = property.getName();
            BeanToNodeChildData data = nodesToConvert.get(propertyName);
            final Class<?> propertyType = property.getPropertyType();

            final Object value = property.getReadMethod().invoke(bean);
            if (SimpleNodeType.class.isAssignableFrom(propertyType)) {
                if (data == null) {
                    data = new BeanToNodeChildData(propertyName,
                            Collection.class.isAssignableFrom(propertyType),
                            propertyType);
                    nodesToConvert.put(propertyName, data);
                }
                data.childrenToSave.add((SimpleNodeType) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation =
                    unwrapCollectionFromMethodReturn(property
                        .getReadMethod());
                if (data == null) {
                    data = new BeanToNodeChildData(propertyName,
                            Collection.class.isAssignableFrom(propertyType),
                            methodInformation.getItemType());
                    nodesToConvert.put(propertyName, data);
                }
                if (List.class.isAssignableFrom(methodInformation
                        .getCollectionType())) {
                    for (final SimpleNodeType t: (List<SimpleNodeType>) value) {
                        data.childrenToSave.add(t);
                    }
                } else if (Set.class.isAssignableFrom(methodInformation
                        .getCollectionType())) {
                    for (final SimpleNodeType t: (Set<SimpleNodeType>) value) {
                        data.childrenToSave.add(t);
                    }
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else {
                throw new IllegalStateException("invalid type:"
                        + property.getPropertyType());
            }
        }
        for (final BeanToNodeChildData data: nodesToConvert.values()) {
            if (!data.multiple && data.childrenToSave.size() > 1) { throw new IllegalStateException(
                        "single property with more than one child"); }
            for (final SimpleNodeType beanBeenSaved: data.childrenToSave) {
                internalConvertBeanToNode(context, data.propertyName,
                        beanBeenSaved, newNodeEntry);
            }
            context.allNodes.addAll(iterableToList(newNodeEntry
                    .getChildrenByType(currentPartition, currentSession,
                            internalGetNodeName(data.nodeType))));
        }

    }

    private <T> void fillNodeStreamProperties(final T bean,
                                              final List<PropertyDescriptor> streamPropertiesDescriptor,
                                              final StorageNode newNodeEntry)
        throws Exception {
        for (final PropertyDescriptor property: streamPropertiesDescriptor) {
            final Class<?> propertyType = property.getPropertyType();
            final Method readMethod = property.getReadMethod();
            final Object value = readMethod.invoke(bean);
            if (InputStream.class.isAssignableFrom(propertyType)) {
                newNodeEntry.setSimpleProperty(currentSession,
                        property.getName(), (InputStream) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation =
                    unwrapCollectionFromMethodReturn(property
                        .getReadMethod());
                if (List.class.isAssignableFrom(methodInformation
                        .getCollectionType())) {
                    newNodeEntry.setSimpleProperty(
                            currentSession,
                            property.getName(),
                            asStream(beforeSerializeList(
                                    (List<? extends Serializable>) value,
                                    readMethod)));
                } else if (Set.class.isAssignableFrom(methodInformation
                        .getCollectionType())) {
                    newNodeEntry.setSimpleProperty(
                            currentSession,
                            property.getName(),
                            asStream(beforeSerializeSet(
                                    (Set<? extends Serializable>) value,
                                    readMethod)));
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else if (Map.class.isAssignableFrom(propertyType)) {
                newNodeEntry
                        .setSimpleProperty(
                                currentSession,
                                property.getName(),
                                asStream(beforeSerializeMap(
                                        (Map<? extends Serializable, ? extends Serializable>) value,
                                        readMethod)));

            } else if (Serializable.class.isAssignableFrom(propertyType)) {
                if (propertyType.equals(String.class)
                        || Number.class.isAssignableFrom(propertyType)
                        || propertyType.isPrimitive()
                        || Boolean.class.equals(propertyType)
                        || Character.class.equals(propertyType)
                        || Date.class.equals(propertyType)) {
                    newNodeEntry.setSimpleProperty(currentSession,
                            property.getName(),
                            Conversion.convert(value, String.class));
                } else {
                    newNodeEntry
                            .setSimpleProperty(
                                    currentSession,
                                    property.getName(),
                                    asStream(beforeSerializeSerializable((Serializable) value)));
                }

            } else {
                throw new IllegalStateException("invalid type");
            }

        }
    }

    private <T> void fillNodeSimpleProperties(final T bean,
                                              final List<PropertyDescriptor> simplePropertiesDescriptor,
                                              final StorageNode newNodeEntry)
        throws Exception {
        for (final PropertyDescriptor property: simplePropertiesDescriptor) {
            if (property.getReadMethod().isAnnotationPresent(
                    IndexedProperty.class)) {
                newNodeEntry.setIndexedProperty(currentSession, property
                        .getName(), Conversion.convert(property.getReadMethod()
                        .invoke(bean), String.class));
            } else {
                newNodeEntry.setSimpleProperty(currentSession, property
                        .getName(), Conversion.convert(property.getReadMethod()
                        .invoke(bean), String.class));
            }
        }
    }

    private <T> StorageNode createNewNode(
                                          final ConversionToNodeContext context,
                                          final StorageNode parentNode, final Descriptors descriptors,
                                          final String propertyName)
        throws Exception {
        final String name = internalGetNodeName(descriptors.bean);
        final NodeFactory.NodeBuilder builder = currentSession.withPartition(
                currentPartition).createWithType(name);

        if (parentNode != null) {
            builder.withParent(parentNode);
        }
        for (final PropertyDescriptor descriptor: descriptors.keyPropertiesDescriptor) {
            builder.withSimpleKey(descriptor.getName(), Conversion.convert(
                    descriptor.getReadMethod().invoke(descriptors.bean),
                    String.class));
        }
        if (propertyName != null) {
            builder.withSimpleKey(NODE_PROPERTY_NAME, propertyName);
        }
        final StorageNode newNode = builder.andCreate();
        newNode.setIndexedProperty(currentSession, NODE_ENTRY_TYPE,
                internalGetNodeType(descriptors.bean));
        return newNode;
    }

    @Override
    public <T> Iterable<T> convertNodesToBeans(final Iterable<StorageNode> nodes) {
        try {
            return internalConvertNodesToBeans(nodes);
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);

        }

    }

    private <T> Iterable<T> internalConvertNodesToBeans(
                                                        final Iterable<StorageNode> nodes)
        throws Exception {
        final IteratorBuilder.SimpleIteratorBuilder<T, StorageNode> b = IteratorBuilder
                .createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<T, StorageNode>() {
            @Override
            public T convert(final StorageNode nodeEntry)
                throws Exception {
                return (T) convertNodeToBean(nodeEntry);
            }
        });
        final Iterable<T> result = b.withItems(nodes).andBuild();
        return result;

    }

    private String internalGetNodeType(final StorageNode node) {
        return node.getType();
    }

    private <T> String internalGetNodeName(final T bean) {
        return this.<T>internalGetNodeName((Class<T>) bean.getClass());
    }

    private <T> String internalGetNodeType(final T bean) {
        return this.<T>internalGetNodeType((Class<T>) bean.getClass());
    }

    private <T> String internalGetNodeName(final Class<T> beanType) {
        final Name annotation = beanType.getAnnotation(Name.class);
        return annotation != null ? annotation.value() : beanType.getName();
    }

    private <T> String internalGetNodeType(final Class<T> beanType) {
        return beanType.getName();
    }

    @Override
    public <T> T convertNodeToBean(final StorageNode node)
        throws Exception {
        final T bean = (T) internalConvertNodeToBean(
                new ConversionToBeanContext(node), null, null);
        return bean;
    }

    private Object internalConvertNodeToBean(
                                             final ConversionToBeanContext context, final StorageNode nodeEntry,
                                             final Object beanParent)
        throws Exception {

        if (nodeEntry == null) {
            final List<StorageNode> parents = newLinkedList();
            StorageNode currentParent = context.node;
            while (currentParent != null && isSimpleNode(currentParent)) {
                parents.add(currentParent);
                currentParent = currentParent.getParent(currentSession);
            }
            reverse(parents);
            Object currentParentAsBean = null;
            for (final StorageNode parentEntry: parents) {
                currentParentAsBean = internalConvertNodeToBean(context,
                        parentEntry, currentParentAsBean);
            }
            return context.beanReference.getWrapped();
        } else {
            Object cached = context.beansConverted.get(nodeEntry);
            if (cached == null) {
                final Class<?> beanType = findClassFromNode(nodeEntry);
                cached = beanType.newInstance();
                context.beansConverted.put(nodeEntry, cached);
                final Descriptors descriptors = Descriptors
                        .fillDescriptors(beanType);
                fillBeanParent(descriptors.parentPropertiesDescriptor, cached,
                        beanParent);
                fillBeanSimpleProperties(nodeEntry, cached,
                        descriptors.keyPropertiesDescriptor);
                fillBeanSimpleProperties(nodeEntry, cached,
                        descriptors.simplePropertiesDescriptor);
                fillBeanStreamProperties(nodeEntry, cached,
                        descriptors.streamPropertiesDescriptor);
                fillBeanLazyProperties(nodeEntry, cached,
                        descriptors.lazyPropertiesDescriptor);
                fillBeanChildren(context, nodeEntry, cached,
                        descriptors.childrenPropertiesDescriptor);
                if (nodeEntry.equals(context.node)) {
                    context.beanReference.setWrapped((SimpleNodeType) cached);
                }
            }
            return cached;
        }

    }

    private boolean isSimpleNode(final StorageNode currentParent) {
        return currentParent.getPropertyValueAsString(currentSession,
                NODE_ENTRY_TYPE) != null;
    }

    private Class<?> findClassFromNode(final StorageNode nodeEntry)
            throws Exception {
        return forName(nodeEntry.getPropertyValueAsString(currentSession,
                NODE_ENTRY_TYPE));
    }

    private void fillBeanLazyProperties(final StorageNode cached,
                                        final Object bean,
                                        final List<PropertyDescriptor> lazyPropertiesDescriptors)
            throws Exception {
        for (final PropertyDescriptor property: lazyPropertiesDescriptors) {

            final String propertyName = property.getName();
            final LazyProperty<?> lazyProperty = (LazyProperty<?>) property
                    .getReadMethod().invoke(bean);
            lazyProperty.getMetadata().setParentKey(cached.getKey());
            lazyProperty.getMetadata().setSavedNode(cached);
            lazyProperty.getMetadata().setPropertyName(propertyName);
            final String sha1 = cached.getPropertyValueAsString(currentSession,
                    format(SHA1_PROPERTY_NAME, propertyName));
            lazyProperty.getMetadata().internalSetSha1(sha1);
        }

    }

    private <T> void fillBeanChildren(final ConversionToBeanContext context,
                                      final StorageNode node, final T bean,
                                      final List<PropertyDescriptor> childrenPropertiesDescriptor)
            throws Exception {
        for (final PropertyDescriptor descriptor: childrenPropertiesDescriptor) {
            final Class<?> propertyType = descriptor.getPropertyType();
            Class<?> nodeType = null;
            final boolean isMultiple = Collection.class
                    .isAssignableFrom(propertyType);
            final Method readMethod = descriptor.getReadMethod();
            if (isMultiple) {
                final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription =
                    unwrapCollectionFromMethodReturn(readMethod);
                if (List.class.isAssignableFrom(propertyType)) {
                    descriptor.getWriteMethod().invoke(bean, newLinkedList());
                } else if (Set.class.isAssignableFrom(propertyType)) {
                    descriptor.getWriteMethod().invoke(bean, newHashSet());
                } else {
                    throw new IllegalStateException("wrong child type");
                }
                nodeType = methodDescription.getItemType();
            } else if (SimpleNodeType.class.isAssignableFrom(propertyType)) {
                nodeType = propertyType;
            } else {
                throw new IllegalStateException("wrong child type");
            }

            if (!SimpleNodeType.class.isAssignableFrom(nodeType)) { throw new IllegalStateException("wrong child type"); }
            final String childrenName = internalGetNodeName(nodeType);
            Iterable<StorageNode> children = iterableToList(node
                    .getChildrenByType(currentPartition, currentSession,
                            childrenName));
            children = filterChildrenWithProperty(children,
                    descriptor.getName());
            final List<Object> childrenAsBeans = newLinkedList();
            for (final StorageNode child: children) {
                childrenAsBeans.add(internalConvertNodeToBean(context, child,
                        bean));
            }
            if (isMultiple) {
                final Collection c = (Collection) readMethod.invoke(bean);
                for (final Object o: childrenAsBeans) {
                    c.add(o);
                }
                if (Comparable.class.isAssignableFrom(nodeType)
                        && c instanceof List) {
                    sort((List) c);
                }
            } else if (childrenAsBeans.size() > 0) {
                final Object value = childrenAsBeans.iterator().next();
                descriptor.getWriteMethod().invoke(bean, value);
            }
        }
    }

    private Iterable<StorageNode> filterChildrenWithProperty(
                                                             final Iterable<StorageNode> children, final String name) {
        if (name == null) { return children; }
        final List<StorageNode> filtered = newLinkedList();
        for (final StorageNode e: children) {
            final String propertyValue = e.getPropertyValueAsString(currentSession,
                    NODE_PROPERTY_NAME);
            if (name.equals(propertyValue)) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    private <T> void fillBeanStreamProperties(final StorageNode node,
                                              final T bean,
                                              final List<PropertyDescriptor> streamPropertiesDescriptor)
            throws Exception {
        for (final PropertyDescriptor descriptor: streamPropertiesDescriptor) {
            final Class<?> propertyType = descriptor.getPropertyType();
            if (InputStream.class.isAssignableFrom(propertyType)) {
                final InputStream value = node.getPropertyValueAsStream(
                        currentSession, descriptor.getName());
                descriptor.getWriteMethod().invoke(bean, value);
            } else if (Serializable.class.isAssignableFrom(propertyType)
                    || Collection.class.isAssignableFrom(propertyType)
                    || Map.class.isAssignableFrom(propertyType)) {
                final Serializable value = asObject(node.getPropertyValueAsStream(
                        currentSession, descriptor.getName()));
                descriptor.getWriteMethod().invoke(
                        bean,
                        getInternalMethods().beforeUnConvert(
                                (SimpleNodeType) bean, value,
                                descriptor.getReadMethod()));
            } else {
                throw new IllegalStateException("wrong type");
            }
        }
    }

    private <T> void fillBeanSimpleProperties(final StorageNode node,
                                              final T bean,
                                              final List<PropertyDescriptor> simplePropertiesDescriptor)
            throws Exception {
        for (final PropertyDescriptor descriptor: simplePropertiesDescriptor) {
            final String value = node.getPropertyValueAsString(currentSession,
                    descriptor.getName());
            if (value == null && descriptor.getPropertyType().isPrimitive()) {
                continue;
            }
            descriptor.getWriteMethod().invoke(bean,
                    Conversion.convert(value, descriptor.getPropertyType()));
        }
    }

    private void fillBeanParent(
                                final List<PropertyDescriptor> parentPropertyDescriptors,
                                final Object bean, final Object beanParent)
        throws Exception {
        if (beanParent != null) {
            final Class<?> parentType = beanParent.getClass();
            for (final PropertyDescriptor descriptor: parentPropertyDescriptors) {
                if (descriptor.getPropertyType().isAssignableFrom(parentType)) {
                    descriptor.getWriteMethod().invoke(bean, beanParent);
                    break;
                }
            }
        }
    }

    private final InternalMethods internalMethods = new InternalMethodsImpl();

    @Override
    public InternalMethods getInternalMethods() {
        return internalMethods;

    }

    private class InternalMethodsImpl implements InternalMethods {
        @Override
        public Object beforeUnConvert(final SimpleNodeType bean,
                                      final Serializable value, final Method readMethod) {
            try {
                if (value instanceof Collection) {
                    boolean mayBeStreamPropertyWithParent = true;
                    if (readMethod != null) {
                        final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription =
                            unwrapCollectionFromMethodReturn(readMethod);
                        mayBeStreamPropertyWithParent = StreamPropertyWithParent.class
                                .isAssignableFrom(methodDescription
                                        .getItemType());
                    }
                    if (mayBeStreamPropertyWithParent) {
                        final Collection valueAsCollection = (Collection) value;
                        for (final Object o: valueAsCollection) {
                            if (o instanceof StreamPropertyWithParent) {
                                ((StreamPropertyWithParent) o).setParent(bean);
                            }
                        }
                    }
                } else if (value instanceof Map) {
                    boolean mayBeStreamPropertyWithParent = true;
                    if (readMethod != null) {
                        final Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodDescription =
                            unwrapMapFromMethodReturn(readMethod);
                        mayBeStreamPropertyWithParent = StreamPropertyWithParent.class
                                .isAssignableFrom(methodDescription
                                        .getItemType().getK2());

                    }

                    if (mayBeStreamPropertyWithParent) {
                        final Map<?, ?> valueAsMap = (Map<?, ?>) value;
                        for (final Map.Entry<?, ?> entry: valueAsMap
                                .entrySet()) {
                            if (entry.getValue() instanceof StreamPropertyWithParent) {
                                ((StreamPropertyWithParent) entry.getValue())
                                        .setParent(bean);
                            }
                        }
                    }
                } else if (value instanceof StreamPropertyWithParent) {
                    ((StreamPropertyWithParent) value).setParent(bean);
                }
                return value;
            } catch (final Exception e) {
                throw logAndReturnNew(e, SLRuntimeException.class);

            }

        }

        @Override
        public String getNodeName(final Class<?> nodeType) {
            return internalGetNodeName(nodeType);
        }

    }

    private Serializable beforeSerializeSet(
                                            final Set<? extends Serializable> value, final Method readMethod)
            throws Exception {
        if (readMethod != null) {
            final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription =
                unwrapCollectionFromMethodReturn(readMethod);

            if (StreamPropertyWithParent.class
                    .isAssignableFrom(methodDescription.getItemType())) {
                final HashSet<Serializable> newCollection = newHashSet();
                for (final Serializable o: value) {
                    newCollection.add(beforeSerializeSerializable(o));
                }
                return newCollection;
            }
            return (Serializable) value;
        } else {
            final HashSet<Serializable> newCollection = newHashSet();
            for (final Serializable o: value) {
                newCollection.add(beforeSerializeSerializable(o));
            }
            return newCollection;
        }

    }

    private Serializable beforeSerializeList(
                                             final List<? extends Serializable> value, final Method readMethod)
            throws Exception {
        if (readMethod != null) {

            final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription =
                unwrapCollectionFromMethodReturn(readMethod);

            if (StreamPropertyWithParent.class
                    .isAssignableFrom(methodDescription.getItemType())) {
                final LinkedList<Serializable> newCollection = newLinkedList();
                for (final Serializable o: value) {
                    newCollection.add(beforeSerializeSerializable(o));
                }
                return newCollection;
            }
            return (Serializable) value;
        } else {
            final LinkedList<Serializable> newCollection = newLinkedList();
            for (final Serializable o: value) {
                newCollection.add(beforeSerializeSerializable(o));
            }
            return newCollection;
        }

    }

    private Serializable beforeSerializeMap(
                                            final Map<? extends Serializable, ? extends Serializable> value,
                                            final Method readMethod)
        throws Exception {
        if (readMethod != null) {
            final Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodDescription =
                unwrapMapFromMethodReturn(readMethod);

            if (StreamPropertyWithParent.class
                    .isAssignableFrom(methodDescription.getItemType().getK2())) {
                final HashMap<Serializable, Serializable> newMap = newHashMap();
                for (final Map.Entry<? extends Serializable, ? extends Serializable> entry: value
                        .entrySet()) {
                    newMap.put(entry.getKey(),
                            beforeSerializeSerializable(entry.getValue()));
                }
                return newMap;
            }
            return (Serializable) value;
        } else {
            final HashMap<Serializable, Serializable> newMap = newHashMap();
            for (final Map.Entry<? extends Serializable, ? extends Serializable> entry: value
                    .entrySet()) {
                newMap.put(entry.getKey(),
                        beforeSerializeSerializable(entry.getValue()));
            }
            return newMap;

        }
    }

    private Serializable beforeSerializeSerializable(final Serializable value) {
        if (value instanceof StreamPropertyWithParent) {
            final StreamPropertyWithParent typedValue = (StreamPropertyWithParent) value;

            final SimpleNodeType oldParent = typedValue.getParent();
            typedValue.setParent(null);
            final StreamPropertyWithParent newValue = (StreamPropertyWithParent) SerializationUtils
                    .clone(value);
            typedValue.setParent(oldParent);
            return newValue;
        }
        return value;
    }

    @Override
    public <T> Iterable<T> findByProperties(final StorageNode parent,
                                            final Class<T> beanType, final String[] propertyNames,
                                            final Object[] propertyValues) {
        try {
            checkNotNull("currentPartition", currentPartition);
            checkNotNull("currentSession", currentSession);
            checkNotNull("beanType", beanType);
            checkNotNull("propertyNames", propertyNames);
            checkNotNull("propertyValues", propertyValues);
            checkCondition("namesAndValues:sameSize",
                    propertyNames.length == propertyValues.length);

            final CriteriaBuilder builder = currentSession
                    .withPartition(currentPartition).createCriteria()
                    .withNodeEntry(internalGetNodeName(beanType));
            final Map<String, PropertyDescriptor> allDescriptors = createMapWith(PropertyUtils
                    .getPropertyDescriptors(beanType));

            for (int i = 0, size = propertyNames.length; i < size; i++) {

                final PropertyDescriptor descriptor = allDescriptors
                        .get(propertyNames[i]);
                if (descriptor == null) { throw new SLRuntimeException("invalid property:"
                            + propertyNames[i]); }
                builder.withProperty(propertyNames[i]).equalsTo(
                        Conversion.convert(propertyValues[i], String.class));
            }
            final Iterable<StorageNode> foundItems = builder.buildCriteria()
                    .andFind(currentSession);

            final IteratorBuilder.SimpleIteratorBuilder<T, StorageNode> b = IteratorBuilder
                    .<T, StorageNode>createIteratorBuilder();
            b.withConverter(new IteratorBuilder.Converter<T, StorageNode>() {
                @Override
                public T convert(final StorageNode nodeEntry)
                    throws Exception {
                    return (T) convertNodeToBean(nodeEntry);
                }
            });
            b.withReferee(new IteratorBuilder.NextItemReferee<StorageNode>() {
                @Override
                public boolean canAcceptAsNewItem(final StorageNode nodeEntry) {
                    final String typeAsString = nodeEntry.getPropertyValueAsString(
                            currentSession, NODE_ENTRY_TYPE);
                    if (typeAsString != null
                            && typeAsString.equals(beanType.getName())) {
                        if (parent != null) {
                            StorageNode parentNode = nodeEntry;
                            while (parentNode != null) {
                                if (parentNode.getKey().equals(parent.getKey())) {
                                    return true;
                                }
                                parentNode = parentNode
                                        .getParent(currentSession);
                            }
                            return false;
                        }
                        return true;
                    }
                    return false;
                }
            });
            final Iterable<T> result = b.withItems(foundItems).andBuild();
            return result;

        } catch (final Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);

        }

    }

    private Map<String, PropertyDescriptor> createMapWith(
                                                          final PropertyDescriptor[] propertyDescriptors) {
        final ImmutableMap.Builder<String, PropertyDescriptor> builder = ImmutableMap
                .<String, PropertyDescriptor>builder();
        for (final PropertyDescriptor d: propertyDescriptors) {
            builder.put(d.getName(), d);

        }
        return builder.build();
    }

    @Override
    public <T> T findUniqueByProperties(final StorageNode parent,
                                        final Class<T> beanType, final String[] propertyNames,
                                        final Object[] propertyValues) {
        final Iterable<T> result = findByProperties(parent, beanType,
                propertyNames, propertyValues);
        final Iterator<T> it = result.iterator();
        final T resultAsBean = it.hasNext() ? it.next() : null;
        return resultAsBean;
    }

    @Override
    public <T> Iterable<StorageNode> convertBeansToNodes(final Iterable<T> beans) {
        return convertBeansToNodes(null, beans);
    }

    @Override
    public <T> StorageNode convertBeanToNode(final T bean)
        throws Exception {
        return convertBeanToNode(null, bean);
    }

    @Override
    public <T> Iterable<T> findByProperties(final Class<T> beanType,
                                            final String[] propertyNames, final Object[] propertyValues) {
        return findByProperties(null, beanType, propertyNames, propertyValues);
    }

    private static final String[] EMPTY_NAMES  = new String[] {};

    private static final Object[] EMPTY_VALUES = new Object[] {};

    @Override
    public <T> Iterable<T> findAll(final Class<T> beanType) {
        return findByProperties(beanType, EMPTY_NAMES, EMPTY_VALUES);
    }

    @Override
    public <T> Iterable<T> findAll(final StorageNode parentNode,
                                   final Class<T> beanType) {
        return findByProperties(parentNode, beanType, EMPTY_NAMES, EMPTY_VALUES);
    }

    @Override
    public <T> T findUnique(final Class<T> beanType) {
        return findUniqueByProperties(beanType, EMPTY_NAMES, EMPTY_VALUES);
    }

    @Override
    public <T> T findUnique(final StorageNode parentNode,
                            final Class<T> beanType) {
        return findUniqueByProperties(parentNode, beanType, EMPTY_NAMES,
                EMPTY_VALUES);
    }

    @Override
    public <T> T findUniqueByProperties(final Class<T> beanType,
                                        final String[] propertyNames, final Object[] propertyValues) {
        return findUniqueByProperties(null, beanType, propertyNames,
                propertyValues);
    }

    private static class Descriptors {
        private Descriptors(final Class<?> beanType, final SimpleNodeType bean,
                            final Wrapper<PropertyDescriptor> parentPropertyDescriptor,
                            final List<PropertyDescriptor> simplePropertiesDescriptor,
                            final List<PropertyDescriptor> keyPropertiesDescriptor,
                            final List<PropertyDescriptor> streamPropertiesDescriptor,
                            final List<PropertyDescriptor> childrenPropertiesDescriptor,
                            final List<PropertyDescriptor> parentPropertiesDescriptor,
                            final List<PropertyDescriptor> lazyPropertiesDescriptor) {
            this.beanType = beanType;
            this.bean = bean;
            this.parentPropertyDescriptor = parentPropertyDescriptor;
            this.simplePropertiesDescriptor = simplePropertiesDescriptor;
            this.keyPropertiesDescriptor = keyPropertiesDescriptor;
            this.streamPropertiesDescriptor = streamPropertiesDescriptor;
            this.childrenPropertiesDescriptor = childrenPropertiesDescriptor;
            this.parentPropertiesDescriptor = parentPropertiesDescriptor;
            this.lazyPropertiesDescriptor = lazyPropertiesDescriptor;
        }

        private List<Pair<String, SimpleNodeType>> rootObjects = null;
        final Class<?>                             beanType;
        final SimpleNodeType                       bean;
        final Wrapper<PropertyDescriptor>          parentPropertyDescriptor;
        final List<PropertyDescriptor>             simplePropertiesDescriptor;
        final List<PropertyDescriptor>             keyPropertiesDescriptor;
        final List<PropertyDescriptor>             streamPropertiesDescriptor;
        final List<PropertyDescriptor>             childrenPropertiesDescriptor;
        final List<PropertyDescriptor>             parentPropertiesDescriptor;
        final List<PropertyDescriptor>             lazyPropertiesDescriptor;

        public static Descriptors createMutable(final Class<?> beanType,
                                                final Object bean) {
            return new Descriptors(beanType, (SimpleNodeType) bean,
                    Wrapper.<PropertyDescriptor>createMutable(),
                    Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList());
        }

        public static Descriptors createImmutableFrom(final Descriptors from) {
            return new Descriptors(
                    from.beanType,
                    from.bean,
                    Wrapper.<PropertyDescriptor>createImmutable(from.parentPropertyDescriptor
                            .getWrapped()),
                    ImmutableList.copyOf(from.simplePropertiesDescriptor),
                    ImmutableList.copyOf(from.keyPropertiesDescriptor),
                    ImmutableList.copyOf(from.streamPropertiesDescriptor),
                    ImmutableList.copyOf(from.childrenPropertiesDescriptor),
                    ImmutableList.copyOf(from.parentPropertiesDescriptor),
                    ImmutableList.copyOf(from.lazyPropertiesDescriptor));
        }

        static <T extends SimpleNodeType> Descriptors fillDescriptors(
                                                                      final Object bean)
            throws Exception {
            return fillDescriptors(bean, bean.getClass());

        }

        static <T extends SimpleNodeType> Descriptors fillDescriptors(
                                                                      final Class<?> beanType)
            throws Exception {
            return fillDescriptors(null, beanType);
        }

        static <T extends SimpleNodeType> Descriptors fillDescriptors(
                                                                      final Object bean, final Class<?> beanType)
            throws Exception {
            final Descriptors descriptors = Descriptors.createMutable(beanType,
                    bean);
            for (final PropertyDescriptor descriptor: getPropertyDescriptors(beanType)) {
                if (descriptor.getName().equals("class")) {
                    continue;// Object#getClass
                }
                final Method readMethod = descriptor.getReadMethod();
                if (readMethod.isAnnotationPresent(TransientProperty.class)) {
                    continue;
                }
                final Class<?> returnType = readMethod.getReturnType();
                if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                    final Object value = bean != null ? readMethod.invoke(bean)
                            : null;
                    if (value != null
                            && descriptors.parentPropertyDescriptor != null
                            && descriptors.parentPropertyDescriptor
                                    .getWrapped() != null) { throw new IllegalStateException(
                                "only one parent property is allowed"); }
                    if (value != null) {
                        descriptors.parentPropertyDescriptor
                                .setWrapped(descriptor);
                    }
                    descriptors.parentPropertiesDescriptor.add(descriptor);
                } else if (readMethod.isAnnotationPresent(KeyProperty.class)) {
                    descriptors.keyPropertiesDescriptor.add(descriptor);
                } else if (readMethod
                        .isAnnotationPresent(PersistPropertyAsStream.class)) {
                    descriptors.streamPropertiesDescriptor.add(descriptor);
                } else if (returnType.isAssignableFrom(InputStream.class)) {
                    descriptors.streamPropertiesDescriptor.add(descriptor);
                } else if (SimpleNodeType.class.isAssignableFrom(returnType)) {
                    descriptors.childrenPropertiesDescriptor.add(descriptor);
                } else if (Collection.class.isAssignableFrom(returnType)) {
                    final Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation =
                        unwrapCollectionFromMethodReturn(readMethod);
                    if (SimpleNodeType.class.isAssignableFrom(methodInformation
                            .getItemType())) {
                        descriptors.childrenPropertiesDescriptor
                                .add(descriptor);
                    } else {
                        descriptors.streamPropertiesDescriptor.add(descriptor);
                    }
                } else if (Map.class.isAssignableFrom(returnType)) {
                    final Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation =
                        unwrapMapFromMethodReturn(readMethod);
                    if (SimpleNodeType.class.isAssignableFrom(methodInformation
                            .getItemType().getK2())) {
                        descriptors.childrenPropertiesDescriptor
                                .add(descriptor);
                    } else {
                        descriptors.streamPropertiesDescriptor.add(descriptor);
                    }
                } else if (LazyProperty.class.isAssignableFrom(returnType)) {
                    descriptors.lazyPropertiesDescriptor.add(descriptor);
                } else {
                    descriptors.simplePropertiesDescriptor.add(descriptor);
                }
            }
            return Descriptors.createImmutableFrom(descriptors);
        }

        public List<Pair<String, SimpleNodeType>> getRootObjects()
                throws Exception {
            if (rootObjects == null) {
                rootObjects = loadRootObjects();
            }
            return rootObjects;
        }

        private List<Pair<String, SimpleNodeType>> loadRootObjects()
                throws Exception {
            final List<Pair<String, SimpleNodeType>> resultInReverseOrder = newLinkedList();
            Descriptors currentDescriptors = this;
            Object parent = bean;
            Object oldParent = null;

            do {
                final PropertyDescriptor descriptor = currentDescriptors.parentPropertyDescriptor
                        .getWrapped();
                oldParent = parent;
                final Class<?> oldType = oldParent != null ? oldParent
                        .getClass() : null;
                parent = descriptor != null ? descriptor.getReadMethod()
                        .invoke(parent) : null;
                currentDescriptors = parent != null ? Descriptors
                        .fillDescriptors(parent) : null;
                String oldParentName = null;
                if (oldParent != null && currentDescriptors != null) {
                    lookingForNames: for (final PropertyDescriptor childDescriptor: currentDescriptors.childrenPropertiesDescriptor) {
                        if (childDescriptor.getPropertyType().equals(oldType)) {
                            oldParentName = childDescriptor.getName();
                            break lookingForNames;
                        }
                    }
                }
                resultInReverseOrder.add(Pair.newPair(oldParentName,
                        (SimpleNodeType) oldParent));
            } while (currentDescriptors != null && oldParent != null);
            reverse(resultInReverseOrder);
            return ImmutableList.copyOf(resultInReverseOrder);

        }
    }

    private static class ConversionToNodeContext {
        ConversionToNodeContext(final SimpleNodeType bean) {
            this.bean = bean;
        }

        final Set<StorageNode>         allNodes       = newHashSet();
        final Wrapper<StorageNode>     nodeReference  = Wrapper.createMutable();
        final SimpleNodeType           bean;
        final Map<Object, StorageNode> nodesConverted = newHashMap();

    }

    private static class ConversionToBeanContext {
        private ConversionToBeanContext(final StorageNode node) {
            this.node = node;
        }

        final Wrapper<SimpleNodeType>  beanReference  = Wrapper.createMutable();
        final StorageNode              node;
        final Map<StorageNode, Object> beansConverted = newHashMap();
    }

    private static class BeanToNodeChildData {

        private BeanToNodeChildData(final String propertyName,
                                    final boolean multiple, final Class<?> nodeType) {
            this.propertyName = propertyName;
            this.nodeType = nodeType;
            childrenToSave = newHashSet();
            this.multiple = multiple;
        }

        final Class<?>                   nodeType;

        final String                     propertyName;

        final Collection<SimpleNodeType> childrenToSave;

        final boolean                    multiple;

    }

    @Override
    public void closeResources() {
        currentSession.closeResources();
    }

}
