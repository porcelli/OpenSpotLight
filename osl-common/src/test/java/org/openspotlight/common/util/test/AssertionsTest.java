package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkEachParameterNotNull;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Assertions.checkNullMandatory;

import org.junit.Test;
import org.openspotlight.common.util.Assertions;

/**
 * Test class for {@link Assertions}
 * 
 * @author feu
 * 
 */
public class AssertionsTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGettingNullParameter() {
		checkNotNull("notNullable", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGettinAllNullParameter() {
		checkEachParameterNotNull("notNullable", (Object)null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGettinOneNullParameter() {
		checkEachParameterNotNull("notNullable", "nonNull",null);
	}

	@Test
	public void shouldDoNotThrowExceptionWhenGettinOnlyNonNullParameters() {
		checkEachParameterNotNull("notNullable", "nonNull");
		checkEachParameterNotNull("notNullable", "nonNull","anotherNonNull");
	}

	@Test
	public void shouldDoNotThrowExceptionWhenGettingNotNullParameter() {
		checkNotNull("notNullable", "notNullValue");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGettingNonNullParameter() {
		checkNullMandatory("nullable", "non null");
	}

	@Test
	public void shouldDoNotThrowExceptionWhenGettingNullParameter() {
		checkNullMandatory("nullable", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGettingEmptyParameter() {
		checkNotEmpty("notEmpty", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenGettingSpacedParameter() {
		checkNotEmpty("notEmpty", "    ");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionOnCheckEmptyWhenGettingNullParameter() {
		checkNotEmpty("notEmpty", null);
	}

	@Test
	public void shouldDoNotThrowExceptionWhenGettingNotEmptyParameter() {
		checkNotEmpty("notEmpty", "notEmpty");
	}

	@Test
	public void shouldDoNotThrowExceptionWhenGettingValidConditionParameter() {
		checkCondition("valid", true);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionWhenGettingInvalidConditionParameter() {
		checkCondition("valid", false);
	}

	@Test
	public void shouldGetCorrectErrorNessage() {
		try {
			checkNotNull("notNullable", null);
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(),
					is("Parameter named notNullable should be not null!"));
		}
	}

}
