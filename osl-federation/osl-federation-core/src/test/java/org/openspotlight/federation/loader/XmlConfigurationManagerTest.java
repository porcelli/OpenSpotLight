package org.openspotlight.federation.loader;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;

public class XmlConfigurationManagerTest {

    @Test
    public void shouldSaveAndLoadTheConfiguration() throws Exception {

        final GlobalSettings setting = new GlobalSettings();
        setting.setDefaultSleepingIntervalInMilliseconds(500);
        setting.setMaxResultListSize(50);
        setting.setNumberOfParallelThreads(50);

        final Repository repository = new Repository();
        repository.setActive(true);
        repository.setName("repo 1");

        final ConfigurationManager manager = XmlConfigurationManagerFactory.loadMutableFromFile("./target/XmlConfigurationManagerTest/newSavedConfiguration.xml");
        manager.saveGlobalSettings(setting);
        manager.saveRepository(repository);

        final ConfigurationManager manager1 = XmlConfigurationManagerFactory.loadMutableFromFile("./target/XmlConfigurationManagerTest/newSavedConfiguration.xml");
        final GlobalSettings sessing1 = manager1.getGlobalSettings();
        final Repository repo1 = manager1.getRepositoryByName("repo 1");
        assertThat(sessing1, is(notNullValue()));
        assertThat(repo1, is(repository));
    }

}
