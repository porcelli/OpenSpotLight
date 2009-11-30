package org.openspotlight.jcr.provider.test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.JCRUtil;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class MultithreadSessionsWorkingOnSameNodesTest {

	private static enum State {
		NOT_STARTED, DONE, ERROR

	}

	private static class Worker implements Callable<State> {

		public State state = State.NOT_STARTED;

		public State call() throws Exception {
			try {

				for (int i = 0; i < MultithreadSessionsWorkingOnSameNodesTest.ITEMS; i++) { // tava
					Node node;
					try {
						synchronized (MultithreadSessionsWorkingOnSameNodesTest.session) {
							node = MultithreadSessionsWorkingOnSameNodesTest.newNode
									.getNode("NODE " + i);
						}
					} catch (final PathNotFoundException e) {
						synchronized (MultithreadSessionsWorkingOnSameNodesTest.session) {
							node = MultithreadSessionsWorkingOnSameNodesTest.newNode
									.addNode("NODE " + i);
						}
					}
					synchronized (MultithreadSessionsWorkingOnSameNodesTest.session) {
						node.setProperty("test", "ok");
					}
				}
				MultithreadSessionsWorkingOnSameNodesTest.session.save();
				// }
				this.state = State.DONE;
			} catch (final Exception e) {
				e.printStackTrace();
				this.state = State.ERROR;
			}
			System.err.println("Done");
			return this.state;
		}
	}

	public static int ITEMS = 1000;

	public static int THREADS = 4;

	private static JcrConnectionProvider provider;

	private static Session session;

	private static Node rootNode;
	private static Node newNode;

	private static int WORKERS = 10;

	@BeforeClass
	public static void setup() throws Exception {
		MultithreadSessionsWorkingOnSameNodesTest.provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		MultithreadSessionsWorkingOnSameNodesTest.provider.openRepository();
	}

	@Test
	public void shouldOpenAndSaveSeveralSessions() throws Exception {
		MultithreadSessionsWorkingOnSameNodesTest.session = MultithreadSessionsWorkingOnSameNodesTest.provider
				.openSession();

		MultithreadSessionsWorkingOnSameNodesTest.rootNode = MultithreadSessionsWorkingOnSameNodesTest.session
				.getRootNode();
		MultithreadSessionsWorkingOnSameNodesTest.newNode = MultithreadSessionsWorkingOnSameNodesTest.rootNode
				.addNode("abc");
		JCRUtil
				.makeVersionable(MultithreadSessionsWorkingOnSameNodesTest.newNode);
		MultithreadSessionsWorkingOnSameNodesTest.newNode.checkout();
		MultithreadSessionsWorkingOnSameNodesTest.session.save();
		// MultithreadSessionsWorkingOnSameNodesTest.session.logout();
		final ExecutorService executor = Executors
				.newFixedThreadPool(MultithreadSessionsWorkingOnSameNodesTest.THREADS);
		final List<Worker> workers = new CopyOnWriteArrayList<Worker>();
		for (int i = 0; i < MultithreadSessionsWorkingOnSameNodesTest.WORKERS; i++) {
			workers.add(new Worker());
		}
		final List<Future<State>> allStatus = executor.invokeAll(workers);
		System.err.println("got the executor running");

		for (final Future<State> status : allStatus) {
			System.err.println("got the status");
			Assert.assertThat(status.get(), Is.is(State.DONE));
		}
		System.err.println("all done");

		executor.shutdown();
		System.err.println("shutdown ok");

		final Node rootNode1 = MultithreadSessionsWorkingOnSameNodesTest.session
				.getRootNode();
		System.err.println("got the root node");
		int result = 0;
		synchronized (MultithreadSessionsWorkingOnSameNodesTest.session) {
			final Node newNode1 = rootNode1.getNode("abc");
			newNode1.checkin();
			System.err.println("got new node 1");

			final NodeIterator nodes = newNode1.getNodes("NODE*");
			System.err.println("got all nodes");

			while (nodes.hasNext()) {
				nodes.nextNode();// oops! n‹o tinha isto!
				result++;
				System.err.println(result);

			}
			System.err.println("loop done");

		}

		MultithreadSessionsWorkingOnSameNodesTest.session.logout();
		System.err.println("logout");

		Assert.assertThat(result, Is
				.is(MultithreadSessionsWorkingOnSameNodesTest.ITEMS));
	}

}
