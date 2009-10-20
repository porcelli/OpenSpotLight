package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestOpenFileCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        this.command = new OpenFileCommand();
        try {
            PrintWriter fileOut = new PrintWriter("out.slql");
            fileOut.append("select *;");
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
        }
    }

    @After
    public void deleteTestFile() {
        new File("out.slql").delete();
    }

    @Test
    public void testAcceptNullInout() {
        ConsoleState state = new ConsoleState(null);
        state.setInput(null);

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("get filename.slql");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("get something");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("get ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("getx property");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add get");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("getx property ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("get filename.slql");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery().length(), is(0));
        assertThat(state.getInput(), is(""));
    }

    @Test
    public void testValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("get out.slql");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;\n"));
        assertThat(state.getInput(), is(""));
    }

}
