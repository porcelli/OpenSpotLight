package org.openspotlight.graph.persistence;

import java.util.ArrayList;
import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

public class SLPersistentQueryImpl implements SLPersistentQuery {
	
	private SLPersistentTreeSession treeSession;
	private Session session;
	private String statement;
	private int type;
	
	public SLPersistentQueryImpl(SLPersistentTreeSession treeSession, Session session, String statement, int type) {
		this.treeSession = treeSession;
		this.session = session;
		this.statement = statement;
		this.type = type;
	}

	//@Override
	public String getStatement() {
		return statement;
	}

	//@Override
	public int getType() {
		return type;
	}
	
	//@Override
	public SLPersistentQueryResult execute() throws SLPersistentTreeSessionException {
		try {
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery(statement , Query.XPATH);
			QueryResult result = query.execute();
			NodeIterator iter = result.getNodes();

			Collection<SLPersistentNode> persistentNodes = new ArrayList<SLPersistentNode>();
			
			while (iter.hasNext()) {
				Node node = iter.nextNode();
				String[] names = node.getPath().split("/");
				SLPersistentNode persistentNode = null;
				for (int i = 0; i < names.length; i++) {
					if (names[i].trim().equals("")) continue;
					if (persistentNode == null && names[i].equals("osl")) {
						persistentNode = treeSession.getRootNode();
					}
					else {
						persistentNode = persistentNode.getNode(names[i]);
					}
				}
				persistentNodes.add(persistentNode);
			}
			
			return new SLPersistentQueryResultImpl(persistentNodes);
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to execute query.", e);
		}
	}
}


