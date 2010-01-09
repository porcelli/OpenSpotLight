package org.openspotlight.persist.test;

import java.io.InputStream;

import org.openspotlight.persist.annotation.SimpleNodeType;

public class ObjectWithInputStream implements SimpleNodeType {

	private InputStream stream;

	public InputStream getStream() {
		return stream;
	}

	public void setStream(final InputStream stream) {
		this.stream = stream;
	}

}
