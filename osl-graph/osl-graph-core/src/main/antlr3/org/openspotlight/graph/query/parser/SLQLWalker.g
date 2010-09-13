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
 * OpenSpotLight - Plataforma de Governanca de TI de Codigo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuicao de direito autoral declarada e atribuida pelo autor.
 * Todas as contribuicoes de terceiros estao distribuidas sob licenca da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa e software livre; voce pode redistribui-lo e/ou modifica-lo sob os 
 * termos da Licenca Publica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa e distribuido na expectativa de que seja util, porem, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implicita de COMERCIABILIDADE OU ADEQUACAO A UMA
 * FINALIDADE ESPECIFICA. Consulte a Licenca Publica Geral Menor do GNU para mais detalhes.  
 * 
 * Voce deve ter recebido uma copia da Licenca Publica Geral Menor do GNU junto com este
 * programa; se nao, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
tree grammar SLQLWalker;

options{
	output=template;
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
 * OpenSpotLight - Plataforma de Governanca de TI de Codigo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuicao de direito autoral declarada e atribuida pelo autor.
 * Todas as contribuicoes de terceiros estao distribuidas sob licenca da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa e software livre; voce pode redistribui-lo e/ou modifica-lo sob os 
 * termos da Licenca Publica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa e distribuido na expectativa de que seja util, porem, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implicita de COMERCIABILIDADE OU ADEQUACAO A UMA
 * FINALIDADE ESPECIFICA. Consulte a Licenca Publica Geral Menor do GNU para mais detalhes.  
 * 
 * Voce deve ter recebido uma copia da Licenca Publica Geral Menor do GNU junto com este
 * programa; se nao, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.query.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.io.Serializable;

}

@members {
	private QueryTextInternalInfo queryInfo = new QueryTextInternalInfo();
	private int stringCount = -1;
	
	private String formatUnicodeString(String input){
		try {
			StringBuilder sb = new StringBuilder();
	        char[] testArray = input.toCharArray();
	        int index = 0;
	        while (true) {
	            if (testArray.length >= index + 1 && testArray[index] == '\\' && testArray[index + 1] == 'u') {
	                StringBuilder tsb = new StringBuilder();
	                tsb.append(testArray[index + 2]);
	                tsb.append(testArray[index + 3]);
	                tsb.append(testArray[index + 4]);
	                tsb.append(testArray[index + 5]);
	                Integer intValue = Integer.parseInt(tsb.toString(), 16);
	                sb.append(Character.toChars(intValue)[0]);
	                index = index + 6;
	            } else {
	                sb.append(testArray[index]);
	                index++;
	            }
	            if (index == testArray.length) {
	                break;
	            }
	        }
	        return sb.toString();
	    } catch (Exception ex){
			return input;
		}
	}
}

compilationUnit returns [QueryTextInternalInfo queryInfoReturn]
	:	enclosedCompilationUnit
	{	queryInfo.setContent($enclosedCompilationUnit.st.toString());
		$queryInfoReturn = queryInfo;	}
	;

enclosedCompilationUnit
	:	^(VT_COMPILATION_UNIT
			generalCollatorLevel?
			defineOutput?
			defineMessage*
			defineDominValues*
			(defineTarget {queryInfo.setDefineTargetContent($defineTarget.st.toString());})?
			(sl+=select)+)
		-> compilationUnit(generalCollatorLevel={$generalCollatorLevel.st},selects={$sl})
	;

generalCollatorLevel
	:	^(VT_COLLATOR_LEVEL collatorLevel)
		-> generalCollatorLevel(collatorLevel={$collatorLevel.st})
	;

collatorLevel
	:	IDENTICAL_VK	-> collatorLevelIdentical()
	|	PRIMARY_VK		-> collatorLevelPrimary()
	|	SECONDARY_VK	-> collatorLevelSecondary()
	|	TERTIARY_VK		-> collatorLevelTertiary()
	;

defineOutput
	:	GRAPHIC_MODEL_NAME
	{	queryInfo.setOutputModelName($GRAPHIC_MODEL_NAME.text);	}
	;

defineTarget
@init	{
	boolean hasSelect = false;
	boolean hasKeepResult = false;
}
@after	{
	queryInfo.setHasTarget(true);
	queryInfo.setTargetKeepsResult(hasKeepResult);
}	:	^(DEFINE_TARGET_VK (nodeType (KEEP_RESULT_VK {hasKeepResult = true;})?|select {hasSelect = true; hasKeepResult = $select.hasKeepResult;}))
	-> {hasSelect}? defineTargetWithSelect(select={$select.st})
	-> defineTarget(nodeType={$nodeType.st})
	;

defineMessage
	:	^(DEFINE_MESSAGE_VK variable STRING)
		{	queryInfo.getMessageVariables().put($variable.varName, $STRING.text.substring(1, $STRING.text.length() -1));	}
	;

