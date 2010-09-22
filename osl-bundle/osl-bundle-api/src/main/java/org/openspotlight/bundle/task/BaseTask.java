package org.openspotlight.bundle.task;

import org.openspotlight.bundle.annotation.Dependency;
import org.openspotlight.bundle.annotation.StrongDependsOn;
import org.openspotlight.bundle.annotation.WeakDependsOn;

public abstract class BaseTask implements Task {

	protected BaseTask() {
		Class<? extends Task> thisType = getClass();
		level = countDependencies(thisType);
	}

	private static int countDependencies(Class<? extends Task> thisType) {
		StrongDependsOn strongDeps = thisType
				.getAnnotation(StrongDependsOn.class);
		WeakDependsOn weakDeps = thisType.getAnnotation(WeakDependsOn.class);
		int weakDepsCount = weakDeps != null ? countDependencies(weakDeps
				.value()) : 0;
		int strongDepsCount = strongDeps != null ? countDependencies(strongDeps
				.value()) : 0;
		return (weakDepsCount >= strongDepsCount ? weakDepsCount
				: strongDepsCount) + 1;
	}

	private static int countDependencies(Dependency[] value) {
		if (value == null)
			return 0;
		int count = 0;
		for (Dependency dep : value) {
			int thisCount = dep == null ? 0 : countDependencies(dep.value());
			if (count < thisCount)
				count = thisCount;
		}
		return count;
	}

	private final int level;

	@Override
	public int compareTo(Task arg0) {
		BaseTask baseTask = (BaseTask) arg0;
		return this.level - baseTask.level;
	}

}
