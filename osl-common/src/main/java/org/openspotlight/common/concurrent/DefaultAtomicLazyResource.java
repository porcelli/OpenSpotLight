package org.openspotlight.common.concurrent;

import org.openspotlight.common.exception.SLRuntimeException;

/**
 * This class extends {@link AtomicLazyResource} wrapping all exceptions on
 * {@link SLRuntimeException}
 *
 * @author feu
 *
 * @param <R>
 */
public abstract class DefaultAtomicLazyResource<R> extends
		AtomicLazyResource<R, SLRuntimeException> {

	public DefaultAtomicLazyResource() {
		super(SLRuntimeException.class);
	}

	public DefaultAtomicLazyResource(final LockContainer lockContainer) {
		super(lockContainer, SLRuntimeException.class);
	}

}
