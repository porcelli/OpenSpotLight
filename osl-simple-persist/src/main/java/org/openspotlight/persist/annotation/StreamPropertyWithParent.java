package org.openspotlight.persist.annotation;

import java.io.Serializable;

public interface StreamPropertyWithParent<P extends SimpleNodeType> extends
		Serializable {

	public P getParent();

	public void setParent(P parent);

}
