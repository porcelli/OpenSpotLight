package org.openspotlight.common.concurrent;

public class UnsafeDummyLockContainer implements LockContainer {

	private final Lock lock = new UnsafeDummyLock();

	public Lock getLockObject() {
		return lock;
	}

}
