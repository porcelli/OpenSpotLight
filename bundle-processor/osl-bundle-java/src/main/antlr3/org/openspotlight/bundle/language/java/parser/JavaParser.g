/*
 [The "BSD licence"]
 Copyright (c) 2007-2008 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */
parser grammar JavaParser;
options {tokenVocab=JavaLexer; TokenLabelType=SLCommonToken;backtrack=true; memoize=true; output=AST;}

tokens {
    ANNOTATION_BODY;
    ANNOTATION_DECLARATION;
    ANNOTATIONS;
    ANONYMOUS_CLASS_DECLARATION;
    ARGUMENTS;
    ARRAY_ACCESS;
    ARRAY_CREATION;
    ARRAY_DIMENSION;
    ARRAY_INITIALIZER;
    ARRAY_TYPE;
    BLOCK;
    BOOLEAN_EXPRESSION;
    BOOLEAN_LITERAL;
    CASE_CONSTANT;
    CASE_ENUM;
    CAST_EXPRESSION;
    CAST_TYPE;
    CATCHES;
    CHARACTER_LITERAL;
    CLASS_BODY_DECLARATION;
    CLASS_DECLARATION;
    CLASS_INSTANCE_CREATION;
    COMPILATION_UNIT;
    CONDITIONAL_EXPRESSION;
    CONSTRUCTOR_DECLARATION;
    CONSTRUCTOR_INVOCATION;
    DIMENSION;
    EMPTY_STATEMENT;
    ENHANCED_FOR;
    ENUM_BODY;
    ENUM_CONSTANT_DECLARATION;
    ENUM_DECLARATION;
    EXPR_LIST;
    EXPRESSION;
    FIELD_DECLARATION;
    FLOATING_POINT_LITERAL;
    FOR_CONDITION;
    FOR_INIT;
    FOR_UPDATE;
    FORMAL_PARAMETERS;
    GREATER_EQUALS;
    IMPORT_DECLARATION;
    INITIALIZER_BLOCK;
    INNER_DECLARATION;
    INTEGER_LITERAL;
    INTERFACE_BODY_DECLARATION;
    INTERFACE_DECLARATION;
    LABEL_DECLARE;
    LEFT_SHIFT_ASSIGN;
    LEFT_SHIFT;
    LESS_EQUALS;
    MARKER_ANNOTATION;
    MEMBER_VALUE_PAIR;
    METHOD_DECLARATION;
    METHOD_INVOCATION;
    MODIFIERS;
    NORMAL_ANNOTATION;
    NULL_LITERAL;
    PACKAGE_DECLARATION;
    PARAMETERIZED_TYPE;
    PARENTHESIZED_EXPRESSION;
    POSTFIX_EXPRESSION;
    PREFIX_EXPRESSION;
    PRIMITIVE_TYPE;
    QUALIFIED_NAME;
    QUALIFIED_TYPE;
    SIGNED_RIGHT_SHIFT_ASSIGN;
    SIGNED_RIGHT_SHIFT;
    SIMPLE_TYPE;
    SINGLE_MEMBER_ANNOTATION;
    SINGLE_VARIABLE_DECLARATION;
    STATEMENT_EXPRESSION;
    STRING_LITERAL;
    SUPER_CONSTRUCTOR_INVOCATION;
    SUPER_FIELD_ACCESS;
    SUPER_METHOD_INVOCATION;
    SWITCH_BLOCK;
    THIS_EXPRESSION;
    TYPE_ARGUMENTS;
    TYPE_BOUND;
    TYPE_LIST;
    TYPE_LITERAL;
    TYPE_PARAMETER;
    TYPE_PARAMETERS;
    UNARY_MINUS;
    UNARY_PLUS;
    UNSIGNED_RIGHT_SHIFT_ASSIGN;
    UNSIGNED_RIGHT_SHIFT;
    VARIABLE_DECLARATION_FRAGMENT;
    VARIABLE_DECLARATION;
    WILDCARD_TYPE;
}

@header {
package org.openspotlight.bundle.language.java.parser;
import org.openspotlight.bundle.language.java.parser.executor.JavaParserExecutor;
import java.util.LinkedList;
import org.openspotlight.bundle.common.parser.SLCommonToken;
}

