package org.openspotlight.common.task;

import org.openspotlight.common.task.exception.RunnableWithException;

public interface RunnableListener {

	public void afterRunningTask(RunnableWithException r);

	public void beforeRunningTask(RunnableWithException r);

	public void runnableRunnedWithSuccess(RunnableWithException r);

	public void taskResultedInError(RunnableWithException r, Exception e);

}
