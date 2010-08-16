package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.Link;
import org.openspotlight.storage.domain.Node;

public class LinkImpl extends PropertyContainerImpl implements
		Link {

    private static final long serialVersionUID = -3462836679437486046L;

    public LinkImpl(String linkName, Node origin,
			Node target, boolean resetTimeout) {
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

	private final Node origin;

	private final Node target;

	private final Partition originPartition;

	public String getLinkId() {
		return linkId;
	}

	public String getLinkName() {
		return linkName;
	}

	public Node getOrigin() {
		return origin;
	}

	public Node getTarget() {
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
		if (!(o instanceof Link))
			return false;
		return getKeyAsString().equals(((Link) o).getKeyAsString());
	}

	public int hashCode() {
		return getKeyAsString().hashCode();
	}

}
