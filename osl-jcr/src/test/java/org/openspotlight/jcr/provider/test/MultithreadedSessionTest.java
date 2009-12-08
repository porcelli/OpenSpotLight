package org.openspotlight.jcr.provider.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jcr.Node;
import javax.jcr.Session;

import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class MultithreadedSessionTest {

	enum Status {
		OK, ERROR
	}

	class Worker implements Callable<Status> {

		private final Session session;
		private final int i;

		public Worker(final Session session, final int i) {
			this.session = session;
			this.i = i;
		}

		public Status call() throws Exception {
			try {

				Node parent1 = session.getRootNode().getNode("root");
				for (int j = 0; j < NODES_SIZE; j++) {
					parent1 = parent1.addNode("node_" + i + "_" + j);
				}
				session.save();
				session.logout();
				return Status.OK;
			} catch (final Exception e) {
				e.printStackTrace();
				return Status.ERROR;
			}
		}

	}

	private final int THREAD_SIZE = 100;

	private final int NODES_SIZE = 10;

	private Session openSession() {
		final JcrConnectionProvider provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
		final Session s = provider.openSession();
		return s;
	}

	@Test
	public void shouldInsertNodesInParallel() throws Exception {
		final Session s = openSession();
		s.getRootNode().addNode("root");
		s.save();
		s.logout();
		final List<Callable<Status>> workers = new ArrayList<Callable<Status>>(
				THREAD_SIZE);

		for (int i = 0; i < THREAD_SIZE; i++) {
			final Session session = openSession();
			workers.add(new Worker(session, i));
		}

		final ExecutorService threadPool = Executors.newFixedThreadPool(4);
		final List<Future<Status>> resultList = threadPool.invokeAll(workers);
		for (final Future<Status> result : resultList) {
			Assert.assertTrue(result.get().equals(Status.OK));
		}

	}

}
