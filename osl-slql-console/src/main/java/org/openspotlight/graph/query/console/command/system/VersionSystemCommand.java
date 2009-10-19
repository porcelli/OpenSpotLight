package org.openspotlight.graph.query.console.command.system;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.SLQLPlus;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.Command.FileCompletionMode;

public class VersionSystemCommand implements SystemCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        out.println("slqlplus version \"" + SLQLPlus.VERSION + "\"");
        out.flush();
    }

    public String getCommand() {
        return "version";
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
        if (state.getActiveCommand() == null && state.getInput().equals("version")) {
            return true;
        }
        return false;
    }

}
