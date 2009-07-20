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
import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableCollection;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.jcip.annotations.ThreadSafe;

/**
 * Class to abstract thread safe {@link ConfigurationNodeMetadata} creation. All
 * the public methods of this class are final. The correct way to extend this
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
 * FIXME use this as an internal representation and just create an interface with #getInternalRepresentation
 * </pre>
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
public abstract class AbstractConfigurationNode implements
        ConfigurationNodeMetadata {
    
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
     * volatile field to be used on {@link #toString()} method
     */
    private volatile String description = null;
    
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
    
    private final Class<?> parentType;
    
    private final Set<Class<?>> childrenTypes;
    
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
    public <N extends AbstractConfigurationNode> AbstractConfigurationNode(
            final ConfigurationNode owner,
            final ConfigurationNodeStaticMetadata metadata) {
        checkNotNull("owner", owner);
        checkNotEmpty("name", name);
        checkNotNull("propertyTypes", this.propertyTypes);
        if (this.getParentType() != null) {
            checkNotNull("parent", this.parent);
            checkCondition("correctParentClass", this.getParentType().equals(
                    this.parent.getClass()));
        } else {
            checkNullMandatory("parent", this.parent);
        }
        checkNotNull("childrenNodeClasses", this.getChildrenTypes());
        this.propertyTypes = unmodifiableMap(metadata.getPropertyTypes());
        this.parent = (ConfigurationNode) this.parent;
        if (this.parent != null) {
            this.parent.getMetadata().addChild(this);
            this.nodeListeners = this.parent.nodeListeners;
            this.propertyListeners = this.parent.propertyListeners;
            
            this.nodeChangeCache = this.parent.nodeChangeCache;
            this.propertyChangeCache = this.parent.propertyChangeCache;
            
            this.dirtyFlag = this.parent.dirtyFlag;
            
            this.cacheEntryLock = this.parent.cacheEntryLock;
        } else {
            this.nodeListeners = synchronizedList(new LinkedList<ItemEventListener<ConfigurationNode>>());
            this.propertyListeners = synchronizedList(new LinkedList<ItemEventListener<PropertyValue>>());
            
            this.nodeChangeCache = synchronizedList(new LinkedList<ItemChangeEvent<ConfigurationNode>>());
            this.propertyChangeCache = synchronizedList(new LinkedList<ItemChangeEvent<PropertyValue>>());
            
            this.dirtyFlag = new AtomicBoolean(false);
            
            this.cacheEntryLock = new Object();
        }
        for (final Class<?> childClass : this.getChildrenTypes()) {
            this.children.put(childClass,
                    new HashMap<String, ConfigurationNode>());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void __dont_implement_Node__instead_extend_AbstractNode__() {
    }
    
    /**
     * {@inheritDoc}
     */
    public final synchronized <N extends ConfigurationNode> void addChild(
            final N child) {
        checkNotNull("child", child);
        checkCondition("childClassOwned", this.getChildrenTypes().contains(
                child.getClass()));
        if (this.children.containsValue(child)) {
            return;
        }
        final ConfigurationNode oldNode = this.getChildByName(child.getClass(),
                child.getMetadata().getName());
        this.children.get(child.getClass()).put(child.getMetadata().getName(),
                child);
        this.fireNodeChange(oldNode, child);
    }
    
    /**
     * {@inheritDoc}
     */
    public final int compareTo(final ConfigurationNode o) {
        return this.toString().compareTo(o.toString());
    }
    
    /**
     * Equals method that is safe to use also over an inheritance. Should not be
     * overloaded.
     */
    @Override
    public final boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AbstractConfigurationNode)) {
            return false;
        }
        final AbstractConfigurationNode that = (AbstractConfigurationNode) o;
        return eachEquality(of(this.getClass(), this.getName(), this
                .getParent()), andOf(that.getClass(), that.getName(), that
                .getParent()));
        
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final <N extends ConfigurationNode> N getChildByName(
            final Class<N> childClass, final String name) {
        checkNotNull("childClass", childClass);
        checkNotEmpty("name", name);
        checkCondition("childClassOwned", this.getChildrenTypes().contains(
                childClass));
        return (N) this.children.get(childClass).get(name);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final <N extends ConfigurationNode> Collection<N> getChildrensOfType(
            final Class<N> childClass) {
        checkNotNull("childClass", childClass);
        checkCondition("childClassOwned", this.getChildrenTypes().contains(
                childClass));
        final Map<String, ConfigurationNode> childrenMap = this.children
                .get(childClass);
        final Collection<ConfigurationNode> childrenCollection = childrenMap
                .values();
        return (Collection<N>) unmodifiableCollection(childrenCollection);
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<Class<?>> getChildrenTypes() {
        return this.childrenTypes;
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
    public final Set<String> getNamesFromChildrenOfType(
            final Class<? extends ConfigurationNode> childClass) {
        checkNotNull("childClass", childClass);
        checkCondition("childClassOwned", this.getChildrenTypes().contains(
                childClass));
        final Map<String, ConfigurationNode> childrenMap = this.children
                .get(childClass);
        final Set<String> childrenNames = childrenMap.keySet();
        return unmodifiableSet(childrenNames);
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
    public Class<?> getParentType() {
        return this.parentType;
    }
    
    /**
     * {@inheritDoc}
     */
    public final Map<String, Serializable> getProperties() {
        return unmodifiableMap(this.properties);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final <N extends Serializable> N getProperty(final String name) {
        checkNotEmpty("name", name);
        final N value = (N) this.properties.get(name);
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public final Map<String, Class<?>> getPropertyTypes() {
        return unmodifiableMap(this.propertyTypes);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final <N> N getTransientProperty(final String name) {
        checkNotEmpty("name", name);
        return (N) this.transientProperties.get(name);
    }
    
    /**
     * Hash code that uses the same field of the equals method.
     */
    @Override
    public final int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = hashOf(this.getClass(), this.getName(), this
                    .getParent());
        }
        return this.hashcode;
    }
    
    /**
     * {@inheritDoc}
     */
    final synchronized <N extends ConfigurationNode> void removeChild(
            final N child) {
        checkNotNull("child", child);
        checkCondition("childClassOwned", this.getChildrenTypes().contains(
                child.getClass()));
        if (!this.children.get(child.getClass()).containsValue(child)) {
            return;
        }
        this.children.get(child.getClass()).put(child.getMetadata().getName(),
                null);
        this.fireNodeChange(child, null);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public final synchronized <N extends Serializable> void setProperty(
            final String name, final N value) {
        checkNotEmpty("name", name);
        if ((value != null)
                && !value.getClass().equals(this.propertyTypes.get(name))) {
            logAndThrow(new AssertionError(
                    format(
                            "Error on property type mapping on class {0} for property named {1} and value class {2}.",
                            this.getClass(), name, value.getClass())));
        }
        final N oldValue = (N) this.getProperty(name);
        if (!eachEquality(oldValue, value)) {
            this.properties.put(name, value);
            this.firePropertyChange(name, oldValue, value);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    public final synchronized <N> void setTransientProperty(final String name,
            final N value) {
        checkNotEmpty("name", name);
        this.transientProperties.put(name, value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        if (this.description == null) {
            this.description = "AbstractConfigurationNode["
                    + this.getClass().getName() + "] " + name;
        }
        return this.description;
    }
}
