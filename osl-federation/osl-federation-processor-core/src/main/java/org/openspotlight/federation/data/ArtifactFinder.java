package org.openspotlight.federation.data;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;

// TODO: Auto-generated Javadoc
/**
 * The Interface StreamArtifactFinder.
 */
public interface ArtifactFinder<A extends Artifact> {

    /**
     * Can accept artifact source.
     * 
     * @param artifactSource the artifact source
     * @return true, if successful
     */
    public boolean canAcceptArtifactSource( ArtifactSource artifactSource );

    /**
     * Find by path.
     * 
     * @param path the path
     * @param artifactSource the artifact source
     * @return the stream artifact
     */
    public A findByPath( ArtifactSource artifactSource,
                         String path );

    /**
     * Find by relative path.
     * 
     * @param relativeTo the relative to
     * @param path the path
     * @param artifactSource the artifact source
     * @return the stream artifact
     */
    public A findByRelativePath( ArtifactSource artifactSource,
                                 A relativeTo,
                                 String path );

    /**
     * List by path.
     * 
     * @param path the path
     * @param artifactSource the artifact source
     * @return the set< stream artifact>
     */
    public Set<A> listByPath( ArtifactSource artifactSource,
                              String path );

    /**
     * Retrieve all artifact names.
     * 
     * @param artifactSource the artifact source
     * @return the set< string>
     */
    public Set<String> retrieveAllArtifactNames( ArtifactSource artifactSource );

}
