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
package org.openspotlight.graph.query.console.command.system;

import java.io.PrintWriter;
import java.util.Collection;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.SystemCommand;

/**
 * The Class HelpSystemCommand. This system command display all command descriptions.
 * 
 * @author porcelli
 */
public class HelpSystemCommand implements SystemCommand {

    /** The descriptions. */
    String[] descriptions;

    /**
     * Instantiates a new help system command.
     * 
     * @param commands all the commands (system and dynamic)
     */
    public HelpSystemCommand(
                              Collection<Command> commands ) {
        int maxSize = 4;
        if (commands != null) {

            int size = 0;
            for (Command command : commands) {
                if (command != null) {
                    size++;
                }
            }

            descriptions = new String[size + 1];
            for (Command command : commands) {
                if (command != null && command.getCommand().length() > maxSize) {
                    maxSize = command.getCommand().length();
                }
            }

            int i = 0;
            for (Command command : commands) {
                if (command != null) {
                    descriptions[i] = StringUtils.leftPad(command.getCommand(), maxSize) + " - " + command.getDescription();
                    i++;
                }
            }
        } else {
            descriptions = new String[1];
        }
        descriptions[0] = StringUtils.leftPad("help", maxSize) + " - " + getDescription();
    }

    /**
     * {@inheritDoc}
     */
    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        Assertions.checkNotNull("reader", reader);
        Assertions.checkNotNull("out", out);
        Assertions.checkNotNull("state", state);
        if (!accept(state)) {
            return;
        }
        for (String activeDesc : descriptions) {
            out.println(activeDesc);
        }
        out.flush();
        state.setInput(null);
        state.clearBuffer();
    }

    /**
     * {@inheritDoc}
     */
    public String getCommand() {
        return "help";
    }

    /**
     * {@inheritDoc}
     */
    public String getAutoCompleteCommand() {
        return getCommand();
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "display these instructions";
    }

    /**
     * {@inheritDoc}
     */
    public String getFileCompletionCommand() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public FileCompletionMode getFileCompletionMode() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasFileCompletion() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean accept( ConsoleState state ) {
        Assertions.checkNotNull("state", state);
        if (state.getActiveCommand() == null && state.getInput().trim().equals("help")) {
            return true;
        }
        return false;
    }
}
