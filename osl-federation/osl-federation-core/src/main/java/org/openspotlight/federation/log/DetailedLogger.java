package org.openspotlight.federation.log;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.jcr.LogableObject;
import org.openspotlight.common.util.Dates;
import org.openspotlight.federation.log.DetailedLogger.LogEntry.LoggedObjectInformation;

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

        public String getDescription();

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
            private final Session       session;

            /** The Constant NODE_LOG. */
            private static final String NODE_LOG                                = "osl:log";

            /** The Constant NODE_LOG_ENTRY. */
            private static final String NODE_LOG_ENTRY                          = "osl:logEntry";

            /** The Constant NODE_OBJ_INFO. */
            private static final String NODE_OBJ_INFO                           = "osl:objectInfo";

            /** The Constant PROPERTY_LOG_ENTRY__EVENT_TYPE. */
            private static final String PROPERTY_LOG_ENTRY__EVENT_TYPE          = "osl:eventType";

            /** The Constant PROPERTY_LOG_ENTRY__DATE. */
            private static final String PROPERTY_LOG_ENTRY__DATE                = "osl:date";

            /** The Constant PROPERTY_LOG_ENTRY__ERROR_CODE. */
            private static final String PROPERTY_LOG_ENTRY__ERROR_CODE          = "osl:errorCode";

            /** The Constant PROPERTY_LOG_ENTRY__MESSAGE. */
            private static final String PROPERTY_LOG_ENTRY__MESSAGE             = "osl:message";

            /** The Constant PROPERTY_LOG_ENTRY__DETAILED_MESSAGE. */
            private static final String PROPERTY_LOG_ENTRY__DETAILED_MESSAGE    = "osl:detailedMessage";

            /** The Constant PROPERTY_OBJ_INFO__TYPE_NAME. */
            private static final String PROPERTY_OBJ_INFO__TYPE_NAME            = "osl:typeName";

            /** The Constant PROPERTY_OBJ_INFO__FRIENDLY_DESCRIPTION. */
            private static final String PROPERTY_OBJ_INFO__FRIENDLY_DESCRIPTION = "osl:friendlyDescription";

            /** The Constant PROPERTY_OBJ_INFO__UNIQUE_ID. */
            private static final String PROPERTY_OBJ_INFO__UNIQUE_ID            = "osl:uniqueId";

            /** The Constant PROPERTY_OBJ_INFO__ORDER. */
            private static final String PROPERTY_OBJ_INFO__ORDER                = "osl:order";

            /**
             * Instantiates a new jcr detailed logger.
             * 
             * @param session the session
             */
            public JcrDetailedLogger(
                                      final Session session ) {
                checkNotNull("session", session);
                checkCondition("sessionAlive", session.isLive());
                this.session = session;
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#findLogByDateInterval(java.util.Date, java.util.Date)
             */
            public List<LogEntry> findLogByDateInterval( final Date start,
                                                         final Date end ) {
                return findLogByParameters(null, null, null, start, end);
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#findLogByErrorCode(org.openspotlight.federation.log.DetailedLogger.ErrorCode)
             */
            public List<LogEntry> findLogByErrorCode( final ErrorCode code ) {
                return findLogByParameters(null, null, code, null, null);
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#findLogByEventType(org.openspotlight.federation.log.DetailedLogger.EventType)
             */
            public List<LogEntry> findLogByEventType( final LogEventType eventType ) {
                return findLogByParameters(eventType, null, null, null, null);
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#findLogByLogableObject(org.openspotlight.common.jcr.LogableObject)
             */
            public List<LogEntry> findLogByLogableObject( final LogableObject object ) {
                return findLogByParameters(null, object, null, null, null);
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#findLogByParameters(org.openspotlight.federation.log.DetailedLogger.EventType, org.openspotlight.common.jcr.LogableObject, org.openspotlight.federation.log.DetailedLogger.ErrorCode, java.util.Date, java.util.Date)
             */
            public List<LogEntry> findLogByParameters( final LogEventType eventType,
                                                       final LogableObject object,
                                                       final ErrorCode code,
                                                       final Date start,
                                                       final Date end ) {
                if (true) {
                    throw new UnsupportedOperationException();
                }
                try {
                    final StringBuilder xpath = new StringBuilder(MessageFormat.format("//{0}/{1}", NODE_LOG, NODE_LOG_ENTRY));
                    if (code != null || eventType != null || start != null || end != null) {
                        boolean hasWhere = false;
                        xpath.append("[@");
                        if (eventType != null) {
                            xpath.append(PROPERTY_LOG_ENTRY__EVENT_TYPE);
                            xpath.append("=\"");
                            xpath.append(eventType.name());
                            xpath.append("\"");
                        }
                        if (code != null) {
                            if (hasWhere) {
                                xpath.append(" and ");
                            }
                            hasWhere = true;
                            xpath.append(PROPERTY_LOG_ENTRY__ERROR_CODE);
                            xpath.append("=\"");
                            //                            xpath.append(code.name());
                            xpath.append("\"");
                        }
                        if (start != null) {
                            if (hasWhere) {
                                xpath.append(" and ");
                            }
                            hasWhere = true;
                            xpath.append(PROPERTY_LOG_ENTRY__DATE);
                            xpath.append(" >= xs:dateTime('");
                            xpath.append(Dates.stringFromDate(start));
                            xpath.append("T00:00:00.000Z')");
                        }
                        if (end != null) {
                            if (hasWhere) {
                                xpath.append(" and ");
                            }
                            hasWhere = true;
                            xpath.append(PROPERTY_LOG_ENTRY__DATE);
                            xpath.append(" <= xs:dateTime('");
                            xpath.append(Dates.stringFromDate(end));
                            xpath.append("T00:00:00.000Z')");
                        }

                        xpath.append("]");

                    }
                    if (object != null) {
                        xpath.append('/');
                        xpath.append(NODE_OBJ_INFO);
                        xpath.append("[@");
                        xpath.append(PROPERTY_OBJ_INFO__UNIQUE_ID);
                        xpath.append("=\"");
                        xpath.append(new LoggedObjectInformation(0, object).getUniqueId());
                        xpath.append("\"");
                        xpath.append("]");
                    }
                    final Query query = this.session.getWorkspace().getQueryManager().createQuery(xpath.toString(), Query.XPATH);
                    final QueryResult result = query.execute();
                    final NodeIterator nodeIterator = result.getNodes();
                    final List<LogEntry> logEntries = new LinkedList<LogEntry>();
                    Node node;
                    while (nodeIterator.hasNext()) {
                        node = nodeIterator.nextNode();
                        if (object != null) {
                            node = node.getParent();
                        }
                        final Date date = node.getProperty(PROPERTY_LOG_ENTRY__DATE).getDate().getTime();
                        final LogEventType type = LogEventType.valueOf(node.getProperty(PROPERTY_LOG_ENTRY__EVENT_TYPE).getString());
                        final String message = node.getProperty(PROPERTY_LOG_ENTRY__MESSAGE).getString();
                        final String detailedMessage = node.getProperty(PROPERTY_LOG_ENTRY__DETAILED_MESSAGE).getString();
                        final List<LoggedObjectInformation> nodes = new ArrayList<LoggedObjectInformation>();
                        //                        final LogEntry entry = new LogEntry(errorCode, date, type, message, detailedMessage, nodes);
                        //                        if (!logEntries.contains(entry)) {
                        //                            logEntries.add(entry);
                        //                            final NodeIterator children = node.getNodes(NODE_OBJ_INFO);
                        //                            Node objectInfo;
                        //                            while (children.hasNext()) {
                        //                                objectInfo = children.nextNode();
                        //                                final int order = (int)objectInfo.getProperty(PROPERTY_OBJ_INFO__ORDER).getLong();
                        //                                final String uniqueId = objectInfo.getProperty(PROPERTY_OBJ_INFO__UNIQUE_ID).getString();
                        //                                final String className = objectInfo.getProperty(PROPERTY_OBJ_INFO__TYPE_NAME).getString();
                        //                                final String friendlyDescription = objectInfo.getProperty(PROPERTY_OBJ_INFO__FRIENDLY_DESCRIPTION).getString();
                        //                                final LoggedObjectInformation info = new LoggedObjectInformation(order, uniqueId, className,
                        //                                                                                                 friendlyDescription);
                        //                                nodes.add(info);
                        //                            }
                        //                        }

                    }
                    return logEntries;
                } catch (final Exception e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);
                }
            }

            public List<LogEntry> findLogByParameters( final String user,
                                                       final LogEventType eventType,
                                                       final LogableObject object,
                                                       final ErrorCode code,
                                                       final Date start,
                                                       final Date end ) {
                // TODO Auto-generated method stub
                return null;
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#log(org.openspotlight.federation.log.DetailedLogger.EventType, org.openspotlight.federation.log.DetailedLogger.ErrorCode, java.lang.String, org.openspotlight.common.jcr.LogableObject, org.openspotlight.common.jcr.LogableObject[])
             */
            public void log( final LogEventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                this.log(type, errorCode, message, null, node, anotherNodes);

            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#log(org.openspotlight.federation.log.DetailedLogger.EventType, org.openspotlight.federation.log.DetailedLogger.ErrorCode, java.lang.String, java.lang.String, org.openspotlight.common.jcr.LogableObject, org.openspotlight.common.jcr.LogableObject[])
             */
            public void log( final LogEventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final String detailedMessage,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                if (true) {
                    throw new UnsupportedOperationException();
                }

                try {
                    checkNotNull("type", type);
                    checkNotNull("errorCode", errorCode);
                    checkNotEmpty("message", message);
                    checkNotNull("node", node);
                    Node logNode;
                    try {
                        logNode = this.session.getRootNode().getNode(NODE_LOG);
                    } catch (final PathNotFoundException pnfe) {
                        logNode = this.session.getRootNode().addNode(NODE_LOG);
                    }
                    final Node entry = logNode.addNode(NODE_LOG_ENTRY);
                    entry.setProperty(PROPERTY_LOG_ENTRY__EVENT_TYPE, type.name());
                    entry.setProperty(PROPERTY_LOG_ENTRY__MESSAGE, message);
                    entry.setProperty(PROPERTY_LOG_ENTRY__DATE, Calendar.getInstance());
                    entry.setProperty(PROPERTY_LOG_ENTRY__DETAILED_MESSAGE, detailedMessage != null ? detailedMessage : message);
                    final List<LoggedObjectInformation> loggedObjectHierarchyList = LoggedObjectInformation.getHierarchyFrom(
                                                                                                                             node,
                                                                                                                             anotherNodes);
                    for (final LoggedObjectInformation info : loggedObjectHierarchyList) {
                        final Node objectInfo = entry.addNode(NODE_OBJ_INFO);
                        objectInfo.setProperty(PROPERTY_OBJ_INFO__TYPE_NAME, info.getTypeName());
                        objectInfo.setProperty(PROPERTY_OBJ_INFO__FRIENDLY_DESCRIPTION, info.getFriendlyDescription());
                        objectInfo.setProperty(PROPERTY_OBJ_INFO__UNIQUE_ID, info.getUniqueId());
                        objectInfo.setProperty(PROPERTY_OBJ_INFO__ORDER, info.getOrder());
                    }
                    this.session.save();
                } catch (final Exception e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);
                }
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#log(org.openspotlight.federation.log.DetailedLogger.EventType, java.lang.String, org.openspotlight.common.jcr.LogableObject, org.openspotlight.common.jcr.LogableObject[])
             */
            public void log( final LogEventType type,
                             final String message,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                throw new UnsupportedOperationException();
            }

            /* (non-Javadoc)
             * @see org.openspotlight.federation.log.DetailedLogger#log(org.openspotlight.federation.log.DetailedLogger.EventType, java.lang.String, java.lang.String, org.openspotlight.common.jcr.LogableObject, org.openspotlight.common.jcr.LogableObject[])
             */
            public void log( final LogEventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {

                throw new UnsupportedOperationException();
            }

            public void log( final String user,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

            public void log( final String user,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

            public void log( final String user,
                             final LogEventType type,
                             final String message,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

            public void log( final String user,
                             final LogEventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

            public void log( final String user,
                             final String repository,
                             final LogEventType type,
                             final ErrorCode errorCode,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

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
                // TODO Auto-generated method stub

            }

            public void log( final String user,
                             final String repository,
                             final LogEventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

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
    public static class LogEntry {

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
                } else if (o instanceof ConfigurationNode) {
                    final ConfigurationNode node = (ConfigurationNode)o;
                    return node.getInstanceMetadata().getDefaultParent();
                } else {
                    throw logAndReturn(new IllegalArgumentException());
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
                } else if (object instanceof ConfigurationNode) {
                    final ConfigurationNode node = (ConfigurationNode)object;
                    this.uniqueId = node.getInstanceMetadata().getSavedUniqueId();
                    this.friendlyDescription = node.getInstanceMetadata().getPath();
                    this.className = node.getClass().getName();
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
     * Find log by date interval.
     * 
     * @param start the start
     * @param end the end
     * @return the list< log entry>
     */
    public List<LogEntry> findLogByDateInterval( Date start,
                                                 Date end );

    /**
     * Find log by error code.
     * 
     * @param code the code
     * @return the list< log entry>
     */
    public List<LogEntry> findLogByErrorCode( ErrorCode code );

    /**
     * Find log by event type.
     * 
     * @param eventType the event type
     * @return the list< log entry>
     */
    public List<LogEntry> findLogByEventType( LogEventType eventType );

    /**
     * Find log by logable object.
     * 
     * @param object the object
     * @return the list< log entry>
     */
    public List<LogEntry> findLogByLogableObject( LogableObject object );

    /**
     * Find log by parameters.
     * 
     * @param eventType the event type
     * @param object the object
     * @param code the code
     * @param start the start
     * @param end the end
     * @return the list< log entry>
     */
    public List<LogEntry> findLogByParameters( String user,
                                               LogEventType eventType,
                                               LogableObject object,
                                               ErrorCode code,
                                               Date start,
                                               Date end );

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param detailedMessage the detailed message
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
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
     * @param node the node
     * @param anotherNodes the another nodes
     */
    public void log( String user,
                     String repository,
                     LogEventType type,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes );

}
