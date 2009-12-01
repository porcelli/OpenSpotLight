package org.openspotlight.graph;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;

public class MultipleGraphSessionsTest {

	private static SLGraph graph = null;
	private static SLGraphSession session = null;
	private static AuthenticatedUser user;

	@AfterClass
	public static void finish() {
		MultipleGraphSessionsTest.session.close();
		MultipleGraphSessionsTest.graph.shutdown();
	}

	@BeforeClass
	public static void init() throws AbstractFactoryException,
			SLInvalidCredentialException, IdentityException {
		final SLGraphFactory factory = AbstractFactory
				.getDefaultInstance(SLGraphFactory.class);
		MultipleGraphSessionsTest.graph = factory
				.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		MultipleGraphSessionsTest.user = securityFactory.createIdentityManager(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser,
				"password");
	}

	@Ignore //FIXME test this again
	@Test
	public void testMultipleSessions() throws AbstractFactoryException,
			Exception {
		final SLGraphSession session = MultipleGraphSessionsTest.graph
				.openSession(MultipleGraphSessionsTest.user);
		final SLGraphSession session2 = MultipleGraphSessionsTest.graph
				.openSession(MultipleGraphSessionsTest.user);

		final SLNode abstractTestNode = session.createContext("abstractTest")
				.getRootNode();
		final SLNode node1 = abstractTestNode.addNode("teste!");
		final SLNode testRootNode = session.createContext("test").getRootNode();
		final SLNode node2 = testRootNode.addNode("teste!");

		Assert.assertEquals(false, node1.getID().equals(node2.getID()));

		final String node1ID = node1.getID();
		final String node2ID = node2.getID();

		session.close();

		final SLNode abstractTestNode2 = session2.createContext("abstractTest")
				.getRootNode();
		final SLNode node3 = abstractTestNode2.addNode("teste!");
		final SLNode testRootNode2 = session2.createContext("test")
				.getRootNode();
		final SLNode node4 = testRootNode2.addNode("teste!");

		Assert.assertEquals(false, node3.getID().equals(node4.getID()));

		System.out.println("node4: " + node4.getID());
		System.out.println("node2ID: " + node2ID);

		System.out.println("node3: " + node3.getID());
		System.out.println("node1ID: " + node1ID);

		Assert.assertEquals(true, node2ID.equals(node4.getID()));
		Assert.assertEquals(true, node1ID.equals(node3.getID()));

		session2.close();
	}

	@Test
	public void testOpenCloseSessions() throws AbstractFactoryException,
			SLGraphException, SLInvalidCredentialException {
		MultipleGraphSessionsTest.session = MultipleGraphSessionsTest.graph
				.openSession(MultipleGraphSessionsTest.user);

		SLNode abstractTestNode = MultipleGraphSessionsTest.session
				.createContext("abstractTest").getRootNode();
		SLNode node1 = abstractTestNode.addNode("teste!");
		SLNode testRootNode = MultipleGraphSessionsTest.session.createContext(
				"test").getRootNode();
		SLNode node2 = testRootNode.addNode("teste!");

		Assert.assertEquals(false, node1.getID().equals(node2.getID()));
		MultipleGraphSessionsTest.session.close();
		MultipleGraphSessionsTest.session = MultipleGraphSessionsTest.graph
				.openSession(MultipleGraphSessionsTest.user);

		abstractTestNode = MultipleGraphSessionsTest.session.createContext(
				"abstractTest").getRootNode();
		node1 = abstractTestNode.addNode("teste!");
		testRootNode = MultipleGraphSessionsTest.session.createContext("test")
				.getRootNode();
		node2 = testRootNode.addNode("teste!");

		Assert.assertEquals(false, node1.getID().equals(node2.getID()));
	}

}
