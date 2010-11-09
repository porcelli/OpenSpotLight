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
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.federation.loader.MutableConfigurationManager;
import org.openspotlight.graph.GraphSessionFactory;
import org.openspotlight.guice.ThreadLocalProvider;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.StorageSession;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Created by IntelliJ IDEA. User: feu Date: Sep 30, 2010 Time: 11:42:35 AM To change this template use File | Settings | File
 * Templates.
 */
public class DefaultExecutionContextFactory extends ThreadLocalProvider<ExecutionContext> implements ExecutionContextFactory {
    private final MutableConfigurationManager                     configurationManager;

    private final GraphSessionFactory                             graphSessionFactory;

    private final Iterable<Class<? extends OriginArtifactLoader>> loaderRegistry;

    private final PersistentArtifactManagerProvider               persistentArtifactManagerProvider;

    private final Provider<StorageSession>                        sessionProvider;

    private final SimplePersistFactory                            simplePersistFactory;

    @Inject
    public DefaultExecutionContextFactory(
                                          final Provider<StorageSession> sessionProvider,
                                          final GraphSessionFactory graphSessionFactory,
                                          final SimplePersistFactory simplePersistFactory,
                                          final PersistentArtifactManagerProvider persistentArtifactManagerProvider,
                                          final MutableConfigurationManager configurationManager,
                                          @ArtifactLoaderRegistry final Iterable<Class<? extends OriginArtifactLoader>> loaderRegistry) {
        this.sessionProvider = sessionProvider;
        this.graphSessionFactory = graphSessionFactory;
        this.simplePersistFactory = simplePersistFactory;
        this.persistentArtifactManagerProvider = persistentArtifactManagerProvider;
        this.configurationManager = configurationManager;
        this.loaderRegistry = loaderRegistry;
    }

    public static void closeResourcesIfNeeded(final Object o) {
        if (o instanceof Disposable) {
            ((Disposable) o).closeResources();
        }
    }

    @Override
    protected ExecutionContext createInstance() {
        return new DefaultExecutionContext(sessionProvider,
                    graphSessionFactory, simplePersistFactory,
                    persistentArtifactManagerProvider, configurationManager,
                    loaderRegistry);
    }

    @Override
    public void closeResources() {
        closeResourcesIfNeeded(sessionProvider);
        closeResourcesIfNeeded(graphSessionFactory);
        closeResourcesIfNeeded(simplePersistFactory);
        closeResourcesIfNeeded(persistentArtifactManagerProvider);
        closeResourcesIfNeeded(configurationManager);

    }

}