@members {
  int codeArea = -1;

	private JavaParserExecutor 	executor 		= null;

	public void setParserExecutor(final JavaParserExecutor executor){
		this.executor = executor;
	}

	private Object buildModifiersAST(List<?> listElements,
			Object annotationsTree) {
		LinkedList<Tree> modifiers = new LinkedList<Tree>();
		LinkedList<Tree> annotations = new LinkedList<Tree>();
		Tree resultTree = (Tree) adaptor.nil();

		if (listElements == null) {
			Tree emptyModifiersTree = (Tree) adaptor.nil();
			emptyModifiersTree = (Tree) adaptor.becomeRoot((Object) adaptor
					.create(MODIFIERS, "MODIFIERS"), emptyModifiersTree);
			adaptor.addChild(resultTree, emptyModifiersTree);
			if (annotationsTree != null) {
				adaptor.addChild(resultTree, annotationsTree);
			}
			return resultTree;
		}

		for (Object objectElement : listElements) {
			Tree treeElement = (Tree) objectElement;
			if (treeElement.getChildCount() == 0) {
				modifiers.add(treeElement);
			} else {
				annotations.add(treeElement);
			}
		}

		Tree modifiersTree = (Tree) adaptor.nil();
		modifiersTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				MODIFIERS, "MODIFIERS"), modifiersTree);
		if (modifiers.size() > 0) {

			for (Tree tree : modifiers) {
				adaptor.addChild(modifiersTree, tree);
			}
		}
		adaptor.addChild(resultTree, modifiersTree);

		if (annotations.size() > 0) {
			Tree annotationsGeneratedTree = null;
			if (annotationsTree == null) {
				annotationsGeneratedTree = (Tree) adaptor.nil();
				annotationsGeneratedTree = (Tree) adaptor.becomeRoot(
						(Object) adaptor.create(ANNOTATIONS, "ANNOTATIONS"),
						annotationsGeneratedTree);
			} else {
				annotationsGeneratedTree = (Tree) annotationsTree;
			}
			for (Tree tree : annotations) {
				adaptor.addChild(annotationsGeneratedTree, tree);
			}
			adaptor.addChild(resultTree, annotationsGeneratedTree);
		} else {
			if (annotationsTree != null) {
				adaptor.addChild(resultTree, annotationsTree);
			}
		}

		return resultTree;
	}

	private Object buildClassDeclarationAST(Token clazzToken,
			Token identifierToken, Token extendsToken, Token implementsToken,
			Object complementTree, Object typeParametersTree, Object typeTree,
			Object typeListTree, Object classBodyTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree classDeclarationTree = (Tree) adaptor.nil();
		classDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(CLASS_DECLARATION, clazzToken), classDeclarationTree);

		classDeclarationTree.addChild((Tree) adaptor.create(identifierToken));

		classDeclarationTree.addChild((Tree) complementTree);

		if (typeParametersTree != null) {
			classDeclarationTree.addChild((Tree) typeParametersTree);
		}

		if (extendsToken != null) {
			Tree extendsTree = (Tree) adaptor.nil();
			extendsTree = (Tree) adaptor.becomeRoot((Object) adaptor
					.create(extendsToken), extendsTree);

			extendsTree.addChild((Tree) typeTree);
			classDeclarationTree.addChild((Tree) extendsTree);
		}

		if (implementsToken != null) {
			Tree implementsTree = (Tree) adaptor.nil();
			implementsTree = (Tree) adaptor.becomeRoot((Object) adaptor
					.create(implementsToken), implementsTree);

			implementsTree.addChild((Tree) typeListTree);
			classDeclarationTree.addChild((Tree) implementsTree);
		}

		classDeclarationTree.addChild((Tree) classBodyTree);

		adaptor.addChild(resultTree, classDeclarationTree);
		return resultTree;
	}

	private Object buildInterfaceDeclarationAST(Token interfaceToken,
			Token identifierToken, Token extendsToken, Object complementTree,
			Object typeParametersTree, Object typeListTree,
			Object interfaceBodyTree) {

		Tree resultTree = (Tree) adaptor.nil();

		Tree interfaceDeclarationTree = (Tree) adaptor.nil();
		interfaceDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(INTERFACE_DECLARATION, interfaceToken),
				interfaceDeclarationTree);

		interfaceDeclarationTree.addChild((Tree) adaptor
				.create(identifierToken));

		interfaceDeclarationTree.addChild((Tree) complementTree);

		if (typeParametersTree != null) {
			interfaceDeclarationTree.addChild((Tree) typeParametersTree);
		}

		if (extendsToken != null) {
			Tree extendsTree = (Tree) adaptor.nil();
			extendsTree = (Tree) adaptor.becomeRoot((Object) adaptor
					.create(extendsToken), extendsTree);

			extendsTree.addChild((Tree) typeListTree);
			interfaceDeclarationTree.addChild((Tree) extendsTree);
		}

		interfaceDeclarationTree.addChild((Tree) interfaceBodyTree);

		adaptor.addChild(resultTree, interfaceDeclarationTree);

		return resultTree;
	}

	private Object buildAnnotationDeclarationAST(Token atSymbolToken,
			Token interfaceToken, Token identifierToken, Object complementTree,
			Object annotationTypeBodyTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree annotationDeclarationTree = (Tree) adaptor.nil();
		annotationDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(ANNOTATION_DECLARATION, atSymbolToken),
				annotationDeclarationTree);

		annotationDeclarationTree.addChild((Tree) adaptor
				.create(identifierToken));

		annotationDeclarationTree.addChild((Tree) complementTree);

		annotationDeclarationTree.addChild((Tree) annotationTypeBodyTree);

		adaptor.addChild(resultTree, annotationDeclarationTree);

		return resultTree;
	}

	private Object buildEnumDeclarationAST(Token enumToken,
			Token identifierToken, Token implementsToken,
			Object complementTree, Object typeListTree, Object enumBodyTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree enumDeclarationTree = (Tree) adaptor.nil();
		enumDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(ENUM_DECLARATION, enumToken), enumDeclarationTree);

		enumDeclarationTree.addChild((Tree) adaptor.create(identifierToken));

		enumDeclarationTree.addChild((Tree) complementTree);

		if (implementsToken != null) {
			Tree implementsTree = (Tree) adaptor.nil();
			implementsTree = (Tree) adaptor.becomeRoot((Object) adaptor
					.create(implementsToken), implementsTree);

			implementsTree.addChild((Tree) typeListTree);
			enumDeclarationTree.addChild((Tree) implementsTree);
		}

		enumDeclarationTree.addChild((Tree) enumBodyTree);

		adaptor.addChild(resultTree, enumDeclarationTree);

		return resultTree;
	}

	private Object buildConstructorDeclarationAST(Token identifierToken,
			Object modifiersTree, Object typeParametersTree,
			Object constructorDeclaratorRestTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree constructorDeclarationTree = (Tree) adaptor.nil();
		constructorDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(CONSTRUCTOR_DECLARATION, "CONSTRUCTOR_DECLARATION"),
				constructorDeclarationTree);

		constructorDeclarationTree.addChild((Tree) adaptor
				.create(identifierToken));

		constructorDeclarationTree.addChild((Tree) modifiersTree);

		if (typeParametersTree != null) {
			constructorDeclarationTree.addChild((Tree) typeParametersTree);
		}

		constructorDeclarationTree
				.addChild((Tree) constructorDeclaratorRestTree);

		adaptor.addChild(resultTree, constructorDeclarationTree);

		return resultTree;
	}

	private Object buildVoidMethodDeclarationAST(Token voidToken,
			Token identifierToken, Object modifiersTree,
			Object typeParametersTree, Object methodDeclarationRest) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree voidMethodDeclarationTree = (Tree) adaptor.nil();
		voidMethodDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(METHOD_DECLARATION, "METHOD_DECLARATION"),
				voidMethodDeclarationTree);

		voidMethodDeclarationTree.addChild((Tree) adaptor
				.create(identifierToken));

		voidMethodDeclarationTree.addChild((Tree) modifiersTree);

		if (typeParametersTree != null) {
			voidMethodDeclarationTree.addChild((Tree) typeParametersTree);
		}

		Tree typeTree = (Tree) adaptor.nil();
		typeTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				SIMPLE_TYPE, "SIMPLE_TYPE"), typeTree);

		typeTree.addChild((Tree) adaptor.create(voidToken));

		voidMethodDeclarationTree.addChild(typeTree);

		voidMethodDeclarationTree.addChild((Tree) methodDeclarationRest);

		adaptor.addChild(resultTree, voidMethodDeclarationTree);

		return resultTree;
	}

	private Object buildMethodDeclarationAST(Object modifiersTree,
			Object typeTree, Object methodDeclarationRestTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree methodDeclarationTree = (Tree) adaptor.nil();
		methodDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(METHOD_DECLARATION, "METHOD_DECLARATION"),
				methodDeclarationTree);

		Tree methodDeclarationRest = (Tree) methodDeclarationRestTree;

		methodDeclarationTree
				.addChild((Tree) methodDeclarationRest.getChild(0));

		methodDeclarationTree.addChild((Tree) modifiersTree);

		methodDeclarationTree.addChild((Tree) typeTree);

		for (int i = 1; i < methodDeclarationRest.getChildCount(); i++) {
			methodDeclarationTree.addChild((Tree) methodDeclarationRest
					.getChild(i));
		}

		adaptor.addChild(resultTree, methodDeclarationTree);

		return resultTree;
	}

	private Object buildMethodDeclarationAST(Token identifierToken,
			Object modifiersTree, Object typeParametersTree, Object typeTree,
			Object methodDeclarationRestTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree methodDeclarationTree = (Tree) adaptor.nil();
		methodDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(METHOD_DECLARATION, "METHOD_DECLARATION"),
				methodDeclarationTree);

		methodDeclarationTree.addChild((Tree) adaptor.create(identifierToken));

		methodDeclarationTree.addChild((Tree) modifiersTree);

		if (typeParametersTree != null) {
			methodDeclarationTree.addChild((Tree) typeParametersTree);
		}

		methodDeclarationTree.addChild((Tree) typeTree);

		methodDeclarationTree.addChild((Tree) methodDeclarationRestTree);

		adaptor.addChild(resultTree, methodDeclarationTree);

		return resultTree;
	}

	private Object buildFieldDeclarationAST(Object modifiersTree,
			Object typeTree, Object fieldDeclarationRestTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree fieldDeclarationTree = (Tree) adaptor.nil();
		fieldDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(FIELD_DECLARATION, "FIELD_DECLARATION"),
				fieldDeclarationTree);

		Tree fieldDeclarationRest = (Tree) fieldDeclarationRestTree;

		fieldDeclarationTree.addChild((Tree) modifiersTree);

		fieldDeclarationTree.addChild((Tree) typeTree);

		fieldDeclarationTree.addChild((Tree) fieldDeclarationRest);

		adaptor.addChild(resultTree, fieldDeclarationTree);

		return resultTree;
	}

	private Object buildFieldDeclarationAST(Token identifierToken,
			Object modifiersTree, Object typeTree,
			Object interfaceMethodOrFieldRestTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree fieldDeclarationTree = (Tree) adaptor.nil();
		fieldDeclarationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(FIELD_DECLARATION, "FIELD_DECLARATION"),
				fieldDeclarationTree);

		fieldDeclarationTree.addChild((Tree) modifiersTree);

		fieldDeclarationTree.addChild((Tree) typeTree);

		Tree interfaceMethodOrFieldRest = (Tree) interfaceMethodOrFieldRestTree;

		Tree manualVariableDeclarationFragment = (Tree) adaptor.nil();
		manualVariableDeclarationFragment = (Tree) adaptor.becomeRoot(
				(Object) adaptor.create(VARIABLE_DECLARATION_FRAGMENT,
						"VARIABLE_DECLARATION_FRAGMENT"),
				manualVariableDeclarationFragment);

		manualVariableDeclarationFragment.addChild((Tree) adaptor
				.create(identifierToken));

		if (interfaceMethodOrFieldRest.getType() == ASSIGN){
			manualVariableDeclarationFragment.addChild(interfaceMethodOrFieldRest);
			fieldDeclarationTree.addChild(manualVariableDeclarationFragment);
		} else {
			int i = 0;
			for (; i < interfaceMethodOrFieldRest.getChildCount(); i++) {
				manualVariableDeclarationFragment
						.addChild(interfaceMethodOrFieldRest.getChild(i));
				if (interfaceMethodOrFieldRest.getChild(i).getType() == ASSIGN) {
					break;
				}
			}
			i++;
	
			fieldDeclarationTree.addChild(manualVariableDeclarationFragment);
	
			for (; i < interfaceMethodOrFieldRest.getChildCount(); i++) {
				fieldDeclarationTree.addChild((Tree) interfaceMethodOrFieldRest
						.getChild(i));
			}
	
		}
		adaptor.addChild(resultTree, fieldDeclarationTree);

		return resultTree;
	}

	private Object buildFormalParametersAST(Token leftParenSymbolToken,
			Object formalParameterDeclsTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree formalParametersTree = (Tree) adaptor.nil();
		formalParametersTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(FORMAL_PARAMETERS, leftParenSymbolToken),
				formalParametersTree);

		if (formalParameterDeclsTree != null) {
			List<Tree[]> elements = new LinkedList<Tree[]>();
			Tree formalParameterDecls = (Tree) formalParameterDeclsTree;

			Tree[] singleVariableElement = null;
			for (int i = 0; i < formalParameterDecls.getChildCount(); i++) {
				switch (formalParameterDecls.getChild(i).getType()) {
				case MODIFIERS:
					if (singleVariableElement != null) {
						elements.add(singleVariableElement);
					}
					singleVariableElement = new Tree[6];
					singleVariableElement[1] = formalParameterDecls.getChild(i);
					break;
				case ANNOTATIONS:
					singleVariableElement[2] = formalParameterDecls.getChild(i);
					break;

				case THREE_DOTS:
					singleVariableElement[4] = formalParameterDecls.getChild(i);
					break;

				case Identifier:
					singleVariableElement[0] = formalParameterDecls.getChild(i);
					break;

				case ARRAY_DIMENSION:
					singleVariableElement[5] = formalParameterDecls.getChild(i);
					break;

				default: // type
					singleVariableElement[3] = formalParameterDecls.getChild(i);
					break;
				}
			}
			elements.add(singleVariableElement);

			for (Tree[] activeParameter : elements) {
				Tree singleVariableDeclaration = (Tree) adaptor.nil();
				singleVariableDeclaration = (Tree) adaptor.becomeRoot(
						(Object) adaptor.create(SINGLE_VARIABLE_DECLARATION,
								"SINGLE_VARIABLE_DECLARATION"),
						singleVariableDeclaration);

				singleVariableDeclaration.addChild(activeParameter[0]);
				singleVariableDeclaration.addChild(activeParameter[1]);
				if (activeParameter[2] != null) {
					singleVariableDeclaration.addChild(activeParameter[2]);
				}
				singleVariableDeclaration.addChild(activeParameter[3]);
				if (activeParameter[4] != null) {
					singleVariableDeclaration.addChild(activeParameter[4]);
				}
				if (activeParameter[5] != null) {
					singleVariableDeclaration.addChild(activeParameter[5]);
				}
				formalParametersTree.addChild(singleVariableDeclaration);
			}
		}

		resultTree.addChild(formalParametersTree);

		return resultTree;
	}

	private Object buildIdentifierSuffixTypeLiteral(Token dotSymbolToken,
			Token classToken, Object arrayDimensionDeclarationTree,
			Object primary) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree primaryTree = (Tree) primary;

		Tree resultType = null;
		Tree type = (Tree) adaptor.nil();
		if (primaryTree.getChildCount() == 0) {
			type = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					SIMPLE_TYPE, "SIMPLE_TYPE"), type);
			type.addChild(primaryTree);
		} else {
			type = buildQualified(QUALIFIED_TYPE, "QUALIFIED_TYPE",
					SIMPLE_TYPE, "SIMPLE_TYPE", primaryTree, 0, primaryTree
							.getChildCount());
		}
		if (arrayDimensionDeclarationTree != null) {
			Tree arrayTypeTree = (Tree) adaptor.nil();
			arrayTypeTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					ARRAY_TYPE, "ARRAY_TYPE"), arrayTypeTree);
			arrayTypeTree.addChild(type);
			arrayTypeTree.addChild((Tree) arrayDimensionDeclarationTree);
			resultType = arrayTypeTree;
		} else {
			resultType = type;
		}

		Tree typeLiteralTree = (Tree) adaptor.nil();
		typeLiteralTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				TYPE_LITERAL, "TYPE_LITERAL"), typeLiteralTree);

		typeLiteralTree.addChild(resultType);
		typeLiteralTree.addChild((Tree) adaptor.create(dotSymbolToken));
		typeLiteralTree.addChild((Tree) adaptor.create(classToken));

		adaptor.addChild(resultTree, typeLiteralTree);

		return resultTree;
	}

	private Tree buildQualified(int qualifiedType, String qualifiedDescr,
			int simpleType, String simpleDescr, Tree primary,
			int startPosition, int endPosition) {
		Tree qualifiedTree = (Tree) adaptor.nil();
		qualifiedTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				qualifiedType, qualifiedDescr), qualifiedTree);

		for (int i = startPosition; i < endPosition; i++) {
			Tree simpleTree = (Tree) adaptor.nil();
			simpleTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					simpleType, simpleDescr), simpleTree);
			if (primary.getChild(i).getType() == DOT) {
				qualifiedTree.addChild(primary.getChild(i));
				i++;
				simpleTree.addChild(primary.getChild(i));
			} else {
				simpleTree.addChild(primary.getChild(i));
			}
			qualifiedTree.addChild(simpleTree);
		}

		return qualifiedTree;
	}

	private Object buildIdentifierSuffixMethodInvocation(Object primary,
			Object argumentsTree) {
		Tree resultTree = (Tree) adaptor.nil();

		Tree methodInvocationTree = (Tree) adaptor.nil();
		methodInvocationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(METHOD_INVOCATION, "METHOD_INVOCATION"),
				methodInvocationTree);

		Tree primaryTree = (Tree) primary;
		Tree expressionTreePart = null;
		Tree methodName = null;
		if (primaryTree.getChildCount() > 0) {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);

			for (int i = 0; i < primaryTree.getChildCount() - 2; i++) {
				qualifiedName.addChild(primaryTree.getChild(i));
			}

			Tree expressionTree = (Tree) adaptor.nil();
			expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					EXPRESSION, "EXPRESSION"), expressionTree);
			expressionTree.addChild(qualifiedName);
			expressionTreePart = (Tree) adaptor.nil();
			expressionTreePart.addChild(expressionTree);
			expressionTreePart.addChild(primaryTree.getChild(primaryTree
					.getChildCount() - 2));
			methodName = primaryTree.getChild(primaryTree.getChildCount() - 1);
		} else {
			methodName = primaryTree;
		}
		if (expressionTreePart != null) {
			methodInvocationTree.addChild(expressionTreePart);
		}
		methodInvocationTree.addChild(methodName);
		methodInvocationTree.addChild((Tree) argumentsTree);

		resultTree.addChild(methodInvocationTree);

		return resultTree;
	}

	private Object buildIdentifierSuffixMethodGenericInvocation(
			Token dotSymbolToken, Object explicitGenericInvocationTree,
			Object primary) {
		Tree resultTree = (Tree) adaptor.nil();
		Tree primaryTree = (Tree) primary;

		Tree methodInvocationTree = (Tree) adaptor.nil();
		methodInvocationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(METHOD_INVOCATION, "METHOD_INVOCATION"),
				methodInvocationTree);

		Tree qualifiedName = (Tree) adaptor.nil();
		qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);

		if (primaryTree.getChildCount() == 0) {
			qualifiedName.addChild(primaryTree);
		} else {
			for (int i = 0; i < primaryTree.getChildCount(); i++) {
				qualifiedName.addChild(primaryTree.getChild(i));
			}
		}

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);
		expressionTree.addChild(qualifiedName);

		methodInvocationTree.addChild(expressionTree);
		methodInvocationTree.addChild((Tree) adaptor.create(dotSymbolToken));
		methodInvocationTree.addChild((Tree) explicitGenericInvocationTree);

		resultTree.addChild(methodInvocationTree);

		return resultTree;
	}

	private Object buildQualifiedPrimaryAST(Object qualifiedNameTree,
			Object identifierSuffixTree) {
		if (identifierSuffixTree == null) {
			return qualifiedNameTree;
		}
		return identifierSuffixTree;
	}

	private Object buildSelectorQualifiedNameAST(Token dotSymbolToken,
			Token identifierToken, boolean isParameterOk, Object parameter) {
		Tree qualifiedName = (Tree) adaptor.nil();
		qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
		if (isParameterOk) {
			Tree expressionTree = (Tree) adaptor.nil();
			expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					EXPRESSION, "EXPRESSION"), expressionTree);
			expressionTree.addChild((Tree) parameter);
			qualifiedName.addChild(expressionTree);
		} else {
			qualifiedName.addChild((Tree) parameter);
		}
		qualifiedName.addChild((Tree) adaptor.create(dotSymbolToken));
		qualifiedName.addChild((Tree) adaptor.create(identifierToken));

		return qualifiedName;
	}

	private Object buildSelectorMethodInvocationAST(Token dotSymbolToken,
			Token identifierToken, Object argumentsTree, boolean isParameterOk,
			Object parameter) {
		Tree methodInvocationTree = (Tree) adaptor.nil();
		methodInvocationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(METHOD_INVOCATION, "METHOD_INVOCATION"),
				methodInvocationTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);
		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}
		methodInvocationTree.addChild(expressionTree);

		methodInvocationTree.addChild((Tree) adaptor.create(dotSymbolToken));
		methodInvocationTree.addChild((Tree) adaptor.create(identifierToken));
		methodInvocationTree.addChild((Tree) argumentsTree);

		return methodInvocationTree;
	}

	private Object buildSelectorThisExpressionAST(Token dotSymbolToken,
			Token thisToken, boolean isParameterOk, Object parameter) {
		Tree thisExpressionTree = (Tree) adaptor.nil();
		thisExpressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				THIS_EXPRESSION, "THIS_EXPRESSION"), thisExpressionTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);

		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}

		thisExpressionTree.addChild(expressionTree);
		thisExpressionTree.addChild((Tree) adaptor.create(dotSymbolToken));
		thisExpressionTree.addChild((Tree) adaptor.create(thisToken));

		return thisExpressionTree;
	}

	private Object buildSelectorArrayAccessAST(Object dimensionValueTree,
			boolean isParameterOk, Object parameter) {

		Tree arrayAccessTree = (Tree) adaptor.nil();
		arrayAccessTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				ARRAY_ACCESS, "ARRAY_ACCESS"), arrayAccessTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);

		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}

		arrayAccessTree.addChild(expressionTree);
		arrayAccessTree.addChild((Tree) dimensionValueTree);

		return arrayAccessTree;
	}

	private Object buildSelectorClassInstanceCreationAST(
			Token dotSymbolToken, Token newToken, Object innerCreatorTree,
			boolean isParameterOk, Object parameter) {
		Tree classInstanceCreationTree = (Tree) adaptor.nil();
		classInstanceCreationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(CLASS_INSTANCE_CREATION, newToken),
				classInstanceCreationTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);

		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}

		classInstanceCreationTree.addChild(expressionTree);
		classInstanceCreationTree.addChild((Tree) adaptor
				.create(dotSymbolToken));
		classInstanceCreationTree.addChild((Tree) innerCreatorTree);

		return classInstanceCreationTree;
	}

	// Supers are the same!
	private Object buildSelectorSuperMethodInvocationAST(
			Token dotSymbolToken, Token superToken, Object superSuffixTree,
			boolean isParameterOk, Object parameter) {
		Tree superMethodInvocationTree = (Tree) adaptor.nil();
		superMethodInvocationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(SUPER_METHOD_INVOCATION, superToken),
				superMethodInvocationTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);
		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}
		superMethodInvocationTree.addChild(expressionTree);

		superMethodInvocationTree.addChild((Tree) superSuffixTree);

		return superMethodInvocationTree;
	}

	private Object buildSelectorSuperFieldAccessAST(Token dotSymbolToken,
			Token superToken, Object superSuffixTree, boolean isParameterOk,
			Object parameter) {
		Tree superFieldAccessTree = (Tree) adaptor.nil();
		superFieldAccessTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(SUPER_FIELD_ACCESS, superToken), superFieldAccessTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);
		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}
		superFieldAccessTree.addChild(expressionTree);
		superFieldAccessTree.addChild((Tree) superSuffixTree);

		return superFieldAccessTree;
	}

	private Object buildSelectorSuperConstructorInvocationAST(
			Token dotSymbolToken, Token superToken, Object superSuffixTree,
			boolean isParameterOk, Object parameter) {
		Tree superMethodInvocationTree = (Tree) adaptor.nil();
		superMethodInvocationTree = (Tree) adaptor.becomeRoot((Object) adaptor
				.create(SUPER_CONSTRUCTOR_INVOCATION, superToken),
				superMethodInvocationTree);

		Tree expressionTree = (Tree) adaptor.nil();
		expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
				EXPRESSION, "EXPRESSION"), expressionTree);
		if (isParameterOk) {
			expressionTree.addChild((Tree) parameter);
		} else {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) parameter);
			expressionTree.addChild(qualifiedName);
		}
		superMethodInvocationTree.addChild(expressionTree);
		superMethodInvocationTree.addChild((Tree) superSuffixTree);

		return superMethodInvocationTree;
	}

	private Object buildUnaryAST(Token doublePlusToken,
			Token doubleMinusToken, boolean isOk, Object previusTree) {
		Tree resultTree = (Tree) adaptor.nil();
		// TODO Auto-generated method stub
		if (!isOk) {
			Tree qualifiedName = (Tree) adaptor.nil();
			qualifiedName = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					QUALIFIED_NAME, "QUALIFIED_NAME"), qualifiedName);
			qualifiedName.addChild((Tree) previusTree);
			resultTree = qualifiedName;
		} else {
			resultTree = (Tree) previusTree;
		}

		if (doublePlusToken != null || doubleMinusToken != null) {
			Tree postfixExpressionTree = (Tree) adaptor.nil();
			postfixExpressionTree = (Tree) adaptor.becomeRoot((Object) adaptor
					.create(POSTFIX_EXPRESSION, "POSTFIX_EXPRESSION"),
					postfixExpressionTree);
			Tree expressionTree = (Tree) adaptor.nil();
			expressionTree = (Tree) adaptor.becomeRoot((Object) adaptor.create(
					EXPRESSION, "EXPRESSION"), expressionTree);
			expressionTree.addChild(resultTree);

			postfixExpressionTree.addChild(expressionTree);
			if (doublePlusToken != null) {
				postfixExpressionTree.addChild((Tree) adaptor
						.create(doublePlusToken));
			} else {
				postfixExpressionTree.addChild((Tree) adaptor
						.create(doubleMinusToken));
			}
			return postfixExpressionTree;
		}

		return resultTree;
	}
}

