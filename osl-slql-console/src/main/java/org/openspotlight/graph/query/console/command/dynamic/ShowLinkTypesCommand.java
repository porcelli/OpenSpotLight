package org.openspotlight.graph.query.console.command.dynamic;

import java.io.PrintWriter;
import java.util.Collection;

import jline.ConsoleReader;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLMetaLinkType;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class ShowLinkTypesCommand implements DynamicCommand {

    public boolean accept( ConsoleState state ) {
        Assertions.checkNotNull("state", state);
        if (state.getActiveCommand() == null && state.getInput().trim().equals("show link types")) {
            return true;
        }
        return false;
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
        try {
            out.println("link types:");
            if (state.getSession() == null) {
                out.println("\t(none)");
            } else {
                Collection<SLMetaLinkType> nodeTypes = state.getSession().getMetadata().getMetaLinkTypes();
                if (nodeTypes.size() == 0) {
                    out.println("\t(none)");
                } else {
                    for (SLMetaLinkType linkType : nodeTypes) {
                        out.print("\t- ");
                        out.println(linkType.getType().getName());
                    }
                }
            }
        } catch (SLGraphSessionException e) {
            out.print("ERROR: ");
            out.println(e.getMessage());
        }
        out.flush();
        state.setInput(null);
        state.clearBuffer();
    }

    public String getAutoCompleteCommand() {
        return "show link types";
    }

    public String getCommand() {
        return "show link types";
    }

    public String getDescription() {
        return "display link types available";
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

}
