package org.openspotlight.web.command;

import java.util.Map;

public interface WebCommand {

    public static class WebCommandContext {

    }

    String execute( WebCommandContext context,
                    Map<String, String> parameters ) throws WebException;

}
