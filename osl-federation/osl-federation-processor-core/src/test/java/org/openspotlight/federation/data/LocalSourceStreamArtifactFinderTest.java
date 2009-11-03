package org.openspotlight.federation.data;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.impl.StreamArtifact.ChangeType;

public class LocalSourceStreamArtifactFinderTest {

    private final LocalSourceStreamArtifactFinder streamArtifactFinder = new LocalSourceStreamArtifactFinder();

    @Test
    public void shouldFindByRelativePath() throws Exception {
        final StreamArtifact streamArtifact1 = this.streamArtifactFinder.findByPath("classpath:",
                                                                                    "folder/subfolder/file_included1");
        final StreamArtifact streamArtifact2 = this.streamArtifactFinder.findByRelativePath(streamArtifact1, "../file_included1");
        assertThat(streamArtifact2, is(notNullValue()));
        assertThat(streamArtifact2.getArtifactCompleteName(), is("classpath:/folder/file_included1"));
    }

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

    @Test
    public void souldListAllKindsOfFiles() throws Exception {
        final Set<StreamArtifact> listedFiles = this.streamArtifactFinder.listByPath("classpath:", "folder");
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_included1",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_included2",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_included3",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_included1",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_included2",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_included3",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_included1",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_included2",
                                                                               ChangeType.INCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_included3",
                                                                               ChangeType.INCLUDED, "")), is(true));

        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_changed1",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_changed2",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_changed3",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_changed1",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_changed2",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_changed3",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_changed1",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_changed2",
                                                                               ChangeType.CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_changed3",
                                                                               ChangeType.CHANGED, "")), is(true));

        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_not_changed1",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_not_changed2",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_not_changed3",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_not_changed1",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_not_changed2",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_not_changed3",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_not_changed1",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_not_changed2",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_not_changed3",
                                                                               ChangeType.NOT_CHANGED, "")), is(true));

        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_excluded1",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_excluded2",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/file_excluded3",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_excluded1",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_excluded2",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(listedFiles.contains(StreamArtifact.createNewStreamArtifact("classpath:/folder/subfolder/file_excluded3",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_excluded1",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_excluded2",
                                                                               ChangeType.EXCLUDED, "")), is(true));
        assertThat(
                   listedFiles.contains(StreamArtifact.createNewStreamArtifact(
                                                                               "classpath:/folder/subfolder/anothersubfolder/file_excluded3",
                                                                               ChangeType.EXCLUDED, "")), is(true));

    }
}
