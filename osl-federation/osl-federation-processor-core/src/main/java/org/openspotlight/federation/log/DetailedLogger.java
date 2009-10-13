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
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.log.DetailedLogger.LogEntry.LoggedObjectInformation;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;

public interface DetailedLogger {

    public static enum ErrorCode {
        NO_ERROR_CODE(0, "No error code"),
        ERR_001(1, "Example error #1"),
        ERR_002(2, "Example error #2");

        private final int    code;
        private final String description;
        private final String toStringDescription;

        private ErrorCode(
                           final int code, final String description ) {
            assert code > 0;
            assert description != null;
            assert description.trim().length() > 0;
            this.code = code;
            this.description = description;
            this.toStringDescription = "ErrorCode: " + this.code + " " + this.description;
        }

        public int getCode() {
            return this.code;
        }

        public String getDescription() {
            return this.description;
        }

        @Override
        public String toString() {
            return this.toStringDescription;
        }

    }

    public static enum EventType {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    public static final class Factory {

        private static final class JcrDetailedLogger implements DetailedLogger {
            private final Session       session;

            private static final String NODE_LOG                                = "osl:log";

            private static final String NODE_LOG_ENTRY                          = "osl:logEntry";

            private static final String NODE_OBJ_INFO                           = "osl:objectInfo";

            private static final String PROPERTY_LOG_ENTRY__EVENT_TYPE          = "osl:eventType";
            private static final String PROPERTY_LOG_ENTRY__DATE                = "osl:date";

            private static final String PROPERTY_LOG_ENTRY__ERROR_CODE          = "osl:errorCode";

            private static final String PROPERTY_LOG_ENTRY__MESSAGE             = "osl:message";

            private static final String PROPERTY_LOG_ENTRY__DETAILED_MESSAGE    = "osl:detailedMessage";

            private static final String PROPERTY_OBJ_INFO__TYPE_NAME            = "osl:typeName";
            private static final String PROPERTY_OBJ_INFO__FRIENDLY_DESCRIPTION = "osl:friendlyDescription";
            private static final String PROPERTY_OBJ_INFO__UNIQUE_ID            = "osl:uniqueId";
            private static final String PROPERTY_OBJ_INFO__ORDER                = "osl:order";

            public JcrDetailedLogger(
                                      final Session session ) {
                checkNotNull("session", session);
                checkCondition("sessionAlive", session.isLive());
                this.session = session;
            }

            public List<LogEntry> findLogByDateInterval( final Date start,
                                                         final Date end ) {
                return findLogByParameters(null, null, null, start, end);
            }

            public List<LogEntry> findLogByErrorCode( final ErrorCode code ) {
                return findLogByParameters(null, null, code, null, null);
            }

            public List<LogEntry> findLogByEventType( final EventType eventType ) {
                return findLogByParameters(eventType, null, null, null, null);
            }

            public List<LogEntry> findLogByLogableObject( final LogableObject object ) {
                return findLogByParameters(null, object, null, null, null);
            }

            public List<LogEntry> findLogByParameters( final EventType eventType,
                                                       final LogableObject object,
                                                       final ErrorCode code,
                                                       final Date start,
                                                       final Date end ) {
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
                            xpath.append(code.name());
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
                        final ErrorCode errorCode = ErrorCode.valueOf(node.getProperty(PROPERTY_LOG_ENTRY__ERROR_CODE).getString());
                        final Date date = node.getProperty(PROPERTY_LOG_ENTRY__DATE).getDate().getTime();
                        final EventType type = EventType.valueOf(node.getProperty(PROPERTY_LOG_ENTRY__EVENT_TYPE).getString());
                        final String message = node.getProperty(PROPERTY_LOG_ENTRY__MESSAGE).getString();
                        final String detailedMessage = node.getProperty(PROPERTY_LOG_ENTRY__DETAILED_MESSAGE).getString();
                        final List<LoggedObjectInformation> nodes = new ArrayList<LoggedObjectInformation>();
                        final LogEntry entry = new LogEntry(errorCode, date, type, message, detailedMessage, nodes);
                        if (!logEntries.contains(entry)) {
                            logEntries.add(entry);
                            final NodeIterator children = node.getNodes(NODE_OBJ_INFO);
                            Node objectInfo;
                            while (children.hasNext()) {
                                objectInfo = children.nextNode();
                                final int order = (int)objectInfo.getProperty(PROPERTY_OBJ_INFO__ORDER).getLong();
                                final String uniqueId = objectInfo.getProperty(PROPERTY_OBJ_INFO__UNIQUE_ID).getString();
                                final String className = objectInfo.getProperty(PROPERTY_OBJ_INFO__TYPE_NAME).getString();
                                final String friendlyDescription = objectInfo.getProperty(PROPERTY_OBJ_INFO__FRIENDLY_DESCRIPTION).getString();
                                final LoggedObjectInformation info = new LoggedObjectInformation(order, uniqueId, className,
                                                                                                 friendlyDescription);
                                nodes.add(info);
                            }
                        }

                    }
                    return logEntries;
                } catch (final Exception e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);
                }
            }

