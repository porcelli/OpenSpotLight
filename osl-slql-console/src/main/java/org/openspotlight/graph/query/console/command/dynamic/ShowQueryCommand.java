package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class ShowQueryCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        out.println("query: ");
        out.println(state.getLastQuery());
        out.flush();
        state.clearBuffer();
    }

    public String getCommand() {
        return "show query";
    }

    public String getAutoCompleteCommand() {
        return "show query";
    }

    public String getDescription() {
        return "shows last slql query";
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
        if (state.getActiveCommand() == null && state.getInput().equals("show query")) {
            return true;
        }
        return false;
    }

}
