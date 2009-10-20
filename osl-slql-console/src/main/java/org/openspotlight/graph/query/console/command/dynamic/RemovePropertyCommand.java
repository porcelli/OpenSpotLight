package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class RemovePropertyCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        String propertyName = state.getInput().substring(16).trim();
        state.removesAdditionalProperties(propertyName);
        out.print(propertyName);
        out.println(" property removed.");
        out.flush();
        state.setInput(null);
        state.clearBuffer();
    }

    public String getCommand() {
        return "remove property";
    }

    public String getAutoCompleteCommand() {
        return "remove property";
    }

    public String getDescription() {
        return "removes a property from slql output result";
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
        if (state.getActiveCommand() == null && state.getInput().trim().startsWith("remove property ")) {
            return true;
        }
        return false;
    }

}
