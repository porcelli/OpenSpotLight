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
package org.openspotlight.bundle.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.bundle.common.metrics.CompleteSourceLineInfo;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.metrics.SystemMetaModel;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.manipulation.GraphWriter;

public class MetricsAggregator {

	private final Map<String, Node> currentNodes = new HashMap<String, Node>();

	private boolean methodsAreBlocks = false;
	// <Node#id, Count>
	private final Map<String, Integer> conditionalNesting;

	// <Node#id, Sum>
	private final Map<String, Integer> maxConditionalNesting;

	// <Node#id, Count>
	private final Map<String, Integer> depthLooping;

	// <Node#id, Sum>
	private final Map<String, Integer> maxDepthLooping;

	// <Node#id, Count>
	private final Map<String, Integer> returnPoint;

	// <Node#id, Count>
	private final Map<String, Integer> parameters;

	// <Node#id, Count>
	private final Map<String, Integer> cyclomatic1;

	// <Node#id, Count>
	private final Map<String, Integer> cyclomatic2;

	// <Node#id, Count>
	private final Map<String, Integer> cyclomatic3;

	// <Node#id, Count>
	private final Map<String, Integer> cyclomatic4;

	// <Node#id, Count>
	private final Map<String, Integer> parentCyclomatic1;

	// <Node#id, Count>
	private final Map<String, Integer> parentCyclomatic2;

	// <Node#id, Count>
	private final Map<String, Integer> parentCyclomatic3;

	// <Node#id, Count>
	private final Map<String, Integer> parentCyclomatic4;

	// <Node#id, Count>
	private final Map<String, Integer> declarativeStatement;

	// <Node#id, Count>
	private final Map<String, Integer> executableStatement;

	// <Node#id, Count>
	private final Map<String, Integer> controlStatement;

	// <Node#id, Count>
	private final Map<String, Integer> privateMethod;

	// <Node#id, Count>
	private final Map<String, Integer> nonPrivateMethod;

	// <Node#id, Count>
	private final Map<String, Integer> privateVariable;

	// <Node#id, Count>
	private final Map<String, Integer> nonPrivateVariable;

	// <Node#id, Count>
	private final Map<String, Integer> event;

	// <Node#id, Count>
	private final Map<String, Integer> constructor;

	// <Node#id, Count>
	private final Map<String, Integer> implementedInterfaces;

	// <Node#id, Count>
	private final Map<String, Integer> extendedClasses;

	// <Node#id, Count>
	private final Map<String, Integer> extendedInterfaces;

	// <SourcePath, SourceLineInfo>
	private final SourceLineInfoAggregator sourceLineInfo;

	// <Node#id, CodeArea>
	private final Map<String, Integer> codeAreas;

	private int functionPointPerLogicalLines = -1;

	// <Node#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> interfaceComplexity;

	// <Node#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> statementCount;

	// <Node#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> methodsCount;

	// <Node#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> variablesCount;

	public MetricsAggregator(final boolean useBlocksInstedOfMethods,
			final SourceLineInfoAggregator sourceLineInfo,
			final int functionPointPerLogicalLines) {
		this(sourceLineInfo, functionPointPerLogicalLines);
		methodsAreBlocks = useBlocksInstedOfMethods;
	}

