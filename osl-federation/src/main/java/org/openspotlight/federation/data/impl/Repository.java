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

/**
 * A repository represents a group of projects. There's some properties that can
 * reflect on project processing such as number or threads for processing this
 * repository.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("unchecked")
@ThreadSafe
@StaticMetadata(propertyNames = { "active", "numberOfParallelThreads" }, propertyTypes = {
        Boolean.class, Integer.class }, keyPropertyName = "name", keyPropertyType = String.class, validParentTypes = Configuration.class, validChildrenTypes = Project.class)
public final class Repository implements ConfigurationNode {
    
    private static final String NUMBER_OF_PARALLEL_THREADS = "numberOfParallelThreads"; //$NON-NLS-1$
    
    private static final String ACTIVE = "active"; //$NON-NLS-1$
    
    private final InstanceMetadata instanceMetadata;
    
    private static final long serialVersionUID = -3606246260530743008L;
    
    /**
     * Creates a repository within a configuration.
     * 
     * @param configuration
     * @param name
     */
    public Repository(final Configuration configuration, final String name) {
        this.instanceMetadata = createWithKeyProperty(this, configuration, name);
        checkCondition("noRepository", //$NON-NLS-1$
                configuration.getRepositoryByName(name) == null);
        configuration.addRepository(this);
        
    }
    
    /**
     * adds a project inside this repository.
     * 
     * @param project
     */
    public final void addProject(final Project project) {
        this.instanceMetadata.addChild(project);
    }
    
    /**
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
     * @return active property
     */
    public final Boolean getActive() {
        return this.instanceMetadata.getProperty(ACTIVE);
    }
    
    /**
     * @return the configuration (parent node)
     */
    public final Configuration getConfiguration() {
        return this.instanceMetadata.getParent(Configuration.class);
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
     * 
     * @return the number of parallel threads for this repository processing
     */
    public final Integer getNumberOfParallelThreads() {
        return this.instanceMetadata.getProperty(NUMBER_OF_PARALLEL_THREADS);
    }
    
    /**
     * 
     * @param name
     * @return a project by its name
     */
    public final Project getProjectByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Project.class, name);
    }
    
    /**
     * 
     * @return all valid names existing projects inside this repository
     */
    public final Set<String> getProjectNames() {
        return (Set<String>) this.instanceMetadata
                .getKeysFromChildrenOfType(Project.class);
    }
    
    /**
     * 
     * @return all projects inside this repository
     */
    public final Collection<Project> getProjects() {
        return this.instanceMetadata.getChildrensOfType(Project.class);
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
     * removes a given project from this repository.
     * 
     * @param project
     */
    public final void removeProject(final Project project) {
        this.instanceMetadata.removeChild(project);
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
     * Sets the number of parallel threads.
     * 
     * @param numberOfParallelThreads
     */
    public final void setNumberOfParallelThreads(
            final Integer numberOfParallelThreads) {
        this.instanceMetadata.setProperty(NUMBER_OF_PARALLEL_THREADS,
                numberOfParallelThreads);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return this.instanceMetadata.toString();
    }
    
}
