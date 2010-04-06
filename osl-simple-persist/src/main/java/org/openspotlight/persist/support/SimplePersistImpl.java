package org.openspotlight.persist.support;

import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Wrapper;
import org.openspotlight.persist.annotation.*;
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
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
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
public class SimplePersistImpl implements SimplePersistCapable<STNodeEntry, STStorageSession> {
    public <T> Iterable<STNodeEntry> convertBeansToNodes(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, Iterable<T> beans) throws Exception {
        List<STNodeEntry> itemsConverted = newArrayList();
        for (T bean : beans) itemsConverted.add(convertBeanToNode(partition, parentNodeN, session, bean));
        return itemsConverted;
    }

    public <T> STNodeEntry convertBeanToNode(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, T bean) throws Exception {
        return internalConvertBeanToNode(partition, parentNodeN, session, bean);
    }

    private <T> String internalGetNodeName(T bean) {
        Class<?> type = bean.getClass();
        if (type.isAnnotationPresent(Name.class)) {
            return type.getAnnotation(Name.class).value();
        }
        return type.getName();
    }

    private <T> STNodeEntry internalConvertBeanToNode(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, T bean) throws Exception {
        List<PropertyDescriptor> simplePropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> keyPropertiesDescriptor = newLinkedList();
        Wrapper<PropertyDescriptor> parentPropertiesDescriptor = createMutable();
        List<PropertyDescriptor> childrenPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> streamPropertiesDescriptor = newLinkedList();
        fillDescriptors(bean, bean.getClass(), simplePropertiesDescriptor, keyPropertiesDescriptor, parentPropertiesDescriptor, childrenPropertiesDescriptor, streamPropertiesDescriptor);
        STNodeEntry newNodeEntry = createNewNode(partition, parentNodeN, session, bean, keyPropertiesDescriptor, parentPropertiesDescriptor);
        fillSimpleProperties(session, bean, simplePropertiesDescriptor, newNodeEntry);
        fillStreamProperties(session, bean, streamPropertiesDescriptor, newNodeEntry);
        fillChildrenProperties(partition, session, bean, childrenPropertiesDescriptor, newNodeEntry);
        return newNodeEntry;
    }

