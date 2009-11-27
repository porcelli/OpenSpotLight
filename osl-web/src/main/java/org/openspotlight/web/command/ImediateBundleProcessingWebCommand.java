package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;

/**
 * The Class ImediateBundleProcessingWebCommand.
 */
public class ImediateBundleProcessingWebCommand implements WebCommand {

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
			// final Scheduler scheduler = context.getScheduler();
			// final ConfigurationManager manager = new
			// JcrSessionConfigurationManager(context.getJcrSession());
			// final GlobalSettings configuration = manager.load(LazyType.LAZY);
			// final Set<ArtifactSource> allBundles =
			// ConfigurationNodes.findAllNodesOfType(configuration,
			// ArtifactSource.class);
			//
			// scheduler.fireImmediateExecution(allBundles.toArray(new
			// ArtifactSource[] {}));
			return "fired";
		} catch (final Exception e) {
			Exceptions.catchAndLog(e);
			throw new MessageWebException(
					"There's something wrong during the initial data import: "
							+ e.getMessage());
		}

	}

}
