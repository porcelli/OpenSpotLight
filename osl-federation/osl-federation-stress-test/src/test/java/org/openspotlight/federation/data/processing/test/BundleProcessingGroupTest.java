package org.openspotlight.federation.data.processing.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.impl.Artifact.Status;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.ConfigurationManagerProvider;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingStartAction;
import org.openspotlight.federation.data.util.ConfigurationNodes;
import org.openspotlight.federation.data.util.JcrConfigurationManagerProvider;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

@SuppressWarnings( "all" )
public class BundleProcessingGroupTest {

    final int addedSize        = 5;

    final int allArtifactsSize = this.addedSize + this.changedSize + this.notChangedSize;
    final int changedSize      = 2;
    final int excludedSize     = 3;
    final int newArtifactsSize = this.addedSize + this.changedSize;
    final int notChangedSize   = 4;

    @Before
    public void resetCounter() throws Exception {
        ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(null);
        ArtifactCounterBundleProcessor.getProcessedArtifacts().clear();
        ArtifactCounterBundleProcessor.setLastGroup(null);
    }

    private Repository setupTemporaryRepository() throws Exception {
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        final Configuration configuration = new Configuration();
        configuration.setNumberOfParallelThreads(Integer.valueOf(1));
        final Repository repository = new Repository(configuration, "repository");
        repository.setActive(Boolean.TRUE);
        final Group project = new Group(repository, "project");
        project.setGraphRoot(Boolean.TRUE);
        project.setActive(Boolean.TRUE);
        final Bundle bundle = new Bundle(project, "bundle");
        bundle.setActive(Boolean.TRUE);

        new BundleProcessorType(bundle, "org.openspotlight.federation.data.processing.test.ArtifactCounterBundleProcessor").setActive(Boolean.TRUE);

        new StreamArtifact(bundle, "notChangedAtAllArtifact1").setStatus(Status.ALREADY_PROCESSED);
        new StreamArtifact(bundle, "notChangedAtAllArtifact2").setStatus(Status.ALREADY_PROCESSED);
        new StreamArtifact(bundle, "notChangedAtAllArtifact3").setStatus(Status.ALREADY_PROCESSED);
        new StreamArtifact(bundle, "notChangedAtAllArtifact4").setStatus(Status.ALREADY_PROCESSED);

        new StreamArtifact(bundle, "excluded1").setStatus(Status.EXCLUDED);
        new StreamArtifact(bundle, "excluded2").setStatus(Status.EXCLUDED);
        new StreamArtifact(bundle, "excluded3").setStatus(Status.EXCLUDED);

        new StreamArtifact(bundle, "changedArtifact1").setStatus(Status.CHANGED);
        new StreamArtifact(bundle, "changedArtifact2").setStatus(Status.CHANGED);

        new StreamArtifact(bundle, "included1").setStatus(Status.INCLUDED);
        new StreamArtifact(bundle, "included2").setStatus(Status.INCLUDED);
        new StreamArtifact(bundle, "included3").setStatus(Status.INCLUDED);
        new StreamArtifact(bundle, "included4").setStatus(Status.INCLUDED);
        new StreamArtifact(bundle, "included5").setStatus(Status.INCLUDED);

        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(configuration, Bundle.class);
        boolean hasProcessed = false;
        boolean hasChanged = false;
        boolean hasExcluded = false;
        boolean hasIncluded = false;
        for (final Bundle b : bundles) {
            looping: for (final StreamArtifact sa : b.getStreamArtifacts()) {
                if (sa.getStatus() == null) {
                    continue looping;
                }
                switch (sa.getStatus()) {
                    case ALREADY_PROCESSED:
                        hasProcessed = true;
                        break;
                    case CHANGED:
                        hasChanged = true;
                        break;
                    case EXCLUDED:
                        hasExcluded = true;
                        break;
                    case INCLUDED:
                        hasIncluded = true;
                        break;
                }
            }
        }
        assertThat(hasProcessed, is(true));
        assertThat(hasChanged, is(true));
        assertThat(hasExcluded, is(true));
        assertThat(hasIncluded, is(true));

        return bundle.getRepository();

    }

