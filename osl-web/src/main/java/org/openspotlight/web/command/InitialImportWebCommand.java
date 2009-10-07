package org.openspotlight.web.command;

import static org.openspotlight.common.util.Exceptions.catchAndLog;

import java.util.Map;

import javax.jcr.Session;

import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;
import org.openspotlight.web.util.ConfigurationSupport;

public class InitialImportWebCommand implements WebCommand {

    @SuppressWarnings( "boxing" )
    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        try {
            final Session jcrSession = context.getJcrSession();
            final String forceReloadString = parameters.get("forceReload");
            final boolean forceReload = forceReloadString == null ? false : Boolean.valueOf(forceReloadString);
            final boolean reloaded = ConfigurationSupport.initializeConfiguration(forceReload, jcrSession);
            return "{message:'" + (reloaded ? "was" : "was not") + " reloaded'}";
        } catch (final Exception e) {
            catchAndLog(e);
            throw new MessageWebException("There's something wrong during the initial data import: " + e.getMessage());
        }
    }

}
