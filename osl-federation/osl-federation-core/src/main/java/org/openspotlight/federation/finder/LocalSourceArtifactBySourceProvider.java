package org.openspotlight.federation.finder;

import java.util.Collections;
import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.StreamArtifact;

public class LocalSourceArtifactBySourceProvider implements ArtifactFinderBySourceProvider {

    public <S extends ArtifactSource> Set<ArtifactFinder<? extends Artifact>> getForType( final Class<? extends Artifact> artifactType,
                                                                                          final S source ) {
        if (artifactType.equals(StreamArtifact.class)) {

            return org.openspotlight.common.util.Collections.<ArtifactFinder<? extends Artifact>>setOf(new LocalSourceStreamArtifactFinder(
                                                                                                                                           source));
        }
        return Collections.emptySet();
    }

}
