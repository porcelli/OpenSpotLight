package org.openspotlight.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to deal with exceptions, and also to log then.
 * 
 * @author feu
 * 
 */
public class Exceptions {

	private static final Logger logger = LoggerFactory
			.getLogger(Exceptions.class);

	/**
	 * Should not be instantiated
	 */
	private Exceptions() {
		throw new IllegalStateException(Messages
				.getString("invalidConstructor")); //$NON-NLS-1$
	}

	/**
	 * Just catch and log the exception.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	catchAndLog(e);
	 * }
	 * </pre>
	 * 
	 * @param exception
	 */
	public static void catchAndLog(Exception exception) {
		logger.error(exception.getMessage(), exception);
	}

	/**
	 * Log the exception and throw the same exception on a void method.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	logAndThrow(e);
	 * }
	 * </pre>
	 * 
	 * @param <E>
	 * @param exception
	 * @throws E
	 */
	public static <E extends Throwable> void logAndThrow(E exception) throws E {
		logger.error(exception.getMessage(), exception);
		throw exception;
	}

	/**
	 * Log the exception and return the same exception to be used in non void
	 * methods.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	throw logAndReturn(e);
	 * }
	 * </pre>
	 * 
	 * @param <E>
	 * @param exception
	 */
	public static <E extends Throwable> E logAndReturn(E exception) {
		logger.error(exception.getMessage(), exception);
		return exception;
	}

	/**
	 * Log the exception and throw a new exception on the described class. To be
	 * used on void methods.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	logAndThrowNew(e, MyCheckedException.class);
	 * }
	 * </pre>
	 * 
	 * @param <E>
	 * @param baseException
	 * @param newExceptionClass
	 *            that has a constructor to wrap the original exception
	 * @throws E
	 *             the new exception
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Exception> void logAndThrowNew(
			Exception baseException, Class<E> newExceptionClass) throws E {
		logger.error(baseException.getMessage(), baseException);
		if (baseException.getClass().equals(newExceptionClass)) {
			throw (E) baseException;
		}
		E newException;
		try {
			newException = newExceptionClass.getDeclaredConstructor(
					Throwable.class).newInstance(baseException);
		} catch (Exception e) {
			logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
			throw new RuntimeException(e);
		}
		throw newException;
	}

	/**
	 * Log the exception and throw a new exception on the described class. To be
	 * used on void methods.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	logAndThrowNew(&quot;it was so dangerous&quot;, e, MyCheckedException.class);
	 * }
	 * </pre>
	 * 
	 * @param <E>
	 * @param baseException
	 * @param newExceptionClass
	 *            that has a constructor to wrap the original exception
	 * @throws E
	 *             the new exception
	 */
	public static <E extends Exception> void logAndThrowNew(String message,
			Exception baseException, Class<E> newExceptionClass) throws E {
		logger.error(message, baseException);
		E newException;
		try {
			newException = newExceptionClass.getDeclaredConstructor(
					String.class, Throwable.class).newInstance(message,
					baseException);
		} catch (Exception e) {
			logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
			throw new RuntimeException(e);
		}
		throw newException;
	}

	/**
	 * Log the exception and return a new exception on the described class. To
	 * be used on non void methods.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	throw logAndReturnNew(e, MyCheckedException.class);
	 * }
	 * </pre>
	 * 
	 * @param <E>
	 * @param baseException
	 * @param newExceptionClass
	 *            that has a constructor to wrap the original exception
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Exception> E logAndReturnNew(
			Exception baseException, Class<E> newExceptionClass) {
		logger.error(baseException.getMessage(), baseException);
		if (baseException.getClass().equals(newExceptionClass)) {
			return (E) baseException;
		}
		E newException;
		try {
			newException = newExceptionClass.getDeclaredConstructor(
					Throwable.class).newInstance(baseException);
		} catch (Exception e) {
			logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
			throw new RuntimeException(e);
		}
		return newException;
	}

	/**
	 * Log the exception and return a new exception on the described class. To
	 * be used on non void methods.
	 * 
	 * <pre>
	 * try {
	 * 	somethingDangerous();
	 * } catch (Exception e) {
	 * 	throw logAndReturnNew(&quot;it was so dangerous&quot;, e, MyCheckedException.class);
	 * }
	 * </pre>
	 * 
	 * @param <E>
	 * @param message
	 * @param baseException
	 * @param newExceptionClass
	 *            that has a constructor to wrap the original exception
	 */
	public static <E extends Exception> E logAndReturnNew(String message,
			Exception baseException, Class<E> newExceptionClass) {
		logger.error(message, baseException);
		E newException;
		try {
			newException = newExceptionClass.getDeclaredConstructor(
					String.class, Throwable.class).newInstance(message,
					baseException);
		} catch (Exception e) {
			logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
			throw new RuntimeException(e);
		}
		return newException;
	}

}
