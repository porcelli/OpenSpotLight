package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Dates.dateFromString;
import static org.openspotlight.common.util.Dates.stringFromDate;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrowNew;
import static org.openspotlight.common.util.Serialization.readFromBase64;
import static org.openspotlight.common.util.Serialization.serializeToBase64;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.AbstractConfigurationNode;
import org.openspotlight.federation.data.Configuration;

/**
 * Configuration manager that stores and loads the configuration from a
 * JcrRepository.
 * 
 * @author feu
 * 
 */
public class JcrSessionConfigurationManager implements ConfigurationManager {

	private static final String NS_DESCRIPTION = "www.openspotlight.org";

	/**
	 * Constructor. It's mandatory that the session is valid during object
	 * liveness.
	 * 
	 * @param session
	 *            valid session
	 * @throws ConfigurationException
	 */
	public JcrSessionConfigurationManager(Session session)
			throws ConfigurationException {
		checkNotNull("session", session);
		checkCondition("session", session.isLive());
		this.session = session;
		initDataInsideSession();
	}

	/**
	 * JCR session
	 */
	private final Session session;

	private final NodeClassHelper classHelper = new NodeClassHelper();

	private final PropertyEntryHelper propertyHelper = new PropertyEntryHelper();

	/**
	 * Just create the "osl" prefix if that one doesn't exists, and after that
	 * created the node "osl:configuration" if that doesn't exists.
	 * 
	 * @throws ConfigurationException
	 */
	private void initDataInsideSession() throws ConfigurationException {
		try {
			NamespaceRegistry namespaceRegistry = session.getWorkspace()
					.getNamespaceRegistry();
			if (!prefixExists(namespaceRegistry)) {
				namespaceRegistry.registerNamespace(DEFAULT_OSL_PREFIX,
						NS_DESCRIPTION);
			}
		} catch (Exception e) {
			logAndThrowNew(e, ConfigurationException.class);
		}
	}

	/**
	 * Verify if the prefix "osl" exists
	 * 
	 * @param namespaceRegistry
	 * @return true if exists
	 * @throws RepositoryException
	 */
	private boolean prefixExists(NamespaceRegistry namespaceRegistry)
			throws RepositoryException {
		String[] prefixes = namespaceRegistry.getPrefixes();
		boolean hasFound = false;
		for (String prefix : prefixes) {
			if (DEFAULT_OSL_PREFIX.equals(prefix)) {
				hasFound = true;
				break;
			}
		}
		return hasFound;
	}

