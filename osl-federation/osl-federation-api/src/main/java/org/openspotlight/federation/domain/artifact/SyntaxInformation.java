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
package org.openspotlight.federation.domain.artifact;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.internal.StreamPropertyWithParent;

/**
 * The Class SyntaxInformation.
 */
public class SyntaxInformation implements StreamPropertyWithParent<Artifact>, Comparable<SyntaxInformation> {
    private static final long serialVersionUID = 9056717121341748618L;

    private static int compare( final int anotherVal,
                                final int thisVal ) {
        return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
    }

    /** The hashcode. */
    private volatile transient int hashcode;

    /** The stream artifact. */
    private Artifact               parent;

    /** The line start. */
    private int                    lineStart;

    /** The line end. */
    private int                    lineEnd;

    /** The column start. */
    private int                    columnStart;

    /** The column end. */
    private int                    columnEnd;

    /** The type. */
    private SyntaxInformationType  type;

    public SyntaxInformation() {
    }

    public SyntaxInformation(
                              final Artifact parent, final int lineStart, final int lineEnd, final int columnStart,
                              final int columnEnd, final SyntaxInformationType type ) {
        this.parent = parent;
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
        this.type = type;
    }

    public int compareTo( final SyntaxInformation o ) {
        int compare = 0;
        if ((compare = compare(lineStart, o.lineStart)) != 0) {
            return compare;
        }
        if ((compare = compare(lineEnd, o.lineEnd)) != 0) {
            return compare;
        }
        if ((compare = compare(columnStart, o.columnStart)) != 0) {
            return compare;
        }
        if ((compare = compare(columnEnd, o.columnEnd)) != 0) {
            return compare;
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings( "boxing" )
    @Override
    public boolean equals( final Object o ) {
        if (!(o instanceof SyntaxInformation)) {
            return false;
        }
        final SyntaxInformation that = (SyntaxInformation)o;
        if (!Equals.eachEquality(parent, that.parent)) {
            return false;
        }
        if (!Equals.eachEquality(type, that.type)) {
            return false;
        }
        if (!Equals.eachEquality(lineStart, that.lineStart)) {
            return false;
        }
        if (!Equals.eachEquality(lineEnd, that.lineEnd)) {
            return false;
        }
        if (!Equals.eachEquality(columnStart, that.columnStart)) {
            return false;
        }
        if (!Equals.eachEquality(columnEnd, that.columnEnd)) {
            return false;
        }
        return true;
    }

    /**
     * Gets the column end.
     * 
     * @return the column end
     */
    public int getColumnEnd() {
        return columnEnd;
    }

    /**
     * Gets the column start.
     * 
     * @return the column start
     */
    public int getColumnStart() {
        return columnStart;
    }

    /**
     * Gets the line end.
     * 
     * @return the line end
     */
    public int getLineEnd() {
        return lineEnd;
    }

    /**
     * Gets the line start.
     * 
     * @return the line start
     */

    public int getLineStart() {
        return lineStart;
    }

    /**
     * Gets the stream artifact.
     * 
     * @return the stream artifact
     */
    public Artifact getParent() {
        return parent;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public SyntaxInformationType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = hashcode;
        if (result == 0) {
            result = 17;
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            result = 31 * result + lineStart;
            result = 31 * result + lineEnd;
            result = 31 * result + columnStart;
            result = 31 * result + columnEnd;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            hashcode = result;
        }
        return result;
    }

    public void setColumnEnd( final int columnEnd ) {
        this.columnEnd = columnEnd;
    }

    public void setColumnStart( final int columnStart ) {
        this.columnStart = columnStart;
    }

    public void setLineEnd( final int lineEnd ) {
        this.lineEnd = lineEnd;
    }

    public void setLineStart( final int lineStart ) {
        this.lineStart = lineStart;
    }

    public void setParent( final Artifact parent ) {
        this.parent = parent;
    }

    public void setType( final SyntaxInformationType type ) {
        this.type = type;
    }

}
