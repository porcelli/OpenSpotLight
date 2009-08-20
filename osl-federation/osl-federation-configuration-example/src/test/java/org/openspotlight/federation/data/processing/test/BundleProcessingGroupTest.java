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
import org.openspotlight.federation.data.impl.Project;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.impl.StreamArtifact;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.openspotlight.federation.data.processing.BundleProcessor.BundleProcessingGroup;
import org.openspotlight.federation.data.processing.BundleProcessor.ProcessingStartAction;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphSession;

@SuppressWarnings("all")
public class BundleProcessingGroupTest {

	private Repository createDirtyRepository() throws Exception {
		Configuration configuration = new Configuration();
		configuration.setNumberOfParallelThreads(Integer.valueOf(4));
		Repository repository = new Repository(configuration, "repository");
		repository.setActive(Boolean.TRUE);
		Project project = new Project(repository, "project");
		project.setActive(Boolean.TRUE);
		Bundle bundle = new Bundle(project, "bundle");
		bundle.setActive(Boolean.TRUE);
		new BundleProcessorType(bundle,"org.openspotlight.federation.data.processing.test.ArtifactCounterBundleProcessor").setActive(Boolean.TRUE);
		new StreamArtifact(bundle, "notChangedAtAllArtifact1");
		new StreamArtifact(bundle, "notChangedAtAllArtifact2");
		new StreamArtifact(bundle, "notChangedAtAllArtifact3");
		new StreamArtifact(bundle, "notChangedAtAllArtifact4");
		StreamArtifact excluded1 = new StreamArtifact(bundle, "excluded1");
		StreamArtifact excluded2 = new StreamArtifact(bundle, "excluded2");
		StreamArtifact excluded3 = new StreamArtifact(bundle, "excluded3");
		StreamArtifact changedArtifact1 = new StreamArtifact(bundle,
				"changedArtifact1");
		StreamArtifact changedArtifact2 = new StreamArtifact(bundle,
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
	final int addedSize = 5;
	final int excludedSize = 3;
	final int changedSize = 2;
	final int notChangedSize = 4;
	final int allArtifactsSize = addedSize+changedSize+notChangedSize;
	final int newArtifactsSize = addedSize+changedSize;
	@Test
	public void shouldCreateCorrectBundleProcessingGroup() throws Exception {

		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_EACH_ONE_NEW);
		BundleProcessorManager manager = new BundleProcessorManager(graph);
		Repository repository = createDirtyRepository();
		manager.processRepository(repository);
		BundleProcessingGroup<StreamArtifact> lastGroup = ArtifactCounterBundleProcessor.getLastGroup();
		assertThat(lastGroup.getAddedArtifacts().size(), is(addedSize));
		assertThat(lastGroup.getExcludedArtifacts().size(), is(excludedSize));
		assertThat(lastGroup.getModifiedArtifacts().size(), is(changedSize));
		assertThat(lastGroup.getAllValidArtifacts().size(), is(allArtifactsSize));
	}

	@Test
	public void shouldProcessAllArtifacts() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_ALL_AGAIN);
		BundleProcessorManager manager = new BundleProcessorManager(graph);
		Repository repository = createDirtyRepository();
		manager.processRepository(repository);
		List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
		assertThat(processed.size(), is(allArtifactsSize));
	}

	@Test
	public void shouldProcessChangedArtifacts() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.PROCESS_EACH_ONE_NEW);
		BundleProcessorManager manager = new BundleProcessorManager(graph);
		Repository repository = createDirtyRepository();
		manager.processRepository(repository);
		List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
		assertThat(processed.size(), is(newArtifactsSize));
	}

	@Test
	public void shouldProcessAnyArtifacts() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.ALL_PROCESSING_ALREADY_DONE);
		BundleProcessorManager manager = new BundleProcessorManager(graph);
		Repository repository = createDirtyRepository();
		manager.processRepository(repository);
		List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
		assertThat(processed.size(), is(0));
	}
	
	@Test
	public void shouldProcessAnyArtifactsOnIgnore() throws Exception {
		final SLGraph graph = mock(SLGraph.class);
		final SLGraphSession session = mock(SLGraphSession.class);
		when(graph.openSession()).thenReturn(session);
		ArtifactCounterBundleProcessor.setDefaultProcessingStartAction(ProcessingStartAction.IGNORE_ALL);
		BundleProcessorManager manager = new BundleProcessorManager(graph);
		Repository repository = createDirtyRepository();
		manager.processRepository(repository);
		List<StreamArtifact> processed = ArtifactCounterBundleProcessor.getProcessedArtifacts();
		assertThat(processed.size(), is(0));
		
	}
	
}
