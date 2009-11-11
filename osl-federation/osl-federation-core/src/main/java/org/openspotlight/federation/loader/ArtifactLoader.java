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

package org.openspotlight.federation.loader;

import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspotlight.common.Disposable;
import org.openspotlight.common.Pair;
import org.openspotlight.common.Triple;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactMapping;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Configuration;
import org.openspotlight.federation.finder.ArtifactFinder;

public interface ArtifactLoader extends Disposable {

    public static enum ArtifactLoaderBehavior {
        ONE_LOADER_PER_SOURCE,
        MULTIPLE_LOADERS_PER_SOURCE
    }

    public static class Factory {

        private static class ArtifactLoaderImpl implements ArtifactLoader {
            private final Configuration          configuration;
            private final ArtifactLoaderBehavior behavior;
            private final ArtifactFinder<?>[]    artifactFinders;

            private final long                   sleepTime;

            private ExecutorService              executor;

            public ArtifactLoaderImpl(
                                       final Configuration configuration, final ArtifactLoaderBehavior behavior,
                                       final ArtifactFinder<?>... artifactFinders ) {
                this.configuration = configuration;
                this.behavior = behavior;
                this.artifactFinders = artifactFinders;
                this.sleepTime = configuration.getDefaultSleepingIntervalInMilliseconds();
            }

            public void closeResources() {
                try {
                    this.executor.shutdown();
                } catch (final Exception e) {
                    Exceptions.catchAndLog(e);
                }
                for (final ArtifactFinder<?> finder : this.artifactFinders) {
                    try {
                        finder.closeResources();
                    } catch (final Exception e) {
                        Exceptions.catchAndLog(e);
                    }
                }
            }

            public Iterable<Artifact> loadArtifactsFromSource( final ArtifactSource... sources ) {
                final Queue<Pair<ArtifactFinder<?>, ArtifactSource>> sourcesToLoad = new ConcurrentLinkedQueue<Pair<ArtifactFinder<?>, ArtifactSource>>();
                final Queue<Triple<ArtifactFinder<?>, ArtifactSource, String>> sourcesToProcess = new ConcurrentLinkedQueue<Triple<ArtifactFinder<?>, ArtifactSource, String>>();
                final Queue<Artifact> loadedArtifacts = new ConcurrentLinkedQueue<Artifact>();

                addingSources: for (final ArtifactSource source : sources) {
                    boolean hasAnyFinder = false;
                    for (final ArtifactFinder<?> finder : this.artifactFinders) {
                        if (finder.canAcceptArtifactSource(source)) {
                            sourcesToLoad.add(new Pair<ArtifactFinder<?>, ArtifactSource>(finder, source));
                            hasAnyFinder = true;
                            if (ArtifactLoaderBehavior.ONE_LOADER_PER_SOURCE.equals(this.behavior)) {
                                continue addingSources;
                            }
                        }
                    }
                    if (hasAnyFinder) {
                        Exceptions.logAndThrow(new IllegalStateException("No configured artifact finder to process source "
                                                                         + source));
                    }
                }
                for (final Pair<ArtifactFinder<?>, ArtifactSource> pair : new ArrayList<Pair<ArtifactFinder<?>, ArtifactSource>>(
                                                                                                                                 sourcesToLoad)) {
                    this.executor.execute(new Runnable() {

                        public void run() {

                            for (final ArtifactMapping mapping : pair.getK2().getMappings()) {
                                final Set<String> rawNames = pair.getK1().retrieveAllArtifactNames(pair.getK2(),
                                                                                                   mapping.getRelative());
                                final FilterResult newNames = filterNamesByPattern(rawNames, mapping.getIncludeds(),
                                                                                   mapping.getExcludeds(), false);

                                for (final String name : newNames.getIncludedNames()) {

                                    sourcesToProcess.add(new Triple<ArtifactFinder<?>, ArtifactSource, String>(pair.getK1(),
                                                                                                               pair.getK2(), name));
                                    sourcesToLoad.remove(pair);
                                }
                            }
                        }
                    });
                }
                while (sourcesToLoad.size() != 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (final InterruptedException e) {

                    }
                }
                for (final Triple<ArtifactFinder<?>, ArtifactSource, String> triple : new ArrayList<Triple<ArtifactFinder<?>, ArtifactSource, String>>(
                                                                                                                                                       sourcesToProcess)) {
                    this.executor.execute(new Runnable() {

                        public void run() {
                            final Artifact loaded = triple.getK1().findByPath(triple.getK2(), triple.getK3());
                            loadedArtifacts.add(loaded);
                            sourcesToProcess.remove(triple);
                        }
                    });
                }
                while (sourcesToProcess.size() != 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (final InterruptedException e) {

                    }
                }
                return new ArrayList<Artifact>(loadedArtifacts);
            }

            public synchronized void setup() {
                this.executor = Executors.newFixedThreadPool(configuration.getNumberOfParallelThreads());
            }
        }

        public static ArtifactLoader createNewLoader( final Configuration configuration,
                                                      final ArtifactLoaderBehavior behavior,
                                                      final ArtifactFinder<?>... artifactFinders ) {
            final ArtifactLoaderImpl loader = new ArtifactLoaderImpl(configuration, behavior, artifactFinders);
            loader.setup();
            return loader;
        }

        private Factory() {
        }

    }

    public void closeResources();

    public Iterable<Artifact> loadArtifactsFromSource( ArtifactSource... source );

}
