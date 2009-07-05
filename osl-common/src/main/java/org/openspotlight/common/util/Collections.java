package org.openspotlight.common.util;

import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class to deal with collections
 * 
 * @author feu
 * 
 */
public class Collections {

	/**
	 * Should not be instantiated
	 */
	private Collections() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Convenient method to create a typed set using varargs
	 * 
	 * @param <T>
	 * @param elements
	 * @return
	 */
	public static <T> Set<T> setOf(T... elements) {
		HashSet<T> set = new HashSet<T>();
		for (T e : elements)
			set.add(e);
		return set;
	}
}
