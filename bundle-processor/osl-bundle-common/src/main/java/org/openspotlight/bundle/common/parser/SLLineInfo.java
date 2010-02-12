package org.openspotlight.bundle.common.parser;


public interface SLLineInfo {

    public int getStartLine();

    public int getEndLine();

    public int getEndCharPositionInLine();

    public int getStartCharPositionInLine();

    public SLArtifactStream getArtifact();

}
