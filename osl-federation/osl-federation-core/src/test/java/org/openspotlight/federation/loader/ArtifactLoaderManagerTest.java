package org.openspotlight.federation.loader;

import org.junit.Test;
import org.openspotlight.common.util.Files;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;
import org.openspotlight.federation.finder.JcrPersistentArtifactManagerProvider;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ArtifactLoaderManagerTest {

	@Test
	public void shouldLoad() throws Exception {
		final GlobalSettings settings = new GlobalSettings();
		settings.getLoaderRegistry().add(FileSystemOriginArtifactLoader.class);
		settings.setDefaultSleepingIntervalInMilliseconds(500);
		final String initialRawPath = Files.getNormalizedFileName(new File(
				"."));
		final String initial = initialRawPath.substring(0, initialRawPath
				.lastIndexOf('/'));
		final String finalStr = initialRawPath.substring(initial.length());
		final ArtifactSource source = new ArtifactSource();
		final Repository repository = new Repository();
		repository.setName("repository");
		source.setRepository(repository);
		final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
		mapping.setFrom(finalStr);
		mapping.setTo("/sources/java/myProject");
		mapping.setIncludeds(new HashSet<String>());
		mapping.setExcludeds(new HashSet<String>());
		mapping.getIncludeds().add("*.java");
		mapping.setSource(source);
		source.setMappings(new HashSet<ArtifactSourceMapping>());
		source.getMappings().add(mapping);
		source.setActive(true);
		source.setBinary(false);
		source.setInitialLookup(initial);
		source.setName("sourceName");
		JcrPersistentArtifactManagerProvider provider = new JcrPersistentArtifactManagerProvider(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR, repository);

		ArtifactLoaderManager.INSTANCE.refreshResources(settings, source,
				provider);
		Set<StringArtifact> artifacts = provider.get().listByPath(
				StringArtifact.class, null);
		provider.closeResources();
		boolean hasAny = false;

		for (final Artifact a : artifacts) {
			assertThat(a, is(notNullValue()));
			assertThat(a.getArtifactCompleteName().startsWith(
					mapping.getTo() + "/"), is(true));
			assertThat(a.getArtifactCompleteName().contains(
					mapping.getFrom() + "/"), is(false));
			hasAny = true;
		}
		assertThat(hasAny, is(true));
		provider.closeResources();
	}

}
