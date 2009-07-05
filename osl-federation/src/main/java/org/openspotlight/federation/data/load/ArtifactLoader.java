package org.openspotlight.federation.data.load;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Compare.compareAll;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.Bundle;

/**
 * Artifact loader is the interface witch abstract the Artifact loading stuff,
 * such as reading a Artifact from its source and adding it to the configuration
 * meta data.
 * 
 * Each Artifact mapping has an syntax to match some bunch of Artifact using ant
 * style syntax with * and so on. So, the Artifact loader can decide if it
 * should load or not some type of Artifact, and based on mapping it should take
 * decisions if it knows any form to load the Artifacts that match the mapping
 * that it receives.
 * 
 * The Artifact loader has the responsibility to resolve each mapping.
 * 
 * @author feu
 * 
 */
public interface ArtifactLoader {

	/**
	 * Loads all the Artifacts contained from each ArtifactMapping.
	 * 
	 * @param bundle
	 */
	public ArtifactProcessingCount loadArtifactsFromMappings(Bundle bundle)
			throws ConfigurationException;

	/**
	 * Returns the count for each possible action during a Artifact load, such
	 * as load, ignore and error.
	 * 
	 * @author feu
	 * 
	 */
	public final class ArtifactProcessingCount implements
			Comparable<ArtifactProcessingCount> {

		public static final ArtifactProcessingCount ONE_LOADED = new ArtifactProcessingCount(
				1, 0, 0);
		public static final ArtifactProcessingCount ONE_IGNORED = new ArtifactProcessingCount(
				0, 1, 0);
		public static final ArtifactProcessingCount ONE_ERROR = new ArtifactProcessingCount(
				0, 0, 1);

		public ArtifactProcessingCount(long loadCount, long ignoreCount,
				long errorCount) {
			this.loadCount = loadCount;
			this.ignoreCount = ignoreCount;
			this.errorCount = errorCount;
			this.hashCode = hashOf(loadCount, ignoreCount, errorCount);
			this.description = format("loaded:{0} ignored:{1} error:{2}",
					loadCount, ignoreCount, errorCount);
		}

		private final long loadCount;
		private final long ignoreCount;
		private final long errorCount;
		private final int hashCode;
		private final String description;

		public long getLoadCount() {
			return loadCount;
		}

		public long getIgnoreCount() {
			return ignoreCount;
		}

		public long getErrorCount() {
			return errorCount;
		}

		public int compareTo(ArtifactProcessingCount that) {
			return compareAll(of(this.loadCount, this.ignoreCount,
					this.errorCount), andOf(that.loadCount, that.ignoreCount,
					that.errorCount));
		}

		@Override
		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof ArtifactProcessingCount))
				return false;
			ArtifactProcessingCount that = (ArtifactProcessingCount) o;
			return eachEquality(of(this.loadCount, this.ignoreCount,
					this.errorCount), andOf(that.loadCount, that.ignoreCount,
					that.errorCount));
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public String toString() {
			return description;
		}
	}

}
