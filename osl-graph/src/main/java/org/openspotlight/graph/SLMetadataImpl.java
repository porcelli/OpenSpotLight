package org.openspotlight.graph;

import java.util.ArrayList;
import java.util.Collection;

import org.openspotlight.SLException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;

public class SLMetadataImpl implements SLMetadata {
	
	private SLPersistentTreeSession treeSession;
	
	public SLMetadataImpl(SLPersistentTreeSession treeSession) {
		this.treeSession = treeSession;
	}

	@Override
	public SLMetaNode getMetaNode(Class<? extends SLNode> nodeClass) throws SLGraphSessionException {
		try {
			SLPersistentQuery query = treeSession.createQuery("//osl/metadata/types/" + nodeClass.getName(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			SLMetaNode metaNode = null;
			if (result.getRowCount() == 1) {
				SLPersistentNode pMetaNode = result.getNodes().iterator().next();
				metaNode = new SLMetaNodeImpl(this, pMetaNode);
			}
			return metaNode;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node.", e);
		}
	}

	@Override
	public Collection<SLMetaNode> getMetaNodes() throws SLGraphSessionException {
		try {
			Collection<SLMetaNode> metaNodes = new ArrayList<SLMetaNode>();
			SLPersistentQuery query = treeSession.createQuery("//osl/metadata/types/*", SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> pNodes = result.getNodes();
			for (SLPersistentNode pNode : pNodes) {
				SLMetaNode metaNode = new SLMetaNodeImpl(this, pNode);
				metaNodes.add(metaNode);
			}
			return metaNodes;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node metadata.", e);
		}
	}

	@Override
	public SLMetaLinkType getMetaLinkType(Class<? extends SLLink> linkType) throws SLGraphSessionException {
		try {
			StringBuilder statement = new StringBuilder();
			statement.append("//osl/metadata/links/").append(linkType.getName());
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			SLMetaLinkType metaLinkType = null;
			if (result.getRowCount() == 1) {
				metaLinkType = new SLMetaLinkTypeImpl(this, result.getNodes().iterator().next());
			}
			return metaLinkType;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link type.", e);
		}
	}

	@Override
	public Collection<SLMetaLinkType> getMetaLinkTypes() throws SLGraphSessionException {
		try {
			Collection<SLMetaLinkType> metaLinkTypes = new ArrayList<SLMetaLinkType>();
			StringBuilder statement = new StringBuilder();
			statement.append("//osl/metadata/links/*");
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			Collection<SLPersistentNode> linkTypeNodes = result.getNodes();
			for (SLPersistentNode linkTypeNode : linkTypeNodes) {
				SLMetaLinkType metaLinkType = new SLMetaLinkTypeImpl(this, linkTypeNode);
				metaLinkTypes.add(metaLinkType);
			}
			return metaLinkTypes;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link type.", e);
		}
	}
}

