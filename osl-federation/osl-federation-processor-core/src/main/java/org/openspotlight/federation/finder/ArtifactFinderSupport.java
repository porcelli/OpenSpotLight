package org.openspotlight.federation.finder;

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ChangeType;

/**
 * The Class ArtifactFinderSupport.
 */
public class ArtifactFinderSupport {

    /**
     * Apply difference on existents.
     * 
     * @param existents the existents
     * @param newOnes the new ones
     * @return the set< t>
     */
    public static <T extends Artifact> Set<T> applyDifferenceOnExistents( final Set<T> existents,
                                                                          final Set<T> newOnes ) {
        final Set<T> result = new HashSet<T>();
        final Set<T> delta = new HashSet<T>(newOnes);
        for (final T existent : existents) {
            delta.remove(existent);
            final T equivalent = findTheEquivalent(existent, newOnes);
            if (equivalent != null) {
                if (equivalent.contentEquals(existent)) {
                    existent.setChangeType(ChangeType.NOT_CHANGED);
                    result.add(existent);
                } else {
                    equivalent.setChangeType(ChangeType.CHANGED);
                    result.add(equivalent);
                }
            } else {
                existent.setChangeType(ChangeType.EXCLUDED);
                result.add(existent);
            }
        }
        for (final T newOne : delta) {
            newOne.setChangeType(ChangeType.INCLUDED);
        }
        result.addAll(delta);
        existents.clear();
        existents.addAll(result);
        return existents;
    }

    /**
     * Find the equivalent.
     * 
     * @param artifact the artifact
     * @param setWithEquivalent the set with equivalent
     * @return the t
     */
    public static <T extends Artifact> T findTheEquivalent( final T artifact,
                                                            final Set<T> setWithEquivalent ) {
        if (setWithEquivalent.contains(artifact)) {
            for (final T equivalent : setWithEquivalent) {
                if (equivalent.equals(artifact)) {
                    return equivalent;
                }
            }
        }
        return null;
    }

    /**
     * Freeze changes after bundle processing. This will remove all artifacts marked as excluded and change all other status to
     * not changed.
     * 
     * @param existents the existents
     */
    public static <T extends Artifact> void freezeChangesAfterBundleProcessing( final Set<T> existents ) {
        final Set<T> toBeRemoved = new HashSet<T>();
        for (final T t : existents) {
            if (ChangeType.EXCLUDED.equals(t.getChangeType())) {
                toBeRemoved.add(t);
            } else {
                t.setChangeType(ChangeType.NOT_CHANGED);
            }
        }
        existents.removeAll(toBeRemoved);
    }
}