// starting point for parsing a java file
/* The annotations are separated out to make parsing faster, but must be associated with
   a packageDeclaration or a typeDeclaration (and not an empty one). */
compilationUnit
@init
    {   int activeCodeArea = ++codeArea; 
        int mode=0;   }
@after {
     executor.sourceLine().addCodeArea(input, activeCodeArea, 0, $stop.getTokenIndex());
}
    :   annotations
        (   packageDeclaration[false] importDeclaration* typeDeclaration*
        |   classOrInterfaceDeclaration[$annotations.tree] {mode = 1;} typeDeclaration*
        )
        -> {mode == 1}? ^(COMPILATION_UNIT classOrInterfaceDeclaration typeDeclaration*)
        -> ^(COMPILATION_UNIT ^(PACKAGE_DECLARATION annotations packageDeclaration) importDeclaration* typeDeclaration*)
    |   packageDeclaration[true] importDeclaration* typeDeclaration*
        -> ^(COMPILATION_UNIT packageDeclaration? importDeclaration* typeDeclaration*)
    ;

packageDeclaration[boolean buildPackageDeclaration]
    :   PACKAGE qualifiedName SEMI_COLON
    	{	executor.createPackageNode($qualifiedName.tree);	}
        ->{$buildPackageDeclaration}? ^(PACKAGE_DECLARATION PACKAGE ^(QUALIFIED_NAME qualifiedName))
        -> PACKAGE ^(QUALIFIED_NAME qualifiedName)
    |	{	executor.createDefaultPackage();	}
    ;

