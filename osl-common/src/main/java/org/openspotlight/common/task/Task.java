package org.openspotlight.common.task;

import java.util.List;

public interface Task extends RunnableWithException {
	public boolean didRun();

	public List<Task> getParentTasks();

	public String getReadableDescription();

	public RunnableWithException getRunnable();

	public String getUniqueId();

	public void run() throws Exception;
}
