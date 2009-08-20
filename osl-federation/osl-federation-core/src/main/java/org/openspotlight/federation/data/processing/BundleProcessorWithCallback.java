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

import org.openspotlight.federation.data.impl.Artifact;

/**
 * This interface will change a little bit the behavior of an
 * {@link BundleProcessor} implementation. When a bundle processor implement
 * this interface, the {@link BundleProcessor.GraphContext} used on a processing
 * task will be create for each artifact processing.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 * @param <T>
 *            artifact type
 */
public interface BundleProcessorWithCallback<T extends Artifact> extends
        BundleProcessor<T> {
    
    /**
     * Callback method to notify that the target artifact is finalized.
     * 
     * @param targetArtifact
     *            the artifact to be processed
     * @param bundleProcessingGroup
     *            with lists of all processed attributes and so on
     * @param graphContext
     *            with all convenient object for graph manipulation
     * @param returnStatus
     *            the {@link BundleProcessor.ProcessingAction} returned by
     *            {@link BundleProcessor#processArtifact(Artifact, BundleProcessingGroup, GraphContext)
     *            processing method}
     */
    public void artifactProcessingFinalized(T targetArtifact,
            BundleProcessingGroup<T> bundleProcessingGroup,
            GraphContext graphContext, ProcessingAction returnStatus);
    
    /**
     * Callback method to notify that the target artifact is about to be
     * processed.
     * 
     * @param targetArtifact
     *            the artifact to be processed
     * @param bundleProcessingGroup
     *            with lists of all processed attributes and so on
     * @param graphContext
     *            with all convenient object for graph manipulation
     * @throws BundleProcessingNonFatalException
     *             if a error on the current artifact has happened
     * @throws BundleProcessingFatalException
     *             if a fatal error has happened
     */
    public void artifactProcessingStarted(T targetArtifact,
            BundleProcessingGroup<T> bundleProcessingGroup,
            GraphContext graphContext)
            throws BundleProcessingNonFatalException,
            BundleProcessingFatalException;
    
}
