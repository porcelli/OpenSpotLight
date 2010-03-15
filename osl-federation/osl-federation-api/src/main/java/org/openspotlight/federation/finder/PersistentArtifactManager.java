package org.openspotlight.federation.finder;

import java.util.Set;

import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.persist.support.SimplePersistSupport.InternalMethods;

/**
 * This class persists the artifacts loaded from {@link OriginArtifactLoader}
 * classes. So, this class unifies the {@link Artifact artifacts} loaded from
 * different sources into a single place.
 * 
 * @author feu
 * 
 */
public interface PersistentArtifactManager extends Disposable {

	/**
	 * 
	 * @return {@link InternalMethods} instance
	 */
	public PersistentArtifactInternalMethods getInternalMethods();

	/**
	 * This class have methods that should be used only on low level stuff.
	 * 
	 * @author feu
	 * 
	 */
	public interface PersistentArtifactInternalMethods {

		/**
		 * 
		 * @param <A>
		 * @param type
		 * @return true if the given type is supported
		 */
		public <A extends Artifact> boolean isTypeSupported(Class<A> type);

		/**
		 * This method returns the original names as it was before any mapping.
		 * 
		 * @param <A>
		 * @param source
		 * @param type
		 * @return
		 */
		public <A extends Artifact> Set<String> retrieveOriginalNames(
				ArtifactSource source, Class<A> type, String initialPath);

		/**
		 * This method returns the current names as it is.
		 * 
		 * @param <A>
		 * @param source
		 * @param type
		 * @return
		 */
		public <A extends Artifact> Set<String> retrieveNames(Class<A> type,
				String initialPath);

		/**
		 * This method finds an artifact by its name before any mapping.
		 * 
		 * @param <A>
		 * @param source
		 * @param type
		 * @param originName
		 * @return
		 */
		public <A extends Artifact> A findByOriginalName(ArtifactSource source,
				Class<A> type, String originName);

		/**
		 * 
		 * @param <A>
		 * @param source
		 * @param type
		 * @param originName
		 * @return
		 */
		public <A extends Artifact> Set<A> listByOriginalNames(
				ArtifactSource source, Class<A> type, String originName);

	}

	/**
	 * Adds an artifact to be saved later
	 * 
	 * @param <A>
	 * @param artifact
	 */
	public <A extends Artifact> void addTransient(A artifact);

	/**
	 * Marks an artifact to be removed on next save
	 * 
	 * @param <A>
	 * @param artifact
	 */
	public <A extends Artifact> void markAsRemoved(A artifact);

	/**
	 * find method
	 * 
	 * @param <A>
	 * @param type
	 * @param path
	 * @return
	 */
	public <A extends Artifact> A findByPath(Class<A> type, String path);

	/**
	 * list method
	 * 
	 * @param <A>
	 * @param type
	 * @param path
	 * @return
	 */
	public <A extends Artifact> Set<A> listByPath(Class<A> type, String path);

	/**
	 * Saves or flush transient data to the persistent store
	 */
	public void saveTransientData();

}
