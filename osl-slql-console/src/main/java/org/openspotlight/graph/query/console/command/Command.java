package org.openspotlight.graph.query.console.command;

import java.io.PrintWriter;

import org.openspotlight.graph.query.console.ConsoleState;

import jline.ConsoleReader;

public interface Command {

    public enum FileCompletionMode {
        STARTS_WITH,
        CONTAINS
    }

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state );

    public String getCommand();

    public String getDescription();

    public boolean hasFileCompletion();

    public String getFileCompletionCommand();

    public FileCompletionMode getFileCompletionMode();

    public boolean accept( ConsoleState state );

}
