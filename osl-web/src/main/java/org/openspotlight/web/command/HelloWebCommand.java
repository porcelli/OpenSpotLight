package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.web.WebException;

// TODO: Auto-generated Javadoc
/**
 * The Class HelloWebCommand.
 */
public class HelloWebCommand implements WebCommand {

    /* (non-Javadoc)
     * @see org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web.command.WebCommand.WebCommandContext, java.util.Map)
     */
    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        return "{message : 'Hello World!'}";
    }

}
