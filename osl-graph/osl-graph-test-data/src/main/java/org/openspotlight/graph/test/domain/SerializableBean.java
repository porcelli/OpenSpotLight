package org.openspotlight.graph.test.domain;

import org.openspotlight.common.util.Equals;

import java.io.Serializable;

public class SerializableBean implements Serializable {

	private static final long serialVersionUID = -2289174312505299304L;

	public String testString;

	public byte[] testBuffer;

	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof SerializableBean)) {
			return false;
		}
		final SerializableBean that = (SerializableBean) o;
		return Equals.eachEquality(testString, that.testString)
				&& Equals.eachEquality(testBuffer, that.testBuffer);
	}

}
