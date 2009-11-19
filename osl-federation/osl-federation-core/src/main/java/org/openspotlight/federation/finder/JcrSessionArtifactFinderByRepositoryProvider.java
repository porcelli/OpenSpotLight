package org.openspotlight.federation.finder;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.openspotlight.common.Pair;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Repository;

public class JcrSessionArtifactFinderByRepositoryProvider implements ArtifactFinderByRepositoryProvider {

    private final Session                                                                              session;

    private final Map<Pair<Repository, Class<? extends Artifact>>, ArtifactFinder<? extends Artifact>> cache = new HashMap<Pair<Repository, Class<? extends Artifact>>, ArtifactFinder<? extends Artifact>>();

    public JcrSessionArtifactFinderByRepositoryProvider(
                                                         final Session session ) {
        this.session = session;
    }

    public synchronized <A extends Artifact> ArtifactFinder<A> getByRepository( final Class<A> artifactType,
                                                                                final Repository repository ) {
        final Pair<Repository, Class<? extends Artifact>> key = new Pair<Repository, Class<? extends Artifact>>(repository,
                                                                                                                artifactType);
        ArtifactFinder<A> result = (ArtifactFinder<A>)this.cache.get(key);
        if (result == null) {
            result = JcrSessionArtifactFinder.createArtifactFinder(artifactType, repository, this.session);
            this.cache.put(key, result);
        }
        return result;
    }

}
