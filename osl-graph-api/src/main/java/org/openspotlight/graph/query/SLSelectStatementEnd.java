package org.openspotlight.graph.query;


public interface SLSelectStatementEnd extends SLStatement {
	
	public SLWhereStatement where();
	public SLOrderByStatement orderBy();

}