variable returns [String varName]
	:	VAR_INT		{$varName = $VAR_INT.text; queryInfo.getIntVariables().add($VAR_INT.text);}
	|	VAR_DEC		{$varName = $VAR_DEC.text; queryInfo.getDecVariables().add($VAR_DEC.text);}
	|	VAR_STRING	{$varName = $VAR_STRING.text; queryInfo.getStringVariables().add($VAR_STRING.text);}
	|	VAR_BOOL	{$varName = $VAR_BOOL.text; queryInfo.getBoolVariables().add($VAR_BOOL.text);}
	;

defineDominValues
@init	{
	Set<Serializable> domainValues = new HashSet<Serializable>();
}	:	^(DEFINE_DOMAIN_VK variable (ve=valueExpr {domainValues.add((Serializable)$ve.value);})+)
		{	queryInfo.getDomainVariables().put($variable.varName, domainValues);	}
	;

select returns [boolean hasKeepResult]
scope	{
	boolean isSelectStar;
}
@init	{
	$select::isSelectStar = false;
	$hasKeepResult = false;
}	:	^(SELECT selectedElements 
			byLink?
			where?
			executing?
			limitOffset?
			(keepResult {$hasKeepResult = true;})?
			orderBy?
			selectCollatorLevel?)
		-> select(selectedElements={$selectedElements.st},
					byLink={$byLink.st},
					where={$where.st},
					orderBy={$orderBy.st},
					keepResult={$keepResult.st},
					executing={$executing.st},
					limitOffset={$limitOffset.st},
					collatorLevel={$selectCollatorLevel.st})
	;

keepResult
	:	KEEP_RESULT_VK	-> keepResult()
	;

selectedElements
	:	STAR			{$select::isSelectStar = true;} -> selectStar()
	|	DOUBLE_STAR		{$select::isSelectStar = true;} -> selectDoubleStar()
	|	nodeType moreNodeTypes -> nodeTypes(firstType={$nodeType.st}, moreTypes={$moreNodeTypes.st}) 
	;

moreNodeTypes
	:	swcnt+=startWithCommaNodeType*	-> moreNodeTypes(nodeTypes={swcnt})
	;

startWithCommaNodeType
	:	nodeType -> startWithCommaNodeType(nodeType={$nodeType.st})
	;

byLink
@init	{
	int count = 0;
}	:	^(BY_LINK_VK (bld+=byLinkDefinition[count] {count++;})+) -> byLink(byLinkDefinitions={$bld})
	;

byLinkDefinition [int count]
	:	LINK_TYPE_NAME linkDirections	
		-> {$select::isSelectStar && $count == 0}? byLinkDefinitionWithoutComma(linkType={$LINK_TYPE_NAME.text}, linkDirections={$linkDirections.st})
		-> byLinkDefinition(linkType={$LINK_TYPE_NAME.text}, linkDirections={$linkDirections.st})
	;

where
	:	^(WHERE_VK (wgbnt+=whereGroupByNodeType)+)
		-> where(content={$wgbnt})
	;

whereGroupByNodeType
	:	^(VT_GROUP_BY_NODE_TYPE nodeType expr)
		-> whereGroupByNodeType(nodeType={$nodeType.st}, expr={$expr.st})
	;

limitOffset
@init	{
	boolean hasOffset = false;
}	:	^(LIMIT_VK valueExpr (offset {hasOffset = true;})?)
		-> {hasOffset}? limitOffset(expr={$valueExpr.st},offset={$offset.st})
		-> limit(expr={$valueExpr.st})
	;

offset
	:	^(OFFSET_VK valueExpr)			-> offset(expr={$valueExpr.st})
	;

orderBy
	:	^(ORDER_BY_VK (obgbnt+=orderByGroupByNodeType)+)
		-> orderBy(content={$obgbnt})
	;

orderByGroupByNodeType
@init	{
	int propertyCount = 0;
}	:	^(VT_GROUP_BY_NODE_TYPE nodeType (pr+=propertyReference[propertyCount]{propertyCount++;})*)
		-> orderByGroupByNodeType(nodeType={$nodeType.st}, properties={$pr} )
	;

selectCollatorLevel
	:	^(VT_COLLATOR_LEVEL collatorLevel)
		-> selectCollatorLevel(collatorLevel={$collatorLevel.st})
	;

propertyReference[int propertyCount]
	:	^(PROPERTY PROPERTY_NAME orderType)
		-> {(propertyCount > 0)}? commaOrderProperty(propertyName={$PROPERTY_NAME.text}, orderType={$orderType.st})
		-> orderProperty(propertyName={$PROPERTY_NAME.text}, orderType={$orderType.st})
	;

orderType
	:	ASC_VK	-> ascType()
	|	DESC_VK	-> descType()
	;

