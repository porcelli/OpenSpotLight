package org.openspotlight.common.util.test;

import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrow;
import static org.openspotlight.common.util.Exceptions.logAndThrowNew;

import org.junit.Test;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;

/**
 * Test class for {@link Exceptions}
 * 
 * @author feu
 * 
 */
public class ExceptionsTest {

	public void dangerousMethod() throws Exception {
		throw new ConfigurationException("Am I going to be thrown?");
	}

	@Test
	public void shouldCatchException() throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			catchAndLog(e);
		}
	}

	@Test(expected = ConfigurationException.class)
	public void shouldCatchExceptionAndThrowTheSame() throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			logAndThrow(e);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCatchExceptionAndThrowAnotherKind() throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			logAndThrowNew(e, IllegalArgumentException.class);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCatchExceptionAndThrowAnotherKindWithMessage()
			throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			logAndThrowNew("it was so dangerous!", e,
					IllegalArgumentException.class);
		}
	}

	@Test(expected = ConfigurationException.class)
	public void shouldCatchExceptionAndThrowTheSameThatReturned()
			throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			throw logAndReturn(e);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCatchExceptionAndThrowAnotherKindThatReturned()
			throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			throw logAndReturnNew(e, IllegalArgumentException.class);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCatchExceptionAndThrowAnotherKindThatReturnedWithMessage()
			throws Exception {
		try {
			dangerousMethod();
		} catch (Exception e) {
			throw logAndReturnNew("it was so dangerous!", e,
					IllegalArgumentException.class);
		}
	}

}
