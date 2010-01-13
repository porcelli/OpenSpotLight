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
lexer grammar JavaLexer;

options {
	superClass = SLLexer;
}

@header {
package org.openspotlight.bundle.language.java.parser;

import org.openspotlight.bundle.common.parser.SLLexer;
import org.openspotlight.bundle.language.java.parser.executor.JavaLexerExecutor;
}

@members {
	private boolean 			enumIsKeyword 	= true;
	private boolean 			assertIsKeyword = true;
	private JavaLexerExecutor 	executor 		= null;
	
	public void setEnumIsKeyword(boolean isKeyword){
		this.enumIsKeyword = isKeyword;
	}

	public void setAssertIsKeyword(boolean isKeyword){
		this.assertIsKeyword = isKeyword;
	}

	public void setLexerExecutor(final JavaLexerExecutor executor){
		this.executor = executor;
	}
}

THREE_DOTS
    :    '...'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    
    ;

AMPERSAND_ASSIGN
    :    '&='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

EQUALS
    :    '=='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

EXCLAMATION_EQUALS
    :    '!='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CIRCUMFLEX_ASSIGN
    :    '^='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DOUBLE_PLUS
    :    '++'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DOUBLE_MINUS
    :    '--'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PLUS_ASSIGN
    :    '+='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

MINUS_ASSIGN
    :    '-='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

STAR_ASSIGN
    :    '*='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SLASH_ASSIGN
    :    '/='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PIPE_ASSIGN
    :    '|='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PERCENT_ASSIGN
    :    '%='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DOUPLE_PIPE
    :    '||'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DOUBLE_AMPERSAND
    :    '&&'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DOT
    :    '.'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

STAR
    :    '*'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SEMI_COLON
    :    ';'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

AMPERSAND
    :    '&'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

COLON
    :    ':'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

COMMA
    :    ','
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

LESS
    :    '<'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

GREATER
    :    '>'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

LEFT_CURLY
    :    '{'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

RIGHT_CURLY
    :    '}'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

LEFT_SQUARE
    :    '['
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

RIGHT_SQUARE
    :    ']'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

ASSIGN
    :    '='
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

QUESTION
    :    '?'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

LEFT_PAREN
    :    '('
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

RIGHT_PAREN
    :    ')'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

AT
    :    '@'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PLUS
    :    '+'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

MINUS
    :    '-'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PERCENT
    :    '%'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SLASH
    :    '/'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PIPE
    :    '|'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

EXCLAMATION
    :    '!'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CIRCUMFLEX
    :    '^'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

TILDE
    :    '~'
        {	executor.addSyntaxHighlightSymbol(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

ABSTRACT
    :    'abstract'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

BOOLEAN
    :    'boolean'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

BREAK
    :    'break'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

BYTE
    :    'byte'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CASE
    :    'case'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CATCH
    :    'catch'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CHAR
    :    'char'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CLASS
    :    'class'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

CONTINUE
    :    'continue'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DEFAULT
    :    'default'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DO
    :    'do'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

DOUBLE
    :    'double'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

ELSE
    :    'else'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

EXTENDS
    :    'extends'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

FALSE
    :    'false'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

FINALLY
    :    'finally'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

FINAL
    :    'final'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

FLOAT
    :    'float'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

FOR
    :    'for'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

IF
    :    'if'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

IMPLEMENTS
    :    'implements'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

IMPORT
    :    'import'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

INSTANCEOF
    :    'instanceof'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

INTERFACE
    :    'interface'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

INT
    :    'int'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

LONG
    :    'long'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

NATIVE
    :    'native'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

NEW
    :    'new'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

NULL
    :    'null'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PACKAGE
    :    'package'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PRIVATE
    :    'private'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PROTECTED
    :    'protected'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

PUBLIC
    :    'public'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

RETURN
    :    'return'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SHORT
    :    'short'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

STATIC
    :    'static'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

STRICTFP
    :    'strictfp'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SUPER
    :    'super'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SWITCH
    :    'switch'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

SYNCHRONIZED
    :    'synchronized'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

THIS
    :    'this'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

THROWS
    :    'throws'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

THROW
    :    'throw'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

TRANSIENT
    :    'transient'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

TRUE
    :    'true'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

TRY
    :    'try'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

VOID
    :    'void'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

VOLATILE
    :    'volatile'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

WHILE
    :    'while'
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

HexLiteral : '0' ('x'|'X') HexDigit+ IntegerTypeSuffix? 
    {	executor.addSyntaxHighlightNumberLiteral(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
	;

DecimalLiteral : ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix? 
    {	executor.addSyntaxHighlightNumberLiteral(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
	;

OctalLiteral : '0' ('0'..'7')+ IntegerTypeSuffix? 
    {	executor.addSyntaxHighlightNumberLiteral(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
	;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
IntegerTypeSuffix : ('l'|'L') ;

FloatingPointLiteral
    :  ( ('0'..'9')+ DOT ('0'..'9')* Exponent? FloatTypeSuffix? 
    |   DOT ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix )
    {	executor.addSyntaxHighlightNumberLiteral(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

fragment
Exponent : ('e'|'E') (PLUS|MINUS)? ('0'..'9')+ ;

fragment
FloatTypeSuffix : ('f'|'F'|'d'|'D') ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    {	executor.addSyntaxHighlightNumberLiteral(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

StringLiteral
    :  '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    {	executor.addSyntaxHighlightStringLiteral(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

ENUM:   'enum' {if (!enumIsKeyword) $type=Identifier;}
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;
    
ASSERT
    :   'assert' {if (!assertIsKeyword) $type=Identifier;}
    {	executor.addSyntaxHighlightReserved(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;
    
Identifier 
    :   Letter (Letter|JavaIDDigit)*
{	executor.addSyntaxHighlightIdentifier(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getCharPositionInLine());	}
    ;

/**I found this char range in JavaCC's grammar, but Letter and Digit overlap.
   Still works, but...
 */
fragment
Letter
    :  '\u0024' |
       '\u0041'..'\u005a' |
       '\u005f' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;

fragment
JavaIDDigit
    :  '\u0030'..'\u0039' |
       '\u0660'..'\u0669' |
       '\u06f0'..'\u06f9' |
       '\u0966'..'\u096f' |
       '\u09e6'..'\u09ef' |
       '\u0a66'..'\u0a6f' |
       '\u0ae6'..'\u0aef' |
       '\u0b66'..'\u0b6f' |
       '\u0be7'..'\u0bef' |
       '\u0c66'..'\u0c6f' |
       '\u0ce6'..'\u0cef' |
       '\u0d66'..'\u0d6f' |
       '\u0e50'..'\u0e59' |
       '\u0ed0'..'\u0ed9' |
       '\u1040'..'\u1049'
   ;

WS  :  (' '|'\r'|'\t'|'\u000C'|'\n') {$channel=HIDDEN;}
    ;

COMMENT
    :   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    {	executor.addSyntaxHighlightMultiLineComment(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getLine(), input.getCharPositionInLine());	}
    ;

LINE_COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    {	executor.addSyntaxHighlightMultiLineComment(state.tokenStartLine, state.tokenStartCharPositionInLine, input.getLine(), input.getCharPositionInLine());	}
    ;
