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
package org.openspotlight.bundle.language.java.resolver;

import org.openspotlight.common.util.Exceptions;

import java.util.HashSet;
import java.util.Set;

public enum JavaLiteralType {
    BOOLEAN("boolean", "(true|false)"),

    CHAR("char", "^['](.*)[']$"),

    INT("int", "[+-]?((0x[0123456789ABCDEFabcdef]+)|\\d+)"),

    LONG("long", "[+-]?((0x[0123456789ABCDEFabcdef]+)|\\d+)[lL]"),

    FLOAT("float", "[+-]?(\\d+)?([.])?(\\d+)?((e|E)\\d+)?[fF]"),

    DOUBLE("double",
           "[+-]?(((\\d+)?([.])?(\\d+)?((e|E)\\d+)?[dD])|((\\d+)?[.](\\d+)?((e|E)\\d+)?)|((\\d+)?[.]?(\\d+)?(e|E)(\\d+)?))"),

    STRING("java.lang.String", "^[\"](.*)[\"]$");

    public static JavaLiteralType findLiteralType( final String literal ) {
        final Set<JavaLiteralType> matched = new HashSet<JavaLiteralType>();
        for (final JavaLiteralType type : JavaLiteralType.values()) {
            if (type.matches(literal)) {
                matched.add(type);
            }
        }
        if (matched.size() > 1) {
            throw Exceptions.logAndReturn(new IllegalStateException("matched " + matched + " types for literal " + literal));
        }
        if (matched.size() == 0) {
            return null;
        }
        return matched.iterator().next();
    }

    private final String regex;

    private final String name;

    private JavaLiteralType(
                             final String name, final String regex ) {
        this.name = name;
        this.regex = regex;
    }

    public boolean matches( final String literal ) {
        return literal.matches(regex);
    }

    public String toString() {
        return name;
    }
}