package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.Link;
import org.openspotlight.storage.domain.Node;

public class LinkImpl extends PropertyContainerImpl implements
        Link {

    private static final long serialVersionUID = -3462836679437486046L;

    public LinkImpl(final String linkType, final Node origin,
                    final Node target, final boolean resetTimeout) {
        super(resetTimeout);
        this.linkType = linkType;
        this.origin = origin;
        this.target = target;
        originPartition = origin.getPartition();
        linkId = StringIDSupport.getLinkKeyAsString(originPartition,
                linkType, origin, target);
    }

    private final String    linkId;

    private final String    linkType;

    private final Node      origin;

    private final Node      target;

    private final Partition originPartition;

    @Override
    public String getLinkId() {
        return linkId;
    }

    @Override
    public String getLinkType() {
        return linkType;
    }

    @Override
    public Node getOrigin() {
        return origin;
    }

    @Override
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

    @Override
    public boolean equals(final Object o) {
        if (o == this) { return true; }
        if (!(o instanceof Link)) { return false; }
        return getKeyAsString().equals(((Link) o).getKeyAsString());
    }

    @Override
    public int hashCode() {
        return getKeyAsString().hashCode();
    }

}
