package org.openspotlight.common.taskexec;

import org.openspotlight.common.concurrent.Priority;

public interface TaskExecGroup {

	public String getName();

	public Priority getPriority();

	public TaskExecBuilder prepareTask();

}
