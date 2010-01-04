package org.openspotlight.common.concurrent;

import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * This class has a lot of static factory methods to create {@link Collection
 * collections} with a {@link LockContainer} used to synchronize all its
 * methods. Its behavior works like static methods from {@link Collections}
 * class such as {@link Collections#synchronizedCollection(Collection)}. The
 * synchronization during iterations to avoid
 * {@link ConcurrentModificationException} needs to be done with
 * {@link LockContainer#getLockObject()} instead of the intrinsic object lock.
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
public class LockedCollections {

	private static class SynchronizedCollectionWithLock<E> implements
			NeedsSyncronizationCollection<E> {
		private final Lock lockObject;

		private final Collection<E> items;

		public SynchronizedCollectionWithLock(final Lock lockObject,
				final Collection<E> items) {
			this.lockObject = lockObject;
			this.items = items;
		}

		public boolean add(final E o) {
			return items.add(o);
		}

		public boolean addAll(final Collection<? extends E> c) {
			synchronized (lockObject) {
				return items.addAll(c);
			}
		}

		public void clear() {
			synchronized (lockObject) {
				items.clear();
			}
		}

		public boolean contains(final Object o) {
			synchronized (lockObject) {
				return items.contains(o);
			}
		}

		public boolean containsAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.containsAll(c);
			}
		}

		public boolean equals(final Object o) {
			synchronized (lockObject) {
				return items.equals(o);
			}
		}

		public Lock getLockObject() {
			return lockObject;
		}

		public int hashCode() {
			synchronized (lockObject) {
				return items.hashCode();
			}
		}

		public boolean isEmpty() {
			synchronized (lockObject) {
				return items.isEmpty();
			}
		}

		public Iterator<E> iterator() {
			synchronized (lockObject) {
				return items.iterator();
			}
		}

		public boolean remove(final Object o) {
			synchronized (lockObject) {
				return items.remove(o);
			}
		}

		public boolean removeAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.removeAll(c);
			}
		}

		public boolean retainAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.retainAll(c);
			}
		}

		public int size() {
			synchronized (lockObject) {
				return items.size();
			}
		}

		public Object[] toArray() {
			synchronized (lockObject) {
				return items.toArray();
			}
		}

		public <T> T[] toArray(final T[] a) {
			synchronized (lockObject) {
				return items.toArray(a);
			}
		}
	}

	private static class SynchronizedListWithLock<E> implements
			NeedsSyncronizationList<E> {
		private final Lock lockObject;

		private final List<E> items;

		public SynchronizedListWithLock(final Lock lockObject,
				final List<E> items) {
			super();
			this.lockObject = lockObject;
			this.items = items;
		}

		public boolean add(final E o) {
			synchronized (lockObject) {
				return items.add(o);
			}
		}

		public void add(final int index, final E element) {
			synchronized (lockObject) {
				items.add(index, element);
			}
		}

		public boolean addAll(final Collection<? extends E> c) {
			synchronized (lockObject) {
				return items.addAll(c);
			}
		}

		public boolean addAll(final int index, final Collection<? extends E> c) {
			synchronized (lockObject) {
				return items.addAll(index, c);
			}
		}

		public void clear() {
			synchronized (lockObject) {
				items.clear();
			}
		}

		public boolean contains(final Object o) {
			synchronized (lockObject) {
				return items.contains(o);
			}
		}

		public boolean containsAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.containsAll(c);
			}
		}

		public boolean equals(final Object o) {
			synchronized (lockObject) {
				return items.equals(o);
			}
		}

		public E get(final int index) {
			synchronized (lockObject) {
				return items.get(index);
			}
		}

		public Lock getLockObject() {
			return lockObject;
		}

		public int hashCode() {
			synchronized (lockObject) {
				return items.hashCode();
			}
		}

		public int indexOf(final Object o) {
			synchronized (lockObject) {
				return items.indexOf(o);
			}
		}

		public boolean isEmpty() {
			synchronized (lockObject) {
				return items.isEmpty();
			}
		}

		public Iterator<E> iterator() {
			synchronized (lockObject) {
				return items.iterator();
			}
		}

		public int lastIndexOf(final Object o) {
			synchronized (lockObject) {
				return items.lastIndexOf(o);
			}
		}

		public ListIterator<E> listIterator() {
			synchronized (lockObject) {
				return items.listIterator();
			}
		}

		public ListIterator<E> listIterator(final int index) {
			synchronized (lockObject) {
				return items.listIterator(index);
			}
		}

		public E remove(final int index) {
			synchronized (lockObject) {
				return items.remove(index);
			}
		}

		public boolean remove(final Object o) {
			synchronized (lockObject) {
				return items.remove(o);
			}
		}

		public boolean removeAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.removeAll(c);
			}
		}

		public boolean retainAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.retainAll(c);
			}
		}

		public E set(final int index, final E element) {
			synchronized (lockObject) {
				return items.set(index, element);
			}
		}

		public int size() {
			synchronized (lockObject) {
				return items.size();
			}
		}

		public List<E> subList(final int fromIndex, final int toIndex) {
			synchronized (lockObject) {
				return items.subList(fromIndex, toIndex);
			}
		}

		public Object[] toArray() {
			synchronized (lockObject) {
				return items.toArray();
			}
		}

		public <T> T[] toArray(final T[] a) {
			synchronized (lockObject) {
				return items.toArray(a);
			}
		}
	}

	private static class SynchronizedMapWithLock<K, V> implements
			NeedsSyncronizationMap<K, V> {
		private final Lock lockObject;

		private final Map<K, V> items;

		public SynchronizedMapWithLock(final Lock lockObject,
				final Map<K, V> items) {
			super();
			this.lockObject = lockObject;
			this.items = items;
		}

		public void clear() {
			items.clear();
		}

		public boolean containsKey(final Object key) {
			synchronized (lockObject) {
				return items.containsKey(key);
			}
		}

		public boolean containsValue(final Object value) {
			synchronized (lockObject) {
				return items.containsValue(value);
			}
		}

		public Set<Entry<K, V>> entrySet() {
			synchronized (lockObject) {
				return items.entrySet();
			}
		}

		public boolean equals(final Object o) {
			synchronized (lockObject) {
				return items.equals(o);
			}
		}

		public V get(final Object key) {
			synchronized (lockObject) {
				return items.get(key);
			}
		}

		public Lock getLockObject() {
			return lockObject;
		}

		public int hashCode() {
			synchronized (lockObject) {
				return items.hashCode();
			}
		}

		public boolean isEmpty() {
			synchronized (lockObject) {
				return items.isEmpty();
			}
		}

		public Set<K> keySet() {
			synchronized (lockObject) {
				return items.keySet();
			}
		}

		public V put(final K key, final V value) {
			synchronized (lockObject) {
				return items.put(key, value);
			}
		}

		public void putAll(final Map<? extends K, ? extends V> t) {
			synchronized (lockObject) {
				items.putAll(t);
			}
		}

		public V remove(final Object key) {
			synchronized (lockObject) {
				return items.remove(key);
			}
		}

		public int size() {
			synchronized (lockObject) {
				return items.size();
			}
		}

		public Collection<V> values() {
			synchronized (lockObject) {
				return items.values();
			}
		}

	}

	private static class SynchronizedSetWithLock<E> implements
			NeedsSyncronizationSet<E> {

		private final Lock lockObject;

		private final Set<E> items;

		public SynchronizedSetWithLock(final Lock lockObject, final Set<E> items) {
			this.lockObject = lockObject;
			this.items = items;
		}

		public boolean add(final E o) {
			return items.add(o);
		}

		public boolean addAll(final Collection<? extends E> c) {
			synchronized (lockObject) {
				return items.addAll(c);
			}
		}

		public void clear() {
			synchronized (lockObject) {
				items.clear();
			}
		}

		public boolean contains(final Object o) {
			synchronized (lockObject) {
				return items.contains(o);
			}
		}

		public boolean containsAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.containsAll(c);
			}
		}

		public boolean equals(final Object o) {
			synchronized (lockObject) {
				return items.equals(o);
			}
		}

		public Lock getLockObject() {
			return lockObject;
		}

		public int hashCode() {
			synchronized (lockObject) {
				return items.hashCode();
			}
		}

		public boolean isEmpty() {
			synchronized (lockObject) {
				return items.isEmpty();
			}
		}

		public Iterator<E> iterator() {
			synchronized (lockObject) {
				return items.iterator();
			}
		}

		public boolean remove(final Object o) {
			synchronized (lockObject) {
				return items.remove(o);
			}
		}

		public boolean removeAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.removeAll(c);
			}
		}

		public boolean retainAll(final Collection<?> c) {
			synchronized (lockObject) {
				return items.retainAll(c);
			}
		}

		public int size() {
			synchronized (lockObject) {
				return items.size();
			}
		}

		public Object[] toArray() {
			synchronized (lockObject) {
				return items.toArray();
			}
		}

		public <T> T[] toArray(final T[] a) {
			synchronized (lockObject) {
				return items.toArray(a);
			}
		}

	}

	public static <E> NeedsSyncronizationCollection<E> createCollectionWithLock(
			final LockContainer parent, final Collection<E> originData) {
		synchronized (parent.getLockObject()) {
			return new SynchronizedCollectionWithLock<E>(
					parent.getLockObject(), originData);
		}
	}

	public static <E> NeedsSyncronizationList<E> createListWithLock(
			final LockContainer parent, final List<E> originData) {
		synchronized (parent.getLockObject()) {
			return new SynchronizedListWithLock<E>(parent.getLockObject(),
					originData);
		}
	}

	public static <K, V> NeedsSyncronizationMap<K, V> createMapWithLock(
			final LockContainer parent, final Map<K, V> originData) {
		synchronized (parent.getLockObject()) {
			return new SynchronizedMapWithLock<K, V>(parent.getLockObject(),
					originData);
		}
	}

	public static <E> NeedsSyncronizationSet<E> createSetWithLock(
			final LockContainer parent, final Set<E> originData) {
		synchronized (parent.getLockObject()) {
			return new SynchronizedSetWithLock<E>(parent.getLockObject(),
					originData);
		}
	}

}
