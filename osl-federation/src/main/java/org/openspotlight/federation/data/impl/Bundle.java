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

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;
import static org.openspotlight.federation.data.StaticMetadata.Factory.createImmutable;
import static org.openspotlight.federation.data.StaticMetadata.Factory.createMutable;

import java.util.Collection;
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
 * As this class is non final, the protected constructor should be called and
 * also all nonfinal methods should be ovewriten.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("unchecked")
@ThreadSafe
public class Bundle implements ConfigurationNode {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1092283780730455977L;
    
    private static final String TYPE = "type"; //$NON-NLS-1$
    
    private static final String INITIAL_LOOKUP = "initialLookup"; //$NON-NLS-1$
    
    private static final String ACTIVE = "active"; //$NON-NLS-1$
    
    private static final String NAME = "name"; //$NON-NLS-1$
    static {
        final StaticMetadata newStaticMetadata = createMutable();
        newStaticMetadata.setChildrenNodeValidTypes(Project.class,
                StreamArtifact.class, JcrArtifact.class, ArtifactMapping.class,
                Bundle.class);
        newStaticMetadata.setType(Bundle.class);
        newStaticMetadata.setParentNodeValidTypes(Project.class);
        newStaticMetadata.setKeyProperty(NAME);
        newStaticMetadata.setPropertyTypes(map(ofKeys(ACTIVE, TYPE,
                INITIAL_LOOKUP), andValues(Boolean.class, String.class,
                String.class)));
        staticMetadata = createImmutable(newStaticMetadata);
    }
    
    private final InstanceMetadata instanceMetadata;
    
    private static final StaticMetadata staticMetadata;
    
    /**
     * Creates a bundle within a project.
     * 
     * @param project
     * @param name
     */
    public Bundle(final Project project, final String name) {
        this.instanceMetadata = createWithKeyProperty(staticMetadata, this,
                project, name);
    }
    
    /**
     * This constructor should be called when overiding this class. The method
     * {@link #getStaticMetadata()} should be overiten too.
     * 
     * @param staticMetadata
     * @param instanceMetadata
     */
    protected Bundle(final StaticMetadata staticMetadata,
            final InstanceMetadata instanceMetadata) {
        this.instanceMetadata = instanceMetadata;
    }
    
    /**
     * Adds an artifact.
     * 
     * @param Artifact
     */
    public final void addArtifact(final StreamArtifact Artifact) {
        this.instanceMetadata.addChild(Artifact);
    }
    
    /**
     * Adds an artifact mapping.
     * 
     * @param ArtifactMapping
     */
    public final void addArtifactMapping(final ArtifactMapping ArtifactMapping) {
        this.instanceMetadata.addChild(ArtifactMapping);
    }
    
    /**
     * Adds a bundle.
     * 
     * @param bundle
     */
    public final void addBundle(final Bundle bundle) {
        this.instanceMetadata.addChild(bundle);
    }
    
    /**
     * Adds a jcr artifact.
     * 
     * @param JcrArtifact
     */
    public final void addJcrArtifact(final JcrArtifact JcrArtifact) {
        this.instanceMetadata.addChild(JcrArtifact);
    }
    
    /**
     * Adds a inner project.
     * 
     * @param Project
     */
    public final void addProject(final Project Project) {
        this.instanceMetadata.addChild(Project);
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
     * @return active property
     */
    public final Boolean getActive() {
        return this.instanceMetadata.getProperty(ACTIVE);
    }
    
    /**
     * Returns an artifact by its name.
     * 
     * @param name
     * @return an artifact
     */
    public final StreamArtifact getArtifactByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(StreamArtifact.class,
                name);
    }
    
