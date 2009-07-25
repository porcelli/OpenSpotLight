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

import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.graph.SLGraphSession;

/**
 * This interface abstracts the bundle processing capabilite. It receive
 * notification about all artifact events. With this events, this interface
 * implementation should process all artifacts that needs processing.
 * 
 * All new types of {@link BundleProcessor} to be used inside an OSL instance
 * should be also inside {@link Configuration} for this instance.
 * 
 * The better scenario to implement this class is with a stateless class with
 * all logic to process just the target artifact inside the
 * {@link BundleProcessingContext}. Other parameters are passed on the context
 * just for convenience.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * @param <T>
 *            Artifact type for this bundle processor
 * 
 */
public interface BundleProcessor<T extends Artifact> {
    
    /**
     * Processing context with all information needed to process an artifact.
     * The most important attributes inside this class are target artifact and
     * graph session. The main objective here is to get information for the
     * target artifact and create information inside graph session.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     * @param <T>
     */
    @ThreadSafe
    public static class BundleProcessingContext<T> {
        private final Bundle bundle;
        private final T targetArtifact;
        private final Set<T> addedArtifacts;
        private final Set<T> excludedArtifacts;
        private final Set<T> ignoredArtifacts;
        private final Set<T> artifactsWithError;
        private final Set<T> modifiedArtifacts;
        private final Set<T> allValidArtifacts;
        private final Set<T> notProcessedArtifacts;
        private final Set<T> alreadyProcessedArtifacts;
        private final SLGraphSession graphSession;
        
        /**
         * Constructor to initialize all mandatory fields
         * 
         * @param bundle
         * @param targetArtifact
         * @param addedArtifacts
         * @param excludedArtifacts
         * @param ignoredArtifacts
         * @param artifactsWithError
         * @param modifiedArtifacts
         * @param allValidArtifacts
         * @param notProcessedArtifacts
         * @param alreadyProcessedArtifacts
         * @param graphSession
         */
        public BundleProcessingContext(final Bundle bundle,
                final T targetArtifact, final Set<T> addedArtifacts,
                final Set<T> excludedArtifacts, final Set<T> ignoredArtifacts,
                final Set<T> artifactsWithError,
                final Set<T> modifiedArtifacts, final Set<T> allValidArtifacts,
                final Set<T> notProcessedArtifacts,
                final Set<T> alreadyProcessedArtifacts,
                final SLGraphSession graphSession) {
            this.bundle = bundle;
            this.targetArtifact = targetArtifact;
            this.addedArtifacts = addedArtifacts;
            this.excludedArtifacts = excludedArtifacts;
            this.ignoredArtifacts = ignoredArtifacts;
            this.artifactsWithError = artifactsWithError;
            this.modifiedArtifacts = modifiedArtifacts;
            this.allValidArtifacts = allValidArtifacts;
            this.notProcessedArtifacts = notProcessedArtifacts;
            this.alreadyProcessedArtifacts = alreadyProcessedArtifacts;
            this.graphSession = graphSession;
        }
        
        /**
         * 
         * @return all added artifacts.
         */
        public Set<T> getAddedArtifacts() {
            return this.addedArtifacts;
        }
        
        /**
         * 
         * @return all valid artifacts
         */
        public Set<T> getAllValidArtifacts() {
            return this.allValidArtifacts;
        }
        
        /**
         * 
         * @return all already processed artifacts
         */
        public Set<T> getAlreadyProcessedArtifacts() {
            return this.alreadyProcessedArtifacts;
        }
        
        /**
         * 
         * @return artifacts with error
         */
        public Set<T> getArtifactsWithError() {
            return this.artifactsWithError;
        }
        
        /**
         * 
         * @return the parent bundle for all this artifacts
         */
        public Bundle getBundle() {
            return this.bundle;
        }
        
        /**
         * 
         * @return all excluded artifacts
         */
        public Set<T> getExcludedArtifacts() {
            return this.excludedArtifacts;
        }
        
        /**
         * 
         * @return graph session
         */
        public SLGraphSession getGraphSession() {
            return this.graphSession;
        }
        
        /**
         * 
         * @return all ignored artifacts
         */
        public Set<T> getIgnoredArtifacts() {
            return this.ignoredArtifacts;
        }
        
        /**
         * 
         * @return all modified artifacts
         */
        public Set<T> getModifiedArtifacts() {
            return this.modifiedArtifacts;
        }
        
        /**
         * 
         * @return all processed artifacts
         */
        public Set<T> getNotProcessedArtifacts() {
            return this.notProcessedArtifacts;
        }
        
        /**
         * 
         * @return the target artifact to be processed
         */
        public T getTargetArtifact() {
            return this.targetArtifact;
        }
        
    }
    
    /**
     * This enum can change the behavior of
     * {@link BundleProcessor#processArtifact(BundleProcessingContext)} method.
     * The return types are used for statistical information and also to stop
     * the processing when the return value is
     * {@link ProcessingAction#ALL_PROCESSING_DONE}. The normal return value for
     * {@link BundleProcessor#processArtifact(BundleProcessingContext)} must be
     * {@link ProcessingAction#ARTIFACT_PROCESSED}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum ProcessingAction {
        /**
         * Artifact ignored. This artifact should not be processed by this
         * {@link BundleProcessor}.
         */
        ARTIFACT_IGNORED,
        /**
         * Artifact processed by this {@link BundleProcessor}.
         */
        ARTIFACT_PROCESSED,
        /**
         * There's an error processing this artifact by this
         * {@link BundleProcessor}.
         */
        ERROR_PROCESSING_ARTIFACT,
        /**
         * For a reason all work was done. So, no more artifacts should be
         * processed.
         */
        ALL_PROCESSING_DONE
    }
    
    /**
     * Method to process a target artifact. This method should be called a lot
     * of times by concurrent threads, so this class should be stateless.
     * 
     * @param context
     * @return {@link ProcessingAction#ARTIFACT_PROCESSED} if this method
     *         processed this artifact anyway.
     * @throws BundleProcessingNonFatalException
     *             if a error on the current artifact has happened
     * @throws BundleProcessingFatalException
     *             if a fatal error has happened
     */
    public ProcessingAction processArtifact(BundleProcessingContext<T> context)
            throws BundleProcessingNonFatalException,
            BundleProcessingFatalException;
    
}
