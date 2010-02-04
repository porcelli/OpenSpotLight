/*
* OpenSpotLight - Open Source IT Governance Platform
*
* Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
* or third-party contributors as indicated by the @author tags or express
* copyright attribution statements applied by the authors. All third-party
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
* See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this distribution; if not, write to:
* Free Software Foundation, Inc.
* 51 Franklin Street, Fifth Floor
* Boston, MA 02110-1301 USA
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
* Boston, MA 02110-1301 USA
*/
tree grammar JavaPublicElementsTree;
options{
    tokenVocab=JavaParser;
    ASTLabelType=CommonTree;
}

@header {
package org.openspotlight.bundle.language.java.parser;
import org.openspotlight.bundle.language.java.parser.executor.JavaPublicElemetsTreeExecutor;
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
    private Stack<SLNode> stack = new Stack<SLNode>();
    private JavaPublicElemetsTreeExecutor executor;
    
    public void setExecutor(JavaPublicElemetsTreeExecutor executor){
        this.executor = executor;
    }

    public JavaPublicElemetsTreeExecutor getExecutor(){
        return this.executor;
    }
}
// starting point for parsing a java file
compilationUnit
    :    ^(COMPILATION_UNIT packageDeclaration importDeclaration* typeDeclaration*)
    ;
packageDeclaration
    :    ^(PACKAGE_DECLARATION annotations? PACKAGE qualifiedName)
	       { stack.push(executor.packageDeclaration($qualifiedName.name,$packageDeclaration.start)); }
    |    { stack.push(executor.packageDeclaration(null,$packageDeclaration.start)); }
    ;
importDeclaration
    :    ^(IMPORT_DECLARATION STATIC? STAR? qualifiedName)
	       { SLNode node = executor.importDeclaration(stack.peek(),$STATIC.text!=null,$STAR.text!=null,$qualifiedName.name); 
	         executor.addLineReference($qualifiedName.start, node);   }
    ;
typeDeclaration
    :    normalClassDeclaration
    |    enumDeclaration
    |    normalInterfaceDeclaration
    |    annotationTypeDeclaration
    ;

modifiers returns [List<JavaModifier> modifiersResultList]
    @init  { $modifiersResultList=new ArrayList<JavaModifier>(); }
    :    ^(MODIFIERS (modifier 
         { $modifiersResultList.add($modifier.modifierResult); } )*)
    ;

modifier returns [JavaModifier modifierResult]
    :    (PUBLIC
    |    PROTECTED
    |    PRIVATE
    |    STATIC
    |    ABSTRACT
    |    FINAL
    |    NATIVE
    |    SYNCHRONIZED
    |    TRANSIENT
    |    VOLATILE
    |    STRICTFP) 
         { $modifierResult = executor.getModifier($modifier.text); }
    ;

normalClassDeclaration
    @after{ stack.pop(); }
    :    ^(CLASS_DECLARATION Identifier modifiers annotations? typeParameters? normalClassExtends? normalClassImplements?
	       { SLNode node = executor.createJavaClass(stack.peek(), $Identifier.text, $modifiers.modifiersResultList, 
	               $annotations.resultList, $normalClassExtends.typeResult, 
	               $normalClassImplements.typeListReturn,$typeParameters.resultList);
	         executor.addLineReference($Identifier, node);
	         stack.push(node); } 
	classBody)
    ;
normalClassExtends returns [JavaType typeResult]
    :    ^(EXTENDS type 
         { $typeResult = $type.typeReturn; })
    ;
normalClassImplements returns [List<JavaType> typeListReturn]
    :    ^(IMPLEMENTS typeList 
         { $typeListReturn=$typeList.resultList; })
    ;
typeParameters returns [List<TypeParameterDto> resultList]
    @init{ $resultList = new ArrayList<TypeParameterDto>(); }
    :    ^(TYPE_PARAMETERS (typeParameter 
         { $resultList.add($typeParameter.result); } )+)
    ;
typeParameter returns [TypeParameterDto result]
    :    ^(TYPE_PARAMETER Identifier typeParameterExtends? 
         { $result = new TypeParameterDto($Identifier.text,$typeParameterExtends.resultList); })
    ;
typeParameterExtends returns [List<JavaType> resultList]
    :    ^(EXTENDS typeBound 
         { $resultList = $typeBound.resultList; })
    ;
