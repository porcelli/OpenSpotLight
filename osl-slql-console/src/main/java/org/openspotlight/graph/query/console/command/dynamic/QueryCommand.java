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
package org.openspotlight.graph.query.console.command.dynamic;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQueryResult;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

/**
 * The Class QueryCommand. This command executes a slql query.
 * 
 * @author porcelli
 */
public class QueryCommand implements DynamicCommand {

    /** The COLUMN_SIZE. */
    private static int COLUMN_SIZE = 36;

    /**
     * {@inheritDoc}
     */
    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        Assertions.checkNotNull("reader", reader);
        Assertions.checkNotNull("out", out);
        Assertions.checkNotNull("state", state);
        if (!accept(state)) {
            return;
        }
        if (state.getInput().endsWith(";") || state.getInput().contains("; > ")) {
            if (state.getActiveCommand() != null) {
                state.appendBuffer(state.getInput());
                state.setInput(state.getBuffer());
            }
            String lastQuery = "";
            String outputFileName = null;
            if (state.getInput().contains("; > ")) {
                int index = state.getInput().lastIndexOf("; > ");
                lastQuery = state.getInput().substring(0, index + 1);
                outputFileName = state.getInput().substring(index + 4);
            } else {
                lastQuery = state.getInput();
            }
            // execute query here
            executeQuery(reader, out, state, lastQuery, outputFileName);
            state.setLastQuery(lastQuery);
            state.clearBuffer();
            state.setActiveCommand(null);
        } else if (state.getInput().contains(";") || state.getInput().contains("; >")) {
            out.println("invalid statement");
            out.flush();
            state.clearBuffer();
            state.setActiveCommand(null);
        } else {
            if (state.getActiveCommand() == null) {
                state.clearBuffer();
                state.setActiveCommand(this);
            }
            state.appendLineBuffer(state.getInput());
        }
        state.setInput(null);
    }

    /**
     * Executes query. If there is any problem during executing, it display its error message. Queries that needs variables
     * content or target can't be executed at slql console application.
     * 
     * @param reader the reader
     * @param out the out
     * @param state the state
     * @param query the query
     * @param outputFileName the output file name
     */
    protected void executeQuery( ConsoleReader reader,
                                 PrintWriter out,
                                 ConsoleState state,
                                 String query,
                                 String outputFileName ) {
        try {
            SLQueryText slqlText = state.getSession().createQueryText(query);
            if (!slqlText.hasTarget() && slqlText.getVariables() == null) {
                SLQueryResult result = slqlText.execute();
                String outputString = generateOutput(result.getNodes(), state.getAdditionalProperties());
                out.println(outputString);
                if (outputFileName != null) {
                    File outputFile = new File(outputFileName);
                    outputFile.createNewFile();
                    PrintWriter fileOut = new PrintWriter(outputFile);
                    fileOut.append("Query: " + query);
                    fileOut.append("\n\n");
                    fileOut.append(outputString);
                    fileOut.flush();
                    fileOut.close();
                }
            } else if (slqlText.hasTarget()) {
                out.println("ERROR: can't execute queries with target.");
            } else if (slqlText.getVariables() == null) {
                out.println("ERROR: can't execute queries with variables.");
            }
        } catch (Throwable e) {
            out.print("ERROR: ");
            out.println(e.getMessage());
        }
        out.flush();
    }

    /**
     * Generate output based on result nodes.
     * 
     * @param nodes the nodes
     * @param additionalProperties the additional properties
     * @return the string
     */
    protected String generateOutput( Collection<SLNode> nodes,
                                     Collection<String> additionalProperties ) {
        StringBuilder buffer = new StringBuilder();
        // Header
        StringBuilderUtil.append(buffer, StringUtils.repeat("-", ((3 + additionalProperties.size()) * (COLUMN_SIZE + 3)) + 1),
                                 "\n");
        StringBuilderUtil.append(buffer, "|", StringUtils.center("type name", COLUMN_SIZE + 2), "|");
        StringBuilderUtil.append(buffer, StringUtils.center("name", COLUMN_SIZE + 2), "|");
        StringBuilderUtil.append(buffer, StringUtils.center("parent name", COLUMN_SIZE + 2), "|");
        for (String property : additionalProperties) {
            StringBuilderUtil.append(buffer, StringUtils.center(property, COLUMN_SIZE + 2), "|");
        }
        StringBuilderUtil.append(buffer, "\n");
        StringBuilderUtil.append(buffer, StringUtils.repeat("-", ((3 + additionalProperties.size()) * (COLUMN_SIZE + 3)) + 1),
                                 "\n");
        if (!nodes.isEmpty()) {
            for (SLNode node : nodes) {
                List<String> output = new LinkedList<String>();
                output.add("| ");
                output.add(StringUtils.rightPad(StringUtils.abbreviate(node.getTypeName(), COLUMN_SIZE), COLUMN_SIZE));
                output.add(" | ");
                output.add(StringUtils.rightPad(StringUtils.abbreviate(node.getName(), COLUMN_SIZE), COLUMN_SIZE));
                output.add(" | ");
                output.add(StringUtils.rightPad(StringUtils.abbreviate(node.getParent().getName(), COLUMN_SIZE), COLUMN_SIZE));
                output.add(" | ");
                for (String propertyName : additionalProperties) {
                    String propertyValue = "";
                    try {
                        propertyValue = node.getPropertyValueAsString(propertyName);
                    } catch (Exception e) {
                    }
                    output.add(StringUtils.rightPad(StringUtils.abbreviate(propertyValue, COLUMN_SIZE), COLUMN_SIZE));
                    output.add(" | ");
                }
                StringBuilderUtil.appendLine(buffer, output);
            }
            StringBuilderUtil.append(buffer, "\n", nodes.size(), " nodes affected.", "\n");
        } else {
            StringBuilderUtil.append(buffer, "\n", "0 nodes affected.", "\n");
        }

        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getCommand() {
        return "select";
    }

    /**
     * {@inheritDoc}
     */
    public String getAutoCompleteCommand() {
        return "select";
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return "query the graph database";
    }

    /**
     * {@inheritDoc}
     */
    public String getFileCompletionCommand() {
        return "; >";
    }

    /**
     * {@inheritDoc}
     */
    public FileCompletionMode getFileCompletionMode() {
        return FileCompletionMode.CONTAINS;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasFileCompletion() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean accept( ConsoleState state ) {
        Assertions.checkNotNull("state", state);
        if (state.getActiveCommand() != null && state.getActiveCommand() instanceof QueryCommand) {
            return true;
        } else if (validateStatement(state, "select") || validateStatement(state, "use") || validateStatement(state, "define")) {
            if (state.getInput().trim().contains(";")) {
                if (state.getInput().trim().contains("; > ")) {
                    return true;
                }
                if (state.getInput().trim().endsWith(";")) {
                    return true;
                }
                return false;
            }
            return true;
        } else if (state.getInput().trim().contains("; > ")) {
            return true;
        } else if (state.getInput().trim().endsWith(";")) {
            return true;
        }
        return false;
    }

    /**
     * Validate statement.
     * 
     * @param state the state
     * @param word the word
     * @return true, if successful
     */
    private boolean validateStatement( ConsoleState state,
                                       String word ) {
        if (state.getInput().trim().length() > word.length() && state.getInput().trim().startsWith(word + " ")) {
            return true;
        } else if (state.getInput().trim().length() == word.length() && state.getInput().trim().equals(word)) {
            return true;
        }
        return false;
    }
}
