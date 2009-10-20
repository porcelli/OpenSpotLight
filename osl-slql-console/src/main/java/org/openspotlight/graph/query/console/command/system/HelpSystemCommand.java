package org.openspotlight.graph.query.console.command.system;

import java.io.PrintWriter;
import java.util.Collection;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.SystemCommand;

public class HelpSystemCommand implements SystemCommand {

    String[] descriptions;

    public HelpSystemCommand(
                              Collection<Command> commands ) {
        int maxSize = 4;
        if (commands != null) {

            int size = 0;
            for (Command command : commands) {
                if (command != null) {
                    size++;
                }
            }

            descriptions = new String[size + 1];
            for (Command command : commands) {
                if (command != null && command.getCommand().length() > maxSize) {
                    maxSize = command.getCommand().length();
                }
            }

            int i = 0;
            for (Command command : commands) {
                if (command != null) {
                    descriptions[i] = StringUtils.leftPad(command.getCommand(), maxSize) + " - " + command.getDescription();
                    i++;
                }
            }
        } else {
            descriptions = new String[1];
        }
        descriptions[0] = StringUtils.leftPad("help", maxSize) + " - " + getDescription();
    }

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        Assertions.checkNotNull("reader", reader);
        Assertions.checkNotNull("out", out);
        Assertions.checkNotNull("state", state);
        if (!accept(state)) {
            return;
        }
        for (String activeDesc : descriptions) {
            out.println(activeDesc);
        }
        out.flush();
        state.setInput(null);
        state.clearBuffer();
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
        Assertions.checkNotNull("state", state);
        if (state.getActiveCommand() == null && state.getInput().trim().equals("help")) {
            return true;
        }
        return false;
    }
}