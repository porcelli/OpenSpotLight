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

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.jcr.provider.JcrConnectionProvider.SessionClosingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

class SessionWrapper implements SessionWithLock {

    private final Lock           lock   = new Lock();

    private final Session        session;
    private final int            sessionId;
    final SessionClosingListener sessionClosingListener;

    private final Logger         logger = LoggerFactory.getLogger(getClass());

    public SessionWrapper(
                           final Session session, final int sessionId,
                           final SessionClosingListener sessionClosingListener ) {
        this.session = session;
        this.sessionId = sessionId;
        this.sessionClosingListener = sessionClosingListener;
    }

    public void addLockToken( final String lt ) throws LockException,
        RepositoryException {
        synchronized (lock) {

            session.addLockToken(lt);
        }
    }

    public void checkPermission( final String absPath,
                                 final String actions )
        throws AccessControlException, RepositoryException {
        synchronized (lock) {

            session.checkPermission(absPath, actions);

        }
    }

    public void exportDocumentView( final String absPath,
                                    final ContentHandler contentHandler,
                                    final boolean skipBinary,
                                    final boolean noRecurse ) throws PathNotFoundException,
            SAXException, RepositoryException {
        synchronized (lock) {

            session.exportDocumentView(absPath, contentHandler, skipBinary,
                                       noRecurse);

        }
    }

    public void exportDocumentView( final String absPath,
                                    final OutputStream out,
                                    final boolean skipBinary,
                                    final boolean noRecurse ) throws IOException, PathNotFoundException,
            RepositoryException {
        synchronized (lock) {

            session.exportDocumentView(absPath, out, skipBinary, noRecurse);

        }
    }

    public void exportSystemView( final String absPath,
                                  final ContentHandler contentHandler,
                                  final boolean skipBinary,
                                  final boolean noRecurse ) throws PathNotFoundException,
            SAXException, RepositoryException {
        synchronized (lock) {

            session.exportSystemView(absPath, contentHandler, skipBinary,
                                     noRecurse);

        }
    }

    public void exportSystemView( final String absPath,
                                  final OutputStream out,
                                  final boolean skipBinary,
                                  final boolean noRecurse )
        throws IOException, PathNotFoundException, RepositoryException {
        synchronized (lock) {

            session.exportSystemView(absPath, out, skipBinary, noRecurse);

        }
    }

    public Object getAttribute( final String name ) {
        synchronized (lock) {

            return session.getAttribute(name);

        }
    }

    public String[] getAttributeNames() {
        synchronized (lock) {

            return session.getAttributeNames();

        }
    }

    public ContentHandler getImportContentHandler( final String parentAbsPath,
                                                   final int uuidBehavior ) throws PathNotFoundException,
            ConstraintViolationException, VersionException, LockException,
            RepositoryException {
        synchronized (lock) {

            return session.getImportContentHandler(parentAbsPath, uuidBehavior);

        }
    }

    public Item getItem( final String absPath ) throws PathNotFoundException,
        RepositoryException {
        synchronized (lock) {

            return session.getItem(absPath);

        }
    }

    public Lock getLockObject() {
        return lock;
    }

    public String[] getLockTokens() {
        synchronized (lock) {

            return session.getLockTokens();

        }
    }

    public String getNamespacePrefix( final String uri )
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

    public String getNamespaceURI( final String prefix )
        throws NamespaceException, RepositoryException {
        synchronized (lock) {

            return session.getNamespaceURI(prefix);

        }
    }

    public Node getNodeByUUID( final String uuid ) throws ItemNotFoundException,
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

    public Session impersonate( final Credentials credentials )
        throws LoginException, RepositoryException {
        synchronized (lock) {

            return session.impersonate(credentials);

        }
    }

    public void importXML( final String parentAbsPath,
                           final InputStream in,
                           final int uuidBehavior ) throws IOException, PathNotFoundException,
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

    public boolean itemExists( final String absPath ) throws RepositoryException {
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

    public void move( final String srcAbsPath,
                      final String destAbsPath )
        throws ItemExistsException, PathNotFoundException,
        VersionException, ConstraintViolationException, LockException,
        RepositoryException {
        synchronized (lock) {

            session.move(srcAbsPath, destAbsPath);

        }
    }

    public void refresh( final boolean keepChanges ) throws RepositoryException {
        synchronized (lock) {

            session.refresh(keepChanges);

        }
    }

    public void removeLockToken( final String lt ) {
        synchronized (lock) {

            session.removeLockToken(lt);

        }
    }

    public void save() throws AccessDeniedException, ItemExistsException,
        ConstraintViolationException, InvalidItemStateException,
        VersionException, LockException, NoSuchNodeTypeException,
        RepositoryException {
        synchronized (lock) {
            logger.debug("starting save");
            session.save();
            logger.debug("saving done");
        }
    }

    public void setNamespacePrefix( final String newPrefix,
                                    final String existingUri ) throws NamespaceException,
            RepositoryException {
        synchronized (lock) {

            session.setNamespacePrefix(newPrefix, existingUri);

        }
    }
}
