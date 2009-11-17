package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;
import org.openspotlight.federation.domain.scheduler.ArtifactSourceSchedulable;
import org.openspotlight.federation.domain.scheduler.GroupSchedulable;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
@Name( "configuration" )
public class GlobalSettings implements SimpleNodeType, Serializable {

    private Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> schedulableCommandMap = new HashMap<Class<? extends Schedulable>, Class<? extends SchedulableCommand>>();

    private long                                                                   defaultSleepingIntervalInMilliseconds;

    /** The number of parallel threads. */
    private int                                                                    numberOfParallelThreads;

    /** The max result list size. */
    private int                                                                    maxResultListSize;

    public GlobalSettings() {
        this.schedulableCommandMap.put(Group.class, GroupSchedulable.class);
        this.schedulableCommandMap.put(ArtifactSource.class, ArtifactSourceSchedulable.class);
    }

    public long getDefaultSleepingIntervalInMilliseconds() {
        return this.defaultSleepingIntervalInMilliseconds;
    }

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

    public Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> getSchedulableCommandMap() {
        return this.schedulableCommandMap;
    }

    public void setDefaultSleepingIntervalInMilliseconds( final long defaultSleepingIntervalInMilliseconds ) {
        this.defaultSleepingIntervalInMilliseconds = defaultSleepingIntervalInMilliseconds;
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

    public void setSchedulableCommandMap( final Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> schedulableCommandMap ) {
        this.schedulableCommandMap = schedulableCommandMap;
    }

}
