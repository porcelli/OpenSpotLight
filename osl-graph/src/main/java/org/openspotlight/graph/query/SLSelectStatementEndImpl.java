package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLSelectStatementInfo;
import org.openspotlight.graph.query.info.SLWhereStatementInfo;

public class SLSelectStatementEndImpl implements SLSelectStatementEnd {
	
	private SLWhereStatement where;
	private SLOrderByStatement orderBy;
	
	SLSelectStatementEndImpl(SLSelectStatementInfo selectInfo) {
		this.orderBy = new SLOrderByStatementImpl();
		SLWhereStatementInfo whereInfo = selectInfo.addWhereInfo();
		this.where = new SLWhereStatementImpl(orderBy, whereInfo);
	}

	public SLWhereStatement where() {
		return where;
	}

	public SLOrderByStatement orderBy() {
		return orderBy;
	}
}
