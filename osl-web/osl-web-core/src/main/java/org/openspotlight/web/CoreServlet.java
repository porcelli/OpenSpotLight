/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.web;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.ClassPathResource;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.web.command.WebCommand;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

/**
 * The Class CoreServlet is used to load {@link WebCommand web commands} by its actions.
 */
public class CoreServlet extends HttpServlet {

    /**
     * The Class CommandLoader.
     */
    private static class CommandLoader {

        /** The properties. */
        private final Properties              properties;

        /** The command cache. */
        private final Map<String, WebCommand> commandCache = new TreeMap<String, WebCommand>();

        /**
         * Instantiates a new command loader.
         */
        public CommandLoader() {
            try {
                final InputStream inputStream = ClassPathResource.getResourceFromClassPath("actions.properties");
                properties = new Properties();
                properties.load(inputStream);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
            }
        }

        /**
         * Load command.
         * 
         * @param actionName the action name
         * @return the web command
         */
        public synchronized WebCommand loadCommand( final String actionName ) {
            try {
                String newActionName = actionName;
                String className = actionName != null ? properties.getProperty(actionName) : null;
                if (className == null) {
                    newActionName = "invalidAction";
                }
                WebCommand loaded = commandCache.get(newActionName);
                if (loaded == null) {
                    className = properties.getProperty(newActionName);
                    @SuppressWarnings( "unchecked" )
                    final Class<? extends WebCommand> commandClass = (Class<? extends WebCommand>)Class.forName(className);
                    loaded = commandClass.newInstance();
                    commandCache.put(newActionName, loaded);
                }
                return loaded;
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, ConfigurationException.class);

            }
        }
    }

    /** The Constant serialVersionUID. */
    private static final long   serialVersionUID = -909328553298519604L;

    /** The loader. */
    private final CommandLoader loader           = new CommandLoader();

    /**
     * Do action.
     * 
     * @param req the req
     * @param resp the resp
     */
    protected void doAction( final HttpServletRequest req,
                             final HttpServletResponse resp ) {
        ExecutionContext context = null;
        try {
            context = WebExecutionContextFactory.INSTANCE.createExecutionContext(getServletContext(), req);
            final String action = req.getParameter("action");
            final WebCommand command = loader.loadCommand(action);
            final Map<String, String> parameters = new TreeMap<String, String>();
            final Enumeration<?> names = req.getParameterNames();
            while (names.hasMoreElements()) {
                final String name = (String)names.nextElement();
                parameters.put(name, req.getParameter(name));
            }

            String result;
            try {
                result = command.execute(context, parameters);
            } catch (final WebException e) {
                result = e.toJsonString();
            }
            resp.getOutputStream().print(result);
            resp.getOutputStream().flush();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        } finally {
            if (context != null) {
                context.closeResources();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet( final HttpServletRequest req,
                          final HttpServletResponse resp ) throws ServletException, IOException {
        doAction(req, resp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost( final HttpServletRequest req,
                           final HttpServletResponse resp ) throws ServletException, IOException {
        doAction(req, resp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( final ServletConfig config ) throws ServletException {
        config.getServletContext();
        super.init(config);
    }
}
