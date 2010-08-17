package org.openspotlight.storage.domain;

public interface Link extends StorageDataMarker, PropertyContainer {

    Node getOrigin();

    Node getTarget();

    String getLinkType();

    String getLinkId();
}
