package org.openspotlight.federation.data;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Compare.compareAll;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface to abstract the ConfigurationNode. The configuration node is used
 * on configuration purposes and also to load the artifacts that will be
 * processed by the parsers on OSL.
 * 
 * It has a lot of methods to make possible to discover each node characteristic
 * without using reflection.
 * 
 * This interface has some mandatory methods just to tell the parent and
 * children classes. This methods needs to be used inside the implementation
 * (example: {@link AbstractConfigurationNode}) to verify if the parent classes
 * passed on constructor or the children classes are correct or not.
 * 
 * So, why do we need to to a lot of stuff like this instead of just using
 * simple java beans? There's a lot of good reasons to do that. First of all, it
 * contains the listener infrastructure to observe property and node changes.
 * Another reason is that the default implementation
 * {@link AbstractConfigurationNode} is by default thread safe. As these
 * configuration nodes should be used by a lot of parses at the same time, it is
 * mandatory to these classes to be thread safe.
 * 
 * TODO set the key property instead of assuming "name"
 * 
 * TODO work with no "key property" as an option
 * 
 * TODO Fix some generic issues.
 * 
 * TODO Guarantee that only one artifact will exist with that name.
 * 
 * TODO Throw an exception when changing some configuration in the listener.
 * 
 * @author feu
 * 
 */
