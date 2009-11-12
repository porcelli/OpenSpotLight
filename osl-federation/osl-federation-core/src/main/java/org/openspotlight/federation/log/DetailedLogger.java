/*
 * 
 */
package org.openspotlight.federation.log;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Session;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.jcr.LogableObject;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
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
    public static interface ErrorCode {

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
     * The Factory used to create {@link DetailedLogger}.
     */
    public static final class Factory {

        /**
         * The JcrDetailedLogger is an implementation of {@link DetailedLogger} based on Jcr. This kind of logger was implemented
         * 'by hand' instead of using {@link SLNode} or {@link ConfigurationNode} by a simple reason. Should not be possible to
         * log the log. If it's necessary this implementation could be changed on the future.
         */
        private static final class JcrDetailedLogger implements DetailedLogger {

            /** The session. */
            private final Session session;

            public JcrDetailedLogger(
                                      final Session session ) {
                this.session = session;
            }

            public void log( final String user,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {

            }

            public void log( final String user,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                log(user, null, type, errorCode, message, detailedMessage, anotherNodes);

            }

            public void log( final String user,
                             final LogEventType type,
                             final String message,
                             final LogableObject... anotherNodes ) {
                log(user, null, type, null, message, null, anotherNodes);

            }

            public void log( final String user,
                             final LogEventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                log(user, null, type, null, message, detailedMessage, anotherNodes);

            }

            public void log( final String user,
                             final String repository,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                log(user, repository, type, errorCode, null, detailedMessage, anotherNodes);

            }

            public void log( final String user,
                             final String repository,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

            public void log( final String user,
                             final String repository,
                             final LogEventType type,
                             final String message,
                             final LogableObject... anotherNodes ) {
                log(user, repository, type, null, message, null, anotherNodes);

            }

            public void log( final String user,
                             final String repository,
                             final LogEventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                log(user, repository, type, null, message, detailedMessage, anotherNodes);
            }

        }

        /**
         * Creates the jcr detailed logger.
         * 
         * @param session the session
         * @return the detailed logger
         */
        public static DetailedLogger createJcrDetailedLogger( final Session session ) {
            return new JcrDetailedLogger(session);
        }
    }

    /**
     * The Class LogEntry is used to represent a new log entry.
     */
    public static class LogEntry implements SimpleNodeType, Serializable {

        /**
         * The Class LoggedObjectInformation is used to represent objects related to a given log.
         */
        public static class LoggedObjectInformation {

            /**
             * Gets the hierarchy from.
             * 
             * @param o the o
             * @return the hierarchy from
             */
            private static List<LogableObject> getHierarchyFrom( final LogableObject o ) {
                final List<LogableObject> result = new LinkedList<LogableObject>();
                result.add(o);
                LogableObject parent = getParent(o);
                while (parent != null) {
                    result.add(parent);
                    parent = getParent(parent);
                }
                return result;
            }

            /**
             * Gets the hierarchy from.
             * 
             * @param node the node
             * @param anotherNodes the another nodes
             * @return the hierarchy from
             */
            public static List<LoggedObjectInformation> getHierarchyFrom( final LogableObject node,
                                                                          final LogableObject... anotherNodes ) {
                final List<LogableObject> nodes = new LinkedList<LogableObject>();
                nodes.addAll(getHierarchyFrom(node));
                for (final LogableObject o : anotherNodes) {
                    nodes.addAll(getHierarchyFrom(o));
                }
                Collections.reverse(nodes);
                final List<LoggedObjectInformation> result = new ArrayList<LoggedObjectInformation>(nodes.size());
                for (int i = 0, size = nodes.size(); i < size; i++) {
                    result.add(new LoggedObjectInformation(i, nodes.get(i)));
                }
                return result;
            }

            /**
             * Gets the parent.
             * 
             * @param o the o
             * @return the parent
             */
            private static LogableObject getParent( final LogableObject o ) {
                if (o instanceof SLNode) {
                    final SLNode node = (SLNode)o;
                    try {
                        return node.getParent();
                    } catch (final SLGraphSessionException e) {
                        throw logAndReturnNew(e, SLRuntimeException.class);
                    }
                } else {
                    return null;// other types have the path information. Now the parent nodes isn't necessary
                }
            }

            /** The order. */
            private final int    order;

            /** The unique id. */
            private final String uniqueId;

            /** The friendly description. */
            private final String friendlyDescription;

            /** The class name. */
            private final String className;

            /**
             * Instantiates a new logged object information.
             * 
             * @param order the order
             * @param object the object
             */
            LoggedObjectInformation(
                                     final int order, final LogableObject object ) {
                this.order = order;
                if (object instanceof SLNode) {
                    final SLNode node = (SLNode)object;
                    try {
                        this.uniqueId = node.getID();
                    } catch (final SLGraphSessionException e) {
                        throw logAndReturnNew(e, SLRuntimeException.class);
                    }
                    this.friendlyDescription = node.toString();
                    this.className = node.getClass().getInterfaces()[0].getName();
                } else if (object instanceof ArtifactSource) {
                    final ArtifactSource node = (ArtifactSource)object;
                    this.friendlyDescription = node.getUniqueReference();
                    this.className = node.getClass().getName();
                    this.uniqueId = null;
                } else if (object instanceof Artifact) {
                    final Artifact node = (Artifact)object;
                    this.friendlyDescription = node.getArtifactCompleteName();
                    this.className = node.getClass().getName();
                    this.uniqueId = null;
                } else {
                    throw logAndReturn(new IllegalArgumentException());
                }
                checkNotEmpty("uniqueId", this.uniqueId);
                checkNotEmpty("friendlyDescription", this.friendlyDescription);
                checkNotEmpty("className", this.className);
            }

            /**
             * Instantiates a new logged object information.
             * 
             * @param order the order
             * @param uniqueId the unique id
             * @param className the class name
             * @param friendlyDescription the friendly description
             */
            LoggedObjectInformation(
                                     final int order, final String uniqueId, final String className,
                                     final String friendlyDescription ) {
                checkNotEmpty("uniqueId", uniqueId);
                checkNotEmpty("friendlyDescription", friendlyDescription);
                checkNotEmpty("className", className);
                this.order = order;
                this.uniqueId = uniqueId;
                this.friendlyDescription = friendlyDescription;
                this.className = className;

            }

            /**
             * Gets the friendly description.
             * 
             * @return the friendly description
             */
            public String getFriendlyDescription() {
                return this.friendlyDescription;
            }

            /**
             * Gets the order.
             * 
             * @return the order
             */
            public int getOrder() {
                return this.order;
            }

            /**
             * Gets the type name.
             * 
             * @return the type name
             */
            public String getTypeName() {
                return this.className;
            }

            /**
             * Gets the unique id.
             * 
             * @return the unique id
             */
            public String getUniqueId() {
                return this.uniqueId;
            }

        }

        /** The error code. */
        private final ErrorCode                     errorCode;

        /** The type. */
        private final LogEventType                  type;

        /** The message. */
        private final String                        message;

        /** The detailed message. */
        private final String                        detailedMessage;

        /** The nodes. */
        private final List<LoggedObjectInformation> nodes;

        /** The date. */
        private final Date                          date;

        /** The hash code. */
        private final int                           hashCode;

        /**
         * Instantiates a new log entry.
         * 
         * @param errorCode the error code
         * @param date the date
         * @param type the type
         * @param message the message
         * @param detailedMessage the detailed message
         * @param nodes the nodes
         */
        private LogEntry(
                          final ErrorCode errorCode, final Date date, final LogEventType type, final String message,
                          final String detailedMessage, final List<LoggedObjectInformation> nodes ) {
            this.errorCode = errorCode;
            this.type = type;
            this.message = message;
            this.detailedMessage = detailedMessage;
            this.nodes = Collections.unmodifiableList(nodes);
            this.date = date;
            this.hashCode = hashOf(this.type, this.message, this.detailedMessage, this.nodes, this.date, this.errorCode);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( final Object obj ) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LogEntry)) {
                return false;
            }
            final LogEntry that = (LogEntry)obj;
            return eachEquality(of(this.type, this.message, this.detailedMessage, this.nodes, this.date, this.errorCode),
                                andOf(that.type, that.message, that.detailedMessage, that.nodes, that.date, that.errorCode));
        }

        /**
         * Gets the date.
         * 
         * @return the date
         */
        public Date getDate() {
            return this.date;
        }

        /**
         * Gets the detailed message.
         * 
         * @return the detailed message
         */
        public String getDetailedMessage() {
            return this.detailedMessage;
        }

        /**
         * Gets the error code.
         * 
         * @return the error code
         */
        public ErrorCode getErrorCode() {
            return this.errorCode;
        }

        /**
         * Gets the message.
         * 
         * @return the message
         */
        public String getMessage() {
            return this.message;
        }

        /**
         * Gets the nodes.
         * 
         * @return the nodes
         */
        public List<LoggedObjectInformation> getNodes() {
            return this.nodes;
        }

        /**
         * Gets the type.
         * 
         * @return the type
         */
        public LogEventType getType() {
            return this.type;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.hashCode;
        }

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
