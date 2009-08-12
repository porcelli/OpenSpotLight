package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementTypeLinkSideImpl implements SLWhereStatementTypeLinkSide {
	
	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementTypeLinkSideImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatementTypeLinkSideValue equalsTo() {
		conditionInfo.setEqualityOperator(SLEqualityOperatorType.EQUAL);
		return new SLWhereStatementTypeLinkSideValueImpl(statement, conditionInfo);
	}
	
	public SLWhereStatementTypeLinkSideValue notEqualsTo() {
		conditionInfo.setEqualityOperator(SLEqualityOperatorType.NOT_EQUAL);
		return new SLWhereStatementTypeLinkSideValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeLinkSideValue greaterOrEqualThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_OR_EQUAL_THAN);
		return new SLWhereStatementTypeLinkSideValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeLinkSideValue greaterThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_THAN);
		return new SLWhereStatementTypeLinkSideValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeLinkSideValue lesserOrEqualThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_OR_EQUAL_THAN);
		return new SLWhereStatementTypeLinkSideValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeLinkSideValue lesserThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_THAN);
		return new SLWhereStatementTypeLinkSideValueImpl(statement, conditionInfo);
	}
}
