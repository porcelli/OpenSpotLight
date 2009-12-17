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
package org.openspotlight.graph.client;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.remote.client.CantConnectException;
import org.openspotlight.remote.client.RemoteObjectFactory;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.InvalidReferenceTypeException;

/**
 * A factory for creating RemoteGraphSession objects.
 */
public class RemoteGraphSessionFactory {

	/**
	 * The Interface RemoteGraphFactoryConnectionData.
	 */
	public interface RemoteGraphFactoryConnectionData {

		/**
		 * Gets the host.
		 * 
		 * @return the host
		 */
		public String getHost();

		/**
		 * Gets the password.
		 * 
		 * @return the password
		 */
		public String getPassword();

		/**
		 * Gets the port.
		 * 
		 * @return the port
		 */
		public int getPort();

		/**
		 * Gets the user name.
		 * 
		 * @return the user name
		 */
		public String getUserName();
	}

	public static final class RemoteGraphFactoryConnectionDataImpl implements
			RemoteGraphFactoryConnectionData {
		private final String host;

		private final String password;

		private final int port;

		private final String userName;

		public RemoteGraphFactoryConnectionDataImpl(final String host,
				final String userName, final String password, final int port) {
			this.host = host;
			this.password = password;
			this.port = port;
			this.userName = userName;
		}

		public String getHost() {
			return host;
		}

		public String getPassword() {
			return password;
		}

		public int getPort() {
			return port;
		}

		public String getUserName() {
			return userName;
		}
	}

	public static final int DEFAULT_PORT = 7070;

	public static final long DEFAULT_TIMOUT_IN_MILLISECONDS = 10 * 60 * 1000; // 10
	// minutes

	/** The remote object factory. */
	private final RemoteObjectFactory remoteObjectFactory;

	/**
	 * Instantiates a new remote graph session factory.
	 * 
	 * @param connectionData
	 *            the connection data
	 * @param descriptor
	 *            the descriptor
	 * @throws CantConnectException
	 *             the cant connect exception
	 * @throws AccessDeniedException
	 *             the access denied exception
	 */
	public RemoteGraphSessionFactory(
			final RemoteGraphFactoryConnectionData connectionData)
			throws CantConnectException, AccessDeniedException {
		remoteObjectFactory = new RemoteObjectFactory(connectionData.getHost(),
				connectionData.getPort(), connectionData.getUserName(),
				connectionData.getPassword());
	}

	/**
	 * Creates a new RemoteGraphSession object.
	 * 
	 * @return the SL graph session
	 */
	public SLGraphSession createRemoteGraphSession(final String username,
			final String password, final String repository) {
		try {
			return remoteObjectFactory.createRemoteObject(SLGraphSession.class,
					username, password, repository);
		} catch (final InvalidReferenceTypeException e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}
}
