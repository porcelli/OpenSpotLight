package org.openspotlight.federation.data.test;

import org.junit.Test;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;

public abstract class AbstractConfigurationManagerTest extends NodeTest {

	protected abstract ConfigurationManager createInstance();

	@Test
	public void shouldSaveTheConfiguration() throws Exception {
		Configuration configuration = createSampleData();
		ConfigurationManager manager = createInstance();
		manager.save(configuration);
		Configuration anotherGroup = manager.load();
		assertTheSameInitialDataOnSomeNodes(anotherGroup, true);
	}
}
