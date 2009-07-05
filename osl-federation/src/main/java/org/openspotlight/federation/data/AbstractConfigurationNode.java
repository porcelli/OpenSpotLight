package org.openspotlight.federation.data;

import static java.text.MessageFormat.format;
import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Assertions.checkNullMandatory;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndThrow;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jcip.annotations.ThreadSafe;

/**
 * Class to abstract thread safe {@link ConfigurationNode} creation. All the
 * public methods of this class are final. The correct way to extend this
 * abstract class is to implement its abstract methods and also to create some
 * new methods to be used as a syntactic sugar.
 * 
 * The following example class has a parent node called Repository and a child
 * node called Bundle, and a bunch of properties. Here's some example on how to
 * extend this class:
 * 
 * <pre>
 * &#064;ThreadSafe
 * public final class Project extends AbstractConfigurationNode {
 * 
 * 	private static final long serialVersionUID = 2046784379709017337L;
 * 
 * 	public Project(String name, Repository repository) {
 * 		super(name, repository, PROPERTY_TYPES);
 * 	}
 * 
 * 	private static final String ACTIVE = &quot;active&quot;;
 * 
 * 	&#064;SuppressWarnings(&quot;unchecked&quot;)
 * 	private static final Map&lt;String, Class&lt;?&gt;&gt; PROPERTY_TYPES = map(
 * 			ofKeys(ACTIVE), andValues(Boolean.class));
 * 
 * 	public Boolean getActive() {
 * 		return getProperty(ACTIVE);
 * 	}
 * 
 * 	public void setActive(Boolean active) {
 * 		setProperty(ACTIVE, active);
 * 	}
 * 
 * 	public void addBundle(Bundle bundle) {
 * 		addChild(bundle);
 * 	}
 * 
 * 	public void removeBundle(Bundle bundle) {
 * 		removeChild(bundle);
 * 	}
 * 
 * 	public Collection&lt;Bundle&gt; getBundles() {
 * 		return super.getChildrensOfType(Bundle.class);
 * 	}
 * 
 * 	public Bundle getBundleByName(String name) {
 * 		return super.getChildByName(Bundle.class, name);
 * 	}
 * 
 * 	public Set&lt;String&gt; getBundleNames() {
 * 		return super.getNamesFromChildrenOfType(Bundle.class);
 * 	}
 * 
 * 	public Repository getRepository() {
 * 		return getParent();
 * 	}
 * 
 * 	private static final Set&lt;Class&lt;?&gt;&gt; CHILDREN_CLASSES = new HashSet&lt;Class&lt;?&gt;&gt;();
 * 	static {
 * 		CHILDREN_CLASSES.add(Bundle.class);
 * 	}
 * 
 * 	&#064;Override
 * 	public Set&lt;Class&lt;?&gt;&gt; getChildrenTypes() {
 * 		return CHILDREN_CLASSES;
 * 	}
 * 
 * 	&#064;Override
 * 	public Class&lt;?&gt; getParentType() {
 * 		return Repository.class;
 * 	}
 * 
 * }
 * 
 * </pre>
 * 
 * @author feu
 * 
 */
@ThreadSafe
public abstract class AbstractConfigurationNode implements ConfigurationNode {

