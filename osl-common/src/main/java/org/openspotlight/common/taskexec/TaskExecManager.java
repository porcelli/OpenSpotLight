package org.openspotlight.common.taskexec;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import org.openspotlight.common.concurrent.GossipExecutor;
import org.openspotlight.common.concurrent.Priority;
import org.openspotlight.common.task.exception.PoolAlreadyStoppedException;
import org.openspotlight.common.task.exception.RunnableWithException;
import org.openspotlight.common.task.exception.RunningPriorityBigger;
import org.openspotlight.common.task.exception.TaskAlreadyOnPoolException;
import org.openspotlight.common.task.exception.TaskAlreadyRunnedException;
import org.openspotlight.common.task.exception.TaskRunningException;
import org.openspotlight.common.util.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TaskExecManager {

	INSTANCE;

	private static class TaskBuilderImpl implements TaskExecBuilder {
		private final TaskGroupImpl taskGroup;
		private final AtomicBoolean published = new AtomicBoolean(false);
		private final AtomicBoolean started = new AtomicBoolean(false);
		private String description = null;
		private RunnableWithException thisRunnable;
		private String taskId;
		private final CopyOnWriteArrayList<TaskExec> parents = new CopyOnWriteArrayList<TaskExec>();

		public TaskBuilderImpl(final TaskGroupImpl taskGroup) {
			this.taskGroup = taskGroup;
		}

		public TaskExec andPublishTask() {
			checkCondition("notPublished", published.get() == false);
			final TaskImpl task = new TaskImpl(parents, description,
					thisRunnable, taskId);
			taskGroup.addTaskToPool(task);
			published.set(true);
			return task;
		}

		public TaskExecBuilder withParentTasks(final Iterable<TaskExec> parent) {
			checkCondition("notStarted", started.get() == false);
			for (final TaskExec t : parent) {
				parents.add(t);
			}
			return this;
		}

		public TaskExecBuilder withParentTasks(final TaskExec... parent) {
			checkCondition("notStarted", started.get() == false);
			for (final TaskExec t : parent) {
				parents.add(t);
			}
			return this;
		}

		public TaskExecBuilder withReadableDescription(
				final String readableDescription) {
			checkNotEmpty("readableDescription", readableDescription);
			checkCondition("notPublished", published.get() == false);
			checkCondition("notStarded", started.get() == false);
			checkCondition("withoutDescription", description == null);
			description = readableDescription;
			return this;
		}

		public TaskExecBuilder withReadableDescriptionAndUniqueId(
				final String readableDescriptionAndUniqueId) {
			checkNotEmpty("readableDescriptionAndUniqueId",
					readableDescriptionAndUniqueId);
			withUniqueId(readableDescriptionAndUniqueId);
			withReadableDescription(readableDescriptionAndUniqueId);
			return this;
		}

		public TaskExecBuilder withRunnable(final RunnableWithException runnable) {
			checkNotNull("runnable", runnable);
			checkCondition("notPublished", published.get() == false);
			checkCondition("notStarded", started.get() == false);
			checkCondition("withoutRunnable", thisRunnable == null);
			thisRunnable = runnable;
			return this;
		}

		public TaskExecBuilder withUniqueId(final String uniqueId) {
			checkNotEmpty("uniqueId", uniqueId);
			checkCondition("notPublished", published.get() == false);
			checkCondition("notStarded", started.get() == false);
			checkCondition("withoutUniqueId:" + taskId, taskId == null);
			taskId = uniqueId;
			return this;
		}

	}

	private static class TaskGroupImpl implements TaskExecGroup {
		private final Logger logger = LoggerFactory.getLogger(getClass());

		private final Priority thisGroupPriority;

		private final CountDownLatch stopped;

		private final BlockingQueue<TaskImpl> queue;
		private final List<String> alreadyRunnedTaskIds;
		private final List<String> runningTaskIds;
		private final ReentrantLock lock;
		private final AtomicReference<Priority> currentPriorityRunning;

		LinkedBlockingQueue<TaskImpl> tasksForThisPriority;

		private final String name;

		private final String poolName;

		public TaskGroupImpl(final Priority thisGroupPriority,
				final CountDownLatch stopped,
				final BlockingQueue<TaskImpl> queue,
				final List<String> alreadyRunnedTaskIds,
				final List<String> runningTaskIds, final ReentrantLock lock,
				final AtomicReference<Priority> currentPriorityRunning,
				final LinkedBlockingQueue<TaskImpl> tasksForThisPriority,
				final String name, final String poolName) {
			this.thisGroupPriority = thisGroupPriority;
			this.stopped = stopped;
			this.queue = queue;
			this.alreadyRunnedTaskIds = alreadyRunnedTaskIds;
			this.runningTaskIds = runningTaskIds;
			this.lock = lock;
			this.currentPriorityRunning = currentPriorityRunning;
			this.tasksForThisPriority = tasksForThisPriority;
			this.name = name;
			this.poolName = poolName;
		}

		public void addTaskToPool(final TaskExec task)
				throws TaskAlreadyOnPoolException, TaskAlreadyRunnedException,
				TaskRunningException, PoolAlreadyStoppedException,
				RunningPriorityBigger {
			try {
				lock.lock();
				final Priority curPriority = currentPriorityRunning.get();
				if (stopped.getCount() == 0) {
					Exceptions.logAndThrow(new PoolAlreadyStoppedException());
				}
				if (alreadyRunnedTaskIds.contains(task.getUniqueId())) {
					Exceptions.logAndThrow(new TaskAlreadyRunnedException(
							"task already runned: " + task.getUniqueId()));
				}
				if (runningTaskIds.contains(task.getUniqueId())) {
					Exceptions.logAndThrow(new TaskRunningException(
							"task running: " + task.getUniqueId()));
				}
				if (curPriority != null
						&& curPriority.compareTo(thisGroupPriority) > 0) {
					Exceptions.logAndThrow(new RunningPriorityBigger());
				}
				if (curPriority != null
						&& curPriority.compareTo(thisGroupPriority) == 0) {
					if (queue.contains(task)) {
						Exceptions
								.logAndThrow(new TaskAlreadyOnPoolException());
					}
					queue.add((TaskImpl) task);
					if (logger.isDebugEnabled()) {
						logger.debug("added task " + task.getUniqueId() + " "
								+ task.getReadableDescription()
								+ " to the current pool " + poolName
								+ " and group " + name);
					}
				} else {
					if (tasksForThisPriority.contains(task)) {
						Exceptions
								.logAndThrow(new TaskAlreadyOnPoolException());
					}
					tasksForThisPriority.add((TaskImpl) task);
					if (logger.isDebugEnabled()) {
						logger.debug("added task " + task.getUniqueId() + " "
								+ task.getReadableDescription()
								+ " to the future pool " + poolName
								+ " and group " + name);
					}
				}
			} finally {
				lock.unlock();
			}
		}

		public String getName() {
			return name;
		}

		public Priority getPriority() {
			return thisGroupPriority;
		}

		public TaskExecBuilder prepareTask() {
			return new TaskBuilderImpl(this);
		}
	}

	private static class TaskImpl implements TaskExec {

		private final List<TaskExec> parentTasks;

		private final String readableDescription;

		private final RunnableWithException runnable;

		private final String uniqueId;

		private final CountDownLatch latch = new CountDownLatch(1);

		private final Logger logger = LoggerFactory.getLogger(getClass());

		public TaskImpl(final List<TaskExec> parentTasks,
				final String readableDescription,
				final RunnableWithException runnable, final String uniqueId) {
			this.parentTasks = parentTasks;
			this.readableDescription = readableDescription;
			this.runnable = runnable;
			this.uniqueId = uniqueId;
		}

		public void awaitToRun() throws InterruptedException {
			if (logger.isDebugEnabled()) {
				logger.debug("verifying if parents did run for task "
						+ getUniqueId() + " " + getReadableDescription());
			}
			for (final TaskExec parent : parentTasks) {
				if (logger.isDebugEnabled()) {
					logger.debug("verifying if parent " + parent.getUniqueId()
							+ " " + parent.getReadableDescription()
							+ " did run for task " + getUniqueId() + " "
							+ getReadableDescription());
				}
				parent.awaitToRunChild();
				if (logger.isDebugEnabled()) {
					logger.debug("parent " + parent.getUniqueId() + " "
							+ parent.getReadableDescription()
							+ " runned for task " + getUniqueId() + " "
							+ getReadableDescription());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("all parents runned for task " + getUniqueId()
						+ " " + getReadableDescription());
			}
		}

		public void awaitToRunChild() throws InterruptedException {

			latch.await();
		}

		public boolean didRun() {
			return latch.getCount() == 0l;
		}

		public List<TaskExec> getParentTasks() {
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

	private static class TaskPoolImpl implements TaskExecPool {

		private final String poolName;
		private final int poolSize;
		private final CountDownLatch stopped = new CountDownLatch(1);
		private final BlockingQueue<TaskImpl> queue = new LinkedBlockingQueue<TaskImpl>();
		private final List<String> alreadyRunnedTaskIds = new CopyOnWriteArrayList<String>();
		private final List<String> runningTaskIds = new CopyOnWriteArrayList<String>();
		private final ReentrantLock lock = new ReentrantLock(true);
		private final GossipExecutor executor;

		private final AtomicReference<Priority> currentPriorityRunning = new AtomicReference<Priority>();
		private final BlockingQueue<Priority> existentPriorities = new PriorityBlockingQueue<Priority>();
		private final Map<Priority, BlockingQueue<TaskImpl>> taskMap = new ConcurrentHashMap<Priority, BlockingQueue<TaskImpl>>();

		private final CopyOnWriteArrayList<RunnableListener> listeners = new CopyOnWriteArrayList<RunnableListener>();

		public TaskPoolImpl(final String poolName, final int poolSize) {
			this.poolName = poolName;
			this.poolSize = poolSize;
			executor = GossipExecutor.newFixedThreadPool(poolSize, poolName);
		}

		public void addListener(final RunnableListener listener) {
			listeners.add(listener);
		}

		public TaskExecGroup createTaskGroup(final String taskGroupName,
				final int... priorities) throws RunningPriorityBigger {
			try {
				lock.lock();
				final Priority priorityAsObj = Priority
						.createPriority(priorities);
				final Priority currentPriority = currentPriorityRunning.get();
				if (currentPriority != null) {
					if (currentPriority.compareTo(priorityAsObj) < 0) {
						Exceptions.logAndThrow(new RunningPriorityBigger());
					}
				}
				existentPriorities.add(priorityAsObj);
				final LinkedBlockingQueue<TaskImpl> tasksForThisPriority = new LinkedBlockingQueue<TaskImpl>();
				taskMap.put(priorityAsObj, tasksForThisPriority);
				return new TaskGroupImpl(priorityAsObj, stopped, queue,
						alreadyRunnedTaskIds, runningTaskIds, lock,
						currentPriorityRunning, tasksForThisPriority,
						taskGroupName, poolName);
			} finally {
				lock.unlock();
			}
		}

		public String getPoolName() {
			return poolName;
		}

		public boolean isRunningAnyTask() {

			return runningTaskIds.size() > 0;
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
				executor.execute(new Worker(listeners, poolName + "_" + i,
						stopped, queue, alreadyRunnedTaskIds, runningTaskIds,
						lock, currentPriorityRunning, existentPriorities,
						taskMap));
			}
			executor.shutdown();
		}
	}

	private static class Worker implements Runnable {
		private final AtomicReference<Priority> currentPriorityRunning;
		private final BlockingQueue<Priority> existentPriorities;
		private final Map<Priority, BlockingQueue<TaskImpl>> taskMap;

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
				final String workerId, final CountDownLatch stopped,
				final BlockingQueue<TaskImpl> queue,
				final List<String> alreadyRunnedTaskIds,
				final List<String> runningTaskIds, final ReentrantLock lock,
				final AtomicReference<Priority> currentPriorityRunning,
				final BlockingQueue<Priority> existentPriorities,
				final Map<Priority, BlockingQueue<TaskImpl>> taskMap) {
			this.stopped = stopped;
			this.queue = queue;
			this.alreadyRunnedTaskIds = alreadyRunnedTaskIds;
			this.runningTaskIds = runningTaskIds;
			this.lock = lock;
			this.workerId = workerId;
			this.listeners = listeners;
			this.currentPriorityRunning = currentPriorityRunning;
			this.existentPriorities = existentPriorities;
			this.taskMap = taskMap;

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
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId
								+ ": locking to get a task");
					}

					lock.lock();
					if (queue.size() > 0) {
						task = queue.take();
					} else {
						if (runningTaskIds.size() == 0) {
							if (existentPriorities.size() > 0) {

								final Priority priority = existentPriorities
										.poll();
								currentPriorityRunning.set(priority);
								final BlockingQueue<TaskImpl> tasksForThisPriority = taskMap
										.get(priority);
								taskMap.remove(priority);
								queue.addAll(tasksForThisPriority);
								logger
										.info("worker "
												+ workerId
												+ ": needs to get new tasks for another priority: from "
												+ currentPriorityRunning
												+ " to " + priority);

								continue;
							} else {
								currentPriorityRunning.set(null);
								logger
										.info("worker "
												+ workerId
												+ ": no more priorities. Going to shutdown");
								stopped.countDown();
							}
						}
					}
					if (task != null) {
						runningTaskIds.add(task.getUniqueId());
						if (logger.isDebugEnabled()) {
							logger.debug("worker " + workerId
									+ ": getting task " + task.getUniqueId()
									+ " " + task.getReadableDescription());
						}
						if (logger.isDebugEnabled()) {
							logger.debug("worker " + workerId
									+ ": unlocking to get a task");
						}
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
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId
								+ ": waiting to run task " + task.getUniqueId()
								+ " " + task.getReadableDescription());
					}
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
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId
								+ ": about to run task " + task.getUniqueId()
								+ " " + task.getReadableDescription());
					}
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
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId + ": error on task "
								+ task.getUniqueId() + " "
								+ task.getReadableDescription());
					}
					Exceptions.catchAndLog(e);
				}
				try {
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId
								+ ": locking to update task information ");
					}
					lock.lock();
					alreadyRunnedTaskIds.add(task.getUniqueId());
					runningTaskIds.remove(task.getUniqueId());
					try {
						if (!(queue.size() == 0 && runningTaskIds.size() == 0 && taskMap
								.size() == 0)) {
							continue;
						}
					} catch (final Exception e) {
						Exceptions.catchAndLog(e);
					}
				} catch (final Exception e) {
					Exceptions.catchAndLog(e);
				} finally {
					lock.unlock();
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId
								+ ": finished to run task "
								+ task.getUniqueId() + " "
								+ task.getReadableDescription());
					}
					if (logger.isDebugEnabled()) {
						logger.debug("worker " + workerId
								+ ": unlocking to update task information ");
					}

				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("stopping worker " + workerId);
			}
			try {
				for (final RunnableListener l : listeners) {
					l.beforeShutdownWorker(threadLocalMap);
				}

			} catch (final Exception e) {
				Exceptions.catchAndLog(e);
			}
		}
	}

	public TaskExecPool createTaskPool(final String poolName, final int poolSize) {
		checkNotEmpty("poolName", poolName);
		return new TaskPoolImpl(poolName, poolSize);
	}

}
