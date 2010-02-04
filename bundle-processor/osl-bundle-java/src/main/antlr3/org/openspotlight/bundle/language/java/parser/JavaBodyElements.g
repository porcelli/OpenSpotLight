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
tree grammar JavaBodyElements;
options{
    tokenVocab=JavaParser;
    ASTLabelType=CommonTree;
}

@header {
package org.openspotlight.bundle.language.java.parser;
import java.util.Collections;
import org.openspotlight.bundle.language.java.parser.executor.JavaBodyElementsExecutor;
}

@members{
    private JavaBodyElementsExecutor executor;
    public void setExecutor(JavaBodyElementsExecutor executor){
        this.executor = executor;
    } 
    
}


// starting point for parsing a java file
compilationUnit
    :   ^(COMPILATION_UNIT packageDeclaration importDeclaration* typeDeclaration*)
    ;

packageDeclaration
    :   ^(PACKAGE_DECLARATION annotations? PACKAGE qualifiedName)
        { executor.pushToElementStack($packageDeclaration.start); }
    |	  
    ;

importDeclaration
    :   ^(IMPORT_DECLARATION STATIC? STAR? qualifiedName)
        { executor.addToImportedList($qualifiedName.start); }
    ;
    
typeDeclaration
    :   normalClassDeclaration
    |   enumDeclaration
    |   normalInterfaceDeclaration
    |   annotationTypeDeclaration
    ;

modifiers
    :   ^(MODIFIERS modifier*)
    ;

modifier
    :   PUBLIC
    |   PROTECTED
    |   PRIVATE
    |   STATIC
    |   ABSTRACT
    |   FINAL
    |   NATIVE
    |   SYNCHRONIZED
    |   TRANSIENT
    |   VOLATILE
    |   STRICTFP
    ;
    
normalClassDeclaration
    @after{ executor.popFromElementStack(); }
    :   ^(CLASS_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier); }
            modifiers annotations? typeParameters? (normalClassExtends 
        { executor.addExtends(executor.peek(), $normalClassExtends.treeElement); }
            )? (normalClassImplements
        { executor.addImplements(executor.peek(), $normalClassImplements.treeElements); }    
            )? classBody)
    ;

normalClassExtends returns [CommonTree treeElement]
    :    ^(EXTENDS type 
         { $treeElement = $type.treeElement; })
    ;

normalClassImplements returns [List<CommonTree> treeElements]
    :    ^(IMPLEMENTS typeList 
        { $treeElements=$typeList.treeElements; })
    ;

typeParameters
    :   ^(TYPE_PARAMETERS typeParameter+)
    ;

typeParameter
    :   ^(TYPE_PARAMETER Identifier typeParameterExtends?)
    ;

typeParameterExtends
    :   ^(EXTENDS typeBound)
    ;

typeBound
    :   ^(TYPE_BOUND type+)
    ;

enumDeclaration
    @after{ executor.popFromElementStack(); }
    :   ^(ENUM_DECLARATION Identifier
        { executor.pushToElementStack($Identifier); }
            modifiers annotations? (enumDeclarationImplements 
        { executor.addImplements(executor.peek(), $enumDeclarationImplements.treeElements); }    
            )? enumBody)
    ;


enumDeclarationImplements returns [List<CommonTree> treeElements]
    :   ^(IMPLEMENTS typeList 
        { $treeElements=$typeList.treeElements; } )
    ;

enumBody
    :   ^(ENUM_BODY enumConstant* typeBodyDeclaration* RIGHT_CURLY)
    ;

enumConstant
    :   ^(ENUM_CONSTANT_DECLARATION Identifier annotations? arguments? classBody?)
    ;
    
normalInterfaceDeclaration
    @after{ executor.popFromElementStack(); }
    :   ^(INTERFACE_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier); }    
        modifiers annotations? typeParameters? (normalInterfaceDeclarationExtends 
        { executor.addExtends(executor.peek(), $normalInterfaceDeclarationExtends.treeElements); }
         )? interfaceBody)
    ;

normalInterfaceDeclarationExtends returns [List<CommonTree> treeElements]
    :   ^(EXTENDS typeList 
        { $treeElements = $typeList.treeElements; })
    ;

