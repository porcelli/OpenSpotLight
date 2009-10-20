package org.openspotlight.graph.query.console.command.dynamic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class SaveQueryCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (!accept(state)) {
            return;
        }
        String fileName = state.getInput().substring(4).trim();
        File outputFile = new File(fileName);
        if (!outputFile.isDirectory()) {
            try {
                outputFile.createNewFile();
                PrintWriter fileOut = new PrintWriter(outputFile);
                fileOut.append(state.getLastQuery());
                fileOut.flush();
                fileOut.close();
                out.println("query saved.");
            } catch (IOException e) {
                out.print("ERROR: ");
                out.println(e.getMessage());
            }
        } else {
            out.print("ERROR: ");
            out.println("Invalid file name.");
        }
        out.flush();
        state.clearBuffer();
    }

    public String getCommand() {
        return "save";
    }

    public String getAutoCompleteCommand() {
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
        if (state.getActiveCommand() == null && state.getInput().trim().startsWith("save ")) {
            return true;
        }
        return false;
    }

}
