package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Compare.compareAll;
import static org.openspotlight.common.util.Compare.npeSafeCompare;

import org.junit.Test;
import org.openspotlight.common.util.Compare;

/**
 * Test class for {@link Compare}
 * 
 * @author feu
 * 
 */
public class CompareTest {

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionGettingDifferentParameterSize() {
		compareAll(of(1, 2, 3), andOf(1, 2));
	}

	@Test
	public void shouldWorkWithNulls() {
		assertThat(compareAll(of(1, 2, null), andOf(1, 2, null)), is(0));
		assertThat(compareAll(of(1, 2, null), andOf(1, 2, 3)), is(not(0)));
	}

	@Test
	public void shouldVerifyEquality() {
		assertThat(compareAll(of(1, 2, 3), andOf(1, 2, 3)), is(0));
		assertThat(compareAll(of(1, 2, 5), andOf(1, 2, 3)), is(not(0)));
	}

	@Test
	public void shouldVerifyEqualityInANullPointerSafeWay() {
		assertThat(npeSafeCompare(null, 1), is(not(0)));
		assertThat(npeSafeCompare(1, null), is(not(0)));
		assertThat(npeSafeCompare(1, 1), is(0));
		assertThat(npeSafeCompare(null, null), is(0));
		assertThat(npeSafeCompare(2, 1), is(not(0)));
	}
	
	@Test
	public void shouldWorkWithNonComparables(){
		Object o1 = new Object();
		Object o2 = new Object();
		assertThat(o1.equals(o2), is(false));
		assertThat(npeSafeCompare(o1, o2), is(not(0)));
		assertThat(npeSafeCompare(o1, o1), is(0));
	}

}
