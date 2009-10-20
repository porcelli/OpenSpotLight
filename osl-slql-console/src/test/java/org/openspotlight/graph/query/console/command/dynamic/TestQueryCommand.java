package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestQueryCommand extends AbstractCommandTest {

    private ConsoleState state = null;

    @Override
    protected void setupCommand() {
        if (this.state == null) {
            this.state = new ConsoleState(null);
            command = new QueryCommand();
        }
    }

    @Test
    public void testAcceptNullInout() {
        state.setInput(null);

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptValidParameter() {
        state.setInput("select *; > test.out");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        state.setInput("select *;");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter3() {
        state.setInput("select");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter4() {
        state.setInput("use");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter5() {
        state.setInput("define");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidMultiLineParameter() {
        state.setInput("define");
        assertThat(command.accept(state), is(true));
        command.execute(reader, out, state);

        assertThat(state.getInput(), is(""));
        assertThat(state.getBuffer(), is("define\n"));

        state.setInput("anything here");
        assertThat(command.accept(state), is(true));
        command.execute(reader, out, state);
        assertThat(state.getInput(), is(""));
        assertThat(state.getBuffer(), is("define\nanything here\n"));
    }

    @Test
    public void testAcceptValidMultiLineParameter1() {
        state.setInput("select");
        assertThat(command.accept(state), is(true));
        command.execute(reader, out, state);

        assertThat(state.getInput(), is(""));
        assertThat(state.getBuffer(), is("select\n"));

        state.setInput("anything here");
        assertThat(command.accept(state), is(true));
        command.execute(reader, out, state);
        assertThat(state.getInput(), is(""));
        assertThat(state.getBuffer(), is("select\nanything here\n"));
    }

    @Test
    public void testAcceptValidMultiLineParameter2() {
        state.setInput("use");
        assertThat(command.accept(state), is(true));
        command.execute(reader, out, state);

        assertThat(state.getInput(), is(""));
        assertThat(state.getBuffer(), is("use\n"));

        state.setInput("anything here");
        assertThat(command.accept(state), is(true));
        command.execute(reader, out, state);
        assertThat(state.getInput(), is(""));
        assertThat(state.getBuffer(), is("use\nanything here\n"));
    }

    @Test
    public void testAcceptInValidParameter() {
        state.setInput("selecx ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add select");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("selectx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("selectx xx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter5() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("select *; > ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter6() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("select *; < ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select *;");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;"));

        this.state.getSession().close();
    }

    @Test
    public void testValidMultiLineParameter() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("select \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state.setInput("*;");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select\n*;\n"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        this.state.getSession().close();
    }

    //
    //    private String getFileContent( File in ) {
    //        StringBuilder sb = new StringBuilder();
    //        LineNumberReader fileReader;
    //        try {
    //            fileReader = new LineNumberReader(new FileReader(in));
    //            while (fileReader.ready()) {
    //                sb.append(fileReader.readLine());
    //                sb.append("\n");
    //            }
    //            return sb.toString();
    //        } catch (Exception e) {
    //            // TODO Auto-generated catch block
    //            return "";
    //        }
    //    }
}
