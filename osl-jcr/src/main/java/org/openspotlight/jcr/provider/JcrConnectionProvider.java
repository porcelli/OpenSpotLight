package org.openspotlight.jcr.provider;

import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Files.delete;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

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

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.util.RepositoryLock;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.ClassPathResource;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * The Class JcrConnectionProvider is used to provide access to {@link Session jcr sessions} and {@link Repository jcr
 * repositories} in a way that there's no need to know the implementation.
 * 
 * @author feu
 */
public abstract class JcrConnectionProvider {

    /**
     * The Class JackRabbitConnectionProvider is a {@link JcrConnectionProvider} based on Jack Rabbit.
     */
    private static class JackRabbitConnectionProvider extends JcrConnectionProvider {

        /** The repository. */
        private Repository                                       repository;

        /** The repository closed. */
        private boolean                                          repositoryClosed = true;

        private static final AtomicInteger                       sessionIdFactory = new AtomicInteger(0);

        private static final CopyOnWriteArraySet<SessionWrapper> openSessions     = new CopyOnWriteArraySet<SessionWrapper>();

        /**
         * Instantiates a new jack rabbit connection provider.
         * 
         * @param data the data
         */
        public JackRabbitConnectionProvider(
                                             final JcrConnectionDescriptor data ) {
            super(data);
        }

        /* (non-Javadoc)
         * @see org.openspotlight.jcr.provider.JcrConnectionProvider#closeRepository()
         */
        @Override
        public synchronized void beforeCloseRepository() {
            if (this.repository == null) {
                this.repositoryClosed = true;
                return;
            }
            final RepositoryImpl repositoryCasted = (org.apache.jackrabbit.core.RepositoryImpl)this.repository;

            repositoryCasted.shutdown();

            final RepositoryLock repoLock = new RepositoryLock();
            try {
                repoLock.init(this.getData().getConfigurationDirectory());
                repoLock.acquire();
                repoLock.release();
            } catch (final RepositoryException e) {
            }
            if (this.getData().isTemporary()) {
                try {
                    delete(this.getData().getConfigurationDirectory());
                } catch (final SLException e) {
                    throw logAndReturnNew(e, SLRuntimeException.class);

                }
            }

            this.repositoryClosed = true;
        }

        public void openRepository() {
            if (this.repository == null || this.repositoryClosed) {
                try {
                    try {
                        delete(this.getData().getConfigurationDirectory());
                    } catch (final SLException e) {
                        throw logAndReturnNew(e, SLRuntimeException.class);
                    }

                    final RepositoryConfig config = RepositoryConfig.create(
                                                                            ClassPathResource.getResourceFromClassPath(this.getData().getXmlClasspathLocation()),
                                                                            this.getData().getConfigurationDirectory());

                    this.repository = RepositoryImpl.create(config);
                    this.repositoryClosed = false;
                } catch (final Exception e) {
                    throw logAndReturnNew(e, ConfigurationException.class);
                }
            }
        }

        /* (non-Javadoc)
         * @see org.openspotlight.jcr.provider.JcrConnectionProvider#openRepository()
         */
        @Override
        public synchronized void openRepositoryAndCleanIfItIsTemporary() {
            if (this.getData().isTemporary()) {
                this.beforeCloseRepository();
            }

            this.openRepository();
        }

        /* (non-Javadoc)
         * @see org.openspotlight.jcr.provider.JcrConnectionProvider#openSession()
         */
        @Override
        public Session openSession() {
            try {
                this.openRepository();
                Session newSession;
                newSession = this.repository.login(this.getData().getCredentials());
                final int sessionId = sessionIdFactory.getAndIncrement();
                final SessionWrapper wrappedSession = new SessionWrapper(newSession, sessionId, new SessionClosingListener() {

                    public void sessionClosed( final int id,
                                               final SessionWrapper wrapper,
                                               final Session session ) {
                        JackRabbitConnectionProvider.openSessions.remove(wrapper);

                    }
                });
                openSessions.add(wrappedSession);
                return wrappedSession;
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }

    }

    private interface SessionClosingListener {
        public void sessionClosed( int id,
                                   SessionWrapper wrapper,
                                   Session session );
    }

    private static class SessionWrapper implements Session {
        private final Session             session;
        private final StackTraceElement[] creationStackTrace;
        private final int                 sessionId;
        final SessionClosingListener      sessionClosingListener;

        public SessionWrapper(
                               final Session session, final int sessionId, final SessionClosingListener sessionClosingListener ) {
            this.session = session;
            this.creationStackTrace = Thread.currentThread().getStackTrace();
            this.sessionId = sessionId;
            this.sessionClosingListener = sessionClosingListener;
        }

        public void addLockToken( final String lt ) throws LockException, RepositoryException {
            this.session.addLockToken(lt);
        }

        public void checkPermission( final String absPath,
                                     final String actions ) throws AccessControlException, RepositoryException {
            this.session.checkPermission(absPath, actions);
        }

        public void exportDocumentView( final String absPath,
                                        final ContentHandler contentHandler,
                                        final boolean skipBinary,
                                        final boolean noRecurse ) throws PathNotFoundException, SAXException, RepositoryException {
            this.session.exportDocumentView(absPath, contentHandler, skipBinary, noRecurse);
        }

        public void exportDocumentView( final String absPath,
                                        final OutputStream out,
                                        final boolean skipBinary,
                                        final boolean noRecurse ) throws IOException, PathNotFoundException, RepositoryException {
            this.session.exportDocumentView(absPath, out, skipBinary, noRecurse);
        }

