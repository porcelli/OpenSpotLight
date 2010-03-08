package org.openspotlight.persist.lazy.test;

import org.openspotlight.persist.internal.StreamPropertyWithParent;

public class SerializablePojoProperty implements
StreamPropertyWithParent<ClassWithLazyProperty> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4223160649233298985L;

	private ClassWithLazyProperty parent;

	private String anotherProperty;

	public String getAnotherProperty() {
		return anotherProperty;
	}

	public ClassWithLazyProperty getParent() {
		return parent;
	}

	public void setAnotherProperty(final String anotherProperty) {
		this.anotherProperty = anotherProperty;
	}

	public void setParent(final ClassWithLazyProperty parent) {
		this.parent = parent;
	}

}
