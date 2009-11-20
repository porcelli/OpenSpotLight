package org.openspotlight.federation.domain;

import java.util.HashSet;
import java.util.Set;

public abstract class ArtifactWithSyntaxInformation extends Artifact {

    /** The syntax information set. */
    private Set<SyntaxInformation> syntaxInformationSet = new HashSet<SyntaxInformation>();

    public ArtifactWithSyntaxInformation() {
        super();
    }

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
     * Sets the syntax information set.
     * 
     * @param syntaxInformationSet the new syntax information set
     */
    public void setSyntaxInformationSet( final Set<SyntaxInformation> syntaxInformationSet ) {
        this.syntaxInformationSet = syntaxInformationSet;
    }

}
