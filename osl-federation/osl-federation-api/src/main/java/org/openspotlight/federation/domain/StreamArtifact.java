package org.openspotlight.federation.domain;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;

// TODO: Auto-generated Javadoc
/**
 * This is the {@link StreamArtifact} class 'on steroids'. It has a lot of {@link PathElement path elements} used to locate a new
 * {@link StreamArtifact} based on another one.
 */
@Name( "stream_artifact" )
public class StreamArtifact extends ArtifactWithSyntaxInformation {

    private static final long serialVersionUID = -8912205023568005794L;

    /** The content. */
    private String            content;

    @Override
    public boolean contentEquals( final Artifact other ) {
        if (other instanceof StreamArtifact) {
            final StreamArtifact that = (StreamArtifact)other;
            return Equals.eachEquality(this.content, that.content);
        }
        return false;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Sets the content.
     * 
     * @param content the new content
     */
    public void setContent( final String content ) {
        this.content = content;
    }

}
