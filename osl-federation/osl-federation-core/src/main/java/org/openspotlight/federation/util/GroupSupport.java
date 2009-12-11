package org.openspotlight.federation.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Session;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;

public class GroupSupport {

	public static class GroupDifferences implements SimpleNodeType,
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

	private static String ROOT_NODE = SharedConstants.DEFAULT_JCR_ROOT_NAME
			+ "/differences";

	private static void createDifferences(final GroupDifferences differences,
			final Repository newOne, final Set<Group> newGroups,
			final Set<Group> oldGroups) {
		findChangesOnNewGroups(differences, oldGroups, newGroups);
	}

	public static Set<Group> findAllGroups(final Repository repository) {
		if (repository == null) {
			return Collections.<Group> emptySet();
		}
		final Set<Group> groups = new HashSet<Group>();
		final AggregateVisitor<Group> visitor = new AggregateVisitor<Group>(
				groups);
		SimpleNodeTypeVisitorSupport.<Group> acceptVisitorOn(Group.class,
				repository, visitor);
		return groups;
	}

	private static void findChangesOnNewGroups(
			final GroupDifferences differences, final Set<Group> oldGroups,
			final Set<Group> newOnes) {

		for (final Group newOne : newOnes) {
			if (!oldGroups.contains(newOne)) {
				differences.getAddedGroups().add(newOne.getUniqueName());
			}
		}
		for (final Group oldOne : oldGroups) {
			if (!newOnes.contains(oldOne)) {
				differences.getRemovedGroups().add(oldOne.getUniqueName());
			}
		}
	}

	public static void findDifferencesOnAllRepositories(
			final GroupDifferences differences, final Repository oldOne,
			final Repository newOne) {
		final Set<Group> newGroups = findAllGroups(newOne);
		final Set<Group> oldGroups = findAllGroups(oldOne);
		createDifferences(differences, newOne, newGroups, oldGroups);
	}

	public static GroupDifferences getDifferences(final Session session,
			final String repositoryName) {
		final Set<GroupDifferences> result = SimplePersistSupport
				.findNodesByProperties(ROOT_NODE, session,
						GroupDifferences.class, LazyType.EAGER,
						new String[] { "repositoryName" },
						new Object[] { repositoryName });
		if (result.size() > 0) {
			return result.iterator().next();
		}
		return null;

	}

	public static void saveDifferences(final Session session,
			final GroupDifferences differences) {
		try {
			SimplePersistSupport.convertBeanToJcr(ROOT_NODE, session,
					differences);
			session.save();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	private GroupSupport() {
	}

}
