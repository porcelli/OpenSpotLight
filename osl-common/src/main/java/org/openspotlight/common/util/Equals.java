package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

/**
 * Helper class to build equals methods in a secure and concise way
 * 
 * @author feu
 * 
 */
public class Equals {

	/**
	 * Should not be instantiated
	 */
	private Equals() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Equals method to be used like this:
	 * 
	 * <pre>
	 * import static org.openspotlight.common.util.Arrays.of;
	 * import static org.openspotlight.common.util.Arrays.andOf;
	 * import static org.openspotlight.common.util.Equals.equals;
	 * 
	 * //...
	 * 
	 * public void equals(Object o){
	 *     if(o==this)
	 *         return true;
	 *     if(!(o instanceof ThisClass))
	 *         return false;
	 *     that = (ThisClass) o;
	 *     return eachEquality(of(this.attribute1,this.attribute2)
	 *                     ,andOf(that.attribute1,that.attribute2));
	 * }
	 * 
	 * </pre>
	 * 
	 * @param of
	 * @param andOf
	 * @return
	 */
	public static boolean eachEquality(Object[] of,
			Object[] andOf) {
		checkCondition("sameSize",
				(of == null && andOf == null)
						|| (of.length == andOf.length));
		if(of==null && andOf==null)
			return true;
		int size = of.length;
		for (int i = 0; i < size; i++) {
			if (!eachEquality(of[i], andOf[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method that call equals in a null pointer safe way.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean eachEquality(Object o1, Object o2) {
		if (o1 == o2)
			return true;
		if (o1 == null && o2 != null)
			return false;
		if (o2 == null && o1 != null)
			return false;
		return o1.equals(o2);
	}
}