importDeclaration
    :   IMPORT STATIC? qualifiedName (DOT STAR)? SEMI_COLON
        -> ^(IMPORT_DECLARATION[$IMPORT] STATIC? STAR? ^(QUALIFIED_NAME qualifiedName))
    ;
    
typeDeclaration
    :   classOrInterfaceDeclaration[null]
    |   SEMI_COLON!
    ;

classOrInterfaceDeclaration[Object annotationsTree]
@init
    {   int activeCodeArea = -1;
        if (state.backtracking==0) activeCodeArea = ++codeArea;  }
@after {
     executor.sourceLine().addCodeArea(input, activeCodeArea, 0, $stop.getTokenIndex());
}

    :   classOrInterfaceModifiers[$annotationsTree]!
    (    classDeclaration[$classOrInterfaceModifiers.tree] 
    |    interfaceDeclaration[$classOrInterfaceModifiers.tree]
    )
    ;
    
classOrInterfaceModifiers[Object annotationsTree]
    :   elements+=classOrInterfaceModifier*
        -> {    buildModifiersAST($elements, $annotationsTree)    }
    ;

classOrInterfaceModifier
    :   annotation
    |   PUBLIC
    |   PROTECTED
    |   PRIVATE
    |   ABSTRACT
    |   STATIC
    |   FINAL
    |   STRICTFP
    ;

modifiers returns [int tokenStart]
@after {   $tokenStart = $stop.getTokenIndex();  }
    :   elements+=modifier*
        -> {    buildModifiersAST($elements, null)    }
    ;

classDeclaration[Object complementTree]
    :   normalClassDeclaration[$complementTree]
    |   enumDeclaration[$complementTree]
    ;

normalClassDeclaration[Object complementTree]
   @after { executor.popContext();  }
    :   CLASS Identifier
        {	executor.createJavaTypeClass($Identifier);	}
        typeParameters?    
        (EXTENDS type )?
        (IMPLEMENTS typeList)?
        classBody
        -> {buildClassDeclarationAST($CLASS, $Identifier, $EXTENDS, $IMPLEMENTS, $complementTree, $typeParameters.tree, $type.tree, $typeList.tree, $classBody.tree )}
    ;

typeParameters
    :   LESS typeParameter (COMMA typeParameter)* GREATER
        -> ^(TYPE_PARAMETERS typeParameter+)
    ;

typeParameter
    :   Identifier (EXTENDS typeBound)?
        -> ^(TYPE_PARAMETER Identifier ^(EXTENDS typeBound)?)
    ;
        
typeBound
    :   type (AMPERSAND type)*
        -> ^(TYPE_BOUND type+)
    ;

enumDeclaration[Object complementTree]
   @after { executor.popContext();  }
    :   ENUM Identifier
        {	executor.createJavaTypeEnum($Identifier);	}    
     (IMPLEMENTS typeList)? enumBody
        -> {buildEnumDeclarationAST($ENUM, $Identifier, $IMPLEMENTS, $complementTree, $typeList.tree, $enumBody.tree )}
    ;

