package org.openspotlight.federation.domain;

import java.util.Set;

import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.Name;

// TODO: Auto-generated Javadoc
/**
 * This is the {@link StreamArtifact} class 'on steroids'. It has a lot of {@link PathElement path elements} used to locate a new
 * {@link StreamArtifact} based on another one.
 */
@Name( "stream_artifact" )
public class StreamArtifact extends Artifact {

    /** The content. */
    private String                 content;

    /** The syntax information set. */
    private Set<SyntaxInformation> syntaxInformationSet;

    /**
     * Adds the syntax information.
     * 
     * @param lineStart the line start
     * @param lineEnd the line end
     * @param columnStart the column start
     * @param columnEnd the column end
     * @param type the type
     */
    public void addSyntaxInformation( final int lineStart,
                                      final int lineEnd,
                                      final int columnStart,
                                      final int columnEnd,
                                      final SyntaxInformationType type ) {
        final SyntaxInformation syntaxInformation = new SyntaxInformation();
        syntaxInformation.setColumnEnd(columnEnd);
        syntaxInformation.setColumnStart(columnStart);
        syntaxInformation.setLineEnd(lineEnd);
        syntaxInformation.setLineStart(lineStart);
        syntaxInformation.setStreamArtifact(this);
        syntaxInformation.setType(type);
        this.syntaxInformationSet.add(syntaxInformation);
    }

    /**
     * Clear syntax information set.
     */
    public void clearSyntaxInformationSet() {
        this.syntaxInformationSet.clear();
    }

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
     * Gets the syntax information set.
     * 
     * @return the syntax information set
     */
    public Set<SyntaxInformation> getSyntaxInformationSet() {
        return this.syntaxInformationSet;
    }

    /**
     * Removes the syntax information.
     * 
     * @param lineStart the line start
     * @param lineEnd the line end
     * @param columnStart the column start
     * @param columnEnd the column end
     * @param type the type
     */
    public void removeSyntaxInformation( final int lineStart,
                                         final int lineEnd,
                                         final int columnStart,
                                         final int columnEnd,
                                         final SyntaxInformationType type ) {
        final SyntaxInformation syntaxInformation = new SyntaxInformation();
        syntaxInformation.setColumnEnd(columnEnd);
        syntaxInformation.setColumnStart(columnStart);
        syntaxInformation.setLineEnd(lineEnd);
        syntaxInformation.setLineStart(lineStart);
        syntaxInformation.setStreamArtifact(this);
        syntaxInformation.setType(type);
        this.syntaxInformationSet.remove(syntaxInformation);
    }

    /**
     * Removes the syntax information.
     * 
     * @param syntaxInformation the syntax information
     */
    public void removeSyntaxInformation( final SyntaxInformation syntaxInformation ) {
        this.syntaxInformationSet.remove(syntaxInformation);
    }

    /**
     * Sets the content.
     * 
     * @param content the new content
     */
    public void setContent( final String content ) {
        this.content = content;
    }

    /**
     * Sets the syntax information set.
     * 
     * @param syntaxInformationSet the new syntax information set
     */
    public void setSyntaxInformationSet( final Set<SyntaxInformation> syntaxInformationSet ) {
        this.syntaxInformationSet = syntaxInformationSet;
    }

}
