package org.openspotlight.graph.query.console.command;

import java.io.PrintWriter;

import org.openspotlight.graph.query.console.ConsoleState;

import jline.ConsoleReader;

public class OpenFileCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        String fileName = state.getInput().substring(3).trim();
        out.println("execte query from file: " + state.getSb().toString() + " -> " + fileName);
        out.flush();
    }

    public String getCommand() {
        return "get";
    }

    public String getDescription() {
        return "gets the slql query from file";
    }

    public String getFileCompletionCommand() {
        return "get";
    }

    public FileCompletionMode getFileCompletionMode() {
        return FileCompletionMode.STARTS_WITH;
    }

    public boolean hasFileCompletion() {
        return true;
    }

    public boolean accept( ConsoleState state ) {
        if (state.getActiveCommand() == null && state.getInput().startsWith("get ")) {
            return true;
        }
        return false;
    }
}