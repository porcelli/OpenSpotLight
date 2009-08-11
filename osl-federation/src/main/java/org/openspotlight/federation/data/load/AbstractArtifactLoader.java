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
import static org.openspotlight.common.util.Exceptions.catchAndLog;
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

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.CustomArtifact;
import org.openspotlight.federation.data.impl.Excluded;
import org.openspotlight.federation.data.impl.Included;
import org.openspotlight.federation.data.impl.StreamArtifact;

/**
 * The AbstractArtifactLoader class is itself a {@link ArtifactLoader} that do
 * the common stuff such as filtering artifacts before processing them or
 * creating the sha-1 key for the content. This is working now as a multi
 * threaded artifact loader also.
 * 
 * There's a {@link Map} with {@link String} keys and {@link Object} values
 * passed as an argument on the methods
 * {@link #getAllArtifactNames(Bundle, ArtifactMapping, Map)} and
 * {@link #loadArtifact(Bundle, ArtifactMapping, String, Map)}. This {@link Map}
 * is used to get a more "functional" approach when loading artifacts. To do a
 * loading in a thread safe way, it's secure to do not use instance variables.
 * So this {@link Map} is shared in a single invocation of the loading methods.
 * Its safe to put anything in this cache. An example: To get all the artifact
 * names should have a massive IO and should be better to get all content. So,
 * its just to fill the cache and use it later on the
 * {@link #loadArtifact(Bundle, ArtifactMapping, String, Map)} method.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 * 
 */
public abstract class AbstractArtifactLoader implements ArtifactLoader {
    
    /**
     * Return type for method
     * {@link AbstractArtifactLoader#createErrorHandler()}. This class will
     * receive callbacks on each error during the artifact loading process.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    protected interface ArtifactErrorHandler {
        
        /**
         * An fatal error happened and the artifact processing was interrupted.
         * 
         * @param bundle
         * @param e
         */
        public void fatalErrorHappened(final Bundle bundle, Exception e);
        
