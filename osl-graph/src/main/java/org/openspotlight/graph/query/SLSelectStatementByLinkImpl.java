package org.openspotlight.graph.query;

import static org.openspotlight.graph.query.SLSideType.ANY_SIDE;
import static org.openspotlight.graph.query.SLSideType.A_SIDE;
import static org.openspotlight.graph.query.SLSideType.B_SIDE;

import org.openspotlight.graph.query.info.SLSelectStatementByLinkInfo;

public class SLSelectStatementByLinkImpl implements SLSelectStatementByLink {
	
	private SLSelectStatement select;
	private SLSelectStatementByLinkInfo byLinkInfo;
	
	public SLSelectStatementByLinkImpl(SLSelectStatement select, SLSelectStatementByLinkInfo byLinkInfo) {
		this.select = select;
		this.byLinkInfo = byLinkInfo;
	}

	public SLSelectStatementByLink a() {
		byLinkInfo.getSides().add(A_SIDE);
		return this;
	}

	public SLSelectStatementByLink b() {
		byLinkInfo.getSides().add(B_SIDE);
		return this;
	}

	public SLSelectStatementByLink any() {
		byLinkInfo.getSides().add(ANY_SIDE);
		return this;
	}

	public SLSelectStatement comma() {
		byLinkInfo.setComma(true);
		return select;
	}

	public SLSelectStatementEnd end() {
		return select.end();
	}
}
