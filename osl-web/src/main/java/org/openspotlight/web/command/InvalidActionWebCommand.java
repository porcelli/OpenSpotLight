package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.web.WebException;

public class InvalidActionWebCommand implements WebCommand {

    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        return "{error : 'Invalid Action " + parameters.get("action") + "'}";
    }

}
