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
package org.openspotlight.federation.processing.internal.task;

import java.util.Date;
import java.util.HashSet;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.SyntaxInformation;
import org.openspotlight.federation.finder.PersistentArtifactManager;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.SaveBehavior;
import org.openspotlight.federation.processing.internal.RunnableWithBundleContext;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EachArtifactTask<T extends Artifact> extends RunnableWithBundleContext {

    private static final Object                   SAVE_LOCK = new Object();
    private final boolean                         first;

    private final T                               artifact;
    private final SaveBehavior                    saveBehavior;
    private final BundleProcessorArtifactPhase<T> bundleProcessor;
    private final CurrentProcessorContextImpl     currentContextImpl;
    private final Logger                          logger    = LoggerFactory.getLogger(getClass());

    public EachArtifactTask(
                             final boolean first, final String repositoryName, final T artifact, final SaveBehavior saveBehavior,
                             final BundleProcessorArtifactPhase<T> bundleProcessor,
                             final CurrentProcessorContextImpl currentContextImpl ) {
        super(repositoryName);
        this.first = first;
        this.artifact = artifact;
        this.saveBehavior = saveBehavior;
        this.bundleProcessor = bundleProcessor;
        this.currentContextImpl = currentContextImpl;
    }

    public void doIt() throws Exception {

        if (LastProcessStatus.EXCEPTION_DURRING_PROCESS.equals(this.artifact.getLastProcessStatus())
            || LastProcessStatus.EXCEPTION_DURRING_PROCESS.equals(this.artifact.getLastProcessStatus())) {
            logger.info("ignoring " + this.artifact + " due to its last process status: " + this.artifact.getLastProcessStatus());
            return;
        }
        this.bundleProcessor.beforeProcessArtifact(this.artifact, this.currentContextImpl, getBundleContext());
        LastProcessStatus result = null;
        try {
            if (first && this.artifact instanceof ArtifactWithSyntaxInformation) {
                final ArtifactWithSyntaxInformation artifactWithInfo = (ArtifactWithSyntaxInformation)this.artifact;
                artifactWithInfo.getSyntaxInformationSet().setTransient(new HashSet<SyntaxInformation>());
            }
            result = this.bundleProcessor.processArtifact(this.artifact, this.currentContextImpl, getBundleContext());
            if (SaveBehavior.PER_ARTIFACT.equals(this.saveBehavior)) {
                getBundleContext().getGraphSession().save();
            } else {
                logger.warn("Didn't save because of its save behavior");
            }
        } catch (final Exception e) {
            result = LastProcessStatus.EXCEPTION_DURRING_PROCESS;
            Exceptions.catchAndLog(e);
            getBundleContext().getLogger().log(
                                               getBundleContext().getUser(),
                                               LogEventType.ERROR,
                                               "Error during artifact processing on bundle processor "
                                               + this.bundleProcessor.getClass().getName(), this.artifact);
            throw e;
        } finally {
            this.artifact.setLastProcessStatus(result);
            this.artifact.setLastProcessedDate(new Date());
            final PersistentArtifactManager manager = getBundleContext().getPersistentArtifactManager();
            Exception ex = null;
            try {
                synchronized (SAVE_LOCK) {
                    if (ChangeType.EXCLUDED.equals(this.artifact.getChangeType())) {
                        manager.markAsRemoved(this.artifact);
                    } else {
                        manager.addTransient(this.artifact);
                    }
                    manager.saveTransientData();
                }

            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
                ex = e;
            }
            this.bundleProcessor.didFinishToProcessArtifact(this.artifact, result, this.currentContextImpl, getBundleContext());
            if (ex != null) {
                throw ex;
            }
        }
    }

    public CurrentProcessorContextImpl getCurrentContext() {
        return this.currentContextImpl;
    }

    @Override
    public void setBundleContext( final ExecutionContext bundleContext ) {
        super.setBundleContext(bundleContext);
        SLContext groupContext;
        try {
            groupContext = bundleContext.getGraphSession().createContext(SLConsts.DEFAULT_GROUP_CONTEXT);
            this.currentContextImpl.setGroupContext(groupContext);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

}