    /**
     * Returns a artifact mapping by its name.
     * 
     * @param name
     * @return an artifact mapping
     */
    public final ArtifactMapping getArtifactMappingByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(ArtifactMapping.class,
                name);
    }
    
    /**
     * 
     * @return all artifact mapping names
     */
    public final Set<String> getArtifactMappingNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(ArtifactMapping.class);
    }
    
    /**
     * 
     * @return all artifact mappings
     */
    public final Collection<ArtifactMapping> getArtifactMappings() {
        return this.instanceMetadata.getChildrensOfType(ArtifactMapping.class);
    }
    
    /**
     * 
     * @return all artifact names
     */
    public final Set<String> getArtifactNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(StreamArtifact.class);
    }
    
    /**
     * 
     * @return all artifacts
     */
    public final Collection<StreamArtifact> getArtifacts() {
        return this.instanceMetadata.getChildrensOfType(StreamArtifact.class);
    }
    
    /**
     * Returns a bundle by its name
     * 
     * @param name
     * @return a bundle
     */
    public final Bundle getBundleByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Bundle.class, name);
    }
    
    /**
     * 
     * @return all bundle names
     */
    public final Set<String> getBundleNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(Bundle.class);
    }
    
    /**
     * 
     * @return all bundles
     */
    public final Collection<Bundle> getBundles() {
        return this.instanceMetadata.getChildrensOfType(Bundle.class);
    }
    
    /**
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
     * Returns a jcr artifact by its name.
     * 
     * @param name
     * @return a jcr artifact
     */
    public final JcrArtifact getJcrArtifactByName(final String name) {
        return this.instanceMetadata
                .getChildByKeyValue(JcrArtifact.class, name);
    }
    
    /**
     * @return all jcr artifact names
     */
    public final Set<String> getJcrArtifactNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(JcrArtifact.class);
    }
    
    /**
     * @return all jcr artifacts
     */
    public final Collection<JcrArtifact> getJcrArtifacts() {
        return this.instanceMetadata.getChildrensOfType(JcrArtifact.class);
    }
    
    /**
     * Returns a child project by its name
     * 
     * @param name
     * @return a project
     */
    public final Project getProjectByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Project.class, name);
    }
    
    /**
     * 
     * @return all child project names
     */
    public final Set<String> getProjectNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(Project.class);
    }
    
    /**
     * 
     * @return all child projects
     */
    public final Collection<Project> getProjects() {
        return this.instanceMetadata.getChildrensOfType(Project.class);
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
        } else if (parent instanceof Project) {
            final Project proj = (Project) parent;
            return proj.getRepository();
        }
        return null;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public StaticMetadata getStaticMetadata() {
        return staticMetadata;
    }
    
    /**
     * 
     * @return the type property
     */
    public final String getType() {
        return this.instanceMetadata.getProperty(TYPE);
    }
    
    /**
     * Removes an artifact.
     * 
     * @param Artifact
     */
    public final void removeArtifact(final StreamArtifact Artifact) {
        this.instanceMetadata.removeChild(Artifact);
    }
    
    /**
     * Removes a artifact mapping.
     * 
     * @param ArtifactMapping
     */
    public final void removeArtifactMapping(
            final ArtifactMapping ArtifactMapping) {
        this.instanceMetadata.removeChild(ArtifactMapping);
    }
    
    /**
     * removes a bundle.
     * 
     * @param bundle
     */
    public final void removeBundle(final Bundle bundle) {
        this.instanceMetadata.removeChild(bundle);
    }
    
    /**
     * Removes a Jcr Artifact.
     * 
     * @param JcrArtifact
     */
    public final void removeJcrArtifact(final JcrArtifact JcrArtifact) {
        this.instanceMetadata.removeChild(JcrArtifact);
    }
    
    /**
     * Removes a project.
     * 
     * @param Project
     */
    public final void removeProject(final Project Project) {
        this.instanceMetadata.removeChild(Project);
    }
    
    /**
     * Sets the active property.
     * 
     * @param active
     */
    public final void setActive(final Boolean active) {
        this.instanceMetadata.setProperty(ACTIVE, active);
    }
    
    /**
     * Sets the initial lookup property.
     * 
     * @param initialLookup
     */
    public final void setInitialLookup(final String initialLookup) {
        this.instanceMetadata.setProperty(INITIAL_LOOKUP, initialLookup);
    }
    
    /**
     * Sets the type property.
     * 
     * @param type
     */
    public final void setType(final String type) {
        this.instanceMetadata.setProperty(TYPE, type);
    }
    
}
