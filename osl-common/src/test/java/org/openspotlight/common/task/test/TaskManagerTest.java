package org.openspotlight.common.task.test;

import org.junit.Test;
import org.openspotlight.common.task.RunnableWithException;
import org.openspotlight.common.task.Task;
import org.openspotlight.common.task.TaskManager;
import org.openspotlight.common.task.TaskPool;

public class TaskManagerTest {

	@Test
	public void shouldExecuteDisconnectedTasks() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool");
		final Task task1 = pool.prepareTask().withReadableDescription(
				"thisThread").withUniqueId("1").withRunnable(
				new RunnableWithException() {

					public void run() throws Exception {
						// TODO Auto-generated method stub

					}
				}).andPublishTask();

	}

}
