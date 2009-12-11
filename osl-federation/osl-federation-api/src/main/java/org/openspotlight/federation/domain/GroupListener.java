package org.openspotlight.federation.domain;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.graph.SLNode;

public interface GroupListener {

	public void groupAdded(SLNode groupNode, ExecutionContext context);

	public void groupRemoved(SLNode groupNode, ExecutionContext context);

}
