package org.openspotlight.web;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
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
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.web.command.WebCommand;
import org.openspotlight.web.command.WebCommand.WebCommandContext;

public class CoreServlet extends HttpServlet {

    private static class CommandLoader {

        private final Properties              properties;
        private final Map<String, WebCommand> commandCache = new TreeMap<String, WebCommand>();

        public CommandLoader() {
            try {
                final InputStream inputStream = ClassPathResource.getResourceFromClassPath("actions.properties");
                this.properties = new Properties();
                this.properties.load(inputStream);
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }

        public synchronized WebCommand loadCommand( final String actionName ) {
            try {
                String newActionName = actionName;
                String className = actionName != null ? this.properties.getProperty(actionName) : null;
                if (className == null) {
                    newActionName = "invalidAction";
                }
                WebCommand loaded = this.commandCache.get(newActionName);
                if (loaded == null) {
                    className = this.properties.getProperty(newActionName);
                    @SuppressWarnings( "unchecked" )
                    final Class<? extends WebCommand> commandClass = (Class<? extends WebCommand>)Class.forName(className);
                    loaded = commandClass.newInstance();
                    this.commandCache.put(newActionName, loaded);
                }
                return loaded;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);

            }
        }
    }

    /**
     * 
     */
    private static final long   serialVersionUID = -909328553298519604L;

    private final CommandLoader loader           = new CommandLoader();

    protected void doAction( final HttpServletRequest req,
                             final HttpServletResponse resp ) {
        try {
            final String action = req.getParameter("action");
            final WebCommand command = this.loader.loadCommand(action);
            final Map<String, String> parameters = new TreeMap<String, String>();
            final Enumeration<?> names = req.getParameterNames();
            while (names.hasMoreElements()) {
                final String name = (String)names.nextElement();
                parameters.put(name, req.getParameter(name));
            }
            final SLGraph graph = OslServletContextSupport.getGraphFrom(this.getServletContext());
            final JcrConnectionProvider provider = OslServletContextSupport.getJcrConnectionFrom(this.getServletContext());
            final SLGraphSession session = graph.openSession();
            final WebCommandContext context = new WebCommandContext(session, provider);

            String result;
            try {
                result = command.execute(context, parameters);
            } catch (final WebException e) {
                result = e.toJsonString();
            } finally {
                session.close();
            }
            resp.getOutputStream().print(result);
            resp.getOutputStream().flush();
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    @Override
    protected void doGet( final HttpServletRequest req,
                          final HttpServletResponse resp ) throws ServletException, IOException {
        this.doAction(req, resp);
    }

    @Override
    protected void doPost( final HttpServletRequest req,
                           final HttpServletResponse resp ) throws ServletException, IOException {
        this.doAction(req, resp);
    }

    @Override
    public void init( final ServletConfig config ) throws ServletException {
        config.getServletContext();

        super.init(config);
    }
}
