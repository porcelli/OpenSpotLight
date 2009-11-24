package org.openspotlight.security.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.identity.idm.spi.model.IdentityObjectAttribute;
import org.openspotlight.common.util.Assertions;

public class SLTransientIdentityObjectAttribute implements
		IdentityObjectAttribute {

	private Set<String> values = new HashSet<String>();

	private String name;

	public void addValue(final Object value) {
		Assertions.checkCondition("stringValue", value instanceof String);
		final String valueAsString = (String) value;
		this.values.add(valueAsString);

	}

	public String getName() {
		return this.name;
	}

	public int getSize() {
		return this.values.size();
	}

	public Object getValue() {
		if (this.values.size() == 1) {
			return this.values.iterator().next();
		}
		return null;
	}

	public Collection<?> getValues() {
		return this.values;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setValues(final Set<String> values) {
		this.values = values;
	}

}
