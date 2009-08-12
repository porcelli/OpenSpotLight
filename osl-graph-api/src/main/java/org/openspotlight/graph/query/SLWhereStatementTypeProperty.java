package org.openspotlight.graph.query;


public interface SLWhereStatementTypeProperty {
	public SLWhereStatementTypePropertyValue lesserThan();
	public SLWhereStatementTypePropertyValue greaterThan();
	public SLWhereStatementTypePropertyValue equalsTo();
	public SLWhereStatementTypePropertyValue notEqualsTo();
	public SLWhereStatementTypePropertyValue lesserOrEqualThan();
	public SLWhereStatementTypePropertyValue greaterOrEqualThan();
	public SLWhereStatementTypePropertyValue contains();
}
