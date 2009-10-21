package org.openspotlight.graph.query.console.command.dynamic;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
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

public class TestQueryCommand extends AbstractCommandTest {

    private ConsoleState state = null;

    @Override
    protected void setupCommand() {
        this.state = new ConsoleState(null);
        command = new QueryCommand();
    }

    @After
    public void deleteTestFile() {
        if (this.state.getSession() != null) {
            this.state.getSession().close();
        }
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
        assertThat(state.getLastQuery(), is("select \n*;"));
        assertThat(state.getActiveCommand(), is(nullValue()));
    }

    @Test
    public void testValidParameter2() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select *; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;"));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.isFile(), is(true));
        String fileContent = getFileContent(generatedFile);
        assertThat(fileContent, is(notNullValue()));
        assertThat(fileContent.length(), is(not(0)));
    }

    @Test
    public void testValidMultiLineParameter2() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("select \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state.setInput("*; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select \n*;"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.isFile(), is(true));
        String fileContent = getFileContent(generatedFile);
        assertThat(fileContent, is(is(notNullValue())));
        assertThat(fileContent.length(), is(not(0)));
    }

    @Test
    public void testValidParameterSyntaxError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select *?*; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *?*;"));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testValidMultiLineParameterSyntaxError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("select \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state.setInput("*?*; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select \n*?*;"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidQueryWithTargetError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b); > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);"));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidMultiLineQueryWithTargetError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("define ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("define \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state.setInput("target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b); > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("define \ntarget org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidQueryWithVariablesError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidMultiLineQueryWithVariablesError() throws SLException, IOException, ClassNotFoundException {
        GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("sa", "sa", "sa"));

        state.setInput("select ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("select \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state.setInput("** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select \n** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
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
