package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class SimpleObject implements SimpleNodeType , Comparable<SimpleObject>{

	private int id;

	@KeyProperty
	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

    public int compareTo(SimpleObject o) {
        return this.id<o.id ? -1 : (this.id==o.id ? 0 : 1);
    }
}