    @Test
    public void shouldCreateCorrectBundleProcessingGroup() throws Exception {

        ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_EACH_ONE_NEW);
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        final Repository repository = this.setupTemporaryRepository();

        configurationManager.save(repository.getConfiguration());
        final Configuration newConfiguration = configurationManager.load(LazyType.LAZY);
        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(newConfiguration, Bundle.class);
        boolean hasProcessed = false;
        boolean hasChanged = false;
        boolean hasExcluded = false;
        boolean hasIncluded = false;
        for (final Bundle bundle : bundles) {
            looping: for (final StreamArtifact sa : bundle.getStreamArtifacts()) {
                if (sa.getStatus() == null) {
                    continue looping;
                }
                //                assertThat(sa.getStatus(), is(notNullValue()));
                switch (sa.getStatus()) {
                    case ALREADY_PROCESSED:
                        hasProcessed = true;
                        break;
                    case CHANGED:
                        hasChanged = true;
                        break;
                    case EXCLUDED:
                        hasExcluded = true;
                        break;
                    case INCLUDED:
                        hasIncluded = true;
                        break;
                }
            }
        }
        assertThat(hasProcessed, is(true));
        assertThat(hasIncluded, is(true));
        assertThat(hasChanged, is(true));
        assertThat(hasExcluded, is(true));

        manager.processBundles(bundles);
        final BundleProcessingGroup<StreamArtifact> lastGroup = ArtifactCounterBundleProcessor.getLastGroup();
        configurationManager.closeResources();
        assertThat(lastGroup.getAddedArtifacts().size(), is(this.addedSize));
        assertThat(lastGroup.getExcludedArtifacts().size(), is(this.excludedSize));
        assertThat(lastGroup.getModifiedArtifacts().size(), is(this.changedSize));
        assertThat(lastGroup.getAllValidArtifacts().size(), is(this.allArtifactsSize));

    }

    @Test
    public void shouldProcessAllArtifacts() throws Exception {

        ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_ALL_AGAIN);
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        final Repository repository = this.setupTemporaryRepository();
        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(repository, Bundle.class);
        configurationManager.save(repository.getConfiguration());
        configurationManager.closeResources();
        manager.processBundles(bundles);
        final List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();

        assertThat(processed.size(), is(this.allArtifactsSize));
    }

    @Test
    public void shouldProcessAnyArtifacts() throws Exception {

        ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.ALL_PROCESSING_ALREADY_DONE);
        assertThat(ArtifactCounterBundleProcessor.getDefaultProcessingStartAction(),
                   is(ProcessingStartAction.ALL_PROCESSING_ALREADY_DONE));
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        final Repository repository = this.setupTemporaryRepository();
        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(repository, Bundle.class);
        configurationManager.save(repository.getConfiguration());
        configurationManager.closeResources();

        manager.processBundles(bundles);
        final List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
        assertThat(processed.size(), is(0));
    }

    @Test
    public void shouldProcessAnyArtifactsOnIgnore() throws Exception {

        ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.IGNORE_ALL);
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        final Repository repository = this.setupTemporaryRepository();
        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(repository, Bundle.class);
        configurationManager.save(repository.getConfiguration());
        configurationManager.closeResources();

        manager.processBundles(bundles);
        final List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
        assertThat(processed.size(), is(0));
    }

    @Test
    public void shouldProcessChangedArtifacts() throws Exception {

        ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_EACH_ONE_NEW);
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

        final ConfigurationManagerProvider configurationManagerProvider = new JcrConfigurationManagerProvider(provider);
        final BundleProcessorManager manager = new BundleProcessorManager(provider, configurationManagerProvider);
        final ConfigurationManager configurationManager = configurationManagerProvider.getNewInstance();
        final Repository repository = this.setupTemporaryRepository();
        final Set<Bundle> bundles = ConfigurationNodes.findAllNodesOfType(repository, Bundle.class);
        configurationManager.save(repository.getConfiguration());
        configurationManager.closeResources();

        manager.processBundles(bundles);
        final List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
        assertThat(processed.size(), is(this.newArtifactsSize));
    }

}