	/**
	 * Method to create nodes on jcr only when necessary.
	 * 
	 * @param parentNode
	 * @param nodePath
	 * @return
	 * @throws ConfigurationException
	 */
	private Node createIfDontExists(Node parentNode, String nodePath)
			throws ConfigurationException {
		checkNotNull("parentNode", parentNode);
		checkNotEmpty("nodePath", nodePath);
		try {
			try {
				return session.getRootNode().getNode(nodePath);
			} catch (PathNotFoundException e) {
				Node newNode = parentNode.addNode(nodePath);
				return newNode;
			}
		} catch (Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Configuration load() throws ConfigurationException {
		checkCondition("sessionAlive", session.isLive());
		try {
			String defaultRootNode = classHelper
					.getNameFromNodeClass(Configuration.class);
			Node rootJcrNode = session.getRootNode().getNode(defaultRootNode);
			Configuration rootNode = new Configuration();
			load(rootJcrNode, rootNode);
			rootNode.markAsSaved();
			return rootNode;
		} catch (Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	/**
	 * Loads the newly created node and also it's properties and it's children
	 * 
	 * @param jcrNode
	 * @param configurationNode
	 * @throws Exception
	 */
	private void load(Node jcrNode, org.openspotlight.federation.data.ConfigurationNode configurationNode)
			throws Exception {
		loadProperties(jcrNode, configurationNode, configurationNode.getPropertyTypes());
		loadChildren(jcrNode, configurationNode);
	}

	@SuppressWarnings("unchecked")
	private void loadChildren(Node jcrNode,
			org.openspotlight.federation.data.ConfigurationNode configurationNode)
			throws PathNotFoundException, RepositoryException,
			ConfigurationException, Exception {
		Set<Class<?>> childClasses = configurationNode.getChildrenTypes();
		for (Class<?> childClass : childClasses) {
			String childNodeClassName = classHelper
					.getNameFromNodeClass((Class<? extends AbstractConfigurationNode>) childClass);
			Node childNode = jcrNode.getNode(childNodeClassName);
			NodeIterator grandChildren = childNode.getNodes();
			while (grandChildren.hasNext()) {
				Node grandChild = grandChildren.nextNode();
				String childName = removeBegginingFrom(
						DEFAULT_OSL_PREFIX + ":", grandChild.getName());
				org.openspotlight.federation.data.ConfigurationNode newNode = classHelper
						.createInstance(childName, configurationNode, childNodeClassName);
				load(grandChild, newNode);
			}
		}
	}

	private void loadProperties(Node jcrNode,
			org.openspotlight.federation.data.ConfigurationNode configurationNode,
			Map<String, Class<?>> propertyTypes) throws RepositoryException,
			ConfigurationException, Exception {
		PropertyIterator propertyIterator = jcrNode.getProperties();
		while (propertyIterator.hasNext()) {
			Property prop = propertyIterator.nextProperty();
			String propertyIdentifier = prop.getName();

			if (propertyHelper.isPropertyNode(propertyIdentifier)) {
				String nodeName = removeBegginingFrom(DEFAULT_OSL_PREFIX + ":",
						propertyIdentifier);
				Class<?> propertyClass = propertyTypes.get(nodeName);
				if (propertyClass != null) {
					Serializable value = getProperty(jcrNode,
							propertyIdentifier, propertyClass);
					configurationNode.setProperty(nodeName, value);
				}

			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void save(Configuration configuration) throws ConfigurationException {
		checkNotNull("group", configuration);
		checkCondition("sessionAlive", session.isLive());
		AbstractConfigurationNode node = configuration;
		try {
			String nodeStr = classHelper.getNameFromNodeClass(node.getClass());
			Node newJcrNode = createIfDontExists(session.getRootNode(), nodeStr);
			saveProperties(node, newJcrNode);

			saveChilds(node, newJcrNode);

			session.save();
			configuration.markAsSaved();
		} catch (Exception e) {
			logAndThrowNew(e, ConfigurationException.class);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveChilds(AbstractConfigurationNode node, Node newJcrNode)
			throws ConfigurationException, Exception {
		Set<Class<?>> classes = node.getChildrenTypes();
		for (Class<?> clazz : classes) {
			Class<? extends AbstractConfigurationNode> nodeClass = (Class<? extends AbstractConfigurationNode>) clazz;
			Set<String> childNames = node.getNamesFromChildrenOfType(nodeClass);
			String childNodeStr = classHelper.getNameFromNodeClass(nodeClass);
			Node newChildJcrNode = createIfDontExists(newJcrNode, childNodeStr);
			for (String childName : childNames) {
				AbstractConfigurationNode childNode = node.getChildByName(nodeClass,
						childName);
				Node newGranphChildNode = createIfDontExists(newChildJcrNode,
						DEFAULT_OSL_PREFIX + ":" + childName);
				saveProperties(childNode, newGranphChildNode);
				saveChilds(childNode, newGranphChildNode);
			}
		}
	}

	private void saveProperties(org.openspotlight.federation.data.ConfigurationNode configurationNode,
			Node innerNewJcrNode) throws Exception {
		Map<String, Serializable> properties = configurationNode.getProperties();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			Serializable value = entry.getValue();
			Class<?> clazz = value != null ? value.getClass() : null;
			setProperty(innerNewJcrNode, DEFAULT_OSL_PREFIX + ":"
					+ entry.getKey(), clazz, entry.getValue());
		}
	}

	/**
	 * Sets an property on a jcr node
	 * 
	 * @param jcrNode
	 * @param propertyName
	 * @param propertyClass
	 * @param value
	 * @throws Exception
	 */
	private void setProperty(Node jcrNode, String propertyName,
			Class<?> propertyClass, Serializable value) throws Exception {
		if (value == null) {
			jcrNode.setProperty(propertyName, (String) null);
		} else if (Boolean.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Boolean) value);
		} else if (Calendar.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Calendar) value);
		} else if (Double.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Double) value);
		} else if (Long.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Long) value);
		} else if (String.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (String) value);
		} else if (Integer.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Integer) value);
		} else if (Byte.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Byte) value);
		} else if (Float.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, (Float) value);
		} else if (Date.class.equals(propertyClass)) {
			jcrNode.setProperty(propertyName, stringFromDate((Date) value));
		} else {
			String valueAsString = serializeToBase64(value);
			jcrNode.setProperty(propertyName, valueAsString);
		}
	}

	/**
	 * Reads an property on jcr node
	 * 
	 * @param jcrNode
	 * @param propertyName
	 * @return
	 * @throws Exception
	 */
	private Serializable getProperty(Node jcrNode, String propertyName,
			Class<?> propertyClass) throws Exception {
		Property jcrProperty = null;
		Serializable value = null;
		try {
			jcrProperty = jcrNode.getProperty(propertyName);
		} catch (Exception e) {
			catchAndLog(e);
			return null;
		}
		if (Boolean.class.equals(propertyClass)) {
			value = jcrProperty.getBoolean();
		} else if (Calendar.class.equals(propertyClass)) {
			value = jcrProperty.getDate();
		} else if (Double.class.equals(propertyClass)) {
			value = jcrProperty.getDouble();
		} else if (Long.class.equals(propertyClass)) {
			value = jcrProperty.getLong();
		} else if (String.class.equals(propertyClass)) {
			value = jcrProperty.getString();
		} else if (Integer.class.equals(propertyClass)) {
			value = (int) jcrProperty.getLong();
		} else if (Byte.class.equals(propertyClass)) {
			value = (byte) jcrProperty.getLong();
		} else if (Float.class.equals(propertyClass)) {
			value = (float) jcrProperty.getDouble();
		} else if (Date.class.equals(propertyClass)) {
			if (jcrProperty.getString() != null)
				value = dateFromString(jcrProperty.getString());
		} else {
			String valueAsString = jcrProperty.getString();
			if (valueAsString != null)
				value = readFromBase64(valueAsString);
		}
		return value;
	}
}
