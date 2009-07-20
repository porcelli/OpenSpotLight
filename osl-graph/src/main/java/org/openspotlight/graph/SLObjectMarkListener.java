package org.openspotlight.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SLObjectMarkListener implements SLGraphSessionEventListener {
	
	private Set<SLLink> linksForDeletion;
	private Set<SLNode> nodesForDeletion;
	
	public SLObjectMarkListener() {
		linksForDeletion = new HashSet<SLLink>();
		nodesForDeletion = new HashSet<SLNode>();
	}

	@Override
	public void nodeAdded(SLNodeEvent event) throws SLGraphSessionException {
		
		SLGraphSession session = event.getSession();
		SLNode node = event.getNode();
		Collection<Class<? extends SLLink>> linkTypesForLinkDeletion = event.getLinkTypesForLinkDeletion();
		Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion = event.getLinkTypesForLinkedNodesDeletion();

		if (linkTypesForLinkDeletion != null) {
			// mark for deletion links that have the added node as side ... 
			for (Class<? extends SLLink> linkType : linkTypesForLinkDeletion) {
				Collection<? extends SLLink> links = session.getLinks(linkType, node, null, SLLink.DIRECTION_ANY);
				linksForDeletion.addAll(links);
			}
		}

		if (linkTypesForLinkedNodeDeletion != null) {
			// mark for deletion all the nodes linked to this node ...
	 		for (Class<? extends SLLink> linkType : linkTypesForLinkedNodeDeletion) {
				Collection<SLNode> nodes = session.getNodesByLink(linkType, node);
				nodesForDeletion.addAll(nodes);
			}
		}
		
		// unmark the added node (if it's present in the set) ...
		nodesForDeletion.remove(node);
	}

	@Override
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException {
		// unmark link and its sides ...
		SLLink link = event.getLink();
		SLNode[] sides = link.getSides();
		linksForDeletion.remove(link);
		nodesForDeletion.remove(sides[0]);
		nodesForDeletion.remove(sides[1]);
	}

	@Override
	public void beforeSave(SLGraphSessionEvent event) throws SLGraphSessionException {
		
		// delete links ...
		for (SLLink link : linksForDeletion) {
			link.remove();
		}
		
		// delete nodes ...
		for (SLNode node : nodesForDeletion) {
			node.remove();
		}
	}
}
