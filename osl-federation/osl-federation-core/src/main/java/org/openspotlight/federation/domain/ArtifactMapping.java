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

// TODO: Auto-generated Javadoc
/**
 * The Class ArtifactMapping.
 */
@Name( "artifact_mapping" )
public class ArtifactMapping implements SimpleNodeType, Serializable {

    /** The relative. */
    private String         relative;

    /** The group. */
    private Group          group;

    /** The source. */
    private ArtifactSource source;

    /** The excludeds. */
    private Set<String>    excludeds = new HashSet<String>();

    /** The includeds. */
    private Set<String>    includeds = new HashSet<String>();

    private volatile int   hashCode;

    public boolean equals( final Object o ) {
        if (!(o instanceof Group)) {
            return false;
        }
        final ArtifactMapping that = (ArtifactMapping)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.group, this.source, this.relative), Arrays.andOf(that.group,
                                                                                                                   that.source,
                                                                                                                   that.relative));
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
     * Gets the group.
     * 
     * @return the group
     */
    @ParentProperty
    public Group getGroup() {
        return this.group;
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
     * Gets the relative.
     * 
     * @return the relative
     */
    @KeyProperty
    public String getRelative() {
        return this.relative;
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

    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.group, this.source, this.relative);
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
     * Sets the group.
     * 
     * @param group the new group
     */
    public void setGroup( final Group group ) {
        this.group = group;
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
     * Sets the relative.
     * 
     * @param relative the new relative
     */
    public void setRelative( final String relative ) {
        this.relative = relative;
    }

    /**
     * Sets the source.
     * 
     * @param source the new source
     */
    public void setSource( final ArtifactSource source ) {
        this.source = source;
    }

}
