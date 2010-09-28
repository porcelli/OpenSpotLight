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
 * termos da Licença Pública Geral Menor do GNU conforme privateada pela Free Software
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

import org.openspotlight.graph.Node;

public abstract class NodeWithMetrics extends Node {

    
    public int getBlocksCount() {
		return blocksCount;
	}


	public void setBlocksCount(int blocksCount) {
		this.blocksCount = blocksCount;
	}


	public int getCodePercentage() {
		return codePercentage;
	}


	public void setCodePercentage(int codePercentage) {
		this.codePercentage = codePercentage;
	}


	public int getCommentDensity() {
		return commentDensity;
	}


	public void setCommentDensity(int commentDensity) {
		this.commentDensity = commentDensity;
	}


	public int getCommentedLines() {
		return commentedLines;
	}


	public void setCommentedLines(int commentedLines) {
		this.commentedLines = commentedLines;
	}


	public int getComponentInterfaceSize() {
		return componentInterfaceSize;
	}


	public void setComponentInterfaceSize(int componentInterfaceSize) {
		this.componentInterfaceSize = componentInterfaceSize;
	}


	public int getComponentSize() {
		return componentSize;
	}


	public void setComponentSize(int componentSize) {
		this.componentSize = componentSize;
	}


	public int getConstructorsCount() {
		return constructorsCount;
	}


	public void setConstructorsCount(int constructorsCount) {
		this.constructorsCount = constructorsCount;
	}


	public int getControlDensity() {
		return controlDensity;
	}


	public void setControlDensity(int controlDensity) {
		this.controlDensity = controlDensity;
	}


	public int getControlStatementCount() {
		return controlStatementCount;
	}


	public void setControlStatementCount(int controlStatementCount) {
		this.controlStatementCount = controlStatementCount;
	}


	public int getCyclomaticComplexity1() {
		return cyclomaticComplexity1;
	}


	public void setCyclomaticComplexity1(int cyclomaticComplexity1) {
		this.cyclomaticComplexity1 = cyclomaticComplexity1;
	}


	public int getCyclomaticComplexity2() {
		return cyclomaticComplexity2;
	}


	public void setCyclomaticComplexity2(int cyclomaticComplexity2) {
		this.cyclomaticComplexity2 = cyclomaticComplexity2;
	}


	public int getCyclomaticComplexity3() {
		return cyclomaticComplexity3;
	}


	public void setCyclomaticComplexity3(int cyclomaticComplexity3) {
		this.cyclomaticComplexity3 = cyclomaticComplexity3;
	}


	public int getCyclomaticComplexity4() {
		return cyclomaticComplexity4;
	}


	public void setCyclomaticComplexity4(int cyclomaticComplexity4) {
		this.cyclomaticComplexity4 = cyclomaticComplexity4;
	}


	public int getDecisionDensity1() {
		return decisionDensity1;
	}


	public void setDecisionDensity1(int decisionDensity1) {
		this.decisionDensity1 = decisionDensity1;
	}


	public int getDecisionDensity2() {
		return decisionDensity2;
	}


	public void setDecisionDensity2(int decisionDensity2) {
		this.decisionDensity2 = decisionDensity2;
	}


	public int getDecisionDensity3() {
		return decisionDensity3;
	}


	public void setDecisionDensity3(int decisionDensity3) {
		this.decisionDensity3 = decisionDensity3;
	}


	public int getDecisionDensity4() {
		return decisionDensity4;
	}


	public void setDecisionDensity4(int decisionDensity4) {
		this.decisionDensity4 = decisionDensity4;
	}


	public int getDeclarativeStatementCount() {
		return declarativeStatementCount;
	}


	public void setDeclarativeStatementCount(int declarativeStatementCount) {
		this.declarativeStatementCount = declarativeStatementCount;
	}


	public int getEfectiveLines() {
		return efectiveLines;
	}


	public void setEfectiveLines(int efectiveLines) {
		this.efectiveLines = efectiveLines;
	}


	public int getEventsCount() {
		return eventsCount;
	}


	public void setEventsCount(int eventsCount) {
		this.eventsCount = eventsCount;
	}


	public int getExecutability() {
		return executability;
	}


	public void setExecutability(int executability) {
		this.executability = executability;
	}


	public int getExecutableStatementCount() {
		return executableStatementCount;
	}


