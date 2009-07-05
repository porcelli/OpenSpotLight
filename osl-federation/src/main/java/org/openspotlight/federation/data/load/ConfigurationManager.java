package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Strings.firstLetterToLowerCase;
import static org.openspotlight.common.util.Strings.firstLetterToUpperCase;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.AbstractConfigurationNode;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.ConfigurationNode;

/**
 * Interface responsible to load and save the group data on a persistent layer
 * 
 * @author feu
 * 
 */
public interface ConfigurationManager {

	public static final String DEFAULT_OSL_PREFIX = "osl";

	/**
	 * Loads the current group from configuration, marking the configuration as
	 * saved.
	 * 
	 * @return
	 */
	Configuration load() throws ConfigurationException;

	/**
	 * Saves the group on a persistent layer marking the current configuration
	 * as a saved configuration.
	 * 
	 * @param configuration
	 */
	void save(Configuration configuration) throws ConfigurationException;

	/**
	 * Helper class to map the class name to a valid node name and vice versa.
	 * 
	 * @author feu
	 * 
	 */
	public static class NodeClassHelper {

		public static final String DEFAULT_NODE_PACKAGE = AbstractConfigurationNode.class
				.getPackage().getName();

		/**
		 * Class and name cache
		 */
		private final Map<Class<? extends AbstractConfigurationNode>, String> cache = new ConcurrentHashMap<Class<? extends AbstractConfigurationNode>, String>();

		/**
		 * Returns a valid node name based on class name
		 * 
		 * @param nodeClass
		 * @return
		 */
		public String getNameFromNodeClass(
				Class<? extends AbstractConfigurationNode> nodeClass) {
			checkNotNull("nodeClass", nodeClass);
			checkCondition("samePackage", DEFAULT_NODE_PACKAGE.equals(nodeClass
					.getPackage().getName()));
			String name = cache.get(nodeClass);
			if (name == null) {
				name = nodeClass.getSimpleName();
				name = firstLetterToLowerCase(name);
				name = DEFAULT_OSL_PREFIX + ":" + name;
				cache.put(nodeClass, name);
			}
			return name;
		}

		/**
		 * Returns a valid node Class based on node class name
		 * 
		 * @param nodeClassName
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <N extends ConfigurationNode> Class<N> getNodeClassFromName(
				String nodeClassName) throws ConfigurationException {
			checkNotEmpty("nodeClassName", nodeClassName);
			checkCondition("nodeClassNameWithPrefix", nodeClassName
					.startsWith(DEFAULT_OSL_PREFIX + ":"));
			if (cache.containsValue(nodeClassName)) {
				for (Map.Entry<Class<? extends AbstractConfigurationNode>, String> entry : cache
						.entrySet()) {
					if (nodeClassName.equals(entry.getValue())) {
						return (Class<N>) entry.getKey();
					}
				}
			}
			String realClassName = removeBegginingFrom(
					DEFAULT_OSL_PREFIX + ":", nodeClassName);
			realClassName = firstLetterToUpperCase(realClassName);
			realClassName = DEFAULT_NODE_PACKAGE + "." + realClassName;
			try {
				Class<? extends AbstractConfigurationNode> clazz = (Class<? extends AbstractConfigurationNode>) Class
						.forName(realClassName);
				cache.put(clazz, nodeClassName);
				return (Class<N>) clazz;
			} catch (Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}

		}

		/**
		 * Create a new node instance based on a node name, a parent node and
		 * the class name. The node class should have a constructor with a
		 * string and a node, as in the super class {@link AbstractConfigurationNode}.
		 * 
		 * @param <N>
		 * @param nodeName
		 * @param parentNode
		 * @param nodeClassName
		 * @return
		 */
		public <N extends ConfigurationNode> N createInstance(String nodeName,
				ConfigurationNode parentNode, String nodeClassName)
				throws ConfigurationException {
			checkNotEmpty("nodeName", nodeName);
			checkNotNull("parentNode", parentNode);
			checkNotEmpty("nodeClassName", nodeClassName);
			checkCondition("nodeClassNameWithPrefix", nodeClassName
					.startsWith(DEFAULT_OSL_PREFIX + ":"));
			try {
				Class<N> clazz = getNodeClassFromName(nodeClassName);
				Constructor<N> constructor = clazz.getConstructor(String.class,
						parentNode.getClass());
				N node = constructor.newInstance(nodeName, parentNode);
				return node;
			} catch (Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}

		/**
		 * Creates a new root node instance based on class name. The node class
		 * should have a default constructor with calls the super constructor
		 * from {@AbstractConfigurationNode} using default arguments.
		 * 
		 * @param <N>
		 * @param nodeClassName
		 * @return
		 */
		public <N extends ConfigurationNode> N createRootInstance(String nodeClassName)
				throws ConfigurationException {
			checkNotEmpty("nodeClassName", nodeClassName);
			checkCondition("nodeClassNameWithPrefix", nodeClassName
					.startsWith(DEFAULT_OSL_PREFIX + ":"));
			try {
				Class<N> clazz = getNodeClassFromName(nodeClassName);
				N node = clazz.newInstance();
				return node;
			} catch (Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}
	}

	/**
	 * Helper class to help to deal with property types and classes
	 * 
	 * @author feu
	 * 
	 */
	public static class PropertyEntryHelper {

		/**
		 * Verify if that identifier is an osl property
		 */
		public boolean isPropertyNode(String propertyIdentifier) {
			checkNotEmpty("propertyIdentifier", propertyIdentifier);
			if (propertyIdentifier.startsWith(DEFAULT_OSL_PREFIX + ":")) {
				return true;
			}
			return false;
		}

	}
}
