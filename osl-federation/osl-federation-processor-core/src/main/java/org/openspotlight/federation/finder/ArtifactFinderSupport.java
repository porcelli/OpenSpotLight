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
            final T newOne = findTheEquivalent(existent, newOnes);
            delta.remove(newOne);

            if (newOne != null) {
                final ChangeType defaultChangeType = newOne.contentEquals(existent) ? ChangeType.NOT_CHANGED : ChangeType.CHANGED;
                switch (existent.getChangeType()) {
                    case INCLUDED:
                        newOne.setChangeType(ChangeType.INCLUDED);
                        break;
                    case EXCLUDED:
                        newOne.setChangeType(ChangeType.CHANGED);
                        break;
                    default:
                        newOne.setChangeType(defaultChangeType);
                }
                result.add(newOne);
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
        for (final T equivalent : setWithEquivalent) {
            if (equivalent.getArtifactCompleteName().equals(artifact.getArtifactCompleteName())) {
                return equivalent;
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
