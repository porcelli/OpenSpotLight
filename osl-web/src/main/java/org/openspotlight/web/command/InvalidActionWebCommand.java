package org.openspotlight.web.command;

import java.util.Map;

public class InvalidActionWebCommand implements WebCommand {

    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        return "{error : 'Invalid Action " + parameters.get("action") + "'}";
    }

}
