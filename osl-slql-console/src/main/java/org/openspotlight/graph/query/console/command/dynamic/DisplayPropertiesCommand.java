package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;

import jline.ConsoleReader;

import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class DisplayPropertiesCommand implements DynamicCommand {

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        out.println("additional properties:");
        if (state.getAdditionalProperties().size() == 0){
            out.println("\t(none)");
        } else {
            for (String propertyName : state.getAdditionalProperties()) {
                out.print("\t- ");
                out.println(propertyName);
            }            
        }
        out.flush();
    }

    public String getCommand() {
        return "display properties";
    }

    public String getAutoCompleteCommand() {
        return "display properties";
    }

    public String getDescription() {
        return "display slql properties output";
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
        if (state.getActiveCommand() == null && state.getInput().equals("display properties")) {
            return true;
        }
        return false;
    }

}
