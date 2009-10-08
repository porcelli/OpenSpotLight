package org.openspotlight.federation.log;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
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

import javax.jcr.Session;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.jcr.LogableObject;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;

public interface DetailedLogger {

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

            public void log( final EventType type,
                             final String message,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

            public void log( final EventType type,
                             final String message,
                             final String detailedMessage,
                             final LogableObject node,
                             final LogableObject... anotherNodes ) {
                // TODO Auto-generated method stub

            }

        }

        public static DetailedLogger createJcrDetailedLogger( final Session session ) {
            return new JcrDetailedLogger(session);
        }
    }

    public static class LogInformation {
        public static class LoggedObjectInformation {

            private static List<LogableObject> getHierarchyFrom( final LogableObject o ) {
                final List<LogableObject> result = new LinkedList<LogableObject>();
                result.add(o);
                LogableObject parent = getParent(o);
                while (parent != null) {
                    result.add(parent);
                    parent = getParent(o);
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
            }

            public String getClassName() {
                return this.className;
            }

            public String getFriendlyDescription() {
                return this.friendlyDescription;
            }

            public int getOrder() {
                return this.order;
            }

            public String getUniqueId() {
                return this.uniqueId;
            }

        }

        private final EventType                     type;
        private final String                        message;
        private final String                        detailedMessage;
        private final List<LoggedObjectInformation> nodes;
        private final Date                          date;
        private final int                           hashCode;

        private LogInformation(
                                final Date date, final EventType type, final String message, final String detailedMessage,
                                final List<LoggedObjectInformation> nodes ) {
            this.type = type;
            this.message = message;
            this.detailedMessage = detailedMessage;
            this.nodes = nodes;
            this.date = date;
            this.hashCode = hashOf(this.type, this.message, this.detailedMessage, this.nodes, this.date);
        }

        @Override
        public boolean equals( final Object obj ) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LogInformation)) {
                return false;
            }
            final LogInformation that = (LogInformation)obj;
            return eachEquality(of(this.type, this.message, this.detailedMessage, this.nodes, this.date),
                                andOf(that.type, that.message, that.detailedMessage, that.nodes, that.date));
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

    }

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
