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
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.contains;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Compare.compareAll;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.HashCodes.hashOf;
import static org.openspotlight.common.util.Reflection.INHERITED_TYPES;
import static org.openspotlight.common.util.Reflection.searchInheritanceType;
import static org.openspotlight.common.util.Reflection.searchType;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openspotlight.common.util.Reflection.InheritanceType;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.util.ConfigurationNodes;

/**
 * This type guards the {@link ConfigurationNode} instance metadata, using the
 * {@link StaticMetadata} to validate the internal data.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface InstanceMetadata {

	/**
	 * Visitor interface for {@link ConfigurationNode}.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	public static interface ConfigurationNodeVisitor {
		/**
		 * Method called durring node visiting
		 * 
		 * @param node
		 */
		public void visitNode(ConfigurationNode node);
	}

	/**
	 * This interface has some methods to provide optional lazy loading.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	public static interface DataLoader {

		/**
		 * Load all properties from the given node. Should not repeat the heavy
		 * operations when requested to load some children that is already
		 * loaded.
		 * 
		 * @param targetNode
		 */
		public void loadChildren(ConfigurationNode targetNode);

		/**
		 * Load all properties from the given node. Should not repeat the heavy
		 * operations when requested to load some children that is already
		 * loaded.
		 * 
		 * @param targetNode
		 */
		public void loadProperties(ConfigurationNode targetNode);

	}

	/**
	 * Factory class to create the private implementations for
	 * {@link InstanceMetadata}.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	public static class Factory {

		/**
		 * Default implementation for {@link InstanceMetadata}.
		 * 
		 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
		 * 
		 */
		private static class BasicInstanceMetadata implements InstanceMetadata {

			private String savedUniqueId;

			/**
			 * Name and type map for properties.
			 */
			final Map<String, Class<?>> propertyTypes;

			/**
			 * Type metadata.
			 */
			private final StaticMetadata staticMetadata;

			/**
			 * Shared data to use on listener infrastructure.
			 */
			private final SharedData sharedData;

			/**
			 * volatile field to be used on {@link #toString()} method
			 */
			private volatile String description = null;

			/**
			 * The key property value.
			 */
			private final Serializable keyPropertyValue;

			/**
			 * hashCode cache
			 */
			private volatile int hashcode = 0;

			/**
			 * optional parent node
			 */
			private final ConfigurationNode parent;

			/**
			 * mandatory owner node
			 */
			private final ConfigurationNode owner;

			/**
			 * Children node map that contains the children classes and a map
			 * with its entries.
			 */
			private final Map<Class<?>, Map<Serializable, ConfigurationNode>> children = new ConcurrentHashMap<Class<?>, Map<Serializable, ConfigurationNode>>();

			/**
			 * Property map.
			 */
			private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>();

			/**
			 * Property map.
			 */
			private final Map<Class<? extends ConfigurationNode>, ConfigurationNode> nodeProperties = new ConcurrentHashMap<Class<? extends ConfigurationNode>, ConfigurationNode>();

			/**
			 * Transient property map. This properties won't be saved.
			 */
			private final Map<String, Object> transientProperties = new ConcurrentHashMap<String, Object>();

			/**
			 * Constructor with mandatory fields.
			 * 
			 * @param staticMetadata
			 * @param sharedData
			 * @param keyPropertyValue
			 * @param parent
			 * @param owner
			 */
			public BasicInstanceMetadata(final StaticMetadata staticMetadata,
					final SharedData sharedData,
					final Serializable keyPropertyValue,
					final ConfigurationNode parent,
					final ConfigurationNode owner) {

				this.staticMetadata = staticMetadata;
				this.sharedData = sharedData;
				this.keyPropertyValue = keyPropertyValue;
				this.parent = parent;
				this.owner = owner;
				for (final Class<?> childClass : staticMetadata
						.validChildrenTypes()) {
					this.children.put(childClass,
							new HashMap<Serializable, ConfigurationNode>());
				}
				final String[] propKeys = staticMetadata.propertyNames();
				final Class<?>[] propValues = staticMetadata.propertyTypes();
				this.propertyTypes = map(ofKeys(propKeys),
						andValues(propValues));

			}

			/**
			 * Class for {@link ConfigurationNode configuration nodes}
			 * supporting the visitor GoF pattern in a stack overflow safe way.
			 * 
			 * @author feu
			 * 
			 */
			private static class VisitorSupport {

				private static int getDepth(ConfigurationNode n) {
					ConfigurationNode c = n.getInstanceMetadata()
							.getDefaultParent();
					int i = 0;
					while (c != null) {
						i++;
						c = c.getInstanceMetadata().getDefaultParent();
					}
					return i;
				}

				/**
				 * Just add new children if the current level is not equal max
				 * levels.
				 * 
				 * @param n
				 * @param currenctlyAddedNodes
				 */
				private static void fillChildren(
						final ConfigurationNode n,
						final LinkedList<ConfigurationNode> currenctlyAddedNodes,
						Integer limit, final ConfigurationNodeVisitor visitor) {

					if (limit == null || getDepth(n) < limit.intValue()) {
						for (final Class<? extends ConfigurationNode> childClass : n
								.getInstanceMetadata().getStaticMetadata()
								.validChildrenTypes()) {
							final Collection<? extends ConfigurationNode> allChildren = n
									.getInstanceMetadata().getChildrensOfType(
											childClass);
							for (final ConfigurationNode newNode : allChildren) {
								currenctlyAddedNodes.add(newNode);
								visitor.visitNode(newNode);
							}
						}
					}
				}

				/**
				 * It will visit children nodes until the current level is equal
				 * the max levels.
				 * 
				 * @param n
				 * @param visitor
				 * @param maxLevels
				 */
				public static void fillQueue(final ConfigurationNode n,
						final ConfigurationNodeVisitor visitor,
						final Integer maxLevels) {
					visitor.visitNode(n);
					final LinkedList<ConfigurationNode> currenctlyAddedNodes = new LinkedList<ConfigurationNode>();
					fillChildren(n, currenctlyAddedNodes, maxLevels, visitor);
					while (currenctlyAddedNodes.size() != 0) {
						final LinkedList<ConfigurationNode> toIterate = new LinkedList<ConfigurationNode>(
								currenctlyAddedNodes);
						currenctlyAddedNodes.clear();
						for (final ConfigurationNode current : toIterate) {
							fillChildren(current, currenctlyAddedNodes,
									maxLevels, visitor);
						}
					}

				}

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public void accept(final ConfigurationNodeVisitor visitor) {
				VisitorSupport.fillQueue(this.getOwner(), visitor, null);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N extends ConfigurationNode> void addChild(final N child) {
				addChild(child, true);
			}

			private <N extends ConfigurationNode> void addChild(final N child,
					final boolean fireEvent) {
				checkNotNull("child", child); //$NON-NLS-1$
				final InheritanceType inheritanceType = searchInheritanceType(
						child.getClass(), this.staticMetadata
								.validChildrenTypes());
				checkCondition(
						"correctClass", INHERITED_TYPES.contains(inheritanceType)); //$NON-NLS-1$
				final Class<?> correctType = searchType(child.getClass(),
						this.staticMetadata.validChildrenTypes());
				if (this.children.get(correctType).containsValue(child)) {
					return;
				}
				final ConfigurationNode oldNode = getChildByKeyValue(child
						.getClass(), child.getInstanceMetadata()
						.getKeyPropertyValue());
				this.children.get(correctType).put(
						child.getInstanceMetadata().getKeyPropertyValue(),
						child);
				if (fireEvent) {
					this.sharedData.fireNodeChange(oldNode, child);
				}

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N extends ConfigurationNode> void addChildIgnoringListener(
					final N child) {
				addChild(child, false);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public int compare(final ConfigurationNode thisNode,
					final ConfigurationNode that) {
				return compareAll(of(thisNode.getInstanceMetadata().getOwner()
						.getClass(), thisNode.getInstanceMetadata()
						.getKeyPropertyValue(), thisNode.getInstanceMetadata()
						.getDefaultParent()), andOf(that.getClass(), that
						.getInstanceMetadata().getKeyPropertyValue(), that
						.getInstanceMetadata().getDefaultParent()));
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@Override
			public final boolean equals(final Object o) {
				if (o == this) {
					return true;
				}
				if (o instanceof ConfigurationNode) {
					final ConfigurationNode that = (ConfigurationNode) o;
					return eachEquality(of(this.getOwner().getClass(), this
							.getKeyPropertyValue(), this.getDefaultParent()),
							andOf(that.getClass(), that.getInstanceMetadata()
									.getKeyPropertyValue(), that
									.getInstanceMetadata().getDefaultParent()));
				} else if (o instanceof InstanceMetadata) {
					final InstanceMetadata that = (InstanceMetadata) o;
					return eachEquality(of(this.getOwner().getClass(), this
							.getKeyPropertyValue(), this.getDefaultParent()),
							andOf(that.getClass(), that.getKeyPropertyValue(),
									that.getDefaultParent()));
				} else {
					return false;
				}

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N extends ConfigurationNode> N getChildByKeyValue(
					final Class<N> type, final Serializable key) {
				checkNotNull("type", type); //$NON-NLS-1$
				checkNotNull("key", key);//$NON-NLS-1$
				this.sharedData.loadChildren(getOwner());
				final InheritanceType inheritanceType = searchInheritanceType(
						type, this.staticMetadata.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<?> correctType = searchType(type,
						this.staticMetadata.validChildrenTypes());
				return (N) this.children.get(correctType).get(key);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N extends ConfigurationNode> Collection<N> getChildrensOfType(
					final Class<N> type) {
				checkNotNull("type", type);//$NON-NLS-1$
				this.sharedData.loadChildren(getOwner());
				final InheritanceType inheritanceType = searchInheritanceType(
						type, this.staticMetadata.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<?> correctType = searchType(type,
						this.staticMetadata.validChildrenTypes());

				final Map<Serializable, ConfigurationNode> childrenMap = this.children
						.get(correctType);
				final Collection<ConfigurationNode> childrenCollection = childrenMap
						.values();
				return (Collection<N>) unmodifiableCollection(childrenCollection);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public ConfigurationNode getDefaultParent() {
				return this.parent;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public Set<? extends Serializable> getKeyFromChildrenOfTypes(
					final Class<? extends ConfigurationNode> type) {
				checkNotNull("type", type);//$NON-NLS-1$
				this.sharedData.loadChildren(getOwner());
				final InheritanceType inheritanceType = searchInheritanceType(
						type, this.staticMetadata.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<?> correctType = searchType(type,
						this.staticMetadata.validChildrenTypes());

				final Map<Serializable, ConfigurationNode> childrenMap = this.children
						.get(correctType);
				final Set<Serializable> childrenNames = childrenMap.keySet();
				return unmodifiableSet(childrenNames);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public Serializable getKeyPropertyValue() {
				return this.keyPropertyValue;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public Map<Class<? extends ConfigurationNode>, ConfigurationNode> getNodeProperties() {
				return unmodifiableMap(this.nodeProperties);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N extends ConfigurationNode> N getNodeProperty(
					final Class<N> type) {
				checkNotNull("type", type);//$NON-NLS-1$
				final InheritanceType inheritanceType = searchInheritanceType(
						type, this.staticMetadata.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<?> correctType = searchType(type,
						this.staticMetadata.validChildrenTypes());

				final N value = (N) this.nodeProperties.get(correctType
						.getSimpleName());
				return value;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public ConfigurationNode getOwner() {
				return this.owner;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N extends ConfigurationNode> N getParent(
					final Class<N> parentType) {
				checkNotNull("parentType", parentType);//$NON-NLS-1$
				final InheritanceType inheritanceType = searchInheritanceType(
						parentType, this.staticMetadata.validParentTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));

				return parentType.cast(this.parent);
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public Map<String, Object> getProperties() {
				this.sharedData.loadProperties(getOwner());
				return unmodifiableMap(this.properties);

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N extends Serializable> N getProperty(final String name) {
				checkNotEmpty("name", name); //$NON-NLS-1$
				checkCondition("validName", contains(name, this.staticMetadata//$NON-NLS-1$
						.propertyNames()));
				this.sharedData.loadProperties(getOwner());
				final N value = (N) this.properties.get(name);
				return value;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public String getSavedUniqueId() {
				return this.savedUniqueId;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public SharedData getSharedData() {
				return this.sharedData;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public StaticMetadata getStaticMetadata() {
				return this.staticMetadata;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public InputStream getStreamProperty(final String name) {
				checkNotEmpty("name", name);//$NON-NLS-1$
				this.sharedData.loadProperties(getOwner());
				final InputStream value = (InputStream) this.properties
						.get(name);
				return value;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N> N getTransientProperty(final String name) {
				checkNotEmpty("name", name);//$NON-NLS-1$
				return (N) this.transientProperties.get(name);
			}

			/**
			 * Hash code that uses the same field of the equals method.
			 */
			@Override
			public final int hashCode() {
				if (this.hashcode == 0) {
					this.hashcode = hashOf(this.getOwner().getClass(), this
							.getKeyPropertyValue(), this.getDefaultParent());
				}
				return this.hashcode;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public boolean isDirty() {
				return this.sharedData.isNodeDirty(this.getOwner());
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N extends ConfigurationNode> void removeAllChildrenOfType(
					final Class<N> type) {
				checkNotNull("type", type);//$NON-NLS-1$
				final InheritanceType inheritanceType = searchInheritanceType(
						type, this.staticMetadata.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<?> correctType = searchType(type,
						this.staticMetadata.validChildrenTypes());
				this.children.get(correctType).clear();
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N extends ConfigurationNode> void removeChild(final N child) {
				checkNotNull("child", child);//$NON-NLS-1$
				final InheritanceType inheritanceType = searchInheritanceType(
						child.getClass(), this.staticMetadata
								.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<?> correctType = searchType(child.getClass(),
						this.staticMetadata.validChildrenTypes());
				if (!this.children.get(correctType).containsValue(child)) {
					return;
				}
				this.children.get(correctType).remove(
						child.getInstanceMetadata().getKeyPropertyValue());
				this.sharedData.fireNodeChange(child, null);

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N extends ConfigurationNode> void setNodeProperty(
					final Class<N> type, final N value) {
				checkNotNull("type", type);//$NON-NLS-1$
				final InheritanceType inheritanceType = searchInheritanceType(
						type, this.staticMetadata.validChildrenTypes());
				checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
						.contains(inheritanceType));
				final Class<? extends ConfigurationNode> correctType = (Class<? extends ConfigurationNode>) searchType(
						type, this.staticMetadata.validChildrenTypes());
				final String name = correctType.getSimpleName();
				final N oldValue = (N) getNodeProperty(correctType);
				if (!eachEquality(oldValue, value)) {
					this.nodeProperties.put(correctType, value);
					this.sharedData.firePropertyChange(this.getOwner(), name,
							oldValue, value);
				}
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			@SuppressWarnings("unchecked")
			public <N extends Serializable> void setProperty(final String name,
					final N value) {
				checkNotEmpty("name", name);//$NON-NLS-1$
				checkCondition("correctName", this.propertyTypes//$NON-NLS-1$
						.containsKey(name));
				if (value != null) {
					final InheritanceType inheritanceType = searchInheritanceType(
							value.getClass(), this.propertyTypes.get(name));
					checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
							.contains(inheritanceType));

				}
				final N oldValue = (N) getProperty(name);
				if (!eachEquality(oldValue, value)) {
					if (value != null) {
						this.properties.put(name, value);
					} else {
						this.properties.remove(name);
					}
					this.sharedData.firePropertyChange(this.getOwner(), name,
							oldValue, value);
				}

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N extends Serializable> void setPropertyIgnoringListener(
					final String name, final N value) {
				checkNotEmpty("name", name);//$NON-NLS-1$
				checkCondition("correctName", this.propertyTypes//$NON-NLS-1$
						.containsKey(name));
				if (value != null) {
					final InheritanceType inheritanceType = searchInheritanceType(
							value.getClass(), this.propertyTypes.get(name));
					checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
							.contains(inheritanceType));

				}
				this.properties.put(name, value);

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public synchronized void setSavedUniqueId(final String savedUniqueId) {
				checkNotNull("savedUniqueId", savedUniqueId); //$NON-NLS-1$
				if (this.savedUniqueId != null) {
					if (!this.savedUniqueId.equals(savedUniqueId)) {
						throw new IllegalStateException(
								"trying to set a unique node id for the second time"); //$NON-NLS-1$
					}
				}
				this.savedUniqueId = savedUniqueId;
			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public void setStreamProperty(final String name,
					final InputStream value) {
				checkNotEmpty("name", name);//$NON-NLS-1$
				checkCondition("correctName", this.propertyTypes//$NON-NLS-1$
						.containsKey(name));
				if (value != null) {
					final InheritanceType inheritanceType = searchInheritanceType(
							value.getClass(), this.propertyTypes.get(name));
					checkCondition("correctClass", INHERITED_TYPES//$NON-NLS-1$
							.contains(inheritanceType));

				}
				this.properties.put(name, value);

			}

			/**
			 * 
			 * {@inheritDoc}
			 */
			public <N> void setTransientProperty(final String name,
					final N value) {
				checkNotEmpty("name", name);//$NON-NLS-1$
				this.transientProperties.put(name, value);

			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final String toString() {
				if (this.description == null) {
					this.description = format(
							Messages
									.getString("InstanceMetadata.configurationNodeToString"), getOwner().getClass() //$NON-NLS-1$
									.getName(), getKeyPropertyValue());
				}
				return this.description;
			}
		}

		private static final Class<?>[] EMPTY_TYPE = new Class<?>[] { ConfigurationNode.class };

		/**
		 * Creates a dynamic metadata for a root node.
		 * 
		 * @param owner
		 * @return a new root dynamic metadata
		 */
		public static InstanceMetadata createRoot(final ConfigurationNode owner) {
			checkNotNull("owner", owner);//$NON-NLS-1$
			checkCondition("hasStaticMetadataAnnotation",//$NON-NLS-1$
					owner.getClass()
							.getAnnotation(StaticMetadata.class) != null);
			checkCondition("ownerIsRoot", Arrays.equals(owner.getClass() //$NON-NLS-1$
					.getAnnotation(StaticMetadata.class)
					.validParentTypes(), EMPTY_TYPE));
			final BasicInstanceMetadata metadata = new BasicInstanceMetadata(
					owner.getClass()
							.getAnnotation(StaticMetadata.class), new SharedData(), null, null, owner);
			return metadata;

		}

		/**
		 * Creates a new child root node with a key property. This node should
		 * be the most common.
		 * 
		 * @param <K>
		 *            key property type
		 * @param owner
		 *            the node itself
		 * @param parentNode
		 *            node parent
		 * @param keyPropertyValue
		 *            the key value
		 * @return a new dynamic metadata
		 */
		public static <K extends Serializable> InstanceMetadata createWithKeyProperty(
				final ConfigurationNode owner,
				final ConfigurationNode parentNode, final K keyPropertyValue) {
			checkNotNull("owner", owner); //$NON-NLS-1$
			checkNotNull("parentNode", parentNode);//$NON-NLS-1$
			checkNotNull("keyPropertyValue", keyPropertyValue);//$NON-NLS-1$
			final StaticMetadata staticMetadata = owner.getClass()
					.getAnnotation(StaticMetadata.class);
			checkCondition("hasStaticMetadataAnnotation",//$NON-NLS-1$
					staticMetadata != null);
			checkCondition("ownerIsNotRoot", !staticMetadata //$NON-NLS-1$
					.validParentTypes().equals(ConfigurationNode.class));
			checkCondition(
					"correctKeyPropertyClass", staticMetadata.keyPropertyType() //$NON-NLS-1$
							.isInstance(keyPropertyValue));
			final InheritanceType inheritanceType = searchInheritanceType(
					parentNode.getClass(), staticMetadata.validParentTypes());
			checkCondition("correctRootClass", INHERITED_TYPES//$NON-NLS-1$
					.contains(inheritanceType));

			final BasicInstanceMetadata newInstanceMetadata = new BasicInstanceMetadata(
					staticMetadata, parentNode.getInstanceMetadata()
							.getSharedData(), keyPropertyValue, parentNode,
					owner);

			return newInstanceMetadata;

		}

	}

	/**
	 * This visitor groups all children of a given type from the node that
	 * accepted this visitor. This visitor should be used only in a one visit,
	 * because it stores the found nodes inside an attribute.
	 * 
	 * The easier way is to use the static method
	 * {@link ConfigurationNodes#findAllNodesOfType(ConfigurationNode, Class)}
	 * method.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 * @param <T>
	 *            type of nodes that should be found
	 */
	public static class FindAllNodesVisitor<T extends ConfigurationNode>
			implements ConfigurationNodeVisitor {

		private final Class<T> typeClass;

		private boolean valid = true;

		private final Serializable key;

		private final Set<T> foundNodes = new HashSet<T>();

		/**
		 * Constructor with target type class to be found.
		 * 
		 * @param newTypeClass
		 */
		public FindAllNodesVisitor(final Class<T> newTypeClass) {
			checkNotNull("newTypeClass", newTypeClass); //$NON-NLS-1$
			this.typeClass = newTypeClass;
			this.key = null;
		}

		/**
		 * Constructor with target type class to be found.
		 * 
		 * @param newTypeClass
		 * @param key
		 */
		public FindAllNodesVisitor(final Class<T> newTypeClass,
				final Serializable key) {
			checkNotNull("newTypeClass", newTypeClass); //$NON-NLS-1$
			checkNotNull("key", key); //$NON-NLS-1$
			this.typeClass = newTypeClass;
			this.key = key;
		}

		/**
		 * Return the found nodes and invalidate this visitor
		 * 
		 * @return all found nodes of a given type
		 */
		public Set<T> getFoundNodesAndInvalidate() {
			this.valid = false;
			return this.foundNodes;
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		public void visitNode(final ConfigurationNode node) {
			if (!this.valid) {
				throw logAndReturn(new IllegalStateException());
			}
			if (this.typeClass.isInstance(node)) {
				if (this.key == null) {
					this.foundNodes.add((T) node);
				} else {
					if (this.key.equals(node.getInstanceMetadata()
							.getKeyPropertyValue())) {
						this.foundNodes.add((T) node);
					}
				}
			}
		}

	}

	/**
	 * A node or property change event witch is used on
	 * {@link ConfigurationNode} listener infrastructure. <B>It's not okay to
	 * change node and property values inside its listeners</b>. It can result
	 * in unexpected results such as dead locks since the list of changes are
	 * synchronized and setting a new value will try to add a new change to the
	 * list of changes.
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
	 * {@link SharedData#removePropertyListener(ItemEventListener)}
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
			checkNotNull("owner", owner);//$NON-NLS-1$
			this.propertyName = propertyName;
			this.propertyValue = propertyValue;
			this.owner = owner;
			this.hashcode = hashOf(propertyName, propertyValue, owner);
			this.toString = format(Messages
					.getString("InstanceMetadata.propertyToString"),//$NON-NLS-1$
					propertyName, propertyValue);
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
	public static class SharedData implements DataLoader {

		/**
		 * dirty flag for all nodes of the current graph.
		 */
		private final AtomicBoolean dirtyFlag = new AtomicBoolean(false);

		private final Set<ConfigurationNode> dirtyNodes = new CopyOnWriteArraySet<ConfigurationNode>();

		/**
		 * All the node listeners for all nodes of the current graph.
		 */
		private final List<ItemEventListener<ConfigurationNode>> nodeListeners = new CopyOnWriteArrayList<ItemEventListener<ConfigurationNode>>();

		/**
		 * All the property listeners for all nodes of the current graph.
		 */
		private final List<ItemEventListener<PropertyValue>> propertyListeners = new CopyOnWriteArrayList<ItemEventListener<PropertyValue>>();

		/**
		 * Node change cache that store all the node change events since the
		 * last {@link #markAsSaved()} method call. This cache is used by all
		 * nodes of the current graph.
		 */
		private final List<ItemChangeEvent<ConfigurationNode>> nodeChangeCache = new CopyOnWriteArrayList<ItemChangeEvent<ConfigurationNode>>();
		private DataLoader loader = null;

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
			ItemChangeType type;
			if ((oldValue == null) && (newValue != null)) {
				type = ItemChangeType.ADDED;
			} else if ((oldValue != null) && (newValue == null)) {
				type = ItemChangeType.EXCLUDED;
			} else {
				type = ItemChangeType.CHANGED;
			}
			final ItemChangeEvent<ConfigurationNode> changeEvent = new ItemChangeEvent<ConfigurationNode>(
					type, oldValue, newValue);

			synchronized (this) {
				this.nodeChangeCache.add(changeEvent);
				if ((changeEvent.getNewItem() != null)
						&& !this.dirtyNodes.contains(changeEvent.getNewItem())) {
					this.dirtyNodes.add(changeEvent.getNewItem());
				}
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
			checkNotEmpty("propertyName", propertyName);//$NON-NLS-1$
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

			this.dirtyFlag.set(true);
			for (final ItemEventListener<PropertyValue> listener : this.propertyListeners) {
				listener.changeEventHappened(changeEvent);
			}
			if (!this.dirtyNodes.contains(owner)) {
				fireNodeChange(owner, owner);// to fire node change of type
				// changed.
			}

		}

		/**
		 * 
		 * @return the current dirty nodes
		 */
		public Set<ConfigurationNode> getDirtyNodes() {
			return unmodifiableSet(this.dirtyNodes);
		}

		/**
		 * 
		 * @return all changes since last save operation
		 */
		public List<ItemChangeEvent<ConfigurationNode>> getNodeChangesSinceLastSave() {
			return unmodifiableList(this.nodeChangeCache);
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
		 * 
		 * Check if the node is dirty since its last save.
		 * 
		 * @param node
		 * @return true if this node is dirty
		 */
		public boolean isNodeDirty(final ConfigurationNode node) {
			return this.dirtyNodes.contains(node);
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		public void loadChildren(final ConfigurationNode targetNode) {

			if (this.loader != null) {
				this.loader.loadChildren(targetNode);
			}

		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		public void loadProperties(final ConfigurationNode targetNode) {

			if (this.loader != null) {
				this.loader.loadProperties(targetNode);
			}
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
				this.nodeChangeCache.clear();
				this.dirtyNodes.clear();
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

		/**
		 * Sets a data loader on a node for lazy evaluation purposes.
		 * 
		 * @param loader
		 */
		public void setDataLoader(final DataLoader loader) {
			checkNotNull("loader", loader); //$NON-NLS-1$
			checkCondition("loaderNotTheSame", loader != this);//$NON-NLS-1$
			this.loader = loader;
		}

	}

	/**
	 * Accepts a {@link ConfigurationNodeVisitor} on this node and all its
	 * children.
	 * 
	 * Repare that the parent wont be visited by this method. If there's a need
	 * to visit all nodes, call this method on the root parent.
	 * 
	 * @param visitor
	 */
	public void accept(ConfigurationNodeVisitor visitor);

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
	 * Adds a child witch should be used as a set of childs. It must have a key
	 * property. It also would not fire any listener events.
	 * 
	 * @param <N>
	 *            child type
	 * @param child
	 */
	public <N extends ConfigurationNode> void addChildIgnoringListener(N child);

	/**
	 * To be used on {@link Comparable} interface
	 * 
	 * @param configuration
	 * @param o
	 * @return an int compatible with {@link Comparable} interface
	 */
	public int compare(ConfigurationNode configuration, ConfigurationNode o);

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
	 * Returns a set of all keys for a given type, since multiple types needs to
	 * have a key property.
	 * 
	 * @param childClass
	 * @return key for each child of a given type
	 */
	public Set<? extends Serializable> getKeyFromChildrenOfTypes(
			Class<? extends ConfigurationNode> childClass);

	/**
	 * Some nodes are using in multiple times inside a node. For this nodes to
	 * be uniquely identified there is the key property name on static metadata
	 * and key property value on dynamic metadata.
	 * 
	 * @return the key property value
	 */
	public Serializable getKeyPropertyValue();

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
	public Map<String, Object> getProperties();

	/**
	 * This returns a binary property. This property has the hability to be
	 * saved on persist operations on configuration nodes.
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
	 * Returns this node unique identifier after its saving, or null if it
	 * wasn't saved yet.
	 * 
	 * @return a unique id for this node
	 */
	public String getSavedUniqueId();

	/**
	 * This is the {@link SharedData} object. This is used to share the
	 * listeners infrastructure between all nodes on this graph.
	 * 
	 * @return the shared data
	 */
	public SharedData getSharedData();

	/**
	 * Instance method to return this useful annotation.
	 * 
	 * @return the static metadata
	 */
	public StaticMetadata getStaticMetadata();

	/**
	 * This returns binary property.
	 * 
	 * @param name
	 *            of a valid property
	 * @return a property
	 */
	public InputStream getStreamProperty(String name);

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
	 * 
	 * @return true if this node is dirty.
	 */
	public boolean isDirty();

	/**
	 * Removes all children nodes of a given type from this object.
	 * 
	 * @param <N>
	 *            type of the child
	 * @param type
	 *            of child to be removed
	 */
	<N extends ConfigurationNode> void removeAllChildrenOfType(Class<N> type);

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
	 * Sets a normal property, but dont notify the listener. See
	 * {@link #setProperty(String, Serializable)}.
	 * 
	 * @param <N>
	 *            type of the property that should be {@link Serializable}
	 * @param name
	 *            of the property
	 * @param value
	 *            of the property
	 */
	public <N extends Serializable> void setPropertyIgnoringListener(
			final String name, final N value);

	/**
	 * Sets the unique id. This method should be used only by
	 * {@link ConfigurationManager managers}.
	 * 
	 * @param id
	 * @throws IllegalStateException
	 *             if someone try to set its id for a second tyme.
	 */
	public void setSavedUniqueId(String id);

	/**
	 * Sets a bynary property
	 * 
	 * @param name
	 *            of the property
	 * @param value
	 *            of the property
	 */
	public void setStreamProperty(String name, InputStream value);

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
