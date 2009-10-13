package org.openspotlight.web;

import javax.servlet.ServletContext;

import org.openspotlight.federation.scheduler.Scheduler;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class OslServletContextSupport implements ServletContextConstants {
    public static SLGraph getGraphFrom( final ServletContext ctx ) {
        return (SLGraph)ctx.getAttribute(GRAPH);
    }

    public static JcrConnectionProvider getJcrConnectionFrom( final ServletContext ctx ) {
        return (JcrConnectionProvider)ctx.getAttribute(PROVIDER);
    }

    public static Scheduler getSchedulerFrom( final ServletContext ctx ) {
        return (Scheduler)ctx.getAttribute(SCHEDULER);
    }
}
