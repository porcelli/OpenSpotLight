package org.openspotlight.web.command;

import static org.openspotlight.common.util.Exceptions.catchAndLog;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Session;

import net.sf.json.JSONArray;

import org.openspotlight.common.LazyType;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.util.ConfigurationNodes;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;

/**
 * The Class ListBundleNamesWebCommand.
 */
public class ListBundleNamesWebCommand implements WebCommand {

    /* (non-Javadoc)
     * @see org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web.command.WebCommand.WebCommandContext, java.util.Map)
     */
    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        try {
            final Session jcrSession = context.getJcrSession();
            final ConfigurationManager manager = new JcrSessionConfigurationManager(jcrSession);
            final Configuration configuration = manager.load(LazyType.LAZY);
            final Set<Bundle> allBundles = ConfigurationNodes.findAllNodesOfType(configuration, Bundle.class);
            final Set<String> allBundlesAsString = new HashSet<String>();
            for (final Bundle b : allBundles) {
                allBundlesAsString.add(b.getName());
            }
            final JSONArray allBundlesAsJson = JSONArray.fromObject(allBundlesAsString);
            return allBundlesAsJson.toString();
        } catch (final Exception e) {
            catchAndLog(e);
            throw new MessageWebException("There's something wrong during the initial data import: " + e.getMessage());
        }
    }
}
