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
package org.openspotlight.bundle.common.metrics;

public class SimpleSourceLineInfo {
    private int physicalLines;
    private int efectiveLines;
    private int logicalLines;
    private int logicalLinesOfCode;
    private int logicalLinesOfWhitespace;
    private int commentedLines;
    private int fullCommentLines;
    private int meaningfulCommentLines;

    public void add( final SimpleSourceLineInfo otherSource ) {
        physicalLines += otherSource.getPhysicalLines();
        efectiveLines += otherSource.getEfectiveLines();
        logicalLines += otherSource.getLogicalLines();
        logicalLinesOfCode += otherSource.getLogicalLinesOfCode();
        logicalLinesOfWhitespace += otherSource.getLogicalLinesOfWhitespace();
        commentedLines += otherSource.getCommentedLines();
        fullCommentLines += otherSource.getFullCommentLines();
        meaningfulCommentLines += otherSource.getMeaningfulCommentLines();
    }

    public Integer getCommentedLines() {
        return commentedLines;
    }

    public Integer getEfectiveLines() {
        return efectiveLines;
    }

    public Integer getFullCommentLines() {
        return fullCommentLines;
    }

    public Integer getLogicalLines() {
        return logicalLines;
    }

    public Integer getLogicalLinesOfCode() {
        return logicalLinesOfCode;
    }

    public Integer getLogicalLinesOfWhitespace() {
        return logicalLinesOfWhitespace;
    }

    public Integer getMeaningfulCommentLines() {
        return meaningfulCommentLines;
    }

    public Integer getPhysicalLines() {
        return physicalLines;
    }

    public void setCommentedLines( final int commentedLines ) {
        this.commentedLines = commentedLines;
    }

    public void setEfectiveLines( final int efectiveLines ) {
        this.efectiveLines = efectiveLines;
    }

    public void setFullCommentLines( final int fullCommentLines ) {
        this.fullCommentLines = fullCommentLines;
    }

    public void setLogicalLines( final int logicalLines ) {
        this.logicalLines = logicalLines;
    }

    public void setLogicalLinesOfCode( final int logicalLinesOfCode ) {
        this.logicalLinesOfCode = logicalLinesOfCode;
    }

    public void setLogicalLinesOfWhitespace( final int logicalLinesOfWhitespace ) {
        this.logicalLinesOfWhitespace = logicalLinesOfWhitespace;
    }

    public void setMeaningfulCommentLines( final int meaningfulCommentLines ) {
        this.meaningfulCommentLines = meaningfulCommentLines;
    }

    public void setPhysicalLines( final int physicalLines ) {
        this.physicalLines = physicalLines;
    }
}
