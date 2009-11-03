package org.openspotlight.web;

import javax.servlet.ServletContext;

import org.openspotlight.federation.scheduler.Scheduler;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.idm.AuthenticatedUser;

/**
 * The Class OslServletContextSupport contains methods to retrieve some useful objects from {@link ServletContext}.
 */
public class OslServletContextSupport implements ServletContextConstants {

    public static AuthenticatedUser getAuthenticatedUerFrom( final ServletContext ctx ) {
        return (AuthenticatedUser)ctx.getAttribute(AUTHENTICATED_USER);
    }

    /**
     * Gets the graph from.
     * 
     * @param ctx the ctx
     * @return the graph from
     */
    public static SLGraph getGraphFrom( final ServletContext ctx ) {
        return (SLGraph)ctx.getAttribute(GRAPH);
    }

    /**
     * Gets the jcr connection from.
     * 
     * @param ctx the ctx
     * @return the jcr connection from
     */
    public static JcrConnectionProvider getJcrConnectionFrom( final ServletContext ctx ) {
        return (JcrConnectionProvider)ctx.getAttribute(PROVIDER);
    }

    /**
     * Gets the scheduler from.
     * 
     * @param ctx the ctx
     * @return the scheduler from
     */
    public static Scheduler getSchedulerFrom( final ServletContext ctx ) {
        return (Scheduler)ctx.getAttribute(SCHEDULER);
    }
}
