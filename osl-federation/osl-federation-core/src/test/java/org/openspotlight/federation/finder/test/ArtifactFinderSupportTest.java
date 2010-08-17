/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.federation.finder.test;

import org.junit.Test;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.finder.ArtifactFinderSupport;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ArtifactFinderSupportTest {

    @Test
    public void shouldCleanAllArtifactsMarkedWithExcluded() {
        final Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final StringArtifact existent1 = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent1.getContent().setTransient(asList("abc"));
        final StringArtifact existent2 = Artifact.createArtifact(StringArtifact.class, "a/b/d", ChangeType.CHANGED);
        existent2.getContent().setTransient(asList("def"));
        existents.add(existent1);
        existents.add(existent2);
        ArtifactFinderSupport.freezeChangesAfterBundleProcessing(existents,null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getContent().get(null), is(not(asList("abc"))));
    }

    @Test
    public void shouldFindChangedArtifactsWhenTheExistentExcluded() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("def"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent().get(null), is(asList("abc")));
    }

    @Test
    public void shouldFindChangedArtifactsWhenTheExistentIsChanged() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("def"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent().get(null), is(asList("abc")));
    }

    @Test
    public void shouldFindChangedArtifactsWhenTheExistentIsNotChanged() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("def"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent().get(null), is(asList("abc")));
    }

    @Test
    public void shouldFindExcludedArtifactsWhenTheExistentIsExcludedWithOtherContent() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("def"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.EXCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindExcludedArtifactsWhenTheExistentIsExcludedWithSameContent() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("abc"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.EXCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindExcludedArtifactsWhenTheresNoOther() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("def"));
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.EXCLUDED));
        assertThat(existents.iterator().next(), is(existent));
    }

    @Test
    public void shouldFindIncludedArtifacts() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        newOnes.add(newOne);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.INCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindIncludedArtifactsWhenTheExistentIsIncluded() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        existent.getContent().setTransient(asList("def"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.INCLUDED));
        assertThat(existents.iterator().next(), is(newOne));
        assertThat(existents.iterator().next().getContent().get(null), is(asList("abc")));

    }

    @Test
    public void shouldFindNotChangedArtifactsWhenTheExistentIsExcluded() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent.getContent().setTransient(asList("abc"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldFindNotChangedArtifactsWhenTheExistentIsNotChanged() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> newOnes = new HashSet<StringArtifact>();
        final StringArtifact newOne = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        newOne.getContent().setTransient(asList("abc"));
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.NOT_CHANGED);
        existent.getContent().setTransient(asList("abc"));
        newOnes.add(newOne);
        existents.add(existent);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, newOnes, null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.NOT_CHANGED));
        assertThat(existents.iterator().next(), is(newOne));
    }

    @Test
    public void shouldIgnoreArtifactsIncludedAndExcluded() {
        Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final Set<StringArtifact> excludeds = new HashSet<StringArtifact>();
        final StringArtifact existent = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        existent.getContent().setTransient(asList("willBeExcluded"));
        existents.add(existent);
        final StringArtifact excluded = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        excluded.getContent().setTransient(asList("willBeExcluded"));
        excludeds.add(excluded);
        existents = ArtifactFinderSupport.applyDifferenceOnExistents(existents, excludeds, null);
        assertThat(existents.size(), is(0));
    }

    @Test
    public void shouldMarkAllArtifactsWithNotChanged() {
        final Set<StringArtifact> existents = new HashSet<StringArtifact>();
        final StringArtifact existent1 = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.INCLUDED);
        existent1.getContent().setTransient(asList("def"));
        final StringArtifact existent2 = Artifact.createArtifact(StringArtifact.class, "a/b/c", ChangeType.EXCLUDED);
        existent2.getContent().setTransient(asList("abc"));
        existents.add(existent1);
        existents.add(existent2);
        ArtifactFinderSupport.freezeChangesAfterBundleProcessing(existents,null);
        assertThat(existents.size(), is(1));
        assertThat(existents.iterator().next().getContent().get(null), is(asList("def")));
        assertThat(existents.iterator().next().getChangeType(), is(ChangeType.NOT_CHANGED));

    }

}
