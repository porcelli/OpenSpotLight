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
 * The Class BundleSource.
 */
@Name( "bundle_source" )
public class BundleSource implements SimpleNodeType, Serializable {

    private static final long   serialVersionUID = -5266436076638737597L;

    /** The relative. */
    private String              relative;

    /** The source. */
    private BundleProcessorType bundleProcessorType;

    /** The excludeds. */
    private Set<String>         excludeds        = new HashSet<String>();

    /** The includeds. */
    private Set<String>         includeds        = new HashSet<String>();

    /** The hash code. */
    private volatile int        hashCode;

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object o ) {
        if (!(o instanceof BundleSource)) {
            return false;
        }
        final BundleSource that = (BundleSource)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.bundleProcessorType, this.relative),
                                                   Arrays.andOf(that.bundleProcessorType, that.relative));
        return result;
    }

    /**
     * Gets the bundle processor type.
     * 
     * @return the bundle processor type
     */
    @ParentProperty
    public BundleProcessorType getBundleProcessorType() {
        return this.bundleProcessorType;
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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(this.bundleProcessorType, this.relative);
            this.hashCode = result;
        }
        return result;
    }

    /**
     * Sets the bundle processor type.
     * 
     * @param bundleProcessorType the new bundle processor type
     */
    public void setBundleProcessorType( final BundleProcessorType bundleProcessorType ) {
        this.bundleProcessorType = bundleProcessorType;
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

}
