package org.openspotlight.graph.query.console.command;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;

public class ExecuteLastQueryCommand extends QueryCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        state.clearBuffer();
        executeQuery(reader, out, state, state.getLastQuery());
    }

    public String getCommand() {
        return "/";
    }

    public String getDescription() {
        return "executes the last slql query.";
    }

    public String getFileCompletionCommand() {
        return null;
    }

    public FileCompletionMode getFileCompletionMode() {
        return null;
    }

    public boolean hasFileCompletion() {
        return false;
    }

    public boolean accept( ConsoleState state ) {
        if (state.getActiveCommand() == null && state.getInput().equals("/")) {
            return true;
        }
        return false;
    }

}
