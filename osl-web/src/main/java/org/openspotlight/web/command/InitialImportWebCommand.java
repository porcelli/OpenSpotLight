package org.openspotlight.web.command;

import static org.openspotlight.common.util.Exceptions.catchAndLog;

import java.io.InputStream;
import java.util.Map;

import javax.jcr.Session;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.NoConfigurationYetException;
import org.openspotlight.federation.data.InstanceMetadata.ConfigurationNodeVisitor;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;
import org.openspotlight.federation.data.load.XmlConfigurationManager;

public class InitialImportWebCommand implements WebCommand {

    @SuppressWarnings( "boxing" )
    public String execute( final WebCommandContext context,
                           final Map<String, String> parameters ) throws WebException {
        try {
            final Session jcrSession = context.getJcrSession();
            final ConfigurationManager manager = new JcrSessionConfigurationManager(jcrSession);
            Configuration configuration = null;
            final String forceReloadString = parameters.get("forceReload");
            final boolean forceReload = forceReloadString == null ? false : Boolean.valueOf(forceReloadString);
            boolean firstTime = false;
            try {
                configuration = manager.load(LazyType.LAZY);
            } catch (final NoConfigurationYetException e) {
                firstTime = true;
            }
            boolean reloaded = false;
            if (firstTime || forceReload) {
                configuration = this.saveXmlOnJcr(manager);
                reloaded = true;
            }
            return "{message:'" + (reloaded ? "was" : "was not") + " reloaded'}";
        } catch (final Exception e) {
            catchAndLog(e);
            throw new MessageWebException("There's something wrong during the initial data import: " + e.getMessage());
        }
    }

    private Configuration saveXmlOnJcr( final ConfigurationManager manager ) throws SLException {
        Configuration configuration;
        final InputStream is = ClassPathResource.getResourceFromClassPath("osl-configuration.xml");
        final XmlConfigurationManager xmlManager = new XmlConfigurationManager(is);
        configuration = xmlManager.load(LazyType.EAGER);
        configuration.getInstanceMetadata().accept(new ConfigurationNodeVisitor() {
            public void visitNode( final ConfigurationNode node ) {
                node.getInstanceMetadata().getSharedData().fireNodeChange(node, node);
            }
        });
        manager.save(configuration);
        return configuration;
    }
}
