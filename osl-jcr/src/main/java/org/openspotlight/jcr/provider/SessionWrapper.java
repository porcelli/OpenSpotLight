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

    private final Object              lock = new Object();

    private final Session             session;
    private final StackTraceElement[] creationStackTrace;
    private final int                 sessionId;
    final SessionClosingListener      sessionClosingListener;

    public SessionWrapper(
                           final Session session, final int sessionId,
                           final SessionClosingListener sessionClosingListener ) {
        this.session = session;
        this.creationStackTrace = Thread.currentThread().getStackTrace();
        this.sessionId = sessionId;
        this.sessionClosingListener = sessionClosingListener;
    }

    public void addLockToken( final String lt ) throws LockException,
        RepositoryException {
        synchronized (this.lock) {

            this.session.addLockToken(lt);
        }
    }

    public void checkPermission( final String absPath,
                                 final String actions )
        throws AccessControlException, RepositoryException {
        synchronized (this.lock) {

            this.session.checkPermission(absPath, actions);

        }
    }

    public void exportDocumentView( final String absPath,
                                    final ContentHandler contentHandler,
                                    final boolean skipBinary,
                                    final boolean noRecurse ) throws PathNotFoundException,
        SAXException, RepositoryException {
        synchronized (this.lock) {

            this.session.exportDocumentView(absPath, contentHandler,
                                            skipBinary, noRecurse);

        }
    }

    public void exportDocumentView( final String absPath,
                                    final OutputStream out,
                                    final boolean skipBinary,
                                    final boolean noRecurse ) throws IOException, PathNotFoundException,
        RepositoryException {
        synchronized (this.lock) {

            this.session
                        .exportDocumentView(absPath, out, skipBinary, noRecurse);

        }
    }

    public void exportSystemView( final String absPath,
                                  final ContentHandler contentHandler,
                                  final boolean skipBinary,
                                  final boolean noRecurse ) throws PathNotFoundException,
        SAXException, RepositoryException {
        synchronized (this.lock) {

            this.session.exportSystemView(absPath, contentHandler, skipBinary,
                                          noRecurse);

        }
    }

    public void exportSystemView( final String absPath,
                                  final OutputStream out,
                                  final boolean skipBinary,
                                  final boolean noRecurse )
        throws IOException, PathNotFoundException, RepositoryException {
        synchronized (this.lock) {

            this.session.exportSystemView(absPath, out, skipBinary, noRecurse);

        }
    }

    public Object getAttribute( final String name ) {
        synchronized (this.lock) {

            return this.session.getAttribute(name);

        }
    }

    public String[] getAttributeNames() {
        synchronized (this.lock) {

            return this.session.getAttributeNames();

        }
    }

    public ContentHandler getImportContentHandler( final String parentAbsPath,
                                                   final int uuidBehavior ) throws PathNotFoundException,
        ConstraintViolationException, VersionException, LockException,
        RepositoryException {
        synchronized (this.lock) {

            return this.session.getImportContentHandler(parentAbsPath,
                                                        uuidBehavior);

        }
    }

    public Item getItem( final String absPath ) throws PathNotFoundException,
        RepositoryException {
        synchronized (this.lock) {

            return this.session.getItem(absPath);

        }
    }

    public Object getLockObject() {
        return this.lock;
    }

    public String[] getLockTokens() {
        synchronized (this.lock) {

            return this.session.getLockTokens();

        }
    }

    public String getNamespacePrefix( final String uri )
        throws NamespaceException, RepositoryException {
        synchronized (this.lock) {

            return this.session.getNamespacePrefix(uri);

        }
    }

    public String[] getNamespacePrefixes() throws RepositoryException {
        synchronized (this.lock) {

            return this.session.getNamespacePrefixes();

        }
    }

    public String getNamespaceURI( final String prefix )
        throws NamespaceException, RepositoryException {
        synchronized (this.lock) {

            return this.session.getNamespaceURI(prefix);

        }
    }

    public Node getNodeByUUID( final String uuid ) throws ItemNotFoundException,
        RepositoryException {
        synchronized (this.lock) {

            return this.session.getNodeByUUID(uuid);

        }
    }

    public Repository getRepository() {
        synchronized (this.lock) {

            return this.session.getRepository();

        }
    }

    public Node getRootNode() throws RepositoryException {
        synchronized (this.lock) {

            return this.session.getRootNode();

        }
    }

    public String getUserID() {
        synchronized (this.lock) {

            return this.session.getUserID();

        }
    }

    public ValueFactory getValueFactory()
        throws UnsupportedRepositoryOperationException, RepositoryException {
        synchronized (this.lock) {

            return this.session.getValueFactory();

        }
    }

    public Workspace getWorkspace() {
        synchronized (this.lock) {

            return this.session.getWorkspace();

        }
    }

    public boolean hasPendingChanges() throws RepositoryException {
        synchronized (this.lock) {

            return this.session.hasPendingChanges();

        }
    }

    public Session impersonate( final Credentials credentials )
        throws LoginException, RepositoryException {
        synchronized (this.lock) {

            return this.session.impersonate(credentials);

        }
    }

    public void importXML( final String parentAbsPath,
                           final InputStream in,
                           final int uuidBehavior ) throws IOException, PathNotFoundException,
        ItemExistsException, ConstraintViolationException,
        VersionException, InvalidSerializedDataException, LockException,
        RepositoryException {
        synchronized (this.lock) {

            this.session.importXML(parentAbsPath, in, uuidBehavior);

        }
    }

    public boolean isLive() {
        synchronized (this.lock) {

            return this.session.isLive();

        }
    }

    public boolean itemExists( final String absPath ) throws RepositoryException {
        synchronized (this.lock) {

            return this.session.itemExists(absPath);

        }
    }

    public void logout() {
        synchronized (this.lock) {

            this.session.logout();
            this.sessionClosingListener.sessionClosed(this.sessionId, this,
                                                      this.session);

        }
    }

    public void move( final String srcAbsPath,
                      final String destAbsPath )
        throws ItemExistsException, PathNotFoundException,
        VersionException, ConstraintViolationException, LockException,
        RepositoryException {
        synchronized (this.lock) {

            this.session.move(srcAbsPath, destAbsPath);

        }
    }

    public void refresh( final boolean keepChanges ) throws RepositoryException {
        synchronized (this.lock) {

            this.session.refresh(keepChanges);

        }
    }

    public void removeLockToken( final String lt ) {
        synchronized (this.lock) {

            this.session.removeLockToken(lt);

        }
    }

    public void save() throws AccessDeniedException, ItemExistsException,
        ConstraintViolationException, InvalidItemStateException,
        VersionException, LockException, NoSuchNodeTypeException,
        RepositoryException {
        synchronized (this.lock) {

            this.session.save();

        }
    }

    public void setNamespacePrefix( final String newPrefix,
                                    final String existingUri ) throws NamespaceException,
        RepositoryException {
        synchronized (this.lock) {

            this.session.setNamespacePrefix(newPrefix, existingUri);

        }
    }
}
