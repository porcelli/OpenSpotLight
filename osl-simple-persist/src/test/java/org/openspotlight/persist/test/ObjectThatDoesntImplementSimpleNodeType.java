package org.openspotlight.persist.test;

import org.openspotlight.persist.internal.StreamPropertyWithParent;

public class ObjectThatDoesntImplementSimpleNodeType implements
		StreamPropertyWithParent<RootObj> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5823210668928982292L;

	private RootObj parent;

	private String name;

	private int number;

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

	public RootObj getParent() {
		return parent;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public void setParent(final RootObj parent) {
		this.parent = parent;
	}

}
