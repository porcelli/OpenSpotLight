package org.openspotlight.common.task;

import static org.openspotlight.common.util.Assertions.checkCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.openspotlight.common.concurrent.GossipExecutor;
import org.openspotlight.common.task.exception.PoolAlreadyStoppedException;
import org.openspotlight.common.task.exception.RunnableWithException;
import org.openspotlight.common.task.exception.TaskAlreadyOnPoolException;
import org.openspotlight.common.task.exception.TaskAlreadyRunnedException;
import org.openspotlight.common.task.exception.TaskRunningException;
import org.openspotlight.common.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			final TaskImpl task = new TaskImpl(parents, description,
					thisRunnable, taskId);
			taskPool.addTaskToPool(task);
			published.set(true);
			return task;
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

		private final List<Task> parentTasks;

		private final String readableDescription;

		private final RunnableWithException runnable;

		private final String uniqueId;

		private final CountDownLatch latch = new CountDownLatch(1);

		private final Logger logger = LoggerFactory.getLogger(getClass());

		public TaskImpl(final List<Task> parentTasks,
				final String readableDescription,
				final RunnableWithException runnable, final String uniqueId) {
			this.parentTasks = parentTasks;
			this.readableDescription = readableDescription;
			this.runnable = runnable;
			this.uniqueId = uniqueId;
		}

		public void awaitToRun() throws InterruptedException {
			logger.info("verifying if parents did run for task "
					+ getUniqueId() + " " + getReadableDescription());
			for (final Task parent : parentTasks) {
				logger.info("verifying if parent " + parent.getUniqueId() + " "
						+ parent.getReadableDescription()
						+ " did run for task " + getUniqueId() + " "
						+ getReadableDescription());
				parent.awaitToRunChild();
				logger.info("parent " + parent.getUniqueId() + " "
						+ parent.getReadableDescription() + " runned for task "
						+ getUniqueId() + " " + getReadableDescription());
			}
			logger.info("all parents runned for task " + getUniqueId() + " "
					+ getReadableDescription());
		}

		public void awaitToRunChild() throws InterruptedException {

			latch.await();
		}

		public boolean didRun() {
			return latch.getCount() == 0l;
		}

		public List<Task> getParentTasks() {
			return parentTasks;
		}

		public String getReadableDescription() {
			return readableDescription;
		}

		public RunnableWithException getRunnable() {
			return runnable;
		}

		public String getUniqueId() {
			return uniqueId;
		}

		public void run() throws Exception {
			if (didRun()) {
				Exceptions.logAndThrow(new IllegalArgumentException(
						"trying to run a task more than once: "
								+ getReadableDescription()));
			}
			try {
				runnable.run();
			} finally {
				latch.countDown();
			}
		}
	}

	private static class TaskPoolImpl implements TaskPool {

		private final Logger logger = LoggerFactory.getLogger(getClass());
		private final String poolName;
		private final int poolSize;
		private final CountDownLatch stopped = new CountDownLatch(1);
		private final BlockingQueue<TaskImpl> queue = new LinkedBlockingQueue<TaskImpl>();
		private final List<String> alreadyRunnedTaskIds = new CopyOnWriteArrayList<String>();
		private final List<String> runningTaskIds = new CopyOnWriteArrayList<String>();
		private final ReentrantLock lock = new ReentrantLock(true);
		private final GossipExecutor executor;

		private final CopyOnWriteArrayList<RunnableListener> listeners = new CopyOnWriteArrayList<RunnableListener>();

		public TaskPoolImpl(final String poolName, final int poolSize) {
			this.poolName = poolName;
			this.poolSize = poolSize;
			executor = GossipExecutor.newFixedThreadPool(poolSize, poolName);
		}

		public void addListener(final RunnableListener listener) {
			listeners.add(listener);
		}

		public void addTaskToPool(final Task task)
				throws TaskAlreadyOnPoolException, TaskAlreadyRunnedException,
				TaskRunningException, PoolAlreadyStoppedException {

			try {
				lock.lock();
				if (stopped.getCount() == 0) {
					Exceptions.logAndThrow(new PoolAlreadyStoppedException());
				}
				if (queue.contains(task)) {
					Exceptions.logAndThrow(new TaskAlreadyOnPoolException());
				}
				if (alreadyRunnedTaskIds.contains(task.getUniqueId())) {
					Exceptions.logAndThrow(new TaskAlreadyRunnedException());
				}
				if (runningTaskIds.contains(task.getUniqueId())) {
					Exceptions.logAndThrow(new TaskRunningException());
				}
				queue.add((TaskImpl) task);
			} finally {
				lock.unlock();
			}
			logger.info("added task " + task.getUniqueId() + " "
					+ task.getReadableDescription() + " to the pool "
					+ poolName);
		}

		public String getPoolName() {
			return poolName;
		}

		public boolean isRunningAnyTask() {

			return runningTaskIds.size() > 0;
		}

		public TaskBuilder prepareTask() {
			return new TaskBuilderImpl(this);
		}

		public void removeListener(final RunnableListener listener) {
			listeners.remove(listener);
		}

		public void startExecutorBlockingUntilFinish()
				throws InterruptedException {
			startExecutorOnBackground();
			stopped.await();
		}

		public void startExecutorOnBackground() {
			for (int i = 0; i < poolSize; i++) {
				executor.execute(new Worker(listeners, executor, poolName + "_"
						+ i, stopped, queue, alreadyRunnedTaskIds,
						runningTaskIds, lock));
			}
		}

	}

	private static class Worker implements Runnable {
		private final GossipExecutor executor;
		private final AtomicReference<TaskImpl> currentTask = new AtomicReference<TaskImpl>();
		private final CountDownLatch stopped;

		private final String workerId;
		private final BlockingQueue<TaskImpl> queue;
		private final List<String> alreadyRunnedTaskIds;
		private final List<String> runningTaskIds;
		private final ReentrantLock lock;
		private final Logger logger = LoggerFactory.getLogger(getClass());
		private final Map<String, Object> threadLocalMap = new HashMap<String, Object>();
		private final CopyOnWriteArrayList<RunnableListener> listeners;

		public Worker(final CopyOnWriteArrayList<RunnableListener> listeners,
				final GossipExecutor executor, final String workerId,
				final CountDownLatch stopped,
				final BlockingQueue<TaskImpl> queue,
				final List<String> alreadyRunnedTaskIds,
				final List<String> runningTaskIds, final ReentrantLock lock) {
			this.stopped = stopped;
			this.queue = queue;
			this.alreadyRunnedTaskIds = alreadyRunnedTaskIds;
			this.runningTaskIds = runningTaskIds;
			this.lock = lock;
			this.workerId = workerId;
			this.executor = executor;
			this.listeners = listeners;
		}

		public void run() {
			try {
				for (final RunnableListener l : listeners) {
					l.beforeSetupWorker(threadLocalMap);
				}

			} catch (final Exception e) {
				Exceptions.catchAndLog(e);
			}
			while (stopped.getCount() != 0) {
				TaskImpl task = null;
				try {
					logger.info("worker " + workerId
							+ ": locking to get a task");

					lock.lock();
					if (queue.size() > 0) {
						task = queue.take();
					}
					if (task != null) {
						runningTaskIds.add(task.getUniqueId());
						logger.info("worker " + workerId + ": getting task "
								+ task.getUniqueId() + " "
								+ task.getReadableDescription());
						logger.info("worker " + workerId
								+ ": unlocking to get a task");
					}

				} catch (final Exception e) {
					Exceptions.catchAndLog(e);
				} finally {
					lock.unlock();
				}
				if (task == null) {
					continue;
				}
				try {
					logger.info("worker " + workerId + ": waiting to run task "
							+ task.getUniqueId() + " "
							+ task.getReadableDescription());
					currentTask.set(task);
					try {
						for (final RunnableListener l : listeners) {
							l.beforeRunningTask(threadLocalMap, task
									.getRunnable());
						}
					} catch (final Exception e) {
						Exceptions.catchAndLog(e);
					}

					task.awaitToRun();
					logger.info("worker " + workerId + ": about to run task "
							+ task.getUniqueId() + " "
							+ task.getReadableDescription());
					task.run();
					try {
						for (final RunnableListener l : listeners) {
							l.afterRunningTask(threadLocalMap, task
									.getRunnable());
						}
					} catch (final Exception e) {
						Exceptions.catchAndLog(e);
					}
					currentTask.set(null);
				} catch (final Exception e) {
					logger.info("worker " + workerId + ": error on task "
							+ task.getUniqueId() + " "
							+ task.getReadableDescription());
					Exceptions.catchAndLog(e);
				}
				try {
					logger.info("worker " + workerId
							+ ": locking to update task information ");
					lock.lock();
					alreadyRunnedTaskIds.add(task.getUniqueId());
					runningTaskIds.remove(task.getUniqueId());
					try {
						if (!(queue.size() == 0 && runningTaskIds.size() == 0)) {
							continue;
						}
					} catch (final Exception e) {
						Exceptions.catchAndLog(e);
					}
				} catch (final Exception e) {
					Exceptions.catchAndLog(e);
				} finally {
					lock.unlock();
					logger.info("worker " + workerId
							+ ": finished to run task " + task.getUniqueId()
							+ " " + task.getReadableDescription());
					logger.info("worker " + workerId
							+ ": unlocking to update task information ");

				}
				stopped.countDown();
				executor.shutdown();
			}
			logger.info("stopping worker " + workerId);
			try {
				for (final RunnableListener l : listeners) {
					l.beforeShutdownWorker(threadLocalMap);
				}

			} catch (final Exception e) {
				Exceptions.catchAndLog(e);
			}
		}

	}

	public TaskPool createTaskPool(final String poolName, final int poolSize) {
		return new TaskPoolImpl(poolName, poolSize);
	}

}
