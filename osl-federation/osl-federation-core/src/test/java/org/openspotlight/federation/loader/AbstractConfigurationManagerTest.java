package org.openspotlight.federation.loader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ArtifactSourceMapping;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.BundleSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;

/**
 * The Class XmlConfigurationManagerTest.
 */
public abstract class AbstractConfigurationManagerTest {

    protected abstract ConfigurationManager createNewConfigurationManager();

    /**
     * Should save and load the configuration.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldSaveAndLoadTheConfiguration() throws Exception {
        //FIXME Create a detailed and real configuration to be used as an example
        final GlobalSettings setting = new GlobalSettings();
        setting.setDefaultSleepingIntervalInMilliseconds(500);
        setting.setMaxResultListSize(50);
        setting.setNumberOfParallelThreads(50);

        final Repository repository = new Repository();
        repository.setActive(true);
        repository.setName("repository name");

        final ArtifactSource artifactSource = new ArtifactSource();
        artifactSource.setActive(true);
        artifactSource.setName("artifactSource name");
        artifactSource.setRepository(repository);
        artifactSource.setInitialLookup("/usr/src/files");
        artifactSource.setRepository(repository);
        repository.getArtifactSources().add(artifactSource);

        artifactSource.getCronInformation().add("* * 16 * - ! #");
        artifactSource.getCronInformation().add("* 1 * - ! #");
        artifactSource.getCronInformation().add("* * XD * - ! #");

        final ArtifactSourceMapping mapping = new ArtifactSourceMapping();
        mapping.setSource(artifactSource);
        mapping.setFrom("/first-dir/");
        mapping.setTo("/virtual-dir/java");
        mapping.getExcludeds().add("**/*.class");
        mapping.getIncludeds().add("**/*.java");
        mapping.getIncludeds().add("**/*.properties");
        mapping.getIncludeds().add("**/*.xml");
        artifactSource.getMappings().add(mapping);

        final Group group = new Group();
        group.setActive(true);
        group.setName("group name");
        group.setRepository(repository);
        group.setType("group type");
        repository.getGroups().add(group);

        group.getCronInformation().add("* * 16 * - ! #");
        group.getCronInformation().add("* 1 * - ! #");
        group.getCronInformation().add("* * XD * - ! #");

        final BundleProcessorType newBundle = new BundleProcessorType();
        newBundle.setActive(true);
        newBundle.setGroup(group);
        newBundle.setType(ExampleBundleProcessor.class);
        group.getBundleTypes().add(newBundle);

        final BundleSource bundleSource = new BundleSource();
        bundleSource.setBundleProcessorType(newBundle);
        bundleSource.setRelative("/virtual-dir/java");
        bundleSource.getIncludeds().add("**/*.java");
        bundleSource.getIncludeds().add("**/*.xml");
        newBundle.getSources().add(bundleSource);

        final ConfigurationManager manager = this.createNewConfigurationManager();
        manager.saveGlobalSettings(setting);
        manager.saveRepository(repository);

        final ConfigurationManager manager1 = this.createNewConfigurationManager();
        final GlobalSettings sessing1 = manager1.getGlobalSettings();
        final Repository repo1 = manager1.getRepositoryByName("repository name");
        assertThat(sessing1, is(notNullValue()));
        assertThat(repo1, is(repository));
    }

}
