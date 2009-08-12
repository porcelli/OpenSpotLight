package org.openspotlight.graph.query;


public interface SLWhereStatementEnd extends SLStatement {
	public SLOrderByStatement orderBy();
	public void close();
}
