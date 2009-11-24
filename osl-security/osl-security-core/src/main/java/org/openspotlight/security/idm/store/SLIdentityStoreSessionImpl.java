package org.openspotlight.security.idm.store;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.jboss.identity.idm.common.exception.IdentityException;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistSupport;

public class SLIdentityStoreSessionImpl implements IdentityStoreSession {
	public static final String SECURITY_NODE = SharedConstants.DEFAULT_JCR_ROOT_NAME
			+ "/{0}/securityStore";

	private final Set<SimpleNodeType> dirtyNodesToSave = new HashSet<SimpleNodeType>();
	private final Set<SimpleNodeType> dirtyNodesToRemove = new HashSet<SimpleNodeType>();
	// FIXME remove "deleted nodes" on tx commit
	private final Session session;
	private final SLIdentityStoreSessionContext context = new SLIdentityStoreSessionContext(
			this);

	private final String rootNode;

	public SLIdentityStoreSessionImpl(final Session session,
			final String repositoryName) {
		this.session = session;
		this.rootNode = MessageFormat.format(
				SLIdentityStoreSessionImpl.SECURITY_NODE, repositoryName);
	}

	public void addDirtyNodeToRemove(final SimpleNodeType node) {
		this.dirtyNodesToRemove.add(node);
	}

	public void addDirtyNodeToSave(final SimpleNodeType node) {
		this.dirtyNodesToSave.add(node);
	}

	public void clear() throws IdentityException {
		this.dirtyNodesToSave.clear();
	}

	public void close() throws IdentityException {
		try {
			this.session.logout();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}

	}

	public void commitTransaction() {
		try {
			this.session.save();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public String getRootNode() {
		return this.rootNode;
	}

	public Session getSession() {
		return this.session;
	}

	public Object getSessionContext() throws IdentityException {
		return this.context;
	}

	public boolean isOpen() {
		return this.session.isLive();
	}

	public boolean isTransactionActive() {
		return this.dirtyNodesToSave.size() > 0;
	}

	public boolean isTransactionSupported() {
		return true;
	}

	public void rollbackTransaction() {
		try {
			this.dirtyNodesToSave.clear();
			if (this.session.hasPendingChanges()) {
				this.session.logout();
			}
		} catch (final RepositoryException e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public void save() throws IdentityException {
		try {

			SimplePersistSupport.convertBeansToJcrs(this.rootNode,
					this.session, this.dirtyNodesToSave);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public void startTransaction() {

	}

}
