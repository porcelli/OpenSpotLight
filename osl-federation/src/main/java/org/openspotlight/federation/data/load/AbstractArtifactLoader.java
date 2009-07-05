package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.PatternMatcher.filterNamesByPattern;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.PatternMatcher.FilterResult;
import org.openspotlight.federation.data.Artifact;
import org.openspotlight.federation.data.ArtifactMapping;
import org.openspotlight.federation.data.Bundle;

/**
 * The AbstractArtifactLoader class is itself a {@link ArtifactLoader} that do
 * the common stuff such as filtering artifacts before processing them or
 * creating the sha-1 key for the content.
 * 
 * @author feu
 * 
 */
public abstract class AbstractArtifactLoader implements ArtifactLoader {

	/**
	 * Filter the included and excluded patterns and also creates each artifact
	 * and calculates the sha-1 key for the content.
	 */
	public ArtifactProcessingCount loadArtifactsFromMappings(Bundle bundle)
			throws ConfigurationException {
		checkNotNull("bundle", bundle);
		int loadCount = 0;
		int errorCount = 0;
		Set<String> includedPatterns = new HashSet<String>();
		Set<String> excludedPatterns = new HashSet<String>();
		for (ArtifactMapping mapping : bundle.getArtifactMappings()) {
			if (mapping.getIncluded() != null)
				includedPatterns.add(mapping.getIncluded());
			if (mapping.getExcluded() != null)
				includedPatterns.add(mapping.getExcluded());
		}
		Set<String> namesToFilter = getAllArtifactNames(bundle);
		FilterResult result = filterNamesByPattern(namesToFilter,
				includedPatterns, excludedPatterns, false);
		Set<String> namesToProcess = result.getIncludedNames();
		for (String artifactName : namesToProcess) {
			try {
				byte[] content = loadArtifact(bundle, artifactName);
				String sha1 = getSha1SignatureEncodedAsBase64(content);
				InputStream is = new ByteArrayInputStream(content);
				Artifact artifact = bundle.addArtifact(artifactName);
				artifact.setData(is);
				artifact.setDataSha1(sha1);
				loadCount++;
			} catch (Exception e) {
				errorCount++;
			}
		}
		int ignoreCount = result.getIgnoredNames().size();
		return new ArtifactProcessingCount(loadCount, ignoreCount, errorCount);
	}

	/**
	 * The implementation class needs to load all the possible artifact names
	 * without filtering this.
	 * 
	 * @param bundle
	 * @return
	 * @throws ConfigurationException
	 */
	protected abstract Set<String> getAllArtifactNames(Bundle bundle)
			throws ConfigurationException;

	/**
	 * This method loads an artifact using its names
	 * 
	 * @param bundle
	 * @param artifactName
	 * @return
	 * @throws Exception
	 */
	protected abstract byte[] loadArtifact(Bundle bundle, String artifactName)
			throws Exception;
}
