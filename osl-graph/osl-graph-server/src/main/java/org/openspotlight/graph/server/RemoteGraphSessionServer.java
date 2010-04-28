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
package org.openspotlight.graph.server;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.remote.server.RemoteObjectServer;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

/**
 * The Class RemoteGraphSessionServer.
 */
public class RemoteGraphSessionServer {

    /**
     * A factory for creating InternalGraphSession objects.
     */
    private static class InternalGraphSessionFactory implements InternalObjectFactory<SLGraphSession> {

        private final SLGraph                 graph;
        private final JcrConnectionDescriptor descriptor;

        /**
         * Instantiates a new internal graph session factory.
         */
        public InternalGraphSessionFactory(
                                            final JcrConnectionDescriptor descriptor ) {
            try {
                this.descriptor = descriptor;
                final SLGraphFactory graphFactory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
                graph = graphFactory.createGraph(descriptor);
            } catch (final AbstractFactoryException e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory
         * #createNewInstance(java.lang.Object[])
         */
        public synchronized SLGraphSession createNewInstance( final Object... parameters ) throws Exception {
            checkNotNull("parameters", parameters);
            checkCondition("correctParamSize", parameters.length == 3);
            checkCondition("correctTypeForFirstParam", parameters[0] instanceof String);
            checkCondition("correctTypeForSecondParam", parameters[1] instanceof String);
            checkCondition("correctTypeForThirdParam", parameters[2] instanceof String);
            final String user = (String)parameters[0];
            final String pass = (String)parameters[1];
            final String repository = (String)parameters[2];
            final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser(user);
            final AuthenticatedUser authenticatedUser = securityFactory.createIdentityManager(descriptor).authenticate(
                                                                                                                       simpleUser,
                                                                                                                       pass);

            return graph.openSession(authenticatedUser, repository);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory
         * #getTargetObjectType()
         */
        public Class<SLGraphSession> getTargetObjectType() {
            return SLGraphSession.class;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.openspotlight.remote.server.RemoteObjectServer.InternalObjectFactory
         * #shutdown()
         */
        public void shutdown() {
            graph.shutdown();
            final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(descriptor);
            provider.closeRepositoryAndCleanResources();

        }
    }

    /** The remote object server. */
    private final RemoteObjectServer remoteObjectServer;

    /**
     * Instantiates a new remote graph session server.
     * 
     * @param userAutenticator the user autenticator
     * @param portToUse the port to use
     * @param timeoutInMilliseconds the timeout in milliseconds
     */
    public RemoteGraphSessionServer(
                                     final UserAuthenticator userAutenticator, final Integer portToUse,
                                     final Long timeoutInMilliseconds, final JcrConnectionDescriptor descriptor ) {
        checkNotNull("userAutenticator", userAutenticator);
        checkNotNull("portToUse", portToUse);
        checkNotNull("timeoutInMilliseconds", timeoutInMilliseconds);
        checkNotNull("descriptor", descriptor);
        remoteObjectServer = RemoteObjectServerImpl.getDefault(userAutenticator, portToUse, timeoutInMilliseconds);
        remoteObjectServer.registerInternalObjectFactory(SLGraphSession.class, new InternalGraphSessionFactory(descriptor));
    }

    public void removeAllObjectsFromServer() {
        remoteObjectServer.closeAllObjects();
    }

    /**
     * Shutdown. This method should be called <b>only one time during the VM life cycle</b>. This is necessary due some static
     * garbage on RMI.
     */
    public void shutdown() {
        remoteObjectServer.shutdown();
    }
}
