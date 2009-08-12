package org.openspotlight.graph.query;

import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.annotation.SLProperty;

public interface JavaPackage extends SLNode {
	
	@SLProperty
	public String getCaption() throws SLGraphSessionException;
	public void setCaption(String caption) throws SLGraphSessionException;

}
