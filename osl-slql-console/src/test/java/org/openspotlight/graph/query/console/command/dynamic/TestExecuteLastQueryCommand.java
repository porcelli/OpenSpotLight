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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;
import org.openspotlight.remote.server.RemoteObjectServer;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAuthenticator;

public class TestExecuteLastQueryCommand extends AbstractCommandTest {

    private static RemoteObjectServer remoteObjectServer;

    @BeforeClass
    public static void setupServer() throws Exception {
        remoteObjectServer = new RemoteObjectServerImpl(new UserAuthenticator() {

            public boolean canConnect( final String userName,
                                       final String password,
                                       final String clientHost ) {
                return true;
            }
        }, 7070, 10 * 60 * 1000L);

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
        if (this.state == null) {
            this.state = new ConsoleState(null);
            this.command = new ExecuteLastQueryCommand();
        }
    }

    @Test
    public void testAcceptInValidParameter() {
        this.state.setInput("/x ");

        assertThat(this.command.accept(this.state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add /");

        assertThat(this.command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter5() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("/ > ");

        assertThat(this.command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter6() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("/ < ");

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
    public void testAcceptValidParameter() {
        this.state.setInput("/ > test.out");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        this.state.setInput("/");

        assertThat(this.command.accept(this.state), is(true));
    }

    @Test( expected = IllegalArgumentException.class )
    public void testExecuteNull() {
        this.command.execute(null, null, null);
    }

    @Test
    public void testInvalidQueryWithTargetError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setLastQuery("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);");
        this.state.setInput("/ > out,txt");

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
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setLastQuery("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;");
        this.state.setInput("/ > out,txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(
                   this.state.getLastQuery(),
                   is("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testValidParameter() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setLastQuery("select *;");
        this.state.setInput("/");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select *;"));

        this.state.getSession().close();
    }

    @Test
    public void testValidParameter2() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setLastQuery("select *;");
        this.state.setInput("/ > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select *;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(true));
        final String fileContent = this.getFileContent(generatedFile);
        assertThat(fileContent, is(notNullValue()));
        assertThat(fileContent.length(), is(not(0)));

        this.state.getSession().close();
    }

    @Test
    public void testValidParameterSyntaxError() throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        this.state = new ConsoleState(graphConnection.connect("localhost:7070", "sa", "sa"));

        this.state.setLastQuery("select *?*;");
        this.state.setInput("/ > out.txt");

        this.command.execute(this.reader, this.out, this.state);

        assertThat(this.state.getBuffer().length(), is(0));
        assertThat(this.state.getLastQuery(), is("select *?*;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));

        this.state.getSession().close();
    }
}
