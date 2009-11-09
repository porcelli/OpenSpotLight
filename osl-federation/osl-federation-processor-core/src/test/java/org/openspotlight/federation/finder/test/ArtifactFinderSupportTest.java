package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.ArtifactFinderSupport;

public class ArtifactFinderSupportTest {

    private ArtifactSource artifactSource;

    @Test
    public void shouldCleanAllArtifactsMarkedWithExcluded() {
        final Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final StreamArtifact existent1 = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "abc");
        final StreamArtifact existent2 = StreamArtifact.createNewStreamArtifact("a/b/d", ChangeType.CHANGED, "def");
        existents.add(existent1);
        existents.add(existent2);
        ArtifactFinderSupport.freezeChangesAfterBundleProcessing(existents);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getContent(), is(not("abc")));
    }

    @Test
    public void shouldFindChangedArtifactsWhenTheExistentExcluded() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "def");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent(), is("abc"));
    }

    @Test
    public void shouldFindChangedArtifactsWhenTheExistentIsChanged() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.CHANGED, "def");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent(), is("abc"));
    }

    @Test
    public void shouldFindChangedArtifactsWhenTheExistentIsNotChanged() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.NOT_CHANGED, "def");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent(), is("abc"));
    }

    @Test
    public void shouldFindExcludedArtifactsWhenTheExistentIsExcludedWithOtherContent() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "def");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.EXCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindExcludedArtifactsWhenTheExistentIsExcludedWithSameContent() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "abc");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.EXCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindExcludedArtifactsWhenTheresNoOther() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "def");
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.EXCLUDED));
        assertThat(existents.iterator().next(), is(existent));
    }

    @Test
    public void shouldFindIncludedArtifacts() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        newOnes.add(newOne);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.INCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindIncludedArtifactsWhenTheExistentIsIncluded() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "def");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.INCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent(), is("abc"));

    }

    @Test
    public void shouldFindNotChangedArtifactsWhenTheExistentIsExcluded() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "abc");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindNotChangedArtifactsWhenTheExistentIsNotChanged() {
        Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> newOnes = new HashSet<StreamArtifact>();
        final StreamArtifact newOne = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.INCLUDED, "abc");
        final StreamArtifact existent = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.NOT_CHANGED, "abc");
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.NOT_CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldMarkAllArtifactsWithNotChanged() {
        final Set<StreamArtifact> existents = new HashSet<StreamArtifact>();
        final StreamArtifact existent1 = StreamArtifact.createNewStreamArtifact("a/b/c", ChangeType.EXCLUDED, "abc");
        final StreamArtifact existent2 = StreamArtifact.createNewStreamArtifact("a/b/d", ChangeType.CHANGED, "def");
        existents.add(existent1);
        existents.add(existent2);
        ArtifactFinderSupport.freezeChangesAfterBundleProcessing(existents);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getContent(), is("def"));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.NOT_CHANGED));

    }

}
