package org.openspotlight.federation.data;

import java.util.Set;

import org.openspotlight.federation.domain.StreamArtifact;

/**
 * The Interface StreamArtifactFinder.
 */
public interface StreamArtifactFinder {

    /**
     * Find by path.
     * 
     * @param path the path
     * @return the set< stream artifact>
     */
    public StreamArtifact findByPath( String path );

    /**
     * Find by path.
     * 
     * @param artifactSourceReference the artifact source reference
     * @param path the path
     * @return the stream artifact
     */
    public StreamArtifact findByPath( String artifactSourceReference,
                                      String path );

    /**
     * Find by relative path.
     * 
     * @param relativeTo the relative to
     * @param path the path
     * @return the stream artifact
     */
    public StreamArtifact findByRelativePath( StreamArtifact relativeTo,
                                              String path );

    /**
     * List by path.
     * 
     * @param path the path
     * @return the set< stream artifact>
     */
    public Set<StreamArtifact> listByPath( String path );

    /**
     * List by path.
     * 
     * @param path the path
     * @param artifactSourceReference the artifact source reference
     * @return the set< stream artifact>
     */
    public Set<StreamArtifact> listByPath( String artifactSourceReference,
                                           String path );

}
