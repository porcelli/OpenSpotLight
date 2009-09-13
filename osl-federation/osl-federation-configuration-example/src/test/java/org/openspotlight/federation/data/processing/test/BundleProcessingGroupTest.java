package org.openspotlight.federation.data.processing.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.BundleProcessorType;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Group;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingStartAction;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphSession;

@SuppressWarnings("all")
public class BundleProcessingGroupTest {

	final int addedSize = 5;
	final int allArtifactsSize = this.addedSize + this.changedSize
			+ this.notChangedSize;
	final int changedSize = 2;
	final int excludedSize = 3;
	final int newArtifactsSize = this.addedSize + this.changedSize;
	final int notChangedSize = 4;

	private Repository createDirtyRepository() throws Exception {
		final Configuration configuration = new Configuration();
		configuration.setNumberOfParallelThreads(Integer.valueOf(4));
		final Repository repository = new Repository(configuration,
				"repository");
		repository.setActive(Boolean.TRUE);
		final Group project = new Group(repository, "project");
		project.setGraphRoot(Boolean.TRUE);
		project.setActive(Boolean.TRUE);
		final Bundle bundle = new Bundle(project, "bundle");
		bundle.setActive(Boolean.TRUE);
		new BundleProcessorType(
				bundle,
				"org.openspotlight.federation.data.processing.test.ArtifactCounterBundleProcessor")
				.setActive(Boolean.TRUE);
		new StreamArtifact(bundle, "notChangedAtAllArtifact1");
		new StreamArtifact(bundle, "notChangedAtAllArtifact2");
		new StreamArtifact(bundle, "notChangedAtAllArtifact3");
		new StreamArtifact(bundle, "notChangedAtAllArtifact4");
		final StreamArtifact excluded1 = new StreamArtifact(bundle, "excluded1");
		final StreamArtifact excluded2 = new StreamArtifact(bundle, "excluded2");
		final StreamArtifact excluded3 = new StreamArtifact(bundle, "excluded3");
		final StreamArtifact changedArtifact1 = new StreamArtifact(bundle,
				"changedArtifact1");
		final StreamArtifact changedArtifact2 = new StreamArtifact(bundle,
				"changedArtifact2");
		configuration.getInstanceMetadata().getSharedData().markAsSaved();
		new StreamArtifact(bundle, "included1");
		new StreamArtifact(bundle, "included2");
		new StreamArtifact(bundle, "included3");
		new StreamArtifact(bundle, "included4");
		new StreamArtifact(bundle, "included5");
		changedArtifact1.setDataSha1("changed!");
		changedArtifact2.setDataSha1("changed!");
		bundle.removeStreamArtifact(excluded1);
		bundle.removeStreamArtifact(excluded2);
		bundle.removeStreamArtifact(excluded3);

		return repository;
	}

	@Test
	public void shouldCreateCorrectBundleProcessingGroup() throws Exception {

		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor
				.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_EACH_ONE_NEW);
		final BundleProcessorManager manager = new BundleProcessorManager(graph);
		final Repository repository = this.createDirtyRepository();
		manager.processRepository(repository);
		final BundleProcessingGroup<StreamArtifact> lastGroup = ArtifactCounterBundleProcessor
				.getLastGroup();
		assertThat(lastGroup.getAddedArtifacts().size(), is(this.addedSize));
		assertThat(lastGroup.getExcludedArtifacts().size(),
				is(this.excludedSize));
		assertThat(lastGroup.getModifiedArtifacts().size(),
				is(this.changedSize));
		assertThat(lastGroup.getAllValidArtifacts().size(),
				is(this.allArtifactsSize));
	}

	@Test
	public void shouldProcessAllArtifacts() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor
				.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_ALL_AGAIN);
		final BundleProcessorManager manager = new BundleProcessorManager(graph);
		final Repository repository = this.createDirtyRepository();
		manager.processRepository(repository);
		final List<StreamArtifact> processed = ArtifactCounterBundleProcessor
				.getProcessedArtifacts();
		assertThat(processed.size(), is(this.allArtifactsSize));
	}

	@Test
	public void shouldProcessAnyArtifacts() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor
				.setDefaultProcessingStartAction(ProcessingStartAction.ALL_PROCESSING_ALREADY_DONE);
		final BundleProcessorManager manager = new BundleProcessorManager(graph);
		final Repository repository = this.createDirtyRepository();
		manager.processRepository(repository);
		final List<StreamArtifact> processed = ArtifactCounterBundleProcessor
				.getProcessedArtifacts();
		assertThat(processed.size(), is(0));
	}

	@Test
	public void shouldProcessAnyArtifactsOnIgnore() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor
				.setDefaultProcessingStartAction(ProcessingStartAction.IGNORE_ALL);
		final BundleProcessorManager manager = new BundleProcessorManager(graph);
		final Repository repository = this.createDirtyRepository();
		manager.processRepository(repository);
		final List<StreamArtifact> processed = ArtifactCounterBundleProcessor
				.getProcessedArtifacts();
		assertThat(processed.size(), is(0));

	}

	@Test
	public void shouldProcessChangedArtifacts() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor
				.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_EACH_ONE_NEW);
		final BundleProcessorManager manager = new BundleProcessorManager(graph);
		final Repository repository = this.createDirtyRepository();
		manager.processRepository(repository);
		final List<StreamArtifact> processed = ArtifactCounterBundleProcessor
				.getProcessedArtifacts();
		assertThat(processed.size(), is(this.newArtifactsSize));
	}

}