	public MetricsAggregator(final SourceLineInfoAggregator sourceLineInfo,
			final int functionPointPerLogicalLines) {
		this.sourceLineInfo = sourceLineInfo;
		conditionalNesting = new TreeMap<String, Integer>();
		maxConditionalNesting = new TreeMap<String, Integer>();
		depthLooping = new TreeMap<String, Integer>();
		maxDepthLooping = new TreeMap<String, Integer>();
		returnPoint = new TreeMap<String, Integer>();
		parameters = new TreeMap<String, Integer>();
		cyclomatic1 = new TreeMap<String, Integer>();
		cyclomatic2 = new TreeMap<String, Integer>();
		cyclomatic3 = new TreeMap<String, Integer>();
		cyclomatic4 = new TreeMap<String, Integer>();
		parentCyclomatic1 = new TreeMap<String, Integer>();
		parentCyclomatic2 = new TreeMap<String, Integer>();
		parentCyclomatic3 = new TreeMap<String, Integer>();
		parentCyclomatic4 = new TreeMap<String, Integer>();
		declarativeStatement = new TreeMap<String, Integer>();
		executableStatement = new TreeMap<String, Integer>();
		controlStatement = new TreeMap<String, Integer>();
		privateMethod = new TreeMap<String, Integer>();
		nonPrivateMethod = new TreeMap<String, Integer>();
		privateVariable = new TreeMap<String, Integer>();
		nonPrivateVariable = new TreeMap<String, Integer>();
		event = new TreeMap<String, Integer>();
		constructor = new TreeMap<String, Integer>();
		implementedInterfaces = new TreeMap<String, Integer>();
		extendedClasses = new TreeMap<String, Integer>();
		extendedInterfaces = new TreeMap<String, Integer>();
		codeAreas = new TreeMap<String, Integer>();
		this.functionPointPerLogicalLines = functionPointPerLogicalLines;
	}

	public void addConditionalNesting(final Node node) {
		refreshNodeInformation(node);

		generalAcumulate(conditionalNesting, node);
		final Integer contConditionalNesting = conditionalNesting.get(node
				.getId());
		Integer maxConditional = maxConditionalNesting.get(node.getId());
		if (maxConditional == null) {
			maxConditionalNesting.put(node.getId(), contConditionalNesting);
			maxConditional = 1;
		} else {
			if (contConditionalNesting > maxConditional) {
				maxConditionalNesting.put(node.getId(), contConditionalNesting);
			}
		}
	}

	public void addConstructor(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(constructor, node);
	}

	public void addControlStatement(final Node node) {
		refreshNodeInformation(node);
		addControlStatement(node, 1);
	}

	public void addControlStatement(final Node node, final int size) {
		refreshNodeInformation(node);
		generalAcumulate(controlStatement, node, size);
	}

	public void addCyclomatic1(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(cyclomatic1, node);
	}

	public void addCyclomatic2(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(cyclomatic2, node);
	}

	public void addCyclomatic3(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(cyclomatic3, node);
	}

	public void addCyclomatic4(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(cyclomatic4, node);
	}

	public void addDeclarativeStatement(final Node node) {
		refreshNodeInformation(node);
		addDeclarativeStatement(node, 1);
	}

	public void addDeclarativeStatement(final Node node, final int qty) {
		refreshNodeInformation(node);
		generalAcumulate(declarativeStatement, node, qty);
	}

	public void addDepthLooping(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(depthLooping, node);
		final Integer contDepthLooping = depthLooping.get(node.getId());
		Integer maxDepth = maxDepthLooping.get(node.getId());
		if (maxDepth == null) {
			maxDepthLooping.put(node.getId(), contDepthLooping);
			maxDepth = 1;
		} else {
			if (contDepthLooping > maxDepth) {
				maxDepthLooping.put(node.getId(), contDepthLooping);
			}
		}
	}

	public void addEvents(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(event, node);
	}

	public void addExecutableStatement(final Node node) {
		refreshNodeInformation(node);
		addExecutableStatement(node, 1);
	}

	public void addExecutableStatement(final Node node, final int size) {
		refreshNodeInformation(node);
		generalAcumulate(executableStatement, node, size);
	}

	public void addExtendedClass(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(extendedClasses, node);
	}

	public void addExtendedInterface(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(extendedInterfaces, node);
	}

	public void addImplementedInterface(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(implementedInterfaces, node);
	}

	public void addNonPrivateMethod(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(nonPrivateMethod, node);
	}

	public void addNonPrivateVariable(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(nonPrivateVariable, node);
	}

	public void addParameter(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(parameters, node);
	}

	public void addParentCyclomatic1(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(parentCyclomatic1, node);
	}

	public void addParentCyclomatic2(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(parentCyclomatic2, node);
	}

