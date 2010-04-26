/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.federation.log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.*;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.graph.SLNode;
import org.openspotlight.guice.ThreadLocalProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.log.LogableObject;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.io.Serializable;
import java.util.*;

/**
 * The Factory used to create {@link DetailedLogger}.
 */
@Singleton
public final class DetailedLoggerProvider extends ThreadLocalProvider<DetailedLogger> {


    @Inject
    public DetailedLoggerProvider(SimplePersistFactory simplePersistFactory, Provider<STStorageSession> sessionProvider) {
        this.simplePersistFactory = simplePersistFactory;
        this.sessionProvider = sessionProvider;
    }

    private final SimplePersistFactory simplePersistFactory;

    private final Provider<STStorageSession> sessionProvider;

    private final STPartition partition = SLPartition.LOG;


    @Override
    protected DetailedLogger createInstance() {
        SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist = simplePersistFactory
                .createSimplePersist(partition);
        return new DetailedLoggerImpl(simplePersist);
    }


    /**
     * The Class LogEntry is used to represent a new log entry.
     */
    @Name("log_entry")
    public static class LogEntry implements SimpleNodeType, Serializable {

        /**
         *
         */
        private static final long serialVersionUID = -1429744150741798679L;

        /**
         * The error code.
         */
        private ErrorCode errorCode;

        /**
         * The type.
         */
        private LogEventType type;

        /**
         * The message.
         */
        private String message;

        /**
         * The detailed message.
         */
        private String detailedMessage;

        /**
         * The nodes.
         */
        private List<LoggedObjectInformation> nodes;

        /**
         * The date.
         */
        private Date date;

        private long timestamp;

        /**
         * The hash code.
         */
        private int hashCode;

        public LogEntry() {
        }

