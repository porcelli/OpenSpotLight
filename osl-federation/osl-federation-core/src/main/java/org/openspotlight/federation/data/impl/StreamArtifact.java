package org.openspotlight.federation.data.impl;

import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArraySet;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Compare;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.Sha1;

// TODO: Auto-generated Javadoc
/**
 * This is the {@link StreamArtifact} class 'on steroids'. It has a lot of {@link PathElement path elements} used to locate a new
 * {@link StreamArtifact} based on another one.
 */
public final class StreamArtifact {

    /**
     * The Enum ChangeType.
     */
    public static enum ChangeType {

        /** The N o_ change. */
        NOT_CHANGED,

        /** The INCLUDED. */
        INCLUDED,

        /** The EXCLUDED. */
        EXCLUDED,

        /** The CHANGED. */
        CHANGED
    }

    /**
     * The Interface LazyContentLoader.
     */
    public interface LazyContentLoader {

        /**
         * Load content.
         * 
         * @param hash the hash
         * @return the string
         */
        public String loadContent( String hash );

        /**
         * Load syntax informations.
         * 
         * @param hash the hash
         * @param artifact the artifact
         * @return the set< syntax information>
         */
        public Set<SyntaxInformation> loadSyntaxInformations( String hash,
                                                              StreamArtifact artifact );
    }

    /**
     * The Class PathElement.
     */
    public final static class PathElement implements Comparable<PathElement> {

        /**
         * Creates the from path string.
         * 
         * @param pathString the path string
         * @return the path element
         */
        static PathElement createFromPathString( final String pathString ) {
            Assertions.checkNotEmpty("pathString", pathString);
            final StringTokenizer tok = new StringTokenizer(pathString, "/");
            PathElement lastPath = new PathElement(tok.nextToken());
            while (tok.hasMoreTokens()) {
                lastPath = new PathElement(tok.nextToken(), lastPath);
            }
            return lastPath;

        }

        /** The name. */
        private final String      name;

        /** The hash. */
        private final String      hash;

        /** The parent. */
        private final PathElement parent;

        /** The hashcode. */
        private final int         hashcode;

        /**
         * Instantiates a new path element.
         * 
         * @param name the name
         */
        private PathElement(
                             final String name ) {
            Assertions.checkNotEmpty("name", name);
            this.name = name;
            this.hash = getHashFromString(name);
            this.parent = null;
            this.hashcode = HashCodes.hashOf(this.getCompletePath());
        }

