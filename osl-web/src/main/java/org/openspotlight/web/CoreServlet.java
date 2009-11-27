package org.openspotlight.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.web.command.WebCommand;
import org.openspotlight.web.command.WebCommand.WebCommandContext;

/**
 * The Class CoreServlet is used to load {@link WebCommand web commands} by its
 * actions.
 */
public class CoreServlet extends HttpServlet {

	/**
	 * The Class CommandLoader.
	 */
	private static class CommandLoader {

		/** The properties. */
		private final Properties properties;

		/** The command cache. */
		private final Map<String, WebCommand> commandCache = new TreeMap<String, WebCommand>();

		/**
		 * Instantiates a new command loader.
		 */
		public CommandLoader() {
			try {
				final InputStream inputStream = ClassPathResource
						.getResourceFromClassPath("actions.properties");
				this.properties = new Properties();
				this.properties.load(inputStream);
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);
			}
		}

		/**
		 * Load command.
		 * 
		 * @param actionName
		 *            the action name
		 * @return the web command
		 */
		public synchronized WebCommand loadCommand(final String actionName) {
			try {
				String newActionName = actionName;
				String className = actionName != null ? this.properties
						.getProperty(actionName) : null;
				if (className == null) {
					newActionName = "invalidAction";
				}
				WebCommand loaded = this.commandCache.get(newActionName);
				if (loaded == null) {
					className = this.properties.getProperty(newActionName);
					@SuppressWarnings("unchecked")
					final Class<? extends WebCommand> commandClass = (Class<? extends WebCommand>) Class
							.forName(className);
					loaded = commandClass.newInstance();
					this.commandCache.put(newActionName, loaded);
				}
				return loaded;
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e,
						ConfigurationException.class);

			}
		}
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -909328553298519604L;

	/** The loader. */
	private final CommandLoader loader = new CommandLoader();

	/**
	 * Do action.
	 * 
	 * @param req
	 *            the req
	 * @param resp
	 *            the resp
	 */
	protected void doAction(final HttpServletRequest req,
			final HttpServletResponse resp) {
		final WebCommandContext context = null;
		try {
			// final SLGraph graph =
			// OslServletContextSupport.getGraphFrom(this.getServletContext());
			// final JcrConnectionProvider provider =
			// OslServletContextSupport.getJcrConnectionFrom(this.getServletContext());
			// final Scheduler scheduler =
			// OslServletContextSupport.getSchedulerFrom(this.getServletContext());
			// final AuthenticatedUser user =
			// OslServletContextSupport.getAuthenticatedUerFrom(this.getServletContext());
			// context = new WebCommandContext(user, graph, provider,
			// scheduler);
			// final String action = req.getParameter("action");
			// final WebCommand command = this.loader.loadCommand(action);
			// final Map<String, String> parameters = new TreeMap<String,
			// String>();
			// final Enumeration<?> names = req.getParameterNames();
			// while (names.hasMoreElements()) {
			// final String name = (String)names.nextElement();
			// parameters.put(name, req.getParameter(name));
			// }
			//
			// String result;
			// try {
			// result = command.execute(context, parameters);
			// } catch (final WebException e) {
			// result = e.toJsonString();
			// }
			// resp.getOutputStream().print(result);
			// resp.getOutputStream().flush();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		} finally {
			if (context != null) {
				context.closeResources();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		this.doAction(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		this.doAction(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		config.getServletContext();

		super.init(config);
	}
}
