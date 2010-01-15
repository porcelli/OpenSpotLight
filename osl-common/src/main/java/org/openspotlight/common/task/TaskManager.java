package org.openspotlight.common.task;

import static org.openspotlight.common.util.Assertions.checkCondition;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public enum TaskManager {

	INSTANCE;

	private static class TaskBuilderImpl implements TaskBuilder {
		private final TaskPoolImpl taskPool;
		private final AtomicBoolean published = new AtomicBoolean(false);
		private final AtomicBoolean started = new AtomicBoolean(false);
		private String description = null;
		private RunnableWithException thisRunnable;
		private String taskId;
		private final CopyOnWriteArrayList<Task> parents = new CopyOnWriteArrayList<Task>();

		public TaskBuilderImpl(final TaskPoolImpl parentPool) {
			taskPool = parentPool;
		}

		public Task andPublishTask() {
			checkCondition("notPublished", published.get() == false);
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();

			// this.taskPool.addTaskToPool(new TaskI)
		}

		public TaskBuilder withParentTasks(final Task... parent) {
			checkCondition("notStarted", started.get() == false);
			for (final Task t : parent) {
				parents.add(t);
			}
			return this;
		}

		public TaskBuilder withReadableDescription(
				final String readableDescription) {
			checkCondition("notPublished", published.get() == false);
			checkCondition("notStarded", started.get() == false);
			checkCondition("withoutDescription", description == null);
			description = readableDescription;
			return this;
		}

		public TaskBuilder withRunnable(final RunnableWithException t) {
			checkCondition("notPublished", published.get() == false);
			checkCondition("notStarded", started.get() == false);
			checkCondition("withoutRunnable", thisRunnable == null);
			thisRunnable = t;
			return this;
		}

		public TaskBuilder withUniqueId(final String uniqueId) {
			checkCondition("notPublished", published.get() == false);
			checkCondition("notStarded", started.get() == false);
			checkCondition("withoutUniqueId", taskId == null);
			taskId = uniqueId;
			return this;
		}

	}

	private static class TaskImpl implements Task {

		private final AtomicBoolean runned = new AtomicBoolean(false);

		public boolean didRun() {
			// TODO Auto-generated method stub
			return false;
		}

		public List<Task> getParentTasks() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getReadableDescription() {
			// TODO Auto-generated method stub
			return null;
		}

		public RunnableWithException getRunnable() {
			// TODO Auto-generated method stub
			return null;
		}

		public Task getTask() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUniqueId() {
			// TODO Auto-generated method stub
			return null;
		}

		public void run() throws Exception {
			// TODO Auto-generated method stub

		}

	}

	private static class TaskPoolImpl implements TaskPool {

		private final String poolName;

		public TaskPoolImpl(final String poolName) {
			this.poolName = poolName;
		}

		public void addTaskToPool(final Task task)
				throws TaskAlreadyOnPoolException {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		public String getPoolName() {
			return poolName;
		}

		public boolean isRunningAnyTask() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		public TaskBuilder prepareTask() {
			return new TaskBuilderImpl(this);
		}

		public void run() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

	}

	public static TaskPool createInstance(final String poolName) {
		return new TaskPoolImpl(poolName);
	}

}
