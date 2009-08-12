package org.openspotlight.graph.query;


public interface SLSelectStatementByLink {
	
	public SLSelectStatement comma();
	public SLSelectStatementEnd end();
	public SLSelectStatementByLink a();
	public SLSelectStatementByLink b();
	public SLSelectStatementByLink any();


}
