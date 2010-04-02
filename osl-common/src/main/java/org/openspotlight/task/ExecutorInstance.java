package org.openspotlight.task;

import org.openspotlight.common.concurrent.GossipExecutor;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public enum ExecutorInstance {

	INSTANCE;

	private ExecutorInstance() {
	}

	public <T> List<Future<T>> invokeAll(Collection<Callable<T>> itemsToExecute) {
		try {
			int threads = 2 * Runtime.getRuntime().availableProcessors();
			GossipExecutor executor = GossipExecutor.newFixedThreadPool(
					threads, "temporary-pool-" + UUID.randomUUID().toString());
			List<Future<T>> future;
			future = executor.invokeAll(itemsToExecute);
			executor.shutdown();
			return future;
		} catch (InterruptedException e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

}
