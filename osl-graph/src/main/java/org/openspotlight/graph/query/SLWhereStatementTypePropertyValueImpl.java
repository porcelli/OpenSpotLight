package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementTypePropertyValueImpl implements SLWhereStatementTypePropertyValue {
	
	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementTypePropertyValueImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatement value(String value) {
		conditionInfo.setValue(value);
		return statement;
	}

	public SLWhereStatement value(Integer value) {
		conditionInfo.setValue(value);
		return statement;
	}

	public SLWhereStatement value(Long value) {
		conditionInfo.setValue(value);
		return statement;
	}

	public SLWhereStatement value(Float value) {
		conditionInfo.setValue(value);
		return statement;
	}

	public SLWhereStatement value(Double value) {
		conditionInfo.setValue(value);
		return statement;
	}

	public SLWhereStatement value(Boolean value) {
		conditionInfo.setValue(value);
		return statement;
	}
}
