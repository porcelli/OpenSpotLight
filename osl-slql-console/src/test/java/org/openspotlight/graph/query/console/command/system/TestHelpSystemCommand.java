package org.openspotlight.graph.query.console.command.system;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;
import org.openspotlight.graph.query.console.command.Command;
import org.openspotlight.graph.query.console.command.dynamic.SaveQueryCommand;

public class TestHelpSystemCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new HelpSystemCommand(null);
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
        state.setInput("help");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("help  ");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("xxhelp ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add help");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("help something");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("help");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getInput(), is(""));
    }

    @Test
    public void testValidParameter2() {
        List<Command> commands = new LinkedList<Command>();
        commands.add(new ExitSystemCommand());
        commands.add(null);
        commands.add(new SaveQueryCommand());
        command = new HelpSystemCommand(commands);
        
        ConsoleState state = new ConsoleState(null);
        state.setInput("help");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getInput(), is(""));
    }

}