typeBound returns [List<JavaType> resultList]
    @init{ $resultList = new ArrayList<JavaType>(); }
    :    ^(TYPE_BOUND (type 
         { $resultList.add($type.typeReturn); } )+)
    ;
enumDeclaration
    @after{ stack.pop(); }
    :    ^(ENUM_DECLARATION Identifier modifiers annotations? enumDeclarationImplements?
	       { SLNode node = executor.createEnum(stack.peek(), $Identifier.text, 
	                                $modifiers.modifiersResultList, $annotations.resultList, $enumDeclarationImplements.resultList);
           executor.addLineReference($Identifier,node);
           stack.push(node); }
	       enumBody)
    ;
enumDeclarationImplements returns [List<JavaType> resultList]
    @init{ $resultList = new ArrayList<JavaType>(); }
    :    ^(IMPLEMENTS typeList 
         { $resultList = $typeList.resultList; })
    ;
enumBody
    :    ^(ENUM_BODY enumConstant* typeBodyDeclaration* RIGHT_CURLY)
    ;
enumConstant
    :    ^(ENUM_CONSTANT_DECLARATION Identifier annotations? arguments? classBody?)
    ;
normalInterfaceDeclaration
    @after{ stack.pop(); }
    :    ^(INTERFACE_DECLARATION Identifier modifiers annotations? typeParameters? normalInterfaceDeclarationExtends? interfaceBody)
	       { SLNode node = executor.createInterface(stack.peek(), $Identifier.text, $modifiers.modifiersResultList, 
	                                  $annotations.resultList, $normalInterfaceDeclarationExtends.resultList,$typeParameters.resultList);
	         executor.addLineReference($Identifier, node);
	         stack.push(node); }
    ;
normalInterfaceDeclarationExtends returns [List<JavaType> resultList]
    @init{ $resultList = new ArrayList<JavaType>(); }
    :    ^(EXTENDS typeList
         { $resultList = $typeList.resultList; })
    ;
typeList returns [List<JavaType> resultList]
    @init{ resultList=new ArrayList<JavaType>(); } 
    :    ^(TYPE_LIST type+ 
         { $resultList.add($type.typeReturn); })
    ;
classBody
    :    ^(CLASS_BODY_DECLARATION typeBodyDeclaration* RIGHT_CURLY)
    ;
interfaceBody
    :    ^(INTERFACE_BODY_DECLARATION typeBodyDeclaration* RIGHT_CURLY)
    ;
typeBodyDeclaration
    :    ^(INITIALIZER_BLOCK STATIC? block)
    |    ^(CONSTRUCTOR_DECLARATION Identifier modifiers annotations? typeParameters? formalParameters typeBodyDeclarationThrows? block)
	       { SLNode node = executor.createMethodConstructorDeclaration(stack.peek(), $Identifier.text, $modifiers.modifiersResultList, 
	                                   $formalParameters.resultList, $annotations.resultList,  $typeBodyDeclarationThrows.resultList); 
	         executor.addLineReference( $Identifier,node); }
    |    ^(FIELD_DECLARATION 
	       { List<VariableDeclarationDto> variables = new ArrayList<VariableDeclarationDto>();
	         List<CommonTree> treeItems = new ArrayList<CommonTree>(); }
	       modifiers annotations? type (variableDeclarator 
	       { variables.add($variableDeclarator.newVar); 
	         treeItems.add($variableDeclarator.treeItem); })+)
	       { List<SLNode> nodes = executor.createFieldDeclaration(stack.peek(), $modifiers.modifiersResultList, 
	                     $annotations.resultList, $type.typeReturn, variables); 
	         for(int i=0,size=nodes.size();i<size;i++){
	             executor.addLineReference(treeItems.get(i),nodes.get(i));
	         }
	       }
    |    ^(METHOD_DECLARATION Identifier modifiers annotations? typeParameters? type formalParameters typeBodyDeclarationThrows? defaultValue? block?)
	       { SLNode node = executor.createMethodDeclaration(stack.peek(), $Identifier.text, $modifiers.modifiersResultList, 
	                 $formalParameters.resultList,$annotations.resultList, $type.typeReturn, $typeBodyDeclarationThrows.resultList); 
	         executor.addLineReference($Identifier,node); }
    |    ^(INNER_DECLARATION typeDeclaration)
    ;
