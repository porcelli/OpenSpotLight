package org.openspotlight.federation.finder;

import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Collections.setOf;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.domain.TableArtifact;
import org.openspotlight.federation.domain.ViewArtifact;

public enum ArtifactTypeRegistry {

    INSTANCE;

    private final Set<Class<? extends Artifact>> artifactTypes = unmodifiableSet(setOf(StreamArtifact.class, TableArtifact.class,
                                                                                       ViewArtifact.class));

    public Set<Class<? extends Artifact>> getRegisteredArtifactTypes() {
        return this.artifactTypes;
    }

}
