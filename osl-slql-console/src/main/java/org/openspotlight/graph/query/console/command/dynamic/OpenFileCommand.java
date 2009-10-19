package org.openspotlight.graph.query.console.command.dynamic;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class OpenFileCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        String fileName = state.getInput().substring(3).trim();

        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            try {
                state.clearBuffer();
                LineNumberReader fileReader = new LineNumberReader(new FileReader(file));
                while (fileReader.ready()) {
                    state.appendLineBuffer(fileReader.readLine());
                }
                state.setLastQuery(state.getBuffer());
                state.clearBuffer();
                out.println("query loaded into buffer.");
            } catch (Exception e) {
                state.clearBuffer();
                out.print("ERROR: ");
                out.println(e.getMessage());
            }
        } else {
            out.print("ERROR: ");
            out.println("invalid file name.");
        }
        out.flush();
        state.clearBuffer();
    }

    public String getCommand() {
        return "get";
    }

    public String getAutoCompleteCommand() {
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
        if (state.getActiveCommand() == null && state.getInput().trim().startsWith("get ")) {
            return true;
        }
        return false;
    }

}
