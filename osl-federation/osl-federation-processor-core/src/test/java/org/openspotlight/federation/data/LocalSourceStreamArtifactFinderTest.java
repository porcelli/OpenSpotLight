package org.openspotlight.federation.data;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.federation.data.impl.StreamArtifact;

public class LocalSourceStreamArtifactFinderTest {

    private final LocalSourceStreamArtifactFinder streamArtifactFinder = new LocalSourceStreamArtifactFinder();

    @Test
    public void shouldLoadAddedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath("classpath:", "folder/file_included1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/file_included1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/anothersubfolder/file_included1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

    @Test
    public void shouldLoadChangedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath("classpath:", "folder/file_changed1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/file_changed1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/anothersubfolder/file_changed1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

    @Test
    public void shouldLoadExcludedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath("classpath:", "folder/file_excluded1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/file_excluded1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/anothersubfolder/file_excluded1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

    @Test
    public void shouldLoadNotChangedArtifact() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath("classpath:", "folder/file_not_changed1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/file_not_changed1");
        final StreamArtifact streamArtifact3 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/anothersubfolder/file_not_changed1");
        assertThat(streamArtifact1, is(notNullValue()));
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact3, is(notNullValue()));
    }

}
