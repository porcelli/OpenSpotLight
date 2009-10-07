package org.openspotlight.web.command;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.Map;

import javax.jcr.Session;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.federation.scheduler.Scheduler;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.web.WebException;

public interface WebCommand {

    public static class WebCommandContext {
        private volatile SLGraphSession     graphSession;
        private final JcrConnectionProvider provider;
        private volatile Session            jcrSession;
        private final Scheduler             scheduler;
        private final SLGraph               graph;

        public WebCommandContext(
                                  final SLGraph graph, final JcrConnectionProvider provider, final Scheduler scheduler ) {
            this.graph = graph;
            this.provider = provider;
            this.scheduler = scheduler;
        }

        public void closeResources() {
            if (graphSession != null) {
                this.graphSession.close();
                this.graphSession = null;
            }
            if (jcrSession != null) {
                this.jcrSession.logout();
                this.jcrSession = null;
            }
        }

        public SLGraphSession getGraphSession() {
            if (this.graphSession == null) {
                try {
                    this.graphSession = this.graph.openSession();
                } catch (final SLGraphException e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);
                }
            }
            return this.graphSession;
        }

        public Session getJcrSession() {
            if (this.jcrSession == null) {
                this.jcrSession = this.provider.openSession();
            }
            return this.jcrSession;
        }

        public JcrConnectionProvider getProvider() {
            return this.provider;
        }

        public Scheduler getScheduler() {
            return this.scheduler;
        }

    }

    String execute( WebCommandContext context,
                    Map<String, String> parameters ) throws WebException;

}
