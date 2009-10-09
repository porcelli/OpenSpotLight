package org.openspotlight.web;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import javax.jcr.Session;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.util.JcrConfigurationManagerProvider;
import org.openspotlight.federation.scheduler.Scheduler;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.web.util.ConfigurationSupport;

public class OslContextListener implements ServletContextListener, ServletContextConstants {

    public void contextDestroyed( final ServletContextEvent arg0 ) {
        final JcrConnectionProvider provider = OslServletContextSupport.getJcrConnectionFrom(arg0.getServletContext());
        final SLGraph graph = OslServletContextSupport.getGraphFrom(arg0.getServletContext());
        final Scheduler scheduler = OslServletContextSupport.getSchedulerFrom(arg0.getServletContext());
        graph.shutdown();
        provider.closeRepository();
        scheduler.shutdown();
    }

    public void contextInitialized( final ServletContextEvent arg0 ) {
        try {
            final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.DEFAULT_DESCRIPTOR);
            final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            final SLGraph graph = factory.createGraph(provider);
            final Scheduler scheduler = Scheduler.Factory.createScheduler();

            final Session jcrSession = provider.openSession();
            ConfigurationSupport.initializeConfiguration(false, jcrSession);
            jcrSession.logout();
            final JcrConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
            scheduler.setBundleProcessorManager(new BundleProcessorManager(provider, configurationManagerProvider));
            scheduler.setConfigurationManagerProvider(configurationManagerProvider);
            scheduler.start();
            arg0.getServletContext().setAttribute(SCHEDULER, scheduler);
            arg0.getServletContext().setAttribute(PROVIDER, provider);
            arg0.getServletContext().setAttribute(GRAPH, graph);
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

}
