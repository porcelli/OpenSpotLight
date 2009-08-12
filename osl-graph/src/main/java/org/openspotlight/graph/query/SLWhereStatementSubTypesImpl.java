package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementSubTypesImpl implements SLWhereStatementSubTypes {

	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementSubTypesImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatementTypeLink link(String name) {
		conditionInfo.setLinkTypeName(name);
		return new SLWhereStatementTypeLinkImpl(statement, conditionInfo);
	}

	public SLWhereStatementTypeProperty property(String name) {
		conditionInfo.setPropertyName(name);
		return new SLWhereStatementTypePropertyImpl(statement, conditionInfo);
	}

}
