package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class AddPropertyCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        String propertyName = state.getInput().substring(13).trim();
        state.addAdditionalProperties(propertyName);
        out.print(propertyName);
        out.println(" property added.");
        out.flush();
        state.setInput(null);
        state.clearBuffer();
    }

    public String getCommand() {
        return "add property";
    }

    public String getAutoCompleteCommand() {
        return "add property";
    }

    public String getDescription() {
        return "adds a property to slql output result";
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
        if (state.getActiveCommand() == null && state.getInput().trim().startsWith("add property ")) {
            return true;
        }
        return false;
    }

}
