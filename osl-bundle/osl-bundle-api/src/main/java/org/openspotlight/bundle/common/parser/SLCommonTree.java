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
package org.openspotlight.bundle.common.parser;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.openspotlight.graph.SLNode;

public class SLCommonTree extends CommonTree implements SLLineInfo {

    private int              startCharOffset       = -1;
    private int              endCharOffset         = -1;
    private int              endCharPositionInLine = -1;
    private int              endLine               = -1;

    private SLNode           node;

    private SLArtifactStream artifactCache         = null;

    public SLCommonTree() {
        super();
    }

    public SLCommonTree(
                         final CommonTree node ) {
        super(node);
        token = node.token;
        if (node instanceof SLLineInfo) {
            final SLLineInfo typed = (SLLineInfo)node;
            setEndCharPositionInLine(typed.getEndCharPositionInLine());
            setEndLine(typed.getEndLine());
        }
    }

    public SLCommonTree(
                         final Token t ) {
        super(t);
        if (t instanceof SLLineInfo) {
            final SLLineInfo typed = (SLLineInfo)t;
            setEndCharPositionInLine(typed.getEndCharPositionInLine());
            setEndLine(typed.getEndLine());
        }
    }

    @Override
    public Tree dupNode() {
        return new SLCommonTree(this);
    }

    public SLArtifactStream getArtifact() {
        if (artifactCache == null) {
            if (token != null) {
                artifactCache = ((SLCommonToken)token).getArtifact();
            }
            if (artifactCache == null) {
                if (getChildCount() > 0) {
                    for (int i = 0, size = getChildCount(); i < size; i++) {
                        final SLArtifactStream result = ((SLCommonTree)getChild(0)).getArtifact();
                        if (result != null) {
                            artifactCache = result;
                            break;
                        }
                    }
                }

            }
        }
        return artifactCache;
    }

    public int getEndCharOffset() {
        return endCharOffset;
    }

    public int getEndCharPositionInLine() {
        if (token == null || token.getCharPositionInLine() == -1) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(getChildCount() - 1)).getEndCharPositionInLine();
            }
            return 0;
        }
        return endCharPositionInLine;
    }

    public int getEndLine() {
        if (token == null || token.getCharPositionInLine() == -1) {
            if (getChildCount() > 0) {
                return ((SLCommonTree)getChild(getChildCount() - 1)).getEndLine();
            }
            return 0;
        }
        return endLine;
    }

    public SLNode getNode() {
        return node;
    }

    public int getStartCharOffset() {
        return startCharOffset;
    }

    public int getStartCharPositionInLine() {
        return getCharPositionInLine();
    }

    public int getStartLine() {
        return getLine();
    }

    public void setEndCharOffset( final int endCharOffset ) {
        this.endCharOffset = endCharOffset;
    }

    public void setEndCharPositionInLine( final int charPositionInLine ) {
        endCharPositionInLine = charPositionInLine;
    }

    public void setEndLine( final int endLine ) {
        this.endLine = endLine;
    }

    public void setNode( final SLNode node ) {
        this.node = node;
    }

    public void setStartCharOffset( final int startCharOffset ) {
        this.startCharOffset = startCharOffset;
    }
}
