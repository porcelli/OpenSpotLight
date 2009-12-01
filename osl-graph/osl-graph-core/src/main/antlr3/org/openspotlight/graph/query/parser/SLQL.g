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
grammar SLQL;

options {
	output=AST;
}

tokens {
	VT_COMPILATION_UNIT;
	VT_COLLATOR_LEVEL;
	VT_GROUP_BY_NODE_TYPE;
	VT_EXECUTING_TIMES;
	VT_LINK_DIRECTIONS;
	
	ASC_VK;
	DESC_VK;
	WHERE_VK;
	LINK_VK;
	VALUES_VK;
	A_VK;
	B_VK;
	BOTH_VK;
	NULL_VK;
	TRUE_VK;
	FALSE_VK;
	EXECUTING_VK;
	TIMES_VK;
	KEEP_RESULT_VK;
	N_VK;
	COLLATOR_VK;
	LEVEL_VK;
	IDENTICAL_VK;
	PRIMARY_VK;
	SECONDARY_VK;
	TERTIARY_VK;
	ORDER_BY_VK;
	BY_LINK_VK;
	DEFINE_OUTPUT_VK;
	DEFINE_TARGET_VK;
	DEFINE_MESSAGE_VK;
	DEFINE_DOMAIN_VK;
	LIMIT_VK;
	OFFSET_VK;
	
	GRAPHIC_MODEL_NAME;
	LINK_TYPE_NAME;
	NODE_TYPE_NAME_WITH_SUBTYPES;
	NODE_TYPE_NAME;
	PROPERTY_NAME;
}

@lexer::header {
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

import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxExceptionFactory;
}

@parser::header {
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

import java.util.HashSet;
import java.util.Set;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxExceptionFactory;
}

@parser::members {
	private List<SLInvalidQuerySyntaxException> errors = new ArrayList<SLInvalidQuerySyntaxException>();
	private SLInvalidQuerySyntaxExceptionFactory errorMessageFactory = new SLInvalidQuerySyntaxExceptionFactory(tokenNames);
	private boolean isInsideDefineTarget = false;
	private boolean isgUnitTest = true;
	private Set<String> defineMessageVariableSet = new HashSet<String>();
	private Set<String> defineDomainVariableSet = new HashSet<String>();
	private String defineTargetTreeResult = null;

	public void setIsTesting(boolean isTesting){
		this.isgUnitTest = isTesting;
	}

	private boolean validateIdentifierKey(String text) {
		return validateLT(1, text);
	}

	private boolean validateLT(int LTNumber, String text) {
		String text2Validate = retrieveLT( LTNumber );
		return text2Validate == null ? false : text2Validate.equalsIgnoreCase(text);
	}

	private String retrieveLT(int LTNumber) {
      	if (null == input)
			return null;
		if (null == input.LT(LTNumber))
			return null;
		if (null == input.LT(LTNumber).getText())
			return null;
	
		return input.LT(LTNumber).getText();
	}

	public void reportError(RecognitionException ex) {
		// if we've already reported an error and have not matched a token
		// yet successfully, don't report any errors.
		if (state.errorRecovery) {
			return;
		}
		state.errorRecovery = true;
	
		errors.add(errorMessageFactory.createSLQueryLanguageException(ex));
		
		if (isgUnitTest){
			throw new RuntimeException();
		}
	}
	
	/** return the raw DroolsParserException errors */
	public List<SLInvalidQuerySyntaxException> getErrors() {
		return errors;
	}
	
	/** Return a list of pretty strings summarising the errors */
	public List<String> getErrorMessages() {
		List<String> messages = new ArrayList<String>(errors.size());
	
		for (SLInvalidQuerySyntaxException activeException : errors) {
			messages.add(activeException.getMessage());
		}
	
		return messages;
	}
	
	/** return true if any parser errors were accumulated */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	public String getDefineTargetTreeResult(){
		return defineTargetTreeResult;
	}
}

