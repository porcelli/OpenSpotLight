/*
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

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.Strings;
import org.openspotlight.common.util.Reflection.UnwrappedCollectionTypeFromMethodReturn;
import org.openspotlight.common.util.Reflection.UnwrappedMapTypeFromMethodReturn;
import org.openspotlight.jcr.util.JCRUtil;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.PersistPropertyAsStream;
import org.openspotlight.persist.annotation.SetUniqueIdOnThisProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.StreamPropertyWithParent;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class SimplePersistSupport.
 */
public class SimplePersistSupport {

	/**
	 * The Class BeanDescriptor.
	 */
	private static class BeanDescriptor {

		/** The node name. */
		String nodeName;;

		String uuid;

		/** The parent. */
		BeanDescriptor parent;

		/** The node properties. */
		Map<String, BeanDescriptor> nodeProperties = new HashMap<String, BeanDescriptor>();

		/** The node properties. */
		Map<String, InputStream> serializedProperties = new HashMap<String, InputStream>();

		/** The multiple simple properties. */
		Map<String, SimpleMultiplePropertyDescriptor> multipleSimpleProperties = new HashMap<String, SimpleMultiplePropertyDescriptor>();

		/** The multiple complex properties. */
		Map<String, ComplexMultiplePropertyDescriptor> multipleComplexProperties = new HashMap<String, ComplexMultiplePropertyDescriptor>();

		/** The properties. */
		final Map<String, String> properties = new HashMap<String, String>();

		final Map<String, InputStream> streamProperties = new HashMap<String, InputStream>();

		private BeanDescriptor() {
		}

		@Override
		public String toString() {
			return "BeanDescriptor " + nodeName;
		}

	}

	/**
	 * The Class ComplexMultiplePropertyDescriptor.
	 */
	private static class ComplexMultiplePropertyDescriptor {

		/** The multiple type. */
		String multipleType;

		/** The values as strings. */
		List<Pair<String, BeanDescriptor>> valuesAsBeanDescriptors = new ArrayList<Pair<String, BeanDescriptor>>();

		/** The key type. */
		String keyType;

		/** The value type. */
		String valueType;

	}

	/**
	 * The Enum JcrNodeType.
	 */
	private static enum JcrNodeType {

		/** The MULTIPL e_ nod e_ property. */
		MULTIPLE_NODE_PROPERTY,

		/** The NODE. */
		NODE,

		/** The NOD e_ property. */
		NODE_PROPERTY

	}

	// FIXME implement Lazy and DoNotLoad

	/**
	 * The Class SimpleMultiplePropertyDescriptor.
	 */
	private static class SimpleMultiplePropertyDescriptor {

		/** The multiple type. */
		String multipleType;

		/** The keys as strings. */
		List<String> keysAsStrings = new ArrayList<String>();

		/** The values as strings. */
		List<String> valuesAsStrings = new ArrayList<String>();

		/** The key type. */
		String keyType;

		/** The value type. */
		String valueType;

	}

	/** The Constant defaultPrefix. */
	private static final String DEFAULT_NODE_PREFIX = "node.";

	private static final String DEFAULT_STREAM_PREFIX = "stream.property.";

	private static final String DEFAULT_SERIALIZED_PREFIX = "serialized.property.";

	/** The Constant defaultPrefix. */
	public static final String DEFAULT_MULTIPLE_PROPERTY_PREFIX = "multiple.property.";

	/** The Constant typeName. */
	public static final String TYPE_NAME = "node.typeName";

	/** The Constant MULTIPLE_PROPERTY_KEYS. */
	public static final String MULTIPLE_PROPERTY_KEYS = "multiple.property.{0}.keys";

	/** The Constant MULTIPLE_PROPERTY_VALUES. */
	public static final String MULTIPLE_PROPERTY_VALUES = "multiple.property.{0}.values";

	public static final String STREAM_PROPERTY_VALUE = "stream.property.{0}.value";
	public static final String SERIALIZED_PROPERTY_VALUE = "serialized.property.{0}.value";

	/** The Constant MULTIPLE_PROPERTY_MULTIPLE_TYPE. */
	public static final String MULTIPLE_PROPERTY_MULTIPLE_TYPE = "multiple.property.{0}.multiple.type";

	/** The Constant MULTIPLE_PROPERTY_VALUE_TYPE. */
	public static final String MULTIPLE_PROPERTY_VALUE_TYPE = "multiple.property.{0}.value.type";

	/** The Constant MULTIPLE_PROPERTY_KEY_TYPE. */
	public static final String MULTIPLE_PROPERTY_KEY_TYPE = "multiple.property.{0}.key.type";

	/** The Constant MULTIPLE_PROPERTY_KEY_VALUE. */
	public static final String MULTIPLE_PROPERTY_KEY_VALUE = "multiple.property.{0}.key.value";

	/** The Constant nodePropertyName. */
	public static final String PROPERTY_NAME = "property.name";

	/** The Constant hashValue. */
	public static final String HASH_VALUE = "node.hashValue";

	/** The Constant hashValue. */
	public static final String LOCAL_HASH_VALUE = "node.pkonly.hashValue";

	/** The Constant propertyValue. */
	public static final String PROPERTY_VALUE = "node.property.{0}.value";

	/** The Constant propertyType. */
	public static final String PROPERTY_TYPE = "node.property.{0}.type";

	/** The Constant keyValue. */
	public static final String KEY_VALUE = "node.key.{0}.value";

	/** The Constant keyType. */
	public static final String KEY_TYPE = "node.key.{0}.type";

	/**
	 * Adds the or create jcr node.
	 * 
	 * @param session
	 *            the session
	 * @param parentNode
	 *            the parent node
	 * @param descriptor
	 *            the it obj
	 * @param nodeType
	 *            the node type
	 * @param propertyName
	 *            the property name
	 * @return the node
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws Exception
	 *             the exception
	 */
	private static Node addUpdateOrRemoveJcrNode(final JcrNodeType nodeType,
			final Session session, final Node parentNode,
			final BeanDescriptor descriptor, final String propertyName)
			throws Exception {
		Node result = null;
		final String nodeName = SimplePersistSupport.getNodeName(nodeType,
				descriptor, propertyName);
		if (JcrNodeType.NODE_PROPERTY.equals(nodeType)) {
			result = SimplePersistSupport.addUpdateOrRemoveNodeProperty(
					parentNode, descriptor, propertyName, nodeName);
		} else {
			try {
				final NodeIterator it = parentNode.getNodes(nodeName);
				while (it.hasNext()) {
					final Node nextNode = it.nextNode();
					final String hashProperty = nextNode.getProperty(
							SimplePersistSupport.HASH_VALUE).getString();
					final String expectedHash = descriptor.properties
							.get(SimplePersistSupport.HASH_VALUE);
					if (expectedHash.equals(hashProperty)) {
						result = nextNode;
						break;
					}
				}
			} catch (final PathNotFoundException e) {
				// ok, nothing to do here
			}
			if (result == null && descriptor != null) {
				final Node newNode = parentNode.addNode(nodeName);
				JCRUtil.makeReferenceable(newNode);
				result = newNode;
			}
		}
		if (result != null) {
			if (descriptor == null) {
				result.remove();
				return null;
			}
			for (final Map.Entry<String, String> entry : descriptor.properties
					.entrySet()) {
				result.setProperty(entry.getKey(), entry.getValue());
			}
			// Stream properties are handled here
			for (final Map.Entry<String, InputStream> entry : descriptor.streamProperties
					.entrySet()) {
				result.setProperty(MessageFormat.format(STREAM_PROPERTY_VALUE,
						entry.getKey()), entry.getValue());
			}
			// Serialized properties are handled here
			for (final Map.Entry<String, InputStream> entry : descriptor.serializedProperties
					.entrySet()) {
				result.setProperty(MessageFormat.format(
						SERIALIZED_PROPERTY_VALUE, entry.getKey()), entry
						.getValue());
			}

			SimplePersistSupport.saveSimplePropertiesOnJcr(descriptor, result);
			SimplePersistSupport.saveComplexMultiplePropertiesOnJcr(session,
					descriptor, result);

		}
		return result;
	}

