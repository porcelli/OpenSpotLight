package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.STLinkEntry;
import org.openspotlight.storage.domain.STNodeEntry;

public class STLinkEntryImpl extends PropertyContainerImpl implements
		STLinkEntry {

    private static final long serialVersionUID = -3462836679437486046L;

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

	private final Partition originPartition;

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
	public Partition getPartition() {
		return originPartition;
	}

	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof STLinkEntry))
			return false;
		return getKeyAsString().equals(((STLinkEntry) o).getKeyAsString());
	}

	public int hashCode() {
		return getKeyAsString().hashCode();
	}

}
