package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc
/**
 * The Class Repository.
 */
@Name( "repository" )
public class Repository implements SimpleNodeType, Serializable {

    private Configuration            configuration;

    /** The name. */
    private String                   name;

    /** The groups. */
    private final Map<String, Group> groups = new HashMap<String, Group>();

    /** The active. */
    private boolean                  active;

    @TransientProperty
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Gets the groups.
     * 
     * @return the groups
     */
    public Map<String, Group> getGroups() {
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

    public void setConfiguration( final Configuration configuration ) {
        this.configuration = configuration;
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
