package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;

public class FileSystemOriginArtifactLoaderTest {

	private FileSystemOriginArtifactLoader loader = new FileSystemOriginArtifactLoader();
	private ArtifactSource artifactSource;
	private ArtifactSource stringArtifactSource;

	@Before
	public void prepareArtifactSource() throws Exception {
		final Repository repository = new Repository();
		repository.setName("repositoryName");
		artifactSource = new ArtifactSource();
		artifactSource.setRepository(repository);
		artifactSource.setName("classpath");
		artifactSource.setBinary(true);
		artifactSource
				.setInitialLookup("./src/test/resources/artifacts/not_changed");

		stringArtifactSource = new ArtifactSource();
		stringArtifactSource.setRepository(repository);
		stringArtifactSource.setName("classpath");
		stringArtifactSource
				.setInitialLookup("./src/test/resources/artifacts/not_changed");

	}

	/**
	 * Should find by relative path.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldFindByRelativePath() throws Exception {
		final StreamArtifact streamArtifact1 = loader.findByPath(
				StreamArtifact.class, artifactSource,
				"/folder/subfolder/file_not_changed1");
		final StreamArtifact streamArtifact2 = loader.findByRelativePath(
				StreamArtifact.class, artifactSource, streamArtifact1,
				"../file_not_changed1");
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact2.getArtifactCompleteName(),
				is("/folder/file_not_changed1"));
	}

	/**
	 * Should load not changed artifact.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldLoadNotChangedArtifact() throws Exception {
		final ArtifactWithSyntaxInformation streamArtifact1 = loader
				.findByPath(StreamArtifact.class, artifactSource,
						"folder/file_not_changed1");
		final ArtifactWithSyntaxInformation streamArtifact2 = loader
				.findByPath(StreamArtifact.class, artifactSource,
						"folder/subfolder/file_not_changed1");
		final ArtifactWithSyntaxInformation streamArtifact3 = loader
				.findByPath(StreamArtifact.class, artifactSource,
						"folder/subfolder/anothersubfolder/file_not_changed1");
		assertThat(streamArtifact1, is(notNullValue()));
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact3, is(notNullValue()));
	}

	/**
	 * Should find by relative path.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldFindStringByRelativePath() throws Exception {
		final StringArtifact streamArtifact1 = loader.findByPath(
				StringArtifact.class, stringArtifactSource,
				"/folder/subfolder/file_not_changed1");
		final StringArtifact streamArtifact2 = loader.findByRelativePath(
				StringArtifact.class, stringArtifactSource, streamArtifact1,
				"../file_not_changed1");
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact2.getArtifactCompleteName(),
				is("/folder/file_not_changed1"));

	}

	/**
	 * Should load not changed artifact.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldLoadNotChangedStringArtifact() throws Exception {
		final ArtifactWithSyntaxInformation streamArtifact1 = loader
				.findByPath(StringArtifact.class, stringArtifactSource,
						"folder/file_not_changed1");
		final ArtifactWithSyntaxInformation streamArtifact2 = loader
				.findByPath(StringArtifact.class, stringArtifactSource,
						"folder/subfolder/file_not_changed1");
		final ArtifactWithSyntaxInformation streamArtifact3 = loader
				.findByPath(StringArtifact.class, stringArtifactSource,
						"folder/subfolder/anothersubfolder/file_not_changed1");
		assertThat(streamArtifact1, is(notNullValue()));
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact3, is(notNullValue()));
	}

}
