package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class ArtifactMapping.
 */
@Name( "artifact_source_mapping" )
public class ArtifactSourceMapping implements SimpleNodeType, Serializable {

    /** The to. */
    private String         to;

    /** The relative. */
    private String         from;

    /** The source. */
    private ArtifactSource source;

    /** The excludeds. */
    private Set<String>    excludeds = new HashSet<String>();

    /** The includeds. */
    private Set<String>    includeds = new HashSet<String>();

    /** The hash code. */
    private volatile int   hashCode;

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object o ) {
        if (!(o instanceof ArtifactSourceMapping)) {
            return false;
        }
        final ArtifactSourceMapping that = (ArtifactSourceMapping)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.to, this.source, this.from), Arrays.andOf(that.to, that.source,
                                                                                                            that.from));
        return result;
    }

    /**
     * Gets the excludeds.
     * 
     * @return the excludeds
     */
    public Set<String> getExcludeds() {
        return this.excludeds;
    }

    /**
     * Gets the relative.
     * 
     * @return the relative
     */
    @KeyProperty
    public String getFrom() {
        return this.from;
    }

    /**
     * Gets the includeds.
     * 
     * @return the includeds
     */
    public Set<String> getIncludeds() {
        return this.includeds;
    }

    /**
     * Gets the source.
     * 
     * @return the source
     */
    @ParentProperty
    public ArtifactSource getSource() {
        return this.source;
    }

    /**
     * Gets the to.
     * 
     * @return the to
     */
    public String getTo() {
        return this.to;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.to, this.source, this.from);
            this.hashCode = result;
        }
        return result;
    }

    /**
     * Sets the excludeds.
     * 
     * @param excludeds the new excludeds
     */
    public void setExcludeds( final Set<String> excludeds ) {
        this.excludeds = excludeds;
    }

    /**
     * Sets the relative.
     * 
     * @param from the new relative
     */
    public void setFrom( final String from ) {
        this.from = from;
    }

    /**
     * Sets the includeds.
     * 
     * @param includeds the new includeds
     */
    public void setIncludeds( final Set<String> includeds ) {
        this.includeds = includeds;
    }

    /**
     * Sets the source.
     * 
     * @param source the new source
     */
    public void setSource( final ArtifactSource source ) {
        this.source = source;
    }

    /**
     * Sets the to.
     * 
     * @param to the new to
     */
    public void setTo( final String to ) {
        this.to = to;
    }

}