compilationUnit
scope	{
	int selectCount;
	boolean hasDefineTarget;
}
@init	{
	$compilationUnit::selectCount = 0;
	$compilationUnit::hasDefineTarget = false;
}
@after	{
	if (isgUnitTest && hasErrors()) {
		throw new RecognitionException();
	}
}	:	useCollatorLevel?
		defineOutput?
		defineMessage*
		defineDominValues*
		(defineTarget {$compilationUnit::hasDefineTarget = true; defineTargetTreeResult = ((CommonTree)$defineTarget.tree).toStringTree().toLowerCase();} )? 
		(select {$compilationUnit::selectCount++;})+ SEMICOLON? EOF
	->	^(VT_COMPILATION_UNIT
			useCollatorLevel?
			defineOutput?
			defineMessage*
			defineDominValues*
			defineTarget? 
			select+)
	;

useCollatorLevel
	:	use_key collator_key level_key (identical_key|primary_key|secondary_key|tertiary_key)
		-> ^(VT_COLLATOR_LEVEL identical_key? primary_key? secondary_key? tertiary_key?)
	;

defineOutput
	:	define_output_key ASSIGN graphicModel
		-> graphicModel
	;

defineTarget
@after	{
	isInsideDefineTarget = false;
}	:	define_target_key {isInsideDefineTarget = true;} ASSIGN (nodeType keep_result_key?|select) 
		-> ^(define_target_key nodeType? keep_result_key? select?)
	;

defineMessage
	:	define_message_key (var=VAR_INT|var=VAR_DEC|var=VAR_STRING|var=VAR_BOOL)
	{	if( defineMessageVariableSet.contains($var.text) ) {
			errors.add(errorMessageFactory.createDuplicatedDefineMessageException());
		} else {
			defineMessageVariableSet.add($var.text);
		}	}
		ASSIGN STRING
		-> ^(define_message_key $var STRING)
	;

defineDominValues
	:	define_domain_key values_key domainValues
		-> ^(define_domain_key domainValues)
	;

domainValues
@init	{
	String varName = null;
}
@after	{
	if( defineDomainVariableSet.contains(varName) ) {
		errors.add(errorMessageFactory.createDuplicatedDefineDomainException());
	} else {
		defineDomainVariableSet.add(varName);
	}
}	:	VAR_STRING {varName = $VAR_STRING.text;} ASSIGN! STRING (COMMA! STRING)*
	|	VAR_INT {varName = $VAR_INT.text;} ASSIGN! INT (COMMA! INT)*
	|	VAR_DEC {varName = $VAR_DEC.text;} ASSIGN! (DEC|INT) (COMMA! (DEC|INT))*
	;

select
scope {
	boolean hasByLink;
	boolean hasDoubleStar;
	boolean hasWhereClause;
}
@init {
	$select::hasByLink = false;
	$select::hasDoubleStar = false;
	$select::hasWhereClause = false;
}
@after	{
	if ($select::hasDoubleStar && !$select::hasWhereClause){
		errors.add(errorMessageFactory.createInvalidDoubleStarException());
	}
	if (isInsideDefineTarget == true && $select::hasByLink) {
		errors.add(errorMessageFactory.createDefineTargetWithByLinkException());
	}
	if ($compilationUnit != null && $compilationUnit.size() > 0 && 
		$compilationUnit::selectCount == 0 && !$select::hasByLink &&
		$compilationUnit::hasDefineTarget) {
		errors.add(errorMessageFactory.createDefineTargetWithoutByLinkException());
	}
	if (isgUnitTest && hasErrors()) {
		throw new RecognitionException();
	}
}	:	select_key
	(	STAR
	|	DOUBLE_STAR { $select::hasDoubleStar = true; }
	|	nodeType (COMMA nodeType)*	)
		byLink?
		where?
		executing?
		limitOffset?
		keep_result_key?
		orderBy?
		useCollatorLevel?
	-> ^(select_key 
			STAR? DOUBLE_STAR? nodeType* 
			byLink?
			where?
			executing?
			limitOffset?
			keep_result_key?
			orderBy?
			useCollatorLevel?)
	;

