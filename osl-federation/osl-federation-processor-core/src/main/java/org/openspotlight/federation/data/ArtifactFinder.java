package org.openspotlight.federation.data;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;

/**
 * The Interface StreamArtifactFinder.
 */
public interface ArtifactFinder<A extends Artifact> {

    /**
     * Find by path.
     * 
     * @param path the path
     * @return the set< stream artifact>
     */
    public A findByPath( String path );

    /**
     * Find by path.
     * 
     * @param artifactSourceReference the A source reference
     * @param path the path
     * @return the stream artifact
     */
    public A findByPath( String artifactSourceReference,
                         String path );

    /**
     * Find by relative path.
     * 
     * @param relativeTo the relative to
     * @param path the path
     * @return the stream artifact
     */
    public A findByRelativePath( A relativeTo,
                                 String path );

    /**
     * List by path.
     * 
     * @param path the path
     * @return the set< stream artifact>
     */
    public Set<A> listByPath( String path );

    /**
     * List by path.
     * 
     * @param path the path
     * @param artifactSourceReference the Artifact source reference
     * @return the set< stream artifact>
     */
    public Set<A> listByPath( String artifactSourceReference,
                              String path );

}
