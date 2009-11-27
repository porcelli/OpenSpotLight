package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;

/**
 * The Class ListBundleNamesWebCommand.
 */
public class ListBundleNamesWebCommand implements WebCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web
	 * .command.WebCommand.WebCommandContext, java.util.Map)
	 */
	public String execute(final WebCommandContext context,
			final Map<String, String> parameters) throws WebException {
		try {
			// final Session jcrSession = context.getJcrSession();
			// final ConfigurationManager manager = new
			// JcrSessionConfigurationManager(jcrSession);
			// final GlobalSettings configuration = manager.load(LazyType.LAZY);
			// final Set<ArtifactSource> allBundles =
			// ConfigurationNodes.findAllNodesOfType(configuration,
			// ArtifactSource.class);
			// final Set<String> allBundlesAsString = new HashSet<String>();
			// for (final ArtifactSource b : allBundles) {
			// allBundlesAsString.add(b.getName());
			// }
			// final JSONArray allBundlesAsJson =
			// JSONArray.fromObject(allBundlesAsString);
			// return allBundlesAsJson.toString();
			return "";
		} catch (final Exception e) {
			Exceptions.catchAndLog(e);
			throw new MessageWebException(
					"There's something wrong during the initial data import: "
							+ e.getMessage());
		}
	}
}
