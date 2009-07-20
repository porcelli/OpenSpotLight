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
import static org.openspotlight.common.util.Strings.firstLetterToLowerCase;
import static org.openspotlight.common.util.Strings.firstLetterToUpperCase;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.AbstractConfigurationNode;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.ConfigurationNodeMetadata;

/**
 * Interface responsible to load and save the group data on a persistent layer
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
        
        public static final String DEFAULT_NODE_PACKAGE = AbstractConfigurationNode.class
                .getPackage().getName();
        
        /**
         * Class and name cache
         */
        private final Map<Class<? extends AbstractConfigurationNode>, String> cache = new ConcurrentHashMap<Class<? extends AbstractConfigurationNode>, String>();
        
        /**
         * Create a new node instance based on a node name, a parent node and
         * the class name. The node class should have a constructor with a
         * string and a node, as in the super class
         * {@link AbstractConfigurationNode}.
         * 
         * @param <N>
         * @param nodeName
         * @param parentNode
         * @param nodeClassName
         * @return
         */
        public <N extends ConfigurationNodeMetadata> N createInstance(
                final String nodeName,
                final ConfigurationNodeMetadata parentNode,
                final String nodeClassName) throws ConfigurationException {
            checkNotEmpty("nodeName", nodeName);
            checkNotNull("parentNode", parentNode);
            checkNotEmpty("nodeClassName", nodeClassName);
            checkCondition("nodeClassNameWithPrefix", nodeClassName
                    .startsWith(DEFAULT_OSL_PREFIX + ":"));
            try {
                final Class<N> clazz = getNodeClassFromName(nodeClassName);
                final Constructor<N> constructor = clazz.getConstructor(
                        String.class, parentNode.getClass());
                final N node = constructor.newInstance(nodeName, parentNode);
                return node;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }
        
        /**
         * Creates a new root node instance based on class name. The node class
         * should have a default constructor with calls the super constructor
         * from {@AbstractConfigurationNode} using
         * default arguments.
         * 
         * @param <N>
         * @param nodeClassName
         * @return
         */
        public <N extends ConfigurationNodeMetadata> N createRootInstance(
                final String nodeClassName) throws ConfigurationException {
            checkNotEmpty("nodeClassName", nodeClassName);
            checkCondition("nodeClassNameWithPrefix", nodeClassName
                    .startsWith(DEFAULT_OSL_PREFIX + ":"));
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
         * @return
         */
        public String getNameFromNodeClass(
                final Class<? extends AbstractConfigurationNode> nodeClass) {
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
        public <N extends ConfigurationNodeMetadata> Class<N> getNodeClassFromName(
                final String nodeClassName) throws ConfigurationException {
            checkNotEmpty("nodeClassName", nodeClassName);
            checkCondition("nodeClassNameWithPrefix", nodeClassName
                    .startsWith(DEFAULT_OSL_PREFIX + ":"));
            if (cache.containsValue(nodeClassName)) {
                for (final Map.Entry<Class<? extends AbstractConfigurationNode>, String> entry : cache
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
                final Class<? extends AbstractConfigurationNode> clazz = (Class<? extends AbstractConfigurationNode>) Class
                        .forName(realClassName);
                cache.put(clazz, nodeClassName);
                return (Class<N>) clazz;
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
         * Verify if that identifier is an osl property
         */
        public boolean isPropertyNode(final String propertyIdentifier) {
            checkNotEmpty("propertyIdentifier", propertyIdentifier);
            if (propertyIdentifier.startsWith(DEFAULT_OSL_PREFIX + ":")) {
                return true;
            }
            return false;
        }
        
    }
    
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
}
