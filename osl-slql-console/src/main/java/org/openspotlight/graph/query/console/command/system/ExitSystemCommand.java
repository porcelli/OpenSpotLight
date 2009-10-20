package org.openspotlight.graph.query.console.command.system;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.SystemCommand;

public class ExitSystemCommand implements SystemCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        state.setQuitApplication(true);
        state.setInput(null);
        state.clearBuffer();
    }

    public String getCommand() {
        return "exit";
    }

    public String getAutoCompleteCommand() {
        return getCommand();
    }

    public String getDescription() {
        return "ends the application";
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
        if (state.getActiveCommand() == null && state.getInput().trim().equals("exit")) {
            return true;
        }
        return false;
    }
}