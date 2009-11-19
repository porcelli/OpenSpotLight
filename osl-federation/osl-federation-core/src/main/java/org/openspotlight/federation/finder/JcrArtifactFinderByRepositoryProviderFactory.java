package org.openspotlight.federation.finder;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.jcr.Session;

import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating JcrArtifactFinderByRepositoryProvider objects.
 */
public class JcrArtifactFinderByRepositoryProviderFactory implements ArtifactFinderByRepositoryProviderFactory {

    private final CopyOnWriteArrayList<Session> oppendedSessions = new CopyOnWriteArrayList<Session>();

    /** The provider. */
    private final JcrConnectionProvider         provider;

    /**
     * Instantiates a new jcr artifact finder by repository provider factory.
     * 
     * @param descriptor the descriptor
     */
    public JcrArtifactFinderByRepositoryProviderFactory(
                                                         final JcrConnectionDescriptor descriptor ) {
        this.provider = JcrConnectionProvider.createFromData(descriptor);
    }

    public synchronized void closeResources() {
        for (final Session sess : this.oppendedSessions) {
            sess.logout();
        }

    }

    /* (non-Javadoc)
     * @see org.openspotlight.federation.finder.ArtifactFinderByRepositoryProviderFactory#createNew()
     */
    public ArtifactFinderByRepositoryProvider createNew() {
        final Session session = this.provider.openSession();
        this.oppendedSessions.add(session);
        return new JcrSessionArtifactFinderByRepositoryProvider(session);
    }

}
