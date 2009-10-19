package org.openspotlight.graph.query.console.command.system;

import java.io.IOException;
import java.io.PrintWriter;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.Command.FileCompletionMode;

import jline.ConsoleReader;

public class ClearSystemCommand implements SystemCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        try {
            reader.clearScreen();
        } catch (IOException e) {
        }
    }

    public String getCommand() {
        return "clear";
    }

    public String getDescription() {
        return "clear the terminal screen";
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
        if (state.getActiveCommand() == null && state.getInput().equals("clear")) {
            return true;
        }
        return false;
    }

}
