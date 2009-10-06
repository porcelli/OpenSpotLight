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
tree grammar SLQLWalker;

options{
	tokenVocab=SLQL;
	ASTLabelType=CommonTree;
	TokenLabelType=CommonToken;
}

@header {
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

package org.openspotlight.slql.parser;
}

compilationUnit
	:	^(VT_COMPILATION_UNIT
			useCollatorLevel?
			defineOutput?
			defineMessage*
			defineDominValues*
			defineTarget? 
			select+)
	;

useCollatorLevel
	:	^(VT_COLLATOR_LEVEL (IDENTICAL_VK|PRIMARY_VK|SECONDARY_VK|TERTIARY_VK))
	;

defineOutput
	:	GRAPHIC_MODEL_NAME
	;

defineTarget
	:	^(DEFINE_TARGET_VK nodeType KEEP_RESULT_VK?)
	;

defineMessage
	:	^(DEFINE_MESSAGE_VK (VAR_INT|VAR_DEC|VAR_STRING|VAR_BOOL) STRING)
	;

defineDominValues
	:	^(DEFINE_DOMAIN_VK domainValues)
	;

domainValues
	:	VAR_STRING STRING+
	|	VAR_INT INT+
	|	VAR_DEC (DEC|INT)+
	;

select
	:	^(SELECT_VK (STAR|DOUBLE_STAR|nodeType+) 
			byLink?
			where?
			executing?
			limitOffset?
			(KEEP_RESULT_VK|orderBy)?
			useCollatorLevel?)
	;

byLink
	:	^(BY_LINK_VK byLinkDefinition+)
	;

byLinkDefinition
	:	linkType linkDirections
	;

where
	:	^(WHERE_VK whereGroupByNodeType+)
	;

whereGroupByNodeType
	:	^(VT_GROUP_BY_NODE_TYPE nodeType expr)
	;

limitOffset
	:	^(LIMIT_VK INT? VAR_INT? offset?)
	;

offset
	:	^(OFFSET_VK (INT|VAR_INT))
	;

orderBy
	:	^(ORDER_BY_VK orderByGroupByNodeType+)
	;

orderByGroupByNodeType
	:	^(VT_GROUP_BY_NODE_TYPE nodeType propertyReference*)
	;

propertyReference
	:	^(PROPERTY propertyName)
	;

linkCountReference
	:	^(LINK_VK linkType linkDirections)
	;

expr
	:	^(AND_OPERATOR expr expr)
	|	^(OR_OPERATOR expr expr)
	|	^(NEGATED_OPERATOR expr)
	|	^(PROPERTY ^(operator propertyName valueExpr)) 
	|	^(LINK_VK ^(numericOperator linkType linkDirections numericValue))
	;

linkDirections
	:	^(VT_LINK_DIRECTIONS linkDirectionOptions)
	;

linkDirectionOptions
	:	A_VK B_VK BOTH_VK
	|	A_VK B_VK
	|	A_VK BOTH_VK
	|	B_VK BOTH_VK
	|	A_VK
	|	B_VK
	|	BOTH_VK
	;

operator
	:	stringOperator
	|	numericOperator
	;

stringOperator
	:	STARTS_WITH
	|	ENDS_WITH
	|	CONTAINS
	;

numericOperator
	:	EQUALS
	|	GREATER
	|	LESSER
	|	GREATER_OR_EQUALS
	|	LESSER_OR_EQUALS
	|	NOT_EQUALS
	;

valueExpr
	:	NULL_VK
	|	booleanValue
	|	numericValue
	|	stringValue
	;

booleanValue
	:	TRUE_VK
	|	FALSE_VK
	|	VAR_BOOL
	;

stringValue
	:	STRING
	|	VAR_STRING
	;

numericValue
	:	INT
	|	DEC
	|	VAR_DEC
	|	VAR_INT
	;

executing
	:	^(VT_EXECUTING_TIMES (INT|VAR_INT|N_VK))
	;

graphicModel
	:	GRAPHIC_MODEL_NAME
	;

linkType
	:	LINK_TYPE_NAME
	;

nodeType
	:	NODE_TYPE_NAME
	|	NODE_TYPE_NAME_WITH_SUBTYPES
	;

propertyName
	:	PROPERTY_NAME
	;
