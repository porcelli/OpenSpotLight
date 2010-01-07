package org.openspotlight.common.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openspotlight.common.util.Assertions;

/**
 * This map has a support for adding a key only once. It is possible to clear
 * its values, but it isn't possible to add values more than once. Also, the
 * replace methods are illegal.
 * 
 * @author feu
 * 
 * @param <K>
 * @param <V>
 */
public class AddOnlyConcurrentMap<K, V> implements ConcurrentMap<K, V> {

	public static <K, V> AddOnlyConcurrentMap<K, V> newMap() {

		return new AddOnlyConcurrentMap<K, V>(new ConcurrentHashMap<K, V>());
	}

	private final ConcurrentMap<K, V> wrapped;

	public AddOnlyConcurrentMap(final ConcurrentMap<K, V> wrapped) {
		Assertions.checkNotNull("wrapped", wrapped);
		this.wrapped = wrapped;
	}

	public synchronized void clear() {
		wrapped.clear();
	}

	public boolean containsKey(final Object key) {
		return wrapped.containsKey(key);
	}

	public boolean containsValue(final Object value) {
		return wrapped.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return wrapped.entrySet();
	}

	public boolean equals(final Object o) {
		return wrapped.equals(o);
	}

	public V get(final Object key) {
		return wrapped.get(key);
	}

	public int hashCode() {
		return wrapped.hashCode();
	}

	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	public Set<K> keySet() {
		return wrapped.keySet();
	}

	public synchronized V put(final K key, final V value) {
		validateKey(key);
		return wrapped.put(key, value);
	}

	public synchronized void putAll(final Map<? extends K, ? extends V> t) {
		for (final Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
			validateKey(entry.getKey());
			wrapped.put(entry.getKey(), entry.getValue());
		}
	}

	public synchronized V putIfAbsent(final K key, final V value) {
		validateKey(key);
		return wrapped.putIfAbsent(key, value);
	}

	public V remove(final Object key) {
		return wrapped.remove(key);
	}

	public boolean remove(final Object key, final Object value) {
		return wrapped.remove(key, value);
	}

	public V replace(final K key, final V value) {
		throw new UnsupportedOperationException();
	}

	public boolean replace(final K key, final V oldValue, final V newValue) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return wrapped.size();
	}

	private void validateKey(final K key) {
		if (containsKey(key)) {
			throw new IllegalStateException(
					"this key was already associated with another value");
		}
	}

	public Collection<V> values() {
		return wrapped.values();
	}

}
