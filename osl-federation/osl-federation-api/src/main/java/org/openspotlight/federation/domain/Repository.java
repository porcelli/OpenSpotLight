package org.openspotlight.federation.domain;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class Repository.
 */
@Name( "repository" )
public class Repository implements SimpleNodeType, Serializable {

    public static interface GroupVisitor {
        public void visitGroup( Group group );
    }

    /** The artifact sources. */
    private Set<ArtifactSource> artifactSources = new HashSet<ArtifactSource>();

    /** The name. */
    private String              name;

    /** The groups. */
    private Set<Group>          groups          = new HashSet<Group>();

    /** The active. */
    private boolean             active;

    private volatile int        hashCode;

    public void acceptGroupVisitor( final GroupVisitor visitor ) {
        for (final Group entry : this.getGroups()) {
            entry.acceptVisitor(visitor);
        }
    }

    public boolean equals( final Object o ) {
        if (!(o instanceof Repository)) {
            return false;
        }
        final Repository that = (Repository)o;
        return Equals.eachEquality(of(this.name), andOf(that.name));
    }

    public Set<ArtifactSource> getArtifactSources() {
        return this.artifactSources;
    }

    /**
     * Gets the groups.
     * 
     * @return the groups
     */
    public Set<Group> getGroups() {
        return this.groups;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @KeyProperty
    public String getName() {
        return this.name;
    }

    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.name);
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

    public void setArtifactSources( final Set<ArtifactSource> artifactSources ) {
        this.artifactSources = artifactSources;
    }

    public void setGroups( final Set<Group> groups ) {
        this.groups = groups;
    }

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName( final String name ) {
        this.name = name;
    }

}