	public void setExecutableStatementCount(int executableStatementCount) {
		this.executableStatementCount = executableStatementCount;
	}


	public int getFullCommentLines() {
		return fullCommentLines;
	}


	public void setFullCommentLines(int fullCommentLines) {
		this.fullCommentLines = fullCommentLines;
	}


	public int getFunctionalComplexity1() {
		return functionalComplexity1;
	}


	public void setFunctionalComplexity1(int functionalComplexity1) {
		this.functionalComplexity1 = functionalComplexity1;
	}


	public int getFunctionalComplexity2() {
		return functionalComplexity2;
	}


	public void setFunctionalComplexity2(int functionalComplexity2) {
		this.functionalComplexity2 = functionalComplexity2;
	}


	public int getFunctionalComplexity3() {
		return functionalComplexity3;
	}


	public void setFunctionalComplexity3(int functionalComplexity3) {
		this.functionalComplexity3 = functionalComplexity3;
	}


	public int getFunctionalComplexity4() {
		return functionalComplexity4;
	}


	public void setFunctionalComplexity4(int functionalComplexity4) {
		this.functionalComplexity4 = functionalComplexity4;
	}


	public int getFunctionPointLOCBased() {
		return functionPointLOCBased;
	}


	public void setFunctionPointLOCBased(int functionPointLOCBased) {
		this.functionPointLOCBased = functionPointLOCBased;
	}


	public int getImplementedInterfacesCount() {
		return implementedInterfacesCount;
	}


	public void setImplementedInterfacesCount(int implementedInterfacesCount) {
		this.implementedInterfacesCount = implementedInterfacesCount;
	}


	public int getInterfaceComplexity() {
		return interfaceComplexity;
	}


	public void setInterfaceComplexity(int interfaceComplexity) {
		this.interfaceComplexity = interfaceComplexity;
	}


	public int getLogicalLines() {
		return logicalLines;
	}


	public void setLogicalLines(int logicalLines) {
		this.logicalLines = logicalLines;
	}


	public int getLogicalLinesOfCode() {
		return logicalLinesOfCode;
	}


	public void setLogicalLinesOfCode(int logicalLinesOfCode) {
		this.logicalLinesOfCode = logicalLinesOfCode;
	}


	public int getLogicalLinesOfWhitespace() {
		return logicalLinesOfWhitespace;
	}


	public void setLogicalLinesOfWhitespace(int logicalLinesOfWhitespace) {
		this.logicalLinesOfWhitespace = logicalLinesOfWhitespace;
	}


	public int getMaxConditionalNesting() {
		return maxConditionalNesting;
	}


	public void setMaxConditionalNesting(int maxConditionalNesting) {
		this.maxConditionalNesting = maxConditionalNesting;
	}


	public int getMaxLoopingDepth() {
		return maxLoopingDepth;
	}


	public void setMaxLoopingDepth(int maxLoopingDepth) {
		this.maxLoopingDepth = maxLoopingDepth;
	}


	public int getMeaningfulCommentLines() {
		return meaningfulCommentLines;
	}


	public void setMeaningfulCommentLines(int meaningfulCommentLines) {
		this.meaningfulCommentLines = meaningfulCommentLines;
	}


	public int getMethodsCount() {
		return methodsCount;
	}


	public void setMethodsCount(int methodsCount) {
		this.methodsCount = methodsCount;
	}


	public int getNonControlStatementCount() {
		return nonControlStatementCount;
	}


	public void setNonControlStatementCount(int nonControlStatementCount) {
		this.nonControlStatementCount = nonControlStatementCount;
	}


	public int getNonPrivateMethodsCount() {
		return nonPrivateMethodsCount;
	}


	public void setNonPrivateMethodsCount(int nonPrivateMethodsCount) {
		this.nonPrivateMethodsCount = nonPrivateMethodsCount;
	}


	public int getNonPrivateVariablesCount() {
		return nonPrivateVariablesCount;
	}


	public void setNonPrivateVariablesCount(int nonPrivateVariablesCount) {
		this.nonPrivateVariablesCount = nonPrivateVariablesCount;
	}


	public int getParameterMetric() {
		return parameterMetric;
	}


	public void setParameterMetric(int parameterMetric) {
		this.parameterMetric = parameterMetric;
	}


	public int getPhysicalLines() {
		return physicalLines;
	}


	public void setPhysicalLines(int physicalLines) {
		this.physicalLines = physicalLines;
	}


