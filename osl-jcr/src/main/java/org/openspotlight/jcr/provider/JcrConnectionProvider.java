package org.openspotlight.jcr.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

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
			if (this.repository == null) {
				this.repositoryClosed = true;
				return;
			}
			final RepositoryImpl repositoryCasted = (org.apache.jackrabbit.core.RepositoryImpl) this.repository;

			repositoryCasted.shutdown();

			final RepositoryLock repoLock = new RepositoryLock();
			try {
				repoLock.init(this.getData().getConfigurationDirectory());
				repoLock.acquire();
				repoLock.release();
			} catch (final RepositoryException e) {
			}
			if (this.getData().isTemporary()) {
				try {
					Files.delete(this.getData().getConfigurationDirectory());
				} catch (final SLException e) {
					throw Exceptions.logAndReturnNew(e,
							SLRuntimeException.class);

				}
			}

			this.repositoryClosed = true;
		}

		@Override
		public void openRepository() {
			if (this.repository == null || this.repositoryClosed) {
				try {
					try {
						Files
								.delete(this.getData()
										.getConfigurationDirectory());
					} catch (final SLException e) {
						throw Exceptions.logAndReturnNew(e,
								SLRuntimeException.class);
					}

					final RepositoryConfig config = RepositoryConfig.create(
							ClassPathResource.getResourceFromClassPath(this
									.getData().getXmlClasspathLocation()), this
									.getData().getConfigurationDirectory());

					this.repository = RepositoryImpl.create(config);
					this.repositoryClosed = false;
				} catch (final Exception e) {
					throw Exceptions.logAndReturnNew(e,
							ConfigurationException.class);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.jcr.provider.JcrConnectionProvider#openRepository()
		 */
		@Override
		public synchronized void openRepositoryAndCleanIfItIsTemporary() {
			if (this.getData().isTemporary()) {
				this.beforeCloseRepository();
			}

			this.openRepository();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.openspotlight.jcr.provider.JcrConnectionProvider#openSession()
		 */
		@Override
		public Session openSession() {
			try {
				this.openRepository();
				final Session newSession = this.repository.login(this.getData()
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

	public void closeRepository() {
		this.beforeCloseRepository();
		JcrConnectionProvider.cache.remove(this);
	}

	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public JcrConnectionDescriptor getData() {
		return this.data;
	}

	public final boolean isTemporary() {
		return this.data.isTemporary();
	}

	/**
	 * Open repository.
	 * 
	 * @return the repository
	 */
	public abstract void openRepository();

	/**
	 * Open repository.
	 * 
	 * @return the repository
	 */
	public abstract void openRepositoryAndCleanIfItIsTemporary();

	/**
	 * Open session.
	 * 
	 * @return the session
	 */
	public abstract Session openSession();

}
