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
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
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
 * threads obeying the {@link Repository#getNumberOfParallelThreads() number of
 * threads} configured for this {@link Repository}.
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
    private static class BundleProcessorCallable implements
            Callable<ProcessingAction> {
        
        private final BundleProcessor<StreamArtifact> bundleProcessor;
        
        private final StreamArtifact targetArtifact;
        private final GraphContext graphContext;
        private final BundleProcessingGroup<StreamArtifact> immutableProcessingGroup;
        private final BundleProcessingGroup<StreamArtifact> mutableProcessingGroup;
        
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
                final BundleProcessor<StreamArtifact> bundleProcessor,
                final StreamArtifact targetArtifact,
                final GraphContext graphContext,
                final BundleProcessingGroup<StreamArtifact> immutableProcessingGroup,
                final BundleProcessingGroup<StreamArtifact> mutableProcessingGroup) {
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
    private List<BundleProcessorCallable> createProcessorActions(
            final GraphContext graphContext, final Bundle bundle,
            final Set<StreamArtifact> allValidArtifacts,
            final Set<StreamArtifact> addedArtifacts,
            final Set<StreamArtifact> excludedArtifacts,
            final Set<StreamArtifact> modifiedArtifacts,
            final Set<StreamArtifact> notProcessedArtifacts,
            final Set<StreamArtifact> alreadyProcessedArtifacts,
            final Set<StreamArtifact> ignoredArtifacts,
            final Set<StreamArtifact> artifactsWithError,
            final StreamArtifactBundleProcessor processor)
            throws BundleProcessingFatalException {
        final Set<StreamArtifact> copyOfaddedArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                addedArtifacts);
        final Set<StreamArtifact> copyOfexcludedArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                excludedArtifacts);
        final Set<StreamArtifact> copyOfignoredArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                ignoredArtifacts);
        final Set<StreamArtifact> copyOfartifactsWithError = new CopyOnWriteArraySet<StreamArtifact>(
                artifactsWithError);
        final Set<StreamArtifact> copyOfmodifiedArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                modifiedArtifacts);
        final Set<StreamArtifact> copyOfallValidArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                allValidArtifacts);
        final Set<StreamArtifact> copyOfnotProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                notProcessedArtifacts);
        final Set<StreamArtifact> copyOfalreadyProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>(
                alreadyProcessedArtifacts);
        
        final BundleProcessingGroup<StreamArtifact> mutableGroup = new BundleProcessingGroup<StreamArtifact>(
                bundle, copyOfaddedArtifacts, copyOfexcludedArtifacts,
                copyOfignoredArtifacts, copyOfartifactsWithError,
                copyOfmodifiedArtifacts, copyOfallValidArtifacts,
                copyOfnotProcessedArtifacts, copyOfalreadyProcessedArtifacts,
                MutableType.MUTABLE);
        final BundleProcessingGroup<StreamArtifact> immutableGroup = new BundleProcessingGroup<StreamArtifact>(
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
        final List<BundleProcessorCallable> processActions = new ArrayList<BundleProcessorCallable>(
                copyOfnotProcessedArtifacts.size());
        for (final StreamArtifact targetArtifact : copyOfnotProcessedArtifacts) {
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
    private void findArtifactsByChangeType(final Bundle bundle,
            final Set<StreamArtifact> allValidArtifacts,
            final List<ItemChangeEvent<ConfigurationNode>> nodeChanges,
            final Set<StreamArtifact> addedArtifacts,
            final Set<StreamArtifact> excludedArtifacts,
            final Set<StreamArtifact> modifiedArtifacts) {
        for (final ItemChangeEvent<ConfigurationNode> change : nodeChanges) {
            if ((change.getNewItem() != null)
                    && allValidArtifacts.contains(change.getNewItem())) {
                if (ItemChangeType.ADDED.equals(change.getType())) {
                    addedArtifacts.add((StreamArtifact) change.getNewItem());
                } else if (ItemChangeType.CHANGED.equals(change.getType())) {
                    modifiedArtifacts.add((StreamArtifact) change.getNewItem());
                }
            } else {
                if (ItemChangeType.EXCLUDED.equals(change.getType())
                        && bundle.equals(change.getOldItem()
                                .getInstanceMetadata().getDefaultParent())) {
                    if (change.getOldItem() instanceof StreamArtifact) {
                        excludedArtifacts.add((StreamArtifact) change
                                .getOldItem());
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
     * @return a set of {@link StreamArtifactBundleProcessor}
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private Set<StreamArtifactBundleProcessor> findConfiguredBundleProcessors(
            final Bundle bundle) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        final Set<String> typeNames = bundle.getAllProcessorTypeNames();
        final Set<StreamArtifactBundleProcessor> processors = new HashSet<StreamArtifactBundleProcessor>();
        for (final String type : typeNames) {
            final BundleProcessor<?> processor = (BundleProcessor<?>) Class
                    .forName(type).newInstance();
            if (processor instanceof StreamArtifactBundleProcessor) {
                processors.add((StreamArtifactBundleProcessor) processor);
            }
        }
        return processors;
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
        try {
            final List<Callable<ProcessingAction>> allProcessActions = new ArrayList<Callable<ProcessingAction>>();
            final Integer numberOfParallelThreads = repository
                    .getNumberOfParallelThreads();
            final Set<Bundle> bundles = findAllNodesOfType(repository,
                    Bundle.class);
            for (final Bundle bundle : bundles) {
                if (!bundle.getActive()) {
                    continue;
                }
                final Set<StreamArtifactBundleProcessor> processors = this
                        .findConfiguredBundleProcessors(bundle);
                
                final Set<StreamArtifact> allValidArtifacts = findAllNodesOfType(
                        bundle, StreamArtifact.class);
                final List<ItemChangeEvent<ConfigurationNode>> nodeChanges = bundle
                        .getInstanceMetadata().getSharedData()
                        .getNodeChangesSinceLastSave();
                final Set<StreamArtifact> addedArtifacts = new HashSet<StreamArtifact>();
                final Set<StreamArtifact> excludedArtifacts = new HashSet<StreamArtifact>();
                final Set<StreamArtifact> modifiedArtifacts = new HashSet<StreamArtifact>();
                final Set<StreamArtifact> notProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
                final Set<StreamArtifact> alreadyProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
                final Set<StreamArtifact> ignoredArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
                final Set<StreamArtifact> artifactsWithError = new CopyOnWriteArraySet<StreamArtifact>();
                this.findArtifactsByChangeType(bundle, allValidArtifacts,
                        nodeChanges, addedArtifacts, excludedArtifacts,
                        modifiedArtifacts);
                notProcessedArtifacts.addAll(addedArtifacts);
                notProcessedArtifacts.addAll(modifiedArtifacts);
                
                for (final StreamArtifactBundleProcessor processor : processors) {
                    
                    final List<BundleProcessorCallable> processActions = this
                            .createProcessorActions(graphContext, bundle,
                                    allValidArtifacts, addedArtifacts,
                                    excludedArtifacts, modifiedArtifacts,
                                    notProcessedArtifacts,
                                    alreadyProcessedArtifacts,
                                    ignoredArtifacts, artifactsWithError,
                                    processor);
                    allProcessActions.addAll(processActions);
                }
            }
            final ExecutorService executor = Executors
                    .newFixedThreadPool(numberOfParallelThreads);
            executor.invokeAll(allProcessActions);
            
            while (executor.awaitTermination(300, TimeUnit.MILLISECONDS)) {
                this.wait();
            }
            
        } catch (final Exception e) {
            throw logAndReturnNew(e, BundleProcessingFatalException.class);
        }
    }
}
