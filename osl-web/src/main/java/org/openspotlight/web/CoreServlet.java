package org.openspotlight.web;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.web.command.WebCommand;
import org.openspotlight.web.command.WebException;

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
                String className = this.properties.getProperty(actionName);
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

    private final CommandLoader loader = new CommandLoader();

    protected void doAction( final HttpServletRequest req,
                             final HttpServletResponse resp ) throws ServletException, IOException {
        final String action = req.getParameter("action");
        final WebCommand command = this.loader.loadCommand(action);
        final Map<String, String> parameters = new TreeMap<String, String>();
        final Enumeration<?> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            final String name = (String)names.nextElement();
            parameters.put(name, req.getParameter(name));
        }

        String result;
        try {
            result = command.execute(parameters);
        } catch (final WebException e) {
            result = e.toJsonString();
        }
        resp.getOutputStream().print(result);
        resp.getOutputStream().flush();
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
}
