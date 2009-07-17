package org.openspotlight.common.util;

import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.text.MessageFormat;

/**
 * Helper class for parameters validation, such as not null arguments. This
 * class uses {@link Messages} for i18n.
 * 
 * @author feu
 * 
 */
public class Assertions {

	/**
	 * Should not be instantiated
	 */
	private Assertions() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Assert that this parameter is not null.
	 * 
	 * @param name
	 *            of parameter
	 * @param parameter
	 *            itself
	 */
	public static void checkNotNull(String name, Object parameter) {
		if (parameter == null) {
			logAndThrow(new IllegalArgumentException(MessageFormat.format(
					Messages.getString("Assertions.notNullMandatory"), name))); //$NON-NLS-1$
		}
	}

	/**
	 * Assert that this parameter is not null, as also each item of the array is
	 * not null.
	 * 
	 * @param name
	 *            of parameter
	 * @param parameter
	 *            itself
	 */
	public static <T> void checkEachParameterNotNull(String name, T... parameters) {
		if (parameters == null) {
			logAndThrow(new IllegalArgumentException(MessageFormat.format(
					Messages.getString("Assertions.notNullMandatory"), name))); //$NON-NLS-1$
		}
		for (Object parameter : parameters) {
			if (parameter == null) {
				logAndThrow(new IllegalArgumentException(
						MessageFormat
								.format(
										Messages
												.getString("Assertions.notNullMandatory"), name))); //$NON-NLS-1$

			}
		}

	}

	/**
	 * Assert that this parameter is null.
	 * 
	 * @param name
	 *            of parameter
	 * @param parameter
	 *            itself
	 */
	public static void checkNullMandatory(String name, Object parameter) {
		if (parameter != null) {
			logAndThrow(new IllegalArgumentException(MessageFormat.format(
					Messages.getString("Assertions.nullMandatory"), name))); //$NON-NLS-1$
		}
	}

	/**
	 * Assert that this parameter is not empty. It trims the parameter to see if
	 * have any valid data on that.
	 * 
	 * @param name
	 *            of parameter
	 * @param parameter
	 *            itself
	 */
	public static void checkNotEmpty(String name, String parameter) {
		if (parameter == null || parameter.trim().length() == 0) {
			logAndThrow(new IllegalArgumentException(MessageFormat.format(
					Messages.getString("Assertions.notEmptyMandatory"), name))); //$NON-NLS-1$
		}
	}

	/**
	 * Assert that this parameter is marked as valid by the condition passed as
	 * parameter.
	 * 
	 * @param name
	 *            of parameter
	 * @param condition
	 *            itself
	 */
	public static void checkCondition(String name, boolean condition) {
		if (!condition) {
			logAndThrow(new IllegalStateException(MessageFormat.format(Messages
					.getString("Assertions.illegalCondition"), name))); //$NON-NLS-1$
		}
	}

}
