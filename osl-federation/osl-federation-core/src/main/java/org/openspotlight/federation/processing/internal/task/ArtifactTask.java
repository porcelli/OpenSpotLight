package org.openspotlight.federation.processing.internal.task;

import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;

public interface ArtifactTask {

	public void doTask();

	public CurrentProcessorContextImpl getCurrentContext();

	public int getPriority();

	public void setBundleContext(ExecutionContext context);

	public void setQueue(PriorityBlockingQueue<ArtifactTask> queue);

}