enumBody
    :   LEFT_CURLY enumConstants? COMMA? enumBodyDeclarations? RIGHT_CURLY
        -> ^(ENUM_BODY[$LEFT_CURLY] enumConstants? enumBodyDeclarations? RIGHT_CURLY)
    ;

enumConstants
    :   enumConstant (COMMA! enumConstant)*
    ;
    
enumConstant
    :   annotations? Identifier arguments? classBody?
        -> ^(ENUM_CONSTANT_DECLARATION Identifier annotations? arguments? classBody?)
    ;
    
enumBodyDeclarations
    :   SEMI_COLON! classBodyDeclaration*
    ;
    
interfaceDeclaration[Object complementTree]
    :   normalInterfaceDeclaration[$complementTree]
    |   annotationTypeDeclaration[$complementTree]
    ;
    
normalInterfaceDeclaration[Object complementTree]
@after	{	executor.popContext();	}
    :   INTERFACE Identifier 
        {	executor.createJavaTypeInterface($Identifier);	}    
    
    typeParameters? (EXTENDS typeList)? interfaceBody
        -> { buildInterfaceDeclarationAST($INTERFACE, $Identifier, $EXTENDS, $complementTree, $typeParameters.tree, $typeList.tree, $interfaceBody.tree) }
    ;
    
typeList
    :   type (COMMA type)*
        -> ^(TYPE_LIST type+)
    ;
    
classBody
    :   LEFT_CURLY classBodyDeclaration* RIGHT_CURLY
        -> ^(CLASS_BODY_DECLARATION[$LEFT_CURLY] classBodyDeclaration* RIGHT_CURLY)
    ;

interfaceBody
    :   LEFT_CURLY interfaceBodyDeclaration* RIGHT_CURLY
        -> ^(INTERFACE_BODY_DECLARATION[$LEFT_CURLY] interfaceBodyDeclaration* RIGHT_CURLY)
    ;

classBodyDeclaration
    :   SEMI_COLON!
    |   STATIC? block
        -> ^(INITIALIZER_BLOCK STATIC? block)
    |   modifiers! memberDecl[$modifiers.tree, $modifiers.tokenStart]
    ;

memberDecl[Object modifiersTree, int tokenStart]
@init{   int activeCodeArea = -1;    }
    :   genericMethodOrConstructorDecl[$modifiersTree,$tokenStart]
    |   memberDeclaration[$modifiersTree,$tokenStart]
     {   activeCodeArea = ++codeArea;    }
    |   VOID Identifier voidMethodDeclaratorRest
     {   if ($tokenStart <= 0) $tokenStart = $start.getTokenIndex();
            executor.sourceLine().addCodeArea(input, activeCodeArea, $tokenStart, input.LT(-1).getTokenIndex());    }
        -> { buildVoidMethodDeclarationAST($VOID, $Identifier, $modifiersTree, null, $voidMethodDeclaratorRest.tree) }
    | 
     {   activeCodeArea = ++codeArea;    }
      Identifier constructorDeclaratorRest
     {   if ($tokenStart <= 0) $tokenStart = $start.getTokenIndex();
            executor.sourceLine().addCodeArea(input, activeCodeArea, $tokenStart, input.LT(-1).getTokenIndex());    }
        -> { buildConstructorDeclarationAST($Identifier, $modifiersTree, null, $constructorDeclaratorRest.tree) }
    |   interfaceDeclaration[$modifiersTree] -> ^(INNER_DECLARATION interfaceDeclaration)
    |   classDeclaration[$modifiersTree] -> ^(INNER_DECLARATION classDeclaration)
    ;
    
memberDeclaration[Object modifiersTree, int tokenStart]
@init
    {   int activeCodeArea = -1;    }
    :   type
    (    {   activeCodeArea = ++codeArea;    }
      methodDeclaration
       {   if ($tokenStart <= 0) $tokenStart = $start.getTokenIndex();
            executor.sourceLine().addCodeArea(input, activeCodeArea, $tokenStart, input.LT(-1).getTokenIndex());    }
        -> { buildMethodDeclarationAST($modifiersTree, $type.tree, $methodDeclaration.tree) }
    |   fieldDeclaration
        -> { buildFieldDeclarationAST($modifiersTree, $type.tree, $fieldDeclaration.tree) }
    )
    ;

genericMethodOrConstructorDecl[Object modifiersTree, int tokenStart]
@init
    {   int activeCodeArea = -1;
        if (state.backtracking==0) activeCodeArea = ++codeArea;  }
@after
    {   if ($tokenStart <= 0) $tokenStart = $start.getTokenIndex();
        executor.sourceLine().addCodeArea(input, activeCodeArea, $tokenStart, $stop.getTokenIndex());    }
    :   typeParameters
    (    (type | VOID) id1=Identifier methodDeclaratorRest
        -> {$VOID != null }? { buildVoidMethodDeclarationAST($VOID, $id1, $modifiersTree, $typeParameters.tree, $methodDeclaratorRest.tree) }
        -> { buildMethodDeclarationAST($id1, $modifiersTree, $typeParameters.tree, $type.tree, $methodDeclaratorRest.tree) }
    |    id2=Identifier constructorDeclaratorRest
        -> { buildConstructorDeclarationAST($id2, $modifiersTree, $typeParameters.tree, $constructorDeclaratorRest.tree) }
    )
    ;

methodDeclaration
    :   Identifier methodDeclaratorRest
    ;

fieldDeclaration
    :   variableDeclarators SEMI_COLON!
    ;
        
interfaceBodyDeclaration
    :   modifiers! interfaceMemberDecl[$modifiers.tree, $modifiers.tokenStart]
    |   SEMI_COLON!
    ;

interfaceMemberDecl[Object modifiersTree, int tokenStart]
    :   interfaceMethodOrFieldDecl[$modifiersTree]
    |   interfaceGenericMethodDecl[$modifiersTree]
    |   VOID Identifier voidInterfaceMethodDeclaratorRest
        -> { buildVoidMethodDeclarationAST($VOID, $Identifier, $modifiersTree, null, $voidInterfaceMethodDeclaratorRest.tree) }
    |   interfaceDeclaration[$modifiersTree] -> ^(INNER_DECLARATION interfaceDeclaration)
    |   classDeclaration[$modifiersTree] -> ^(INNER_DECLARATION classDeclaration)
    ;
    
interfaceMethodOrFieldDecl[Object modifiersTree]
    :   type Identifier imfr=interfaceMethodOrFieldRest
    -> {$imfr.isMethod}? { buildMethodDeclarationAST($Identifier, $modifiersTree, null, $type.tree, $imfr.tree ) }
    -> { buildFieldDeclarationAST($Identifier, $modifiersTree, $type.tree, $imfr.tree ) }
    ;

interfaceMethodOrFieldRest returns [boolean isMethod]
    :   constantDeclaratorsRest SEMI_COLON! {$isMethod = false;}
    |   interfaceMethodDeclaratorRest {$isMethod = true;}
    ;

methodDeclaratorRest
    :   formalParameters arrayDimensionDeclaration?
        (THROWS qualifiedTypeList)?
        (   methodBody
        |   SEMI_COLON
        )
        -> formalParameters arrayDimensionDeclaration? ^(THROWS qualifiedTypeList)? methodBody?
    ;

voidMethodDeclaratorRest
    :   formalParameters (THROWS qualifiedTypeList)?
        (   methodBody
        |   SEMI_COLON
        )
        -> formalParameters ^(THROWS qualifiedTypeList)? methodBody?
    ;

interfaceMethodDeclaratorRest
    :   formalParameters arrayDimensionDeclaration? (THROWS qualifiedTypeList)? SEMI_COLON
        -> formalParameters arrayDimensionDeclaration? ^(THROWS qualifiedTypeList)?
    ;

interfaceGenericMethodDecl[Object modifiersTree]
    :   typeParameters (type | VOID) Identifier interfaceMethodDeclaratorRest
    -> {$VOID != null}? { buildVoidMethodDeclarationAST($VOID, $Identifier, $modifiersTree, $typeParameters.tree, $interfaceMethodDeclaratorRest.tree) }
    -> { buildMethodDeclarationAST($Identifier, $modifiersTree, $typeParameters.tree, $type.tree, $interfaceMethodDeclaratorRest.tree) }
    ;

voidInterfaceMethodDeclaratorRest
    :   formalParameters (THROWS qualifiedTypeList)? SEMI_COLON
        -> formalParameters ^(THROWS qualifiedTypeList)?
    ;

constructorDeclaratorRest
    :   formalParameters (THROWS qualifiedTypeList)? constructorBody
        -> formalParameters ^(THROWS qualifiedTypeList)? constructorBody
    ;

constantDeclarator
    :   Identifier constantDeclaratorRest
        -> ^(VARIABLE_DECLARATION_FRAGMENT Identifier constantDeclaratorRest)
    ;

