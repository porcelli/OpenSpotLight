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
import org.openspotlight.bundle.language.java.parser.executor.JavaBodyElementsExecutor;
import org.openspotlight.bundle.language.java.parser.executor.JavaModifier;
import org.openspotlight.bundle.language.java.parser.executor.VariableDeclarationDto;
import org.openspotlight.bundle.language.java.metamodel.node.*;
import org.openspotlight.graph.SLNode;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;
import org.openspotlight.bundle.language.java.parser.executor.TypeParameterDto;
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
        { executor.pushToElementStack($qualifiedName.start); }
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
        { executor.pushToElementStack(executor.createBlockAndReturnTree(executor.peek(), $STATIC!=null)); 
          addedToStack=true; }
           )
    |   ^(CONSTRUCTOR_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier)); 
          addedToStack=true; }
            modifiers annotations? typeParameters? formalParameters typeBodyDeclarationThrows? block)
    |   ^(FIELD_DECLARATION modifiers annotations? type (variableDeclarator 
        { executor.addField(executor.peek(),$variableDeclarator.treeElement); }
            )+)
    |   ^(METHOD_DECLARATION Identifier 
        { executor.pushToElementStack($Identifier)); 
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

variableDeclaratorAssign
    :   ^(ASSIGN variableInitializer)
    ;

variableInitializer
    :   arrayInitializer
    |   expression
    ;

arrayInitializer
    :   ^(ARRAY_INITIALIZER variableInitializer* RIGHT_CURLY)
    ;

type returns [CommonTree treeElement]
    :   ^(ARRAY_TYPE t1=type ARRAY_DIMENSION 
        { $treeElement = $t1.start; } )
    |   ^(QUALIFIED_TYPE t2=type 
        { $treeElement = $t2.start; } 
            (DOT t3=type)+)
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

typeArguments
    :   ^(TYPE_ARGUMENTS typeList)
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
    :   ^(VARIABLE_DECLARATION modifiers annotations? type variableDeclarator+)
    ;
    
statement
    :   block
    |	EMPTY_STATEMENT
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

expressionList
    :   ^(EXPR_LIST expression+)
    ;

expression
    :   ^(BOOLEAN_EXPRESSION expression)
    |   ^(EXPRESSION expression)
    |   ^(PARENTHESIZED_EXPRESSION expression)
    |   ^(assignmentOperator expression expression)
    |   ^(CONDITIONAL_EXPRESSION expression expression expression)
    |   ^(DOUPLE_PIPE expression expression)
    |   ^(DOUBLE_AMPERSAND expression expression)
    |   ^(PIPE expression expression)
    |   ^(CIRCUMFLEX expression expression)
    |   ^(AMPERSAND expression expression)
    |   ^(EQUALS expression expression)
    |   ^(EXCLAMATION_EQUALS expression expression)
    |   ^(INSTANCEOF expression type)
    |   ^(relationalOp expression expression)
    |   ^(shiftOp expression expression)
    |   ^(PLUS ex1=expression expression)
    |   ^(MINUS expression expression)
    |   ^(STAR expression expression)
    |   ^(SLASH expression expression)
    |   ^(PERCENT expression expression)
    |   ^(PREFIX_EXPRESSION expression)
    |   ^(POSTFIX_EXPRESSION expression (DOUBLE_PLUS|DOUBLE_MINUS))
    |   ^(UNARY_PLUS expression)
    |   ^(UNARY_MINUS expression)
    |   ^(DOUBLE_PLUS expression)
    |   ^(DOUBLE_MINUS expression)
    |   ^(TILDE expression)
    |   ^(EXCLAMATION expression)
    |   ^(CAST_TYPE type expression)
    |   ^(CAST_EXPRESSION expression expression)

    |   ^(SUPER_CONSTRUCTOR_INVOCATION expression? arguments)
    |   ^(SUPER_METHOD_INVOCATION expression? DOT Identifier typeArguments? arguments)
    |   ^(SUPER_FIELD_ACCESS expression? DOT Identifier)
    |   ^(TYPE_LITERAL type DOT CLASS)
    |   ^(THIS_EXPRESSION (expression DOT)? THIS)
    |   ^(ARRAY_ACCESS expression? dimensionValue)
    |   ^(CLASS_INSTANCE_CREATION (expression DOT)? type arguments anonymousClassDeclaration?)
    |   ^(ARRAY_CREATION type dimensionValue+ arrayInitializer?)
    |   ^(QUALIFIED_NAME (Identifier|THIS|expression) (DOT Identifier)*)
    |   ^(METHOD_INVOCATION (expression DOT)? Identifier typeArguments? arguments)
    |   ^(INTEGER_LITERAL integerLiteral)
    |   ^(FLOATING_POINT_LITERAL FloatingPointLiteral)
    |   ^(CHARACTER_LITERAL StringLiteral)
    |   ^(STRING_LITERAL StringLiteral)
    |   ^(BOOLEAN_LITERAL booleanLiteral)
    |   ^(NULL_LITERAL NULL)
    ;

anonymousClassDeclaration
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

arguments
    :   ^(ARGUMENTS expressionList?)
    ;