	public void addParentCyclomatic3(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(parentCyclomatic3, node);
	}

	public void addParentCyclomatic4(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(parentCyclomatic4, node);
	}

	public void addPrivateMethod(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(privateMethod, node);
	}

	public void addPrivateVariable(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(privateVariable, node);
	}

	public void addReturnPoint(final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(returnPoint, node);
	}

	public void buildPropertyValues(final GraphWriter session) {
		setProperties(session, SystemMetaModel.propMaxConditionalNesting,
				maxConditionalNesting);
		setProperties(session, SystemMetaModel.propMaxLoopingDepth,
				maxDepthLooping);
		setProperties(session, SystemMetaModel.propParameterMetric, parameters);
		setProperties(session, SystemMetaModel.propReturnPointMetric,
				returnPoint);
		interfaceComplexity = sumMaps(session, parameters, returnPoint,
				SystemMetaModel.propInterfaceComplexity);
		setProperties(session, SystemMetaModel.propDeclarativeStatementCount,
				declarativeStatement);
		setProperties(session, SystemMetaModel.propExecutableStatementCount,
				executableStatement);
		setProperties(session, SystemMetaModel.propControlStatementCount,
				controlStatement);
		final Map<String, Integer> nonControlStatementsCount = sumMaps(session,
				executableStatement, declarativeStatement,
				SystemMetaModel.propNonControlStatementCount);
		statementCount = sumMaps(session, nonControlStatementsCount,
				controlStatement, SystemMetaModel.propStatementCount);

		divMaps(session, executableStatement, statementCount,
				SystemMetaModel.propExecutability);
		divMaps(session, controlStatement, statementCount,
				SystemMetaModel.propControlDensity);

		if (!methodsAreBlocks) {
			setProperties(session, SystemMetaModel.propPrivateMethodsCount,
					privateMethod);
			setProperties(session, SystemMetaModel.propNonPrivateMethodsCount,
					nonPrivateMethod);
			methodsCount = sumMaps(session, privateMethod, nonPrivateMethod,
					SystemMetaModel.propMethodsCount);
		} else {
			methodsCount = sumMaps(session, privateMethod, nonPrivateMethod,
					SystemMetaModel.propBlocksCount);
		}

		setProperties(session, SystemMetaModel.propPrivateVariablesCount,
				privateVariable);
		setProperties(session, SystemMetaModel.propNonPrivateVariablesCount,
				nonPrivateVariable);
		variablesCount = sumMaps(session, privateVariable, nonPrivateVariable,
				SystemMetaModel.propVariablesCount);

		setProperties(session, SystemMetaModel.propConstructorsCount,
				constructor);
		setProperties(session, SystemMetaModel.propEventsCount, event);
		setProperties(session, SystemMetaModel.propImplementedInterfacesCount,
				implementedInterfaces);

		sumMaps(session, methodsCount, variablesCount,
				SystemMetaModel.propComponentSize);
		sumMaps(session, nonPrivateMethod, nonPrivateVariable,
				SystemMetaModel.propComponentInterfaceSize);

		setProperties(session, SystemMetaModel.propCyclomaticComplexity1,
				cyclomatic1);
		setProperties(session, SystemMetaModel.propCyclomaticComplexity2,
				cyclomatic2);
		setProperties(session, SystemMetaModel.propCyclomaticComplexity3,
				cyclomatic3);
		setProperties(session, SystemMetaModel.propCyclomaticComplexity4,
				cyclomatic4);
		setTotalCyclomaticComplexity(session, parentCyclomatic1, methodsCount,
				SystemMetaModel.propTotalCyclomaticComplexity1);
		setTotalCyclomaticComplexity(session, parentCyclomatic2, methodsCount,
				SystemMetaModel.propTotalCyclomaticComplexity2);
		setTotalCyclomaticComplexity(session, parentCyclomatic3, methodsCount,
				SystemMetaModel.propTotalCyclomaticComplexity3);
		setTotalCyclomaticComplexity(session, parentCyclomatic4, methodsCount,
				SystemMetaModel.propTotalCyclomaticComplexity4);
		sumMaps(session, cyclomatic1, interfaceComplexity,
				SystemMetaModel.propFunctionalComplexity1);
		sumMaps(session, cyclomatic2, interfaceComplexity,
				SystemMetaModel.propFunctionalComplexity2);
		sumMaps(session, cyclomatic3, interfaceComplexity,
				SystemMetaModel.propFunctionalComplexity3);
		sumMaps(session, cyclomatic4, interfaceComplexity,
				SystemMetaModel.propFunctionalComplexity4);

		setDecisionDensity(session, cyclomatic1,
				SystemMetaModel.propDecisionDensity1);
		setDecisionDensity(session, cyclomatic2,
				SystemMetaModel.propDecisionDensity2);
		setDecisionDensity(session, cyclomatic3,
				SystemMetaModel.propDecisionDensity3);
		setDecisionDensity(session, cyclomatic4,
				SystemMetaModel.propDecisionDensity4);

		for (final Entry<String, Integer> activeCodeArea : codeAreas.entrySet()) {
			final CompleteSourceLineInfo info = sourceLineInfo
					.getCodeArea(activeCodeArea.getValue());
			if (activeCodeArea.getValue() == 0 && info != null) {
				final double functionPoint = info.getLogicalLinesOfCode()
						.doubleValue() / functionPointPerLogicalLines;
				setPropertyOnNode(activeCodeArea.getKey(),
						SystemMetaModel.propFunctionPointLOCBased,
						functionPoint);
			}
			if (info != null) {
				setLineMetrics(session, activeCodeArea.getKey(), info);
			}
		}
	}

