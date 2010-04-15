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
package org.openspotlight.federation.domain.artifact.db;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Equals.eachEquality;

import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.persist.annotation.Name;

@Name( "database" )
public class PrimaryKeyConstraintArtifact extends ConstraintArtifact {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3232090373069518849L;
    private String            constraintName;
    private String            catalogName;
    private String            schemaName;

    private String            tableName;
    private String            columnName;

    @Override
    public boolean contentEquals( final Artifact other ) {
        if (!equals(other)) {
            return false;
        }
        final PrimaryKeyConstraintArtifact that = (PrimaryKeyConstraintArtifact)other;
        return eachEquality(of(tableName, columnName), andOf(that.tableName,
                                                             that.columnName));
    }

    @SuppressWarnings( "unchecked" )
    public boolean equals( final Object o ) {
        if (!(o instanceof PrimaryKeyConstraintArtifact)) {
            return false;
        }
        final PrimaryKeyConstraintArtifact that = (PrimaryKeyConstraintArtifact)o;

        return eachEquality(
                            of(constraintName, catalogName, schemaName, getDatabaseName(),
                               getDatabaseType(), getServerName(), getUrl()),
                            andOf(of(that.constraintName, that.catalogName,
                                     that.schemaName, that.getDatabaseName(), that
                                                                                  .getDatabaseType(), that.getServerName(), that
                                                                                                                                .getUrl())));
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public int hashCode() {
        return HashCodes
                        .hashOf(constraintName, catalogName, schemaName,
                                getDatabaseName(), getDatabaseType(), getServerName(),
                                getUrl());
    }

    public void setCatalogName( final String catalogName ) {
        this.catalogName = catalogName;
    }

    public void setColumnName( final String columnName ) {
        this.columnName = columnName;
    }

    public void setConstraintName( final String constraintName ) {
        this.constraintName = constraintName;
    }

    public void setSchemaName( final String schemaName ) {
        this.schemaName = schemaName;
    }

    public void setTableName( final String tableName ) {
        this.tableName = tableName;
    }

}
