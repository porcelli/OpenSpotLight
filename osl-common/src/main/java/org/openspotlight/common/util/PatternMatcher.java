package org.openspotlight.common.util;

import static java.util.Collections.unmodifiableSet;
import static org.apache.tools.ant.types.selectors.SelectorUtils.match;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.types.selectors.SelectorUtils;

public class PatternMatcher {

	/**
	 * Should not be instantiated
	 */
	private PatternMatcher() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Filter the names using the Apache Ant expression syntax (and also the
	 * {@link SelectorUtils} class.
	 * 
	 * @param namesToFilter
	 * @param includedPatterns
	 * @param excludedPatterns
	 * @param caseSensitive
	 * @return
	 */
	public static FilterResult filterNamesByPattern(Set<String> namesToFilter,
			Set<String> includedPatterns, Set<String> excludedPatterns,
			boolean caseSensitive) {
		checkNotNull("namesToFilter", namesToFilter);
		checkNotNull("includedPatterns", includedPatterns);
		checkNotNull("excludedPatterns", excludedPatterns);

		Set<String> includedNames = new HashSet<String>();
		Set<String> excludedNames = new HashSet<String>();

		for (String nameToFilter : namesToFilter) {
			for (String included : includedPatterns) {
				if (match(included, nameToFilter, caseSensitive)) {
					includedNames.add(nameToFilter);
				}
			}
			for (String excluded : excludedPatterns) {
				if (match(excluded, nameToFilter, caseSensitive)) {
					excludedNames.add(nameToFilter);
					includedNames.remove(nameToFilter);
				}
			}
		}
		Set<String> ignoredNames = new HashSet<String>(namesToFilter);
		ignoredNames.removeAll(includedNames);
		ignoredNames.removeAll(excludedNames);
		FilterResult result = new FilterResult(namesToFilter, includedNames,
				excludedNames, ignoredNames);
		return result;
	}

	/**
	 * Result class with the results of a filter matching using the Ant
	 * expression syntax.
	 * 
	 * @author feu
	 * 
	 */
	public static final class FilterResult implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6700182758852743670L;

		private FilterResult(Set<String> allNames, Set<String> includedNames,
				Set<String> excludedNames, Set<String> ignoredNames) {
			this.allNames = unmodifiableSet(allNames);
			this.includedNames = unmodifiableSet(includedNames);
			this.excludedNames = unmodifiableSet(excludedNames);
			this.ignoredNames = unmodifiableSet(ignoredNames);
		}

		private final Set<String> allNames;
		private final Set<String> includedNames;
		private final Set<String> excludedNames;
		private final Set<String> ignoredNames;

		public Set<String> getAllNames() {
			return allNames;
		}

		public Set<String> getIncludedNames() {
			return includedNames;
		}

		public Set<String> getExcludedNames() {
			return excludedNames;
		}

		public Set<String> getIgnoredNames() {
			return ignoredNames;
		}

	}

}
