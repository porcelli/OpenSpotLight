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
package org.openspotlight.graph;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Interface SLLineReference.
 * 
 * @author Vitor Hugo Chagas
 */
public final class SLLineReference implements SimpleNodeType {

    private String nodeId;
    private int    startLine;
    private int    endLine;
    private int    startColumn;
    private int    endColumn;
    private String statement;
    private String artifactId;
    private String artifactVersion;

    @KeyProperty
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId( String nodeId ) {
        this.nodeId = nodeId;
    }

    @KeyProperty
    public int getStartLine() {
        return startLine;
    }

    public void setStartLine( int startLine ) {
        this.startLine = startLine;
    }

    @KeyProperty
    public int getEndLine() {
        return endLine;
    }

    public void setEndLine( int endLine ) {
        this.endLine = endLine;
    }

    @KeyProperty
    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn( int startColumn ) {
        this.startColumn = startColumn;
    }

    @KeyProperty
    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn( int endColumn ) {
        this.endColumn = endColumn;
    }

    @KeyProperty
    public String getStatement() {
        return statement;
    }

    public void setStatement( String statement ) {
        this.statement = statement;
    }

    @KeyProperty
    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId( String artifactId ) {
        this.artifactId = artifactId;
    }

    @KeyProperty
    public String getArtifactVersion() {
        return artifactVersion;
    }

    public void setArtifactVersion( String artifactVersion ) {
        this.artifactVersion = artifactVersion;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SLLineReference that = (SLLineReference)o;

        if (endColumn != that.endColumn) return false;
        if (endLine != that.endLine) return false;
        if (startColumn != that.startColumn) return false;
        if (startLine != that.startLine) return false;
        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null) return false;
        if (artifactVersion != null ? !artifactVersion.equals(that.artifactVersion) : that.artifactVersion != null)
            return false;
        if (statement != null ? !statement.equals(that.statement) : that.statement != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startLine;
        result = 31 * result + endLine;
        result = 31 * result + startColumn;
        result = 31 * result + endColumn;
        result = 31 * result + (statement != null ? statement.hashCode() : 0);
        result = 31 * result + (artifactId != null ? artifactId.hashCode() : 0);
        result = 31 * result + (artifactVersion != null ? artifactVersion.hashCode() : 0);
        return result;
    }
}
