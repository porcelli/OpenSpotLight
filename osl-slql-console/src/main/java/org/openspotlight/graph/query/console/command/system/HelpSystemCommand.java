package org.openspotlight.graph.query.console.command.system;

import java.io.PrintWriter;
import java.util.Collection;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.SystemCommand;

public class HelpSystemCommand implements SystemCommand {

    String[] descriptions;

    public HelpSystemCommand(
                              Collection<Command> commands ) {
        descriptions = new String[commands.size() + 1];
        int maxSize = 4;
        for (Command command : commands) {
            if (command.getCommand().length() > maxSize) {
                maxSize = command.getCommand().length();
            }
        }

        int i = 0;
        for (Command command : commands) {
            descriptions[i] = StringUtils.leftPad(command.getCommand(), maxSize) + " - " + command.getDescription();
            i++;
        }
        descriptions[i] = StringUtils.leftPad("help", maxSize) + " - " + getDescription();
    }

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        for (String activeDesc : descriptions) {
            out.println(activeDesc);
        }
        out.flush();
    }

    public String getCommand() {
        return "help";
    }

    public String getAutoCompleteCommand() {
        return getCommand();
    }

    public String getDescription() {
        return "display these instructions";
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
        if (state.getActiveCommand() == null && state.getInput().equals("help")) {
            return true;
        }
        return false;
    }
}
