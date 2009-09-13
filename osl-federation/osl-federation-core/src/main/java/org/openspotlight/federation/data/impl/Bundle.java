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

package org.openspotlight.federation.data.impl;

import static java.util.Collections.unmodifiableSet;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * A bundle is a group of artifact sources such as source folders, database
 * tables and so on. The bundle should group similar artifacts (example: java
 * files).
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings("unchecked")
@ThreadSafe
@StaticMetadata(propertyNames = { "active", "initialLookup" }, propertyTypes = {
		Boolean.class, String.class }, keyPropertyName = "name", keyPropertyType = String.class, validParentTypes = { Group.class }, validChildrenTypes = {
		BundleProcessorType.class, StreamArtifact.class, CustomArtifact.class,
		ArtifactMapping.class })
public class Bundle implements ConfigurationNode {

	/** The Constant ACTIVE. */
	private static final String ACTIVE = "active"; //$NON-NLS-1$

	/** The Constant INITIAL_LOOKUP. */
	private static final String INITIAL_LOOKUP = "initialLookup"; //$NON-NLS-1$

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1092283780730455977L;

	/** The instance metadata. */
	private final InstanceMetadata instanceMetadata;

	/**
	 * creates a bundle inside this project.
	 * 
	 * @param project
	 *            the project
	 * @param name
	 *            the name
	 */
	public Bundle(final Group project, final String name) {
		this.instanceMetadata = createWithKeyProperty(this, project, name);
		checkCondition("noBundle", //$NON-NLS-1$
				project.getBundleByName(name) == null);
		project.getInstanceMetadata().addChild(this);

	}

