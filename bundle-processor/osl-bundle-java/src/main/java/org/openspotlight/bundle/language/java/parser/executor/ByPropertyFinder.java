/**
 * 
 */
package org.openspotlight.bundle.language.java.parser.executor;

import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLInvalidQueryElementException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ByPropertyFinder {

	private final String completeArtifactName;

	private final SLGraphSession session;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final SLNode contextRootNode;

	public ByPropertyFinder(final String completeArtifactName,
			final SLGraphSession session, final SLNode contextRootNode) {
		super();
		this.completeArtifactName = completeArtifactName;
		this.session = session;
		this.contextRootNode = contextRootNode;
	}

	@SuppressWarnings("unchecked") <T extends SLNode> T findByProperty(final Class<T> type,
			final String propertyName, final String propertyValue)
			throws SLQueryException,
			SLInvalidQuerySyntaxException, SLInvalidQueryElementException {
		final SLQueryApi query1 = session.createQueryApi();
		query1.select().type(type.getName()).subTypes().selectEnd().where()
				.type(type.getName()).subTypes().each().property(
						propertyName).equalsTo().value(propertyValue)
				.typeEnd().whereEnd();
		final NeedsSyncronizationList<SLNode> result1 = query1.execute()
				.getNodes();
		if (result1.size() > 0) {
			synchronized (result1.getLockObject()) {
				for (final SLNode found : result1) {
					if (found.getContext().getRootNode().equals(
							contextRootNode)) {
						if (logger.isDebugEnabled()) {
							logger.debug(completeArtifactName + ": "
									+ "found on 1st try " + found.getName()
									+ " for search on type:"
									+ type.getSimpleName() + " with "
									+ propertyName + "=" + propertyValue);
						}
						return (T) found;
					}
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(completeArtifactName + ": "
					+ "not found any node for search on type:"
					+ type.getSimpleName() + " with " + propertyName + "="
					+ propertyValue);
		}
		return null;
	}

}