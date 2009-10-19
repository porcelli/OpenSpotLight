package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class ResetPropertiesCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        state.resetAdditionalProperties();
        out.println("properties reset.");
        out.flush();
    }

    public String getCommand() {
        return "reset properties";
    }

    public String getAutoCompleteCommand() {
        return "reset properties";
    }

    public String getDescription() {
        return "resets slql properties output";
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
        if (state.getActiveCommand() == null && state.getInput().equals("reset properties")) {
            return true;
        }
        return false;
    }

}