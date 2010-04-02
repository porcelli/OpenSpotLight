package org.openspotlight.common.taskexec;

import org.openspotlight.common.task.exception.RunnableWithException;

import java.util.List;

public interface TaskExec extends RunnableWithException {
	public void awaitToRun() throws InterruptedException;

	public void awaitToRunChild() throws InterruptedException;

	public boolean didRun();

	public List<TaskExec> getParentTasks();

	public String getReadableDescription();

	public RunnableWithException getRunnable();

	public String getUniqueId();

	public void run() throws Exception;
}
