package org.openspotlight.federation.processing.internal.task;

import java.util.Comparator;

public class ArtifactTaskPriorityComparator implements Comparator<ArtifactTask> {

    public int compare( final ArtifactTask o1,
                        final ArtifactTask o2 ) {
        final int o1Priority = o1.getPriority();
        final int o2Priority = o2.getPriority();
        return o1Priority < o2Priority ? -1 : o1Priority == o2Priority ? 0 : 1;
    }

}
