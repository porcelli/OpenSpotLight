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

class SessionWrapper implements SessionWithLock {

	private final Object lock = new Object();

	private final Session session;
	private final int sessionId;
	final SessionClosingListener sessionClosingListener;

	public SessionWrapper(final Session session, final int sessionId,
			final SessionClosingListener sessionClosingListener) {
		this.session = session;
		this.sessionId = sessionId;
		this.sessionClosingListener = sessionClosingListener;
	}

	public void addLockToken(final String lt) throws LockException,
			RepositoryException {
		synchronized (lock) {

			session.addLockToken(lt);
		}
	}

	public void checkPermission(final String absPath, final String actions)
			throws AccessControlException, RepositoryException {
		synchronized (lock) {

			session.checkPermission(absPath, actions);

		}
	}

	public void exportDocumentView(final String absPath,
			final ContentHandler contentHandler, final boolean skipBinary,
			final boolean noRecurse) throws PathNotFoundException,
			SAXException, RepositoryException {
		synchronized (lock) {

			session.exportDocumentView(absPath, contentHandler, skipBinary,
					noRecurse);

		}
	}

	public void exportDocumentView(final String absPath,
			final OutputStream out, final boolean skipBinary,
			final boolean noRecurse) throws IOException, PathNotFoundException,
			RepositoryException {
		synchronized (lock) {

			session.exportDocumentView(absPath, out, skipBinary, noRecurse);

		}
	}

	public void exportSystemView(final String absPath,
			final ContentHandler contentHandler, final boolean skipBinary,
			final boolean noRecurse) throws PathNotFoundException,
			SAXException, RepositoryException {
		synchronized (lock) {

			session.exportSystemView(absPath, contentHandler, skipBinary,
					noRecurse);

		}
	}

	public void exportSystemView(final String absPath, final OutputStream out,
			final boolean skipBinary, final boolean noRecurse)
			throws IOException, PathNotFoundException, RepositoryException {
		synchronized (lock) {

			session.exportSystemView(absPath, out, skipBinary, noRecurse);

		}
	}

	public Object getAttribute(final String name) {
		synchronized (lock) {

			return session.getAttribute(name);

		}
	}

	public String[] getAttributeNames() {
		synchronized (lock) {

			return session.getAttributeNames();

		}
	}

	public ContentHandler getImportContentHandler(final String parentAbsPath,
			final int uuidBehavior) throws PathNotFoundException,
			ConstraintViolationException, VersionException, LockException,
			RepositoryException {
		synchronized (lock) {

			return session.getImportContentHandler(parentAbsPath, uuidBehavior);

		}
	}

	public Item getItem(final String absPath) throws PathNotFoundException,
			RepositoryException {
		synchronized (lock) {

			return session.getItem(absPath);

		}
	}

	public Object getLockObject() {
		return lock;
	}

	public String[] getLockTokens() {
		synchronized (lock) {

			return session.getLockTokens();

		}
	}

	public String getNamespacePrefix(final String uri)
			throws NamespaceException, RepositoryException {
		synchronized (lock) {

			return session.getNamespacePrefix(uri);

		}
	}

	public String[] getNamespacePrefixes() throws RepositoryException {
		synchronized (lock) {

			return session.getNamespacePrefixes();

		}
	}

	public String getNamespaceURI(final String prefix)
			throws NamespaceException, RepositoryException {
		synchronized (lock) {

			return session.getNamespaceURI(prefix);

		}
	}

	public Node getNodeByUUID(final String uuid) throws ItemNotFoundException,
			RepositoryException {
		synchronized (lock) {

			return session.getNodeByUUID(uuid);

		}
	}

	public Repository getRepository() {
		synchronized (lock) {

			return session.getRepository();

		}
	}

	public Node getRootNode() throws RepositoryException {
		synchronized (lock) {

			return session.getRootNode();

		}
	}

	public String getUserID() {
		synchronized (lock) {

			return session.getUserID();

		}
	}

	public ValueFactory getValueFactory()
			throws UnsupportedRepositoryOperationException, RepositoryException {
		synchronized (lock) {

			return session.getValueFactory();

		}
	}

	public Workspace getWorkspace() {
		synchronized (lock) {

			return session.getWorkspace();

		}
	}

	public boolean hasPendingChanges() throws RepositoryException {
		synchronized (lock) {

			return session.hasPendingChanges();

		}
	}

	public Session impersonate(final Credentials credentials)
			throws LoginException, RepositoryException {
		synchronized (lock) {

			return session.impersonate(credentials);

		}
	}

	public void importXML(final String parentAbsPath, final InputStream in,
			final int uuidBehavior) throws IOException, PathNotFoundException,
			ItemExistsException, ConstraintViolationException,
			VersionException, InvalidSerializedDataException, LockException,
			RepositoryException {
		synchronized (lock) {

			session.importXML(parentAbsPath, in, uuidBehavior);

		}
	}

	public boolean isLive() {
		synchronized (lock) {

			return session.isLive();

		}
	}

	public boolean itemExists(final String absPath) throws RepositoryException {
		synchronized (lock) {

			return session.itemExists(absPath);

		}
	}

	public void logout() {
		synchronized (lock) {

			session.logout();
			sessionClosingListener.sessionClosed(sessionId, this, session);

		}
	}

	public void move(final String srcAbsPath, final String destAbsPath)
			throws ItemExistsException, PathNotFoundException,
			VersionException, ConstraintViolationException, LockException,
			RepositoryException {
		synchronized (lock) {

			session.move(srcAbsPath, destAbsPath);

		}
	}

	public void refresh(final boolean keepChanges) throws RepositoryException {
		synchronized (lock) {

			session.refresh(keepChanges);

		}
	}

	public void removeLockToken(final String lt) {
		synchronized (lock) {

			session.removeLockToken(lt);

		}
	}

	public void save() throws AccessDeniedException, ItemExistsException,
			ConstraintViolationException, InvalidItemStateException,
			VersionException, LockException, NoSuchNodeTypeException,
			RepositoryException {
		synchronized (lock) {

			session.save();

		}
	}

	public void setNamespacePrefix(final String newPrefix,
			final String existingUri) throws NamespaceException,
			RepositoryException {
		synchronized (lock) {

			session.setNamespacePrefix(newPrefix, existingUri);

		}
	}
}
