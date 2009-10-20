package org.openspotlight.graph.query.console.command.system;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.SLQLPlus;
import org.openspotlight.graph.query.console.command.SystemCommand;

public class VersionSystemCommand implements SystemCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        out.println("slqlplus version \"" + SLQLPlus.VERSION + "\"");
        out.flush();
        state.setInput(null);
        state.clearBuffer();
    }

    public String getCommand() {
        return "version";
    }

    public String getAutoCompleteCommand() {
        return getCommand();
    }

    public String getDescription() {
        return "print product version";
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
        if (state.getActiveCommand() == null && state.getInput().trim().equals("version")) {
            return true;
        }
        return false;
    }

}
