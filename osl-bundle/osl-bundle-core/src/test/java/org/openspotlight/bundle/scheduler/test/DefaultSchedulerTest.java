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
package org.openspotlight.bundle.scheduler.test;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.number.IsCloseTo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.context.ExecutionContextModule;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.Scheduler;
import org.openspotlight.bundle.scheduler.SchedulerModule;
import org.openspotlight.bundle.scheduler.SchedulerTask;
import org.openspotlight.common.util.Files;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.GlobalSettings;
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

public class DefaultSchedulerTest {
    public static class SampleGroupSchedulableCommand implements SchedulableTaskFactory<Group> {
        public static AtomicInteger counter     = new AtomicInteger(0);
        public static AtomicBoolean wasExecuted = new AtomicBoolean(false);

        @Override
        public SchedulerTask[] createTasks(final Group schedulable, final ExecutionContextFactory factory) {
            return TaskSupport.wrapTask(new SchedulerTask() {

                @Override
                public Void call()
                    throws Exception {
                    wasExecuted.set(true);
                    counter.incrementAndGet();
                    return null;
                }

                @Override
                public String getUniqueJobId() {
                    return schedulable.getUniqueName();
                }
            });
        }
    }

    private ArrayList<Repository> repositories;
    private boolean               runned = false;

    private Scheduler             scheduler;

    private GlobalSettings        settings;

    public void resetStatus() {
        SampleGroupSchedulableCommand.wasExecuted.set(false);
        SampleGroupSchedulableCommand.counter.set(0);
        scheduler.startScheduler();
        System.err.println(">>> before");

    }

    @Before
    public void setupScheduler() {

        if (!runned) {
            final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap = newHashMap();
            schedulableMap.put(Group.class, SampleGroupSchedulableCommand.class);
            final List<Class<? extends OriginArtifactLoader>> loaderRegistry = newArrayList();
            loaderRegistry.add(FileSystemOriginArtifactLoader.class);
            final Injector injector = Guice.createInjector(
                    new SchedulerModule(schedulableMap), new ExecutionContextModule(loaderRegistry),
                    new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                            ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                    new PersistentConfigurationManagerModule(),
                    new SimplePersistModule(),
                    new GraphModule());

            final ArtifactSource source = new ArtifactSource();
            final String initialRawPath = Files.getNormalizedFileName(new File(".."));
            final String initial = initialRawPath.substring(0, initialRawPath.lastIndexOf('/'));
            source.setActive(true);
            source.setInitialLookup(initial);
            source.setName("sourceName");
            repositories = new ArrayList<Repository>();
            scheduler = injector.getInstance(Scheduler.class);

            settings = new GlobalSettings();
            final Repository repository = new Repository();
            repositories.add(repository);
            repository.setActive(true);
            repository.setName("repository");
            source.setRepository(repository);

            final Group group = new Group();
            group.setActive(true);
            group.setName("new group");
            group.setRepository(repository);
            group.setType("types");
            repository.getGroups().add(group);
            scheduler.refreshJobs(settings, repositories);
            scheduler.startScheduler();
            System.err.println(">>> started");

            runned = true;
        }
        resetStatus();

    }

    @Test
    public void shouldStartCronJobs()
        throws Exception {
        final Group group = repositories.iterator().next().getGroups().iterator().next();
        try {
            group.getCronInformation().add("0/1 * * * * ?");
            scheduler.refreshJobs(settings, repositories);
            Thread.sleep(3000);
            Assert.assertThat((double) SampleGroupSchedulableCommand.counter.get(), IsCloseTo.closeTo(4d, 1d));

        } finally {
            group.getCronInformation().clear();
            scheduler.refreshJobs(settings, repositories);
        }
    }

    @Test
    public void shouldStartImediateJob()
        throws Exception {
        System.err.println("method");

        scheduler.fireSchedulable("username", "password", repositories.iterator().next().getGroups().iterator().next());
        for (int i = 0; i < 20; i++) {
            if (SampleGroupSchedulableCommand.wasExecuted.get()) { return; }
            Thread.sleep(100);
        }
        Assert.fail("Didn't execute in 20 seconds!");
    }

}
