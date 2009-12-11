package org.openspotlight.federation.util.test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.federation.util.GroupSupport.GroupDifferences;
import org.openspotlight.federation.util.GroupSupport.RepositoryGroupDifferences;

public class GroupSupportTest {

	@Test
	public void shouldFindAddedGroupsOnNewRepositories() throws Exception {
		final RepositoryGroupDifferences empty = new RepositoryGroupDifferences();
		final Set<Repository> newRepositories = new HashSet<Repository>();
		final Repository repository = new Repository();
		newRepositories.add(repository);
		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(empty, Collections
				.<Repository> emptySet(), newRepositories);
		Assert.assertThat(empty.getDifferencesByRepository().get(
				"repositoryName"), Is.is(IsNull.notNullValue()));
		Assert.assertThat(
				empty.getDifferencesByRepository().get("repositoryName")
						.getAddedGroups().contains("repositoryName/1"), Is
						.is(true));
		Assert.assertThat(empty.getDifferencesByRepository().get(
				"repositoryName").getAddedGroups().contains(
				"repositoryName/1/2"), Is.is(true));

	}

	@Test
	public void shouldFindAddedGroupsOnNewRepositoriesOnDelta()
			throws Exception {
		final RepositoryGroupDifferences notEmpty = new RepositoryGroupDifferences();
		final GroupDifferences differences = new GroupDifferences();
		differences.setRepositoryName("repositoryName");
		differences.setRepositoryGroupDifferences(notEmpty);
		differences.getAddedGroups().add("repositoryName/existentBefore");
		differences.getRemovedGroups().add("repositoryName/excludedBefore");
		notEmpty.getDifferencesByRepository()
				.put("repositoryName", differences);
		final Set<Repository> newRepositories = new HashSet<Repository>();
		final Repository repository = new Repository();
		newRepositories.add(repository);
		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(notEmpty, Collections
				.<Repository> emptySet(), newRepositories);
		Assert.assertThat(notEmpty.getDifferencesByRepository().get(
				"repositoryName"), Is.is(IsNull.notNullValue()));
		Assert.assertThat(
				notEmpty.getDifferencesByRepository().get("repositoryName")
						.getAddedGroups().contains("repositoryName/1"), Is
						.is(true));
		Assert.assertThat(notEmpty.getDifferencesByRepository().get(
				"repositoryName").getAddedGroups().contains(
				"repositoryName/1/2"), Is.is(true));
		Assert.assertThat(notEmpty.getDifferencesByRepository().get(
				"repositoryName").getAddedGroups().contains(
				"repositoryName/existentBefore"), Is.is(true));
		Assert.assertThat(notEmpty.getDifferencesByRepository().get(
				"repositoryName").getRemovedGroups().contains(
				"repositoryName/excludedBefore"), Is.is(true));
	}

	@Test
	public void shouldFindAddedGroupsWithinExistentRepositories()
			throws Exception {
		Assert.fail();
	}

	@Test
	public void shouldFindAddedGroupsWithinExistentRepositoriesOnDelta()
			throws Exception {
		Assert.fail();
	}

	@Test
	public void shouldFindExcludedGroupsOnNewRepositories() throws Exception {
		Assert.fail();
	}

	@Test
	public void shouldFindExcludedGroupsOnNewRepositoriesOnDelta()
			throws Exception {
		Assert.fail();
	}

	@Test
	public void shouldFindExcludedGroupsWithinExistentRepositories()
			throws Exception {
		Assert.fail();
	}

	@Test
	public void shouldFindExcludedGroupsWithinExistentRepositoriesOnDelta()
			throws Exception {
		Assert.fail();
	}

}