	public int getPrivateMethodsCount() {
		return privateMethodsCount;
	}


	public void setPrivateMethodsCount(int privateMethodsCount) {
		this.privateMethodsCount = privateMethodsCount;
	}


	public int getPrivateVariablesCount() {
		return privateVariablesCount;
	}


	public void setPrivateVariablesCount(int privateVariablesCount) {
		this.privateVariablesCount = privateVariablesCount;
	}


	public int getReturnPointMetric() {
		return returnPointMetric;
	}


	public void setReturnPointMetric(int returnPointMetric) {
		this.returnPointMetric = returnPointMetric;
	}


	public int getStatementCount() {
		return statementCount;
	}


	public void setStatementCount(int statementCount) {
		this.statementCount = statementCount;
	}


	public int getTotalCyclomaticComplexity1() {
		return totalCyclomaticComplexity1;
	}


	public void setTotalCyclomaticComplexity1(int totalCyclomaticComplexity1) {
		this.totalCyclomaticComplexity1 = totalCyclomaticComplexity1;
	}


	public int getTotalCyclomaticComplexity2() {
		return totalCyclomaticComplexity2;
	}


	public void setTotalCyclomaticComplexity2(int totalCyclomaticComplexity2) {
		this.totalCyclomaticComplexity2 = totalCyclomaticComplexity2;
	}


	public int getTotalCyclomaticComplexity3() {
		return totalCyclomaticComplexity3;
	}


	public void setTotalCyclomaticComplexity3(int totalCyclomaticComplexity3) {
		this.totalCyclomaticComplexity3 = totalCyclomaticComplexity3;
	}


	public int getTotalCyclomaticComplexity4() {
		return totalCyclomaticComplexity4;
	}


	public void setTotalCyclomaticComplexity4(int totalCyclomaticComplexity4) {
		this.totalCyclomaticComplexity4 = totalCyclomaticComplexity4;
	}


	public int getVariablesCount() {
		return variablesCount;
	}


	public void setVariablesCount(int variablesCount) {
		this.variablesCount = variablesCount;
	}


	public int getWhitespacePercentage() {
		return whitespacePercentage;
	}


	public void setWhitespacePercentage(int whitespacePercentage) {
		this.whitespacePercentage = whitespacePercentage;
	}


	private int  blocksCount;

    
    private int  codePercentage;

    
    private int  commentDensity;

    
    private int  commentedLines;

    
    private int  componentInterfaceSize;

    
    private int  componentSize;

    
    private int  constructorsCount;

    
    private int  controlDensity;

    
    private int  controlStatementCount;

    
    private int  cyclomaticComplexity1;

    
    private int  cyclomaticComplexity2;

    
    private int  cyclomaticComplexity3;

    
    private int  cyclomaticComplexity4;

    
    private int  decisionDensity1;

    
    private int  decisionDensity2;

    
    private int  decisionDensity3;

    
    private int  decisionDensity4;

    
    private int  declarativeStatementCount;

    
    private int  efectiveLines;

    
    private int  eventsCount;

    
    private int  executability;

    
    private int  executableStatementCount;

    
    private int  fullCommentLines;

    
    private int  functionalComplexity1;

    
    private int  functionalComplexity2;

    
    private int  functionalComplexity3;

    
    private int  functionalComplexity4;

    
    private int  functionPointLOCBased;

    
    private int  implementedInterfacesCount;

    
    private int  interfaceComplexity;

    
    private int  logicalLines;

    
    private int  logicalLinesOfCode;

    
    private int  logicalLinesOfWhitespace;

    
    private int  maxConditionalNesting;

    
    private int  maxLoopingDepth;

    
    private int  meaningfulCommentLines;

    
    private int  methodsCount;

    
    private int  nonControlStatementCount;

    
    private int  nonPrivateMethodsCount;

    
    private int  nonPrivateVariablesCount;

    
    private int  parameterMetric;

    
    private int  physicalLines;

    
    private int  privateMethodsCount;

    
    private int  privateVariablesCount;

    
    private int  returnPointMetric;

    
    private int  statementCount;

    
    private int  totalCyclomaticComplexity1;

    
    private int  totalCyclomaticComplexity2;

    
    private int  totalCyclomaticComplexity3;

    
    private int  totalCyclomaticComplexity4;

    
    private int  variablesCount;

    
    private int  whitespacePercentage;

}
