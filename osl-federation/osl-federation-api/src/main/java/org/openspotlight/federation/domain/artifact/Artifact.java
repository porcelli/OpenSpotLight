/*
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

import java.io.Serializable;
import java.util.Date;

import org.openspotlight.common.collection.AddOnlyConcurrentMap;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.log.LogableObject;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc
/**
 * This is the {@link Artifact} class 'on steroids'. It has a lot of
 * {@link PathElement path elements} used to locate a new {@link Artifact} based
 * on another one. Please register any non-abstract implementation of Artifact
 * subclass on {@link ArtifactTypeRegistry}, so the bundle processor manager
 * should load this classes.
 */
public abstract class Artifact implements SimpleNodeType, Serializable,
		LogableObject {

	private String originalName;

	public void updateOriginalName(ArtifactSource source, String originalName) {
		this.originalName = source + ":" + originalName;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	private long lastChange;

	public long getLastChange() {
		return lastChange;
	}

	public void setLastChange(long lastChange) {
		this.lastChange = lastChange;
	}

	/**
	 * Creates the new artifact.
	 * 
	 * @param artifactCompletePath
	 *            the artifact complete path
	 * @param changeType
	 *            the change type
	 * @param artifactType
	 *            the artifact type
	 * @return the stream artifact
	 */
	public static <A extends Artifact> A createArtifact(
			final Class<A> artifactType, final String artifactCompletePath,
			final ChangeType changeType) {

		try {
			final String internalArtifactName = artifactCompletePath
					.substring(artifactCompletePath.lastIndexOf('/') + 1);
			final String path = artifactCompletePath.substring(0,
					artifactCompletePath.length()
							- internalArtifactName.length());
			final PathElement pathElement = PathElement
					.createFromPathString(path);
			final A artifact = artifactType.newInstance();

			artifact.setArtifactName(internalArtifactName);
			artifact.setChangeType(changeType);
			artifact.setParent(pathElement);
			return artifact;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	private String uniqueContextName;

	/** The Constant SEPARATOR. */
	final static String SEPARATOR = "/";

	private AddOnlyConcurrentMap<String, Object> transientMap;

	private String repositoryName;

	private static final long serialVersionUID = 372692540369995072L;

	private LastProcessStatus lastProcessStatus = LastProcessStatus.NOT_PROCESSED_YET;

	private Date lastProcessedDate;

	/** The artifact name. */
	private String artifactName;

	/** The artifact complete name. */
	private volatile transient String artifactCompleteName;

	/** The change type. */
	private ChangeType changeType = ChangeType.INCLUDED;

	/** The parent. */
	private transient PathElement parent;

	/** The hashcode. */
	private volatile transient int hashcode;

	public Artifact() {
		transientMap = AddOnlyConcurrentMap.newMap();
	}

	/**
	 * Content equals.
	 * 
	 * @param other
	 *            the other
	 * @return true, if successful
	 */
	public abstract boolean contentEquals(Artifact other);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Artifact)) {
			return false;
		}
		if (o.getClass() != this.getClass()) {
			return false;
		}

		final Artifact that = (Artifact) o;
		return Equals.eachEquality(parent, that.parent)
				&& Equals.eachEquality(artifactName, that.artifactName);
	}

	/**
	 * Gets the artifact complete name.
	 * 
	 * @return the artifact complete name
	 */
	public String getArtifactCompleteName() {
		String result = artifactCompleteName;
		if (result == null) {
			if (parent != null && artifactName != null) {
				result = parent.getCompletePath() + SEPARATOR + artifactName;
				artifactCompleteName = result;
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
		return artifactName;
	}

	/**
	 * Gets the change type.
	 * 
	 * @return the change type
	 */
	public ChangeType getChangeType() {
		return changeType;
	}

	public Date getLastProcessedDate() {
		return lastProcessedDate;
	}

	public LastProcessStatus getLastProcessStatus() {
		return lastProcessStatus;
	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	@ParentProperty
	public PathElement getParent() {
		return parent;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	@TransientProperty
	public AddOnlyConcurrentMap<String, Object> getTransientMap() {
		return transientMap;
	}

	public String getUniqueContextName() {
		return uniqueContextName;
	}

	@TransientProperty
	public String getVersion() {
		return "1";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
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

	public void setArtifactCompleteName(final String artifactCompleteName) {
		this.artifactCompleteName = artifactCompleteName;
	}

	/**
	 * Sets the artifact name.
	 * 
	 * @param artifactName
	 *            the new artifact name
	 */
	public void setArtifactName(final String artifactName) {
		this.artifactName = artifactName;
	}

	/**
	 * Sets the change type.
	 * 
	 * @param changeType
	 *            the new change type
	 */
	public void setChangeType(final ChangeType changeType) {
		this.changeType = changeType;
	}

	public void setLastProcessedDate(final Date lastProcessedDate) {
		this.lastProcessedDate = lastProcessedDate;
	}

	public void setLastProcessStatus(final LastProcessStatus lastProcessStatus) {
		this.lastProcessStatus = lastProcessStatus;
	}

	/**
	 * Sets the parent.
	 * 
	 * @param parent
	 *            the new parent
	 */
	public void setParent(final PathElement parent) {
		this.parent = parent;
		artifactCompleteName = null;
	}

	public void setRepositoryName(final String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public void setTransientMap(
			final AddOnlyConcurrentMap<String, Object> transientMap) {
		this.transientMap = transientMap;
	}

	public void setUniqueContextName(final String uniqueContextName) {
		this.uniqueContextName = uniqueContextName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getClass().getSimpleName() + getArtifactCompleteName() + " "
				+ getChangeType();
	}

}
