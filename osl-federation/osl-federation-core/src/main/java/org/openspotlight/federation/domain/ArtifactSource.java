package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class ArtifactSource.
 */
@Name( "artifact_source" )
public class ArtifactSource implements SimpleNodeType, Serializable {

    /** The repository. */
    private Repository               repository;

    /** The active. */
    private boolean                  active;

    /** The initial lookup. */
    private String                   initialLookup;

    /** The name. */
    private String                   name;

    /** The group. */
    private Group                    group;

    /** The mappings. */
    private Set<ArtifactMapping>     mappings;

    /** The schedule datas. */
    private Set<ScheduleData>        scheduleDatas;

    /** The bundle processor types. */
    private Set<BundleProcessorType> bundleProcessorTypes;

    /**
     * Gets the bundle processor types.
     * 
     * @return the bundle processor types
     */
    public Set<BundleProcessorType> getBundleProcessorTypes() {
        return this.bundleProcessorTypes;
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
     * Gets the initial lookup.
     * 
     * @return the initial lookup
     */
    public String getInitialLookup() {
        return this.initialLookup;
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
     * Gets the schedule datas.
     * 
     * @return the schedule datas
     */
    public Set<ScheduleData> getScheduleDatas() {
        return this.scheduleDatas;
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
     * Sets the bundle processor types.
     * 
     * @param bundleProcessorTypes the new bundle processor types
     */
    public void setBundleProcessorTypes( final Set<BundleProcessorType> bundleProcessorTypes ) {
        this.bundleProcessorTypes = bundleProcessorTypes;
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
     * Sets the initial lookup.
     * 
     * @param initialLookup the new initial lookup
     */
    public void setInitialLookup( final String initialLookup ) {
        this.initialLookup = initialLookup;
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
     * Sets the schedule datas.
     * 
     * @param scheduleDatas the new schedule datas
     */
    public void setScheduleDatas( final Set<ScheduleData> scheduleDatas ) {
        this.scheduleDatas = scheduleDatas;
    }
}
