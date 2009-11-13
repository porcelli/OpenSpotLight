package org.openspotlight.federation.loader;

import java.io.File;

import org.junit.BeforeClass;

/**
 * The Class XmlConfigurationManagerTest.
 */
public class XmlConfigurationManagerTest extends AbstractConfigurationManagerTest {

    @BeforeClass
    public static void cleanFiles() {
        new File("./target/XmlConfigurationManagerTest/newSavedConfiguration.xml").delete();
    }

    @Override
    protected ConfigurationManager createNewConfigurationManager() {
        return XmlConfigurationManagerFactory.loadMutableFromFile("./target/XmlConfigurationManagerTest/newSavedConfiguration.xml");
    }

}
