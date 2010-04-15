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
package org.openspotlight.bundle.db.processor.wrapped;

import org.openspotlight.bundle.db.processor.DbWrappedType;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.artifact.db.DatabaseType;

public enum WrappedTypeFactory {

    INSTANCE;

    public DbWrappedType createByType( final DatabaseType type ) {
        Assertions.checkNotNull("type", type);
        if (DatabaseType.DB2.equals(type)) {
            return Db2WrappedType.INSTANCE;
        }
        if (DatabaseType.H2.equals(type)) {
            return H2WrappedType.INSTANCE;
        }
        if (DatabaseType.MY_SQL.equals(type)) {
            return MySqlWrappedType.INSTANCE;
        }
        if (DatabaseType.ORACLE.equals(type)) {
            return OracleWrappedType.INSTANCE;
        }
        if (DatabaseType.ORACLE9.equals(type)) {
            return Oracle9WrappedType.INSTANCE;
        }
        if (DatabaseType.POSTGRES.equals(type)) {
            return PostgresWrappedType.INSTANCE;
        }
        if (DatabaseType.SQL_SERVER.equals(type)) {
            return SqlServerWrappedType.INSTANCE;
        }
        throw Exceptions.logAndReturn(new IllegalArgumentException());
    }
}
