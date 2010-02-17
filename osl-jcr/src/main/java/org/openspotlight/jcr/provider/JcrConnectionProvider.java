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
package org.openspotlight.jcr.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.util.RepositoryLock;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Files;

/**
 * The Class JcrConnectionProvider is used to provide access to {@link Session
 * jcr sessions} and {@link Repository jcr repositories} in a way that there's
 * no need to know the implementation.
 * 
 * @author feu
 */
public abstract class JcrConnectionProvider {

	/**
	 * The Class JackRabbitConnectionProvider is a {@link JcrConnectionProvider}
	 * based on Jack Rabbit.
	 */
	private static class JackRabbitConnectionProvider extends
	JcrConnectionProvider {

		/** The repository. */
		private Repository repository;

		/** The repository closed. */
		private boolean repositoryClosed = true;

		private static final AtomicInteger sessionIdFactory = new AtomicInteger(
				0);

		private static final CopyOnWriteArraySet<SessionWrapper> openSessions = new CopyOnWriteArraySet<SessionWrapper>();

		/**
		 * Instantiates a new jack rabbit connection provider.
		 * 
		 * @param data
		 *            the data
		 */
		public JackRabbitConnectionProvider(final JcrConnectionDescriptor data) {
			super(data);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.jcr.provider.JcrConnectionProvider#closeRepository
		 * ()
		 */
		@Override
		public synchronized void beforeCloseRepository() {
			if (repository == null) {
				repositoryClosed = true;
				return;
			}
			final RepositoryImpl repositoryCasted = (org.apache.jackrabbit.core.RepositoryImpl) repository;

			repositoryCasted.shutdown();

			final RepositoryLock repoLock = new RepositoryLock();
			try {
				repoLock.init(getData().getConfigurationDirectory());
				repoLock.acquire();
				repoLock.release();
			} catch (final RepositoryException e) {
			}
			if (getData().isTemporary()) {
				try {
					Files.delete(getData().getConfigurationDirectory());
				} catch (final SLException e) {
					throw Exceptions.logAndReturnNew(e,
							SLRuntimeException.class);

				}
			}

			repositoryClosed = true;
		}

		public Repository getRepository() {
			return repository;
		}

		@Override
		public Repository internalOpenRepository() {
			if (repository == null || repositoryClosed) {
				try {
					try {
						Files.delete(getData().getConfigurationDirectory());
					} catch (final SLException e) {
						throw Exceptions.logAndReturnNew(e,
								SLRuntimeException.class);
					}

					final RepositoryConfig config = RepositoryConfig.create(
							ClassPathResource
							.getResourceFromClassPath(getData()
									.getXmlClasspathLocation()),
									getData().getConfigurationDirectory());

					repository = RepositoryImpl.create(config);
					repositoryClosed = false;
				} catch (final Exception e) {
					throw Exceptions.logAndReturnNew(e,
							ConfigurationException.class);
				}
			}

			return repository;
		}

		@Override
		protected void internalShutdownRepository() {
			final RepositoryImpl repositoryTyped = (RepositoryImpl) getRepository();
			repositoryTyped.shutdown();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.jcr.provider.JcrConnectionProvider#openSession()
		 */
		@Override
		public SessionWithLock openSession() {
			try {
				openRepository();
				final Session newSession = repository.login(getData()
						.getCredentials());
				final int sessionId = JackRabbitConnectionProvider.sessionIdFactory
				.getAndIncrement();
				final SessionWrapper wrappedSession = new SessionWrapper(
						newSession, sessionId, new SessionClosingListener() {

							public void sessionClosed(final int id,
									final SessionWrapper wrapper,
									final Session session) {
								JackRabbitConnectionProvider.openSessions
								.remove(wrapper);

							}
						});
				JackRabbitConnectionProvider.openSessions.add(wrappedSession);
				return wrappedSession;
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}
		}

	}

	interface SessionClosingListener {
		public void sessionClosed(int id, SessionWrapper wrapper,
				Session session);
	}

	/** The cache. */
	private static Map<JcrConnectionDescriptor, JcrConnectionProvider> cache = new ConcurrentHashMap<JcrConnectionDescriptor, JcrConnectionProvider>();

	/**
	 * Creates the from data.
	 * 
	 * @param data
	 *            the data
	 * @return the jcr connection provider
	 */
	public static synchronized JcrConnectionProvider createFromData(
			final JcrConnectionDescriptor data) {
		JcrConnectionProvider provider = JcrConnectionProvider.cache.get(data);
		if (provider == null) {
			switch (data.getJcrType()) {
			case JACKRABBIT:
				provider = new JackRabbitConnectionProvider(data);
				break;
			default:
				throw Exceptions.logAndReturn(new IllegalStateException(
				"Invalid jcr type"));
			}
			JcrConnectionProvider.cache.put(data, provider);
		}
		return provider;
	}

	/** The data. */
	private final JcrConnectionDescriptor data;
	private final AtomicReference<Thread> shutdownHook = new AtomicReference<Thread>();

	/**
	 * Instantiates a new jcr connection provider.
	 * 
	 * @param data
	 *            the data
	 */
	JcrConnectionProvider(final JcrConnectionDescriptor data) {
		this.data = data;

	}

	/**
	 * Close repository.
	 */
	protected abstract void beforeCloseRepository();

	public void closeRepositoryAndCleanResources() {
		beforeCloseRepository();
		JcrConnectionProvider.cache.remove(this);
	}


	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public JcrConnectionDescriptor getData() {
		return data;
	}

	/**
	 * Open repository.
	 * 
	 * @return the repository
	 */
	public abstract Repository getRepository();

	/**
	 * Close repository.
	 */
	protected abstract Repository internalOpenRepository();

	/**
	 * Close repository.
	 */
	protected abstract void internalShutdownRepository();

	public final boolean isTemporary() {
		return data.isTemporary();
	}

	/**
	 * Open repository.
	 * 
	 * @return the repository
	 */
	public synchronized final Repository openRepository() {
		final Repository repository = internalOpenRepository();
		if (shutdownHook.get() == null) {
			shutdownHook.set(new Thread(new Runnable() {

				public void run() {

					internalShutdownRepository();

				}
			}));

			Runtime.getRuntime().addShutdownHook(shutdownHook.get());
		}
		return repository;
	}

	/**
	 * Open session.
	 * 
	 * @return the session
	 */
	public abstract SessionWithLock openSession();

	/**
	 * This method should be called once in the JVM lifetime. 
	 */
	public synchronized final void shutdownRepository() {
		if(shutdownHook.get()!=null) {
			Runtime.getRuntime().removeShutdownHook(shutdownHook.get());
		}
		shutdownHook.set(null);
		internalShutdownRepository();
	}

}
