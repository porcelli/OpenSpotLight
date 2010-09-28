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
package org.openspotlight.bundle.common.metrics;

import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.federation.domain.artifact.SyntaxInformationType;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.domain.StorageNode;

public class SyntaxInformationAggregator {
    private final ArtifactWithSyntaxInformation                       artifact;

    private final SimplePersistCapable<StorageNode, StorageSession> simplePersist;

    public SyntaxInformationAggregator(
                                        SimplePersistFactory simplePersistFactory, final ArtifactWithSyntaxInformation artifact ) {
        this.artifact = artifact;
        this.simplePersist = simplePersistFactory.createSimplePersist(RegularPartitions.SYNTAX_HIGHLIGHT);
    }

    public void addHidden( final int tokenLine,
                           final int tokenStartCharPositionInLine,
                           final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenLine, tokenLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.HIDDEN, simplePersist);
    }

    public void addIdentifier( final int tokenLine,
                               final int tokenStartCharPositionInLine,
                               final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenLine, tokenLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.IDENTIFIER, simplePersist);
    }

    public void addMultiLineComment( final int tokenStartLine,
                                     final int tokenStartCharPositionInLine,
                                     final int tokenEndLine,
                                     final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenStartLine, tokenEndLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.COMMENT, simplePersist);
    }

    public void addNumberLiteral( final int tokenLine,
                                  final int tokenStartCharPositionInLine,
                                  final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenLine, tokenLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.NUMBER_LITERAL, simplePersist);
    }

    public void addReserved( final int tokenStartLine,
                             final int tokenStartCharPositionInLine,
                             final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenStartLine, tokenStartLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.RESERVED, simplePersist);
    }

    public void addSingleLineComment( final int tokenLine,
                                      final int tokenStartCharPositionInLine,
                                      final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenLine, tokenLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.COMMENT, simplePersist);
    }

    public void addStringLiteral( final int tokenLine,
                                  final int tokenStartCharPositionInLine,
                                  final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenLine, tokenLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.STRING_LITERAL, simplePersist);
    }

    public void addSymbol( final int tokenLine,
                           final int tokenStartCharPositionInLine,
                           final int tokenEndCharPositionInLine ) {
        artifact.addSyntaxInformation(tokenLine, tokenLine, tokenStartCharPositionInLine, tokenEndCharPositionInLine,
                                      SyntaxInformationType.SYMBOL, simplePersist);
    }

}
