package org.openspotlight.federation.finder;

import org.openspotlight.federation.domain.Repository;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;

public class JcrPersistentArtifactManagerProvider extends
		PersistentAbstractManagerProvider {

	private static class JcrPersistentArtifactManagerItemFactory implements
			ItemFactory<PersistentArtifactManager> {
		private final Repository repository;
		private final JcrConnectionProvider provider;

		public JcrPersistentArtifactManagerItemFactory(
				JcrConnectionDescriptor descriptor, Repository repository) {
			this.provider = JcrConnectionProvider.createFromData(descriptor);
			this.repository = repository;
		}

		public PersistentArtifactManager createNew() {
			SessionWithLock session = provider.openSession();
			JcrPersistenArtifactManager manager = new JcrPersistenArtifactManager(
					session, repository);
			return manager;
		}

		public boolean useOnePerThread() {
			return true;
		}

	}

	public JcrPersistentArtifactManagerProvider(
			JcrConnectionDescriptor descriptor, Repository repository) {
		super(new JcrPersistentArtifactManagerItemFactory(descriptor,
				repository));
	}

}
