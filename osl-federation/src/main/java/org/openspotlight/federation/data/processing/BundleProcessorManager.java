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

import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.federation.data.util.ConfiguratonNodes.findAllNodesOfType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.openspotlight.graph.SLGraphSession;

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
     * 
     * @param <A>
     *            type of {@link Artifact}
     */
    private static class BundleProcessorCallable<A extends Artifact> implements
            Callable<ProcessingAction> {
        
        private final BundleProcessor<A> bundleProcessor;
        
        private final A targetArtifact;
        private final GraphContext graphContext;
        private final BundleProcessingGroup<A> immutableProcessingGroup;
        private final BundleProcessingGroup<A> mutableProcessingGroup;
        
        /**
         * Constructor to initialize final mandatory fields.
         * 
         * @param bundleProcessor
         * @param targetArtifact
         * @param graphContext
         */
        public BundleProcessorCallable(
                final BundleProcessor<A> bundleProcessor,
                final A targetArtifact, final GraphContext graphContext,
                final BundleProcessingGroup<A> immutableProcessingGroup,
                final BundleProcessingGroup<A> mutableProcessingGroup) {
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
            this.mutableProcessingGroup.getNotProcessedArtifacts().remove(
                    this.targetArtifact);
            switch (ret) {
                case ARTIFACT_IGNORED:
                    this.mutableProcessingGroup.getIgnoredArtifacts().add(
                            this.targetArtifact);
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
     * Start to process this {@link Repository} and to distribute all the
     * processing jobs for its {@link BundleProcessor configured processors}.
     * 
     * @param repository
     * @param graphSession
     * @throws BundleProcessingFatalException
     *             if a fatal error occurs.
     */
    public synchronized void processConfiguration(final Repository repository,
            final SLGraphSession graphSession)
            throws BundleProcessingFatalException {
        
        final Integer numberOfParallelThreads = repository
                .getNumberOfParallelThreads();
        final Set<Bundle> bundles = findAllNodesOfType(repository, Bundle.class);
        for (final Bundle bundle : bundles) {
            final Set<StreamArtifact> allValidArtifacts = findAllNodesOfType(
                    repository, StreamArtifact.class);
            final List<ItemChangeEvent<ConfigurationNode>> nodeChanges = bundle
                    .getInstanceMetadata().getSharedData()
                    .getNodeChangesSinceLastSave();
            final Set<StreamArtifact> addedArtifacts = new HashSet<StreamArtifact>();
            final Set<StreamArtifact> excludedArtifacts = new HashSet<StreamArtifact>();
            final Set<StreamArtifact> modifiedArtifacts = new HashSet<StreamArtifact>();
            final Set<StreamArtifact> notProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
            notProcessedArtifacts.addAll(addedArtifacts);
            notProcessedArtifacts.addAll(modifiedArtifacts);
            final Set<StreamArtifact> alreadyProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
            final Set<StreamArtifact> ignoredArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
            final Set<StreamArtifact> artifactsWithError = new CopyOnWriteArraySet<StreamArtifact>();
            
            for (final ItemChangeEvent<ConfigurationNode> change : nodeChanges) {
                if ((change.getNewItem() != null)
                        && allValidArtifacts.contains(change.getNewItem())) {
                    if (ItemChangeType.ADDED.equals(change.getType())) {
                        addedArtifacts
                                .add((StreamArtifact) change.getNewItem());
                    } else if (ItemChangeType.CHANGED.equals(change.getType())) {
                        modifiedArtifacts.add((StreamArtifact) change
                                .getNewItem());
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
            final BundleProcessingGroup<StreamArtifact> mutableGroup = new BundleProcessingGroup<StreamArtifact>(
                    bundle, addedArtifacts, excludedArtifacts,
                    ignoredArtifacts, artifactsWithError, modifiedArtifacts,
                    allValidArtifacts, notProcessedArtifacts,
                    alreadyProcessedArtifacts, MutableType.MUTABLE);
            final BundleProcessingGroup<StreamArtifact> immutableGroup = new BundleProcessingGroup<StreamArtifact>(
                    bundle, addedArtifacts, excludedArtifacts,
                    ignoredArtifacts, artifactsWithError, modifiedArtifacts,
                    allValidArtifacts, notProcessedArtifacts,
                    alreadyProcessedArtifacts, MutableType.IMMUTABLE);
        }
        
        final ExecutorService executor = Executors.newFixedThreadPool(40);
        
    }
}