typeBodyDeclarationThrows returns [List<JavaType> resultList]
    @init { resultList=new ArrayList<JavaType>(); } 
    :    ^(THROWS type+ {$resultList.add($type.typeReturn);})
    ;
variableDeclarator returns [VariableDeclarationDto newVar, CommonTree treeItem]
    :    ^(VARIABLE_DECLARATION_FRAGMENT Identifier ARRAY_DIMENSION? variableDeclaratorAssign? 
         { $newVar = new VariableDeclarationDto($Identifier.text,null,null,null,$ARRAY_DIMENSION.text, $Identifier);
	         $treeItem = $Identifier; })
    ;
variableDeclaratorAssign
    :    ^(ASSIGN variableInitializer)
    ;
variableInitializer
    :    arrayInitializer
    |    expression
    ;
arrayInitializer
    :    ^(ARRAY_INITIALIZER variableInitializer* RIGHT_CURLY)
    ;
type returns [JavaType typeReturn]
    :    ^(ARRAY_TYPE tp0=type ARRAY_DIMENSION) 
         { $typeReturn = executor.findArrayType($tp0.typeReturn, $ARRAY_DIMENSION.text); 
           executor.addLineReference($tp0.start,$typeReturn); }
    |    ^(QUALIFIED_TYPE  tp1=type 
	       { List<JavaType> types = new ArrayList<JavaType>(); 
	         types.add($tp1.typeReturn); } 
	       (DOT tp2=type 
	       { types.add($tp2.typeReturn); } )+) 
	       { $typeReturn = executor.findByQualifiedTypes(types); 
	         executor.addLineReference($tp1.start,$typeReturn);}
    |    ^(PARAMETERIZED_TYPE tp3=type typeArguments 
         { $typeReturn = executor.findParamerizedType($tp3.typeReturn,$typeArguments.resultList); 
           executor.addLineReference($tp3.start,$typeReturn); } )
    |    ^(WILDCARD_TYPE QUESTION (^(EXTENDS tp4=type 
         { $typeReturn = executor.findExtendsParameterizedType($tp4.typeReturn); 
           executor.addLineReference($tp4.start,$typeReturn); })
         |^(SUPER tp5=type 
         { $typeReturn = executor.findSuperParameterizedType($tp5.typeReturn); 
           executor.addLineReference($tp5.start,$typeReturn); }))? )
    |    ^(SIMPLE_TYPE (Identifier 
	       { $typeReturn = executor.findSimpleType($Identifier.text);
	         executor.addLineReference($Identifier,$typeReturn);} 
	  |    VOID 
	       { $typeReturn = executor.findVoidType();
	         executor.addLineReference($VOID,$typeReturn);}))
    |    ^(PRIMITIVE_TYPE primitiveType
	       { $typeReturn = executor.findPrimitiveType($primitiveType.text);
	         executor.addLineReference($PRIMITIVE_TYPE,$typeReturn); })
    ;
primitiveType
    :    BOOLEAN
    |    CHAR
    |    BYTE
    |    SHORT
    |    INT
    |    LONG
    |    FLOAT
    |    DOUBLE
    ;
typeArguments returns [List<JavaType> resultList]
    :    ^(TYPE_ARGUMENTS typeList 
         { $resultList=$typeList.resultList; })
    ;
formalParameters returns [List<VariableDeclarationDto> resultList]
    @init{ $resultList = new ArrayList<VariableDeclarationDto>(); }
    :    ^(FORMAL_PARAMETERS singleVariableDeclaration* 
    { $resultList.add($singleVariableDeclaration.result); })
    ;
singleVariableDeclaration returns [VariableDeclarationDto result]
    :    ^(SINGLE_VARIABLE_DECLARATION Identifier modifiers annotations? type THREE_DOTS? ARRAY_DIMENSION? 
	       { $result=new VariableDeclarationDto($Identifier.text, $modifiers.modifiersResultList, 
	                      $type.typeReturn, $THREE_DOTS.text, $ARRAY_DIMENSION.text, $Identifier);})
    ;
qualifiedName returns [String name]
    @init{ StringBuilder sb = new StringBuilder(); }
    @after{ $name = sb.toString(); }
    :    ^(QUALIFIED_NAME (id1=Identifier
	       { sb.append($id1.text); }
	  |    THIS
	       { sb.append("this"); }) 
	       (DOT id2=Identifier 
	       { sb.append('.');
	         sb.append($id2.text);})*)
    ;

