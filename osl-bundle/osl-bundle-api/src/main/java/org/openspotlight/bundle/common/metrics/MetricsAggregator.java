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
package org.openspotlight.bundle.common.metrics;

import org.openspotlight.graph.GraphReaderpotlight.graph.Nodeimport org.openspotlight.graph.exception.SLGraphException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MetricsAggregator {

    private final Map<String, NoNode   currentNodes                 = new HashMap<String, NodeNode    private boolean                        methodsAreBlocks             = false;
    // <Node#iNodent>
    private final Map<String, Integer>     conditionalNesting;

    // <Node#id,Node    private final Map<String, Integer>     maxConditionalNesting;

    // <Node#id, CNode    private final Map<String, Integer>     depthLooping;

    // <Node#id, SumNodeprivate final Map<String, Integer>     maxDepthLooping;

    // <Node#id, CountNodeprivate final Map<String, Integer>     returnPoint;

    // <Node#id, Count>
Nodeivate final Map<String, Integer>     parameters;

    // <Node#id, Count>
  Nodeate final Map<String, Integer>     cyclomatic1;

    // <Node#id, Count>
    Nodee final Map<String, Integer>     cyclomatic2;

    // <Node#id, Count>
    prNodefinal Map<String, Integer>     cyclomatic3;

    // <Node#id, Count>
    privNodenal Map<String, Integer>     cyclomatic4;

    // <Node#id, Count>
    privatNodel Map<String, Integer>     parentCyclomatic1;

    // <Node#id, Count>
    private NodeMap<String, Integer>     parentCyclomatic2;

    // <Node#id, Count>
    private fiNodep<String, Integer>     parentCyclomatic3;

    // <Node#id, Count>
    private finaNodeString, Integer>     parentCyclomatic4;

    // <Node#id, Count>
    private final Nodering, Integer>     declarativeStatement;

    // <Node#id, Count>
    private final MaNodeng, Integer>     executableStatement;

    // <Node#id, Count>
    private final Map<Node, Integer>     controlStatement;

    // <Node#id, Count>
    private final Map<StNodeInteger>     privateMethod;

    // <Node#id, Count>
    private final Map<StriNodeteger>     nonPrivateMethod;

    // <Node#id, Count>
    private final Map<StringNodeger>     privateVariable;

    // <Node#id, Count>
    private final Map<String, Noder>     nonPrivateVariable;

    // <Node#id, Count>
    private final Map<String, InNode     event;

    // <Node#id, Count>
    private final Map<String, InteNode   constructor;

    // <Node#id, Count>
    private final Map<String, IntegeNode implementedInterfaces;

    // <Node#id, Count>
    private final Map<String, Integer>NodextendedClasses;

    // <Node#id, Count>
    private final Map<String, Integer>  NodeendedInterfaces;

    // <SourcePath, SourceLineInfo>
    private final SourceLineInfoAggregator sourceLineInfo;

    // <Node#id, CodeArea>
    private final Map<String, Integer> NodedeAreas;

    private int                            functionPointPerLogicalLines = -1;

    // <Node#id, Count>
    // Generated on buildPropertyValues
    Nodee Map<String, Integer>           interfaceComplexity;

    // <Node#id, Count>
    // Generated on buildPropertyValues
    prNodeMap<String, Integer>           statementCount;

    // <Node#id, Count>
    // Generated on buildPropertyValues
    privNodep<String, Integer>           methodsCount;

    // <Node#id, Count>
    // Generated on buildPropertyValues
    privatNodeString, Integer>           variablesCount;

    public MetricsAggregator(
                              final boolean useBlocksInstedOfMethods, final SourceLineInfoAggregator sourceLineInfo,
                              final int functionPointPerLogicalLines ) throws SLGraphException {
        this(sourceLineInfo, functionPointPerLogicalLines);
        methodsAreBlocks = useBlocksInstedOfMethods;
    }

    public MetricsAggregator(
                              final SourceLineInfoAggregator sourceLineInfo, final int functionPointPerLogicalLines )
        throws SLGraphException {
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

    public void addConditionalNesting( final Node node ) throws SLGraphException {
        refreshNodeInformationNode;

        generalAcumulate(conditionalNesting, node);
        final Integer contConditionalNesting = conditionalNesting.get(node.getID());
        Integer maxConditional = maxConditionalNesting.get(node.getID());
        if (maxConditional == null) {
            maxConditionalNesting.put(node.getID(), contConditionalNesting);
            maxConditional = 1;
        } else {
            if (contConditionalNesting > maxConditional) {
                maxConditionalNesting.put(node.getID(), contConditionalNesting);
            }
        }
    }

    public void addConstructor( final Node node ) throws SLGraphException {
        refreshNodeInformation(nNode        generalAcumulate(constructor, node);
    }

    public void addControlStatement( final Node node ) throws SLGraphException {
        refreshNodeInformation(nodNode      addControlStatement(node, 1);
    }

    public void addControlStatement( final Node node,
                                     final int size ) throws SLNodexception {
        refreshNodeInformation(node);
        generalAcumulate(controlStatement, node, size);
    }

    public void addCyclomatic1( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
Node  generalAcumulate(cyclomatic1, node);
    }

    public void addCyclomatic2( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
  NodegeneralAcumulate(cyclomatic2, node);
    }

    public void addCyclomatic3( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
    NodeneralAcumulate(cyclomatic3, node);
    }

    public void addCyclomatic4( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
      NoderalAcumulate(cyclomatic4, node);
    }

    public void addDeclarativeStatement( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        NodelarativeStatement(node, 1);
    }

    public void addDeclarativeStatement( final Node node,
                                         final int qty ) throws SLGraphExceNode{
        refreshNodeInformation(node);
        generalAcumulate(declarativeStatement, node, qty);
    }

    public void addDepthLooping( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        geneNodemulate(depthLooping, node);
        final Integer contDepthLooping = depthLooping.get(node.getID());
        Integer maxDepth = maxDepthLooping.get(node.getID());
        if (maxDepth == null) {
            maxDepthLooping.put(node.getID(), contDepthLooping);
            maxDepth = 1;
        } else {
            if (contDepthLooping > maxDepth) {
                maxDepthLooping.put(node.getID(), contDepthLooping);
            }
        }
    }

    public void addEvents( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generaNodelate(event, node);
    }

    public void addExecutableStatement( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        addExecuNodetatement(node, 1);
    }

    public void addExecutableStatement( final Node node,
                                        final int size ) throws SLGraphException {
Node  refreshNodeInformation(node);
        generalAcumulate(executableStatement, node, size);
    }

    public void addExtendedClass( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumuNodextendedClasses, node);
    }

    public void addExtendedInterface( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulaNodeendedInterfaces, node);
    }

    public void addImplementedInterface( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulateNodementedInterfaces, node);
    }

    public void addNonPrivateMethod( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(nNodeateMethod, node);
    }

    public void addNonPrivateVariable( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(nonNodeeVariable, node);
    }

    public void addParameter( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(paramNode node);
    }

    public void addParentCyclomatic1( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(parentCNodetic1, node);
    }

    public void addParentCyclomatic2( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(parentCycNodec2, node);
    }

    public void addParentCyclomatic3( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(parentCycloNode, node);
    }

    public void addParentCyclomatic4( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(parentCyclomaNodenode);
    }

    public void addPrivateMethod( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(privateMethod, Node
    }

    public void addPrivateVariable( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(privateVariable, Node
    }

    public void addReturnPoint( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(returnPoint, node);Node

    public void buildPropertyValues( final GraphReadGraphReadGraphReader
        setProperties(session, SystemMetaModel.propMaxConditionalNesting, maxConditionalNesting);
        setProperties(session, SystemMetaModel.propMaxLoopingDepth, maxDepthLooping);
        setProperties(session, SystemMetaModel.propParameterMetric, parameters);
        setProperties(session, SystemMetaModel.propReturnPointMetric, returnPoint);
        interfaceComplexity = sumMaps(session, parameters, returnPoint, SystemMetaModel.propInterfaceComplexity);
        setProperties(session, SystemMetaModel.propDeclarativeStatementCount, declarativeStatement);
        setProperties(session, SystemMetaModel.propExecutableStatementCount, executableStatement);
        setProperties(session, SystemMetaModel.propControlStatementCount, controlStatement);
        final Map<String, Integer> nonControlStatementsCount = sumMaps(session, executableStatement, declarativeStatement,
                                                                       SystemMetaModel.propNonControlStatementCount);
        statementCount = sumMaps(session, nonControlStatementsCount, controlStatement, SystemMetaModel.propStatementCount);

        divMaps(session, executableStatement, statementCount, SystemMetaModel.propExecutability);
        divMaps(session, controlStatement, statementCount, SystemMetaModel.propControlDensity);

        if (!methodsAreBlocks) {
            setProperties(session, SystemMetaModel.propPrivateMethodsCount, privateMethod);
            setProperties(session, SystemMetaModel.propNonPrivateMethodsCount, nonPrivateMethod);
            methodsCount = sumMaps(session, privateMethod, nonPrivateMethod, SystemMetaModel.propMethodsCount);
        } else {
            methodsCount = sumMaps(session, privateMethod, nonPrivateMethod, SystemMetaModel.propBlocksCount);
        }

        setProperties(session, SystemMetaModel.propPrivateVariablesCount, privateVariable);
        setProperties(session, SystemMetaModel.propNonPrivateVariablesCount, nonPrivateVariable);
        variablesCount = sumMaps(session, privateVariable, nonPrivateVariable, SystemMetaModel.propVariablesCount);

        setProperties(session, SystemMetaModel.propConstructorsCount, constructor);
        setProperties(session, SystemMetaModel.propEventsCount, event);
        setProperties(session, SystemMetaModel.propImplementedInterfacesCount, implementedInterfaces);

        sumMaps(session, methodsCount, variablesCount, SystemMetaModel.propComponentSize);
        sumMaps(session, nonPrivateMethod, nonPrivateVariable, SystemMetaModel.propComponentInterfaceSize);

        setProperties(session, SystemMetaModel.propCyclomaticComplexity1, cyclomatic1);
        setProperties(session, SystemMetaModel.propCyclomaticComplexity2, cyclomatic2);
        setProperties(session, SystemMetaModel.propCyclomaticComplexity3, cyclomatic3);
        setProperties(session, SystemMetaModel.propCyclomaticComplexity4, cyclomatic4);
        setTotalCyclomaticComplexity(session, parentCyclomatic1, methodsCount, SystemMetaModel.propTotalCyclomaticComplexity1);
        setTotalCyclomaticComplexity(session, parentCyclomatic2, methodsCount, SystemMetaModel.propTotalCyclomaticComplexity2);
        setTotalCyclomaticComplexity(session, parentCyclomatic3, methodsCount, SystemMetaModel.propTotalCyclomaticComplexity3);
        setTotalCyclomaticComplexity(session, parentCyclomatic4, methodsCount, SystemMetaModel.propTotalCyclomaticComplexity4);
        sumMaps(session, cyclomatic1, interfaceComplexity, SystemMetaModel.propFunctionalComplexity1);
        sumMaps(session, cyclomatic2, interfaceComplexity, SystemMetaModel.propFunctionalComplexity2);
        sumMaps(session, cyclomatic3, interfaceComplexity, SystemMetaModel.propFunctionalComplexity3);
        sumMaps(session, cyclomatic4, interfaceComplexity, SystemMetaModel.propFunctionalComplexity4);

        setDecisionDensity(session, cyclomatic1, SystemMetaModel.propDecisionDensity1);
        setDecisionDensity(session, cyclomatic2, SystemMetaModel.propDecisionDensity2);
        setDecisionDensity(session, cyclomatic3, SystemMetaModel.propDecisionDensity3);
        setDecisionDensity(session, cyclomatic4, SystemMetaModel.propDecisionDensity4);

        for (final Entry<String, Integer> activeCodeArea : codeAreas.entrySet()) {
            final CompleteSourceLineInfo info = sourceLineInfo.getCodeArea(activeCodeArea.getValue());
            if (activeCodeArea.getValue() == 0 && info != null) {
                final double functionPoint = info.getLogicalLinesOfCode().doubleValue() / functionPointPerLogicalLines;
                setPropertyOnNode(activeCodeArea.getKey(), SystemMetaModel.propFunctionPointLOCBased, functionPoint);
            }
            if (info != null) {
                setLineMetrics(session, activeCodeArea.getKey(), info);
            }
        }
    }

    private Map<String, Double> divMaps( final Map<String, Integer> mainMap,
                                         final Map<String, Integer> secondMap ) throws SLGraphException {
        final Map<String, Double> resultMap = new TreeMap<String, Double>();
        for (final Entry<String, Integer> entry : mainMap.entrySet()) {
            resultMap.put(entry.getKey(), entry.getValue().doubleValue());
        }
        for (final Entry<String, Integer> activeReturn : secondMap.entrySet()) {
            final Double complexityCount = (Double)resultMap.get(activeReturn.getKey());
            if (complexityCount == null) {
                resultMap.put(activeReturn.getKey(), activeReturn.getValue().doubleValue());
            } else {
                final Double result = complexityCount.doubleValue() / activeReturn.getValue().doubleValue() * 100;
                resultMap.put(activeReturn.getKey(), result);
            }
        }

        return resultMap;
    }

    private Map<String, Double> divMaps( final GraphReader sessioGraphReader       GraphReaderal Map<String, Integer> mainMap,
                                         final Map<String, Integer> secondMap,
                                         final String propertyName ) throws SLGraphException {
        final Map<String, Double> resultMap = divMaps(mainMap, secondMap);
        setDoubleProperties(session, propertyName, resultMap);

        return resultMap;
    }

    private void generalAcumulate( final Map<String, Integer> map2Accumulate,
                                   final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        generalAcumulate(map2Accumulate, node,Node   }

    private void generalAcumulate( final Map<String, Integer> map2Accumulate,
                                   final Node node,
                                   final int size ) throws SLGraphException {
        refreshNodeInformation(nodeNode     final Integer count = map2Accumulate.get(node.getID());
        if (count == null) {
            map2Accumulate.put(node.getID(), size);
        } else {
            map2Accumulate.put(node.getID(), count + size);
        }
    }

    public Map<String, Integer> getCodeAreas() throws SLGraphException {
        return codeAreas;
    }

    public Map<String, Integer> getConditionalNesting() throws SLGraphException {
        return conditionalNesting;
    }

    public Map<String, Integer> getConstructor() throws SLGraphException {
        return constructor;
    }

    public Map<String, Integer> getControlStatement() throws SLGraphException {
        return controlStatement;
    }

    public Map<String, Integer> getCyclomatic1() throws SLGraphException {
        return cyclomatic1;
    }

    public Map<String, Integer> getCyclomatic2() throws SLGraphException {
        return cyclomatic2;
    }

    public Map<String, Integer> getCyclomatic3() throws SLGraphException {
        return cyclomatic3;
    }

    public Map<String, Integer> getCyclomatic4() throws SLGraphException {
        return cyclomatic4;
    }

    public Map<String, Integer> getDeclarativeStatement() throws SLGraphException {
        return declarativeStatement;
    }

    public Map<String, Integer> getDepthLooping() throws SLGraphException {
        return depthLooping;
    }

    public Map<String, Integer> getEvent() throws SLGraphException {
        return event;
    }

    public Map<String, Integer> getExecutableStatement() throws SLGraphException {
        return executableStatement;
    }

    public Map<String, Integer> getExtendedClasses() throws SLGraphException {
        return extendedClasses;
    }

    public Map<String, Integer> getExtendedInterfaces() throws SLGraphException {
        return extendedInterfaces;
    }

    public int getFunctionPointPerLogicalLines() throws SLGraphException {
        return functionPointPerLogicalLines;
    }

    public Map<String, Integer> getImplementedInterfaces() throws SLGraphException {
        return implementedInterfaces;
    }

    public Map<String, Integer> getInterfaceComplexity() throws SLGraphException {
        return interfaceComplexity;
    }

    public Map<String, Integer> getMaxConditionalNesting() throws SLGraphException {
        return maxConditionalNesting;
    }

    public Map<String, Integer> getMaxDepthLooping() throws SLGraphException {
        return maxDepthLooping;
    }

    public Map<String, Integer> getNonPrivateMethod() throws SLGraphException {
        return nonPrivateMethod;
    }

    public Map<String, Integer> getNonPrivateVariable() throws SLGraphException {
        return nonPrivateVariable;
    }

    public Map<String, Integer> getParameters() throws SLGraphException {
        return parameters;
    }

    public Map<String, Integer> getParentCyclomatic1() throws SLGraphException {
        return parentCyclomatic1;
    }

    public Map<String, Integer> getParentCyclomatic2() throws SLGraphException {
        return parentCyclomatic2;
    }

    public Map<String, Integer> getParentCyclomatic3() throws SLGraphException {
        return parentCyclomatic3;
    }

    public Map<String, Integer> getParentCyclomatic4() throws SLGraphException {
        return parentCyclomatic4;
    }

    public Map<String, Integer> getPrivateMethod() throws SLGraphException {
        return privateMethod;
    }

    public Map<String, Integer> getPrivateVariable() throws SLGraphException {
        return privateVariable;
    }

    public Map<String, Integer> getReturnPoint() throws SLGraphException {
        return returnPoint;
    }

    public SourceLineInfoAggregator getSourceLineInfo() throws SLGraphException {
        return sourceLineInfo;
    }

    public Map<String, Integer> getStatementCount() throws SLGraphException {
        return statementCount;
    }

    public boolean isMethodsAreBlocks() throws SLGraphException {
        return methodsAreBlocks;
    }

    private void refreshNodeInformation( final Node node ) {
        currentNodes.put(node.getID(), node);
    }

    private void setDecisionDensity( final SLSimpleGraphSesNodeession,
                                     final Map<String, Integer> cyclomatic,
                                     final String propertyName ) throws SLGraphException {
        for (final Entry<String, Integer> activeCyclomatic : cyclomatic.entrySet()) {
            final Integer activeCodeArea = codeAreas.get(activeCyclomatic.getKey());
            if (activeCodeArea != null) {
                final CompleteSourceLineInfo info = sourceLineInfo.getCodeArea(activeCodeArea);
                if (info != null) {
                    final double decisionDensity = activeCyclomatic.getValue().doubleValue()
                                                   / info.getLogicalLinesOfCode().doubleValue();
                    setPropertyOnNode(activeCyclomatic.getKey(), propertyName, decisionDensity);
                }
            }
        }
    }

    private void setDoubleProperties( final GraphReader session,
      GraphReader            finaGraphReadere,
                                      final Map<String, Double> counter ) throws SLGraphException {
        for (final Entry<String, Double> activeElement : counter.entrySet()) {
            setPropertyOnNode(activeElement.getKey(), propertyName, activeElement.getValue());
        }
    }

    public void setLineInfo( final int codeArea,
                             final Node node ) throws SLGraphException {
        codeAreas.put(node.getID(), codeArea);
    }

    private void setLineMetrics( finNodeimpleGraphSession session,
                                 final String nodeId,
                                 final CompleteSourceLineInfo lineInfo ) throws SLGraphException {
        setPropertyOnNode(nodeId, SystemMetaModel.propPhysicalLines, lineInfo.getPhysicalLines());
        setPropertyOnNode(nodeId, SystemMetaModel.propEfectiveLines, lineInfo.getEfectiveLines());
        setPropertyOnNode(nodeId, SystemMetaModel.propLogicalLines, lineInfo.getLogicalLines());
        setPropertyOnNode(nodeId, SystemMetaModel.propLogicalLinesOfCode, lineInfo.getLogicalLinesOfCode());
        setPropertyOnNode(nodeId, SystemMetaModel.propLogicalLinesOfWhitespace, lineInfo.getLogicalLinesOfWhitespace());
        setPropertyOnNode(nodeId, SystemMetaModel.propCommentedLines, lineInfo.getCommentedLines());
        setPropertyOnNode(nodeId, SystemMetaModel.propFullCommentLines, lineInfo.getFullCommentLines());
        setPropertyOnNode(nodeId, SystemMetaModel.propMeaningfulCommentLines, lineInfo.getMeaningfulCommentLines());
        setPropertyOnNode(nodeId, SystemMetaModel.propCommentDensity, lineInfo.getCommentDensity());
        setPropertyOnNode(nodeId, SystemMetaModel.propCodePercentage, lineInfo.getCodePercentage());
        setPropertyOnNode(nodeId, SystemMetaModel.propWhitespacePercentage, lineInfo.getWhitespacePercentage());
    }

    private void setProperties( final GraphReader session,
               GraphReaderal String propertyName,
 GraphReader           final Map<String, Integer> counter ) throws SLGraphException {
        for (final Entry<String, Integer> activeElement : counter.entrySet()) {
            setPropertyOnNode(activeElement.getKey(), propertyName, activeElement.getValue());
        }
    }

    private void setPropertyOnNode( final String key,
                                    final String propertyName,
                                    final Double value ) throws SLGraphException {
        final Node node = currentNodes.get(key);
        node.setProperty(Double.class, propertyName, value);
    }

    private void setPropertNodee( final String key,
                                    final String propertyName,
                                    final Integer value ) throws SLGraphException {
        final Node node = currentNodes.get(key);
        node.setProperty(Integer.class, propertyName, value);
    }

    private void setTotalCycNodecComplexity( final GraphReader session,
                        GraphReader   final Map<String, Integer> pareGraphReader                                         final Map<String, Integer> totalMethods,
                                               final String propertyName ) throws SLGraphException {
        for (final Entry<String, Integer> activeMethodCount : totalMethods.entrySet()) {
            Integer cyclomaticCount = parentCyclomatic.get(activeMethodCount.getKey());
            if (cyclomaticCount != null) {
                cyclomaticCount = cyclomaticCount - activeMethodCount.getValue() + 1;
                setPropertyOnNode(activeMethodCount.getKey(), propertyName, cyclomaticCount);
            }
        }
    }

    public void subtractConditionalNesting( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        Integer counter = conditionalNesting.get(node.getINode        conditionalNesting.put(node.getID(), --counter);
    }

    public void subtractDepthLooping( final Node node ) throws SLGraphException {
        refreshNodeInformation(node);
        Integer counter = depthLooping.get(node.getID());
  NodedepthLooping.put(node.getID(), --counter);
    }

    private Map<String, Integer> sumMaps( final Map<String, Integer> mainMap,
                                          final Map<String, Integer> secondMap ) throws SLGraphException {
        final Map<String, Integer> resultMap = new TreeMap<String, Integer>(mainMap);

        for (final Entry<String, Integer> activeReturn : secondMap.entrySet()) {
            final Integer complexityCount = resultMap.get(activeReturn.getKey());
            if (complexityCount == null) {
                resultMap.put(activeReturn.getKey(), activeReturn.getValue());
            } else {
                resultMap.put(activeReturn.getKey(), complexityCount + activeReturn.getValue());
            }
        }

        return resultMap;
    }

    private Map<String, Integer> sumMaps( final GraphReader session,
                                 GraphReadertring, Integer> mainMap,
                  GraphReader    final Map<String, Integer> secondMap,
                                          final String propertyName ) throws SLGraphException {
        final Map<String, Integer> resultMap = sumMaps(mainMap, secondMap);
        setProperties(session, propertyName, resultMap);

        return resultMap;
    }
}
