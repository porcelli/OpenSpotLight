package org.openspotlight.graph.query.console;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jline.FileNameCompletor;

import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.Command.FileCompletionMode;

public class SLQLFileNameCompletor extends FileNameCompletor {

    List<Command> commands;

    public SLQLFileNameCompletor(
                                  Collection<Command> commands ) {
        super();
        this.commands = new LinkedList<Command>();
        for (Command command : commands) {
            if (command.hasFileCompletion()) {
                this.commands.add(command);
            }
        }
    }

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
