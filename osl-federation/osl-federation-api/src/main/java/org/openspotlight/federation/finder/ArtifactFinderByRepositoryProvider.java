package org.openspotlight.federation.finder;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.Repository;

public interface ArtifactFinderByRepositoryProvider {

    public <A extends Artifact> ArtifactFinder<A> getByRepository( Class<A> artifactType,
                                                                   final Repository repository );

}
