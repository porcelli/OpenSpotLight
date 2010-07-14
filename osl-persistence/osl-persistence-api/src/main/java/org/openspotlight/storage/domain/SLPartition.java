package org.openspotlight.storage.domain;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;

/**
 * Created by User: feu - Date: Apr 20, 2010 - Time: 9:33:28 AM
 */
public enum SLPartition implements STPartition {

	GRAPH("graph"), FEDERATION("federation"), SYNTAX_HIGHLIGHT(
			"syntax_highlight", FEDERATION), LINE_REFERENCE("line_reference",
			GRAPH), SECURITY("security"), LOG("log");

	public static final STPartitionFactory FACTORY = new STPartitionFactory() {

		@Override
		public STPartition getPartitionByName(String name) {
			return valueOf(name.toUpperCase());
		}

		@Override
		public STPartition[] getValues() {
			return values();
		}
	};

	private String partitionName;
	private SLPartition parent;

	SLPartition(String partitionName, SLPartition parent) {
		this.partitionName = partitionName;
		this.parent = parent;
	}

	SLPartition(String partitionName) {
		this.partitionName = partitionName;
	}

	public String getPartitionName() {
		return partitionName;
	}

	public SLPartition getParent() {
		return parent;
	}
}