variableDeclarators
    :    variableDeclarator (COMMA! variableDeclarator)*
    ;

variableDeclarator
@init    {    int mode = 0;    }
    :   variableDeclaratorId (ASSIGN variableInitializer {mode = 1;})?
        ->{mode == 1}? ^(VARIABLE_DECLARATION_FRAGMENT variableDeclaratorId ^(ASSIGN variableInitializer)) 
        ->  ^(VARIABLE_DECLARATION_FRAGMENT variableDeclaratorId)
    ;

constantDeclaratorsRest
    :   constantDeclaratorRest (COMMA! constantDeclarator)*
    ;

constantDeclaratorRest
    :   arrayDimensionDeclaration? ASSIGN variableInitializer
        -> arrayDimensionDeclaration? ^(ASSIGN variableInitializer)
    ;
    
variableDeclaratorId
    :   Identifier arrayDimensionDeclaration?
    ;

variableInitializer
    :   arrayInitializer
    |   expressionWrapper
    ;

arrayDimensionDeclaration
    :    (lsq+=LEFT_SQUARE RIGHT_SQUARE)+
        -> ARRAY_DIMENSION[String.valueOf($lsq.size())]
    ;

arrayInitializer
    :   LEFT_CURLY (variableInitializer (COMMA variableInitializer)* (COMMA)? )? RIGHT_CURLY
        -> ^(ARRAY_INITIALIZER[$LEFT_CURLY] variableInitializer* RIGHT_CURLY)
    ;

modifier
    :   annotation
    |   PUBLIC
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

type
    :    classOrInterfaceTypeQualifed {} arrayDimensionDeclaration?
        -> {$arrayDimensionDeclaration.tree != null}? ^(ARRAY_TYPE classOrInterfaceTypeQualifed arrayDimensionDeclaration)
        -> classOrInterfaceTypeQualifed
    |    primitiveType arrayDimensionDeclaration?
        -> {$arrayDimensionDeclaration.tree != null}? ^(ARRAY_TYPE ^(PRIMITIVE_TYPE primitiveType) arrayDimensionDeclaration)
        -> ^(PRIMITIVE_TYPE primitiveType)
    ;

classOrInterfaceTypeQualifed
@init    {    int mode = 0;    }
    :    classOrInterfaceType (dottedClassOrInterfaceType {mode = 1;})*
    -> {mode == 1}? ^(QUALIFIED_TYPE classOrInterfaceType dottedClassOrInterfaceType+)
    -> classOrInterfaceType
    ;

dottedClassOrInterfaceType
    :    DOT classOrInterfaceType
    ;


classOrInterfaceType
    :    Identifier typeArguments?
    -> {$typeArguments.tree !=null }? ^(PARAMETERIZED_TYPE ^(SIMPLE_TYPE Identifier) typeArguments)
    -> ^(SIMPLE_TYPE Identifier)
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

variableModifier
    :   FINAL
    |   annotation
    ;

typeArguments
    :   LESS typeArgument (COMMA typeArgument)* GREATER
    -> ^(TYPE_ARGUMENTS ^(TYPE_LIST typeArgument+))
    ;

typeArgument
@init    {int mode = 0;}
    :   type 
    |   QUESTION ((EXTENDS {mode = 1;} | SUPER {mode = 2;}) type)? 
        ->{mode == 1}? ^(WILDCARD_TYPE QUESTION ^(EXTENDS type) )
        ->{mode == 2}? ^(WILDCARD_TYPE QUESTION ^(SUPER type))
        -> ^(WILDCARD_TYPE QUESTION)
    ;

qualifiedTypeList
    :   qualifiedType (COMMA! qualifiedType)*
    ;

qualifiedType
@init    {    int mode = 0;    }
    :   simpleType (qualifierType {mode = 1;})*
    -> {mode == 1}? ^(QUALIFIED_TYPE simpleType qualifierType+)
    -> simpleType
    ;

qualifierType
    :    DOT simpleType
    ;

simpleType
    :    Identifier
    -> ^(SIMPLE_TYPE Identifier)
    ;

formalParameters
    :   LEFT_PAREN formalParameterDecls? RIGHT_PAREN
        -> {    buildFormalParametersAST($LEFT_PAREN, $formalParameterDecls.tree)    }
    ;

formalParameterDecls
    :   variableModifiers type formalParameterDeclsRest
    ;
    
formalParameterDeclsRest
    :   variableDeclaratorId (COMMA! formalParameterDecls)?
    |   THREE_DOTS variableDeclaratorId
    ;
    
methodBody
    :   block
    ;

constructorBody
    :   LEFT_CURLY explicitConstructorInvocation? blockStatement* RIGHT_CURLY
        -> ^(BLOCK[$LEFT_CURLY] explicitConstructorInvocation? blockStatement* RIGHT_CURLY)
    ;

explicitConstructorInvocation
    :   nonWildcardTypeArguments? (THIS | SUPER) arguments SEMI_COLON
    -> {$SUPER == null}? ^(CONSTRUCTOR_INVOCATION[$THIS] nonWildcardTypeArguments? arguments)
    -> ^(SUPER_CONSTRUCTOR_INVOCATION[$SUPER] nonWildcardTypeArguments? arguments)
    |   primary DOT nonWildcardTypeArguments? SUPER arguments SEMI_COLON
    -> {!$primary.isOk}? ^(SUPER_CONSTRUCTOR_INVOCATION[$SUPER] ^(EXPRESSION ^(QUALIFIED_NAME primary)) nonWildcardTypeArguments? arguments)
    -> ^(SUPER_CONSTRUCTOR_INVOCATION[$SUPER] ^(EXPRESSION primary) nonWildcardTypeArguments? arguments)
    ;

qualifiedName
    :   Identifier qualifier*
    ;

thisQualifiedName
    :   THIS qualifier*
    -> ^(QUALIFIED_NAME THIS qualifier*)
    ;

qualifier
    :    DOT Identifier
    ;

literal
    :   integerLiteral -> ^(INTEGER_LITERAL integerLiteral)
    |   FloatingPointLiteral -> ^(FLOATING_POINT_LITERAL FloatingPointLiteral)
    |   CharacterLiteral -> ^(CHARACTER_LITERAL StringLiteral)
    |   StringLiteral -> ^(STRING_LITERAL StringLiteral)
    |   booleanLiteral -> ^(BOOLEAN_LITERAL booleanLiteral)
    |   NULL -> ^(NULL_LITERAL NULL)
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

// ANNOTATIONS

annotations
    :   annotation+
        -> ^(ANNOTATIONS annotation+)
    ;

annotation
@init    {    int mode = 0;    }
    :   AT qualifiedName ( LEFT_PAREN ( elementValuePairs { mode = 2; } | elementValue {mode = 1;})? RIGHT_PAREN )?
        ->{mode == 0}? ^(MARKER_ANNOTATION[$AT] ^(QUALIFIED_NAME qualifiedName) )
        ->{mode == 1}? ^(SINGLE_MEMBER_ANNOTATION[$AT] ^(QUALIFIED_NAME qualifiedName) elementValue)
        -> ^(NORMAL_ANNOTATION[$AT] ^(QUALIFIED_NAME qualifiedName) elementValuePairs)
    ;

elementValuePairs
    :   elementValuePair (COMMA elementValuePair)*
        -> ^(MEMBER_VALUE_PAIR elementValuePair+) 
    ;

elementValuePair
    :   Identifier ASSIGN^ elementValue
    ;
    
elementValue
    :   conditionalExpression -> ^(EXPRESSION conditionalExpression)
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :   LEFT_CURLY (elementValue (COMMA elementValue)*)? COMMA? RIGHT_CURLY
        -> ^(ARRAY_INITIALIZER[$LEFT_CURLY] elementValue* RIGHT_CURLY)
    ;

annotationTypeDeclaration[Object complementTree]
   @after { executor.popContext();  }
    :   AT INTERFACE Identifier 
        {	executor.createJavaTypeAnnotation($Identifier);	}        
    	annotationTypeBody
        -> { buildAnnotationDeclarationAST($AT, $INTERFACE, $Identifier, $complementTree, $annotationTypeBody.tree )}
    ;
    
annotationTypeBody
    :   LEFT_CURLY annotationTypeElementDeclaration* RIGHT_CURLY
        -> ^(ANNOTATION_BODY[$LEFT_CURLY] annotationTypeElementDeclaration* RIGHT_CURLY)
    ;
    
annotationTypeElementDeclaration
    :   modifiers! annotationTypeElementRest[$modifiers.tree, $modifiers.tokenStart]
    ;
    
