package org.openspotlight.common.concurrent;

import org.openspotlight.common.Disposable;
import org.openspotlight.common.util.Exceptions;

/**
 * This class is used to wrap resources with lazy initialization. This also
 * synchronizes the important methods using the {@link LockContainer} passed on
 * constructor.
 *
 * @author feu
 *
 * @param <R>
 */
public abstract class AtomicLazyResource<R, E extends Exception> implements
		LockContainer, Disposable {

	private final Object lock;

	private R reference = null;

	private final Class<E> exceptionType;

	/**
	 * creates an new instance with a new lock object
	 */
	protected AtomicLazyResource(final Class<E> exceptionType) {
		this.lock = new Object();
		this.exceptionType = exceptionType;
	}

	/**
	 * creates a new instance using the specified {@link LockContainer} internal
	 * lock.
	 *
	 * @param lockContainer
	 */
	protected AtomicLazyResource(final LockContainer lockContainer,
			final Class<E> exceptionType) {
		this.lock = lockContainer.getLockObject();
		this.exceptionType = exceptionType;
	}

	/**
	 * This method will be called before try to close resources. Why try?
	 * Because the wrapped resource "could" implement {@link Disposable}, but
	 * this isn't mandatory.
	 *
	 * @param mayBeNullReference
	 */
	protected void afterTryToCloseResources(final R mayBeNullReference) {

	}

	public final void closeResources() {
		synchronized (this.lock) {
			if (this.reference instanceof Disposable) {
				((Disposable) this.reference).closeResources();
			}
			this.afterTryToCloseResources(this.reference);
		}

	}

	/**
	 * Method used to create a new reference. It will be called once and within
	 * a synchronized block.
	 *
	 * @return
	 */
	protected abstract R createReference() throws Exception;

	public final R get() throws E {
		synchronized (this.lock) {
			if (this.reference == null) {
				try {
					this.reference = this.createReference();
				} catch (final Exception e) {
					throw Exceptions.logAndReturnNew(e, this.exceptionType);
				}
			}
			return this.reference;
		}
	}

	public final Object getLockObject() {

		return this.lock;
	}

}
