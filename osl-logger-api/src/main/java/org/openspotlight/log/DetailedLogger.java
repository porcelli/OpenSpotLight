/*
 * 
 */
package org.openspotlight.log;

import java.io.Serializable;

import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * This interface describes the Detailed Logger. This logger should be used to log information related to the {@link SLNode}
 * subtypes or {@link ConfigurationNode} subtypes.
 * 
 * @author feu
 */
public interface DetailedLogger {

    /**
     * The ErrorCode describes some special kind of errors.
     */
    public static interface ErrorCode extends SimpleNodeType, Serializable {

        /**
         * Gets the description.
         * 
         * @return the description
         */
        public String getDescription();

        /**
         * Gets the error code.
         * 
         * @return the error code
         */
        public String getErrorCode();

    }

    /**
     * The EventType.
     */
    public static enum LogEventType {

        /** The TRACE. */
        TRACE,

        /** The DEBUG. */
        DEBUG,

        /** The INFO. */
        INFO,

        /** The WARN. */
        WARN,

        /** The ERROR. */
        ERROR,

        /** The FATAL. */
        FATAL
    }

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log( String user,
                     LogEventType type,
                     ErrorCode errorCode,
                     String detailedMessage,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log( String user,
                     LogEventType type,
                     ErrorCode errorCode,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log( String user,
                     LogEventType type,
                     String message,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log( String user,
                     LogEventType type,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log( String user,
                     String repository,
                     LogEventType type,
                     ErrorCode errorCode,
                     String detailedMessage,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log( String user,
                     String repository,
                     LogEventType type,
                     ErrorCode errorCode,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log( String user,
                     String repository,
                     LogEventType type,
                     String message,
                     LogableObject... anotherNodes );

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log( String user,
                     String repository,
                     LogEventType type,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes );

}
