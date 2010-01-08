package org.openspotlight.federation.processing;

import java.util.Set;

import org.openspotlight.federation.domain.artifact.Artifact;

/**
 * The Interface ArtifactProcessingResults.
 */
public interface ArtifactProcessingResults<T extends Artifact> {

	/**
	 * Gets the artifacts with error.
	 * 
	 * @return the artifacts with error
	 */
	public Set<T> getArtifactsWithError();

	/**
	 * Gets the ignored artifacts.
	 * 
	 * @return the ignored artifacts
	 */
	public Set<T> getIgnoredArtifacts();

	/**
	 * Gets the processed arifacts.
	 * 
	 * @return the processed arifacts
	 */
	public Set<T> getProcessedArifacts();

}