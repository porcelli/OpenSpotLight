package org.openspotlight.common.util;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class to deal with dates
 * 
 * @author feu
 * 
 */
public class Dates {

	private static final DateFormat DF = SimpleDateFormat.getDateInstance();
	static {
		((SimpleDateFormat) DF).applyPattern(Messages
				.getString("Dates.defaultFormat")); //$NON-NLS-1$
	}

	/**
	 * Should not be instantiated
	 */
	private Dates() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Creates a date using the default date format
	 * 
	 * @param dateString
	 * @return
	 */
	public static Date dateFromString(String dateString) {
		checkNotEmpty("dateString", dateString); //$NON-NLS-1$
		try {
			return DF.parse(dateString);
		} catch (Exception e) {
			throw logAndReturn(new IllegalArgumentException(format(Messages
					.getString("Dates.invalidDateFormat"), dateString))); //$NON-NLS-1$
		}
	}

	/**
	 * Returns a string using the date passed on argument and the default format
	 * 
	 * @param date
	 * @return
	 */
	public static String stringFromDate(Date date) {
		checkNotNull("date", date); //$NON-NLS-1$
		return DF.format(date);

	}

}
