package org.openspotlight.web.command;

import java.util.Map;

import javax.jcr.Session;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public interface WebCommand {

    public static class WebCommandContext {
        private final SLGraphSession        graphSession;
        private final JcrConnectionProvider provider;
        private volatile Session            jcrSession;

        public WebCommandContext(
                                  final SLGraphSession session, final JcrConnectionProvider provider ) {
            this.graphSession = session;
            this.provider = provider;
        }

        public SLGraphSession getGraphSession() {
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

    }

    String execute( WebCommandContext context,
                    Map<String, String> parameters ) throws WebException;

}
