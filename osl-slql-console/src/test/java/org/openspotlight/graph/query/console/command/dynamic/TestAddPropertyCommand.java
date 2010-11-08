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
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.AbstractCommandTest;

public class TestAddPropertyCommand extends AbstractCommandTest {

    @Override
    protected void setupCommand() {
        command = new AddPropertyCommand();
    }

    @Test
    public void testAcceptInValidParameter() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add propertx myProperty");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter2() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add property");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter3() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add propertyx");

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptInValidParameter4() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add property ");

        assertThat(command.accept(state), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptNull() {
        assertThat(command.accept(null), is(false));
    }

    @Test
    public void testAcceptNullInout() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput(null);

        assertThat(command.accept(state), is(false));
    }

    @Test
    public void testAcceptValidParameter() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add property myProperty");

        assertThat(command.accept(state), is(true));
    }

    @Test
    public void testAcceptValidParameter2() {
        final ConsoleState state = new ConsoleState(null);
        state.setInput("add property ?");

        assertThat(command.accept(state), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExecuteNull() {
        command.execute(null, null, null);
    }

    @Test
    public void testValidParameter() {
        final ConsoleState state = new ConsoleState(null);
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
        final ConsoleState state = new ConsoleState(null);
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
