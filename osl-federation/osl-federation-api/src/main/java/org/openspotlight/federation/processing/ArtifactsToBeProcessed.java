package org.openspotlight.federation.processing;

import java.util.Set;

import org.openspotlight.federation.domain.Artifact;

/**
 * The Interface ArtifactsToBeProcessed.
 */
public interface ArtifactsToBeProcessed<T extends Artifact> {

	/**
	 * Gets the artifacts already processed.
	 * 
	 * @return the artifacts already processed
	 */
	public Set<T> getArtifactsAlreadyProcessed();

	/**
	 * Gets the artifacts to be processed.
	 * 
	 * @return the artifacts to be processed
	 */
	public Set<T> getArtifactsToBeProcessed();

	/**
	 * Sets the artifacts already processed.
	 * 
	 * @param artifacts
	 *            the new artifacts already processed
	 */
	public void setArtifactsAlreadyProcessed(Set<T> artifacts);

	/**
	 * Sets the artifacts to be processed.
	 * 
	 * @param artifacts
	 *            the new artifacts to be processed
	 */
	public void setArtifactsToBeProcessed(Set<T> artifacts);

}