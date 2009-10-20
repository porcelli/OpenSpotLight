package org.openspotlight.graph.query.console;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.query.console.command.Command;

public class ConsoleState {

    private SLGraphSession session              = null;
    private StringBuilder  sb                   = new StringBuilder();
    private Set<String>    additionalProperties = new HashSet<String>();
    private Command        activeCommand        = null;
    private boolean        quitApplication      = false;
    private String         input                = "";
    private String         lastQuery            = "";

    public ConsoleState(
                         SLGraphSession session ) {
        this.session = session;
    }

    public String getBuffer() {
        return sb.toString();
    }

    public void appendBuffer( String buffer ) {
        this.sb.append(buffer);
    }

    public void appendLineBuffer( String buffer ) {
        appendBuffer(buffer);
        appendBuffer("\n");
    }

    public void clearBuffer() {
        sb = new StringBuilder();
    }

    public Command getActiveCommand() {
        return activeCommand;
    }

    public void setActiveCommand( Command activeCommand ) {
        this.activeCommand = activeCommand;
    }

    public String getLastQuery() {
        return lastQuery;
    }

    public void setLastQuery( String lastQuery ) {
        if (lastQuery == null) {
            this.lastQuery = "";
        } else {
            this.lastQuery = lastQuery;
        }
    }

    public String getInput() {
        return input;
    }

    public void setInput( String input ) {
        if (input == null) {
            this.input = "";
        } else {
            this.input = input;
        }
    }

    public boolean quitApplication() {
        return quitApplication;
    }

    public void setQuitApplication( boolean quitApplication ) {
        this.quitApplication = quitApplication;
    }

    public SLGraphSession getSession() {
        return session;
    }

    public Collection<String> getAdditionalProperties() {
        return additionalProperties;
    }

    public void addAdditionalProperties( String additionalProperty ) {
        if (additionalProperty.trim().length() > 0) {
            this.additionalProperties.add(additionalProperty);
        }
    }

    public void removesAdditionalProperties( String additionalProperty ) {
        if (additionalProperty.trim().length() > 0 && this.additionalProperties.contains(additionalProperty)) {
            this.additionalProperties.remove(additionalProperty);
        }
    }

    public void resetAdditionalProperties() {
        additionalProperties = new HashSet<String>();
    }

}
