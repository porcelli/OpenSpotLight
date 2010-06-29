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

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.openspotlight.bundle.common.parser.SLArtifactStream;
import org.openspotlight.bundle.common.parser.SLCommonToken;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceLineInfoAggregator {

    private enum CommentState {
        SIMPLE,
        MEANINGFUL
    }

    private enum LineState {
        NONE,
        EMPTY,
        HIDDEN,
        STATEMENT,
        STATEMENT_CONTINUATION
    }

    private static String                                 REGEX_MEANINGFUL_COMMENT = "\\w\\w\\w\\w*\\s+\\w\\w\\w\\w*";
    private static Pattern                                pattern                  = Pattern.compile(REGEX_MEANINGFUL_COMMENT);
    // Map<Source, Map<Line, LineState>>
    private final Map<String, Map<Integer, LineState>>    lineMap                  = new HashMap<String, Map<Integer, LineState>>();
    // Map<Source, Map<Line, CommentState>>
    private final Map<String, Map<Integer, CommentState>> lineCommentMap           = new HashMap<String, Map<Integer, CommentState>>();
    // Map<Source, List<Line>>
    private final Map<String, List<Integer>>              continuationLines        = new HashMap<String, List<Integer>>();
    // Map<Source, Qty of Lines>
    private final Map<String, Integer>                    lines                    = new HashMap<String, Integer>();
    // Map<CodeArea, CompleteSourceLineInfo>
    private final Map<Integer, CompleteSourceLineInfo>    codeArea                 = new TreeMap<Integer, CompleteSourceLineInfo>();

    public void addCodeArea( final TokenStream tokenInput,
                             final int area ) {
        final CompleteSourceLineInfo codeAreaSourceLineInfo = new CompleteSourceLineInfo();

        final SimpleSourceLineInfo lastLineInfo = getSimpleLineSourceInfo(
                                                                          ((SLCommonToken)tokenInput.get(0)).getInputStream().getSourceName(),
                                                                          0, Integer.MAX_VALUE);
        codeAreaSourceLineInfo.add(lastLineInfo);
        codeAreaSourceLineInfo.calculate();

        codeArea.put(area, codeAreaSourceLineInfo);
    }

    public void addCodeArea( final TokenStream tokenInput,
                             final int area,
                             final int startIndex,
                             final int endIndex ) {
        final CompleteSourceLineInfo codeAreaSourceLineInfo = new CompleteSourceLineInfo();
        SLCommonToken previosToken = (SLCommonToken)tokenInput.get(startIndex);
        int startLine = previosToken.getLine();
        for (int i = startIndex; i < endIndex; i++) {
            final SLCommonToken token = (SLCommonToken)tokenInput.get(i);
            if (!token.getInputStream().getSourceName().equals(previosToken.getInputStream().getSourceName())) {
                final SimpleSourceLineInfo activeLineInfo = getSimpleLineSourceInfo(
                                                                                    previosToken.getInputStream().getSourceName(),
                                                                                    startLine, previosToken.getLine());
                codeAreaSourceLineInfo.add(activeLineInfo);
                startLine = token.getLine();
            }
            previosToken = token;
        }
        final SimpleSourceLineInfo lastLineInfo = getSimpleLineSourceInfo(previosToken.getInputStream().getSourceName(),
                                                                          startLine, previosToken.getLine());
        codeAreaSourceLineInfo.add(lastLineInfo);
        codeAreaSourceLineInfo.calculate();
        codeArea.put(area, codeAreaSourceLineInfo);
    }

    public void addContinuationLine( final SLArtifactStream input,
                                     final int lineNumber ) {
        setupSource(input);
        final List<Integer> lineContinuationList = continuationLines.get(input.getSourceName());
        lineContinuationList.add(lineNumber);
    }

    public boolean cointainsCodeArea( final int codeArea ) {
        return this.codeArea.containsKey(codeArea);
    }

    public CompleteSourceLineInfo getCodeArea( final int area ) {
        if (codeArea.containsKey(area)) {
            return codeArea.get(area);
        }
        return null;
    }

    private SimpleSourceLineInfo getSimpleLineSourceInfo( final String sourceName,
                                                          int lineStart,
                                                          int lineEnd ) {
        final SimpleSourceLineInfo info = new SimpleSourceLineInfo();
        if (lineStart == 0) {
            lineStart = 1;
        }
        if (lineEnd == Integer.MAX_VALUE) {
            lineEnd = lines.get(sourceName);
        }
        final int activePhysicalLines = lineEnd - lineStart + 1;
        info.setPhysicalLines(activePhysicalLines);
        int hiddenCount = 0;
        Integer codeCount = 0;
        Integer emptyCount = 0;
        int fullCommenCount = 0;
        for (final Entry<Integer, LineState> activeState : lineMap.get(sourceName).entrySet()) {
            if (activeState.getKey() >= lineStart && activeState.getKey() <= lineEnd) {
                switch (activeState.getValue()) {
                    case HIDDEN:
                        hiddenCount++;
                        break;
                    case STATEMENT:
                        codeCount++;
                        break;
                    case STATEMENT_CONTINUATION:
                        codeCount++;
                        break;
                    case NONE:
                        if (!lineCommentMap.get(sourceName).containsKey(activeState.getKey())) {
                            emptyCount++;
                        }
                        break;
                    case EMPTY:
                        if (!lineCommentMap.get(sourceName).containsKey(activeState.getKey())) {
                            emptyCount++;
                        }
                        break;
                }
                if ((activeState.getValue().equals(LineState.EMPTY) || activeState.getValue().equals(LineState.HIDDEN) || activeState.getValue().equals(
                                                                                                                                                        LineState.NONE))
                    && lineCommentMap.get(sourceName).containsKey(activeState.getKey())) {
                    fullCommenCount++;
                }
            }
        }
        Integer meaningfulCount = 0;
        for (final Entry<Integer, CommentState> activeComment : lineCommentMap.get(sourceName).entrySet()) {
            if (activeComment.getKey() >= lineStart && activeComment.getKey() <= lineEnd) {
                switch (activeComment.getValue()) {
                    case MEANINGFUL:
                        meaningfulCount++;
                        break;
                }
            }
        }
        int continuationCount = 0;
        for (final Integer lineNumber : continuationLines.get(sourceName)) {
            if (lineNumber >= lineStart && lineNumber <= lineEnd) {
                continuationCount++;
            }
        }

        int commentCount = 0;
        for (final Integer lineNumber : lineCommentMap.get(sourceName).keySet()) {
            if (lineNumber >= lineStart && lineNumber <= lineEnd) {
                commentCount++;
            }
        }

        final int efectiveLines = activePhysicalLines - hiddenCount;
        info.setEfectiveLines(efectiveLines);
        final Integer logicalLines = efectiveLines - continuationCount;
        info.setLogicalLines(logicalLines);
        info.setLogicalLinesOfCode(codeCount);
        info.setLogicalLinesOfWhitespace(emptyCount);
        info.setCommentedLines(commentCount);
        info.setFullCommentLines(fullCommenCount);
        info.setMeaningfulCommentLines(meaningfulCount);

        return info;
    }

    public CompleteSourceLineInfo getSourceLineInfo( final String sourceName ) {
        final CompleteSourceLineInfo result = new CompleteSourceLineInfo();
        final SimpleSourceLineInfo sourceInfo = getSimpleLineSourceInfo(sourceName, 0, lines.get(sourceName));
        result.add(sourceInfo);
        result.calculate();
        return result;
    }

    public boolean hasSource( final String sourcePath ) {
        return lines.containsKey(sourcePath);
    }

    public void setCode( final SLArtifactStream input,
                         final int line ) {
        setupSource(input);
        final Map<Integer, LineState> lineMapSource = lineMap.get(input.getSourceName());
        lineMapSource.put(line, LineState.STATEMENT);
    }

    public void setCode( final TokenStream tokenInput,
                         final int startIndex,
                         final int endIndex ) {
        final SLCommonToken previosToken = (SLCommonToken)tokenInput.get(startIndex);
        setLineCode(previosToken, LineState.STATEMENT);
        for (int i = startIndex; i <= endIndex; i++) {
            final SLCommonToken token = (SLCommonToken)tokenInput.get(i);
            if (token.getChannel() != Token.HIDDEN_CHANNEL) {
                if (token.getInputStream().getSourceName().equals(previosToken.getInputStream().getSourceName())) {
                    if (token.getLine() != previosToken.getLine()) {
                        setLineCode(token, LineState.STATEMENT_CONTINUATION);
                    }
                } else {
                    setLineCode(token, LineState.STATEMENT_CONTINUATION);
                }
            }
        }
    }

    public void setComment( final SLArtifactStream input,
                            final int lineStart,
                            final String comment ) {
        setupSource(input);
        final String[] text = comment.split("\n");
        int cont = 0;
        for (final String activeLine : text) {
            final Matcher matcher = pattern.matcher(activeLine);
            if (matcher.find()) {
                setLineComment(input.getSourceName(), lineStart + cont, CommentState.MEANINGFUL);
            } else {
                setLineComment(input.getSourceName(), lineStart + cont, CommentState.SIMPLE);
            }
            cont++;
        }
    }

    public void setHiddenCode( final SLArtifactStream input,
                               final int lineStart,
                               final int lineEnd ) {
        setupSource(input);
        for (int i = lineStart; i <= lineEnd; i++) {
            setHiddenCode(input.getSourceName(), i);
        }
    }

    private void setHiddenCode( final String sourceName,
                                final int line ) {
        final Map<Integer, LineState> lineMapSource = lineMap.get(sourceName);
        lineMapSource.put(line, LineState.HIDDEN);
    }

    public void setHiddenCode( final TokenStream tokenInput,
                               final int startIndex,
                               final int endIndex ) {
        for (int i = startIndex; i < endIndex; i++) {
            final SLCommonToken token = (SLCommonToken)tokenInput.get(i);
            setHiddenCode(token.getInputStream().getSourceName(), token.getLine());
        }
    }

    private void setLineCode( final SLCommonToken token,
                              final LineState state ) {
        final Map<Integer, LineState> lineMapSource = lineMap.get(token.getInputStream().getSourceName());
        lineMapSource.put(token.getLine(), state);
    }

    private void setLineComment( final String sourceName,
                                 final int line,
                                 final CommentState state ) {
        final Map<Integer, CommentState> commentStateMap = lineCommentMap.get(sourceName);
        commentStateMap.put(line, state);
    }

    private void setLineWhitespace( final String sourceName,
                                    final int line ) {
        final Map<Integer, LineState> lineMapSource = lineMap.get(sourceName);
        if (lineMapSource.containsKey(line)) {
            if (lineMapSource.get(line) == LineState.NONE) {
                lineMapSource.put(line, LineState.EMPTY);
            }
        } else {
            lineMapSource.put(line, LineState.EMPTY);
        }
    }

    private void setupSource( final SLArtifactStream source ) {
        final Integer line = lines.get(source.getSourceName());
        if (line == null) {
            final Map<Integer, LineState> sourceLineMap = new TreeMap<Integer, LineState>();
            for (int i = 1; i <= source.getPhysicalLineCount(); i++) {
                sourceLineMap.put(i, LineState.NONE);
            }
            lineMap.put(source.getSourceName(), sourceLineMap);
            lines.put(source.getSourceName(), source.getPhysicalLineCount());
            lineCommentMap.put(source.getSourceName(), new TreeMap<Integer, CommentState>());
            continuationLines.put(source.getSourceName(), new LinkedList<Integer>());
        }
    }

    public void setWhitespace( final SLArtifactStream input,
                               final int lineStart,
                               final int endLine ) {
        setupSource(input);
        for (int i = lineStart; i <= endLine; i++) {
            setLineWhitespace(input.getSourceName(), i);
        }
    }
}
