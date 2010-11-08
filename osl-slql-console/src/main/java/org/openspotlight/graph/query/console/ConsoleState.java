/**
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
package org.openspotlight.graph.query.console;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.query.console.command.Command;

/**
 * The Class ConsoleState. This is a simple data class that holds console application state.
 * 
 * @author porcelli
 */
public class ConsoleState {

    /** The active command. */
    private Command       activeCommand        = null;

    /**
     * The additional properties that should be displayed during query output result.
     */
    private Set<String>   additionalProperties = new HashSet<String>();

    /** The session. */
    private GraphReader   graphReader          = null;

    /** The input. */
    private String        input                = "";

    /** The last query. */
    private String        lastQuery            = "";

    /** The quit application. */
    private boolean       quitApplication      = false;

    /** The StringBuilder that holds the buffer. */
    private StringBuilder sb                   = new StringBuilder();

    /**
     * Instantiates a new console state.
     * 
     * @param session the session
     */
    public ConsoleState(final GraphReader graphReader) {
        this.graphReader = graphReader;
    }

    /**
     * Adds the additional property that should be displayed during query output result.
     * 
     * @param additionalProperty the additional property
     */
    public void addAdditionalProperty(final String additionalProperty) {
        if (additionalProperty.trim().length() > 0) {
            additionalProperties.add(additionalProperty);
        }
    }

    /**
     * Append buffer.
     * 
     * @param buffer the buffer
     */
    public void appendBuffer(final String buffer) {
        sb.append(buffer);
    }

    /**
     * Append line buffer.
     * 
     * @param buffer the buffer
     */
    public void appendLineBuffer(final String buffer) {
        appendBuffer(buffer);
        appendBuffer("\n");
    }

    /**
     * Clear buffer.
     */
    public void clearBuffer() {
        sb = new StringBuilder();
    }

    /**
     * Gets the active command.
     * 
     * @return the active command
     */
    public Command getActiveCommand() {
        return activeCommand;
    }

    /**
     * Gets the additional properties that should be displayed during query output result.
     * 
     * @return the additional properties
     */
    public Collection<String> getAdditionalProperties() {
        return additionalProperties;
    }

    /**
     * Gets the buffer.
     * 
     * @return the buffer
     */
    public String getBuffer() {
        return sb.toString();
    }

    /**
     * Gets the input.
     * 
     * @return the input
     */
    public String getInput() {
        return input;
    }

    /**
     * Gets the last query.
     * 
     * @return the last query
     */
    public String getLastQuery() {
        return lastQuery;
    }

    /**
     * Gets the session.
     * 
     * @return the session
     */
    public GraphReader getSession() {
        return graphReader;
    }

    /**
     * Quit application.
     * 
     * @return true, if application should quit
     */
    public boolean quitApplication() {
        return quitApplication;
    }

    /**
     * Removes additional property from query output result.
     * 
     * @param additionalProperty the additional property
     */
    public void removesAdditionalProperty(final String additionalProperty) {
        if (additionalProperty.trim().length() > 0
                && additionalProperties.contains(additionalProperty)) {
            additionalProperties.remove(additionalProperty);
        }
    }

    /**
     * Reset additional properties.
     */
    public void resetAdditionalProperties() {
        additionalProperties = new HashSet<String>();
    }

    /**
     * Sets the active command.
     * 
     * @param activeCommand the new active command
     */
    public void setActiveCommand(final Command activeCommand) {
        this.activeCommand = activeCommand;
    }

    /**
     * Sets the input.
     * 
     * @param input the new input
     */
    public void setInput(final String input) {
        if (input == null) {
            this.input = "";
        } else {
            this.input = input;
        }
    }

    /**
     * Sets the last query.
     * 
     * @param lastQuery the new last query
     */
    public void setLastQuery(final String lastQuery) {
        if (lastQuery == null) {
            this.lastQuery = "";
        } else {
            this.lastQuery = lastQuery;
        }
    }

    /**
     * Sets the quit application.
     * 
     * @param quitApplication the new quit application
     */
    public void setQuitApplication(final boolean quitApplication) {
        this.quitApplication = quitApplication;
    }

}
