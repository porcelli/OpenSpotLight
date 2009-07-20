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

package org.openspotlight.federation.data;

import static java.text.MessageFormat.format;
import static java.util.Collections.unmodifiableList;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This type guards the {@link ConfigurationNode} instance metadata, using the
 * {@link ConfigurationNodeStaticMetadata} to validate the internal data.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface ConfigurationNodeDynamicMetadata {
    
    /**
     * Factory class to create the private implementations for
     * {@link ConfigurationNodeDynamicMetadata}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static class Factory {
        
        /**
         * Creates a dynamic metadata for a root node.
         * 
         * @param staticMetadata
         * @param owner
         * @return a new root dynamic metadata
         */
        public static ConfigurationNodeDynamicMetadata createRoot(
                final ConfigurationNodeStaticMetadata staticMetadata,
                final ConfigurationNode owner) {
            throw new IllegalArgumentException("not implemented yet"); //$NON-NLS-1$
        }
        
        /**
         * Creates a new child root node with a key property. This node should
         * be the most common.
         * 
         * @param <K>
         * @param staticMetadata
         *            for the node
         * @param owner
         *            the node itself
         * @param parentNode
         *            node parent
         * @param keyPropertyValue
         *            the key value
         * @return a new dynamic metadata
         */
        public static <K extends Serializable> ConfigurationNodeDynamicMetadata createWithKeyProperty(
                final ConfigurationNodeStaticMetadata staticMetadata,
                final ConfigurationNode owner,
                final ConfigurationNode parentNode, final K keyPropertyValue) {
            
            throw new IllegalArgumentException("not implemented yet"); //$NON-NLS-1$
        }
        
        /**
         * Creates a dynamic metadata for a node that should be used as a simple
         * property.
         * 
         * @param staticMetadata
         * @param owner
         * @param parentNode
         * @return a new dynamic metadata
         */
        public static ConfigurationNodeDynamicMetadata createWithoutKeyProperty(
                final ConfigurationNodeStaticMetadata staticMetadata,
                final ConfigurationNode owner,
                final ConfigurationNode parentNode) {
            throw new IllegalArgumentException("not implemented yet"); //$NON-NLS-1$
        }
    }
    
    /**
     * A node or property change event witch is used on
     * {@link ConfigurationNodeMetadata} listener infrastructure. <B>It's not
     * okay to change node and property values inside its listeners</b>. It can
     * result in unexpected results such as dead locks since the list of changes
     * are synchronized and setting a new value will try to add a new change to
     * the list of changes.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     * @param <T>
     *            the item type been changed
     * 
     */
    public static class ItemChangeEvent<T extends Comparable<T>> implements
            Comparable<ItemChangeEvent<T>>, Serializable {
        
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
         * Constructor to set all the final fields.
         * 
         * @param type
         *            of change event.
         * @param oldItem
         *            the old value of this item before change.
         * @param newItem
         *            the new value of this item after change.
         */
        public ItemChangeEvent(final ItemChangeType type, final T oldItem,
                final T newItem) {
            checkNotNull("type", type); //$NON-NLS-1$
            checkCondition("atLeastOneNonNull", (oldItem != null) //$NON-NLS-1$
                    || (newItem != null));
            this.type = type;
            this.oldItem = oldItem;
            this.newItem = newItem;
            this.hashcode = hashOf(type, oldItem, newItem);
        }
        
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public int compareTo(final ItemChangeEvent that) {
            return compareAll(of(this.type, this.oldItem, this.newItem), andOf(
                    that.type, that.oldItem, that.newItem));
        }
        
        /**
         * {@link Object#equals(Object)} implementation.
         */
        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ItemChangeEvent)) {
                return false;
            }
            final ItemChangeEvent that = (ItemChangeEvent) o;
            return eachEquality(of(this.type, this.oldItem, this.newItem),
                    andOf(that.type, that.oldItem, that.newItem));
        }
        
        /**
         * returns the item value after change.
         * 
         * @return item value after change.
         */
        public T getNewItem() {
            return this.newItem;
        }
        
        /**
         * returns the item value before change.
         * 
         * @return item value before change.
         */
        public T getOldItem() {
            return this.oldItem;
        }
        
        /**
         * Returns the type of change event.
         * 
         * @return the type of change event.
         */
        public ItemChangeType getType() {
            return this.type;
        }
        
        /**
         * {@link Object#hashCode()} implementation.
         */
        @Override
        public int hashCode() {
            return this.hashcode;
        }
        
    }
    
    /**
     * Type of event changes that are possible to properties and configuration
     * nodes.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public enum ItemChangeType {
        /**
         * inclusion event
         */
        ADDED,
        /**
         * update event
         */
        CHANGED,
        /**
         * delete event
         */
        EXCLUDED
    }
    
    /**
     * Listener interface to be used on listener infrastructure for node and
     * property changes.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     * @param <T>
     *            the type been listened
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
     * This class is used to group a property change with possible needed data,
     * such as owner node, property name and value.
     * 
     * Its used on property listener methods such as
     * {@link SharedData#addPropertyListener(ItemEventListener)},
     * {@link SharedData#removePropertyListener(ItemEventListener)} and
     * {@link ConfigurationNodeMetadata#getPropertyChangesSinceLastSave()}
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     * 
     */
    public static class PropertyValue implements Comparable<PropertyValue> {
        
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
         * Constructor to set all the final fields.
         * 
         * @param propertyName
         * @param propertyValue
         * @param owner
         */
        public PropertyValue(final String propertyName,
                final Serializable propertyValue, final ConfigurationNode owner) {
            checkNotEmpty("propertyName", propertyName); //$NON-NLS-1$
            checkNotNull("owner", owner); //$NON-NLS-1$
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
            this.owner = owner;
            this.hashcode = hashOf(propertyName, propertyValue, owner);
            this.toString = format("Property[{0} = {1}]", propertyName, //$NON-NLS-1$
                    propertyValue);
        }
        
        /**
         * {@inheritDoc}
         */
        public int compareTo(final PropertyValue that) {
            return compareAll(of(this.propertyName, this.propertyValue,
                    this.owner), andOf(that.propertyName, that.propertyValue,
                    that.owner));
        }
        
        /**
         * {@link Object#equals(Object)} implementation.
         */
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PropertyValue)) {
                return false;
            }
            final PropertyValue that = (PropertyValue) o;
            return eachEquality(of(this.propertyName, this.propertyValue,
                    this.owner), andOf(that.propertyName, that.propertyValue,
                    that.owner));
        }
        
        /**
         * Gets the configuration node that owns its property.
         * 
         * @return the owner node.
         */
        public ConfigurationNode getOwner() {
            return this.owner;
        }
        
        /**
         * Gets the property name.
         * 
         * @return the property name.
         */
        public String getPropertyName() {
            return this.propertyName;
        }
        
        /**
         * Gets the property value.
         * 
         * @return the property value.
         */
        public Serializable getPropertyValue() {
            return this.propertyValue;
        }
        
        /**
         * {@link Object#hashCode()} implementation.
         */
        @Override
        public int hashCode() {
            return this.hashcode;
        }
        
        /**
         * {@link Object#toString()} implementation.
         */
        @Override
        public String toString() {
            return this.toString;
        }
    }
    
    /**
     * This class guards all shared node metadata witch is used to listen and
     * notify about node changes. This instance is shared by all instances of a
     * given {@link ConfigurationNode} graph.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static class SharedData {
        /**
         * dirty flag for all nodes of the current graph.
         */
        private final AtomicBoolean dirtyFlag = new AtomicBoolean(false);
        
        /**
         * All the node listeners for all nodes of the current graph.
         */
        private final List<ItemEventListener<ConfigurationNode>> nodeListeners = new CopyOnWriteArrayList<ItemEventListener<ConfigurationNode>>();
        
        /**
         * All the property listeners for all nodes of the current graph.
         */
        private final List<ItemEventListener<PropertyValue>> propertyListeners = new CopyOnWriteArrayList<ItemEventListener<PropertyValue>>();
        
        /**
         * Property change cache that store all the property change events since
         * the last {@link #markAsSaved()} method call. This cache is used by
         * all nodes of the current graph.
         */
        private final List<ItemChangeEvent<PropertyValue>> propertyChangeCache = new CopyOnWriteArrayList<ItemChangeEvent<PropertyValue>>();
        
        /**
         * Node change cache that store all the node change events since the
         * last {@link #markAsSaved()} method call. This cache is used by all
         * nodes of the current graph.
         */
        private final List<ItemChangeEvent<ConfigurationNode>> nodeChangeCache = new CopyOnWriteArrayList<ItemChangeEvent<ConfigurationNode>>();
        
        /**
         * Adds an event listener for node changes.
         * 
         * @param listener
         */
        public final void addNodeListener(
                final ItemEventListener<ConfigurationNode> listener) {
            this.nodeListeners.add(listener);
        }
        
        /**
         * Adds an event listener for property changes.
         * 
         * @param listener
         */
        public final void addPropertyListener(
                final ItemEventListener<PropertyValue> listener) {
            this.propertyListeners.add(listener);
        }
        
        /**
         * Method used to fire a node change event. It discovers the event type
         * based on old and new values. This method is synchronized on the
         * notification part that is common for all nodes of the current graph.
         * This method will mark the dirty flag that is common for all the nodes
         * of the current graph.
         * 
         * @param oldValue
         *            the node value before the change event
         * @param newValue
         *            the node value after the change event
         */
        public void fireNodeChange(final ConfigurationNode oldValue,
                final ConfigurationNode newValue) {
            if (eachEquality(of(oldValue), andOf(newValue))) {
                return; // no changes at all
            }
            ItemChangeType type;
            if ((oldValue == null) && (newValue != null)) {
                type = ItemChangeType.ADDED;
            } else if ((oldValue != null) && (newValue == null)) {
                type = ItemChangeType.EXCLUDED;
            } else {
                type = ItemChangeType.CHANGED; // should never happen at this
                // actual
                // implementation
            }
            final ItemChangeEvent<ConfigurationNode> changeEvent = new ItemChangeEvent<ConfigurationNode>(
                    type, oldValue, newValue);
            
            synchronized (this) {
                this.nodeChangeCache.add(changeEvent);
                this.dirtyFlag.set(true);
            }
            for (final ItemEventListener<ConfigurationNode> listener : this.nodeListeners) {
                listener.changeEventHappened(changeEvent);
            }
        }
        
        /**
         * Method used to fire a property change event. It discovers the event
         * type based on old and new values. This method is synchronized on the
         * notification part that is common for all nodes of the current graph.
         * This method will mark the dirty flag that is common for all the nodes
         * of the current graph.
         * 
         * @param owner
         * 
         * @param propertyName
         *            the name of the property.
         * @param oldValue
         *            the convenient values of the property before the change.
         * @param newValue
         *            the convenient values of the property after the change.
         */
        public void firePropertyChange(final ConfigurationNode owner,
                final String propertyName, final Serializable oldValue,
                final Serializable newValue) {
            checkNotEmpty("propertyName", propertyName); //$NON-NLS-1$
            if (eachEquality(oldValue, newValue)) {
                return; // no changes at all
            }
            ItemChangeType type;
            if ((oldValue == null) && (newValue != null)) {
                type = ItemChangeType.ADDED;
            } else if ((oldValue != null) && (newValue == null)) {
                type = ItemChangeType.EXCLUDED;
            } else {
                type = ItemChangeType.CHANGED;
            }
            final PropertyValue newPropertyValue = new PropertyValue(
                    propertyName, newValue, owner);
            final PropertyValue oldPropertyValue = new PropertyValue(
                    propertyName, oldValue, owner);
            final ItemChangeEvent<PropertyValue> changeEvent = new ItemChangeEvent<PropertyValue>(
                    type, oldPropertyValue, newPropertyValue);
            
            synchronized (this) {
                this.propertyChangeCache.add(changeEvent);
                this.dirtyFlag.set(true);
            }
            for (final ItemEventListener<PropertyValue> listener : this.propertyListeners) {
                listener.changeEventHappened(changeEvent);
            }
            
        }
        
        /**
         * 
         * @return the change events that happened on nodes since the last save
         */
        public final List<ItemChangeEvent<ConfigurationNode>> getNodeChangesSinceLastSave() {
            return unmodifiableList(this.nodeChangeCache);
        }
        
        /**
         * 
         * @return the change events that happened on properties since the last
         *         save
         */
        public final List<ItemChangeEvent<PropertyValue>> getPropertyChangesSinceLastSave() {
            return unmodifiableList(this.propertyChangeCache);
        }
        
        /**
         * The dirty flag is setted in case that any property or node changed
         * since last save event.
         * 
         * @return the dirty flag
         */
        public final boolean isDirty() {
            return this.dirtyFlag.get();
        }
        
        /**
         * Sets true on the dirty flag.
         */
        public final void markAsDirty() {
            this.dirtyFlag.set(true);
        }
        
        /**
         * Resets the dirty flag and clears all cache.
         */
        public final void markAsSaved() {
            synchronized (this) {
                this.dirtyFlag.set(false);
                this.propertyChangeCache.clear();
                this.nodeChangeCache.clear();
            }
        }
        
        /**
         * Removes the node change event listener.
         * 
         * @param listener
         */
        public final void removeNodeListener(
                final ItemEventListener<ConfigurationNode> listener) {
            this.nodeListeners.remove(listener);
        }
        
        /**
         * Removes the property change event listener.
         * 
         * @param listener
         */
        public final void removePropertyListener(
                final ItemEventListener<PropertyValue> listener) {
            this.propertyListeners.remove(listener);
        }
        
    }
    
    /**
     * Adds a child witch should be used as a set of childs. It must have a key
     * property.
     * 
     * @param <N>
     *            child type
     * @param child
     */
    public <N extends ConfigurationNode> void addChild(N child);
    
    /**
     * Returns a given child by its type and key.
     * 
     * @param <N>
     *            type of child
     * @param childClass
     * @param key
     * @return A given child by its key or null when not found
     */
    public <N extends ConfigurationNode> N getChildByKeyValue(
            Class<N> childClass, Serializable key);
    
    /**
     * Returns a collection of children for a given type.
     * 
     * @param <N>
     *            type of children to be returned
     * @param childClass
     *            type of children to be returned
     * @return all children of a given type
     */
    public <N extends ConfigurationNode> Collection<N> getChildrensOfType(
            Class<N> childClass);
    
    /**
     * This method returns the parent without the need to pass the parent type.
     * 
     * @return the parent
     */
    public ConfigurationNode getDefaultParent();
    
    /**
     * Some nodes are using in multiple times inside a node. For this nodes to
     * be uniquely identified there is the key property name on static metadata
     * and key property value on dynamic metadata.
     * 
     * @return the key property value
     */
    public Serializable getKeyPropertyValue();
    
    /**
     * Returns a set of all keys for a given type, since multiple types needs to
     * have a key property.
     * 
     * @param childClass
     * @return key for each child of a given type
     */
    public Set<Serializable> getKeysFromChildrenOfType(
            Class<? extends ConfigurationNode> childClass);
    
    /**
     * The node properties are properties to store single node types inside a
     * node.
     * 
     * @return the map with all types and nodes
     */
    public Map<Class<? extends ConfigurationNode>, ConfigurationNode> getNodeProperties();
    
    /**
     * The property as a node returned by its type.
     * 
     * @param <N>
     *            type of node for this property
     * @param type
     *            of node for this property
     * @return a node
     */
    public <N extends ConfigurationNode> N getNodeProperty(Class<N> type);
    
    /**
     * The metadata is associated with one instance of {@link ConfigurationNode}
     * . So, its property just store the node who created its metadata.
     * 
     * @return the node who owns its metadata
     */
    public ConfigurationNode getOwner();
    
    /**
     * Retuns the parent node for this configuration node. In case of this node
     * been the root node, it should return null instead of throw an exception.
     * 
     * Note that a kind of node could have more than one single type of parent.
     * A node can be inserted inside more than one kind of node.
     * 
     * @param <N>
     *            parent node type
     * @param parentType
     * @return the parent node
     */
    public <N extends ConfigurationNode> N getParent(Class<N> parentType);
    
    /**
     * Returns a map with all of the property names and values to be used on
     * reflection purposes during saving and loading.
     * 
     * @return the property map
     */
    public Map<String, Serializable> getProperties();
    
    /**
     * This returns a property. This property has the hability to be saved on
     * persist operations on configuration nodes.
     * 
     * @param <N>
     *            type of a property that should be valid and
     *            {@link Serializable}
     * @param name
     *            of a valid property
     * @return a property
     */
    public <N extends Serializable> N getProperty(String name);
    
    /**
     * This is the {@link SharedData} object. This is used to share the
     * listeners infrastructure between all nodes on this graph.
     * 
     * @return the shared data
     */
    public SharedData getSharedData();
    
    /**
     * This property is a kind of property that will not be saved on save
     * operations. So, it doesn't have any restriction (such as must be
     * serializable).
     * 
     * @param <N>
     *            type of the property
     * @param name
     *            of the property
     * @return a transient property
     */
    public <N> N getTransientProperty(String name);
    
    /**
     * Removes a child node from this object, wich should be its parent.
     * 
     * @param <N>
     *            type of the child
     * @param child
     *            to be removed
     */
    <N extends ConfigurationNode> void removeChild(N child);
    
    /**
     * Sets a node property. See {@link #getNodeProperty(Class)}.
     * 
     * @param <N>
     * @param type
     *            of the property that should be a {@link ConfigurationNode} in
     *            this case
     * @param value
     *            of the property
     */
    public <N extends ConfigurationNode> void setNodeProperty(Class<N> type,
            N value);
    
    /**
     * Sets a normal property. See {@link #setProperty(String, Serializable)}.
     * 
     * @param <N>
     *            type of the property that should be {@link Serializable}
     * @param name
     *            of the property
     * @param value
     *            of the property
     */
    public <N extends Serializable> void setProperty(String name, N value);
    
    /**
     * Sets a transient property. See {@link #getTransientProperty(String)}.
     * 
     * @param <N>
     *            type of transient property
     * @param name
     *            of the transient property
     * @param value
     *            of the transient property
     */
    public <N> void setTransientProperty(String name, N value);
    
}