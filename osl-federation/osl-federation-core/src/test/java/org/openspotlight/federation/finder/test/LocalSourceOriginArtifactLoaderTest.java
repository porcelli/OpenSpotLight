package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.LocalSourceOriginArtifactLoader;

public class LocalSourceOriginArtifactLoaderTest {

	private LocalSourceOriginArtifactLoader loader = new LocalSourceOriginArtifactLoader();
	private ArtifactSource artifactSource;

	@Before
	public void prepareArtifactSource() throws Exception {
		artifactSource = new ArtifactSource();
		final Repository repo = new Repository();
		repo.setName("name");
		artifactSource.setRepository(repo);
		artifactSource.setName("classpath");
		artifactSource.setInitialLookup("./src/test/resources/artifacts");
	}

	/**
	 * Should find by relative path.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldFindByRelativePath() throws Exception {
		final StringArtifact streamArtifact1 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/file_included1");
		assertThat(streamArtifact1, is(notNullValue()));

		final StringArtifact streamArtifact2 = loader.findByRelativePath(
				StringArtifact.class, artifactSource, streamArtifact1,
				"../file_included1");
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact2.getArtifactCompleteName(),
				is("/folder/file_included1"));
	}

	/**
	 * Should load added artifact.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldLoadAddedArtifact() throws Exception {
		final StringArtifact streamArtifact1 = loader.findByPath(
				StringArtifact.class, artifactSource, "folder/file_included1");
		final StringArtifact streamArtifact2 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/file_included1");
		final StringArtifact streamArtifact3 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/anothersubfolder/file_included1");
		assertThat(streamArtifact1, is(notNullValue()));
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact3, is(notNullValue()));
	}

	/**
	 * Should load changed artifact.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldLoadChangedArtifact() throws Exception {
		final StringArtifact streamArtifact1 = loader.findByPath(
				StringArtifact.class, artifactSource, "folder/file_changed1");
		final StringArtifact streamArtifact2 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/file_changed1");
		final StringArtifact streamArtifact3 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/anothersubfolder/file_changed1");
		assertThat(streamArtifact1, is(notNullValue()));
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact3, is(notNullValue()));
	}

	/**
	 * Should load excluded artifact.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldLoadExcludedArtifact() throws Exception {
		final StringArtifact streamArtifact1 = loader.findByPath(
				StringArtifact.class, artifactSource, "folder/file_excluded1");
		final StringArtifact streamArtifact2 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/file_excluded1");
		final StringArtifact streamArtifact3 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/anothersubfolder/file_excluded1");
		assertThat(streamArtifact1, is(notNullValue()));
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact3, is(notNullValue()));
	}

	/**
	 * Should load not changed artifact.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldLoadNotChangedArtifact() throws Exception {
		final StringArtifact streamArtifact1 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/file_not_changed1");
		final StringArtifact streamArtifact2 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/file_not_changed1");
		final StringArtifact streamArtifact3 = loader.findByPath(
				StringArtifact.class, artifactSource,
				"folder/subfolder/anothersubfolder/file_not_changed1");
		assertThat(streamArtifact1, is(notNullValue()));
		assertThat(streamArtifact2, is(notNullValue()));
		assertThat(streamArtifact3, is(notNullValue()));
	}

	/**
	 * Sould list all kinds of files.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void souldListAllKindsOfFiles() throws Exception {
		final Set<StringArtifact> listedFiles = loader.listByPath(
				StringArtifact.class, artifactSource, "/folder");
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_included1",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_included2",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_included3",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_included1",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_included2",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_included3",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_included1",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_included2",
				ChangeType.INCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_included3",
				ChangeType.INCLUDED)), is(true));

		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_changed1",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_changed2",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_changed3",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_changed1",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_changed2",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_changed3",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_changed1",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_changed2",
				ChangeType.CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_changed3",
				ChangeType.CHANGED)), is(true));

		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_not_changed1",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_not_changed2",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_not_changed3",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_not_changed1",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_not_changed2",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_not_changed3",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_not_changed1",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_not_changed2",
				ChangeType.NOT_CHANGED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_not_changed3",
				ChangeType.NOT_CHANGED)), is(true));

		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_excluded1",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_excluded2",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/file_excluded3",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_excluded1",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_excluded2",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class, "/folder/subfolder/file_excluded3",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_excluded1",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_excluded2",
				ChangeType.EXCLUDED)), is(true));
		assertThat(listedFiles.contains(Artifact.createArtifact(
				StringArtifact.class,

				"/folder/subfolder/anothersubfolder/file_excluded3",
				ChangeType.EXCLUDED)), is(true));

	}

}
