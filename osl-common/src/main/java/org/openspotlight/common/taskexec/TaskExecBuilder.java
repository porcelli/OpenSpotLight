package org.openspotlight.common.taskexec;

import org.openspotlight.common.task.exception.RunnableWithException;

public interface TaskExecBuilder {

	public TaskExec andPublishTask();

	public TaskExecBuilder withParentTasks(Iterable<TaskExec> parents);

	public TaskExecBuilder withParentTasks(TaskExec... parent);

	public TaskExecBuilder withReadableDescription(String readableDescription);

	public TaskExecBuilder withReadableDescriptionAndUniqueId(
			String readableDescriptionAndUniqueId);

	public TaskExecBuilder withRunnable(RunnableWithException r);

	public TaskExecBuilder withUniqueId(String uniqueId);

}
