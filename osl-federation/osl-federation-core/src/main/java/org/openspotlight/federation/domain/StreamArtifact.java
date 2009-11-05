package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc
/**
 * This is the {@link StreamArtifact} class 'on steroids'. It has a lot of {@link PathElement path elements} used to locate a new
 * {@link StreamArtifact} based on another one.
 */
@Name( "stream_artifact" )
public class StreamArtifact implements SimpleNodeType, Serializable {

    /** The Constant SEPARATOR. */
    final static String SEPARATOR = "/";

    /**
     * Creates the new stream artifact.
     * 
     * @param artifactCompletePath the artifact complete path
     * @param changeType the change type
     * @param content the content
     * @return the stream artifact
     */
    public static StreamArtifact createNewStreamArtifact( final String artifactCompletePath,
                                                          final ChangeType changeType,
                                                          final String content ) {

        final String internalArtifactName = artifactCompletePath.substring(artifactCompletePath.lastIndexOf('/') + 1);
        final String path = artifactCompletePath.substring(0, artifactCompletePath.length() - internalArtifactName.length());
        final PathElement pathElement = PathElement.createFromPathString(path);
        final StreamArtifact streamArtifact = new StreamArtifact();
        streamArtifact.setArtifactName(internalArtifactName);
        streamArtifact.setChangeType(changeType);
        streamArtifact.setParent(pathElement);
        streamArtifact.setContent(content);
        return streamArtifact;

    }

    /**
     * Gets the hash from string.
     * 
     * @param name the name
     * @return the hash from string
     */
    public static String getHashFromString( final String name ) {
        try {
            return Sha1.getSha1SignatureEncodedAsHexa(name);
        } catch (final SLException e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /** The artifact name. */
    private String                 artifactName;

    /** The artifact complete name. */
    private volatile String        artifactCompleteName;

    /** The change type. */
    private ChangeType             changeType;

    /** The parent. */
    private PathElement            parent;

    /** The content. */
    private String                 content;

    /** The hashcode. */
    private volatile int           hashcode;

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
     * Adds the syntax information.
     * 
     * @param syntaxInformation the syntax information
     */
    public void addSyntaxInformation( final SyntaxInformation syntaxInformation ) {
        Assertions.checkNotNull("syntaxInformation", syntaxInformation);
        this.syntaxInformationSet.add(syntaxInformation);
    }

    /**
     * Clear syntax information set.
     */
    public void clearSyntaxInformationSet() {
        this.syntaxInformationSet.clear();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public boolean equals( final Object o ) {
        if (!(o instanceof StreamArtifact)) {
            return false;
        }
        final StreamArtifact that = (StreamArtifact)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.parent, this.artifactName, this.changeType),
                                                   Arrays.andOf(that.parent, that.artifactName, that.changeType));
        return result;
    }

    /**
     * Gets the artifact complete name.
     * 
     * @return the artifact complete name
     */
    @TransientProperty
    public String getArtifactCompleteName() {
        String result = this.artifactCompleteName;
        if (result == null) {
            if (this.parent != null && this.artifactName != null) {
                result = this.parent.getCompletePath() + SEPARATOR + this.artifactName;
                this.artifactCompleteName = result;
            }
        }
        return result;
    }

    /**
     * Gets the artifact name.
     * 
     * @return the artifact name
     */
    @KeyProperty
    public String getArtifactName() {
        return this.artifactName;
    }

    /**
     * Gets the change type.
     * 
     * @return the change type
     */
    public ChangeType getChangeType() {
        return this.changeType;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    public synchronized String getContent() {
        return this.content;
    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    @ParentProperty
    public PathElement getParent() {
        return this.parent;
    }

    /**
     * Gets the syntax information set.
     * 
     * @return the syntax information set
     */
    public Set<SyntaxInformation> getSyntaxInformationSet() {
        return this.syntaxInformationSet;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = this.hashcode;
        if (result == 0) {
            result = HashCodes.hashOf(this.parent, this.artifactName, this.changeType);
            this.hashcode = result;
        }
        return result;
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
     * Sets the artifact name.
     * 
     * @param artifactName the new artifact name
     */
    public void setArtifactName( final String artifactName ) {
        this.artifactName = artifactName;
    }

    /**
     * Sets the change type.
     * 
     * @param changeType the new change type
     */
    public void setChangeType( final ChangeType changeType ) {
        this.changeType = changeType;
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
     * Sets the parent.
     * 
     * @param parent the new parent
     */
    public void setParent( final PathElement parent ) {
        this.parent = parent;
    }

    /**
     * Sets the syntax information set.
     * 
     * @param syntaxInformationSet the new syntax information set
     */
    public void setSyntaxInformationSet( final Set<SyntaxInformation> syntaxInformationSet ) {
        this.syntaxInformationSet = syntaxInformationSet;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "StreamArtifact: " + this.getArtifactCompleteName();
    }

}
