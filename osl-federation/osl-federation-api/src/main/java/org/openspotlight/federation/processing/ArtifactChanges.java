package org.openspotlight.federation.processing;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;

/**
 * The Interface ArtifactChanges.
 */
public interface ArtifactChanges<T extends Artifact> {

	/**
	 * Gets the changed artifacts.
	 * 
	 * @return the changed artifacts
	 */
	public Set<T> getChangedArtifacts();

	/**
	 * Gets the excluded artifacts.
	 * 
	 * @return the excluded artifacts
	 */
	public Set<T> getExcludedArtifacts();

	/**
	 * Gets the included artifacts.
	 * 
	 * @return the included artifacts
	 */
	public Set<T> getIncludedArtifacts();

	/**
	 * Gets the not changed artifacts.
	 * 
	 * @return the not changed artifacts
	 */
	public Set<T> getNotChangedArtifacts();
}