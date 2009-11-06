package org.openspotlight.federation.data;

import java.util.Set;

import org.openspotlight.federation.data.impl.Artifact;

public interface ArtifactFinder {

    public <A extends Artifact> Set<A> findByPath( String path );

    public <A extends Artifact> A findByPath( String artifactSourceReference,
                                              String path );

    public <A extends Artifact> A findByRelativePath( A relativeTo,
                                                      String path );

}
