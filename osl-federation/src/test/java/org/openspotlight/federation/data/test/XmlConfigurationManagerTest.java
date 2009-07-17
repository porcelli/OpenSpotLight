package org.openspotlight.federation.data.test;

import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.XmlConfigurationManager;

public class XmlConfigurationManagerTest extends
		AbstractConfigurationManagerTest {

	@Override
	protected ConfigurationManager createInstance() {
		return new XmlConfigurationManager("./target/exampleConfigFile.xml",false);
	}

}
