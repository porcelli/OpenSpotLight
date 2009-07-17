package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

/**
 * Helper class with comparable convenient methods. To be used like that:
 * 
 * 
 * <pre>
 * import static org.openspotlight.common.util.Arrays.of;
 * import static org.openspotlight.common.util.Arrays.andOf;
 * import static org.openspotlight.common.util.Compare.compareAll;
 * 
 *  //...
 * public int compareTo(That that){ 
 *     compareAll(of(attribute1,attribute2), andOf(that.attribute1,that.attribute2));
 * }
 * 
 * </pre>
 * 
 * @author feu
 * 
 */
public class Compare {

	/**
	 * Should not be instantiated
	 */
	private Compare() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Comparable method to be used inside Comparable classes to compare each
	 * parameter and return the first non zero result.
	 * 
	 * If the objects are instances of Comparable, it will use the compareTo
	 * method. On the other side, it will use the toString().compareTo() method.
	 * This can make a class comparable just calling this method with all the
	 * attributes of the class, also it the parameters itself are not
	 * comparables.
	 * 
	 * <pre>
	 * import static org.openspotlight.common.util.Arrays.of;
	 * import static org.openspotlight.common.util.Arrays.andOf;
	 * import static org.openspotlight.common.util.Compare.compareAll;
	 * 
	 *  //...
	 * public int compareTo(That that){ 
	 *     compareAll(of(attribute1,attribute2), andOf(that.attribute1,that.attribute2));
	 * }
	 * 
	 * </pre>
	 * 
	 * @param <T>
	 * @param of
	 * @param andOf
	 * @return
	 */
	public static <T> int compareAll(T[] of, T andOf[]) {
		checkCondition("sameSize", (of == null && andOf == null)
				|| (of.length == andOf.length));
		if (of == null && andOf == null)
			return 0;
		int size = of.length;
		int sum = 0;
		for (int i = 0; i < size; i++) {
			sum += npeSafeCompare(of[i], andOf[i]);
			if (sum != 0)
				return sum;
		}
		return sum;
	}

	/**
	 * Comparable method witch don't throw null pointer exception.
	 * 
	 * If the objects are instances of Comparable, it will use the compareTo
	 * method. On the other side, it will use the toString().compareTo() method.
	 * This can make a class comparable just calling this method with all the
	 * attributes of the class, also it the parameters itself are not
	 * comparables.
	 * 
	 * @param <T>
	 * @param thisObject
	 * @param thatObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> int npeSafeCompare(T thisObject, T thatObject) {
		if (thisObject == thatObject)
			return 0;
		if (thisObject == null && thatObject != null) {
			return -1;
		}
		if (thisObject != null && thatObject == null) {
			return 1;
		}
		if (thisObject.equals(thatObject))
			return 0;
		if (thisObject instanceof Comparable<?>
				&& thatObject instanceof Comparable<?>)
			return ((Comparable<T>) thisObject).compareTo(thatObject);
		else
			return npeSafeCompare(thisObject.toString(), thatObject.toString());
	}
}
