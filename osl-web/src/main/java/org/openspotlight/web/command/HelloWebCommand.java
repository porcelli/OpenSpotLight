package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.web.WebException;

public class HelloWebCommand implements WebCommand {

    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        return "{message : 'Hello World!'}";
    }

}
