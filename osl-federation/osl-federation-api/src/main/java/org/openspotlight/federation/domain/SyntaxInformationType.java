package org.openspotlight.federation.domain;

/**
 * Enum to guard syntax information type.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public enum SyntaxInformationType {

    /** Comment or multi line comment. */
    COMMENT,

    /** Reserved keyword. */
    RESERVED,

    /** Number literal. */
    NUMBER_LITERAL,

    /** String literal. */
    STRING_LITERAL,

    /** Variable identifier. */
    IDENTIFIER,

    /** Symbol, such as +, -, /, ... */
    SYMBOL,

    /** Hidden information on source code, such as form information on VB code. */
    HIDDEN
}