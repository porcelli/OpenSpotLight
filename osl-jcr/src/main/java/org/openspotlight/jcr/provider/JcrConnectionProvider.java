package org.openspotlight.jcr.provider;

import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.ClassPathResource;

public abstract class JcrConnectionProvider {

    private static class JackRabbitConnectionProvider extends JcrConnectionProvider {

        private Repository repository;

        public JackRabbitConnectionProvider(
                                             final JcrConnectionDescriptor data ) {
            super(data);
        }

        @Override
        public void closeRepository() {

        }

        @Override
        public synchronized Repository openRepository() {
            if (this.repository == null) {
                try {
                    final RepositoryConfig config = RepositoryConfig.create(
                                                                            ClassPathResource.getResourceFromClassPath(this.getData().getXmlClasspathLocation()),
                                                                            this.getData().getConfigurationDirectory());

                    this.repository = RepositoryImpl.create(config);
                } catch (final Exception e) {
                    throw logAndReturnNew(e, ConfigurationException.class);
                }
            }
            return this.repository;
        }

    }

    private static Map<JcrConnectionDescriptor, JcrConnectionProvider> cache = new ConcurrentHashMap<JcrConnectionDescriptor, JcrConnectionProvider>();

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

    private final JcrConnectionDescriptor data;

    private JcrConnectionProvider(
                                   final JcrConnectionDescriptor data ) {
        this.data = data;
    }

    public abstract void closeRepository();

    public JcrConnectionDescriptor getData() {
        return this.data;
    }

    public abstract Repository openRepository();
}
