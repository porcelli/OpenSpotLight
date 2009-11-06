package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.FileSystemStreamArtifactFinder;

/**
 * The Class LocalSourceStreamArtifactFinderTest.
 */
public class FileSystemStreamArtifactFinderTest {

    /** The stream artifact finder. */
    private final FileSystemStreamArtifactFinder streamArtifactFinder = new FileSystemStreamArtifactFinder();

    /** The artifact source. */
    private ArtifactSource                       artifactSource;

    /**
     * Prepare artifact source.
     * 
     * @throws Exception the exception
     */
    @Before
    public void prepareArtifactSource() throws Exception {
        this.artifactSource = new ArtifactSource();
        this.artifactSource.setName("classpath:");
        this.artifactSource.setInitialLookup("./src/test/resources/artifacts/not_changed");
    }

    /**
     * Should find by relative path.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindByRelativePath() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/file_not_changed1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByRelativePath(this.artifactSource, streamArtifact1,
                                                                                            "../file_not_changed1");
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact2.getArtifactCompleteName(), is(this.artifactSource.getUniqueReference()
                                                                 + "/folder/file_not_changed1"));
    }

    /**
     * Should load not changed artifact.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldLoadNotChangedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/file_not_changed1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/file_not_changed1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/anothersubfolder/file_not_changed1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

}
