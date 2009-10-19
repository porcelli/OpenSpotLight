package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;

public class ExecuteLastQueryCommand extends QueryCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        state.clearBuffer();
        String outputFileName = null;
        if (state.getInput().contains(" > ")) {
            int index = state.getInput().lastIndexOf(" > ");
            outputFileName = state.getInput().substring(index + 3);
        }
        executeQuery(reader, out, state, state.getLastQuery(), outputFileName);
    }

    public String getCommand() {
        return "/";
    }

    public String getDescription() {
        return "executes the last slql query.";
    }

    public String getFileCompletionCommand() {
        return "/ >";
    }

    public FileCompletionMode getFileCompletionMode() {
        return FileCompletionMode.CONTAINS;
    }

    public boolean hasFileCompletion() {
        return true;
    }

    public boolean accept( ConsoleState state ) {
        if (state.getActiveCommand() == null && state.getInput().equals("/") || state.getActiveCommand() == null && state.getInput().startsWith("/ > ")) {
            return true;
        }
        return false;
    }

}
