package org.openspotlight.graph.test.link;

import org.openspotlight.graph.SLLink;

public abstract class TypeExtends extends SLLink {
	private String extendsName;
	private boolean extendsFlag;

	public String getExtendsName() {
		return extendsName;
	}

	public void setExtendsName(String extendsName) {
		this.extendsName = extendsName;
	}

	public boolean isExtendsFlag() {
		return extendsFlag;
	}

	public void setExtendsFlag(boolean extendsFlag) {
		this.extendsFlag = extendsFlag;
	}
}
