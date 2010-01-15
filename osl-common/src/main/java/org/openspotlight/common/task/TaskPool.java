package org.openspotlight.common.task;

public interface TaskPool extends Runnable {

	public TaskBuilder prepareTask();

	public String getPoolName();

	public boolean isRunningAnyTask();

	public void run();

}
