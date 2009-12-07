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
package org.openspotlight.federation.processing.internal;

import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.common.Disposable;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.finder.ArtifactFinderByRepositoryProvider;
import org.openspotlight.federation.finder.ArtifactFinderByRepositoryProviderFactory;
import org.openspotlight.federation.processing.internal.domain.BundleProcessorContextImpl;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLoggerFactory;
import org.openspotlight.security.idm.AuthenticatedUser;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorSupport.
 */
public class BundleProcessorContextFactory implements Disposable {

    /** The graph. */
    private final SLGraph                                   graph;

    /** The user. */
    private final AuthenticatedUser                         user;

    /** The descriptor. */
    private final JcrConnectionDescriptor                   descriptor;

    /** The artifact finder factory. */
    private final ArtifactFinderByRepositoryProviderFactory artifactFinderFactory;

    /** The logger factory. */
    private final DetailedLoggerFactory                     loggerFactory;

    /** The oppened graph sessions. */
    private final CopyOnWriteArrayList<SLGraphSession>      oppenedGraphSessions = new CopyOnWriteArrayList<SLGraphSession>();

    /**
     * Instantiates a new bundle processor support.
     * 
     * @param user the user
     * @param descriptor the descriptor
     * @param artifactFinderFactory the artifact finder factory
     * @param loggerFactory the logger factory
     * @throws Exception the exception
     */
    public BundleProcessorContextFactory(
                                          final AuthenticatedUser user, final JcrConnectionDescriptor descriptor,
                                          final ArtifactFinderByRepositoryProviderFactory artifactFinderFactory,
                                          final DetailedLoggerFactory loggerFactory ) throws Exception {
        super();
        this.user = user;
        this.descriptor = descriptor;
        this.artifactFinderFactory = artifactFinderFactory;
        this.loggerFactory = loggerFactory;
        this.graph = AbstractFactory.getDefaultInstance(SLGraphFactory.class).createGraph(descriptor);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.common.Disposable#closeResources()
     */
    public void closeResources() {
        try {
            this.loggerFactory.closeResources();
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
        try {
            this.artifactFinderFactory.closeResources();
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
        }
        for (final SLGraphSession session : this.oppenedGraphSessions) {
            try {
                session.save();
                session.close();
            } catch (final Exception e) {
                Exceptions.catchAndLog(e);
            }
        }

    }

    /**
     * Creates the bundle context.
     * 
     * @return the bundle processor context impl
     * @throws Exception the exception
     */
    public <A extends Artifact> BundleProcessorContextImpl createBundleContext() throws Exception {

        final SLGraphSession graphSession = this.graph.openSession(this.user);
        this.oppenedGraphSessions.add(graphSession);
        final DetailedLogger logger = this.loggerFactory.createNewLogger();
        final ArtifactFinderByRepositoryProvider artifactFinderProvider = this.artifactFinderFactory.createNew();
        final BundleProcessorContextImpl ctx = new BundleProcessorContextImpl(artifactFinderProvider, this.user, graphSession,
                                                                              logger);
        return ctx;
    }

    public ArtifactFinderByRepositoryProviderFactory getArtifactFinderFactory() {
        return this.artifactFinderFactory;
    }

    public JcrConnectionDescriptor getDescriptor() {
        return this.descriptor;
    }

    public SLGraph getGraph() {
        return this.graph;
    }

    public DetailedLoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    /**
     * Gets the or create group node for.
     * 
     * @param context the context
     * @param group the group
     * @return the or create group node for
     */
    public SLNode getOrCreateGroupNodeFor( final SLContext context,
                                           final Group group ) {
        throw new UnsupportedOperationException();//FIXME implement
    }

    public AuthenticatedUser getUser() {
        return this.user;
    }
}
