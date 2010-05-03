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
package org.openspotlight.graph;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.exception.SLGraphException;
import org.openspotlight.graph.exception.SLGraphRuntimeException;
import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.security.SLInvalidCredentialException;
import org.openspotlight.security.authz.*;
import org.openspotlight.security.authz.graph.GraphElement;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.SystemUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityManager;

import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

/**
 * The Class SLGraphImpl.
 *
 * @author Vitor Hugo Chagas
 */
@Singleton
public class SLGraphImpl implements SLGraph {

    /**
     * The tree.
     */
    private final SLPersistentTree tree;

    /**
     * The graph state.
     */
    private GraphState graphState;

    private final SimplePersistFactory simplePersistFactory;

    /**
     * The user.
     */
    private final SystemUser user;

    /**
     * The policy enforcement.
     */
    private final PolicyEnforcement policyEnforcement;

    private final IdentityManager identityManager;

    /**
     * Instantiates a new sL graph impl.
     */
    @Inject
    public SLGraphImpl(final SLPersistentTree tree, SystemUser systemUser, IdentityManager identityManager,
                       PolicyEnforcement graphPolicyEnforcement, SimplePersistFactory simplePersistFactory) {
        try {
            this.simplePersistFactory = simplePersistFactory;
            if (!identityManager.isValid(systemUser)) {
                throw new SLInvalidCredentialException("SystemUser is not valid.");
            }

            this.tree = tree;
            graphState = GraphState.OPENED;
            this.identityManager = identityManager;
            this.policyEnforcement = graphPolicyEnforcement;
            this.user = systemUser;
        } catch (Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * {@inheritDoc}
     */
    public User getUser() {
        return user;
    }

    /**
     * Checks for privileges.
     *
     * @param user           the user
     * @param repositoryName the repository name
     * @param action         the action
     * @return true, if successful
     */
    private boolean hasPrivileges(final AuthenticatedUser user,
                                  final String repositoryName,
                                  final Action action) {
        final EnforcementContext enforcementContext = new EnforcementContext();
        enforcementContext.setAttribute("user", user);
        enforcementContext.setAttribute("graphElement", GraphElement.REPOSITORY);
        enforcementContext.setAttribute("repository", repositoryName);
        enforcementContext.setAttribute("action", action);
        enforcementContext.setAttribute("graph", this);

        try {
            final EnforcementResponse response = policyEnforcement.checkAccess(enforcementContext);
            if (response.equals(EnforcementResponse.GRANTED)) {
                return true;
            }
            return false;
        } catch (final EnforcementException e) {
            catchAndLog(e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public SLGraphSession openSession(final AuthenticatedUser user,
                                      final String repositoryName) throws SLGraphException {
        if (graphState == GraphState.SHUTDOWN) {
            throw new SLGraphException("Could not open graph session. Graph is shutdown.");
        }

        Assertions.checkNotNull("repositoryName", repositoryName);
        Assertions.checkNotNull("user", user);

        if (!identityManager.isValid(user)) {
            throw new SLInvalidCredentialException("Invalid user.");
        }

        if (!hasPrivileges(user, repositoryName, Action.READ)) {
            throw new SLInvalidCredentialException("User does not have privilegies to access repository.");
        }

        try {
            final SLPersistentTreeSession treeSession = tree.openSession(repositoryName);
            return new SLGraphSessionImpl(treeSession, policyEnforcement, user, simplePersistFactory);
        } catch (final Exception e) {
            throw new SLGraphRuntimeException("Could not open graph session.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        tree.shutdown();
        graphState = GraphState.SHUTDOWN;
    }
}
