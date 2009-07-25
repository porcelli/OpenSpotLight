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

import static java.util.Collections.unmodifiableSet;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
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
public class BundleProcessorManager {
    
    /**
     * Start to process this {@link Configuration} and to distribute all the
     * processing jobs for its {@link BundleProcessor configured processors}.
     * 
     * @param configuration
     * @param graphSession
     * @throws BundleProcessingFatalException
     *             if a fatal error occurs.
     */
    public synchronized void processConfiguration(
            final Configuration configuration, final SLGraphSession graphSession)
            throws BundleProcessingFatalException {
        
        final Set<StreamArtifact> addedArtifacts = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> excludedArtifacts = new HashSet<StreamArtifact>();
        final Set<StreamArtifact> modifiedArtifacts = new HashSet<StreamArtifact>();
        
        final Set<StreamArtifact> allValidArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
        final Set<StreamArtifact> notProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
        final Set<StreamArtifact> alreadyProcessedArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
        final Set<StreamArtifact> ignoredArtifacts = new CopyOnWriteArraySet<StreamArtifact>();
        final Set<StreamArtifact> artifactsWithError = new CopyOnWriteArraySet<StreamArtifact>();
        
        // immutable collections to be passed to each new BundleProcessingGroup
        final Set<StreamArtifact> immutableAddedArtifacts = unmodifiableSet(addedArtifacts);
        final Set<StreamArtifact> immutableExcludedArtifacts = unmodifiableSet(excludedArtifacts);
        final Set<StreamArtifact> immutableModifiedArtifacts = unmodifiableSet(modifiedArtifacts);
        final Set<StreamArtifact> immutableAllValidArtifacts = unmodifiableSet(allValidArtifacts);
        final Set<StreamArtifact> immutableNotProcessedArtifacts = unmodifiableSet(notProcessedArtifacts);
        final Set<StreamArtifact> immutableAlreadyProcessedArtifacts = unmodifiableSet(alreadyProcessedArtifacts);
        final Set<StreamArtifact> immutableIgnoredArtifacts = unmodifiableSet(ignoredArtifacts);
        final Set<StreamArtifact> immutableArtifactsWithError = unmodifiableSet(artifactsWithError);
        
        final ExecutorService executor = Executors.newFixedThreadPool(40);
        
    }
}
