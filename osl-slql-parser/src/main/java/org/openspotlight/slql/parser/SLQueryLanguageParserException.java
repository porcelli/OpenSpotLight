package org.openspotlight.slql.parser;

import org.openspotlight.common.exception.SLException;

public class SLQueryLanguageParserException extends SLException {

    /**
     * 
     */
    private static final long serialVersionUID = 400L;

    private String            errorCode        = null;
    private int               lineNumber;
    private int               column;
    private int               offset;

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    public SLQueryLanguageParserException(
                                           final String message ) {
        super(message);
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    public SLQueryLanguageParserException(
                                           final String message,
                                           final Throwable cause ) {
        super(message);
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    public SLQueryLanguageParserException(
                                           final Throwable cause ) {
        super(cause);
    }

    /**
     * HibernateParserException constructor.
     * 
     * @param errorCode error code
     * @param message message
     * @param lineNumber line number
     * @param column column
     * @param offset offset
     * @param cause exception cause
     */
    public SLQueryLanguageParserException(
                                           String errorCode,
                                           String message,
                                           int lineNumber,
                                           int column,
                                           int offset,
                                           Throwable cause ) {
        super(message, cause);
        this.errorCode = errorCode;
        this.lineNumber = lineNumber;
        this.column = column;
        this.offset = offset;
    }

    public String getMessage() {
        if (null == errorCode) {
            return super.getMessage();
        }
        return "[" + errorCode + "] " + super.getMessage();
    }

    /**
     * getter for error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * getter for line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * getter for column position
     */
    public int getColumn() {
        return column;
    }

    /**
     * getter for char offset
     */
    public int getOffset() {
        return offset;
    }

}
