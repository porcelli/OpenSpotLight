package org.openspotlight.common.taskexec;

import java.util.Map;

import org.openspotlight.common.task.exception.RunnableWithException;

public interface RunnableListener {

	public void afterRunningTask(Map<String, Object> threadLocalMap,
			RunnableWithException r);

	public void beforeRunningTask(Map<String, Object> threadLocalMap,
			RunnableWithException r);

	public void beforeSetupWorker(Map<String, Object> threadLocalMap);

	public void beforeShutdownWorker(Map<String, Object> threadLocalMap);

}
