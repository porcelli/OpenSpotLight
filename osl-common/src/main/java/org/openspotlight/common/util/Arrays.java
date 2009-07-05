package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkEachParameterNotNull;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to deal with arrays
 * 
 * @author feu
 * 
 */
public class Arrays {

	/**
	 * Should not be instantiated
	 */
	private Arrays() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Convenient method to group varargs on a array. This can be used on a
	 * situation that is needed to group more than a group of Ts on a method.
	 * 
	 * <pre>
	 * someMethod(of(someParam1,   someParam2,...,   someParamN),
	 *         andOf(anotherParam1,anotherParam2,...,anotherParamN));
	 * </pre>
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T[] of(T... array) {
		return array;
	}

	/**
	 * Convenient method to group varargs on a array. This can be used on a
	 * situation that is needed to group more than a group of Ts on a method.
	 * 
	 * <pre>
	 * someMethod(of(someParam1,   someParam2,...,   someParamN),
	 *         andOf(anotherParam1,anotherParam2,...,anotherParamN));
	 * </pre>
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T[] andOf(T... array) {
		return array;
	}

	/**
	 * Convenient method to group varargs on a array. This can be used on a
	 * situation that is needed to group more than a group of Ts on a method.
	 * 
	 * <pre>
	 * someMethod(ofNames(someParam1,   someParam2,...,   someParamN),
	 *         andValues(anotherParam1,anotherParam2,...,anotherParamN));
	 * </pre>
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T[] ofKeys(T... array) {
		return array;
	}

	/**
	 * Convenient method to group varargs on a array. This can be used on a
	 * situation that is needed to group more than a group of Ts on a method.
	 * 
	 * <pre>
	 * someMethod(ofNames(someParam1,   someParam2,...,   someParamN),
	 *         andValues(anotherParam1,anotherParam2,...,anotherParamN));
	 * </pre>
	 * 
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T[] andValues(T... array) {
		return array;
	}

	/**
	 * Returns a Map in the following way:
	 * 
	 * <pre>
	 * Map&lt;String, Integer&gt; map = map(ofKeys(&quot;1&quot;, &quot;2&quot;, &quot;3&quot;), andValues(1, 2, 3));
	 * </pre>
	 * 
	 * @param <K>
	 * @param <V>
	 * @param ofKeys
	 * @param andValues
	 * @return
	 */
	public static <K, V> Map<K, V> map(K[] ofKeys, V[] andValues) {
		checkCondition("keysAndValuesWithSameSize",
				(ofKeys == null && andValues == null)
						|| ofKeys.length == andValues.length);
		if (ofKeys == null)
			return new HashMap<K, V>();
		checkEachParameterNotNull("ofKeys", ofKeys);
		Map<K, V> map = new HashMap<K, V>();
		int size = ofKeys.length;
		for (int i = 0; i < size; i++) {
			map.put(ofKeys[i], andValues[i]);
		}
		return map;
	}

	/**
	 * Returns a Map in the following way:
	 * 
	 * <pre>
	 * Map&lt;String, Integer&gt; map = map(ofKeys(&quot;1&quot;, &quot;2&quot;, &quot;3&quot;), andValues(1, 2, 3));
	 * </pre>
	 * 
	 * @param <K>
	 * @param <V>
	 * @param ofKeys
	 * @param andValues
	 * @return
	 */
	public static <K> Map<K, Class<?>> map(K[] ofKeys, Class<?>[] andValues) {
		checkCondition("keysAndValuesWithSameSize",
				(ofKeys == null && andValues == null)
						|| ofKeys.length == andValues.length);
		if (ofKeys == null)
			return new HashMap<K, Class<?>>();
		checkEachParameterNotNull("ofKeys", ofKeys);
		Map<K, Class<?>> map = new HashMap<K, Class<?>>();
		int size = ofKeys.length;
		for (int i = 0; i < size; i++) {
			map.put(ofKeys[i], andValues[i]);
		}
		return map;
	}

}
