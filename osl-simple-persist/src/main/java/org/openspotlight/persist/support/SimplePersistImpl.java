package org.openspotlight.persist.support;

import org.openspotlight.common.util.Reflection;
import org.openspotlight.persist.annotation.*;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryFactory;
import org.openspotlight.storage.domain.node.STProperty;

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
        List<PropertyDescriptor> parentPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> childrenPropertiesDescriptor = newLinkedList();
        List<PropertyDescriptor> streamPropertiesDescriptor = newLinkedList();
        fillDescriptors(bean.getClass(), simplePropertiesDescriptor, keyPropertiesDescriptor, parentPropertiesDescriptor, childrenPropertiesDescriptor, streamPropertiesDescriptor);
        STNodeEntry newNodeEntry = createNewNode(partition, parentNodeN, session, bean, keyPropertiesDescriptor);
        for(PropertyDescriptor property: simplePropertiesDescriptor){
                    newNodeEntry.getVerifiedOperations().setSimpleProperty(session,property.getName(),
                            (Class<? extends Serializable>)property.getPropertyType(),(Serializable)property.getReadMethod().invoke(bean));
                }
        for(PropertyDescriptor property: streamPropertiesDescriptor){

            newNodeEntry.getVerifiedOperations().setSimpleProperty(session,property.getName(),
                            (Class<? extends Serializable>)property.getPropertyType(),(Serializable)property.getReadMethod().invoke(bean));
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private <T> STNodeEntry createNewNode(STPartition partition, STNodeEntry parentNodeN, STStorageSession session, T bean, List<PropertyDescriptor> keyPropertiesDescriptor) throws IllegalAccessException, InvocationTargetException {
        String name = internalGetNodeName(bean);
        STNodeEntryFactory.STNodeEntryBuilder builder = session.withPartition(partition).createWithName(name);

        if(parentNodeN!=null){
            builder.withParent(parentNodeN);
        }
        for (PropertyDescriptor descriptor : keyPropertiesDescriptor) {
            builder.withKey(descriptor.getName(),(Class<? extends Serializable>)descriptor.getPropertyType(),
                    (Serializable)descriptor.getReadMethod().invoke(bean));
        }
        STNodeEntry newNodeEntry = builder.andCreate();
        return newNodeEntry;
    }

    private <T> void fillDescriptors(Class<T> beanType, List<PropertyDescriptor> simplePropertiesDescriptor, List<PropertyDescriptor> keyPropertiesDescriptor, List<PropertyDescriptor> parentPropertiesDescriptor, List<PropertyDescriptor> childrenPropertiesDescriptor, List<PropertyDescriptor> streamPropertiesDescriptor) throws Exception {
        for (PropertyDescriptor descriptor : getPropertyDescriptors(beanType)) {
            Method readMethod = descriptor.getReadMethod();
            Class<?> returnType = readMethod.getReturnType();
            if (readMethod.isAnnotationPresent(ParentProperty.class)) {
                parentPropertiesDescriptor.add(descriptor);
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
