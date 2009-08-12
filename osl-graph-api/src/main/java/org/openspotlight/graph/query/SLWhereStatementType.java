package org.openspotlight.graph.query;


public interface SLWhereStatementType {
	
	public SLWhereStatementSubTypes subTypes();
	
	public SLWhereStatementTypeProperty property(String name);
	public SLWhereStatementTypeLink link(String name);

}
