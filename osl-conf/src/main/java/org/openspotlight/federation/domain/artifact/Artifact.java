/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.federation.domain.artifact;

import static org.openspotlight.common.util.Strings.concatPaths;

import java.io.Serializable;
import java.util.Date;

import org.openspotlight.common.collection.AddOnlyConcurrentMap;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.persist.annotation.IndexedProperty;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.PersistPropertyAsStream;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc

/**
 * This is the {@link Artifact} class 'on steroids'. It has a lot of {@link PathElement path elements} used to locate a new
 * {@link Artifact} based on another one. Please register any non-abstract implementation of Artifact subclass on , so the bundle
 * processor manager should load this classes.
 */
public abstract class Artifact implements SimpleNodeType, Serializable {
    private static final long                              serialVersionUID = 372692540369995072L;

    /**
     * The Constant SEPARATOR.
     */
    final static String                                    SEPARATOR        = "/";

    /**
     * The artifact complete name.
     */
    private String                                         artifactCompleteName;

    /**
     * The artifact name.
     */
    private String                                         artifactName;

    /**
     * The change type.
     */
    private ChangeType                                     changeType       = ChangeType.INCLUDED;
    /**
     * The hashcode.
     */
    private volatile transient int                         hashcode;

    private long                                           lastChange;

    private Date                                           lastProcessedDate;

    private String                                         mappedFrom;

    private String                                         mappedTo;

    private String                                         originalName;

    /**
     * The parent.
     */
    private PathElement                                    parent;

    private String                                         repositoryName;

    private transient AddOnlyConcurrentMap<String, Object> transientMap;

    private String                                         uniqueContextName;

    public Artifact() {
        transientMap = AddOnlyConcurrentMap.newMap();
    }

    /**
     * Creates the new artifact.
     * 
     * @param artifactCompletePath the artifact complete path
     * @param changeType the change type
     * @param artifactType the artifact type
     * @return the stream artifact
     */
    public static <A extends Artifact> A createArtifact(
                                                        final Class<A> artifactType, final String artifactCompletePath,
                                                        final ChangeType changeType) {

        try {
            final String internalArtifactName = artifactCompletePath
                    .substring(artifactCompletePath.lastIndexOf('/') + 1);
            final String path = artifactCompletePath.substring(
                    0,
                    artifactCompletePath.length()
                            - internalArtifactName.length());
            final PathElement pathElement = PathElement
                    .createFromPathString(path);
            final A artifact = artifactType.newInstance();

            artifact.setArtifactName(internalArtifactName);
            artifact.setChangeType(changeType);
            artifact.setParent(pathElement);
            if (pathElement != null) {
                artifact.setArtifactCompleteName(Strings.concatPaths(
                        pathElement.getCompletePath(), internalArtifactName));

            } else {
                artifact.setArtifactCompleteName(internalArtifactName);

            }

            return artifact;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    /**
     * Content equalsTo.
     * 
     * @param other the other
     * @return true, if successful
     */
    public abstract boolean contentEquals(Artifact other);

    @Override
    public synchronized boolean equals(final Object o) {
        if (!(o instanceof Artifact)) { return false; }
        if (o.getClass() != this.getClass()) { return false; }

        final Artifact that = (Artifact) o;
        return Equals.eachEquality(parent, that.parent)
                && Equals.eachEquality(artifactName, that.artifactName);
    }

    /**
     * Gets the artifact complete name.
     * 
     * @return the artifact complete name
     */
    @KeyProperty
    public synchronized String getArtifactCompleteName() {
        return artifactCompleteName;
    }

    /**
     * Gets the artifact name.
     * 
     * @return the artifact name
     */
    @KeyProperty
    public synchronized String getArtifactName() {
        return artifactName;
    }

    /**
     * Gets the change type.
     * 
     * @return the change type
     */
    public synchronized ChangeType getChangeType() {
        return changeType;
    }

    public synchronized long getLastChange() {
        return lastChange;
    }

    public synchronized Date getLastProcessedDate() {
        return lastProcessedDate;
    }

    @IndexedProperty
    public synchronized String getMappedFrom() {
        return mappedFrom;
    }

    @IndexedProperty
    public synchronized String getMappedTo() {
        return mappedTo;
    }

    @IndexedProperty
    public synchronized String getOriginalName() {
        return originalName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    @PersistPropertyAsStream
    public synchronized PathElement getParent() {
        return parent;
    }

    public synchronized String getRepositoryName() {
        return repositoryName;
    }

    @TransientProperty
    public synchronized AddOnlyConcurrentMap<String, Object> getTransientMap() {
        return transientMap;
    }

    public synchronized String getUniqueContextName() {
        return uniqueContextName;
    }

    @TransientProperty
    public synchronized String getVersion() {
        return "1";
    }

    @Override
    public synchronized int hashCode() {
        int result = hashcode;
        if (result == 0) {
            result = 17;
            result = 31 * result + this.getClass().hashCode();
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            result = 31 * result
                    + (artifactName != null ? artifactName.hashCode() : 0);
            result = 31 * result
                    + (artifactName != null ? artifactName.hashCode() : 0);
            result = 31 * result
                    + (changeType != null ? changeType.hashCode() : 0);
            hashcode = result;
        }
        return result;
    }

    public synchronized void setArtifactCompleteName(
                                                     final String artifactCompleteName) {
        if (artifactCompleteName == null) { throw new NullPointerException(); }
        this.artifactCompleteName = artifactCompleteName;
    }

    /**
     * Sets the artifact name.
     * 
     * @param artifactName the new artifact name
     */
    public synchronized void setArtifactName(final String artifactName) {
        this.artifactName = artifactName;
    }

    /**
     * Sets the change type.
     * 
     * @param changeType the new change type
     */
    public synchronized void setChangeType(final ChangeType changeType) {
        this.changeType = changeType;

    }

    public synchronized void setLastChange(final long lastChange) {
        this.lastChange = lastChange;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    public synchronized void setLastProcessedDate(final Date lastProcessedDate) {
        this.lastProcessedDate = lastProcessedDate;
    }

    public synchronized void setMappedFrom(final String mappedFrom) {
        this.mappedFrom = mappedFrom;
    }

    public synchronized void setMappedTo(final String mappedTo) {
        this.mappedTo = mappedTo;
    }

    public synchronized void setOriginalName(final String originalName) {
        this.originalName = originalName;
    }

    /**
     * Sets the parent.
     * 
     * @param parent the new parent
     */
    public synchronized void setParent(final PathElement parent) {
        this.parent = parent;
        if (parent != null) {
            setArtifactCompleteName(Strings.concatPaths(
                    parent.getCompletePath(), artifactName));
        } else {
            setArtifactCompleteName(artifactName);

        }

    }

    public synchronized void setRepositoryName(final String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public synchronized void setTransientMap(
                                             final AddOnlyConcurrentMap<String, Object> transientMap) {
        this.transientMap = transientMap;
    }

    public synchronized void setUniqueContextName(final String uniqueContextName) {
        this.uniqueContextName = uniqueContextName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + getArtifactCompleteName() + " "
                + getChangeType();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */

    public void updateOriginalName(final ArtifactSource source, final String originalName) {
        this.originalName = concatPaths(source.getInitialLookup(), originalName);

    }

}
