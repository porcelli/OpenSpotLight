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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Class.forName;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.openspotlight.common.Pair.create;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Reflection.unwrapCollectionFromMethodReturn;
import static org.openspotlight.common.util.Reflection.unwrapMapFromMethodReturn;
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

    private static final String NODE_ENTRY_NAME = "internal-node-entry-name";

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
        newNodeEntry.getVerifiedOperations().setSimpleProperty(session,NODE_ENTRY_NAME,String.class,newNodeEntry.getNodeEntryName());//to be used on find operations
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
                newNodeEntry.getVerifiedOperations().setSimpleProperty(session, property.getName(),
                        (Class<? super Serializable>) property.getPropertyType(), beforeSerializeSerializable((Serializable) value));
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
        String name = internalGetNodeName(bean, propertyName);
        STNodeEntryFactory.STNodeEntryBuilder builder = session.withPartition(partition).createWithName(name);

        if (parentNodeN != null) {
            builder.withParent(parentNodeN);
        } else if (parentPropertiesDescriptor.getWrapped() != null) {
            STUniqueKey parentKey = buildParentKey(partition, session, bean, parentPropertiesDescriptor, parentPropertiesDescriptor.getWrapped().getName());
            if (parentKey != null) builder.withParentKey(parentKey);
        }
        for (PropertyDescriptor descriptor : keyPropertiesDescriptor) {
            builder.withKey(descriptor.getName(), (Class<? extends Serializable>) descriptor.getPropertyType(),
                    (Serializable) descriptor.getReadMethod().invoke(bean));
        }
        return builder.andCreate();
    }

    private <T> STUniqueKey buildParentKey(STPartition partition, STStorageSession session, T bean,
                                           Wrapper<PropertyDescriptor> parentPropertiesDescriptor,
                                           String firstParentPropertyName) throws Exception {
        if (parentPropertiesDescriptor.getWrapped() == null) return null;
        Object parent = parentPropertiesDescriptor.getWrapped().getReadMethod().invoke(bean);
        String parentPropertyName = firstParentPropertyName;
        STStorageSession.STUniqueKeyBuilder builder = session.withPartition(partition).createKey(internalGetNodeName(parent, parentPropertyName));
        while (parent != null) {
            boolean parentChanged = false;
            for (PropertyDescriptor descriptor : getPropertyDescriptors(parent)) {
                Method readMethod = descriptor.getReadMethod();
                if (readMethod.isAnnotationPresent(KeyProperty.class)) {

                } else if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                    Object newParent = readMethod.invoke(parent);
                    if (newParent != null) {
                        if (parentChanged) throw new IllegalStateException("it is allowed only one parent property");
                        parent = newParent;
                        parentChanged = true;
                        parentPropertyName = descriptor.getName();
                    }
                }
            }
            if (!parentChanged) break;
            if (parent != null) {
                builder.withParent(internalGetNodeName(parent, parentPropertyName));
            }
        }
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
                if (parentPropertyDescriptor.getWrapped() != null)
                    throw new IllegalStateException("only one parent property is allowed");
                if (bean != null && readMethod.invoke(bean) != null) parentPropertyDescriptor.setWrapped(descriptor);
                parentPropertiesDescriptors.add(descriptor);
            } else if (readMethod.isAnnotationPresent(KeyProperty.class)) {
                keyPropertiesDescriptor.add(descriptor);
            } else if (readMethod.isAnnotationPresent(PersistPropertyAsStream.class)) {
                streamPropertiesDescriptor.add(descriptor);
            } else if (returnType.isAssignableFrom(InputStream.class)) {
                streamPropertiesDescriptor.add(descriptor);
            } else if (returnType.isAssignableFrom(SimpleNodeType.class)) {
                childrenPropertiesDescriptor.add(descriptor);
            } else if (returnType.isAssignableFrom(Collection.class)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(readMethod);
                if (methodInformation.getItemType().isAssignableFrom(SimpleNodeType.class)) {
                    childrenPropertiesDescriptor.add(descriptor);
                } else {
                    simplePropertiesDescriptor.add(descriptor);
                }
            } else if (returnType.isAssignableFrom(Map.class)) {
                Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation = unwrapMapFromMethodReturn(readMethod);
                if (methodInformation.getItemType().getK2().isAssignableFrom(SimpleNodeType.class)) {
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

    private static final String SEPARATOR = "--";

    private String internalNodeGetPropertyName(STNodeEntry node) {
        String nodeName = node.getNodeEntryName();
        if (nodeName.contains(SEPARATOR)) return nodeName.substring(nodeName.indexOf(SEPARATOR) + SEPARATOR.length());
        return null;
    }

    private String internalGetNodeType(STNodeEntry node) {
        String nodeName = node.getNodeEntryName();
        if (nodeName.contains(SEPARATOR)) return nodeName.substring(0, nodeName.indexOf(SEPARATOR));
        return nodeName;
    }

    private <T> String internalGetNodeName(T bean, String propertyName) {
        return this.<T>internalGetNodeName((Class<T>) bean.getClass(), propertyName);
    }

    private <T> String internalGetNodeName(Class<T> beanType, String propertyName) {
        return beanType.getName() + (propertyName != null ? SEPARATOR + propertyName : "");
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
            String childrenName = internalGetNodeName(propertyType, descriptor.getName());
            Set<STNodeEntry> children = node.getChildrenNamed(session, childrenName);
            List<Object> childrenAsBeans = newLinkedList();
            if ((!isMultiple) && children.size() > 0)
                throw new IllegalStateException("more than one child on a unique property");
            for (STNodeEntry child : children) childrenAsBeans.add(internalConvertNodeToBean(session, child, bean));
            if (isMultiple) {
                Collection c = (Collection) readMethod.invoke(bean);
                for (Object o : childrenAsBeans) {
                    c.add(o);
                }
            } else if (children.size() > 0) descriptor.getWriteMethod().invoke(bean, children.iterator().next());
        }
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
            descriptor.getWriteMethod().invoke(bean, node.<Object>getPropertyValue(session, descriptor.getName()));
        }
    }

    private <T> void fillParents(STStorageSession session, STNodeEntry node, T bean,
                                 List<PropertyDescriptor> parentPropertiesDescriptor, Object beanParent) throws Exception {
        STNodeEntry parent = node.getParent(session);
        String propertyName = internalNodeGetPropertyName(node);
        for (PropertyDescriptor descriptor : parentPropertiesDescriptor) {
            if (propertyName.equals(descriptor.getName())) {
                Object parentAsBean = beanParent == null ? convertNodeToBean(session, parent) : beanParent;
                descriptor.getWriteMethod().invoke(bean, parentAsBean);
                break;
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

    public <T> Set<T> findByProperties(STPartition partition, STStorageSession session, Class<T> beanType,
                                       String[] propertyNames, Object[] propertyValues) {
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

    public <T> Set<T> findByProperties(STPartition partition, STStorageSession session, Class<T> beanType, String[] propertyNames, Class[] propertyTypes, Object[] propertyValues) {
    }

    public <T> T findUniqueByProperties(STPartition partition, STStorageSession session, Class<T> beanType, String[] propertyNames, Object[] propertyValues) {
        Set<T> result = findByProperties(partition, session, beanType, propertyNames, propertyValues);
        checkCondition("validUniqueResultSize", result.size() <= 1);
        return result.size() == 0 ? null : result.iterator().next();
    }

    public <T> T findUniqueByProperties(STPartition partition, STStorageSession session, Class<T> beanType, String[] propertyNames, Class[] propertyTypes, Object[] propertyValues) {
        Set<T> result = findByProperties(partition, session, beanType, propertyNames, propertyTypes, propertyValues);
        checkCondition("validUniqueResultSize", result.size() <= 1);
        return result.size() == 0 ? null : result.iterator().next();
    }


}
