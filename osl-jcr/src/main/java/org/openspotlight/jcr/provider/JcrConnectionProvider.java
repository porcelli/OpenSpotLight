package org.openspotlight.jcr.provider;

import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Files.delete;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.util.RepositoryLock;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.ClassPathResource;

/**
 * The Class JcrConnectionProvider is used to provide access to {@link Session jcr sessions} and {@link Repository jcr
 * repositories} in a way that there's no need to know the implementation.
 * 
 * @author feu
 */
public abstract class JcrConnectionProvider {

    /**
     * The Class JackRabbitConnectionProvider is a {@link JcrConnectionProvider} based on Jack Rabbit.
     */
    private static class JackRabbitConnectionProvider extends JcrConnectionProvider {

        /** The repository. */
        private Repository repository;

        /** The repository closed. */
        private boolean    repositoryClosed = true;

        /**
         * Instantiates a new jack rabbit connection provider.
         * 
         * @param data the data
         */
        public JackRabbitConnectionProvider(
                                             final JcrConnectionDescriptor data ) {
            super(data);
        }

        /* (non-Javadoc)
         * @see org.openspotlight.jcr.provider.JcrConnectionProvider#closeRepository()
         */
        @Override
        public synchronized void closeRepository() {
            if (this.repository == null) {
                this.repositoryClosed = true;
                return;
            }
            final RepositoryImpl repositoryCasted = (org.apache.jackrabbit.core.RepositoryImpl)this.repository;

            repositoryCasted.shutdown();

            RepositoryLock repoLock = new RepositoryLock();
            try {
                repoLock.init(this.getData().getConfigurationDirectory());
                repoLock.acquire();
                repoLock.release();
            } catch (RepositoryException e) {
            }

            this.repositoryClosed = true;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.jcr.provider.JcrConnectionProvider#openRepository()
         */
        @Override
        public synchronized Repository openRepository() {
            if (this.repository == null || this.repositoryClosed) {
                try {

                    final RepositoryConfig config = RepositoryConfig.create(
                                                                            ClassPathResource.getResourceFromClassPath(this.getData().getXmlClasspathLocation()),
                                                                            this.getData().getConfigurationDirectory());

                    this.repository = RepositoryImpl.create(config);
                    this.repositoryClosed = false;
                } catch (final Exception e) {
                    throw logAndReturnNew(e, ConfigurationException.class);
                }
            }
            return this.repository;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.jcr.provider.JcrConnectionProvider#openSession()
         */
        @Override
        public Session openSession() {
            try {
                final Repository repo = this.openRepository();
                Session session;
                session = repo.login(this.getData().getCredentials());
                return session;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }

    }

    /** The cache. */
    private static Map<JcrConnectionDescriptor, JcrConnectionProvider> cache = new ConcurrentHashMap<JcrConnectionDescriptor, JcrConnectionProvider>();

    public static synchronized void invalidateCache( final JcrConnectionDescriptor data ) {
        if (cache.containsKey(data)) {
            JcrConnectionProvider provider = cache.get(data);
            if (provider != null) {
                provider.closeRepository();
                try {
                    delete(provider.getData().getConfigurationDirectory());
                } catch (SLException e) {
                }
            }
            cache.remove(data);
        }
    }

    /**
     * Creates the from data.
     * 
     * @param data the data
     * @return the jcr connection provider
     */
    public static synchronized JcrConnectionProvider createFromData( final JcrConnectionDescriptor data ) {
        JcrConnectionProvider provider = cache.get(data);
        if (provider == null) {
            switch (data.getJcrType()) {
                case JACKRABBIT:
                    provider = new JackRabbitConnectionProvider(data);
                    break;
                default:
                    throw logAndReturn(new IllegalStateException("Invalid jcr type"));
            }
            cache.put(data, provider);
        }
        return provider;
    }

    /** The data. */
    private final JcrConnectionDescriptor data;

    /**
     * Instantiates a new jcr connection provider.
     * 
     * @param data the data
     */
    JcrConnectionProvider(
                           final JcrConnectionDescriptor data ) {
        this.data = data;
    }

    /**
     * Close repository.
     */
    public abstract void closeRepository();

    /**
     * Gets the data.
     * 
     * @return the data
     */
    public JcrConnectionDescriptor getData() {
        return this.data;
    }

    /**
     * Open repository.
     * 
     * @return the repository
     */
    public abstract Repository openRepository();

    /**
     * Open session.
     * 
     * @return the session
     */
    public abstract Session openSession();
}
