/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.GraphLocation;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;
import org.openspotlight.graph.query.console.command.ExampleRemoteServerWithData;

public class TestQueryCommand extends AbstractCommandTest {

    private static final String DEFAULT_REPOSITORY_NAME = "default";

    private ConsoleState        state                   = null;

    @BeforeClass
    public static void setupServer()
        throws Exception {
        ExampleRemoteServerWithData.populateSomeDataAndStartTheServer();
    }

    private String getFileContent(final File in) {
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
        state = new ConsoleState(null);
        command = new QueryCommand();
    }

    /***
     * We love you Windows... Thanks for all the enjoyable moments we spend together...
     */
    @Before
    public void deleteFile() {
        final File f = new File("out.txt");
        while (f.exists()) {
            f.delete();
            try {
                Thread.sleep(250);
            } catch (final InterruptedException e) {
                // ignore
            }
        }
    }

    @After
    public void deleteTestFile() {
        // if (this.state.getSession() != null) {
        // this.state.getSession().close();
        // }
        new File("out.txt").delete();
    }

    @Test
    public void testAcceptInValidParameter() {
        state.setInput("selecx ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add select");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("selectx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("selectx xx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter5() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("select *; > ");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter6() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("select *; < ");

        assertThat(command.accept(state), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptNull() {
        assertThat(command.accept(null), is(false));
    }

    @Test
    public void testAcceptNullInout() {
        state.setInput(null);

        assertThat(command.accept(state), is(false));
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

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNull() {
        command.execute(null, null, null);
    }

    @Test
    public void testInvalidMultiLineQueryWithTargetError()
        throws SLException,
            IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state.setInput("define ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("define \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state
                .setInput("target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b); > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(
                state.getLastQuery(),
                is("define \ntarget org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidMultiLineQueryWithVariablesError()
            throws SLException, IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state.setInput("select ");
        state.appendBuffer("something");

        command.execute(reader, out, state);

        assertThat(state.getLastQuery(), is(""));
        assertThat(state.getBuffer(), is("select \n"));
        assertThat(state.getActiveCommand(), is(notNullValue()));

        state
                .setInput("** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(
                state.getLastQuery(),
                is("select \n** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));
        assertThat(state.getActiveCommand(), is(nullValue()));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidQueryWithTargetError()
        throws SLException,
            IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state
                .setInput("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b); > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(
                state.getLastQuery(),
                is("define target org.openspotlight.graph.query.console.test.domain.JavaInterface select * by link JavaTypeMethod (b);"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testInvalidQueryWithVariablesError()
        throws SLException,
            IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state
                .setInput("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(
                state.getLastQuery(),
                is("select ** where org.openspotlight.graph.query.console.test.domain.JavaInterface property caption == $var;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testValidMultiLineParameter()
        throws SLException, IOException,
            ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

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
    public void testValidMultiLineParameter2()
        throws SLException, IOException,
            ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

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

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.isFile(), is(true));
        final String fileContent = getFileContent(generatedFile);
        assertThat(fileContent, is(is(notNullValue())));
        assertThat(fileContent.length(), is(not(0)));
    }

    @Test
    public void testValidMultiLineParameterSyntaxError()
        throws SLException,
            IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

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

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }

    @Test
    public void testValidParameter()
        throws SLException, IOException,
            ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state.setInput("select *;");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;"));
    }

    @Test
    public void testValidParameter2()
        throws SLException, IOException,
            ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state.setInput("select *; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.isFile(), is(true));
        final String fileContent = getFileContent(generatedFile);
        assertThat(fileContent, is(notNullValue()));
        assertThat(fileContent.length(), is(not(0)));
    }

    @Test
    public void testValidParameterSyntaxError()
        throws SLException,
            IOException, ClassNotFoundException {
        final GraphConnection graphConnection = new GraphConnection();
        command = new QueryCommand();
        state = new ConsoleState(graphConnection.connect("localhost",
                7070, "sa", "sa", DEFAULT_REPOSITORY_NAME).from(
                GraphLocation.SERVER));

        state.setInput("select *?*; > out.txt");

        command.execute(reader, out, state);

        assertThat(state.getBuffer().length(), is(0));
        assertThat(state.getLastQuery(), is("select *?*;"));

        final File generatedFile = new File("out.txt");
        assertThat(generatedFile.exists(), is(false));
    }
}
