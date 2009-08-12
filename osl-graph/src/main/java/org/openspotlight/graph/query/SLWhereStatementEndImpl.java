package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLWhereStatementInfo;


public class SLWhereStatementEndImpl implements SLWhereStatementEnd {
	
	private SLWhereStatementInfo statementInfo;
	private SLOrderByStatement orderBy;
	
	public SLWhereStatementEndImpl(SLWhereStatementInfo statementInfo, SLOrderByStatement orderBy) {
		this.statementInfo = statementInfo;
		this.orderBy = orderBy;
	}

	public SLOrderByStatement orderBy() {
		return orderBy;
	}
	
	public void close() {
		statementInfo.setClosed(true);
	}
}
