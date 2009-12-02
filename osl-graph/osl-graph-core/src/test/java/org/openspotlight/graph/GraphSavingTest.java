package org.openspotlight.graph;

import org.apache.log4j.Logger;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
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
@Ignore
public class GraphSavingTest {

	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLGraphTest.class);

	/** The graph. */
	private static SLGraph graph;

	/** The session. */
	private static SLGraphSession session;

	private static AuthenticatedUser user;

	/**
	 * Finish.
	 */
	@AfterClass
	public static void finish() {
		GraphSavingTest.session.close();
		GraphSavingTest.graph.shutdown();
	}

	/**
	 * Inits the.
	 * 
	 * @throws AbstractFactoryException
	 *             the abstract factory exception
	 */
	@BeforeClass
	public static void init() throws AbstractFactoryException,
			SLInvalidCredentialException, IdentityException {
		final SLGraphFactory factory = AbstractFactory
				.getDefaultInstance(SLGraphFactory.class);
		GraphSavingTest.graph = factory
				.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		GraphSavingTest.user = securityFactory.createIdentityManager(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser,
				"password");
	}

	@Test
	public void shouldDoNotSaveChanges() throws Exception {
		final SLGraphSession session = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		session.createContext("new context not saved").getRootNode().addNode(
				"node 1 not saved").addNode("node 2 not saved");
		session.close();

		final SLGraphSession session1 = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		final SLContext createdCtx = session1
				.getContext("new context not saved");
		Assert.assertThat(createdCtx, Is.is(IsNull.nullValue()));
		session1.close();

	}

	@Test
	public void shouldSaveChanges() throws Exception {

		final SLGraphSession session = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		session.createContext("new context").getRootNode().addNode("node 1")
				.addNode("node 2");
		session.save();
		session.close();

		final SLGraphSession session1 = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		final SLContext createdCtx = session1.getContext("new context");
		Assert.assertThat(createdCtx, Is.is(IsNull.notNullValue()));
		final SLNode createdNode1 = createdCtx.getRootNode().getNode("node 1");
		Assert.assertThat(createdNode1, Is.is(IsNull.notNullValue()));
		final SLNode createdNode2 = createdNode1.getNode("node 2");
		Assert.assertThat(createdNode2, Is.is(IsNull.notNullValue()));
		session1.close();
	}

	@Test
	public void shouldSaveChangesOnOneSessionAndDontSaveOnAnother()
			throws Exception {

		final SLGraphSession sessionToSave = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		sessionToSave.createContext("new saved context").getRootNode().addNode(
				"node 1 saved").addNode("node 2 saved");
		sessionToSave.save();
		sessionToSave.close();
		final SLGraphSession sessionToDismiss = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		sessionToDismiss.createContext("new context not saved").getRootNode()
				.addNode("node 1 not saved").addNode("node 2 not saved");
		sessionToDismiss.close();

		final SLGraphSession session1 = GraphSavingTest.graph
				.openSession(GraphSavingTest.user);
		final SLContext createdCtx = session1.getContext("new saved context");
		Assert.assertThat(createdCtx, Is.is(IsNull.notNullValue()));
		final SLNode createdNode1 = createdCtx.getRootNode().getNode(
				"node 1 saved");
		Assert.assertThat(createdNode1, Is.is(IsNull.notNullValue()));
		final SLNode createdNode2 = createdNode1.getNode("node 2 saved");
		Assert.assertThat(createdNode2, Is.is(IsNull.notNullValue()));
		final SLContext nonCreatedCtx = session1
				.getContext("new context not saved");
		Assert.assertThat(nonCreatedCtx, Is.is(IsNull.nullValue()));

		session1.close();
	}

}