	private Map<String, Double> divMaps(final Map<String, Integer> mainMap,
			final Map<String, Integer> secondMap) {
		final Map<String, Double> resultMap = new TreeMap<String, Double>();
		for (final Entry<String, Integer> entry : mainMap.entrySet()) {
			resultMap.put(entry.getKey(), entry.getValue().doubleValue());
		}
		for (final Entry<String, Integer> activeReturn : secondMap.entrySet()) {
			final Double complexityCount = (Double) resultMap.get(activeReturn
					.getKey());
			if (complexityCount == null) {
				resultMap.put(activeReturn.getKey(), activeReturn.getValue()
						.doubleValue());
			} else {
				final Double result = complexityCount.doubleValue()
						/ activeReturn.getValue().doubleValue() * 100;
				resultMap.put(activeReturn.getKey(), result);
			}
		}

		return resultMap;
	}

	private Map<String, Double> divMaps(final GraphWriter session,
			final Map<String, Integer> mainMap,
			final Map<String, Integer> secondMap, final String propertyName) {
		final Map<String, Double> resultMap = divMaps(mainMap, secondMap);
		setDoubleProperties(session, propertyName, resultMap);

		return resultMap;
	}

	private void generalAcumulate(final Map<String, Integer> map2Accumulate,
			final Node node) {
		refreshNodeInformation(node);
		generalAcumulate(map2Accumulate, node, 1);
	}

	private void generalAcumulate(final Map<String, Integer> map2Accumulate,
			final Node node, final int size) {
		refreshNodeInformation(node);
		final Integer count = map2Accumulate.get(node.getId());
		if (count == null) {
			map2Accumulate.put(node.getId(), size);
		} else {
			map2Accumulate.put(node.getId(), count + size);
		}
	}

	public Map<String, Integer> getCodeAreas() {
		return codeAreas;
	}

	public Map<String, Integer> getConditionalNesting() {
		return conditionalNesting;
	}

	public Map<String, Integer> getConstructor() {
		return constructor;
	}

	public Map<String, Integer> getControlStatement() {
		return controlStatement;
	}

	public Map<String, Integer> getCyclomatic1() {
		return cyclomatic1;
	}

	public Map<String, Integer> getCyclomatic2() {
		return cyclomatic2;
	}

	public Map<String, Integer> getCyclomatic3() {
		return cyclomatic3;
	}

