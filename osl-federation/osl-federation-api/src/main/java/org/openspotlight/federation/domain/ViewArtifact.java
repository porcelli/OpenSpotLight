package org.openspotlight.federation.domain;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;

@Name( "view" )
public class ViewArtifact extends TableArtifact {

    @Override
    public boolean contentEquals( final Artifact other ) {
        if (!(other instanceof ViewArtifact)) {
            return false;
        }
        final ViewArtifact that = (ViewArtifact)other;
        return Equals.eachEquality(this.getColumns(), that.getColumns());
    }

}
