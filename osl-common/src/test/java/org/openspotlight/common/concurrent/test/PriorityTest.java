/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.common.concurrent.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.concurrent.Priority.createPriority;

import org.junit.Test;

public class PriorityTest {

    @Test
    public void shouldCompare()
        throws Exception {
        assertThat(createPriority(2).compareTo(createPriority(1)), is(1));
        assertThat(createPriority(1).compareTo(createPriority(2)), is(-1));
        assertThat(createPriority(1).compareTo(createPriority(1)), is(0));

        assertThat(createPriority(2, 1).compareTo(createPriority(1)), is(1));
        assertThat(createPriority(1).compareTo(createPriority(2, 1)), is(-1));
        assertThat(createPriority(1, 1).compareTo(createPriority(1, 1)), is(0));

        assertThat(createPriority(2, 1).compareTo(createPriority(1, 1)), is(1));
        assertThat(createPriority(1, 1).compareTo(createPriority(2, 1)), is(-1));
        assertThat(createPriority(1, 1).compareTo(createPriority(1, 1)), is(0));

        assertThat(createPriority(2).compareTo(createPriority(1, 1)), is(1));
        assertThat(createPriority(1, 1).compareTo(createPriority(2)), is(-1));
        assertThat(createPriority(1, 1, 1).compareTo(createPriority(1, 1, 1)), is(0));

        assertThat(createPriority(1, 2).compareTo(createPriority(1, 1)), is(1));
        assertThat(createPriority(1, 1).compareTo(createPriority(1, 2)), is(-1));
    }

    @Test
    public void shouldDoEquals()
        throws Exception {
        assertThat(createPriority(2).equals(createPriority(1)), is(false));
        assertThat(createPriority(1).equals(createPriority(2)), is(false));
        assertThat(createPriority(1).equals(createPriority(1)), is(true));

        assertThat(createPriority(2, 1).equals(createPriority(1)), is(false));
        assertThat(createPriority(1).equals(createPriority(2, 1)), is(false));
        assertThat(createPriority(1, 1).equals(createPriority(1, 1)), is(true));

        assertThat(createPriority(2, 1).equals(createPriority(1, 1)), is(false));
        assertThat(createPriority(1, 1).equals(createPriority(2, 1)), is(false));
        assertThat(createPriority(1, 1).equals(createPriority(1, 1)), is(true));

        assertThat(createPriority(2).equals(createPriority(1, 1)), is(false));
        assertThat(createPriority(1, 1).equals(createPriority(2)), is(false));
        assertThat(createPriority(1, 1, 1).equals(createPriority(1, 1, 1)), is(true));

        assertThat(createPriority(1, 2).equals(createPriority(1, 1)), is(false));
        assertThat(createPriority(1, 1).equals(createPriority(1, 2)), is(false));
    }

    @Test
    public void shouldDoHashCode()
        throws Exception {
        assertThat(createPriority(2).hashCode() == createPriority(1).hashCode(), is(false));
        assertThat(createPriority(1).hashCode() == createPriority(2).hashCode(), is(false));
        assertThat(createPriority(1).hashCode() == createPriority(1).hashCode(), is(true));

        assertThat(createPriority(2, 1).hashCode() == createPriority(1).hashCode(), is(false));
        assertThat(createPriority(1).hashCode() == createPriority(2, 1).hashCode(), is(false));
        assertThat(createPriority(1, 1).hashCode() == createPriority(1, 1).hashCode(), is(true));

        assertThat(createPriority(2, 1).hashCode() == createPriority(1, 1).hashCode(), is(false));
        assertThat(createPriority(1, 1).hashCode() == createPriority(2, 1).hashCode(), is(false));
        assertThat(createPriority(1, 1).hashCode() == createPriority(1, 1).hashCode(), is(true));

        assertThat(createPriority(2).hashCode() == createPriority(1, 1).hashCode(), is(false));
        assertThat(createPriority(1, 1).hashCode() == createPriority(2).hashCode(), is(false));
        assertThat(createPriority(1, 1, 1).hashCode() == createPriority(1, 1, 1).hashCode(), is(true));

        assertThat(createPriority(1, 2).hashCode() == createPriority(1, 1).hashCode(), is(false));
        assertThat(createPriority(1, 1).hashCode() == createPriority(1, 2).hashCode(), is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateWithNegative() {
        createPriority(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateWithNegativeOnSubLevel() {
        createPriority(1, -1);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateWithZero() {
        createPriority(0);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotCreateWithZeroOnSubLevel() {
        createPriority(1, 0);
    }

}
