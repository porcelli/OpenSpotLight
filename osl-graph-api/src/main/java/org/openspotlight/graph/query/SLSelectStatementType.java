package org.openspotlight.graph.query;


public interface SLSelectStatementType extends SLStatement {
	public SLSelectStatement comma();
	public SLSelectStatementEnd end();
	public SLSelectStatementType subTypes();
}
