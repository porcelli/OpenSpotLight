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

package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Excluded;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;

/**
 * The AbstractArtifactLoader class is itself a {@link ArtifactLoader} that do
 * the common stuff such as filtering artifacts before processing them or
 * creating the sha-1 key for the content. This is working now as a multi
 * threaded artifact loader also.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public abstract class AbstractArtifactLoader implements ArtifactLoader {
    /**
     * Worker class for loading artifacts
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    class Worker implements Callable<Void> {
        final Bundle bundle;
        final AtomicInteger errorCounter;
        final AtomicInteger loadCounter;
        final ArtifactMapping mapping;
        final Set<String> namesToProcess;
        
        /**
         * All parameters are mandatory
         * 
         * @param bundle
         * @param errorCounter
         * @param loadCounter
         * @param mapping
         * @param namesToProcess
         */
        public Worker(final Bundle bundle, final AtomicInteger errorCounter,
                final AtomicInteger loadCounter, final ArtifactMapping mapping,
                final Set<String> namesToProcess) {
            super();
            this.bundle = bundle;
            this.errorCounter = errorCounter;
            this.loadCounter = loadCounter;
            this.mapping = mapping;
            this.namesToProcess = namesToProcess;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public Void call() throws Exception {
            AbstractArtifactLoader.this.loadArtifact(this.bundle,
                    this.errorCounter, this.loadCounter, this.mapping,
                    this.namesToProcess);
            return null;
        }
        
    }
    
    /**
     * Executor for this loader.
     */
    private final ExecutorService executor;
    
    /**
     * Default constructor
     */
    public AbstractArtifactLoader() {
        final int numberOfThreads = this.numberOfParallelThreads();
        this.executor = Executors.newFixedThreadPool(numberOfThreads);
    }
    
    /**
     * The implementation class needs to load all the possible artifact names
     * without filtering this.
     * 
     * @param bundle
     * @param mapping
     * @return
     * @throws ConfigurationException
     */
    protected abstract Set<String> getAllArtifactNames(Bundle bundle,
            ArtifactMapping mapping) throws ConfigurationException;
    
    /**
     * This method loads an artifact using its names
     * 
     * @param bundle
     * @param mapping
     * @param artifactName
     * @return
     * @throws Exception
     */
    protected abstract byte[] loadArtifact(Bundle bundle,
            ArtifactMapping mapping, String artifactName) throws Exception;
    
    /**
     * Mehtod to be used by the {@link Worker}.
     * 
     * @param bundle
     * @param errorCounter
     * @param loadCounter
     * @param mapping
     * @param namesToProcess
     */
    void loadArtifact(final Bundle bundle, final AtomicInteger errorCounter,
            final AtomicInteger loadCounter, final ArtifactMapping mapping,
            final Set<String> namesToProcess) {
        for (final String artifactName : namesToProcess) {
            try {
                final byte[] content = this.loadArtifact(bundle, mapping,
                        artifactName);
                final String sha1 = getSha1SignatureEncodedAsBase64(content);
                final InputStream is = new ByteArrayInputStream(content);
                final StreamArtifact artifact = bundle
                        .addStreamArtifact(artifactName);
                artifact.setData(is);
                artifact.setDataSha1(sha1);
                loadCounter.incrementAndGet();
            } catch (final Exception e) {
                errorCounter.incrementAndGet();
            }
        }
    }
    
    /**
     * Filter the included and excluded patterns and also creates each artifact
     * and calculates the sha-1 key for the content. In this method we have also
     * the logic for dividing the tasks between
     * {@link Repository#getNumberOfParallelThreads() the number of parallel
     * threads}.
     * 
     * @param bundle
     * @return a {@link ArtifactLoader.ArtifactProcessingCount} with statistical
     *         data
     * @throws ConfigurationException
     */
    public ArtifactProcessingCount loadArtifactsFromMappings(final Bundle bundle)
            throws ConfigurationException {
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        final AtomicInteger errorCounter = new AtomicInteger();
        final AtomicInteger loadCounter = new AtomicInteger();
        final List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
        final Set<String> includedPatterns = new HashSet<String>();
        final Set<String> excludedPatterns = new HashSet<String>();
        int ignoreCount = 0;
        for (final ArtifactMapping mapping : bundle.getArtifactMappings()) {
            for (final Included included : mapping.getIncludeds()) {
                includedPatterns.add(included.getName());
            }
            for (final Excluded excluded : mapping.getExcludeds()) {
                excludedPatterns.add(excluded.getName());
            }
            final Set<String> namesToFilter = this.getAllArtifactNames(bundle,
                    mapping);
            final FilterResult innerResult = filterNamesByPattern(
                    namesToFilter, includedPatterns, excludedPatterns, false);
            final Set<String> namesToProcess = innerResult.getIncludedNames();
            ignoreCount += innerResult.getIgnoredNames().size();
            
            this.splitWorkBetweenThreads(bundle, errorCounter, loadCounter,
                    mapping, namesToProcess, workers);
            
        }
        try {
            this.executor.invokeAll(workers);
            while (this.executor.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                this.wait();
            }
        } catch (final InterruptedException e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
        return new ArtifactProcessingCount(loadCounter.get(), ignoreCount,
                errorCounter.get());
    }
    
    /**
     * 
     * @return the best number of parallel threads for this artifact loader
     */
    protected abstract int numberOfParallelThreads();
    
    /**
     * This method splits all job between
     * {@link Repository#getNumberOfParallelThreads() the number of parallel
     * threads}.
     * 
     * @param bundle
     * @param errorCounter
     * @param loadCounter
     * @param mapping
     * @param namesToProcess
     * @throws ConfigurationException
     */
    @SuppressWarnings("boxing")
    private void splitWorkBetweenThreads(final Bundle bundle,
            final AtomicInteger errorCounter, final AtomicInteger loadCounter,
            final ArtifactMapping mapping, final Set<String> namesToProcess,
            final List<Callable<Void>> workers) throws ConfigurationException {
        final int numberOfThreads = bundle.getRepository()
                .getNumberOfParallelThreads();
        final int allNamesToProcessSize = namesToProcess.size();
        final int numberOfNamesPerThread = allNamesToProcessSize
                / numberOfThreads;
        final Map<Integer, Set<String>> listsForThreads = new HashMap<Integer, Set<String>>();
        final Stack<String> nameStack = new Stack<String>();
        nameStack.addAll(namesToProcess);
        for (int i = 0, last = numberOfThreads - 1; i < numberOfThreads; i++) {
            final Set<String> names = new HashSet<String>();
            listsForThreads.put(i, names);
            for (int j = 0; j < numberOfNamesPerThread; j++) {
                names.add(nameStack.pop());
            }
            if (i == last) {
                while (!nameStack.isEmpty()) {
                    names.add(nameStack.pop());
                }
            }
        }
        
        for (final Map.Entry<Integer, Set<String>> entry : listsForThreads
                .entrySet()) {
            final Worker w = new Worker(bundle, errorCounter, loadCounter,
                    mapping, entry.getValue());
            workers.add(w);
        }
        
    }
}
