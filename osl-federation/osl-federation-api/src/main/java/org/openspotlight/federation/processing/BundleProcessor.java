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

package org.openspotlight.federation.processing;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.LastProcessStatus;
import org.openspotlight.federation.finder.ArtifactFinder;
import org.openspotlight.federation.finder.ArtifactFinderProvider;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.log.DetailedLogger;

// TODO: Auto-generated Javadoc
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

    /**
     * The Interface ArtifactChanges.
     */
    public static interface ArtifactChanges<T extends Artifact> {

        /**
         * Gets the changed artifacts.
         * 
         * @return the changed artifacts
         */
        public Set<T> getChangedArtifacts();

        /**
         * Gets the excluded artifacts.
         * 
         * @return the excluded artifacts
         */
        public Set<T> getExcludedArtifacts();

        /**
         * Gets the included artifacts.
         * 
         * @return the included artifacts
         */
        public Set<T> getIncludedArtifacts();

        /**
         * Gets the not changed artifacts.
         * 
         * @return the not changed artifacts
         */
        public Set<T> getNotChangedArtifacts();
    }

    /**
     * The Interface ArtifactProcessingResults.
     */
    public static interface ArtifactProcessingResults<T extends Artifact> {

        /**
         * Gets the artifacts with error.
         * 
         * @return the artifacts with error
         */
        public Set<T> getArtifactsWithError();

        /**
         * Gets the ignored artifacts.
         * 
         * @return the ignored artifacts
         */
        public Set<T> getIgnoredArtifacts();

        /**
         * Gets the processed arifacts.
         * 
         * @return the processed arifacts
         */
        public Set<T> getProcessedArifacts();

    }

    /**
     * The Interface ArtifactsToBeProcessed.
     */
    public static interface ArtifactsToBeProcessed<T extends Artifact> {

        /**
         * Gets the artifacts already processed.
         * 
         * @return the artifacts already processed
         */
        public Set<T> getArtifactsAlreadyProcessed();

        /**
         * Gets the artifacts to be processed.
         * 
         * @return the artifacts to be processed
         */
        public Set<T> getArtifactsToBeProcessed();

        /**
         * Sets the artifacts already processed.
         * 
         * @param artifacts the new artifacts already processed
         */
        public void setArtifactsAlreadyProcessed( Set<T> artifacts );

        /**
         * Sets the artifacts to be processed.
         * 
         * @param artifacts the new artifacts to be processed
         */
        public void setArtifactsToBeProcessed( Set<T> artifacts );

    }

    /**
     * The Interface BundleProcessorContext.
     */
    public static interface BundleProcessorContext<A extends Artifact> {

        /**
         * Gets the artifact finder provider.
         * 
         * @return the artifact finder provider
         */
        public ArtifactFinderProvider getArtifactFinderProvider();

        /**
         * Gets the current group.
         * 
         * @return the current group
         */
        public Group getCurrentGroup();

        /**
         * Gets the current node group.
         * 
         * @return the current node group
         */
        public SLNode getCurrentNodeGroup();

        /**
         * Gets the default artifact finder.
         * 
         * @return the default artifact finder
         */
        public ArtifactFinder<A> getDefaultArtifactFinder();

        /**
         * Gets the graph session.
         * 
         * @return the graph session
         */
        public SLGraphSession getGraphSession();

        /**
         * Gets the logger.
         * 
         * @return the logger
         */
        public DetailedLogger getLogger();

        /**
         * Gets the node for group.
         * 
         * @param group the group
         * @return the node for group
         */
        public SLNode getNodeForGroup( Group group );

    }

    /**
     * Accept kind of artifact.
     * 
     * @param kindOfArtifact the kind of artifact
     * @return true, if successful
     */
    public <A extends Artifact> boolean acceptKindOfArtifact( Class<A> kindOfArtifact );

    /**
     * After process artifact.
     * 
     * @param artifact the artifact
     * @param status the status
     */
    public void afterProcessArtifact( T artifact,
                                      LastProcessStatus status );

    /**
     * Before process artifact.
     * 
     * @param artifact the artifact
     */
    public void beforeProcessArtifact( T artifact );

    /**
     * Global processing done.
     * 
     * @param results the results
     */
    public void globalProcessingDone( ArtifactProcessingResults<T> results );

    /**
     * Process artifact.
     * 
     * @param artifact the artifact
     * @param context the context
     * @return the last process status
     * @throws Exception the exception
     */
    public LastProcessStatus processArtifact( T artifact,
                                              BundleProcessorContext<T> context ) throws Exception;

    /**
     * Return artifacts to be processed.
     * 
     * @param changes the changes
     * @param context the context
     * @param toBeReturned the to be returned
     * @return the artifacts to be processed< t>
     */
    public void selectArtifactsToBeProcessed( ArtifactChanges<T> changes,
                                              BundleProcessorContext<T> context,
                                              ArtifactsToBeProcessed<T> toBeReturned );

}