	/**
	 * Adds a new {@link StreamArtifact} to this bundle if there's no
	 * {@link StreamArtifact} with the artifactName passed as a param. If
	 * there's any {@link StreamArtifact} with this artifactName, this method
	 * just returns the existing one.
	 * 
	 * @param artifactName
	 *            the artifact name
	 * 
	 * @return a stream artifact
	 */
	public StreamArtifact addStreamArtifact(final String artifactName) {
		final StreamArtifact streamArtifact = this
				.getStreamArtifactByName(artifactName);
		if (streamArtifact != null) {
			return streamArtifact;
		}
		return new StreamArtifact(this, artifactName);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public final int compareTo(final ConfigurationNode o) {
		return this.instanceMetadata.compare(this, o);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object obj) {
		return this.instanceMetadata.equals(obj);
	}

	/**
	 * Gets the active.
	 * 
	 * @return active property
	 */
	public final Boolean getActive() {
		return this.instanceMetadata.getProperty(ACTIVE);
	}

	/**
	 * To process this bundle, the processor type classes should be configured.
	 * This method groups all class names on a set and return this names.
	 * 
	 * @return all processor types names
	 */
	@SuppressWarnings("boxing")
	public Set<String> getAllProcessorTypeNames() {
		final Collection<BundleProcessorType> allProcessorTypes = this.instanceMetadata
				.getChildrensOfType(BundleProcessorType.class);
		final Set<String> allTypeNames = new HashSet<String>();
		for (final BundleProcessorType type : allProcessorTypes) {
			if (type.getActive()) {
				allTypeNames.add(type.getName());
			}
		}
		return unmodifiableSet(allTypeNames);
	}

	/**
	 * Returns a artifact mapping by its name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return an artifact mapping
	 */
	public final ArtifactMapping getArtifactMappingByName(final String name) {
		return this.instanceMetadata.getChildByKeyValue(ArtifactMapping.class,
				name);
	}

	/**
	 * Gets the artifact mapping names.
	 * 
	 * @return all artifact mapping names
	 */
	public final Set<String> getArtifactMappingNames() {
		return (Set<String>) this.instanceMetadata
				.getKeyFromChildrenOfTypes(ArtifactMapping.class);
	}

	/**
	 * Gets the artifact mappings.
	 * 
	 * @return all artifact mappings
	 */
	public final Collection<ArtifactMapping> getArtifactMappings() {
		return this.instanceMetadata.getChildrensOfType(ArtifactMapping.class);
	}

	/**
	 * Returns a bundle by its name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return a bundle
	 */
	public final Bundle getBundleByName(final String name) {
		return this.instanceMetadata.getChildByKeyValue(Bundle.class, name);
	}

	/**
	 * Gets the bundle names.
	 * 
	 * @return all bundle names
	 */
	public final Set<String> getBundleNames() {
		return (Set<String>) this.instanceMetadata
				.getKeyFromChildrenOfTypes(Bundle.class);
	}

	/**
	 * Gets the bundles.
	 * 
	 * @return all bundles
	 */
	public final Collection<Bundle> getBundles() {
		return this.instanceMetadata.getChildrensOfType(Bundle.class);
	}

	/**
	 * Returns a custom artifact by its name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return a custom artifact
	 */
	public final CustomArtifact getCustomArtifactByName(final String name) {
		return this.instanceMetadata.getChildByKeyValue(CustomArtifact.class,
				name);
	}

	/**
	 * Gets the custom artifact names.
	 * 
	 * @return all custom artifact names
	 */
	public final Set<String> getCustomArtifactNames() {
		return (Set<String>) this.instanceMetadata
				.getKeyFromChildrenOfTypes(CustomArtifact.class);
	}

	/**
	 * Gets the custom artifacts.
	 * 
	 * @return all custom artifacts
	 */
	public final Collection<CustomArtifact> getCustomArtifacts() {
		return this.instanceMetadata.getChildrensOfType(CustomArtifact.class);
	}

	/**
	 * Gets the group.
	 * 
	 * @return the group
	 */
	public Group getGroup() {
		return this.instanceMetadata.getParent(Group.class);
	}

	/**
	 * Gets the initial lookup.
	 * 
	 * @return the initial lookup property.
	 */
	public final String getInitialLookup() {
		return this.instanceMetadata.getProperty(INITIAL_LOOKUP);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public final InstanceMetadata getInstanceMetadata() {
		return this.instanceMetadata;
	}

	/**
	 * The name, in this case, is a unique identifier (with parent node) to this
	 * node.
	 * 
	 * @return the node name
	 */
	public String getName() {
		return (String) this.instanceMetadata.getKeyPropertyValue();
	}

	/**
	 * Gets the project.
	 * 
	 * @return the parent project
	 */
	public Group getProject() {
		return (Group) this.instanceMetadata.getDefaultParent();
	}

	/**
	 * Returns the repository if this node has one, or the parent's project
	 * repository instead.
	 * 
	 * @return a repository
	 */
	public final Repository getRepository() {
		final ConfigurationNode parent = this.instanceMetadata
				.getDefaultParent();
		if (parent instanceof Repository) {
			return (Repository) parent;
		} else if (parent instanceof Group) {
			final Group proj = (Group) parent;
			return proj.getRepository();
		}
		return null;
	}

	/**
	 * Gets the root group.
	 * 
	 * @return the root group
	 */
	public Group getRootGroup() {
		Group parentGroup = this.getGroup();
		boolean found = (this.getGroup().getGraphRoot() != null)
				&& this.getGroup().getGraphRoot().booleanValue();
		while (!found) {
			found = (this.getGroup().getGraphRoot() != null)
					&& this.getGroup().getGraphRoot().booleanValue();

			final ConfigurationNode defaultParent = parentGroup
					.getInstanceMetadata().getDefaultParent();
			if (!(defaultParent instanceof Group)) {
				throw logAndReturn(new IllegalStateException(
						"Parent group for this bundle was not found"));
			}
			parentGroup = Group.class.cast(defaultParent);
		}
		return parentGroup;

	}

	/**
	 * Returns an artifact by its name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return an artifact
	 */
	public final StreamArtifact getStreamArtifactByName(final String name) {
		return this.instanceMetadata.getChildByKeyValue(StreamArtifact.class,
				name);
	}

	/**
	 * Gets the stream artifact names.
	 * 
	 * @return all artifact names
	 */
	public final Set<String> getStreamArtifactNames() {
		return (Set<String>) this.instanceMetadata
				.getKeyFromChildrenOfTypes(StreamArtifact.class);
	}

	/**
	 * Gets the stream artifacts.
	 * 
	 * @return all artifacts
	 */
	public final Collection<StreamArtifact> getStreamArtifacts() {
		return this.instanceMetadata.getChildrensOfType(StreamArtifact.class);
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.instanceMetadata.hashCode();
	}

	/**
	 * Removes a artifact mapping.
	 * 
	 * @param ArtifactMapping
	 *            the artifact mapping
	 */
	public final void removeArtifactMapping(
			final ArtifactMapping ArtifactMapping) {
		this.instanceMetadata.removeChild(ArtifactMapping);
	}

	/**
	 * removes a bundle.
	 * 
	 * @param bundle
	 *            the bundle
	 */
	public final void removeBundle(final Bundle bundle) {
		this.instanceMetadata.removeChild(bundle);
	}

	/**
	 * Removes a Custom Artifact.
	 * 
	 * @param CustomArtifact
	 *            the custom artifact
	 */
	public final void removeCustomArtifact(final CustomArtifact CustomArtifact) {
		this.instanceMetadata.removeChild(CustomArtifact);
	}

	/**
	 * Removes a project.
	 * 
	 * @param group
	 *            the group
	 */
	public final void removeProject(final Group group) {
		this.instanceMetadata.removeChild(group);
	}

	/**
	 * Removes an artifact.
	 * 
	 * @param Artifact
	 *            the artifact
	 */
	public final void removeStreamArtifact(final StreamArtifact Artifact) {
		this.instanceMetadata.removeChild(Artifact);
	}

	/**
	 * Sets the active property.
	 * 
	 * @param active
	 *            the active
	 */
	public final void setActive(final Boolean active) {
		this.instanceMetadata.setProperty(ACTIVE, active);
	}

	/**
	 * Sets the initial lookup property.
	 * 
	 * @param initialLookup
	 *            the initial lookup
	 */
	public final void setInitialLookup(final String initialLookup) {
		this.instanceMetadata.setProperty(INITIAL_LOOKUP, initialLookup);
	}

}
