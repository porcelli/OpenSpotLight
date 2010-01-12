package org.openspotlight.bundle.common.metrics;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.openspotlight.graph.SLGraphSession;

public class MetricsAggregator {

	private boolean methodsAreBlocks = false;
	// <SLNode#id, Count>
	private final Map<String, Integer> conditionalNesting;

	// <SLNode#id, Sum>
	private final Map<String, Integer> maxConditionalNesting;

	// <SLNode#id, Count>
	private final Map<String, Integer> depthLooping;

	// <SLNode#id, Sum>
	private final Map<String, Integer> maxDepthLooping;

	// <SLNode#id, Count>
	private final Map<String, Integer> returnPoint;

	// <SLNode#id, Count>
	private final Map<String, Integer> parameters;

	// <SLNode#id, Count>
	private final Map<String, Integer> cyclomatic1;

	// <SLNode#id, Count>
	private final Map<String, Integer> cyclomatic2;

	// <SLNode#id, Count>
	private final Map<String, Integer> cyclomatic3;

	// <SLNode#id, Count>
	private final Map<String, Integer> cyclomatic4;

	// <SLNode#id, Count>
	private final Map<String, Integer> parentCyclomatic1;

	// <SLNode#id, Count>
	private final Map<String, Integer> parentCyclomatic2;

	// <SLNode#id, Count>
	private final Map<String, Integer> parentCyclomatic3;

	// <SLNode#id, Count>
	private final Map<String, Integer> parentCyclomatic4;

	// <SLNode#id, Count>
	private final Map<String, Integer> declarativeStatement;

	// <SLNode#id, Count>
	private final Map<String, Integer> executableStatement;

	// <SLNode#id, Count>
	private final Map<String, Integer> controlStatement;

	// <SLNode#id, Count>
	private final Map<String, Integer> privateMethod;

	// <SLNode#id, Count>
	private final Map<String, Integer> nonPrivateMethod;

	// <SLNode#id, Count>
	private final Map<String, Integer> privateVariable;

	// <SLNode#id, Count>
	private final Map<String, Integer> nonPrivateVariable;

	// <SLNode#id, Count>
	private final Map<String, Integer> event;

	// <SLNode#id, Count>
	private final Map<String, Integer> constructor;

	// <SLNode#id, Count>
	private final Map<String, Integer> implementedInterfaces;

	// <SLNode#id, Count>
	private final Map<String, Integer> extendedClasses;

	// <SLNode#id, Count>
	private final Map<String, Integer> extendedInterfaces;

	// <SourcePath, SourceLineInfo>
	private final SourceLineInfoBuilder sourceLineInfo;

	// <SLNode#id, CodeArea>
	private final Map<String, Integer> codeAreas;

	private int functionPointPerLogicalLines = -1;

	// <SLNode#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> interfaceComplexity;

	// <SLNode#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> statementCount;

	// <SLNode#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> methodsCount;

	// <SLNode#id, Count>
	// Generated on buildPropertyValues
	private Map<String, Integer> variablesCount;

	public MetricsAggregator(final boolean useBlocksInstedOfMethods,
			final SourceLineInfoBuilder sourceLineInfo,
			final int functionPointPerLogicalLines) {
		this(sourceLineInfo, functionPointPerLogicalLines);
		methodsAreBlocks = useBlocksInstedOfMethods;
	}

