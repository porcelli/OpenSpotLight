package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestResetPropertiesCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new ResetPropertiesCommand();
    }

    @Test
    public void testAcceptValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("reset properties");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("reset properties  ");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("xxreset properties");

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
        state.setInput("reset property");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("reset properties ?");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("reset properties");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(command.accept(state), is(true));
        assertThat(state.getBuffer().length(), is(0));
    }

    @Test
    public void testValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.addAdditionalProperties("property1");
        state.addAdditionalProperties("property2");
        state.setInput("reset properties");
        state.appendBuffer("something");

        assertThat(state.getAdditionalProperties().size(), is(2));
        assertThat(state.getAdditionalProperties().contains("property1"), is(true));
        assertThat(state.getAdditionalProperties().contains("property2"), is(true));

        command.execute(reader, out, state);

        assertThat(state.getAdditionalProperties().size(), is(0));
        assertThat(state.getAdditionalProperties().contains("property1"), is(false));
        assertThat(state.getAdditionalProperties().contains("property2"), is(false));

        assertThat(command.accept(state), is(true));
        assertThat(state.getBuffer().length(), is(0));
    }

}
