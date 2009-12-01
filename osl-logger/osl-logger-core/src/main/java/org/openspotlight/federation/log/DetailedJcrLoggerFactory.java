package org.openspotlight.federation.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jcr.Session;

import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.DetailedLoggerFactory;
import org.openspotlight.log.LogableObject;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.security.idm.AuthenticatedUser;

/**
 * The Factory used to create {@link DetailedLogger}.
 */
public final class DetailedJcrLoggerFactory implements DetailedLoggerFactory {
	/**
	 * The JcrDetailedLogger is an implementation of {@link DetailedLogger}
	 * based on Jcr. This kind of logger was implemented 'by hand' instead of
	 * using {@link SLNode} or {@link ConfigurationNode} by a simple reason.
	 * Should not be possible to log the log. If it's necessary this
	 * implementation could be changed on the future.
	 */
	private static final class JcrDetailedLogger implements DetailedLogger {

		/** The session. */
		private final Session session;

		public JcrDetailedLogger(final Session session) {
			this.session = session;
		}

		public void log(final AuthenticatedUser user, final LogEventType type,
				final ErrorCode errorCode, final String detailedMessage,
				final LogableObject... anotherNodes) {

		}

		public void log(final AuthenticatedUser user, final LogEventType type,
				final ErrorCode errorCode, final String message,
				final String detailedMessage,
				final LogableObject... anotherNodes) {
			this.log(user, null, type, errorCode, message, detailedMessage,
					anotherNodes);

		}

		public void log(final AuthenticatedUser user, final LogEventType type,
				final String message, final LogableObject... anotherNodes) {
			this.log(user, null, type, null, message, null, anotherNodes);

		}

		public void log(final AuthenticatedUser user, final LogEventType type,
				final String message, final String detailedMessage,
				final LogableObject... anotherNodes) {
			this.log(user, null, type, null, message, detailedMessage,
					anotherNodes);

		}

		public void log(final AuthenticatedUser user, final String repository,
				final LogEventType type, final ErrorCode errorCode,
				final String detailedMessage,
				final LogableObject... anotherNodes) {
			this.log(user, repository, type, errorCode, null, detailedMessage,
					anotherNodes);

		}

		public void log(final AuthenticatedUser user, final String repository,
				final LogEventType type, final ErrorCode errorCode,
				final String message, final String detailedMessage,
				final LogableObject... anotherNodes) {
			final LogEntry entry = new LogEntry(errorCode, new Date(), type,
					message, detailedMessage, LoggedObjectInformation
							.getHierarchyFrom(anotherNodes));

			final String initialPath = SharedConstants.DEFAULT_JCR_ROOT_NAME
					+ "/" + (repository != null ? repository : "noRepository")
					+ "/" + (user != null ? user.getId() : "noUser") + "/log";
			SimplePersistSupport.convertBeanToJcr(initialPath, this.session,
					entry);
			try {
				// this.session.save();
			} catch (final Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}

		}

		public void log(final AuthenticatedUser user, final String repository,
				final LogEventType type, final String message,
				final LogableObject... anotherNodes) {
			this.log(user, repository, type, null, message, null, anotherNodes);

		}

		public void log(final AuthenticatedUser user, final String repository,
				final LogEventType type, final String message,
				final String detailedMessage,
				final LogableObject... anotherNodes) {
			this.log(user, repository, type, null, message, detailedMessage,
					anotherNodes);
		}

	}

	/**
	 * The Class LogEntry is used to represent a new log entry.
	 */
	@Name("log_entry")
	public static class LogEntry implements SimpleNodeType, Serializable {

		/** The error code. */
		private ErrorCode errorCode;

		/** The type. */
		private LogEventType type;

		/** The message. */
		private String message;

		/** The detailed message. */
		private String detailedMessage;

		/** The nodes. */
		private List<LoggedObjectInformation> nodes;

		/** The date. */
		private Date date;

		/** The hash code. */
		private int hashCode;

		public LogEntry() {
		}

		/**
		 * Instantiates a new log entry.
		 * 
		 * @param errorCode
		 *            the error code
		 * @param date
		 *            the date
		 * @param type
		 *            the type
		 * @param message
		 *            the message
		 * @param detailedMessage
		 *            the detailed message
		 * @param nodes
		 *            the nodes
		 */
		LogEntry(final ErrorCode errorCode, final Date date,
				final LogEventType type, final String message,
				final String detailedMessage,
				final List<LoggedObjectInformation> nodes) {
			this.errorCode = errorCode;
			this.type = type;
			this.message = message;
			this.detailedMessage = detailedMessage;
			this.nodes = Collections.unmodifiableList(nodes);
			this.date = date;
			this.hashCode = HashCodes
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
			return Equals.eachEquality(Arrays
					.of(this.type, this.message, this.detailedMessage,
							this.nodes, this.date, this.errorCode), Arrays
					.andOf(that.type, that.message, that.detailedMessage,
							that.nodes, that.date, that.errorCode));
		}

		/**
		 * Gets the date.
		 * 
		 * @return the date
		 */
		@KeyProperty
		public Date getDate() {
			return this.date;
		}

