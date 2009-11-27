package org.openspotlight.jcr.provider.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jcr.Node;
import javax.jcr.Session;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
				final Node newNode = rootNode.addNode("abc");
				for (int i = 0; i < 100; i++) {
					final Node node = newNode.addNode("node " + i);
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

		final ExecutorService executor = Executors.newFixedThreadPool(4);
		final List<Worker> workers = new ArrayList<Worker>();
		for (int i = 0; i < 8; i++) {
			workers.add(new Worker());
		}
		final List<Future<State>> allStatus = executor.invokeAll(workers);

		for (final Future<State> status : allStatus) {
			Assert.assertThat(status.get(), Is.is(State.DONE));
		}

		executor.shutdown();

	}

}
