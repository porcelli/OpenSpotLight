package org.openspotlight.federation.domain.scheduler;

import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Schedulable.SchedulableCommand;

/**
 * The Class ArtifactSourceSchedulable.
 */
public class ArtifactSourceSchedulable implements SchedulableCommand<ArtifactSource> {

    /* (non-Javadoc)
     * @see org.openspotlight.federation.domain.Schedulable.SchedulableCommand#execute(org.openspotlight.federation.domain.Schedulable)
     */
    public void execute( final ArtifactSource schedulable ) {
        //FIXME execute artifact loading on this source
    }

}
