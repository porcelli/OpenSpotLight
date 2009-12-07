package org.openspotlight.federation.domain;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;

@Name( "database" )
public class ViewArtifact extends TableArtifact {

    private static final long serialVersionUID = -3337935385738334416L;

    @Override
    public boolean contentEquals( final Artifact other ) {
        if (!(other instanceof ViewArtifact)) {
            return false;
        }
        final ViewArtifact that = (ViewArtifact)other;
        return Equals.eachEquality(this.getColumns(), that.getColumns());
    }

}
