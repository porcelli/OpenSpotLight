package org.openspotlight.persist.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Map.Entry;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Strings;
import org.openspotlight.common.util.Reflection.UnwrappedCollectionTypeFromMethodReturn;
import org.openspotlight.common.util.Reflection.UnwrappedMapTypeFromMethodReturn;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class SimplePersistSupport.
 */
public class SimplePersistSupport {

    //FIXME collectionOfNodeProperties

    //FIXME mapOfNodeProperties

    //FIXME testAddNodePropertyOnCollection

    //FIXME testAddSimplePropertyOnMap

    //FIXME testAddNodePropertyOnMap

    //FIXME testRemoveNodePropertyOnCollection

    //FIXME testRemoveNodePropertyOnMap

    /**
     * The Class BeanDescriptor.
     */
    private static class BeanDescriptor {

        /** The node name. */
        String                                  nodeName;

        /** The parent. */
        BeanDescriptor                          parent;

        /** The node properties. */
        Map<String, BeanDescriptor>             nodeProperties             = new HashMap<String, BeanDescriptor>();

        /** The multiple simple properties. */
        Map<String, MultiplePropertyDescriptor> multipleSimpleProperties   = new HashMap<String, MultiplePropertyDescriptor>();

        /** The collection of node properties. */
        Map<String, List<BeanDescriptor>>       collectionOfNodeProperties = new HashMap<String, List<BeanDescriptor>>();

        /** The properties. */
        final Map<String, String>               properties                 = new HashMap<String, String>();

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

    /**
     * The Class MultiplePropertyDescriptor.
     */
    private static class MultiplePropertyDescriptor {

        /** The name. */
        String       name;

        /** The multiple type. */
        String       multipleType;

        /** The keys as strings. */
        List<String> keysAsStrings   = new ArrayList<String>();

        /** The values as strings. */
        List<String> valuesAsStrings = new ArrayList<String>();

        /** The key type. */
        String       keyType;

        /** The value type. */
        String       valueType;

    }

    /** The Constant NULL_VALUE. */
    private static final String NULL_VALUE                       = "!!! <null value> !!!";

    /** The Constant defaultPrefix. */
    private static final String DEFAULT_NODE_PREFIX              = "node.";

    /** The Constant defaultPrefix. */
    private static final String DEFAULT_MULTIPLE_PROPERTY_PREFIX = "multiple.property.";

    /** The Constant typeName. */
    private static final String TYPE_NAME                        = "node.typeName";

    /** The Constant MULTIPLE_PROPERTY_KEYS. */
    private static final String MULTIPLE_PROPERTY_KEYS           = "multiple.property.{0}.keys";

    /** The Constant MULTIPLE_PROPERTY_VALUES. */
    private static final String MULTIPLE_PROPERTY_VALUES         = "multiple.property.{0}.values";

    /** The Constant MULTIPLE_PROPERTY_MULTIPLE_TYPE. */
    private static final String MULTIPLE_PROPERTY_MULTIPLE_TYPE  = "multiple.property.{0}.multiple.type";

    /** The Constant MULTIPLE_PROPERTY_VALUE_TYPE. */
    private static final String MULTIPLE_PROPERTY_VALUE_TYPE     = "multiple.property.{0}.value.type";

    /** The Constant MULTIPLE_PROPERTY_KEY_TYPE. */
    private static final String MULTIPLE_PROPERTY_KEY_TYPE       = "multiple.property.{0}.key.type";

    /** The Constant nodePropertyName. */
    private static final String PROPERTY_NAME                    = "property.name";

    /** The Constant hashValue. */
    private static final String HASH_VALUE                       = "node.hashValue";

    /** The Constant propertyValue. */
    private static final String PROPERTY_VALUE                   = "node.property.{0}.value";

    /** The Constant propertyType. */
    private static final String PROPERTY_TYPE                    = "node.property.{0}.type";

    /** The Constant keyValue. */
    private static final String KEY_VALUE                        = "node.key.{0}.value";

    /** The Constant keyType. */
    private static final String KEY_TYPE                         = "node.key.{0}.type";

