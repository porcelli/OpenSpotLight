package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.LocalSourceStreamArtifactFinder;

// TODO: Auto-generated Javadoc
/**
 * The Class LocalSourceStreamArtifactFinderTest.
 */
@SuppressWarnings( "boxing" )
public class LocalSourceStreamArtifactFinderTest {

    /** The stream artifact finder. */
    private final LocalSourceStreamArtifactFinder streamArtifactFinder = new LocalSourceStreamArtifactFinder();

    /** The artifact source. */
    private ArtifactSource                        artifactSource;

    /**
     * Prepare artifact source.
     * 
     * @throws Exception the exception
     */
    @Before
    public void prepareArtifactSource() throws Exception {
        this.artifactSource = new ArtifactSource();
        this.artifactSource.setName("classpath:");
        this.artifactSource.setInitialLookup("./src/test/resources/artifacts");
    }

    /**
     * Should find by relative path.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindByRelativePath() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/file_included1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByRelativePath(this.artifactSource, streamArtifact1,
                                                                                            "../file_included1");
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact2.getArtifactCompleteName(), is(this.artifactSource.getUniqueReference()
                                                                 + "/folder/file_included1"));
    }

    /**
     * Should load added artifact.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldLoadAddedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath(this.artifactSource, "folder/file_included1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/file_included1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/anothersubfolder/file_included1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

    /**
     * Should load changed artifact.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldLoadChangedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath(this.artifactSource, "folder/file_changed1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/file_changed1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/anothersubfolder/file_changed1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

    /**
     * Should load excluded artifact.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldLoadExcludedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath(this.artifactSource, "folder/file_excluded1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/file_excluded1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath(this.artifactSource,
                                                                                    "folder/subfolder/anothersubfolder/file_excluded1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
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

    /**
     * Sould list all kinds of files.
     * 
     * @throws Exception the exception
     */
    @Test
    public void souldListAllKindsOfFiles() throws Exception {
        final Set<StreamArtifact> listedFiles = this.streamArtifactFinder.listByPath(this.artifactSource, "folder");
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_included1", ChangeType.INCLUDED,
                                                                               "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_included2", ChangeType.INCLUDED,
                                                                               "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_included3", ChangeType.INCLUDED,
                                                                               "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_included1",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_included2",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_included3",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_included1",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_included2",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_included3",
                                                                               ChangeType.INCLUDED, "")), is(true));

        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_changed1", ChangeType.CHANGED, "")),
                   is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_changed2", ChangeType.CHANGED, "")),
                   is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_changed3", ChangeType.CHANGED, "")),
                   is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_changed1",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_changed2",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_changed3",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_changed1",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_changed2",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_changed3",
                                                                               ChangeType.CHANGED, "")), is(true));

        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_not_changed1",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_not_changed2",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_not_changed3",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_not_changed1",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_not_changed2",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_not_changed3",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_not_changed1",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_not_changed2",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_not_changed3",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));

        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_excluded1", ChangeType.EXCLUDED,
                                                                               "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_excluded2", ChangeType.EXCLUDED,
                                                                               "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/file_excluded3", ChangeType.EXCLUDED,
                                                                               "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_excluded1",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_excluded2",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact(this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/file_excluded3",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_excluded1",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_excluded2",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               this.artifactSource.getUniqueReference()
                                                                               + "/folder/subfolder/anothersubfolder/file_excluded3",
                                                                               ChangeType.EXCLUDED, "")), is(true));

    }
}