        public void exportSystemView( final String absPath,
                                      final ContentHandler contentHandler,
                                      final boolean skipBinary,
                                      final boolean noRecurse ) throws PathNotFoundException, SAXException, RepositoryException {
            this.session.exportSystemView(absPath, contentHandler, skipBinary, noRecurse);
        }

        public void exportSystemView( final String absPath,
                                      final OutputStream out,
                                      final boolean skipBinary,
                                      final boolean noRecurse ) throws IOException, PathNotFoundException, RepositoryException {
            this.session.exportSystemView(absPath, out, skipBinary, noRecurse);
        }

        public Object getAttribute( final String name ) {
            return this.session.getAttribute(name);
        }

        public String[] getAttributeNames() {
            return this.session.getAttributeNames();
        }

        public ContentHandler getImportContentHandler( final String parentAbsPath,
                                                       final int uuidBehavior )
            throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException {
            return this.session.getImportContentHandler(parentAbsPath, uuidBehavior);
        }

        public Item getItem( final String absPath ) throws PathNotFoundException, RepositoryException {
            return this.session.getItem(absPath);
        }

        public String[] getLockTokens() {
            return this.session.getLockTokens();
        }

        public String getNamespacePrefix( final String uri ) throws NamespaceException, RepositoryException {
            return this.session.getNamespacePrefix(uri);
        }

        public String[] getNamespacePrefixes() throws RepositoryException {
            return this.session.getNamespacePrefixes();
        }

        public String getNamespaceURI( final String prefix ) throws NamespaceException, RepositoryException {
            return this.session.getNamespaceURI(prefix);
        }

        public Node getNodeByUUID( final String uuid ) throws ItemNotFoundException, RepositoryException {
            return this.session.getNodeByUUID(uuid);
        }

        public Repository getRepository() {
            return this.session.getRepository();
        }

        public Node getRootNode() throws RepositoryException {
            return this.session.getRootNode();
        }

        public String getUserID() {
            return this.session.getUserID();
        }

        public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException {
            return this.session.getValueFactory();
        }

        public Workspace getWorkspace() {
            return this.session.getWorkspace();
        }

        public boolean hasPendingChanges() throws RepositoryException {
            return this.session.hasPendingChanges();
        }

        public Session impersonate( final Credentials credentials ) throws LoginException, RepositoryException {
            return this.session.impersonate(credentials);
        }

        public void importXML( final String parentAbsPath,
                               final InputStream in,
                               final int uuidBehavior )
            throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException,
            InvalidSerializedDataException, LockException, RepositoryException {
            this.session.importXML(parentAbsPath, in, uuidBehavior);
        }

        public boolean isLive() {
            return this.session.isLive();
        }

        public boolean itemExists( final String absPath ) throws RepositoryException {
            return this.session.itemExists(absPath);
        }

        public void logout() {
            this.session.logout();
            this.sessionClosingListener.sessionClosed(this.sessionId, this, this.session);
        }

        public void move( final String srcAbsPath,
                          final String destAbsPath )
            throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException,
            RepositoryException {
            this.session.move(srcAbsPath, destAbsPath);
        }

        public void refresh( final boolean keepChanges ) throws RepositoryException {
            this.session.refresh(keepChanges);
        }

        public void removeLockToken( final String lt ) {
            this.session.removeLockToken(lt);
        }

        public void save()
            throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException,
            VersionException, LockException, NoSuchNodeTypeException, RepositoryException {
            this.session.save();
        }

        public void setNamespacePrefix( final String newPrefix,
                                        final String existingUri ) throws NamespaceException, RepositoryException {
            this.session.setNamespacePrefix(newPrefix, existingUri);
        }
    }

    /** The cache. */
    private static Map<JcrConnectionDescriptor, JcrConnectionProvider> cache = new ConcurrentHashMap<JcrConnectionDescriptor, JcrConnectionProvider>();

    /**
     * Creates the from data.
     * 
     * @param data the data
     * @return the jcr connection provider
     */
    public static synchronized JcrConnectionProvider createFromData( final JcrConnectionDescriptor data ) {
        JcrConnectionProvider provider = cache.get(data);
        if (provider == null) {
            switch (data.getJcrType()) {
                case JACKRABBIT:
                    provider = new JackRabbitConnectionProvider(data);
                    break;
                default:
                    throw logAndReturn(new IllegalStateException("Invalid jcr type"));
            }
            cache.put(data, provider);
        }
        return provider;
    }

    /** The data. */
    private final JcrConnectionDescriptor data;

    /**
     * Instantiates a new jcr connection provider.
     * 
     * @param data the data
     */
    JcrConnectionProvider(
                           final JcrConnectionDescriptor data ) {
        this.data = data;

    }

    /**
     * Close repository.
     */
    protected abstract void beforeCloseRepository();

    public void closeRepository() {
        this.beforeCloseRepository();
        cache.remove(this);
    }

    /**
     * Gets the data.
     * 
     * @return the data
     */
    public JcrConnectionDescriptor getData() {
        return this.data;
    }

    public final boolean isTemporary() {
        return this.data.isTemporary();
    }

    /**
     * Open repository.
     * 
     * @return the repository
     */
    public abstract void openRepository();

    /**
     * Open repository.
     * 
     * @return the repository
     */
    public abstract void openRepositoryAndCleanIfItIsTemporary();

    /**
     * Open session.
     * 
     * @return the session
     */
    public abstract Session openSession();

}