	/**
	 * Adds the update or remove node property.
	 * 
	 * @param parentNode
	 *            the parent node
	 * @param itObj
	 *            the it obj
	 * @param propertyName
	 *            the property name
	 * @param nodeName
	 *            the node name
	 * @return the node
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws VersionException
	 *             the version exception
	 * @throws LockException
	 *             the lock exception
	 * @throws ConstraintViolationException
	 *             the constraint violation exception
	 * @throws ItemExistsException
	 *             the item exists exception
	 * @throws PathNotFoundException
	 *             the path not found exception
	 * @throws ValueFormatException
	 *             the value format exception
	 * @throws Exception
	 *             the exception
	 */
	private static Node addUpdateOrRemoveNodeProperty(final Node parentNode,
			final BeanDescriptor itObj, final String propertyName,
			final String nodeName) throws Exception {
		Node result = null;
		try {
			result = parentNode.getNode(nodeName);
		} catch (final PathNotFoundException e) {
			// there's no property node yet
		}
		if (itObj == null) {
			if (result != null) {
				result.remove();
			}
			return null;
		}
		if (result == null) {
			final Node newNode = parentNode.addNode(nodeName);
			JCRUtil.makeReferenceable(newNode);
			result = newNode;
		}
		if (result != null) {
			result
					.setProperty(SimplePersistSupport.PROPERTY_NAME,
							propertyName);
		}
		return result;
	}

	private static <T> String buildPropertyString(final Class<T> nodeType,
			final String[] propertyNames, final Object[] propertyValues,
			final String basePropertyName) throws InstantiationException,
			IllegalAccessException, SLException {
		final StringBuilder propertyWhereXpath = new StringBuilder();
		final T dummyInstance = nodeType.newInstance();
		propertyWhereXpath.append('@');
		propertyWhereXpath.append("node.typeName");
		propertyWhereXpath.append('=');
		propertyWhereXpath.append("'");
		propertyWhereXpath.append(nodeType.getName());
		propertyWhereXpath.append("'");

		for (int i = 0, size = propertyNames.length; i < size; i++) {
			propertyWhereXpath.append(" and ");
			try {
				PropertyUtils.getProperty(dummyInstance, propertyNames[i]);
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew("No property named "
						+ nodeType.getName() + "#" + propertyNames[i], e,
						SLRuntimeException.class);
			}
			final String keyString = MessageFormat.format(basePropertyName,
					propertyNames[i]);

			if (propertyValues[i] != null) {
				final String propertyValye = Conversion.convert(
						propertyValues[i], String.class);
				propertyWhereXpath.append('@');
				propertyWhereXpath.append(keyString);
				propertyWhereXpath.append('=');
				propertyWhereXpath.append("'");
				propertyWhereXpath.append(propertyValye);
				propertyWhereXpath.append("'");
			} else {
				propertyWhereXpath.append(" not(@");
				propertyWhereXpath.append(keyString);
				propertyWhereXpath.append(") ");

			}
		}
		return propertyWhereXpath.toString();
	}

	/**
	 * Convert beans to jcrs.
	 * 
	 * @param parentJcrNode
	 *            the parent jcr node
	 * @param session
	 *            the session
	 * @param beans
	 *            the beans
	 * @return the iterable< node>
	 */
	public static <T> Iterable<Node> convertBeansToJcrs(
			final Node parentJcrNode, final Session session,
			final Iterable<T> beans) {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		Assertions.checkCondition("sessionAlive", session.isLive());
		final Set<Node> result = new HashSet<Node>();
		for (final T bean : beans) {
			final Node newNode = SimplePersistSupport.convertBeanToJcr(
					parentJcrNode, session, bean);
			result.add(newNode);
		}
		return result;
	}

	/**
	 * Convert beans to jcrs.
	 * 
	 * @param startNodePath
	 *            the start node path
	 * @param session
	 *            the session
	 * @param beans
	 *            the beans
	 * @return the iterable< node>
	 */
	public static <T> Iterable<Node> convertBeansToJcrs(
			final String startNodePath, final Session session,
			final Iterable<T> beans) {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		final Set<Node> result = new HashSet<Node>();
		for (final T bean : beans) {
			if (bean != null) {
				final Node newNode = SimplePersistSupport.convertBeanToJcr(
						startNodePath, session, bean);
				result.add(newNode);
			}
		}
		return result;
	}

