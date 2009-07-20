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
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.impl.Configuration;

/**
 * Configuration manager that stores and loads the configuration from a
 * JcrRepository.
 * 
 * FIXME implement node property
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class JcrSessionConfigurationManager implements ConfigurationManager {
    
    private static final String NS_DESCRIPTION = "www.openspotlight.org"; //$NON-NLS-1$
    
    /**
     * JCR session
     */
    private final Session session;
    
    private final NodeClassHelper classHelper = new NodeClassHelper();
    
    private final PropertyEntryHelper propertyHelper = new PropertyEntryHelper();
    
    /**
     * Constructor. It's mandatory that the session is valid during object
     * liveness.
     * 
     * @param session
     *            valid session
     * @throws ConfigurationException
     */
    public JcrSessionConfigurationManager(final Session session)
            throws ConfigurationException {
        checkNotNull("session", session); //$NON-NLS-1$
        checkCondition("session", session.isLive()); //$NON-NLS-1$
        this.session = session;
        this.initDataInsideSession();
    }
    
    /**
     * Method to create nodes on jcr only when necessary.
     * 
     * @param parentNode
     * @param nodePath
     * @return
     * @throws ConfigurationException
     */
    private Node createIfDontExists(final Node parentNode, final String nodePath)
            throws ConfigurationException {
        checkNotNull("parentNode", parentNode); //$NON-NLS-1$
        checkNotEmpty("nodePath", nodePath); //$NON-NLS-1$
        try {
            try {
                return this.session.getRootNode().getNode(nodePath);
            } catch (final PathNotFoundException e) {
                final Node newNode = parentNode.addNode(nodePath);
                return newNode;
            }
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
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
    @SuppressWarnings("boxing")
    private Serializable getProperty(final Node jcrNode,
            final String propertyName, final Class<?> propertyClass)
            throws Exception {
        Property jcrProperty = null;
        Serializable value = null;
        try {
            jcrProperty = jcrNode.getProperty(propertyName);
        } catch (final Exception e) {
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
            if (jcrProperty.getString() != null) {
                value = dateFromString(jcrProperty.getString());
            }
        } else {
            final String valueAsString = jcrProperty.getString();
            if (valueAsString != null) {
                value = readFromBase64(valueAsString);
            }
        }
        return value;
    }
    
    /**
     * Just create the "osl" prefix if that one doesn't exists, and after that
     * created the node "osl:configuration" if that doesn't exists.
     * 
     * @throws ConfigurationException
     */
    private void initDataInsideSession() throws ConfigurationException {
        try {
            final NamespaceRegistry namespaceRegistry = this.session
                    .getWorkspace().getNamespaceRegistry();
            if (!this.prefixExists(namespaceRegistry)) {
                namespaceRegistry.registerNamespace(DEFAULT_OSL_PREFIX,
                        NS_DESCRIPTION);
            }
        } catch (final Exception e) {
            logAndThrowNew(e, ConfigurationException.class);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Configuration load() throws ConfigurationException {
        checkCondition("sessionAlive", this.session.isLive()); //$NON-NLS-1$
        try {
            final String defaultRootNode = this.classHelper
                    .getNameFromNodeClass(Configuration.class);
            final Node rootJcrNode = this.session.getRootNode().getNode(
                    defaultRootNode);
            final Configuration rootNode = new Configuration();
            this.load(rootJcrNode, rootNode);
            rootNode.getInstanceMetadata().getSharedData().markAsSaved();
            return rootNode;
        } catch (final Exception e) {
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
    private void load(final Node jcrNode,
            final ConfigurationNode configurationNode) throws Exception {
        this.loadProperties(jcrNode, configurationNode, configurationNode
                .getStaticMetadata().getPropertyTypes());
        this.loadChildren(jcrNode, configurationNode);
    }
    
    @SuppressWarnings("unchecked")
    private void loadChildren(final Node jcrNode,
            final ConfigurationNode configurationNode)
            throws PathNotFoundException, RepositoryException,
            ConfigurationException, Exception {
        final Set<Class<? extends ConfigurationNode>> childClasses = configurationNode
                .getStaticMetadata().getChildrenNodeValidTypes();
        for (final Class<?> childClass : childClasses) {
            final String childNodeClassName = this.classHelper
                    .getNameFromNodeClass((Class<? extends ConfigurationNode>) childClass);
            final Node childNode = jcrNode.getNode(childNodeClassName);
            final NodeIterator grandChildren = childNode.getNodes();
            while (grandChildren.hasNext()) {
                final Node grandChild = grandChildren.nextNode();
                final String childName = removeBegginingFrom(DEFAULT_OSL_PREFIX
                        + ":", grandChild.getName()); //$NON-NLS-1$
                final ConfigurationNode newNode = this.classHelper
                        .createInstance(childName, configurationNode,
                                childNodeClassName);
                this.load(grandChild, newNode);
            }
        }
    }
    
    private void loadProperties(final Node jcrNode,
            final ConfigurationNode configurationNode,
            final Map<String, Class<?>> propertyTypes)
            throws RepositoryException, ConfigurationException, Exception {
        final PropertyIterator propertyIterator = jcrNode.getProperties();
        while (propertyIterator.hasNext()) {
            final Property prop = propertyIterator.nextProperty();
            final String propertyIdentifier = prop.getName();
            
            if (this.propertyHelper.isPropertyNode(propertyIdentifier)) {
                final String nodeName = removeBegginingFrom(DEFAULT_OSL_PREFIX
                        + ":", propertyIdentifier); //$NON-NLS-1$
                final Class<?> propertyClass = propertyTypes.get(nodeName);
                if (propertyClass != null) {
                    final Serializable value = this.getProperty(jcrNode,
                            propertyIdentifier, propertyClass);
                    configurationNode.getInstanceMetadata().setProperty(
                            nodeName, value);
                }
                
            }
        }
        
    }
    
    /**
     * Verify if the prefix "osl" exists
     * 
     * @param namespaceRegistry
     * @return true if exists
     * @throws RepositoryException
     */
    private boolean prefixExists(final NamespaceRegistry namespaceRegistry)
            throws RepositoryException {
        final String[] prefixes = namespaceRegistry.getPrefixes();
        boolean hasFound = false;
        for (final String prefix : prefixes) {
            if (DEFAULT_OSL_PREFIX.equals(prefix)) {
                hasFound = true;
                break;
            }
        }
        return hasFound;
    }
    
    /**
     * {@inheritDoc}
     */
    public void save(final Configuration configuration)
            throws ConfigurationException {
        checkNotNull("group", configuration); //$NON-NLS-1$
        checkCondition("sessionAlive", this.session.isLive()); //$NON-NLS-1$
        final ConfigurationNode node = configuration;
        try {
            final String nodeStr = this.classHelper.getNameFromNodeClass(node
                    .getClass());
            final Node newJcrNode = this.createIfDontExists(this.session
                    .getRootNode(), nodeStr);
            this.saveProperties(node, newJcrNode);
            
            this.saveChilds(node, newJcrNode);
            
            this.session.save();
            configuration.getInstanceMetadata().getSharedData().markAsSaved();
        } catch (final Exception e) {
            logAndThrowNew(e, ConfigurationException.class);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void saveChilds(final ConfigurationNode node, final Node newJcrNode)
            throws ConfigurationException, Exception {
        final Set<Class<? extends ConfigurationNode>> classes = node
                .getStaticMetadata().getChildrenNodeValidTypes();
        for (final Class<? extends ConfigurationNode> clazz : classes) {
            final Class<? extends ConfigurationNode> nodeClass = clazz;
            final Set<Serializable> childKeys = (Set<Serializable>) node
                    .getInstanceMetadata().getKeysFromChildrenOfType(nodeClass);
            final String childNodeStr = this.classHelper
                    .getNameFromNodeClass(nodeClass);
            final Node newChildJcrNode = this.createIfDontExists(newJcrNode,
                    childNodeStr);
            for (final Serializable key : childKeys) {
                final ConfigurationNode childNode = node.getInstanceMetadata()
                        .getChildByKeyValue(nodeClass, key);
                final Node newGranphChildNode = this.createIfDontExists(
                        newChildJcrNode, DEFAULT_OSL_PREFIX + ":" + key); //$NON-NLS-1$
                this.saveProperties(childNode, newGranphChildNode);
                this.saveChilds(childNode, newGranphChildNode);
            }
        }
    }
    
    private void saveProperties(final ConfigurationNode configurationNode,
            final Node innerNewJcrNode) throws Exception {
        final Map<String, Serializable> properties = configurationNode
                .getInstanceMetadata().getProperties();
        for (final Map.Entry<String, Serializable> entry : properties
                .entrySet()) {
            final Serializable value = entry.getValue();
            final Class<?> clazz = value != null ? value.getClass() : null;
            this.setProperty(innerNewJcrNode, DEFAULT_OSL_PREFIX + ":" //$NON-NLS-1$
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
    @SuppressWarnings("boxing")
    private void setProperty(final Node jcrNode, final String propertyName,
            final Class<?> propertyClass, final Serializable value)
            throws Exception {
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
            final String valueAsString = serializeToBase64(value);
            jcrNode.setProperty(propertyName, valueAsString);
        }
    }
}
