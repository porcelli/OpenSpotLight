package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestAddPropertyCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new AddPropertyCommand();
    }

    @Test( expected = IllegalArgumentException.class )
    public void testAcceptNull() {
        assertThat(command.accept(null), is(false));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testExecuteNull() {
        command.execute(null, null, null);
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
        state.setInput("add property myProperty");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add property ?");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add propertx myProperty");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add property");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add propertyx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add property ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add property ?");
        state.appendBuffer("something");

        assertThat(state.getAdditionalProperties().size(), is(0));

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getAdditionalProperties().size(), is(1));
        assertThat(state.getAdditionalProperties().contains("?"), is(true));
    }

    @Test
    public void testValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add property myProperty");
        state.appendBuffer("something");

        assertThat(state.getAdditionalProperties().size(), is(0));

        command.execute(reader, out, state);
        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getInput(), is(""));

        state.setInput("add property myProperty2");
        state.appendBuffer("something");
        command.execute(reader, out, state);
        assertThat(state.getBuffer().length(), is(0));

        assertThat(state.getAdditionalProperties().size(), is(2));
        assertThat(state.getAdditionalProperties().contains("myProperty"), is(true));
        assertThat(state.getAdditionalProperties().contains("myProperty2"), is(true));
        assertThat(state.getAdditionalProperties().contains("myProperty3"), is(false));
        assertThat(state.getInput(), is(""));
    }
}
