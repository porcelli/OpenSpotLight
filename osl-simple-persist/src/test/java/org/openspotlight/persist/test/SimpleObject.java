package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class SimpleObject implements SimpleNodeType {

	private int id;

	@KeyProperty
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

}
