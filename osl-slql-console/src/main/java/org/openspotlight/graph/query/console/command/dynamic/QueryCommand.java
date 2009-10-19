package org.openspotlight.graph.query.console.command.dynamic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jline.ConsoleReader;

import org.apache.commons.lang.StringUtils;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryResult;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.graph.query.console.ConsoleState;
import org.openspotlight.graph.query.console.command.DynamicCommand;

public class QueryCommand implements DynamicCommand {

    private static int COLUMN_SIZE = 36;

    public void execute( ConsoleReader reader,
                         PrintWriter out,
                         ConsoleState state ) {
        if (state.getInput().endsWith(";") || state.getInput().contains("; > ")) {
            if (state.getActiveCommand() != null){
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
            //execute query here
            executeQuery(reader, out, state, lastQuery, outputFileName);
            state.setLastQuery(lastQuery);
            state.clearBuffer();
            state.setActiveCommand(null);
        } else {
            state.setActiveCommand(this);
            state.appendLineBuffer(state.getInput());
        }
    }

    protected void executeQuery( ConsoleReader reader,
                                 PrintWriter out,
                                 ConsoleState state,
                                 String query,
                                 String outputFileName ) {
        try {
            SLQueryText slqlText = state.getSession().createQueryText(query);
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
        } catch (SLGraphSessionException e) {
            out.print("ERROR: ");
            out.println(e.getMessage());
        } catch (SLInvalidQuerySyntaxException e) {
            out.print("ERROR: ");
            out.println(e.getMessage());
        } catch (IOException e) {
            out.print("ERROR: ");
            out.println(e.getMessage());
        }
        out.flush();
    }

    protected String generateOutput( Collection<SLNode> nodes,
                                     Collection<String> additionalProperties ) throws SLGraphSessionException {
        StringBuilder buffer = new StringBuilder();
        //Header
        StringBuilderUtil.append(buffer, StringUtils.repeat("-", ((3 + additionalProperties.size()) * (COLUMN_SIZE + 3)) + 1), "\n");
        StringBuilderUtil.append(buffer, "|", StringUtils.center("type name", COLUMN_SIZE + 2), "|");
        StringBuilderUtil.append(buffer, StringUtils.center("name", COLUMN_SIZE + 2), "|");
        StringBuilderUtil.append(buffer, StringUtils.center("parent name", COLUMN_SIZE + 2), "|");
        for (String property : additionalProperties) {
            StringBuilderUtil.append(buffer, StringUtils.center(property, COLUMN_SIZE + 2), "|");
        }
        StringBuilderUtil.append(buffer, "\n");
        StringBuilderUtil.append(buffer, StringUtils.repeat("-", ((3 + additionalProperties.size()) * (COLUMN_SIZE + 3)) + 1), "\n");
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

    public String getCommand() {
        return "select";
    }

    public String getAutoCompleteCommand() {
        return "select";
    }

    public String getDescription() {
        return "query the graph database";
    }

    public String getFileCompletionCommand() {
        return "; >";
    }

    public FileCompletionMode getFileCompletionMode() {
        return FileCompletionMode.CONTAINS;
    }

    public boolean hasFileCompletion() {
        return true;
    }

    public boolean accept( ConsoleState state ) {
        if (state.getActiveCommand() != null && state.getActiveCommand() instanceof QueryCommand) {
            return true;
        } else if (state.getInput().startsWith("select") || state.getInput().startsWith("use") || state.getInput().startsWith("define")) {
            return true;
        }
        return false;
    }
}
