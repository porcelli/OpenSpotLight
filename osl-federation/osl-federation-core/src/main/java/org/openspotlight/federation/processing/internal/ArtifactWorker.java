package org.openspotlight.federation.processing.internal;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.federation.processing.internal.task.ArtifactTask;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactWorker implements RunnableWithBundleContext {
	private final AtomicBoolean working = new AtomicBoolean(false);
	private final AtomicBoolean stopped = new AtomicBoolean(false);

	private final long timeoutInMilli;

	private final PriorityBlockingQueue<ArtifactTask> queue;

	private ExecutionContext context;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ArtifactWorker(final long timeoutInMilli,
			final PriorityBlockingQueue<ArtifactTask> queue) {
		this.queue = queue;
		this.timeoutInMilli = timeoutInMilli;
	}

	public boolean isWorking() {
		return working.get();
	}

	public void run() {
		ArtifactTask task = null;
		infiniteLoop: while (true) {
			try {
				try {
					task = queue.poll(timeoutInMilli, TimeUnit.MILLISECONDS);
					if (task == null) {
						if (!stopped.get()) {
							continue infiniteLoop;
						} else {
							break infiniteLoop;
						}
					}
					try {
						working.set(true);
						logger.info("starting " + task.getClass() + " "
								+ task.toString());
						task.setQueue(queue);
						task.setBundleContext(context);
						final CurrentProcessorContextImpl currentCtx = task
								.getCurrentContext();
						if (currentCtx != null) {
							final SLContext groupContext = context
									.getGraphSession().createContext(
											SLConsts.DEFAULT_GROUP_CONTEXT);
							currentCtx.setGroupContext(groupContext);
						}
						task.doTask();
					} catch (final Exception e) {
						Exceptions.catchAndLog(e);
					}
				} catch (final InterruptedException e) {
					Exceptions.catchAndLog(e);
				}
			} finally {
				if (task != null) {
					logger.info("stopping " + task.getClass() + " "
							+ task.toString());
				}
				working.set(false);
			}
		}
	}

	public void setBundleContext(final ExecutionContext context) {
		this.context = context;
	}

	public void stop() {
		stopped.set(true);
	}

}
