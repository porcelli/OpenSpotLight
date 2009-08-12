package org.openspotlight.graph.query;

public enum SLSideType {

	A_SIDE ("A"),
	B_SIDE ("B"),
	ANY_SIDE ("Any");
	
	private String symbol;
	
	SLSideType(String symbol) {
		this.symbol = symbol;
	}
	
	public String symbol() {
		return symbol;
	}
}
