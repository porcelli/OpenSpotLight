package org.openspotlight.common.concurrent;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Map;

/**
 * This is a {@link Map} with a {@link LockContainer} used to synchronize
 * all its methods. Its behavior works like
 * {@link Collections#synchronizedMap(Map)}. The synchronization during
 * iterations to avoid {@link ConcurrentModificationException} needs to be
 * done with {@link LockContainer#getLockObject()} instead of the intrinsic
 * object lock.
 * 
 * example:
 * 
 * <pre>
 * synchronized (items.getLockObject()) {
 * 	for (Entry&lt;K, V&gt; entry : items.entrySet()) {
 * 		// do stuff
 * 	}
 * }
 * </pre>
 * 
 * @author feu
 * 
 * @param <E>
 */
public interface NeedsSyncronizationMap<K, V> extends Map<K, V>,
		LockContainer {

}