package org.openspotlight.common.task.test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.common.task.Task;
import org.openspotlight.common.task.TaskManager;
import org.openspotlight.common.task.TaskPool;
import org.openspotlight.common.task.exception.RunnableWithException;

public class TaskManagerTest {

	private class Worker implements RunnableWithException {
		private final String description;

		private final List<String> list;

		public Worker(final String description, final List<String> list) {
			this.description = description;
			this.list = list;
		}

		public void run() throws Exception {
			list.add(description);
		}

	}

	public static void main(final String... args) throws Exception {
		new TaskManagerTest().shouldExecuteTasksOnPoolWithEigth();
	}

	private void createTasks(final TaskPool pool, final List<String> list) {
		final Task task1 = pool.prepareTask().withReadableDescription(
				"thisThread").withUniqueId("1").withRunnable(
				new Worker("1", list)).andPublishTask();

		final Task task1_1 = pool.prepareTask().withReadableDescription(
				"thisThread-1_1").withUniqueId("1_1").withRunnable(
				new Worker("1_1", list)).withParentTasks(task1)
				.andPublishTask();

		final Task task1_2 = pool.prepareTask().withReadableDescription(
				"thisThread-1_2").withUniqueId("1_2").withRunnable(
				new Worker("1_2", list)).withParentTasks(task1, task1_1)
				.andPublishTask();

		final Task task1_3 = pool.prepareTask().withReadableDescription(
				"thisThread-1_3").withUniqueId("1_3").withRunnable(
				new Worker("1_3", list)).withParentTasks(task1, task1_2)
				.andPublishTask();

		final Task task1_4 = pool.prepareTask().withReadableDescription(
				"thisThread-1_4").withUniqueId("1_4").withRunnable(
				new Worker("1_4", list)).withParentTasks(task1, task1_3)
				.andPublishTask();

		pool.prepareTask().withReadableDescription("thisThread-taskFactory")
				.withUniqueId("thisThread-taskFactory").withRunnable(
						new RunnableWithException() {

							public void run() throws Exception {
								final Task task2_1 = pool.prepareTask()
										.withReadableDescription(
												"thisThread-2_1").withUniqueId(
												"2_1").withRunnable(
												new Worker("2_1", list))
										.withParentTasks(task1_1, task1_2,
												task1_3, task1_4)
										.andPublishTask();

								final Task task2_2 = pool.prepareTask()
										.withReadableDescription(
												"thisThread-2_2").withUniqueId(
												"2_2").withRunnable(
												new Worker("2_2", list))
										.withParentTasks(task1_1, task1_2,
												task1_3, task1_4, task2_1)
										.andPublishTask();

								final Task task2_3 = pool.prepareTask()
										.withReadableDescription(
												"thisThread-2_3").withUniqueId(
												"2_3").withRunnable(
												new Worker("2_3", list))
										.withParentTasks(task1_1, task1_2,
												task1_3, task1_4, task2_2)
										.andPublishTask();

								final Task task2_4 = pool.prepareTask()
										.withReadableDescription(
												"thisThread-2_4").withUniqueId(
												"2_4").withRunnable(
												new Worker("2_4", list))
										.withParentTasks(task1_1, task1_2,
												task1_3, task1_4, task2_3)
										.andPublishTask();

								final Task task3 = pool
										.prepareTask()
										.withReadableDescription("thisThread-3")
										.withUniqueId("3").withRunnable(
												new Worker("3", list))
										.withParentTasks(task2_1, task2_2,
												task2_3, task2_4)
										.andPublishTask();

								pool.prepareTask().withReadableDescription(
										"thisThread-4").withUniqueId("4")
										.withRunnable(new Worker("4", list))
										.withParentTasks(task3)
										.andPublishTask();

							}
						}).withParentTasks(task1, task1_4).andPublishTask();

	}

	@Test
	public void shouldExecuteTasksOnPoolWithEigth() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 8);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithFive() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 5);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithFour() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 4);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithSeven() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 7);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithSix() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 6);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithSixteen() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 16);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithThree() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 3);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnPoolWithTwoTasks() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 2);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

	@Test
	public void shouldExecuteTasksOnSinglePool() throws Exception {

		final TaskPool pool = TaskManager.createInstance("test-pool", 1);
		final List<String> list = new CopyOnWriteArrayList<String>();
		createTasks(pool, list);
		pool.startExecutorBlockingUntilFinish();
		Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3",
				"1_4", "2_1", "2_2", "2_3", "2_4", "3", "4")));
	}

}
