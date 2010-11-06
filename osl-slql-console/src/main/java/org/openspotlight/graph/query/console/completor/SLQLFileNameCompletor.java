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
package org.openspotlight.graph.query.console.completor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jline.FileNameCompletor;

import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.Command.FileCompletionMode;

/**
 * The Class SLQLFileNameCompletor. This class executes file name completion based on Command.
 * 
 * @author porcelli
 */
public class SLQLFileNameCompletor extends FileNameCompletor {

    /** The commands that has fileCompletion. */
    List<Command> commands = null;

    /**
     * Instantiates a new SLQL file name completor.
     * 
     * @param commands the commands
     */
    public SLQLFileNameCompletor(
                                  Collection<Command> commands ) {
        super();
        this.commands = new LinkedList<Command>();
        if (commands != null && commands.size() > 0) {
            for (Command command : commands) {
                if (command != null && command.hasFileCompletion()) {
                    this.commands.add(command);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public int complete( String buf,
                         final int cursor,
                         @SuppressWarnings( "unchecked" ) final List candidates ) {
        for (Command activeCommand : commands) {
            int complementCursorPosition = 0;
            boolean commandAccepted = false;
            if (activeCommand.getFileCompletionMode() == FileCompletionMode.STARTS_WITH) {
                if (buf.startsWith(activeCommand.getFileCompletionCommand() + " ")) {
                    buf = buf.substring(activeCommand.getFileCompletionCommand().length() + 1);
                    complementCursorPosition = activeCommand.getFileCompletionCommand().length() + 1;
                    commandAccepted = true;
                }
            } else if (activeCommand.getFileCompletionMode() == FileCompletionMode.CONTAINS) {
                if (buf.contains(activeCommand.getFileCompletionCommand() + " ")) {
                    int indexOfOutputDef = buf.lastIndexOf(activeCommand.getFileCompletionCommand() + " ");
                    buf.substring(indexOfOutputDef);
                    indexOfOutputDef = indexOfOutputDef + activeCommand.getFileCompletionCommand().length() + 1;
                    buf = buf.substring(indexOfOutputDef);
                    complementCursorPosition = indexOfOutputDef;
                    commandAccepted = true;
                }
            }

            if (commandAccepted) {
                return super.complete(buf, cursor, candidates) + complementCursorPosition;
            }
        }
        return cursor;
    }
}
