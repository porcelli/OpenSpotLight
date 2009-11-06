package org.openspotlight.federation.finder.test;

import java.io.File;

import org.junit.Before;
import org.openspotlight.federation.domain.ArtifactSource;

public class ArtifactFinderSupportTest {

    private ArtifactSource artifactSource;

    /**
     * Prepare artifact source.
     * 
     * @throws Exception the exception
     */
    @Before
    public void prepareArtifactSource() throws Exception {
        final File dir = new File("./target/test-data/ArtifactFinderSupportTest");
        if (dir.exists()) {
            dir.delete();
        }
        dir.mkdirs();
        this.artifactSource = new ArtifactSource();
        this.artifactSource.setName("target");
        this.artifactSource.setInitialLookup("./target/test-data/ArtifactFinderSupportTest/");
    }

    public void shouldFindIncludedArtifacts() {

    }
}
