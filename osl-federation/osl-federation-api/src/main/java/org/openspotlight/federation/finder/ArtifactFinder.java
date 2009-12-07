package org.openspotlight.federation.finder;

import java.util.Set;

import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.Artifact;

// TODO: Auto-generated Javadoc
/**
 * The Interface StreamArtifactFinder.
 */
public interface ArtifactFinder<A extends Artifact> extends Disposable {

	/**
	 * Find by path.
	 * 
	 * @param path
	 *            the path
	 * @param artifactSource
	 *            the artifact source
	 * @return the stream artifact
	 */
	public A findByPath(String path);

	/**
	 * Find by relative path.
	 * 
	 * @param relativeTo
	 *            the relative to
	 * @param path
	 *            the path
	 * @param artifactSource
	 *            the artifact source
	 * @return the stream artifact
	 */
	public A findByRelativePath(A relativeTo, String path);

	public String getCurrentRepository();

	/**
	 * List by path.
	 * 
	 * @param path
	 *            the path
	 * @param artifactSource
	 *            the artifact source
	 * @return the set< stream artifact>
	 */
	public Set<A> listByPath(String path);

	/**
	 * Retrieve all artifact names.
	 * 
	 * @param artifactSource
	 *            the artifact source
	 * @return the set< string>
	 */
	public Set<String> retrieveAllArtifactNames(String initialPath);

}
