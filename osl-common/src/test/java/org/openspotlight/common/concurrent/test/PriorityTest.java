package org.openspotlight.common.concurrent.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.concurrent.Priority.createPriority;

import org.junit.Test;

public class PriorityTest {

	@Test
	public void shouldCompare() throws Exception {
		assertThat(createPriority(2).compareTo(createPriority(1)), is(1));
		assertThat(createPriority(1).compareTo(createPriority(2)), is(-1));
		assertThat(createPriority(1).compareTo(createPriority(1)), is(0));

		assertThat(createPriority(2, 1).compareTo(createPriority(1)), is(1));
		assertThat(createPriority(1).compareTo(createPriority(2, 1)), is(-1));
		assertThat(createPriority(1, 1).compareTo(createPriority(1, 1)), is(0));

		assertThat(createPriority(2, 1).compareTo(createPriority(1, 1)), is(1));
		assertThat(createPriority(1, 1).compareTo(createPriority(2, 1)), is(-1));
		assertThat(createPriority(1, 1).compareTo(createPriority(1, 1)), is(0));

		assertThat(createPriority(2).compareTo(createPriority(1, 1)), is(1));
		assertThat(createPriority(1, 1).compareTo(createPriority(2)), is(-1));
		assertThat(createPriority(1, 1, 1).compareTo(createPriority(1, 1, 1)),
				is(0));

		assertThat(createPriority(1, 2).compareTo(createPriority(1, 1)), is(1));
		assertThat(createPriority(1, 1).compareTo(createPriority(1, 2)), is(-1));
	}

	@Test
	public void shouldDoEquals() throws Exception {
		assertThat(createPriority(2).equals(createPriority(1)), is(false));
		assertThat(createPriority(1).equals(createPriority(2)), is(false));
		assertThat(createPriority(1).equals(createPriority(1)), is(true));

		assertThat(createPriority(2, 1).equals(createPriority(1)), is(false));
		assertThat(createPriority(1).equals(createPriority(2, 1)), is(false));
		assertThat(createPriority(1, 1).equals(createPriority(1, 1)), is(true));

		assertThat(createPriority(2, 1).equals(createPriority(1, 1)), is(false));
		assertThat(createPriority(1, 1).equals(createPriority(2, 1)), is(false));
		assertThat(createPriority(1, 1).equals(createPriority(1, 1)), is(true));

		assertThat(createPriority(2).equals(createPriority(1, 1)), is(false));
		assertThat(createPriority(1, 1).equals(createPriority(2)), is(false));
		assertThat(createPriority(1, 1, 1).equals(createPriority(1, 1, 1)),
				is(true));

		assertThat(createPriority(1, 2).equals(createPriority(1, 1)), is(false));
		assertThat(createPriority(1, 1).equals(createPriority(1, 2)), is(false));
	}

	@Test
	public void shouldDoHashCode() throws Exception {
		assertThat(
				createPriority(2).hashCode() == createPriority(1).hashCode(),
				is(false));
		assertThat(
				createPriority(1).hashCode() == createPriority(2).hashCode(),
				is(false));
		assertThat(
				createPriority(1).hashCode() == createPriority(1).hashCode(),
				is(true));

		assertThat(createPriority(2, 1).hashCode() == createPriority(1)
				.hashCode(), is(false));
		assertThat(createPriority(1).hashCode() == createPriority(2, 1)
				.hashCode(), is(false));
		assertThat(createPriority(1, 1).hashCode() == createPriority(1, 1)
				.hashCode(), is(true));

		assertThat(createPriority(2, 1).hashCode() == createPriority(1, 1)
				.hashCode(), is(false));
		assertThat(createPriority(1, 1).hashCode() == createPriority(2, 1)
				.hashCode(), is(false));
		assertThat(createPriority(1, 1).hashCode() == createPriority(1, 1)
				.hashCode(), is(true));

		assertThat(createPriority(2).hashCode() == createPriority(1, 1)
				.hashCode(), is(false));
		assertThat(createPriority(1, 1).hashCode() == createPriority(2)
				.hashCode(), is(false));
		assertThat(
				createPriority(1, 1, 1).hashCode() == createPriority(1, 1, 1)
						.hashCode(), is(true));

		assertThat(createPriority(1, 2).hashCode() == createPriority(1, 1)
				.hashCode(), is(false));
		assertThat(createPriority(1, 1).hashCode() == createPriority(1, 2)
				.hashCode(), is(false));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCreateWithNegative() {
		createPriority(-1);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCreateWithNegativeOnSubLevel() {
		createPriority(1, -1);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCreateWithZero() {
		createPriority(0);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotCreateWithZeroOnSubLevel() {
		createPriority(1, 0);
	}

}
