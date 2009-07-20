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

import static java.util.Collections.unmodifiableList;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Equals.eachEquality;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openspotlight.federation.data.ConfigurationNodeMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.ConfigurationNodeMetadata.ItemChangeType;
import org.openspotlight.federation.data.ConfigurationNodeMetadata.ItemEventListener;
import org.openspotlight.federation.data.ConfigurationNodeMetadata.PropertyValue;

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
    static class Factory {
        
        public static ConfigurationNodeDynamicMetadata createRoot(
                final ConfigurationNodeStaticMetadata staticMetadata,
                final ConfigurationNode owner) {
            return null;
        }
        
        public static <K extends Serializable> ConfigurationNodeDynamicMetadata createWithKeyProperty(
                final ConfigurationNodeStaticMetadata staticMetadata,
                final ConfigurationNode owner,
                final ConfigurationNode parentNode, final K keyPropertyValue) {
            
            return null;
        }
        
        public static ConfigurationNodeDynamicMetadata createWithoutKeyProperty(
                final ConfigurationNodeStaticMetadata staticMetadata,
                final ConfigurationNode owner,
                final ConfigurationNode parentNode) {
            return null;
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
    static class SharedData {
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
    
    public <N extends ConfigurationNode> void addChild(N child);
    
    public <N extends ConfigurationNode> N getChildByKeyValue(
            Class<N> childClass, Serializable key);
    
    public <N extends ConfigurationNode> Collection<N> getChildrensOfType(
            Class<N> childClass);
    
    public ConfigurationNode getDefaultParent();
    
    public Serializable getKeyPropertyValue();
    
    public Set<Serializable> getKeysFromChildrenOfType(
            Class<? extends ConfigurationNode> childClass);
    
    /**
     * 
     * @param <N>
     * @param type
     * @return
     */
    public <N extends ConfigurationNode> N getNodeProperty(Class<N> type);
    
    public ConfigurationNode getOwner();
    
    public <N extends ConfigurationNode> N getParent(Class<N> parentType);
    
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