typeList returns [List<CommonTree> treeElements]
    @init{ $treeElements = new ArrayList<CommonTree>(); }
    :   ^(TYPE_LIST (type 
        { $treeElements.add($type.treeElement); } )+)
    ;
    
classBody
    :   ^(CLASS_BODY_DECLARATION typeBodyDeclaration* RIGHT_CURLY)
    ;

interfaceBody
    :   ^(INTERFACE_BODY_DECLARATION typeBodyDeclaration* RIGHT_CURLY)
    ;

typeBodyDeclaration
    @init{ boolean addedToStack=false; }
    @after{ if(addedToStack) { executor.popFromElementStack(); }; }
    :   ^(INITIALIZER_BLOCK STATIC? block 
        { CommonTree blockTree = executor.createBlockAndReturnTree(executor.peek(), $block.start, $STATIC!=null); 
          executor.pushToElementStack(blockTree);
          addedToStack=true; }
           )
    |   ^(CONSTRUCTOR_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier); 
          addedToStack=true; }
            modifiers annotations? typeParameters? formalParameters typeBodyDeclarationThrows? block)
    |   ^(FIELD_DECLARATION modifiers annotations? type (variableDeclarator 
        { executor.addField(executor.peek(),$variableDeclarator.treeElement); }
            )+)
    |   ^(METHOD_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier); 
          addedToStack=true; }
            modifiers annotations? typeParameters? type formalParameters typeBodyDeclarationThrows? defaultValue? block?)
    |   ^(INNER_DECLARATION typeDeclaration)
    ;

typeBodyDeclarationThrows
    :   ^(THROWS type+)
    ;

variableDeclarator returns [CommonTree treeElement]
    :   ^(VARIABLE_DECLARATION_FRAGMENT Identifier 
        { $treeElement=$Identifier; }
           ARRAY_DIMENSION? variableDeclaratorAssign?) 
    ;

variableDeclaratorAssign returns [ExpressionDto info]
    :   ^(ASSIGN variableInitializer 
        { $info=$variableInitializer.info; } )
    ;

variableInitializer returns [ExpressionDto info]
    :   arrayInitializer 
        { $info=$arrayInitializer.info; } 
    |   expression 
        { $info=$expression.info; } 
    ;

arrayInitializer returns [ExpressionDto info]
    @init{ List<ExpressionDto> expressions = new ArrayList<ExpressionDto>(); }
    @after{ $info=executor.createFromArrayInitialization(expressions); }
    :   ^(ARRAY_INITIALIZER (variableInitializer 
        { expressions.add($variableInitializer.info); } )* RIGHT_CURLY)
    ;

type returns [CommonTree treeElement]
    :   ^(ARRAY_TYPE t1=type ARRAY_DIMENSION 
        { $treeElement = $t1.start; } )
    |   ^(QUALIFIED_TYPE t2=type 
        { $treeElement = $t2.start; } 
            (DOT type)+)
    |   ^(PARAMETERIZED_TYPE t4=type 
        { $treeElement = $t4.start; } 
            typeArguments)
    |   ^(WILDCARD_TYPE QUESTION (^(EXTENDS t5=type 
        { $treeElement = $t5.start; } 
             )|^(SUPER t6=type 
        { $treeElement = $t6.start; } ))? )
    |   ^(SIMPLE_TYPE (t7=Identifier 
        { $treeElement = $t7; } 
             |t8=VOID 
        { $treeElement = $t8; } ))
    |   ^(t9=PRIMITIVE_TYPE primitiveType 
        { $treeElement = $t9; } )
    ;

primitiveType
    :   BOOLEAN
    |   CHAR
    |   BYTE
    |   SHORT
    |   INT
    |   LONG
    |   FLOAT
    |   DOUBLE
    ;

typeArguments returns [List<CommonTree> treeElements]
    :   ^(TYPE_ARGUMENTS typeList 
        { $treeElements=$typeList.treeElements; } )
    ;

formalParameters
    :   ^(FORMAL_PARAMETERS singleVariableDeclaration*)
    ;

singleVariableDeclaration
    :   ^(SINGLE_VARIABLE_DECLARATION Identifier modifiers annotations? type THREE_DOTS? ARRAY_DIMENSION?)
    ;

