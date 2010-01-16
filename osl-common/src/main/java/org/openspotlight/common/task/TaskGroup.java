package org.openspotlight.common.task;

import org.openspotlight.common.concurrent.Priority;

public interface TaskGroup {

	public String getName();

	public Priority getPriority();

	public TaskBuilder prepareTask();

}