	/**
	 * Protected constructor that sets all the final fields and verify if the
	 * inheritance was correct by verifying the parent node, creating entries
	 * for children classes and so on.
	 * 
	 * This constructor also will share some properties of the parent node, such
	 * as dirty flag, it's listeners and it's change cache.
	 * 
	 * @param <N>
	 * @param name
	 * @param parent
	 * @param propertyTypes
	 */
	protected <N extends AbstractConfigurationNode> AbstractConfigurationNode(
			String name, N parent, Map<String, Class<?>> propertyTypes) {
		checkNotEmpty("name", name);
		checkNotNull("propertyTypes", propertyTypes);
		if (getParentType() != null) {
			checkNotNull("parent", parent);
			checkCondition("correctParentClass", this.getParentType().equals(
					parent.getClass()));
		} else {
			checkNullMandatory("parent", parent);
		}
		checkNotNull("childrenNodeClasses", getChildrenTypes());
		this.propertyTypes = propertyTypes;
		this.name = name;
		this.parent = parent;
		if (this.parent != null) {
			parent.addChild(this);
			nodeListeners = parent.nodeListeners;
			propertyListeners = parent.propertyListeners;

			nodeChangeCache = parent.nodeChangeCache;
			propertyChangeCache = parent.propertyChangeCache;

			dirtyFlag = parent.dirtyFlag;

			cacheEntryLock = parent.cacheEntryLock;
		} else {
			nodeListeners = synchronizedList(new LinkedList<ItemEventListener<ConfigurationNode>>());
			propertyListeners = synchronizedList(new LinkedList<ItemEventListener<PropertyValue>>());

			nodeChangeCache = synchronizedList(new LinkedList<ItemChangeEvent<ConfigurationNode>>());
			propertyChangeCache = synchronizedList(new LinkedList<ItemChangeEvent<PropertyValue>>());

			dirtyFlag = new AtomicBoolean(false);

			cacheEntryLock = new Object();
		}
		for (Class<?> childClass : getChildrenTypes()) {
			children.put(childClass, new HashMap<String, ConfigurationNode>());
		}
	}

	private static final long serialVersionUID = 75811796109301931L;

	/**
	 * reference for the property types of this kind of node.
	 */
	private final Map<String, Class<?>> propertyTypes;

	/**
	 * Lock for all the changes on all nodes of the current graph.
	 */
	private final Object cacheEntryLock;

	/**
	 * dirty flag for all nodes of the current graph.
	 */
	private final AtomicBoolean dirtyFlag;

	/**
	 * All the node listeners for all nodes of the current graph.
	 */
	private final List<ItemEventListener<ConfigurationNode>> nodeListeners;

	/**
	 * All the property listeners for all nodes of the current graph.
	 */
	private final List<ItemEventListener<PropertyValue>> propertyListeners;

	/**
	 * Property change cache that store all the property change events since the
	 * last {@link #markAsSaved()} method call. This cache is used by all nodes
	 * of the current graph.
	 */
	private final List<ItemChangeEvent<PropertyValue>> propertyChangeCache;

	/**
	 * Node change cache that store all the node change events since the last
	 * {@link #markAsSaved()} method call. This cache is used by all nodes of
	 * the current graph.
	 */
	private final List<ItemChangeEvent<ConfigurationNode>> nodeChangeCache;

	/**
	 * volatile field to be used on {@link #toString()} method
	 */
	private volatile String description = null;

	/**
	 * Mandatory name
	 */
	private final String name;

	/**
	 * hashCode cache
	 */
	private volatile int hashcode = 0;

	/**
	 * optional parent node
	 */
	private final ConfigurationNode parent;

	/**
	 * Children node map that contains the children classes and a map with its
	 * entries.
	 */
	private final Map<Class<?>, Map<String, ConfigurationNode>> children = new HashMap<Class<?>, Map<String, ConfigurationNode>>();

	/**
	 * Property map.
	 */
	private final Map<String, Serializable> properties = new HashMap<String, Serializable>();

	/**
	 * Transient property map. This properties won't be saved.
	 */
	private final Map<String, Object> transientProperties = new HashMap<String, Object>();

	/**
	 * {@inheritDoc}
	 */
	public abstract Class<?> getParentType();

	/**
	 * {@inheritDoc}
	 */
	public abstract Set<Class<?>> getChildrenTypes();

