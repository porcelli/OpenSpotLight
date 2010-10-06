package org.openspotlight.federation.loader;

import com.google.inject.AbstractModule;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Oct 6, 2010
 * Time: 10:40:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class XmlConfigurationManagerModule extends AbstractModule{

    private final String xmlFileLocation;

    public XmlConfigurationManagerModule(String xmlFileLocation) {
        this.xmlFileLocation = xmlFileLocation;
    }

    @Override
    protected void configure() {
        bind(ConfigurationManagerFactory.class).to(XmlConfigurationManagerFactory.class);
        bind(String.class).annotatedWith(XmlConfigFileLocation.class).toInstance(xmlFileLocation);
    }
}
