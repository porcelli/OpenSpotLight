package org.openspotlight.graph.query;

public interface SLWhereStatementTypePropertyValue {
	public SLWhereStatement value(String value);
	public SLWhereStatement value(Integer value);
	public SLWhereStatement value(Long value);
	public SLWhereStatement value(Float value);
	public SLWhereStatement value(Double value);
	public SLWhereStatement value(Boolean value);
}