	/**
	 * {@inheritDoc}
	 */
	public final Map<String, Class<?>> getPropertyTypes() {
		return unmodifiableMap(propertyTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final <N extends ConfigurationNode> N getParent() {
		return (N) this.parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Map<String, Serializable> getProperties() {
		return unmodifiableMap(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final <N extends ConfigurationNode> Collection<N> getChildrensOfType(
			Class<N> childClass) {
		checkNotNull("childClass", childClass);
		checkCondition("childClassOwned", getChildrenTypes().contains(
				childClass));
		Map<String, ConfigurationNode> childrenMap = children.get(childClass);
		Collection<ConfigurationNode> childrenCollection = childrenMap.values();
		return (Collection<N>) unmodifiableCollection(childrenCollection);
	}

	/**
	 * {@inheritDoc}
	 */
	public final Set<String> getNamesFromChildrenOfType(
			Class<? extends ConfigurationNode> childClass) {
		checkNotNull("childClass", childClass);
		checkCondition("childClassOwned", getChildrenTypes().contains(
				childClass));
		Map<String, ConfigurationNode> childrenMap = children.get(childClass);
		Set<String> childrenNames = childrenMap.keySet();
		return unmodifiableSet(childrenNames);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final <N extends ConfigurationNode> N getChildByName(
			Class<N> childClass, String name) {
		checkNotNull("childClass", childClass);
		checkNotEmpty("name", name);
		checkCondition("childClassOwned", getChildrenTypes().contains(
				childClass));
		return (N) children.get(childClass).get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public final synchronized <N extends ConfigurationNode> void addChild(
			N child) {
		checkNotNull("child", child);
		checkCondition("childClassOwned", getChildrenTypes().contains(
				child.getClass()));
		if (children.containsValue(child)) {
			return;
		}
		ConfigurationNode oldNode = getChildByName(child.getClass(), child
				.getName());
		children.get(child.getClass()).put(child.getName(), child);
		fireNodeChange(oldNode, child);
	}

	/**
	 * {@inheritDoc}
	 */
	final synchronized <N extends ConfigurationNode> void removeChild(N child) {
		checkNotNull("child", child);
		checkCondition("childClassOwned", getChildrenTypes().contains(
				child.getClass()));
		if (!children.get(child.getClass()).containsValue(child)) {
			return;
		}
		children.get(child.getClass()).put(child.getName(), null);
		fireNodeChange(child, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final <N extends Serializable> N getProperty(String name) {
		checkNotEmpty("name", name);
		N value = (N) properties.get(name);
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final synchronized <N extends Serializable> void setProperty(
			String name, N value) {
		checkNotEmpty("name", name);
		if (value != null && !value.getClass().equals(propertyTypes.get(name))) {
			logAndThrow(new AssertionError(
					format(
							"Error on property type mapping on class {0} for property named {1} and value class {2}.",
							getClass(), name, value.getClass())));
		}
		N oldValue = (N) getProperty(name);
		if (!eachEquality(oldValue, value)) {
			properties.put(name, value);
			firePropertyChange(name, oldValue, value);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public final <N> N getTransientProperty(String name) {
		checkNotEmpty("name", name);
		return (N) transientProperties.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	public final synchronized <N> void setTransientProperty(String name, N value) {
		checkNotEmpty("name", name);
		transientProperties.put(name, value);
	}

	/**
	 * Equals method that is safe to use also over an inheritance. Should not be
	 * overloaded.
	 */
	@Override
	public final boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AbstractConfigurationNode))
			return false;
		AbstractConfigurationNode that = (AbstractConfigurationNode) o;
		return eachEquality(of(this.getClass(), this.getName(), this
				.getParent()), andOf(that.getClass(), that.getName(), that
				.getParent()));

	}

	/**
	 * Hash code that uses the same field of the equals method.
	 */
	@Override
	public final int hashCode() {
		if (hashcode == 0) {
			hashcode = hashOf(this.getClass(), this.getName(), this.getParent());
		}
		return hashcode;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public final int compareTo(ConfigurationNode o) {
		return toString().compareTo(o.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		if (description == null) {
			this.description = "AbstractConfigurationNode["
					+ getClass().getName() + "] " + name;
		}
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addNodeListener(
			ItemEventListener<ConfigurationNode> listener) {
		nodeListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addPropertyListener(
			ItemEventListener<PropertyValue> listener) {
		propertyListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public final List<ItemChangeEvent<ConfigurationNode>> getNodeChangesSinceLastSave() {
		return unmodifiableList(nodeChangeCache);
	}

	/**
	 * {@inheritDoc}
	 */
	public final List<ItemChangeEvent<PropertyValue>> getPropertyChangesSinceLastSave() {
		return unmodifiableList(propertyChangeCache);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isDirty() {
		return dirtyFlag.get();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void markAsSaved() {
		synchronized (cacheEntryLock) {
			dirtyFlag.set(false);
			propertyChangeCache.clear();
			nodeChangeCache.clear();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void markAsDirty() {
		dirtyFlag.set(true);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removeNodeListener(
			ItemEventListener<ConfigurationNode> listener) {
		nodeListeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removePropertyListener(
			ItemEventListener<PropertyValue> listener) {
		propertyListeners.remove(listener);
	}

	/**
	 * Method used to fire a property change event. It discovers the event type
	 * based on old and new values. This method is synchronized on the
	 * notification part that is common for all nodes of the current graph. This
	 * method will mark the dirty flag that is common for all the nodes of the
	 * current graph.
	 * 
	 * @param propertyName
	 *            the name of the property.
	 * @param oldValue
	 *            the convenient values of the property before the change.
	 * @param newValue
	 *            the convenient values of the property after the change.
	 */
	private void firePropertyChange(String propertyName, Serializable oldValue,
			Serializable newValue) {
		checkNotEmpty("propertyName", propertyName);
		if (eachEquality(oldValue, newValue)) {
			return; // no changes at all
		}
		ItemChangeType type;
		if (oldValue == null && newValue != null) {
			type = ItemChangeType.ADDED;
		} else if (oldValue != null && newValue == null) {
			type = ItemChangeType.EXCLUDED;
		} else {
			type = ItemChangeType.CHANGED;
		}
		PropertyValue newPropertyValue = new PropertyValue(propertyName,
				newValue, this);
		PropertyValue oldPropertyValue = new PropertyValue(propertyName,
				oldValue, this);
		ItemChangeEvent<PropertyValue> changeEvent = new ItemChangeEvent<PropertyValue>(
				type, oldPropertyValue, newPropertyValue);

		synchronized (cacheEntryLock) {
			propertyChangeCache.add(changeEvent);
			dirtyFlag.set(true);
		}
		synchronized (propertyListeners) {
			for (ItemEventListener<PropertyValue> listener : propertyListeners) {
				listener.changeEventHappened(changeEvent);
			}
		}
	}

	/**
	 * Method used to fire a node change event. It discovers the event type
	 * based on old and new values. This method is synchronized on the
	 * notification part that is common for all nodes of the current graph. This
	 * method will mark the dirty flag that is common for all the nodes of the
	 * current graph.
	 * 
	 * @param oldValue
	 *            the node value before the change event
	 * @param newValue
	 *            the node value after the change event
	 */
	private void fireNodeChange(ConfigurationNode oldValue,
			ConfigurationNode newValue) {
		if (eachEquality(of(oldValue), andOf(newValue))) {
			return; // no changes at all
		}
		ItemChangeType type;
		if (oldValue == null && newValue != null) {
			type = ItemChangeType.ADDED;
		} else if (oldValue != null && newValue == null) {
			type = ItemChangeType.EXCLUDED;
		} else {
			type = ItemChangeType.CHANGED; // should never happen at this actual
			// implementation
		}
		ItemChangeEvent<ConfigurationNode> changeEvent = new ItemChangeEvent<ConfigurationNode>(
				type, oldValue, newValue);

		synchronized (cacheEntryLock) {
			nodeChangeCache.add(changeEvent);
			dirtyFlag.set(true);
		}
		synchronized (nodeListeners) {
			for (ItemEventListener<ConfigurationNode> listener : nodeListeners) {
				listener.changeEventHappened(changeEvent);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void __dont_implement_Node__instead_extend_AbstractNode__() {
	}
}
