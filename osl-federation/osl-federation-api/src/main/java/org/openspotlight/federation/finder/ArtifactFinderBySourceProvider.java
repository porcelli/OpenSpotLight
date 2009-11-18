package org.openspotlight.federation.finder;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;

/**
 * This class will serve as a factory to create {@link ArtifactFinder Artifact finders} based on it's type and also on the source
 * been passed. This will be used only during artifact loading.
 * 
 * @author feu
 */
public interface ArtifactFinderBySourceProvider {

    /**
     * returns a set of Artifact finder for a given type. It should return an empty collection when it does't have any
     * {@link ArtifactFinder} for the given {@link ArtifactSource}.
     * 
     * @param artifactType the artifact type
     * @param source the source
     * @return the for type
     */
    public <S extends ArtifactSource> Set<ArtifactFinder<? extends Artifact>> getForType( Class<? extends Artifact> artifactType,
                                                                                          S source );

}
