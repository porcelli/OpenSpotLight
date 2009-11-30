/**
 * 
 */
package org.openspotlight.jcr.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.openspotlight.jcr.provider.JcrConnectionProvider.SessionClosingListener;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

class SessionWrapper implements Session {
	private final Session session;
	private final StackTraceElement[] creationStackTrace;
	private final int sessionId;
	final SessionClosingListener sessionClosingListener;

	public SessionWrapper(final Session session, final int sessionId,
			final SessionClosingListener sessionClosingListener) {
		this.session = session;
		this.creationStackTrace = Thread.currentThread().getStackTrace();
		this.sessionId = sessionId;
		this.sessionClosingListener = sessionClosingListener;
	}

	public synchronized void addLockToken(final String lt)
			throws LockException, RepositoryException {

		this.session.addLockToken(lt);
	}

	public synchronized void checkPermission(final String absPath,
			final String actions) throws AccessControlException,
			RepositoryException {

		this.session.checkPermission(absPath, actions);

	}

	public synchronized void exportDocumentView(final String absPath,
			final ContentHandler contentHandler, final boolean skipBinary,
			final boolean noRecurse) throws PathNotFoundException,
			SAXException, RepositoryException {

		this.session.exportDocumentView(absPath, contentHandler, skipBinary,
				noRecurse);

	}

	public synchronized void exportDocumentView(final String absPath,
			final OutputStream out, final boolean skipBinary,
			final boolean noRecurse) throws IOException, PathNotFoundException,
			RepositoryException {

		this.session.exportDocumentView(absPath, out, skipBinary, noRecurse);

	}

	public synchronized void exportSystemView(final String absPath,
			final ContentHandler contentHandler, final boolean skipBinary,
			final boolean noRecurse) throws PathNotFoundException,
			SAXException, RepositoryException {

		this.session.exportSystemView(absPath, contentHandler, skipBinary,
				noRecurse);

	}

	public synchronized void exportSystemView(final String absPath,
			final OutputStream out, final boolean skipBinary,
			final boolean noRecurse) throws IOException, PathNotFoundException,
			RepositoryException {

		this.session.exportSystemView(absPath, out, skipBinary, noRecurse);

	}

	public synchronized Object getAttribute(final String name) {

		return this.session.getAttribute(name);

	}

	public synchronized String[] getAttributeNames() {

		return this.session.getAttributeNames();

	}

	public synchronized ContentHandler getImportContentHandler(
			final String parentAbsPath, final int uuidBehavior)
			throws PathNotFoundException, ConstraintViolationException,
			VersionException, LockException, RepositoryException {

		return this.session
				.getImportContentHandler(parentAbsPath, uuidBehavior);

	}

	public synchronized Item getItem(final String absPath)
			throws PathNotFoundException, RepositoryException {

		return this.session.getItem(absPath);

	}

	public synchronized String[] getLockTokens() {

		return this.session.getLockTokens();

	}

	public synchronized String getNamespacePrefix(final String uri)
			throws NamespaceException, RepositoryException {

		return this.session.getNamespacePrefix(uri);

	}

	public synchronized String[] getNamespacePrefixes()
			throws RepositoryException {

		return this.session.getNamespacePrefixes();

	}

	public synchronized String getNamespaceURI(final String prefix)
			throws NamespaceException, RepositoryException {

		return this.session.getNamespaceURI(prefix);

	}

	public synchronized Node getNodeByUUID(final String uuid)
			throws ItemNotFoundException, RepositoryException {

		return this.session.getNodeByUUID(uuid);

	}

	public synchronized Repository getRepository() {

		return this.session.getRepository();

	}

	public synchronized Node getRootNode() throws RepositoryException {

		return this.session.getRootNode();

	}

	public synchronized String getUserID() {

		return this.session.getUserID();

	}

	public synchronized ValueFactory getValueFactory()
			throws UnsupportedRepositoryOperationException, RepositoryException {

		return this.session.getValueFactory();

	}

	public synchronized Workspace getWorkspace() {

		return this.session.getWorkspace();

	}

	public synchronized boolean hasPendingChanges() throws RepositoryException {

		return this.session.hasPendingChanges();

	}

	public synchronized Session impersonate(final Credentials credentials)
			throws LoginException, RepositoryException {

		return this.session.impersonate(credentials);

	}

	public synchronized void importXML(final String parentAbsPath,
			final InputStream in, final int uuidBehavior) throws IOException,
			PathNotFoundException, ItemExistsException,
			ConstraintViolationException, VersionException,
			InvalidSerializedDataException, LockException, RepositoryException {

		this.session.importXML(parentAbsPath, in, uuidBehavior);

	}

	public synchronized boolean isLive() {

		return this.session.isLive();

	}

	public synchronized boolean itemExists(final String absPath)
			throws RepositoryException {

		return this.session.itemExists(absPath);

	}

	public synchronized void logout() {

		this.session.logout();
		this.sessionClosingListener.sessionClosed(this.sessionId, this,
				this.session);

	}

	public synchronized void move(final String srcAbsPath,
			final String destAbsPath) throws ItemExistsException,
			PathNotFoundException, VersionException,
			ConstraintViolationException, LockException, RepositoryException {

		this.session.move(srcAbsPath, destAbsPath);

	}

	public synchronized void refresh(final boolean keepChanges)
			throws RepositoryException {

		this.session.refresh(keepChanges);

	}

	public synchronized void removeLockToken(final String lt) {

		this.session.removeLockToken(lt);

	}

	public synchronized void save() throws AccessDeniedException,
			ItemExistsException, ConstraintViolationException,
			InvalidItemStateException, VersionException, LockException,
			NoSuchNodeTypeException, RepositoryException {

		this.session.save();

	}

	public synchronized void setNamespacePrefix(final String newPrefix,
			final String existingUri) throws NamespaceException,
			RepositoryException {

		this.session.setNamespacePrefix(newPrefix, existingUri);

	}
}