annotationTypeElementRest[Object modifiersTree, int tokenStart]
    :   type amcr=annotationMethodOrConstantRest SEMI_COLON
    -> {$amcr.isMethod}? { buildMethodDeclarationAST($modifiersTree, $type.tree, $amcr.tree ) }
    -> { buildFieldDeclarationAST($modifiersTree, $type.tree, $amcr.tree ) }
    |   normalClassDeclaration[$modifiersTree] SEMI_COLON? -> ^(INNER_DECLARATION normalClassDeclaration)
    |   normalInterfaceDeclaration[$modifiersTree] SEMI_COLON? -> ^(INNER_DECLARATION normalInterfaceDeclaration)
    |   enumDeclaration[$modifiersTree] SEMI_COLON? -> ^(INNER_DECLARATION enumDeclaration)
    |   annotationTypeDeclaration[$modifiersTree] SEMI_COLON? -> ^(INNER_DECLARATION annotationTypeDeclaration)
    ;

annotationMethodOrConstantRest returns [boolean isMethod]
    :   annotationMethodRest {$isMethod = true;}
    |   annotationConstantRest {$isMethod = false;}
    ;

annotationMethodRest
    :   Identifier LEFT_PAREN RIGHT_PAREN defaultValue?
    	-> Identifier FORMAL_PARAMETERS[$LEFT_PAREN] defaultValue?
    ;
    
annotationConstantRest
    :   variableDeclarators
    ;
    
defaultValue
    :   DEFAULT^ elementValue
    ;

// STATEMENTS / BLOCKS

block
    :   LEFT_CURLY blockStatement* RIGHT_CURLY
        -> ^(BLOCK[$LEFT_CURLY] blockStatement* RIGHT_CURLY)
    ;
    
blockStatement
    :   localVariableDeclarationStatement
    |   classOrInterfaceDeclaration[null] -> ^(INNER_DECLARATION classOrInterfaceDeclaration)
    |   statement
    ;

localVariableDeclarationStatement
    :    localVariableDeclaration SEMI_COLON!
    ;

localVariableDeclaration
    :   variableModifiers type variableDeclarators
        -> ^(VARIABLE_DECLARATION variableModifiers type variableDeclarators)
    ;
    
variableModifiers
    :   elements+=variableModifier*
        -> {    buildModifiersAST($elements, null)    }
    ;

statement
    :   block
    |   ASSERT^ expressionWrapper (COLON! expressionWrapper)? SEMI_COLON!
    |   IF booleanExpression st1=statement (options {k=1;}:ELSE st2=statement)?
        -> {$ELSE == null}? ^(IF booleanExpression $st1)
        -> ^(IF booleanExpression $st1 ^(ELSE $st2))
    |   FOR LEFT_PAREN forControl RIGHT_PAREN statement
        -> {$forControl.isEnhancedFor}? ^(ENHANCED_FOR[$FOR] forControl statement)
        -> ^(FOR forControl statement) 
    |   WHILE^ booleanExpression statement
    |   DO statement WHILE booleanExpression SEMI_COLON
        -> ^(DO statement ^(WHILE booleanExpression))
    |   TRY bl1=block
        ( catches fnl=FINALLY bl2=block
        | catches
        | fnl=FINALLY bl2=block 
        )
        -> {fnl == null}? ^(TRY $bl1 catches)
        -> ^(TRY $bl1 catches? ^(FINALLY $bl2))
    |   SWITCH^ parExpression LEFT_CURLY! switchBlockStatementGroups RIGHT_CURLY!
    |   SYNCHRONIZED^ parExpression block
    |   RETURN^ expressionWrapper? SEMI_COLON!
    |   THROW^ expressionWrapper SEMI_COLON!
    |   BREAK id=Identifier? SEMI_COLON
        -> {id == null}? ^(BREAK)
        -> ^(BREAK Identifier)
    |   CONTINUE id=Identifier? SEMI_COLON
        -> {id == null}? ^(CONTINUE)
        -> ^(CONTINUE Identifier)
    |   SEMI_COLON -> EMPTY_STATEMENT[$SEMI_COLON]
    |   statementExpression SEMI_COLON!
    |   Identifier COLON statement
        -> ^(LABEL_DECLARE[$COLON] Identifier statement)
    ;
    
catches
    :   catchClause+
        -> ^(CATCHES catchClause+)
    ;

catchClause
    :   CATCH LEFT_PAREN formalParameter RIGHT_PAREN block
        -> ^(CATCH ^(FORMAL_PARAMETERS[$LEFT_PAREN] formalParameter) block)
    ;

formalParameter
    :   variableModifiers type Identifier arrayDimensionDeclaration?
        -> ^(SINGLE_VARIABLE_DECLARATION Identifier variableModifiers type arrayDimensionDeclaration?)
    ;
        
switchBlockStatementGroups
    :   switchBlockStatementGroup*
    ;
    
/* The change here (switchLabel -> switchLabel+) technically makes this grammar
   ambiguous; but with appropriately greedy parsing it yields the most
   appropriate AST, one in which each group, except possibly the last one, has
   labels and statements. */
switchBlockStatementGroup
    :   switchLabel+ blockStatement*
        -> ^(SWITCH_BLOCK switchLabel+ blockStatement*)
    ;
    
switchLabel
    :   CASE constantExpression COLON -> ^(CASE_CONSTANT[$CASE] constantExpression)
    |   CASE Identifier COLON -> ^(CASE_ENUM[$CASE] Identifier)
    |   DEFAULT COLON!
    ;
    
forControl returns [boolean isEnhancedFor]
options {k=3;} // be efficient for common case: for (ID ID : ID) ...
    :   enhancedForControl {$isEnhancedFor = true;}
    |   forInit? SEMI_COLON forBooleanExpression? SEMI_COLON forUpdate? {$isEnhancedFor = false;}
        -> ^(FOR_INIT forInit?) ^(FOR_CONDITION forBooleanExpression?) ^(FOR_UPDATE forUpdate?)
    ;

forInit
    :   localVariableDeclaration
	|	expressionList
    ;
    
enhancedForControl
    :   variableModifiers type Identifier COLON expressionWrapper
        -> ^(VARIABLE_DECLARATION variableModifiers type ^(VARIABLE_DECLARATION_FRAGMENT Identifier)) expressionWrapper
    ;

forUpdate
    :   expressionList
    ;

// EXPRESSIONS

booleanExpression
    :   LEFT_PAREN expression RIGHT_PAREN -> ^(BOOLEAN_EXPRESSION expression)
    ;

forBooleanExpression
    :   expression -> ^(BOOLEAN_EXPRESSION expression)
    ;

parExpression
    :   LEFT_PAREN! expressionWrapper RIGHT_PAREN!
    ;

expressionList
    :   expressionWrapper (COMMA expressionWrapper)*
        -> ^(EXPR_LIST expressionWrapper+)
    ;

statementExpression
    :   expressionWrapper
        -> ^(STATEMENT_EXPRESSION expressionWrapper)
    ;
    
constantExpression
    :   expressionWrapper
    ;

expressionWrapper
    :    expression -> ^(EXPRESSION expression)
    ;

expression
    :   conditionalExpression (assignmentOperator^ expression)?
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
    |   (LESS LESS ASSIGN)=> t1=LESS t2=LESS t3=ASSIGN 
        { $t1.getLine() == $t2.getLine() &&
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
          -> LEFT_SHIFT_ASSIGN[$t1, "<<="]
    |   (GREATER GREATER GREATER ASSIGN)=> t1=GREATER t2=GREATER t3=GREATER t4=ASSIGN
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&
          $t3.getLine() == $t4.getLine() && 
          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() }?
          -> UNSIGNED_RIGHT_SHIFT_ASSIGN[$t1, ">>>="]
    |   (GREATER GREATER ASSIGN)=> t1=GREATER t2=GREATER t3=ASSIGN
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
          -> SIGNED_RIGHT_SHIFT_ASSIGN[$t1, ">>="]
    ;

conditionalExpression
    :   conditionalOrExpression ( QUESTION expressionWrapper COLON expressionWrapper )?
        -> {$QUESTION == null}? conditionalOrExpression
        -> ^(CONDITIONAL_EXPRESSION[$QUESTION] conditionalOrExpression expressionWrapper+)
    ;

