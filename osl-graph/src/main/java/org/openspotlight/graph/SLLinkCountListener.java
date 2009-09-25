package org.openspotlight.graph;

import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

import static org.openspotlight.graph.SLCommonSupport.getInternalPropertyAsString;
import static org.openspotlight.graph.SLCommonSupport.getLinkType;
import static org.openspotlight.graph.SLCommonSupport.getInternalPropertyAsInteger;
import static org.openspotlight.graph.SLCommonSupport.setInternalIntegerProperty;

public class SLLinkCountListener extends SLAbstractGraphSessionEventListener {
	
	@Override
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException {
		SLLink link = event.getLink();
		if (link.isBidirectional()) return;
		
		SLPersistentNode pLinkNode = event.getLinkNode();
		
		try {
			String sourceID = getInternalPropertyAsString(pLinkNode, SLConsts.PROPERTY_NAME_SOURCE_ID);
			String targetID = getInternalPropertyAsString(pLinkNode, SLConsts.PROPERTY_NAME_TARGET_ID);
			
			SLPersistentTreeSession treeSession = pLinkNode.getSession();
			
			SLPersistentNode sourceNode = treeSession.getNodeByID(sourceID);
			SLPersistentNode targetNode = treeSession.getNodeByID(targetID);
			
			Class<? extends SLLink> linkType = getLinkType(link);
			String sourceLinkCountName = linkType.hashCode() + "." + SLConsts.PROPERTY_NAME_SOURCE_COUNT;
			String targetLinkCountName = linkType.hashCode() + "." + SLConsts.PROPERTY_NAME_TARGET_COUNT;
			
			Integer sourceLinkCount = getInternalPropertyAsInteger(sourceNode, sourceLinkCountName);
			Integer targetLinkCount = getInternalPropertyAsInteger(targetNode, targetLinkCountName);
			
			sourceLinkCount = sourceLinkCount == null ? 1 : sourceLinkCount + 1;
			targetLinkCount = targetLinkCount == null ? 1 : targetLinkCount + 1;
			
			setInternalIntegerProperty(sourceNode, sourceLinkCountName, sourceLinkCount);
			setInternalIntegerProperty(targetNode, targetLinkCountName, targetLinkCount);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to add link count info.", e);
		}
	}
}
