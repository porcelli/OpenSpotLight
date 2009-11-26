package org.openspotlight.common.visitor;

public interface Visitable {
	public void accept(Visitor v);
}
