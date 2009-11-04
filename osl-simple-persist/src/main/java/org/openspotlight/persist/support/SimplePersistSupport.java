package org.openspotlight.persist.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

public class SimplePersistSupport {

    private static class BeanDescriptor {

        private String                    nodeName;

        private BeanDescriptor            parent;

        private final Map<String, String> properties = new HashMap<String, String>();
    }

    private static final String defaultPrefix = "internal.";
    private static final String typeName      = "internal.typeName";
    private static final String hashValue     = "internal.hashValue";
    private static final String propertyValue = "internal.property.{0}.value";
    private static final String propertyType  = "internal.property.{0}.type";
    private static final String keyValue      = "internal.key.{0}.value";
    private static final String keyType       = "internal.key.{0}.type";

    private static Node addOrCreateJcrNode( final Session session,
                                            final Node parentNode,
                                            final BeanDescriptor itObj ) throws RepositoryException {
        try {
            final NodeIterator it = parentNode.getNodes(itObj.nodeName);
            while (it.hasNext()) {
                final Node nextNode = it.nextNode();
                final String hashProperty = nextNode.getProperty(hashValue).getString();
                final String expectedHash = itObj.properties.get(hashValue);
                if (expectedHash.equals(hashProperty)) {
                    for (final Map.Entry<String, String> entry : itObj.properties.entrySet()) {
                        nextNode.setProperty(entry.getKey(), entry.getValue());
                    }
                    return nextNode;
                }

            }
        } catch (final PathNotFoundException e) {

        }
        final Node newNode = parentNode.addNode(itObj.nodeName);
        for (final Map.Entry<String, String> entry : itObj.properties.entrySet()) {
            newNode.setProperty(entry.getKey(), entry.getValue());
        }
        return newNode;
    }