            public void log( final EventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                this.log(type, errorCode, message, null, node, anotherNodes);

            }

            public void log( final EventType type,
                             final ErrorCode errorCode,
                             final String message,
                             final String detailedMessage,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
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
                    entry.setProperty(PROPERTY_LOG_ENTRY__ERROR_CODE, errorCode.name());
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

            public void log( final EventType type,
                             final String message,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                this.log(type, ErrorCode.NO_ERROR_CODE, message, null, node, anotherNodes);

            }

            public void log( final EventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                this.log(type, ErrorCode.NO_ERROR_CODE, message, detailedMessage, node, anotherNodes);

            }

        }

        public static DetailedLogger createJcrDetailedLogger( final Session session ) {
            return new JcrDetailedLogger(session);
        }
    }

    public static class LogEntry {
        public static class LoggedObjectInformation {

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

            private final int    order;

            private final String uniqueId;

            private final String friendlyDescription;

            private final String className;

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

            public String getFriendlyDescription() {
                return this.friendlyDescription;
            }

            public int getOrder() {
                return this.order;
            }

            public String getTypeName() {
                return this.className;
            }

            public String getUniqueId() {
                return this.uniqueId;
            }

        }

        private final ErrorCode                     errorCode;

        private final EventType                     type;

        private final String                        message;
        private final String                        detailedMessage;
        private final List<LoggedObjectInformation> nodes;
        private final Date                          date;
        private final int                           hashCode;

        private LogEntry(
                          final ErrorCode errorCode, final Date date, final EventType type, final String message,
                          final String detailedMessage, final List<LoggedObjectInformation> nodes ) {
            this.errorCode = errorCode;
            this.type = type;
            this.message = message;
            this.detailedMessage = detailedMessage;
            this.nodes = Collections.unmodifiableList(nodes);
            this.date = date;
            this.hashCode = hashOf(this.type, this.message, this.detailedMessage, this.nodes, this.date, this.errorCode);
        }

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

        public Date getDate() {
            return this.date;
        }

        public String getDetailedMessage() {
            return this.detailedMessage;
        }

        public ErrorCode getErrorCode() {
            return this.errorCode;
        }

        public String getMessage() {
            return this.message;
        }

        public List<LoggedObjectInformation> getNodes() {
            return this.nodes;
        }

        public EventType getType() {
            return this.type;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

    }

    public List<LogEntry> findLogByDateInterval( Date start,
                                                 Date end );

    public List<LogEntry> findLogByErrorCode( ErrorCode code );

    public List<LogEntry> findLogByEventType( EventType eventType );

    public List<LogEntry> findLogByLogableObject( LogableObject object );

    public List<LogEntry> findLogByParameters( EventType eventType,
                                               LogableObject object,
                                               ErrorCode code,
                                               Date start,
                                               Date end );

    public void log( EventType type,
                     ErrorCode errorCode,
                     String detailedMessage,
                     LogableObject node,
                     LogableObject... anotherNodes );

    public void log( EventType type,
                     ErrorCode errorCode,
                     String message,
                     String detailedMessage,
                     LogableObject node,
                     LogableObject... anotherNodes );

    public void log( EventType type,
                     String message,
                     LogableObject node,
                     LogableObject... anotherNodes );

    public void log( EventType type,
                     String message,
                     String detailedMessage,
                     LogableObject node,
                     LogableObject... anotherNodes );

}
