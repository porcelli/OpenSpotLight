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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.federation.data.load;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

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
	protected final static class DefaultErrorHandler implements
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
	protected abstract static class DefaultGlobalExecutionContext implements
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

		/**
		 * Overwrite this method to change the thread pool size
		 * 
		 * @param bundle
		 * @return the thread pool size
		 */
		public Integer withThreadPoolSize(final Bundle bundle) {
			return bundle.getRepository().getConfiguration()
					.getNumberOfParallelThreads();
		}
	}

	/**
	 * A {@link ContentExecutionContext} to serve as a default implementation.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	protected abstract static class DefaultThreadExecutionContext implements
			ThreadExecutionContext {
		/**
		 * 
		 * {@inheritDoc}
		 */
		public void threadExecutionAboutToStart(final Bundle bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalExecutionContext) {
			// nothign to do here
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		public void threadExecutionFinished(final Bundle bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalExecutionContext) {
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
	 * {@link ContentExecutionContext} instead.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	@NotThreadSafe
	protected static interface GlobalExecutionContext {

		/**
		 * The implementation class needs to load all the possible artifact
		 * names without filtering this.This method is "mono threaded", so it's
		 * not dangerous to do something non multi-threaded here.
		 * 
		 * @param bundle
		 * @param mapping
		 * @param context
		 * @return all artifact names
		 * @throws ConfigurationException
		 */
		public abstract Set<String> getAllArtifactNames(Bundle bundle,
				ArtifactMapping mapping) throws ConfigurationException;

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

		/**
		 * Overwrite this method to change the thread pool size
		 * 
		 * @param bundle
		 * @return the thread pool size
		 */
		public Integer withThreadPoolSize(Bundle bundle);

	}

	/**
	 * Durring the artifact processing, there cold exists some resources to
	 * initialize durring the starting and stopping phases of a single thread. A
	 * single instance of this class will be created for each thread and it
	 * won't be shared by all threads. The same {@link ContentExecutionContext}
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
		 * This method should do the loading processing itself. It will be done
		 * on a multi threaded execution. This will be called in a lazy way.
		 * 
		 * In case of a null returning, the {@link ArtifactLoader} will consider
		 * that all the work to add a metadata on the repository is already
		 * done.
		 * 
		 * @param bundle
		 * @param mapping
		 * @param artifactName
		 * @param globalContext
		 * @param localContext
		 * @return the bytes from a given artifact name
		 * @throws Exception
		 */
		public byte[] loadArtifactOrReturnNullToIgnore(Bundle bundle,
				ArtifactMapping mapping, String artifactName,
				GlobalExecutionContext globalContext) throws Exception;

		/**
		 * This thread will start its execution.
		 * 
		 * @param bundle
		 * @param mapping
		 * @param globalContext
		 */
		public void threadExecutionAboutToStart(final Bundle bundle,
				final ArtifactMapping mapping,
				GlobalExecutionContext globalContext);

		/**
		 * This thread just ended its execution.
		 * 
		 * @param bundle
		 * @param mapping
		 * @param globalContext
		 */
		public void threadExecutionFinished(final Bundle bundle,
				final ArtifactMapping mapping,
				GlobalExecutionContext globalContext);

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
		final ArtifactErrorHandler errorHandler;
		final GlobalExecutionContext globalContext;
		final AtomicInteger loadCounter;
		final ArtifactMapping mapping;
		final Set<String> namesToProcess;

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
		 * {@inheritDoc}
		 */
		public Void call() {

			AbstractArtifactLoader.this.loadArtifactsOfNames(this.bundle,
					this.errorCounter, this.loadCounter, this.mapping,
					this.namesToProcess, this.globalContext, this.errorHandler);
			return null;
		}

	}

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
	protected abstract GlobalExecutionContext createGlobalExecutionContext();

	/**
	 * Overwrite this method to create a custom {@link ThreadExecutionContext}
	 * to be used on a {@link AbstractArtifactLoader} extended class.
	 * 
	 * 
	 * @return an {@link ArtifactErrorHandler}
	 */
	protected abstract ThreadExecutionContext createThreadExecutionContext();

	/**
	 * Some artifact loaders needs different behavior to create the mapping
	 * string to be used on pattern matchers. So, this method should be
	 * overwritten in this cases.
	 * 
	 * @param mapString
	 * @param bundle
	 * @param mapping
	 * @return new mapping string
	 */
	protected String fixMapping(final String mapString, final Bundle bundle,
			final ArtifactMapping mapping) {
		return mapString;
	}

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
			for (final ArtifactMapping mapping : bundle.getArtifactMappings()) {
				final String starting = mapping.getRelative();
				final Set<String> includedPatterns = new HashSet<String>();
				final Set<String> excludedPatterns = new HashSet<String>();
				for (final Included included : mapping.getIncludeds()) {
					final String newMapping = this.fixMapping(included
							.getName(), bundle, mapping);
					includedPatterns.add(newMapping);
				}
				for (final Excluded excluded : mapping.getExcludeds()) {
					final String newMapping = this.fixMapping(excluded
							.getName(), bundle, mapping);
					excludedPatterns.add(newMapping);
				}
				final Set<String> namesToFilter = globalContext
						.getAllArtifactNames(bundle, mapping);
				final FilterResult innerResult = filterNamesByPattern(
						namesToFilter, includedPatterns, excludedPatterns,
						false);
				final Set<String> namesToProcess = innerResult
						.getIncludedNames();
				final Set<String> names = new HashSet<String>(bundle
						.getStreamArtifactNames());
				for (final String name : names) {
					final String newName = name.startsWith(mapping
							.getRelative()) ? removeBegginingFrom(mapping
							.getRelative(), name) : name;
					if (!namesToProcess.contains(newName)
							&& !namesToProcess.contains(name)) {
						final StreamArtifact artifactToDelete = bundle
								.getStreamArtifactByName(name);
						bundle.markStreamArtifactAsRemoved(artifactToDelete);
					}
				}
				final Set<CustomArtifact> artifactsToRemove = new HashSet<CustomArtifact>();
				for (final String name : bundle.getCustomArtifactNames()) {
					boolean found = false;
					looking: for (final String nameToProcess : namesToProcess) {
						final String completeName = starting + nameToProcess;
						if (completeName.equals(name)) {
							found = true;
							break looking;
						}
					}
					if (!found) {
						final CustomArtifact artifactToDelete = bundle
								.getCustomArtifactByName(name);
						artifactsToRemove.add(artifactToDelete);
					}
				}
				for (final CustomArtifact artifactToDelete : artifactsToRemove) {
					bundle.markCustomArtifactAsRemoved(artifactToDelete);
				}
				ignoreCount += innerResult.getIgnoredNames().size();

				this.splitWorkBetweenThreads(bundle, errorCounter, loadCounter,
						mapping, namesToProcess, workers, globalContext,
						errorHandler);

			}
			try {
				final ExecutorService executor = newFixedThreadPool(globalContext
						.withThreadPoolSize(bundle));

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

		localContext
				.threadExecutionAboutToStart(bundle, mapping, globalContext);
		try {
			for (final String artifactName : namesToProcess) {
				try {

					final byte[] content = localContext
							.loadArtifactOrReturnNullToIgnore(bundle, mapping,
									artifactName, globalContext);
					if (content == null) {
						continue;
					}
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
			localContext
					.threadExecutionFinished(bundle, mapping, globalContext);
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
