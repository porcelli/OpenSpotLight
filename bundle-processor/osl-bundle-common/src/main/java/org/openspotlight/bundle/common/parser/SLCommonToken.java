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
package org.openspotlight.bundle.common.parser;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

public class SLCommonToken extends CommonToken implements SLLineInfo {

    private static final long serialVersionUID      = -7832734839073755630L;

    private int               endCharPositionInLine = -1;
    private int               endLine               = -1;

    public SLCommonToken(
                          final CharStream input, final int type,
                          final int channel, final int start, final int stop ) {
        super(type);
        this.input = input;
        this.channel = channel;
        this.start = start;
        this.stop = stop;
    }

    public SLCommonToken(
                          final int type ) {
        super(type);
    }

    public SLCommonToken(
                          final int type, final String text ) {
        super(type);
        channel = DEFAULT_CHANNEL;
        this.text = text;
    }

    public SLCommonToken(
                          final Token oldToken ) {
        super(oldToken.getType());
        text = oldToken.getText();
        line = oldToken.getLine();
        index = oldToken.getTokenIndex();
        charPositionInLine = oldToken.getCharPositionInLine();
        channel = oldToken.getChannel();
        input = oldToken.getInputStream();
        if (oldToken instanceof CommonToken) {
            start = ((CommonToken)oldToken).getStartIndex();
            stop = ((CommonToken)oldToken).getStopIndex();
        }
        if (oldToken instanceof SLCommonToken) {
            endLine = ((SLCommonToken)oldToken).getEndLine();
            endCharPositionInLine = ((SLCommonToken)oldToken)
                                                             .getEndCharPositionInLine();
        }
    }

    public SLArtifactStream getArtifact() {
        if (input instanceof SLArtifactStream) {
            return (SLArtifactStream)input;
        }
        return null;
    }

    public int getEndCharPositionInLine() {
        return endCharPositionInLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndCharPositionInLine( final int endCharPositionInLine ) {
        this.endCharPositionInLine = endCharPositionInLine;
    }

    public void setEndLine( final int endLine ) {
        this.endLine = endLine;
    }

    public int getStartCharPositionInLine() {
        return getCharPositionInLine();
    }

    public int getStartLine() {
        return getLine();
    }

}
