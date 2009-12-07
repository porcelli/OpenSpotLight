package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.web.WebException;

/**
 * The Interface WebCommand.
 */
public interface WebCommand {

	/**
	 * Execute.
	 * 
	 * @param context
	 *            the context
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws WebException
	 *             the web exception
	 */
	String execute(ExecutionContext context, Map<String, String> parameters)
			throws WebException;

}
