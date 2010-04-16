package org.openspotlight.persist.lazy.test;

import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.internal.LazyProperty;

public class ClassWithLazyProperty implements SimpleNodeType {

	private String test;

	private final LazyProperty<SerializablePojoProperty> bigPojoProperty = LazyProperty.Factory.create(SerializablePojoProperty.class,this);


	public LazyProperty<SerializablePojoProperty> getBigPojoProperty() {
		return bigPojoProperty;
	}

	public String getTest() {
		return test;
	}

	public void setTest(final String test) {
		this.test = test;
	}

}