    private <T> void fillChildrenProperties(STPartition partition, STStorageSession session, T bean, List<PropertyDescriptor> childrenPropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        List<SimpleNodeType> nodesToConvert = newLinkedList();
        for (PropertyDescriptor property : childrenPropertiesDescriptor) {
            Class<?> propertyType = property.getPropertyType();
            Object value = property.getReadMethod().invoke(bean);
            if (SimpleNodeType.class.isAssignableFrom(propertyType)) {
                nodesToConvert.add((SimpleNodeType) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(property.getReadMethod());
                if (List.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    for (SimpleNodeType t : (List<SimpleNodeType>) value) nodesToConvert.add(t);
                } else if (Set.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    for (SimpleNodeType t : (Set<SimpleNodeType>) value) nodesToConvert.add(t);
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else {
                throw new IllegalStateException("invalid type");
            }
        }
        for (SimpleNodeType t : nodesToConvert) internalConvertBeanToNode(partition, newNodeEntry, session, t);
    }

    private <T> void fillStreamProperties(STStorageSession session, T bean, List<PropertyDescriptor> streamPropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        for (PropertyDescriptor property : streamPropertiesDescriptor) {
            Class<?> propertyType = property.getPropertyType();
            Object value = property.getReadMethod().invoke(bean);
            if (InputStream.class.isAssignableFrom(propertyType)) {
                newNodeEntry.getVerifiedOperations().setInputStreamProperty(session, property.getName(), (InputStream) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(property.getReadMethod());
                if (List.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    newNodeEntry.getVerifiedOperations().setSerializedListProperty(session, property.getName(),
                            (Class<? extends Serializable>) methodInformation.getItemType(), (List<? extends Serializable>) value);
                } else if (Set.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    newNodeEntry.getVerifiedOperations().setSerializedSetProperty(session, property.getName(),
                            (Class<? extends Serializable>) methodInformation.getItemType(), (Set<? extends Serializable>) value);
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else if (Map.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation = unwrapMapFromMethodReturn(property.getReadMethod());
                newNodeEntry.getVerifiedOperations().setSerializedMapProperty(session, property.getName(),
                        (Class<? extends Serializable>) methodInformation.getItemType().getK1(),
                        (Class<? extends Serializable>) methodInformation.getItemType().getK2(),
                        (Map<? extends Serializable, ? extends Serializable>) value);

            } else if (Serializable.class.isAssignableFrom(propertyType)) {
                newNodeEntry.getVerifiedOperations().setSimpleProperty(session, property.getName(),
                        (Class<? extends Serializable>) property.getPropertyType(), (Serializable) value);
            } else {
                throw new IllegalStateException("invalid type");
            }

        }
    }

    private <T> void fillSimpleProperties(STStorageSession session, T bean, List<PropertyDescriptor> simplePropertiesDescriptor, STNodeEntry newNodeEntry) throws IllegalAccessException, InvocationTargetException {
        for (PropertyDescriptor property : simplePropertiesDescriptor) {
            newNodeEntry.getVerifiedOperations().setSimpleProperty(session, property.getName(),
                    (Class<? extends Serializable>) property.getPropertyType(), (Serializable) property.getReadMethod().invoke(bean));
        }
    }

    private <T> STNodeEntry createNewNode(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, T bean, List<PropertyDescriptor> keyPropertiesDescriptor, Wrapper<PropertyDescriptor> parentPropertiesDescriptor) throws Exception{
        String name = internalGetNodeName(bean);
        STNodeEntryFactory.STNodeEntryBuilder builder = session.withPartition(partition).createWithName(name);

        if (parentNodeN != null) {
            builder.withParent(parentNodeN);
        } else {
            STUniqueKey parentKey = buildParentKey(partition,session,bean,parentPropertiesDescriptor);
            if (parentKey != null) builder.withParentKey(parentKey);
        }
        for (PropertyDescriptor descriptor : keyPropertiesDescriptor) {
            builder.withKey(descriptor.getName(), (Class<? extends Serializable>) descriptor.getPropertyType(),
                    (Serializable) descriptor.getReadMethod().invoke(bean));
        }
        return builder.andCreate();
    }

    private <T> STUniqueKey buildParentKey(STPartition partition, STStorageSession session, T bean, Wrapper<PropertyDescriptor> parentPropertiesDescriptor) throws Exception {
        if (parentPropertiesDescriptor.getWrapped() == null) return null;
        Object parent = parentPropertiesDescriptor.getWrapped().getReadMethod().invoke(bean);
        STStorageSession.STUniqueKeyBuilder builder = session.withPartition(partition).createKey(internalGetNodeName(parent));
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
                    }
                }
            }
            if (!parentChanged) break;
            if (parent != null) {
                builder.withParent(internalGetNodeName(parent));
            }
        }
        return builder.andCreate();
    }

    private <T> void fillDescriptors(T bean, Class<? extends Object> beanType, List<PropertyDescriptor> simplePropertiesDescriptor, List<PropertyDescriptor> keyPropertiesDescriptor,
                                     Wrapper<PropertyDescriptor> parentPropertiesDescriptor, List<PropertyDescriptor> childrenPropertiesDescriptor,
                                     List<PropertyDescriptor> streamPropertiesDescriptor) throws Exception {
        for (PropertyDescriptor descriptor : getPropertyDescriptors(beanType)) {
            Method readMethod = descriptor.getReadMethod();
            if (readMethod.isAnnotationPresent(TransientProperty.class)) continue;
            Class<?> returnType = readMethod.getReturnType();
            if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                if (parentPropertiesDescriptor.getWrapped() != null)
                    throw new IllegalStateException("only one parent property is allowed");
                if (readMethod.invoke(bean) != null) parentPropertiesDescriptor.setWrapped(descriptor);
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

    public <T> T convertNodeToBean(STStorageSession session, STNodeEntry nodes) throws Exception {
        return null;
    }

    public <T> Set<T> findNsByProperties(STPartition partition, STStorageSession session, STNodeEntry parentNodeN, Class<T> nodeType,
                                         String[] propertyNames, Object[] propertyValues) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
