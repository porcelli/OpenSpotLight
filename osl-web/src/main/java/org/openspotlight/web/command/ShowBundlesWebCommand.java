package org.openspotlight.web.command;

import java.util.Map;
import java.util.Set;

import javax.jcr.Session;

import net.sf.json.JSONObject;

import org.openspotlight.common.LazyType;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.util.ConfigurationNodes;

public class ShowBundlesWebCommand implements WebCommand {

    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        final Session jcrSession = context.getJcrSession();
        final ConfigurationManager manager = new JcrSessionConfigurationManager(jcrSession);
        final Configuration configuration = manager.load(LazyType.LAZY);
        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(configuration, Bundle.class);
        return JSONObject.fromObject(bundles).toString();
    }

}
