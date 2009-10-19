package org.openspotlight.graph.query.console;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.query.console.command.Command;

public class ConsoleState {

    private SLGraphSession session              = null;
    private String         input                = null;
    private StringBuilder  sb                   = new StringBuilder();
    private Command        activeCommand        = null;
    private String         lastQuery            = null;
    private boolean        quitApplication      = false;
    private String[]       additionalProperties = new String[] {};

    public ConsoleState(
                         SLGraphSession session ) {
        this.session = session;
    }

    public StringBuilder getSb() {
        return sb;
    }

    public void setSb( StringBuilder sb ) {
        this.sb = sb;
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
        this.lastQuery = lastQuery;
    }

    public String getInput() {
        return input;
    }

    public void setInput( String input ) {
        this.input = input;
    }

    public void clearBuffer() {
        sb = new StringBuilder();
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

    public String[] getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties( String[] additionalProperties ) {
        this.additionalProperties = additionalProperties;
    }
}