        /**
         * Instantiates a new path element.
         * 
         * @param name the name
         * @param parent the parent
         */
        private PathElement(
                             final String name, final PathElement parent ) {
            Assertions.checkNotEmpty("name", name);
            Assertions.checkNotNull("parent", parent);
            this.name = name;
            this.hash = getHashFromString(name);
            this.parent = parent;
            this.hashcode = HashCodes.hashOf(this.getCompletePath());
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo( final PathElement o ) {
            return this.getCompletePath().compareTo(o.getCompletePath());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( final Object o ) {
            if (!(o instanceof PathElement)) {
                return false;
            }
            final PathElement that = (PathElement)o;
            final boolean response = Equals.eachEquality(this.getCompletePath(), that.getCompletePath());
            return response;
        }

        /**
         * Gets the complete path.
         * 
         * @return the complete path
         */
        public String getCompletePath() {
            if (this.isRootElement()) {
                return this.getName();
            }
            return this.getParent().getCompletePath() + SEPARATOR + this.getName();
        }

        /**
         * Gets the hash.
         * 
         * @return the hash
         */
        public String getHash() {
            return this.hash;
        }

        /**
         * Gets the name.
         * 
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the parent.
         * 
         * @return the parent
         */
        public PathElement getParent() {
            return this.parent;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return this.hashcode;
        }

        /**
         * Checks if is root element.
         * 
         * @return true, if is root element
         */
        public boolean isRootElement() {
            return this.parent == null;
        }

    }

    /**
     * The Class SyntaxInformation.
     */
    public static final class SyntaxInformation implements Comparable<SyntaxInformation> {

        /**
         * Enum to guard syntax information type.
         * 
         * @author Luiz Fernando Teston - feu.teston@caravelatech.com
         */
        public static enum SyntaxInformationType {

            /** Comment or multi line comment. */
            COMMENT,

            /** Reserved keyword. */
            RESERVED,

            /** Number literal. */
            NUMBER_LITERAL,

            /** String literal. */
            STRING_LITERAL,

            /** Variable identifier. */
            IDENTIFIER,

            /** Symbol, such as +, -, /, ... */
            SYMBOL,

            /** Hidden information on source code, such as form information on VB code. */
            HIDDEN
        }

        /** The hashcode. */
        private final int                   hashcode;

        /** The stream artifact. */
        private final StreamArtifact        streamArtifact;

        /** The line start. */
        private final int                   lineStart;

        /** The line end. */
        private final int                   lineEnd;

        /** The column start. */
        private final int                   columnStart;

        /** The column end. */
        private final int                   columnEnd;

        /** The type. */
        private final SyntaxInformationType type;

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
        @SuppressWarnings( "boxing" )
        public SyntaxInformation(
                                  final StreamArtifact streamArtifact, final int lineStart, final int lineEnd,
                                  final int columnStart, final int columnEnd, final SyntaxInformationType type ) {
            Assertions.checkNotNull("streamArtifact", streamArtifact);
            Assertions.checkCondition("lineStartPositive", lineStart >= 0);
            Assertions.checkCondition("lineEndPositive", lineEnd >= 0);
            Assertions.checkCondition("columnStartPositive", columnStart >= 0);
            Assertions.checkCondition("columnEndPositive", columnEnd >= 0);
            Assertions.checkNotNull("type", type);
            this.streamArtifact = streamArtifact;
            this.lineStart = lineStart;
            this.lineEnd = lineEnd;
            this.columnStart = columnStart;
            this.columnEnd = columnEnd;
            this.type = type;
            this.hashcode = HashCodes.hashOf(this.streamArtifact, this.lineStart, this.lineEnd, this.columnStart, this.columnEnd,
                                             this.type);
        }

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
            final boolean result = Equals.eachEquality(Arrays.of(this.streamArtifact, this.lineStart, this.lineEnd,
                                                                 this.columnStart, this.columnEnd, this.type),
                                                       Arrays.andOf(that.streamArtifact, that.lineStart, that.lineEnd,
                                                                    that.columnStart, that.columnEnd, that.type));
            return result;
        }

        /**
         * Gets the column end.
         * 
         * @return the column end
         */
        public int getColumnEnd() {
            return this.columnEnd;
        }

        /**
         * Gets the column start.
         * 
         * @return the column start
         */
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
        public int getLineEnd() {
            return this.lineEnd;
        }

        /**
         * Gets the line start.
         * 
         * @return the line start
         */
        public int getLineStart() {
            return this.lineStart;
        }

        /**
         * Gets the stream artifact.
         * 
         * @return the stream artifact
         */
        public StreamArtifact getStreamArtifact() {
            return this.streamArtifact;
        }

        /**
         * Gets the type.
         * 
         * @return the type
         */
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

    }

    /** The Constant SEPARATOR. */
    private static final String SEPARATOR = "/";

    /**
     * Creates the new stream artifact.
     * 
     * @param artifactCompletePath the artifact complete path
     * @param changeType the change type
     * @param lazyContentLoader the lazy content loader
     * @return the stream artifact
     */
    public static StreamArtifact createNewStreamArtifact( final String artifactCompletePath,
                                                          final ChangeType changeType,
                                                          final LazyContentLoader lazyContentLoader ) {
        final String internalArtifactName = artifactCompletePath.substring(0, artifactCompletePath.indexOf('/'));
        final String path = artifactCompletePath.substring(0, artifactCompletePath.length() - internalArtifactName.length());
        final PathElement pathElement = PathElement.createFromPathString(path);
        final StreamArtifact streamArtifact = new StreamArtifact(pathElement, internalArtifactName, changeType, lazyContentLoader);
        return streamArtifact;
    }

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

        final String internalArtifactName = artifactCompletePath.substring(0, artifactCompletePath.indexOf('/'));
        final String path = artifactCompletePath.substring(0, artifactCompletePath.length() - internalArtifactName.length());
        final PathElement pathElement = PathElement.createFromPathString(path);
        final StreamArtifact streamArtifact = new StreamArtifact(pathElement, internalArtifactName, changeType, content);
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
    private final String                 artifactName;

    /** The artifact complete name. */
    private final String                 artifactCompleteName;

    /** The hash. */
    private final String                 hash;

    /** The change type. */
    private final ChangeType             changeType;

    /** The parent. */
    private final PathElement            parent;

    /** The content. */
    private String                       content;

    /** The hashcode. */
    private final int                    hashcode;

