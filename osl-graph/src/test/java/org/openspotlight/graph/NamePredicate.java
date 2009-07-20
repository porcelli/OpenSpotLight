package org.openspotlight.graph;

import org.openspotlight.SLRuntimeException;

public class NamePredicate implements SLNodePredicate {
	
	private String name;
	
	public NamePredicate(String name) {
		this.name = name;
	}

	//@Override
	public boolean evaluate(SLNode node) {
		try {
			return node.getName().indexOf(name) > -1;
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to evaluate name predicate.", e);
		}
	}
}