qualifiedName
    :   ^(QUALIFIED_NAME (Identifier|THIS) (DOT Identifier)*)
    ;

// ANNOTATIONS

annotations
    :   ^(ANNOTATIONS annotation+)
    ;

annotation
    :   ^(MARKER_ANNOTATION qualifiedName)
    |   ^(SINGLE_MEMBER_ANNOTATION qualifiedName elementValue)
    |   ^(NORMAL_ANNOTATION qualifiedName elementValuePairs)
    ;

elementValuePairs
    :   ^(MEMBER_VALUE_PAIR elementValuePair+) 
    ;

elementValuePair
    :   ^(ASSIGN Identifier elementValue)
    ;
    
elementValue
    :   expression
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :   ^(ARRAY_INITIALIZER elementValue* RIGHT_CURLY)
    ;

annotationTypeDeclaration
    @after{ executor.popFromElementStack(); }
    :   ^(ANNOTATION_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier); }
            modifiers annotations? annotationTypeBody)
        
    ;
    
annotationTypeBody
    :   ^(ANNOTATION_BODY typeBodyDeclaration* RIGHT_CURLY)
    ;

defaultValue
    :   ^(DEFAULT elementValue)
    ;

// STATEMENTS / BLOCKS

block
    :   ^(BLOCK blockStatement* RIGHT_CURLY)
    ;
    
blockStatement
    :   localVariableDeclaration
    |   ^(INNER_DECLARATION typeDeclaration)
    |   ^(CONSTRUCTOR_INVOCATION typeArguments? arguments)
    |   ^(SUPER_CONSTRUCTOR_INVOCATION expression? typeArguments? arguments)
    |   statement
    ;

localVariableDeclaration
    :   ^(VARIABLE_DECLARATION modifiers annotations? type (variableDeclarator  
        { executor.addLocalVariableDeclaration(executor.peek(), $type.treeElement, $variableDeclarator.treeElement ); }
            )+)
    ;
    
statement
    @init{ executor.createStatement(executor.peek(),$statement.start); 
           executor.pushToElementStack($statement.start); }
    @after{ executor.popFromElementStack(); }
    :   block
    |   EMPTY_STATEMENT
    |   ^(ASSERT expression expression?)
    |   ^(IF expression statement statementElse?)
    |   ^(ENHANCED_FOR localVariableDeclaration expression statement)
    |   ^(FOR ^(FOR_INIT (localVariableDeclaration|expressionList)?) ^(FOR_CONDITION expression?) ^(FOR_UPDATE expressionList?) statement) 
    |   ^(WHILE expression statement)
    |   ^(DO statement ^(WHILE expression))
    |   ^(TRY block catches? statementFinally?)
    |   ^(SWITCH expression switchBlockStatementGroup*)
    |   ^(SYNCHRONIZED expression block)
    |   ^(RETURN expression?)
    |   ^(THROW expression)
    |   ^(BREAK Identifier?)
    |   ^(CONTINUE Identifier?)
    |   ^(STATEMENT_EXPRESSION expression)
    |   ^(LABEL_DECLARE Identifier statement)
    ;

statementElse
    :   ^(ELSE statement)
    ;

statementFinally
    :   ^(FINALLY block)
    ;
    
catches
    :   ^(CATCHES catchClause+)
    ;

catchClause
    :   ^(CATCH formalParameters block)
    ;

switchBlockStatementGroup
    :   ^(SWITCH_BLOCK switchLabel+ blockStatement*)
    ;
    
switchLabel
    :   ^(CASE_CONSTANT expression)
    |   ^(CASE_ENUM Identifier)
    |   DEFAULT
    ;

// EXPRESSIONS

expressionList returns [List<ExpressionDto> expressions]
    @init{ $expressions = new ArrayList<ExpressionDto>(); }
    :   ^(EXPR_LIST (expression 
        { $expressions.add($expression.info); } )+)
    ;

