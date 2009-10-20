package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.query.console.ConsoleState;

public class ExecuteLastQueryCommand extends QueryCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        Assertions.checkNotNull("reader", reader);
        Assertions.checkNotNull("out", out);
        Assertions.checkNotNull("state", state);
        if (!accept(state)) {
            return;
        }
        String outputFileName = null;
        if (state.getInput().contains(" > ")) {
            int index = state.getInput().lastIndexOf(" > ");
            outputFileName = state.getInput().substring(index + 3);
        }
        if (state.getLastQuery() != null || state.getLastQuery().trim().length() > 0) {
            executeQuery(reader, out, state, state.getLastQuery(), outputFileName);
        } else {
            out.println("there is no query at buffer.");
        }
        out.flush();
        state.setInput(null);
        state.clearBuffer();
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
        Assertions.checkNotNull("state", state);
        if (state.getActiveCommand() == null && state.getInput().trim().equals("/") || state.getActiveCommand() == null && state.getInput().trim().startsWith("/ > ")) {
            return true;
        }
        return false;
    }
}