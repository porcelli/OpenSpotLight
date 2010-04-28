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

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

public class SLCommonTreeAdaptor extends CommonTreeAdaptor {

    @Override
    public void addChild( final Object t,
                          final Object child ) {
        if (t != null && child != null) {
            if (t instanceof SLCommonTree && child instanceof SLCommonTree) {
                final SLCommonTree tParent = (SLCommonTree)t;
                final SLCommonTree tChild = (SLCommonTree)child;

                if (0 >= tParent.getStartCharOffset()) {
                    tParent.setStartCharOffset(tChild.getStartCharOffset());
                    tParent.setEndCharOffset(tChild.getEndCharOffset());
                }
                if (0 < tParent.getChildCount()) {
                    tParent.setEndCharOffset(tChild.getEndCharOffset());
                }
            }
            final Tree typedT = (Tree)t;
            final Tree typedChild = (Tree)child;
            typedT.addChild(typedChild);
        }
    }

    @Override
    public Object create( final int tokenType,
                          final Token fromToken,
                          final String text ) {
        if (fromToken instanceof SLCommonToken) {
            final SLCommonTree result = (SLCommonTree)super.create(tokenType, fromToken, text);
            return result;
        }
        return super.create(tokenType, fromToken, text);
    }

    @Override
    public Object create( final Token payload ) {
        final SLCommonTree tree = new SLCommonTree(payload);
        if (payload instanceof SLCommonToken) {
            final CommonToken typed = (CommonToken)payload;
            tree.setStartCharOffset(typed.getStartIndex());
            tree.setEndCharOffset(typed.getStopIndex());
        }
        return tree;
    }

    @Override
    public Token createToken( final int tokenType,
                              final String text ) {
        return new SLCommonToken(tokenType, text);
    }

    @Override
    public Token createToken( final Token fromToken ) {
        return new SLCommonToken(fromToken);
    }

    @Override
    public Object errorNode( final TokenStream input,
                             final Token start,
                             final Token stop,
                             final RecognitionException e ) {
        final SLCommonTree t = new SLCommonTree();
        return t;
    }

}
