package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Assertions.checkEachParameterNotNull;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.Bundle;

/**
 * The ArtifactLoaderGroup class is itself a {@link ArtifactLoader} that groups
 * all the valid Artifact loaders and execute all of that in order.
 * 
 * @author feu
 * 
 */
public class ArtifactLoaderGroup implements ArtifactLoader {

	private final ArtifactLoader[] artifactLoaders;

	/**
	 * Constructor with varargs for mandatory Artifact loaders.
	 * 
	 * @param artifactLoaders
	 */
	public ArtifactLoaderGroup(ArtifactLoader... artifactLoaders) {
		checkEachParameterNotNull("artifactLoaders", artifactLoaders);
		this.artifactLoaders = artifactLoaders;
	}

	/**
	 * Executes each Artifact loader in order on the passed bundle.
	 */
	public ArtifactProcessingCount loadArtifactsFromMappings(Bundle bundle)
			throws ConfigurationException {
		checkNotNull("bundle", bundle);
		long loadCount = 0;
		long ignoreCount = 0;
		long errorCount = 0;
		for (ArtifactLoader ArtifactLoader : artifactLoaders) {
			ArtifactProcessingCount result = ArtifactLoader
					.loadArtifactsFromMappings(bundle);
			checkNotNull("result", result);
			loadCount += result.getLoadCount();
			ignoreCount += result.getIgnoreCount();
			errorCount += result.getErrorCount();
		}
		return new ArtifactProcessingCount(loadCount, ignoreCount, errorCount);
	}
}
