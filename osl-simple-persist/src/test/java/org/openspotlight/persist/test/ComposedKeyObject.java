package org.openspotlight.persist.test;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class ComposedKeyObject implements SimpleNodeType {

	private String key1;

	private int key2;

	@KeyProperty
	public String getKey1() {
		return this.key1;
	}

	@KeyProperty
	public int getKey2() {
		return this.key2;
	}

	public void setKey1(final String key1) {
		this.key1 = key1;
	}

	public void setKey2(final int key2) {
		this.key2 = key2;
	}

}
