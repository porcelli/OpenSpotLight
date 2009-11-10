package org.openspotlight.federation.loader;

import java.io.File;

import org.junit.Test;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Configuration;
import org.openspotlight.federation.finder.FileSystemStreamArtifactFinder;
import org.openspotlight.federation.loader.ArtifactLoader.ArtifactLoaderBehavior;

public class ArtifactLoaderTest {

    @Test
    public void shouldLoad() throws Exception {
        final Configuration configuration = new Configuration();
        configuration.setNumberOfParallelThreads(4);
        final ArtifactLoader loader = ArtifactLoader.Factory.createNewLoader(configuration,
                                                                             ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE,
                                                                             new FileSystemStreamArtifactFinder());
        final String initialRawPath = new File("../../..").getCanonicalPath();
        final ArtifactSource source = new ArtifactSource();
        source.setActive(true);
        source.setInitialLookup(initialRawPath);
        final Iterable<Artifact> artifacts = loader.loadArtifactsFromSource(source);
    }

}
