package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.HashCodes.hashOf;

import org.junit.Test;
import org.openspotlight.common.util.HashCodes;

/**
 * Test class for {@link HashCodes}
 * 
 * @author feu
 * 
 */
public class HashsTest {

	@Test
	public void shouldCreateValidHashs() {
		assertThat(hashOf(true), is(not(0)));
		assertThat(hashOf('c'), is(not(0)));
		assertThat(hashOf(1), is(not(0)));
		assertThat(hashOf(0), is(0));
		assertThat(hashOf((byte) 1), is(not(0)));
		assertThat(hashOf((short) 1), is(not(0)));
		assertThat(hashOf(1l), is(not(0)));
		assertThat(hashOf(1.0f), is(not(0)));
		assertThat(hashOf(1.0d), is(not(0)));
		assertThat(hashOf(new Object()), is(not(0)));
		assertThat(hashOf(1, 2, 3, 4l, "", true), is(not(0)));
	}

}
