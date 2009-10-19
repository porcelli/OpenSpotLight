package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;

public class TestRemovePropertyCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new RemovePropertyCommand();
    }

    @Test
    public void testAcceptValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove property myProperty");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove property ?");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove propertx myProperty");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove property");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove propertyx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove property ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("remove property ?");
        state.addAdditionalProperties("?");

        assertThat(state.getAdditionalProperties().size(), is(1));
        assertThat(state.getAdditionalProperties().contains("?"), is(true));

        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(command.accept(state), is(true));
        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getAdditionalProperties().size(), is(0));
        assertThat(state.getAdditionalProperties().contains("?"), is(false));
    }

    @Test
    public void testValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.addAdditionalProperties("myProperty");
        state.addAdditionalProperties("myProperty2");

        assertThat(state.getAdditionalProperties().contains("myProperty"), is(true));
        assertThat(state.getAdditionalProperties().contains("myProperty2"), is(true));

        state.setInput("remove property myProperty");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getAdditionalProperties().size(), is(1));
        assertThat(state.getAdditionalProperties().contains("myProperty"), is(false));
        assertThat(state.getAdditionalProperties().contains("myProperty2"), is(true));

        state.appendBuffer("something");
        state.setInput("remove property myProperty2");

        command.execute(reader, out, state);
        assertThat(state.getBuffer().length(), is(0));

        assertThat(command.accept(state), is(true));
        assertThat(state.getAdditionalProperties().size(), is(0));
        assertThat(state.getAdditionalProperties().contains("myProperty"), is(false));
        assertThat(state.getAdditionalProperties().contains("myProperty2"), is(false));
    }
}
