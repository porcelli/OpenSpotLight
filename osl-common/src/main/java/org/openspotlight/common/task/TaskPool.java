package org.openspotlight.common.task;

public interface TaskPool {

	public String getPoolName();

	public boolean isRunningAnyTask();

	public TaskBuilder prepareTask();

	public void startExecutorBlockingUntilFinish() throws InterruptedException;

	public void startExecutorOnBackground();

}
