package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.data.processing.BundleProcessor;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class BundleProcessorType.
 */
@Name( "bundle_processor_type" )
public class BundleProcessorType implements SimpleNodeType, Serializable {

    /** The type. */
    private Class<? extends BundleProcessor<? extends Artifact>> type;

    /** The active. */
    private boolean                                              active;

    /** The group. */
    private Group                                                group;

    /** The sources. */
    private Set<BundleSource>                                    sources = new HashSet<BundleSource>();

    /** The hash code. */
    private volatile int                                         hashCode;

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object o ) {
        if (!(o instanceof BundleProcessorType)) {
            return false;
        }
        final BundleProcessorType that = (BundleProcessorType)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.group, this.type), Arrays.andOf(that.group, that.type));
        return result;
    }

    /**
     * Gets the artifact source.
     * 
     * @return the artifact source
     */
    @ParentProperty
    public Group getGroup() {
        return this.group;
    }

    /**
     * Gets the sources.
     * 
     * @return the sources
     */
    public Set<BundleSource> getSources() {
        return this.sources;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @KeyProperty
    public Class<? extends BundleProcessor<? extends Artifact>> getType() {
        return this.type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.group, this.type);
            this.hashCode = result;
        }
        return result;
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
     * Sets the group.
     * 
     * @param group the new group
     */
    public void setGroup( final Group group ) {
        this.group = group;
    }

    /**
     * Sets the sources.
     * 
     * @param sources the new sources
     */
    public void setSources( final Set<BundleSource> sources ) {
        this.sources = sources;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType( final Class<? extends BundleProcessor<? extends Artifact>> type ) {
        this.type = type;
    }

}
