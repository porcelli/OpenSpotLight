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
import java.util.Map.Entry;

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

/**
 * The Class SimplePersistSupport.
 */
public class SimplePersistSupport {

    //FIXME collectionOfSimpleProperties

    //FIXME collectionOfNodeProperties

    //FIXME mapOfSimpleProperties

    //FIXME mapOfNodeProperties

    //FIXME singleNodeProperty

    //FIXME testAddSimplePropertyOnCollection

    //FIXME testAddNodePropertyOnCollection

    //FIXME testAddSimplePropertyOnMap

    //FIXME testAddNodePropertyOnMap

    //FIXME testRemoveSimplePropertyOnCollection

    //FIXME testRemoveNodePropertyOnCollection

    //FIXME testRemoveSimplePropertyOnMap

    //FIXME testRemoveNodePropertyOnMap

    /**
     * The Class BeanDescriptor.
     */
    private static class BeanDescriptor {

        /** The node name. */
        String                            nodeName;

        /** The parent. */
        BeanDescriptor                    parent;

        /** The node properties. */
        Map<String, BeanDescriptor>       nodeProperties             = new HashMap<String, BeanDescriptor>();

        /** The collection of node properties. */
        Map<String, List<BeanDescriptor>> collectionOfNodeProperties = new HashMap<String, List<BeanDescriptor>>();

        /** The properties. */
        final Map<String, String>         properties                 = new HashMap<String, String>();
    }

    /**
     * The Enum JcrNodeType.
     */
    private static enum JcrNodeType {

        /** The NODE. */
        NODE,

        /** The NOD e_ property. */
        NODE_PROPERTY
    }

    /** The Constant defaultPrefix. */
    private static final String defaultNodePrefix = "node.";

    /** The Constant typeName. */
    private static final String typeName          = "node.typeName";

    /** The Constant nodePropertyName. */
    private static final String nodePropertyName  = "property.name";

    /** The Constant hashValue. */
    private static final String hashValue         = "node.hashValue";

    /** The Constant propertyValue. */
    private static final String propertyValue     = "node.property.{0}.value";

    /** The Constant propertyType. */
    private static final String propertyType      = "node.property.{0}.type";

    /** The Constant keyValue. */
    private static final String keyValue          = "node.key.{0}.value";

    /** The Constant keyType. */
    private static final String keyType           = "node.key.{0}.type";

