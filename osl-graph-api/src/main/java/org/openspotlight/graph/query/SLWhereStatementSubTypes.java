package org.openspotlight.graph.query;


public interface SLWhereStatementSubTypes extends SLStatement {

	public SLWhereStatementTypeProperty property(String name);
	public SLWhereStatementTypeLink link(String name);
}
