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
 * {@link BundleProcessingGroup}. Other parameters are passed on the context
 * just for convenience.
 * 
 * Please, implement one of the child interfaces that extended this
 * {@link BundleProcessor} interface.
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
    public static class BundleProcessingGroup<T> {
        private final Bundle bundle;
        private final Set<T> addedArtifacts;
        private final Set<T> excludedArtifacts;
        private final Set<T> ignoredArtifacts;
        private final Set<T> artifactsWithError;
        private final Set<T> modifiedArtifacts;
        private final Set<T> allValidArtifacts;
        private final Set<T> notProcessedArtifacts;
        private final Set<T> alreadyProcessedArtifacts;
        
        /**
         * Constructor to initialize all mandatory fields
         * 
         * @param bundle
         * @param addedArtifacts
         * @param excludedArtifacts
         * @param ignoredArtifacts
         * @param artifactsWithError
         * @param modifiedArtifacts
         * @param allValidArtifacts
         * @param notProcessedArtifacts
         * @param alreadyProcessedArtifacts
         */
        public BundleProcessingGroup(final Bundle bundle,
                final Set<T> addedArtifacts, final Set<T> excludedArtifacts,
                final Set<T> ignoredArtifacts, final Set<T> artifactsWithError,
                final Set<T> modifiedArtifacts, final Set<T> allValidArtifacts,
                final Set<T> notProcessedArtifacts,
                final Set<T> alreadyProcessedArtifacts) {
            checkNotNull("bundle", bundle); //$NON-NLS-1$
            checkNotNull("addedArtifacts", addedArtifacts);//$NON-NLS-1$
            checkNotNull("excludedArtifacts", excludedArtifacts);//$NON-NLS-1$
            checkNotNull("ignoredArtifacts", ignoredArtifacts);//$NON-NLS-1$
            checkNotNull("artifactsWithError", artifactsWithError);//$NON-NLS-1$
            checkNotNull("modifiedArtifacts", modifiedArtifacts);//$NON-NLS-1$
            checkNotNull("allValidArtifacts", allValidArtifacts);//$NON-NLS-1$
            checkNotNull("notProcessedArtifacts", notProcessedArtifacts);//$NON-NLS-1$
            checkNotNull("alreadyProcessedArtifacts", alreadyProcessedArtifacts);//$NON-NLS-1$
            
            this.bundle = bundle;
            this.addedArtifacts = addedArtifacts;
            this.excludedArtifacts = excludedArtifacts;
            this.ignoredArtifacts = ignoredArtifacts;
            this.artifactsWithError = artifactsWithError;
            this.modifiedArtifacts = modifiedArtifacts;
            this.allValidArtifacts = allValidArtifacts;
            this.notProcessedArtifacts = notProcessedArtifacts;
            this.alreadyProcessedArtifacts = alreadyProcessedArtifacts;
            
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
        
    }
    
    /**
     * This enum is the argument of method
     * {@link BundleProcessor#processFinished(FinishStatus)}. This enum tells
     * the {@link BundleProcessor} that it ended with some status.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum FinishStatus {
        /**
         * All artifacts was processed.
         */
        ALL_PROCESSED,
        /**
         * Some artifacts wasn't processed because of non fatal errors or
         * optional ignoring.
         */
        SOME_PROCESSED,

        /**
         * No artifacts was processed at all because of non fatal errors or
         * optional ignoring.
         */
        ANY_PROCESSED,

        /**
         * There was a fatal error.
         */
        FATAL_ERROR
    }
    
    /**
     * Graph context to be possible to add or change Graph utility objects
     * without changing the bundle processing interface.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    @ThreadSafe
    public static class GraphContext {
        
        private final SLGraphSession graphSession;
        
        /**
         * Constructor with final mandatory fields.
         * 
         * @param graphSession
         */
        public GraphContext(final SLGraphSession graphSession) {
            checkNotNull("graphSession", graphSession); //$NON-NLS-1$
            this.graphSession = graphSession;
        }
        
        /**
         * 
         * @return the graph session
         */
        public SLGraphSession getGraphSession() {
            return this.graphSession;
        }
    }
    
    /**
     * This enum is the return of
     * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, GraphContext)}
     * method. The return types are used for statistical information. The normal
     * return value for
     * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, GraphContext)}
     * must be {@link ProcessingAction#ARTIFACT_PROCESSED}.
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
        
    }
    
    /**
     * This enum is the return type of
     * {@link BundleProcessor#startProcessing(BundleProcessingGroup, GraphContext)}
     * method. Depending of the return value, is possible to change the behavior
     * of artifact processing for this execution. The common behavior should be
     * returning {@link #PROCESS_EACH_ONE_NEW}.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum ProcessingStartAction {
        /**
         * This should be the normal return for
         * {@link BundleProcessor#startProcessing(BundleProcessingGroup, GraphContext)}
         * . This constant tells the {@link BundleProcessor} to process all the
         * added artifact ou modified artifact. This artifacts are called new,
         * because they wasn't present on previous runnings.
         */
        PROCESS_EACH_ONE_NEW,
        /**
         * For a reason, all artifacts processed on a previous running are
         * invalid for now. So, it need to be run on all new artifacts and the
         * old ones. This constant tells the {@link BundleProcessor} to do that.
         */
        PROCESS_ALL_AGAIN,
        /**
         * For a reason, it's not convenient to process the artifacts rigth now.
         * So, with this constant as a return type, all the artifacts are
         * ignored.
         */
        IGNORE_ALL,
        /**
         * This constant tells the {@link BundleProcessor} that some fatal
         * condition happened. It's the same as throwing an exception.
         */
        FATAL_ERROR_ON_START_PROCESSING,
        /**
         * For some reason was really, really easy to process all artifacts on a
         * simple loop without threads and so on. So, in this situations,
         * process all artifacts and return this constant value and the
         * {@link BundleProcessor} will know that everything is ok.
         */
        ALL_PROCESSING_ALREADY_DONE
    }
    
    /**
     * Method to process a target artifact. This method should be called a lot
     * of times by concurrent threads, so this class should be stateless.
     * 
     * @param targetArtifact
     *            the artifact to be processed
     * @param bundleProcessingGroup
     *            with lists of all processed attributes and so on
     * @param graphContext
     *            with all convenient object for graph manipulation
     * @return {@link ProcessingAction#ARTIFACT_PROCESSED} if this method
     *         processed this artifact anyway.
     * @throws BundleProcessingNonFatalException
     *             if a error on the current artifact has happened
     * @throws BundleProcessingFatalException
     *             if a fatal error has happened
     */
    public ProcessingAction processArtifact(T targetArtifact,
            BundleProcessingGroup<T> bundleProcessingGroup,
            GraphContext graphContext)
            throws BundleProcessingNonFatalException,
            BundleProcessingFatalException;
    
    /**
     * This method tells the {@link BundleProcessor} that all processing was
     * done. This should be used to close resources and so on. It will have just
     * one call for each {@link Bundle} processing.
     * 
     * @param status
     *            of the processing
     */
    public void processFinished(FinishStatus status);
    
    /**
     * This method will be called once, before the threads creation to process
     * all the artifacts. This return is very important, because with this is
     * possible to change the behavior of this {@link BundleProcessor} execution
     * for its {@link Bundle}. Take a look on the {@link ProcessingStartAction}
     * documentation. The common behavior should be returning
     * {@link ProcessingStartAction#PROCESS_EACH_ONE_NEW}.
     * 
     * @param bundleProcessingGroup
     *            with lists of all processed attributes and so on
     * @param graphContext
     *            with all convenient object for graph manipulation
     * @return {@link ProcessingStartAction} to set the behavior of the
     *         {@link BundleProcessor}
     * @throws BundleProcessingFatalException
     *             if a fatal error has happened
     */
    public ProcessingStartAction startProcessing(
            BundleProcessingGroup<T> bundleProcessingGroup,
            GraphContext graphContext) throws BundleProcessingFatalException;
}
