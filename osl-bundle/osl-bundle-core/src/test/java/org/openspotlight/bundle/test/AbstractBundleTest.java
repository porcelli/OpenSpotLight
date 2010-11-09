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
package org.openspotlight.bundle.test;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.openspotlight.bundle.context.ExecutionContextModule;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.Scheduler;
import org.openspotlight.bundle.scheduler.SchedulerModule;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.domain.Schedulable;
import org.openspotlight.federation.finder.FileSystemOriginArtifactLoader;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.loader.PersistentConfigurationManagerModule;
import org.openspotlight.graph.GraphModule;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 5, 2010 Time: 2:55:10 PM To change this template use File | Settings | File
 * Templates.
 */
public abstract class AbstractBundleTest {

    private ArtifactSource artifactSource;

    private Group          group;

    private Repository     repository;

    private Scheduler      scheduler;

    protected Injector createInjector() {
        final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap = newHashMap();
        //                    schedulableMap.put(Group.class, SampleGroupSchedulableCommand.class);
        final List<Class<? extends OriginArtifactLoader>> loaderRegistry = newArrayList();
        loaderRegistry.add(FileSystemOriginArtifactLoader.class);
        final Injector injector = Guice.createInjector(
                            new SchedulerModule(schedulableMap), new ExecutionContextModule(loaderRegistry),
                            new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                                    ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                            new PersistentConfigurationManagerModule(),
                            new SimplePersistModule(),
                            new GraphModule());

        return injector;
    }

    public abstract Repository createRepository();

    public ArtifactSource getArtifactSource() {
        return artifactSource;
    }

    public Group getGroup() {
        return group;
    }

    public Repository getRepository() {
        return repository;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    @Before
    public void setup()
        throws Exception {

        final Injector injector = createInjector();

        repository = createRepository();
        group = repository.getGroups().iterator().next();
        artifactSource = group.getArtifactSources().iterator().next();
        scheduler = injector.getInstance(Scheduler.class);

        ExampleExecutionHistory.resetData();

    }

}