	public MetricsAggregator(final SourceLineInfoBuilder sourceLineInfo,
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

	public void addConditionalNesting(final String nodeId) {
		generalAcumulate(conditionalNesting, nodeId);
		final Integer contConditionalNesting = conditionalNesting.get(nodeId);
		Integer maxConditional = maxConditionalNesting.get(nodeId);
		if (maxConditional == null) {
			maxConditionalNesting.put(nodeId, contConditionalNesting);
			maxConditional = 1;
		} else {
			if (contConditionalNesting > maxConditional) {
				maxConditionalNesting.put(nodeId, contConditionalNesting);
			}
		}
	}

	public void addConstructor(final String nodeId) {
		generalAcumulate(constructor, nodeId);
	}

	public void addControlStatement(final String nodeId) {
		addControlStatement(nodeId, 1);
	}

	public void addControlStatement(final String nodeId, final int size) {
		generalAcumulate(controlStatement, nodeId, size);
	}

	public void addCyclomatic1(final String nodeId) {
		generalAcumulate(cyclomatic1, nodeId);
	}

	public void addCyclomatic2(final String nodeId) {
		generalAcumulate(cyclomatic2, nodeId);
	}

	public void addCyclomatic3(final String nodeId) {
		generalAcumulate(cyclomatic3, nodeId);
	}

	public void addCyclomatic4(final String nodeId) {
		generalAcumulate(cyclomatic4, nodeId);
	}

	public void addDeclarativeStatement(final String nodeId) {
		addDeclarativeStatement(nodeId, 1);
	}

	public void addDeclarativeStatement(final String nodeId, final int qty) {
		generalAcumulate(declarativeStatement, nodeId, qty);
	}

	public void addDepthLooping(final String nodeId) {
		generalAcumulate(depthLooping, nodeId);
		final Integer contDepthLooping = depthLooping.get(nodeId);
		Integer maxDepth = maxDepthLooping.get(nodeId);
		if (maxDepth == null) {
			maxDepthLooping.put(nodeId, contDepthLooping);
			maxDepth = 1;
		} else {
			if (contDepthLooping > maxDepth) {
				maxDepthLooping.put(nodeId, contDepthLooping);
			}
		}
	}

	public void addEvents(final String nodeId) {
		generalAcumulate(event, nodeId);
	}

	public void addExecutableStatement(final String nodeId) {
		addExecutableStatement(nodeId, 1);
	}

	public void addExecutableStatement(final String nodeId, final int size) {
		generalAcumulate(executableStatement, nodeId, size);
	}

	public void addExtendedClass(final String nodeId) {
		generalAcumulate(extendedClasses, nodeId);
	}

	public void addExtendedInterface(final String nodeId) {
		generalAcumulate(extendedInterfaces, nodeId);
	}

	public void addImplementedInterface(final String nodeId) {
		generalAcumulate(implementedInterfaces, nodeId);
	}

	public void addNonPrivateMethod(final String nodeId) {
		generalAcumulate(nonPrivateMethod, nodeId);
	}

	public void addNonPrivateVariable(final String nodeId) {
		generalAcumulate(nonPrivateVariable, nodeId);
	}

	public void addParameter(final String nodeId) {
		generalAcumulate(parameters, nodeId);
	}

	public void addParentCyclomatic1(final String nodeId) {
		generalAcumulate(parentCyclomatic1, nodeId);
	}

	public void addParentCyclomatic2(final String nodeId) {
		generalAcumulate(parentCyclomatic2, nodeId);
	}

	public void addParentCyclomatic3(final String nodeId) {
		generalAcumulate(parentCyclomatic3, nodeId);
	}

	public void addParentCyclomatic4(final String nodeId) {
		generalAcumulate(parentCyclomatic4, nodeId);
	}

	public void addPrivateMethod(final String nodeId) {
		generalAcumulate(privateMethod, nodeId);
	}

	public void addPrivateVariable(final String nodeId) {
		generalAcumulate(privateVariable, nodeId);
	}

	public void addReturnPoint(final String nodeId) {
		generalAcumulate(returnPoint, nodeId);
	}

	public void buildPropertyValues(final SLGraphSession session) {
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
						.doubleValue()
						/ functionPointPerLogicalLines;
				setPropertyOnNode(session, activeCodeArea.getKey(),
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

	private Map<String, Double> divMaps(final SLGraphSession session,
			final Map<String, Integer> mainMap,
			final Map<String, Integer> secondMap, final String propertyName) {
		final Map<String, Double> resultMap = divMaps(mainMap, secondMap);
		setDoubleProperties(session, propertyName, resultMap);

		return resultMap;
	}

	private void generalAcumulate(final Map<String, Integer> map2Accumulate,
			final String nodeId) {
		generalAcumulate(map2Accumulate, nodeId, 1);
	}

	private void generalAcumulate(final Map<String, Integer> map2Accumulate,
			final String nodeId, final int size) {
		final Integer count = map2Accumulate.get(nodeId);
		if (count == null) {
			map2Accumulate.put(nodeId, size);
		} else {
			map2Accumulate.put(nodeId, count + size);
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

	public SourceLineInfoBuilder getSourceLineInfo() {
		return sourceLineInfo;
	}

	public Map<String, Integer> getStatementCount() {
		return statementCount;
	}

	public boolean isMethodsAreBlocks() {
		return methodsAreBlocks;
	}

	private void setDecisionDensity(final SLGraphSession session,
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
					setPropertyOnNode(session, activeCyclomatic.getKey(),
							propertyName, decisionDensity);
				}
			}
		}
	}

	private void setDoubleProperties(final SLGraphSession session,
			final String propertyName, final Map<String, Double> counter) {
		for (final Entry<String, Double> activeElement : counter.entrySet()) {
			setPropertyOnNode(session, activeElement.getKey(), propertyName,
					activeElement.getValue());
		}
	}

	public void setLineInfo(final int codeArea, final String nodeId) {
		codeAreas.put(nodeId, codeArea);
	}

	private void setLineMetrics(final SLGraphSession session,
			final String nodeId, final CompleteSourceLineInfo lineInfo) {
		setPropertyOnNode(session, nodeId, SystemMetaModel.propPhysicalLines,
				lineInfo.getPhysicalLines());
		setPropertyOnNode(session, nodeId, SystemMetaModel.propEfectiveLines,
				lineInfo.getEfectiveLines());
		setPropertyOnNode(session, nodeId, SystemMetaModel.propLogicalLines,
				lineInfo.getLogicalLines());
		setPropertyOnNode(session, nodeId,
				SystemMetaModel.propLogicalLinesOfCode, lineInfo
						.getLogicalLinesOfCode());
		setPropertyOnNode(session, nodeId,
				SystemMetaModel.propLogicalLinesOfWhitespace, lineInfo
						.getLogicalLinesOfWhitespace());
		setPropertyOnNode(session, nodeId, SystemMetaModel.propCommentedLines,
				lineInfo.getCommentedLines());
		setPropertyOnNode(session, nodeId,
				SystemMetaModel.propFullCommentLines, lineInfo
						.getFullCommentLines());
		setPropertyOnNode(session, nodeId,
				SystemMetaModel.propMeaningfulCommentLines, lineInfo
						.getMeaningfulCommentLines());
		setPropertyOnNode(session, nodeId, SystemMetaModel.propCommentDensity,
				lineInfo.getCommentDensity());
		setPropertyOnNode(session, nodeId, SystemMetaModel.propCodePercentage,
				lineInfo.getCodePercentage());
		setPropertyOnNode(session, nodeId,
				SystemMetaModel.propWhitespacePercentage, lineInfo
						.getWhitespacePercentage());
	}

	private void setProperties(final SLGraphSession session,
			final String propertyName, final Map<String, Integer> counter) {
		for (final Entry<String, Integer> activeElement : counter.entrySet()) {
			setPropertyOnNode(session, activeElement.getKey(), propertyName,
					activeElement.getValue());
		}
	}

	private void setPropertyOnNode(final SLGraphSession session,
			final String key, final String propertyName, final Double string) {
		// TODO Auto-generated method stub

	}

	private void setPropertyOnNode(final SLGraphSession session,
			final String key, final String propertyName, final Integer string) {
		// TODO Auto-generated method stub

	}

	private void setTotalCyclomaticComplexity(final SLGraphSession session,
			final Map<String, Integer> parentCyclomatic,
			final Map<String, Integer> totalMethods, final String propertyName) {
		for (final Entry<String, Integer> activeMethodCount : totalMethods
				.entrySet()) {
			Integer cyclomaticCount = parentCyclomatic.get(activeMethodCount
					.getKey());
			if (cyclomaticCount != null) {
				cyclomaticCount = cyclomaticCount
						- activeMethodCount.getValue() + 1;
				setPropertyOnNode(session, activeMethodCount.getKey(),
						propertyName, cyclomaticCount);
			}
		}
	}

	public void subtractConditionalNesting(final String nodeId) {
		Integer counter = conditionalNesting.get(nodeId);
		conditionalNesting.put(nodeId, --counter);
	}

	public void subtractDepthLooping(final String nodeId) {
		Integer counter = depthLooping.get(nodeId);
		depthLooping.put(nodeId, --counter);
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

	private Map<String, Integer> sumMaps(final SLGraphSession session,
			final Map<String, Integer> mainMap,
			final Map<String, Integer> secondMap, final String propertyName) {
		final Map<String, Integer> resultMap = sumMaps(mainMap, secondMap);
		setProperties(session, propertyName, resultMap);

		return resultMap;
	}
}
