package org.openspotlight.web;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import javax.jcr.Session;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.manager.JcrConfigurationManagerProvider;
import org.openspotlight.federation.scheduler.Scheduler;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.web.util.ConfigurationSupport;

/**
 * The listener interface for receiving oslContext events. The class that is interested in processing a oslContext event
 * implements this interface, and the object created with that class is registered with a component using the component's
 * <code>addOslContextListener<code> method. When
 * the oslContext event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see OslContextEvent
 */
public class OslContextListener implements ServletContextListener, ServletContextConstants {

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed( final ServletContextEvent arg0 ) {
        final JcrConnectionProvider provider = OslServletContextSupport.getJcrConnectionFrom(arg0.getServletContext());
        final SLGraph graph = OslServletContextSupport.getGraphFrom(arg0.getServletContext());
        final Scheduler scheduler = OslServletContextSupport.getSchedulerFrom(arg0.getServletContext());
        graph.shutdown();
        provider.closeRepository();
        scheduler.shutdown();
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized( final ServletContextEvent arg0 ) {
        try {
            //FIXME create a login process and a per user configuration stored in a web session
            final DefaultJcrDescriptor descriptor = DefaultJcrDescriptor.DEFAULT_DESCRIPTOR;
            final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");
            final AuthenticatedUser user = securityFactory.createIdentityManager(descriptor).authenticate(simpleUser, "password");

            final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(descriptor);
            final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            final SLGraph graph = factory.createGraph(descriptor);
            final Scheduler scheduler = Scheduler.Factory.createScheduler();

            final Session jcrSession = provider.openSession();
            ConfigurationSupport.initializeConfiguration(false, jcrSession);
            jcrSession.logout();
            final JcrConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
            scheduler.setBundleProcessorManager(new BundleProcessorManager(user, provider, configurationManagerProvider));
            scheduler.setConfigurationManagerProvider(configurationManagerProvider);
            scheduler.start();
            arg0.getServletContext().setAttribute(SCHEDULER, scheduler);
            arg0.getServletContext().setAttribute(PROVIDER, provider);
            arg0.getServletContext().setAttribute(GRAPH, graph);
            arg0.getServletContext().setAttribute(AUTHENTICATED_USER, user);
            arg0.getServletContext().setAttribute(JCR_DESCRIPTOR, descriptor);

        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

}
