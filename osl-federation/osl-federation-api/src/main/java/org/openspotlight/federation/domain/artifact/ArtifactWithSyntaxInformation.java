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

import java.util.HashSet;
import java.util.Set;


public abstract class ArtifactWithSyntaxInformation extends Artifact {

    private static final long      serialVersionUID     = -3359480990669655877L;

    /** The syntax information set. */
    private Set<SyntaxInformation> syntaxInformationSet = new HashSet<SyntaxInformation>();

    public ArtifactWithSyntaxInformation() {
        super();
    }

    /**
     * Adds the syntax information.
     * 
     * @param lineStart the line start
     * @param lineEnd the line end
     * @param columnStart the column start
     * @param columnEnd the column end
     * @param type the type
     */
    public void addSyntaxInformation( final int lineStart,
                                      final int lineEnd,
                                      final int columnStart,
                                      final int columnEnd,
                                      final SyntaxInformationType type ) {
        final SyntaxInformation syntaxInformation = new SyntaxInformation();
        syntaxInformation.setColumnEnd(columnEnd);
        syntaxInformation.setColumnStart(columnStart);
        syntaxInformation.setLineEnd(lineEnd);
        syntaxInformation.setLineStart(lineStart);
        syntaxInformation.setStreamArtifact(this);
        syntaxInformation.setType(type);
        this.syntaxInformationSet.add(syntaxInformation);
    }

    /**
     * Clear syntax information set.
     */
    public void clearSyntaxInformationSet() {
        this.syntaxInformationSet.clear();
    }

    /**
     * Gets the syntax information set.
     * 
     * @return the syntax information set
     */
    public Set<SyntaxInformation> getSyntaxInformationSet() {
        return this.syntaxInformationSet;
    }

    /**
     * Removes the syntax information.
     * 
     * @param lineStart the line start
     * @param lineEnd the line end
     * @param columnStart the column start
     * @param columnEnd the column end
     * @param type the type
     */
    public void removeSyntaxInformation( final int lineStart,
                                         final int lineEnd,
                                         final int columnStart,
                                         final int columnEnd,
                                         final SyntaxInformationType type ) {
        final SyntaxInformation syntaxInformation = new SyntaxInformation();
        syntaxInformation.setColumnEnd(columnEnd);
        syntaxInformation.setColumnStart(columnStart);
        syntaxInformation.setLineEnd(lineEnd);
        syntaxInformation.setLineStart(lineStart);
        syntaxInformation.setStreamArtifact(this);
        syntaxInformation.setType(type);
        this.syntaxInformationSet.remove(syntaxInformation);
    }

    /**
     * Removes the syntax information.
     * 
     * @param syntaxInformation the syntax information
     */
    public void removeSyntaxInformation( final SyntaxInformation syntaxInformation ) {
        this.syntaxInformationSet.remove(syntaxInformation);
    }

    /**
     * Sets the syntax information set.
     * 
     * @param syntaxInformationSet the new syntax information set
     */
    public void setSyntaxInformationSet( final Set<SyntaxInformation> syntaxInformationSet ) {
        this.syntaxInformationSet = syntaxInformationSet;
    }

}
