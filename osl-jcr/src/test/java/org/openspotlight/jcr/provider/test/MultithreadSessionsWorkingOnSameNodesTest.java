package org.openspotlight.jcr.provider.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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
				final Session session = MultithreadSessionsWorkingOnSameNodesTest.provider
						.openSession();

				final Node rootNode = session.getRootNode();
				final Node newNode = rootNode.getNode("abc");
				for (int i = 0; i < 1000; i++) {
					Node node;
					try {
						node = newNode.getNode("node " + i);
					} catch (final PathNotFoundException e) {
						node = newNode.addNode("node " + i);
					}
					node.setProperty("test", "ok");
				}
				session.save();
				session.logout();
				this.state = State.DONE;
			} catch (final Exception e) {
				e.printStackTrace();
				this.state = State.ERROR;
			}
			return this.state;
		}
	}

	private static JcrConnectionProvider provider;

	@BeforeClass
	public static void setup() throws Exception {
		MultithreadSessionsWorkingOnSameNodesTest.provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		MultithreadSessionsWorkingOnSameNodesTest.provider.openRepository();
	}

	@Test
	public void shouldOpenAndSaveSeveralSessions() throws Exception {
		final Session session = MultithreadSessionsWorkingOnSameNodesTest.provider
				.openSession();

		final Node rootNode = session.getRootNode();
		final Node newNode = rootNode.addNode("abc");
		JCRUtil.makeVersionable(newNode);
		newNode.checkout();
		session.save();
		session.logout();
		final ExecutorService executor = Executors.newFixedThreadPool(4);
		final List<Worker> workers = new ArrayList<Worker>();
		for (int i = 0; i < 10; i++) {
			workers.add(new Worker());
		}
		final List<Future<State>> allStatus = executor.invokeAll(workers);

		for (final Future<State> status : allStatus) {
			Assert.assertThat(status.get(), Is.is(State.DONE));
		}

		executor.shutdown();

		final Session session1 = MultithreadSessionsWorkingOnSameNodesTest.provider
				.openSession();

		final Node rootNode1 = session1.getRootNode();
		final Node newNode1 = rootNode1.getNode("abc");
		newNode1.checkin();
		final NodeIterator nodes = newNode1.getNodes("node*");
		int result = 0;
		while (nodes.hasNext()) {
			result++;
		}

		Assert.assertThat(result, Is.is(1000));
	}

}
