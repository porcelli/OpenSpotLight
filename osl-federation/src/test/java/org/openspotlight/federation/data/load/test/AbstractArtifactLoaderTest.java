package org.openspotlight.federation.data.load.test;

import static org.openspotlight.common.util.Collections.setOf;

import java.util.Set;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.Bundle;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.Project;
import org.openspotlight.federation.data.Repository;
import org.openspotlight.federation.data.load.AbstractArtifactLoader;
import org.openspotlight.federation.data.load.ArtifactLoader;
import org.openspotlight.federation.data.test.AbstractNodeTest;

/**
 * Test for class {@link AbstractArtifactLoader}
 * 
 * @author feu
 * 
 */
public class AbstractArtifactLoaderTest extends AbstractNodeTest {

	protected ArtifactLoader artifactLoader;

	protected Configuration configuration;

	@Before
	public void createArtifactLoader() {
		artifactLoader = new AbstractArtifactLoader() {

			@Override
			protected Set<String> getAllArtifactNames(Bundle bundle)
					throws ConfigurationException {
				return setOf("1", "2", "3", "4", "5");
			}

			@Override
			protected byte[] loadArtifact(Bundle bundle, String artifactName)
					throws Exception {
				return artifactName.getBytes();
			}

		};
	}

	@Before
	public void createConfiguration() {
		this.configuration = createSampleData();
	}

	@Test
	public void shouldLoadArtifacts() throws Exception {
		for (Repository repository : configuration.getRepositories()) {
			for (Project project : repository.getProjects()) {
				for (Bundle bundle : project.getBundles()) {
					artifactLoader.loadArtifactsFromMappings(bundle);
					assertThat(bundle.getArtifacts().size(), is(not(0)));
				}
			}
		}
	}

}
