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

package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Reflection.searchType;
import static org.openspotlight.common.util.Strings.firstLetterToLowerCase;
import static org.openspotlight.common.util.Strings.firstLetterToUpperCase;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Configuration;

/**
 * Interface responsible to load and save the configuration data on a persistent
 * layer.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface ConfigurationManager {
    
    /**
     * Helper class to map the class name to a valid node name and vice versa.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static class NodeClassHelper {
        
        /**
         * Default package used to find configuration pojo classes.
         */
        public static final String DEFAULT_NODE_PACKAGE = Configuration.class
                .getPackage().getName();
        
        /**
         * Class and name cache
         */
        private final Map<Class<? extends ConfigurationNode>, String> cache = new ConcurrentHashMap<Class<? extends ConfigurationNode>, String>();
        
        /**
         * Create a new node instance based on a node name, a parent node and
         * the class name. The node class should have a constructor with a
         * string and a node, as in the super class {@link ConfigurationNode}.
         * 
         * @param <N>
         * @param <S>
         * @param keyValue
         * @param parentNode
         * @param nodeClassName
         * @return a new configuration node
         * @throws ConfigurationException
         */
        public <N extends ConfigurationNode, S extends Serializable> N createInstance(
                final S keyValue, final ConfigurationNode parentNode,
                final String nodeClassName) throws ConfigurationException {
            checkNotNull("keyValue", keyValue); //$NON-NLS-1$
            checkNotNull("parentNode", parentNode); //$NON-NLS-1$
            checkNotEmpty("nodeClassName", nodeClassName); //$NON-NLS-1$
            checkCondition("nodeClassNameWithPrefix", nodeClassName //$NON-NLS-1$
                    .startsWith(DEFAULT_OSL_PREFIX + ":")); //$NON-NLS-1$
            try {
                
                final Class<N> nodeClass = getNodeClassFromName(nodeClassName);
                final StaticMetadata staticMetadata = getStaticMetadataFromClass(nodeClass);
                final Class<?>[] types = new Class<?>[2];
                types[0] = searchType(parentNode.getClass(), staticMetadata
                        .validParentTypes());
                checkNotNull("parentType", types[0]); //$NON-NLS-1$
                types[1] = staticMetadata.keyPropertyType();
                final Constructor<N> constructor = nodeClass
                        .getConstructor(types);
                final N node = constructor.newInstance(parentNode, keyValue);
                return node;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }
        
        /**
         * Creates a new root node instance based on class name. The node class
         * should have a default constructor with calls the super constructor
         * from {@ConfigurationNode} using default
         * arguments.
         * 
         * @param <N>
         * @param nodeClassName
         * @return a new root instance for a configuration node
         * @throws ConfigurationException
         */
        public <N extends ConfigurationNode> N createRootInstance(
                final String nodeClassName) throws ConfigurationException {
            checkNotEmpty("nodeClassName", nodeClassName); //$NON-NLS-1$
            checkCondition("nodeClassNameWithPrefix", nodeClassName //$NON-NLS-1$
                    .startsWith(DEFAULT_OSL_PREFIX + ":")); //$NON-NLS-1$
            try {
                final Class<N> clazz = getNodeClassFromName(nodeClassName);
                final N node = clazz.newInstance();
                return node;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }
        
        /**
         * Returns a valid node name based on class name
         * 
         * @param nodeClass
         * @return a new name for this node type
         */
        public String getNameFromNodeClass(
                final Class<? extends ConfigurationNode> nodeClass) {
            checkNotNull("nodeClass", nodeClass); //$NON-NLS-1$
            checkCondition("samePackage", DEFAULT_NODE_PACKAGE.equals(nodeClass //$NON-NLS-1$
                    .getPackage().getName()));
            String name = this.cache.get(nodeClass);
            if (name == null) {
                name = nodeClass.getSimpleName();
                name = firstLetterToLowerCase(name);
                name = DEFAULT_OSL_PREFIX + ":" + name; //$NON-NLS-1$
                this.cache.put(nodeClass, name);
            }
            return name;
        }
        
        /**
         * Returns a valid node Class based on node class name. This class will
         * be taken from the {@link #DEFAULT_NODE_PACKAGE} static attribute.
         * 
         * @param <N>
         * @param nodeClassName
         * @return a node class for this name
         * @throws ConfigurationException
         */
        @SuppressWarnings("unchecked")
        public <N extends ConfigurationNode> Class<N> getNodeClassFromName(
                final String nodeClassName) throws ConfigurationException {
            checkNotEmpty("nodeClassName", nodeClassName); //$NON-NLS-1$
            checkCondition("nodeClassNameWithPrefix", nodeClassName //$NON-NLS-1$
                    .startsWith(DEFAULT_OSL_PREFIX + ":")); //$NON-NLS-1$
            if (this.cache.containsValue(nodeClassName)) {
                for (final Map.Entry<Class<? extends ConfigurationNode>, String> entry : this.cache
                        .entrySet()) {
                    if (nodeClassName.equals(entry.getValue())) {
                        return (Class<N>) entry.getKey();
                    }
                }
            }
            String realClassName = removeBegginingFrom(
                    DEFAULT_OSL_PREFIX + ":", nodeClassName); //$NON-NLS-1$
            realClassName = firstLetterToUpperCase(realClassName);
            realClassName = DEFAULT_NODE_PACKAGE + "." + realClassName; //$NON-NLS-1$
            try {
                final Class<? extends ConfigurationNode> clazz = (Class<? extends ConfigurationNode>) Class
                        .forName(realClassName);
                this.cache.put(clazz, nodeClassName);
                return (Class<N>) clazz;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
            
        }
        
        /**
         * Gets the static metadata from a {@link ConfigurationNode} extended
         * class.
         * 
         * @param configurationNodeClass
         * @return the static metadata
         * @throws ConfigurationException
         */
        public StaticMetadata getStaticMetadataFromClass(
                final Class<? extends ConfigurationNode> configurationNodeClass)
                throws ConfigurationException {
            try {
                return configurationNodeClass
                        .getAnnotation(StaticMetadata.class);
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }
    }
    
    /**
     * Helper class to help to deal with property types and classes
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static class PropertyEntryHelper {
        
        /**
         * Verify if that identifier is an osl property. Used on Jcr when
         * loading the nodes.
         * 
         * @param propertyIdentifier
         * @return true if this is a valid property
         */
        public boolean isPropertyNode(final String propertyIdentifier) {
            checkNotEmpty("propertyIdentifier", propertyIdentifier); //$NON-NLS-1$
            if (propertyIdentifier.startsWith(DEFAULT_OSL_PREFIX + ":")) { //$NON-NLS-1$
                return true;
            }
            return false;
        }
        
    }
    
    /**
     * The default OpenSpotlight prefix used on Jcr.
     */
    public static final String DEFAULT_OSL_PREFIX = "osl"; //$NON-NLS-1$
    
    /**
     * Search a node using its internal key value. Note that a note have a
     * unique combination of key and parent. If any nodes has the same keys,
     * same types and different parents, it will return more than one result.
     * So, its possible to choose the correct node by comparing the parent.
     * 
     * <b>Warning:</b> Do not use a abstract type, such as {@link Artifact} or
     * so on, because, it will use the class name to create the query. If you
     * need to find all kinds of a abstract type, use the non lazy static method
     * {@link org.openspotlight.federation.data.util.ConfiguratonNodes#findAllNodesOfType}
     * 
     * @param <T>
     *            node type
     * @param <K>
     *            key type
     * @param root
     * @param nodeType
     *            node type class
     * @param key
     *            value
     * @return a set of nodes
     * @throws ConfigurationException
     *             if anything wrong happens
     */
    public <T extends ConfigurationNode, K extends Serializable> Set<T> findNodesByKey(
            ConfigurationNode root, Class<T> nodeType, K key)
            throws ConfigurationException;
    
    /**
     * Loads the current group from configuration, marking the configuration as
     * saved.
     * 
     * @param lazyType
     *            to determine the loading behavior
     * 
     * @return a fresh configuration
     * @throws ConfigurationException
     */
    Configuration load(LazyType lazyType) throws ConfigurationException;
    
    /**
     * Saves the group on a persistent layer marking the current configuration
     * as a saved configuration.
     * 
     * @param configuration
     * @throws ConfigurationException
     */
    void save(Configuration configuration) throws ConfigurationException;
}
