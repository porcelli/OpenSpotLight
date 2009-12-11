package org.openspotlight.federation.domain;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.graph.SLNode;

public interface GroupListener {

	public static enum ListenerAction {
		CONTINUE, IGNORE
	}

	public ListenerAction groupAdded(SLNode groupNode, ExecutionContext context);

	public ListenerAction groupRemoved(SLNode groupNode,
			ExecutionContext context);

}
