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
package org.openspotlight.bundle.language.java.parser.executor;

import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLCommonToken;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataField;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.Node;

import java.util.List;
import java.util.Stack;

public class JavaParserExecutor implements JavaConstants {
    private final JavaParserNodeHelper        helper;

    // <NodeOnAbstractContext,NodeOnCurrentContext>
    private final Stack<Pair<Node, Node>> typeContext = new Stack<Pair<Node, Node>>();

    private final SourceLineInfoAggregator    sourceLineAggregator;

    public JavaParserExecutor(
                               final SourceLineInfoAggregator sourceLineInfoAggregator, final JavaParserNodeHelper helper ) {
        sourceLineAggregator = sourceLineInfoAggregator;
        this.helper = helper;
    }

    public void createDefaultPackage() {
        final Pair<Node, Node> newNode = helper.createDefaultPackage();
        typeContext.push(newNode);
    }

    public void createFields( final List<String> list ) {
        try {
            final Node parent = typeContext.peek().getK1();
            for (final String s : list) {
                parent.addChildNode(JavaDataField.class, s);
            }

        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public void createJavaTypeAnnotation( final SLCommonToken identifier292 ) {
        final Pair<Node, Node> newNode = helper.createJavaTypeAnnotation(typeContext.peek(), identifier292.getText());
        typeContext.push(newNode);
    }

    public void createJavaTypeClass( final SLCommonToken identifier35 ) {
        final Pair<Node, Node> newNode = helper.createJavaTypeClass(typeContext.peek(), identifier35.getText());
        typeContext.push(newNode);
    }

    public void createJavaTypeEnum( final SLCommonToken identifier54 ) {
        final Pair<Node, Node> newNode = helper.createJavaTypeEnum(typeContext.peek(), identifier54.getText());
        typeContext.push(newNode);

    }

    public void createJavaTypeInterface( final SLCommonToken identifier75 ) {
        final Pair<Node, Node> newNode = helper.createJavaTypeInterface(typeContext.peek(), identifier75.getText());
        typeContext.push(newNode);

    }

    public void createMethodDeclare( final String string ) {
        // TODO Auto-generated method stub

    }

    public void createPackageNode( final Object object ) {
        final Tree qualifiedName = getTree(object);
        final StringBuilder packageName = new StringBuilder();
        for (int i = 0, count = qualifiedName.getChildCount(); i < count; i++) {
            packageName.append(qualifiedName.getChild(i));
        }
        final Pair<Node, Node> newNode = helper.createPackageNode(packageName.toString());
        typeContext.push(newNode);
    }

    private Tree getTree( final Object element ) {
        return (Tree)element;
    }

    public void popContext() {
        typeContext.pop();
    }

    public SourceLineInfoAggregator sourceLine() {
        return sourceLineAggregator;
    }
}
