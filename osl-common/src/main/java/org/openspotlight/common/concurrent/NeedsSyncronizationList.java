package org.openspotlight.common.concurrent;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * This is a {@link List} with a {@link LockContainer} used to synchronize
 * all its methods. Its behavior works like
 * {@link Collections#synchronizedList(List)}. The synchronization during
 * iterations to avoid {@link ConcurrentModificationException} needs to be
 * done with {@link LockContainer#getLockObject()} instead of the intrinsic
 * object lock.
 * 
 * example:
 * 
 * <pre>
 * synchronized (items.getLockObject()) {
 * 	for (A a : items) {
 * 		// do stuff
 * 	}
 * }
 * </pre>
 * 
 * @author feu
 * 
 * @param <E>
 */
public interface NeedsSyncronizationList<E> extends List<E>, LockContainer,
		NeedsSyncronizationCollection<E> {

}