package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.StringIDSupport;

public class STLinkEntryImpl extends STPropertyContainerImpl implements
		STLinkEntry {

	public STLinkEntryImpl(String linkName, STNodeEntry origin,
			STNodeEntry target, boolean resetTimeout) {
		super(resetTimeout);
		this.linkName = linkName;
		this.origin = origin;
		this.target = target;
		this.originPartition = origin.getPartition();
		this.linkId = StringIDSupport.getLinkKeyAsString(originPartition,
				linkName, origin, target);
	}

	private final String linkId;

	private final String linkName;

	private final STNodeEntry origin;

	private final STNodeEntry target;

	private final STPartition originPartition;

	public String getLinkId() {
		return linkId;
	}

	public String getLinkName() {
		return linkName;
	}

	public STNodeEntry getOrigin() {
		return origin;
	}

	public STNodeEntry getTarget() {
		return target;
	}

	@Override
	public String getKeyAsString() {
		return linkId;
	}

	@Override
	public STPartition getPartition() {
		return originPartition;
	}

}
