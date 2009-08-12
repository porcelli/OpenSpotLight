package org.openspotlight.graph.query.info;

import java.io.Serializable;
import java.util.EnumSet;

import org.openspotlight.graph.query.SLSideType;

public class SLSelectStatementByLinkInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private EnumSet<SLSideType> sides;
	private boolean comma;
	
	SLSelectStatementByLinkInfo(String name) {
		setName(name);
		sides = EnumSet.noneOf(SLSideType.class);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EnumSet<SLSideType> getSides() {
		return sides;
	}

	public void setSides(EnumSet<SLSideType> sides) {
		this.sides = sides;
	}

	public boolean isComma() {
		return comma;
	}

	public void setComma(boolean comma) {
		this.comma = comma;
	}
}
