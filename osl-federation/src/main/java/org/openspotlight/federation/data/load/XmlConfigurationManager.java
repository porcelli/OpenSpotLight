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

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Dates.dateFromString;
import static org.openspotlight.common.util.Dates.stringFromDate;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Serialization.readFromBase64;
import static org.openspotlight.common.util.Serialization.serializeToBase64;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;
import static org.openspotlight.federation.data.util.ConfigurationNodes.findAllNodesOfType;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.GeneratedNode;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.impl.Configuration;

/**
 * This configuration manager class loads and stores the configuration on a
 * simple and easily readable xml file, since the xml exported from jcr was to
 * much dirty for using as a simple configuration.
 * 
 * LATER_TASK implement node property
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class XmlConfigurationManager implements ConfigurationManager {
    
    private final NodeClassHelper classHelper = new NodeClassHelper();
    
    private final String url;
    private final SAXReader reader = new SAXReader();
    
    /**
     * Default constructor that receives a file url and a boolean to mark the
     * artifacts as an ignored node. This should be useful to use this as a
     * simple configuration and not to store all the artifact metadata.
     * 
     * @param url
     */
    public XmlConfigurationManager(final String url) {
        checkNotEmpty("url", url); //$NON-NLS-1$
        this.url = url;
    }
    
    @SuppressWarnings("unchecked")
    private void createEachXmlNode(final Element element,
            final ConfigurationNode configurationNode) throws SLException {
        if (configurationNode instanceof GeneratedNode) {
            return;
        }
        final Map<String, Object> properties = configurationNode
                .getInstanceMetadata().getProperties();
        final String[] propKeys = configurationNode.getInstanceMetadata()
                .getStaticMetadata().propertyNames();
        final Class<?>[] propValues = configurationNode.getInstanceMetadata()
                .getStaticMetadata().propertyTypes();
        final Map<String, Class<?>> propertyTypesMap = map(ofKeys(propKeys),
                andValues(propValues));
        for (final Map.Entry<String, Object> propertyEntry : properties
                .entrySet()) {
            if (!propertyEntry.getKey().equals("")) { //$NON-NLS-1$
                if (Serializable.class.isAssignableFrom(propertyTypesMap
                        .get(propertyEntry.getKey()))) {
                    this.setPropertyOnXml(element, propertyEntry.getKey(),
                            (Serializable) propertyEntry.getValue(),
                            propertyTypesMap.get(propertyEntry.getKey()));
                }
            }
        }
        for (final Class<? extends ConfigurationNode> configuredChildClass : configurationNode
                .getInstanceMetadata().getStaticMetadata().validChildrenTypes()) {
            if (configuredChildClass.equals(ConfigurationNode.class)) {
                continue;
            }
            if (GeneratedNode.class.isAssignableFrom(configuredChildClass)) {
                continue;
            }
            
            final Set<Serializable> keys = (Set<Serializable>) configurationNode
                    .getInstanceMetadata().getKeyFromChildrenOfTypes(
                            configuredChildClass);
            for (final Serializable key : keys) {
                final ConfigurationNode innerNode = configurationNode
                        .getInstanceMetadata()
                        .getChildByKeyValue(
                                (Class<? extends ConfigurationNode>) configuredChildClass,
                                key);
                final Class<? extends ConfigurationNode> childClass = innerNode
                        .getClass();
                final Element newElement = element
                        .addElement(removeBegginingFrom("osl:", //$NON-NLS-1$
                                this.classHelper
                                        .getNameFromNodeClass(childClass)));
                if (!innerNode.getInstanceMetadata().getStaticMetadata()
                        .keyPropertyName().equals("")) { //$NON-NLS-1$
                    newElement.addAttribute(innerNode.getInstanceMetadata()
                            .getStaticMetadata().keyPropertyName(), convert(
                            key, String.class));
                }
                
                this.createEachXmlNode(newElement, innerNode);
            }
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public <T extends ConfigurationNode, K extends Serializable> Set<T> findNodesByKey(
            final ConfigurationNode root, final Class<T> nodeType, final K key)
            throws ConfigurationException {
        checkNotNull("root", root); //$NON-NLS-1$
        checkNotNull("nodeType", nodeType); //$NON-NLS-1$
        checkNotNull("key", key); //$NON-NLS-1$
        final Set<T> allNodesOfType = findAllNodesOfType(root, nodeType);
        final Set<T> result = new HashSet<T>();
        for (final T t : allNodesOfType) {
            if (key.equals(t.getInstanceMetadata().getKeyPropertyValue())) {
                result.add(t);
            }
        }
        return result;
    }
    
    /**
     * {@inheritDoc}
     */
    public Configuration load(final LazyType type)
            throws ConfigurationException {
        checkNotNull("type", type); //$NON-NLS-1$
        checkCondition("typeEqualsLazy", LazyType.NON_LAZY.equals(type)); //$NON-NLS-1$
        try {
            final Document document = this.reader.read(this.url);
            final Element root = document.getRootElement();
            final Configuration configuration = new Configuration();
            this.loopOnEachElement(root, configuration);
            configuration.getInstanceMetadata().getSharedData().markAsSaved();
            return configuration;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loopOnEachElement(final Element parentElement,
            final ConfigurationNode parentNode) throws Exception {
        final String[] propKeys = parentNode.getInstanceMetadata()
                .getStaticMetadata().propertyNames();
        final Class<?>[] propValues = parentNode.getInstanceMetadata()
                .getStaticMetadata().propertyTypes();
        final Map<String, Class<?>> propertyTypes = map(ofKeys(propKeys),
                andValues(propValues));
        final StaticMetadata parentStaticMetadata = this.classHelper
                .getStaticMetadataFromClass(parentNode.getClass());
        final String parentKeyProperty = parentStaticMetadata.keyPropertyName();
        for (final Iterator<Attribute> properties = parentElement
                .attributeIterator(); properties.hasNext();) {
            
            final Attribute nextProperty = properties.next();
            final String propertyName = nextProperty.getName();
            if (parentKeyProperty.equals(propertyName)) {
                continue;
            }
            final String valueAsString = nextProperty.getStringValue();
            this.setPropertyOnNode(parentNode, propertyName, propertyTypes
                    .get(propertyName), valueAsString);
        }
        for (final Iterator<Element> elements = parentElement.elementIterator(); elements
                .hasNext();) {
            final Element nextElement = elements.next();
            final String nodeClassName = nextElement.getName();
            final Class<? extends ConfigurationNode> nodeClass = this.classHelper
                    .getNodeClassFromName("osl:" + nodeClassName); //$NON-NLS-1$
            if (GeneratedNode.class.isAssignableFrom(nodeClass)) {
                continue;
            }
            final StaticMetadata staticMetadata = this.classHelper
                    .getStaticMetadataFromClass(nodeClass);
            final String keyProperty = staticMetadata.keyPropertyName();
            final ConfigurationNode newNode;
            if (!"".equals(keyProperty)) { //$NON-NLS-1$
                final String keyPropertyValueAsString = nextElement
                        .attributeValue(keyProperty);
                final Serializable keyPropertyValue = convert(
                        keyPropertyValueAsString, staticMetadata
                                .keyPropertyType());
                newNode = this.classHelper.createInstance(keyPropertyValue,
                        parentNode, "osl:" + nodeClassName); //$NON-NLS-1$
            } else {
                newNode = this.classHelper.createInstance(null, parentNode,
                        "osl:" + nodeClassName); //$NON-NLS-1$
            }
            
            this.loopOnEachElement(nextElement, newNode);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void save(final Configuration configuration)
            throws ConfigurationException {
        try {
            if (this.url.indexOf("/") != -1) { //$NON-NLS-1$
                final String dir = this.url.substring(0, this.url
                        .lastIndexOf("/")); //$NON-NLS-1$
                new File(dir).mkdirs();
            }
            
            final Document document = DocumentFactory.getInstance()
                    .createDocument();
            final Element element = document.addElement("configuration"); //$NON-NLS-1$
            final ConfigurationNode configurationNode = configuration;
            this.createEachXmlNode(element, configurationNode);
            final OutputStream os = new BufferedOutputStream(
                    new FileOutputStream(this.url));
            final OutputFormat outformat = OutputFormat.createPrettyPrint();
            final XMLWriter writer = new XMLWriter(os, outformat);
            writer.write(document);
            writer.flush();
            configuration.getInstanceMetadata().getSharedData().markAsSaved();
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
    private void setPropertyOnNode(final ConfigurationNode newNode,
            final String propertyName, final Class<?> propertyClass,
            final String valueAsString) throws Exception {
        if (valueAsString == null) {
            newNode.getInstanceMetadata().setProperty(propertyName, null);
        } else if (Boolean.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    Boolean.valueOf(valueAsString));
        } else if (Double.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    Double.valueOf(valueAsString));
        } else if (Long.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    Long.valueOf(valueAsString));
        } else if (String.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    valueAsString);
        } else if (Integer.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    Integer.valueOf(valueAsString));
        } else if (Byte.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    Byte.valueOf(valueAsString));
        } else if (Float.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    Float.valueOf(valueAsString));
        } else if (Date.class.equals(propertyClass)) {
            newNode.getInstanceMetadata().setProperty(propertyName,
                    dateFromString(valueAsString));
        } else if (propertyClass.isEnum()) {
            final Field[] flds = propertyClass.getDeclaredFields();
            for (final Field f : flds) {
                if (f.isEnumConstant()) {
                    if (f.getName().equals(propertyName)) {
                        final Serializable newValue = (Serializable) f
                                .get(null);
                        newNode.getInstanceMetadata().setProperty(propertyName,
                                newValue);
                        break;
                    }
                }
            }
        } else {
            final Serializable value = readFromBase64(valueAsString);
            newNode.getInstanceMetadata().setProperty(propertyName, value);
        }
        
    }
    
    @SuppressWarnings("boxing")
    private void setPropertyOnXml(final Element newElement, final String key,
            final Serializable value, final Class<?> propertyClass)
            throws SLException {
        if (value == null) {
            newElement.addAttribute(key, null);
        } else if (Boolean.class.equals(propertyClass)) {
            newElement.addAttribute(key, Boolean.toString((Boolean) value));
        } else if (Double.class.equals(propertyClass)) {
            newElement.addAttribute(key, Double.toString((Double) value));
        } else if (Long.class.equals(propertyClass)) {
            newElement.addAttribute(key, Long.toString((Long) value));
        } else if (String.class.equals(propertyClass)) {
            newElement.addAttribute(key, (String) value);
        } else if (Integer.class.equals(propertyClass)) {
            newElement.addAttribute(key, Integer.toString((Integer) value));
        } else if (Byte.class.equals(propertyClass)) {
            newElement.addAttribute(key, Byte.toString((Byte) value));
        } else if (Float.class.equals(propertyClass)) {
            newElement.addAttribute(key, Float.toString((Float) value));
        } else if (Date.class.equals(propertyClass)) {
            newElement.addAttribute(key, stringFromDate((Date) value));
        } else if (propertyClass.isEnum()) {
            newElement.addAttribute(key, ((Enum<?>) value).name());
        } else {
            final String valueAsString = serializeToBase64(value);
            newElement.addAttribute(key, valueAsString);
        }
    }
    
}
