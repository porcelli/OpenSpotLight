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

package org.openspotlight.federation.data.processing;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import static org.openspotlight.federation.data.util.ConfigurationNodes.findAllNodesOfType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.MutableType;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.CustomArtifact;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.impl.Artifact.Status;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.ConfigurationManagerProvider;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingContext;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingAction;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingStartAction;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

/**
 * The {@link BundleProcessorManager} is the class reposable to get an {@link Configuration} and to process all {@link Artifact
 * artifacts} on this {@link Configuration}. The {@link BundleProcessorManager} should get the {@link Bundle bundle's}
 * {@link BundleProcessorType types} and find all the {@link BundleProcessor processors} for each {@link BundleProcessorType type}
 * . After all {@link BundleProcessor processors} was found, the {@link BundleProcessorManager} should distribute the processing
 * job in some threads obeying the {@link Configuration#getNumberOfParallelThreads() number of threads} configured for this
 * {@link Repository}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public final class BundleProcessorManager {

    /**
     * This callable class is used to wrap a {@link BundleProcessor} instance and to group its artifacts by the
     * {@link BundleProcessor} return type. So, it will be possible to know if an artifact was processed, ignored, and so on.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * @param <T>
     */
    private static class BundleProcessorCallable<T extends Artifact> implements Callable<ProcessingAction> {

        private final BundleProcessor<T>             bundleProcessor;
        private final BundleProcessorWithCallback<T> callBack;
        private final BundleProcessingContext        graphContext;
        private final BundleProcessingGroup<T>       immutableProcessingGroup;
        private final BundleProcessingGroup<T>       mutableProcessingGroup;
        private final T                              targetArtifact;

        /**
         * Constructor to initialize final mandatory fields.
         * 
         * @param bundleProcessor
         * @param targetArtifact
         * @param graphContext
         * @param immutableProcessingGroup
         * @param mutableProcessingGroup
         */
        public BundleProcessorCallable(
                                        final BundleProcessor<T> bundleProcessor, final T targetArtifact,
                                        final BundleProcessingContext graphContext,
                                        final BundleProcessingGroup<T> immutableProcessingGroup,
                                        final BundleProcessingGroup<T> mutableProcessingGroup ) {
            checkNotNull("bundleProcessor", bundleProcessor); //$NON-NLS-1$
            checkNotNull("targetArtifact", targetArtifact); //$NON-NLS-1$
            checkNotNull("graphContext", graphContext); //$NON-NLS-1$
            checkNotNull("immutableProcessingGroup", immutableProcessingGroup); //$NON-NLS-1$
            checkNotNull("mutableProcessingGroup", mutableProcessingGroup); //$NON-NLS-1$
            this.bundleProcessor = bundleProcessor;
            this.targetArtifact = targetArtifact;
            this.graphContext = graphContext;
            this.immutableProcessingGroup = immutableProcessingGroup;
            this.mutableProcessingGroup = mutableProcessingGroup;
            if (this.bundleProcessor instanceof BundleProcessorWithCallback) {
                this.callBack = (BundleProcessorWithCallback<T>)this.bundleProcessor;
            } else {
                this.callBack = null;
            }
        }

        /**
         * Starts the {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, BundleProcessingContext)} method.
         * After its processing, depending on return type this method will do the necessary manipulation on
         * {@link BundleProcessingGroup} object.
         * 
         * @return the {@link ProcessingAction} depending on {@link BundleProcessor} result
         * @throws Exception if anything goes wrong
         */
        public ProcessingAction call() throws Exception {
            if (this.callBack != null) {
                this.graphContext.processStarted();
                this.callBack.artifactProcessingStarted(this.targetArtifact, this.immutableProcessingGroup, this.graphContext);
            }
            ProcessingAction ret;
            try {
                ret = this.bundleProcessor.processArtifact(this.targetArtifact, this.immutableProcessingGroup, this.graphContext);
            } catch (final Exception e) {
                catchAndLog(e);
                ret = ProcessingAction.ERROR_PROCESSING_ARTIFACT;
            }
            if (ret == null) {
                ret = ProcessingAction.ERROR_PROCESSING_ARTIFACT;
            }
            this.mutableProcessingGroup.getNotProcessedArtifacts().remove(this.targetArtifact);
            switch (ret) {
                case ARTIFACT_IGNORED:
                    this.mutableProcessingGroup.getIgnoredArtifacts().add(this.targetArtifact);
                    break;
                case ARTIFACT_PROCESSED:
                    this.mutableProcessingGroup.getAlreadyProcessedArtifacts().add(this.targetArtifact);
                    break;
                case ERROR_PROCESSING_ARTIFACT:
                    this.mutableProcessingGroup.getArtifactsWithError().add(this.targetArtifact);
                    break;
            }

            if (this.callBack != null) {
                this.callBack.artifactProcessingFinalized(this.targetArtifact, this.immutableProcessingGroup, this.graphContext,
                                                          ret);
            }
            this.graphContext.processFinished();

            return ret;
        }

    }

    /**
     * This class groups the necessary data for processing each artifact and also the {@link FinalizationContext} for each bundle.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * @param <T>
     */
    private static class CreateProcessorActionsResult<T extends Artifact> {
        FinalizationContext<T>                         finalizationContext;
        private final List<BundleProcessorCallable<T>> processorCallables;

        public CreateProcessorActionsResult(
                                             final List<BundleProcessorCallable<T>> processorCallables,
                                             final FinalizationContext<T> finalizationContext ) {
            super();
            this.processorCallables = processorCallables;
            this.finalizationContext = finalizationContext;
        }

        public FinalizationContext<T> getFinalizationContext() {
            return this.finalizationContext;
        }

        public List<BundleProcessorCallable<T>> getProcessorCallables() {
            return this.processorCallables;
        }

    }

    /**
     * Some necessary data for finalization callback methods.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * @param <T> Artifact type
     */
    private static class FinalizationContext<T extends Artifact> {
        private final BundleProcessingGroup<T> bundleProcessingGroup;
        private final BundleProcessingContext  graphContext;
        private final BundleProcessor<T>       processor;

        public FinalizationContext(
                                    final BundleProcessingContext graphContext,
                                    final BundleProcessingGroup<T> bundleProcessingGroup, final BundleProcessor<T> processor ) {
            checkNotNull("graphContext", graphContext); //$NON-NLS-1$
            checkNotNull("bundleProcessingGroup", bundleProcessingGroup); //$NON-NLS-1$
            checkNotNull("processor", processor); //$NON-NLS-1$
            this.graphContext = graphContext;
            this.bundleProcessingGroup = bundleProcessingGroup;
            this.processor = processor;
        }

        /**
         * @return the bundle processing group
         */
        public BundleProcessingGroup<T> getBundleProcessingGroup() {
            return this.bundleProcessingGroup;
        }

        /**
         * @return the global graph context
         */
        public BundleProcessingContext getGraphContext() {
            return this.graphContext;
        }

        /**
         * @return the bundle processor
         */
        public BundleProcessor<T> getProcessor() {
            return this.processor;
        }

    }

    /**
     * This class is used to identify a processor by its artifact type. The processor type must have only interfaces.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * @param <T> target artifact type
     */
    private static class MappedProcessor<T extends Artifact> {
        private final Class<T>                            artifactType;
        private final int                                 hashcode;
        private final Class<? extends BundleProcessor<T>> processorType;

        public MappedProcessor(
                                final Class<T> artifactType, final Class<? extends BundleProcessor<T>> processorType ) {
            checkNotNull("artifactType", artifactType); //$NON-NLS-1$
            checkNotNull("processorType", processorType); //$NON-NLS-1$
            checkCondition("processorTypeIsInterface", processorType.isInterface()); //$NON-NLS-1$

            this.artifactType = artifactType;
            this.processorType = processorType;
            this.hashcode = hashOf(artifactType, processorType);
        }

        @SuppressWarnings( "unchecked" )
        @Override
        public boolean equals( final Object o ) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MappedProcessor)) {
                return false;
            }
            final MappedProcessor that = (MappedProcessor)o;
            return eachEquality(of(this.artifactType, this.processorType), andOf(that.artifactType, that.processorType));
        }

        /**
         * @return the target artifact type
         */
        public Class<T> getArtifactType() {
            return this.artifactType;
        }

        /**
         * @return bundle processor type for given artifact
         */
        public Class<? extends BundleProcessor<T>> getProcessorType() {
            return this.processorType;
        }

        @Override
        public int hashCode() {
            return this.hashcode;
        }
    }

    private static final CreateProcessorActionsResult<Artifact>   emptyResult = new CreateProcessorActionsResult<Artifact>(
                                                                                                                           new ArrayList<BundleProcessorCallable<Artifact>>(
                                                                                                                                                                            0),
                                                                                                                           null);

    private static final Set<MappedProcessor<? extends Artifact>> processorRegistry;

    /**
     * Here all maped processors are added to the processor regristry static attribute.
     */
    static {
        processorRegistry = new HashSet<MappedProcessor<? extends Artifact>>();
        processorRegistry.add(new MappedProcessor<StreamArtifact>(StreamArtifact.class, StreamArtifactBundleProcessor.class));
        processorRegistry.add(new MappedProcessor<CustomArtifact>(CustomArtifact.class, CustomArtifactBundleProcessor.class));
    }

    /**
     * This method fills the artifacts by change type.
     * 
     * @param <T>
     * @param bundle
     * @param allValidArtifacts
     * @param nodeChanges
     * @param addedArtifacts
     * @param excludedArtifacts
     * @param modifiedArtifacts
     */
    private static <T extends Artifact> void findArtifactsByChangeType( final Bundle bundle,
                                                                        final Set<T> allValidArtifacts,
                                                                        final Set<T> addedArtifacts,
                                                                        final Set<T> excludedArtifacts,
                                                                        final Set<T> modifiedArtifacts ) {
        for (final T artifact : allValidArtifacts) {
            final Status status = artifact.getStatus();
            switch (status) {
                case ALREADY_PROCESSED:
                    break;
                case CHANGED:
                    modifiedArtifacts.add(artifact);
                    break;
                case EXCLUDED:
                    excludedArtifacts.add(artifact);
                    break;
                case INCLUDED:
                    addedArtifacts.add(artifact);
                    break;

                default:
                    throw logAndReturn(new IllegalStateException());
            }
        }
    }

    /**
     * This method looks for a {@link BundleProcessor} inside the {@link Bundle} configuration.
     * 
     * @param bundle
     * @return a set of {@link BundleProcessor}
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private static Set<BundleProcessor<?>> findConfiguredBundleProcessors( final Bundle bundle )
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Set<String> typeNames = bundle.getAllProcessorTypeNames();
        final Set<BundleProcessor<?>> processors = new HashSet<BundleProcessor<?>>();
        for (final String type : typeNames) {
            final BundleProcessor<?> processor = (BundleProcessor<?>)Class.forName(type).newInstance();
            processors.add(processor);
        }
        return processors;
    }

    /**
     * To create {@link SLGraphSession sessions} when needed.
     */
    private final SLGraph                      graph;

    private final JcrConnectionProvider        provider;

    private final ConfigurationManagerProvider configurationManagerProvider;

    /**
     * Constructor to initialize jcr connection provider
     * 
     * @param graph
     */
    public BundleProcessorManager(
                                   final JcrConnectionProvider provider,
                                   final ConfigurationManagerProvider configurationManagerProvider ) {
        checkNotNull("provider", provider); //$NON-NLS-1$
        checkNotNull("configurationManagerProvider", configurationManagerProvider); //$NON-NLS-1$
        this.provider = provider;
        this.configurationManagerProvider = configurationManagerProvider;
        try {
            this.graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class).createGraph(provider);
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

    /**
     * This method creates each {@link Callable} to call
     * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, BundleProcessingContext)} . It make a few copies
     * because the decision to mark some artifact as ignored or processed should be done by each {@link BundleProcessor},
     * independent of the other {@link BundleProcessor} involved.
     * 
     * @param <T>
     * @param bundle
     * @param allValidArtifacts
     * @param addedArtifacts
     * @param excludedArtifacts
     * @param modifiedArtifacts
     * @param notProcessedArtifacts
     * @param alreadyProcessedArtifacts
     * @param ignoredArtifacts
     * @param artifactsWithError
     * @param processor
     * @return a list of {@link Callable} processor action
     * @throws BundleProcessingFatalException
     */
    @SuppressWarnings( "unchecked" )
    private <T extends Artifact> CreateProcessorActionsResult<T> createProcessorActions( final Bundle bundle,
                                                                                         final Set<T> allValidArtifacts,
                                                                                         final Set<T> addedArtifacts,
                                                                                         final Set<T> excludedArtifacts,
                                                                                         final Set<T> modifiedArtifacts,
                                                                                         final Set<T> notProcessedArtifacts,
                                                                                         final Set<T> alreadyProcessedArtifacts,
                                                                                         final Set<T> ignoredArtifacts,
                                                                                         final Set<T> artifactsWithError,
                                                                                         final BundleProcessor<T> processor )
        throws BundleProcessingFatalException {
        BundleProcessingContext startingGraphContext = null;
        try {
            final Set<T> copyOfaddedArtifacts = new CopyOnWriteArraySet<T>(addedArtifacts);
            final Set<T> copyOfexcludedArtifacts = new CopyOnWriteArraySet<T>(excludedArtifacts);
            final Set<T> copyOfignoredArtifacts = new CopyOnWriteArraySet<T>(ignoredArtifacts);
            final Set<T> copyOfartifactsWithError = new CopyOnWriteArraySet<T>(artifactsWithError);
            final Set<T> copyOfmodifiedArtifacts = new CopyOnWriteArraySet<T>(modifiedArtifacts);
            final Set<T> copyOfallValidArtifacts = new CopyOnWriteArraySet<T>(allValidArtifacts);
            final Set<T> copyOfnotProcessedArtifacts = new CopyOnWriteArraySet<T>(notProcessedArtifacts);
            final Set<T> copyOfalreadyProcessedArtifacts = new CopyOnWriteArraySet<T>(alreadyProcessedArtifacts);

            final BundleProcessingGroup<T> mutableGroup = new BundleProcessingGroup<T>(bundle, copyOfaddedArtifacts,
                                                                                       copyOfexcludedArtifacts,
                                                                                       copyOfignoredArtifacts,
                                                                                       copyOfartifactsWithError,
                                                                                       copyOfmodifiedArtifacts,
                                                                                       copyOfallValidArtifacts,
                                                                                       copyOfnotProcessedArtifacts,
                                                                                       copyOfalreadyProcessedArtifacts,
                                                                                       MutableType.MUTABLE);
            final BundleProcessingGroup<T> immutableGroup = new BundleProcessingGroup<T>(bundle, copyOfaddedArtifacts,
                                                                                         copyOfexcludedArtifacts,
                                                                                         copyOfignoredArtifacts,
                                                                                         copyOfartifactsWithError,
                                                                                         copyOfmodifiedArtifacts,
                                                                                         copyOfallValidArtifacts,
                                                                                         copyOfnotProcessedArtifacts,
                                                                                         copyOfalreadyProcessedArtifacts,
                                                                                         MutableType.IMMUTABLE);
            startingGraphContext = new BundleProcessingContext(this.graph.openSession(), this.provider.openSession(),
                                                               bundle.getRootGroup(),
                                                               this.configurationManagerProvider.getNewInstance());
            startingGraphContext.processStarted();
            final ProcessingStartAction start = processor.globalProcessingStarted(mutableGroup, startingGraphContext);
            switch (start) {
                case ALL_PROCESSING_ALREADY_DONE:
                case IGNORE_ALL:
                case FATAL_ERROR_ON_START_PROCESSING:
                    processor.globalProcessingFinalized(immutableGroup, startingGraphContext);
                    return (CreateProcessorActionsResult<T>)emptyResult;
                case PROCESS_ALL_AGAIN:
                    copyOfnotProcessedArtifacts.clear();
                    copyOfnotProcessedArtifacts.addAll(allValidArtifacts);
                    break;
                case PROCESS_EACH_ONE_NEW:
                case PROCESS_CUSTOMIZED_LIST:
                    break;
                default:
                    throw logAndReturn(new IllegalStateException("Unexpected return type for startProcessing")); //$NON-NLS-1$

            }
            final List<BundleProcessorCallable<T>> processActions = new ArrayList<BundleProcessorCallable<T>>(
                                                                                                              copyOfnotProcessedArtifacts.size());
            for (final T targetArtifact : copyOfnotProcessedArtifacts) {
                final BundleProcessingContext graphContext = new BundleProcessingContext(
                                                                                         this.graph.openSession(),
                                                                                         this.provider.openSession(),
                                                                                         bundle.getRootGroup(),
                                                                                         this.configurationManagerProvider.getNewInstance());
                processActions.add(new BundleProcessorCallable(processor, targetArtifact, graphContext, mutableGroup,
                                                               immutableGroup));
            }
            final FinalizationContext<T> finalizationContext = new FinalizationContext<T>(startingGraphContext, immutableGroup,
                                                                                          processor);
            final CreateProcessorActionsResult<T> result = new CreateProcessorActionsResult<T>(processActions,
                                                                                               finalizationContext);
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, BundleProcessingFatalException.class);
        } finally {
            if (startingGraphContext != null) {
                startingGraphContext.processFinished();
            }
        }

    }

    /**
     * This method will look on static attribute {@link #processorRegistry} to map the processor interfaces and artifact types.
     * After that mapping, it will group all processing actions depending of the type, to be processed later.
     * 
     * @param <T> artifact type
     * @param mappedProcessor
     * @param allProcessActions
     * @param finalizationContexts
     * @param bundle
     * @param graphContext
     * @param processors
     * @throws BundleProcessingFatalException
     */
    @SuppressWarnings( "unchecked" )
    private <T extends Artifact> void groupProcessingActionsByArtifactType( final MappedProcessor<T> mappedProcessor,
                                                                            final List<Callable<ProcessingAction>> allProcessActions,
                                                                            final List<FinalizationContext<? extends Artifact>> finalizationContexts,
                                                                            final Bundle bundle,
                                                                            final Set<BundleProcessor<?>> processors )
        throws BundleProcessingFatalException {

        final Set<T> allValidArtifacts = findAllNodesOfType(bundle, mappedProcessor.getArtifactType());
        final Set<T> addedArtifacts = new HashSet<T>();
        final Set<T> excludedArtifacts = new HashSet<T>();
        final Set<T> modifiedArtifacts = new HashSet<T>();
        final Set<T> notProcessedArtifacts = new CopyOnWriteArraySet<T>();
        final Set<T> alreadyProcessedArtifacts = new CopyOnWriteArraySet<T>();
        final Set<T> ignoredArtifacts = new CopyOnWriteArraySet<T>();
        final Set<T> artifactsWithError = new CopyOnWriteArraySet<T>();
        findArtifactsByChangeType(bundle, allValidArtifacts, addedArtifacts, excludedArtifacts, modifiedArtifacts);
        notProcessedArtifacts.addAll(addedArtifacts);
        notProcessedArtifacts.addAll(modifiedArtifacts);
        for (final BundleProcessor<?> processor : processors) {
            if (mappedProcessor.getProcessorType().isInstance(processor)) {
                final CreateProcessorActionsResult<T> result = this.createProcessorActions(bundle, allValidArtifacts,
                                                                                           addedArtifacts, excludedArtifacts,
                                                                                           modifiedArtifacts,
                                                                                           notProcessedArtifacts,
                                                                                           alreadyProcessedArtifacts,
                                                                                           ignoredArtifacts, artifactsWithError,
                                                                                           (BundleProcessor<T>)processor);
                allProcessActions.addAll(result.getProcessorCallables());
                finalizationContexts.add(result.getFinalizationContext());
            }
        }
    }

    /**
     * Start to process this {@link Bundle} and to distribute all the processing jobs for its {@link BundleProcessor configured
     * processors}.
     * 
     * @param bundles
     * @throws BundleProcessingFatalException if a fatal error occurs.
     */
    @SuppressWarnings( "boxing" )
    public synchronized void processBundles( final Collection<Bundle> bundles ) throws BundleProcessingFatalException {
        checkNotNull("bundles", bundles); //$NON-NLS-1$
        checkNotNull("graph", this.graph); //$NON-NLS-1$
        final ConfigurationManager configurationManager = this.configurationManagerProvider.getNewInstance();
        try {

            final List<Callable<ProcessingAction>> allProcessActions = new ArrayList<Callable<ProcessingAction>>();
            final List<FinalizationContext<? extends Artifact>> finalizationContexts = new ArrayList<FinalizationContext<? extends Artifact>>(
                                                                                                                                              bundles.size());
            for (final Bundle targetBundle : bundles) {
                if (!targetBundle.getActive()) {
                    continue;
                }
                final Configuration configuration = configurationManager.load(LazyType.LAZY);
                final Bundle bundle = configurationManager.findNodeByUuidAndVersion(
                                                                                    configuration,
                                                                                    Bundle.class,
                                                                                    targetBundle.getInstanceMetadata().getSavedUniqueId(),
                                                                                    null);//FIXME set version
                final Set<BundleProcessor<?>> processors = BundleProcessorManager.findConfiguredBundleProcessors(bundle);
                for (final MappedProcessor<? extends Artifact> mappedProcessor : processorRegistry) {
                    this.groupProcessingActionsByArtifactType(mappedProcessor, allProcessActions, finalizationContexts, bundle,
                                                              processors);
                }
            }
            final Integer numberOfParallelThreads = bundles.iterator().next().getRepository().getConfiguration().getNumberOfParallelThreads();
            final ExecutorService executor = Executors.newFixedThreadPool(numberOfParallelThreads);
            try {

                executor.invokeAll(allProcessActions);

                while (executor.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                    this.wait();
                }

                for (final FinalizationContext<? extends Artifact> context : finalizationContexts) {
                    if (context != null) {

                        context.getProcessor().globalProcessingFinalized(context.getBundleProcessingGroup(),
                                                                         context.getGraphContext());
                    }
                }
            } finally {
                executor.shutdown();
            }
        } catch (final Exception e) {
            throw logAndReturnNew(e, BundleProcessingFatalException.class);
        } finally {
            configurationManager.closeResources();
        }
    }

}
