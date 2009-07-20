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

import static org.openspotlight.common.util.Dates.dateFromString;
import static org.openspotlight.common.util.Dates.stringFromDate;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Serialization.readFromBase64;
import static org.openspotlight.common.util.Serialization.serializeToBase64;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
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
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.impl.Configuration;

/**
 * This configuration manager class loads and stores the configuration on a
 * simple and easily readable xml file, since the xml exported from jcr was to
 * much dirty for using as a simple configuration.
 * 
 * FIXME implement node property
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class XmlConfigurationManager implements ConfigurationManager {
    
    private final NodeClassHelper classHelper = new NodeClassHelper();
    
    private final boolean ignoreArtifacts;
    private final String url;
    private final SAXReader reader = new SAXReader();
    
    /**
     * Default constructor that receives a file url and a boolean to mark the
     * artifacts as an ignored node. This should be useful to use this as a
     * simple configuration and not to store all the artifact metadata.
     * 
     * @param url
     * @param ignoreArtifacts
     */
    public XmlConfigurationManager(final String url,
            final boolean ignoreArtifacts) {
        this.url = url;
        this.ignoreArtifacts = ignoreArtifacts;
    }
    
    @SuppressWarnings("unchecked")
    private void createEachXmlNode(final Element element,
            final ConfigurationNode configurationNode) throws SLException {
        final Set<Class<? extends ConfigurationNode>> childrenClasses = configurationNode
                .getStaticMetadata().getChildrenValidNodeTypes();
        for (final Class<? extends ConfigurationNode> clazz : childrenClasses) {
            
            final Set<Serializable> keys = (Set<Serializable>) configurationNode
                    .getInstanceMetadata().getKeysFromChildrenOfType(clazz);
            for (final Serializable key : keys) {
                final ConfigurationNode innerNode = configurationNode
                        .getInstanceMetadata()
                        .getChildByKeyValue(
                                (Class<? extends ConfigurationNode>) clazz, key);
                final Element newElement = element
                        .addElement(removeBegginingFrom("osl:", //$NON-NLS-1$
                                this.classHelper.getNameFromNodeClass(clazz)));
                newElement.addAttribute(innerNode.getStaticMetadata()
                        .getKeyProperty(), key.toString());
                final Map<String, Serializable> properties = innerNode
                        .getInstanceMetadata().getProperties();
                final Map<String, Class<?>> propertyTypes = innerNode
                        .getStaticMetadata().getPropertyTypes();
                for (final Map.Entry<String, Serializable> propertyEntry : properties
                        .entrySet()) {
                    if (propertyEntry.getValue() != null) {
                        this.setPropertyOnXml(newElement, propertyEntry
                                .getKey(), propertyEntry.getValue(),
                                propertyTypes.get(propertyEntry.getKey()));
                    }
                }
                this.createEachXmlNode(newElement, innerNode);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Configuration load() throws ConfigurationException {
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
            final ConfigurationNode parentNode) throws SLException {
        for (final Iterator<Element> elements = parentElement.elementIterator(); elements
                .hasNext();) {
            final Element nextElement = elements.next();
            final String nodeClass = nextElement.getName();
            // FIXME create a list of ignored artifacts (using interfaces)
            if ((this.ignoreArtifacts && ("osl:streamArtifact" //$NON-NLS-1$
                    .equals(nodeClass)))
                    || "osl:jcrArtifact".equals(nodeClass)) { //$NON-NLS-1$ 
                continue;
            }
            final String keyProperty = "key";//FIXME get this in a dynamic way //$NON-NLS-1$
            final String nodeName = nextElement.attributeValue(keyProperty);
            final ConfigurationNode newNode = this.classHelper.createInstance(
                    nodeName, parentNode, "osl:" + nodeClass); //$NON-NLS-1$
            final Map<String, Class<?>> propertyTypes = newNode
                    .getStaticMetadata().getPropertyTypes();
            
            for (final Iterator<Attribute> properties = nextElement
                    .attributeIterator(); properties.hasNext();) {
                final Attribute nextProperty = properties.next();
                final String propertyName = nextProperty.getName();
                if (keyProperty.equals(propertyName)) {
                    continue;
                }
                final String valueAsString = nextProperty.getStringValue();
                this.setPropertyOnNode(newNode, propertyName, propertyTypes
                        .get(propertyName), valueAsString);
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
            final String valueAsString) throws SLException {
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
        } else {
            final String valueAsString = serializeToBase64(value);
            newElement.addAttribute(key, valueAsString);
        }
    }
    
}
