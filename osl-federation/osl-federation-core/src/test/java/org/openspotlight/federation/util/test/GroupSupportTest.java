/*
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
package org.openspotlight.federation.util.test;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.util.GroupDifferences;
import org.openspotlight.federation.util.GroupSupport;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;

public class GroupSupportTest {

    @Test
    public void shouldFindAddedGroupsOnNewRepositories() throws Exception {
        final GroupDifferences empty = new GroupDifferences();
        final Repository repository = new Repository();
        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(empty, null, repository);
        Assert.assertThat(empty.getAddedGroups().contains("repositoryName/1"),
                          Is.is(true));
        Assert.assertThat(
                          empty.getAddedGroups().contains("repositoryName/1/2"), Is
                                                                                   .is(true));

    }

    @Test
    public void shouldFindAddedGroupsOnNewRepositoriesOnDelta()
            throws Exception {
        final GroupDifferences notEmpty = new GroupDifferences();
        notEmpty.setRepositoryName("repositoryName");
        notEmpty.getAddedGroups().add("repositoryName/existentBefore");
        notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

        final Repository repository = new Repository();
        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(notEmpty, null,
                                                      repository);
        Assert.assertThat(notEmpty.getAddedGroups()
                                  .contains("repositoryName/1"), Is.is(true));
        Assert.assertThat(notEmpty.getAddedGroups().contains(
                                                             "repositoryName/1/2"), Is.is(true));
        Assert.assertThat(notEmpty.getAddedGroups().contains(
                                                             "repositoryName/existentBefore"), Is.is(true));
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/excludedBefore"), Is.is(true));
    }

    @Test
    public void shouldFindAddedGroupsWithinExistentRepositories()
            throws Exception {

        final Repository oldRepository = new Repository();

        oldRepository.setName("repositoryName");
        final Group oldGroup = new Group();
        oldGroup.setRepository(oldRepository);
        oldGroup.setName("1");
        oldRepository.getGroups().add(oldGroup);

        final GroupDifferences empty = new GroupDifferences();

        final Repository repository = new Repository();

        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(empty, oldRepository,
                                                      repository);
        Assert.assertThat(empty.getAddedGroups().contains("repositoryName/1"),
                          Is.is(false));
        Assert.assertThat(
                          empty.getAddedGroups().contains("repositoryName/1/2"), Is
                                                                                   .is(true));
    }

    @Test
    public void shouldFindAddedGroupsWithinExistentRepositoriesOnDelta()
            throws Exception {
        final GroupDifferences notEmpty = new GroupDifferences();
        notEmpty.setRepositoryName("repositoryName");

        notEmpty.getAddedGroups().add("repositoryName/existentBefore");
        notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

        final Repository oldRepository = new Repository();

        oldRepository.setName("repositoryName");
        final Group oldGroup = new Group();
        oldGroup.setRepository(oldRepository);
        oldGroup.setName("1");
        oldRepository.getGroups().add(oldGroup);

        final Repository repository = new Repository();

        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(notEmpty, oldRepository,
                                                      repository);
        Assert.assertThat(notEmpty.getAddedGroups()
                                  .contains("repositoryName/1"), Is.is(false));
        Assert.assertThat(notEmpty.getAddedGroups().contains(
                                                             "repositoryName/1/2"), Is.is(true));
        Assert.assertThat(notEmpty.getAddedGroups().contains(
                                                             "repositoryName/existentBefore"), Is.is(true));
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/excludedBefore"), Is.is(true));
    }

    @Test
    public void shouldFindExcludedGroupsOnOldRepositories() throws Exception {
        final GroupDifferences empty = new GroupDifferences();

        final Repository repository = new Repository();

        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(empty, repository, null);
        Assert.assertThat(
                          empty.getRemovedGroups().contains("repositoryName/1"), Is
                                                                                   .is(true));
        Assert.assertThat(empty.getRemovedGroups().contains(
                                                            "repositoryName/1/2"), Is.is(true));
    }

    @Test
    public void shouldFindExcludedGroupsOnOldRepositoriesOnDelta()
            throws Exception {
        final GroupDifferences notEmpty = new GroupDifferences();
        notEmpty.setRepositoryName("repositoryName");

        notEmpty.getAddedGroups().add("repositoryName/existentBefore");
        notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

        final Repository repository = new Repository();

        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(notEmpty, repository,
                                                      null);
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/1"), Is.is(true));
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/1/2"), Is.is(true));
        Assert.assertThat(notEmpty.getAddedGroups().contains(
                                                             "repositoryName/existentBefore"), Is.is(true));
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/excludedBefore"), Is.is(true));
    }

    @Test
    public void shouldFindExcludedGroupsWithinExistentRepositories()
            throws Exception {

        final Repository oldRepository = new Repository();
        oldRepository.setName("repositoryName");
        final Group oldGroup = new Group();
        oldGroup.setRepository(oldRepository);
        oldGroup.setName("1");
        oldRepository.getGroups().add(oldGroup);

        final GroupDifferences empty = new GroupDifferences();

        final Repository repository = new Repository();

        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        GroupSupport.findDifferencesOnAllRepositories(empty, oldRepository,
                                                      repository);
        Assert.assertThat(empty, Is.is(IsNull.notNullValue()));
        Assert.assertThat(
                          empty.getRemovedGroups().contains("repositoryName/1"), Is
                                                                                   .is(false));
    }

    @Test
    public void shouldFindExcludedGroupsWithinExistentRepositoriesOnDelta()
            throws Exception {
        final GroupDifferences notEmpty = new GroupDifferences();
        notEmpty.setRepositoryName("repositoryName");

        notEmpty.getAddedGroups().add("repositoryName/existentBefore");
        notEmpty.getRemovedGroups().add("repositoryName/excludedBefore");

        final Repository oldRepository = new Repository();
        oldRepository.setName("repositoryName");
        final Group oldGroup = new Group();
        oldGroup.setRepository(oldRepository);
        oldGroup.setName("1");
        oldRepository.getGroups().add(oldGroup);

        final Repository repository = new Repository();

        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        final Group newGroup2 = new Group();
        newGroup2.setGroup(newGroup);
        newGroup2.setName("2");
        newGroup.getGroups().add(newGroup2);
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/1"), Is.is(false));
        Assert.assertThat(notEmpty.getAddedGroups().contains(
                                                             "repositoryName/existentBefore"), Is.is(true));
        Assert.assertThat(notEmpty.getRemovedGroups().contains(
                                                               "repositoryName/excludedBefore"), Is.is(true));
    }

    @Test
    public void shouldNotSeeAddedAndRemovedGroups() throws Exception {
        final GroupDifferences empty = new GroupDifferences();
        final Repository repository = new Repository();
        repository.setName("repositoryName");
        final Group newGroup = new Group();
        newGroup.setRepository(repository);
        newGroup.setName("1");
        repository.getGroups().add(newGroup);
        GroupSupport.findDifferencesOnAllRepositories(empty, null, repository);
        Assert.assertThat(empty.getAddedGroups().contains("repositoryName/1"),
                          Is.is(true));
        GroupSupport.findDifferencesOnAllRepositories(empty, repository,
                                                      new Repository());
        Assert.assertThat(empty.getAddedGroups().contains("repositoryName/1"),
                          Is.is(false));
        Assert.assertThat(
                          empty.getRemovedGroups().contains("repositoryName/1"), Is
                                                                                   .is(false));
    }

    @Test
    public void shouldPersistAndRetrieveProperties() throws Exception {
        final SessionWithLock session = JcrConnectionProvider.createFromData(
                                                                             DefaultJcrDescriptor.TEMP_DESCRIPTOR).openSession();
        final GroupDifferences differences = new GroupDifferences();
        differences.setRepositoryName("repositoryName");
        differences.getAddedGroups().add("a");
        differences.getAddedGroups().add("b");
        differences.getAddedGroups().add("c");

        differences.getRemovedGroups().add("d");
        differences.getRemovedGroups().add("e");
        differences.getRemovedGroups().add("f");
        GroupSupport.saveDifferences(session, differences);
        session.save();
        final GroupDifferences loaded = GroupSupport.getDifferences(session,
                                                                    "repositoryName");
        Assert.assertThat(loaded.getRepositoryName(), Is.is(differences
                                                                       .getRepositoryName()));
        Assert.assertThat(loaded.getAddedGroups().size(), Is.is(3));
        Assert.assertThat(loaded.getRemovedGroups().size(), Is.is(3));

    }
}
