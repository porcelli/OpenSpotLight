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

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.util.Collection;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.impl.ArtifactAboutToChange.Status;

/**
 * A project represents a group of artifacts such as source folders with program files, database artifacts such as tables,
 * procedures, and so on, and so on. A common processing task should take care of project bundles. But is possible to store
 * artifacts inside projects.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "unchecked" )
@ThreadSafe
@StaticMetadata( propertyNames = {"active", "graphRoot", "type"}, propertyTypes = {Boolean.class, Boolean.class, String.class}, keyPropertyName = "name", keyPropertyType = String.class, validParentTypes = {
    Group.class, Repository.class}, validChildrenTypes = {ScheduleData.class, Group.class, StreamArtifactAboutToChange.class,
    CustomArtifact.class, ArtifactMapping.class, ArtifactSource.class} )
public final class Group implements ConfigurationNode, Schedulable<Group> {

    private static final String    ACTIVE           = "active";             //$NON-NLS-1$

    private static final String    GRAPH_ROOT       = "graphRoot";
    static final long              serialVersionUID = -3606246260530743008L;

    private static final String    TYPE             = "type";

    private final InstanceMetadata instanceMetadata;

    /**
     * Creates a project within a other project.
     * 
     * @param project
     * @param name
     */
    public Group(
                  final Group project, final String name ) {
        this.instanceMetadata = createWithKeyProperty(this, project, name);
        checkCondition("noProject", //$NON-NLS-1$
                       project.getGroupByName(name) == null);
        project.getInstanceMetadata().addChild(this);

    }

    /**
     * Creates a project within a repository.
     * 
     * @param repository
     * @param name
     */
    public Group(
                  final Repository repository, final String name ) {
        this.instanceMetadata = createWithKeyProperty(this, repository, name);
        checkCondition("noProject", //$NON-NLS-1$
                       repository.getGroupByName(name) == null);
        repository.getInstanceMetadata().addChild(this);
    }

    /**
     * {@inheritDoc}
     */
    public final int compareTo( final ConfigurationNode o ) {
        return this.instanceMetadata.compare(this, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object obj ) {
        return this.instanceMetadata.equals(obj);
    }

    /**
     * @return active property
     */
    public final Boolean getActive() {
        return this.instanceMetadata.getProperty(ACTIVE);
    }

    /**
     * Returns a artifact mapping by its name.
     * 
     * @param name
     * @return an artifact mapping
     */
    public final ArtifactMapping getArtifactMappingByName( final String name ) {
        return this.instanceMetadata.getChildByKeyValue(ArtifactMapping.class, name);
    }

    /**
     * @return all artifact mapping names
     */
    public final Set<String> getArtifactMappingNames() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(ArtifactMapping.class);
    }

    /**
     * @return all artifact mappings
     */
    public final Collection<ArtifactMapping> getArtifactMappings() {
        return this.instanceMetadata.getChildrensOfType(ArtifactMapping.class);
    }

    /**
     * @return all artifact names
     */
    public final Set<String> getArtifactNames() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(StreamArtifactAboutToChange.class);
    }

    /**
     * @return all artifacts
     */
    public final Collection<StreamArtifactAboutToChange> getArtifacts() {
        return this.instanceMetadata.getChildrensOfType(StreamArtifactAboutToChange.class);
    }

    /**
     * Returns a bundle by its name
     * 
     * @param name
     * @return a bundle
     */
    public final ArtifactSource getArtifactSourceByName( final String name ) {
        return this.instanceMetadata.getChildByKeyValue(ArtifactSource.class, name);
    }

    /**
     * @return all bundle names
     */
    public final Set<String> getArtifactSourceNames() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(ArtifactSource.class);
    }

    /**
     * @return all bundles
     */
    public final Collection<ArtifactSource> getArtifactSources() {
        return this.instanceMetadata.getChildrensOfType(ArtifactSource.class);
    }

    /**
     * Returns a custom artifact by its name.
     * 
     * @param name
     * @return a custom artifact
     */
    public final CustomArtifact getCustomArtifactByName( final String name ) {
        return this.instanceMetadata.getChildByKeyValue(CustomArtifact.class, name);
    }

    /**
     * @return all custom artifact names
     */
    public final Set<String> getCustomArtifactNames() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(CustomArtifact.class);
    }

    /**
     * @return all custom artifacts
     */
    public final Collection<CustomArtifact> getCustomArtifacts() {
        return this.instanceMetadata.getChildrensOfType(CustomArtifact.class);
    }

    /**
     * @return the graph root property
     */
    public Boolean getGraphRoot() {
        return this.instanceMetadata.getProperty(GRAPH_ROOT);
    }

    /**
     * Returns a child project by its name
     * 
     * @param name
     * @return a project
     */
    public final Group getGroupByName( final String name ) {
        return this.instanceMetadata.getChildByKeyValue(Group.class, name);
    }

    /**
     * @return all child project names
     */
    public final Set<String> getGroupNames() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(Group.class);
    }

    /**
     * @return all child projects
     */
    public final Collection<Group> getGroups() {
        return this.instanceMetadata.getChildrensOfType(Group.class);
    }

    /**
     * {@inheritDoc}
     */
    public final InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }

    /**
     * The name, in this case, is a unique identifier (with parent node) to this node.
     * 
     * @return the node name
     */
    public String getName() {
        return (String)this.instanceMetadata.getKeyPropertyValue();
    }

    /**
     * Returns the repository if this node has one, or the parent's project repository instead.
     * 
     * @return a repository
     */
    public final Repository getRepository() {
        final ConfigurationNode parent = this.instanceMetadata.getDefaultParent();
        if (parent instanceof Repository) {
            return (Repository)parent;
        } else if (parent instanceof Group) {
            final Group proj = (Group)parent;
            return proj.getRepository();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ScheduleData> getScheduleData() {
        return this.instanceMetadata.getChildrensOfType(ScheduleData.class);
    }

    /**
     * {@inheritDoc}
     */
    public ScheduleData getScheduleDataByCronInformation( final String cronInformation ) {
        return this.instanceMetadata.getChildByKeyValue(ScheduleData.class, cronInformation);
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> getScheduleDataCronInformations() {
        return (Set<String>)this.instanceMetadata.getKeyFromChildrenOfTypes(ScheduleData.class);
    }

    /**
     * Returns an artifact by its name.
     * 
     * @param name
     * @return an artifact
     */
    public final StreamArtifactAboutToChange getStreamArtifactByName( final String name ) {
        return this.instanceMetadata.getChildByKeyValue(StreamArtifactAboutToChange.class, name);
    }

    /**
     * @return the type property
     */
    public String getType() {
        return this.instanceMetadata.getProperty(TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return this.instanceMetadata.hashCode();
    }

    /**
     * Removes a Custom Artifact.
     * 
     * @param customArtifact the custom artifact
     */
    public final void markCustomArtifactAsRemoved( final CustomArtifact customArtifact ) {
        customArtifact.getInstanceMetadata().setProperty("status", Status.EXCLUDED);
    }

    /**
     * Removes an artifact.
     * 
     * @param streamArtifact the artifact
     */
    public final void markStreamArtifactAsRemoved( final StreamArtifactAboutToChange streamArtifact ) {
        streamArtifact.getInstanceMetadata().setProperty("status", Status.EXCLUDED);
    }

    /**
     * Removes a artifact mapping.
     * 
     * @param ArtifactMapping
     */
    public final void removeArtifactMapping( final ArtifactMapping ArtifactMapping ) {
        this.instanceMetadata.removeChild(ArtifactMapping);
    }

    /**
     * removes a bundle.
     * 
     * @param bundle
     */
    public final void removeArtifactSource( final ArtifactSource bundle ) {
        this.instanceMetadata.removeChild(bundle);
    }

    /**
     * Removes a project.
     * 
     * @param group
     */
    public final void removeGroups( final Group group ) {
        this.instanceMetadata.removeChild(group);
    }

    /**
     * {@inheritDoc}
     */
    public void removeScheduleData( final ScheduleData scheduleData ) {
        this.instanceMetadata.removeChild(scheduleData);
    }

    /**
     * Sets the active property.
     * 
     * @param active
     */
    public final void setActive( final Boolean active ) {
        this.instanceMetadata.setProperty(ACTIVE, active);
    }

    /**
     * Sets the graph root property.
     * 
     * @param graphRoot
     */
    public void setGraphRoot( final Boolean graphRoot ) {
        this.instanceMetadata.setProperty(GRAPH_ROOT, graphRoot);
    }

    /**
     * Sets the type property.
     * 
     * @param type
     */
    public void setType( final String type ) {
        this.instanceMetadata.setProperty(TYPE, type);
    }

}
