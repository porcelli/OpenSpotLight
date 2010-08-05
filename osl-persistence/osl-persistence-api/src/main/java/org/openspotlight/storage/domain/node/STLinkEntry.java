package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.domain.STAData;

public interface STLinkEntry extends STAData, STPropertyContainer {

	STNodeEntry getOrigin();

	STNodeEntry getTarget();

	String getLinkName();

	String getLinkId();

	STPartition getLinkPartition();
}
