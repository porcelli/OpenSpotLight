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

import static java.util.Collections.emptyList;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;
import static org.openspotlight.federation.data.util.ConfiguratonNodes.findAllNodesOfType;
import static org.openspotlight.federation.data.util.JcrLogger.log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openspotlight.common.MutableType;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeType;
import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.CustomArtifact;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup;
import org.openspotlight.federation.data.processing.BundleProcessor.GraphContext;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingAction;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingStartAction;

/**
 * The {@link BundleProcessorManager} is the class reposable to get an
 * {@link Configuration} and to process all {@link Artifact artifacts} on this
 * {@link Configuration}. The {@link BundleProcessorManager} should get the
 * {@link Bundle bundle's} {@link BundleProcessorType types} and find all the
 * {@link BundleProcessor processors} for each {@link BundleProcessorType type}.
 * 
 * After all {@link BundleProcessor processors} was found, the
 * {@link BundleProcessorManager} should distribute the processing job in some
 * threads obeying the {@link Configuration#getNumberOfParallelThreads() number
 * of threads} configured for this {@link Repository}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public final class BundleProcessorManager {
    
    /**
     * This callable class is used to wrap a {@link BundleProcessor} instance
     * and to group its artifacts by the {@link BundleProcessor} return type.
     * So, it will be possible to know if an artifact was processed, ignored,
     * and so on.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    private static class BundleProcessorCallable<T extends Artifact> implements
            Callable<ProcessingAction> {
        
        private final BundleProcessor<T> bundleProcessor;
        
        private final T targetArtifact;
        private final GraphContext graphContext;
        private final BundleProcessingGroup<T> immutableProcessingGroup;
        private final BundleProcessingGroup<T> mutableProcessingGroup;
        
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
                final BundleProcessor<T> bundleProcessor,
                final T targetArtifact, final GraphContext graphContext,
                final BundleProcessingGroup<T> immutableProcessingGroup,
                final BundleProcessingGroup<T> mutableProcessingGroup) {
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
        }
        
        /**
         * Starts the
         * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, GraphContext)}
         * method. After its processing, depending on return type this method
         * will do the necessary manipulation on {@link BundleProcessingGroup}
         * object.
         * 
         * @return the {@link ProcessingAction} depending on
         *         {@link BundleProcessor} result
         * @throws Exception
         *             if anything goes wrong
         */
        public ProcessingAction call() throws Exception {
            ProcessingAction ret;
            try {
                ret = this.bundleProcessor.processArtifact(this.targetArtifact,
                        this.immutableProcessingGroup, this.graphContext);
            } catch (final Exception e) {
                ret = ProcessingAction.ERROR_PROCESSING_ARTIFACT;
            }
            if (ret == null) {
                ret = ProcessingAction.ERROR_PROCESSING_ARTIFACT;
            }
            this.mutableProcessingGroup.getNotProcessedArtifacts().remove(
                    this.targetArtifact);
            switch (ret) {
                case ARTIFACT_IGNORED:
                    this.mutableProcessingGroup.getIgnoredArtifacts().add(
                            this.targetArtifact);
                    log(ret, this.targetArtifact);
                    break;
                case ARTIFACT_PROCESSED:
                    this.mutableProcessingGroup.getAlreadyProcessedArtifacts()
                            .add(this.targetArtifact);
                    break;
                case ERROR_PROCESSING_ARTIFACT:
                    this.mutableProcessingGroup.getArtifactsWithError().add(
                            this.targetArtifact);
                    break;
            }
            return ret;
        }
        
    }
    
    /**
     * This class is used to identify a processor by its artifact type. The
     * processor type must have only interfaces.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     * @param <T>
     *            target artifact type
     */
    private static class MappedProcessor<T extends Artifact> {
        private final Class<T> artifactType;
        private final Class<? extends BundleProcessor<T>> processorType;
        private final int hashcode;
        
        public MappedProcessor(final Class<T> artifactType,
                final Class<? extends BundleProcessor<T>> processorType) {
            checkNotNull("artifactType", artifactType); //$NON-NLS-1$
            checkNotNull("processorType", processorType); //$NON-NLS-1$
            checkCondition(
                    "processorTypeIsInterface", processorType.isInterface()); //$NON-NLS-1$
            
            this.artifactType = artifactType;
            this.processorType = processorType;
            this.hashcode = hashOf(artifactType, processorType);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof MappedProcessor)) {
                return false;
            }
            final MappedProcessor that = (MappedProcessor) o;
            return eachEquality(of(this.artifactType, this.processorType),
                    andOf(that.artifactType, that.processorType));
        }
        
        /**
         * 
         * @return the target artifact type
         */
        public Class<T> getArtifactType() {
            return this.artifactType;
        }
        
        /**
         * 
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
    
    private static final Set<MappedProcessor<? extends Artifact>> processorRegistry = new HashSet<MappedProcessor<? extends Artifact>>();
    
    /**
     * Here all maped processors are added to the processor regristry static
     * attribute.
     */
    static {
        processorRegistry.add(new MappedProcessor<StreamArtifact>(
                StreamArtifact.class, StreamArtifactBundleProcessor.class));
        processorRegistry.add(new MappedProcessor<CustomArtifact>(
                CustomArtifact.class, CustomArtifactBundleProcessor.class));
    }
    
    /**
     * thread pool.
     */
    private ExecutorService executor;
    
    /**
     * This method creates each {@link Callable} to call
     * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, GraphContext)}
     * . It make a few copies because the decision to mark some artifact as
     * ignored or processed should be done by each {@link BundleProcessor},
     * independent of the other {@link BundleProcessor} involved.
     * 
     * @param graphContext
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
    @SuppressWarnings("unchecked")
    private <T extends Artifact> List<BundleProcessorCallable<T>> createProcessorActions(
            final GraphContext graphContext, final Bundle bundle,
            final Set<T> allValidArtifacts, final Set<T> addedArtifacts,
            final Set<T> excludedArtifacts, final Set<T> modifiedArtifacts,
            final Set<T> notProcessedArtifacts,
            final Set<T> alreadyProcessedArtifacts,
            final Set<T> ignoredArtifacts, final Set<T> artifactsWithError,
            final BundleProcessor<T> processor)
            throws BundleProcessingFatalException {
        final Set<T> copyOfaddedArtifacts = new CopyOnWriteArraySet<T>(
                addedArtifacts);
        final Set<T> copyOfexcludedArtifacts = new CopyOnWriteArraySet<T>(
                excludedArtifacts);
        final Set<T> copyOfignoredArtifacts = new CopyOnWriteArraySet<T>(
                ignoredArtifacts);
        final Set<T> copyOfartifactsWithError = new CopyOnWriteArraySet<T>(
                artifactsWithError);
        final Set<T> copyOfmodifiedArtifacts = new CopyOnWriteArraySet<T>(
                modifiedArtifacts);
        final Set<T> copyOfallValidArtifacts = new CopyOnWriteArraySet<T>(
                allValidArtifacts);
        final Set<T> copyOfnotProcessedArtifacts = new CopyOnWriteArraySet<T>(
                notProcessedArtifacts);
        final Set<T> copyOfalreadyProcessedArtifacts = new CopyOnWriteArraySet<T>(
                alreadyProcessedArtifacts);
        
        final BundleProcessingGroup<T> mutableGroup = new BundleProcessingGroup<T>(
                bundle, copyOfaddedArtifacts, copyOfexcludedArtifacts,
                copyOfignoredArtifacts, copyOfartifactsWithError,
                copyOfmodifiedArtifacts, copyOfallValidArtifacts,
                copyOfnotProcessedArtifacts, copyOfalreadyProcessedArtifacts,
                MutableType.MUTABLE);
        final BundleProcessingGroup<T> immutableGroup = new BundleProcessingGroup<T>(
                bundle, copyOfaddedArtifacts, copyOfexcludedArtifacts,
                copyOfignoredArtifacts, copyOfartifactsWithError,
                copyOfmodifiedArtifacts, copyOfallValidArtifacts,
                copyOfnotProcessedArtifacts, copyOfalreadyProcessedArtifacts,
                MutableType.IMMUTABLE);
        final ProcessingStartAction start = processor.startProcessing(
                immutableGroup, graphContext);
        switch (start) {
            case ALL_PROCESSING_ALREADY_DONE:
            case IGNORE_ALL:
            case FATAL_ERROR_ON_START_PROCESSING:
                log(start, bundle);
                return emptyList();
            case PROCESS_ALL_AGAIN:
                copyOfnotProcessedArtifacts.clear();
                copyOfnotProcessedArtifacts.addAll(allValidArtifacts);
                break;
            case PROCESS_EACH_ONE_NEW:
                break;
            default:
                throw logAndReturn(new IllegalStateException(
                        "Unexpected return type for startProcessing")); //$NON-NLS-1$
                
        }
        final List<BundleProcessorCallable<T>> processActions = new ArrayList<BundleProcessorCallable<T>>(
                copyOfnotProcessedArtifacts.size());
        for (final T targetArtifact : copyOfnotProcessedArtifacts) {
            processActions
                    .add(new BundleProcessorCallable(processor, targetArtifact,
                            graphContext, mutableGroup, immutableGroup));
        }
        return processActions;
        
    }
    
    /**
     * This method fills the artifacts by change type.
     * 
     * @param bundle
     * @param allValidArtifacts
     * @param nodeChanges
     * @param addedArtifacts
     * @param excludedArtifacts
     * @param modifiedArtifacts
     */
    @SuppressWarnings("unchecked")
    private <T extends Artifact> void findArtifactsByChangeType(
            final Bundle bundle, final Set<T> allValidArtifacts,
            final List<ItemChangeEvent<ConfigurationNode>> nodeChanges,
            final Set<T> addedArtifacts, final Set<T> excludedArtifacts,
            final Set<T> modifiedArtifacts) {
        for (final ItemChangeEvent<ConfigurationNode> change : nodeChanges) {
            if ((change.getNewItem() != null)
                    && allValidArtifacts.contains(change.getNewItem())) {
                if (ItemChangeType.ADDED.equals(change.getType())) {
                    addedArtifacts.add((T) change.getNewItem());
                } else if (ItemChangeType.CHANGED.equals(change.getType())) {
                    modifiedArtifacts.add((T) change.getNewItem());
                }
            } else {
                if (ItemChangeType.EXCLUDED.equals(change.getType())
                        && bundle.equals(change.getOldItem()
                                .getInstanceMetadata().getDefaultParent())) {
                    if (change.getOldItem() instanceof Artifact) {
                        excludedArtifacts.add((T) change.getOldItem());
                    }
                }
            }
        }
    }
    
    /**
     * This method looks for a {@link BundleProcessor} inside the {@link Bundle}
     * configuration.
     * 
     * @param bundle
     * @return a set of {@link BundleProcessor}
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private Set<BundleProcessor<?>> findConfiguredBundleProcessors(
            final Bundle bundle) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        final Set<String> typeNames = bundle.getAllProcessorTypeNames();
        final Set<BundleProcessor<?>> processors = new HashSet<BundleProcessor<?>>();
        for (final String type : typeNames) {
            final BundleProcessor<?> processor = (BundleProcessor<?>) Class
                    .forName(type).newInstance();
            processors.add(processor);
        }
        return processors;
    }
    
    /**
     * This method will look on static attribute {@link #processorRegistry} to
     * map the processor interfaces and artifact types. After that mapping, it
     * will group all processing actions depending of the type, to be processed
     * later.
     * 
     * @param <T>
     *            artifact type
     * @param mappedProcessor
     * @param allProcessActions
     * @param bundle
     * @param graphContext
     * @param processors
     * @throws BundleProcessingFatalException
     */
    @SuppressWarnings("unchecked")
    private <T extends Artifact> void groupProcessingActionsByArtifactType(
            final MappedProcessor<T> mappedProcessor,
            final List<Callable<ProcessingAction>> allProcessActions,
            final Bundle bundle, final GraphContext graphContext,
            final Set<BundleProcessor<?>> processors)
            throws BundleProcessingFatalException {
        
        final Set<T> allValidArtifacts = findAllNodesOfType(bundle,
                mappedProcessor.getArtifactType());
        final List<ItemChangeEvent<ConfigurationNode>> nodeChanges = bundle
                .getInstanceMetadata().getSharedData()
                .getNodeChangesSinceLastSave();
        final Set<T> addedArtifacts = new HashSet<T>();
        final Set<T> excludedArtifacts = new HashSet<T>();
        final Set<T> modifiedArtifacts = new HashSet<T>();
        final Set<T> notProcessedArtifacts = new CopyOnWriteArraySet<T>();
        final Set<T> alreadyProcessedArtifacts = new CopyOnWriteArraySet<T>();
        final Set<T> ignoredArtifacts = new CopyOnWriteArraySet<T>();
        final Set<T> artifactsWithError = new CopyOnWriteArraySet<T>();
        this.findArtifactsByChangeType(bundle, allValidArtifacts, nodeChanges,
                addedArtifacts, excludedArtifacts, modifiedArtifacts);
        notProcessedArtifacts.addAll(addedArtifacts);
        notProcessedArtifacts.addAll(modifiedArtifacts);
        for (final BundleProcessor<?> processor : processors) {
            if (mappedProcessor.getProcessorType().isInstance(processor)) {
                final List<BundleProcessorCallable<T>> processActions = this
                        .createProcessorActions(graphContext, bundle,
                                allValidArtifacts, addedArtifacts,
                                excludedArtifacts, modifiedArtifacts,
                                notProcessedArtifacts,
                                alreadyProcessedArtifacts, ignoredArtifacts,
                                artifactsWithError,
                                (BundleProcessor<T>) processor);
                allProcessActions.addAll(processActions);
            }
        }
    }
    
    /**
     * Start to process this {@link Repository} and to distribute all the
     * processing jobs for its {@link BundleProcessor configured processors}.
     * 
     * @param repository
     * @param graphContext
     * @throws BundleProcessingFatalException
     *             if a fatal error occurs.
     */
    @SuppressWarnings("boxing")
    public synchronized void processRepository(final Repository repository,
            final GraphContext graphContext)
            throws BundleProcessingFatalException {
        checkNotNull("repository", repository); //$NON-NLS-1$
        checkNotNull("graphContext", graphContext); //$NON-NLS-1$
        
        try {
            final List<Callable<ProcessingAction>> allProcessActions = new ArrayList<Callable<ProcessingAction>>();
            final Set<Bundle> bundles = findAllNodesOfType(repository,
                    Bundle.class);
            for (final Bundle bundle : bundles) {
                if (!bundle.getActive()) {
                    continue;
                }
                
                final Set<BundleProcessor<?>> processors = this
                        .findConfiguredBundleProcessors(bundle);
                for (final MappedProcessor<? extends Artifact> mappedProcessor : processorRegistry) {
                    this
                            .groupProcessingActionsByArtifactType(
                                    mappedProcessor, allProcessActions, bundle,
                                    graphContext, processors);
                }
            }
            
            if (this.executor == null) {
                final Integer numberOfParallelThreads = repository
                        .getConfiguration().getNumberOfParallelThreads();
                this.executor = Executors
                        .newFixedThreadPool(numberOfParallelThreads);
            }
            this.executor.invokeAll(allProcessActions);
            
            while (this.executor.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                this.wait();
            }
            
        } catch (final Exception e) {
            throw logAndReturnNew(e, BundleProcessingFatalException.class);
        }
    }
}
