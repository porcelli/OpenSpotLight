package org.openspotlight.persist.internal;

import org.openspotlight.persist.annotation.SimpleNodeType;

import java.io.Serializable;

public interface StreamPropertyWithParent<P extends SimpleNodeType> extends
		Serializable {

	public P getParent();

	public void setParent(P parent);

}
