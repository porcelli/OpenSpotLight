package org.openspotlight.federation.finder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.task.ExecutorInstance;

public abstract class AbstractPersistentArtifactManager implements
		PersistentArtifactManager {

	protected abstract boolean isMultithreaded();

	private final PersistentArtifactInternalMethods internalMethods = new PersistentArtifactInternalMethodsImpl();

	private final class PersistentArtifactInternalMethodsImpl implements
			PersistentArtifactInternalMethods {

		public <A extends Artifact> A findByOriginalName(ArtifactSource source,
				Class<A> type, String originName) {
			try {
				return internalFindByOriginalName(source, type, originName);
			} catch (Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

		public <A extends Artifact> boolean isTypeSupported(Class<A> type) {
			try {
				return internalIsTypeSupported(type);
			} catch (Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

		public <A extends Artifact> Set<A> listByOriginalNames(
				ArtifactSource source, Class<A> type, String originName) {
			try {
				return internalListByOriginalNames(source, type, originName);
			} catch (Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

		public <A extends Artifact> Set<String> retrieveOriginalNames(
				ArtifactSource source, Class<A> type, String initialPath) {
			try {
				return internalRetrieveOriginalNames(source, type, initialPath);
			} catch (Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

		public <A extends Artifact> Set<String> retrieveNames(Class<A> type,
				String initialPath) {
			try {
				return internalRetrieveNames(type, initialPath);
			} catch (Exception e) {
				throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
			}
		}

	}

	public <A extends Artifact> void addTransient(A artifact) {
		try {
			internalAddTransient(artifact);
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public <A extends Artifact> A findByPath(Class<A> type, String path) {
		try {
			return internalFindByPath(type, path);
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public PersistentArtifactInternalMethods getInternalMethods() {
		return internalMethods;
	}

	public <A extends Artifact> Set<A> listByPath(Class<A> type, String path) {
		try {
			return internalListByPath(type, path);
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public <A extends Artifact> void markAsRemoved(A artifact) {
		try {
			internalMarkAsRemoved(artifact);
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public void saveTransientData() {
		try {
			internalSaveTransientData();
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public void closeResources() {
		try {
			internalCloseResources();
		} catch (Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	protected abstract <A extends Artifact> A internalFindByOriginalName(
			ArtifactSource source, Class<A> type, String originName)
			throws Exception;

	protected abstract <A extends Artifact> boolean internalIsTypeSupported(
			Class<A> type) throws Exception;

	protected abstract <A extends Artifact> Set<String> internalRetrieveOriginalNames(
			ArtifactSource source, Class<A> type, String initialPath)
			throws Exception;

	protected abstract <A extends Artifact> Set<String> internalRetrieveNames(
			Class<A> type, String initialPath) throws Exception;

	protected abstract <A extends Artifact> void internalAddTransient(A artifact)
			throws Exception;

	protected abstract <A extends Artifact> A internalFindByPath(Class<A> type,
			String path) throws Exception;

	protected abstract <A extends Artifact> void internalMarkAsRemoved(
			A artifact) throws Exception;

	protected abstract void internalSaveTransientData() throws Exception;

	protected abstract void internalCloseResources() throws Exception;

	protected final <A extends Artifact> Set<A> internalListByOriginalNames(
			final ArtifactSource source, final Class<A> type, String initialPath)
			throws Exception {
		Set<String> paths = getInternalMethods().retrieveOriginalNames(source,
				type, initialPath);
		Set<A> result = new HashSet<A>();
		if (isMultithreaded()) {
			List<Callable<A>> tasks = new ArrayList<Callable<A>>();
			for (final String path : paths) {
				Callable<A> callable = new Callable<A>() {
					public A call() throws Exception {
						return internalFindByOriginalName(source, type, path);
					}
				};
				tasks.add(callable);
			}
			List<Future<A>> futures = ExecutorInstance.INSTANCE
					.getExecutorInstance().invokeAll(tasks);
			for (Future<A> f : futures)
				result.add(f.get());
		} else {
			for (final String path : paths) {
				result.add(internalFindByOriginalName(source, type, path));
			}
		}
		return result;
	}

	protected final <A extends Artifact> Set<A> internalListByPath(
			final Class<A> type, String initialPath) throws Exception {
		Set<String> paths = getInternalMethods().retrieveNames(type,
				initialPath);
		Set<A> result = new HashSet<A>();
		if (isMultithreaded()) {
			List<Callable<A>> tasks = new ArrayList<Callable<A>>();
			for (final String path : paths) {
				Callable<A> callable = new Callable<A>() {
					public A call() throws Exception {
						return internalFindByPath(type, path);
					}
				};
				tasks.add(callable);
			}
			List<Future<A>> futures = ExecutorInstance.INSTANCE
					.getExecutorInstance().invokeAll(tasks);
			for (Future<A> f : futures)
				result.add(f.get());
		} else {
			for (final String path : paths) {
				result.add(internalFindByPath(type, path));
			}
		}
		return result;
	}

}
