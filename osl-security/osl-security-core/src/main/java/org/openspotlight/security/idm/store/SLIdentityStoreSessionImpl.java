package org.openspotlight.security.idm.store;

import java.text.MessageFormat;

import javax.jcr.Node;
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

	public void addNode(final SimpleNodeType node) throws Exception {
		SimplePersistSupport
				.convertBeanToJcr(this.rootNode, this.session, node);
		this.session.save();
	}

	public void clear() throws IdentityException {
	}

	public void close() throws IdentityException {
		try {
			this.session.logout();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, IdentityException.class);
		}

	}

	public void commitTransaction() {
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
		return false;
	}

	public boolean isTransactionSupported() {
		return false;
	}

	public void remove(final SimpleNodeType node) throws Exception {
		final Node jcrNode = SimplePersistSupport.convertBeanToJcr(
				this.rootNode, this.session, node);
		jcrNode.remove();

	}

	public void rollbackTransaction() {
	}

	public void save() throws IdentityException {
		try {

			this.session.save();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public void startTransaction() {

	}

}
