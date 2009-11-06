package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorType.
 */
@Name( "bundle_processor_type" )
public class BundleProcessorType implements SimpleNodeType, Serializable {

    /** The type. */
    private Class<?>       type;

    /** The active. */
    private boolean        active;

    /** The artifact source. */
    private ArtifactSource artifactSource;

    /**
     * Gets the artifact source.
     * 
     * @return the artifact source
     */
    @ParentProperty
    public ArtifactSource getArtifactSource() {
        return this.artifactSource;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @KeyProperty
    public Class<?> getType() {
        return this.type;
    }

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sets the active.
     * 
     * @param active the new active
     */
    public void setActive( final boolean active ) {
        this.active = active;
    }

    /**
     * Sets the artifact source.
     * 
     * @param artifactSource the new artifact source
     */
    public void setArtifactSource( final ArtifactSource artifactSource ) {
        this.artifactSource = artifactSource;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType( final Class<?> type ) {
        this.type = type;
    }

}