		/**
		 * Gets the detailed message.
		 * 
		 * @return the detailed message
		 */
		@KeyProperty
		public String getDetailedMessage() {
			return this.detailedMessage;
		}

		public ErrorCode getErrorCode() {
			return this.errorCode;
		}

		/**
		 * Gets the message.
		 * 
		 * @return the message
		 */
		@KeyProperty
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
		@KeyProperty
		public LogEventType getType() {
			return this.type;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.hashCode;
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
	 * The Class LoggedObjectInformation is used to represent objects related to
	 * a given log.
	 */
	@Name("logged_object_information")
	public static class LoggedObjectInformation implements SimpleNodeType,
			Serializable {

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
		 * @param node
		 *            the node
		 * @param anotherNodes
		 *            the another nodes
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
		 * @param o
		 *            the o
		 * @return the parent
		 */
		private static LogableObject getParent(final LogableObject o) {
			if (o instanceof SLNode) {
				final SLNode node = (SLNode) o;
				try {
					return node.getParent();
				} catch (final SLGraphSessionException e) {
					throw Exceptions.logAndReturnNew(e,
							SLRuntimeException.class);
				}
			} else {
				return null;// other types have the path information. Now the
							// parent nodes isn't necessary
			}
		}

		private int order;

		/** The unique id. */
		private String uniqueId;

		/** The friendly description. */
		private String friendlyDescription;

		/** The class name. */
		private String className;

		public LoggedObjectInformation() {
		}

		/**
		 * Instantiates a new logged object information.
		 * 
		 * @param order
		 *            the order
		 * @param object
		 *            the object
		 */
		LoggedObjectInformation(final int order, final LogableObject object) {
			this.order = order;
			if (object instanceof SLNode) {
				final SLNode node = (SLNode) object;
				try {
					this.uniqueId = node.getID();
				} catch (final SLGraphSessionException e) {
					throw Exceptions.logAndReturnNew(e,
							SLRuntimeException.class);
				}
				this.friendlyDescription = node.toString();
				this.className = node.getClass().getInterfaces()[0].getName();
			} else if (object instanceof ArtifactSource) {
				final ArtifactSource node = (ArtifactSource) object;
				this.friendlyDescription = node.getName();
				this.className = node.getClass().getName();
				this.uniqueId = null;
			} else if (object instanceof Artifact) {
				final Artifact node = (Artifact) object;
				this.friendlyDescription = node.getArtifactCompleteName();
				this.className = node.getClass().getName();
				this.uniqueId = null;
			} else {
				throw Exceptions.logAndReturn(new IllegalArgumentException());
			}
			Assertions.checkNotEmpty("friendlyDescription",
					this.friendlyDescription);
			Assertions.checkNotEmpty("className", this.className);
		}

		/**
		 * Instantiates a new logged object information.
		 * 
		 * @param order
		 *            the order
		 * @param uniqueId
		 *            the unique id
		 * @param className
		 *            the class name
		 * @param friendlyDescription
		 *            the friendly description
		 */
		LoggedObjectInformation(final int order, final String uniqueId,
				final String className, final String friendlyDescription) {
			Assertions.checkNotEmpty("uniqueId", uniqueId);
			Assertions
					.checkNotEmpty("friendlyDescription", friendlyDescription);
			Assertions.checkNotEmpty("className", className);
			this.order = order;
			this.uniqueId = uniqueId;
			this.friendlyDescription = friendlyDescription;
			this.className = className;

		}

		public String getClassName() {
			return this.className;
		}

		/**
		 * Gets the friendly description.
		 * 
		 * @return the friendly description
		 */
		@KeyProperty
		public String getFriendlyDescription() {
			return this.friendlyDescription;
		}

		/**
		 * Gets the order.
		 * 
		 * @return the order
		 */
		@KeyProperty
		public int getOrder() {
			return this.order;
		}

		/**
		 * Gets the type name.
		 * 
		 * @return the type name
		 */
		@KeyProperty
		public String getTypeName() {
			return this.className;
		}

		/**
		 * Gets the unique id.
		 * 
		 * @return the unique id
		 */
		@KeyProperty
		public String getUniqueId() {
			return this.uniqueId;
		}

		public void setClassName(final String className) {
			this.className = className;
		}

		public void setFriendlyDescription(final String friendlyDescription) {
			this.friendlyDescription = friendlyDescription;
		}

		public void setOrder(final int order) {
			this.order = order;
		}

		public void setUniqueId(final String uniqueId) {
			this.uniqueId = uniqueId;
		}

	}

	final JcrConnectionProvider provider;

	private final CopyOnWriteArrayList<Session> oppenedSessions = new CopyOnWriteArrayList<Session>();

	public DetailedJcrLoggerFactory(final JcrConnectionDescriptor descriptor) {
		this.provider = JcrConnectionProvider.createFromData(descriptor);
	}

	public void closeResources() {
		for (final Session session : this.oppenedSessions) {
			session.logout();
		}

	}

	/**
	 * Creates the jcr detailed logger.
	 * 
	 * @param session
	 *            the session
	 * @return the detailed logger
	 */
	public DetailedLogger createNewLogger() {
		final Session session = this.provider.openSession();
		this.oppenedSessions.add(session);
		return new JcrDetailedLogger(session);
	}

}
