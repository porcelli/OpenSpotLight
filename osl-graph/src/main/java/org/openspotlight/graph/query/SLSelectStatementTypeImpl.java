package org.openspotlight.graph.query;

import org.openspotlight.graph.query.info.SLSelectStatementTypeInfo;

public class SLSelectStatementTypeImpl implements SLSelectStatementType {
	
	private SLSelectStatement select;
	private SLSelectStatementTypeInfo typeInfo;
	
	public SLSelectStatementTypeImpl(SLSelectStatement select, SLSelectStatementTypeInfo typeInfo) {
		this.select = select;
		this.typeInfo = typeInfo;
	}

	public SLSelectStatement comma() {
		typeInfo.setComma(true);
		return select;
	}

	public SLSelectStatementEnd end() {
		return select.end();
	}

	public SLSelectStatementType subTypes() {
		typeInfo.setSubTypes(true);
		return this;
	}

}
