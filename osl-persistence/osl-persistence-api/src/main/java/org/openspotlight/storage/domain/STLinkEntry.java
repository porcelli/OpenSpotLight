package org.openspotlight.storage.domain;


public interface STLinkEntry extends StorageDataMarker, PropertyContainer {

	STNodeEntry getOrigin();

	STNodeEntry getTarget();

	String getLinkName();

	String getLinkId();
}
