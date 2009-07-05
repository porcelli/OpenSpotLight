package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.AbstractConfigurationNode;
import org.openspotlight.federation.data.Repository;
import org.openspotlight.federation.data.load.ConfigurationManager.NodeClassHelper;

public class NodeClassHelperTest {

	private NodeClassHelper nodeClassHelper;

	@Before
	public void createNodeHelper() {
		nodeClassHelper = new NodeClassHelper();
	}

	@Test
	public void shouldCreateInstance() throws Exception {
		Configuration configuration = new Configuration();
		Repository theSameRepository = new Repository("name", configuration);
		Repository newRepository = nodeClassHelper.createInstance("name",
				configuration, "osl:repository");
		assertThat(newRepository, is(theSameRepository));

	}

	@Test(expected = ConfigurationException.class)
	public void shouldThrowExceptionWhenCreatingInstanceWithInvalidParameters()
			throws Exception {
		Configuration configuration = new Configuration();
		nodeClassHelper.createInstance("name", configuration, "osl:invalidName");
	}

	@Test
	public void shouldCreateRootInstance() throws Exception {
		Configuration theSameGroup = new Configuration();
		Configuration newGroup = nodeClassHelper.createRootInstance("osl:configuration");
		assertThat(newGroup, is(theSameGroup));
	}

	@Test(expected = ConfigurationException.class)
	public void shouldThrowExceptionWhenCreatingRootInstanceWithInvalidParameters()
			throws Exception {
		nodeClassHelper.createRootInstance("osl:invalidName");
	}

	@Test
	public void shouldGetNameFromNodeClass() throws Exception {
		String name = nodeClassHelper.getNameFromNodeClass(Repository.class);
		assertThat(name, is("osl:repository"));
	}

	@Test
	public void shouldGetNodeClassFromName() throws Exception {
		Class<? extends AbstractConfigurationNode> clazz = nodeClassHelper.getNodeClassFromName("osl:repository");
		assertThat(Repository.class.equals(clazz), is(true));
	}

	@Test(expected = ConfigurationException.class)
	public void shouldThrowExceptionWhenGetingNodeClassFromNameWithInvalidParameters()
			throws Exception {
		nodeClassHelper.getNodeClassFromName("osl:invalidName");
	}

}
