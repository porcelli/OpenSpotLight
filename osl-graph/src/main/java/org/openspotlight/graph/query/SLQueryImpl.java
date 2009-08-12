package org.openspotlight.graph.query;

import java.util.ArrayList;
import java.util.List;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.query.info.SLSelectStatementInfo;
import org.openspotlight.graph.query.info.SLWhereStatementConditionInfo;
import org.openspotlight.graph.query.info.SLWhereStatementInfo;

public class SLQueryImpl implements SLQuery {
	
	private List<SLSelectStatement> selects;
	
	public SLQueryImpl() {
		selects = new ArrayList<SLSelectStatement>();
	}

	public SLQueryResult execute() throws SLInvalidQuerySyntaxException, SLGraphSessionException {
		for (SLSelectStatement select : selects) {
			validate(select);
			System.out.println(select);
		}
		return null;
	}

	public SLSelectStatement select() throws SLGraphSessionException {
		SLSelectStatement select = new SLSelectStatementImpl();
		selects.add(select);
		return select;
	}
	
	private void validate(SLSelectStatement select) throws SLInvalidQuerySyntaxException {
		SLSelectStatementInfoGetter statementInfoGetter = SLSelectStatementInfoGetter.class.cast(select);
		SLSelectStatementInfo selectStatementInfo = statementInfoGetter.getSelectInfo();
		validateWhereStatement(selectStatementInfo.getWhereStatementInfo());
	}
	
	private void validateWhereStatement(SLWhereStatementInfo whereStatementInfo) {
		if (!whereStatementInfo.isClosed()) {
			SLRuntimeException e = new SLInvalidQuerySyntaxRuntimeException("bracket must be closed.");
			e.setStackTrace(whereStatementInfo.getOpenBraceStackTrace());
			throw e;
		}
		List<SLWhereStatementConditionInfo> conditionInfoList = whereStatementInfo.getConditionInfoList();
		for (SLWhereStatementConditionInfo conditionInfo : conditionInfoList) {
			if (conditionInfo.getStatementInfo() != null) {
				validateWhereStatement(conditionInfo.getStatementInfo());
			}
		}
	}

}
