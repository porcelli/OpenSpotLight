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
package org.openspotlight.bundle.language.java.bundle;

import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.processing.*;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class JavaGlobalPhase implements BundleProcessorGlobalPhase<Artifact> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void didFinishProcessing( final ArtifactChanges<Artifact> changes,
                                     final ExecutionContext context,
                                     final CurrentProcessorContext currentContext ) {

    }

    @SuppressWarnings( "unchecked" )
    public Set<Class<? extends Artifact>> getArtifactTypes() {
        return SLCollections.<Class<? extends Artifact>>setOf(StreamArtifact.class, StringArtifact.class);
    }

    public SaveBehavior getSaveBehavior() {
        return SaveBehavior.PER_ARTIFACT;
    }

    @SuppressWarnings( "unchecked" )
    public void selectArtifactsToBeProcessed( final CurrentProcessorContext currentContext,
                                              final ExecutionContext context,
                                              final ArtifactChanges<Artifact> changes,
                                              final ArtifactsToBeProcessed<Artifact> toBeReturned ) throws Exception {
        final String classpahtEntries = currentContext.getBundleProperties().get(JavaConstants.JAR_CLASSPATH);
        final Set<String> contexts = new LinkedHashSet<String>();
        if (classpahtEntries != null) {
            final String[] entries = classpahtEntries.split(JavaConstants.CLASSPATH_SEPARATOR_REGEXP);
            for (final String entry : entries) {
                final StreamArtifact artifact = context.getPersistentArtifactManager().findByPath(StreamArtifact.class, entry);
                Assertions.checkNotNull("artifact:" + entry, artifact);
                Assertions.checkCondition("artifactNameEndsWithJar:" + artifact.getArtifactCompleteName(),
                                          artifact.getArtifactCompleteName().endsWith(".jar"));
                String ctxName = artifact.getUniqueContextName();
                if (ctxName == null) {
                    ctxName = JavaBinaryProcessor.discoverContextName(artifact,
                                                                      context.getPersistentArtifactManager().getSimplePersist());
                    if (logger.isDebugEnabled()) {
                        logger.debug("context unique name for " + artifact.getArtifactCompleteName() + " = " + ctxName);
                    }
                }
                Assertions.checkNotEmpty("ctxName", ctxName);
                contexts.add(ctxName);
            }
        }
        final String contextsEntries = currentContext.getBundleProperties().get(JavaConstants.CONTEXT_CLASSPATH_ENTRY);
        if (contextsEntries != null) {
            final String[] entries = contextsEntries.split(JavaConstants.CLASSPATH_SEPARATOR_REGEXP);
            for (final String entry : entries) {
                contexts.add(entry);
            }
        }
        final SLGraphSession session = context.getGraphSession();
        for (final String entry : contexts) {
            final SLContext slContext = session.getContext(entry);
            Assertions.checkNotNull("slContext:" + entry, slContext);
        }

        synchronized (currentContext.getTransientProperties()) {

            if (currentContext.getTransientProperties().containsKey(JavaConstants.USING_CONTEXTS)) {
                final Set<String> existent = (Set<String>)currentContext.getTransientProperties().get(
                                                                                                      JavaConstants.USING_CONTEXTS);
                existent.addAll(contexts);
                if (logger.isDebugEnabled()) {
                    logger.debug("current contexts: " + existent);
                }
            } else {
                currentContext.getTransientProperties().put(JavaConstants.USING_CONTEXTS, contexts);
                if (logger.isDebugEnabled()) {
                    logger.debug("adding contexts: " + contexts);
                }
            }
        }
    }

}
