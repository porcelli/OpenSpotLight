package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
@Name( "configuration" )
public class Configuration implements SimpleNodeType, Serializable {

    /** The number of parallel threads. */
    private int numberOfParallelThreads;
    
    /** The max result list size. */
    private int maxResultListSize;

    /**
     * Gets the max result list size.
     * 
     * @return the max result list size
     */
    public int getMaxResultListSize() {
        return this.maxResultListSize;
    }

    /**
     * Gets the number of parallel threads.
     * 
     * @return the number of parallel threads
     */
    public int getNumberOfParallelThreads() {
        return this.numberOfParallelThreads;
    }

    /**
     * Sets the max result list size.
     * 
     * @param maxResultListSize the new max result list size
     */
    public void setMaxResultListSize( final int maxResultListSize ) {
        this.maxResultListSize = maxResultListSize;
    }

    /**
     * Sets the number of parallel threads.
     * 
     * @param numberOfParallelThreads the new number of parallel threads
     */
    public void setNumberOfParallelThreads( final int numberOfParallelThreads ) {
        this.numberOfParallelThreads = numberOfParallelThreads;
    }

}
