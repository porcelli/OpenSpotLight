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
 * The Class Group.
 */
@Name( "group" )
public class Group implements SimpleNodeType, Serializable {

    /** The repository. */
    private Repository           repository;

    /** The type. */
    private String               type;

    /** The name. */
    private String               name;

    /** The active. */
    private boolean              active;

    /** The group. */
    private Group                group;

    /** The mappings. */
    private Set<ArtifactMapping> mappings        = new HashSet<ArtifactMapping>();

    /** The artifact sources. */
    private Set<ArtifactSource>  artifactSources = new HashSet<ArtifactSource>();

    /** The schedule data. */
    private Set<ScheduleData>    scheduleData    = new HashSet<ScheduleData>();

    private volatile int         hashCode;

    public boolean equals( final Object o ) {
        if (!(o instanceof Group)) {
            return false;
        }
        final Group that = (Group)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.group, this.repository, this.name),
                                                   Arrays.andOf(that.group, that.repository, that.name));
        return result;
    }

    /**
     * Gets the artifact sources.
     * 
     * @return the artifact sources
     */
    public Set<ArtifactSource> getArtifactSources() {
        return this.artifactSources;
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
     * Gets the mappings.
     * 
     * @return the mappings
     */
    public Set<ArtifactMapping> getMappings() {
        return this.mappings;
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

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    @ParentProperty
    public Repository getRepository() {
        return this.repository;
    }

    /**
     * Gets the schedule data.
     * 
     * @return the schedule data
     */
    public Set<ScheduleData> getScheduleData() {
        return this.scheduleData;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.group, this.repository, this.name);
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
     * Sets the artifact sources.
     * 
     * @param artifactSources the new artifact sources
     */
    public void setArtifactSources( final Set<ArtifactSource> artifactSources ) {
        this.artifactSources = artifactSources;
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
     * Sets the mappings.
     * 
     * @param mappings the new mappings
     */
    public void setMappings( final Set<ArtifactMapping> mappings ) {
        this.mappings = mappings;
    }

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName( final String name ) {
        this.name = name;
    }

    /**
     * Sets the repository.
     * 
     * @param repository the new repository
     */
    public void setRepository( final Repository repository ) {
        this.repository = repository;
    }

    /**
     * Sets the schedule data.
     * 
     * @param scheduleData the new schedule data
     */
    public void setScheduleData( final Set<ScheduleData> scheduleData ) {
        this.scheduleData = scheduleData;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType( final String type ) {
        this.type = type;
    }

}
