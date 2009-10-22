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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.remote.server.UserAuthenticator;

public class TestQueryCommand extends AbstractCommandTest {

    private static RemoteGraphSessionServer remoteObjectServer;

    @BeforeClass
    public static void setupServer() throws Exception {
        remoteObjectServer = new RemoteGraphSessionServer(new UserAuthenticator() {

            public boolean canConnect( final String userName,
                                       final String password,
                                       final String clientHost ) {
                return true;
            }
        }, 7070, 60 * 1000 * 10L, DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    }

    @AfterClass
    public static void shutdownServer() throws Exception {
        if (remoteObjectServer != null) {
            remoteObjectServer.shutdown();
        }
    }

    private ConsoleState state = null;

    @After
    public void deleteTestFile() {
        if (this.state.getSession() != null) {
            this.state.getSession().close();
        }
        new File("out.txt").delete();
    }

    private String getFileContent( final File in ) {
        final StringBuilder sb = new StringBuilder();
        LineNumberReader fileReader;
        try {
            fileReader = new LineNumberReader(new FileReader(in));
            while (fileReader.ready()) {
                sb.append(fileReader.readLine());
                sb.append("\n");
            }
            return sb.toString();
        } catch (final Exception e) {
            return "";
        }
    }

    @Override
    protected void setupCommand() {
        this.state = new ConsoleState(null);
        this.command = new QueryCommand();
    }

    @Test
    public void testAcceptInValidParameter() {
        this.state.setInput("selecx ");

        assertThat(this.command.accept(this.state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add select");

        assertThat(this.command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("selectx");

        assertThat(this.command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("selectx xx");

        assertThat(this.command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter5() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("select *; > ");

        assertThat(this.command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter6() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("select *; < ");

        assertThat(this.command.accept(state), is(false));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testAcceptNull() {
        assertThat(this.command.accept(null), is(false));
    }

    @Test
    public void testAcceptNullInout() {
        this.state.setInput(null);

        assertThat(this.command.accept(this.state), is(false));
    }

    @Test
    public void testAcceptValidMultiLineParameter() {
        this.state.setInput("define");
        assertThat(this.command.accept(this.state), is(true));
        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getInput(), is(""));
        assertThat(this.state.getBuffer(), is("define\n"));

        this.state.setInput("anything here");
        assertThat(this.command.accept(this.state), is(true));
        this.command.execute(this.reader, this.out, this.state);
        assertThat(this.state.getInput(), is(""));
        assertThat(this.state.getBuffer(), is("define\nanything here\n"));
    }

    @Test
    public void testAcceptValidMultiLineParameter1() {
        this.state.setInput("select");
        assertThat(this.command.accept(this.state), is(true));
        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getInput(), is(""));
        assertThat(this.state.getBuffer(), is("select\n"));

        this.state.setInput("anything here");
        assertThat(this.command.accept(this.state), is(true));
        this.command.execute(this.reader, this.out, this.state);
        assertThat(this.state.getInput(), is(""));
        assertThat(this.state.getBuffer(), is("select\nanything here\n"));
    }

    @Test
    public void testAcceptValidMultiLineParameter2() {
        this.state.setInput("use");
        assertThat(this.command.accept(this.state), is(true));
        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getInput(), is(""));
        assertThat(this.state.getBuffer(), is("use\n"));

        this.state.setInput("anything here");
        assertThat(this.command.accept(this.state), is(true));
        this.command.execute(this.reader, this.out, this.state);
        assertThat(this.state.getInput(), is(""));
        assertThat(this.state.getBuffer(), is("use\nanything here\n"));
    }

    @Test
    public void testAcceptValidParameter() {
        this.state.setInput("select *; > test.out");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        this.state.setInput("select *;");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test
    public void testAcceptValidParameter3() {
        this.state.setInput("select");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test
    public void testAcceptValidParameter4() {
        this.state.setInput("use");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test
    public void testAcceptValidParameter5() {
        this.state.setInput("define");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testExecuteNull() {
        this.command.execute(null, null, null);
    }

    @Test
    public void testInvalidMultiLineQueryWithTargetError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("define ");
        this.state.appendBuffer("something");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getLastQuery(), is(""));
        assertThat(this.state.getBuffer(), is("define \n"));
        assertThat(this.state.getActiveCommand(), is(notNullValue()));

        this.state.setInput("target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b); > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(
                   this.state.getLastQuery(),
                   is("define \ntarget org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);"));
        assertThat(this.state.getActiveCommand(), is(nullValue()));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidMultiLineQueryWithVariablesError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select ");
        this.state.appendBuffer("something");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getLastQuery(), is(""));
        assertThat(this.state.getBuffer(), is("select \n"));
        assertThat(this.state.getActiveCommand(), is(notNullValue()));

        this.state.setInput("** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var; > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(
                   this.state.getLastQuery(),
                   is("select \n** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));
        assertThat(this.state.getActiveCommand(), is(nullValue()));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidQueryWithTargetError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b); > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(
                   this.state.getLastQuery(),
                   is("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidQueryWithVariablesError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var; > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(
                   this.state.getLastQuery(),
                   is("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testValidMultiLineParameter() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select ");
        this.state.appendBuffer("something");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getLastQuery(), is(""));
        assertThat(this.state.getBuffer(), is("select \n"));
        assertThat(this.state.getActiveCommand(), is(notNullValue()));

        this.state.setInput("*;");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select \n*;"));
        assertThat(this.state.getActiveCommand(), is(nullValue()));
    }

    @Test
    public void testValidMultiLineParameter2() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select ");
        this.state.appendBuffer("something");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getLastQuery(), is(""));
        assertThat(this.state.getBuffer(), is("select \n"));
        assertThat(this.state.getActiveCommand(), is(notNullValue()));

        this.state.setInput("*; > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select \n*;"));
        assertThat(this.state.getActiveCommand(), is(nullValue()));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.isFile(), is(true));
        final String fileContent = this.getFileContent(generatedFile);
        assertThat(fileContent, is(is(notNullValue())));
        assertThat(fileContent.length(), is(not(0)));
    }

    @Test
    public void testValidMultiLineParameterSyntaxError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select ");
        this.state.appendBuffer("something");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getLastQuery(), is(""));
        assertThat(this.state.getBuffer(), is("select \n"));
        assertThat(this.state.getActiveCommand(), is(notNullValue()));

        this.state.setInput("*?*; > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select \n*?*;"));
        assertThat(this.state.getActiveCommand(), is(nullValue()));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testValidParameter() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select *;");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select *;"));
    }

    @Test
    public void testValidParameter2() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select *; > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select *;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.isFile(), is(true));
        final String fileContent = this.getFileContent(generatedFile);
        assertThat(fileContent, is(notNullValue()));
        assertThat(fileContent.length(), is(not(0)));
    }

    @Test
    public void testValidParameterSyntaxError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.command = new QueryCommand();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setInput("select *?*; > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select *?*;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }
}
