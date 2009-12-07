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

	private final String currentRepository;

	protected AbstractArtifactFinder(final String currentRepository) {
		this.currentRepository = currentRepository;
	}

	private void didLoadArtifact(final A artifact) {
		if (artifact != null) {
			artifact.setRepositoryName(currentRepository);
		}
	}

	private void didLoadArtifacts(final Iterable<A> artifacts) {
		if (artifacts != null) {
			for (final A artifact : artifacts) {
				if (artifact != null) {
					artifact.setRepositoryName(currentRepository);
				}
			}
		}
	}

	public final A findByPath(final String path) {
		final A result = internalFindByPath(path);
		didLoadArtifact(result);
		return result;
	}

	public final A findByRelativePath(final A relativeTo, final String path) {
		final A result = internalFindByRelativePath(relativeTo, path);
		didLoadArtifact(result);
		return result;
	}

	public final String getCurrentRepository() {
		return currentRepository;
	}

	protected abstract A internalFindByPath(String path);

	protected A internalFindByRelativePath(final A relativeTo, final String path) {
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
	protected Set<A> internalListByPath(final String rawPath) {
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

	protected abstract Set<String> internalRetrieveAllArtifactNames(
			String initialPath);

	public final Set<A> listByPath(final String path) {
		final Set<A> result = internalListByPath(path);
		didLoadArtifacts(result);
		return result;
	}

	public final Set<String> retrieveAllArtifactNames(final String initialPath) {
		return internalRetrieveAllArtifactNames(initialPath);
	}

}
