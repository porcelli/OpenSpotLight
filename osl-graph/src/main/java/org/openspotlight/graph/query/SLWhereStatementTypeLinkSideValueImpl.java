package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementTypeLinkSideValueImpl implements SLWhereStatementTypeLinkSideValue {
	
	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementTypeLinkSideValueImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatement value(Integer value) {
		conditionInfo.setValue(value);
		return statement;
	}

	public SLWhereStatement value(Long value) {
		conditionInfo.setValue(value);
		return statement;
	}

}