annotations returns [List<JavaType> resultList]
    @init{ $resultList = new LinkedList<JavaType>(); }
    :    ^(ANNOTATIONS (annotation 
         { $resultList.add($annotation.typeNode);} )+)
    ;
annotation returns [ JavaType typeNode]
    :    (^(MARKER_ANNOTATION q1=qualifiedName)
		     { $typeNode=executor.resolveAnnotation($q1.name); 
			     executor.addLineReference($q1.start, $typeNode); }
	  |    ^(SINGLE_MEMBER_ANNOTATION q2=qualifiedName elementValue) 
	       { $typeNode=executor.resolveAnnotation($q2.name);  
           executor.addLineReference($q2.start, $typeNode); }
	  |    ^(NORMAL_ANNOTATION q3=qualifiedName elementValuePairs)
	       { $typeNode=executor.resolveAnnotation($q3.name);  
           executor.addLineReference($q3.start, $typeNode); }
	)
	
    ;
elementValuePairs
    :    ^(MEMBER_VALUE_PAIR elementValuePair+)
    ;
elementValuePair
    :    ^(ASSIGN Identifier elementValue)
    ;
elementValue
    :    expression
    |    annotation
    |    elementValueArrayInitializer
    ;
elementValueArrayInitializer
    :    ^(ARRAY_INITIALIZER elementValue* RIGHT_CURLY)
    ;
annotationTypeDeclaration
    :    ^(ANNOTATION_DECLARATION Identifier modifiers annotations? annotationTypeBody
         { SLNode node = executor.createAnnotation(stack.peek(), $Identifier.text, $modifiers.modifiersResultList, 
                                    $annotations.resultList);
           executor.addLineReference($Identifier, node);
           stack.push(node); } )
    ;
      
    
    
annotationTypeBody
    :    ^(ANNOTATION_BODY typeBodyDeclaration* RIGHT_CURLY)
    ;
defaultValue
    :    ^(DEFAULT elementValue)
    ;
// STATEMENTS / BLOCKS
block
    :    ^(BLOCK blockStatement* RIGHT_CURLY)
    ;
blockStatement
    :    localVariableDeclaration
    |    ^(INNER_DECLARATION typeDeclaration)
    |    ^(CONSTRUCTOR_INVOCATION typeArguments? arguments)
    |    ^(SUPER_CONSTRUCTOR_INVOCATION expression? typeArguments? arguments)
    |    statement
    ;
localVariableDeclaration
    :    ^(VARIABLE_DECLARATION modifiers annotations? type variableDeclarator+)
    ;
statement
    :    block
    |    EMPTY_STATEMENT
    |    ^(ASSERT expression expression?)
    |    ^(IF expression statement statementElse?)
    |    ^(ENHANCED_FOR localVariableDeclaration expression statement)
    |    ^(FOR ^(FOR_INIT (localVariableDeclaration|expressionList)?) ^(FOR_CONDITION expression?) ^(FOR_UPDATE expressionList?) statement)
    |    ^(WHILE expression statement)
    |    ^(DO statement ^(WHILE expression))
    |    ^(TRY block catches? statementFinally?)
    |    ^(SWITCH expression switchBlockStatementGroup*)
    |    ^(SYNCHRONIZED expression block)
    |    ^(RETURN expression?)
    |    ^(THROW expression)
    |    ^(BREAK Identifier?)
    |    ^(CONTINUE Identifier?)
    |    ^(STATEMENT_EXPRESSION expression)
    |    ^(LABEL_DECLARE Identifier statement)
    ;
statementElse
    :    ^(ELSE statement)
    ;
statementFinally
    :    ^(FINALLY block)
    ;
catches
    :    ^(CATCHES catchClause+)
    ;
catchClause
    :    ^(CATCH formalParameters block)
    ;
switchBlockStatementGroup
    :    ^(SWITCH_BLOCK switchLabel+ blockStatement*)
    ;
switchLabel
    :    ^(CASE_CONSTANT expression)
    |    ^(CASE_ENUM Identifier)
    |    DEFAULT
    ;
// EXPRESSIONS
expressionList
    :    ^(EXPR_LIST expression+)
    ;
