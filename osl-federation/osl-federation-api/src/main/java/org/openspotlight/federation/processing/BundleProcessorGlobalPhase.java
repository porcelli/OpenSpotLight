package org.openspotlight.federation.processing;

import java.util.Set;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.Artifact;

public interface BundleProcessorGlobalPhase<T extends Artifact> {

	/**
	 * Did finish processing.
	 * 
	 * @param changes
	 *            the changes
	 */
	public void didFinishProcessing(ArtifactChanges<T> changes);

	/**
	 * Gets the artifact type.
	 * 
	 * @return the artifact type
	 */
	public Set<Class<? extends T>> getArtifactTypes();

	/**
	 * Gets the save behavior.
	 * 
	 * @return the save behavior
	 */
	public SaveBehavior getSaveBehavior();

	/**
	 * Return artifacts to be processed.
	 * 
	 * @param changes
	 *            the changes
	 * @param context
	 *            the context
	 * @param toBeReturned
	 *            the to be returned
	 * @param currentContext
	 *            the current context
	 * @return the artifacts to be processed< t>
	 */
	public void selectArtifactsToBeProcessed(
			CurrentProcessorContext currentContext, ExecutionContext context,
			ArtifactChanges<T> changes,

			ArtifactsToBeProcessed<T> toBeReturned) throws Exception;

}