byLink
@after	{
	$select::hasByLink = true;
	if ($compilationUnit != null && $compilationUnit.size() > 0 && 
		$compilationUnit::selectCount == 0 && !$compilationUnit::hasDefineTarget) {
		errors.add(errorMessageFactory.createByLinkWithoutDefineTargetException());
	}
	
}	:	by_link_key byLinkDefinition (COMMA byLinkDefinition)*
	-> ^(by_link_key byLinkDefinition+)
	;

byLinkDefinition
	:	linkType linkDirections
	;

where
@after {
	$select::hasWhereClause = true;
	if ($select::hasByLink) {
		errors.add(errorMessageFactory.createCannotUseWhereWithByLInkException());
	}
}	:	where_key 
		(nodeType booleanExpr)+
	-> ^(where_key ^(VT_GROUP_BY_NODE_TYPE nodeType booleanExpr)+)
	;

limitOffset
	:	limit_key (INT|VAR_INT) 
		offset?
		-> ^(limit_key INT? VAR_INT? offset?)
	;

offset
	:	offset_key^ (INT|VAR_INT)
	;

orderBy
	:	order_by_key^
		orderByGroupNode (COMMA! orderByGroupNode)*
	;

orderByGroupNode
	:	nodeType (propertyReference (COMMA propertyReference)*)?
		-> ^(VT_GROUP_BY_NODE_TYPE nodeType propertyReference*)
	;

propertyReference
	:	property_key propertyName (asc_key|desc_key)?
		-> {$asc_key.tree == null && $desc_key.tree == null}? ^(property_key propertyName ASC_VK)
		->  ^(property_key propertyName asc_key? desc_key?)
	;

booleanExpr
	:	expr ((AND_OPERATOR^|OR_OPERATOR^) NEGATED_OPERATOR? expr)*
	;

expr
	:	property_key propertyName operator valueExpr
		 -> ^(property_key ^(operator propertyName valueExpr)) 
	|	link_key linkType linkDirections numericOperator numericValue
		 -> ^(link_key ^(numericOperator linkType linkDirections numericValue)) 
	|	LEFT_PAREN! booleanExpr RIGHT_PAREN!
	;

linkDirections
	:	LEFT_PAREN linkDirectionOptions RIGHT_PAREN
		-> ^(VT_LINK_DIRECTIONS linkDirectionOptions)
	;

linkDirectionOptions
	:	a_link_side_key COMMA! b_link_side_key COMMA! both_link_side_key
	|	a_link_side_key COMMA! b_link_side_key
	|	a_link_side_key COMMA! both_link_side_key
	|	b_link_side_key COMMA! both_link_side_key
	|	a_link_side_key
	|	b_link_side_key
	|	both_link_side_key
	;

operator
	:	stringOperator
	|	numericOperator
	;

stringOperator
	:	STARTS_WITH
	|	ENDS_WITH
	|	CONTAINS
	|	NOT_CONTAINS
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
	:	null_key
	|	booleanValue
	|	numericValue
	|	stringValue
	;

booleanValue
	:	true_key
	|	false_key
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
@after	{
	if ($select !=null && $select.size() > 0 && !$select::hasByLink) {
		errors.add(errorMessageFactory.createInvalidExecutingException());
	}
}	:	executing_key (INT|VAR_INT|n_times_key) times_key
		-> ^(VT_EXECUTING_TIMES INT? VAR_INT? n_times_key?)
	;

graphicModel
@init{
	StringBuilder sb = new StringBuilder();
}	:	tn=typeName {sb.append($tn.cleanText);} (DOT {sb.append(".");} tn2=typeName {sb.append($tn2.cleanText);})*
	-> GRAPHIC_MODEL_NAME[$start, sb.toString()]
	;

linkType
@init{
	StringBuilder sb = new StringBuilder();
}
	:	tn=typeName {sb.append($tn.cleanText);} (DOT {sb.append(".");} tn2=typeName {sb.append($tn2.cleanText);})*
	-> LINK_TYPE_NAME[$start, sb.toString()]
	;

