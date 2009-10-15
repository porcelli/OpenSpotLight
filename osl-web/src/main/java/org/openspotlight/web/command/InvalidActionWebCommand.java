package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.web.WebException;

/**
 * The Class InvalidActionWebCommand.
 */
public class InvalidActionWebCommand implements WebCommand {

    /* (non-Javadoc)
     * @see org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web.command.WebCommand.WebCommandContext, java.util.Map)
     */
    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        return "{error : 'Invalid Action " + parameters.get("action") + "'}";
    }

}
