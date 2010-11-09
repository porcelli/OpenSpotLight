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
package org.openspotlight.federation.finder;

import static org.openspotlight.common.collection.IteratorBuilder.createIteratorBuilder;
import static org.openspotlight.common.util.Strings.concatPaths;

import org.openspotlight.common.collection.IteratorBuilder;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.domain.StorageNode;

public class PersistentArtifactManagerImpl extends
        AbstractPersistentArtifactManager {

    private static final int                                                                         IDX_ARTIFACT_NAME               = 0,
    IDX_MAPPED = 1;

    private static final String[]                                                                    PROPERTY_NAME_ARTIFACT_PATH     =
                                                                                                                                         {
                                                                                                                                         "artifactCompleteName",
                                                                                                                                         "mappedTo"};

    private static final String[]                                                                    PROPERTY_NAME_OLD_ARTIFACT_PATH =
                                                                                                                                         {
                                                                                                                                         "originalName",
                                                                                                                                         "mappedFrom"};

    private final String                                                                             repositoryName;

    private final SimplePersistCapable<org.openspotlight.storage.domain.StorageNode, StorageSession> simplePersist;

    public PersistentArtifactManagerImpl(final Repository repository,
                                         final SimplePersistFactory factory) {
        simplePersist = factory
                .createSimplePersist(RegularPartitions.FEDERATION);
        repositoryName = repository.getName();
    }

    private String createOriginName(final ArtifactSource source, final String originName) {
        return concatPaths(source.getInitialLookup(), originName);
    }

    private <A> A internalFind(final Class<A> type, final String path, final String propertyName)
            throws Exception {
        return simplePersist.findUniqueByProperties(type,
                new String[] {propertyName}, new Object[] {path});

    }

    private <A> Iterable<String> privateRetrieveNames(final Class<A> type,
                                                      final String initialPath, final String[] propertyNameAndPath)
            throws Exception {
        Iterable<StorageNode> foundNodes;
        final String nodeName = simplePersist.getInternalMethods().getNodeName(type);
        if (initialPath != null) {
            foundNodes = simplePersist.getPartitionMethods().createCriteria()
                    .withNodeEntry(nodeName)
                    .withProperty(propertyNameAndPath[IDX_MAPPED])
                    .equalsTo(initialPath).buildCriteria()
                    .andFind(simplePersist.getCurrentSession());
        } else {
            foundNodes = simplePersist.getPartitionMethods().findByType(
                    nodeName);

        }

        final IteratorBuilder.SimpleIteratorBuilder<String, StorageNode> b = createIteratorBuilder();
        b.withConverter(new IteratorBuilder.Converter<String, StorageNode>() {
            @Override
            public String convert(final StorageNode nodeEntry)
                throws Exception {
                final String name = nodeEntry.getPropertyValueAsString(
                        simplePersist.getCurrentSession(),
                        propertyNameAndPath[IDX_ARTIFACT_NAME]);
                if (name == null) {
                    throw new IllegalStateException("Mandatory property "
                            + propertyNameAndPath[IDX_ARTIFACT_NAME]
                            + " from node " + nodeEntry + " with null value");
                }
                return name;
            }
        });
        final Iterable<String> result = b.withItems(foundNodes).andBuild();
        return result;
    }

    @Override
    protected <A extends Artifact> void internalAddTransient(final A artifact)
            throws Exception {
        artifact.setRepositoryName(repositoryName);
        simplePersist.convertBeanToNode(artifact);
    }

    @Override
    protected void internalCloseResources()
        throws Exception {

    }

    @Override
    protected <A extends Artifact> A internalFindByOriginalName(
                                                                final ArtifactSource source, final Class<A> type,
                                                                final String originName)
            throws Exception {
        return internalFind(type, createOriginName(source, originName),
                PROPERTY_NAME_OLD_ARTIFACT_PATH[IDX_ARTIFACT_NAME]);
    }

    @Override
    protected <A extends Artifact> A internalFindByPath(final Class<A> type,
                                                        final String path)
        throws Exception {
        return internalFind(type, path,
                PROPERTY_NAME_ARTIFACT_PATH[IDX_ARTIFACT_NAME]);
    }

    @Override
    protected <A extends Artifact> boolean internalIsTypeSupported(final Class<A> type)
            throws Exception {
        return true;
    }

    @Override
    protected <A extends Artifact> void internalMarkAsRemoved(final A artifact)
            throws Exception {
        final StorageNode node = simplePersist.convertBeanToNode(artifact);
        simplePersist.getCurrentSession().removeNode(node);
    }

    @Override
    protected <A extends Artifact> Iterable<String> internalRetrieveNames(
                                                                          final Class<A> type, final String initialPath)
        throws Exception {
        return privateRetrieveNames(type, initialPath,
                PROPERTY_NAME_ARTIFACT_PATH);

    }

    @Override
    protected <A extends Artifact> Iterable<String> internalRetrieveOriginalNames(
                                                                                  final ArtifactSource source,
                                                                                  final Class<A> type,
                                                                                  final String initialPath)
            throws Exception {
        final Iterable<String> result = privateRetrieveNames(type, initialPath,
                PROPERTY_NAME_OLD_ARTIFACT_PATH);
        return result;

    }

    @Override
    protected void internalSaveTransientData()
        throws Exception {
        simplePersist.getCurrentSession().flushTransient();
    }

    @Override
    protected boolean isMultithreaded() {
        return true;
    }

    @Override
    public SimplePersistCapable<StorageNode, StorageSession> getSimplePersist() {
        return simplePersist;
    }

    @Override
    public StorageSession getStorageSession() {
        return simplePersist.getCurrentSession();
    }

}
