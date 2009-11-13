package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.jcr.LogableObject;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class ArtifactSource.
 */
@Name( "artifact_source" )
public class ArtifactSource implements SimpleNodeType, Serializable, LogableObject, Schedulable {

    private final List<String>         cronInformation = new ArrayList<String>();

    /** The repository. */
    private Repository                 repository;

    /** The active. */
    private boolean                    active;

    /** The initial lookup. */
    private String                     initialLookup;

    /** The name. */
    private String                     name;

    /** The mappings. */
    private Set<ArtifactSourceMapping> mappings        = new HashSet<ArtifactSourceMapping>();

    private volatile int               hashCode;

    public boolean equals( final Object o ) {
        if (!(o instanceof ArtifactSource)) {
            return false;
        }
        final ArtifactSource that = (ArtifactSource)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.getClass(), this.name, this.repository),
                                                   Arrays.andOf(that.getClass(), that.name, that.repository));
        return result;
    }

    public List<String> getCronInformation() {
        return this.cronInformation;
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
    public Set<ArtifactSourceMapping> getMappings() {
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

    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.getClass(), this.name, this.repository);
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
    public void setMappings( final Set<ArtifactSourceMapping> mappings ) {
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

}
