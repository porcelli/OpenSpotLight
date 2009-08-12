package org.openspotlight.graph.query;


public interface SLWhereStatement extends SLStatement {
	
	public SLWhereStatementType type(String typeName);
	
	public SLWhereStatement or();
	public SLWhereStatement and();

	public SLWhereStatement openBracket();
	public SLWhereStatement closeBracket();
	
	public SLWhereStatementEnd end();
	
}
