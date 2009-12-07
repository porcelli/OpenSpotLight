package org.openspotlight.federation.finder;

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.PathElement;

/**
 * The Class AbstractArtifactFinder.
 */
/**
 * @author feu
 * @param <A>
 */
public abstract class AbstractArtifactFinder<A extends Artifact> implements
		ArtifactFinder<A> {

	public A findByRelativePath(final A relativeTo, final String path) {
		final String newPath = PathElement.createRelativePath(
				relativeTo.getParent(), path).getCompletePath();

		return findByPath(newPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.federation.finder.ArtifactFinder#listByPath(org.
	 * openspotlight.federation.domain.ArtifactSource, java.lang.String)
	 */
	public Set<A> listByPath(final String rawPath) {
		try {
			final Set<A> result = new HashSet<A>();
			final Set<String> allFilePaths = retrieveAllArtifactNames(rawPath);
			for (final String path : allFilePaths) {
				final A sa = findByPath(path);
				result.add(sa);
			}
			return result;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

}
