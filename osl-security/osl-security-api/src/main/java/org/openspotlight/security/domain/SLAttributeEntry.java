package org.openspotlight.security.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

public class SLAttributeEntry implements SimpleNodeType, Serializable {

    private static final long serialVersionUID = 2070513319138325001L;

    private SLIdentityObject parent;

	private String name;

	private Set<String> entries;

	public SLTransientIdentityObjectAttribute asIdentityAttribute() {
		final SLTransientIdentityObjectAttribute result = new SLTransientIdentityObjectAttribute();
		result.setName(this.name);
		result.setValues(new HashSet<String>(this.entries));
		return result;
	}

	public Set<String> getEntries() {
		return this.entries;
	}

	@KeyProperty
	public String getName() {
		return this.name;
	}

	@ParentProperty
	public SLIdentityObject getParent() {
		return this.parent;
	}

	public void setEntries(final Set<String> entries) {
		this.entries = entries;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setParent(final SLIdentityObject parent) {
		this.parent = parent;
	}

}
