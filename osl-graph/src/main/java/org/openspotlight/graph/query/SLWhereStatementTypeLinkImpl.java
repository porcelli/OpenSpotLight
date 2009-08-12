package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementTypeLinkImpl implements SLWhereStatementTypeLink {
	
	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementTypeLinkImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatementTypeLinkSide a() {
		conditionInfo.setSide(SLSideType.A_SIDE);
		return new SLWhereStatementTypeLinkSideImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeLinkSide b() {
		conditionInfo.setSide(SLSideType.A_SIDE);
		return new SLWhereStatementTypeLinkSideImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeLinkSide any() {
		conditionInfo.setSide(SLSideType.A_SIDE);
		return new SLWhereStatementTypeLinkSideImpl(statement, conditionInfo);
	}
}
