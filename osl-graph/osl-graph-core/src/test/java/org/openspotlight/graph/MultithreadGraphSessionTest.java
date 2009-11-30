package org.openspotlight.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;

public class MultithreadGraphSessionTest {
	private static enum State {
		NOT_STARTED, DONE, ERROR

	}

	private static class Worker implements Callable<State> {

		public State state = State.NOT_STARTED;

		public State call() throws Exception {
			try {
				for (int i = 0; i < 500; i++) {
					MultithreadGraphSessionTest.newNode.addNode("node " + i);
					MultithreadGraphSessionTest.session.save();
				}
				MultithreadGraphSessionTest.session.close();
				this.state = State.DONE;
			} catch (final Exception e) {
				e.printStackTrace();
				this.state = State.ERROR;
			}
			return this.state;
		}
	}

	/** The graph. */
	private static SLGraph graph;

	/** The session. */
	private static SLGraphSession session;

	private static AuthenticatedUser user;

	private static SLNode rootNode;

	private static SLNode newNode;

	/**
	 * Finish.
	 */
	@AfterClass
	public static void finish() {
		MultithreadGraphSessionTest.graph.shutdown();
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
		MultithreadGraphSessionTest.graph = factory
				.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		MultithreadGraphSessionTest.user = securityFactory
				.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR)
				.authenticate(simpleUser, "password");
	}

	@Test
	public void startExecutorAndSaveAllChangedGraphSessions() throws Exception {
		MultithreadGraphSessionTest.session = MultithreadGraphSessionTest.graph
				.openSession(MultithreadGraphSessionTest.user);
		MultithreadGraphSessionTest.rootNode = MultithreadGraphSessionTest.session
				.createContext("new context").getRootNode();
		MultithreadGraphSessionTest.newNode = MultithreadGraphSessionTest.rootNode
				.addNode("abc");
		MultithreadGraphSessionTest.session.save();
		// session.close();

		final ExecutorService executor = Executors.newFixedThreadPool(4);
		final List<Worker> workers = new ArrayList<Worker>();
		for (int i = 0; i < 8; i++) {
			workers.add(new Worker());
		}
		final List<Future<State>> allStatus = executor.invokeAll(workers);

		for (final Future<State> status : allStatus) {
			System.out.println(status.get());
		}

		for (final Future<State> status : allStatus) {
			Assert.assertThat(status.get(), Is.is(State.DONE));
		}

		executor.shutdown();
	}
}
