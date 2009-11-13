package org.openspotlight.federation.loader;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.persist.support.SimplePersistSupport;

/**
 * A factory for creating JcrSessionConfigurationManager objects.
 */
public class JcrSessionConfigurationManagerFactory {

    /**
     * The Class MutableJcrSessionConfigurationManager.
     */
    private static class MutableJcrSessionConfigurationManager implements ConfigurationManager {

        /** The session. */
        private final Session       session;

        /** The Constant GLOBAL_SETTINGS_LOCATION. */
        private static final String GLOBAL_SETTINGS_LOCATION = SharedConstants.DEFAULT_JCR_ROOT_NAME + "/config/global";

        /** The Constant REPOSITORIES_LOCATION. */
        private static final String REPOSITORIES_LOCATION    = SharedConstants.DEFAULT_JCR_ROOT_NAME + "/config/repositories";

        /**
         * Instantiates a new mutable jcr session configuration manager.
         * 
         * @param session the session
         */
        public MutableJcrSessionConfigurationManager(
                                                      final Session session ) {
            this.session = session;
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#closeResources()
         */
        public void closeResources() {
            if (this.session.isLive()) {
                this.session.logout();
            }

        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#getGlobalSettings()
         */
        public GlobalSettings getGlobalSettings() {
            try {
                final String nodeName = SimplePersistSupport.getJcrNodeName(GlobalSettings.class);
                final String xpath = GLOBAL_SETTINGS_LOCATION + "/" + nodeName;
                final Query query = this.session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
                final NodeIterator nodeIterator = query.execute().getNodes();
                if (nodeIterator.hasNext()) {
                    final Node node = nodeIterator.nextNode();
                    final GlobalSettings settings = SimplePersistSupport.convertJcrToBean(this.session, node, LazyType.EAGER);
                    return settings;
                }
                return null;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }

        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#getRepositoryByName(java.lang.String)
         */
        public Repository getRepositoryByName( final String name ) throws ConfigurationException {
            try {
                final Set<Repository> repository = SimplePersistSupport.findNodesByProperties(REPOSITORIES_LOCATION,
                                                                                              this.session, Repository.class,
                                                                                              LazyType.EAGER,
                                                                                              new String[] {"name"},
                                                                                              new Object[] {name});
                return repository.iterator().next();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#saveGlobalSettings(org.openspotlight.federation.domain.GlobalSettings)
         */
        public void saveGlobalSettings( final GlobalSettings globalSettings ) throws ConfigurationException {
            try {
                SimplePersistSupport.convertBeanToJcr(GLOBAL_SETTINGS_LOCATION, this.session, globalSettings);
                this.session.save();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /* (non-Javadoc)
         * @see org.openspotlight.federation.loader.ConfigurationManager#saveRepository(org.openspotlight.federation.domain.Repository)
         */
        public void saveRepository( final Repository configuration ) throws ConfigurationException {
            try {
                SimplePersistSupport.convertBeanToJcr(REPOSITORIES_LOCATION, this.session, configuration);
                this.session.save();
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

    }

    /**
     * Creates a new JcrSessionConfigurationManager object.
     * 
     * @param session the session
     * @return the configuration manager
     */
    public static ConfigurationManager createMutableUsingSession( final Session session ) {
        return new MutableJcrSessionConfigurationManager(session);
    }

}