//ExpressionDto[JavaType resultType, SLNode leaf, List<ExpressionDto> participants]
expression returns [ExpressionDto info]
    :   ^(BOOLEAN_EXPRESSION e1=expression 
        { $info=executor.createBooleanExpression($e1.info); } )
    |   ^(EXPRESSION e2=expression 
        { $info=$e2.info; } )
    |   ^(PARENTHESIZED_EXPRESSION e3=expression
        { $info=$e3.info; } )
    |   ^(assignmentOperator e4=expression e5=expression
        { $info=executor.createAssignExpression($e4.info, $e5.info); } )
    |   ^(CONDITIONAL_EXPRESSION e6=expression e7=expression e8=expression
        { $info=executor.createConditionalExpression($e6.info, $e7.info, $e8.info); } )
    |   ^(DOUPLE_PIPE e9=expression e10=expression 
        { $info=executor.createBooleanExpression($e9.info,$e10.info); } )
    |   ^(DOUBLE_AMPERSAND e11=expression e12=expression 
        { $info=executor.createBooleanExpression($e11.info,$e12.info); } )
    |   ^(PIPE e13=expression e14=expression 
        { $info=executor.createBinaryExpression($e13.info,$e14.info); } )
    |   ^(CIRCUMFLEX e15=expression e16=expression 
        { $info=executor.createBinaryExpression($e15.info,$e16.info); } )
    |   ^(AMPERSAND e17=expression e18=expression 
        { $info=executor.createBinaryExpression($e17.info,$e18.info); } )
    |   ^(EQUALS e19=expression e20=expression 
        { $info=executor.createBooleanExpression($e19.info,$e20.info); } )
    |   ^(EXCLAMATION_EQUALS e21=expression e22=expression 
        { $info=executor.createBooleanExpression($e21.info,$e22.info); } )
    |   ^(INSTANCEOF e23=expression t1=type 
        { $info=executor.createBooleanExpression($e23.info,$t1.treeElement); } )
    |   ^(relationalOp e24=expression e25=expression 
        { $info=executor.createBooleanExpression($e24.info,$e25.info); } )
    |   ^(shiftOp e26=expression e27=expression 
        { $info=executor.createNumberExpression($e26.info,$e27.info); } )
    |   ^(PLUS e28=expression e29=expression 
        { $info=executor.createPlusExpression($e28.info,$e29.info); } )
    |   ^(MINUS e30=expression e31=expression 
        { $info=executor.createNumberExpression($e30.info,$e31.info); } )
    |   ^(STAR e32=expression e33=expression 
        { $info=executor.createNumberExpression($e32.info,$e33.info); } )
    |   ^(SLASH e34=expression e35=expression 
        { $info=executor.createNumberExpression($e34.info,$e35.info); } )
    |   ^(PERCENT e36=expression e37=expression 
        { $info=executor.createNumberExpression($e36.info,$e37.info); } )
    |   ^(PREFIX_EXPRESSION e38=expression 
        { $info=executor.createNumberExpression($e38.info); } )
    |   ^(POSTFIX_EXPRESSION e39=expression (DOUBLE_PLUS|DOUBLE_MINUS) 
        { $info=executor.createNumberExpression($e39.info); } )
    |   ^(UNARY_PLUS e40=expression 
        { $info=executor.createNumberExpression($e40.info); } )
    |   ^(UNARY_MINUS e41=expression 
        { $info=executor.createNumberExpression($e41.info); } )
    |   ^(DOUBLE_PLUS e42=expression 
        { $info=executor.createNumberExpression($e42.info); } )
    |   ^(DOUBLE_MINUS e43=expression 
        { $info=executor.createNumberExpression($e43.info); } )
    |   ^(TILDE e44=expression 
        { $info=executor.createBooleanExpression($e44.info); } )
    |   ^(EXCLAMATION e45=expression 
        { $info=executor.createBooleanExpression($e45.info); } )
    |   ^(CAST_TYPE t2=type e46=expression 
        { $info=executor.createCastExpression($e46.info,$t2.treeElement); } )
    |   ^(CAST_EXPRESSION e47=expression e48=expression 
        { $info=executor.createCastExpression($e47.info,$e48.info); } )

    |   ^(SUPER_CONSTRUCTOR_INVOCATION e48=expression? a1=arguments 
        { $info=executor.createSuperConstructorExpression($e48.info,$a1.expressions); } )
    |   ^(SUPER_METHOD_INVOCATION e49=expression? DOT id1=Identifier ta1=typeArguments? a2=arguments 
        { $info=executor.createSuperInvocationExpression($e49.info,$ta1.treeElements,$a2.expressions,$id1.text); } )
    |   ^(SUPER_FIELD_ACCESS e50=expression? DOT id2=Identifier 
        { $info=executor.createSuperFieldExpression($e50.info,$id1.text); } )
    |   ^(TYPE_LITERAL t3=type DOT CLASS 
        { $info=executor.createTypeLiteralExpression($t3.treeElement); } )
    |   ^(THIS_EXPRESSION (e51=expression DOT)? THIS 
        { $info=executor.createThisExpression($e51.info); } )
    |   ^(ARRAY_ACCESS e52=expression? dimensionValue 
        { $info=executor.createArrayExpression($e52.info); } )
    |   ^(CLASS_INSTANCE_CREATION (e53=expression DOT)? t4=type a2=arguments acd1=anonymousClassDeclaration? 
        { $info=executor.createClassInstanceExpression($e53.info, $t4.treeElement, $a2.expressions, $acd1.start); } )
    |   ^(ARRAY_CREATION t5=type dimensionValue+ arrayInitializer? 
        { $info=executor.createArrayExpression($t5.treeElement,$arrayInitializer.info); } )
    |   ^(QUALIFIED_NAME
        { StringBuilder sb = new StringBuilder(); }
            (id2=Identifier 
            { sb.append($id2.text); }
            |THIS
            { sb.append($THIS.text); }
            |e54=expression) (DOT id3=Identifier
            { sb.append('.');
              sb.append($id3.text); } )*
        { $info=executor.createExpressionFromQualified(sb.toString(),$e54.info); } )
    |   ^(METHOD_INVOCATION (e55=expression DOT)? id4=Identifier ta2=typeArguments? a3=arguments 
        { $info=executor.createMethodInvocation($e55.info,$id4.text,$ta2.treeElements,$a3.expressions); } )
    
    |   ^(INTEGER_LITERAL integerLiteral 
        { $info=executor.createIntegerLiteral(); } )
    |   ^(FLOATING_POINT_LITERAL FloatingPointLiteral 
        { $info=executor.createFloatLiteral(); } )
    |   ^(CHARACTER_LITERAL StringLiteral 
        { $info=executor.createCharLiteral(); } )
    |   ^(STRING_LITERAL StringLiteral 
        { $info=executor.createStringLiteral(); } )
    |   ^(BOOLEAN_LITERAL booleanLiteral 
        { $info=executor.createBooleanLiteral(); } )
    |   ^(NULL_LITERAL NULL 
        { $info=executor.createNullLiteral(); } )
    ;

