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
					synchronized (MultithreadSessionsWorkingOnSameNodesTest.session) {
						Node node;
						try {
							node = MultithreadSessionsWorkingOnSameNodesTest.newNode
									.getNode("NODE " + i);
						} catch (final PathNotFoundException e) {
							node = MultithreadSessionsWorkingOnSameNodesTest.newNode
									.addNode("NODE " + i);
						}
						node.setProperty("test", "ok");
					}
					MultithreadSessionsWorkingOnSameNodesTest.session.save();
				}
				this.state = State.DONE;
			} catch (final Exception e) {
				e.printStackTrace();
				this.state = State.ERROR;
			}
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
		final ExecutorService executor = Executors
				.newFixedThreadPool(MultithreadSessionsWorkingOnSameNodesTest.THREADS);
		final List<Worker> workers = new CopyOnWriteArrayList<Worker>();
		for (int i = 0; i < MultithreadSessionsWorkingOnSameNodesTest.WORKERS; i++) {
			workers.add(new Worker());
		}
		final List<Future<State>> allStatus = executor.invokeAll(workers);

		for (final Future<State> status : allStatus) {
			Assert.assertThat(status.get(), Is.is(State.DONE));
		}

		executor.shutdown();
		final Node rootNode1 = MultithreadSessionsWorkingOnSameNodesTest.session
				.getRootNode();
		int result = 0;
		synchronized (MultithreadSessionsWorkingOnSameNodesTest.session) {
			final Node newNode1 = rootNode1.getNode("abc");
			newNode1.checkin();

			final NodeIterator nodes = newNode1.getNodes("NODE*");

			while (nodes.hasNext()) {
				nodes.nextNode();
				result++;
			}

		}

		MultithreadSessionsWorkingOnSameNodesTest.session.logout();

		Assert.assertThat(result, Is
				.is(MultithreadSessionsWorkingOnSameNodesTest.ITEMS));
	}

}
