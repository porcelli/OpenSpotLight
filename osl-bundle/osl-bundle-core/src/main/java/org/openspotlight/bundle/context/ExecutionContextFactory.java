package org.openspotlight.bundle.context;

import org.openspotlight.bundle.annotation.ArtifactLoaderRegistry;
import org.openspotlight.common.Disposable;
import org.openspotlight.federation.finder.OriginArtifactLoader;
import org.openspotlight.federation.finder.PersistentArtifactManagerProvider;
import org.openspotlight.federation.loader.ConfigurationManager;
import org.openspotlight.graph.GraphSessionFactory;
import org.openspotlight.guice.ThreadLocalProvider;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StorageSession;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class ExecutionContextFactory extends
		ThreadLocalProvider<ExecutionContext> {

	@Inject
	public ExecutionContextFactory(
			Provider<StorageSession> sessionProvider,
			GraphSessionFactory graphSessionFactory,
			SimplePersistFactory simplePersistFactory,
			PersistentArtifactManagerProvider persistentArtifactManagerProvider,
			ConfigurationManager configurationManager,
			RepositoryPath repositoryPath,
			@ArtifactLoaderRegistry Iterable<Class<? extends OriginArtifactLoader>> loaderRegistry) {
		this.sessionProvider = sessionProvider;
		this.graphSessionFactory = graphSessionFactory;
		this.simplePersistFactory = simplePersistFactory;
		this.persistentArtifactManagerProvider = persistentArtifactManagerProvider;
		this.configurationManager = configurationManager;
		this.repositoryPath = repositoryPath;
		this.loaderRegistry = loaderRegistry;
	}

	private final Iterable<Class<? extends OriginArtifactLoader>> loaderRegistry;

	private final Provider<StorageSession> sessionProvider;

	private final GraphSessionFactory graphSessionFactory;

	private final SimplePersistFactory simplePersistFactory;

	private final PersistentArtifactManagerProvider persistentArtifactManagerProvider;

	private final ConfigurationManager configurationManager;

	private final RepositoryPath repositoryPath;

	public static void closeResourcesIfNeeded(Object o) {
		if (o instanceof Disposable) {
			((Disposable) o).closeResources();
		}
	}

	@Override
	public void closeResources() {
		closeResourcesIfNeeded(sessionProvider);
		closeResourcesIfNeeded(graphSessionFactory);
		closeResourcesIfNeeded(simplePersistFactory);
		closeResourcesIfNeeded(persistentArtifactManagerProvider);
		closeResourcesIfNeeded(configurationManager);

	}

	@Override
	protected ExecutionContext createInstance() {
		return new DefaultExecutionContext(sessionProvider,
				graphSessionFactory, simplePersistFactory,
				persistentArtifactManagerProvider, configurationManager,
				repositoryPath, loaderRegistry);
	}

}
