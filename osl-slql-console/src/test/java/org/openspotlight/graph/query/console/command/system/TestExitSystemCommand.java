package org.openspotlight.graph.query.console.command.system;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestExitSystemCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new ExitSystemCommand();
    }

    @Test
    public void testAcceptValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("exit");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("exit  ");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("xxexit ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add exit");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("exit something");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("exit");
        state.setQuitApplication(false);
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(command.accept(state), is(true));
        assertThat(state.quitApplication(), is(true));
        assertThat(state.getBuffer().length(), is(0));
    }

    @Test
    public void testValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("exit");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(command.accept(state), is(true));
        assertThat(state.getBuffer().length(), is(0));
    }

}
