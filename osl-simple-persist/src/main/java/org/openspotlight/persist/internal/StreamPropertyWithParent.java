package org.openspotlight.persist.internal;

import java.io.Serializable;

import org.openspotlight.persist.annotation.SimpleNodeType;

public interface StreamPropertyWithParent<P extends SimpleNodeType> extends
		Serializable {

	public P getParent();

	public void setParent(P parent);

}
