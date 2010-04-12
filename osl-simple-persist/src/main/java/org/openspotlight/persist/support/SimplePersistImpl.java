package org.openspotlight.persist.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.apache.commons.lang.SerializationUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Wrapper;
import org.openspotlight.persist.annotation.*;
import org.openspotlight.persist.internal.StreamPropertyWithParent;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryFactory;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static java.util.Collections.reverse;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.openspotlight.common.Pair.create;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Reflection.unwrapCollectionFromMethodReturn;
import static org.openspotlight.common.util.Reflection.unwrapMapFromMethodReturn;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 05/04/2010
 * Time: 13:19:32
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SimplePersistImpl implements SimplePersistCapable<STNodeEntry, STStorageSession> {

    private static final String NODE_ENTRY_TYPE = "internal-node-entry-type";
    private static final String NODE_PROPERTY_NAME = "internal-node-proeprty-name";


    public <T> Iterable<STNodeEntry> convertBeansToNodes(STPartition partition, STStorageSession session, Iterable<T> beans) throws Exception {
        List<STNodeEntry> itemsConverted = newArrayList();
        for (T bean : beans) itemsConverted.add(convertBeanToNode(partition, session, bean));
        return itemsConverted;
    }

    public <T> STNodeEntry convertBeanToNode(STPartition partition, STStorageSession session, T bean) throws Exception {
        return internalConvertBeanToNode(partition, session, bean);
    }

    private <T> STNodeEntry internalConvertBeanToNode(STPartition partition,
                                                      STStorageSession session, T bean) throws Exception {
        ConversionToNodeContext context = new ConversionToNodeContext(partition, session, (SimpleNodeType) bean);
        internalConvertBeanToNode(context, null, null, null);
        STNodeEntry result = context.nodeReference.getWrapped();
        checkNotNull("result", result);
        return result;
    }


    private <T> STNodeEntry internalConvertBeanToNode(ConversionToNodeContext context, String propertyName, SimpleNodeType bean, STNodeEntry parentNode) throws Exception {
        if (bean == null) {
            STNodeEntry currentParentNode = null;
            Descriptors currentBeanDescriptors = Descriptors.fillDescriptors(context.bean);
            List<Pair<String, SimpleNodeType>> rootObjects = currentBeanDescriptors.getRootObjects();
            for (Pair<String, SimpleNodeType> pair : rootObjects) {
                currentParentNode = internalConvertBeanToNode(context, pair.getK1(), pair.getK2(), currentParentNode);
            }
            return context.nodeReference.getWrapped();
        } else {
            STNodeEntry cached = context.nodesConverted.get(bean);
            if (cached == null) {
                Descriptors parentDescriptors = Descriptors.fillDescriptors(bean);
                cached = createNewNode(context, parentNode, parentDescriptors, propertyName);
                context.nodesConverted.put(bean, cached);
                fillSimpleProperties(context.session, bean, parentDescriptors.simplePropertiesDescriptor, cached);
                fillStreamProperties(context.session, bean, parentDescriptors.streamPropertiesDescriptor, cached);
                fillChildrenProperties(context, bean, parentDescriptors.childrenPropertiesDescriptor, cached);
                if (bean == context.bean) context.nodeReference.setWrapped(cached);
            }
            return cached;
        }

    }

    private <T> void fillChildrenProperties(ConversionToNodeContext context, T bean,
                                            List<PropertyDescriptor> childrenPropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        List<Pair<SimpleNodeType, String>> nodesToConvert = newLinkedList();
        for (PropertyDescriptor property : childrenPropertiesDescriptor) {
            Class<?> propertyType = property.getPropertyType();
            Object value = property.getReadMethod().invoke(bean);
            if (SimpleNodeType.class.isAssignableFrom(propertyType)) {
                nodesToConvert.add(create((SimpleNodeType) value, property.getName()));
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation =
                        unwrapCollectionFromMethodReturn(property.getReadMethod());
                if (List.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    for (SimpleNodeType t : (List<SimpleNodeType>) value)
                        nodesToConvert.add(create(t, property.getName()));
                } else if (Set.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    for (SimpleNodeType t : (Set<SimpleNodeType>) value)
                        nodesToConvert.add(create(t, property.getName()));
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else {
                throw new IllegalStateException("invalid type:" + property.getPropertyType());
            }
        }
        for (Pair<SimpleNodeType, String> p : nodesToConvert)
            internalConvertBeanToNode(context, p.getK2(), p.getK1(), newNodeEntry);
    }

    private <T> void fillStreamProperties(STStorageSession session, T bean, List<PropertyDescriptor> streamPropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        for (PropertyDescriptor property : streamPropertiesDescriptor) {
            Class<?> propertyType = property.getPropertyType();
            Method readMethod = property.getReadMethod();
            Object value = readMethod.invoke(bean);
            if (InputStream.class.isAssignableFrom(propertyType)) {
                newNodeEntry.getVerifiedOperations().setInputStreamProperty(session, property.getName(), (InputStream) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(property.getReadMethod());
                if (List.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    newNodeEntry.getVerifiedOperations().setSerializedListProperty(session, property.getName(),
                            methodInformation.getItemType(), beforeSerializeList((List<? extends Serializable>) value, readMethod));
                } else if (Set.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    newNodeEntry.getVerifiedOperations().setSerializedSetProperty(session, property.getName(),
                            methodInformation.getItemType(), beforeSerializeSet((Set<? extends Serializable>) value, readMethod));
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else if (Map.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation = unwrapMapFromMethodReturn(property.getReadMethod());
                newNodeEntry.getVerifiedOperations().setSerializedMapProperty(session, property.getName(),
                        methodInformation.getItemType().getK1(),
                        methodInformation.getItemType().getK2(),
                        beforeSerializeMap((Map<? extends Serializable, ? extends Serializable>) value, readMethod));

            } else if (Serializable.class.isAssignableFrom(propertyType)) {
                if (propertyType.equals(String.class) || Number.class.isAssignableFrom(propertyType)
                        || propertyType.isPrimitive() || Boolean.class.equals(propertyType)
                        || Character.class.equals(propertyType) || Date.class.equals(propertyType)) {
                    newNodeEntry.getVerifiedOperations().setSimpleProperty(session, property.getName(),
                            (Class<? super Serializable>) property.getPropertyType(), beforeSerializeSerializable((Serializable) value));
                } else {
                    newNodeEntry.getVerifiedOperations().setSerializedPojoProperty(session, property.getName(),
                            (Class<? super Serializable>) property.getPropertyType(), beforeSerializeSerializable((Serializable) value));
                }


            } else {
                throw new IllegalStateException("invalid type");
            }

        }
    }

    private <T> void fillSimpleProperties(STStorageSession session, T bean,
                                          List<PropertyDescriptor> simplePropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        for (PropertyDescriptor property : simplePropertiesDescriptor) {
            newNodeEntry.getVerifiedOperations().setSimpleProperty(session, property.getName(),
                    (Class<? super Serializable>) property.getPropertyType(), (Serializable) property.getReadMethod().invoke(bean));
        }
    }

    private <T> STNodeEntry createNewNode(ConversionToNodeContext context, STNodeEntry parentNode,
                                          Descriptors descriptors, String propertyName) throws Exception {
        String name = internalGetNodeName(descriptors.bean);
        STNodeEntryFactory.STNodeEntryBuilder builder = context.session.withPartition(context.partition).createWithName(name);

        if (parentNode != null) {
            builder.withParent(parentNode);
        }
        for (PropertyDescriptor descriptor : descriptors.keyPropertiesDescriptor) {
            builder.withKey(descriptor.getName(), (Class<? extends Serializable>) descriptor.getPropertyType(),
                    (Serializable) descriptor.getReadMethod().invoke(descriptors.bean));
        }
        if (propertyName != null) {
            builder.withKey(NODE_PROPERTY_NAME, String.class, propertyName);
        }
        STNodeEntry newNode = builder.andCreate();
        newNode.getVerifiedOperations().setSimpleProperty(context.session, NODE_ENTRY_TYPE, String.class, name);
        return newNode;
    }

    public <T> Iterable<T> convertNodesToBeans(STStorageSession session, Iterable<STNodeEntry> nodes) throws Exception {
        List<T> itemsConverted = newArrayList();
        for (STNodeEntry node : nodes) itemsConverted.add(this.<T>convertNodeToBean(session, node));
        return itemsConverted;
    }


    private String internalGetNodeType(STNodeEntry node) {
        return node.getNodeEntryName();
    }

    private <T> String internalGetNodeName(T bean) {
        return this.<T>internalGetNodeName((Class<T>) bean.getClass());
    }

    private <T> String internalGetNodeName(Class<T> beanType) {
        return beanType.getName();
    }

    public <T> T convertNodeToBean(STStorageSession session, STNodeEntry node) throws Exception {
        T bean = this.<T>internalConvertNodeToBean(session, node, null);
        return bean;
    }

    private <T> T internalConvertNodeToBean(STStorageSession session, STNodeEntry node, Object beanParent) throws Exception {
        Class<T> beanType = (Class<T>) forName(internalGetNodeType(node));
        T bean = beanType.newInstance();

        Descriptors descriptors = Descriptors.fillDescriptors(beanType);
        fillParents(session, node, bean, descriptors.parentPropertiesDescriptor, beanParent);
        fillSimpleProperties(session, node, bean, descriptors.keyPropertiesDescriptor);
        fillSimpleProperties(session, node, bean, descriptors.simplePropertiesDescriptor);
        fillStreamProperties(session, node, bean, descriptors.streamPropertiesDescriptor);
        fillChildren(session, node, bean, descriptors.childrenPropertiesDescriptor);
        return bean;
    }

    private <T> void fillChildren(STStorageSession session, STNodeEntry node, T bean, List<PropertyDescriptor> childrenPropertiesDescriptor) throws Exception {
        for (PropertyDescriptor descriptor : childrenPropertiesDescriptor) {
            Class<?> propertyType = descriptor.getPropertyType();
            Class<?> nodeType = null;
            boolean isMultiple = Collection.class.isAssignableFrom(propertyType);
            Method readMethod = descriptor.getReadMethod();
            if (isMultiple) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription =
                        unwrapCollectionFromMethodReturn(readMethod);
                if (List.class.isAssignableFrom(propertyType)) {
                    descriptor.getWriteMethod().invoke(bean, newLinkedList());
                } else if (Set.class.isAssignableFrom(propertyType)) {
                    descriptor.getWriteMethod().invoke(bean, newHashSet());
                } else throw new IllegalStateException("wrong child type");
                nodeType = methodDescription.getItemType();
            } else if (SimpleNodeType.class.isAssignableFrom(propertyType)) {
                nodeType = propertyType;
            } else throw new IllegalStateException("wrong child type");

            if (!SimpleNodeType.class.isAssignableFrom(nodeType)) throw new IllegalStateException("wrong child type");
            String childrenName = internalGetNodeName(propertyType);
            Set<STNodeEntry> children = node.getChildrenNamed(session, childrenName);
            children = filterChildrenWithProperty(session, children, descriptor.getName());
            List<Object> childrenAsBeans = newLinkedList();
            if ((!isMultiple) && children.size() > 1)
                throw new IllegalStateException("more than one child on a unique property");
            for (STNodeEntry child : children) childrenAsBeans.add(internalConvertNodeToBean(session, child, bean));
            if (isMultiple) {
                Collection c = (Collection) readMethod.invoke(bean);
                for (Object o : childrenAsBeans) {
                    c.add(o);
                }
            } else if (childrenAsBeans.size() > 0) {
                Object value = childrenAsBeans.iterator().next();
                descriptor.getWriteMethod().invoke(bean, value);
            }
        }
    }

    private Set<STNodeEntry> filterChildrenWithProperty(STStorageSession session, Set<STNodeEntry> children, String name) {
        if (name == null) return children;
        Set<STNodeEntry> filtered = newHashSet();
        for (STNodeEntry e : children) {
            if (name.equals(e.getPropertyValue(session, NODE_PROPERTY_NAME))) filtered.add(e);
        }
        return filtered;
    }

    private <T> void fillStreamProperties(STStorageSession session, STNodeEntry node, T bean,
                                          List<PropertyDescriptor> streamPropertiesDescriptor) throws Exception {
        for (PropertyDescriptor descriptor : streamPropertiesDescriptor) {
            Class<?> propertyType = descriptor.getPropertyType();
            if (InputStream.class.isAssignableFrom(propertyType)) {
                InputStream value = node.getPropertyValue(session, descriptor.getName());
                descriptor.getWriteMethod().invoke(bean, value);
            } else if (Serializable.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType)
                    || Map.class.isAssignableFrom(propertyType)) {
                Serializable value = node.getPropertyValue(session, descriptor.getName());
                descriptor.getWriteMethod().invoke(bean, beforeUnConvert((SimpleNodeType) bean, value, descriptor.getReadMethod()));
            } else {
                throw new IllegalStateException("wrong type");
            }
        }
    }


    private <T> void fillSimpleProperties(STStorageSession session, STNodeEntry node, T bean,
                                          List<PropertyDescriptor> simplePropertiesDescriptor) throws IllegalAccessException, InvocationTargetException {
        for (PropertyDescriptor descriptor : simplePropertiesDescriptor) {
            Object value = node.<Object>getPropertyValue(session, descriptor.getName());
            if (value == null && descriptor.getPropertyType().isPrimitive()) continue;
            descriptor.getWriteMethod().invoke(bean, value);
        }
    }

    private <T> void fillParents(STStorageSession session, STNodeEntry node, T bean,
                                 List<PropertyDescriptor> parentPropertiesDescriptor, Object beanParent) throws Exception {

        STNodeEntry parent = node.getParent(session);
        if (parent != null) {
            String propertyName = parent.getPropertyValue(session, NODE_PROPERTY_NAME);
            if (propertyName != null) {
                for (PropertyDescriptor descriptor : parentPropertiesDescriptor) {
                    if (propertyName.equals(descriptor.getName())) {
                        Object parentAsBean = beanParent == null ? convertNodeToBean(session, parent) : beanParent;
                        descriptor.getWriteMethod().invoke(bean, parentAsBean);
                        break;
                    }
                }
            }
        }
    }

    private <T extends SimpleNodeType> Object beforeUnConvert(T bean, Serializable value, Method readMethod) throws Exception {
        if (value instanceof Collection) {
            Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription
                    = unwrapCollectionFromMethodReturn(readMethod);
            if (StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType())) {
                Collection valueAsCollection = (Collection) value;
                for (Object o : valueAsCollection) {
                    if (o instanceof StreamPropertyWithParent) {
                        ((StreamPropertyWithParent) o).setParent(bean);
                    }
                }
            }
        } else if (value instanceof Map) {
            Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodDescription
                    = unwrapMapFromMethodReturn(readMethod);
            if (StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType().getK2())) {
                Map<?, ?> valueAsMap = (Map<?, ?>) value;
                for (Map.Entry<?, ?> entry : valueAsMap.entrySet()) {
                    if (entry.getValue() instanceof StreamPropertyWithParent) {
                        ((StreamPropertyWithParent) entry.getValue()).setParent(bean);
                    }
                }
            }
        } else if (value instanceof StreamPropertyWithParent) {
            ((StreamPropertyWithParent) value).setParent(bean);
        }
        return value;
    }

    private Set<? extends Serializable> beforeSerializeSet(Set<? extends Serializable> value, Method readMethod) throws Exception {
        Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription
                = unwrapCollectionFromMethodReturn(readMethod);

        if (StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType())) {
            Set<Serializable> newCollection = newHashSet();
            for (Serializable o : value) {
                newCollection.add(beforeSerializeSerializable(o));
            }
            return newCollection;
        }
        return value;

    }

    private List<? extends Serializable> beforeSerializeList(List<? extends Serializable> value, Method readMethod) throws Exception {
        Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription
                = unwrapCollectionFromMethodReturn(readMethod);

        if (StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType())) {
            List<Serializable> newCollection = newLinkedList();
            for (Serializable o : value) {
                newCollection.add(beforeSerializeSerializable(o));
            }
            return newCollection;
        }
        return value;

    }

    private Map<? extends Serializable, ? extends Serializable> beforeSerializeMap(Map<? extends Serializable, ? extends Serializable> value, Method readMethod) throws Exception {
        Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodDescription = unwrapMapFromMethodReturn(readMethod);

        if (StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType().getK2())) {
            Map<Serializable, Serializable> newMap = newHashMap();
            for (Map.Entry<? extends Serializable, ? extends Serializable> entry : value.entrySet()) {
                newMap.put(entry.getKey(), beforeSerializeSerializable(entry.getValue()));
            }
            return newMap;
        }
        return value;
    }

    private Serializable beforeSerializeSerializable(Serializable value) {
        if (value instanceof StreamPropertyWithParent) {
            StreamPropertyWithParent newValue = (StreamPropertyWithParent) SerializationUtils.clone(value);
            newValue.setParent(null);
            return newValue;
        }
        return value;
    }

    public <T> Iterable<T> findByProperties(STPartition partition, STStorageSession session, Class<T> beanType,
                                            String[] propertyNames, Object[] propertyValues) throws Exception {
        checkNotNull("partition", partition);
        checkNotNull("session", session);
        checkNotNull("beanType", beanType);
        checkNotNull("propertyNames", propertyNames);
        checkNotNull("propertyValues", propertyValues);
        checkCondition("namesAndValues:sameSize", propertyNames.length == propertyValues.length);
        Class<?>[] types = new Class[propertyNames.length];
        for (int i = 0, size = types.length; i < size; i++) {
            types[i] = propertyValues[i].getClass();
        }
        return findByProperties(partition, session, beanType, propertyNames, types, propertyValues);
    }

    public <T> Iterable<T> findByProperties(STPartition partition, STStorageSession session, Class<T> beanType,
                                            String[] propertyNames, Class[] propertyTypes, Object[] propertyValues) throws Exception {
        checkNotNull("partition", partition);
        checkNotNull("session", session);
        checkNotNull("beanType", beanType);
        checkNotNull("propertyNames", propertyNames);
        checkNotNull("propertyTypes", propertyNames);
        checkNotNull("propertyValues", propertyValues);
        checkCondition("namesAndValues:sameSize", propertyNames.length == propertyValues.length);
        checkCondition("namesAndTypes:sameSize", propertyNames.length == propertyTypes.length);

        STStorageSession.STCriteriaBuilder builder = session.withPartition(partition).createCriteria()
                .withProperty(NODE_ENTRY_TYPE).equals(String.class, beanType.getName());
        for (int i = 0, size = propertyNames.length; i < size; i++) {
            checkCondition("correctType:" + propertyNames[i], propertyValues[i] == null || propertyTypes[i].isPrimitive() ||
                    propertyTypes[i].isInstance(propertyValues[i]));
            builder.withProperty(propertyNames[i]).equals(propertyTypes[i], (Serializable) propertyValues[i]);
        }
        Set<STNodeEntry> result = builder.buildCriteria().andFind(session);
        return this.<T>convertNodesToBeans(session, result);

    }

    public <T> T findUniqueByProperties(STPartition partition, STStorageSession session, Class<T> beanType, String[] propertyNames, Object[] propertyValues) throws Exception {
        Iterable<T> result = findByProperties(partition, session, beanType, propertyNames, propertyValues);
        Iterator<T> it = result.iterator();
        return it.hasNext() ? it.next() : null;
    }

    public <T> T findUniqueByProperties(STPartition partition, STStorageSession session, Class<T> beanType, String[] propertyNames, Class[] propertyTypes, Object[] propertyValues) throws Exception {
        Iterable<T> result = findByProperties(partition, session, beanType, propertyNames, propertyTypes, propertyValues);
        Iterator<T> it = result.iterator();
        return it.hasNext() ? it.next() : null;
    }

    private static class Descriptors {
        private Descriptors(Class<?> beanType, SimpleNodeType bean,
                            Wrapper<PropertyDescriptor> parentPropertyDescriptor, List<PropertyDescriptor> simplePropertiesDescriptor,
                            List<PropertyDescriptor> keyPropertiesDescriptor, List<PropertyDescriptor> streamPropertiesDescriptor,
                            List<PropertyDescriptor> childrenPropertiesDescriptor, List<PropertyDescriptor> parentPropertiesDescriptor) {
            this.beanType = beanType;
            this.bean = bean;
            this.parentPropertyDescriptor = parentPropertyDescriptor;
            this.simplePropertiesDescriptor = simplePropertiesDescriptor;
            this.keyPropertiesDescriptor = keyPropertiesDescriptor;
            this.streamPropertiesDescriptor = streamPropertiesDescriptor;
            this.childrenPropertiesDescriptor = childrenPropertiesDescriptor;
            this.parentPropertiesDescriptor = parentPropertiesDescriptor;
        }

        private List<Pair<String, SimpleNodeType>> rootObjects = null;
        final Class<?> beanType;
        final SimpleNodeType bean;
        final Wrapper<PropertyDescriptor> parentPropertyDescriptor;
        final List<PropertyDescriptor> simplePropertiesDescriptor;
        final List<PropertyDescriptor> keyPropertiesDescriptor;
        final List<PropertyDescriptor> streamPropertiesDescriptor;
        final List<PropertyDescriptor> childrenPropertiesDescriptor;
        final List<PropertyDescriptor> parentPropertiesDescriptor;

        public static Descriptors createMutable(Class<?> beanType, Object bean) {
            return new Descriptors(beanType, (SimpleNodeType) bean, Wrapper.<PropertyDescriptor>createMutable(), Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList());
        }

        public static Descriptors createImmutableFrom(Descriptors from) {
            return new Descriptors(from.beanType, from.bean, Wrapper.<PropertyDescriptor>createImmutable(from.parentPropertyDescriptor.getWrapped()),
                    ImmutableList.copyOf(from.simplePropertiesDescriptor), ImmutableList.copyOf(from.keyPropertiesDescriptor),
                    ImmutableList.copyOf(from.streamPropertiesDescriptor), ImmutableList.copyOf(from.childrenPropertiesDescriptor),
                    ImmutableList.copyOf(from.parentPropertiesDescriptor));
        }

        static <T extends SimpleNodeType> Descriptors fillDescriptors(Object bean) throws Exception {
            return fillDescriptors(bean, bean.getClass());

        }


        static <T extends SimpleNodeType> Descriptors fillDescriptors(Class<?> beanType) throws Exception {
            return fillDescriptors(null, beanType);
        }

        static <T extends SimpleNodeType> Descriptors fillDescriptors(Object bean, Class<?> beanType) throws Exception {
            Descriptors descriptors = Descriptors.createMutable(beanType, bean);
            for (PropertyDescriptor descriptor : getPropertyDescriptors(beanType)) {
                if (descriptor.getName().equals("class")) continue;//Object#getClass
                Method readMethod = descriptor.getReadMethod();
                if (readMethod.isAnnotationPresent(TransientProperty.class)) continue;
                Class<?> returnType = readMethod.getReturnType();
                if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                    Object value = bean != null ? readMethod.invoke(bean) : null;
                    if (value != null && descriptors.parentPropertyDescriptor != null && descriptors.parentPropertyDescriptor.getWrapped() != null)
                        throw new IllegalStateException("only one parent property is allowed");
                    if (value != null) descriptors.parentPropertyDescriptor.setWrapped(descriptor);
                    descriptors.parentPropertiesDescriptor.add(descriptor);
                }else if (readMethod.isAnnotationPresent(KeyProperty.class)) {
                    descriptors.keyPropertiesDescriptor.add(descriptor);
                } else if (readMethod.isAnnotationPresent(PersistPropertyAsStream.class)) {
                    descriptors.streamPropertiesDescriptor.add(descriptor);
                } else if (returnType.isAssignableFrom(InputStream.class)) {
                    descriptors.streamPropertiesDescriptor.add(descriptor);
                } else if (SimpleNodeType.class.isAssignableFrom(returnType)) {
                    descriptors.childrenPropertiesDescriptor.add(descriptor);
                } else if (Collection.class.isAssignableFrom(returnType)) {
                    Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(readMethod);
                    if (SimpleNodeType.class.isAssignableFrom(methodInformation.getItemType())) {
                        descriptors.childrenPropertiesDescriptor.add(descriptor);
                    } else {
                        descriptors.streamPropertiesDescriptor.add(descriptor);
                    }
                } else if (Map.class.isAssignableFrom(returnType)) {
                    Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation = unwrapMapFromMethodReturn(readMethod);
                    if (SimpleNodeType.class.isAssignableFrom(methodInformation.getItemType().getK2())) {
                        descriptors.childrenPropertiesDescriptor.add(descriptor);
                    } else {
                        descriptors.streamPropertiesDescriptor.add(descriptor);
                    }
                } else {
                    descriptors.simplePropertiesDescriptor.add(descriptor);
                }
            }
            return Descriptors.createImmutableFrom(descriptors);
        }


        public List<Pair<String, SimpleNodeType>> getRootObjects() throws Exception {
            if (rootObjects == null) rootObjects = loadRootObjects();
            return rootObjects;
        }

        private List<Pair<String, SimpleNodeType>> loadRootObjects() throws Exception {
            List<Pair<String, SimpleNodeType>> resultInReverseOrder = newLinkedList();
            Descriptors currentDescriptors = this;
            Object parent = bean;
            Object oldParent = null;

            do {
                PropertyDescriptor descriptor = currentDescriptors.parentPropertyDescriptor.getWrapped();
                oldParent = parent;
                Class<?> oldType = oldParent != null ? oldParent.getClass() : null;
                parent = descriptor != null ? descriptor.getReadMethod().invoke(parent) : null;
                currentDescriptors = parent != null ? Descriptors.fillDescriptors(parent) : null;
                String oldParentName = null;
                if (oldParent != null && currentDescriptors != null) {
                    lookingForNames:
                    for (PropertyDescriptor childDescriptor : currentDescriptors.childrenPropertiesDescriptor) {
                        if (childDescriptor.getPropertyType().equals(oldType)) {
                            oldParentName = childDescriptor.getName();
                            break lookingForNames;
                        }
                    }
                }
                resultInReverseOrder.add(Pair.create(oldParentName, (SimpleNodeType) oldParent));
            }
            while (currentDescriptors != null && oldParent != null);
            reverse(resultInReverseOrder);
            return ImmutableList.copyOf(resultInReverseOrder);

        }
    }


    private static class ConversionToNodeContext {
        ConversionToNodeContext(STPartition partition, STStorageSession session, SimpleNodeType bean) {
            this.partition = partition;
            this.session = session;
            this.bean = bean;
        }

        final STPartition partition;
        final STStorageSession session;
        final Wrapper<STNodeEntry> nodeReference = Wrapper.createMutable();
        final SimpleNodeType bean;
        final Map<Object, STNodeEntry> nodesConverted = newHashMap();
    }


}