expr
	:	^(AND_OPERATOR lhs=expr NEGATED_OPERATOR? rhs=expr)
		-> {$NEGATED_OPERATOR == null}? booleanAndOperator(lhs={$lhs.st}, rhs={$rhs.st})
		-> negatedBooleanAndOperator(lhs={$lhs.st}, rhs={$rhs.st})
	|	^(OR_OPERATOR lhs=expr NEGATED_OPERATOR? rhs=expr)
		-> {$NEGATED_OPERATOR == null}? booleanOrOperator(lhs={$lhs.st}, rhs={$rhs.st})
		-> negatedBooleanOrOperator(lhs={$lhs.st}, rhs={$rhs.st})
	|	^(PROPERTY ^(operator PROPERTY_NAME valueExpr))
		 -> whereProperty(operator={$operator.st}, propertyName={$PROPERTY_NAME.text}, value={$valueExpr.st})
	|	^(LINK_VK ^(numericOperator LINK_TYPE_NAME linkDirections numericValue))
		 -> whereLinkCount(operator={$numericOperator.st}, linkName={$LINK_TYPE_NAME.text}, linkDirections={$linkDirections.st}, value={$numericValue.st})
	;

linkDirections
	:	^(VT_LINK_DIRECTIONS linkDirectionOptions) -> linkDirections(linkDirectionOptions={$linkDirectionOptions.st}) 
	;

linkDirectionOptions
	:	A_VK B_VK BOTH_VK	-> linkDirectionABBoth()
	|	A_VK B_VK			-> linkDirectionAB()
	|	A_VK BOTH_VK		-> linkDirectionABoth()
	|	B_VK BOTH_VK		-> linkDirectionBBoth()
	|	A_VK				-> linkDirectionA()
	|	B_VK				-> linkDirectionB()
	|	BOTH_VK				-> linkDirectionBoth()
	;

operator
	:	so=stringOperator	-> operator(op={$so.st})
	|	no=numericOperator	-> operator(op={$no.st})
	;

stringOperator
	:	STARTS_WITH			-> startsWithOperator()
	|	ENDS_WITH			-> endsWithOperator()
	|	CONTAINS			-> containsOperator()
	|	NOT_CONTAINS		-> notContainsOperator()
	;

numericOperator
	:	EQUALS				-> equalsOperator()
	|	GREATER				-> greaterOperator()
	|	LESSER				-> lesserOperator()
	|	GREATER_OR_EQUALS	-> greaterOrEqualsOperator()
	|	LESSER_OR_EQUALS	-> lesserOrEqualsOperator()
	|	NOT_EQUALS			-> notEqualsOperator()
	;

valueExpr returns [Object value]
	:	NULL_VK			{$value=null;}		-> nullValue()
	|	bv=booleanValue	{$value=$bv.value;}	-> anyValue(value={$bv.st})
	|	nv=numericValue	{$value=$nv.value;}	-> anyValue(value={$nv.st})
	|	sv=stringValue	{$value=$sv.value;}	-> anyValue(value={$sv.st})
	|	N_VK 
	;

booleanValue returns [Object value]
	:	TRUE_VK		{$value = true;}									->	trueValue()
	|	FALSE_VK	{$value = false;}									->	falseValue()
	|	VAR_BOOL	{queryInfo.getBoolVariables().add($VAR_BOOL.text);}	->	booleanVariableValue(variableName={$VAR_BOOL.text})
	;

stringValue returns [Object value]
	:	STRING		{$value = $STRING.text; stringCount++; queryInfo.getStringsConstant().put(stringCount, formatUnicodeString($STRING.text.substring(1, $STRING.text.length() - 1)));}
				-> stringValue(value={stringCount})
	|	VAR_STRING	{queryInfo.getStringVariables().add($VAR_STRING.text);}
				-> stringVariableValue(variableName={$VAR_STRING.text})
	;

numericValue returns [Object value]
	:	INT		{$value = new Integer($INT.text);}					-> intValue(value={$INT.text})
	|	DEC		{$value = new Float($DEC.text);}					-> decValue(value={$DEC.text})
	|	VAR_DEC	{queryInfo.getDecVariables().add($VAR_DEC.text);}	-> decVariableValue(variableName={$VAR_DEC.text})
	|	VAR_INT	{queryInfo.getIntVariables().add($VAR_INT.text);}	-> intVariableValue(variableName={$VAR_INT.text})
	;

executing
	:	^(VT_EXECUTING_TIMES valueExpr)	-> executing(times={$valueExpr.st})
	;

nodeType
	:	NODE_TYPE_NAME -> nodeType(typeName={$NODE_TYPE_NAME.text})
	|	NODE_TYPE_NAME_WITH_SUBTYPES -> nodeTypeWithSubTypes(typeName={$NODE_TYPE_NAME_WITH_SUBTYPES.text})
	;
