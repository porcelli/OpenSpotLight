package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for JCR test time configuration
 * 
 * @author feu
 * 
 */
public class SimpleJcrTest {

	public static final String TESTDATA_PATH = "./src/test/resources/";
	public static final String JACKRABBIT_DATA_PATH = "./target/testdata/jackrabbittest/";
	public static final String REPOSITORY_DIRECTORY_PATH = JACKRABBIT_DATA_PATH
			+ "repository";
	public static final String REPOSITORY_CONFIG_PATH = TESTDATA_PATH
			+ "jackrabbitDerbyTestRepositoryConfig.xml";
	public static final String DERBY_SYSTEM_HOME = JACKRABBIT_DATA_PATH
			+ "/derby";

	private Session session;

	private TransientRepository repository;

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
	}

	@After
	public void shutdown() throws Exception {
		session.logout();
		repository.shutdown();
	}

	@Test
	public void shouldCreateAndRetrieveSomeNodes() throws Exception {
		session.getWorkspace().getNamespaceRegistry().registerNamespace(
				"dummy", "www.dummy.com");
		Node rootNode = session.getRootNode();
		Node newNode = rootNode.addNode("dummy:newNode");
		newNode.addNode("newInnerNode").addNode("dummy:anotherOne");
		session.save();

		assertThat(session.getRootNode().getNode("dummy:newNode").getNode(
				"newInnerNode").getNode("dummy:anotherOne"), is(notNullValue()));

	}
	
}
