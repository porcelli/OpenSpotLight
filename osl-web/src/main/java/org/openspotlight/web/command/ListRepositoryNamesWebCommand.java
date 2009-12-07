package org.openspotlight.web.command;

import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;

/**
 * The Class ListBundleNamesWebCommand.
 */
public class ListRepositoryNamesWebCommand implements WebCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web
	 * .command.WebCommand.WebCommandContext, java.util.Map)
	 */
	public String execute(final ExecutionContext context,
			final Map<String, String> parameters) throws WebException {
		try {
			final Set<String> repositryNames = context
					.getDefaultConfigurationManager().getAllRepositoryNames();
			final JSONArray json = JSONArray.fromObject(repositryNames
					.toArray());
			return json.toString();
		} catch (final Exception e) {
			Exceptions.catchAndLog(e);
			throw new MessageWebException(
					"There's something wrong during the initial data import: "
							+ e.getMessage());
		}
	}
}
