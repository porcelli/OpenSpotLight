package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.openspotlight.federation.data.Artifact;
import org.openspotlight.federation.data.ArtifactMapping;
import org.openspotlight.federation.data.Bundle;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.Project;
import org.openspotlight.federation.data.Repository;

public class NodeTest {

	public NodeTest() {
		super();
	}

	@Test
	public void shouldCreateSampleData() {
		Configuration configuration = createSampleData();
		assertTheSameInitialDataOnSomeNodes(configuration, true);
	}

	public void assertTheSameInitialDataOnSomeNodes(
			Configuration configuration, boolean verifyArtifacts) {
		assertThat(configuration, is(notNullValue()));
		assertThat(configuration.getRepositories().size(), is(not(0)));
		assertThat(configuration.getRepositoryNames().size(), is(not(0)));
		Repository repository = configuration.getRepositoryByName("r-1");
		assertThat(repository, is(notNullValue()));
		assertThat(repository.getActive(), is(true));
		assertThat(repository.getConfiguration(), is(configuration));
		assertThat(repository.getNumberOfParallelThreads(), is(1));
		assertThat(repository.getProjects().size(), is(not(0)));
		assertThat(repository.getProjectNames().size(), is(not(0)));

		Project project = repository.getProjectByName("p-1,1");
		assertThat(project.getActive(), is(true));
		assertThat(project.getRepository(), is(repository));
		assertThat(project.getBundleNames().size(), is(not(0)));
		assertThat(project.getBundles().size(), is(not(0)));

		Bundle bundle = project.getBundleByName("b-1,1,1");
		assertThat(bundle, is(notNullValue()));
		assertThat(bundle.getActive(), is(true));
		assertThat(bundle.getInitialLookup(), is("initialLookup"));
		assertThat(bundle.getType(), is("type"));
		assertThat(bundle.getProject(), is(project));
		assertThat(bundle.getArtifactMappings().size(), is(not(0)));
		assertThat(bundle.getArtifactMappingNames().size(), is(not(0)));
		if (verifyArtifacts) {
			assertThat(bundle.getArtifacts().size(), is(not(0)));
			assertThat(bundle.getArtifacts().size(), is(not(0)));
		}
		ArtifactMapping artifactMapping = bundle
				.getArtifactMappingByName("rm-1,1,1,1");
		assertThat(artifactMapping, is(notNullValue()));
		assertThat(artifactMapping.getActive(), is(true));
		assertThat(artifactMapping.getBundle(), is(bundle));
		assertThat(artifactMapping.getExcluded(), is("**/*.excluded"));
		assertThat(artifactMapping.getIncluded(), is("*"));

		if (verifyArtifacts) {
			Artifact artifact = bundle.getArtifactByName("r-1,1,1,1");
			// THIS IS TRANSIENT : Artifact.getData()
			assertThat(artifact.getDataSha1(), is(notNullValue()));
		}
	}

	@Test
	public void shouldFindArtifactByName() throws Exception {
		Configuration configuration = createSampleData();
		Artifact artifact = configuration.findByName("initialLookup",
				"r-1,1,1,1");
		assertThat(artifact, is(notNullValue()));
	}

	@Test
	public void shouldRetturnNullWhenFindingArtifactWithInvalidName()
			throws Exception {
		Configuration configuration = createSampleData();
		Artifact artifact = configuration.findByName("initialLookup",
				"invalidName");
		assertThat(artifact, is(nullValue()));
		artifact = configuration.findByName("invalidName", "invalidName");
		assertThat(artifact, is(nullValue()));
	}

	public Configuration createSampleData() {

		int[] numbers = new int[] { 1, 2, 3, 4, 5 };

		Configuration configuration = new Configuration();
		for (int i : numbers) {
			Repository repository = new Repository("r-" + i, configuration);
			repository.setActive(true);
			repository.setNumberOfParallelThreads(1);
			for (int j : numbers) {
				Project project = new Project("p-" + i + "," + j, repository);
				project.setActive(true);
				for (int k : numbers) {
					Bundle bundle = new Bundle("b-" + i + "," + j + "," + k,
							project);
					bundle.setActive(true);
					bundle.setInitialLookup("initialLookup");
					bundle.setType("type");
					for (int l : numbers) {
						ArtifactMapping ArtifactMapping = new ArtifactMapping(
								"rm-" + i + "," + j + "," + k + "," + l, bundle);
						ArtifactMapping.setActive(true);
						ArtifactMapping.setExcluded("**/*.excluded");
						ArtifactMapping.setIncluded("*");

					}
					for (int m : numbers) {
						Artifact Artifact = new Artifact("r-" + i + "," + j
								+ "," + k + "," + m, bundle);
						Artifact.setData(new ByteArrayInputStream(new byte[0]));
						Artifact.setDataSha1("sha1");
					}
				}
			}
		}
		return configuration;
	}

}