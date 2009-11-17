package org.openspotlight.federation.loader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashSet;

import org.junit.Test;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.finder.FileSystemStreamArtifactFinder;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;

public class ArtifactLoaderTest {

    @Test
    public void shouldLoad() throws Exception {
        final GlobalSettings configuration = new GlobalSettings();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);
        configuration.setNumberOfParallelThreads(4);
        final String initialRawPath = new File("../..").getCanonicalPath();
        final String initial = initialRawPath.substring(0, initialRawPath.lastIndexOf('/'));
        final String finalStr = initialRawPath.substring(initial.length());

        final ArtifactSource source = new ArtifactSource();
        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setFrom(finalStr);
        mapping.setTo("/sources/java/myProject");
        mapping.setIncludeds(new HashSet<String>());
        mapping.setExcludeds(new HashSet<String>());
        mapping.getIncludeds().add("*.java");
        source.setMappings(new HashSet<ArtifactSourceMapping>());
        source.getMappings().add(mapping);
        source.setActive(true);
        source.setInitialLookup(initial);
        source.setName("sourceName");

        final ArtifactLoader loader = ArtifactLoader.Factory.createNewLoader(configuration,
                                                                             ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE,
                                                                             new FileSystemStreamArtifactFinder());

        final Iterable<Artifact> artifacts = loader.loadArtifactsFromSource(source);
        boolean hasAny = false;
        for (final Artifact a : artifacts) {
            assertThat(a, is(notNullValue()));
            assertThat(a.getArtifactCompleteName().startsWith(mapping.getTo()), is(true));
            assertThat(a.getArtifactCompleteName().contains(mapping.getFrom()), is(false));
            hasAny = true;
        }
        assertThat(hasAny, is(true));
        loader.closeResources();
    }

}
