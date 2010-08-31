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
package org.openspotlight.bundle.language.java.bundle;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLArtifactStream;
import org.openspotlight.bundle.common.parser.SLArtifactStreamBasicImpl;
import org.openspotlight.bundle.common.parser.SLCommonTreeAdaptor;
import org.openspotlight.bundle.language.java.parser.JavaLexer;
import org.openspotlight.bundle.language.java.parser.JavaParser;
import org.openspotlight.bundle.language.java.parser.executor.JavaLexerExecutor;
import org.openspotlight.bundle.language.java.parser.executor.JavaParserExecutor;
import org.openspotlight.bundle.language.java.parser.executor.JavaParserNodeHelper;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.bundle.processing.BundleProcessorArtifactPhase;
import org.openspotlight.bundle.processing.CurrentProcessorContext;
import org.openspotlight.graph.SLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaLexerAndParserTypesPhase implements BundleProcessorArtifactPhase<StringArtifact> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void beforeProcessArtifact( final StringArtifact artifact,
                                       final CurrentProcessorContext currentContext,
                                       final ExecutionContext context ) {

    }

    public void didFinishToProcessArtifact( final StringArtifact artifact,
                                            final LastProcessStatus status,
                                            final CurrentProcessorContext currentContext,
                                            final ExecutionContext context ) {

    }

    public Class<StringArtifact> getArtifactType() {
        return StringArtifact.class;
    }

    public LastProcessStatus processArtifact( final StringArtifact artifact,
                                              final CurrentProcessorContext currentContext,
                                              final ExecutionContext context ) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(" starting to process artifact " + artifact);
        }
        try {

            final SLNode contextNode = currentContext.getNodeForUniqueBundleConfig();

            final SLArtifactStream stream = new SLArtifactStreamBasicImpl(
                                                                          artifact.getArtifactCompleteName(),
                                                                          artifact.getContent().get(
                                                                                                    context.getPersistentArtifactManager().getSimplePersist()),
                                                                          artifact.getVersion());
            final JavaLexer lexer = new JavaLexer(stream);
            final SourceLineInfoAggregator sourceLine = new SourceLineInfoAggregator();
            final JavaLexerExecutor lexerExecutor = new JavaLexerExecutor(context.getSimplePersistFactory(), artifact, sourceLine);
            lexer.setLexerExecutor(lexerExecutor);
            final CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
            commonTokenStream.getTokens();
            final JavaParser parser = new JavaParser(commonTokenStream);
            parser.setTreeAdaptor(new SLCommonTreeAdaptor());

            final JavaParserNodeHelper helper = new JavaParserNodeHelper(contextNode, context.getGraphSession());
            final JavaParserExecutor parserExecutor = new JavaParserExecutor(sourceLine, helper);
            parser.setParserExecutor(parserExecutor);
            final Tree tree = (Tree)parser.compilationUnit().getTree();
            final JavaTransientDto dto = JavaTransientDto.fromParser().withStream(stream).withLexer(lexer).withSourceline(
                                                                                                                          sourceLine).withLexerExecutor(
                                                                                                                                                        lexerExecutor).withCommonTokenStream(
                                                                                                                                                                                             commonTokenStream).withParser(
                                                                                                                                                                                                                           parser).withParserExecutor(
                                                                                                                                                                                                                                                      parserExecutor).withTree(
                                                                                                                                                                                                                                                                               tree).create();
            artifact.getTransientMap().put("DTO-Parser", dto);

            return LastProcessStatus.PROCESSED;
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("ending to process artifact " + artifact);
            }
        }
    }

}
