package org.openspotlight.web.command;

import static org.openspotlight.common.util.Exceptions.catchAndLog;

import java.util.Map;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;
import org.openspotlight.web.util.ConfigurationSupport;

/**
 * The Class InitialImportWebCommand.
 */
public class InitialImportWebCommand implements WebCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web
	 * .command.WebCommand.WebCommandContext, java.util.Map)
	 */
	@SuppressWarnings("boxing")
	public String execute(final ExecutionContext context,
			final Map<String, String> parameters) throws WebException {
		try {
			final String forceReloadString = parameters.get("forceReload");
			final boolean forceReload = forceReloadString == null ? false
					: Boolean.valueOf(forceReloadString);
			final boolean reloaded = ConfigurationSupport
					.initializeConfiguration(forceReload, context
							.getDefaultConfigurationManager());
			return "{message:'" + (reloaded ? "was" : "was not")
					+ " reloaded'}";
		} catch (final Exception e) {
			catchAndLog(e);
			throw new MessageWebException(
					"There's something wrong during the initial data import: "
							+ e.getMessage());
		}
	}

}
