package org.openspotlight.web.command;

import java.util.Map;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public interface WebCommand {

    public static class WebCommandContext {
        private final SLGraphSession        session;
        private final JcrConnectionProvider provider;

        public WebCommandContext(
                                  final SLGraphSession session, final JcrConnectionProvider provider ) {
            this.session = session;
            this.provider = provider;
        }

        protected JcrConnectionProvider getProvider() {
            return this.provider;
        }

        protected SLGraphSession getSession() {
            return this.session;
        }

    }

    String execute( WebCommandContext context,
                    Map<String, String> parameters ) throws WebException;

}
