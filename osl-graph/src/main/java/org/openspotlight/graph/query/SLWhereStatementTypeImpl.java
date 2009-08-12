package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;

public class SLWhereStatementTypeImpl implements SLWhereStatementType {

	private SLWhereStatement statement;
	private SLWhereStatementConditionInfo conditionInfo;
	
	public SLWhereStatementTypeImpl(SLWhereStatement statement, SLWhereStatementConditionInfo conditionInfo) {
		this.statement = statement;
		this.conditionInfo = conditionInfo;
	}

	public SLWhereStatementSubTypes subTypes() {
		conditionInfo.setSubTypes(true);
		return new SLWhereStatementSubTypesImpl(statement, conditionInfo);
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
