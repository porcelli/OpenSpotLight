package org.openspotlight.federation.log;

import java.util.Date;

import javax.jcr.Session;

import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory.LogEntry;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory.LoggedObjectInformation;
import org.openspotlight.graph.SLNode;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.LogableObject;
import org.openspotlight.log.DetailedLogger.ErrorCode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.security.idm.AuthenticatedUser;

/**
 * The JcrDetailedLogger is an implementation of {@link DetailedLogger}
 * based on Jcr. This kind of logger was implemented 'by hand' instead of
 * using {@link SLNode} or {@link ConfigurationNode} by a simple reason.
 * Should not be possible to log the log. If it's necessary this
 * implementation could be changed on the future.
 */
public final class JcrDetailedLogger implements DetailedLogger {

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
		SimplePersistSupport.convertBeanToJcr(initialPath, session, entry);
		try {
			session.save();
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