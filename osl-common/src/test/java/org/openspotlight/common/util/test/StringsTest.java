package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Strings.firstLetterToLowerCase;
import static org.openspotlight.common.util.Strings.firstLetterToUpperCase;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import org.junit.Test;
import org.openspotlight.common.util.Strings;

/**
 * Test class for {@link Strings}
 * 
 * @author feu
 * 
 */
public class StringsTest {

	@Test
	public void shouldRemoveStartingString() {
		assertThat(
				removeBegginingFrom("beggining", "beggining will be removed"),
				is(" will be removed"));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowErrorWhenGettingInvalidString() {
		removeBegginingFrom("a", "b");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowErrorWhenGettingBiggerBeginning() {
		removeBegginingFrom("acb", "b");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowErrorWhenGettingNulls() {
		removeBegginingFrom(null, "b");
	}

	@Test
	public void shouldConvertFirstCharToLowerCaseWhenLengthIsOneCharacter() {
		assertThat(firstLetterToLowerCase("I"), is("i"));
	}

	@Test
	public void shouldConvertFirstCharToLowerCaseWhenLengthIsMoreThanOneCharacter() {
		assertThat(firstLetterToLowerCase("BiggerCamelCasedWord"),
				is("biggerCamelCasedWord"));
	}

	@Test
	public void shouldDoNothingWhenLowerLengthIsZero() {
		assertThat(firstLetterToLowerCase(""), is(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenLowerTheFirstLetterIsWithInvalidParameter() {
		firstLetterToLowerCase(null);
	}

	@Test
	public void shouldConvertFirstCharToUpperCaseWhenLengthIsOneCharacter() {
		assertThat(firstLetterToUpperCase("i"), is("I"));
	}

	@Test
	public void shouldConvertFirstCharToUpperCaseWhenLengthIsMoreThanOneCharacter() {
		assertThat(firstLetterToUpperCase("biggerCamelCasedWord"),
				is("BiggerCamelCasedWord"));
	}

	@Test
	public void shouldDoNothingWhenUpperLengthIsZero() {
		assertThat(firstLetterToUpperCase(""), is(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenUpperTheFirstLetterIsWithInvalidParameter() {
		firstLetterToUpperCase(null);
	}

}
