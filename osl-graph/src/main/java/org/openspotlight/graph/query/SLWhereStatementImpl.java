package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;
import org.openspotlight.graph.query.info.SLWhereStatementInfo;

public class SLWhereStatementImpl implements SLWhereStatement, SLWhereStatementInfoGetter {
	
	private SLWhereStatement outerStatement;
	private SLWhereStatementEnd whereEnd;
	private SLWhereStatementInfo statementInfo;
	private SLWhereStatementConditionInfo lastConditionInfo;

	public SLWhereStatementImpl(SLOrderByStatement orderBy, SLWhereStatementInfo statementInfo) {
		this(new SLWhereStatementEndImpl(statementInfo, orderBy), statementInfo);
	}

	public SLWhereStatementImpl(SLWhereStatement outerStatement, SLWhereStatementInfo statementInfo) {
		this.outerStatement = outerStatement;
		this.statementInfo = statementInfo;
		this.whereEnd = outerStatement.end();
	}

	public SLWhereStatementImpl(SLWhereStatementEnd whereEnd, SLWhereStatementInfo statementInfo) {
		this.whereEnd = whereEnd;
		this.statementInfo = statementInfo;
	}

	public SLWhereStatementInfo getWhereStatementInfo() {
		return statementInfo;
	}

	public SLWhereStatement and() {
		verifyConditionalOperator();
		lastConditionInfo = statementInfo.addStatementCondition(SLConditionalOperatorType.AND);
		return this;
	}

	public SLWhereStatement or() {
		verifyConditionalOperator();
		lastConditionInfo = statementInfo.addStatementCondition(SLConditionalOperatorType.OR);
		return this;
	}
	
	public SLWhereStatement openBracket() {
		if (lastConditionInfo == null) {
			lastConditionInfo = statementInfo.addStatementCondition();
		}
		SLWhereStatementInfo statementInfo = new SLWhereStatementInfo();
		statementInfo.bookmarkOpenBracket();
		lastConditionInfo.setStatementInfo(statementInfo);
		return new SLWhereStatementImpl(this, statementInfo);
	}

	public SLWhereStatement closeBracket() {
		if (outerStatement == null) {
			throw new SLInvalidQuerySyntaxRuntimeException("cannot close a bracket before open it.");
		}
		statementInfo.setClosed(true);
		return outerStatement;
	}

	public SLWhereStatementType type(String typeName) {
		if (lastConditionInfo == null) {
			lastConditionInfo = statementInfo.addStatementCondition(typeName);
		}
		else {
			lastConditionInfo.setTypeName(typeName);	
		}
		return new SLWhereStatementTypeImpl(this, lastConditionInfo);
	}

	public SLWhereStatementEnd end() {
		whereEnd.close();
		return whereEnd;
	}

	private void verifyConditionalOperator() {
		if (statementInfo.getConditionInfoList().isEmpty()) {
			throw new SLInvalidQuerySyntaxRuntimeException("the first condition of a statement must not start with AND or OR operators");
		}
	}
}
