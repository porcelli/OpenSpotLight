package org.openspotlight.common.taskexec;


public interface TaskExecPool {

	public void addListener(RunnableListener listener);

	public TaskExecGroup createTaskGroup(String taskGroupName, int... priority);

	public String getPoolName();

	public boolean isRunningAnyTask();

	public void removeListener(RunnableListener listener);

	public void startExecutorBlockingUntilFinish() throws InterruptedException;

	public void startExecutorOnBackground();

}
