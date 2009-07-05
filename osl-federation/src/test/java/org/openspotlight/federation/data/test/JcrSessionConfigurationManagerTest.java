package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;

public class JcrSessionConfigurationManagerTest extends
		AbstractConfigurationManagerTest {

	public static final String TESTATA_PATH = "./src/test/resources/";
	public static final String JACKRABBIT_DATA_PATH = "./target/testdata/jackrabbittest/";
	public static final String REPOSITORY_DIRECTORY_PATH = JACKRABBIT_DATA_PATH
			+ "repository";
	public static final String REPOSITORY_CONFIG_PATH = TESTATA_PATH
			+ "jackrabbitDerbyTestRepositoryConfig.xml";
	public static final String DERBY_SYSTEM_HOME = JACKRABBIT_DATA_PATH
			+ "/derby";

	private Session session;

	private TransientRepository repository;

	private ConfigurationManager implementation;

	@Before
	public void initializeSomeConfiguration() throws Exception {
		delete(JACKRABBIT_DATA_PATH);
		System.setProperty("derby.system.home", DERBY_SYSTEM_HOME);
		this.repository = new TransientRepository(REPOSITORY_CONFIG_PATH,
				REPOSITORY_DIRECTORY_PATH);
		SimpleCredentials creds = new SimpleCredentials("jsmith", "password"
				.toCharArray());
		this.session = repository.login(creds);
		assertThat(session, is(notNullValue()));
		this.implementation = new JcrSessionConfigurationManager(session);
	}

	@After
	public void shutdown() throws Exception {
		if (session != null)
			session.logout();
		if (repository != null)
			repository.shutdown();
	}

	@Override
	protected ConfigurationManager createInstance() {
		return implementation;
	}

}
