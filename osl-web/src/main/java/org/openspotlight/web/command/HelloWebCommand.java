package org.openspotlight.web.command;

import java.util.Map;

import net.sf.json.JSONObject;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.web.WebException;

// TODO: Auto-generated Javadoc
/**
 * The Class HelloWebCommand.
 */
public class HelloWebCommand implements WebCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web
	 * .command.WebCommand.WebCommandContext, java.util.Map)
	 */
	public String execute(final ExecutionContext context,
			final Map<String, String> parameters) throws WebException {
		return JSONObject.fromObject("hello world!").toString();
	}

}
