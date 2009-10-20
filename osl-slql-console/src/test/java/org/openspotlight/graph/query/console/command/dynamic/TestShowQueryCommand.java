package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestShowQueryCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new ShowQueryCommand();
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
        state.setInput("show query");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("show query  ");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("xxshow query ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add show query");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("show query something");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("show query");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getInput(), is(""));
    }
}
