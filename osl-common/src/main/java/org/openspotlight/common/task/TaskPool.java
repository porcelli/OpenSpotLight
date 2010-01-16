package org.openspotlight.common.task;

public interface TaskPool {

	public void addListener(RunnableListener listener);

	public String getPoolName();

	public boolean isRunningAnyTask();

	public TaskBuilder prepareTask();

	public void removeListener(RunnableListener listener);

	public void startExecutorBlockingUntilFinish() throws InterruptedException;

	public void startExecutorOnBackground();

}
