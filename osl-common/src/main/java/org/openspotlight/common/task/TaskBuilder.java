package org.openspotlight.common.task;

import org.openspotlight.common.task.exception.RunnableWithException;

public interface TaskBuilder {

	public Task andPublishTask();

	public TaskBuilder withParentTasks(Task... parent);

	public TaskBuilder withReadableDescription(String readableDescription);

	public TaskBuilder withRunnable(RunnableWithException r);

	public TaskBuilder withUniqueId(String uniqueId);

}
