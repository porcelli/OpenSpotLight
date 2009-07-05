package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

/**
 * Helper class with convenient String methods.
 * 
 * @author feu
 * 
 */
public class Strings {

	/**
	 * Should not be instantiated
	 */
	private Strings() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * removes an starting string for a bigger string that starts with it.
	 * 
	 * @param beginning
	 * @param toBeCorrected
	 * @return
	 */
	public static String removeBegginingFrom(String beginning,
			String toBeCorrected) {
		checkNotEmpty("beginning", beginning);
		checkNotEmpty("toBeCorrected", toBeCorrected);
		checkCondition("startsWithBeginning", toBeCorrected
				.startsWith(beginning));
		return toBeCorrected.substring(beginning.length());
	}

	/**
	 * Converts the first character to lower case
	 * 
	 * @param toBeCorrected
	 * @return
	 */
	public static String firstLetterToLowerCase(String toBeCorrected) {
		checkNotNull("toBeCorrected", toBeCorrected);
		if (toBeCorrected.length() == 0)
			return toBeCorrected;
		if (toBeCorrected.length() == 1) {
			return toBeCorrected.toLowerCase();
		}
		String newString = toBeCorrected.substring(0, 1).toLowerCase()
				+ toBeCorrected.substring(1);
		return newString;
	}

	/**
	 * Converts the first character to upper case
	 * 
	 * @param toBeCorrected
	 * @return
	 */
	public static String firstLetterToUpperCase(String toBeCorrected) {
		checkNotNull("toBeCorrected", toBeCorrected);
		if (toBeCorrected.length() == 0)
			return toBeCorrected;
		if (toBeCorrected.length() == 1) {
			return toBeCorrected.toUpperCase();
		}
		String newString = toBeCorrected.substring(0, 1).toUpperCase()
				+ toBeCorrected.substring(1);
		return newString;
	}

}