anonymousClassDeclaration
    @after{ executor.popFromElementStack(); }
    @init{ executor.pushToElementStack($anonymousClassDeclaration.start); }
	:	^(ANONYMOUS_CLASS_DECLARATION classBody)
	;

assignmentOperator
    :   ASSIGN
    |   PLUS_ASSIGN
    |   MINUS_ASSIGN
    |   STAR_ASSIGN
    |   SLASH_ASSIGN
    |   AMPERSAND_ASSIGN
    |   PIPE_ASSIGN
    |   CIRCUMFLEX_ASSIGN
    |   PERCENT_ASSIGN
    |   LEFT_SHIFT_ASSIGN
    |   UNSIGNED_RIGHT_SHIFT_ASSIGN
    |   SIGNED_RIGHT_SHIFT_ASSIGN
    ;

relationalOp
    :   LESS_EQUALS
    |   GREATER_EQUALS
    |   LESS
    |   GREATER 
    ;

shiftOp
    :   LEFT_SHIFT
    |   UNSIGNED_RIGHT_SHIFT
    |   SIGNED_RIGHT_SHIFT
    ;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   TRUE
    |   FALSE
    ;

dimensionValue
    :   ^(DIMENSION expression?)
    ;

arguments returns [List<ExpressionDto> expressions]
    @init{ $expressions=Collections.emptyList(); }
    :   ^(ARGUMENTS (expressionList 
        {$expressions=$expressionList.expressions; } )?)
    ;