nodeType
@init{
	StringBuilder sb = new StringBuilder();
}	:	tn=typeName {sb.append($tn.cleanText);} (DOT {sb.append(".");} tn2=typeName {sb.append($tn2.cleanText);})* (DOT STAR)?
	-> {$STAR != null}? NODE_TYPE_NAME_WITH_SUBTYPES[$start, sb.toString()]
	-> NODE_TYPE_NAME[$start, sb.toString()]
	;

typeName returns [String cleanText]
	:	LEFT_SQUARE .+ RIGHT_SQUARE {$cleanText = $text.substring(1, $text.length() - 1);}
	|	ID {$cleanText = $ID.text;}
	;

propertyName
@init{
	String text = "";
}	:	LEFT_SQUARE .+ RIGHT_SQUARE {text = $text.substring(1, $text.length() - 1);} -> PROPERTY_NAME[$start, text]
	|	ID {text = $text;}  -> PROPERTY_NAME[$start, text]
	;

define_output_key
	:	{validateLT(2, SLSoftKeywords.OUTPUT)}?=>  DEFINE ID
	;

define_target_key
@init{
	String text = "";
}	:	{validateLT(2, SLSoftKeywords.TARGET)}?=>  DEFINE ID {text = $text;}
		-> DEFINE_TARGET_VK[$start, text]
	;

define_message_key
@init{
	String text = "";
}	:	{validateLT(2, SLSoftKeywords.MESSAGE)}?=>  DEFINE ID {text = $text;}
		-> DEFINE_MESSAGE_VK[$start, text]
	;

define_domain_key
@init{
	String text = "";
}	:	{validateLT(2, SLSoftKeywords.DOMAIN)}?=>  DEFINE ID {text = $text;}
		-> DEFINE_DOMAIN_VK[$start, text]
	;

order_by_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(SLSoftKeywords.ORDER) && validateLT(2, SLSoftKeywords.BY))}?=>  ID ID {text = $text;}
		-> ORDER_BY_VK[$start, text]
	;

by_link_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(SLSoftKeywords.BY) && validateLT(2, SLSoftKeywords.LINK))}?=>  ID ID {text = $text;}
		-> BY_LINK_VK[$start, text]
	;

keep_result_key
@init{
	String text = "";
}	:	{(validateIdentifierKey(SLSoftKeywords.KEEP) && validateLT(2, SLSoftKeywords.RESULT))}?=>  ID ID {text = $text;}
		-> KEEP_RESULT_VK[$start, text]
	;

asc_key
	:	{(validateIdentifierKey(SLSoftKeywords.ASC))}?=>	ID
		-> ASC_VK[$ID]
	;

desc_key
	:	{(validateIdentifierKey(SLSoftKeywords.DESC))}?=>	ID
		-> DESC_VK[$ID]
	;

select_key
	:	SELECT
	;

limit_key
	:	{(validateIdentifierKey(SLSoftKeywords.LIMIT))}?=>	ID
		-> LIMIT_VK[$ID]
	;

offset_key
	:	{(validateIdentifierKey(SLSoftKeywords.OFFSET))}?=>	ID
		-> OFFSET_VK[$ID]
	;

where_key
	:	{(validateIdentifierKey(SLSoftKeywords.WHERE))}?=>	ID
		-> WHERE_VK[$ID]
	;

property_key
	:	PROPERTY
	;

link_key
	:	{(validateIdentifierKey(SLSoftKeywords.LINK))}?=>	ID
		-> LINK_VK[$ID]
	;

values_key
	:	{(validateIdentifierKey(SLSoftKeywords.VALUES))}?=>	ID
		-> VALUES_VK[$ID]
	;

a_link_side_key
	:	{(validateIdentifierKey(SLSoftKeywords.A))}?=>	ID
		-> A_VK[$ID]
	;

b_link_side_key
	:	{(validateIdentifierKey(SLSoftKeywords.B))}?=>	ID
		-> B_VK[$ID]
	;