    /** The lazy content loader. */
    private final LazyContentLoader      lazyContentLoader;

    /** The syntax information set. */
    private final Set<SyntaxInformation> syntaxInformationSet;

    /** The immutable syntax information set. */
    private final Set<SyntaxInformation> immutableSyntaxInformationSet;

    /**
     * Instantiates a new stream artifact.
     * 
     * @param parent the parent
     * @param artifactName the artifact name
     * @param changeType the change type
     * @param lazyContentLoader the lazy content loader
     */
    private StreamArtifact(
                            final PathElement parent, final String artifactName, final ChangeType changeType,
                            final LazyContentLoader lazyContentLoader ) {
        Assertions.checkNotNull("parent", parent);
        Assertions.checkNotEmpty("artifactName", "artifactName");
        Assertions.checkNotNull("changeType", changeType);
        Assertions.checkNotNull("lazyContentLoader", lazyContentLoader);
        this.artifactName = artifactName;
        this.parent = parent;
        this.lazyContentLoader = lazyContentLoader;
        this.changeType = changeType;
        this.hash = getHashFromString(artifactName);
        this.artifactCompleteName = parent.getCompletePath() + SEPARATOR + artifactName;
        this.content = null;
        this.hashcode = HashCodes.hashOf(this.getArtifactCompleteName(), this.changeType);
        this.syntaxInformationSet = new CopyOnWriteArraySet<SyntaxInformation>();
        this.immutableSyntaxInformationSet = Collections.unmodifiableSet(this.syntaxInformationSet);
        this.syntaxInformationSet.addAll(this.lazyContentLoader.loadSyntaxInformations(this.hash, this));
    }

    /**
     * Instantiates a new stream artifact.
     * 
     * @param parent the parent
     * @param artifactName the artifact name
     * @param changeType the change type
     * @param content the content
     */
    private StreamArtifact(
                            final PathElement parent, final String artifactName, final ChangeType changeType, final String content ) {
        Assertions.checkNotNull("parent", parent);
        Assertions.checkNotEmpty("artifactName", "artifactName");
        Assertions.checkNotNull("changeType", changeType);
        Assertions.checkNotNull("content", "content");
        this.artifactName = artifactName;
        this.hash = getHashFromString(artifactName);
        this.artifactCompleteName = parent.getCompletePath() + SEPARATOR + artifactName;
        this.parent = parent;
        this.content = content;
        this.lazyContentLoader = null;
        this.changeType = changeType;
        this.hashcode = HashCodes.hashOf(this.getArtifactCompleteName(), this.changeType);
        this.syntaxInformationSet = new CopyOnWriteArraySet<SyntaxInformation>();
        this.immutableSyntaxInformationSet = Collections.unmodifiableSet(this.syntaxInformationSet);
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
                                      final SyntaxInformation.SyntaxInformationType type ) {
        this.syntaxInformationSet.add(new SyntaxInformation(this, lineStart, lineEnd, columnStart, columnEnd, type));
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
        final boolean result = Equals.eachEquality(Arrays.of(this.getArtifactCompleteName(), this.getChangeType()),
                                                   Arrays.andOf(that.getArtifactCompleteName(), that.getChangeType()));
        return result;
    }

    /**
     * Gets the artifact complete name.
     * 
     * @return the artifact complete name
     */
    public String getArtifactCompleteName() {
        return this.artifactCompleteName;
    }

    /**
     * Gets the artifact name.
     * 
     * @return the artifact name
     */
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
        if (this.content == null) {
            this.content = this.lazyContentLoader.loadContent(this.getHash());
        }
        return this.content;
    }

    /**
     * Gets the hash.
     * 
     * @return the hash
     */
    public String getHash() {
        return this.hash;
    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    public PathElement getParent() {
        return this.parent;
    }

    /**
     * Gets the syntax information set.
     * 
     * @return the syntax information set
     */
    public Set<SyntaxInformation> getSyntaxInformationSet() {
        return this.immutableSyntaxInformationSet;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.hashcode;
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
                                         final SyntaxInformation.SyntaxInformationType type ) {
        this.syntaxInformationSet.remove(new SyntaxInformation(this, lineStart, lineEnd, columnStart, columnEnd, type));
    }

    /**
     * Removes the syntax information.
     * 
     * @param syntaxInformation the syntax information
     */
    public void removeSyntaxInformation( final SyntaxInformation syntaxInformation ) {
        this.syntaxInformationSet.remove(syntaxInformation);
    }

}
