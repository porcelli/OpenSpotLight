package org.openspotlight.web.command;

import static org.openspotlight.common.util.Exceptions.catchAndLog;

import java.util.Map;
import java.util.Set;

import org.openspotlight.common.LazyType;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.util.ConfigurationNodes;
import org.openspotlight.federation.scheduler.Scheduler;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.WebException;

/**
 * The Class ImediateBundleProcessingWebCommand.
 */
public class ImediateBundleProcessingWebCommand implements WebCommand {

    /* (non-Javadoc)
     * @see org.openspotlight.web.command.WebCommand#execute(org.openspotlight.web.command.WebCommand.WebCommandContext, java.util.Map)
     */
    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        try {
            final Scheduler scheduler = context.getScheduler();
            final ConfigurationManager manager = new JcrSessionConfigurationManager(context.getJcrSession());
            final Configuration configuration = manager.load(LazyType.LAZY);
            final Set<Bundle> allBundles = ConfigurationNodes.findAllNodesOfType(configuration, Bundle.class);

            scheduler.fireImmediateExecution(allBundles.toArray(new Bundle[] {}));
            return "fired";
        } catch (final Exception e) {
            catchAndLog(e);
            throw new MessageWebException("There's something wrong during the initial data import: " + e.getMessage());
        }

    }

}
