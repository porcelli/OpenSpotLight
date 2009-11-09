package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc
/**
 * This is the {@link Artifact} class 'on steroids'. It has a lot of {@link PathElement path elements} used to locate a new
 * {@link Artifact} based on another one.
 */
public abstract class Artifact implements SimpleNodeType, Serializable {

    /** The Constant SEPARATOR. */
    final static String     SEPARATOR = "/";

    /** The artifact name. */
    private String          artifactName;

    /** The artifact complete name. */
    private volatile String artifactCompleteName;

    /** The change type. */
    private ChangeType      changeType;

    /** The parent. */
    private PathElement     parent;

    /** The hashcode. */
    private volatile int    hashcode;

    /**
     * Content equals.
     * 
     * @param other the other
     * @return true, if successful
     */
    public abstract boolean contentEquals( Artifact other );

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public boolean equals( final Object o ) {
        if (!(o instanceof Artifact)) {
            return false;
        }
        final Artifact that = (Artifact)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.getClass(), this.parent, this.artifactName, this.changeType),
                                                   Arrays.andOf(that.getClass(), that.parent, that.artifactName, that.changeType));
        return result;
    }

    /**
     * Gets the artifact complete name.
     * 
     * @return the artifact complete name
     */
    @TransientProperty
    public String getArtifactCompleteName() {
        String result = this.artifactCompleteName;
        if (result == null) {
            if (this.parent != null && this.artifactName != null) {
                result = this.parent.getCompletePath() + SEPARATOR + this.artifactName;
                this.artifactCompleteName = result;
            }
        }
        return result;
    }

    /**
     * Gets the artifact name.
     * 
     * @return the artifact name
     */
    @KeyProperty
    public String getArtifactName() {
        return this.artifactName;
    }

    /**
     * Gets the change type.
     * 
     * @return the change type
     */
    public ChangeType getChangeType() {
        return this.changeType;
    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    @ParentProperty
    public PathElement getParent() {
        return this.parent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = this.hashcode;
        if (result == 0) {
            result = HashCodes.hashOf(this.getClass(), this.parent, this.artifactName, this.changeType);
            this.hashcode = result;
        }
        return result;
    }

    /**
     * Sets the artifact name.
     * 
     * @param artifactName the new artifact name
     */
    public void setArtifactName( final String artifactName ) {
        this.artifactName = artifactName;
    }

    /**
     * Sets the change type.
     * 
     * @param changeType the new change type
     */
    public void setChangeType( final ChangeType changeType ) {
        this.changeType = changeType;
    }

    /**
     * Sets the parent.
     * 
     * @param parent the new parent
     */
    public void setParent( final PathElement parent ) {
        this.parent = parent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "StreamArtifact: " + this.getArtifactCompleteName() + " " + this.getChangeType();
    }

}
