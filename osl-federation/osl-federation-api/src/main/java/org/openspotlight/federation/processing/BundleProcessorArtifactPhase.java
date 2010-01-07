package org.openspotlight.federation.processing;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;

public interface BundleProcessorArtifactPhase<T extends Artifact> {

	/**
	 * Before process artifact.
	 * 
	 * @param artifact
	 *            the artifact
	 */
	public void beforeProcessArtifact(T artifact);

	/**
	 * After process artifact.
	 * 
	 * @param artifact
	 *            the artifact
	 * @param status
	 *            the status
	 */
	public void didFinishToProcessArtifact(T artifact, LastProcessStatus status);

	public Class<T> getArtifactType();

	/**
	 * Process artifact.
	 * 
	 * @param artifact
	 *            the artifact
	 * @param context
	 *            the context
	 * @param currentContext
	 *            the current context
	 * @return the last process status
	 * @throws Exception
	 *             the exception
	 */
	public LastProcessStatus processArtifact(T artifact,
			CurrentProcessorContext currentContext, ExecutionContext context)
			throws Exception;

}
