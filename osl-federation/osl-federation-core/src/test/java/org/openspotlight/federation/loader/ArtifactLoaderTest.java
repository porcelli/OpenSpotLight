package org.openspotlight.federation.loader;

import java.io.File;
import java.util.HashSet;

import org.junit.Test;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactMapping;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Configuration;
import org.openspotlight.federation.finder.FileSystemStreamArtifactFinder;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;

public class ArtifactLoaderTest {

    @Test
    public void shouldLoad() throws Exception {
        final Configuration configuration = new Configuration();
        configuration.setDefaultSleepingIntervalInMilliseconds(500);
        configuration.setNumberOfParallelThreads(4);
        final ArtifactLoader loader = ArtifactLoader.Factory.createNewLoader(configuration,
                                                                             ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE,
                                                                             new FileSystemStreamArtifactFinder());
        final String initialRawPath = new File("../..").getCanonicalPath();
        final String initial = initialRawPath.substring(0, initialRawPath.lastIndexOf('/'));
        final String finalStr = initialRawPath.substring(initial.length());

        final ArtifactSource source = new ArtifactSource();
        final ArtifactMapping mapping = new ArtifactMapping();
        mapping.setRelative(finalStr);
        mapping.setIncludeds(new HashSet<String>());
        mapping.setExcludeds(new HashSet<String>());
        mapping.getIncludeds().add("*.java");
        source.setMappings(new HashSet<ArtifactMapping>());
        source.getMappings().add(mapping);
        source.setActive(true);
        source.setInitialLookup(initial);
        final Iterable<Artifact> artifacts = loader.loadArtifactsFromSource(source);
        loader.closeResources();
    }

}