	/**
	 * Convert bean to jcr.
	 * 
	 * @param session
	 *            the session
	 * @param bean
	 *            the bean
	 * @param parentJcrNode
	 *            the parent jcr node
	 * @return the node
	 */
	public static <T> Node convertBeanToJcr(final Node parentJcrNode,
			final Session session, final T bean) {
		Assertions.checkNotNull("bean", bean);
		Assertions.checkCondition("correctInstance:"
				+ bean.getClass().getName(), bean instanceof SimpleNodeType);
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());
		Assertions.checkNotNull("parentJcrNode", parentJcrNode);
		try {
			BeanDescriptor descriptor = SimplePersistSupport
					.createDescriptorFromBean(bean, null);
			final LinkedList<BeanDescriptor> list = new LinkedList<BeanDescriptor>();
			while (descriptor != null) {
				list.addFirst(descriptor);
				descriptor = descriptor.parent;
			}

			Node parentNode = parentJcrNode;
			for (final BeanDescriptor itObj : list) {

				parentNode = SimplePersistSupport.addUpdateOrRemoveJcrNode(
						JcrNodeType.NODE, session, parentNode, itObj, null);
				for (final Entry<String, BeanDescriptor> entry : itObj.nodeProperties
						.entrySet()) {
					SimplePersistSupport.addUpdateOrRemoveJcrNode(
							JcrNodeType.NODE_PROPERTY, session, parentNode,
							entry.getValue(), entry.getKey());
				}
			}
			return parentNode;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	/**
	 * Convert bean to jcr.
	 * 
	 * @param startNodePath
	 *            the start node path
	 * @param session
	 *            the session
	 * @param bean
	 *            the bean
	 * @return the node
	 */
	public static <T> Node convertBeanToJcr(final String startNodePath,
			final Session session, final T bean) {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		try {

			Node parentNode = null;
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
					JCRUtil.makeReferenceable(parentNode);
				}
			}
			return SimplePersistSupport.convertBeanToJcr(parentNode, session,
					bean);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	/**
	 * Convert jcrs to beans.
	 * 
	 * @param session
	 *            the session
	 * @param jcrNodes
	 *            the jcr nodes
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @return the iterable< t>
	 * @throws Exception
	 *             the exception
	 */
	public static <T> Iterable<T> convertJcrsToBeans(final Session session,
			final Iterable<Node> jcrNodes,
			final LazyType multipleLoadingStrategy) throws Exception {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		final Set<T> result = new HashSet<T>();
		for (final Node node : jcrNodes) {
			final T bean = SimplePersistSupport.<T> convertJcrToBean(session,
					node, multipleLoadingStrategy);
			result.add(bean);
		}
		return result;
	}

	/**
	 * Convert jcrs to beans.
	 * 
	 * @param session
	 *            the session
	 * @param jcrNodes
	 *            the jcr nodes
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @return the iterable< t>
	 * @throws Exception
	 *             the exception
	 */
	public static <T> Iterable<T> convertJcrsToBeans(final Session session,
			final NodeIterator jcrNodes, final LazyType multipleLoadingStrategy)
			throws Exception {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		final Set<T> result = new HashSet<T>();
		while (jcrNodes.hasNext()) {
			final Node node = jcrNodes.nextNode();
			final T bean = SimplePersistSupport.<T> convertJcrToBean(session,
					node, multipleLoadingStrategy);
			result.add(bean);
		}
		return result;
	}

	/**
	 * Convert jcr to bean.
	 * 
	 * @param session
	 *            the session
	 * @param jcrNode
	 *            the jcr node
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertJcrToBean(final Session session,
			final Node jcrNode, final LazyType multipleLoadingStrategy)
			throws Exception {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());
		Assertions.checkNotNull("jcrNode", jcrNode);
		Assertions.checkCondition("correctJcrNode", isCorrectNode(jcrNode));

		try {

			final LinkedList<Node> list = new LinkedList<Node>();
			Node parentNode = jcrNode;
			while (parentNode != null && isCorrectNode(parentNode)) {
				list.addFirst(parentNode);
				parentNode = parentNode.getParent();
			}
			Object parent = null;
			BeanDescriptor parentDescriptor = null;
			for (final Node node : list) {
				parentDescriptor = SimplePersistSupport
						.createDescriptorFromJcr(node, parentDescriptor,
								multipleLoadingStrategy, session);
				parent = SimplePersistSupport.createBeanFromDescriptor(
						parentDescriptor, parent);
			}
			Assertions.checkCondition("correctInstance",
					parent instanceof SimpleNodeType);

			return (T) parent;

		} catch (final Exception e) {
			throw Exceptions.logAndReturn(e);
		}
	}

	private static InputStream convertSerializableToStream(
			final Serializable obj, final SimpleNodeType parent,
			final String propertyName) throws Exception {
		try {
			nullParentProperty(obj);
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			return new ByteArrayInputStream(baos.toByteArray());
		} catch (final Exception e) {
			throw Exceptions.logAndReturn("problem serializing property "
					+ propertyName + " from parent object of type "
					+ parent.getClass(), e);
		}
	}

	private static Serializable convertStreamToSerializable(
			final InputStream is, final SimpleNodeType parent) throws Exception {
		final ObjectInputStream ois = new ObjectInputStream(is);
		final Serializable serializable = (Serializable) ois.readObject();
		setParentProperty(serializable, parent);
		return serializable;
	}

	private static InputStream convertToByteArrayInputStream(InputStream is)
			throws IOException {
		if (!(is instanceof ByteArrayInputStream)) {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(is, baos);
			is = new ByteArrayInputStream(baos.toByteArray());
		}
		return is;
	}

	/**
	 * Creates the bean from bean descriptor.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param parent
	 *            the parent
	 * @return the t
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> T createBeanFromDescriptor(
			final BeanDescriptor beanDescriptor, final Object parent)
			throws Exception {
		if (beanDescriptor == null) {
			return null;
		}
		final Class<T> typeClass = (Class<T>) Class
				.forName(beanDescriptor.properties
						.get(SimplePersistSupport.TYPE_NAME));
		final T newObject = typeClass.newInstance();
		final PropertyDescriptor[] allProperties = PropertyUtils
				.getPropertyDescriptors(newObject);
		for (final PropertyDescriptor desc : allProperties) {
			if (desc.getName().equals("class")) {
				continue;
			}
			if (desc.getReadMethod().isAnnotationPresent(
					PersistPropertyAsStream.class)) {
				final InputStream is = beanDescriptor.serializedProperties
						.get(desc.getName());
				if (is != null) {
					final Serializable propVal = convertStreamToSerializable(
							is, (SimpleNodeType) newObject);
					desc.getWriteMethod().invoke(newObject, propVal);
				}
				continue;
			}

			if (desc.getReadMethod().isAnnotationPresent(
					TransientProperty.class)) {
				continue;
			}
			if (desc.getWriteMethod() == null) {
				throw Exceptions
						.logAndReturn(new IllegalStateException(
								"Property "
										+ typeClass.getName()
										+ "#"
										+ desc.getName()
										+ " without setter. To ignore this use @TransientProperty annotation"));
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
				SimplePersistSupport.setPropertyFromDescriptorToBean(
						beanDescriptor, newObject, desc, propertyName,
						SimplePersistSupport.KEY_TYPE,
						SimplePersistSupport.KEY_VALUE);
				continue;
			}
			if (desc.getReadMethod().isAnnotationPresent(
					SetUniqueIdOnThisProperty.class)) {
				desc.getWriteMethod().invoke(newObject, beanDescriptor.uuid);
				continue;
			}
			if (SimpleNodeType.class.isAssignableFrom(desc.getPropertyType())) {
				SimplePersistSupport.setNodePropertyFromJcrToBean(
						beanDescriptor, parent, newObject, desc, propertyName);
				continue;
			}
			if (Collection.class.isAssignableFrom(desc.getPropertyType())) {
				final UnwrappedCollectionTypeFromMethodReturn<Object> metadata = Reflection
						.unwrapCollectionFromMethodReturn(desc.getReadMethod());
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType())) {
					SimplePersistSupport
							.setComplexCollectionPropertyFromDescriptorToBean(
									beanDescriptor, newObject, desc);
				} else {
					SimplePersistSupport
							.setSimpleCollectionPropertyFromDescriptorToBean(
									beanDescriptor, newObject, desc);
				}
				continue;
			}
			if (Map.class.isAssignableFrom(desc.getPropertyType())) {
				final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection
						.unwrapMapFromMethodReturn(desc.getReadMethod());
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType().getK2())) {
					SimplePersistSupport
							.setComplexMapPropertyFromDescriptorToBean(
									beanDescriptor, newObject, desc);
				} else {
					SimplePersistSupport
							.setSimpleMapPropertyFromDescriptorToBean(
									beanDescriptor, newObject, desc);
				}

				continue;
			}
			if (InputStream.class.isAssignableFrom(desc.getPropertyType())) {
				final InputStream streamValue = beanDescriptor.streamProperties
						.get(desc.getName());
				desc.getWriteMethod().invoke(newObject, streamValue);
				continue;
			}
			SimplePersistSupport.setPropertyFromDescriptorToBean(
					beanDescriptor, newObject, desc, propertyName,
					SimplePersistSupport.PROPERTY_TYPE,
					SimplePersistSupport.PROPERTY_VALUE);
		}

		return newObject;
	}

	/**
	 * Creates the descriptor from bean.
	 * 
	 * @param bean
	 *            the bean
	 * @param defaultBeanParentDescriptor
	 *            the default bean parent descriptor
	 * @return the bean descriptor
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("synthetic-access")
	private static <T> BeanDescriptor createDescriptorFromBean(final T bean,
			final BeanDescriptor defaultBeanParentDescriptor) throws Exception {
		if (bean == null) {
			return null;
		}
		final BeanDescriptor descriptor = new BeanDescriptor();
		descriptor.nodeName = SimplePersistSupport.getNodeName(bean.getClass());
		final String beanTypeName = bean.getClass().getName();
		final List<String> attributesToHash = new ArrayList<String>();
		descriptor.properties.put(SimplePersistSupport.TYPE_NAME, beanTypeName);
		final PropertyDescriptor[] allProperties = PropertyUtils
				.getPropertyDescriptors(bean);
		for (final PropertyDescriptor desc : allProperties) {
			if (desc.getName().equals("class")) {
				continue;
			}
			if (desc.getReadMethod().isAnnotationPresent(
					TransientProperty.class)) {
				continue;
			}
			if (desc.getReadMethod().isAnnotationPresent(
					PersistPropertyAsStream.class)) {
				final Serializable propertyValue = (Serializable) desc
						.getReadMethod().invoke(bean);
				final InputStream is = convertSerializableToStream(
						propertyValue, (SimpleNodeType) bean, desc.getName());
				descriptor.serializedProperties.put(desc.getName(), is);
				continue;
			}
			if (desc.getReadMethod().isAnnotationPresent(
					SetUniqueIdOnThisProperty.class)) {
				descriptor.uuid = (String) desc.getReadMethod().invoke(bean);
				continue;
			}
			if (desc.getReadMethod().isAnnotationPresent(ParentProperty.class)) {
				SimplePersistSupport.handleParentProperty(bean,
						defaultBeanParentDescriptor, descriptor, desc);
				continue;
			}

			if (SimpleNodeType.class.isAssignableFrom(desc.getPropertyType())) {
				final Object propertyVal = desc.getReadMethod().invoke(bean);
				if (propertyVal != null) {
					final BeanDescriptor propertyDescriptor = SimplePersistSupport
							.createDescriptorFromBean(propertyVal, descriptor);
					descriptor.nodeProperties.put(desc.getName(),
							propertyDescriptor);
				} else {
					descriptor.nodeProperties.put(desc.getName(), null);
				}
				continue;
			}
			if (Collection.class.isAssignableFrom(desc.getPropertyType())) {
				final UnwrappedCollectionTypeFromMethodReturn<Object> metadata = Reflection
						.unwrapCollectionFromMethodReturn(desc.getReadMethod());
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType())) {
					SimplePersistSupport.handlePropertyOfComplexCollection(
							bean, descriptor, desc, metadata);
				} else {
					SimplePersistSupport.handlePropertyOfSimpleCollection(bean,
							descriptor, desc, metadata);
				}
				continue;
			}
			if (Map.class.isAssignableFrom(desc.getPropertyType())) {
				final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata = Reflection
						.unwrapMapFromMethodReturn(desc.getReadMethod());
				if (SimpleNodeType.class.isAssignableFrom(metadata
						.getItemType().getK2())) {
					SimplePersistSupport.handlePropertyOfComplexMap(bean,
							descriptor, desc, metadata);
				} else {
					SimplePersistSupport.handlePropertyOfSimpleMap(bean,
							descriptor, desc, metadata);
				}
				continue;
			}
			if (InputStream.class.isAssignableFrom(desc.getPropertyType())) {
				InputStream propertyVal = (InputStream) desc.getReadMethod()
						.invoke(bean);
				propertyVal = convertToByteArrayInputStream(propertyVal);
				descriptor.streamProperties.put(desc.getName(), propertyVal);
			}
			if (desc.getReadMethod().isAnnotationPresent(KeyProperty.class)) {
				SimplePersistSupport.setPropertyFromBeanToDescriptor(bean,
						descriptor, desc, SimplePersistSupport.KEY_TYPE,
						SimplePersistSupport.KEY_VALUE);
				attributesToHash.add(MessageFormat.format(
						SimplePersistSupport.KEY_VALUE, desc.getName()));
			}
			SimplePersistSupport.setPropertyFromBeanToDescriptor(bean,
					descriptor, desc, SimplePersistSupport.PROPERTY_TYPE,
					SimplePersistSupport.PROPERTY_VALUE);
		}
		SimplePersistSupport.createHash(bean.getClass(), descriptor,
				attributesToHash);
		SimplePersistSupport.createHashUsingOnlyPrimaryKey(bean.getClass(),
				descriptor, attributesToHash);
		return descriptor;
	}

	/**
	 * Creates the descriptor from jcr.
	 * 
	 * @param jcrNode
	 *            the jcr node
	 * @param parent
	 *            the parent
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @param session
	 *            the session
	 * @return the bean descriptor
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("synthetic-access")
	private static BeanDescriptor createDescriptorFromJcr(final Node jcrNode,
			final BeanDescriptor parent,
			final LazyType multipleLoadingStrategy, final Session session)
			throws Exception {
		final BeanDescriptor descriptor = new BeanDescriptor();
		descriptor.uuid = jcrNode.isNew() ? null : jcrNode.getUUID();
		descriptor.parent = parent;
		descriptor.nodeName = jcrNode.getName();
		final PropertyIterator properties = jcrNode.getProperties();
		final Set<String> multiplePropertiesAlreadyLoaded = new HashSet<String>();
		while (properties.hasNext()) {
			final Property property = properties.nextProperty();
			final String rawName = property.getName();
			if (rawName.startsWith(SimplePersistSupport.DEFAULT_NODE_PREFIX)) {
				descriptor.properties.put(property.getName(), property
						.getValue().getString());
				continue;
			}
			if (rawName
					.startsWith(SimplePersistSupport.DEFAULT_MULTIPLE_PROPERTY_PREFIX)) {
				SimplePersistSupport.setMultiplePropertyFromJcrToDescriptor(
						jcrNode, descriptor, multiplePropertiesAlreadyLoaded,
						rawName);
				continue;
			}
			if (rawName.startsWith(DEFAULT_SERIALIZED_PREFIX)) {
				InputStream is = property.getValue().getStream();
				is = convertToByteArrayInputStream(is);
				final String newPropertyName = extractPropertyName(rawName,
						DEFAULT_SERIALIZED_PREFIX);
				descriptor.serializedProperties.put(newPropertyName, is);
				continue;
			}
			if (rawName.startsWith(DEFAULT_STREAM_PREFIX)) {
				InputStream value = property.getValue().getStream();
				value = convertToByteArrayInputStream(value);

				final String newPropertyName = extractPropertyName(rawName,
						DEFAULT_STREAM_PREFIX);
				descriptor.streamProperties.put(newPropertyName, value);
				continue;
			}
		}
		final NodeIterator propertyNodes = jcrNode
				.getNodes(JcrNodeType.NODE_PROPERTY.toString() + "_*");
		while (propertyNodes.hasNext()) {
			final Node property = propertyNodes.nextNode();
			SimplePersistSupport.setJcrNodePropertyOnDescriptor(descriptor,
					property, multipleLoadingStrategy, session);
		}
		final NodeIterator multiplePropertyNodes = jcrNode.getNodes();
		while (multiplePropertyNodes.hasNext()) {
			final Node property = multiplePropertyNodes.nextNode();
			if (property.getName().startsWith(
					JcrNodeType.MULTIPLE_NODE_PROPERTY.toString())) {
				SimplePersistSupport.setJcrComplexMultiplePropertyOnDescriptor(
						descriptor, property, multipleLoadingStrategy, session);
			}
		}
		return descriptor;

	}

	/**
	 * Creates the hash.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param attributesToHash
	 *            the attributes to hash
	 * @param beanClass
	 *            the bean class
	 * @return the string
	 */
	private static <T> String createHash(final Class<T> beanClass,
			final BeanDescriptor descriptor, final List<String> attributesToHash) {
		final StringBuilder hashBuffer = new StringBuilder();
		hashBuffer.append(beanClass.getName());
		hashBuffer.append(';');
		Collections.sort(attributesToHash);
		if (descriptor.parent != null) {
			hashBuffer.append(descriptor.parent.properties
					.get(SimplePersistSupport.HASH_VALUE));
			hashBuffer.append(';');
		}
		for (final String keyProp : attributesToHash) {
			hashBuffer.append(descriptor.properties.get(keyProp));
			hashBuffer.append(';');
		}
		final String hash = UUID.nameUUIDFromBytes(
				hashBuffer.toString().getBytes()).toString();
		descriptor.properties.put(SimplePersistSupport.HASH_VALUE, hash);
		return hash;
	}

	/**
	 * Creates the hash.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param attributesToHash
	 *            the attributes to hash
	 * @param beanClass
	 *            the bean class
	 * @return the string
	 */
	private static <T> String createHashUsingOnlyPrimaryKey(
			final Class<T> beanClass, final BeanDescriptor descriptor,
			final List<String> attributesToHash) {
		final StringBuilder hashBuffer = new StringBuilder();
		hashBuffer.append(beanClass.getName());
		hashBuffer.append(';');
		Collections.sort(attributesToHash);
		for (final String keyProp : attributesToHash) {
			hashBuffer.append(descriptor.properties.get(keyProp));
			hashBuffer.append(';');
		}
		final String hash = UUID.nameUUIDFromBytes(
				hashBuffer.toString().getBytes()).toString();
		descriptor.properties.put(SimplePersistSupport.LOCAL_HASH_VALUE, hash);
		return hash;
	}

	private static <T> void executeXpathAndFillSet(final String rootPath,
			final Session session, final LazyType multipleLoadingStrategy,
			final String propertyWhereXpath, final Set<T> resultNodes)
			throws InvalidQueryException, RepositoryException, Exception {
		String xpath = MessageFormat.format("{0}//*[{1}]", rootPath,
				propertyWhereXpath);
		if (xpath.endsWith("[]")) {
			xpath = xpath.substring(0, xpath.length() - 2);
		}
		final Query query = session.getWorkspace().getQueryManager()
				.createQuery(xpath, Query.XPATH);
		final QueryResult result = query.execute();
		final NodeIterator nodes = result.getNodes();
		while (nodes.hasNext()) {
			final Node jcrNode = nodes.nextNode();
			final T bean = SimplePersistSupport.<T> convertJcrToBean(session,
					jcrNode, multipleLoadingStrategy);
			resultNodes.add(bean);
		}

	}

	private static String extractPropertyName(final String rawName,
			final String prefixToRemove) {
		final String propertyName = Strings.removeBegginingFrom(prefixToRemove,
				rawName);
		final String newPropertyName = propertyName.substring(0, propertyName
				.indexOf('.'));
		return newPropertyName;
	}

	/**
	 * Find children.
	 * 
	 * @param session
	 *            the session
	 * @param nodeType
	 *            the node type
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @param propertyNames
	 *            the property names
	 * @param propertyValues
	 *            the property values
	 * @return the set< c>
	 */
	public static <T> Set<T> findNodesByProperties(final String rootPath,
			final Session session, final Class<T> nodeType,
			final LazyType multipleLoadingStrategy,
			final String[] propertyNames, final Object[] propertyValues) {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		try {

			Assertions.checkNotNull("session", session);
			Assertions.checkCondition("sessionAlive", session.isLive());
			Assertions.checkNotNull("nodeType", nodeType);
			Assertions.checkNotNull("propertyNames", propertyNames);
			Assertions.checkNotNull("propertyValues", propertyValues);
			Assertions.checkCondition("sameSize",
					propertyNames.length == propertyValues.length);

			final String propertyNodeWhereXpath = buildPropertyString(nodeType,
					propertyNames, propertyValues,
					SimplePersistSupport.PROPERTY_VALUE);
			final Set<T> resultNodes = new HashSet<T>();

			executeXpathAndFillSet(rootPath, session, multipleLoadingStrategy,
					propertyNodeWhereXpath, resultNodes);
			return resultNodes;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	/**
	 * Gets the node name.
	 * 
	 * @param class1
	 *            the class1
	 * @return the node name
	 */
	public static String getJcrNodeName(final Class<? extends Object> class1) {
		return JcrNodeType.NODE.toString() + "_"
				+ SimplePersistSupport.getNodeName(class1);
	}

	/**
	 * Gets the node name.
	 * 
	 * @param class1
	 *            the class1
	 * @return the node name
	 */
	private static String getNodeName(final Class<? extends Object> class1) {
		if (class1.isAnnotationPresent(Name.class)) {
			return class1.getAnnotation(Name.class).value();
		} else {
			return class1.getName().replaceAll("[.]", "_").replaceAll("[$]",
					"_");
		}
	}

	/**
	 * Gets the node name.
	 * 
	 * @param nodeType
	 *            the node type
	 * @param descriptor
	 *            the descriptor
	 * @param propertyName
	 *            the property name
	 * @return the node name
	 */
	private static String getNodeName(final JcrNodeType nodeType,
			final BeanDescriptor descriptor, final String propertyName) {
		final String nodeName = nodeType.toString() + "_"
				+ (propertyName == null ? descriptor.nodeName : propertyName);
		return nodeName;
	}

	/**
	 * Handle parent property.
	 * 
	 * @param bean
	 *            the bean
	 * @param defaultBeanParentDescriptor
	 *            the default bean parent descriptor
	 * @param descriptor
	 *            the descriptor
	 * @param desc
	 *            the desc
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws Exception
	 *             the exception
	 */
	private static <T> void handleParentProperty(final T bean,
			final BeanDescriptor defaultBeanParentDescriptor,
			final BeanDescriptor descriptor, final PropertyDescriptor desc)
			throws IllegalAccessException, InvocationTargetException, Exception {
		final BeanDescriptor parentDescriptor;
		if (defaultBeanParentDescriptor == null) {

			final Object parent = desc.getReadMethod().invoke(bean);

			if (parent != null && descriptor.parent != null) {
				throw Exceptions
						.logAndReturn(new IllegalStateException(
								MessageFormat
										.format(
												"Bean {0} of class {1} with more than one parent. Recheck the annotations",
												bean, bean.getClass())));

			}
			parentDescriptor = SimplePersistSupport.createDescriptorFromBean(
					parent, null);
		} else {
			parentDescriptor = defaultBeanParentDescriptor;

		}
		descriptor.parent = parentDescriptor;
	}

	/**
	 * Handle property of complex collection.
	 * 
	 * @param bean
	 *            the bean
	 * @param descriptor
	 *            the descriptor
	 * @param desc
	 *            the desc
	 * @param metadata
	 *            the metadata
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void handlePropertyOfComplexCollection(final T bean,
			final BeanDescriptor descriptor, final PropertyDescriptor desc,
			final UnwrappedCollectionTypeFromMethodReturn<Object> metadata)
			throws Exception {

		final ComplexMultiplePropertyDescriptor multiplePropertyDescriptor = new ComplexMultiplePropertyDescriptor();
		multiplePropertyDescriptor.multipleType = metadata.getCollectionType()
				.getName();
		multiplePropertyDescriptor.valueType = metadata.getItemType().getName();
		final Collection<Object> collection = (Collection<Object>) desc
				.getReadMethod().invoke(bean);
		if (collection != null) {
			for (final Object value : collection) {
				final BeanDescriptor valueAsDescriptor = SimplePersistSupport
						.createDescriptorFromBean(value, descriptor);

				multiplePropertyDescriptor.valuesAsBeanDescriptors
						.add(new Pair<String, BeanDescriptor>(null,
								valueAsDescriptor));
			}
		}
		descriptor.multipleComplexProperties.put(desc.getName(),
				multiplePropertyDescriptor);
	}

	/**
	 * Handle property of complex map.
	 * 
	 * @param bean
	 *            the bean
	 * @param descriptor
	 *            the descriptor
	 * @param desc
	 *            the desc
	 * @param metadata
	 *            the metadata
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void handlePropertyOfComplexMap(final T bean,
			final BeanDescriptor descriptor, final PropertyDescriptor desc,
			final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata)
			throws Exception {
		final ComplexMultiplePropertyDescriptor multiplePropertyDescriptor = new ComplexMultiplePropertyDescriptor();
		multiplePropertyDescriptor.multipleType = Map.class.getName();
		multiplePropertyDescriptor.keyType = metadata.getItemType().getK1()
				.getName();
		multiplePropertyDescriptor.valueType = metadata.getItemType().getK2()
				.getName();
		final Map<Object, Object> map = (Map<Object, Object>) desc
				.getReadMethod().invoke(bean);
		if (map != null) {
			for (final Entry<Object, Object> entry : map.entrySet()) {
				final String keyAsString = Conversion.convert(entry.getKey(),
						String.class);
				final BeanDescriptor valueAsDescriptor = SimplePersistSupport
						.createDescriptorFromBean(entry.getValue(), descriptor);
				multiplePropertyDescriptor.valuesAsBeanDescriptors
						.add(new Pair<String, BeanDescriptor>(keyAsString,
								valueAsDescriptor));
			}
		}
		descriptor.multipleComplexProperties.put(desc.getName(),
				multiplePropertyDescriptor);
	}

	/**
	 * Handle property of simple collection.
	 * 
	 * @param bean
	 *            the bean
	 * @param descriptor
	 *            the descriptor
	 * @param desc
	 *            the desc
	 * @param metadata
	 *            the metadata
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws SLException
	 *             the SL exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void handlePropertyOfSimpleCollection(final T bean,
			final BeanDescriptor descriptor, final PropertyDescriptor desc,
			final UnwrappedCollectionTypeFromMethodReturn<Object> metadata)
			throws IllegalAccessException, InvocationTargetException,
			SLException {
		final SimpleMultiplePropertyDescriptor multiplePropertyDescriptor = new SimpleMultiplePropertyDescriptor();
		multiplePropertyDescriptor.multipleType = metadata.getCollectionType()
				.getName();
		multiplePropertyDescriptor.valueType = metadata.getItemType().getName();
		final Collection<Object> collection = (Collection<Object>) desc
				.getReadMethod().invoke(bean);
		if (collection != null) {
			for (final Object value : collection) {
				String valueAsString = Conversion.convert(value, String.class);
				if (value == null) {
					valueAsString = null;
				}
				multiplePropertyDescriptor.valuesAsStrings.add(valueAsString);
			}
		}
		descriptor.multipleSimpleProperties.put(desc.getName(),
				multiplePropertyDescriptor);
	}

	/**
	 * Handle property of simple map.
	 * 
	 * @param bean
	 *            the bean
	 * @param descriptor
	 *            the descriptor
	 * @param desc
	 *            the desc
	 * @param metadata
	 *            the metadata
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws SLException
	 *             the SL exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void handlePropertyOfSimpleMap(final T bean,
			final BeanDescriptor descriptor, final PropertyDescriptor desc,
			final UnwrappedMapTypeFromMethodReturn<Object, Object> metadata)
			throws IllegalAccessException, InvocationTargetException,
			SLException {
		final SimpleMultiplePropertyDescriptor multiplePropertyDescriptor = new SimpleMultiplePropertyDescriptor();
		multiplePropertyDescriptor.multipleType = Map.class.getName();
		multiplePropertyDescriptor.keyType = metadata.getItemType().getK1()
				.getName();
		multiplePropertyDescriptor.valueType = metadata.getItemType().getK2()
				.getName();
		final Map<Object, Object> map = (Map<Object, Object>) desc
				.getReadMethod().invoke(bean);
		if (map != null) {
			for (final Entry<Object, Object> entry : map.entrySet()) {
				final String keyAsString = Conversion.convert(entry.getKey(),
						String.class);
				String valueAsString = Conversion.convert(entry.getValue(),
						String.class);
				if (entry.getValue() == null) {
					valueAsString = null;
				}
				multiplePropertyDescriptor.keysAsStrings.add(keyAsString);
				multiplePropertyDescriptor.valuesAsStrings.add(valueAsString);
			}
		}
		descriptor.multipleSimpleProperties.put(desc.getName(),
				multiplePropertyDescriptor);
	}

	private static boolean isCorrectNode(final Node jcrNode) throws Exception {
		return jcrNode.getName().startsWith(JcrNodeType.NODE.toString())
				|| jcrNode.getName().startsWith(
						JcrNodeType.MULTIPLE_NODE_PROPERTY.toString())
				|| jcrNode.getName().startsWith(
						JcrNodeType.NODE_PROPERTY.toString());
	}

	private static void nullParentProperty(final Serializable obj)
			throws Exception {
		if (obj == null) {
			return;
		}
		setParentProperty(obj, null);
	}

	/**
	 * Save complex properties on jcr.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param session
	 *            the session
	 * @param parent
	 *            the parent
	 * @throws ValueFormatException
	 *             the value format exception
	 * @throws VersionException
	 *             the version exception
	 * @throws LockException
	 *             the lock exception
	 * @throws ConstraintViolationException
	 *             the constraint violation exception
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws Exception
	 *             the exception
	 */
	private static void saveComplexMultiplePropertiesOnJcr(
			final Session session, final BeanDescriptor descriptor,
			final Node parent) throws Exception {
		for (final Map.Entry<String, ComplexMultiplePropertyDescriptor> entry : descriptor.multipleComplexProperties
				.entrySet()) {
			final String keyValue = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_KEY_VALUE, entry
							.getKey());
			final String type = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_MULTIPLE_TYPE, entry
							.getKey());
			final String valueType = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_VALUE_TYPE, entry
							.getKey());
			final String keyType = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_KEY_TYPE, entry
							.getKey());
			final ComplexMultiplePropertyDescriptor desc = entry.getValue();
			final String nodeName = SimplePersistSupport.getNodeName(
					JcrNodeType.MULTIPLE_NODE_PROPERTY, null, entry.getKey());
			final NodeIterator existentNodesIterator = parent
					.getNodes(nodeName);
			final HashMap<String, Node> existentNodes = new HashMap<String, Node>();
			while (existentNodesIterator.hasNext()) {
				final Node existentNode = existentNodesIterator.nextNode();
				final String hash = existentNode.getProperty(
						SimplePersistSupport.HASH_VALUE).getString();
				existentNodes.put(hash, existentNode);

			}
			for (final Pair<String, BeanDescriptor> propertyEntry : desc.valuesAsBeanDescriptors) {
				final Node propertyNode = SimplePersistSupport
						.addUpdateOrRemoveJcrNode(
								JcrNodeType.MULTIPLE_NODE_PROPERTY, session,
								parent, propertyEntry.getK2(), entry.getKey());
				setPropertyOnJcrNode(propertyNode, type, desc.multipleType);
				setPropertyOnJcrNode(propertyNode, valueType, desc.valueType);
				if (desc.keyType != null) {
					setPropertyOnJcrNode(propertyNode, keyType, desc.keyType);
					setPropertyOnJcrNode(propertyNode, keyValue, propertyEntry
							.getK1());
				}
				final String hash = propertyNode.getProperty(
						SimplePersistSupport.HASH_VALUE).getString();
				existentNodes.remove(hash);

			}
			for (final Map.Entry<String, Node> toBeRemoved : existentNodes
					.entrySet()) {
				toBeRemoved.getValue().remove();
			}
		}
	}

	/**
	 * Save simple properties on jcr.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param result
	 *            the result
	 * @throws ValueFormatException
	 *             the value format exception
	 * @throws VersionException
	 *             the version exception
	 * @throws LockException
	 *             the lock exception
	 * @throws ConstraintViolationException
	 *             the constraint violation exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private static void saveSimplePropertiesOnJcr(
			final BeanDescriptor descriptor, final Node result)
			throws ValueFormatException, VersionException, LockException,
			ConstraintViolationException, RepositoryException {
		for (final Map.Entry<String, SimpleMultiplePropertyDescriptor> entry : descriptor.multipleSimpleProperties
				.entrySet()) {
			final String keys = MessageFormat
					.format(SimplePersistSupport.MULTIPLE_PROPERTY_KEYS, entry
							.getKey());
			final String values = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_VALUES, entry
							.getKey());
			final String type = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_MULTIPLE_TYPE, entry
							.getKey());
			final String valueType = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_VALUE_TYPE, entry
							.getKey());
			final String keyType = MessageFormat.format(
					SimplePersistSupport.MULTIPLE_PROPERTY_KEY_TYPE, entry
							.getKey());
			final SimpleMultiplePropertyDescriptor desc = entry.getValue();
			setPropertyOnJcrNode(result, type, desc.multipleType);
			setPropertyOnJcrNode(result, valueType, desc.valueType);
			setPropertyOnJcrNode(result, values, desc.valuesAsStrings
					.toArray(new String[0]));

			if (desc.keyType != null) {
				setPropertyOnJcrNode(result, keyType, desc.keyType);
				setPropertyOnJcrNode(result, keys, desc.keysAsStrings
						.toArray(new String[0]));
			}
		}
	}

	/**
	 * Sets the complex collection property from descriptor to bean.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param newObject
	 *            the new object
	 * @param desc
	 *            the desc
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void setComplexCollectionPropertyFromDescriptorToBean(
			final BeanDescriptor beanDescriptor, final T newObject,
			final PropertyDescriptor desc) throws Exception {
		final ComplexMultiplePropertyDescriptor multipleBeanDescriptor = beanDescriptor.multipleComplexProperties
				.get(desc.getName());
		final Class<? extends Collection> type = (Class<? extends Collection>) desc
				.getPropertyType();
		if (multipleBeanDescriptor != null) {
			final Collection<Object> instance = org.openspotlight.common.util.Collections
					.createNewCollection(type,
							multipleBeanDescriptor.valuesAsBeanDescriptors
									.size());
			for (final Pair<String, BeanDescriptor> valueAsDescriptor : multipleBeanDescriptor.valuesAsBeanDescriptors) {
				final Object valueAsObject = SimplePersistSupport
						.createBeanFromDescriptor(valueAsDescriptor.getK2(),
								newObject);
				instance.add(valueAsObject);
			}
			desc.getWriteMethod().invoke(newObject, instance);
		}
	}

	/**
	 * Sets the complex map property from descriptor to bean.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param newObject
	 *            the new object
	 * @param desc
	 *            the desc
	 * @throws Exception
	 *             the exception
	 */
	private static <T> void setComplexMapPropertyFromDescriptorToBean(
			final BeanDescriptor beanDescriptor, final T newObject,
			final PropertyDescriptor desc) throws Exception {
		final ComplexMultiplePropertyDescriptor multipleBeanDescriptor = beanDescriptor.multipleComplexProperties
				.get(desc.getName());
		if (multipleBeanDescriptor != null) {
			final Map<Object, Object> map = new HashMap<Object, Object>();
			final Class<?> keyType = Class
					.forName(multipleBeanDescriptor.keyType);
			for (final Pair<String, BeanDescriptor> valueAsDescriptor : multipleBeanDescriptor.valuesAsBeanDescriptors) {
				final Object valueAsObject = SimplePersistSupport
						.createBeanFromDescriptor(valueAsDescriptor.getK2(),
								newObject);
				final Object keyAsObject = Conversion.convert(valueAsDescriptor
						.getK1(), keyType);
				map.put(keyAsObject, valueAsObject);
			}
			desc.getWriteMethod().invoke(newObject, map);
		}
	}

	/**
	 * Sets the jcr complex multiple property on descriptor.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param property
	 *            the property
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @param session
	 *            the session
	 * @throws Exception
	 *             the exception
	 */
	private static void setJcrComplexMultiplePropertyOnDescriptor(
			final BeanDescriptor descriptor, final Node property,
			final LazyType multipleLoadingStrategy, final Session session)
			throws Exception {
		final String propertyName = Strings.removeBegginingFrom(
				JcrNodeType.MULTIPLE_NODE_PROPERTY.toString() + "_", property
						.getName());
		final String keyValueDescription = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_KEY_VALUE, propertyName);
		final String typeDescription = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_MULTIPLE_TYPE,
				propertyName);
		final String valueTypeDescription = MessageFormat
				.format(SimplePersistSupport.MULTIPLE_PROPERTY_VALUE_TYPE,
						propertyName);
		final String keyTypeDescription = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_KEY_TYPE, propertyName);
		ComplexMultiplePropertyDescriptor desc = descriptor.multipleComplexProperties
				.get(propertyName);
		if (desc == null) {
			desc = new ComplexMultiplePropertyDescriptor();
			desc.multipleType = property.getProperty(typeDescription)
					.getString();
			desc.valueType = property.getProperty(valueTypeDescription)
					.getString();
			if (property.hasProperty(keyTypeDescription)) {
				desc.keyType = property.getProperty(keyTypeDescription)
						.getString();
			}
			descriptor.multipleComplexProperties.put(propertyName, desc);
		}
		String keyString = null;
		if (property.hasProperty(keyValueDescription)) {
			keyString = property.getProperty(keyValueDescription).getString();
		}
		// FIXME here needs to apply lazy loading!
		final BeanDescriptor beanDescriptor = SimplePersistSupport
				.createDescriptorFromJcr(property, descriptor,
						multipleLoadingStrategy, session);
		final Pair<String, BeanDescriptor> pair = new Pair<String, BeanDescriptor>(
				keyString, beanDescriptor);
		desc.valuesAsBeanDescriptors.add(pair);
	}

	/**
	 * Sets the jcr node property on descriptor.
	 * 
	 * @param descriptor
	 *            the descriptor
	 * @param property
	 *            the property
	 * @param multipleLoadingStrategy
	 *            the multiple loading strategy
	 * @param session
	 *            the session
	 * @throws Exception
	 *             the exception
	 */
	private static void setJcrNodePropertyOnDescriptor(
			final BeanDescriptor descriptor, final Node property,
			final LazyType multipleLoadingStrategy, final Session session)
			throws Exception {

		final String propName = property.getProperty(
				SimplePersistSupport.PROPERTY_NAME).getString();
		final BeanDescriptor propertyDesc = SimplePersistSupport
				.createDescriptorFromJcr(property, descriptor,
						multipleLoadingStrategy, session);
		descriptor.nodeProperties.put(propName, propertyDesc);
	}

	/**
	 * Sets the multiple property from jcr to descriptor.
	 * 
	 * @param jcrNode
	 *            the jcr node
	 * @param descriptor
	 *            the descriptor
	 * @param multiplePropertiesAlreadyLoaded
	 *            the multiple properties already loaded
	 * @param rawName
	 *            the raw name
	 * @throws ValueFormatException
	 *             the value format exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private static void setMultiplePropertyFromJcrToDescriptor(
			final Node jcrNode, final BeanDescriptor descriptor,
			final Set<String> multiplePropertiesAlreadyLoaded,
			final String rawName) throws ValueFormatException,
			RepositoryException {
		String name = Strings.removeBegginingFrom(
				SimplePersistSupport.DEFAULT_MULTIPLE_PROPERTY_PREFIX, rawName);
		name = name.substring(0, name.indexOf('.'));
		if (multiplePropertiesAlreadyLoaded.contains(name)) {
			return;
		}
		final SimpleMultiplePropertyDescriptor desc = new SimpleMultiplePropertyDescriptor();
		final String keys = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_KEYS, name);
		final String values = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_VALUES, name);
		final String type = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_MULTIPLE_TYPE, name);
		final String valueType = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_VALUE_TYPE, name);
		final String keyType = MessageFormat.format(
				SimplePersistSupport.MULTIPLE_PROPERTY_KEY_TYPE, name);
		try {
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
			multiplePropertiesAlreadyLoaded.add(name);

		} catch (final PathNotFoundException e) {
			// value for multiple property not setted yet. Do not need to be
			// worried about this exception

		}
	}

	/**
	 * Sets the node property from jcr to bean.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param parent
	 *            the parent
	 * @param newObject
	 *            the new object
	 * @param desc
	 *            the desc
	 * @param propertyName
	 *            the property name
	 * @throws Exception
	 *             the exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 */
	private static <T> void setNodePropertyFromJcrToBean(
			final BeanDescriptor beanDescriptor, final Object parent,
			final T newObject, final PropertyDescriptor desc,
			final String propertyName) throws Exception,
			IllegalAccessException, InvocationTargetException {
		final BeanDescriptor propertyDesc = beanDescriptor.nodeProperties
				.get(propertyName);
		final Object bean = SimplePersistSupport.createBeanFromDescriptor(
				propertyDesc, parent);
		desc.getWriteMethod().invoke(newObject, bean);
	}

	@SuppressWarnings("unchecked")
	private static void setParentProperty(final Serializable serializable,
			final SimpleNodeType parent) throws Exception {
		if (serializable == null) {
			return;
		}
		if (serializable instanceof StreamPropertyWithParent<?>) {
			final StreamPropertyWithParent<SimpleNodeType> property = (StreamPropertyWithParent<SimpleNodeType>) serializable;
			property.setParent(parent);
			return;
		}
		if (serializable instanceof Collection<?>) {
			final Collection<?> collection = (Collection<?>) serializable;
			for (final Object o : collection) {
				setParentProperty((Serializable) o, parent);
			}
			return;
		}
		if (serializable instanceof Map<?, ?>) {
			final Map<?, ?> map = (Map<?, ?>) serializable;
			for (final Map.Entry<?, ?> entry : map.entrySet()) {
				setParentProperty((Serializable) entry.getValue(), parent);
			}
			return;
		}

	}

	/**
	 * Sets the property from bean to descriptor.
	 * 
	 * @param bean
	 *            the bean
	 * @param descriptor
	 *            the descriptor
	 * @param desc
	 *            the desc
	 * @param typeDescription
	 *            the type description
	 * @param valueDescription
	 *            the value description
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @throws SLException
	 *             the SL exception
	 */
	private static <T> void setPropertyFromBeanToDescriptor(final T bean,
			final BeanDescriptor descriptor, final PropertyDescriptor desc,
			final String typeDescription, final String valueDescription)
			throws IllegalAccessException, InvocationTargetException,
			SLException {
		final String propValue = MessageFormat.format(valueDescription, desc
				.getName());
		final String propType = MessageFormat.format(typeDescription, desc
				.getName());
		final Object value = desc.getReadMethod().invoke(bean);
		final String valueTypeString = desc.getPropertyType().getName();
		final String valueAsString = Conversion.convert(value, String.class);
		descriptor.properties.put(propValue, valueAsString);
		descriptor.properties.put(propType, valueTypeString);
	}

	/**
	 * Sets the property from descriptor to bean.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param newObject
	 *            the new object
	 * @param desc
	 *            the desc
	 * @param propertyName
	 *            the property name
	 * @param type
	 *            the type
	 * @param value
	 *            the value
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws SLException
	 *             the SL exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 */
	private static <T> void setPropertyFromDescriptorToBean(
			final BeanDescriptor beanDescriptor, final T newObject,
			final PropertyDescriptor desc, final String propertyName,
			final String type, final String value)
			throws ClassNotFoundException, SLException, IllegalAccessException,
			InvocationTargetException {
		final String propertyTypeString = beanDescriptor.properties
				.get(MessageFormat.format(type, propertyName));
		final String propertyValueAsString = beanDescriptor.properties
				.get(MessageFormat.format(value, propertyName));
		Class<?> propertyType = Conversion
				.getPrimitiveClass(propertyTypeString);
		if (propertyType == null) {
			if (propertyTypeString == null) {
				return;// no property at all
			}
			propertyType = Class.forName(propertyTypeString);
		}
		final Object newPropertyValue = Conversion.convert(
				propertyValueAsString, propertyType);
		desc.getWriteMethod().invoke(newObject, newPropertyValue);
	}

	private static void setPropertyOnJcrNode(final Node jcrNode,
			final String propertyName, final String propertyValue)
			throws RepositoryException {
		if (jcrNode.hasProperty(propertyName)) {
			final String value = jcrNode.getProperty(propertyName).getString();
			if (!Equals.eachEquality(value, propertyValue)) {
				jcrNode.setProperty(propertyName, propertyValue);
			}
		} else {
			jcrNode.setProperty(propertyName, propertyValue);
		}
	}

	private static void setPropertyOnJcrNode(final Node jcrNode,
			final String propertyName, final String propertyValue[])
			throws RepositoryException {
		if (jcrNode.hasProperty(propertyName)) {
			final Value[] rawValues = jcrNode.getProperty(propertyName)
					.getValues();
			final String[] values = new String[rawValues != null ? rawValues.length
					: 0];
			if (rawValues != null) {
				for (int i = 0, size = rawValues.length; i < size; i++) {
					values[i] = rawValues[i].getString();
				}
			}
			if (!java.util.Arrays.equals(values, propertyValue)) {
				jcrNode.setProperty(propertyName, propertyValue);

			}
		} else {
			jcrNode.setProperty(propertyName, propertyValue);

		}
	}

	/**
	 * Sets the simple collection property from descriptor to bean.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param newObject
	 *            the new object
	 * @param desc
	 *            the desc
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws SLException
	 *             the SL exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> void setSimpleCollectionPropertyFromDescriptorToBean(
			final BeanDescriptor beanDescriptor, final T newObject,
			final PropertyDescriptor desc) throws ClassNotFoundException,
			SLException, IllegalAccessException, InvocationTargetException {
		final SimpleMultiplePropertyDescriptor multipleBeanDescriptor = beanDescriptor.multipleSimpleProperties
				.get(desc.getName());
		final Class<? extends Collection> type = (Class<? extends Collection>) desc
				.getPropertyType();
		final Collection<Object> instance = org.openspotlight.common.util.Collections
				.createNewCollection(type,
						multipleBeanDescriptor.valuesAsStrings.size());
		final Class<?> valueType = Class
				.forName(multipleBeanDescriptor.valueType);
		for (final String valueAsString : multipleBeanDescriptor.valuesAsStrings) {
			Object valueAsObject = null;
			if (valueAsString != null) {
				valueAsObject = Conversion.convert(valueAsString, valueType);
			}
			instance.add(valueAsObject);
		}
		desc.getWriteMethod().invoke(newObject, instance);
	}

	/**
	 * Sets the simple map property from descriptor to bean.
	 * 
	 * @param beanDescriptor
	 *            the bean descriptor
	 * @param newObject
	 *            the new object
	 * @param desc
	 *            the desc
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws SLException
	 *             the SL exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 */
	private static <T> void setSimpleMapPropertyFromDescriptorToBean(
			final BeanDescriptor beanDescriptor, final T newObject,
			final PropertyDescriptor desc) throws ClassNotFoundException,
			SLException, IllegalAccessException, InvocationTargetException {
		final SimpleMultiplePropertyDescriptor multipleBeanDescriptor = beanDescriptor.multipleSimpleProperties
				.get(desc.getName());
		final List<Object> values = new ArrayList<Object>();
		final List<Object> keys = new ArrayList<Object>();

		final Class<?> valueType = Class
				.forName(multipleBeanDescriptor.valueType);
		for (final String valueAsString : multipleBeanDescriptor.valuesAsStrings) {
			Object valueAsObject = null;
			if (valueAsString != null) {
				valueAsObject = Conversion.convert(valueAsString, valueType);
			}
			values.add(valueAsObject);
		}
		final Class<?> keyType = Class.forName(multipleBeanDescriptor.keyType);
		for (final String keyAsString : multipleBeanDescriptor.keysAsStrings) {
			final Object keyAsObject = Conversion.convert(keyAsString, keyType);

			keys.add(keyAsObject);
		}
		final Map<Object, Object> map = Arrays.map(keys.toArray(), values
				.toArray());
		desc.getWriteMethod().invoke(newObject, map);
	}

}
