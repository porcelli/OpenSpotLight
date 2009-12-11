package org.openspotlight.federation.util.test;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.federation.util.GroupSupport.GroupDifferences;

public class GroupSupportTest {

	@Test
	public void shouldFindAddedGroupsOnNewRepositories() throws Exception {
		final GroupDifferences empty = new GroupDifferences();
		final Repository repository = new Repository();
		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(empty, null, repository);
		Assert.assertThat(empty.getAddedGroups().contains("repositoryName/1"),
				Is.is(true));
		Assert.assertThat(
				empty.getAddedGroups().contains("repositoryName/1/2"), Is
						.is(true));

	}

	@Test
	public void shouldFindAddedGroupsOnNewRepositoriesOnDelta()
			throws Exception {
		final GroupDifferences notEmpty = new GroupDifferences();
		notEmpty.setRepositoryName("repositoryName");
		notEmpty.getAddedGroups().add("repositoryName/existentBefore");
		notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

		final Repository repository = new Repository();
		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(notEmpty, null,
				repository);
		Assert.assertThat(notEmpty.getAddedGroups()
				.contains("repositoryName/1"), Is.is(true));
		Assert.assertThat(notEmpty.getAddedGroups().contains(
				"repositoryName/1/2"), Is.is(true));
		Assert.assertThat(notEmpty.getAddedGroups().contains(
				"repositoryName/existentBefore"), Is.is(true));
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/excludedBefore"), Is.is(true));
	}

	@Test
	public void shouldFindAddedGroupsWithinExistentRepositories()
			throws Exception {

		final Repository oldRepository = new Repository();

		oldRepository.setName("repositoryName");
		final Group oldGroup = new Group();
		oldGroup.setRepository(oldRepository);
		oldGroup.setName("1");
		oldRepository.getGroups().add(oldGroup);

		final GroupDifferences empty = new GroupDifferences();

		final Repository repository = new Repository();

		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(empty, oldRepository,
				repository);
		Assert.assertThat(empty.getAddedGroups().contains("repositoryName/1"),
				Is.is(false));
		Assert.assertThat(
				empty.getAddedGroups().contains("repositoryName/1/2"), Is
						.is(true));
	}

	@Test
	public void shouldFindAddedGroupsWithinExistentRepositoriesOnDelta()
			throws Exception {
		final GroupDifferences notEmpty = new GroupDifferences();
		notEmpty.setRepositoryName("repositoryName");

		notEmpty.getAddedGroups().add("repositoryName/existentBefore");
		notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

		final Repository oldRepository = new Repository();

		oldRepository.setName("repositoryName");
		final Group oldGroup = new Group();
		oldGroup.setRepository(oldRepository);
		oldGroup.setName("1");
		oldRepository.getGroups().add(oldGroup);

		final Repository repository = new Repository();

		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(notEmpty, oldRepository,
				repository);
		Assert.assertThat(notEmpty.getAddedGroups()
				.contains("repositoryName/1"), Is.is(false));
		Assert.assertThat(notEmpty.getAddedGroups().contains(
				"repositoryName/1/2"), Is.is(true));
		Assert.assertThat(notEmpty.getAddedGroups().contains(
				"repositoryName/existentBefore"), Is.is(true));
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/excludedBefore"), Is.is(true));
	}

	@Test
	public void shouldFindExcludedGroupsOnOldRepositories() throws Exception {
		final GroupDifferences empty = new GroupDifferences();

		final Repository repository = new Repository();

		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(empty, repository, null);
		Assert.assertThat(
				empty.getRemovedGroups().contains("repositoryName/1"), Is
						.is(true));
		Assert.assertThat(empty.getRemovedGroups().contains(
				"repositoryName/1/2"), Is.is(true));
	}

	@Test
	public void shouldFindExcludedGroupsOnOldRepositoriesOnDelta()
			throws Exception {
		final GroupDifferences notEmpty = new GroupDifferences();
		notEmpty.setRepositoryName("repositoryName");

		notEmpty.getAddedGroups().add("repositoryName/existentBefore");
		notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

		final Repository repository = new Repository();

		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(notEmpty, repository,
				null);
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/1"), Is.is(true));
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/1/2"), Is.is(true));
		Assert.assertThat(notEmpty.getAddedGroups().contains(
				"repositoryName/existentBefore"), Is.is(true));
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/excludedBefore"), Is.is(true));
	}

	@Test
	public void shouldFindExcludedGroupsWithinExistentRepositories()
			throws Exception {

		final Repository oldRepository = new Repository();
		oldRepository.setName("repositoryName");
		final Group oldGroup = new Group();
		oldGroup.setRepository(oldRepository);
		oldGroup.setName("1");
		oldRepository.getGroups().add(oldGroup);

		final GroupDifferences empty = new GroupDifferences();

		final Repository repository = new Repository();

		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		GroupSupport.findDifferencesOnAllRepositories(empty, oldRepository,
				repository);
		Assert.assertThat(empty, Is.is(IsNull.notNullValue()));
		Assert.assertThat(
				empty.getRemovedGroups().contains("repositoryName/1"), Is
						.is(false));
	}

	@Test
	public void shouldFindExcludedGroupsWithinExistentRepositoriesOnDelta()
			throws Exception {
		final GroupDifferences notEmpty = new GroupDifferences();
		notEmpty.setRepositoryName("repositoryName");

		notEmpty.getAddedGroups().add("repositoryName/existentBefore");
		notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

		final Repository oldRepository = new Repository();
		oldRepository.setName("repositoryName");
		final Group oldGroup = new Group();
		oldGroup.setRepository(oldRepository);
		oldGroup.setName("1");
		oldRepository.getGroups().add(oldGroup);

		final Repository repository = new Repository();

		repository.setName("repositoryName");
		final Group newGroup = new Group();
		newGroup.setRepository(repository);
		newGroup.setName("1");
		repository.getGroups().add(newGroup);
		final Group newGroup2 = new Group();
		newGroup2.setGroup(newGroup);
		newGroup2.setName("2");
		newGroup.getGroups().add(newGroup2);
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/1"), Is.is(false));
		Assert.assertThat(notEmpty.getAddedGroups().contains(
				"repositoryName/existentBefore"), Is.is(true));
		Assert.assertThat(notEmpty.getRemovedGroups().contains(
				"repositoryName/excludedBefore"), Is.is(true));
	}
}
