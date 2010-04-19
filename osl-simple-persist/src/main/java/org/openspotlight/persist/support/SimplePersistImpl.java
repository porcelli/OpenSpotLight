package org.openspotlight.persist.support;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.SerializationUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Wrapper;
import org.openspotlight.persist.annotation.*;
import org.openspotlight.persist.internal.LazyProperty;
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
import static java.text.MessageFormat.format;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Reflection.*;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

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
    private static final String SHA1_PROPERTY_NAME = "internal-{0}-sha1";

    private final STStorageSession currentSession;

    private final STPartition currentPartition;

    public SimplePersistImpl(STStorageSession currentSession, STPartition currentPartition) {
        this.currentSession = currentSession;
        this.currentPartition = currentPartition;
    }

    public STStorageSession getCurrentSession() {
        return currentSession;
    }

    public STPartition getCurrentPartition() {
        return currentPartition;
    }

    public <T> Iterable<STNodeEntry> convertBeansToNodes(STNodeEntry parent, Iterable<T> beans) throws Exception {
        List<STNodeEntry> itemsConverted = newArrayList();
        for (T bean : beans) itemsConverted.add(convertBeanToNode(parent, bean));
        return itemsConverted;
    }

    public <T> STNodeEntry convertBeanToNode(STNodeEntry parent, T bean) throws Exception {
        return internalConvertBeanToNode(parent, bean);
    }

    private <T> STNodeEntry internalConvertBeanToNode(STNodeEntry parent, T bean) throws Exception {
        ConversionToNodeContext context = new ConversionToNodeContext((SimpleNodeType) bean);
        internalConvertBeanToNode(context, null, null, parent);
        STNodeEntry result = context.nodeReference.getWrapped();
        checkNotNull("result", result);
        return result;
    }


    private <T> STNodeEntry internalConvertBeanToNode(ConversionToNodeContext context, String propertyName,
                                                      SimpleNodeType bean, STNodeEntry parentNode) throws Exception {
        boolean firstInvocation = bean == null;
        if (firstInvocation) {
            STNodeEntry currentParentNode = parentNode;
            Descriptors currentBeanDescriptors = Descriptors.fillDescriptors(context.bean);
            List<Pair<String, SimpleNodeType>> rootObjects = currentBeanDescriptors.getRootObjects();
            for (Pair<String, SimpleNodeType> pair : rootObjects) {
                currentParentNode = internalConvertBeanToNode(context, pair.getK1(), pair.getK2(), currentParentNode);
            }
            context.allNodes.removeAll(context.nodesConverted.values());
            for (STNodeEntry unusedNode : context.allNodes) {
                currentSession.removeNode(unusedNode);
            }
            return context.nodeReference.getWrapped();
        } else {
            STNodeEntry cached = context.nodesConverted.get(bean);
            if (cached == null) {
                Descriptors parentDescriptors = Descriptors.fillDescriptors(bean);
                cached = createNewNode(context, parentNode, parentDescriptors, propertyName);
                context.nodesConverted.put(bean, cached);
                fillNodeSimpleProperties(bean, parentDescriptors.simplePropertiesDescriptor, cached);
                fillNodeStreamProperties(bean, parentDescriptors.streamPropertiesDescriptor, cached);
                fillNodeChildrenProperties(context, bean, parentDescriptors.childrenPropertiesDescriptor, cached);
                fillNodeLazyProperties(context, bean, parentDescriptors.lazyPropertiesDescriptor, cached);
                if (bean == context.bean) context.nodeReference.setWrapped(cached);
            }
            return cached;
        }

    }

    private void fillNodeLazyProperties(ConversionToNodeContext context, SimpleNodeType bean,
                                        List<PropertyDescriptor> lazyPropertiesDescriptor, STNodeEntry nodeEntry) throws Exception {
        for (PropertyDescriptor descriptor : lazyPropertiesDescriptor) {
            LazyProperty<?> property = (LazyProperty<?>) descriptor.getReadMethod().invoke(bean);
            if (property != null && property.getMetadata().needsSave()) {
                String propertyName = descriptor.getName();
                Object value = property.getMetadata().getTransient();
                if (value instanceof InputStream) {
                    nodeEntry.getVerifiedOperations().setInputStreamProperty(currentSession, propertyName, (InputStream) value);
                } else {//Serializable
                    nodeEntry.getVerifiedOperations().setSerializedPojoProperty(currentSession, propertyName,
                            (Class<? super Serializable>) property.getMetadata().getPropertyType(), (Serializable) value);
                }
                nodeEntry.getVerifiedOperations().setSimpleProperty(currentSession, format(SHA1_PROPERTY_NAME, propertyName), String.class, property.getMetadata().getSha1());
                property.getMetadata().markAsSaved();
            }

            ;
        }

    }


    private <T> void fillNodeChildrenProperties(ConversionToNodeContext context, T bean,
                                                List<PropertyDescriptor> childrenPropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {

        Map<String, BeanToNodeChildData> nodesToConvert = newHashMap();
        for (PropertyDescriptor property : childrenPropertiesDescriptor) {
            String propertyName = property.getName();
            BeanToNodeChildData data = nodesToConvert.get(propertyName);
            Class<?> propertyType = property.getPropertyType();

            Object value = property.getReadMethod().invoke(bean);
            if (SimpleNodeType.class.isAssignableFrom(propertyType)) {
                if (data == null) {
                    data = new BeanToNodeChildData(propertyName, Collection.class.isAssignableFrom(propertyType), propertyType);
                    nodesToConvert.put(propertyName, data);
                }
                data.childrenToSave.add((SimpleNodeType) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation =
                        unwrapCollectionFromMethodReturn(property.getReadMethod());
                if (data == null) {
                    data = new BeanToNodeChildData(propertyName, Collection.class.isAssignableFrom(propertyType), methodInformation.getItemType());
                    nodesToConvert.put(propertyName, data);
                }
                if (List.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    for (SimpleNodeType t : (List<SimpleNodeType>) value)
                        data.childrenToSave.add(t);
                } else if (Set.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    for (SimpleNodeType t : (Set<SimpleNodeType>) value)
                        data.childrenToSave.add(t);
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else {
                throw new IllegalStateException("invalid type:" + property.getPropertyType());
            }
        }
        for (BeanToNodeChildData data : nodesToConvert.values()) {
            if (!data.multiple && data.childrenToSave.size() > 1)
                throw new IllegalStateException("single property with more than one child");
            for (SimpleNodeType beanBeenSaved : data.childrenToSave) {
                internalConvertBeanToNode(context, data.propertyName, beanBeenSaved, newNodeEntry);
            }
            context.allNodes.addAll(newNodeEntry.getChildrenNamed(currentSession, internalGetNodeName(data.nodeType)));
        }

    }

    private <T> void fillNodeStreamProperties(T bean, List<PropertyDescriptor> streamPropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        for (PropertyDescriptor property : streamPropertiesDescriptor) {
            Class<?> propertyType = property.getPropertyType();
            Method readMethod = property.getReadMethod();
            Object value = readMethod.invoke(bean);
            if (InputStream.class.isAssignableFrom(propertyType)) {
                newNodeEntry.getVerifiedOperations().setInputStreamProperty(currentSession, property.getName(), (InputStream) value);
            } else if (Collection.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodInformation = unwrapCollectionFromMethodReturn(property.getReadMethod());
                if (List.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    newNodeEntry.getVerifiedOperations().setSerializedListProperty(currentSession, property.getName(),
                            methodInformation.getItemType(), beforeSerializeList((List<? extends Serializable>) value, readMethod));
                } else if (Set.class.isAssignableFrom(methodInformation.getCollectionType())) {
                    newNodeEntry.getVerifiedOperations().setSerializedSetProperty(currentSession, property.getName(),
                            methodInformation.getItemType(), beforeSerializeSet((Set<? extends Serializable>) value, readMethod));
                } else {
                    throw new IllegalStateException("invalid collection type");
                }

            } else if (Map.class.isAssignableFrom(propertyType)) {
                Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodInformation = unwrapMapFromMethodReturn(property.getReadMethod());
                newNodeEntry.getVerifiedOperations().setSerializedMapProperty(currentSession, property.getName(),
                        methodInformation.getItemType().getK1(),
                        methodInformation.getItemType().getK2(),
                        beforeSerializeMap((Map<? extends Serializable, ? extends Serializable>) value, readMethod));

            } else if (Serializable.class.isAssignableFrom(propertyType)) {
                if (propertyType.equals(String.class) || Number.class.isAssignableFrom(propertyType)
                        || propertyType.isPrimitive() || Boolean.class.equals(propertyType)
                        || Character.class.equals(propertyType) || Date.class.equals(propertyType)) {
                    newNodeEntry.getVerifiedOperations().setSimpleProperty(currentSession, property.getName(),
                            (Class<? super Serializable>) property.getPropertyType(), beforeSerializeSerializable((Serializable) value));
                } else {
                    newNodeEntry.getVerifiedOperations().setSerializedPojoProperty(currentSession, property.getName(),
                            (Class<? super Serializable>) property.getPropertyType(), beforeSerializeSerializable((Serializable) value));
                }


            } else {
                throw new IllegalStateException("invalid type");
            }

        }
    }

    private <T> void fillNodeSimpleProperties(T bean,
                                              List<PropertyDescriptor> simplePropertiesDescriptor, STNodeEntry newNodeEntry) throws Exception {
        for (PropertyDescriptor property : simplePropertiesDescriptor) {
            newNodeEntry.getVerifiedOperations().setSimpleProperty(currentSession, property.getName(),
                    (Class<? super Serializable>) property.getPropertyType(), (Serializable) property.getReadMethod().invoke(bean));
        }
    }

    private <T> STNodeEntry createNewNode(ConversionToNodeContext context, STNodeEntry parentNode,
                                          Descriptors descriptors, String propertyName) throws Exception {
        String name = internalGetNodeName(descriptors.bean);
        STNodeEntryFactory.STNodeEntryBuilder builder = currentSession.withPartition(currentPartition).createWithName(name);

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
        newNode.getVerifiedOperations().setSimpleProperty(currentSession, NODE_ENTRY_TYPE, String.class, name);
        return newNode;
    }

    public <T> Iterable<T> convertNodesToBeans(Iterable<STNodeEntry> nodes) throws Exception {
        return internalConvertNodesToBeans(nodes);
    }

    private <T> List<T> internalConvertNodesToBeans(Iterable<STNodeEntry> nodes) throws Exception {
        List<T> itemsConverted = newArrayList();
        for (STNodeEntry node : nodes) itemsConverted.add(this.<T>convertNodeToBean(node));
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

    public <T> T convertNodeToBean(STNodeEntry node) throws Exception {
        T bean = (T) this.internalConvertNodeToBean(new ConversionToBeanContext(node), null, null);
        return bean;
    }

    private Object internalConvertNodeToBean(ConversionToBeanContext context, STNodeEntry nodeEntry, Object beanParent) throws Exception {

        if (nodeEntry == null) {
            List<STNodeEntry> parents = newLinkedList();
            STNodeEntry currentParent = context.node;
            while (currentParent != null && isSimpleNode(currentParent)) {
                parents.add(currentParent);
                currentParent = currentParent.getParent(currentSession);
            }
            reverse(parents);
            Object currentParentAsBean = null;
            for (STNodeEntry parentEntry : parents) {
                currentParentAsBean = this.internalConvertNodeToBean(context, parentEntry, currentParentAsBean);
            }
            return context.beanReference.getWrapped();
        } else {
            Object cached = context.beansConverted.get(nodeEntry);
            if (cached == null) {
                Class<?> beanType = findClassFromNode(nodeEntry);
                cached = beanType.newInstance();
                context.beansConverted.put(nodeEntry, cached);
                Descriptors descriptors = Descriptors.fillDescriptors(beanType);
                fillBeanParent(descriptors.parentPropertiesDescriptor, cached, beanParent);
                fillBeanSimpleProperties(nodeEntry, cached, descriptors.keyPropertiesDescriptor);
                fillBeanSimpleProperties(nodeEntry, cached, descriptors.simplePropertiesDescriptor);
                fillBeanStreamProperties(nodeEntry, cached, descriptors.streamPropertiesDescriptor);
                fillBeanLazyProperties(nodeEntry, cached, descriptors.lazyPropertiesDescriptor);
                fillBeanChildren(context, nodeEntry, cached, descriptors.childrenPropertiesDescriptor);
                if (nodeEntry.equals(context.node)) context.beanReference.setWrapped((SimpleNodeType) cached);
            }
            return cached;
        }

    }

    private boolean isSimpleNode(STNodeEntry currentParent) {
        return currentParent.getPropertyValue(currentSession,NODE_ENTRY_TYPE)!=null;
    }

    private Class<?> findClassFromNode(STNodeEntry nodeEntry) throws Exception{
        return forName(nodeEntry.getNodeEntryName());
    }

    private void fillBeanLazyProperties(STNodeEntry cached, Object bean, List<PropertyDescriptor> lazyPropertiesDescriptors) throws Exception {
        for (PropertyDescriptor property : lazyPropertiesDescriptors) {

            String propertyName = property.getName();
            LazyProperty<?> lazyProperty = (LazyProperty<?>) property.getReadMethod().invoke(bean);
            lazyProperty.getMetadata().setParentKey(cached.getUniqueKey());
            lazyProperty.getMetadata().setPropertyName(propertyName);
            String sha1 = cached.getPropertyValue(currentSession, format(SHA1_PROPERTY_NAME, propertyName));
            lazyProperty.getMetadata().internalSetSha1(sha1);
        }

    }

    private <T> void fillBeanChildren(ConversionToBeanContext context, STNodeEntry node, T bean, List<PropertyDescriptor> childrenPropertiesDescriptor) throws Exception {
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
            String childrenName = internalGetNodeName(nodeType);
            Set<STNodeEntry> children = node.getChildrenNamed(currentSession, childrenName);
            children = filterChildrenWithProperty(children, descriptor.getName());
            List<Object> childrenAsBeans = newLinkedList();
            if ((!isMultiple) && children.size() > 1)
                throw new IllegalStateException("more than one child on a unique property");
            for (STNodeEntry child : children) {
                childrenAsBeans.add(internalConvertNodeToBean(context, child, bean));
            }
            if (isMultiple) {
                Collection c = (Collection) readMethod.invoke(bean);
                for (Object o : childrenAsBeans) {
                    c.add(o);
                }
                if (Comparable.class.isAssignableFrom(nodeType) && c instanceof List) {
                    sort((List) c);
                }
            } else if (childrenAsBeans.size() > 0) {
                Object value = childrenAsBeans.iterator().next();
                descriptor.getWriteMethod().invoke(bean, value);
            }
        }
    }

    private Set<STNodeEntry> filterChildrenWithProperty(Set<STNodeEntry> children, String name) {
        if (name == null) return children;
        Set<STNodeEntry> filtered = newHashSet();
        for (STNodeEntry e : children) {
            Object propertyValue = e.getPropertyValue(currentSession, NODE_PROPERTY_NAME);
            if (name.equals(propertyValue)) filtered.add(e);
        }
        return filtered;
    }

    private <T> void fillBeanStreamProperties(STNodeEntry node, T bean,
                                              List<PropertyDescriptor> streamPropertiesDescriptor) throws Exception {
        for (PropertyDescriptor descriptor : streamPropertiesDescriptor) {
            Class<?> propertyType = descriptor.getPropertyType();
            if (InputStream.class.isAssignableFrom(propertyType)) {
                InputStream value = node.getPropertyValue(currentSession, descriptor.getName());
                descriptor.getWriteMethod().invoke(bean, value);
            } else if (Serializable.class.isAssignableFrom(propertyType) || Collection.class.isAssignableFrom(propertyType)
                    || Map.class.isAssignableFrom(propertyType)) {
                Serializable value = node.getPropertyValue(currentSession, descriptor.getName());
                descriptor.getWriteMethod().invoke(bean, getInternalMethods().beforeUnConvert((SimpleNodeType) bean, value, descriptor.getReadMethod()));
            } else {
                throw new IllegalStateException("wrong type");
            }
        }
    }


    private <T> void fillBeanSimpleProperties(STNodeEntry node, T bean,
                                              List<PropertyDescriptor> simplePropertiesDescriptor) throws IllegalAccessException, InvocationTargetException {
        for (PropertyDescriptor descriptor : simplePropertiesDescriptor) {
            Object value = node.<Object>getPropertyValue(currentSession, descriptor.getName());
            if (value == null && descriptor.getPropertyType().isPrimitive()) continue;
            descriptor.getWriteMethod().invoke(bean, value);
        }
    }

    private void fillBeanParent(List<PropertyDescriptor> parentPropertyDescriptors, Object bean, Object beanParent) throws Exception {
        if (beanParent != null) {
            Class<?> parentType = beanParent.getClass();
            for (PropertyDescriptor descriptor : parentPropertyDescriptors) {
                if (descriptor.getPropertyType().isAssignableFrom(parentType)) {
                    descriptor.getWriteMethod().invoke(bean, beanParent);
                    break;
                }
            }
        }
    }

    private final InternalMethods internalMethods = new InternalMethodsImpl();

    public InternalMethods getInternalMethods() {
        return internalMethods;

    }

    private class InternalMethodsImpl implements InternalMethods {
        public Object beforeUnConvert(SimpleNodeType bean, Serializable value, Method readMethod) throws Exception {
            if (value instanceof Collection) {
                boolean mayBeStreamPropertyWithParent = true;
                if (readMethod != null) {
                    Reflection.UnwrappedCollectionTypeFromMethodReturn<Object> methodDescription
                            = unwrapCollectionFromMethodReturn(readMethod);
                    mayBeStreamPropertyWithParent = StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType());
                }
                if (mayBeStreamPropertyWithParent) {
                    Collection valueAsCollection = (Collection) value;
                    for (Object o : valueAsCollection) {
                        if (o instanceof StreamPropertyWithParent) {
                            ((StreamPropertyWithParent) o).setParent(bean);
                        }
                    }
                }
            } else if (value instanceof Map) {
                boolean mayBeStreamPropertyWithParent = true;
                if (readMethod != null) {
                    Reflection.UnwrappedMapTypeFromMethodReturn<Object, Object> methodDescription
                            = unwrapMapFromMethodReturn(readMethod);
                    mayBeStreamPropertyWithParent = StreamPropertyWithParent.class.isAssignableFrom(methodDescription.getItemType().getK2());

                }

                if (mayBeStreamPropertyWithParent) {
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

    public <T> Iterable<T> findByProperties(STNodeEntry parent, Class<T> beanType,
                                            String[] propertyNames, Object[] propertyValues) throws Exception {
        checkNotNull("currentPartition", currentPartition);
        checkNotNull("currentSession", currentSession);
        checkNotNull("beanType", beanType);
        checkNotNull("propertyNames", propertyNames);
        checkNotNull("propertyValues", propertyValues);
        checkCondition("namesAndValues:sameSize", propertyNames.length == propertyValues.length);

        STStorageSession.STCriteriaBuilder builder = currentSession.withPartition(currentPartition).createCriteria()
                .withProperty(NODE_ENTRY_TYPE).equals(String.class, internalGetNodeName(beanType));
        Object dummyInstance = beanType.newInstance();
        for (int i = 0, size = propertyNames.length; i < size; i++) {

            PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(dummyInstance, propertyNames[i]);
            if (descriptor == null) throw new SLRuntimeException("invalid property:" + propertyNames[i]);
            builder.withProperty(propertyNames[i]).equals((Class<? extends Serializable>)
                    findClassWithoutPrimitives(descriptor.getPropertyType()), (Serializable) propertyValues[i]);
        }
        Set<STNodeEntry> foundItems = builder.buildCriteria().andFind(currentSession);
        if (parent != null) {
            Set<STNodeEntry> children = newHashSet();
            for (STNodeEntry n : foundItems) {
                if (n.isChildOf(parent)) children.add(n);
            }
            foundItems = children;
        }


        List<T> result = this.<T>internalConvertNodesToBeans(foundItems);

        if (Comparable.class.isAssignableFrom(beanType)) {
            sort((List<Comparable<? super Comparable<?>>>) result);
        }
        return result;
    }


    public <T> T findUniqueByProperties(STNodeEntry parent, Class<T> beanType, String[] propertyNames, Object[] propertyValues) throws Exception {
        Iterable<T> result = findByProperties(parent, beanType, propertyNames, propertyValues);
        Iterator<T> it = result.iterator();
        return it.hasNext() ? it.next() : null;
    }

    public <T> Iterable<STNodeEntry> convertBeansToNodes(Iterable<T> beans) throws Exception {
        return convertBeansToNodes(null, beans);
    }

    public <T> STNodeEntry convertBeanToNode(T bean) throws Exception {
        return convertBeanToNode(null, bean);
    }

    public <T> Iterable<T> findByProperties(Class<T> beanType, String[] propertyNames, Object[] propertyValues) throws Exception {
        return findByProperties(null, beanType, propertyNames, propertyValues);
    }

    public <T> T findUniqueByProperties(Class<T> beanType, String[] propertyNames, Object[] propertyValues) throws Exception {
        return findUniqueByProperties(null, beanType, propertyNames, propertyValues);
    }

    private static class Descriptors {
        private Descriptors(Class<?> beanType, SimpleNodeType bean,
                            Wrapper<PropertyDescriptor> parentPropertyDescriptor, List<PropertyDescriptor> simplePropertiesDescriptor,
                            List<PropertyDescriptor> keyPropertiesDescriptor, List<PropertyDescriptor> streamPropertiesDescriptor,
                            List<PropertyDescriptor> childrenPropertiesDescriptor, List<PropertyDescriptor> parentPropertiesDescriptor,
                            List<PropertyDescriptor> lazyPropertiesDescriptor) {
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
        final Class<?> beanType;
        final SimpleNodeType bean;
        final Wrapper<PropertyDescriptor> parentPropertyDescriptor;
        final List<PropertyDescriptor> simplePropertiesDescriptor;
        final List<PropertyDescriptor> keyPropertiesDescriptor;
        final List<PropertyDescriptor> streamPropertiesDescriptor;
        final List<PropertyDescriptor> childrenPropertiesDescriptor;
        final List<PropertyDescriptor> parentPropertiesDescriptor;
        final List<PropertyDescriptor> lazyPropertiesDescriptor;

        public static Descriptors createMutable(Class<?> beanType, Object bean) {
            return new Descriptors(beanType, (SimpleNodeType) bean, Wrapper.<PropertyDescriptor>createMutable(),
                    Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList(),
                    Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList(), Lists.<PropertyDescriptor>newLinkedList());
        }

        public static Descriptors createImmutableFrom(Descriptors from) {
            return new Descriptors(from.beanType, from.bean, Wrapper.<PropertyDescriptor>createImmutable(from.parentPropertyDescriptor.getWrapped()),
                    ImmutableList.copyOf(from.simplePropertiesDescriptor), ImmutableList.copyOf(from.keyPropertiesDescriptor),
                    ImmutableList.copyOf(from.streamPropertiesDescriptor), ImmutableList.copyOf(from.childrenPropertiesDescriptor),
                    ImmutableList.copyOf(from.parentPropertiesDescriptor), ImmutableList.copyOf(from.lazyPropertiesDescriptor));
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
                } else if (readMethod.isAnnotationPresent(KeyProperty.class)) {
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
                } else if (LazyProperty.class.isAssignableFrom(returnType)) {
                    descriptors.lazyPropertiesDescriptor.add(descriptor);
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
        ConversionToNodeContext(SimpleNodeType bean) {
            this.bean = bean;
        }

        final Set<STNodeEntry> allNodes = newHashSet();
        final Wrapper<STNodeEntry> nodeReference = Wrapper.createMutable();
        final SimpleNodeType bean;
        final Map<Object, STNodeEntry> nodesConverted = newHashMap();

    }

    private static class ConversionToBeanContext {
        private ConversionToBeanContext(STNodeEntry node) {
            this.node = node;
        }

        final Wrapper<SimpleNodeType> beanReference = Wrapper.createMutable();
        final STNodeEntry node;
        final Map<STNodeEntry, Object> beansConverted = newHashMap();
    }

    private static class BeanToNodeChildData {

        private BeanToNodeChildData(String propertyName, boolean multiple, Class<?> nodeType) {
            this.propertyName = propertyName;
            this.nodeType = nodeType;
            this.childrenToSave = newHashSet();
            this.multiple = multiple;
        }

        final Class<?> nodeType;

        final String propertyName;

        final Collection<SimpleNodeType> childrenToSave;

        final boolean multiple;


    }


}
