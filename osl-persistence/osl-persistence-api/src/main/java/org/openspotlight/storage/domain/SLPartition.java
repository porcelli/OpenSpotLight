package org.openspotlight.storage.domain;

import org.openspotlight.storage.STPartition;

/**
 * Created by User: feu - Date: Apr 20, 2010 - Time: 9:33:28 AM
 */
public enum SLPartition implements STPartition {


    GRAPH("graph"),
    FEDERATION("federation"),
    SYNTAX_HIGHLIGHT("syntax-highlight",FEDERATION),
    LINE_REFERENCE("line-reference",GRAPH),
    SECURITY("security"),
    LOG("log")
    ;

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
