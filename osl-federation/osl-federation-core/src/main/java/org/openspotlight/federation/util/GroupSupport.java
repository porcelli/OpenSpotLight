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
package org.openspotlight.federation.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openspotlight.bundle.domain.Group;
import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;

public class GroupSupport {

    private static StorageNode getRootNode( final SimplePersistCapable<StorageNode, StorageSession> simplePersist) {
        return simplePersist.getCurrentSession().withPartition(simplePersist.getCurrentPartition()).createNewSimpleNode(
                                                                                                                        "group-differences");
    }

    private static void createDifferences( final GroupDifferences differences,
                                           final Repository newOne,
                                           final Set<Group> newGroups,
                                           final Set<Group> oldGroups ) {
        findChangesOnNewGroups(differences, oldGroups, newGroups);
    }

    public static Set<Group> findAllGroups( final Repository repository ) {
        if (repository == null) {
            return Collections.<Group>emptySet();
        }
        final Set<Group> groups = new HashSet<Group>();
        final AggregateVisitor<Group> visitor = new AggregateVisitor<Group>(groups);
        SimpleNodeTypeVisitorSupport.<Group>acceptVisitorOn(Group.class, repository, visitor);
        return groups;
    }

    private static void findChangesOnNewGroups( final GroupDifferences differences,
                                                final Set<Group> oldGroups,
                                                final Set<Group> newOnes ) {

        for (final Group newOne : newOnes) {
            if (!oldGroups.contains(newOne)) {
                differences.getAddedGroups().add(newOne.getUniqueName());
            }
        }
        for (final Group oldOne : oldGroups) {
            if (!newOnes.contains(oldOne)) {
                differences.getRemovedGroups().add(oldOne.getUniqueName());
            }
        }
    }

    public static void findDifferencesOnAllRepositories( final GroupDifferences differences,
                                                         final Repository oldOne,
                                                         final Repository newOne ) {
        final Set<Group> newGroups = findAllGroups(newOne);
        final Set<Group> oldGroups = findAllGroups(oldOne);
        createDifferences(differences, newOne, newGroups, oldGroups);
        removeDupplicates(differences);
    }

    public static GroupDifferences getDifferences( final  SimplePersistCapable<StorageNode, StorageSession> simplePersist,
                                                   String repositoryName ) {
        final Iterable<GroupDifferences> result = simplePersist.findByProperties(getRootNode(simplePersist),
                                                                                 GroupDifferences.class,
                                                                                 new String[] {"repositoryName"},
                                                                                 new Object[] {repositoryName});
        Iterator<GroupDifferences> it = result.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;

    }

    private static void removeDupplicates( final GroupDifferences differences ) {
        final Set<String> dupplicate = new HashSet<String>();
        for (final String s : differences.getAddedGroups()) {
            if (differences.getRemovedGroups().contains(s)) {
                dupplicate.add(s);
            }
        }
        differences.getAddedGroups().removeAll(dupplicate);
        differences.getRemovedGroups().removeAll(dupplicate);
    }

    public static void saveDifferences( final  SimplePersistCapable<StorageNode, StorageSession> simplePersist,
                                        final GroupDifferences differences ) {
        try {
            Assertions.checkNotNull("differences", differences);
            Assertions.checkNotNull("differences.repositoryName", differences.getRepositoryName());

            simplePersist.convertBeanToNode(getRootNode(simplePersist), differences);
            simplePersist.getCurrentSession().flushTransient();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    private GroupSupport() {
    }

}
