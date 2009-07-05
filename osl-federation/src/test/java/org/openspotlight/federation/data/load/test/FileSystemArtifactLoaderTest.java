package org.openspotlight.federation.data.load.test;

import org.junit.Before;
import org.openspotlight.federation.data.ArtifactMapping;
import org.openspotlight.federation.data.Bundle;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.Project;
import org.openspotlight.federation.data.Repository;
import org.openspotlight.federation.data.load.FileSystemArtifactLoader;

/**
 * Test for class {@link FileSystemArtifactLoader}
 * 
 * @author feu
 * 
 */
public class FileSystemArtifactLoaderTest extends AbstractArtifactLoaderTest {

	@Before
	public void createArtifactLoader() {
		artifactLoader = new FileSystemArtifactLoader();
	}

	@Before
	public void createConfiguration() {
		this.configuration = new Configuration();
		Repository repository = new Repository("Current source files",
				configuration);
		Project project = new Project("current project", repository);
		Bundle bundle = new Bundle("java source", project);
		ArtifactMapping artifactMapping = new ArtifactMapping("All java files",
				bundle);
		bundle.setInitialLookup("src/main/java");
		artifactMapping.setIncluded("**/*.java");
	}

}