    /**
     * Adds the or create jcr node.
     * 
     * @param session the session
     * @param parentNode the parent node
     * @param descriptor the it obj
     * @param nodeType the node type
     * @param propertyName the property name
     * @return the node
     * @throws RepositoryException the repository exception
     */
    private static Node addUpdateOrRemoveJcrNode( final JcrNodeType nodeType,
                                                  final Session session,
                                                  final Node parentNode,
                                                  final BeanDescriptor descriptor,
                                                  final String propertyName ) throws RepositoryException {
        Node result = null;
        final String nodeName = nodeType.toString() + "_" + (propertyName == null ? descriptor.nodeName : propertyName);
        if (propertyName != null) {
            result = addUpdateOrRemoveNodeProperty(parentNode, descriptor, propertyName, nodeName);
        } else {
            try {
                final NodeIterator it = parentNode.getNodes(nodeName);
                while (it.hasNext()) {
                    final Node nextNode = it.nextNode();
                    final String hashProperty = nextNode.getProperty(HASH_VALUE).getString();
                    final String expectedHash = descriptor.properties.get(HASH_VALUE);
                    if (expectedHash.equals(hashProperty)) {
                        result = nextNode;
                        break;
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
            for (final Map.Entry<String, String> entry : descriptor.properties.entrySet()) {
                result.setProperty(entry.getKey(), entry.getValue());
            }
            for (final Map.Entry<String, MultiplePropertyDescriptor> entry : descriptor.multipleSimpleProperties.entrySet()) {
                final String keys = MessageFormat.format(MULTIPLE_PROPERTY_KEYS, entry.getKey());
                final String values = MessageFormat.format(MULTIPLE_PROPERTY_VALUES, entry.getKey());
                final String type = MessageFormat.format(MULTIPLE_PROPERTY_MULTIPLE_TYPE, entry.getKey());
                final String valueType = MessageFormat.format(MULTIPLE_PROPERTY_VALUE_TYPE, entry.getKey());
                final String keyType = MessageFormat.format(MULTIPLE_PROPERTY_KEY_TYPE, entry.getKey());
                final MultiplePropertyDescriptor desc = entry.getValue();
                result.setProperty(type, desc.multipleType);
                result.setProperty(valueType, desc.valueType);
                result.setProperty(values, desc.valuesAsStrings.toArray(new String[0]));
                if (desc.keyType != null) {
                    result.setProperty(keyType, desc.keyType);
                    result.setProperty(keys, desc.keysAsStrings.toArray(new String[0]));
                }
            }
        }
        return result;
    }

    /**
     * Adds the update or remove node property.
     * 
     * @param parentNode the parent node
     * @param itObj the it obj
     * @param propertyName the property name
     * @param nodeName the node name
     * @return the node
     * @throws RepositoryException the repository exception
     * @throws VersionException the version exception
     * @throws LockException the lock exception
     * @throws ConstraintViolationException the constraint violation exception
     * @throws ItemExistsException the item exists exception
     * @throws PathNotFoundException the path not found exception
     * @throws ValueFormatException the value format exception
     */
    private static Node addUpdateOrRemoveNodeProperty( final Node parentNode,
                                                       final BeanDescriptor itObj,
                                                       final String propertyName,
                                                       final String nodeName )
        throws RepositoryException, VersionException, LockException, ConstraintViolationException, ItemExistsException,
        PathNotFoundException, ValueFormatException {
        Node result = null;
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
            result.setProperty(PROPERTY_NAME, propertyName);
        }
        return result;
    }

    /**
     * Convert bean to jcr.
     * 
     * @param session the session
     * @param bean the bean
     * @param startNodePath the start node path
     * @return the node
     */
    public static <T> Node convertBeanToJcr( final String startNodePath,
                                             final Session session,
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
                    parentNode = session.getRootNode();
                    final StringTokenizer tok = new StringTokenizer(startNodePath, "/");
                    while (tok.hasMoreTokens()) {
                        final String currentToken = tok.nextToken();
                        if (currentToken.length() == 0) {
                            continue;
                        }
                        try {
                            parentNode = parentNode.getNode(currentToken);
                        } catch (final PathNotFoundException e) {
                            parentNode = parentNode.addNode(currentToken);
                        }
                    }
                }
                final Node node = addUpdateOrRemoveJcrNode(JcrNodeType.NODE, session, parentNode, itObj, null);
                for (final Entry<String, BeanDescriptor> entry : itObj.nodeProperties.entrySet()) {
                    addUpdateOrRemoveJcrNode(JcrNodeType.NODE_PROPERTY, session, node, entry.getValue(), entry.getKey());
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
        Assertions.checkCondition("correctJcrNode", jcrNode.getName().startsWith(JcrNodeType.NODE.toString()));

        try {

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
        final Class<T> typeClass = (Class<T>)Class.forName(beanDescriptor.properties.get(TYPE_NAME));
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
                setPropertyFromDescriptorToBean(beanDescriptor, newObject, desc, propertyName, KEY_TYPE, KEY_VALUE);
                continue;
            }
            if (SimpleNodeType.class.isAssignableFrom(desc.getPropertyType())) {
                final BeanDescriptor propertyDesc = beanDescriptor.nodeProperties.get(propertyName);
                final Object bean = createBeanFromBeanDescriptor(propertyDesc, parent);
                desc.getWriteMethod().invoke(newObject, bean);
                continue;
            }
            if (Collection.class.isAssignableFrom(desc.getPropertyType())) {
                final MultiplePropertyDescriptor multipleBeanDescriptor = beanDescriptor.multipleSimpleProperties.get(desc.getName());
                final Class<? extends Collection> type = (Class<? extends Collection>)desc.getPropertyType();
                final Collection<Object> instance = org.openspotlight.common.util.Collections.createNewCollection(
                                                                                                                  type,
                                                                                                                  multipleBeanDescriptor.valuesAsStrings.size());
                final Class<?> valueType = Class.forName(multipleBeanDescriptor.valueType);
                for (final String valueAsString : multipleBeanDescriptor.valuesAsStrings) {
                    Object valueAsObject = null;
                    if (!valueAsString.equals(NULL_VALUE)) {
                        valueAsObject = Conversion.convert(valueAsString, valueType);
                    }
                    instance.add(valueAsObject);
                }
                desc.getWriteMethod().invoke(newObject, instance);

                continue;
            }
            if (Map.class.isAssignableFrom(desc.getPropertyType())) {
                final MultiplePropertyDescriptor multipleBeanDescriptor = beanDescriptor.multipleSimpleProperties.get(desc.getName());
                final List<Object> values = new ArrayList<Object>();
                final List<Object> keys = new ArrayList<Object>();

                final Class<?> valueType = Class.forName(multipleBeanDescriptor.valueType);
                for (final String valueAsString : multipleBeanDescriptor.valuesAsStrings) {
                    Object valueAsObject = null;
                    if (!valueAsString.equals(NULL_VALUE)) {
                        valueAsObject = Conversion.convert(valueAsString, valueType);
                    }
                    values.add(valueAsObject);
                }
                final Class<?> keyType = Class.forName(multipleBeanDescriptor.keyType);
                for (final String keyAsString : multipleBeanDescriptor.keysAsStrings) {
                    final Object keyAsObject = Conversion.convert(keyAsString, keyType);

                    keys.add(keyAsObject);
                }
                final Map<Object, Object> map = Arrays.map(keys.toArray(), values.toArray());
                desc.getWriteMethod().invoke(newObject, map);

                continue;
            }

            setPropertyFromDescriptorToBean(beanDescriptor, newObject, desc, propertyName, PROPERTY_TYPE, PROPERTY_VALUE);

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
        descriptor.properties.put(TYPE_NAME, beanTypeName);
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
                setPropertyFromBeanToDescriptor(bean, descriptor, desc, KEY_TYPE, KEY_VALUE);
                attributesToHash.add(MessageFormat.format(KEY_VALUE, desc.getName()));
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
            if (Collection.class.isAssignableFrom(desc.getPropertyType())) {
                final MultiplePropertyDescriptor multiplePropertyDescriptor = new MultiplePropertyDescriptor();
                final UnwrappedCollectionTypeFromMethodReturn<Object> metadata = Reflection.unwrapCollectionFromMethodReturn(desc.getReadMethod());
                multiplePropertyDescriptor.multipleType = metadata.getCollectionType().getName();
                multiplePropertyDescriptor.valueType = metadata.getItemType().getName();
                multiplePropertyDescriptor.name = desc.getName();
                final Collection<Object> collection = (Collection<Object>)desc.getReadMethod().invoke(bean);
                if (collection != null) {
                    for (final Object value : collection) {
                        String valueAsString = Conversion.convert(value, String.class);
                        if (value == null) {
                            valueAsString = NULL_VALUE;
                        }
                        multiplePropertyDescriptor.valuesAsStrings.add(valueAsString);
                    }
                }
                descriptor.multipleSimpleProperties.put(desc.getName(), multiplePropertyDescriptor);
                continue;
            }
            if (Map.class.isAssignableFrom(desc.getPropertyType())) {
                final MultiplePropertyDescriptor multiplePropertyDescriptor = new MultiplePropertyDescriptor();
                final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection.unwrapMapFromMethodReturn(desc.getReadMethod());
                multiplePropertyDescriptor.multipleType = Map.class.getName();
                multiplePropertyDescriptor.keyType = metadata.getItemType().getK1().getName();
                multiplePropertyDescriptor.valueType = metadata.getItemType().getK2().getName();
                multiplePropertyDescriptor.name = desc.getName();
                final Map<Object, Object> map = (Map<Object, Object>)desc.getReadMethod().invoke(bean);
                if (map != null) {
                    for (final Entry<Object, Object> entry : map.entrySet()) {
                        final String keyAsString = Conversion.convert(entry.getKey(), String.class);
                        String valueAsString = Conversion.convert(entry.getValue(), String.class);
                        if (entry.getValue() == null) {
                            valueAsString = NULL_VALUE;
                        }
                        multiplePropertyDescriptor.keysAsStrings.add(keyAsString);
                        multiplePropertyDescriptor.valuesAsStrings.add(valueAsString);
                    }
                }
                descriptor.multipleSimpleProperties.put(desc.getName(), multiplePropertyDescriptor);
                continue;
            }
            setPropertyFromBeanToDescriptor(bean, descriptor, desc, PROPERTY_TYPE, PROPERTY_VALUE);
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
        descriptor.properties.put(SimplePersistSupport.HASH_VALUE, hash);
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
        final Set<String> multiplePropertiesAlreadyLoaded = new HashSet<String>();
        while (properties.hasNext()) {

            final Property property = properties.nextProperty();
            final String rawName = property.getName();
            if (rawName.startsWith(DEFAULT_NODE_PREFIX)) {
                descriptor.properties.put(property.getName(), property.getValue().getString());
                continue;
            }
            if (rawName.startsWith(DEFAULT_MULTIPLE_PROPERTY_PREFIX)) {
                String name = Strings.removeBegginingFrom(DEFAULT_MULTIPLE_PROPERTY_PREFIX, rawName);
                name = name.substring(0, name.lastIndexOf('.') - 1);
                if (multiplePropertiesAlreadyLoaded.contains(name)) {
                    continue;
                }
                multiplePropertiesAlreadyLoaded.add(name);
                final MultiplePropertyDescriptor desc = new MultiplePropertyDescriptor();
                desc.name = name;
                final String keys = MessageFormat.format(MULTIPLE_PROPERTY_KEYS, name);
                final String values = MessageFormat.format(MULTIPLE_PROPERTY_VALUES, name);
                final String type = MessageFormat.format(MULTIPLE_PROPERTY_MULTIPLE_TYPE, name);
                final String valueType = MessageFormat.format(MULTIPLE_PROPERTY_VALUE_TYPE, name);
                final String keyType = MessageFormat.format(MULTIPLE_PROPERTY_KEY_TYPE, name);
                desc.valueType = jcrNode.getProperty(valueType).getString();
                desc.multipleType = jcrNode.getProperty(type).getString();
                final Value[] rawValues = jcrNode.getProperty(values).getValues();
                for (final Value v : rawValues) {
                    desc.valuesAsStrings.add(v.getString());
                }
                if (jcrNode.hasProperty(keyType)) {
                    desc.keyType = jcrNode.getProperty(keyType).getString();
                    final Value[] rawKeys = jcrNode.getProperty(keys).getValues();
                    for (final Value v : rawKeys) {
                        desc.keysAsStrings.add(v.getString());
                    }
                }
                descriptor.multipleSimpleProperties.put(name, desc);
            }
        }
        final NodeIterator propertyNodes = jcrNode.getNodes(JcrNodeType.NODE_PROPERTY.toString() + "_*");
        while (propertyNodes.hasNext()) {
            final Node property = propertyNodes.nextNode();
            final String propName = property.getProperty(PROPERTY_NAME).getString();
            final BeanDescriptor propertyDesc = createDescriptorFromJcr(property, descriptor);
            descriptor.nodeProperties.put(propName, propertyDesc);
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
        Class<?> propertyType = Conversion.getPrimitiveClass(propertyTypeString);
        if (propertyType == null) {
            propertyType = Class.forName(propertyTypeString);
        }
        final Object newPropertyValue = Conversion.convert(propertyValueAsString, propertyType);
        desc.getWriteMethod().invoke(newObject, newPropertyValue);
    }

}
