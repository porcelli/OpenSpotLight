package org.openspotlight.federation.processing;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openspotlight.graph.SLLink;

public class BundleProcessorSupport {

	public static Collection<Class<? extends SLLink>> links(
			final Class<? extends SLLink>... linkTypes) {
		final List<Class<? extends SLLink>> list = Arrays.asList(linkTypes);
		return list;
	}

}