        /**
         * An erro processing one artifact just happened, but it will not stop
         * the artifact processing.
         * 
         * @param <E>
         * @param bundle
         * @param mapping
         * @param nameToProcess
         * @param exception
         */
        public <E extends Exception> void handleError(final Bundle bundle,
                final ArtifactMapping mapping, final String nameToProcess,
                E exception);
    }
    
    /**
     * An {@link ArtifactErrorHandler} just to log the errors.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    private final static class DefaultErrorHandler implements
            ArtifactErrorHandler {
        /**
         * 
         * {@inheritDoc}
         */
        public void fatalErrorHappened(final Bundle bundle, final Exception e) {
            catchAndLog(e);
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public <E extends Exception> void handleError(final Bundle bundle,
                final ArtifactMapping mapping, final String nameToProcess,
                final E exception) {
            catchAndLog(exception);
        }
        
    }
    
    /**
     * A {@link GlobalExecutionContext} to serve as a default implementation.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    private final static class DefaultGlobalExecutionContext implements
            GlobalExecutionContext {
        /**
         * 
         * {@inheritDoc}
         */
        public void globalExecutionAboutToStart(final Bundle bundle) {
            // nothign to do here
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void globalExecutionFinished(final Bundle bundle) {
            // nothign to do here
            
        }
    }
    
    /**
     * A {@link ThreadExecutionContext} to serve as a default implementation.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    private final static class DefaultThreadExecutionContext implements
            ThreadExecutionContext {
        /**
         * 
         * {@inheritDoc}
         */
        public void threadExecutionAboutToStart(final Bundle bundle,
                final ArtifactMapping mapping) {
            // nothign to do here
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void threadExecutionFinished(final Bundle bundle,
                final ArtifactMapping mapping) {
            // nothign to do here
        }
    }
    
    /**
     * Durring the artifact processing, there cold exists some resources to
     * initialize durring the global processing phases. A single instance of
     * this class will be created and it will be shared by all threads and calls
     * during the artifact loading process.
     * 
     * This interface is the return type of method
     * {@link AbstractArtifactLoader#createGlobalExecutionContext()}
     * 
     * If single thread resources initialization is needed, use the
     * {@link ThreadExecutionContext} instead.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    @NotThreadSafe
    protected static interface GlobalExecutionContext {
        /**
         * This method will be called when the artifact processing is about to
         * start.
         * 
         * @param bundle
         */
        public void globalExecutionAboutToStart(final Bundle bundle);
        
        /**
         * This method will be called when all artifacts are processed.
         * 
         * @param bundle
         */
        public void globalExecutionFinished(final Bundle bundle);
        
    }
    
    /**
     * Durring the artifact processing, there cold exists some resources to
     * initialize durring the starting and stopping phases of a single thread. A
     * single instance of this class will be created for each thread and it
     * won't be shared by all threads. The same {@link ThreadExecutionContext}
     * will be shared by various artifact loading invocation on a single thread.
     * 
     * This interface is the return type of method
     * {@link AbstractArtifactLoader#createThreadExecutionContext()}
     * 
     * If global resources initialization is needed, use the
     * {@link GlobalExecutionContext} instead.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    @ThreadSafe
    protected static interface ThreadExecutionContext {
        /**
         * This thread will start its execution.
         * 
         * @param bundle
         * @param mapping
         */
        public void threadExecutionAboutToStart(final Bundle bundle,
                final ArtifactMapping mapping);
        
        /**
         * This thread just ended its execution.
         * 
         * @param bundle
         * @param mapping
         */
        public void threadExecutionFinished(final Bundle bundle,
                final ArtifactMapping mapping);
        
    }
    
    /**
     * Worker class for loading artifacts
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    private final class Worker implements Callable<Void> {
        final Bundle bundle;
        final AtomicInteger errorCounter;
        final AtomicInteger loadCounter;
        final ArtifactMapping mapping;
        final Set<String> namesToProcess;
        final GlobalExecutionContext globalContext;
        final ArtifactErrorHandler errorHandler;
        
        /**
         * Constructor to initialize all final and mandatory fields.
         * 
         * @param bundle
         * @param errorCounter
         * @param loadCounter
         * @param mapping
         * @param namesToProcess
         * @param globalContext
         * @param errorHandler
         */
        public Worker(final Bundle bundle, final AtomicInteger errorCounter,
                final AtomicInteger loadCounter, final ArtifactMapping mapping,
                final Set<String> namesToProcess,
                final GlobalExecutionContext globalContext,
                final ArtifactErrorHandler errorHandler) {
            super();
            this.bundle = bundle;
            this.errorCounter = errorCounter;
            this.loadCounter = loadCounter;
            this.mapping = mapping;
            this.namesToProcess = namesToProcess;
            this.globalContext = globalContext;
            this.errorHandler = errorHandler;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public Void call() throws Exception {
            
            AbstractArtifactLoader.this.loadArtifactsOfNames(this.bundle,
                    this.errorCounter, this.loadCounter, this.mapping,
                    this.namesToProcess, this.globalContext, this.errorHandler);
            return null;
        }
        
    }
    
    private static final ThreadExecutionContext DEFAULT_THREAD_CONTEXT = new DefaultThreadExecutionContext();;
    
    private static final GlobalExecutionContext DEFAULT_GLOBAL_CONTEXT = new DefaultGlobalExecutionContext();;
    
    private static final ArtifactErrorHandler DEFAULT_ERROR_HANDLER = new DefaultErrorHandler();
    
    /**
     * Default constructor
     */
    public AbstractArtifactLoader() {
        //
    }
    
    /**
     * Overwrite this method to create a custom {@link ArtifactErrorHandler} to
     * be used on a {@link AbstractArtifactLoader} extended class.
     * 
     * 
     * @return an {@link ArtifactErrorHandler}
     */
    protected ArtifactErrorHandler createErrorHandler() {
        return DEFAULT_ERROR_HANDLER;
    }
    
    /**
     * Overwrite this method to create a custom {@link GlobalExecutionContext}
     * to be used on a {@link AbstractArtifactLoader} extended class.
     * 
     * 
     * @return an {@link ArtifactErrorHandler}
     */
    protected GlobalExecutionContext createGlobalExecutionContext() {
        return DEFAULT_GLOBAL_CONTEXT;
    }
    
    /**
     * Overwrite this method to create a custom {@link ThreadExecutionContext}
     * to be used on a {@link AbstractArtifactLoader} extended class.
     * 
     * 
     * @return an {@link ArtifactErrorHandler}
     */
    protected ThreadExecutionContext createThreadExecutionContext() {
        return DEFAULT_THREAD_CONTEXT;
    }
    
    /**
     * The implementation class needs to load all the possible artifact names
     * without filtering this.This method is "mono threaded", so it's not
     * dangerous to do something non multi-threaded here.
     * 
     * @param bundle
     * @param mapping
     * @param cachedInformation
     *            could be used for cache purposes
     * @return
     * @throws ConfigurationException
     */
    protected abstract Set<String> getAllArtifactNames(Bundle bundle,
            ArtifactMapping mapping, GlobalExecutionContext context)
            throws ConfigurationException;
    
    /**
     * This method should do the loading processing itself. It will be done on a
     * multi threaded execution. This will be called in a lazy way.
     * 
     * 
     * @param bundle
     * @param mapping
     * @param artifactName
     * @param globalContext
     * @param localContext
     * @return the bytes from a given artifact name
     * @throws Exception
     */
    protected abstract byte[] loadArtifact(Bundle bundle,
            ArtifactMapping mapping, String artifactName,
            GlobalExecutionContext globalContext,
            ThreadExecutionContext localContext) throws Exception;
    
    /**
     * Filter the included and excluded patterns and also creates each artifact
     * and calculates the sha-1 key for the content. In this method we have also
     * the logic for dividing the tasks between
     * {@link Configuration#getNumberOfParallelThreads() the number of parallel
     * threads}.
     * 
     * @param bundle
     * @return a {@link ArtifactLoader.ArtifactProcessingCount} with statistical
     *         data
     * @throws ConfigurationException
     */
    @SuppressWarnings("boxing")
    public final ArtifactProcessingCount loadArtifactsFromMappings(
            final Bundle bundle) throws ConfigurationException {
        
        checkNotNull("bundle", bundle); //$NON-NLS-1$
        final ArtifactErrorHandler errorHandler = this.createErrorHandler();
        final GlobalExecutionContext globalContext = this
                .createGlobalExecutionContext();
        final AtomicInteger errorCounter = new AtomicInteger();
        final AtomicInteger loadCounter = new AtomicInteger();
        int ignoreCount = 0;
        
        try {
            globalContext.globalExecutionAboutToStart(bundle);
            final List<Callable<Void>> workers = new ArrayList<Callable<Void>>();
            final Set<String> includedPatterns = new HashSet<String>();
            final Set<String> excludedPatterns = new HashSet<String>();
            for (final ArtifactMapping mapping : bundle.getArtifactMappings()) {
                for (final Included included : mapping.getIncludeds()) {
                    includedPatterns.add(included.getName());
                }
                for (final Excluded excluded : mapping.getExcludeds()) {
                    excludedPatterns.add(excluded.getName());
                }
                final Set<String> namesToFilter = this.getAllArtifactNames(
                        bundle, mapping, globalContext);
                final FilterResult innerResult = filterNamesByPattern(
                        namesToFilter, includedPatterns, excludedPatterns,
                        false);
                final Set<String> namesToProcess = innerResult
                        .getIncludedNames();
                final Set<String> names = new HashSet<String>(bundle
                        .getStreamArtifactNames());
                for (final String name : names) {
                    if (!namesToProcess.contains(name)) {
                        final StreamArtifact artifactToDelete = bundle
                                .getStreamArtifactByName(name);
                        bundle.removeStreamArtifact(artifactToDelete);
                    }
                }
                for (final String name : bundle.getCustomArtifactNames()) {
                    if (!namesToProcess.contains(name)) {
                        final CustomArtifact artifactToDelete = bundle
                                .getCustomArtifactByName(name);
                        bundle.removeCustomArtifact(artifactToDelete);
                    }
                }
                ignoreCount += innerResult.getIgnoredNames().size();
                
                this.splitWorkBetweenThreads(bundle, errorCounter, loadCounter,
                        mapping, namesToProcess, workers, globalContext,
                        errorHandler);
                
            }
            try {
                final ExecutorService executor = Executors
                        .newFixedThreadPool(bundle.getRepository()
                                .getConfiguration()
                                .getNumberOfParallelThreads());
                
                executor.invokeAll(workers);
                while (executor.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                    this.wait();
                }
                executor.shutdown();
            } catch (final Exception e) {
                errorHandler.fatalErrorHappened(bundle, e);
                throw logAndReturnNew(e, ConfigurationException.class);
            }
            
        } finally {
            globalContext.globalExecutionFinished(bundle);
        }
        return new ArtifactProcessingCount(loadCounter.get(), ignoreCount,
                errorCounter.get());
    }
    
    /**
     * This method will be called by multi threads to load all artifacts of the
     * given names.
     * 
     * @param bundle
     * @param errorCounter
     * @param loadCounter
     * @param mapping
     * @param namesToProcess
     * @param globalContext
     * @param errorHandler
     */
    final void loadArtifactsOfNames(final Bundle bundle,
            final AtomicInteger errorCounter, final AtomicInteger loadCounter,
            final ArtifactMapping mapping, final Set<String> namesToProcess,
            final GlobalExecutionContext globalContext,
            final ArtifactErrorHandler errorHandler) {
        final ThreadExecutionContext localContext = this
                .createThreadExecutionContext();
        
        localContext.threadExecutionAboutToStart(bundle, mapping);
        try {
            for (final String artifactName : namesToProcess) {
                try {
                    
                    final byte[] content = this.loadArtifact(bundle, mapping,
                            artifactName, globalContext, localContext);
                    final String sha1 = getSha1SignatureEncodedAsBase64(content);
                    final InputStream is = new ByteArrayInputStream(content);
                    final StreamArtifact artifact = bundle
                            .addStreamArtifact(artifactName);
                    artifact.setData(is);
                    artifact.setDataSha1(sha1);
                    loadCounter.incrementAndGet();
                } catch (final Exception e) {
                    errorHandler.handleError(bundle, mapping, artifactName, e);
                    errorCounter.incrementAndGet();
                }
            }
        } finally {
            localContext.threadExecutionFinished(bundle, mapping);
        }
    }
    
    /**
     * Here all work of loading artifacts that needs to be done will be shared
     * by the configured number of threads. This method also will split the
     * threads without mixing diferent {@link ArtifactMapping artifact mappings}
     * .
     * 
     * @param bundle
     * @param errorCounter
     * @param loadCounter
     * @param mapping
     * @param namesToProcess
     * @param workers
     * @param globalContext
     * @param errorHandler
     * @throws ConfigurationException
     */
    @SuppressWarnings("boxing")
    private void splitWorkBetweenThreads(final Bundle bundle,
            final AtomicInteger errorCounter, final AtomicInteger loadCounter,
            final ArtifactMapping mapping, final Set<String> namesToProcess,
            final List<Callable<Void>> workers,
            final GlobalExecutionContext globalContext,
            final ArtifactErrorHandler errorHandler)
            throws ConfigurationException {
        final int numberOfThreads = bundle.getRepository().getConfiguration()
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
                    mapping, entry.getValue(), globalContext, errorHandler);
            workers.add(w);
        }
        
    }
}