public interface ConfigurationNode extends Serializable,
		Comparable<ConfigurationNode> {

	/**
	 * Describe it parent class. As it is not mandatory, it can not be made
	 * using generics. This is used to get the correct parent class on saving
	 * and loading the configuration node.
	 * 
	 * @return the parent node class, or null if the node is a root node
	 */
	Class<?> getParentType();

	/**
	 * Describe it children classes, as a node can have more than one type of
	 * children nodes. This is not mandatory, but is mandatory to return a not
	 * null set when it's empty.
	 * 
	 * @return the children classes or a empty set on the case that the current
	 *         configuration node doesn't have any child.
	 */
	Set<Class<?>> getChildrenTypes();

	/**
	 * At this implementation, the name is a mandatory property, and it is used
	 * to retrieve the children nodes using
	 * {@link #getChildByName(Class, String)} method.
	 * 
	 * @return the mandatory name of the configuration node.
	 */
	String getName();

	/**
	 * Used to reflect the kind of properties that this object has. It also is
	 * used on load and save purposes and to verify if the subclass is setting
	 * the property with the correct type.
	 * 
	 * @return a map with the property names and a type associated with each
	 *         name
	 */
	Map<String, Class<?>> getPropertyTypes();

	/**
	 * Getter for parent node.
	 * 
	 * @return the parent node or null if it's node is a root node.
	 */
	<N extends ConfigurationNode> N getParent();

	/**
	 * Used to reflect the properties that each node has associated with. Used
	 * on load and save purposes.
	 * 
	 * @return a map of property names and its values.
	 */
	Map<String, Serializable> getProperties();

	/**
	 * Returns a collection of child items by some type of child.
	 * 
	 * @return the children of the desired type.
	 */
	<N extends ConfigurationNode> Collection<N> getChildrensOfType(
			Class<N> childClass);

	/**
	 * Returns an set of child names based on a type of child.
	 * 
	 * @return the names currently associated with this type of child.
	 */
	Set<String> getNamesFromChildrenOfType(
			Class<? extends ConfigurationNode> childClass);

	/**
	 * Returns a child by it's type or name, or null in the case that it's child
	 * was not found.
	 * 
	 * @param name
	 *            of child
	 * @param childClass
	 *            the type of child class.
	 * @return the child itself or null when not found.
	 */
	<N extends ConfigurationNode> N getChildByName(Class<N> childClass,
			String name);

	/**
	 * Method to add a new child to this current node. It needs to verify if
	 * this child is of the correct type as specified on method
	 * {@link #getChildrenTypes()}.
	 * 
	 * @param child
	 *            the child that needs to be associated with this configuration
	 *            node.
	 */
	<N extends ConfigurationNode> void addChild(N child);

	/**
	 * Return an property with this name associated, or null if it was not
	 * found. It will throw an {@link ClassCastException} if the return type
	 * isn't the same as defined on {@link #getPropertyTypes()}.
	 * 
	 * @param <N>
	 *            the correct type of this property
	 * @param name
	 *            the correct name of this property
	 * @return the property value or null if there's no value associated with
	 *         this property.
	 */
	<N extends Serializable> N getProperty(String name);

	/**
	 * Sets an property with this name associated, or null if it was not found.
	 * It will throw an {@link ClassCastException} if the return type isn't the
	 * same as defined on {@link #getPropertyTypes()}.
	 * 
	 * @param <N>
	 *            the correct type of this property
	 * @param name
	 *            the correct name of this property
	 * @param value
	 *            the new value for that property.
	 */
	<N extends Serializable> void setProperty(String name, N value);

	/**
	 * Return an property that will not be persisted when this configuration is
	 * saved. It can be used to store temporary data. This kind of property
	 * needs to have it's type defined on the {@link #getPropertyTypes()} method
	 * as well as the non transient property. It will throw an
	 * {@link ClassCastException} if the return type isn't the same as defined
	 * on {@link #getPropertyTypes()}.
	 * 
	 * @param <N>
	 *            the correct type of this property
	 * @param name
	 *            the correct name of this property
	 * @return
	 */
	<N> N getTransientProperty(String name);

	/**
	 * Sets an property that will not be persisted when this configuration is
	 * saved. It can be used to store temporary data. This kind of property
	 * needs to have it's type defined on the {@link #getPropertyTypes()} method
	 * as well as the non transient property. It will throw an
	 * {@link ClassCastException} if the return type isn't the same as defined
	 * on {@link #getPropertyTypes()}.
	 * 
	 * @param <N>
	 *            the correct type of this property
	 * @param name
	 *            the correct name of this property
	 * @param value
	 *            the new value for that property.
	 */
	<N> void setTransientProperty(String name, N value);

	/**
	 * It will reset the dirty flag for all nodes, and also will clean the event
	 * cache for property changes and node changes as well.
	 */
	void markAsSaved();

	/**
	 * It will mark all the nodes on this graph as dirty, since one node is
	 * dirty, all of them needs to be.
	 */
	void markAsDirty();

	/**
	 * Returns the global dirty flag used on all nodes on current graph.
	 * 
	 * @return the global dirty flag.
	 */
	boolean isDirty();

	/**
	 * Adds an observer to listen the node adding or removal, since it's not
	 * possible to change a node itself, but only its properties. This observer
	 * will listen all the configuration node graph changes. <B>It's not okay to
	 * change node and property values inside its listeners</b>. It can result
	 * in unexpected results such as dead locks since the list of changes are
	 * synchronized and setting a new value will try to add a new change to the
	 * list of changes.
	 * 
	 * @param listener
	 *            that will listen node events.
	 */
	void addNodeListener(ItemEventListener<ConfigurationNode> listener);

	/**
	 * Removes an observer to the existing node observer chain, or do nothing if
	 * its listener is not listening the node events.
	 * 
	 * @param listener
	 *            to be removed from the observer chain.
	 */
	void removeNodeListener(ItemEventListener<ConfigurationNode> listener);

	/**
	 * Returns all the node changes since this node graph last
	 * {@link #markAsSaved()} method call. This changes are not for only the
	 * current node, but instead, for all the configuration node graph.
	 * 
	 * @return all the changes since last save.
	 */
	List<ItemChangeEvent<ConfigurationNode>> getNodeChangesSinceLastSave();

	/**
	 * Adds an observer to listen the property adding, changing or removal. This
	 * observer will listen all the property changes of all the node graph
	 * changes. <B>It's not okay to change node and property values inside its
	 * listeners</b>. It can result in unexpected results such as dead locks
	 * since the list of changes are synchronized and setting a new value will
	 * try to add a new change to the list of changes.
	 * 
	 * @param listener
	 *            that will listen property events.
	 */
	void addPropertyListener(ItemEventListener<PropertyValue> listener);

	/**
	 * Removes an observer to the existing node observer chain, or do nothing if
	 * its listener is not listening the property events.
	 * 
	 * @param listener
	 *            to be removed from the observer chain.
	 */
	void removePropertyListener(ItemEventListener<PropertyValue> listener);

	/**
	 * Returns all the property changes since this node graph last
	 * {@link #markAsSaved()} method call. This changes are not for only the
	 * current node, but instead, for all the configuration node graph.
	 * 
	 * @return all the changes since last save.
	 */
	List<ItemChangeEvent<PropertyValue>> getPropertyChangesSinceLastSave();

	/**
	 * This class is used to group a property change with possible needed data,
	 * such as owner node, property name and value.
	 * 
	 * Its used on property listener methods such as
	 * {@link ConfigurationNode#addPropertyListener(ItemEventListener)},
	 * {@link ConfigurationNode#removePropertyListener(ItemEventListener)} and
	 * {@link ConfigurationNode#getPropertyChangesSinceLastSave()}
	 * 
	 * @author feu
	 * 
	 */
	public static class PropertyValue implements Comparable<PropertyValue> {

		/**
		 * Constructor to set all the final fields.
		 * 
		 * @param propertyName
		 * @param propertyValue
		 * @param owner
		 */
		public PropertyValue(String propertyName, Serializable propertyValue,
				ConfigurationNode owner) {
			checkNotEmpty("propertyName", propertyName);
			checkNotNull("owner", owner);
			this.propertyName = propertyName;
			this.propertyValue = propertyValue;
			this.owner = owner;
			this.hashcode = hashOf(propertyName, propertyValue, owner);
			this.toString = format("Property[{0} = {1}]", propertyName,
					propertyValue);
		}

		/**
		 * Node that owns it property.
		 */
		private final ConfigurationNode owner;
		/**
		 * The name of the property.
		 */
		private final String propertyName;
		/**
		 * The value of the property.
		 */
		private final Serializable propertyValue;
		/**
		 * return to the {@link #hashCode()} method.
		 */
		private final int hashcode;
		/**
		 * return to the {@link #toString()} method.
		 */
		private final String toString;

		/**
		 * Gets the property name.
		 * 
		 * @return the property name.
		 */
		public String getPropertyName() {
			return propertyName;
		}

		/**
		 * Gets the property value.
		 * 
		 * @return the property value.
		 */
		public Serializable getPropertyValue() {
			return propertyValue;
		}

		/**
		 * Gets the configuration node that owns its property.
		 * 
		 * @return the owner node.
		 */
		public ConfigurationNode getOwner() {
			return owner;
		}

		/**
		 * {@link Object#equals(Object)} implementation.
		 */
		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof PropertyValue))
				return false;
			PropertyValue that = (PropertyValue) o;
			return eachEquality(of(this.propertyName, this.propertyValue,
					this.owner), andOf(that.propertyName, that.propertyValue,
					that.owner));
		}

		/**
		 * {@link Comparable#compareTo(Object)} implementation.
		 */
		public int compareTo(PropertyValue that) {
			return compareAll(of(this.propertyName, this.propertyValue,
					this.owner), andOf(that.propertyName, that.propertyValue,
					that.owner));
		}

		/**
		 * {@link Object#hashCode()} implementation.
		 */
		@Override
		public int hashCode() {
			return hashcode;
		}

		/**
		 * {@link Object#toString()} implementation.
		 */
		@Override
		public String toString() {
			return toString;
		}
	}

	/**
	 * Type of event changes that are possible to properties and configuration
	 * nodes.
	 * 
	 * @author feu
	 * 
	 */
	public enum ItemChangeType {
		ADDED, CHANGED, EXCLUDED
	}

	/**
	 * A node or property change event witch is used on
	 * {@link ConfigurationNode} listener infrastructure. <B>It's not okay to
	 * change node and property values inside its listeners</b>. It can result
	 * in unexpected results such as dead locks since the list of changes are
	 * synchronized and setting a new value will try to add a new change to the
	 * list of changes.
	 * 
	 * @author feu
	 * 
	 */
	public static class ItemChangeEvent<T extends Comparable<T>> implements
			Comparable<ItemChangeEvent<T>>, Serializable {

		/**
		 * Constructor to set all the final fields.
		 * 
		 * @param type
		 *            of change event.
		 * @param oldItem
		 *            the old value of this item before change.
		 * @param newItem
		 *            the new value of this item after change.
		 */
		public ItemChangeEvent(ItemChangeType type, T oldItem, T newItem) {
			checkNotNull("type", type);
			checkCondition("atLeastOneNonNull", oldItem != null
					|| newItem != null);
			this.type = type;
			this.oldItem = oldItem;
			this.newItem = newItem;
			this.hashcode = hashOf(type, oldItem, newItem);
		}

		private static final long serialVersionUID = 1291076849217066265L;

		/**
		 * {@link #hashCode()} return.
		 */
		private final int hashcode;

		/**
		 * Type of change event.
		 */
		private final ItemChangeType type;

		/**
		 * The item value before change.
		 */
		private final T oldItem;

		/**
		 * The item value after change.
		 */
		private final T newItem;

		/**
		 * Returns the type of change event.
		 * 
		 * @return the type of change event.
		 */
		public ItemChangeType getType() {
			return type;
		}

		/**
		 * returns the item value before change.
		 * 
		 * @return item value before change.
		 */
		public T getOldItem() {
			return oldItem;
		}

		/**
		 * returns the item value after change.
		 * 
		 * @return item value after change.
		 */
		public T getNewItem() {
			return newItem;
		}

		/**
		 * {@link Comparable#compareTo(Object)} implementation.
		 */
		@SuppressWarnings("unchecked")
		public int compareTo(ItemChangeEvent that) {
			return compareAll(of(this.type, this.oldItem, this.newItem), andOf(
					that.type, that.oldItem, that.newItem));
		}

		/**
		 * {@link Object#equals(Object)} implementation.
		 */
		@Override
		@SuppressWarnings("unchecked")
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof ItemChangeEvent))
				return false;
			ItemChangeEvent that = (ItemChangeEvent) o;
			return eachEquality(of(this.type, this.oldItem, this.newItem),
					andOf(that.type, that.oldItem, that.newItem));
		}

		/**
		 * {@link Object#hashCode()} implementation.
		 */
		@Override
		public int hashCode() {
			return hashcode;
		}

	}

	/**
	 * Listener interface to be used on listener infrastructure for node and
	 * property changes.
	 * 
	 * @author feu
	 * 
	 */
	public interface ItemEventListener<T extends Comparable<T>> {
		/**
		 * The notification method that will be called to tell about a new
		 * change event that happened.
		 * 
		 * @param event
		 */
		void changeEventHappened(ItemChangeEvent<T> event);
	}

	/**
	 * Marker method.
	 */
	public void __dont_implement_Node__instead_extend_AbstractNode__();

}