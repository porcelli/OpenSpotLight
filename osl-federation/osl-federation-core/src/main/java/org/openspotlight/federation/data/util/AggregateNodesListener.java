package org.openspotlight.federation.data.util;

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.InstanceMetadata.ItemEventListener;

/**
 * Simple listener for grouping all changes for later use.
 * 
 * @author feu
 * 
 */
public class AggregateNodesListener implements
		ItemEventListener<ConfigurationNode> {

	public Set<ConfigurationNode> getInsertedNodes() {
		return insertedNodes;
	}

	public Set<ConfigurationNode> getChangedNodes() {
		return changedNodes;
	}

	public Set<ConfigurationNode> getRemovedNodes() {
		return removedNodes;
	}

	public void clearData(){
		this.insertedNodes.clear();
		this.changedNodes.clear();
		this.removedNodes.clear();
	}
	
	private final Set<ConfigurationNode> insertedNodes = new HashSet<ConfigurationNode>();

	private final Set<ConfigurationNode> changedNodes = new HashSet<ConfigurationNode>();

	private final Set<ConfigurationNode> removedNodes = new HashSet<ConfigurationNode>();

	public void changeEventHappened(ItemChangeEvent<ConfigurationNode> event) {
		switch (event.getType()) {
		case ADDED:
			insertedNodes.add(event.getNewItem());
			break;
		case CHANGED:
			changedNodes.add(event.getNewItem());
			break;
		case EXCLUDED:
			removedNodes.add(event.getOldItem());
			break;
		}

	}
}