        /**
         * Instantiates a new log entry.
         *
         * @param errorCode       the error code
         * @param type            the type
         * @param message         the message
         * @param detailedMessage the detailed message
         * @param nodes           the nodes
         */
        LogEntry(
                final ErrorCode errorCode, final long timestamp,
                final LogEventType type, final String message,
                final String detailedMessage,
                final List<LoggedObjectInformation> nodes) {
            this.errorCode = errorCode;
            this.type = type;
            this.message = message;
            this.detailedMessage = detailedMessage;
            this.nodes = Collections.unmodifiableList(nodes);
            this.timestamp = timestamp;
            this.date = new Date(timestamp);
            hashCode = HashCodes
                    .hashOf(this.type, this.message, this.detailedMessage,
                            this.nodes, this.date, this.errorCode);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof LogEntry)) {
                return false;
            }
            final LogEntry that = (LogEntry) obj;
            return Equals.eachEquality(Arrays.of(type, message,
                    detailedMessage, nodes, date, errorCode), Arrays.andOf(
                    that.type, that.message, that.detailedMessage, that.nodes,
                    that.date, that.errorCode));
        }

        /**
         * Gets the date.
         *
         * @return the date
         */
        public Date getDate() {
            return date;
        }

        @KeyProperty
        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Gets the detailed message.
         *
         * @return the detailed message
         */
        public String getDetailedMessage() {
            return detailedMessage;
        }

        public ErrorCode getErrorCode() {
            return errorCode;
        }

        /**
         * Gets the message.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets the nodes.
         *
         * @return the nodes
         */
        public List<LoggedObjectInformation> getNodes() {
            return nodes;
        }

        /**
         * Gets the type.
         *
         * @return the type
         */
        @KeyProperty
        public LogEventType getType() {
            return type;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */

        @Override
        public int hashCode() {
            return hashCode;
        }

        public void setDate(final Date date) {
            this.date = date;
        }

        public void setDetailedMessage(final String detailedMessage) {
            this.detailedMessage = detailedMessage;
        }

        public void setErrorCode(final ErrorCode errorCode) {
            this.errorCode = errorCode;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public void setNodes(final List<LoggedObjectInformation> nodes) {
            this.nodes = nodes;
        }

        public void setType(final LogEventType type) {
            this.type = type;
        }

    }

    /**
     * The Class LoggedObjectInformation is used to represent objects related to a given log.
     */
    @Name("logged_object_information")
    public static class LoggedObjectInformation implements SimpleNodeType,
            Serializable {

        /**
         *
         */
        private static final long serialVersionUID = 2812040814742711306L;

        private static List<LogableObject> getHierarchyFrom(
                final LogableObject o) {
            final List<LogableObject> result = new LinkedList<LogableObject>();
            result.add(o);
            LogableObject parent = LoggedObjectInformation.getParent(o);
            while (parent != null) {
                result.add(parent);
                parent = LoggedObjectInformation.getParent(parent);
            }
            return result;
        }

        /**
         * Gets the hierarchy from.
         *
         * @param anotherNodes the another nodes
         * @return the hierarchy from
         */
        public static List<LoggedObjectInformation> getHierarchyFrom(
                final LogableObject... anotherNodes) {
            final List<LogableObject> nodes = new LinkedList<LogableObject>();
            for (final LogableObject o : anotherNodes) {
                nodes.addAll(LoggedObjectInformation.getHierarchyFrom(o));
            }
            Collections.reverse(nodes);
            final List<LoggedObjectInformation> result = new ArrayList<LoggedObjectInformation>(
                    nodes.size());
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
        private static LogableObject getParent(final LogableObject o) {
            if (o instanceof SLNode) {
                final SLNode node = (SLNode) o;
                return node.getParent();
            } else {
                return null;// other types have the path information. Now the
                // parent nodes isn't necessary
            }
        }

        private int order;

        /**
         * The unique id.
         */
        private String uniqueId;

        /**
         * The friendly description.
         */
        private String friendlyDescription;

        /**
         * The class name.
         */
        private String typeName;

        public LoggedObjectInformation() {
        }

        /**
         * Instantiates a new logged object information.
         *
         * @param order  the order
         * @param object the object
         */
        LoggedObjectInformation(
                final int order, final LogableObject object) {
            this.order = order;
            if (object instanceof SLNode) {
                final SLNode node = (SLNode) object;
                uniqueId = node.getID().replaceAll("\n","").replaceAll("\t","").replaceAll(" ","");

                friendlyDescription = node.toString();
                typeName = node.getClass().getInterfaces()[0].getName();
            } else if (object instanceof ArtifactSource) {
                final ArtifactSource node = (ArtifactSource) object;
                friendlyDescription = node.getName();
                typeName = node.getClass().getName();
                uniqueId = null;
            } else if (object instanceof Artifact) {
                final Artifact node = (Artifact) object;
                friendlyDescription = node.getArtifactCompleteName();
                typeName = node.getClass().getName();
                uniqueId = null;
            } else {
                throw Exceptions.logAndReturn(new IllegalArgumentException());
            }
            Assertions
                    .checkNotEmpty("friendlyDescription", friendlyDescription);
            Assertions.checkNotEmpty("className", typeName);
        }

        /**
         * Instantiates a new logged object information.
         *
         * @param order               the order
         * @param uniqueId            the unique id
         * @param className           the class name
         * @param friendlyDescription the friendly description
         */
        LoggedObjectInformation(
                final int order, final String uniqueId,
                final String className, final String friendlyDescription) {
            Assertions.checkNotEmpty("uniqueId", uniqueId);
            Assertions
                    .checkNotEmpty("friendlyDescription", friendlyDescription);
            Assertions.checkNotEmpty("className", className);
            this.order = order;
            this.uniqueId = uniqueId;
            this.friendlyDescription = friendlyDescription;
            typeName = className;

        }

        public String getClassName() {
            return typeName;
        }

        /**
         * Gets the friendly description.
         *
         * @return the friendly description
         */
        public String getFriendlyDescription() {
            return friendlyDescription;
        }

        /**
         * Gets the order.
         *
         * @return the order
         */
        @KeyProperty
        public int getOrder() {
            return order;
        }

        /**
         * Gets the type name.
         *
         * @return the type name
         */
        @KeyProperty
        public String getTypeName() {
            return typeName;
        }

        /**
         * Gets the unique id.
         *
         * @return the unique id
         */
        @KeyProperty
        public String getUniqueId() {
            return uniqueId;
        }

        public void setClassName(final String className) {
            typeName = className;
        }

        public void setFriendlyDescription(final String friendlyDescription) {
            this.friendlyDescription = friendlyDescription;
        }

        public void setOrder(final int order) {
            this.order = order;
        }

        public void setTypeName(final String typeName) {
            this.typeName = typeName;
        }

        public void setUniqueId(final String uniqueId) {
            this.uniqueId = uniqueId;
        }

    }

    public void closeResources() {

    }


}
