package org.openspotlight.federation.domain;

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Compare;
import org.openspotlight.common.util.Equals;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class SyntaxInformation.
 */
@Name( "syntax_information" )
public class SyntaxInformation implements Comparable<SyntaxInformation>, SimpleNodeType, Serializable {

    /** The hashcode. */
    private volatile int          hashcode;

    /** The stream artifact. */
    private Artifact        streamArtifact;

    /** The line start. */
    private int                   lineStart;

    /** The line end. */
    private int                   lineEnd;

    /** The column start. */
    private int                   columnStart;

    /** The column end. */
    private int                   columnEnd;

    /** The type. */
    private SyntaxInformationType type;

    /**
     * Instantiates a new syntax information.
     * 
     * @param streamArtifact the stream artifact
     * @param lineStart the line start
     * @param lineEnd the line end
     * @param columnStart the column start
     * @param columnEnd the column end
     * @param type the type
     */

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @SuppressWarnings( "boxing" )
    public int compareTo( final SyntaxInformation o ) {
        Compare.compareAll(Arrays.of(this.streamArtifact, this.lineStart, this.lineEnd, this.columnStart, this.columnEnd,
                                     this.type), Arrays.andOf(o.streamArtifact, o.lineStart, o.lineEnd, o.columnStart,
                                                              o.columnEnd, o.type));
        return 0;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings( "boxing" )
    @Override
    public boolean equals( final Object o ) {
        if (!(o instanceof SyntaxInformation)) {
            return false;
        }
        final SyntaxInformation that = (SyntaxInformation)o;
        final boolean result = Equals.eachEquality(Arrays.of(this.streamArtifact, this.lineStart, this.lineEnd, this.columnStart,
                                                             this.columnEnd, this.type), Arrays.andOf(that.streamArtifact,
                                                                                                      that.lineStart,
                                                                                                      that.lineEnd,
                                                                                                      that.columnStart,
                                                                                                      that.columnEnd, that.type));
        return result;
    }

    /**
     * Gets the column end.
     * 
     * @return the column end
     */
    @KeyProperty
    public int getColumnEnd() {
        return this.columnEnd;
    }

    /**
     * Gets the column start.
     * 
     * @return the column start
     */
    @KeyProperty
    public int getColumnStart() {
        return this.columnStart;
    }

    /**
     * Gets the hashcode.
     * 
     * @return the hashcode
     */
    public int getHashcode() {
        return this.hashcode;
    }

    /**
     * Gets the line end.
     * 
     * @return the line end
     */
    @KeyProperty
    public int getLineEnd() {
        return this.lineEnd;
    }

    /**
     * Gets the line start.
     * 
     * @return the line start
     */
    @KeyProperty
    public int getLineStart() {
        return this.lineStart;
    }

    /**
     * Gets the stream artifact.
     * 
     * @return the stream artifact
     */
    @ParentProperty
    public Artifact getStreamArtifact() {
        return this.streamArtifact;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @KeyProperty
    public SyntaxInformationType getType() {
        return this.type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.hashcode;
    }

    public void setColumnEnd( final int columnEnd ) {
        this.columnEnd = columnEnd;
    }

    public void setColumnStart( final int columnStart ) {
        this.columnStart = columnStart;
    }

    public void setLineEnd( final int lineEnd ) {
        this.lineEnd = lineEnd;
    }

    public void setLineStart( final int lineStart ) {
        this.lineStart = lineStart;
    }

    public void setStreamArtifact( final Artifact streamArtifact ) {
        this.streamArtifact = streamArtifact;
    }

    public void setType( final SyntaxInformationType type ) {
        this.type = type;
    }

}
