/**
 * 
 */
package org.openspotlight.federation.util;

import java.util.Set;

import org.openspotlight.federation.domain.Schedulable;
import org.openspotlight.persist.util.SimpleNodeTypeVisitor;

public class AggregateVisitor<A extends Schedulable> implements
		SimpleNodeTypeVisitor<A> {
	private final Set<A> sources;

	public AggregateVisitor(final Set<A> sources) {
		this.sources = sources;
	}

	public <X extends A> void visitBean(final X bean) {
		sources.add(bean);
	}
}