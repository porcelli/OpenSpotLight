/**
 * 
 */
package org.openspotlight.federation.util;

import java.util.Set;

import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.util.SimpleNodeTypeVisitor;

public class AggregateVisitor<A extends SimpleNodeType> implements
		SimpleNodeTypeVisitor<A> {
	private final Set<A> sources;

	public AggregateVisitor(final Set<A> sources) {
		this.sources = sources;
	}

	public <X extends A> void visitBean(final X bean) {
		sources.add(bean);
	}
}