	public Map<String, Integer> getCyclomatic4() {
		return cyclomatic4;
	}

	public Map<String, Integer> getDeclarativeStatement() {
		return declarativeStatement;
	}

	public Map<String, Integer> getDepthLooping() {
		return depthLooping;
	}

	public Map<String, Integer> getEvent() {
		return event;
	}

	public Map<String, Integer> getExecutableStatement() {
		return executableStatement;
	}

	public Map<String, Integer> getExtendedClasses() {
		return extendedClasses;
	}

	public Map<String, Integer> getExtendedInterfaces() {
		return extendedInterfaces;
	}

	public int getFunctionPointPerLogicalLines() {
		return functionPointPerLogicalLines;
	}

	public Map<String, Integer> getImplementedInterfaces() {
		return implementedInterfaces;
	}

	public Map<String, Integer> getInterfaceComplexity() {
		return interfaceComplexity;
	}

	public Map<String, Integer> getMaxConditionalNesting() {
		return maxConditionalNesting;
	}

	public Map<String, Integer> getMaxDepthLooping() {
		return maxDepthLooping;
	}

	public Map<String, Integer> getNonPrivateMethod() {
		return nonPrivateMethod;
	}

	public Map<String, Integer> getNonPrivateVariable() {
		return nonPrivateVariable;
	}

	public Map<String, Integer> getParameters() {
		return parameters;
	}

	public Map<String, Integer> getParentCyclomatic1() {
		return parentCyclomatic1;
	}

	public Map<String, Integer> getParentCyclomatic2() {
		return parentCyclomatic2;
	}

	public Map<String, Integer> getParentCyclomatic3() {
		return parentCyclomatic3;
	}

	public Map<String, Integer> getParentCyclomatic4() {
		return parentCyclomatic4;
	}

	public Map<String, Integer> getPrivateMethod() {
		return privateMethod;
	}

	public Map<String, Integer> getPrivateVariable() {
		return privateVariable;
	}

	public Map<String, Integer> getReturnPoint() {
		return returnPoint;
	}

	public SourceLineInfoAggregator getSourceLineInfo() {
		return sourceLineInfo;
	}

	public Map<String, Integer> getStatementCount() {
		return statementCount;
	}

	public boolean isMethodsAreBlocks() {
		return methodsAreBlocks;
	}

	private void refreshNodeInformation(final Node node) {
		currentNodes.put(node.getId(), node);
	}

	private void setDecisionDensity(final GraphWriter session,
			final Map<String, Integer> cyclomatic, final String propertyName) {
		for (final Entry<String, Integer> activeCyclomatic : cyclomatic
				.entrySet()) {
			final Integer activeCodeArea = codeAreas.get(activeCyclomatic
					.getKey());
			if (activeCodeArea != null) {
				final CompleteSourceLineInfo info = sourceLineInfo
						.getCodeArea(activeCodeArea);
				if (info != null) {
					final double decisionDensity = activeCyclomatic.getValue()
							.doubleValue()
							/ info.getLogicalLinesOfCode().doubleValue();
					setPropertyOnNode(activeCyclomatic.getKey(), propertyName,
							decisionDensity);
				}
			}
		}
	}

	private void setDoubleProperties(final GraphWriter session,
			final String propertyName, final Map<String, Double> counter) {
		for (final Entry<String, Double> activeElement : counter.entrySet()) {
			setPropertyOnNode(activeElement.getKey(), propertyName,
					activeElement.getValue());
		}
	}

	public void setLineInfo(final int codeArea, final Node node) {
		codeAreas.put(node.getId(), codeArea);
	}

