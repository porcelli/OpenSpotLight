package org.openspotlight.web.command;

import gnu.cajo.utils.extra.Scheduler;

import java.util.Map;

import javax.jcr.Session;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.web.WebException;

/**
 * The Interface WebCommand.
 */
public interface WebCommand {

	/**
	 * The Class WebCommandContext.
	 */
	public static class WebCommandContext {

		/** The graph session. */
		private volatile SLGraphSession graphSession;

		/** The provider. */
		private final JcrConnectionProvider provider;

		/** The jcr session. */
		private volatile Session jcrSession;

		/** The scheduler. */
		private final Scheduler scheduler;

		/** The graph. */
		private final SLGraph graph;

		private final AuthenticatedUser user;

		/**
		 * Instantiates a new web command context.
		 * 
		 * @param graph
		 *            the graph
		 * @param provider
		 *            the provider
		 * @param scheduler
		 *            the scheduler
		 */
		public WebCommandContext(final AuthenticatedUser user,
				final SLGraph graph, final JcrConnectionProvider provider,
				final Scheduler scheduler) {
			this.graph = graph;
			this.provider = provider;
			this.scheduler = scheduler;
			this.user = user;
		}

		/**
		 * Close resources.
		 */
		public void closeResources() {
			if (this.graphSession != null) {
				this.graphSession.close();
				this.graphSession = null;
			}
			if (this.jcrSession != null) {
				this.jcrSession.logout();
				this.jcrSession = null;
			}
		}

		/**
		 * Gets the graph session.
		 * 
		 * @return the graph session
		 */
		public SLGraphSession getGraphSession() {
			if (this.graphSession == null) {
				try {
					this.graphSession = this.graph.openSession(this.user);
				} catch (final Exception e) {
					throw Exceptions.logAndReturnNew(e,
							SLRuntimeException.class);
				}
			}
			return this.graphSession;
		}

		/**
		 * Gets the jcr session.
		 * 
		 * @return the jcr session
		 */
		public Session getJcrSession() {
			if (this.jcrSession == null) {
				this.jcrSession = this.provider.openSession();
			}
			return this.jcrSession;
		}

		/**
		 * Gets the provider.
		 * 
		 * @return the provider
		 */
		public JcrConnectionProvider getProvider() {
			return this.provider;
		}

		/**
		 * Gets the scheduler.
		 * 
		 * @return the scheduler
		 */
		public Scheduler getScheduler() {
			return this.scheduler;
		}

	}

	/**
	 * Execute.
	 * 
	 * @param context
	 *            the context
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws WebException
	 *             the web exception
	 */
	String execute(WebCommandContext context, Map<String, String> parameters)
			throws WebException;

}
