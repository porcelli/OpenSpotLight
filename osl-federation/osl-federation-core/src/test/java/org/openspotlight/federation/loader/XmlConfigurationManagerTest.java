package org.openspotlight.federation.loader;


/**
 * The Class XmlConfigurationManagerTest.
 */
public class XmlConfigurationManagerTest extends AbstractConfigurationManagerTest {

    @Override
    protected ConfigurationManager createNewConfigurationManager() {
        return XmlConfigurationManagerFactory.loadMutableFromFile("./target/XmlConfigurationManagerTest/newSavedConfiguration.xml");
    }

}
