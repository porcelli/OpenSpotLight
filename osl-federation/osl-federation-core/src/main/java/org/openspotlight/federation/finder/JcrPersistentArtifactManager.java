package org.openspotlight.federation.finder;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.persist.support.SimplePersistSupport;

import javax.jcr.*;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

public class JcrPersistentArtifactManager extends
		AbstractPersistentArtifactManager {

	private static String ROOT_PATH = SharedConstants.DEFAULT_JCR_ROOT_NAME
			+ "/{0}/artifacts";

	private static String getArtifactRootPathFor(final Repository repository) {
		return MessageFormat.format(ROOT_PATH, repository.getName());
	}

	private final String repositoryName;

	private final String rootPath;

	private final SessionWithLock session;

	public JcrPersistentArtifactManager(SessionWithLock session,
			Repository repository) {
		this.session = session;
		this.rootPath = getArtifactRootPathFor(repository);
		this.repositoryName = repository.getName();
	}

	@Override
	protected <A extends Artifact> void internalAddTransient(A artifact)
			throws Exception {
		artifact.setRepositoryName(repositoryName);
		SimplePersistSupport.convertBeanToJcr(this.rootPath, this.session,
				artifact);
	}

	@Override
	protected void internalCloseResources() throws Exception {
		if (session.isLive())
			session.logout();
	}

	@Override
	protected <A extends Artifact> A internalFindByOriginalName(
			ArtifactSource source, Class<A> type, String originName)
			throws Exception {
		return internalFind(type, createOriginName(source, originName),
				PROPERTY_NAME_OLD_ARTIFACT_PATH);
	}

	private String createOriginName(ArtifactSource source, String originName) {
		return source.getName() + ":" + (originName != null ? originName : "");
	}

	@Override
	protected <A extends Artifact> A internalFindByPath(Class<A> type,
			String path) throws Exception {
		return internalFind(type, path, PROPERTY_NAME_ARTIFACT_PATH);
	}

	private <A> A internalFind(Class<A> type, String path, String propertyName)
			throws Exception {
		final Set<A> found = SimplePersistSupport.findNodesByProperties(
				this.rootPath, this.session, type, LazyType.DO_NOT_LOAD,
				new String[] { propertyName }, new Object[] { path });
		if (found.size() > 1) {
			throw new Exception("returned more than one result");
		}
		if (found.size() == 0) {
			return null;
		}
		return found.iterator().next();
	}

	@Override
	protected <A extends Artifact> boolean internalIsTypeSupported(Class<A> type)
			throws Exception {
		return true;
	}

	@Override
	protected <A extends Artifact> void internalMarkAsRemoved(A artifact)
			throws Exception {
		final Node node = SimplePersistSupport.convertBeanToJcr(this.rootPath,
				this.session, artifact);
		node.remove();
	}

	@Override
	protected <A extends Artifact> Set<String> internalRetrieveOriginalNames(
			ArtifactSource source, Class<A> type, String initialPath)
			throws Exception {
		return privateRetrieveNames(type,
				createOriginName(source, initialPath),
				PROPERTY_NAME_OLD_ARTIFACT_PATH);
	}

	@Override
	protected void internalSaveTransientData() throws Exception {
		session.save();
	}

	@Override
	protected <A extends Artifact> Set<String> internalRetrieveNames(
			Class<A> type, String initialPath) throws Exception {
		return privateRetrieveNames(type, initialPath,
				PROPERTY_NAME_ARTIFACT_PATH);

	}

	public static final String PROPERTY_NAME_ARTIFACT_PATH = "artifactCompleteName";

	public static final String PROPERTY_NAME_OLD_ARTIFACT_PATH = "originalName";

	private <A> Set<String> privateRetrieveNames(Class<A> type,
			String initialPath, String propertyName)
			throws InvalidQueryException, RepositoryException,
			ValueFormatException, PathNotFoundException {
		final String jcrPropertyName = MessageFormat.format(
				SimplePersistSupport.PROPERTY_VALUE, propertyName);
		final String nodeName = SimplePersistSupport.getJcrNodeName(type);
		final String xpath;
		if (initialPath != null) {
			xpath = MessageFormat.format("{0}//{1}[jcr:like(@{2},''%{3}%'')]",
					this.rootPath, nodeName, jcrPropertyName, initialPath);
		} else {
			xpath = MessageFormat.format("{0}//{1}", this.rootPath, nodeName);
		}

		final Query query = this.session.getWorkspace().getQueryManager()
				.createQuery(xpath, Query.XPATH);
		final QueryResult result = query.execute();
		final NodeIterator nodes = result.getNodes();
		final Set<String> names = new HashSet<String>();
		while (nodes.hasNext()) {
			final Node nextNode = nodes.nextNode();
			if (nextNode.hasProperty(jcrPropertyName)) {
				final String propVal = nextNode.getProperty(jcrPropertyName)
						.getValue().getString();
				names.add(propVal);
			}
		}
		return names;
	}

	@Override
	protected boolean isMultithreaded() {
		return false;
	}

	public Object getPersistentEngine() {
		return session;
	}

}
