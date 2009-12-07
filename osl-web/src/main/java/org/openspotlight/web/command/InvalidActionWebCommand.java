package org.openspotlight.web.command;

import java.text.MessageFormat;
import java.util.Map;

import net.sf.json.JSONObject;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.web.WebException;
import org.openspotlight.web.json.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class InvalidActionWebCommand.
 */
public class InvalidActionWebCommand implements WebCommand {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web
	 * .command.WebCommand.WebCommandContext, java.util.Map)
	 */
	public String execute(final ExecutionContext context,
			final Map<String, String> parameters) throws WebException {
		final Message message = new Message();
		message.setMessage("Invalid Action " + parameters.get("action")
				+ "! See log files for more details");
		logger
				.warn(MessageFormat
						.format(
								"Invalid action {0}! To use the actions in a correct way, take a look on actions.properties and access using a url like localhost:8080/osl-web/?action=actionName",
								parameters.get("action")));
		return JSONObject.fromObject(message).toString();
	}

}
