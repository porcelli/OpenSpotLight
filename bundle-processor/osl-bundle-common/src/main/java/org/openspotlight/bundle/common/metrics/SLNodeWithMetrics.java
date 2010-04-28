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

import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.annotation.SLProperty;

public interface SLNodeWithMetrics extends SLNode {

    @SLProperty
    public Integer getBlocksCount();

    @SLProperty
    public Integer getCodePercentage();

    @SLProperty
    public Integer getCommentDensity();

    @SLProperty
    public Integer getCommentedLines();

    @SLProperty
    public Integer getComponentInterfaceSize();

    @SLProperty
    public Integer getComponentSize();

    @SLProperty
    public Integer getConstructorsCount();

    @SLProperty
    public Integer getControlDensity();

    @SLProperty
    public Integer getControlStatementCount();

    @SLProperty
    public Integer getCyclomaticComplexity1();

    @SLProperty
    public Integer getCyclomaticComplexity2();

    @SLProperty
    public Integer getCyclomaticComplexity3();

    @SLProperty
    public Integer getCyclomaticComplexity4();

    @SLProperty
    public Integer getDecisionDensity1();

    @SLProperty
    public Integer getDecisionDensity2();

    @SLProperty
    public Integer getDecisionDensity3();

    @SLProperty
    public Integer getDecisionDensity4();

    @SLProperty
    public Integer getDeclarativeStatementCount();

    @SLProperty
    public Integer getEfectiveLines();

    @SLProperty
    public Integer getEventsCount();

    @SLProperty
    public Integer getExecutability();

    @SLProperty
    public Integer getExecutableStatementCount();

    @SLProperty
    public Integer getFullCommentLines();

    @SLProperty
    public Integer getFunctionalComplexity1();

    @SLProperty
    public Integer getFunctionalComplexity2();

    @SLProperty
    public Integer getFunctionalComplexity3();

    @SLProperty
    public Integer getFunctionalComplexity4();

    @SLProperty
    public Integer getFunctionPointLOCBased();

    @SLProperty
    public Integer getImplementedInterfacesCount();

    @SLProperty
    public Integer getInterfaceComplexity();

    @SLProperty
    public Integer getLogicalLines();

    @SLProperty
    public Integer getLogicalLinesOfCode();

    @SLProperty
    public Integer getLogicalLinesOfWhitespace();

    @SLProperty
    public Integer getMaxConditionalNesting();

    @SLProperty
    public Integer getMaxLoopingDepth();

    @SLProperty
    public Integer getMeaningfulCommentLines();

    @SLProperty
    public Integer getMethodsCount();

    @SLProperty
    public Integer getNonControlStatementCount();

    @SLProperty
    public Integer getNonPrivateMethodsCount();

    @SLProperty
    public Integer getNonPrivateVariablesCount();

    @SLProperty
    public Integer getParameterMetric();

    @SLProperty
    public Integer getPhysicalLines();

    @SLProperty
    public Integer getPrivateMethodsCount();

    @SLProperty
    public Integer getPrivateVariablesCount();

    @SLProperty
    public Integer getReturnPointMetric();

    @SLProperty
    public Integer getStatementCount();

    @SLProperty
    public Integer getTotalCyclomaticComplexity1();

    @SLProperty
    public Integer getTotalCyclomaticComplexity2();

    @SLProperty
    public Integer getTotalCyclomaticComplexity3();

    @SLProperty
    public Integer getTotalCyclomaticComplexity4();

    @SLProperty
    public Integer getVariablesCount();

    @SLProperty
    public Integer getWhitespacePercentage();

    public void setBlocksCount( Integer newBlocksCount );

    public void setCodePercentage( Integer newCodePercentage );

    public void setCommentDensity( Integer newCommentDensity );

    public void setCommentedLines( Integer newCommentedLines );

    public void setComponentInterfaceSize( Integer newComponentInterfaceSize );

    public void setComponentSize( Integer newComponentSize );

    public void setConstructorsCount( Integer newConstructorsCount );

    public void setControlDensity( Integer newControlDensity );

    public void setControlStatementCount( Integer newControlStatementCount );

    public void setCyclomaticComplexity1( Integer newCyclomaticComplexity1 );

    public void setCyclomaticComplexity2( Integer newCyclomaticComplexity2 );

    public void setCyclomaticComplexity3( Integer newCyclomaticComplexity3 );

    public void setCyclomaticComplexity4( Integer newCyclomaticComplexity4 );

    public void setDecisionDensity1( Integer newDecisionDensity1 );

    public void setDecisionDensity2( Integer newDecisionDensity2 );

    public void setDecisionDensity3( Integer newDecisionDensity3 );

    public void setDecisionDensity4( Integer newDecisionDensity4 );

    public void setDeclarativeStatementCount( Integer newDeclarativeStatementCount );

    public void setEfectiveLines( Integer newEfectiveLines );

    public void setEventsCount( Integer newEventsCount );

    public void setExecutability( Integer newExecutability );

    public void setExecutableStatementCount( Integer newExecutableStatementCount );

    public void setFullCommentLines( Integer newFullCommentLines );

    public void setFunctionalComplexity1( Integer newFunctionalComplexity1 );

    public void setFunctionalComplexity2( Integer newFunctionalComplexity2 );

    public void setFunctionalComplexity3( Integer newFunctionalComplexity3 );

    public void setFunctionalComplexity4( Integer newFunctionalComplexity4 );

    public void setFunctionPointLOCBased( Integer newFunctionPointLOCBased );

    public void setImplementedInterfacesCount( Integer newImplementedInterfacesCount );

    public void setInterfaceComplexity( Integer newInterfaceComplexity );

    public void setLogicalLines( Integer newLogicalLines );

    public void setLogicalLinesOfCode( Integer newLogicalLinesOfCode );

    public void setLogicalLinesOfWhitespace( Integer newLogicalLinesOfWhitespace );

    public void setMaxConditionalNesting( Integer newMaxConditionalNesting );

    public void setMaxLoopingDepth( Integer newMaxLoopingDepth );

    public void setMeaningfulCommentLines( Integer newMeaningfulCommentLines );

    public void setMethodsCount( Integer newMethodsCount );

    public void setNonControlStatementCount( Integer newNonControlStatementCount );

    public void setNonPrivateMethodsCount( Integer newNonPrivateMethodsCount );

    public void setNonPrivateVariablesCount( Integer newNonPrivateVariablesCount );

    public void setParameterMetric( Integer newParameterMetric );

    public void setPhysicalLines( Integer newPhysicalLines );

    public void setPrivateMethodsCount( Integer newPrivateMethodsCount );

    public void setPrivateVariablesCount( Integer newPrivateVariablesCount );

    public void setReturnPointMetric( Integer newReturnPointMetric );

    public void setStatementCount( Integer newStatementCount );

    public void setTotalCyclomaticComplexity1( Integer newTotalCyclomaticComplexity1 );

    public void setTotalCyclomaticComplexity2( Integer newTotalCyclomaticComplexity2 );

    public void setTotalCyclomaticComplexity3( Integer newTotalCyclomaticComplexity3 );

    public void setTotalCyclomaticComplexity4( Integer newTotalCyclomaticComplexity4 );

    public void setVariablesCount( Integer newVariablesCount );

    public void setWhitespacePercentage( Integer newWhitespacePercentage );

}
