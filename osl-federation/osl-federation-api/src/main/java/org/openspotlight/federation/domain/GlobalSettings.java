package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
@Name("configuration")
public class GlobalSettings implements SimpleNodeType, Serializable {

	private Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> schedulableCommandMap = new HashMap<Class<? extends Schedulable>, Class<? extends SchedulableCommand>>();

	private long defaultSleepingIntervalInMilliseconds;

	/** The number of parallel threads. */
	private int numberOfParallelThreads;

	/** The max result list size. */
	private int maxResultListSize;

	private String systemUser;

	private String systemPassword;

	public GlobalSettings() {
	}

	public long getDefaultSleepingIntervalInMilliseconds() {
		return defaultSleepingIntervalInMilliseconds;
	}

	/**
	 * Gets the max result list size.
	 * 
	 * @return the max result list size
	 */
	public int getMaxResultListSize() {
		return maxResultListSize;
	}

	/**
	 * Gets the number of parallel threads.
	 * 
	 * @return the number of parallel threads
	 */
	public int getNumberOfParallelThreads() {
		return numberOfParallelThreads;
	}

	public Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> getSchedulableCommandMap() {
		return schedulableCommandMap;
	}

	public String getSystemPassword() {
		return systemPassword;
	}

	public String getSystemUser() {
		return systemUser;
	}

	public void setDefaultSleepingIntervalInMilliseconds(
			final long defaultSleepingIntervalInMilliseconds) {
		this.defaultSleepingIntervalInMilliseconds = defaultSleepingIntervalInMilliseconds;
	}

	/**
	 * Sets the max result list size.
	 * 
	 * @param maxResultListSize
	 *            the new max result list size
	 */
	public void setMaxResultListSize(final int maxResultListSize) {
		this.maxResultListSize = maxResultListSize;
	}

	/**
	 * Sets the number of parallel threads.
	 * 
	 * @param numberOfParallelThreads
	 *            the new number of parallel threads
	 */
	public void setNumberOfParallelThreads(final int numberOfParallelThreads) {
		this.numberOfParallelThreads = numberOfParallelThreads;
	}

	public void setSchedulableCommandMap(
			final Map<Class<? extends Schedulable>, Class<? extends SchedulableCommand>> schedulableCommandMap) {
		this.schedulableCommandMap = schedulableCommandMap;
	}

	public void setSystemPassword(final String systemPassword) {
		this.systemPassword = systemPassword;
	}

	public void setSystemUser(final String systemUser) {
		this.systemUser = systemUser;
	}

}
