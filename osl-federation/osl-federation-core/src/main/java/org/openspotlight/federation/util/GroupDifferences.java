/**
 * 
 */
package org.openspotlight.federation.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class GroupDifferences implements SimpleNodeType,
		Serializable {

	private String repositoryName;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6595595697385787637L;

	private final Set<String> addedGroups = new HashSet<String>();

	private final Set<String> removedGroups = new HashSet<String>();

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof GroupDifferences)) {
			return false;
		}
		final GroupDifferences that = (GroupDifferences) obj;
		return Equals.eachEquality(Arrays.of(getRepositoryName()), Arrays
				.andOf(that.getRepositoryName()));
	}

	public Set<String> getAddedGroups() {
		return addedGroups;
	}

	public Set<String> getRemovedGroups() {
		return removedGroups;
	}

	@KeyProperty
	public String getRepositoryName() {
		return repositoryName;
	}

	@Override
	public int hashCode() {
		return HashCodes.hashOf(getClass(), repositoryName);
	}

	public void setRepositoryName(final String repositoryName) {
		this.repositoryName = repositoryName;
	}

}