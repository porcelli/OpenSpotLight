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

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.event.SLGraphSessionEventPoster;
import org.openspotlight.graph.exception.SLInvalidCredentialException;
import org.openspotlight.graph.persistence.*;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.SystemUser;
import org.openspotlight.security.idm.auth.IdentityManager;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.openspotlight.common.util.Exceptions.catchAndLog;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

/**
 * The Class SLGraphFactoryImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLGraphFactoryImpl extends SLGraphFactory {

	public static interface SLGraphClosingListener {
		public void graphClosed(SLGraph desc);
	}

	private class SLGraphClosingListenerImpl implements SLGraphClosingListener {

		public void graphClosed(final SLGraph desc) {
			JcrConnectionDescriptor data = null;
			for (final Entry<JcrConnectionDescriptor, SLGraph> entry : cache
					.entrySet()) {
				if (entry.getValue().equals(desc)) {
					data = entry.getKey();
					break;
				}
			}
			synchronized (cache) {
				if (data != null) {
					cache.remove(data);
				}

			}

		}

	}

	private final Map<JcrConnectionDescriptor, SLGraph> cache = new ConcurrentHashMap<JcrConnectionDescriptor, SLGraph>();

	@Override
	public synchronized SLGraph createGraph(
			final JcrConnectionDescriptor descriptor) {
		SLGraph cached = cache.get(descriptor);
		if (cached == null) {
			try {
				final SecurityFactory securityFactory = AbstractFactory
						.getDefaultInstance(SecurityFactory.class);
				final SLPersistentTreeFactory factory = AbstractFactory
						.getDefaultInstance(SLPersistentTreeFactory.class);
				final JcrConnectionProvider provider = JcrConnectionProvider
						.createFromData(descriptor);
				provider.openRepository();

				final SLPersistentTree tree = factory
						.createPersistentTree(descriptor);
				final SystemUser systemUser = securityFactory
						.createSystemUser();
				final IdentityManager identityManager = securityFactory
						.createIdentityManager(descriptor);
				final PolicyEnforcement graphPolicyEnforcement = securityFactory
						.createGraphPolicyEnforcement(descriptor);

				cached = new SLGraphImpl(tree,
						new SLGraphClosingListenerImpl(), identityManager,
						graphPolicyEnforcement, systemUser);
				cache.put(descriptor, cached);
			} catch (final AbstractFactoryException e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			} catch (final SLInvalidCredentialException e) {
                catchAndLog(e);
            }
        }
		return cached;
	}

	/**
	 * Creates the graph session.
	 * 
	 * @param treeSession
	 *            the tree session
	 * @param user
	 *            the user
	 * @return the sL graph session
	 */
	@Override
	SLGraphSession createGraphSession(
			final SLPersistentTreeSession treeSession,
			final PolicyEnforcement policyEnforcement,
			final AuthenticatedUser user) {
		Assertions.checkNotNull("treeSession", treeSession);
		Assertions.checkNotNull("policyEnforcement", policyEnforcement);
		Assertions.checkNotNull("user", user);
		return new SLGraphSessionImpl(treeSession, policyEnforcement, user);
	}


	/**
	 * Creates the node.
	 * 
	 * @param context
	 *            the context
	 * @param parent
	 *            the parent
	 * @param persistentNode
	 *            the persistent node
	 * @param eventPoster
	 *            the event poster
	 * @return the sL node
	 */
	@Override
	SLNode createNode(final SLContext context, final SLNode parent,
			final SLPersistentNode persistentNode,
			final SLGraphSessionEventPoster eventPoster) {
		return new SLNodeImpl(context, parent, persistentNode, eventPoster);
	}


	/**
	 * Creates the property.
	 * 
	 * @param node
	 *            the node
	 * @param persistentProperty
	 *            the persistent property
	 * @return the sL node property< v>
	 */
	@Override
	<V extends Serializable> SLNodeProperty<V> createProperty(
			final SLNode node,
			final SLPersistentProperty<V> persistentProperty,
			final SLGraphSessionEventPoster eventPoster) {
		return new SLNodePropertyImpl<V>(node, persistentProperty, eventPoster);
	}
}