    /**
     * Adds the or create jcr node.
     * 
     * @param session the session
     * @param parentNode the parent node
     * @param itObj the it obj
     * @param nodeType the node type
     * @param propertyName the property name
     * @return the node
     * @throws RepositoryException the repository exception
     */
    private static Node addOrCreateJcrNode( final JcrNodeType nodeType,
                                            final Session session,
                                            final Node parentNode,
                                            final BeanDescriptor itObj,
                                            final String propertyName ) throws RepositoryException {
        Node result = null;
        final String nodeName = nodeType.toString() + "_" + (propertyName == null ? itObj.nodeName : propertyName);
        if (propertyName != null) {
            try {
                result = parentNode.getNode(nodeName);
            } catch (final PathNotFoundException e) {
                //there's no property node yet
            }
            if (itObj == null) {
                if (result != null) {
                    result.remove();
                }
                return null;
            }
            if (result == null) {
                final Node newNode = parentNode.addNode(nodeName);
                result = newNode;
            }
            if (result != null) {
                result.setProperty(nodePropertyName, propertyName);
            }

        } else {
            try {

                if (result == null) {
                    final NodeIterator it = parentNode.getNodes(nodeName);
                    while (it.hasNext()) {
                        final Node nextNode = it.nextNode();
                        final String hashProperty = nextNode.getProperty(hashValue).getString();
                        final String expectedHash = itObj.properties.get(hashValue);
                        if (expectedHash.equals(hashProperty)) {

                            result = nextNode;
                            break;
                        }

                    }
                }
            } catch (final PathNotFoundException e) {
                //ok, nothing to do here
            }
            if (result == null) {
                final Node newNode = parentNode.addNode(nodeName);
                result = newNode;
            }
        }
        if (result != null) {
            for (final Map.Entry<String, String> entry : itObj.properties.entrySet()) {
                result.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Convert bean to jcr.
     * 
     * @param session the session
     * @param bean the bean
     * @return the node
     */
    public static <T> Node convertBeanToJcr( final Session session,
                                             final T bean ) {
        Assertions.checkCondition("correctInstance", bean instanceof SimpleNodeType);
        Assertions.checkNotNull("session", session);
        try {
            BeanDescriptor descriptor = createDescriptorFromBean(bean, null);
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
                final Node node = addOrCreateJcrNode(JcrNodeType.NODE, session, parentNode, itObj, null);
                for (final Entry<String, BeanDescriptor> entry : itObj.nodeProperties.entrySet()) {
                    addOrCreateJcrNode(JcrNodeType.NODE_PROPERTY, session, node, entry.getValue(), entry.getKey());
                }
                parentNode = node;
            }
            return parentNode;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * Convert jcr to bean.
     * 
     * @param session the session
     * @param jcrNode the jcr node
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings( "unchecked" )
    public static <T> T convertJcrToBean( final Session session,
                                          final Node jcrNode ) throws Exception {
        Assertions.checkNotNull("session", session);
        Assertions.checkNotNull("jcrNode", jcrNode);

        try {

            if (!jcrNode.getName().startsWith(JcrNodeType.NODE.toString())) {
                return null;//not a simplePersist node
            }
            final LinkedList<Node> list = new LinkedList<Node>();
            Node parentNode = jcrNode;
            while (parentNode != null && parentNode.getName().startsWith(JcrNodeType.NODE.toString())) {
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

    /**
     * Creates the bean from bean descriptor.
     * 
     * @param beanDescriptor the bean descriptor
     * @param parent the parent
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings( "unchecked" )
    private static <T> T createBeanFromBeanDescriptor( final BeanDescriptor beanDescriptor,
                                                       final Object parent ) throws Exception {
        if (beanDescriptor == null) {
            return null;
        }
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
                continue;
            }
            if (SimpleNodeType.class.isAssignableFrom(desc.getPropertyType())) {
                final BeanDescriptor propertyDesc = beanDescriptor.nodeProperties.get(propertyName);
                final Object bean = createBeanFromBeanDescriptor(propertyDesc, parent);
                desc.getWriteMethod().invoke(newObject, bean);
                continue;
            }

            setPropertyFromDescriptorToBean(beanDescriptor, newObject, desc, propertyName, propertyType, propertyValue);

        }

        return newObject;
    }

    /**
     * Creates the descriptor from bean.
     * 
     * @param bean the bean
     * @param defaultBeanParentDescriptor the default bean parent descriptor
     * @return the bean descriptor
     * @throws Exception the exception
     */
    @SuppressWarnings( "synthetic-access" )
    private static <T> BeanDescriptor createDescriptorFromBean( final T bean,
                                                                final BeanDescriptor defaultBeanParentDescriptor )
        throws Exception {
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
                final BeanDescriptor parentDescriptor;
                if (defaultBeanParentDescriptor == null) {

                    final Object parent = desc.getReadMethod().invoke(bean);

                    if (parent != null && descriptor.parent != null) {
                        throw Exceptions.logAndReturn(new IllegalStateException(
                                                                                MessageFormat.format(
                                                                                                     "Bean {0} of class {1} with more than one parent. Recheck the annotations",
                                                                                                     bean, bean.getClass())));

                    }
                    parentDescriptor = createDescriptorFromBean(parent, null);
                } else {
                    parentDescriptor = defaultBeanParentDescriptor;

                }
                descriptor.parent = parentDescriptor;
                continue;
            }

            if (desc.getReadMethod().isAnnotationPresent(KeyProperty.class)) {
                setPropertyFromBeanToDescriptor(bean, descriptor, desc, keyType, keyValue);
                attributesToHash.add(MessageFormat.format(keyValue, desc.getName()));
                continue;
            }
            if (SimpleNodeType.class.isAssignableFrom(desc.getPropertyType())) {
                final Object propertyVal = desc.getReadMethod().invoke(bean);
                if (propertyVal != null) {
                    final BeanDescriptor propertyDescriptor = createDescriptorFromBean(propertyVal, descriptor);
                    descriptor.nodeProperties.put(desc.getName(), propertyDescriptor);
                }
                continue;
            }
            setPropertyFromBeanToDescriptor(bean, descriptor, desc, propertyType, propertyValue);

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

    /**
     * Creates the descriptor from jcr.
     * 
     * @param jcrNode the jcr node
     * @param parent the parent
     * @return the bean descriptor
     * @throws Exception the exception
     */
    @SuppressWarnings( "synthetic-access" )
    private static BeanDescriptor createDescriptorFromJcr( final Node jcrNode,
                                                           final BeanDescriptor parent ) throws Exception {
        final BeanDescriptor descriptor = new BeanDescriptor();
        descriptor.parent = parent;
        descriptor.nodeName = jcrNode.getName();
        final PropertyIterator properties = jcrNode.getProperties();
        while (properties.hasNext()) {
            final Property property = properties.nextProperty();
            if (property.getName().startsWith(defaultNodePrefix)) {
                descriptor.properties.put(property.getName(), property.getValue().getString());

            }
        }
        final NodeIterator propertyNodes = jcrNode.getNodes(JcrNodeType.NODE_PROPERTY.toString() + "_*");
        while (propertyNodes.hasNext()) {
            final Node property = propertyNodes.nextNode();
            final String propertyName = property.getProperty(nodePropertyName).getString();
            final BeanDescriptor propertyDesc = createDescriptorFromJcr(property, descriptor);
            descriptor.nodeProperties.put(propertyName, propertyDesc);
        }
        return descriptor;

    }

    /**
     * Find children.
     * 
     * @param session the session
     * @param node the node
     * @return the set< c>
     */
    public static <T, C> Set<C> findChildren( final Session session,
                                              final T node ) {
        return null;
    }

    /**
     * Sets the property from bean to descriptor.
     * 
     * @param bean the bean
     * @param descriptor the descriptor
     * @param desc the desc
     * @param typeDescription the type description
     * @param valueDescription the value description
     * @throws IllegalAccessException the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     * @throws SLException the SL exception
     */
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

    /**
     * Sets the property from descriptor to bean.
     * 
     * @param beanDescriptor the bean descriptor
     * @param newObject the new object
     * @param desc the desc
     * @param propertyName the property name
     * @param type the type
     * @param value the value
     * @throws ClassNotFoundException the class not found exception
     * @throws SLException the SL exception
     * @throws IllegalAccessException the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     */
    private static <T> void setPropertyFromDescriptorToBean( final BeanDescriptor beanDescriptor,
                                                             final T newObject,
                                                             final PropertyDescriptor desc,
                                                             final String propertyName,
                                                             final String type,
                                                             final String value )
        throws ClassNotFoundException, SLException, IllegalAccessException, InvocationTargetException {
        final String propertyTypeString = beanDescriptor.properties.get(MessageFormat.format(type, propertyName));
        final String propertyValueAsString = beanDescriptor.properties.get(MessageFormat.format(value, propertyName));
        @SuppressWarnings( "hiding" )
        Class<?> propertyType = Conversion.getPrimitiveClass(propertyTypeString);
        if (propertyType == null) {
            propertyType = Class.forName(propertyTypeString);
        }
        final Object newPropertyValue = Conversion.convert(propertyValueAsString, propertyType);
        desc.getWriteMethod().invoke(newObject, newPropertyValue);
    }

}
