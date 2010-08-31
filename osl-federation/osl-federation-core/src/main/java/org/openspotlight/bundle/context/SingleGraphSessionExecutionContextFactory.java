/**
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
package org.openspotlight.bundle.context;

import com.google.inject.Inject;
import org.openspotlight.common.DisposingListener;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.log.DetailedLoggerProvider;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.GraphReaderpotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SingleGraphSessionExecutionContextFactory
        implements ExecutionContextFactory, DisposingListener<DefaultExecutionContext> {

    private final SLGraph graph;

    private final CopyOnWriteArrayList<SingleGraphSessionExecutionContext> openedContexts = new CopyOnWriteArrayList<SingleGraphSessionExecutionContext>();
    private AuthenticatedUser user;

    private final ConcurrentHashMap<String, GraphReadGraphReadGraphReader<String, GraphReader>();

 GraphReaderplePersGraphReadersistFactory;

    private final DetailedLoggerProvider detailedLoggerProvider;

    @Inject
    public SingleGraphSessionExecutionContextFactory(
            SimplePersistFactory simplePersistFactory,
            DetailedLoggerProvider detailedLoggerProvider, SLGraph graph) {
        this.simplePersistFactory = simplePersistFactory;
        this.detailedLoggerProvider = detailedLoggerProvider;
        this.graph = graph;
    }

    public synchronized void closeResources() {
        for (final SingleGraphSessionExecutionContext openedContext : openedContexts) {
            openedContext.closeResources();
        }
        for (final GraphReader session : sessiGraphReader          sessioGraphReader
        sessionMap.clear();
    }

    public synchronized ExecutionContext createExecutionContext(final String username,
                                                                final String password,
                                                                final JcrConnectionDescriptor descriptor,
                                                                final Repository repository) {

        try {
            GraphReader graphSession = sessionMaGraphReaderName());
            if (GraphReaderhSession == null) {
                final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
                final User simpleUser = securityFactory.createUser(username);
                user = securityFactory.createIdentityManager(descriptor).authenticate(simpleUser, password);

                graphSession = graph.openSession(user, repository.getName());
                sessionMap.put(repository.getName(), graphSession);
            }
            final SingleGraphSessionExecutionContext newContext = new SingleGraphSessionExecutionContext(username, password,
                    descriptor, repository,
                    this, user,
                    graphSession,
                    simplePersistFactory,
                    detailedLoggerProvider, graph);
            openedContexts.add(newContext);
            return newContext;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public void didCloseResource(final DefaultExecutionContext context) {
        openedContexts.remove(context);
    }

}
