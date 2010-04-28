/*
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
package org.openspotlight.bundle.language.java;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.bundle.language.java.resolver.JavaLiteralType;

public class LiteralMatchingTest {

    @Test
    public void shouldFindBooleanLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("false"), is(JavaLiteralType.BOOLEAN));
        assertThat(JavaLiteralType.findLiteralType("true"), is(JavaLiteralType.BOOLEAN));
    }

    @Test
    public void shouldFindCharLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("'a'"), is(JavaLiteralType.CHAR));
        assertThat(JavaLiteralType.findLiteralType("'1'"), is(JavaLiteralType.CHAR));
        assertThat(JavaLiteralType.findLiteralType("'\u0001'"), is(JavaLiteralType.CHAR));
    }

    @Test
    public void shouldFindDoubleLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E10101d"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E10101D"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType(".0"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.0"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123E10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType(".123E10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("123.123E10101"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("-0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10101d"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("-0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10101D"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("-.0"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.0"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123E10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-.123E10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10101"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("+0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.0d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10101d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E1d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10d"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10101d"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("+0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.0D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10101D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E1D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10D"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10101D"), is(JavaLiteralType.DOUBLE));

        assertThat(JavaLiteralType.findLiteralType("+.0"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.0"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123E10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+.123E10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10101"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E1"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10"), is(JavaLiteralType.DOUBLE));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10101"), is(JavaLiteralType.DOUBLE));

    }

    @Test
    public void shouldFindFloatLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123E10101f"), is(JavaLiteralType.FLOAT));

        assertThat(JavaLiteralType.findLiteralType("0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123E10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType(".123E10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("123.123E10101F"), is(JavaLiteralType.FLOAT));

        assertThat(JavaLiteralType.findLiteralType("-0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10101f"), is(JavaLiteralType.FLOAT));

        assertThat(JavaLiteralType.findLiteralType("-0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123E10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-.123E10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("-123.123E10101F"), is(JavaLiteralType.FLOAT));

        assertThat(JavaLiteralType.findLiteralType("+0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.0f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123e1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123E1f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10101f"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.0F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123E10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+.123E10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123e1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123e10101F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123E1F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10F"), is(JavaLiteralType.FLOAT));
        assertThat(JavaLiteralType.findLiteralType("+123.123E10101F"), is(JavaLiteralType.FLOAT));

    }

    @Test
    public void shouldFindIntLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("1"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("10"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("10101"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("01"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("0123"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("0x123"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("0xABC"), is(JavaLiteralType.INT));

        assertThat(JavaLiteralType.findLiteralType("-1"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("-10"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("-10101"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("-01"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("-0123"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("-0x123"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("-0xABC"), is(JavaLiteralType.INT));

        assertThat(JavaLiteralType.findLiteralType("+1"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("+10"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("+10101"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("+01"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("+0123"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("+0x123"), is(JavaLiteralType.INT));
        assertThat(JavaLiteralType.findLiteralType("+0xABC"), is(JavaLiteralType.INT));
    }

    @Test
    public void shouldFindLongLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("1l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("10l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("10101l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("01l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("0123l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("0x123l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("1L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("10L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("10101L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("01L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("0123L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("0x123L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("0xABCL"), is(JavaLiteralType.LONG));

        assertThat(JavaLiteralType.findLiteralType("-1l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-10l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-10101l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-01l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-0123l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-0x123l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-1L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-10L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-10101L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-01L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-0123L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-0x123L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("-0xABCL"), is(JavaLiteralType.LONG));

        assertThat(JavaLiteralType.findLiteralType("+1l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+10l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+10101l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+01l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+0123l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+0x123l"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+1L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+10L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+10101L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+01L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+0123L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+0x123L"), is(JavaLiteralType.LONG));
        assertThat(JavaLiteralType.findLiteralType("+0xABCL"), is(JavaLiteralType.LONG));

    }

    @Test
    public void shouldFindStringLiterals() throws Exception {
        assertThat(JavaLiteralType.findLiteralType("\"\""), is(JavaLiteralType.STRING));
        assertThat(JavaLiteralType.findLiteralType("\"1\""), is(JavaLiteralType.STRING));
        assertThat(JavaLiteralType.findLiteralType("\"alalalala\""), is(JavaLiteralType.STRING));
        assertThat(JavaLiteralType.findLiteralType("\"123d\""), is(JavaLiteralType.STRING));
    }

}
