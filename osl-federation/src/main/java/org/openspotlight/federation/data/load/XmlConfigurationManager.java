package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Dates.stringFromDate;
import static org.openspotlight.common.util.Dates.dateFromString;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Serialization.serializeToBase64;
import static org.openspotlight.common.util.Serialization.readFromBase64;
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
import org.openspotlight.federation.data.AbstractConfigurationNode;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.ConfigurationNode;

/**
 * This configuration manager class loads and stores the configuration on a
 * simple and easily readable xml file, since the xml exported from jcr was to
 * much dirty for using as a simple configuration.
 * 
 * @author feu
 * 
 */
public class XmlConfigurationManager implements ConfigurationManager {

	private final NodeClassHelper classHelper = new NodeClassHelper();

	private final String DEFAULT_NAME = "name";
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
	public XmlConfigurationManager(String url, boolean ignoreArtifacts) {
		this.url = url;
		this.ignoreArtifacts = ignoreArtifacts;
	}

	/**
	 * {@inheritDoc}
	 */
	public Configuration load() throws ConfigurationException {
		try {
			Document document = reader.read(url);
			Element root = document.getRootElement();
			Configuration configuration = new Configuration();
			loopOnEachElement(root, configuration);
			configuration.markAsSaved();
			return configuration;
		} catch (Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	@SuppressWarnings("unchecked")
	private void loopOnEachElement(Element parentElement,
			ConfigurationNode parentNode) throws SLException {
		for (Iterator<Element> elements = parentElement.elementIterator(); elements
				.hasNext();) {
			Element nextElement = elements.next();
			String nodeClass = nextElement.getName();
			if (ignoreArtifacts && "osl:artifact".equals(nodeClass)) {
				continue;
			}
			String nodeName = nextElement.attributeValue(DEFAULT_NAME);
			ConfigurationNode newNode = classHelper.createInstance(nodeName,
					parentNode, "osl:" + nodeClass);
			Map<String, Class<?>> propertyTypes = newNode.getPropertyTypes();

			for (Iterator<Attribute> properties = nextElement
					.attributeIterator(); properties.hasNext();) {
				Attribute nextProperty = properties.next();
				String propertyName = nextProperty.getName();
				if (DEFAULT_NAME.equals(propertyName))
					continue;
				String valueAsString = nextProperty.getStringValue();
				setPropertyOnNode(newNode, propertyName, propertyTypes
						.get(propertyName), valueAsString);
			}
			loopOnEachElement(nextElement, newNode);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void save(Configuration configuration) throws ConfigurationException {
		try {
			Document document = DocumentFactory.getInstance().createDocument();
			Element element = document.addElement("configuration");
			ConfigurationNode configurationNode = configuration;
			createEachXmlNode(element, configurationNode);
			OutputStream os = new BufferedOutputStream(
					new FileOutputStream(url));
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(os, outformat);
			writer.write(document);
			writer.flush();
			configuration.markAsSaved();
		} catch (Exception e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	@SuppressWarnings("unchecked")
	private void createEachXmlNode(Element element,
			ConfigurationNode configurationNode) throws SLException {
		Set<Class<?>> childrenClasses = configurationNode.getChildrenTypes();
		for (Class<?> clazz : childrenClasses) {

			Set<String> names = configurationNode
					.getNamesFromChildrenOfType((Class<? extends ConfigurationNode>) clazz);
			for (String name : names) {
				ConfigurationNode innerNode = configurationNode.getChildByName(
						(Class<? extends ConfigurationNode>) clazz, name);
				Element newElement = element
						.addElement(removeBegginingFrom(
								"osl:",
								classHelper
										.getNameFromNodeClass((Class<? extends AbstractConfigurationNode>) clazz)));
				newElement.addAttribute(DEFAULT_NAME, name);
				Map<String, Serializable> properties = innerNode
						.getProperties();
				Map<String, Class<?>> propertyTypes = innerNode
						.getPropertyTypes();
				for (Map.Entry<String, Serializable> propertyEntry : properties
						.entrySet()) {
					if (propertyEntry.getValue() != null) {
						setPropertyOnXml(newElement, propertyEntry.getKey(),
								propertyEntry.getValue(), propertyTypes
										.get(propertyEntry.getKey()));
					}
				}
				createEachXmlNode(newElement, innerNode);
			}
		}
	}

	private void setPropertyOnXml(Element newElement, String key,
			Serializable value, Class<?> propertyClass) throws SLException {
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
			String valueAsString = serializeToBase64(value);
			newElement.addAttribute(key, valueAsString);
		}
	}

	private void setPropertyOnNode(ConfigurationNode newNode,
			String propertyName, Class<?> propertyClass, String valueAsString)
			throws SLException {
		if (valueAsString == null) {
			newNode.setProperty(propertyName, null);
		} else if (Boolean.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, Boolean.valueOf(valueAsString));
		} else if (Double.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, Double.valueOf(valueAsString));
		} else if (Long.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, Long.valueOf(valueAsString));
		} else if (String.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, valueAsString);
		} else if (Integer.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, Integer.valueOf(valueAsString));
		} else if (Byte.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, Byte.valueOf(valueAsString));
		} else if (Float.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, Float.valueOf(valueAsString));
		} else if (Date.class.equals(propertyClass)) {
			newNode.setProperty(propertyName, dateFromString(valueAsString));
		} else {
			Serializable value = readFromBase64(valueAsString);
			newNode.setProperty(propertyName, value);
		}

	}

}
