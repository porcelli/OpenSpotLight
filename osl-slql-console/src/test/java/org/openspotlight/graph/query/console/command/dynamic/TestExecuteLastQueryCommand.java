package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.junit.After;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestExecuteLastQueryCommand extends AbstractCommandTest {

    private ConsoleState state = null;

    @Override
    protected void setupCommand() {
        if (this.state == null) {
            this.state = new ConsoleState(null);
            command = new ExecuteLastQueryCommand();
        }
    }

    @After
    public void deleteTestFile() {
        new File("out.txt").delete();
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
        state.setInput(null);

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptValidParameter() {
        state.setInput("/ > test.out");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        state.setInput("/");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptInValidParameter() {
        state.setInput("/x ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("add /");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter5() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("/ > ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter6() {
        ConsoleState state = new ConsoleState(null);
        state.setInput("/ < ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testValidParameter() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setLastQuery("select *;");
        state.setInput("/");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;"));

        this.state.getSession().close();
    }

    @Test
    public void testValidParameter2() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setLastQuery("select *;");
        state.setInput("/ > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;"));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(true));
        String fileContent = getFileContent(generatedFile);
        assertThat(fileContent, is(notNullValue()));
        assertThat(fileContent.length(), is(not(0)));

        this.state.getSession().close();
    }

    @Test
    public void testValidParameterSyntaxError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setLastQuery("select *?*;");
        state.setInput("/ > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *?*;"));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));

        this.state.getSession().close();
    }

    private String getFileContent( File in ) {
        StringBuilder sb = new StringBuilder();
        LineNumberReader fileReader;
        try {
            fileReader = new LineNumberReader(new FileReader(in));
            while (fileReader.ready()) {
                sb.append(fileReader.readLine());
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
