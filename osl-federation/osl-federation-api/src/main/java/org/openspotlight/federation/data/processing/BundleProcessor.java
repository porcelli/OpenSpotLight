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

package org.openspotlight.federation.data.processing;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

/**
 * This interface abstracts the bundle processing capabilite. It receive notification about all artifact events. With this events,
 * this interface implementation should process all artifacts that needs processing. All new types of {@link BundleProcessor} to
 * be used inside an OSL instance should be also inside {@link GlobalSettings} for this instance. The better scenario to implement
 * this class is with a stateless class with all logic to process just the target artifact inside the
 * {@link BundleProcessingGroup}. Other parameters are passed on the context just for convenience. Please, implement one of the
 * child interfaces that extended this {@link BundleProcessor} interface.
 * 
 * @param <T> Artifact type for this bundle processor
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public interface BundleProcessor<T extends Artifact> {

    public static interface ArtifactChanges<T> {
        public Set<T> getChangedArtifacts();

        public Set<T> getExcludedArtifacts();

        public Set<T> getIncludedArtifacts();

        public Set<T> getNotChangedArtifacts();
    }

    public static interface BundleProcessorContext {
        public SLNode getCurrentGroup();

        public SLGraphSession getGraphSession();

    }

    public Set<T> returnArtifactsToBeProcessed( ArtifactChanges<T> changes, BundleProcessorContext context );
    
    

    //    /**
    //     * Graph context to be possible to add or change Graph utility objects without changing the bundle processing interface.
    //     * 
    //     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
    //     */
    //    @ThreadSafe
    //    public static class BundleProcessingContext {
    //
    //        /** The session. */
    //        private final SLGraphSession        session;
    //
    //        private ArtifactFinder              artifactFinder;
    //
    //        private ArtifactSource              currentArtifactSource;
    //
    //        public Map<String, ArtifactSource>  availableArtifactSources;
    //
    //        /** The logger. */
    //        private DetailedLogger              logger;
    //
    //        /** The root group. */
    //        private Map<String, ArtifactSource> availableGroupNodes;
    //
    //        private SLNode                      currentGroup;
    //
    //        /**
    //         * Instantiates a new bundle processing context.
    //         * 
    //         * @param graphSession the graph session
    //         * @param jcrSession the jcr session
    //         * @param rootGroup the root group
    //         * @param configurationManager the configuration manager
    //         * @throws SLGraphException the SL graph exception
    //         */
    //        public BundleProcessingContext(
    //                                        final SLGraphSession graphSession, final Session jcrSession, final Group rootGroup,
    //                                        final ConfigurationManager configurationManager ) throws SLGraphException {
    //
    //            checkNotNull("graphSession", graphSession);
    //            checkNotNull("jcrSession", jcrSession);
    //            checkNotNull("configurationManager", configurationManager);
    //            checkNotNull("rootGroup", rootGroup);
    //            this.session = graphSession;
    //            ////            this.configurationManager = configurationManager;
    //            //            this.logger = DetailedLogger.Factory.createJcrDetailedLogger(jcrSession);
    //            //            this.rootGroup = rootGroup;
    //            //            final String contextId = this.encoder.encode(rootGroup.getInstanceMetadata().getPath());
    //            //            SLContext tempContext = null;
    //            //            try {
    //            //                tempContext = this.session.getContext(contextId);
    //            //            } catch (final Exception e) {
    //            //                catchAndLog(e);
    //            //            }
    //            //            if (tempContext == null) {
    //            //                tempContext = this.session.createContext(contextId);
    //            //            }
    //            //            this.context = tempContext;
    //        }
    //
    //        public ArtifactFinder getArtifactFinder() {
    //            return artifactFinder;
    //        }
    //
    //        public String getCurrentArtifactSourceReference() {
    //            return currentArtifactSourceReference;
    //        }
    //
    //        public SLNode getCurrentGroup() {
    //            return currentGroup;
    //        }
    //
    //        public List<SLNode> getGroupList() {
    //            return groupList;
    //        }
    //
    //        public DetailedLogger getLogger() {
    //            return logger;
    //        }
    //
    //        public SLGraphSession getSession() {
    //            return session;
    //        }
    //
    //        /**
    //         * Callback method to be used by {@link BundleProcessor}. Do not use this.
    //         * 
    //         * @throws SLGraphSessionException
    //         */
    //        public void processFinished() {
    //            //            this.configurationManager.save(this.rootGroup.getRepository().getConfiguration());
    //            //            try {
    //            //                //FIXME throws exeption here. Maybe it should have anything to do with multithread execution...
    //            //                this.session.save();
    //            //            } catch (final SLGraphSessionException e) {
    //            //                throw logAndReturnNew(e, ConfigurationException.class);
    //            //            }
    //            //            this.configurationManager.closeResources();
    //            this.session.close();
    //        }
    //
    //        /**
    //         * Callback method to be used by {@link BundleProcessor}. Do not use this.
    //         */
    //        public void processStarted() {
    //            //
    //        }
    //
    //        public void setArtifactFinder( final ArtifactFinder artifactFinder ) {
    //            this.artifactFinder = artifactFinder;
    //        }
    //
    //        public void setCurrentArtifactSourceReference( final String currentArtifactSourceReference ) {
    //            this.currentArtifactSourceReference = currentArtifactSourceReference;
    //        }
    //
    //        public void setCurrentGroup( final SLNode currentGroup ) {
    //            this.currentGroup = currentGroup;
    //        }
    //
    //        public void setGroupList( final List<SLNode> groupList ) {
    //            this.groupList = groupList;
    //        }
    //
    //        public void setLogger( final DetailedLogger logger ) {
    //            this.logger = logger;
    //        }
    //
    //    }
    //
    //    /**
    //     * Processing context with all information needed to process an artifact. The most important attributes inside this class are
    //     * target artifact and graph session. The main objective here is to get information for the target artifact and create
    //     * information inside graph session.
    //     * 
    //     * @param <T> *
    //     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
    //     */
    //    @ThreadSafe
    //    public static class BundleProcessingGroup<T> {
    //
    //        /** The added artifacts. */
    //        private final Set<T>         addedArtifacts;
    //
    //        /** The all valid artifacts. */
    //        private final Set<T>         allValidArtifacts;
    //
    //        /** The already processed artifacts. */
    //        private final Set<T>         alreadyProcessedArtifacts;
    //
    //        /** The artifacts with error. */
    //        private final Set<T>         artifactsWithError;
    //
    //        /** The bundle. */
    //        private final ArtifactSource bundle;
    //
    //        /** The excluded artifacts. */
    //        private final Set<T>         excludedArtifacts;
    //
    //        /** The ignored artifacts. */
    //        private final Set<T>         ignoredArtifacts;
    //
    //        /** The modified artifacts. */
    //        private final Set<T>         modifiedArtifacts;
    //
    //        /** The not processed artifacts. */
    //        private final Set<T>         notProcessedArtifacts;
    //
    //        /**
    //         * Constructor to initialize all mandatory fields.
    //         * 
    //         * @param bundle the bundle
    //         * @param addedArtifacts the added artifacts
    //         * @param excludedArtifacts the excluded artifacts
    //         * @param ignoredArtifacts the ignored artifacts
    //         * @param artifactsWithError the artifacts with error
    //         * @param modifiedArtifacts the modified artifacts
    //         * @param allValidArtifacts the all valid artifacts
    //         * @param notProcessedArtifacts the not processed artifacts
    //         * @param alreadyProcessedArtifacts the already processed artifacts
    //         * @param mutableType the mutable type
    //         */
    //        public BundleProcessingGroup(
    //                                      final ArtifactSource bundle, final Set<T> addedArtifacts, final Set<T> excludedArtifacts,
    //                                      final Set<T> ignoredArtifacts, final Set<T> artifactsWithError,
    //                                      final Set<T> modifiedArtifacts, final Set<T> allValidArtifacts,
    //                                      final Set<T> notProcessedArtifacts, final Set<T> alreadyProcessedArtifacts,
    //                                      final MutableType mutableType ) {
    //            checkNotNull("bundle", bundle); //$NON-NLS-1$
    //            checkNotNull("addedArtifacts", addedArtifacts);//$NON-NLS-1$
    //            checkNotNull("excludedArtifacts", excludedArtifacts);//$NON-NLS-1$
    //            checkNotNull("ignoredArtifacts", ignoredArtifacts);//$NON-NLS-1$
    //            checkNotNull("artifactsWithError", artifactsWithError);//$NON-NLS-1$
    //            checkNotNull("modifiedArtifacts", modifiedArtifacts);//$NON-NLS-1$
    //            checkNotNull("allValidArtifacts", allValidArtifacts);//$NON-NLS-1$
    //            checkNotNull("notProcessedArtifacts", notProcessedArtifacts);//$NON-NLS-1$
    //            checkNotNull("alreadyProcessedArtifacts", alreadyProcessedArtifacts);//$NON-NLS-1$
    //
    //            this.bundle = bundle;
    //            switch (mutableType) {
    //                case IMMUTABLE:
    //                    this.addedArtifacts = unmodifiableSet(addedArtifacts);
    //                    this.excludedArtifacts = unmodifiableSet(excludedArtifacts);
    //                    this.ignoredArtifacts = unmodifiableSet(ignoredArtifacts);
    //                    this.artifactsWithError = unmodifiableSet(artifactsWithError);
    //                    this.modifiedArtifacts = unmodifiableSet(modifiedArtifacts);
    //                    this.allValidArtifacts = unmodifiableSet(allValidArtifacts);
    //                    this.notProcessedArtifacts = unmodifiableSet(notProcessedArtifacts);
    //                    this.alreadyProcessedArtifacts = unmodifiableSet(alreadyProcessedArtifacts);
    //                    break;
    //                case MUTABLE:
    //                    this.addedArtifacts = addedArtifacts;
    //                    this.excludedArtifacts = excludedArtifacts;
    //                    this.ignoredArtifacts = ignoredArtifacts;
    //                    this.artifactsWithError = artifactsWithError;
    //                    this.modifiedArtifacts = modifiedArtifacts;
    //                    this.allValidArtifacts = allValidArtifacts;
    //                    this.notProcessedArtifacts = notProcessedArtifacts;
    //                    this.alreadyProcessedArtifacts = alreadyProcessedArtifacts;
    //                    break;
    //                default:
    //                    throw logAndReturn(new IllegalStateException("unexpected MutableType")); //$NON-NLS-1$
    //            }
    //
    //        }
    //
    //        /**
    //         * Gets the added artifacts.
    //         * 
    //         * @return all added artifacts.
    //         */
    //        public Set<T> getAddedArtifacts() {
    //            return this.addedArtifacts;
    //        }
    //
    //        /**
    //         * Gets the all valid artifacts.
    //         * 
    //         * @return all valid artifacts
    //         */
    //        public Set<T> getAllValidArtifacts() {
    //            return this.allValidArtifacts;
    //        }
    //
    //        /**
    //         * Gets the already processed artifacts.
    //         * 
    //         * @return all already processed artifacts
    //         */
    //        public Set<T> getAlreadyProcessedArtifacts() {
    //            return this.alreadyProcessedArtifacts;
    //        }
    //
    //        /**
    //         * Gets the artifacts with error.
    //         * 
    //         * @return artifacts with error
    //         */
    //        public Set<T> getArtifactsWithError() {
    //            return this.artifactsWithError;
    //        }
    //
    //        /**
    //         * Gets the bundle.
    //         * 
    //         * @return the parent bundle for all this artifacts
    //         */
    //        public ArtifactSource getBundle() {
    //            return this.bundle;
    //        }
    //
    //        /**
    //         * Gets the excluded artifacts.
    //         * 
    //         * @return all excluded artifacts
    //         */
    //        public Set<T> getExcludedArtifacts() {
    //            return this.excludedArtifacts;
    //        }
    //
    //        /**
    //         * Gets the ignored artifacts.
    //         * 
    //         * @return all ignored artifacts
    //         */
    //        public Set<T> getIgnoredArtifacts() {
    //            return this.ignoredArtifacts;
    //        }
    //
    //        /**
    //         * Gets the modified artifacts.
    //         * 
    //         * @return all modified artifacts
    //         */
    //        public Set<T> getModifiedArtifacts() {
    //            return this.modifiedArtifacts;
    //        }
    //
    //        /**
    //         * Gets the not processed artifacts.
    //         * 
    //         * @return all processed artifacts
    //         */
    //        public Set<T> getNotProcessedArtifacts() {
    //            return this.notProcessedArtifacts;
    //        }
    //
    //    }
    //
    //    /**
    //     * This enum is the return of
    //     * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, BundleProcessingContext)} method. The return types
    //     * are used for statistical information. The normal return value for
    //     * {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, BundleProcessingContext)} must be
    //     * {@link ProcessingAction#ARTIFACT_PROCESSED}.
    //     * 
    //     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
    //     */
    //    public static enum ProcessingAction {
    //
    //        /** Artifact ignored. This artifact should not be processed by this {@link BundleProcessor}. */
    //        ARTIFACT_IGNORED,
    //
    //        /** Artifact processed by this {@link BundleProcessor}. */
    //        ARTIFACT_PROCESSED,
    //
    //        /** There's an error processing this artifact by this {@link BundleProcessor}. */
    //        ERROR_PROCESSING_ARTIFACT,
    //
    //    }
    //
    //    /**
    //     * This enum is the return type of
    //     * {@link BundleProcessor#globalProcessingStarted(BundleProcessingGroup, BundleProcessingContext)} method. Depending of the
    //     * return value, is possible to change the behavior of artifact processing for this execution. The common behavior should be
    //     * returning {@link #PROCESS_EACH_ONE_NEW}.
    //     * 
    //     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
    //     */
    //    public static enum ProcessingStartAction {
    //
    //        /**
    //         * For some reason was really, really easy to process all artifacts on a simple loop without threads and so on. So, in
    //         * this situations, process all artifacts and return this constant value and the {@link BundleProcessor} will know that
    //         * everything is ok.
    //         */
    //        ALL_PROCESSING_ALREADY_DONE,
    //
    //        /**
    //         * This constant tells the {@link BundleProcessor} that some fatal condition happened. It's the same as throwing an
    //         * exception.
    //         */
    //        FATAL_ERROR_ON_START_PROCESSING,
    //
    //        /**
    //         * For a reason, it's not convenient to process the artifacts rigth now. So, with this constant as a return type, all the
    //         * artifacts are ignored.
    //         */
    //        IGNORE_ALL,
    //
    //        /**
    //         * For a reason, all artifacts processed on a previous running are invalid for now. So, it need to be run on all new
    //         * artifacts and the old ones. This constant tells the {@link BundleProcessor} to do that.
    //         */
    //        PROCESS_ALL_AGAIN,
    //
    //        /**
    //         * This constant tells the {@link BundleProcessor} to use the contents of
    //         * {@link BundleProcessor.BundleProcessingGroup#getNotProcessedArtifacts()} as a input to be processed on the each item
    //         * phase.
    //         */
    //        PROCESS_CUSTOMIZED_LIST,
    //
    //        /**
    //         * This should be the normal return for
    //         * {@link BundleProcessor#globalProcessingStarted(BundleProcessingGroup, GraphContext)} . This constant tells the
    //         * {@link BundleProcessor} to process all the added artifact ou modified artifact. This artifacts are called new, because
    //         * they wasn't present on previous runnings.
    //         */
    //        PROCESS_EACH_ONE_NEW
    //    }
    //
    //    /**
    //     * Callback method to inform that all the processing finalized.
    //     * 
    //     * @param bundleProcessingGroup the bundle processing group
    //     * @param graphContext the graph context
    //     */
    //    public void globalProcessingFinalized( BundleProcessingGroup<? extends Artifact> bundleProcessingGroup,
    //                                           BundleProcessingContext graphContext );
    //
    //    /**
    //     * This method will be called once, before the threads creation to process all the artifacts. This return is very important,
    //     * because with this is possible to change the behavior of this {@link BundleProcessor} execution for its
    //     * {@link ArtifactSource}. Take a look on the {@link ProcessingStartAction} documentation. The common behavior should be
    //     * returning {@link ProcessingStartAction#PROCESS_EACH_ONE_NEW}.
    //     * 
    //     * @param bundleProcessingGroup with lists of all processed attributes and so on
    //     * @param graphContext with all convenient object for graph manipulation
    //     * @return {@link ProcessingStartAction} to set the behavior of the {@link BundleProcessor}
    //     * @throws BundleProcessingFatalException if a fatal error has happened
    //     */
    //    public ProcessingStartAction globalProcessingStarted( BundleProcessingGroup<T> bundleProcessingGroup,
    //                                                          BundleProcessingContext graphContext )
    //        throws BundleProcessingFatalException;
    //
    //    /**
    //     * Method to process a target artifact. This method should be called a lot of times by concurrent threads, so this class
    //     * should be stateless.
    //     * 
    //     * @param targetArtifact the artifact to be processed
    //     * @param bundleProcessingGroup with lists of all processed attributes and so on
    //     * @param graphContext with all convenient object for graph manipulation
    //     * @return {@link ProcessingAction#ARTIFACT_PROCESSED} if this method processed this artifact anyway.
    //     * @throws BundleProcessingNonFatalException if a error on the current artifact has happened
    //     * @throws BundleProcessingFatalException if a fatal error has happened
    //     */
    //    public ProcessingAction processArtifact( T targetArtifact,
    //                                             BundleProcessingGroup<T> bundleProcessingGroup,
    //                                             BundleProcessingContext graphContext )
    //        throws BundleProcessingNonFatalException, BundleProcessingFatalException;

}
