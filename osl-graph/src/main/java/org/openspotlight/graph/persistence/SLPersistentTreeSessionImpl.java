package org.openspotlight.graph.persistence;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import org.apache.log4j.Logger;
import org.openspotlight.graph.util.JCRUtil;

public class SLPersistentTreeSessionImpl implements SLPersistentTreeSession {
	
	static final Logger LOGGER = Logger.getLogger(SLPersistentTreeSessionImpl.class);
	
	private Session jcrSession;
	private SLPersistentEventPoster eventPoster;
	private Node rootNode;

	public SLPersistentTreeSessionImpl(Session session) {
		this.jcrSession = session;
		SLPersistentEventListener listener = new SLPersistentEventListenerImpl();
		this.eventPoster = new SLPersistentEventPosterImpl(listener);
	}
	
	//@Override
	public SLPersistentNode getRootNode() throws SLPersistentTreeSessionException {
		if (rootNode == null) {
			try {
				rootNode = JCRUtil.getChildNode(jcrSession.getRootNode(), "osl");
				if (rootNode == null) {
					rootNode = jcrSession.getRootNode().addNode("osl");
					JCRUtil.makeVersionable(rootNode);
					JCRUtil.makeReferenceable(rootNode);
					jcrSession.save();
				}
				else {
					SortedSet<Double> versionNumbers = new TreeSet<Double>();
					VersionIterator iter = rootNode.getVersionHistory().getAllVersions();
					while (iter.hasNext()) {
						Version version = iter.nextVersion();
						if (!version.getName().equals("jcr:rootVersion")) {
							versionNumbers.add(new Double(version.getName()));
						}
					}
					if (versionNumbers.isEmpty()) {
						rootNode.remove();
						jcrSession.getRootNode().addNode("osl");
					}
					else {
						rootNode.restore(versionNumbers.last().toString(), true);
					}
				}
				rootNode.checkout();
			}
			catch (RepositoryException e) {
				throw new SLPersistentTreeSessionException("Couldn't create persistent root node.", e);
			}
		}
		return new SLPersistentNodeImpl(this, null, rootNode, eventPoster);		
	}

	//@Override
	public void close() {
		jcrSession.logout();
	}

	//@Override
	public void save() throws SLPersistentTreeSessionException {
		try {
			rootNode.save();
			rootNode.checkin();
			jcrSession.logout();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to save persistent session.", e);
		}
	}

	//@Override
	public void clear() throws SLPersistentTreeSessionException {
		if (rootNode != null) {
			try {
				NodeIterator iter = rootNode.getNodes();
				while (iter.hasNext()) {
					Node node = iter.nextNode();
					node.remove();
				}
			}
			catch (RepositoryException e) {
				throw new SLPersistentTreeSessionException("Error on attempt to clear the persistent tree session.", e);
			}
		}
	}

	//@Override
	public SLPersistentQuery createQuery(String statement, int type) throws SLPersistentTreeSessionException {
		return new SLPersistentQueryImpl(this, jcrSession, statement, type);
	}

	//@Override
	public SLPersistentNode getNodeByID(String id) throws SLPersistentNodeNotFoundException, SLPersistentTreeSessionException {
		try {
			SLPersistentNode persistentNode = null;
			Node jcrNode = jcrSession.getNodeByUUID(id);
			String[] names = jcrNode.getPath().substring(1).split("/");
			for (int i = 0; i < names.length; i++) {
				if (persistentNode == null) {
					persistentNode = getRootNode();
				}
				else {
					persistentNode = persistentNode.getNode(names[i]);
				}
			}
			return persistentNode;
		}
		catch (ItemNotFoundException e) {
			throw new SLPersistentNodeNotFoundException(id, e);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent node by id.", e);
		}
	}

	//@Override
	public SLPersistentNode getNodeByPath(String path) throws SLPersistentTreeSessionException {
		SLPersistentQuery query = createQuery(path, SLPersistentQuery.TYPE_XPATH);
		SLPersistentQueryResult result = query.execute();
		return result.getRowCount() == 1 ? result.getNodes().iterator().next() : null;
	}
}