    public static <T> Node convertBeanToJcr( final Session session,
                                             final T bean ) {
        Assertions.checkCondition("correctInstance", bean instanceof SimpleNodeType);
        Assertions.checkNotNull("session", session);
        try {
            BeanDescriptor descriptor = createDescriptorFromBean(bean);
            final LinkedList<BeanDescriptor> list = new LinkedList<BeanDescriptor>();
            while (descriptor != null) {
                list.addFirst(descriptor);
                descriptor = descriptor.parent;
            }
            Node parentNode = null;
            for (final BeanDescriptor itObj : list) {
                if (parentNode == null) {
                    try {
                        parentNode = session.getRootNode().getNode(SharedConstants.DEFAULT_JCR_ROOT_NAME);
                    } catch (final PathNotFoundException e) {
                        parentNode = session.getRootNode().addNode(SharedConstants.DEFAULT_JCR_ROOT_NAME);
                    }
                }
                final Node node = addOrCreateJcrNode(session, parentNode, itObj);
                parentNode = node;
            }
            return parentNode;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public static <T> T convertJcrToBean( final Session session,
                                          final Node jcrNode ) throws Exception {
        Assertions.checkNotNull("session", session);
        Assertions.checkNotNull("jcrNode", jcrNode);

        try {

            if (!jcrNode.hasProperty(hashValue)) {
                return null;//not a simplePersist node
            }
            final LinkedList<Node> list = new LinkedList<Node>();
            Node parentNode = jcrNode;
            while (parentNode != null && parentNode.hasProperty(hashValue)) {
                list.addFirst(parentNode);
                parentNode = parentNode.getParent();
            }
            Object parent = null;
            BeanDescriptor parentDescriptor = null;
            for (final Node node : list) {
                parentDescriptor = createDescriptorFromJcr(node, parentDescriptor);
                parent = createBeanFromBeanDescriptor(parentDescriptor, parent);
            }
            Assertions.checkCondition("correctInstance", parent instanceof SimpleNodeType);

            return (T)parent;

        } catch (final Exception e) {
            throw Exceptions.logAndReturn(e);
        }
    }

    private static <T> T createBeanFromBeanDescriptor( final BeanDescriptor beanDescriptor,
                                                       final Object parent ) throws Exception {

        final Class<T> typeClass = (Class<T>)Class.forName(beanDescriptor.properties.get(typeName));
        final T newObject = typeClass.newInstance();
        final PropertyDescriptor[] allProperties = PropertyUtils.getPropertyDescriptors(newObject);
        for (final PropertyDescriptor desc : allProperties) {
            if (desc.getName().equals("class")) {
                continue;
            }
            if (desc.getReadMethod().isAnnotationPresent(TransientProperty.class)) {

                continue;
            }
            if (desc.getReadMethod().isAnnotationPresent(ParentProperty.class)) {
                if (parent == null) {
                    continue;
                }
                if (desc.getPropertyType().isInstance(parent)) {
                    desc.getWriteMethod().invoke(newObject, parent);

                }
                continue;
            }
            final String propertyName = desc.getName();

            if (desc.getReadMethod().isAnnotationPresent(KeyProperty.class)) {
                setPropertyFromDescriptorToBean(beanDescriptor, newObject, desc, propertyName, keyType, keyValue);
            } else {
                setPropertyFromDescriptorToBean(beanDescriptor, newObject, desc, propertyName, propertyType, propertyValue);
            }

        }

        return newObject;
    }

    private static <T> BeanDescriptor createDescriptorFromBean( final T bean ) throws Exception {
        final BeanDescriptor descriptor = new BeanDescriptor();
        descriptor.nodeName = bean.getClass().getName().replaceAll("[.]", "_").replaceAll("[$]", "_");
        final String beanTypeName = bean.getClass().getName();
        final List<String> attributesToHash = new ArrayList<String>();
        descriptor.properties.put(typeName, beanTypeName);
        final PropertyDescriptor[] allProperties = PropertyUtils.getPropertyDescriptors(bean);
        for (final PropertyDescriptor desc : allProperties) {
            if (desc.getName().equals("class")) {
                continue;
            }
            if (desc.getReadMethod().isAnnotationPresent(TransientProperty.class)) {

                continue;
            }
            if (desc.getReadMethod().isAnnotationPresent(ParentProperty.class)) {
                final Object parent = desc.getReadMethod().invoke(bean);
                if (parent != null && descriptor.parent != null) {
                    throw Exceptions.logAndReturn(new IllegalStateException(
                                                                            MessageFormat.format(
                                                                                                 "Bean {0} of class {1} with more than one parent. Recheck the annotations",
                                                                                                 bean, bean.getClass())));

                }
                final BeanDescriptor parentDescriptor = createDescriptorFromBean(parent);
                descriptor.parent = parentDescriptor;
                continue;
            }

            if (desc.getReadMethod().isAnnotationPresent(KeyProperty.class)) {
                setPropertyFromBeanToDescriptor(bean, descriptor, desc, keyType, keyValue);
                attributesToHash.add(MessageFormat.format(keyValue, desc.getName()));
            } else {
                setPropertyFromBeanToDescriptor(bean, descriptor, desc, propertyType, propertyValue);
            }

        }
        final StringBuilder hashBuffer = new StringBuilder();
        hashBuffer.append(bean.getClass().getName());
        hashBuffer.append(';');
        Collections.sort(attributesToHash);
        if (descriptor.parent != null) {
            hashBuffer.append(descriptor.parent.properties.get(hashBuffer));
            hashBuffer.append(';');
        }
        for (final String keyProp : attributesToHash) {
            hashBuffer.append(descriptor.properties.get(keyProp));
            hashBuffer.append(';');
        }
        final String hash = UUID.nameUUIDFromBytes(hashBuffer.toString().getBytes()).toString();
        descriptor.properties.put(SimplePersistSupport.hashValue, hash);
        return descriptor;
    }

    private static BeanDescriptor createDescriptorFromJcr( final Node jcrNode,
                                                           final BeanDescriptor parent ) throws Exception {
        final BeanDescriptor descriptor = new BeanDescriptor();
        descriptor.parent = parent;
        descriptor.nodeName = jcrNode.getName();
        final PropertyIterator properties = jcrNode.getProperties();
        while (properties.hasNext()) {
            final Property property = properties.nextProperty();
            if (property.getName().startsWith(defaultPrefix)) {
                descriptor.properties.put(property.getName(), property.getValue().getString());

            }
        }

        return descriptor;

    }

    public static <T, C> Set<C> findChildren( final Session session,
                                              final T node ) {
        return null;
    }

    private static <T> void setPropertyFromBeanToDescriptor( final T bean,
                                                             final BeanDescriptor descriptor,
                                                             final PropertyDescriptor desc,
                                                             final String typeDescription,
                                                             final String valueDescription )
        throws IllegalAccessException, InvocationTargetException, SLException {
        final String propValue = MessageFormat.format(valueDescription, desc.getName());
        final String propType = MessageFormat.format(typeDescription, desc.getName());
        final Object value = desc.getReadMethod().invoke(bean);
        final String valueTypeString = desc.getPropertyType().getName();
        final String valueAsString = Conversion.convert(value, String.class);
        descriptor.properties.put(propValue, valueAsString);
        descriptor.properties.put(propType, valueTypeString);
    }

    private static <T> void setPropertyFromDescriptorToBean( final BeanDescriptor beanDescriptor,
                                                             final T newObject,
                                                             final PropertyDescriptor desc,
                                                             final String propertyName,
                                                             final String type,
                                                             final String value )
        throws ClassNotFoundException, SLException, IllegalAccessException, InvocationTargetException {
        final String propertyTypeString = beanDescriptor.properties.get(MessageFormat.format(type, propertyName));
        final String propertyValueAsString = beanDescriptor.properties.get(MessageFormat.format(value, propertyName));
        final Class<?> propertyType = Class.forName(propertyTypeString);
        final Object newPropertyValue = Conversion.convert(propertyValueAsString, propertyType);
        desc.getWriteMethod().invoke(newObject, newPropertyValue);
    }

}
