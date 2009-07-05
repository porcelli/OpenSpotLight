package org.openspotlight.common.util.test;

import java.util.Set;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Collections.setOf;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import org.junit.Test;
import org.openspotlight.common.util.PatternMatcher.FilterResult;

/**
 * Test class for {@link PatternMatcher}
 * 
 * @author feu
 * 
 */
public class PaternMatcherTest {

	@Test
	public void shouldMatchFileName() throws Exception {
		Set<String> allFiles = setOf("/Test.java", "/Test.cs",
				"/included/Test.cs", "/included/Test.java",
				"/excluded/Test.cs", "/Excluded/Test.java",
				"/ignored/Test.class", "/ignored/Test.txt");
		Set<String> included = setOf("**/*.java", "/included/**");
		Set<String> excluded = setOf("**/*.cs", "/excluded/**");
		FilterResult result = filterNamesByPattern(allFiles, included,
				excluded, false);
		assertThat(result.getAllNames().size(), is(8));
		assertThat(result.getIncludedNames().size(), is(2));
		assertThat(result.getExcludedNames().size(), is(4));
		assertThat(result.getIgnoredNames().size(), is(2));
	}

}
