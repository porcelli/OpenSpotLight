package org.openspotlight.task;

import org.openspotlight.common.concurrent.GossipExecutor;

public enum ExecutorInstance {

	INSTANCE;

	private final GossipExecutor executor;

	private ExecutorInstance() {
		int threads = 2 * Runtime.getRuntime().availableProcessors();
		executor = GossipExecutor.newExecutor(threads,"defaultPool");
	}

	public synchronized GossipExecutor getExecutorInstance() {
		return executor;
	}

}
