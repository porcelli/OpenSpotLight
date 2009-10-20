package org.openspotlight.graph.query.console.command;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Before;

import jline.ConsoleReader;

public abstract class AbstractCommandTest {

    protected Command       command = null;
    protected ConsoleReader reader  = null;
    protected PrintWriter   out     = null;

    @Before
    public void setup() throws IOException {
        if (reader == null) {
            reader = new ConsoleReader();
            reader.setBellEnabled(false);
            out = new PrintWriter(System.out);
            setupCommand();
        }
    }

    protected abstract void setupCommand();
}
