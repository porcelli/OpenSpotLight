package org.openspotlight.common.task;

import java.util.List;

import org.openspotlight.common.task.exception.RunnableWithException;

public interface Task extends RunnableWithException {
	public void awaitToRun() throws InterruptedException;

	public void awaitToRunChild() throws InterruptedException;

	public boolean didRun();

	public List<Task> getParentTasks();

	public String getReadableDescription();

	public RunnableWithException getRunnable();

	public String getUniqueId();

	public void run() throws Exception;
}
