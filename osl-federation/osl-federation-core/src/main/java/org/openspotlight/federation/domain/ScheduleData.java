package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class ScheduleData.
 */
@Name( "schedule_data" )
public class ScheduleData implements SimpleNodeType, Serializable {

    /** The description. */
    private String         description;
    
    /** The cron information. */
    private String         cronInformation;
    
    /** The active. */
    private boolean        active;
    
    /** The group. */
    private Group          group;
    
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
     * Gets the cron information.
     * 
     * @return the cron information
     */
    @KeyProperty
    public String getCronInformation() {
        return this.cronInformation;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return this.description;
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
     * Sets the cron information.
     * 
     * @param cronInformation the new cron information
     */
    public void setCronInformation( final String cronInformation ) {
        this.cronInformation = cronInformation;
    }

    /**
     * Sets the description.
     * 
     * @param description the new description
     */
    public void setDescription( final String description ) {
        this.description = description;
    }

    /**
     * Sets the group.
     * 
     * @param group the new group
     */
    public void setGroup( final Group group ) {
        this.group = group;
    }

}
