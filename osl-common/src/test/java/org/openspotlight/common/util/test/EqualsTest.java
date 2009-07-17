package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;

import org.junit.Test;
import org.openspotlight.common.util.Equals;

/**
 * Test class for {@link Equals}
 * 
 * @author feu
 * 
 */
public class EqualsTest {

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionGettingDifferentParameterSize() {
		eachEquality(of(1, 2, 3), andOf(1, 2));
	}

	@Test
	public void shouldWorkWithNulls() {
		assertThat(eachEquality(of(1, 2, null), andOf(1, 2, null)), is(true));
		assertThat(eachEquality(of(1, 2, null), andOf(1, 2, 3)), is(false));
	}

	@Test
	public void shouldVerifyEquality() {
		assertThat(eachEquality(of(1, 2, 3), andOf(1, 2, 3)), is(true));
		assertThat(eachEquality(of(1, 2, 5), andOf(1, 2, 3)), is(false));
	}

	@Test
	public void shouldVerifyEqualityInANullPointerSafeWay() {
		assertThat(eachEquality(null, 1), is(false));
		assertThat(eachEquality(1, null), is(false));
		assertThat(eachEquality(1, 1), is(true));
		assertThat(eachEquality(null, null), is(true));
		assertThat(eachEquality(2, 1), is(false));
	}

}
