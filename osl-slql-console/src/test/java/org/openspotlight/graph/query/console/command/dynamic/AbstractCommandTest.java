package org.openspotlight.graph.query.console.command.dynamic;

import java.io.IOException;
import java.io.PrintWriter;

import jline.ConsoleReader;

import org.junit.Before;
import org.openspotlight.graph.query.console.command.Command;

public abstract class AbstractCommandTest {

    protected Command       command = null;
    protected ConsoleReader reader  = null;
    protected PrintWriter   out     = null;

    @Before
    public void setup() throws IOException {
        reader = new ConsoleReader();
        reader.setBellEnabled(false);
        out = new PrintWriter(System.out);
        setupCommand();
    }

    protected abstract void setupCommand();
}
