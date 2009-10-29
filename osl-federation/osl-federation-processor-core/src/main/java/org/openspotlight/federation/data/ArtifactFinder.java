package org.openspotlight.federation.data;

import java.util.Set;

import org.openspotlight.federation.data.impl.ArtifactAboutToChange;

public interface ArtifactFinder {

    public <A extends ArtifactAboutToChange> Set<A> findByPath( String path );

    public <A extends ArtifactAboutToChange> A findByPath( String artifactSourceReference,
                                              String path );

    public <A extends ArtifactAboutToChange> A findByRelativePath( A relativeTo,
                                                      String path );

}
