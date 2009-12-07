package org.openspotlight.web.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.federation.loader.XmlConfigurationManagerFactory;

/**
 * The Class ConfigurationSupport contains methods to be used on
 * {@link ConfigurationManager} saved data.
 */
public class ConfigurationSupport {

	private static class ActualConfiguration {
		private Set<Repository> repositories;
		private GlobalSettings settings;

		public Set<Repository> getRepositories() {
			return repositories;
		}

		public GlobalSettings getSettings() {
			return settings;
		}

		public void setRepositories(final Set<Repository> repositories) {
			this.repositories = repositories;
		}

		public void setSettings(final GlobalSettings settings) {
			this.settings = settings;
		}
	}

	/**
	 * Initialize configuration.
	 * 
	 * @param forceReload
	 *            the force reload
	 * @param jcrSession
	 *            the jcr session
	 * @return true, if successful
	 * @throws SLException
	 *             the SL exception
	 */
	public static boolean initializeConfiguration(final boolean forceReload,
			final ConfigurationManager jcrConfigurationManager)
			throws Exception {
		final boolean firstTime = jcrConfigurationManager.getAllRepositories()
				.size() == 0;
		boolean reloaded = false;
		if (firstTime || forceReload) {
			saveXmlOnJcr(jcrConfigurationManager);
			reloaded = true;
		}
		return reloaded;
	}

	/**
	 * Save xml on jcr.
	 * 
	 * @param manager
	 *            the manager
	 * @return the configuration
	 * @throws SLException
	 *             the SL exception
	 */
	private static ActualConfiguration saveXmlOnJcr(
			final ConfigurationManager manager) throws Exception {
		GlobalSettings settings;
		Set<Repository> repositories;
		final InputStream is = ClassPathResource
				.getResourceFromClassPath("osl-configuration.xml");
		final StringWriter writter = new StringWriter();
		IOUtils.copy(is, writter);
		final String xmlContent = writter.toString();
		is.close();
		final ConfigurationManager xmlManager = XmlConfigurationManagerFactory
				.loadImmutableFromXmlContent(xmlContent);
		settings = xmlManager.getGlobalSettings();
		repositories = xmlManager.getAllRepositories();

		manager.saveGlobalSettings(settings);
		for (final Repository repository : repositories) {
			manager.saveRepository(repository);
		}
		final ActualConfiguration configuration2 = new ActualConfiguration();
		configuration2.setRepositories(repositories);
		configuration2.setSettings(settings);
		return configuration2;
	}
}