both_link_side_key
	:	{(validateIdentifierKey(SLSoftKeywords.BOTH))}?=>	ID
		-> BOTH_VK[$ID]
	;

null_key
	:	{(validateIdentifierKey(SLSoftKeywords.NULL))}?=>	ID
		-> NULL_VK[$ID]
	;

true_key
	:	{(validateIdentifierKey(SLSoftKeywords.TRUE))}?=>	ID
		-> TRUE_VK[$ID]
	;

false_key
	:	{(validateIdentifierKey(SLSoftKeywords.FALSE))}?=>	ID
		-> FALSE_VK[$ID]
	;

executing_key
	:	{(validateIdentifierKey(SLSoftKeywords.EXECUTING))}?=>	ID
		-> EXECUTING_VK[$ID]
	;

times_key
	:	{(validateIdentifierKey(SLSoftKeywords.TIMES))}?=>	ID
		-> TIMES_VK[$ID]
	;

n_times_key
	:	{(validateIdentifierKey(SLSoftKeywords.N))}?=>	ID
		-> N_VK[$ID]
	;

use_key
	:	USE
	;

collator_key
	:	{(validateIdentifierKey(SLSoftKeywords.COLLATOR))}?=>	ID
		-> COLLATOR_VK[$ID]
	;

level_key
	:	{(validateIdentifierKey(SLSoftKeywords.LEVEL))}?=>	ID
		-> LEVEL_VK[$ID]
	;

identical_key
	:	{(validateIdentifierKey(SLSoftKeywords.IDENTICAL))}?=>	ID
		-> IDENTICAL_VK[$ID]
	;

primary_key
	:	{(validateIdentifierKey(SLSoftKeywords.PRIMARY))}?=>	ID
		-> PRIMARY_VK[$ID]
	;

secondary_key
	:	{(validateIdentifierKey(SLSoftKeywords.SECONDARY))}?=>	ID
		-> SECONDARY_VK[$ID]
	;

tertiary_key
	:	{(validateIdentifierKey(SLSoftKeywords.TERTIARY))}?=>	ID
		-> TERTIARY_VK[$ID]
	;

DEFINE
	:	'define'
	;

USE
	:	'use'
	;

SELECT
	:	'select'
	;

STARTS_WITH
	:	'*...'
	;

ENDS_WITH
	:	'...*'
	;

NOT_CONTAINS
	:	'!<*>'
	;

CONTAINS
	:	'<*>'
	;

EQUALS
	:	'=='
	;

GREATER
	:	'>'
	;

LESSER
	:	'<'
	;

GREATER_OR_EQUALS
	:	'>='
	;

LESSER_OR_EQUALS
	:	'<='
	;

NOT_EQUALS
	:	'!='
	;

ASSIGN
	:	'='
	;

DOUBLE_STAR
	:	'**'
	;

STAR
	:	'*'
	;

AND_OPERATOR
	:	'&&'
	;

OR_OPERATOR
	:	'||'
	;

NEGATED_OPERATOR
	:	'!'
	;

LEFT_PAREN
	:	'('
	;

RIGHT_PAREN
	:	')'
	;

LEFT_SQUARE
	:	'['
	;

RIGHT_SQUARE
	:	']'
	;

DOT
	:	'.'
	;

COMMA
	:	','
	;

SEMICOLON
	:	';'
	;

VAR_INT
	:	'#' ID
	;

VAR_DEC
	:	'&' ID
	;

VAR_STRING
	:	'$' ID
	;

VAR_BOOL
	:	'@' ID
	;

PROPERTY
	:	'property'
	;

ID
	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
	;

INT
	:	'0'..'9'+
	;

DEC
	:	('0'..'9')+ DOT ('0'..'9')*
	|	DOT ('0'..'9')+
	;

COMMENT
	:	'//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
	|	'/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
	;

WS
	:
	(	' '
	|	'\t'
	|	'\r'
	|	'\n'	) {$channel=HIDDEN;}
    ;

STRING
	:	'"' ( ESC_SEQ | ~('\\'|'"') )* '"'
	;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
