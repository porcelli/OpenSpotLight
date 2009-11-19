package org.openspotlight.federation.finder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jcr.Session;

import org.openspotlight.common.Pair;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Repository;

public class JcrSessionArtifactBySourceProvider implements ArtifactFinderBySourceProvider {

    private final Repository                                                                                repository;
    private final Session                                                                                   session;

    Map<Pair<Class<? extends Artifact>, ? extends ArtifactSource>, Set<ArtifactFinder<? extends Artifact>>> cache = new HashMap<Pair<Class<? extends Artifact>, ? extends ArtifactSource>, Set<ArtifactFinder<? extends Artifact>>>();

    public JcrSessionArtifactBySourceProvider(
                                               final Repository repository, final Session session ) {
        this.repository = repository;
        this.session = session;
    }

    public synchronized <S extends ArtifactSource> Set<ArtifactFinder<? extends Artifact>> getForType( final Class<? extends Artifact> artifactType,
                                                                                                       final S source ) {
        final Pair<Class<? extends Artifact>, ? extends ArtifactSource> key = new Pair<Class<? extends Artifact>, ArtifactSource>(
                                                                                                                                  artifactType,
                                                                                                                                  source);
        Set<ArtifactFinder<? extends Artifact>> result = this.cache.get(key);
        if (result == null) {
            result = org.openspotlight.common.util.Collections.<ArtifactFinder<? extends Artifact>>setOf(JcrSessionArtifactFinder.createArtifactFinder(

            artifactType, this.repository, this.session));
            this.cache.put(key, result);
        }
        return result;
    }
}
