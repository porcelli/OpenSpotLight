package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementTypePropertyImpl implements SLWhereStatementTypeProperty {
	
	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementTypePropertyImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatementTypePropertyValue equalsTo() {
		conditionInfo.setEqualityOperator(SLEqualityOperatorType.EQUAL);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypePropertyValue notEqualsTo() {
		conditionInfo.setEqualityOperator(SLEqualityOperatorType.NOT_EQUAL);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypePropertyValue contains() {
		conditionInfo.setStringLikeOperator(SLStringLikeOperator.CONTAINS);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypePropertyValue greaterOrEqualThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_OR_EQUAL_THAN);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypePropertyValue lesserOrEqualThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_OR_EQUAL_THAN);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypePropertyValue greaterThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.GREATER_THAN);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypePropertyValue lesserThan() {
		conditionInfo.setRelationalOperator(SLRelationalOperatorType.LESSER_THAN);
		return new SLWhereStatementTypePropertyValueImpl(statement, conditionInfo);
	}
}
