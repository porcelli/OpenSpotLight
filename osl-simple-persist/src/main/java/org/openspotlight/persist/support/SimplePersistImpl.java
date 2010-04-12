package org.openspotlight.persist.support;

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
import org.openspotlight.storage.domain.key.STUniqueKey;
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
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.openspotlight.common.Pair.create;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Reflection.*;
import static org.openspotlight.common.util.Wrapper.createMutable;

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

    public <T> Iterable<STNodeEntry> convertBeansToNodes(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, Iterable<T> beans) throws Exception {
        List<STNodeEntry> itemsConverted = newArrayList();
        for (T bean : beans) itemsConverted.add(convertBeanToNode(partition, parentNodeN, session, bean));
        return itemsConverted;
    }

    public <T> STNodeEntry convertBeanToNode(STPartition partition, STNodeEntry parentNodeN,
                                             STStorageSession session, T bean) throws Exception {
        return internalConvertBeanToNode(partition, parentNodeN, session, bean);
    }

    public <T> Iterable<STNodeEntry> convertBeansToNodes(STPartition partition, STStorageSession session, Iterable<T> beans) throws Exception {
        return convertBeansToNodes(partition, null, session, beans);
    }

    public <T> STNodeEntry convertBeanToNode(STPartition partition, STStorageSession session, T bean) throws Exception {
        return convertBeanToNode(partition, null, session, bean);
    }

    private <T> STNodeEntry internalConvertBeanToNode(STPartition partition, STNodeEntry parentNodeN,
                                                      STStorageSession session, T bean) throws Exception {
        return internalConvertBeanToNode(partition, parentNodeN, session, bean, null);
    }


    private <T> STNodeEntry internalConvertBeanToNode(STPartition partition, STNodeEntry parentNodeN,
                                                      STStorageSession session, T bean, String propertyName) throws Exception {
        List<PropertyDescriptor> simplePropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> keyPropertiesDescriptor = newLinkedList();
        Wrapper<PropertyDescriptor> parentPropertiesDescriptor = createMutable();
        List<PropertyDescriptor> childrenPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> streamPropertiesDescriptor = newLinkedList();
        fillDescriptors(bean, bean.getClass(), simplePropertiesDescriptor, keyPropertiesDescriptor,
                parentPropertiesDescriptor, childrenPropertiesDescriptor, streamPropertiesDescriptor);
        STNodeEntry newNodeEntry = createNewNode(partition, parentNodeN, session, bean, keyPropertiesDescriptor,
                parentPropertiesDescriptor, propertyName);
        fillSimpleProperties(session, bean, simplePropertiesDescriptor, newNodeEntry);
        fillStreamProperties(session, bean, streamPropertiesDescriptor, newNodeEntry);
        fillChildrenProperties(partition, session, bean, childrenPropertiesDescriptor, newNodeEntry);
        return newNodeEntry;
    }

    private <T> void fillChildrenProperties(STPartition partition, STStorageSession session, T bean,
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
                throw new IllegalStateException("invalid type");
            }
        }
        for (Pair<SimpleNodeType, String> p : nodesToConvert)
            internalConvertBeanToNode(partition, newNodeEntry, session, p.getK1(), p.getK2());
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

    private <T> STNodeEntry createNewNode(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, T bean,
                                          List<PropertyDescriptor> keyPropertiesDescriptor,
                                          Wrapper<PropertyDescriptor> parentPropertiesDescriptor, String propertyName) throws Exception {
        String name = internalGetNodeName(bean);
        STNodeEntryFactory.STNodeEntryBuilder builder = session.withPartition(partition).createWithName(name);

        if (parentNodeN != null) {
            builder.withParent(parentNodeN);
        } else if (parentPropertiesDescriptor.getWrapped() != null) {
            STUniqueKey parentKey = buildParentKey(partition, session, bean, parentPropertiesDescriptor);
            builder.withParentKey(parentKey);
        }
        for (PropertyDescriptor descriptor : keyPropertiesDescriptor) {
            builder.withKey(descriptor.getName(), (Class<? extends Serializable>) descriptor.getPropertyType(),
                    (Serializable) descriptor.getReadMethod().invoke(bean));
        }
        if (propertyName != null) builder.withKey(NODE_PROPERTY_NAME, String.class, propertyName);
        STNodeEntry newNode = builder.andCreate();
        newNode.getVerifiedOperations().setSimpleProperty(session, NODE_ENTRY_TYPE, String.class, name);
        return newNode;
    }

    private <T> STUniqueKey buildParentKey(STPartition partition, STStorageSession session, T bean,
                                           Wrapper<PropertyDescriptor> paramParentPropertyDescriptor) throws Exception {
        if (paramParentPropertyDescriptor.getWrapped() == null) return null;
        Object parent = paramParentPropertyDescriptor.getWrapped().getReadMethod().invoke(bean);
        Wrapper<PropertyDescriptor> parentPropertyDescriptor = createMutable();
        parentPropertyDescriptor.setWrapped(paramParentPropertyDescriptor.getWrapped());

        STStorageSession.STUniqueKeyBuilder builder = session.withPartition(partition)
                .createKey(internalGetNodeName(parent));
        boolean first = true;
        do {
            PropertyDescriptor wrapped = parentPropertyDescriptor.getWrapped();
            List<PropertyDescriptor> keyPropertiesDescriptor = newLinkedList();
            if (first) first = false;
            else builder = builder.withParent(internalGetNodeName(parent));
            builder.withEntry(NODE_PROPERTY_NAME, String.class, wrapped.getName());
            parentPropertyDescriptor.setWrapped(null);
            fillDescriptors(parent, parent.getClass(), Lists.<PropertyDescriptor>newLinkedList(), keyPropertiesDescriptor,
                    parentPropertyDescriptor, Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList());
            for (PropertyDescriptor keyDescriptor : keyPropertiesDescriptor) {
                Object value = keyDescriptor.getReadMethod().invoke(parent);
                builder.withEntry(keyDescriptor.getName(), (Class<? extends Serializable>) findClass(keyDescriptor.getPropertyType().getName()),
                        (Serializable) value);
            }
            wrapped = parentPropertyDescriptor.getWrapped();
            if (wrapped != null)
                parent = wrapped.getReadMethod().invoke(parent);
            else
                parent = null;
        } while (parent != null);
        return builder.andCreate();
    }


    private <T> void fillDescriptors(T bean, Class<? extends Object> beanType,
                                     List<PropertyDescriptor> simplePropertiesDescriptor,
                                     List<PropertyDescriptor> keyPropertiesDescriptor,
                                     Wrapper<PropertyDescriptor> parentPropertyDescriptor,
                                     List<PropertyDescriptor> childrenPropertiesDescriptor,
                                     List<PropertyDescriptor> streamPropertiesDescriptor) throws Exception {

        fillDescriptors(bean, beanType, simplePropertiesDescriptor, keyPropertiesDescriptor, parentPropertyDescriptor,
                childrenPropertiesDescriptor, streamPropertiesDescriptor, Lists.<PropertyDescriptor>newLinkedList());
    }

    private <T> void fillDescriptors(T bean, Class<? extends Object> beanType, List<PropertyDescriptor> simplePropertiesDescriptor,
                                     List<PropertyDescriptor> keyPropertiesDescriptor,
                                     Wrapper<PropertyDescriptor> parentPropertyDescriptor,
                                     List<PropertyDescriptor> childrenPropertiesDescriptor,
                                     List<PropertyDescriptor> streamPropertiesDescriptor,
                                     List<PropertyDescriptor> parentPropertiesDescriptors) throws Exception {
        for (PropertyDescriptor descriptor : getPropertyDescriptors(beanType)) {
            if (descriptor.getName().equals("class")) continue;//Object#getClass
            Method readMethod = descriptor.getReadMethod();
            if (readMethod.isAnnotationPresent(TransientProperty.class)) continue;
            Class<?> returnType = readMethod.getReturnType();
            if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                Object value = bean!=null?readMethod.invoke(bean):null;
                if (value != null && parentPropertyDescriptor != null && parentPropertyDescriptor.getWrapped() != null)
                    throw new IllegalStateException("only one parent property is allowed");
                if (value != null) parentPropertyDescriptor.setWrapped(descriptor);
                parentPropertiesDescriptors.add(descriptor);
            } else if (readMethod.isAnnotationPresent(KeyProperty.class)) {
                keyPropertiesDescriptor.add(descriptor);
            } else if (readMethod.isAnnotationPresent(PersistPropertyAsStream.class)) {
                streamPropertiesDescriptor.add(descriptor);
            } else if (returnType.isAssignableFrom(InputStream.class)) {
                streamPropertiesDescriptor.add(descriptor);
            } else if (SimpleNodeType.class.isAssignableFrom(returnType)) {
                childrenPropertiesDescriptor.add(descriptor);
            } else if (Collection.class.isAssignableFrom(returnType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(readMethod);
                if (SimpleNodeType.class.isAssignableFrom(methodInformation.getItemType())) {
                    childrenPropertiesDescriptor.add(descriptor);
                } else {
                    simplePropertiesDescriptor.add(descriptor);
                }
            } else if (Map.class.isAssignableFrom(returnType)) {
                Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation = unwrapMapFromMethodReturn(readMethod);
                if (SimpleNodeType.class.isAssignableFrom(methodInformation.getItemType().getK2())) {
                    childrenPropertiesDescriptor.add(descriptor);
                } else {
                    simplePropertiesDescriptor.add(descriptor);
                }
            } else {
                simplePropertiesDescriptor.add(descriptor);
            }
        }
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

        List<PropertyDescriptor> parentPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> simplePropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> keyPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> streamPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> childrenPropertiesDescriptor = newLinkedList();
        fillDescriptors(null, beanType, simplePropertiesDescriptor, keyPropertiesDescriptor,
                null, childrenPropertiesDescriptor, streamPropertiesDescriptor);
        fillParents(session, node, bean, parentPropertiesDescriptor, beanParent);
        fillSimpleProperties(session, node, bean, keyPropertiesDescriptor);
        fillSimpleProperties(session, node, bean, simplePropertiesDescriptor);
        fillStreamProperties(session, node, bean, streamPropertiesDescriptor);
        fillChildren(session, node, bean, childrenPropertiesDescriptor);
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
            } else if (Serializable.class.isAssignableFrom(propertyType)) {
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
        String propertyName = node.getPropertyValue(session, NODE_PROPERTY_NAME);
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


}
