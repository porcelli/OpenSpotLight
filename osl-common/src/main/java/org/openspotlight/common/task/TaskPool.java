package org.openspotlight.common.task;


public interface TaskPool {

	public void addListener(RunnableListener listener);

	public TaskGroup createTaskGroup(String taskGroupName, int... priority);

	public String getPoolName();

	public boolean isRunningAnyTask();

	public void removeListener(RunnableListener listener);

	public void startExecutorBlockingUntilFinish() throws InterruptedException;

	public void startExecutorOnBackground();

}
