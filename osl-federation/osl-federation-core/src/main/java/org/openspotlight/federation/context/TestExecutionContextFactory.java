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
package org.openspotlight.federation.context;

import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.common.DisposingListener;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class TestExecutionContextFactory implements ExecutionContextFactory,
		DisposingListener<DefaultExecutionContext> {

	public static ExecutionContextFactory createFactory(
			final ArtifactSource source) {
		return new TestExecutionContextFactory(source);
	}

	private final CopyOnWriteArrayList<TestExecutionContext> openedContexts = new CopyOnWriteArrayList<TestExecutionContext>();
	private final ArtifactSource source;
	private AuthenticatedUser user;
	private SLGraphSession graphSession;

	private TestExecutionContextFactory(final ArtifactSource source) {
		this.source = source;
	}

	public void closeResources() {
		for (final TestExecutionContext openedContext : openedContexts) {
			openedContext.closeResources();
		}
	}

	public ExecutionContext createExecutionContext(final String username,
			final String password, final JcrConnectionDescriptor descriptor,
			final String repositoryName) {
		try {
			if (user == null || graphSession == null) {
				final SecurityFactory securityFactory = AbstractFactory
						.getDefaultInstance(SecurityFactory.class);
				final User simpleUser = securityFactory.createUser(username);
				user = securityFactory.createIdentityManager(descriptor)
						.authenticate(simpleUser, password);
				final SLGraph graph = AbstractFactory.getDefaultInstance(
						SLGraphFactory.class).createGraph(descriptor);
				graphSession = graph.openSession(user, repositoryName);
			}
			final TestExecutionContext newContext = new TestExecutionContext(
					username, password, descriptor, repositoryName, this, user,
					graphSession, source);
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
