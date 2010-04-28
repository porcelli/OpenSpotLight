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
package org.openspotlight.federation.finder;

import com.google.common.collect.ImmutableSet;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class PersistentArtifactManagerImpl extends AbstractPersistentArtifactManager {

    private final String                                              repositoryName;
    private final SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist;
    private final STNodeEntry                                         rootPath;

    public PersistentArtifactManagerImpl(
                                          Repository repository, SimplePersistFactory factory ) {
        this.simplePersist = factory.createSimplePersist(SLPartition.FEDERATION);
        this.repositoryName = repository.getName();
        this.rootPath = simplePersist.getPartitionMethods().createNewSimpleNode("artifacts");
    }

    @Override
    protected <A extends Artifact> void internalAddTransient( A artifact ) throws Exception {
        artifact.setRepositoryName(repositoryName);
        simplePersist.convertBeanToNode(this.rootPath, artifact);
    }

    @Override
    protected void internalCloseResources() throws Exception {

    }

    @Override
    protected <A extends Artifact> A internalFindByOriginalName( ArtifactSource source,
                                                                 Class<A> type,
                                                                 String originName ) throws Exception {
        return internalFind(type, createOriginName(source, originName), PROPERTY_NAME_OLD_ARTIFACT_PATH);
    }

    private String createOriginName( ArtifactSource source,
                                     String originName ) {
        return source.getName() + ":" + (originName != null ? originName : "");
    }

    @Override
    protected <A extends Artifact> A internalFindByPath( Class<A> type,
                                                         String path ) throws Exception {
        return internalFind(type, path, PROPERTY_NAME_ARTIFACT_PATH);
    }

    private <A> A internalFind( Class<A> type,
                                String path,
                                String propertyName ) throws Exception {
        return simplePersist.findUniqueByProperties(this.rootPath, type, new String[] {propertyName}, new Object[] {path});

    }

    @Override
    protected <A extends Artifact> boolean internalIsTypeSupported( Class<A> type ) throws Exception {
        return true;
    }

    @Override
    protected <A extends Artifact> void internalMarkAsRemoved( A artifact ) throws Exception {
        final STNodeEntry node = simplePersist.convertBeanToNode(this.rootPath, artifact);
        simplePersist.getCurrentSession().removeNode(node);
    }

    @Override
    protected <A extends Artifact> Set<String> internalRetrieveOriginalNames( ArtifactSource source,
                                                                              Class<A> type,
                                                                              String initialPath ) throws Exception {
        return privateRetrieveNames(type, createOriginName(source, initialPath), PROPERTY_NAME_OLD_ARTIFACT_PATH);
    }

    @Override
    protected void internalSaveTransientData() throws Exception {
        simplePersist.getCurrentSession().flushTransient();
    }

    @Override
    protected <A extends Artifact> Set<String> internalRetrieveNames( Class<A> type,
                                                                      String initialPath ) throws Exception {
        return privateRetrieveNames(type, initialPath, PROPERTY_NAME_ARTIFACT_PATH);

    }

    public static final String PROPERTY_NAME_ARTIFACT_PATH     = "artifactCompleteName";

    public static final String PROPERTY_NAME_OLD_ARTIFACT_PATH = "originalName";

    private <A> Set<String> privateRetrieveNames( Class<A> type,
                                                  String initialPath,
                                                  String propertyName ) throws Exception {
        Set<STNodeEntry> foundNodes;
        String nodeName = simplePersist.getInternalMethods().getNodeName(type);
        if (initialPath != null) {
            foundNodes = simplePersist.getPartitionMethods().createCriteria().withNodeEntry(nodeName).withProperty(propertyName).containsString(
                                                                                                                                                initialPath).buildCriteria().andFind(
                                                                                                                                                                                     simplePersist.getCurrentSession());
        } else {
            foundNodes = simplePersist.getPartitionMethods().findNamed(nodeName);

        }
        Set<String> names = newHashSet();
        for (STNodeEntry nodeEntry : foundNodes) {
            String name = nodeEntry.<String>getPropertyValue(simplePersist.getCurrentSession(), propertyName);
            names.add(name);
        }

        return ImmutableSet.copyOf(names);

    }

    @Override
    protected boolean isMultithreaded() {
        return true;
    }

    public SimplePersistCapable<STNodeEntry, STStorageSession> getSimplePersist() {
        return simplePersist;
    }

    public STStorageSession getSession() {
        return simplePersist.getCurrentSession();
    }
}
