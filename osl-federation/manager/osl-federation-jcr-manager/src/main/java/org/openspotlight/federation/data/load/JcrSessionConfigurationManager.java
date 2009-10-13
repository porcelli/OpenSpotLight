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

import static java.text.MessageFormat.format;
import static java.util.Arrays.sort;
import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Dates.dateFromString;
import static org.openspotlight.common.util.Dates.stringFromDate;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrow;
import static org.openspotlight.common.util.Exceptions.logAndThrowNew;
import static org.openspotlight.common.util.Serialization.readFromBase64;
import static org.openspotlight.common.util.Serialization.serializeToBase64;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;
import static org.openspotlight.federation.data.util.ConfigurationNodes.findAllNodesOfType;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.NoConfigurationYetException;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.InstanceMetadata.DataLoader;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeType;
import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.util.ParentNumberComparator;

/**
 * Configuration manager that stores and loads the configuration from a JcrRepository. LATER_TASK implement node property
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class JcrSessionConfigurationManager implements ConfigurationManager {

    /**
     * Class to control the Lazy Loading for {@link JcrSessionConfigurationManager} when the loading is {@link LazyType#LAZY}
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static class JcrDataLoader implements DataLoader {

        /**
         * Value from a cache map to control lazy loading.
         * 
         * @author Luiz Fernando Teston - feu.teston@caravelatech.com
         */

        private static class LazyStatus {

            private final AtomicReference<LoadingStatus> propertyLoadingStatus;

            private final AtomicReference<LoadingStatus> childrenLoadingStatus;

            private final Node                           jcrNode;

            public LazyStatus(
                               final Node jcrNode ) {
                this.jcrNode = jcrNode;
                this.propertyLoadingStatus = new AtomicReference<LoadingStatus>(LoadingStatus.NEEDS_LOAD);
                this.childrenLoadingStatus = new AtomicReference<LoadingStatus>(LoadingStatus.NEEDS_LOAD);
            }

            public AtomicReference<LoadingStatus> getChildrenLoadingStatus() {
                return this.childrenLoadingStatus;
            }

            public Node getJcrNode() {
                return this.jcrNode;
            }

            public AtomicReference<LoadingStatus> getPropertyLoadingStatus() {
                return this.propertyLoadingStatus;
            }

        }

        private static enum LoadingStatus {
            NEEDS_LOAD,
            LOADING,
            LOADED
        }

        private final Map<ConfigurationNode, LazyStatus> lazyCache = new HashMap<ConfigurationNode, LazyStatus>();

        private final Session                            session;

        /**
         * Constructor to initialize the lazy loading for this two corresponding root nodes.
         * 
         * @param configurationNode
         * @param jcrNode
         * @throws RepositoryException
         */
        public JcrDataLoader(
                              final ConfigurationNode configurationNode, final Node jcrNode ) throws RepositoryException {
            this.session = jcrNode.getSession();
            this.lazyCache.put(configurationNode, new LazyStatus(jcrNode));
        }

        private LazyStatus createLazyStatus( final ConfigurationNode targetNode ) throws Exception {
            LazyStatus lazyCacheForTarget;
            final String xpath = XpathSupport.getCompleteXpathFor(targetNode);
            final Node jcrNodeForTarget = JcrSupport.findUnique(this.session, xpath);
            lazyCacheForTarget = new LazyStatus(jcrNodeForTarget);
            this.lazyCache.put(targetNode, lazyCacheForTarget);
            return lazyCacheForTarget;
        }

        public void loadChildren( final ConfigurationNode targetNode ) {
            try {
                LazyStatus lazyCacheForTarget;
                synchronized (this.lazyCache) {
                    lazyCacheForTarget = this.lazyCache.get(targetNode);
                    if (lazyCacheForTarget == null) {
                        lazyCacheForTarget = this.createLazyStatus(targetNode);
                    }
                }
                switch (lazyCacheForTarget.getChildrenLoadingStatus().get()) {
                    case NEEDS_LOAD:
                        try {
                            lazyCacheForTarget.getChildrenLoadingStatus().set(LoadingStatus.LOADING);
                            loadChildrenNodes(lazyCacheForTarget.getJcrNode(), targetNode, LazyType.LAZY);
                        } finally {
                            lazyCacheForTarget.getChildrenLoadingStatus().set(LoadingStatus.LOADED);
                        }
                        break;
                    case LOADING:
                        //                        for (final StackTraceElement e : Thread.currentThread().getStackTrace()) {
                        //                            System.err.println("LOADING " + e);
                        //                        }
                        //                        loop: while (true) {
                        //                            if (lazyCacheForTarget.getChildrenLoadingStatus().get().equals(LoadingStatus.LOADED)) {
                        //                                break loop;
                        //                            }
                        //                            Thread.sleep(250);
                        //                        }
                        break;
                    case LOADED:

                        break;
                }

            } catch (final Exception e) {
                logAndThrowNew(e, SLRuntimeException.class);
            }
        }

        public void loadProperties( final ConfigurationNode targetNode ) {
            try {
                LazyStatus lazyCacheForTarget;
                synchronized (this.lazyCache) {
                    lazyCacheForTarget = this.lazyCache.get(targetNode);
                    if (lazyCacheForTarget == null) {
                        lazyCacheForTarget = this.createLazyStatus(targetNode);
                    }
                }
                switch (lazyCacheForTarget.getPropertyLoadingStatus().get()) {
                    case NEEDS_LOAD:
                        try {
                            lazyCacheForTarget.getPropertyLoadingStatus().set(LoadingStatus.LOADING);
                            final String[] propKeys = targetNode.getInstanceMetadata().getStaticMetadata().propertyNames();
                            final Class<?>[] propValues = targetNode.getInstanceMetadata().getStaticMetadata().propertyTypes();
                            final String keyPropertyName = targetNode.getInstanceMetadata().getStaticMetadata().keyPropertyName();
                            final Map<String, Class<?>> propertyTypes = map(ofKeys(propKeys), andValues(propValues));
                            loadNodeProperties(lazyCacheForTarget.getJcrNode(), targetNode, propertyTypes, keyPropertyName);
                        } finally {
                            lazyCacheForTarget.getPropertyLoadingStatus().set(LoadingStatus.LOADED);
                        }
                        break;
                    case LOADING:
                        //                        loop: while (true) {
                        //                            if (lazyCacheForTarget.getPropertyLoadingStatus().get().equals(LoadingStatus.LOADED)) {
                        //                                break loop;
                        //                            }
                        //                            Thread.sleep(250);
                        //                        }
                        break;
                    case LOADED:

                        break;
                }

            } catch (final Exception e) {
                logAndThrowNew(e, SLRuntimeException.class);
            }
        }

    }

    /**
     * Class with some helper methods to use on Jcr stuff.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static class JcrSupport {

        /**
         * Find all nodes by using a xpath query
         * 
         * @param session
         * @param xpath
         * @return a node iterator
         * @throws Exception
         */
        public static NodeIterator findAll( final Session session,
                                            final String xpath ) throws Exception {
            final Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
            final QueryResult result = query.execute();
            final NodeIterator nodes = result.getNodes();
            return nodes;
        }

        /**
         * Find a node using a xpath query.
         * 
         * @param session
         * @param xpath
         * @return null when no items found, or the jcr item instead
         * @throws Exception if more than one item was found, or if anything wrong happened
         */
        public static Node findUnique( final Session session,
                                       final String xpath ) throws Exception {
            final Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
            final QueryResult result = query.execute();
            final NodeIterator nodes = result.getNodes();
            Node foundNode = null;
            if (nodes.hasNext()) {
                foundNode = nodes.nextNode();
            } else {
                return null;
            }
            if (nodes.hasNext()) {
                logAndThrow(new IllegalStateException("XPath with more than one result: " + xpath)); //$NON-NLS-1$
            }
            return foundNode;
        }
    }

    /**
     * This helper class can create xpath to find the {@link Node Jcr Node} corresponding to a unique {@link ConfigurationNode}
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static class XpathSupport {

        /**
         * Fill the string buffer at the beginning using the node data.
         * 
         * @param xpath
         * @param node
         * @throws Exception on conversion errors
         */
        private static void fillXpathFromNode( final StringBuilder xpath,
                                               final ConfigurationNode node ) throws Exception {
            final String nodePath = classHelper.getNameFromNodeClass(node.getClass());
            final StaticMetadata metadata = node.getInstanceMetadata().getStaticMetadata();
            final String keyPropertyName = metadata.keyPropertyName();
            final String keyPropertyValueAsString = convert(node.getInstanceMetadata().getKeyPropertyValue(), String.class);
            if ("".equals(keyPropertyName) || keyPropertyName == null) { //$NON-NLS-1$
                xpath.insert(0, format("/{0}", nodePath));//$NON-NLS-1$
            } else {
                xpath.insert(0, format("/{0}[@osl:{1}=''{2}'']", //$NON-NLS-1$
                                       nodePath, keyPropertyName, keyPropertyValueAsString));
            }

        }

        /**
         * @param node
         * @return a complete filled xpath based on node data and also its parent's data.
         * @throws Exception
         */
        static String getCompleteXpathFor( final ConfigurationNode node ) throws Exception {
            ConfigurationNode c = node;
            final StringBuilder xpath = new StringBuilder();
            while (c != null) {
                fillXpathFromNode(xpath, c);
                c = c.getInstanceMetadata().getDefaultParent();
            }
            xpath.insert(0, '/');
            return xpath.toString();

        }
    }

    static final NodeClassHelper             classHelper    = new NodeClassHelper();

    private static final String              NS_DESCRIPTION = "www.openspotlight.org";  //$NON-NLS-1$

    private static final PropertyEntryHelper propertyHelper = new PropertyEntryHelper();

    /**
     * Reads an property on jcr node
     * 
     * @param <T>
     * @param jcrNode
     * @param propertyName
     * @param propertyClass
     * @return
     * @throws Exception
     */
    @SuppressWarnings( {"boxing", "unchecked"} )
    private static <T> T getProperty( final Node jcrNode,
                                      final String propertyName,
                                      final Class<?> propertyClass ) throws Exception {
        Property jcrProperty = null;
        Object value = null;
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
            value = (int)jcrProperty.getLong();
        } else if (Byte.class.equals(propertyClass)) {
            value = (byte)jcrProperty.getLong();
        } else if (Float.class.equals(propertyClass)) {
            value = (float)jcrProperty.getDouble();
        } else if (Date.class.equals(propertyClass)) {
            if (jcrProperty.getString() != null) {
                value = dateFromString(jcrProperty.getString());
            }
        } else if (propertyClass.isEnum()) {
            final Field[] flds = propertyClass.getDeclaredFields();
            for (final Field f : flds) {
                if (f.isEnumConstant()) {
                    if (f.getName().equals(propertyName)) {
                        value = f.get(null);
                        break;
                    }
                }
            }
        } else if (InputStream.class.isAssignableFrom(propertyClass)) {
            value = jcrProperty.getStream();
        } else if (Serializable.class.isAssignableFrom(propertyClass)) {
            final String valueAsString = jcrProperty.getString();
            if (valueAsString != null) {
                value = readFromBase64(valueAsString);
            }
        } else {
            throw new IllegalStateException(format("Invalid class for property {0} : {1}", propertyName, //$NON-NLS-1$
                                                   propertyClass));
        }

        return (T)value;
    }

    /**
     * Loads the newly created node and also it's properties and it's children
     * 
     * @param jcrNode
     * @param configurationNode
     * @param staticMetadata
     * @throws Exception
     */
    private static void loadChildrenAndProperties( final Node jcrNode,
                                                   final ConfigurationNode configurationNode,
                                                   final StaticMetadata staticMetadata ) throws Exception {
        final String[] propKeys = configurationNode.getInstanceMetadata().getStaticMetadata().propertyNames();
        final Class<?>[] propValues = configurationNode.getInstanceMetadata().getStaticMetadata().propertyTypes();
        final Map<String, Class<?>> propertyTypes = map(ofKeys(propKeys), andValues(propValues));
        loadNodeProperties(jcrNode, configurationNode, propertyTypes, staticMetadata.keyPropertyName());
        loadChildrenNodes(jcrNode, configurationNode, LazyType.EAGER);
    }

    static void loadChildrenNodes( final Node jcrNode,
                                   final ConfigurationNode parentNode,
                                   final LazyType lazyType )
        throws PathNotFoundException, RepositoryException, ConfigurationException, Exception {
        NodeIterator children;
        try {
            children = jcrNode.getNodes();
        } catch (final PathNotFoundException e) {
            children = null;
            // Thats ok, just didn't find any nodes
        }
        if (children == null) {
            return;
        }
        while (children.hasNext()) {
            final Node jcrChild = children.nextNode();
            if (!jcrChild.getName().startsWith("osl:")) { //$NON-NLS-1$
                continue;
            }
            final Class<ConfigurationNode> nodeClass = classHelper.getNodeClassFromName(jcrChild.getName());
            final StaticMetadata staticMetadata = classHelper.getStaticMetadataFromClass(nodeClass);
            final ConfigurationNode newNode = loadOneChild(parentNode, jcrChild, staticMetadata, nodeClass);

            if (LazyType.EAGER.equals(lazyType)) {
                loadChildrenAndProperties(jcrChild, newNode, staticMetadata);
            }

        }

    }

    static void loadNodeProperties( final Node jcrNode,
                                    final ConfigurationNode configurationNode,
                                    final Map<String, Class<?>> propertyTypes,
                                    final String keyPropertyName ) throws RepositoryException, ConfigurationException, Exception {
        configurationNode.getInstanceMetadata().setSavedUniqueId(jcrNode.getUUID());
        final PropertyIterator propertyIterator = jcrNode.getProperties();
        while (propertyIterator.hasNext()) {
            final Property prop = propertyIterator.nextProperty();
            final String propertyIdentifier = prop.getName();

            if (propertyHelper.isPropertyNode(propertyIdentifier)) {
                final String propertyName = removeBegginingFrom(DEFAULT_OSL_PREFIX + ":", propertyIdentifier); //$NON-NLS-1$
                if (keyPropertyName != null && keyPropertyName.equals(propertyName)) {
                    continue;
                }
                final Class<?> propertyClass = propertyTypes.get(propertyName);
                if (propertyClass != null && Serializable.class.isAssignableFrom(propertyClass)) {
                    final Serializable value = (Serializable)getProperty(jcrNode, propertyIdentifier, propertyClass);
                    configurationNode.getInstanceMetadata().setPropertyIgnoringListener(propertyName, value);
                } else if (propertyClass != null && InputStream.class.isAssignableFrom(propertyClass)) {
                    final InputStream value = (InputStream)getProperty(jcrNode, propertyIdentifier, propertyClass);
                    configurationNode.getInstanceMetadata().setStreamProperty(propertyName, value);
                }

            }
        }

    }

    private static ConfigurationNode loadOneChild( final ConfigurationNode parentNode,
                                                   final Node jcrChild,
                                                   final StaticMetadata staticMetadataForChild,
                                                   final Class<? extends ConfigurationNode> childNodeClass ) throws Exception {
        final Serializable keyValue = getProperty(jcrChild, "osl:" //$NON-NLS-1$
                                                            + staticMetadataForChild.keyPropertyName(),
                                                  staticMetadataForChild.keyPropertyType());
        final String childNodeClassName = jcrChild.getName();
        final ConfigurationNode newNode = classHelper.createInstance(keyValue, parentNode, childNodeClassName);
        if (Artifact.class.isAssignableFrom(childNodeClass)) {
            newNode.getInstanceMetadata().setPropertyIgnoringListener(Artifact.KeyProperties.UUID.toString(), jcrChild.getUUID());
        }
        newNode.getInstanceMetadata().setSavedUniqueId(jcrChild.getUUID());
        return newNode;
    }

    /**
     * JCR session
     */
    private final Session session;

    /**
     * Constructor. It's mandatory that the session is valid during object liveness.
     * 
     * @param session valid session
     * @throws ConfigurationException
     */
    public JcrSessionConfigurationManager(
                                           final Session session ) throws ConfigurationException {
        checkNotNull("session", session); //$NON-NLS-1$
        checkCondition("session", session.isLive()); //$NON-NLS-1$
        this.session = session;
        this.initDataInsideSession();
    }

    public void closeResources() {
        this.session.logout();
    }

    private Node create( final Node parentNode,
                         final String nodePath,
                         final String keyPropertyName,
                         final Serializable keyPropertyValue,
                         final Class<? extends Serializable> keyPropertyType ) throws Exception {
        checkNotNull("parentNode", parentNode); //$NON-NLS-1$
        checkNotEmpty("nodePath", nodePath); //$NON-NLS-1$
        final Node newNode = parentNode.addNode(nodePath);
        if (keyPropertyName != null && !"".equals(keyPropertyName)) { //$NON-NLS-1$
            this.setProperty(newNode, "osl:" + keyPropertyName, //$NON-NLS-1$
                             keyPropertyType, keyPropertyValue);
        }
        newNode.addMixin("mix:referenceable"); //$NON-NLS-1$
        if (nodePath.equals("osl:configuration")) { //$NON-NLS-1$
            newNode.addMixin("mix:versionable"); //$NON-NLS-1$
        }
        return newNode;

    }

    /**
     * Method to create nodes on jcr only when necessary.
     * 
     * @param parentJcrNode
     * @param currentNode
     * @param nodePath
     * @param keyPropertyName
     * @param keyPropertyValue
     * @param keyPropertyType
     * @return
     * @throws ConfigurationException
     */
    private Node createIfDontExists( final Node parentJcrNode,
                                     final ConfigurationNode currentNode,
                                     final String nodePath,
                                     final String keyPropertyName,
                                     final Serializable keyPropertyValue,
                                     final Class<? extends Serializable> keyPropertyType ) throws ConfigurationException {
        checkNotNull("parentJcrNode", parentJcrNode); //$NON-NLS-1$
        checkNotEmpty("nodePath", nodePath); //$NON-NLS-1$
        try {
            try {
                final String xpath = XpathSupport.getCompleteXpathFor(currentNode);
                Node foundNode = JcrSupport.findUnique(this.session, xpath);
                if (foundNode == null) {
                    foundNode = this.create(parentJcrNode, nodePath, keyPropertyName, keyPropertyValue, keyPropertyType);

                }
                return foundNode;
            } catch (final PathNotFoundException e) {
                final Node newNode = this.create(parentJcrNode, nodePath, keyPropertyName, keyPropertyValue, keyPropertyType);

                return newNode;
            }

        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

    private <T> void fillResultForEachItem( final ConfigurationNode root,
                                            final Node node,
                                            final Class<T> nodeType,
                                            final Set<T> result )
        throws RepositoryException, ItemNotFoundException, AccessDeniedException, ConfigurationException, Exception {
        final Stack<Node> nodeStack = new Stack<Node>();
        Node n = node;
        while (n.getDepth() != 1) {
            nodeStack.push(n);
            n = n.getParent();
        }
        ConfigurationNode lastParent = root;
        lookingForChildrenInsideJcr: while (!nodeStack.isEmpty()) {
            n = nodeStack.pop();
            final String name = n.getName();
            if (!name.startsWith(DEFAULT_OSL_PREFIX)) {
                continue lookingForChildrenInsideJcr;
            }
            final Class<? extends ConfigurationNode> childType = classHelper.getNodeClassFromName(name);
            if (childType.equals(root.getClass())) {
                continue lookingForChildrenInsideJcr;
            }
            final StaticMetadata childStaticMetadata = classHelper.getStaticMetadataFromClass(childType);
            final String childKeyPropertyName = DEFAULT_OSL_PREFIX + ":" //$NON-NLS-1$
                                                + childStaticMetadata.keyPropertyName();
            final Class<? extends Serializable> propertyClass = childStaticMetadata.keyPropertyType();
            final Serializable keyProperty = getProperty(n, childKeyPropertyName, propertyClass);
            final Set<? extends ConfigurationNode> children = findAllNodesOfType(lastParent, childType);
            lookingForParent: for (final ConfigurationNode child : children) {
                if (child.getInstanceMetadata().getKeyPropertyValue().equals(keyProperty)) {
                    lastParent = child;
                    break lookingForParent;
                }
            }
            if (nodeStack.size() == 0) {
                result.add(nodeType.cast(lastParent));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T extends ConfigurationNode, K extends Serializable> T findNodeByUuidAndVersion( final ConfigurationNode root,
                                                                                             final Class<T> nodeType,
                                                                                             final String uuid,
                                                                                             final String version )
        throws ConfigurationException {
        try {
            final Set<T> result = new HashSet<T>();
            final Node node = this.session.getNodeByUUID(uuid);
            this.fillResultForEachItem(root, node, nodeType, result);
            if (result.size() > 0) {
                return result.iterator().next();
            }
            return null;

        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }

    }

    /**
     * {@inheritDoc}
     */
    public <T extends ConfigurationNode, K extends Serializable> Set<T> findNodesByKey( final ConfigurationNode root,
                                                                                        final Class<T> nodeType,
                                                                                        final K key )
        throws ConfigurationException {
        try {
            checkNotNull("root", root); //$NON-NLS-1$
            checkNotNull("nodeType", nodeType); //$NON-NLS-1$
            checkNotNull("key", key); //$NON-NLS-1$
            checkCondition("sessionAlive", this.session.isLive()); //$NON-NLS-1$
            final Set<T> result = new HashSet<T>();
            final StaticMetadata metadata = nodeType.getAnnotation(StaticMetadata.class);
            final String keyPropertyName = metadata.keyPropertyName();

            final String nodePath = classHelper.getNameFromNodeClass(nodeType);
            final String pathToFind = format("//*/{0}[@osl:{1}=''{2}'']", //$NON-NLS-1$
                                             nodePath, keyPropertyName, key);
            final NodeIterator nodes = JcrSupport.findAll(this.session, pathToFind);
            while (nodes.hasNext()) {
                final Node n = nodes.nextNode();
                this.fillResultForEachItem(root, n, nodeType, result);
            }
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

    /**
     * Just create the "osl" prefix if that one doesn't exists, and after that created the node "osl:configuration" if that
     * doesn't exists.
     * 
     * @throws ConfigurationException
     */
    private void initDataInsideSession() throws ConfigurationException {
        try {
            final NamespaceRegistry namespaceRegistry = this.session.getWorkspace().getNamespaceRegistry();
            if (!this.prefixExists(namespaceRegistry)) {
                namespaceRegistry.registerNamespace(DEFAULT_OSL_PREFIX, NS_DESCRIPTION);
            }
        } catch (final Exception e) {
            logAndThrowNew(e, ConfigurationException.class);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @throws NoConfigurationYetException
     */
    public Configuration load( final LazyType lazyType ) throws ConfigurationException, NoConfigurationYetException {
        checkNotNull("lazyType", lazyType); //$NON-NLS-1$
        checkCondition("sessionAlive", this.session.isLive()); //$NON-NLS-1$
        final String defaultRootNode = classHelper.getNameFromNodeClass(Configuration.class);
        Node rootJcrNode;
        try {
            rootJcrNode = this.session.getRootNode().getNode(defaultRootNode);
        } catch (final PathNotFoundException e) {
            throw logAndReturnNew(e, NoConfigurationYetException.class);
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);

        }

        try {
            final Configuration rootNode = new Configuration();
            if (LazyType.EAGER.equals(lazyType)) {
                loadChildrenAndProperties(rootJcrNode, rootNode, rootNode.getInstanceMetadata().getStaticMetadata());
                this.setUuidData(rootJcrNode, rootNode);

            } else if (LazyType.LAZY.equals(lazyType)) {
                final JcrDataLoader dataLoader = new JcrDataLoader(rootNode, rootJcrNode);
                rootNode.getInstanceMetadata().getSharedData().setDataLoader(dataLoader);
            } else {
                logAndThrow(new IllegalArgumentException("Invalid lazyType")); //$NON-NLS-1$
            }

            rootNode.getInstanceMetadata().getSharedData().markAsSaved();
            rootNode.getInstanceMetadata().setSavedUniqueId(rootJcrNode.getUUID());
            return rootNode;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

    /**
     * Verify if the prefix "osl" exists
     * 
     * @param namespaceRegistry
     * @return true if exists
     * @throws RepositoryException
     */
    private boolean prefixExists( final NamespaceRegistry namespaceRegistry ) throws RepositoryException {
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

    private void removeNode( final ConfigurationNode oldItem ) throws Exception {
        assert oldItem != null;
        final String pathToFind = XpathSupport.getCompleteXpathFor(oldItem);
        final Node node = JcrSupport.findUnique(this.session, pathToFind);
        node.remove();
    }

    /**
     * {@inheritDoc}
     */
    public void save( final Configuration configuration ) throws ConfigurationException {
        checkNotNull("group", configuration); //$NON-NLS-1$
        checkCondition("sessionAlive", this.session.isLive()); //$NON-NLS-1$

        try {

            final Set<ConfigurationNode> dirtyNodes = configuration.getInstanceMetadata().getSharedData().getDirtyNodes();

            final ConfigurationNode[] dirtyNodesAsArray = dirtyNodes.toArray(new ConfigurationNode[0]);

            sort(dirtyNodesAsArray, new ParentNumberComparator());

            final Map<ConfigurationNode, Node> alreadySaved = new HashMap<ConfigurationNode, Node>();
            for (final ConfigurationNode node : dirtyNodesAsArray) {
                if (alreadySaved.containsKey(node)) {
                    continue;
                }
                Node parentJcrNode = null;
                if (node.getInstanceMetadata().getDefaultParent() == null) {
                    parentJcrNode = this.session.getRootNode();
                } else if (node.getInstanceMetadata().getDefaultParent() != null) {
                    if (dirtyNodes.contains(node.getInstanceMetadata().getDefaultParent())) {

                        parentJcrNode = alreadySaved.get(node.getInstanceMetadata().getDefaultParent());
                        if (parentJcrNode == null) {
                            logAndThrow(new IllegalStateException("Dirty node without dirty parent")); //$NON-NLS-1$
                        }
                    } else {
                        final String pathToFind = XpathSupport.getCompleteXpathFor(node.getInstanceMetadata().getDefaultParent());
                        parentJcrNode = JcrSupport.findUnique(this.session, pathToFind);
                        if (parentJcrNode == null) {
                            logAndThrow(new IllegalStateException("Dirty node without parent")); //$NON-NLS-1$
                        }
                    }
                }
                if (parentJcrNode == null) {
                    logAndThrow(new IllegalStateException("Parent for dirty node not found")); //$NON-NLS-1$
                }

                final StaticMetadata metadata = node.getInstanceMetadata().getStaticMetadata();

                final String nodePath = JcrSessionConfigurationManager.classHelper.getNameFromNodeClass(node.getClass());

                final Node newJcrNode = this.createIfDontExists(parentJcrNode, node, nodePath, metadata.keyPropertyName(),
                                                                node.getInstanceMetadata().getKeyPropertyValue(),
                                                                metadata.keyPropertyType());
                if (node instanceof Artifact) {
                    final Artifact a = (Artifact)node;
                    a.getInstanceMetadata().setPropertyIgnoringListener(Artifact.KeyProperties.UUID.name(), newJcrNode.getUUID());
                }
                node.getInstanceMetadata().setSavedUniqueId(newJcrNode.getUUID());
                alreadySaved.put(node, newJcrNode);
                this.saveProperties(node, newJcrNode);
            }

            final List<ItemChangeEvent<ConfigurationNode>> lastChanges = configuration.getInstanceMetadata().getSharedData().getNodeChangesSinceLastSave();

            for (final ItemChangeEvent<ConfigurationNode> event : lastChanges) {
                if (ItemChangeType.EXCLUDED.equals(event.getType())) {
                    this.removeNode(event.getOldItem());
                }
            }
            this.session.save();

            final String configurationPath = classHelper.getNameFromNodeClass(Configuration.class);
            final Node configurationJcrNode = this.session.getRootNode().getNode(configurationPath);

            final Version version = configurationJcrNode.checkin();
            configurationJcrNode.checkout();
            final String versionName = version.getName();
            final Set<Artifact> artifacts = findAllNodesOfType(configuration, Artifact.class);
            for (final Artifact a : artifacts) {
                a.getInstanceMetadata().setPropertyIgnoringListener(Artifact.KeyProperties.version.toString(), versionName);
            }
            configuration.getInstanceMetadata().getSharedData().markAsSaved();
        } catch (final Exception e) {
            logAndThrowNew(e, ConfigurationException.class);
        }
    }

    private void saveProperties( final ConfigurationNode configurationNode,
                                 final Node innerNewJcrNode ) throws Exception {
        final Map<String, Object> properties = configurationNode.getInstanceMetadata().getProperties();
        for (final Map.Entry<String, Object> entry : properties.entrySet()) {
            final Object value = entry.getValue();
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
    @SuppressWarnings( "boxing" )
    private void setProperty( final Node jcrNode,
                              final String propertyName,
                              final Class<?> propertyClass,
                              final Object value ) throws Exception {
        if (value == null) {
            jcrNode.setProperty(propertyName, (String)null);
        } else if (Boolean.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Boolean)value);
        } else if (Calendar.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Calendar)value);
        } else if (Double.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Double)value);
        } else if (Long.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Long)value);
        } else if (String.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (String)value);
        } else if (Integer.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Integer)value);
        } else if (Byte.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Byte)value);
        } else if (Float.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, (Float)value);
        } else if (Date.class.equals(propertyClass)) {
            jcrNode.setProperty(propertyName, stringFromDate((Date)value));
        } else if (propertyClass.isEnum()) {
            jcrNode.setProperty(propertyName, ((Enum<?>)value).name());
        } else if (InputStream.class.isAssignableFrom(propertyClass)) {
            jcrNode.setProperty(propertyName, (InputStream)value);
        } else if (Serializable.class.isAssignableFrom(propertyClass)) {
            final String valueAsString = serializeToBase64((Serializable)value);
            jcrNode.setProperty(propertyName, valueAsString);
        } else {
            throw new IllegalStateException(format("Invalid class for property {0} : {1}", propertyName, //$NON-NLS-1$
                                                   propertyClass));
        }
    }

    private void setUuidData( final Node rootJcrNode,
                              final Configuration rootNode ) throws RepositoryException, UnsupportedRepositoryOperationException {
        final Set<Artifact> artifacts = findAllNodesOfType(rootNode, Artifact.class);
        final String versionName = rootJcrNode.getBaseVersion().getName();
        for (final Artifact a : artifacts) {
            a.getInstanceMetadata().setPropertyIgnoringListener(Artifact.KeyProperties.version.toString(), versionName);
        }
    }
}