conditionalOrExpression
    :   conditionalAndExpression ( DOUPLE_PIPE^ conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( DOUBLE_AMPERSAND^ inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( PIPE^ exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( CIRCUMFLEX^ andExpression )*
    ;

andExpression
    :   equalityExpression ( AMPERSAND^ equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( (EQUALS^ | EXCLAMATION_EQUALS^) instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression (INSTANCEOF^ type)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp^ shiftExpression )*
    ;
    
relationalOp
    :   (LESS ASSIGN)=> t1=LESS t2=ASSIGN 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
          -> LESS_EQUALS[$t1, "<="]
    |   (GREATER ASSIGN)=> t1=GREATER t2=ASSIGN 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
          -> GREATER_EQUALS[$t1, ">="]
    |   LESS
    |   GREATER 
    ;

shiftExpression
    :   additiveExpression ( shiftOp^ additiveExpression )*
    ;

shiftOp
    :   (LESS LESS)=> t1=LESS t2=LESS
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
          -> LEFT_SHIFT[$t1, "<<"]
    |   (GREATER GREATER GREATER)=> t1=GREATER t2=GREATER t3=GREATER 
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
          -> UNSIGNED_RIGHT_SHIFT[$t1, ">>>"]
    |   (GREATER GREATER)=> t1=GREATER t2=GREATER
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
          -> SIGNED_RIGHT_SHIFT[$t1, ">>"]
    ;


additiveExpression
    :   multiplicativeExpression ( (PLUS^ | MINUS^) multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( STAR^ | SLASH^ | PERCENT^ ) unaryExpression )*
    ;
    
unaryExpression
    :   PLUS unaryExpression -> ^(PREFIX_EXPRESSION ^(UNARY_PLUS[$PLUS] unaryExpression))
    |   MINUS unaryExpression -> ^(PREFIX_EXPRESSION ^(UNARY_MINUS[$MINUS] unaryExpression))
    |   DOUBLE_PLUS unaryExpression -> ^(PREFIX_EXPRESSION ^(DOUBLE_PLUS unaryExpression))
    |   DOUBLE_MINUS unaryExpression -> ^(PREFIX_EXPRESSION ^(DOUBLE_MINUS unaryExpression))
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
@init    {    Object previusTree = null; boolean isOk = true;    }
    :   TILDE unaryExpression -> ^(PREFIX_EXPRESSION ^(TILDE unaryExpression))
    |   EXCLAMATION unaryExpression -> ^(PREFIX_EXPRESSION ^(EXCLAMATION unaryExpression))
    |   castExpression
    |   primary {previusTree = $primary.tree; isOk = $primary.isOk;} (sel=selector[isOk, previusTree] {previusTree = $sel.tree; isOk = true;})* (DOUBLE_PLUS|DOUBLE_MINUS)?
        -> { buildUnaryAST($DOUBLE_PLUS, $DOUBLE_MINUS, isOk, previusTree) }
    ;

castExpression
    :  LEFT_PAREN primitiveType RIGHT_PAREN unaryExpression
         -> ^(CAST_TYPE[$LEFT_PAREN] ^(PRIMITIVE_TYPE primitiveType) unaryExpression)
    |  LEFT_PAREN (tp=type | expressionWrapper) RIGHT_PAREN unaryExpressionNotPlusMinus
         -> {$tp.tree != null }? ^(CAST_TYPE[$LEFT_PAREN] type unaryExpressionNotPlusMinus)
         -> ^(CAST_EXPRESSION[$LEFT_PAREN] expressionWrapper unaryExpressionNotPlusMinus)
    ;

primary returns [boolean isOk]
scope {
    Object qualifiedName;
}
@init    {    $isOk = true;    }
    :   LEFT_PAREN expression RIGHT_PAREN -> ^(PARENTHESIZED_EXPRESSION expression)
    |   tqn=thisQualifiedName { $primary::qualifiedName = $tqn.tree; } identifierSuffix?
        {    if ($identifierSuffix.tree == null) $isOk = false;    }
        -> {    buildQualifiedPrimaryAST($tqn.tree, $identifierSuffix.tree)    } 
    |   SUPER superSuffix
        -> {$superSuffix.mode == 0}? ^(SUPER_CONSTRUCTOR_INVOCATION[$SUPER] superSuffix)
        -> {$superSuffix.mode == 1}? ^(SUPER_FIELD_ACCESS[$SUPER] superSuffix)
        -> ^(SUPER_METHOD_INVOCATION[$SUPER] superSuffix)
    |   literal
    |   NEW creator
         -> {$creator.mode == 1}? ^(ARRAY_CREATION[$NEW] creator)
         -> ^(CLASS_INSTANCE_CREATION[$NEW] creator)
    |   qn=qualifiedName { $primary::qualifiedName = $qn.tree; } identifierSuffix?
        {    if ($identifierSuffix.tree == null) $isOk = false;    }
        -> {    buildQualifiedPrimaryAST($qn.tree, $identifierSuffix.tree)    }
    |   primitiveType arrayDimensionDeclaration? DOT CLASS
         -> {$arrayDimensionDeclaration.tree != null}? ^(TYPE_LITERAL ^(ARRAY_TYPE ^(PRIMITIVE_TYPE primitiveType) arrayDimensionDeclaration) DOT CLASS)
         -> ^(TYPE_LITERAL ^(PRIMITIVE_TYPE primitiveType) DOT CLASS)
    |   VOID DOT CLASS -> ^(TYPE_LITERAL ^(SIMPLE_TYPE VOID) DOT CLASS)
    ;

identifierSuffix
    :   arrayDimensionDeclaration DOT CLASS
        -> {    buildIdentifierSuffixTypeLiteral($DOT, $CLASS, $arrayDimensionDeclaration.tree, $primary::qualifiedName)    }
    |   arguments 
        -> {    buildIdentifierSuffixMethodInvocation($primary::qualifiedName, $arguments.tree)    }
    |   DOT CLASS
        -> {    buildIdentifierSuffixTypeLiteral($DOT, $CLASS, null, $primary::qualifiedName)    }
    |   DOT explicitGenericInvocation
        -> {    buildIdentifierSuffixMethodGenericInvocation($DOT, $explicitGenericInvocation.tree, $primary::qualifiedName)    }
    ;

selector[boolean isParameterOk, Object parameter]
@init    {    int mode = 0;    }
    :   DOT Identifier (arguments { mode = 1; })?
    -> {mode == 1}? {    buildSelectorMethodInvocationAST($DOT, $Identifier, $arguments.tree, $isParameterOk, $parameter)    }
    ->  {    buildSelectorQualifiedNameAST($DOT, $Identifier, $isParameterOk, $parameter)    }
    |   DOT THIS
    ->  {    buildSelectorThisExpressionAST($DOT, $THIS, $isParameterOk, $parameter)    }
    |   DOT SUPER superSuffix
    -> {$superSuffix.mode == 0}? { buildSelectorSuperConstructorInvocationAST($DOT, $SUPER, $superSuffix.tree, $isParameterOk, $parameter) }
    -> {$superSuffix.mode == 1}? { buildSelectorSuperFieldAccessAST($DOT, $SUPER, $superSuffix.tree, $isParameterOk, $parameter) }
    -> { buildSelectorSuperMethodInvocationAST($DOT, $SUPER, $superSuffix.tree, $isParameterOk, $parameter) }
    |   DOT NEW innerCreator
    -> { buildSelectorClassInstanceCreationAST($DOT, $NEW, $innerCreator.tree, $isParameterOk, $parameter) }
    |   dimensionValue
    -> { buildSelectorArrayAccessAST($dimensionValue.tree, $isParameterOk, $parameter) }
    ;

creator returns [int mode]
@init    {    $mode = 0;    }
    :   nonWildcardTypeArguments createdName classCreatorRest -> ^(PARAMETERIZED_TYPE createdName nonWildcardTypeArguments) classCreatorRest
    |   createdName (arrayCreatorRest {$mode = 1;} | classCreatorRest)
    ;

createdName
    :   classOrInterfaceTypeQualifed
    |   primitiveType -> ^(PRIMITIVE_TYPE primitiveType)
    ;

innerCreator
    :   nonWildcardTypeArguments? Identifier classCreatorRest
    -> {$nonWildcardTypeArguments.tree == null}? ^(SIMPLE_TYPE Identifier) classCreatorRest
    -> ^(PARAMETERIZED_TYPE ^(SIMPLE_TYPE Identifier) nonWildcardTypeArguments) classCreatorRest
    ;

arrayCreatorRest
    :   LEFT_SQUARE
        (   RIGHT_SQUARE arrayEmptyDimension* arrayInitializer -> DIMENSION[$LEFT_SQUARE] arrayEmptyDimension* arrayInitializer
        |   expressionWrapper RIGHT_SQUARE dimensionValue* arrayEmptyDimension* -> ^(DIMENSION[$LEFT_SQUARE] expressionWrapper) dimensionValue* arrayEmptyDimension*
        )
    ;

arrayEmptyDimension
    :    LEFT_SQUARE RIGHT_SQUARE
        -> DIMENSION[$LEFT_SQUARE]
    ;

classCreatorRest
@init    {int mode = 0;}
    :   arguments (classBody {mode = 1;})?
        -> {mode == 1}? arguments ^(ANONYMOUS_CLASS_DECLARATION classBody)
        -> arguments
    ;
    
explicitGenericInvocation
    :   nonWildcardTypeArguments Identifier arguments -> Identifier nonWildcardTypeArguments arguments
    ;
    
nonWildcardTypeArguments
    :   LESS typeList GREATER
        -> ^(TYPE_ARGUMENTS[$LESS] typeList)
    ;

dimensionValue
    :    LEFT_SQUARE expressionWrapper RIGHT_SQUARE
        -> ^(DIMENSION[$LEFT_SQUARE] expressionWrapper)
    ;

superSuffix returns [int mode]
    :   arguments {$mode = 0;}
    |   DOT Identifier {$mode = 1;} (arguments {$mode = 2;})?
    ;

arguments
    :   LEFT_PAREN expressionList? RIGHT_PAREN
        -> ^(ARGUMENTS[$LEFT_PAREN] expressionList?)
    ;
