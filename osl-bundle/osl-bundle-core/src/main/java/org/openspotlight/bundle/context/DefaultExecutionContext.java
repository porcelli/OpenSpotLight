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
package org.openspotlight.bundle.context;

import org.openspotlight.bundle.annotation.ArtifactLoaderRegistry;
import org.openspotlight.common.Disposable;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.federation.loader.MutableConfigurationManager;
import org.openspotlight.graph.FullGraphSession;
import org.openspotlight.graph.GraphSessionFactory;
import org.openspotlight.graph.SimpleGraphSession;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This class is an {@link ExecutionContext} which initialize all resources in a lazy way, and also close it in a lazy way also.
 * 
 * @author feu
 */
public class DefaultExecutionContext implements ExecutionContext {

    private final Iterable<Class<? extends OriginArtifactLoader>> loaderRegistry;

    @Inject
    public DefaultExecutionContext(Provider<StorageSession> sessionProvider,
                                   GraphSessionFactory graphSessionFactory,
                                   SimplePersistFactory simplePersistFactory,
                                   PersistentArtifactManagerProvider persistentArtifactManagerProvider,
                                   MutableConfigurationManager configurationManager,
                                   @ArtifactLoaderRegistry Iterable<Class<? extends OriginArtifactLoader>> loaderRegistry) {
        this.sessionProvider = sessionProvider;
        this.graphSessionFactory = graphSessionFactory;
        this.simplePersistFactory = simplePersistFactory;
        this.persistentArtifactManagerProvider = persistentArtifactManagerProvider;
        this.configurationManager = configurationManager;
        this.loaderRegistry = loaderRegistry;

    }

    public Iterable<Class<? extends OriginArtifactLoader>> getLoaderRegistry() {
        return loaderRegistry;
    }

    private final Provider<StorageSession>          sessionProvider;

    private final GraphSessionFactory               graphSessionFactory;

    private final SimplePersistFactory              simplePersistFactory;

    private final PersistentArtifactManagerProvider persistentArtifactManagerProvider;

    private SimpleGraphSession                      openedSimpleGraphSession = null;

    private FullGraphSession                        openedFullGraphSession   = null;

    private final MutableConfigurationManager       configurationManager;

    public static void closeResourcesIfNeeded(Object o) {
        if (o instanceof Disposable) {
            ((Disposable) o).closeResources();
        }
    }

    @Override
    public void closeResources() {
        closeResourcesIfNeeded(sessionProvider);
        closeResourcesIfNeeded(graphSessionFactory);
        closeResourcesIfNeeded(simplePersistFactory);
        closeResourcesIfNeeded(persistentArtifactManagerProvider);
        closeResourcesIfNeeded(openedFullGraphSession);
        closeResourcesIfNeeded(openedSimpleGraphSession);
        closeResourcesIfNeeded(configurationManager);

    }

    @Override
    public SimpleGraphSession openSimple() {
        if (openedSimpleGraphSession == null)
            openedSimpleGraphSession = graphSessionFactory.openSimple();
        return openedSimpleGraphSession;
    }

    @Override
    public FullGraphSession openFull() {
        if (openedFullGraphSession == null)
            openedFullGraphSession = graphSessionFactory.openFull();
        return openedFullGraphSession;
    }

    @Override
    public FullGraphSession openFull(String artifactId)
            throws IllegalArgumentException {
        return graphSessionFactory.openFull(artifactId);
    }

    @Override
    public PersistentArtifactManager getPersistentArtifactManager() {
        return persistentArtifactManagerProvider.get();
    }

    @Override
    public MutableConfigurationManager getDefaultConfigurationManager() {
        return configurationManager;
    }

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AuthenticatedUser getUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUserName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SimplePersistCapable<StorageNode, StorageSession> getSimplePersist(
                                                                              Partition partition) {
        return simplePersistFactory.createSimplePersist(partition);
    }

    @Override
    public SimplePersistFactory getSimplePersistFactory() {
        return simplePersistFactory;
    }

}
