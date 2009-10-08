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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.jcr.LogableObject;
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
            private final Session session;

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
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Not implemented yet");
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
                        logNode = this.session.getRootNode().getNode("osl:log");
                    } catch (final PathNotFoundException pnfe) {
                        logNode = this.session.getRootNode().addNode("osl:log");
                    }
                    final Node entry = logNode.addNode("logEntry");
                    entry.setProperty("type", type.name());
                    entry.setProperty("errorCode", errorCode.name());
                    entry.setProperty("message", message);
                    if (detailedMessage != null) {
                        entry.setProperty("detailedMessage", detailedMessage);
                    }
                    final List<LoggedObjectInformation> loggedObjectHierarchyList = LoggedObjectInformation.getHierarchyFrom(
                                                                                                                             node,
                                                                                                                             anotherNodes);
                    for (final LoggedObjectInformation info : loggedObjectHierarchyList) {
                        final Node objectInfo = entry.addNode("objectInfo");
                        objectInfo.setProperty("type", info.getTypeName());
                        objectInfo.setProperty("friendlyDescription", info.getFriendlyDescription());
                        objectInfo.setProperty("uniqueId", info.getUniqueId());
                        objectInfo.setProperty("order", info.getOrder());
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

            private LoggedObjectInformation(
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
            this.nodes = nodes;
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
