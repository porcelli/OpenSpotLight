package org.openspotlight.federation.util;

import static org.openspotlight.common.util.Exceptions.logAndReturn;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeType;
import org.openspotlight.federation.data.InstanceMetadata.ItemEventListener;
import org.openspotlight.federation.data.impl.Artifact;
import org.openspotlight.federation.data.impl.Artifact.Status;

/**
 * This class will change all status of artifacts been changed to the convenient one.
 * 
 * @author feu
 */
public enum ArtifactStatusUpdateVisitor implements ItemEventListener<ConfigurationNode> {

    /**
     * Single instance
     */
    INSTANCE;

    /**
     * {@inheritDoc}
     */
    public void changeEventHappened( final ItemChangeEvent<ConfigurationNode> event ) {
        final ConfigurationNode target = event.getNewItem() == null ? event.getOldItem() : event.getNewItem();
        if (target instanceof Artifact) {
            final Artifact artifact = (Artifact)target;
            final ItemChangeType changeStatus = event.getType();
            Status newStatus;
            switch (changeStatus) {
                case ADDED:
                    newStatus = Status.INCLUDED;
                    break;
                case CHANGED:
                    if (Status.ALREADY_PROCESSED.equals(artifact.getStatus())) {
                        newStatus = Status.CHANGED;
                    } else {
                        newStatus = artifact.getStatus();
                    }
                    break;
                case EXCLUDED:
                    newStatus = Status.EXCLUDED;
                    //nothing to do here, since this exclusion was fired from outside the artifacts
                    break;
                default:
                    throw logAndReturn(new IllegalStateException());
            }
            artifact.getInstanceMetadata().setProperty("status", newStatus);
        }
    }

}
