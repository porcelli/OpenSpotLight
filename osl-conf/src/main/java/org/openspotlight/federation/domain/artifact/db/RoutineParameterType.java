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
package org.openspotlight.federation.domain.artifact.db;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

public enum RoutineParameterType {
    /**
     * Input parameter.
     */
    IN(1),
    /**
     * Input/Output parameter.
     */
    INOUT(2),
    /**
     * Output parameter.
     */
    OUT(4),
    /**
     * Column result as described in {@link DatabaseMetaData#procedureColumnResult}.
     */
    RESULT_COLUMN(3),
    /**
     * Return value as described in {@link DatabaseMetaData#procedureColumnReturn}.
     */
    RETURN_VALUE(5),
    /**
     * Wasn't possible to discover its type.
     */
    UNKNOWN(0);

    /**
     * Internal cache
     */
    private static final Map<Integer, RoutineParameterType> cache = new HashMap<Integer, RoutineParameterType>();
    private final int                                       sqlTypeValue;

    private RoutineParameterType(
                                  final int sqlTypeValue) {
        this.sqlTypeValue = sqlTypeValue;
    }

    static {
        for (final RoutineParameterType n: values()) {
            cache.put(n.getSqlTypeValue(), n);
        }
    }

    /**
     * Static factory method
     * 
     * @param sqlType
     * @return the correct RoutineParameter type by sql int constant
     */
    public static RoutineParameterType getTypeByInt(final int sqlType) {
        return cache.get(sqlType);
    }

    /**
     * @return the int value equivalent to {@link Types} constants
     */
    public int getSqlTypeValue() {
        return sqlTypeValue;
    }
}
