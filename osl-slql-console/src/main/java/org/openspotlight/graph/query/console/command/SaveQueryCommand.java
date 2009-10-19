package org.openspotlight.graph.query.console.command;

import java.io.PrintWriter;

import org.openspotlight.graph.query.console.ConsoleState;

import jline.ConsoleReader;

public class SaveQueryCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {

        String fileName = state.getInput().substring(4).trim();
        out.println("save last query to file: " + state.getSb().toString() + " -> " + fileName);
        out.flush();
    }

    public String getCommand() {
        return "save";
    }

    public String getDescription() {
        return "saves last slql query from file";
    }

    public String getFileCompletionCommand() {
        return "save";
    }

    public FileCompletionMode getFileCompletionMode() {
        return FileCompletionMode.STARTS_WITH;
    }

    public boolean hasFileCompletion() {
        return true;
    }

    public boolean accept( ConsoleState state ) {
        if (state.getActiveCommand() == null && state.getInput().startsWith("save ")) {
            return true;
        }
        return false;
    }

}