	private void setLineMetrics(final GraphWriter session, final String nodeId,
			final CompleteSourceLineInfo lineInfo) {
		setPropertyOnNode(nodeId, SystemMetaModel.propPhysicalLines,
				lineInfo.getPhysicalLines());
		setPropertyOnNode(nodeId, SystemMetaModel.propEfectiveLines,
				lineInfo.getEfectiveLines());
		setPropertyOnNode(nodeId, SystemMetaModel.propLogicalLines,
				lineInfo.getLogicalLines());
		setPropertyOnNode(nodeId, SystemMetaModel.propLogicalLinesOfCode,
				lineInfo.getLogicalLinesOfCode());
		setPropertyOnNode(nodeId, SystemMetaModel.propLogicalLinesOfWhitespace,
				lineInfo.getLogicalLinesOfWhitespace());
		setPropertyOnNode(nodeId, SystemMetaModel.propCommentedLines,
				lineInfo.getCommentedLines());
		setPropertyOnNode(nodeId, SystemMetaModel.propFullCommentLines,
				lineInfo.getFullCommentLines());
		setPropertyOnNode(nodeId, SystemMetaModel.propMeaningfulCommentLines,
				lineInfo.getMeaningfulCommentLines());
		setPropertyOnNode(nodeId, SystemMetaModel.propCommentDensity,
				lineInfo.getCommentDensity());
		setPropertyOnNode(nodeId, SystemMetaModel.propCodePercentage,
				lineInfo.getCodePercentage());
		setPropertyOnNode(nodeId, SystemMetaModel.propWhitespacePercentage,
				lineInfo.getWhitespacePercentage());
	}

	private void setProperties(final GraphWriter session,
			final String propertyName, final Map<String, Integer> counter) {
		for (final Entry<String, Integer> activeElement : counter.entrySet()) {
			setPropertyOnNode(activeElement.getKey(), propertyName,
					activeElement.getValue());
		}
	}

	private void setPropertyOnNode(final String key, final String propertyName,
			final Double value) {
		final Node node = currentNodes.get(key);
		try {
			PropertyUtils.getPropertyDescriptor(node, propertyName)
					.getWriteMethod().invoke(node, value);
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	private void setPropertyOnNode(final String key, final String propertyName,
			final Integer value) {
		try {
			final Node node = currentNodes.get(key);
			PropertyUtils.getPropertyDescriptor(node, propertyName)
					.getWriteMethod().invoke(node, value);
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	private void setTotalCyclomaticComplexity(final GraphWriter session,
			final Map<String, Integer> parentCyclomatic,
			final Map<String, Integer> totalMethods, final String propertyName) {
		for (final Entry<String, Integer> activeMethodCount : totalMethods
				.entrySet()) {
			Integer cyclomaticCount = parentCyclomatic.get(activeMethodCount
					.getKey());
			if (cyclomaticCount != null) {
				cyclomaticCount = cyclomaticCount
						- activeMethodCount.getValue() + 1;
				setPropertyOnNode(activeMethodCount.getKey(), propertyName,
						cyclomaticCount);
			}
		}
	}

	public void subtractConditionalNesting(final Node node) {
		refreshNodeInformation(node);
		Integer counter = conditionalNesting.get(node.getId());
		conditionalNesting.put(node.getId(), --counter);
	}

	public void subtractDepthLooping(final Node node) {
		refreshNodeInformation(node);
		Integer counter = depthLooping.get(node.getId());
		depthLooping.put(node.getId(), --counter);
	}

	private Map<String, Integer> sumMaps(final Map<String, Integer> mainMap,
			final Map<String, Integer> secondMap) {
		final Map<String, Integer> resultMap = new TreeMap<String, Integer>(
				mainMap);

		for (final Entry<String, Integer> activeReturn : secondMap.entrySet()) {
			final Integer complexityCount = resultMap
					.get(activeReturn.getKey());
			if (complexityCount == null) {
				resultMap.put(activeReturn.getKey(), activeReturn.getValue());
			} else {
				resultMap.put(activeReturn.getKey(), complexityCount
						+ activeReturn.getValue());
			}
		}

		return resultMap;
	}

	private Map<String, Integer> sumMaps(final GraphWriter session,
			final Map<String, Integer> mainMap,
			final Map<String, Integer> secondMap, final String propertyName) {
		final Map<String, Integer> resultMap = sumMaps(mainMap, secondMap);
		setProperties(session, propertyName, resultMap);

		return resultMap;
	}
}
