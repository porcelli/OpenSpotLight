package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Dates.dateFromString;
import static org.openspotlight.common.util.Dates.stringFromDate;

import java.util.Date;

import org.junit.Test;
import org.openspotlight.common.util.Dates;

/**
 * Test class for {@link Dates}
 * 
 * @author feu
 * 
 */
public class DatesTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionGettingDateWithInvalidString() {
		dateFromString("Invalid string");
	}

	@Test
	public void shouldGetTheDateWhenStringIsValid() {
		assertThat(dateFromString("2008-12-01"), is(notNullValue()));
	}

	@Test
	public void shouldVerifyEquality() {
		assertThat(stringFromDate(new Date()), is(notNullValue()));
	}

}