expression
    :    ^(BOOLEAN_EXPRESSION expression)
    |    ^(EXPRESSION expression)
    |    ^(PARENTHESIZED_EXPRESSION expression)
    |    ^(assignmentOperator expression expression)
    |    ^(CONDITIONAL_EXPRESSION expression expression expression)
    |    ^(DOUPLE_PIPE expression expression)
    |    ^(DOUBLE_AMPERSAND expression expression)
    |    ^(PIPE expression expression)
    |    ^(CIRCUMFLEX expression expression)
    |    ^(AMPERSAND expression expression)
    |    ^(EQUALS expression expression)
    |    ^(EXCLAMATION_EQUALS expression expression)
    |    ^(INSTANCEOF expression type)
    |    ^(relationalOp expression expression)
    |    ^(shiftOp expression expression)
    |    ^(PLUS ex1=expression expression)
    |    ^(MINUS expression expression)
    |    ^(STAR expression expression)
    |    ^(SLASH expression expression)
    |    ^(PERCENT expression expression)
    |    ^(PREFIX_EXPRESSION expression)
    |    ^(POSTFIX_EXPRESSION expression (DOUBLE_PLUS|DOUBLE_MINUS))
    |    ^(UNARY_PLUS expression)
    |    ^(UNARY_MINUS expression)
    |    ^(DOUBLE_PLUS expression)
    |    ^(DOUBLE_MINUS expression)
    |    ^(TILDE expression)
    |    ^(EXCLAMATION expression)
    |    ^(CAST_TYPE type expression)
    |    ^(CAST_EXPRESSION expression expression)
    |    ^(SUPER_CONSTRUCTOR_INVOCATION expression? arguments)
    |    ^(SUPER_METHOD_INVOCATION expression? DOT Identifier typeArguments? arguments)
    |    ^(SUPER_FIELD_ACCESS expression? DOT Identifier)
    |    ^(TYPE_LITERAL type DOT CLASS)
    |    ^(THIS_EXPRESSION (expression DOT)? THIS)
    |    ^(ARRAY_ACCESS expression? dimensionValue)
    |    ^(CLASS_INSTANCE_CREATION (expression DOT)? superType1=type arguments (anonymousClassDeclaration[superType1.typeReturn]  
         { executor.addLineReference($anonymousClassDeclaration.start,$anonymousClassDeclaration.typeElement); } )?)
    |    ^(ARRAY_CREATION type dimensionValue+ arrayInitializer?)
    |    ^(QUALIFIED_NAME (Identifier|THIS|expression) (DOT Identifier)*)
    |    ^(METHOD_INVOCATION (expression DOT)? Identifier typeArguments? arguments)
    |    ^(INTEGER_LITERAL integerLiteral)
    |    ^(FLOATING_POINT_LITERAL FloatingPointLiteral)
    |    ^(CHARACTER_LITERAL StringLiteral)
    |    ^(STRING_LITERAL StringLiteral)
    |    ^(BOOLEAN_LITERAL booleanLiteral)
    |    ^(NULL_LITERAL NULL)
    ;
anonymousClassDeclaration [JavaType superType] returns [JavaType typeElement]
@init{ $typeElement = executor.createAnonymousClass(stack.peek(),$superType); 
       stack.push($typeElement); }
@after{ stack.pop(); }
    :    ^(ANONYMOUS_CLASS_DECLARATION classBody)
    ;
assignmentOperator
    :    ASSIGN
    |    PLUS_ASSIGN
    |    MINUS_ASSIGN
    |    STAR_ASSIGN
    |    SLASH_ASSIGN
    |    AMPERSAND_ASSIGN
    |    PIPE_ASSIGN
    |    CIRCUMFLEX_ASSIGN
    |    PERCENT_ASSIGN
    |    LEFT_SHIFT_ASSIGN
    |    UNSIGNED_RIGHT_SHIFT_ASSIGN
    |    SIGNED_RIGHT_SHIFT_ASSIGN
    ;
relationalOp
    :    LESS_EQUALS
    |    GREATER_EQUALS
    |    LESS
    |    GREATER
    ;
shiftOp
    :    LEFT_SHIFT
    |    UNSIGNED_RIGHT_SHIFT
    |    SIGNED_RIGHT_SHIFT
    ;
integerLiteral
    :    HexLiteral
    |    OctalLiteral
    |    DecimalLiteral
    ;
booleanLiteral
    :    TRUE
    |    FALSE
    ;
dimensionValue
    :    ^(DIMENSION expression?)
    ;
arguments
    :    ^(ARGUMENTS expressionList?)
    ;