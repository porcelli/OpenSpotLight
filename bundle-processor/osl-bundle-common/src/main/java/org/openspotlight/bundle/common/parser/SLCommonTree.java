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

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openspotlight.graph.SLNode;

public class SLCommonTree extends CommonTree implements SLLineInfo {

    private int    startCharOffset       = -1;
    private int    endCharOffset         = -1;
    private int    endCharPositionInLine = -1;
    private int    endLine               = -1;

    private SLNode node;

    public SLCommonTree() {
        super();
    }

    public SLCommonTree(
                         final CommonTree node ) {
        super(node);
        token = node.token;
        if (node instanceof SLLineInfo) {
            SLLineInfo typed = (SLLineInfo)node;
            setEndCharPositionInLine(typed.getEndCharPositionInLine());
            setEndLine(typed.getEndLine());
        }
    }

    public SLCommonTree(
                         final Token t ) {
        super(t);
        if (t instanceof SLLineInfo) {
            SLLineInfo typed = (SLLineInfo)t;
            setEndCharPositionInLine(typed.getEndCharPositionInLine());
            setEndLine(typed.getEndLine());
        }
    }

    @Override
    public Tree dupNode() {
        return new SLCommonTree(this);
    }

    public int getEndCharOffset() {
        return endCharOffset;
    }

    public SLNode getNode() {
        return node;
    }

    public int getStartCharOffset() {
        return startCharOffset;
    }

    public void setEndCharOffset( final int endCharOffset ) {
        this.endCharOffset = endCharOffset;
    }

    public void setNode( final SLNode node ) {
        this.node = node;
    }

    public void setStartCharOffset( final int startCharOffset ) {
        this.startCharOffset = startCharOffset;
    }

    public int getEndCharPositionInLine() {
        if ((this.token == null) || (this.token.getCharPositionInLine() == -1)) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(getChildCount())).getEndCharPositionInLine();
            }
            return 0;
        }
        return this.endCharPositionInLine;
    }

    public void setEndCharPositionInLine( int charPositionInLine ) {
        this.endCharPositionInLine = charPositionInLine;
    }

    public int getEndLine() {
        if ((this.token == null) || (this.token.getCharPositionInLine() == -1)) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(getChildCount())).getEndLine();
            }
            return 0;
        }
        return this.endLine;
    }

    public void setEndLine( int endLine ) {
        this.endLine = endLine;
    }

    public int getStartCharPositionInLine() {
        return getCharPositionInLine();
    }

    public int getStartLine() {
        return getLine();
    }

    public SLArtifactStream getArtifact() {
        if ((this.token == null)) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(0)).getArtifact();
            }
            return null;
        }
        return ((SLCommonToken)this.token).getArtifact();
    }
}
